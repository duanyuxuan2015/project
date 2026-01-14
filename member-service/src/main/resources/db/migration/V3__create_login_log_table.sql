-- 登录记录表
-- 用于存储会员登录历史记录,支持安全审计和异常登录检测
CREATE TABLE `login_log` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `member_id` BIGINT NOT NULL COMMENT '会员ID',
    `login_type` VARCHAR(20) NOT NULL COMMENT '登录方式(PASSWORD/SMS/WECHAT/ALIPAY/QQ)',
    `login_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '登录时间',
    `login_ip` VARCHAR(50) DEFAULT NULL COMMENT '登录IP',
    `ip_region` VARCHAR(100) DEFAULT NULL COMMENT 'IP地域(如"北京市朝阳区")',
    `device_type` VARCHAR(50) DEFAULT NULL COMMENT '设备类型(iOS/Android/PC)',
    `device_id` VARCHAR(100) DEFAULT NULL COMMENT '设备唯一标识',
    `login_status` TINYINT NOT NULL DEFAULT 1 COMMENT '登录状态(1:成功,2:失败)',
    `fail_reason` VARCHAR(255) DEFAULT NULL COMMENT '失败原因(如"密码错误")',

    PRIMARY KEY (`id`),
    INDEX `idx_member_id_time` (`member_id`, `login_time` DESC),
    INDEX `idx_login_ip` (`login_ip`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='登录记录表';
