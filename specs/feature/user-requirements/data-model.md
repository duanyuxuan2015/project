# Data Model: 电商会员系统基础功能

**Feature**: 电商会员系统基础功能
**Date**: 2026-01-14
**Phase**: Phase 1 - 数据模型设计

## 数据模型概述

本文档定义电商会员系统基础功能的数据库表结构、实体关系、字段定义及验证规则。持久层采用MyBatis-Plus 3.5。

---

## 1. 核心实体

### 1.1 会员(Member)

**描述**: 平台注册用户,包含会员基础信息、账号安全信息。

**表名**: `member`

**字段定义**:

| 字段名 | 类型 | 长度 | 可空 | 默认值 | 说明 |
|--------|------|------|------|--------|------|
| id | BIGINT | - | NO | AUTO_INCREMENT | 会员ID(主键) |
| phone | VARCHAR | 11 | NO | - | 手机号(唯一索引) |
| password | VARCHAR | 128 | NO | - | 密码(BCrypt加密) |
| nickname | VARCHAR | 50 | YES | NULL | 昵称 |
| avatar | VARCHAR | 255 | YES | NULL | 头像URL |
| gender | TINYINT | - | YES | NULL | 性别(0:未知,1:男,2:女) |
| birthday | DATE | - | YES | NULL | 出生日期 |
| register_type | VARCHAR | 20 | NO | - | 注册方式(PHONE/WECHAT/ALIPAY/QQ) |
| register_time | DATETIME | - | NO | CURRENT_TIMESTAMP | 注册时间 |
| account_status | TINYINT | - | NO | 1 | 账号状态(1:正常,2:冻结,3:未激活,4:已注销) |
| last_login_time | DATETIME | - | YES | NULL | 最后登录时间 |
| last_login_ip | VARCHAR | 50 | YES | NULL | 最后登录IP |
| create_time | DATETIME | - | NO | CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | - | NO | CURRENT_TIMESTAMP ON UPDATE | 更新时间 |

**索引**:
- PRIMARY KEY: `id`
- UNIQUE KEY: `uk_phone` (`phone`)
- INDEX: `idx_register_time` (`register_time`)
- INDEX: `idx_account_status` (`account_status`)

**验证规则**:
- phone: 必须符合中国大陆手机号规则(11位数字,以13/14/15/17/18/19开头)
- password: 必须经过BCrypt加密,不可明文存储
- nickname: 长度2-50个字符,支持中文、字母、数字、下划线
- gender: 枚举值(0,1,2)
- account_status: 枚举值(1,2,3,4)

---

### 1.2 验证码(VerificationCode)

**描述**: 短信验证码记录,用于注册、登录、密码重置等场景。

**表名**: `verification_code`

**字段定义**:

| 字段名 | 类型 | 长度 | 可空 | 默认值 | 说明 |
|--------|------|------|------|--------|------|
| id | BIGINT | - | NO | AUTO_INCREMENT | 主键 |
| phone | VARCHAR | 11 | NO | - | 手机号 |
| code | VARCHAR | 6 | NO | - | 验证码(明文,仅开发环境) |
| code_hash | VARCHAR | 128 | NO | - | 验证码哈希(BCrypt,生产环境) |
| type | VARCHAR | 20 | NO | - | 类型(REGISTER/LOGIN/RESET_PASSWORD) |
| expire_time | DATETIME | - | NO | - | 过期时间 |
| used | TINYINT | - | NO | 0 | 是否已使用(0:未使用,1:已使用) |
| send_count | INT | - | NO | 1 | 发送次数(1分钟内限制) |
| ip | VARCHAR | 50 | YES | NULL | 请求IP |
| create_time | DATETIME | - | NO | CURRENT_TIMESTAMP | 创建时间 |

**索引**:
- PRIMARY KEY: `id`
- INDEX: `idx_phone_type` (`phone`, `type`)
- INDEX: `idx_expire_time` (`expire_time`)

**Redis缓存**:
- Key: `verification_code:{phone}:{type}`
- Value: `{"code": "123456", "expire_time": "2026-01-14 10:05:00", "send_count": 1}`
- TTL: 300秒(5分钟)

**验证规则**:
- phone: 必须符合手机号规则
- code: 6位数字
- type: 枚举值(REGISTER, LOGIN, RESET_PASSWORD)
- expire_time: 创建时间+5分钟
- send_count: 同一手机号1小时内最多5次

---

### 1.3 登录记录(LoginLog)

**描述**: 会员登录历史记录,用于安全审计和异常登录检测。

**表名**: `login_log`

**字段定义**:

| 字段名 | 类型 | 长度 | 可空 | 默认值 | 说明 |
|--------|------|------|------|--------|------|
| id | BIGINT | - | NO | AUTO_INCREMENT | 主键 |
| member_id | BIGINT | - | NO | - | 会员ID |
| login_type | VARCHAR | 20 | NO | - | 登录方式(PASSWORD/SMS/WECHAT/ALIPAY/QQ) |
| login_time | DATETIME | - | NO | CURRENT_TIMESTAMP | 登录时间 |
| login_ip | VARCHAR | 50 | YES | NULL | 登录IP |
| ip_region | VARCHAR | 100 | YES | NULL | IP地域(如"北京市朝阳区") |
| device_type | VARCHAR | 50 | YES | NULL | 设备类型(iOS/Android/PC) |
| device_id | VARCHAR | 100 | YES | NULL | 设备唯一标识 |
| login_status | TINYINT | - | NO | 1 | 登录状态(1:成功,2:失败) |
| fail_reason | VARCHAR | 255 | YES | NULL | 失败原因(如"密码错误") |

**索引**:
- PRIMARY KEY: `id`
- INDEX: `idx_member_id_time` (`member_id`, `login_time` DESC)
- INDEX: `idx_login_ip` (`login_ip`)

**验证规则**:
- member_id: 外键,关联member表
- login_type: 枚举值(PASSWORD, SMS, WECHAT, ALIPAY, QQ)
- login_status: 枚举值(1,2)

**数据保留策略**:
- 保留最近6个月登录记录
- 超过6个月的数据异步归档到冷存储

---

### 1.4 收货地址(Address)

**描述**: 会员收货地址信息。

**表名**: `address`

**字段定义**:

| 字段名 | 类型 | 长度 | 可空 | 默认值 | 说明 |
|--------|------|------|------|--------|------|
| id | BIGINT | - | NO | AUTO_INCREMENT | 地址ID |
| member_id | BIGINT | - | NO | - | 会员ID |
| receiver_name | VARCHAR | 50 | NO | - | 收货人姓名 |
| receiver_phone | VARCHAR | 11 | NO | - | 收货人手机号 |
| province_code | VARCHAR | 20 | NO | - | 省份编码 |
| province_name | VARCHAR | 50 | NO | - | 省份名称 |
| city_code | VARCHAR | 20 | NO | - | 城市编码 |
| city_name | VARCHAR | 50 | NO | - | 城市名称 |
| district_code | VARCHAR | 20 | NO | - | 区县编码 |
| district_name | VARCHAR | 50 | NO | - | 区县名称 |
| detail_address | VARCHAR | 255 | NO | - | 详细地址 |
| postal_code | VARCHAR | 10 | YES | NULL | 邮编 |
| is_default | TINYINT | - | NO | 0 | 是否默认地址(0:否,1:是) |
| create_time | DATETIME | - | NO | CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | - | NO | CURRENT_TIMESTAMP ON UPDATE | 更新时间 |

**索引**:
- PRIMARY KEY: `id`
- INDEX: `idx_member_id` (`member_id`)
- INDEX: `idx_member_id_default` (`member_id`, `is_default`)

**验证规则**:
- member_id: 外键,关联member表
- receiver_phone: 必须符合手机号规则
- is_default: 枚举值(0,1)
- 同一会员只能有一个默认地址

---

### 1.5 第三方绑定(ThirdPartyBinding)

**描述**: 会员与第三方账号(微信/支付宝/QQ)的绑定关系。

**表名**: `third_party_binding`

**字段定义**:

| 字段名 | 类型 | 长度 | 可空 | 默认值 | 说明 |
|--------|------|------|------|--------|------|
| id | BIGINT | - | NO | AUTO_INCREMENT | 绑定ID |
| member_id | BIGINT | - | NO | - | 会员ID |
| platform_type | VARCHAR | 20 | NO | - | 平台类型(WECHAT/ALIPAY/QQ) |
| open_id | VARCHAR | 128 | NO | - | 第三方OpenID |
| union_id | VARCHAR | 128 | YES | NULL | 第三方UnionID(微信) |
| nickname | VARCHAR | 100 | YES | NULL | 第三方昵称 |
| avatar | VARCHAR | 255 | YES | NULL | 第三方头像URL |
| bind_time | DATETIME | - | NO | CURRENT_TIMESTAMP | 绑定时间 |
| unbind_time | DATETIME | - | YES | NULL | 解绑时间 |
| bind_status | TINYINT | - | NO | 1 | 绑定状态(1:已绑定,2:已解绑) |

**索引**:
- PRIMARY KEY: `id`
- INDEX: `idx_member_id` (`member_id`)
- UNIQUE KEY: `uk_platform_openid` (`platform_type`, `open_id`)

**验证规则**:
- member_id: 外键,关联member表
- platform_type: 枚举值(WECHAT, ALIPAY, QQ)
- open_id: 同一平台类型下唯一
- bind_status: 枚举值(1,2)

**业务规则**:
- 同一第三方账号(OpenID)只能绑定一个会员
- 一个会员可以绑定多个第三方账号
- 解绑后需至少保留一种登录方式

---

## 2. 实体关系图(ERD)

```
member (会员)
  ├── 1:N login_log (登录记录)
  ├── 1:N address (收货地址)
  ├── 1:N third_party_binding (第三方绑定)
  └── 1:N verification_code (验证码,逻辑关联)
```

**关系说明**:
- 一个会员有多条登录记录
- 一个会员有多个收货地址
- 一个会员可以绑定多个第三方账号

---

## 3. 数据模型设计原则

### 3.1 命名规范
- 表名: 小写蛇形命名法(如`third_party_binding`)
- 字段名: 小写蛇形命名法(如`receiver_name`)
- 索引名: `idx_`字段名(普通索引), `uk_`字段名(唯一索引)

### 3.2 数据类型选择
- 主键: BIGINT,支持海量数据
- 金额: DECIMAL(10,2),避免精度丢失
- 时间: DATETIME,精确到秒
- 枚举: TINYINT,节省存储空间

### 3.3 性能优化
- 所有表设置主键
- 高频查询字段建立索引(如phone, member_id)
- 大表(如login_log)定期归档
- 敏感字段(如password)加密存储

### 3.4 数据安全
- 密码BCrypt加密,不可逆向解密
- 手机号、地址等敏感信息脱敏显示
- 登录日志完整记录,支持安全审计

---

## 4. 未来扩展

### 4.1 分库分表
- member表按member_id哈希分表(如member_0, member_1...)
- login_log表按时间分表(如login_log_202601, login_log_202602...)

### 4.2 读写分离
- 主库负责写操作
- 从库负责读操作(如信息查询)

### 4.3 新增表
- member_level: 会员等级表(普通会员、银卡、金卡、钻石)
- member_points: 会员积分表
- member_coupon: 会员优惠券表

---

## 5. 总结

本数据模型设计遵循以下原则:
- **规范化**: 符合第三范式,减少数据冗余
- **性能优化**: 合理索引,支持高并发查询
- **安全合规**: 密码加密,敏感信息脱敏
- **可扩展性**: 预留分库分表、读写分离能力
- **审计追溯**: 登录日志完整记录

所有表结构已验证,符合电商微服务宪章的数据存储要求。
