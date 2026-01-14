# Quickstart Guide: 电商会员系统基础功能

**Feature**: 电商会员系统基础功能
**Date**: 2026-01-14
**Audience**: 开发人员、测试人员

## 快速开始指南

本文档提供电商会员系统基础功能的快速搭建和验证指南,帮助开发者快速启动项目并验证核心功能。

---

## 1. 环境准备

### 1.1 必需软件

- **JDK**: 17+ (推荐使用Eclipse Temurin或Oracle JDK)
- **Maven**: 3.8+
- **MySQL**: 8.0+
- **Redis**: 7.0+
- **RocketMQ**: 5.x
- **IDE**: IntelliJ IDEA (推荐) 或 Eclipse

### 1.2 安装验证

```bash
# 验证Java版本
java -version

# 验证Maven版本
mvn -version

# 验证MySQL版本
mysql --version

# 验证Redis版本
redis-server --version

# 验证RocketMQ版本
mqadmin version
```

---

## 2. 项目初始化

### 2.1 创建Spring Boot项目

使用Spring Initializr (https://start.spring.io/) 创建项目:

**项目配置**:
- Project: Maven
- Language: Java
- Spring Boot: 3.2.x
- Group: `com.example.ecommerce`
- Artifact: `member-service`
- Name: `member-service`
- Package name: `com.example.ecommerce.member`
- Packaging: Jar
- Java: 17

**依赖选择**:
- Spring Web
- Spring Security
- Spring Data Redis
- Validation
- MySQL Driver
- Lombok

### 2.2 添加额外依赖

在`pom.xml`中添加:

```xml
<dependencies>
    <!-- Spring Boot Starter -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>

    <!-- Spring Security -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>

    <!-- Spring Data Redis -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-redis</artifactId>
    </dependency>

    <!-- MyBatis-Plus -->
    <dependency>
        <groupId>com.baomidou</groupId>
        <artifactId>mybatis-plus-spring-boot3-starter</artifactId>
        <version>3.5.5</version>
    </dependency>

    <!-- RocketMQ -->
    <dependency>
        <groupId>org.apache.rocketmq</groupId>
        <artifactId>rocketmq-spring-boot-starter</artifactId>
        <version>2.3.0</version>
    </dependency>

    <!-- JWT -->
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-api</artifactId>
        <version>0.12.3</version>
    </dependency>
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-impl</artifactId>
        <version>0.12.3</version>
        <scope>runtime</scope>
    </dependency>
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-jackson</artifactId>
        <version>0.12.3</version>
        <scope>runtime</scope>
    </dependency>

    <!-- BCrypt -->
    <dependency>
        <groupId>org.springframework.security</groupId>
        <artifactId>spring-security-crypto</artifactId>
    </dependency>

    <!-- Validation -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>

    <!-- Lombok -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>

    <!-- MySQL Driver -->
    <dependency>
        <groupId>com.mysql</groupId>
        <artifactId>mysql-connector-j</artifactId>
        <scope>runtime</scope>
    </dependency>

    <!-- Test -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

### 2.3 项目结构

```
member-service/
├── src/main/java/com/example/ecommerce/member/
│   ├── MemberApplication.java          # 启动类
│   ├── config/                         # 配置类
│   │   ├── SecurityConfig.java        # Spring Security配置
│   │   ├── MyBatisPlusConfig.java     # MyBatis-Plus配置
│   │   ├── RedisConfig.java           # Redis配置
│   │   └── RocketMQConfig.java        # RocketMQ配置
│   ├── controller/                     # 控制器
│   │   ├── MemberController.java      # 会员接口
│   │   ├── AuthController.java        # 认证接口
│   │   └── AddressController.java     # 地址接口
│   ├── service/                        # 服务层
│   │   ├── MemberService.java         # 会员服务
│   │   ├── AuthService.java           # 认证服务
│   │   └── AddressService.java        # 地址服务
│   ├── entity/                         # 实体类
│   │   ├── Member.java
│   │   ├── LoginLog.java
│   │   ├── Address.java
│   │   └── ThirdPartyBinding.java
│   ├── mapper/                         # MyBatis-Plus Mapper
│   │   ├── MemberMapper.java
│   │   ├── LoginLogMapper.java
│   │   └── AddressMapper.java
│   └── dto/                           # 数据传输对象
│       ├── request/
│       └── response/
├── src/main/resources/
│   ├── application.yml                # 配置文件
│   ├── application-dev.yml            # 开发环境配置
│   ├── application-prod.yml           # 生产环境配置
│   └── mapper/                        # MyBatis XML映射文件
└── src/test/
    └── java/
```

---

## 3. 配置文件

### 3.1 application.yml

```yaml
spring:
  application:
    name: member-service
  profiles:
    active: dev

  # 数据源配置
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/member_db?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: your_password
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 30000

  # Redis配置
  data:
    redis:
      host: localhost
      port: 6379
      password:
      database: 0
      lettuce:
        pool:
          max-active: 20
          max-idle: 10
          min-idle: 5

# MyBatis-Plus配置
mybatis-plus:
  configuration:
    map-underscore-to-camel-case: true
    log-impl: org.apache.ibatis.logging.slf4j.Slf4jImpl
  global-config:
    db-config:
      id-type: auto
  mapper-locations: classpath*:/mapper/**/*.xml

# RocketMQ配置
rocketmq:
  name-server: localhost:9876
  producer:
    group: member-service-producer
    send-message-timeout: 3000
    retry-times-when-send-failed: 2

# 服务器配置
server:
  port: 8080
  servlet:
    context-path: /v1

# JWT配置
jwt:
  secret: your-secret-key-at-least-256-bits-long
  expiration: 604800  # 7天(秒)
```

---

## 4. 数据库初始化

### 4.1 创建数据库

```sql
CREATE DATABASE member_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE member_db;
```

### 4.2 创建表

执行以下SQL脚本(参见`data-model.md`中的完整表结构):

```sql
-- 会员表
CREATE TABLE member (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    phone VARCHAR(11) NOT NULL UNIQUE COMMENT '手机号',
    password VARCHAR(128) NOT NULL COMMENT '密码(BCrypt加密)',
    nickname VARCHAR(50) COMMENT '昵称',
    avatar VARCHAR(255) COMMENT '头像URL',
    gender TINYINT COMMENT '性别(0:未知,1:男,2:女)',
    birthday DATE COMMENT '出生日期',
    register_type VARCHAR(20) NOT NULL COMMENT '注册方式(PHONE/WECHAT/ALIPAY/QQ)',
    register_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
    account_status TINYINT NOT NULL DEFAULT 1 COMMENT '账号状态(1:正常,2:冻结,3:未激活,4:已注销)',
    last_login_time DATETIME COMMENT '最后登录时间',
    last_login_ip VARCHAR(50) COMMENT '最后登录IP',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_phone (phone),
    INDEX idx_register_time (register_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会员表';

-- 登录记录表
CREATE TABLE login_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id BIGINT NOT NULL COMMENT '会员ID',
    login_type VARCHAR(20) NOT NULL COMMENT '登录方式(PASSWORD/SMS/WECHAT/ALIPAY/QQ)',
    login_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '登录时间',
    login_ip VARCHAR(50) COMMENT '登录IP',
    ip_region VARCHAR(100) COMMENT 'IP地域',
    device_type VARCHAR(50) COMMENT '设备类型(iOS/Android/PC)',
    device_id VARCHAR(100) COMMENT '设备唯一标识',
    login_status TINYINT NOT NULL DEFAULT 1 COMMENT '登录状态(1:成功,2:失败)',
    fail_reason VARCHAR(255) COMMENT '失败原因',
    INDEX idx_member_id_time (member_id, login_time DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='登录记录表';

-- 收货地址表
CREATE TABLE address (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id BIGINT NOT NULL COMMENT '会员ID',
    receiver_name VARCHAR(50) NOT NULL COMMENT '收货人姓名',
    receiver_phone VARCHAR(11) NOT NULL COMMENT '收货人手机号',
    province_code VARCHAR(20) NOT NULL COMMENT '省份编码',
    province_name VARCHAR(50) NOT NULL COMMENT '省份名称',
    city_code VARCHAR(20) NOT NULL COMMENT '城市编码',
    city_name VARCHAR(50) NOT NULL COMMENT '城市名称',
    district_code VARCHAR(20) NOT NULL COMMENT '区县编码',
    district_name VARCHAR(50) NOT NULL COMMENT '区县名称',
    detail_address VARCHAR(255) NOT NULL COMMENT '详细地址',
    postal_code VARCHAR(10) COMMENT '邮编',
    is_default TINYINT NOT NULL DEFAULT 0 COMMENT '是否默认地址(0:否,1:是)',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_member_id (member_id),
    INDEX idx_member_id_default (member_id, is_default)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收货地址表';

-- 第三方绑定表
CREATE TABLE third_party_binding (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id BIGINT NOT NULL COMMENT '会员ID',
    platform_type VARCHAR(20) NOT NULL COMMENT '平台类型(WECHAT/ALIPAY/QQ)',
    open_id VARCHAR(128) NOT NULL COMMENT '第三方OpenID',
    union_id VARCHAR(128) COMMENT '第三方UnionID(微信)',
    nickname VARCHAR(100) COMMENT '第三方昵称',
    avatar VARCHAR(255) COMMENT '第三方头像URL',
    bind_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '绑定时间',
    unbind_time DATETIME COMMENT '解绑时间',
    bind_status TINYINT NOT NULL DEFAULT 1 COMMENT '绑定状态(1:已绑定,2:已解绑)',
    UNIQUE KEY uk_platform_openid (platform_type, open_id),
    INDEX idx_member_id (member_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='第三方绑定表';
```

### 4.3 初始化测试数据

```sql
-- 插入测试会员(密码: Password123)
INSERT INTO member (phone, password, nickname, register_type)
VALUES ('13800138000', '$2a$10$N9qo8uLOickgx2ZMRZoMye1j/ph1U6lXx.HxvLq4nLdFqJw9qJq5u', '测试用户', 'PHONE');
```

---

## 5. 启动项目

### 5.1 启动依赖服务

```bash
# 启动MySQL
brew services start mysql  # macOS
# 或
systemctl start mysql     # Linux

# 启动Redis
redis-server

# 启动RocketMQ NameServer
sh bin/mqnamesrv

# 启动RocketMQ Broker
sh bin/mqbroker -n localhost:9876
```

### 5.2 启动应用

```bash
# 使用Maven编译运行
mvn spring-boot:run

# 或使用IDE运行MemberApplication.java
```

### 5.3 验证启动

访问健康检查接口:
```bash
curl http://localhost:8080/v1/actuator/health
```

预期响应:
```json
{
  "status": "UP"
}
```

---

## 6. API测试

### 6.1 注册接口

```bash
# 1. 发送验证码
curl -X POST http://localhost:8080/v1/verification-code/send \
  -H "Content-Type: application/json" \
  -d '{
    "phone": "13900139000",
    "type": "REGISTER"
  }'

# 2. 注册(验证码假设为123456)
curl -X POST http://localhost:8080/v1/member/register \
  -H "Content-Type: application/json" \
  -d '{
    "phone": "13900139000",
    "verificationCode": "123456",
    "password": "Password123",
    "agreePolicy": true
  }'
```

预期响应:
```json
{
  "code": 200,
  "message": "注册成功",
  "data": {
    "memberId": 123456,
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "expiresIn": 604800
  }
}
```

### 6.2 登录接口

```bash
# 密码登录
curl -X POST http://localhost:8080/v1/member/login \
  -H "Content-Type: application/json" \
  -d '{
    "phone": "13800138000",
    "password": "Password123"
  }'
```

### 6.3 查询会员信息

```bash
# 使用上一步获取的Token
curl -X GET http://localhost:8080/v1/member/info \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### 6.4 添加收货地址

```bash
curl -X POST http://localhost:8080/v1/address \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "receiverName": "张三",
    "receiverPhone": "13800138000",
    "provinceCode": "110000",
    "provinceName": "北京市",
    "cityCode": "110100",
    "cityName": "北京市",
    "districtCode": "110101",
    "districtName": "东城区",
    "detailAddress": "某某街道123号",
    "postalCode": "100000",
    "isDefault": false
  }'
```

---

## 7. 常见问题

### 7.1 数据库连接失败

**问题**: `java.sql.SQLException: Access denied for user`

**解决**: 检查`application.yml`中的数据库用户名和密码是否正确。

### 7.2 Redis连接失败

**问题**: `io.lettuce.core.RedisConnectionException`

**解决**:
1. 确认Redis服务已启动: `redis-cli ping`
2. 检查Redis端口是否正确(默认6379)
3. 如果Redis设置了密码,在配置文件中添加`spring.data.redis.password`

### 7.3 RocketMQ连接失败

**问题**: `org.apache.rocketmq.client.exception.MQClientException`

**解决**:
1. 确认RocketMQ NameServer已启动: `mqadmin clusterList -n localhost:9876`
2. 检查NameServer地址是否正确(默认9876)

### 7.4 JWT验证失败

**问题**: `io.jsonwebtoken.security.SecurityException`

**解决**: 确保`jwt.secret`配置的密钥长度至少256位。

---

## 8. 性能测试

### 8.1 使用JMeter进行压测

1. 下载JMeter: https://jmeter.apache.org/download_jmeter.cgi
2. 导入测试计划(需单独创建)
3. 配置线程组:
   - 线程数: 1000
   - Ramp-Up时间: 10秒
   - 循环次数: 10
4. 运行测试并查看结果

### 8.2 预期性能指标

- 注册/登录接口: 10000 TPS, 响应时间≤1秒(95分位)
- 信息查询: 5000 TPS, 响应时间≤500ms(95分位)

---

## 9. 下一步

- 查看完整API文档: `contracts/member-api.yaml`
- 查看数据模型: `data-model.md`
- 查看技术调研: `research.md`
- 运行单元测试: `mvn test`
- 运行集成测试: `mvn verify`

---

## 10. 联系与支持

- 技术支持: member-team@example.com
- 文档仓库: https://github.com/example/member-service
- 问题反馈: https://github.com/example/member-service/issues
