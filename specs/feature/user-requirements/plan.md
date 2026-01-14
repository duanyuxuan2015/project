# Implementation Plan: 电商会员系统基础功能

**Branch**: `feature/user-requirements` | **Date**: 2026-01-14 | **Spec**: [spec.md](./spec.md)
**Input**: Feature specification from `/specs/feature/user-requirements/spec.md`

**Note**: This template is filled in by the `/speckit.plan` command. See `.specify/templates/commands/plan.md` for the execution workflow.

## Summary

建设电商会员系统基础功能,包括会员注册、登录、信息查询与修改等核心能力。支持手机号+验证码/密码登录、第三方快捷登录(微信/支付宝/QQ)、会员信息管理、收货地址管理等。采用微服务架构,会员服务作为独立业务域,遵循RESTful接口设计,支持高并发(10000 TPS注册/登录)和信息安全要求(BCrypt密码加密、敏感信息脱敏、HTTPS传输)。

## Technical Context

**Language/Version**: Java 17 (Spring Boot 3.x)
**Primary Dependencies**:
  - Spring Boot 3.2 (Web, Security, Data Redis)
  - Spring Cloud (服务注册发现、配置中心)
  - MyBatis-Plus 3.5 (ORM)
  - Redis 7.0 (缓存、分布式锁)
  - RocketMQ 5.x (消息队列)
  - JWT (Token认证)
  - BCrypt (密码加密)
  - Aliyun SMS (短信验证码)

**Storage**:
  - MySQL 8.0 (主数据库,会员信息、登录记录、收货地址)
  - Redis 7.0 (缓存、Session、验证码、分布式锁)
  - RocketMQ (异步消息)

**Testing**:
  - JUnit 5 + Mockito (单元测试)
  - TestContainers (集成测试)
  - JMeter (性能测试)
  - Postman/Newman (契约测试)

**Target Platform**: Linux服务器(Kubernetes部署)
**Project Type**: 微服务(Web应用 - 后端服务)
**Performance Goals**:
  - 注册/登录接口: 10000 TPS (峰值), 响应时间≤1秒 (95分位)
  - 信息查询/修改: 5000 TPS, 响应时间≤500ms (95分位)
  - 页面加载: ≤2秒 (PC端), ≤1.5秒 (移动端)

**Constraints**:
  - 密码必须采用BCrypt加密存储(已澄清)
  - 敏感信息(手机号、收货地址)传输必须采用HTTPS加密
  - 敏感信息存储时必须脱敏处理
  - 必须支持防SQL注入、防XSS攻击
  - 必须记录所有关键操作日志(INFO级别记录关键操作,ERROR级别记录失败)
  - 头像上传限制: 文件大小≤2MB, 仅支持JPG/PNG格式
  - 短信服务故障时降级为图形验证码+弱密码策略(密码长度6-20位即可)
  - 并发注册冲突处理: 通过数据库唯一索引约束保证一致性

**Scale/Scope**:
  - 预估会员数量: 100万+
  - 并发注册/登录: 10000 TPS (峰值)
  - 并发信息查询/修改: 5000 TPS
  - 数据库表数量: 约5张核心表(member, login_log, address, third_party_binding, verification_code)
  - API接口数量: 约20个核心接口

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

基于电商微服务宪章原则的符合性检查:

- [x] **P1 用户价值优先**:
  - [x] 功能是否解决电商真实用户问题(C端/B端/供应链)? - 是,覆盖会员注册、登录、信息管理等C端核心场景
  - [x] 是否定义电商场景化成功标准(转化率、支付成功率等)? - 是,定义注册成功率≥98%,登录成功率≥95%
  - [x] 是否平衡多角色价值(消费者/商户/平台)? - 是,消费者体验优先,同时保障平台安全合规
- [x] **P2 测试驱动开发**:
  - [x] 核心场景(注册/登录)测试策略是否明确? - 是,契约测试(第三方登录)、集成测试(用户旅程)、性能测试(10000 TPS)
  - [x] 是否覆盖契约测试(微服务间通信)? - 是,第三方登录(微信/支付宝/QQ)需契约测试
  - [x] 是否包含性能测试要求(TPS、响应时间)? - 是,10000 TPS注册/登录,响应时间≤1秒(95分位)
  - [x] 是否包含资损风险测试? - N/A,会员系统不涉及资损(无支付功能)
- [x] **P3 简洁性原则**:
  - [x] 是否选择最简单可行的方案? - 是,选择Spring Boot+MyBatis-Plus成熟技术栈,避免过度工程
  - [x] 微服务拆分粒度是否合理(避免过细拆分)? - 是,会员服务作为独立业务域,功能内聚
  - [x] 复杂性是否有明确理由并获得批准? - N/A,无引入复杂性的决策
- [x] **P4 渐进式交付**:
  - [x] 用户故事是否标记优先级(P0/P1/P2)? - 是,5个用户故事标记P0/P1/P2优先级
  - [x] 核心交易流程是否可独立测试(MVP闭环)? - 是,注册(P0)和登录(P0)可独立实现和测试
  - [x] 是否支持灰度发布策略? - 是,支持按用户/商户灰度发布(宪章要求)
- [x] **P5 可观测性**:
  - [x] 是否定义业务KPI(转化率、支付成功率)? - 是,注册成功率≥98%,登录成功率≥95%
  - [x] 是否定义技术KPI(TPS、响应时间、缓存命中率)? - 是,10000 TPS,响应时间≤1秒
  - [x] 是否配置资损监控告警? - N/A,会员系统不涉及资损
  - [x] 日志是否包含核心维度(会员ID/手机号/操作IP)? - 是,INFO级别记录关键操作(注册/登录/信息修改),ERROR级别记录失败,日志包含会员ID、手机号、IP等核心维度

> **复杂性跟踪**: 无违反简洁性原则的决策,无需记录
>
> **性能基线**: 核心接口需通过压测,达到预设性能基线(注册/登录接口支持10000 TPS,99%响应时间<1秒)

## Project Structure

### Documentation (this feature)

```text
specs/feature/user-requirements/
├── plan.md              # This file (/speckit.plan command output)
├── spec.md              # Feature specification
├── research.md          # Phase 0 output (/speckit.plan command)
├── data-model.md        # Phase 1 output (/speckit.plan command)
├── quickstart.md        # Phase 1 output (/speckit.plan command)
├── contracts/           # Phase 1 output (/speckit.plan command)
│   └── member-api.yaml  # OpenAPI 3.0 specification
└── tasks.md             # Phase 2 output (/speckit.tasks command - NOT created yet)
```

### Source Code (repository root)

```text
# Option 2: Web application (backend microservice)
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
│   ├── entity/                         # 实体类(MyBatis-Plus)
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
│   └── application-prod.yml           # 生产环境配置
└── src/test/
    └── java/
        ├── unit/                       # 单元测试
        ├── integration/                # 集成测试
        └── contract/                   # 契约测试
```

**Structure Decision**: 选择微服务后端架构(Option 2),会员服务作为独立微服务部署。前端由独立的前端团队负责,通过RESTful API与会员服务交互。

## Complexity Tracking

> **Fill ONLY if Constitution Check has violations that must be justified**

无违反简洁性原则的决策,此部分留空。

---

## Phase 0: Research Summary

### 研究完成项

✅ 技术栈选型:
- Java 17 + Spring Boot 3.2微服务架构
- MyBatis-Plus 3.5持久层
- RocketMQ 5.x消息队列
- BCrypt密码加密

✅ 架构设计:
- 微服务拆分:会员服务作为独立业务域
- 缓存策略:Redis多层缓存
- 异步处理:RocketMQ处理登录日志、注册欢迎消息
- 安全策略:BCrypt+HTTPS+脱敏+防SQL注入/XSS

✅ 性能优化:
- 数据库索引优化(手机号唯一索引、会员ID+登录时间复合索引)
- Redis缓存热点数据(会员信息、Session、验证码)
- 并发控制:数据库唯一索引约束防止并发注册冲突

✅ 降级策略:
- 短信服务故障:降级为图形验证码+弱密码策略
- 缓存降级:缓存穿透/击穿/雪崩防范

详见: [research.md](./research.md)

---

## Phase 1: Design Artifacts

### Data Model

✅ 已完成数据模型设计,包含5个核心实体:
- Member(会员)
- VerificationCode(验证码)
- LoginLog(登录记录)
- Address(收货地址)
- ThirdPartyBinding(第三方绑定)

详见: [data-model.md](./data-model.md)

### API Contracts

✅ 已完成OpenAPI 3.0规范,包含20+个RESTful接口:
- 注册接口:手机号注册、第三方注册
- 登录接口:密码登录、验证码登录、第三方登录
- 会员信息接口:查询、修改
- 收货地址接口:查询、添加、修改、删除、设置默认

详见: [contracts/member-api.yaml](./contracts/member-api.yaml)

### Quickstart Guide

✅ 已完成快速开始指南,包含:
- 环境准备
- 项目初始化
- 配置文件
- 数据库初始化
- API测试示例

详见: [quickstart.md](./quickstart.md)

---

## Phase 2: Tasks Generation

### 下一步

运行以下命令生成任务列表:

```bash
/speckit.tasks
```

任务列表将包含:
- Phase 1: 项目初始化
- Phase 2: 基础设施(数据库、Redis、RocketMQ)
- Phase 3: User Story 1 - 会员注册(P0)
- Phase 4: User Story 2 - 会员登录(P0)
- Phase 5: User Story 3 - 会员信息查询(P1)
- Phase 6: User Story 4 - 会员信息修改(P1)
- Phase 7: User Story 5 - 账号注销(P2)
- Phase 8: 性能测试与优化
