package com.example.ecommerce.member.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 注册响应DTO
 */
@Data
public class RegisterResponse {

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
     * 注册时间
     */
    private LocalDateTime registerTime;

    /**
     * JWT Token
     */
    private String token;

    /**
     * Token过期时间(秒)
     */
    private Long expiresIn;
}
