-- 第三方绑定表
-- 用于存储会员与第三方账号(微信/支付宝/QQ)的绑定关系
CREATE TABLE `third_party_binding` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '绑定ID',
    `member_id` BIGINT NOT NULL COMMENT '会员ID',
    `platform_type` VARCHAR(20) NOT NULL COMMENT '平台类型(WECHAT/ALIPAY/QQ)',
    `open_id` VARCHAR(128) NOT NULL COMMENT '第三方OpenID',
    `union_id` VARCHAR(128) DEFAULT NULL COMMENT '第三方UnionID(微信)',
    `nickname` VARCHAR(100) DEFAULT NULL COMMENT '第三方昵称',
    `avatar` VARCHAR(255) DEFAULT NULL COMMENT '第三方头像URL',
    `bind_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '绑定时间',
    `unbind_time` DATETIME DEFAULT NULL COMMENT '解绑时间',
    `bind_status` TINYINT NOT NULL DEFAULT 1 COMMENT '绑定状态(1:已绑定,2:已解绑)',

    PRIMARY KEY (`id`),
    INDEX `idx_member_id` (`member_id`),
    UNIQUE KEY `uk_platform_openid` (`platform_type`, `open_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='第三方绑定表';
