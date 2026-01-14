package com.example.ecommerce.member.dto.response;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 会员信息响应DTO
 */
@Data
public class MemberInfoResponse {

    /**
     * 会员ID
     */
    private Long memberId;

    /**
     * 手机号(脱敏)
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
     * 性别(0:未知,1:男,2:女)
     */
    private Integer gender;

    /**
     * 出生日期
     */
    private LocalDate birthday;

    /**
     * 注册方式
     */
    private String registerType;

    /**
     * 注册时间
     */
    private LocalDateTime registerTime;

    /**
     * 账号状态
     */
    private Integer accountStatus;

    /**
     * 最后登录时间
     */
    private LocalDateTime lastLoginTime;

    /**
     * 最后登录IP(脱敏)
     */
    private String lastLoginIp;
}
