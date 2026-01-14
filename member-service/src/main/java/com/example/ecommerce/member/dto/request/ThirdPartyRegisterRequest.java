package com.example.ecommerce.member.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 第三方注册请求DTO
 */
@Data
public class ThirdPartyRegisterRequest {

    /**
     * 平台类型(WECHAT/ALIPAY/QQ)
     */
    @NotBlank(message = "平台类型不能为空")
    private String platformType;

    /**
     * 第三方OpenID
     */
    @NotBlank(message = "OpenID不能为空")
    private String openId;

    /**
     * 第三方UnionID(微信)
     */
    private String unionId;

    /**
     * 手机号(可选,用于绑定已有账号)
     */
    private String phone;

    /**
     * 昵称(可选,默认使用第三方昵称)
     */
    @Size(min = 2, max = 50, message = "昵称长度必须在2-50个字符之间")
    private String nickname;
}
