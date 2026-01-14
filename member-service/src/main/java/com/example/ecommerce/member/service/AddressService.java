package com.example.ecommerce.member.service;

import com.example.ecommerce.member.dto.request.AddAddressRequest;
import com.example.ecommerce.member.dto.request.UpdateAddressRequest;
import com.example.ecommerce.member.dto.response.AddressResponse;

import java.util.List;

/**
 * 收货地址服务接口
 */
public interface AddressService {

    /**
     * 获取收货地址列表
     *
     * @param memberId 会员ID
     * @return 收货地址列表
     */
    List<AddressResponse> getAddresses(Long memberId);

    /**
     * 添加收货地址
     *
     * @param memberId 会员ID
     * @param request  添加请求
     * @return 地址ID
     */
    Long addAddress(Long memberId, AddAddressRequest request);

    /**
     * 更新收货地址
     *
     * @param memberId 会员ID
     * @param request  更新请求
     */
    void updateAddress(Long memberId, UpdateAddressRequest request);

    /**
     * 删除收货地址
     *
     * @param memberId 会员ID
     * @param addressId 地址ID
     */
    void deleteAddress(Long memberId, Long addressId);

    /**
     * 设置默认地址
     *
     * @param memberId 会员ID
     * @param addressId 地址ID
     */
    void setDefaultAddress(Long memberId, Long addressId);
}
