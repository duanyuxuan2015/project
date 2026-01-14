package com.example.ecommerce.member.unit;

import com.example.ecommerce.member.dto.request.AddAddressRequest;
import com.example.ecommerce.member.dto.request.UpdateAddressRequest;
import com.example.ecommerce.member.dto.response.AddressResponse;
import com.example.ecommerce.member.entity.Address;
import com.example.ecommerce.member.exception.BusinessException;
import com.example.ecommerce.member.mapper.AddressMapper;
import com.example.ecommerce.member.service.impl.AddressServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 地址管理单元测试
 */
@ExtendWith(MockitoExtension.class)
class AddressServiceTest {

    @Mock
    private AddressMapper addressMapper;

    @InjectMocks
    private AddressServiceImpl addressService;

    private AddAddressRequest addRequest;
    private UpdateAddressRequest updateRequest;
    private Address testAddress;

    @BeforeEach
    void setUp() {
        // 添加地址请求
        addRequest = new AddAddressRequest();
        addRequest.setReceiverName("张三");
        addRequest.setReceiverPhone("13900139000");
        addRequest.setProvinceCode("110000");
        addRequest.setProvinceName("北京市");
        addRequest.setCityCode("110100");
        addRequest.setCityName("北京市");
        addRequest.setDistrictCode("110101");
        addRequest.setDistrictName("东城区");
        addRequest.setDetailAddress("某某街道1号");
        addRequest.setPostalCode("100000");
        addRequest.setIsDefault(true);

        // 更新地址请求
        updateRequest = new UpdateAddressRequest();
        updateRequest.setAddressId(1L);
        updateRequest.setReceiverName("李四");
        updateRequest.setIsDefault(false);

        // 测试地址
        testAddress = new Address();
        testAddress.setId(1L);
        testAddress.setMemberId(1L);
        testAddress.setReceiverName("张三");
        testAddress.setReceiverPhone("13900139000");
        testAddress.setProvinceName("北京市");
        testAddress.setCityName("北京市");
        testAddress.setDistrictName("东城区");
        testAddress.setDetailAddress("某某街道1号");
        testAddress.setIsDefault(1);
    }

    @Test
    void testGetAddresses_Success() {
        // Given
        when(addressMapper.selectList(any())).thenReturn(Arrays.asList(testAddress));

        // When
        List<AddressResponse> responses = addressService.getAddresses(1L);

        // Then
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("张三", responses.get(0).getReceiverName());
        assertEquals("139****9000", responses.get(0).getReceiverPhone()); // 手机号脱敏
        assertTrue(responses.get(0).getIsDefault());
    }

    @Test
    void testAddAddress_Success() {
        // Given
        when(addressMapper.selectOne(any())).thenReturn(null); // 无默认地址
        when(addressMapper.insert(any())).thenAnswer(invocation -> {
            Address addr = invocation.getArgument(0);
            addr.setId(1L);
            return 1;
        });

        // When
        Long addressId = addressService.addAddress(1L, addRequest);

        // Then
        assertNotNull(addressId);
        assertEquals(1L, addressId);
        verify(addressMapper, times(1)).insert(any(Address.class));
    }

    @Test
    void testAddAddress_WithDefault_CancelOldDefault() {
        // Given: 已有默认地址
        Address oldDefault = new Address();
        oldDefault.setId(2L);
        oldDefault.setMemberId(1L);
        oldDefault.setIsDefault(1);

        when(addressMapper.selectOne(any())).thenReturn(oldDefault);
        when(addressMapper.updateById(any())).thenReturn(1);
        when(addressMapper.insert(any())).thenAnswer(invocation -> {
            Address addr = invocation.getArgument(0);
            addr.setId(3L);
            return 1;
        });

        // When
        Long addressId = addressService.addAddress(1L, addRequest);

        // Then
        assertNotNull(addressId);
        verify(addressMapper, times(1)).updateById(oldDefault); // 取消旧默认地址
        assertEquals(0, oldDefault.getIsDefault());
    }

    @Test
    void testUpdateAddress_Success() {
        // Given
        when(addressMapper.selectById(1L)).thenReturn(testAddress);
        when(addressMapper.updateById(any())).thenReturn(1);

        // When
        addressService.updateAddress(1L, updateRequest);

        // Then
        verify(addressMapper, times(1)).updateById(any(Address.class));
    }

    @Test
    void testUpdateAddress_NotFound() {
        // Given
        when(addressMapper.selectById(999L)).thenReturn(null);

        // When & Then
        assertThrows(BusinessException.class, () -> {
            addressService.updateAddress(1L, updateRequest);
        });
    }

    @Test
    void testUpdateAddress_WrongMember() {
        // Given: 地址属于其他会员
        testAddress.setMemberId(2L);
        when(addressMapper.selectById(1L)).thenReturn(testAddress);

        // When & Then
        assertThrows(BusinessException.class, () -> {
            addressService.updateAddress(1L, updateRequest);
        });
    }

    @Test
    void testDeleteAddress_Success() {
        // Given
        when(addressMapper.selectById(1L)).thenReturn(testAddress);
        when(addressMapper.deleteById(1L)).thenReturn(1);

        // When
        addressService.deleteAddress(1L, 1L);

        // Then
        verify(addressMapper, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteAddress_WrongMember() {
        // Given: 地址属于其他会员
        testAddress.setMemberId(2L);
        when(addressMapper.selectById(1L)).thenReturn(testAddress);

        // When & Then
        assertThrows(BusinessException.class, () -> {
            addressService.deleteAddress(1L, 1L);
        });
    }

    @Test
    void testSetDefaultAddress_Success() {
        // Given
        when(addressMapper.selectById(1L)).thenReturn(testAddress);
        when(addressMapper.selectOne(any())).thenReturn(null); // 无其他默认地址
        when(addressMapper.updateById(any())).thenReturn(1);

        // When
        addressService.setDefaultAddress(1L, 1L);

        // Then
        verify(addressMapper, times(1)).updateById(any(Address.class));
        assertEquals(1, testAddress.getIsDefault());
    }

    @Test
    void testSetDefaultAddress_CancelOldDefault() {
        // Given: 已有默认地址
        Address oldDefault = new Address();
        oldDefault.setId(2L);
        oldDefault.setMemberId(1L);
        oldDefault.setIsDefault(1);

        when(addressMapper.selectById(1L)).thenReturn(testAddress);
        when(addressMapper.selectOne(any())).thenReturn(oldDefault);
        when(addressMapper.updateById(any())).thenReturn(1);

        // When
        addressService.setDefaultAddress(1L, 1L);

        // Then
        verify(addressMapper, times(1)).updateById(oldDefault); // 取消旧默认
        verify(addressMapper, times(1)).updateById(testAddress); // 设置新默认
        assertEquals(0, oldDefault.getIsDefault());
    }

    @Test
    void testGetAddresses_DefaultFirst() {
        // Given: 多个地址，默认地址排在前面
        Address defaultAddr = new Address();
        defaultAddr.setId(1L);
        defaultAddr.setIsDefault(1);

        Address normalAddr = new Address();
        normalAddr.setId(2L);
        normalAddr.setIsDefault(0);

        when(addressMapper.selectList(any())).thenReturn(Arrays.asList(defaultAddr, normalAddr));

        // When
        List<AddressResponse> responses = addressService.getAddresses(1L);

        // Then
        assertNotNull(responses);
        assertEquals(2, responses.size());
        assertTrue(responses.get(0).getIsDefault()); // 第一个是默认地址
        assertFalse(responses.get(1).getIsDefault());
    }
}
