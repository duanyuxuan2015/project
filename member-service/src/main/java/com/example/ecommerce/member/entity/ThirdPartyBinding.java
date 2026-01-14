package com.example.ecommerce.member.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 第三方绑定实体类
 * 对应 third_party_binding 表
 */
@Data
@TableName("third_party_binding")
public class ThirdPartyBinding implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 绑定ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 会员ID
     */
    @TableField("member_id")
    private Long memberId;

    /**
     * 平台类型(WECHAT/ALIPAY/QQ)
     */
    @TableField("platform_type")
    private String platformType;

    /**
     * 第三方OpenID
     */
    @TableField("open_id")
    private String openId;

    /**
     * 第三方UnionID(微信)
     */
    @TableField("union_id")
    private String unionId;

    /**
     * 第三方昵称
     */
    @TableField("nickname")
    private String nickname;

    /**
     * 第三方头像URL
     */
    @TableField("avatar")
    private String avatar;

    /**
     * 绑定时间
     */
    @TableField(value = "bind_time", fill = FieldFill.INSERT)
    private LocalDateTime bindTime;

    /**
     * 解绑时间
     */
    @TableField("unbind_time")
    private LocalDateTime unbindTime;

    /**
     * 绑定状态(1:已绑定,2:已解绑)
     */
    @TableField("bind_status")
    private Integer bindStatus;
}
