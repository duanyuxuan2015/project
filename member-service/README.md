# Member Service

电商会员系统基础功能微服务

## 功能特性

- 会员注册(手机号+验证码、第三方登录)
- 会员登录(密码登录、验证码登录、第三方登录)
- 会员信息查询与修改
- 收货地址管理
- 账号注销

## 技术栈

- Java 17
- Spring Boot 3.2
- Spring Security
- MyBatis-Plus 3.5
- Redis 7.0
- RocketMQ 5.x
- MySQL 8.0
- JWT

## 快速开始

### 环境要求

- JDK 17+
- Maven 3.8+
- MySQL 8.0+
- Redis 7.0+
- RocketMQ 5.x

### 数据库初始化

1. 创建数据库:
```sql
CREATE DATABASE member_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. 执行数据库迁移脚本:
```bash
# 脚本位于 src/main/resources/db/migration/
```

### 配置修改

修改 `src/main/resources/application-dev.yml` 中的数据库和Redis连接信息。

### 运行项目

```bash
mvn spring-boot:run
```

### 访问服务

- 服务地址: http://localhost:8080/v1
- 健康检查: http://localhost:8080/v1/actuator/health

## API文档

请查看 `specs/feature/user-requirements/contracts/member-api.yaml`

## 开发指南

请参考 `specs/feature/user-requirements/quickstart.md`

## 许可证

Copyright © 2026 Example E-Commerce. All rights reserved.
