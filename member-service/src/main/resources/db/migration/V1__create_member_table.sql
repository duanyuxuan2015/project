-- 会员表
-- 用于存储平台注册用户的基础信息和账号安全信息
CREATE TABLE `member` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '会员ID(主键)',
    `phone` VARCHAR(11) NOT NULL COMMENT '手机号',
    `password` VARCHAR(128) NOT NULL COMMENT '密码(BCrypt加密)',
    `nickname` VARCHAR(50) DEFAULT NULL COMMENT '昵称',
    `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
    `gender` TINYINT DEFAULT NULL COMMENT '性别(0:未知,1:男,2:女)',
    `birthday` DATE DEFAULT NULL COMMENT '出生日期',
    `register_type` VARCHAR(20) NOT NULL COMMENT '注册方式(PHONE/WECHAT/ALIPAY/QQ)',
    `register_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
    `account_status` TINYINT NOT NULL DEFAULT 1 COMMENT '账号状态(1:正常,2:冻结,3:未激活,4:已注销)',
    `last_login_time` DATETIME DEFAULT NULL COMMENT '最后登录时间',
    `last_login_ip` VARCHAR(50) DEFAULT NULL COMMENT '最后登录IP',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_phone` (`phone`),
    INDEX `idx_register_time` (`register_time`),
    INDEX `idx_account_status` (`account_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='会员表';
