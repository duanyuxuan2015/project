package com.example.ecommerce.member.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 验证码实体类
 * 对应 verification_code 表
 */
@Data
@TableName("verification_code")
public class VerificationCode implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 手机号
     */
    @TableField("phone")
    private String phone;

    /**
     * 验证码(明文,仅开发环境)
     */
    @TableField("code")
    private String code;

    /**
     * 验证码哈希(BCrypt,生产环境)
     */
    @TableField("code_hash")
    private String codeHash;

    /**
     * 类型(REGISTER/LOGIN/RESET_PASSWORD)
     */
    @TableField("type")
    private String type;

    /**
     * 过期时间
     */
    @TableField("expire_time")
    private LocalDateTime expireTime;

    /**
     * 是否已使用(0:未使用,1:已使用)
     */
    @TableField("used")
    private Integer used;

    /**
     * 发送次数(1分钟内限制)
     */
    @TableField("send_count")
    private Integer sendCount;

    /**
     * 请求IP
     */
    @TableField("ip")
    private String ip;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
