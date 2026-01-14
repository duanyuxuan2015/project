package com.example.ecommerce.member.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

/**
 * 更新会员信息请求DTO
 */
@Data
public class UpdateMemberRequest {

    /**
     * 昵称
     */
    @Size(min = 2, max = 50, message = "昵称长度必须在2-50个字符之间")
    private String nickname;

    /**
     * 性别(0:未知,1:男,2:女)
     */
    @Min(value = 0, message = "性别值不正确")
    @Max(value = 2, message = "性别值不正确")
    private Integer gender;

    /**
     * 出生日期
     */
    private LocalDate birthday;
}
