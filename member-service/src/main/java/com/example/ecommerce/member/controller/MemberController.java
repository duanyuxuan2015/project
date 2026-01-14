package com.example.ecommerce.member.controller;

import com.example.ecommerce.member.annotation.OperationLog;
import com.example.ecommerce.member.dto.request.UpdateMemberRequest;
import com.example.ecommerce.member.dto.response.AddressResponse;
import com.example.ecommerce.member.dto.response.ApiResponse;
import com.example.ecommerce.member.dto.response.LoginHistoryResponse;
import com.example.ecommerce.member.dto.response.MemberInfoResponse;
import com.example.ecommerce.member.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 会员控制器
 */
@Slf4j
@RestController
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;

    @Autowired
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    /**
     * 获取会员信息
     *
     * @param request HTTP请求
     * @return 会员信息
     */
    @GetMapping("/info")
    @OperationLog(
            module = "会员",
            operationType = "QUERY",
            description = "查询会员信息"
    )
    public ApiResponse<MemberInfoResponse> getMemberInfo(HttpServletRequest request) {
        // TODO: 从JWT Token中获取会员ID
        // Long memberId = JwtUtil.getMemberIdFromToken(request);
        // 临时使用硬编码的会员ID用于测试
        Long memberId = 1L;

        MemberInfoResponse response = memberService.getMemberInfo(memberId);
        return ApiResponse.success(response);
    }

    /**
     * 更新会员信息
     *
     * @param request  更新请求
     * @param httpRequest HTTP请求
     * @return 响应
     */
    @PutMapping("/info")
    @OperationLog(
            module = "会员",
            operationType = "UPDATE",
            description = "更新会员信息",
            logRequest = true
    )
    public ApiResponse<Void> updateMemberInfo(
            @Valid @RequestBody UpdateMemberRequest request,
            HttpServletRequest httpRequest) {
        // TODO: 从JWT Token中获取会员ID
        Long memberId = 1L;

        memberService.updateMemberInfo(memberId, request);
        return ApiResponse.success();
    }

    /**
     * 获取登录记录
     *
     * @param limit 限制数量
     * @param request HTTP请求
     * @return 登录记录列表
     */
    @GetMapping("/login-history")
    @OperationLog(
            module = "会员",
            operationType = "QUERY",
            description = "查询登录记录"
    )
    public ApiResponse<List<LoginHistoryResponse>> getLoginHistory(
            @RequestParam(defaultValue = "10") Integer limit,
            HttpServletRequest request) {
        // TODO: 从JWT Token中获取会员ID
        Long memberId = 1L;

        List<LoginHistoryResponse> response = memberService.getLoginHistory(memberId, limit);
        return ApiResponse.success(response);
    }

    /**
     * 获取收货地址列表
     *
     * @param request HTTP请求
     * @return 收货地址列表
     */
    @GetMapping("/addresses")
    @OperationLog(
            module = "会员",
            operationType = "QUERY",
            description = "查询收货地址"
    )
    public ApiResponse<List<AddressResponse>> getAddresses(HttpServletRequest request) {
        // TODO: 从JWT Token中获取会员ID
        Long memberId = 1L;

        List<AddressResponse> response = memberService.getAddresses(memberId);
        return ApiResponse.success(response);
    }
}
