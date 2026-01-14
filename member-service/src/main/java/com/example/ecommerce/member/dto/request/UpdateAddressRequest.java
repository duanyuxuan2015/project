package com.example.ecommerce.member.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 更新收货地址请求DTO
 */
@Data
public class UpdateAddressRequest {

    /**
     * 地址ID
     */
    @NotNull(message = "地址ID不能为空")
    private Long addressId;

    /**
     * 收货人姓名
     */
    @Size(max = 50, message = "收货人姓名长度不能超过50个字符")
    private String receiverName;

    /**
     * 收货人手机号
     */
    private String receiverPhone;

    /**
     * 省份编码
     */
    private String provinceCode;

    /**
     * 省份名称
     */
    private String provinceName;

    /**
     * 城市编码
     */
    private String cityCode;

    /**
     * 城市名称
     */
    private String cityName;

    /**
     * 区县编码
     */
    private String districtCode;

    /**
     * 区县名称
     */
    private String districtName;

    /**
     * 详细地址
     */
    @Size(max = 255, message = "详细地址长度不能超过255个字符")
    private String detailAddress;

    /**
     * 邮编
     */
    @Size(max = 10, message = "邮编长度不能超过10个字符")
    private String postalCode;

    /**
     * 是否设为默认地址
     */
    private Boolean isDefault;
}
