package com.example.ecommerce.member.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 第三方登录请求DTO
 */
@Data
public class ThirdPartyLoginRequest {

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
}
