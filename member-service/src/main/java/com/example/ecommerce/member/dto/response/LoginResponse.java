package com.example.ecommerce.member.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 登录响应DTO
 */
@Data
public class LoginResponse {

    /**
     * 会员ID
     */
    private Long memberId;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 头像URL
     */
    private String avatar;

    /**
     * 账号状态(1:正常,2:冻结,3:未激活,4:已注销)
     */
    private Integer accountStatus;

    /**
     * 最后登录时间
     */
    private LocalDateTime lastLoginTime;

    /**
     * JWT Token
     */
    private String token;

    /**
     * Token过期时间(秒)
     */
    private Long expiresIn;
}
