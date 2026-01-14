-- 验证码表
-- 用于存储短信验证码记录,支持注册、登录、密码重置等场景
CREATE TABLE `verification_code` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `phone` VARCHAR(11) NOT NULL COMMENT '手机号',
    `code` VARCHAR(6) NOT NULL COMMENT '验证码(明文,仅开发环境)',
    `code_hash` VARCHAR(128) NOT NULL COMMENT '验证码哈希(BCrypt,生产环境)',
    `type` VARCHAR(20) NOT NULL COMMENT '类型(REGISTER/LOGIN/RESET_PASSWORD)',
    `expire_time` DATETIME NOT NULL COMMENT '过期时间',
    `used` TINYINT NOT NULL DEFAULT 0 COMMENT '是否已使用(0:未使用,1:已使用)',
    `send_count` INT NOT NULL DEFAULT 1 COMMENT '发送次数(1分钟内限制)',
    `ip` VARCHAR(50) DEFAULT NULL COMMENT '请求IP',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',

    PRIMARY KEY (`id`),
    INDEX `idx_phone_type` (`phone`, `type`),
    INDEX `idx_expire_time` (`expire_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='验证码表';
