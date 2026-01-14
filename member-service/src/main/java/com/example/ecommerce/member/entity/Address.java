package com.example.ecommerce.member.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 收货地址实体类
 * 对应 address 表
 */
@Data
@TableName("address")
public class Address implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 地址ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 会员ID
     */
    @TableField("member_id")
    private Long memberId;

    /**
     * 收货人姓名
     */
    @TableField("receiver_name")
    private String receiverName;

    /**
     * 收货人手机号
     */
    @TableField("receiver_phone")
    private String receiverPhone;

    /**
     * 省份编码
     */
    @TableField("province_code")
    private String provinceCode;

    /**
     * 省份名称
     */
    @TableField("province_name")
    private String provinceName;

    /**
     * 城市编码
     */
    @TableField("city_code")
    private String cityCode;

    /**
     * 城市名称
     */
    @TableField("city_name")
    private String cityName;

    /**
     * 区县编码
     */
    @TableField("district_code")
    private String districtCode;

    /**
     * 区县名称
     */
    @TableField("district_name")
    private String districtName;

    /**
     * 详细地址
     */
    @TableField("detail_address")
    private String detailAddress;

    /**
     * 邮编
     */
    @TableField("postal_code")
    private String postalCode;

    /**
     * 是否默认地址(0:否,1:是)
     */
    @TableField("is_default")
    private Integer isDefault;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
