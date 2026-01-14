package com.example.ecommerce.member.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 登录记录实体类
 * 对应 login_log 表
 */
@Data
@TableName("login_log")
public class LoginLog implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 会员ID
     */
    @TableField("member_id")
    private Long memberId;

    /**
     * 登录方式(PASSWORD/SMS/WECHAT/ALIPAY/QQ)
     */
    @TableField("login_type")
    private String loginType;

    /**
     * 登录时间
     */
    @TableField("login_time")
    private LocalDateTime loginTime;

    /**
     * 登录IP
     */
    @TableField("login_ip")
    private String loginIp;

    /**
     * IP地域(如"北京市朝阳区")
     */
    @TableField("ip_region")
    private String ipRegion;

    /**
     * 设备类型(iOS/Android/PC)
     */
    @TableField("device_type")
    private String deviceType;

    /**
     * 设备唯一标识
     */
    @TableField("device_id")
    private String deviceId;

    /**
     * 登录状态(1:成功,2:失败)
     */
    @TableField("login_status")
    private Integer loginStatus;

    /**
     * 失败原因(如"密码错误")
     */
    @TableField("fail_reason")
    private String failReason;
}
