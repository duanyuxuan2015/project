package com.example.ecommerce.member.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 添加收货地址请求DTO
 */
@Data
public class AddAddressRequest {

    /**
     * 收货人姓名
     */
    @NotBlank(message = "收货人姓名不能为空")
    @Size(max = 50, message = "收货人姓名长度不能超过50个字符")
    private String receiverName;

    /**
     * 收货人手机号
     */
    @NotBlank(message = "收货人手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String receiverPhone;

    /**
     * 省份编码
     */
    @NotBlank(message = "省份编码不能为空")
    private String provinceCode;

    /**
     * 省份名称
     */
    @NotBlank(message = "省份名称不能为空")
    private String provinceName;

    /**
     * 城市编码
     */
    @NotBlank(message = "城市编码不能为空")
    private String cityCode;

    /**
     * 城市名称
     */
    @NotBlank(message = "城市名称不能为空")
    private String cityName;

    /**
     * 区县编码
     */
    @NotBlank(message = "区县编码不能为空")
    private String districtCode;

    /**
     * 区县名称
     */
    @NotBlank(message = "区县名称不能为空")
    private String districtName;

    /**
     * 详细地址
     */
    @NotBlank(message = "详细地址不能为空")
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
