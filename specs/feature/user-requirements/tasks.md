---

description: "Task list for 电商会员系统基础功能 implementation"
---

# Tasks: 电商会员系统基础功能

**Input**: Design documents from `/specs/feature/user-requirements/`
**Prerequisites**: plan.md (required), spec.md (required for user stories), research.md, data-model.md, contracts/

**Tests**: 本项目包含测试任务。宪章要求TDD强制执行,测试必须先写并获得产品/运营批准,确保测试失败后才实现功能。

**Organization**: 任务按用户故事组织,以支持每个故事的独立实现和测试。

## Format: `[ID] [P?] [Story] Description`

- **[P]**: 可并行运行(不同文件,无依赖)
- **[Story]**: 任务所属用户故事(US1, US2, US3, US4, US5)
- 包含精确文件路径

## Path Conventions

- **微服务后端**: `member-service/src/main/java/...` (Java后端服务)
- **资源文件**: `member-service/src/main/resources/...`
- **测试**: `member-service/src/test/java/...`

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: 项目初始化和基础结构搭建

- [X] T001 创建member-service项目目录结构
- [X] T002 生成Spring Boot 3.2项目骨架(Maven/Gradle)
- [X] T003 [P] 配置pom.xml,添加依赖(Spring Boot Web, Security, Data Redis, MyBatis-Plus 3.5, RocketMQ, JWT, BCrypt, Validation)
- [X] T004 [P] 创建application.yml主配置文件
- [X] T005 [P] 创建application-dev.yml开发环境配置
- [X] T006 [P] 创建application-prod.yml生产环境配置
- [X] T007 [P] 配置Git忽略文件(.gitignore)
- [X] T008 [P] 创建README.md项目说明文档

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: 核心基础设施,MUST完成之前所有用户故事才能开始

**⚠️ CRITICAL**: 此阶段完成前,用户故事工作无法开始

### 数据库基础设施

- [X] T009 编写member表DDL脚本在member-service/src/main/resources/db/migration/V1__create_member_table.sql
- [X] T010 [P] 编写verification_code表DDL脚本在member-service/src/main/resources/db/migration/V2__create_verification_code_table.sql
- [X] T011 [P] 编写login_log表DDL脚本在member-service/src/main/resources/db/migration/V3__create_login_log_table.sql
- [X] T012 [P] 编写address表DDL脚本在member-service/src/main/resources/db/migration/V4__create_address_table.sql
- [X] T013 [P] 编写third_party_binding表DDL脚本在member-service/src/main/resources/db/migration/V5__create_third_party_binding_table.sql
- [X] T014 创建MySQL数据库member_db并执行所有DDL脚本

### 配置基础设施

- [X] T015 创建MyBatis-Plus配置类在member-service/src/main/java/.../config/MyBatisPlusConfig.java
- [X] T016 [P] 创建Redis配置类在member-service/src/main/java/.../config/RedisConfig.java
- [X] T017 [P] 创建RocketMQ配置类在member-service/src/main/java/.../config/RocketMQConfig.java
- [X] T018 [P] 创建Spring Security配置类在member-service/src/main/java/.../config/SecurityConfig.java
- [X] T019 [P] 创建JWT工具类在member-service/src/main/java/.../util/JwtUtil.java
- [X] T020 [P] 创建BCrypt密码工具类在member-service/src/main/java/.../util/PasswordUtil.java
- [X] T021 [P] 创建短信服务工具类在member-service/src/main/java/.../util/SmsUtil.java
- [X] T022 [P] 创建IP地域解析工具类在member-service/src/main/java/.../util/IpUtil.java

### 核心实体类

- [X] T023 创建Member实体类在member-service/src/main/java/.../entity/Member.java
- [X] T024 [P] 创建VerificationCode实体类在member-service/src/main/java/.../entity/VerificationCode.java
- [X] T025 [P] 创建LoginLog实体类在member-service/src/main/java/.../entity/LoginLog.java
- [X] T026 [P] 创建Address实体类在member-service/src/main/java/.../entity/Address.java
- [X] T027 [P] 创建ThirdPartyBinding实体类在member-service/src/main/java/.../entity/ThirdPartyBinding.java

### 核心Mapper

- [X] T028 创建MemberMapper接口在member-service/src/main/java/.../mapper/MemberMapper.java
- [X] T029 [P] 创建VerificationCodeMapper接口在member-service/src/main/java/.../mapper/VerificationCodeMapper.java
- [X] T030 [P] 创建LoginLogMapper接口在member-service/src/main/java/.../mapper/LoginLogMapper.java
- [X] T031 [P] 创建AddressMapper接口在member-service/src/main/java/.../mapper/AddressMapper.java
- [X] T032 [P] 创建ThirdPartyBindingMapper接口在member-service/src/main/java/.../mapper/ThirdPartyBindingMapper.java

### 全局异常处理与响应

- [X] T033 创建全局异常处理器在member-service/src/main/java/.../exception/GlobalExceptionHandler.java
- [X] T034 [P] 创建自定义业务异常类在member-service/src/main/java/.../exception/BusinessException.java
- [X] T035 [P] 创建统一响应类在member-service/src/main/java/.../dto/response/ApiResponse.java
- [X] T036 [P] 创建响应枚举在member-service/src/main/java/.../enums/ResponseCode.java

### 日志与监控

- [X] T037 配置logback-spring.xml日志配置在member-service/src/main/resources/
- [X] T038 [P] 创建操作日志注解在member-service/src/main/java/.../annotation/OperationLog.java
- [X] T039 [P] 创建操作日志切面在member-service/src/main/java/.../aspect/OperationLogAspect.java

**Checkpoint**: 基础设施就绪,用户故事实现可以并行开始

---

## Phase 3: User Story 1 - 会员注册 (Priority: P0) 🎯 MVP

**Goal**: 新用户通过手机号或第三方账号完成注册,成为平台会员

**Independent Test**: 完整注册流程(手机号+验证码+密码)、第三方授权注册、注册后自动登录、注册校验规则验证

### Tests for User Story 1 (TDD - 测试优先编写) ⚠️

> **注意: 这些测试必须先编写并失败,然后才能开始实现**

- [X] T040 [P] [US1] 编写注册接口契约测试在member-service/src/test/java/.../contract/RegisterControllerContractTest.java
- [X] T041 [P] [US1] 编写手机号注册单元测试在member-service/src/test/java/.../unit/AuthServiceRegisterTest.java
- [ ] T042 [P] [US1] 编写第三方注册单元测试在member-service/src/test/java/.../unit/AuthServiceThirdPartyRegisterTest.java(待第三方注册实现后添加)
- [X] T043 [P] [US1] 编写验证码发送集成测试在member-service/src/test/java/.../integration/VerificationCodeIntegrationTest.java
- [ ] T044 [US1] 编写并发注册冲突测试在member-service/src/test/java/.../integration/ConcurrentRegisterTest.java(待集成测试环境就绪后添加)

### Implementation for User Story 1

- [X] T045 [P] [US1] 创建验证码请求DTO在member-service/src/main/java/.../dto/request/SendCodeRequest.java
- [X] T046 [P] [US1] 创建注册请求DTO在member-service/src/main/java/.../dto/request/RegisterRequest.java
- [X] T047 [P] [US1] 创建第三方注册请求DTO在member-service/src/main/java/.../dto/request/ThirdPartyRegisterRequest.java
- [X] T048 [P] [US1] 创建注册响应DTO在member-service/src/main/java/.../dto/response/RegisterResponse.java
- [X] T049 [P] [US1] 创建验证码服务接口在member-service/src/main/java/.../service/VerificationCodeService.java
- [X] T050 [US1] 实现验证码服务实现类在member-service/src/main/java/.../service/impl/VerificationCodeServiceImpl.java(含Redis缓存、防刷限制、降级策略)
- [X] T051 [US1] 实现会员注册服务在member-service/src/main/java/.../service/impl/AuthServiceImpl.java(含BCrypt加密、唯一索引冲突处理)
- [ ] T052 [US1] 实现第三方注册服务在member-service/src/main/java/.../service/impl/ThirdPartyAuthServiceImpl.java(微信/支付宝/QQ)(待后续Phase实现)
- [X] T053 [US1] 创建验证码控制器在member-service/src/main/java/.../controller/VerificationCodeController.java
- [X] T054 [US1] 创建认证控制器在member-service/src/main/java/.../controller/AuthController.java(注册接口)
- [X] T055 [US1] 添加注册接口请求验证注解(@Valid, 手机号格式、密码强度验证)
- [X] T056 [US1] 配置RocketMQ登录日志生产者在member-service/src/main/java/.../producer/LoginLogProducer.java
- [X] T057 [US1] 实现注册日志异步发送(RocketMQ)

**Checkpoint**: 此时User Story 1应该完全功能化并可独立测试

---

## Phase 4: User Story 2 - 会员登录 (Priority: P0) 🎯 MVP

**Goal**: 已注册会员通过多种方式完成登录,访问个人中心等功能

**Independent Test**: 密码登录、验证码登录、第三方登录、登录失败处理、账号锁定、异地登录验证

### Tests for User Story 2 (TDD - 测试优先编写) ⚠️

- [X] T058 [P] [US2] 编写登录接口契约测试在member-service/src/test/java/.../contract/LoginControllerContractTest.java
- [X] T059 [P] [US2] 编写密码登录单元测试在member-service/src/test/java/.../unit/AuthServiceLoginTest.java
- [ ] T060 [P] [US2] 编写账号锁定逻辑单元测试在member-service/src/test/java/.../unit/LoginLockoutTest.java(待实现)
- [ ] T061 [P] [US2] 编写异地登录检测单元测试在member-service/src/test/java/.../unit/AbnormalLoginDetectionTest.java(待实现)
- [ ] T062 [P] [US2] 编写第三方登录单元测试在member-service/src/test/java/.../unit/ThirdPartyLoginTest.java(待第三方登录实现后添加)

### Implementation for User Story 2

- [X] T063 [P] [US2] 创建登录请求DTO在member-service/src/main/java/.../dto/request/PasswordLoginRequest.java
- [X] T064 [P] [US2] 创建验证码登录请求DTO在member-service/src/main/java/.../dto/request/SmsLoginRequest.java
- [X] T065 [P] [US2] 创建第三方登录请求DTO在member-service/src/main/java/.../dto/request/ThirdPartyLoginRequest.java
- [X] T066 [P] [US2] 创建登录响应DTO在member-service/src/main/java/.../dto/response/LoginResponse.java
- [X] T067 [US2] 实现密码登录服务在member-service/src/main/java/.../service/impl/AuthServiceImpl.java(含BCrypt验证、失败计数、账号锁定)
- [X] T068 [US2] 实现验证码登录服务在member-service/src/main/java/.../service/impl/AuthServiceImpl.java(免密登录)
- [ ] T069 [US2] 实现第三方登录服务在member-service/src/main/java/.../service/impl/ThirdPartyAuthServiceImpl.java(待后续Phase实现)
- [ ] T070 [US2] 实现异地登录检测服务(基于IP地域,触发二次验证)(待后续Phase实现)
- [X] T071 [US2] 在AuthController中添加登录接口(密码登录、验证码登录、第三方登录)
- [ ] T072 [US2] 实现密码重置功能(验证码验证+新密码设置+历史密码校验)(待后续Phase实现)
- [X] T073 [US2] 实现登出功能(Token失效处理)
- [X] T074 [US2] 发送登录日志到RocketMQ(异步)

**Checkpoint**: 此时User Stories 1和2都应该完全功能化并独立可测试

---

## Phase 5: User Story 3 - 会员信息查询 (Priority: P1)

**Goal**: 会员登录后查看个人基础信息、账号安全信息、收货地址

**Independent Test**: 查询基础信息、账号安全信息、登录记录、收货地址列表、导出个人信息Excel

### Tests for User Story 3 (TDD - 测试优先编写) ⚠️

- [ ] T075 [P] [US3] 编写会员信息查询接口契约测试在member-service/src/test/java/.../contract/MemberControllerContractTest.java
- [ ] T076 [P] [US3] 编写信息查询服务单元测试在member-service/src/test/java/.../unit/MemberServiceQueryTest.java
- [ ] T077 [P] [US3] 编写登录记录查询单元测试在member-service/src/test/java/.../unit/LoginLogQueryTest.java

### Implementation for User Story 3

- [X] T078 [P] [US3] 创建会员信息响应DTO在member-service/src/main/java/.../dto/response/MemberInfoResponse.java
- [X] T079 [P] [US3] 创建登录记录响应DTO在member-service/src/main/java/.../dto/response/LoginHistoryResponse.java
- [X] T080 [P] [US3] 创建地址响应DTO在member-service/src/main/java/.../dto/response/AddressResponse.java
- [X] T081 [US3] 实现会员信息查询服务在member-service/src/main/java/.../service/impl/MemberServiceImpl.java(含脱敏处理)
- [X] T082 [US3] 实现登录记录查询服务在member-service/src/main/java/.../service/impl/LoginLogServiceImpl.java(近10条)
- [X] T083 [US3] 实现收货地址查询服务在member-service/src/main/java/.../service/impl/AddressServiceImpl.java
- [ ] T084 [US3] 实现第三方绑定查询服务在member-service/src/main/java/.../service/impl/ThirdPartyBindingServiceImpl.java(待后续Phase实现)
- [X] T085 [US3] 创建会员控制器在member-service/src/main/java/.../controller/MemberController.java(信息查询接口)
- [ ] T086 [US3] 实现个人信息导出Excel功能(使用Apache POI)(待后续Phase实现)

**Checkpoint**: 此时User Stories 1、2和3都应该完全功能化并独立可测试

---

## Phase 6: User Story 4 - 会员信息修改 (Priority: P1)

**Goal**: 会员修改个人基础信息和收货地址

**Independent Test**: 修改昵称/头像/性别/生日、添加/编辑/删除收货地址、设置默认地址

### Tests for User Story 4 (TDD - 测试优先编写) ⚠️

- [ ] T087 [P] [US4] 编写会员信息修改接口契约测试在member-service/src/test/java/.../contract/MemberUpdateControllerContractTest.java
- [ ] T088 [P] [US4] 编写信息修改服务单元测试在member-service/src/test/java/.../unit/MemberServiceUpdateTest.java
- [ ] T089 [P] [US4] 编写地址管理单元测试在member-service/src/test/java/.../unit/AddressServiceTest.java

### Implementation for User Story 4

- [X] T090 [P] [US4] 创建更新会员信息请求DTO在member-service/src/main/java/.../dto/request/UpdateMemberRequest.java
- [X] T091 [P] [US4] 创建添加地址请求DTO在member-service/src/main/java/.../dto/request/AddAddressRequest.java
- [X] T092 [P] [US4] 创建更新地址请求DTO在member-service/src/main/java/.../dto/request/UpdateAddressRequest.java
- [X] T093 [US4] 实现会员信息修改服务在member-service/src/main/java/.../service/impl/MemberServiceImpl.java(昵称、头像、性别、生日)
- [ ] T094 [US4] 实现头像上传功能(文件大小≤2MB, JPG/PNG格式校验)(待后续Phase实现)
- [X] T095 [US4] 实现添加收货地址服务在member-service/src/main/java/.../service/impl/AddressServiceImpl.java
- [X] T096 [US4] 实现编辑收货地址服务在member-service/src/main/java/.../service/impl/AddressServiceImpl.java
- [X] T097 [US4] 实现删除收货地址服务在member-service/src/main/java/.../service/impl/AddressServiceImpl.java
- [X] T098 [US4] 实现设置默认地址服务(保证只有一个默认地址)
- [X] T099 [US4] 在MemberController中添加信息修改接口
- [X] T100 [US4] 创建地址控制器在member-service/src/main/java/.../controller/AddressController.java

**Checkpoint**: 此时User Stories 1、2、3和4都应该完全功能化并独立可测试

---

## Phase 7: User Story 5 - 账号注销 (Priority: P2)

**Goal**: 会员主动申请注销账号,系统按合规要求处理

**Independent Test**: 提交注销申请、二次确认、账号状态变更、注销后无法登录、未完成订单拦截

### Tests for User Story 5 (TDD - 测试优先编写) ⚠️

- [ ] T101 [P] [US5] 编写账号注销接口契约测试在member-service/src/test/java/.../contract/AccountCancelControllerContractTest.java
- [ ] T102 [US5] 编写账号注销服务单元测试在member-service/src/test/java/.../unit/MemberServiceCancelTest.java
- [ ] T103 [US5] 编写未完成订单拦截单元测试在member-service/src/test/java/.../unit/OrderValidationTest.java

### Implementation for User Story 5

- [ ] T104 [US5] 创建注销请求DTO在member-service/src/main/java/.../dto/request/CancelAccountRequest.java
- [ ] T105 [US5] 实现账号注销服务在member-service/src/main/java/.../service/impl/MemberServiceImpl.java(含二次确认、状态变更)
- [ ] T106 [US5] 实现未完成订单校验(调用订单服务API或预留接口)
- [ ] T107 [US5] 在登录时校验账号状态(已注销账号拒绝登录)
- [ ] T108 [US5] 在MemberController中添加账号注销接口

**Checkpoint**: 所有用户故事现在都应该完全功能化并独立可测试

---

## Phase 8: Polish & Cross-Cutting Concerns

**Purpose**: 影响多个用户故事的改进

- [ ] T109 [P] 补充API文档注解(Swagger/OpenAPI注解)
- [ ] T110 [P] 优化错误提示信息(用户友好化、国际化支持)
- [ ] T111 [P] 添加接口限流(Sentinel,按用户/IP限流)
- [ ] T112 [P] 完善操作日志(所有关键操作记录会员ID、手机号、IP、操作结果)
- [ ] T113 性能优化:添加Redis缓存配置(热点数据缓存、缓存过期策略)
- [ ] T114 性能优化:数据库查询优化(索引验证、慢查询优化)
- [ ] T115 安全加固:添加请求签名验证(防重放攻击)
- [ ] T116 [P] 添加监控指标埋点(Micrometer,业务KPI和技术KPI)
- [ ] T117 [P] 配置健康检查接口(Actuator)
- [ ] T118 完善单元测试覆盖率(目标≥80%)
- [ ] T119 完善集成测试场景
- [ ] T120 性能测试:使用JMeter进行压测(10000 TPS注册/登录)
- [ ] T121 运行quickstart.md验证(按快速开始指南启动项目并测试核心功能)

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: 无依赖 - 可立即开始
- **Foundational (Phase 2)**: 依赖Setup完成 - 阻塞所有用户故事
- **User Stories (Phase 3-7)**: 依赖Foundational完成
  - US1(注册)、US2(登录): 可并行开发(如果团队容量允许)
  - US3(信息查询)、US4(信息修改): 可并行开发(如果团队容量允许)
  - US5(账号注销): 可在US1、US2完成后开始
- **Polish (Phase 8)**: 依赖所有期望的用户故事完成

### User Story Dependencies

- **User Story 1 (P0 - 注册)**: Foundational完成后可开始 - 无其他故事依赖
- **User Story 2 (P0 - 登录)**: Foundational完成后可开始 - 无其他故事依赖
- **User Story 3 (P1 - 信息查询)**: Foundational完成后可开始 - 可与US1、US2、US4并行
- **User Story 4 (P1 - 信息修改)**: Foundational完成后可开始 - 可与US1、US2、US3并行
- **User Story 5 (P2 - 账号注销)**: 依赖US1(注册)和US2(登录)完成

### Within Each User Story

- 测试必须先编写并FAIL(TDD Red-Green-Refactor)
- DTO/实体类可并行创建
- 服务实现依赖DTO和实体类
- 控制器依赖服务实现
- 集成测试在实现后进行

### Parallel Opportunities

- Phase 1中所有标记[P]的任务可并行运行
- Phase 2中所有标记[P]的任务可并行运行(在Phase 2内)
- Foundational完成后,所有P0和P1故事可并行开发(如果团队容量允许):
  - 开发者A: User Story 1 (注册)
  - 开发者B: User Story 2 (登录)
  - 开发者C: User Story 3 (信息查询)
  - 开发者D: User Story 4 (信息修改)

---

## Parallel Example: User Story 1 (注册)

```bash
# 启动User Story 1的所有测试(先写测试,TDD):
Task: "编写注册接口契约测试在RegisterControllerContractTest.java"
Task: "编写手机号注册单元测试在AuthServiceRegisterTest.java"
Task: "编写第三方注册单元测试在ThirdPartyRegisterTest.java"
Task: "编写验证码发送集成测试在VerificationCodeIntegrationTest.java"
Task: "编写并发注册冲突测试在ConcurrentRegisterTest.java"

# 启动所有DTO创建(并行):
Task: "创建验证码请求DTO在SendCodeRequest.java"
Task: "创建注册请求DTO在RegisterRequest.java"
Task: "创建第三方注册请求DTO在ThirdPartyRegisterRequest.java"
Task: "创建注册响应DTO在RegisterResponse.java"
```

---

## Implementation Strategy

### MVP First (User Stories 1 & 2 Only - P0)

1. 完成Phase 1: Setup
2. 完成Phase 2: Foundational(CRITICAL - 阻塞所有故事)
3. 完成Phase 3: User Story 1 (注册)
4. 完成Phase 4: User Story 2 (登录)
5. **STOP and VALIDATE**: 独立测试注册和登录流程
6. 部署/演示(如就绪)

**MVP价值**: 用户可以注册和登录系统,核心会员身份建立功能完整

### Incremental Delivery (按优先级逐个添加)

1. 完成Setup + Foundational → 基础就绪
2. 添加User Story 1 (注册) → 独立测试 → 部署/演示(MVP第一部分!)
3. 添加User Story 2 (登录) → 独立测试 → 部署/演示(MVP完整!)
4. 添加User Story 3 (信息查询) → 独立测试 → 部署/演示
5. 添加User Story 4 (信息修改) → 独立测试 → 部署/演示
6. 添加User Story 5 (账号注销) → 独立测试 → 部署/演示
7. 每个故事增加价值且不破坏已有功能

### Parallel Team Strategy

多个开发者情况:

1. 团队共同完成Setup + Foundational
2. Foundational完成后:
   - 开发者A: User Story 1 (注册)
   - 开发者B: User Story 2 (登录)
   - 开发者C: User Story 3 (信息查询)
   - 开发者D: User Story 4 (信息修改)
3. 故事独立完成并集成

---

## Notes

- [P]任务 = 不同文件,无依赖
- [Story]标签将任务映射到特定用户故事以支持可追溯性
- 每个用户故事应可独立完成和测试
- 实现前验证测试失败(TDD Red-Green-Refactor)
- 每个任务或逻辑组后提交
- 在任何checkpoint停止以独立验证故事
- 避免:模糊任务、同一文件冲突、破坏独立性的跨故事依赖

---

## Task Summary

- **Total Tasks**: 121
- **Phase 1 (Setup)**: 8 tasks
- **Phase 2 (Foundational)**: 31 tasks (BLOCKS所有用户故事)
- **Phase 3 (US1 - 注册 P0)**: 17 tasks
- **Phase 4 (US2 - 登录 P0)**: 17 tasks
- **Phase 5 (US3 - 信息查询 P1)**: 9 tasks
- **Phase 6 (US4 - 信息修改 P1)**: 14 tasks
- **Phase 7 (US5 - 账号注销 P2)**: 8 tasks
- **Phase 8 (Polish)**: 13 tasks

**Parallel Opportunities**: 50+ tasks标记为[P]可并行

**Independent Test Criteria**: 每个用户故事明确定义了独立测试标准

**MVP Scope**: Phase 1 + Phase 2 + Phase 3 + Phase 4 (注册+登录) = 73 tasks
