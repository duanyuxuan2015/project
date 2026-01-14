package com.example.ecommerce.member.controller;

import com.example.ecommerce.member.annotation.OperationLog;
import com.example.ecommerce.member.dto.request.PasswordLoginRequest;
import com.example.ecommerce.member.dto.request.RegisterRequest;
import com.example.ecommerce.member.dto.request.SmsLoginRequest;
import com.example.ecommerce.member.dto.request.ThirdPartyRegisterRequest;
import com.example.ecommerce.member.dto.response.ApiResponse;
import com.example.ecommerce.member.dto.response.LoginResponse;
import com.example.ecommerce.member.dto.response.RegisterResponse;
import com.example.ecommerce.member.service.AuthService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 */
@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * 手机号注册
     *
     * @param request 注册请求
     * @return 注册响应
     */
    @PostMapping("/register")
    @OperationLog(
            module = "认证",
            operationType = "REGISTER",
            description = "会员注册",
            logRequest = true,
            logResponse = true
    )
    public ApiResponse<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        RegisterResponse response = authService.register(request);
        return ApiResponse.success(response);
    }

    /**
     * 第三方注册
     *
     * @param request 第三方注册请求
     * @return 注册响应
     */
    @PostMapping("/third-party/register")
    @OperationLog(
            module = "认证",
            operationType = "THIRD_PARTY_REGISTER",
            description = "第三方注册",
            logRequest = true,
            logResponse = true
    )
    public ApiResponse<RegisterResponse> thirdPartyRegister(@Valid @RequestBody ThirdPartyRegisterRequest request) {
        RegisterResponse response = authService.thirdPartyRegister(request);
        return ApiResponse.success(response);
    }

    /**
     * 密码登录
     *
     * @param request 密码登录请求
     * @return 登录响应
     */
    @PostMapping("/login/password")
    @OperationLog(
            module = "认证",
            operationType = "LOGIN",
            description = "密码登录",
            logRequest = true,
            logResponse = false
    )
    public ApiResponse<LoginResponse> passwordLogin(@Valid @RequestBody PasswordLoginRequest request) {
        LoginResponse response = authService.passwordLogin(request);
        return ApiResponse.success(response);
    }

    /**
     * 验证码登录
     *
     * @param request 验证码登录请求
     * @return 登录响应
     */
    @PostMapping("/login/sms")
    @OperationLog(
            module = "认证",
            operationType = "LOGIN",
            description = "验证码登录",
            logRequest = true,
            logResponse = false
    )
    public ApiResponse<LoginResponse> smsLogin(@Valid @RequestBody SmsLoginRequest request) {
        LoginResponse response = authService.smsLogin(request);
        return ApiResponse.success(response);
    }

    /**
     * 登出
     *
     * @param token JWT Token (从Header中获取)
     * @return 响应
     */
    @PostMapping("/logout")
    @OperationLog(
            module = "认证",
            operationType = "LOGOUT",
            description = "会员登出"
    )
    public ApiResponse<Void> logout(@RequestHeader(name = "Authorization", required = false) String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        authService.logout(token);
        return ApiResponse.success();
    }

    /**
     * 重置密码
     *
     * @param request 重置密码请求
     * @return 响应
     */
    @PostMapping("/reset-password")
    @OperationLog(
            module = "认证",
            operationType = "RESET_PASSWORD",
            description = "重置密码",
            logRequest = true
    )
    public ApiResponse<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ApiResponse.success();
    }
}
