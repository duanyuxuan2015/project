package com.example.ecommerce.member.controller;

import com.example.ecommerce.member.annotation.OperationLog;
import com.example.ecommerce.member.dto.request.AddAddressRequest;
import com.example.ecommerce.member.dto.request.UpdateAddressRequest;
import com.example.ecommerce.member.dto.response.AddressResponse;
import com.example.ecommerce.member.dto.response.ApiResponse;
import com.example.ecommerce.member.service.AddressService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 收货地址控制器
 */
@Slf4j
@RestController
@RequestMapping("/addresses")
public class AddressController {

    private final AddressService addressService;

    @Autowired
    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    /**
     * 添加收货地址
     *
     * @param request  添加请求
     * @param httpRequest HTTP请求
     * @return 地址ID
     */
    @PostMapping
    @OperationLog(
            module = "收货地址",
            operationType = "ADD",
            description = "添加收货地址",
            logRequest = true
    )
    public ApiResponse<Long> addAddress(
            @Valid @RequestBody AddAddressRequest request,
            HttpServletRequest httpRequest) {
        // TODO: 从JWT Token中获取会员ID
        Long memberId = 1L;

        Long addressId = addressService.addAddress(memberId, request);
        return ApiResponse.success(addressId);
    }

    /**
     * 更新收货地址
     *
     * @param request  更新请求
     * @param httpRequest HTTP请求
     * @return 响应
     */
    @PutMapping
    @OperationLog(
            module = "收货地址",
            operationType = "UPDATE",
            description = "更新收货地址",
            logRequest = true
    )
    public ApiResponse<Void> updateAddress(
            @Valid @RequestBody UpdateAddressRequest request,
            HttpServletRequest httpRequest) {
        // TODO: 从JWT Token中获取会员ID
        Long memberId = 1L;

        addressService.updateAddress(memberId, request);
        return ApiResponse.success();
    }

    /**
     * 删除收货地址
     *
     * @param addressId 地址ID
     * @param httpRequest HTTP请求
     * @return 响应
     */
    @DeleteMapping("/{addressId}")
    @OperationLog(
            module = "收货地址",
            operationType = "DELETE",
            description = "删除收货地址"
    )
    public ApiResponse<Void> deleteAddress(
            @PathVariable Long addressId,
            HttpServletRequest httpRequest) {
        // TODO: 从JWT Token中获取会员ID
        Long memberId = 1L;

        addressService.deleteAddress(memberId, addressId);
        return ApiResponse.success();
    }

    /**
     * 设置默认地址
     *
     * @param addressId 地址ID
     * @param httpRequest HTTP请求
     * @return 响应
     */
    @PutMapping("/{addressId}/default")
    @OperationLog(
            module = "收货地址",
            operationType = "SET_DEFAULT",
            description = "设置默认地址"
    )
    public ApiResponse<Void> setDefaultAddress(
            @PathVariable Long addressId,
            HttpServletRequest httpRequest) {
        // TODO: 从JWT Token中获取会员ID
        Long memberId = 1L;

        addressService.setDefaultAddress(memberId, addressId);
        return ApiResponse.success();
    }
}
