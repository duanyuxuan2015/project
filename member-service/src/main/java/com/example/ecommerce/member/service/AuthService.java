package com.example.ecommerce.member.service;

import com.example.ecommerce.member.dto.request.PasswordLoginRequest;
import com.example.ecommerce.member.dto.request.RegisterRequest;
import com.example.ecommerce.member.dto.request.ResetPasswordRequest;
import com.example.ecommerce.member.dto.request.SmsLoginRequest;
import com.example.ecommerce.member.dto.request.ThirdPartyRegisterRequest;
import com.example.ecommerce.member.dto.response.LoginResponse;
import com.example.ecommerce.member.dto.response.RegisterResponse;

/**
 * 认证服务接口
 */
public interface AuthService {

    /**
     * 手机号注册
     *
     * @param request 注册请求
     * @return 注册响应
     */
    RegisterResponse register(RegisterRequest request);

    /**
     * 第三方注册
     *
     * @param request 第三方注册请求
     * @return 注册响应
     */
    RegisterResponse thirdPartyRegister(ThirdPartyRegisterRequest request);

    /**
     * 密码登录
     *
     * @param request 密码登录请求
     * @return 登录响应
     */
    LoginResponse passwordLogin(PasswordLoginRequest request);

    /**
     * 验证码登录
     *
     * @param request 验证码登录请求
     * @return 登录响应
     */
    LoginResponse smsLogin(SmsLoginRequest request);

    /**
     * 重置密码
     *
     * @param request 重置密码请求
     */
    void resetPassword(ResetPasswordRequest request);

    /**
     * 登出
     *
     * @param token JWT Token
     */
    void logout(String token);
}
