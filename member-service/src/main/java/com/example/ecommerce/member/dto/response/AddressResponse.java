package com.example.ecommerce.member.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 收货地址响应DTO
 */
@Data
public class AddressResponse {

    /**
     * 地址ID
     */
    private Long addressId;

    /**
     * 收货人姓名
     */
    private String receiverName;

    /**
     * 收货人手机号(脱敏)
     */
    private String receiverPhone;

    /**
     * 省份名称
     */
    private String provinceName;

    /**
     * 城市名称
     */
    private String cityName;

    /**
     * 区县名称
     */
    private String districtName;

    /**
     * 详细地址
     */
    private String detailAddress;

    /**
     * 邮编
     */
    private String postalCode;

    /**
     * 是否默认地址
     */
    private Boolean isDefault;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
