package com.example.ecommerce.member.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 登录记录响应DTO
 */
@Data
public class LoginHistoryResponse {

    /**
     * 登录时间
     */
    private LocalDateTime loginTime;

    /**
     * 登录方式
     */
    private String loginType;

    /**
     * 登录IP(脱敏)
     */
    private String loginIp;

    /**
     * IP地域
     */
    private String ipRegion;

    /**
     * 设备类型
     */
    private String deviceType;

    /**
     * 登录状态
     */
    private Integer loginStatus;

    /**
     * 失败原因
     */
    private String failReason;
}
