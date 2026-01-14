# Implementation Plan: [FEATURE]

**Branch**: `[###-feature-name]` | **Date**: [DATE] | **Spec**: [link]
**Input**: Feature specification from `/specs/[###-feature-name]/spec.md`

**Note**: This template is filled in by the `/speckit.plan` command. See `.specify/templates/commands/plan.md` for the execution workflow.

## Summary

[Extract from feature spec: primary requirement + technical approach from research]

## Technical Context

<!--
  ACTION REQUIRED: Replace the content in this section with the technical details
  for the project. The structure here is presented in advisory capacity to guide
  the iteration process.
-->

**Language/Version**: [e.g., Python 3.11, Swift 5.9, Rust 1.75 or NEEDS CLARIFICATION]  
**Primary Dependencies**: [e.g., FastAPI, UIKit, LLVM or NEEDS CLARIFICATION]  
**Storage**: [if applicable, e.g., PostgreSQL, CoreData, files or N/A]  
**Testing**: [e.g., pytest, XCTest, cargo test or NEEDS CLARIFICATION]  
**Target Platform**: [e.g., Linux server, iOS 15+, WASM or NEEDS CLARIFICATION]
**Project Type**: [single/web/mobile - determines source structure]  
**Performance Goals**: [domain-specific, e.g., 1000 req/s, 10k lines/sec, 60 fps or NEEDS CLARIFICATION]  
**Constraints**: [domain-specific, e.g., <200ms p95, <100MB memory, offline-capable or NEEDS CLARIFICATION]  
**Scale/Scope**: [domain-specific, e.g., 10k users, 1M LOC, 50 screens or NEEDS CLARIFICATION]

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

基于电商微服务宪章原则的符合性检查:

- [ ] **P1 用户价值优先**:
  - [ ] 功能是否解决电商真实用户问题(C端/B端/供应链)?
  - [ ] 是否定义电商场景化成功标准(转化率、支付成功率等)?
  - [ ] 是否平衡多角色价值(消费者/商户/平台)?
- [ ] **P2 测试驱动开发**:
  - [ ] 核心场景(下单/支付/库存)测试策略是否明确?
  - [ ] 是否覆盖契约测试(微服务间通信)?
  - [ ] 是否包含性能测试要求(TPS、响应时间)?
  - [ ] 是否包含资损风险测试?
- [ ] **P3 简洁性原则**:
  - [ ] 是否选择最简单可行的方案?
  - [ ] 微服务拆分粒度是否合理(避免过细拆分)?
  - [ ] 复杂性是否有明确理由并获得批准?
- [ ] **P4 渐进式交付**:
  - [ ] 用户故事是否标记优先级(P0/P1/P2/P3)?
  - [ ] 核心交易流程是否可独立测试(MVP闭环)?
  - [ ] 是否支持灰度发布策略?
- [ ] **P5 可观测性**:
  - [ ] 是否定义业务KPI(转化率、支付成功率)?
  - [ ] 是否定义技术KPI(TPS、响应时间、缓存命中率)?
  - [ ] 是否配置资损监控告警?
  - [ ] 日志是否包含核心维度(订单号/支付单号/用户ID)?

> **复杂性跟踪**: 如有违反简洁性原则的决策,必须在下方的"Complexity Tracking"部分记录
>
> **性能基线**: 核心接口需通过压测,达到预设性能基线(如下单接口支持1000 TPS,99%响应时间<200ms)

## Project Structure

### Documentation (this feature)

```text
specs/[###-feature]/
├── plan.md              # This file (/speckit.plan command output)
├── research.md          # Phase 0 output (/speckit.plan command)
├── data-model.md        # Phase 1 output (/speckit.plan command)
├── quickstart.md        # Phase 1 output (/speckit.plan command)
├── contracts/           # Phase 1 output (/speckit.plan command)
└── tasks.md             # Phase 2 output (/speckit.tasks command - NOT created by /speckit.plan)
```

### Source Code (repository root)
<!--
  ACTION REQUIRED: Replace the placeholder tree below with the concrete layout
  for this feature. Delete unused options and expand the chosen structure with
  real paths (e.g., apps/admin, packages/something). The delivered plan must
  not include Option labels.
-->

```text
# [REMOVE IF UNUSED] Option 1: Single project (DEFAULT)
src/
├── models/
├── services/
├── cli/
└── lib/

tests/
├── contract/
├── integration/
└── unit/

# [REMOVE IF UNUSED] Option 2: Web application (when "frontend" + "backend" detected)
backend/
├── src/
│   ├── models/
│   ├── services/
│   └── api/
└── tests/

frontend/
├── src/
│   ├── components/
│   ├── pages/
│   └── services/
└── tests/

# [REMOVE IF UNUSED] Option 3: Mobile + API (when "iOS/Android" detected)
api/
└── [same as backend above]

ios/ or android/
└── [platform-specific structure: feature modules, UI flows, platform tests]
```

**Structure Decision**: [Document the selected structure and reference the real
directories captured above]

## Complexity Tracking

> **Fill ONLY if Constitution Check has violations that must be justified**

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|-------------------------------------|
| [e.g., 4th project] | [current need] | [why 3 projects insufficient] |
| [e.g., Repository pattern] | [specific problem] | [why direct DB access insufficient] |
