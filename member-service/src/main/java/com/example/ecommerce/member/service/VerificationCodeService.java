package com.example.ecommerce.member.service;

import com.example.ecommerce.member.dto.request.SendCodeRequest;

/**
 * 验证码服务接口
 */
public interface VerificationCodeService {

    /**
     * 发送验证码
     *
     * @param request 发送验证码请求
     * @return 验证码(降级策略下返回,正常情况返回null)
     */
    String sendCode(SendCodeRequest request);

    /**
     * 验证验证码
     *
     * @param phone 手机号
     * @param type  验证码类型
     * @param code  验证码
     * @return true:验证通过, false:验证失败
     */
    boolean verifyCode(String phone, String type, String code);
}
