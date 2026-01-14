-- 收货地址表
-- 用于存储会员收货地址信息
CREATE TABLE `address` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '地址ID',
    `member_id` BIGINT NOT NULL COMMENT '会员ID',
    `receiver_name` VARCHAR(50) NOT NULL COMMENT '收货人姓名',
    `receiver_phone` VARCHAR(11) NOT NULL COMMENT '收货人手机号',
    `province_code` VARCHAR(20) NOT NULL COMMENT '省份编码',
    `province_name` VARCHAR(50) NOT NULL COMMENT '省份名称',
    `city_code` VARCHAR(20) NOT NULL COMMENT '城市编码',
    `city_name` VARCHAR(50) NOT NULL COMMENT '城市名称',
    `district_code` VARCHAR(20) NOT NULL COMMENT '区县编码',
    `district_name` VARCHAR(50) NOT NULL COMMENT '区县名称',
    `detail_address` VARCHAR(255) NOT NULL COMMENT '详细地址',
    `postal_code` VARCHAR(10) DEFAULT NULL COMMENT '邮编',
    `is_default` TINYINT NOT NULL DEFAULT 0 COMMENT '是否默认地址(0:否,1:是)',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

    PRIMARY KEY (`id`),
    INDEX `idx_member_id` (`member_id`),
    INDEX `idx_member_id_default` (`member_id`, `is_default`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='收货地址表';
