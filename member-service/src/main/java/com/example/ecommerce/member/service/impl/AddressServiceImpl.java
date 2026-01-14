package com.example.ecommerce.member.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.ecommerce.member.dto.request.AddAddressRequest;
import com.example.ecommerce.member.dto.request.UpdateAddressRequest;
import com.example.ecommerce.member.dto.response.AddressResponse;
import com.example.ecommerce.member.entity.Address;
import com.example.ecommerce.member.exception.BusinessException;
import com.example.ecommerce.member.mapper.AddressMapper;
import com.example.ecommerce.member.service.AddressService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 收货地址服务实现类
 */
@Slf4j
@Service
public class AddressServiceImpl implements AddressService {

    private final AddressMapper addressMapper;

    @Autowired
    public AddressServiceImpl(AddressMapper addressMapper) {
        this.addressMapper = addressMapper;
    }

    @Override
    public List<AddressResponse> getAddresses(Long memberId) {
        // 查询收货地址列表,默认地址排在前面
        LambdaQueryWrapper<Address> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Address::getMemberId, memberId)
                .orderByDesc(Address::getIsDefault)
                .orderByDesc(Address::getCreateTime);

        List<Address> addresses = addressMapper.selectList(queryWrapper);

        // 转换为响应DTO并脱敏
        return addresses.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addAddress(Long memberId, AddAddressRequest request) {
        // 如果设置为默认地址,先取消其他默认地址
        if (Boolean.TRUE.equals(request.getIsDefault())) {
            cancelDefaultAddress(memberId);
        }

        // 创建新地址
        Address address = new Address();
        address.setMemberId(memberId);
        address.setReceiverName(request.getReceiverName());
        address.setReceiverPhone(request.getReceiverPhone());
        address.setProvinceCode(request.getProvinceCode());
        address.setProvinceName(request.getProvinceName());
        address.setCityCode(request.getCityCode());
        address.setCityName(request.getCityName());
        address.setDistrictCode(request.getDistrictCode());
        address.setDistrictName(request.getDistrictName());
        address.setDetailAddress(request.getDetailAddress());
        address.setPostalCode(request.getPostalCode());
        address.setIsDefault(Boolean.TRUE.equals(request.getIsDefault()) ? 1 : 0);

        int rows = addressMapper.insert(address);

        if (rows == 0) {
            throw new BusinessException("添加收货地址失败");
        }

        log.info("添加收货地址成功: memberId={}, addressId={}", memberId, address.getId());
        return address.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAddress(Long memberId, UpdateAddressRequest request) {
        Long addressId = request.getAddressId();

        // 查询地址是否存在
        Address address = addressMapper.selectById(addressId);

        if (address == null) {
            throw new BusinessException("收货地址不存在");
        }

        // 验证地址是否属于当前会员
        if (!address.getMemberId().equals(memberId)) {
            throw new BusinessException("无权操作此地址");
        }

        // 如果设置为默认地址,先取消其他默认地址
        if (Boolean.TRUE.equals(request.getIsDefault())) {
            cancelDefaultAddress(memberId);
        }

        // 更新字段(只更新非空字段)
        if (request.getReceiverName() != null) {
            address.setReceiverName(request.getReceiverName());
        }
        if (request.getReceiverPhone() != null) {
            address.setReceiverPhone(request.getReceiverPhone());
        }
        if (request.getProvinceCode() != null) {
            address.setProvinceCode(request.getProvinceCode());
        }
        if (request.getProvinceName() != null) {
            address.setProvinceName(request.getProvinceName());
        }
        if (request.getCityCode() != null) {
            address.setCityCode(request.getCityCode());
        }
        if (request.getCityName() != null) {
            address.setCityName(request.getCityName());
        }
        if (request.getDistrictCode() != null) {
            address.setDistrictCode(request.getDistrictCode());
        }
        if (request.getDistrictName() != null) {
            address.setDistrictName(request.getDistrictName());
        }
        if (request.getDetailAddress() != null) {
            address.setDetailAddress(request.getDetailAddress());
        }
        if (request.getPostalCode() != null) {
            address.setPostalCode(request.getPostalCode());
        }
        if (request.getIsDefault() != null) {
            address.setIsDefault(Boolean.TRUE.equals(request.getIsDefault()) ? 1 : 0);
        }

        int rows = addressMapper.updateById(address);

        if (rows == 0) {
            throw new BusinessException("更新收货地址失败");
        }

        log.info("更新收货地址成功: memberId={}, addressId={}", memberId, addressId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAddress(Long memberId, Long addressId) {
        // 查询地址是否存在
        Address address = addressMapper.selectById(addressId);

        if (address == null) {
            throw new BusinessException("收货地址不存在");
        }

        // 验证地址是否属于当前会员
        if (!address.getMemberId().equals(memberId)) {
            throw new BusinessException("无权操作此地址");
        }

        int rows = addressMapper.deleteById(addressId);

        if (rows == 0) {
            throw new BusinessException("删除收货地址失败");
        }

        log.info("删除收货地址成功: memberId={}, addressId={}", memberId, addressId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setDefaultAddress(Long memberId, Long addressId) {
        // 查询地址是否存在
        Address address = addressMapper.selectById(addressId);

        if (address == null) {
            throw new BusinessException("收货地址不存在");
        }

        // 验证地址是否属于当前会员
        if (!address.getMemberId().equals(memberId)) {
            throw new BusinessException("无权操作此地址");
        }

        // 取消其他默认地址
        cancelDefaultAddress(memberId);

        // 设置为默认地址
        address.setIsDefault(1);
        int rows = addressMapper.updateById(address);

        if (rows == 0) {
            throw new BusinessException("设置默认地址失败");
        }

        log.info("设置默认地址成功: memberId={}, addressId={}", memberId, addressId);
    }

    /**
     * 取消会员的所有默认地址
     *
     * @param memberId 会员ID
     */
    private void cancelDefaultAddress(Long memberId) {
        // 查询所有默认地址
        LambdaQueryWrapper<Address> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Address::getMemberId, memberId)
                .eq(Address::getIsDefault, 1);

        Address defaultAddress = addressMapper.selectOne(queryWrapper);

        if (defaultAddress != null) {
            defaultAddress.setIsDefault(0);
            addressMapper.updateById(defaultAddress);
        }
    }

    /**
     * 转换为响应DTO
     *
     * @param address 地址实体
     * @return 响应DTO
     */
    private AddressResponse convertToResponse(Address address) {
        AddressResponse response = new AddressResponse();
        response.setAddressId(address.getId());
        response.setReceiverName(address.getReceiverName());
        response.setReceiverPhone(maskPhone(address.getReceiverPhone()));
        response.setProvinceName(address.getProvinceName());
        response.setCityName(address.getCityName());
        response.setDistrictName(address.getDistrictName());
        response.setDetailAddress(address.getDetailAddress());
        response.setPostalCode(address.getPostalCode());
        response.setIsDefault(address.getIsDefault() == 1);
        response.setCreateTime(address.getCreateTime());
        return response;
    }

    /**
     * 手机号脱敏
     *
     * @param phone 手机号
     * @return 脱敏后的手机号
     */
    private String maskPhone(String phone) {
        if (phone == null || phone.length() != 11) {
            return phone;
        }
        return phone.substring(0, 3) + "****" + phone.substring(7);
    }
}
