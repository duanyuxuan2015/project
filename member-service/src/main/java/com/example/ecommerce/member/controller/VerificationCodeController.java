package com.example.ecommerce.member.controller;

import com.example.ecommerce.member.annotation.OperationLog;
import com.example.ecommerce.member.dto.request.SendCodeRequest;
import com.example.ecommerce.member.dto.response.ApiResponse;
import com.example.ecommerce.member.service.VerificationCodeService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 验证码控制器
 */
@Slf4j
@RestController
@RequestMapping("/auth")
public class VerificationCodeController {

    private final VerificationCodeService verificationCodeService;

    @Autowired
    public VerificationCodeController(VerificationCodeService verificationCodeService) {
        this.verificationCodeService = verificationCodeService;
    }

    /**
     * 发送验证码
     *
     * @param request 发送验证码请求
     * @return 响应
     */
    @PostMapping("/send-code")
    @OperationLog(module = "认证", operationType = "SEND_CODE", description = "发送验证码")
    public ApiResponse<Map<String, Object>> sendCode(@Valid @RequestBody SendCodeRequest request) {
        String code = verificationCodeService.sendCode(request);

        Map<String, Object> data = new HashMap<>();
        // 降级策略下返回验证码(仅开发环境)
        if (code != null) {
            data.put("code", code);
            data.put("message", "验证码已发送(降级模式)");
        } else {
            data.put("message", "验证码已发送");
        }

        return ApiResponse.success(data);
    }
}
