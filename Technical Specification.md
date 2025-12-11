## TECHNICAL SPECIFICATION

**System Name**: SPMS – Timecard Module Refactoring

AUTHOR : _TBD_  
DATE : _TBD_  

LAST REVIEW DATE : _TBD_  

|     |     |
| --- | --- |
| **IT IS Architect Signoff** |     |
| Document Review by: |     |
| Document Review Date: |     |
| SSR Ticket Number <xxx>: |     |

---

## CONTENTS

1. Document Control  
1.1 Change History  
1.2 Document Cross-referenced  
1.3 Abbreviation  
1.4 Distribution list  

2. Introduction  
2.1 System Objective  
2.2 Scope  

3. Hardware, Software and Network Requirement  
3.1 Components and Relationship  
3.2 Hardware  
3.3 Software  
3.4 Network  

4. System Design Overview  
4.1 Build Philosophy  
4.2 Design Limitation  
4.3 Availability, Redundancy, and Failover  
4.4 Security  
4.4.1 Security Zoning  
4.4.2 Authentication  
4.4.3 Authorization and Access Control  
4.4.4 Password Control  
4.4.5 Audit Trail  
4.4.6 Encryption of Information Storage  
4.4.7 Encryption of Information Transmission  
4.4.8 Data Integrity Checking  

5. Database Specification  
5.1 Database Design  
5.2 Detail Description of File Structure / Table  

6. Sub-system Design  
6.1 Overall Structure  
6.2 Organization Data Maintenance Sub-system  
6.3 Timecard Form Setup and Registration Sub-system  
6.4 Timecard Entry Sub-system  
6.5 Timecard Approval Sub-system  
6.6 Holiday Management Sub-system  
6.7 Project Management Sub-system  
6.8 Reports Sub-system  

7. Common System Features  

A. Appendices  

---

## 1. Document Control

### 1.1 Change History

| Version Number | Issue Date | Author | Abstract |
| --- | --- | --- | --- |
| 0.1 | YYYY-MM-DD | TBD | Initial draft for Timecard Module Refactoring |
| 0.2 | YYYY-MM-DD | TBD | Updated after architecture review feedback |
| 1.0 | YYYY-MM-DD | TBD | First baseline for implementation |

### 1.2 Document Cross-referenced

- **Functional Specification**: `Functional Specification.md`（SPMS 总体功能规格）  
- **Feature Specification – Timecard Module Refactoring**: `specs/002-functional-specification-md/spec.md`  
- **Implementation Plan – Timecard Module Refactoring**: `specs/002-functional-specification-md/plan.md`  
- **Database Specification – SPMS**: `backend/Database_Specification.md`（如需）  
- **SPMS Security / Infrastructure Standards**: _TBD – 参考公司 ITSMF、安规文档_  

### 1.3 Abbreviation

| Abbreviation | Full Form | Description |
| --- | --- | --- |
| SPMS | Staff Project Management System | 员工项目管理系统，总体平台 |
| BPMN | Business Process Model and Notation | 业务流程建模与执行标准 |
| JSON | JavaScript Object Notation | 前后端数据交换与存储格式 |
| API | Application Programming Interface | 应用编程接口 |
| OT | Overtime | 加班时数 |
| UI | User Interface | 用户界面 |
| FR-xxx | Functional Requirement | 功能需求编号，来源于 Functional Spec |
| US-x | User Story | 用户故事编号，来源于 Feature Spec |
| DB | Database | 数据库 |
| SIEM | Security Information and Event Management | 安全信息与事件管理系统 |

### 1.4 Distribution list

| Role / Team | Distribution |
| --- | --- |
| IT Architect | For architecture review and signoff |
| Application Development Team (Frontend & Backend) | For design and implementation reference |
| QA / Testing Team | For test case design与覆盖性检查 |
| Infrastructure / DBA Team | For部署、数据库与性能调优 |
| Information Security Team | For安全设计与合规性评估 |

---

## 2. Introduction

### 2.1 System Objective

Timecard 模块的目标是在现有 SPMS 平台上，提供一套完整、可配置、可审计的工时填报与审批能力，满足以下业务需求：

- 员工可以通过月历视图 **高效录入每日工时**，包括项目代码、任务、活动、申报类型及备注等（对应 US-1, FR-006～FR-023）。  
- 系统基于组织架构与 BPMN 流程，完成 **多级审批路由**，保障数据准确与合规（US-2, FR-026～FR-030）。  
- 管理员可以维护 **组织数据、项目数据、节假日数据**，并通过 Excel 导入导出简化维护（US-3/4/5, FR-001～FR-005, FR-031～FR-041）。  
- 管理者与管理员可以基于已审批的工时数据，生成多种报表，用于资源分析与决策支持（US-6, FR-042～FR-046）。  

### 2.2 Scope

本技术规格的范围包括：

- **前端 Timecard 自定义表单组件** 的技术设计，包括日历视图、批量填报、实时汇总与可视化告警。  
- **后端服务与 API** 的设计，包括 Timecard 提交、校验、审批任务查询、组织/项目/节假日维护与报表生成。  
- **数据库与数据模型设计**，重点是 `spms_process_data.form_type='TIMECARD'` 的 JSON 结构与相关查询。  
- **与现有系统集成**：Flowable BPMN 引擎、Process-Form Management、自定义组件注册机制、组织架构模块、邮件通知系统等。  
- **非功能性要求**：性能（并发 500 用户）、可扩展性、可维护性、安全与审计等（SC-001～SC-010）。

不在本技术规格范围内的内容：

- Flowable BPMN 引擎本身的实现与配置细节（仅描述如何集成与调用）。  
- SPMS 统一认证授权基础设施的底层实现（仅描述如何复用）。  
- 通用运维监控平台、备份与灾难恢复平台的实现（只描述 Timecard 如何接入与依赖）。

---

## 3. Hardware, Software and Network Requirement

### 3.1 Components and Relationship

**主要组件：**

- **Frontend Web Application**：基于 React 18 + MUI 的 SPA，包含 Timecard 表单、节假日管理、项目管理与报表页面。  
- **Backend Application**：基于 Spring Boot 3 的 REST API 服务，集成 Flowable、JPA 与 PostgreSQL。  
- **Database Server**：PostgreSQL，用于存储流程数据（含 Timecard JSON）、组织结构、项目、节假日与日志等。  
- **Flowable BPMN Engine**：负责工时审批流程实例管理与任务分配。  
- **Process-Form Management**：负责表单定义、版本管理与自定义组件挂载。  
- **Email / Notification Service**：发送审批提醒、结果通知等。  
- **Report Generator**：后端报表服务模块（逻辑与 API 级别），将 Timecard 数据聚合并导出 Excel。

组件间逻辑关系（文字版架构图）：

- 浏览器 → Frontend (Timecard Form) → Backend REST API  
- Backend → PostgreSQL（查询/写入 `spms_process_data` 等表）  
- Backend → Flowable（创建/推进审批流程实例）  
- Backend → Email/Notification Service（发送通知）  
- Backend → 组织/项目/节假日相关 DB 表（用于校验与展示）

### 3.2 Hardware

本模块部署在现有 SPMS 基础设施之上，**不额外引入专用硬件**。关键硬件如下（规格示例，可按实际环境调整）：

#### Application Server（共享 SPMS）

- 型号：x86_64 服务器（虚拟或物理）  
- CPU：8 vCPU 及以上  
- Memory：32 GB 及以上  
- Disk：本地系统盘 + 应用日志磁盘（≥ 200 GB）  
- OS：Linux（如 RHEL 8+/CentOS Stream 等），已按公司标准进行 OS Hardening  
- 网络接口：千兆/万兆以太网，使用 TCP/IP 协议  
- 特殊系统软件：
  - Java 17 运行时环境  
  - 应用容器 / 进程管理（如 systemd 或容器平台）  
  - 统一日志采集 Agent（对接 SIEM / 日志平台）

#### Database Server（PostgreSQL）

- 型号：企业级数据库服务器或高可用集群  
- CPU：16 vCPU 及以上  
- Memory：64 GB 及以上  
- Disk：SSD 阵列（含 WAL 与数据盘分离），容量依据 Timecard 数据增长预估规划  
- OS：Linux，按 DB 安全基线加固  
- 软件：
  - PostgreSQL 13+  
  - 备份与监控工具（如 pgBackRest, Prometheus exporter 等）

### 3.3 Software

#### Frontend Application

- 技术栈：
  - React 18.3.1  
  - Material-UI (MUI) v5  
  - React Router v6  
  - react-i18next（多语言支持）
- 运行环境：
  - 部署为静态资源，由 Nginx 或类似 Web Server 提供  
  - 通过 HTTPS 访问 Backend API  
- 配置要点：
  - API Base URL、认证 Token 获取方式  
  - 国际化资源加载配置  
  - 自定义组件注册（Timecard Form 注册到 Form-Process 平台）

#### Backend Application

- 技术栈：
  - Java 17  
  - Spring Boot 3.4.5  
  - Spring Data JPA  
  - Flowable 7.1.0  
  - Spring Security OAuth2（复用现有认证体系）  
- 功能模块：
  - Timecard 服务与控制器  
  - 组织管理扩展  
  - 项目管理扩展  
  - 节假日管理  
  - 报表服务  
- 配置要点：
  - DB 连接池与方言  
  - Flowable 引擎配置  
  - 邮件服务器配置  
  - 系统参数（如 Timecard 截止日期、节假日配置类别等）

#### Database

- PostgreSQL 13+  
- 关键参数：
  - 合理配置连接数、缓存、WAL 策略以支撑 500 并发用户  
  - 针对 `spms_process_data` 等表建立合适索引（如 `form_type`, `user_id`, `timecard_month` 等）

### 3.4 Network

- 网络拓扑：
  - 用户浏览器 → 负载均衡 → Web / App Server（应用层）  
  - App Server → DB Server（数据层，内网专线）  
  - App Server → Flowable / 其他中间件（内网）  
  - App Server → 邮件网关 / 通知服务  
- 网络要求：
  - 外部访问通过 HTTPS（TLS）终止于负载均衡或 Web Server  
  - 内部服务间使用安全网络分区与访问控制列表（ACL）限制端口与 IP 范围  
  - DB 端口仅向应用服务器开放  
- 常用端口（示例）：
  - 443：HTTPS  
  - 5432：PostgreSQL（内网）  
  - Flowable / 其他服务端口按现网标准配置  

---

## 4. System Design Overview

### 4.1 Build Philosophy

- **最大化复用现有 SPMS 基础设施**：包括统一认证、组织模块、Flowable BPMN 流程引擎与 Process-Form Management。  
- **前后端解耦**：前端专注交互和可视化（Timecard 日历、对话框、汇总视图），后端提供清晰、稳定的 REST API 与数据持久化接口。  
- **数据集中存储于流程数据表**：Timecard 采用 JSON 结构存放在 `spms_process_data.form_data` 中，避免额外拆表造成复杂的 schema 变更。  
- **配置驱动**：如 Timecard 截止日期、节假日配置等通过 `system_config` 表配置，实现环境与月份可调。  
- **渐进式集成**：按 Implementation Plan 的 Phase 0～10 分期实现与上线，确保每一阶段可测试、可回退。

### 4.2 Design Limitation

- **必须使用 Flowable BPMN 引擎**，不能自建审批引擎。审批路径的配置完全在 BPMN 编辑器中完成（FR-027, FR-028）。  
- **必须复用现有 Process-Form Management 与自定义组件机制**，Timecard 以自定义表单组件形式集成（FR-000～FR-000-3）。  
- **时间卡 JSON 结构** 已在 Feature Spec 中定义（`employee_info`, `timecard_entries`, `summary`），Timecard 模块需要兼容此结构并可能做版本化扩展。  
- **数据库表结构** 需保持向后兼容，`spms_process_data`、`spms_user`、`spms_department`、`spms_project` 等表不做破坏性修改。  
- **性能约束**：在满足 SC-005, SC-006, SC-007 的前提下，不能引入高复杂度查询或非必要的跨库 JOIN。

### 4.3 Availability, Redundancy, and Failover

- **可用性目标**：继承 SPMS 总体可用性目标（例如 99.9%+），Timecard 不单独定义更高 SLA。  
- **冗余设计**：
  - 应用服务器采用集群或容器编排平台（如 K8s）部署，多实例负载均衡。  
  - 数据库采用主从或集群方案（如 Streaming Replication/Patroni），并配置自动故障切换。  
- **故障切换与灾备**：
  - 应用层：单实例故障时，通过负载均衡移除实例，其他实例继续服务；无状态前端、后端支持滚动升级与重启。  
  - 数据层：按现有 DBA 策略定期备份与演练，支持 PITR（Point-in-time Recovery）。  
- **流程容错**：
  - Timecard 提交失败时，前端提供明确错误提示与可重试能力。  
  - Flowable 流程引擎不可用时，禁止提交 Timecard 并返回业务错误，避免出现“有数据但无流程”的不一致状态。

### 4.4 Security

整体安全设计遵循公司 IT 安全政策与 ITSMF 要求，并重点围绕以下几点：

- 基于现有 SPMS 的统一认证与授权框架。  
- 所有外部访问通过 HTTPS，内部高风险接口通过网络 ACL 与认证双重控制。  
- 对 Timecard 相关数据的访问均经过权限校验（员工仅能访问自有 Timecard，管理者按层级访问）。  
- 完备的审计日志记录与 SIEM 集成能力。

#### 4.4.1 Security Zoning

- **Internet Zone → DMZ → Application Zone → Database Zone** 的分层架构：  
  - Browser 位于外网或办公网，通过负载均衡访问 DMZ 中的 Web Entry。  
  - 应用服务器部署在 Application Zone，仅对 DMZ 与内部服务开放。  
  - 数据库服务器部署在 Database Zone，仅对应用服务器网段开放。  
- **Timecard 前端与后端部署策略**：与现有 SPMS 一致，不新增跨区访问路径，仅在现有 HTTPS 与内网链路上添加接口。  

#### 4.4.2 Authentication

- **统一认证**：复用 SPMS 现有的 SSO / OAuth2 机制，所有 Timecard 相关页面与 API 均要求用户已登录并携带有效 Token。  
- **会话管理**：前端通过 Token 或会话 Cookie 与后端交互，后端在每次请求中校验 Token 有效性与用户身份。  
- **接口级认证**：
  - 员工端 Timecard 页面访问：需员工角色或等效权限。  
  - 审批端接口：需管理者或审批角色。  
  - 管理后台（组织/项目/节假日/报表）接口：需管理员角色。  

#### 4.4.3 Authorization and Access Control

- **基于角色的访问控制（RBAC）**：
  - 员工（Employee）：仅访问本人 Timecard 数据（FR-043 中员工报表限制）。  
  - 管理者（Manager）：访问其下属 Timecard 审批任务与相关报表数据。  
  - 管理员（Admin）：访问组织数据、项目数据、节假日、全局报表等。  
- **数据级访问控制**：
  - 报表模块在查询时，会根据当前用户角色与组织层级过滤可见数据（FR-046, FR-043）。  
  - 组织数据、项目数据、节假日数据的管理操作需记录审计日志（FR-001～FR-005）。  

#### 4.4.4 Password Control

- 依照现有 SPMS 与统一认证平台的密码策略执行：  
  - 密码创建、存储、复杂度、过期与重置由统一认证系统管理。  
  - Timecard 模块自身不存储用户密码，只使用 Token 进行认证。  
- 如存在模块级访问密码（当前需求中未涉及），则必须遵循同等强度的加密与轮换策略。  

#### 4.4.5 Audit Trail

- **审计范围**：
  - 用户认证事件（登录/登出）— 由统一登录系统记录。  
  - Timecard 的创建、修改、提交、审批与驳回（FR-024, FR-029）。  
  - 组织、项目、节假日数据的增删改（FR-001～FR-005, FR-036～FR-041, FR-031～FR-035）。  
  - 配置变更（如系统截止日期配置变更等）。  
- **日志内容**（至少）：
  - 操作人标识（user_id）  
  - 操作时间戳  
  - 操作类型（CREATE/UPDATE/APPROVE/REJECT/IMPORT/EXPORT 等）  
  - 关键业务键（如 staff_id, timecard_month, project_code 等）  
  - 变更前后摘要（必要时）  
- **SIEM 集成**：
  - 日志以统一格式输出（如 JSON 或 CEF）到日志采集组件。  
  - 对关键安全事件（如异常失败登录、多次失败提交、异常审批路径等）配置告警。  

#### 4.4.6 Encryption of Information Storage

- **数据库层面**：
  - 整库或磁盘加密由基础设施/DBA 按公司标准统一实施。  
  - Timecard JSON 中如引入敏感字段（当前标准结构中多为业务数据），可评估是否需字段级加密。  
- **应用层面**：
  - 不在日志中记录完整 Timecard JSON，仅记录必要业务键，避免敏感信息泄露。  

#### 4.4.7 Encryption of Information Transmission

- 所有浏览器到前端的访问使用 HTTPS（TLS 1.2+）。  
- 前端到后端 API 调用通过 HTTPS 或受控内网，敏感数据（如 Token）通过安全头传输。  
- 后端到 DB 通信可按公司标准启用 TLS 连接。  

#### 4.4.8 Data Integrity Checking

- **传输完整性**：依托 TLS 保证数据在传输过程不被篡改。  
- **业务完整性**：
  - 数据校验规则（项目编码合法性、小时数范围、时间边界等）在后端强制执行（FR-015～FR-019, FR-023）。  
  - Timecard 提交时，对 JSON 结构进行 Schema 级校验，确保字段完备性（FR-000-5）。  
- **并发与幂等**：
  - Timecard 提交接口通过幂等机制（如按 user_id + month 唯一约束）防止重复提交导致数据不一致（FR-024）。  

---

## 5. Database Specification

### 5.1 Database Design

整体采用 **现有 SPMS 数据库**，在不破坏既有结构的情况下，增加必要的字段、索引与配置项。核心设计原则：

- **Timecard 数据统一以 JSON 形式存储** 在 `spms_process_data.form_data` 字段中，`form_type='TIMECARD'` 用于区分不同流程数据（FR-000-4, FR-000-5）。  
- **对查询热点字段建立索引**，例如 `form_type`, `user_id`, `status`, `timecard_month` 等，以支持报告与列表查询性能（SC-006）。  
- **配置与辅助数据**（如节假日）统一放置在 `system_config` 表，通过 `category` 与 `key` 组织（FR-024-1, FR-031～FR-035）。  

### 5.2 Detail Description of File Structure / Table

#### 5.2.1 Table: `spms_process_data`（复用）

**Purpose**  
存储所有流程表单数据，其中 Timecard 使用 `form_type='TIMECARD'` 与特定 JSON 结构（FR-000-4, FR-000-5）。

**Key Fields（与 Timecard 相关）**

- `id`：主键，唯一标识流程数据记录。  
- `process_instance_id`：关联 Flowable 流程实例。  
- `user_id`：提交 Timecard 的员工 ID。  
- `form_type`：表单类型，Timecard 模块使用固定值 `'TIMECARD'`。  
- `form_data`：TEXT/JSONB，存储完整 Timecard JSON：  
  - `employee_info`：员工信息（staff_id, name, team, staff_type, department_head, final_approver 等）。  
  - `timecard_entries`：工时条目数组（date, project_code, task_number, activity, claim_type, hours, remark）（FR-014～FR-018）。  
  - `summary`：汇总信息（total_hours, grouped_by_project_task_activity, warning_flags）（FR-011, FR-012）。  
- `status`：Timecard 状态，如 DRAFT, SUBMITTED, APPROVED, REJECTED（FR-025）。  
- （可选）`timecard_month`：便于按月查询的冗余字段（如 `YYYY-MM`），从 `employee_info.timecard_month` 派生。

**Indexes**

- 组合索引 `(form_type, user_id, timecard_month)` 便于查询指定用户某月 Timecard 与报表统计。  
- 索引 `(form_type, status)` 便于审批与报表筛选。  

#### 5.2.2 Table: `spms_user`（复用）

**Purpose**  
员工基本信息与用户账户，Timecard 用于展示员工姓名、团队与类型等（FR-006, FR-007-1）。

**Key Fields**

- `id`：员工 ID（staff_id）。  
- `user_profiles`：JSONB，包含 `staff_name_chinese`, `staff_name_english`, `team`, `staff_type` 等。  

#### 5.2.3 Table: `spms_department`（复用）

**Purpose**  
组织结构与管理者信息，用于审批路由与员工信息展示（FR-001～FR-005, FR-028）。

**Key Fields**

- `id`：部门 ID。  
- `name`：部门名称。  
- `department_head_id`：部门负责人。  
- `local_manager_id`：本地经理。  
- `functional_manager_id`：职能经理。  
- 其他用于构建四级组织结构（公司/事业部/部门/团队）的层级字段。

#### 5.2.4 Table: `spms_project` & `spms_task`（复用/扩展）

**Purpose**  
项目与任务定义，供 Timecard 项目编码、任务与活动自动填充使用（FR-015～FR-017, FR-036～FR-041）。

**关键字段示例**

- `spms_project`：
  - `project_code`：项目唯一编码。  
  - `project_name` / `category`：项目名称与类别。  
  - `status`：`ACTIVE` / `COMPLETED`，Timecard 仅展示 ACTIVE 项目（FR-040）。  
- `spms_task`：
  - `task_number`：任务编号。  
  - `project_id`：关联项目。  
  - `activity`：活动描述。  

#### 5.2.5 Table: `system_config`（复用）

**Purpose**  
存储 Timecard 模块相关配置，如月度截止日期、节假日配置等（FR-024-1, FR-031～FR-035）。

**Key Fields**

- `key`：如 `timecard.deadline.2025-01`, `holiday.2025-01`。  
- `value`：JSON 配置内容（如具体日期列表、国家代码等）。  
- `category`：`TIMECARD`, `HOLIDAY` 等。  

---

## 6. Sub-system Design

### 6.1 Overall Structure

从实现视角，Timecard 模块可分为以下子系统：

- **Organization Data Maintenance Sub-system**（FR-001～FR-005）  
- **Timecard Form Setup and Registration Sub-system**（FR-000～FR-000-5, US-0）  
- **Timecard Entry Sub-system**（FR-006～FR-025, US-1）  
- **Timecard Approval Sub-system**（FR-026～FR-030, US-2）  
- **Holiday Management Sub-system**（FR-031～FR-035, US-5）  
- **Project Management Sub-system**（FR-036～FR-041, US-4）  
- **Reports Sub-system**（FR-042～FR-046, US-6）  

子系统之间通过 REST API 与共享数据库交互，并通过 Flowable BPMN 完成审批流程的编排。

### 6.2 Organization Data Maintenance Sub-system

**Purpose**  
维护四级组织结构及本地/职能经理关联，为 Timecard 的员工信息展示与审批路由提供基础数据（US-3, FR-001～FR-005）。

**Inputs & Outputs**

- **Input**：
  - Excel 导入的组织数据文件。  
  - 管理员通过 Web UI 提交的组织结构编辑操作。  
- **Output**：
  - 更新后的组织结构存入 `spms_department` 等表。  
  - 审计日志记录组织数据变更。  
  - 导出的组织数据 Excel 文件。  

**Processing Logic（概述）**

- 导入流程：
  - 上传 Excel → 解析 → 字段校验（层级完整性、ID 唯一性等） → 批量写入 DB → 记录审计日志（FR-001, FR-004, FR-005）。  
- 编辑流程：
  - 前端提交变更 → 后端校验（如不允许破坏层级完整性） → 更新 DB → 写入审计日志。  
- 导出流程：
  - 根据当前组织结构生成 Excel 文件 → 返回给管理员下载。  

### 6.3 Timecard Form Setup and Registration Sub-system

**Purpose**  
实现 Timecard 自定义表单组件的注册、版本管理及与 BPMN 流程的关联（US-0, FR-000～FR-000-5）。

**Inputs & Outputs**

- **Input**：
  - 自定义组件注册配置（在前端注册表中登记 TimecardForm 组件）。  
  - 管理员在 Process-Form Management 中创建/更新表单版本。  
- **Output**：
  - Timecard 表单在表单管理平台中可见，form_type 设为 `TIMECARD`。  
  - BPMN 流程定义能够选择 Timecard 表单作为流程表单。  

**Processing Logic（概述）**

- 自定义组件注册：
  - 前端注册表加载时，将 TimecardForm 组件与 `form_type='TIMECARD'` 建立映射。  
- 表单版本管理：
  - 管理员在 Process-Form Management 中选择 Timecard 组件创建新版本，系统记录 `form_type='TIMECARD'`。  
- 流程关联：
  - 流程设计者在 BPMN 编辑器中，为相应流程节点绑定 Timecard 表单版本。  
- 数据存储：
  - 流程实例启动与表单提交时，Timecard JSON 存入 `spms_process_data.form_data`，并标记 `form_type='TIMECARD'`（FR-000-4, FR-000-5）。  

### 6.4 Timecard Entry Sub-system

**Purpose**  
支持员工录入与提交月度 Timecard，包括日历视图、项目选择、批量操作、实时汇总与告警（US-1, FR-006～FR-025）。

**Inputs & Outputs**

- **Input**：
  - 员工登录会话信息。  
  - 组织与项目基础数据（通过后端 API 获取）。  
  - 节假日配置（从 `system_config` 读取）。  
  - Flowable 中已有的 Timecard 草稿/实例数据（如存在）。  
- **Output**：
  - 展示给用户的日历视图 + 告警信息。  
  - `spms_process_data` 中新的或更新的 Timecard JSON 数据。  
  - Flowable 流程实例（首次提交时）。  

**Key Functions & Logic**

1. **数据渲染策略（FR-007, FR-007-1）**  
   - 若存在对应 Flowable 流程实例且包含完整 Timecard JSON，则加载并直接渲染（包含全部 entries 与 summary）。  
   - 否则，通过 Backend API 获取员工基本信息（`spms_user` + `spms_department`），填充 `employee_info`，日历为空白。  

2. **日历视图与颜色标记（FR-008～FR-013）**  
   - 月视图展示每天的工时条目摘要，并按项目区分颜色（FR-009）。  
   - 红色背景：单日总小时数 > 8 或周末录入工时（FR-010）。  
   - 黄色警示：monthly total > 160 小时（FR-012）。  
   - 绿色背景：节假日（FR-013，与 Holiday Sub-system 联动）。  

3. **项目录入对话框（FR-014～FR-019, FR-022, FR-023）**  
   - 每个日期可有多条条目（project_code, task_number, activity, claim_type, hours, remark）。  
   - 输入项目码后，系统校验其在 `spms_project` 中存在且为 ACTIVE，否则给出错误。  
   - 提供项目搜索/自动补全，根据输入的部分项目码或名称检索（FR-016, FR-022）。  
   - 校验小时数：非负，且不超过合理上限（如 24），禁止非数字输入（FR-018, FR-023）。  
   - 必填项缺失时提示错误（FR-019）。  

4. **批量填报与上月调整（FR-020～FR-021）**  
   - 批量填报：选择某一天为模板，将其所有条目复制到：
     - 当前周所有工作日，或  
     - 整月所有工作日，或  
     - 仅空白日期  
     拷贝过程中不得覆盖已有条目，除非用户显式选择允许覆盖（FR-020-1, FR-020-2）。  
   - 上月调整：提供“preview month adjust”界面，可仅编辑上一个月份的内容（FR-021），系统在提交前校验该操作仍在允许的调整期内（基于系统配置）。  

5. **提交与重新提交（FR-024～FR-025）**  
   - 提交流程：
     - 前端组装完整 Timecard JSON（含 `employee_info`, `timecard_entries`, `summary`）。  
     - 后端验证 JSON 结构与业务规则。  
     - 校验当前日期是否在配置的截止日期之前（FR-024-1, FR-024-2）。  
     - 将 Timecard 存入 `spms_process_data`，并在首次提交时创建 Flowable 流程实例。  
   - 重新提交：
     - 截止日前允许重复提交，最新一次提交覆盖该用户当月的有效 Timecard（FR-024）。  
   - 状态跟踪：
     - 状态字段反映 DRAFT / SUBMITTED / APPROVAL_PENDING / APPROVED / REJECTED（FR-025）。  

### 6.5 Timecard Approval Sub-system

**Purpose**  
支持管理者查看并审批 Timecard，包括查看红黄警示、批注与驳回处理（US-2, FR-026～FR-030）。

**Inputs & Outputs**

- **Input**：
  - Flowable 中的审批任务列表（与当前管理者相关）。  
  - `spms_process_data` 中的 Timecard JSON。  
- **Output**：
  - 审批意见与决策（通过 Flowable API 回写）。  
  - 更新后的 Timecard 状态。  
  - 通知邮件/系统消息（审批请求 & 结果）。  

**Processing Logic（概述）**

- 审批任务列表：
  - 后端根据当前管理者身份，调用 Flowable API 获取待办任务列表。  
  - 查询对应 `spms_process_data` 记录，渲染完整 Timecard（含红黄绿标识）（FR-026）。  
- 审批动作：
  - 审批通过：向 Flowable 提交完成任务的操作，并根据流程定义流转到下一审批人或结束流程（FR-027, FR-028）。  
  - 驳回：记录审批意见，回退给员工，Timecard 状态更新为 REJECTED（FR-029）。  
- 通知：
  - 审批任务到达时向管理者发送提醒。  
  - 审批结果（通过/驳回）时向员工发送结果通知（FR-030）。  

### 6.6 Holiday Management Sub-system

**Purpose**  
提供多国家节假日维护与导入导出能力，并与 Timecard 日历视图联动展示（US-5, FR-031～FR-035）。

**Inputs & Outputs**

- **Input**：
  - 管理员在节假日日历上的操作。  
  - Excel 导入文件（多国家节假日）。  
- **Output**：
  - `system_config` 中存储的节假日配置。  
  - 用于 Timecard 的节假日查询 API 结果。  
  - 导出的节假日 Excel 文件。  

**Processing Logic（概述）**

- 节假日配置界面：
  - 按月展示节假日日历，管理员点击日期新增/编辑节假日（国家、名称、类型）（FR-031～FR-033）。  
- 导入导出：
  - 导入：解析 Excel → 校验国家代码、日期格式与重复项 → 写入/更新配置。  
  - 导出：按条件导出当前所有节假日。  
- 与 Timecard 的联动：
  - Timecard 日历在渲染时调用节假日查询 API，根据国家与月份返回节假日日期，并以绿色高亮显示（FR-033）。  

### 6.7 Project Management Sub-system

**Purpose**  
维护 Timecard 所需的项目与任务数据，包括唯一项目编码、活动定义、状态管理及导入导出（US-4, FR-036～FR-041）。

**Inputs & Outputs**

- **Input**：
  - 管理员通过 Web UI 或 Excel 导入的项目信息。  
- **Output**：
  - `spms_project` 与 `spms_task` 中的项目/任务记录。  
  - 供 Timecard 使用的项目查询 API 与自动补全列表。  

**Processing Logic（概述）**

- 项目维护页面：
  - 创建：录入项目编码（唯一）、名称、类别、状态（active/completed），以及关联任务与活动（FR-036, FR-039～FR-041）。  
  - 修改：允许调整项目属性与任务信息，禁止物理删除以保留历史数据（FR-040）。  
- 导入导出：
  - 导入：解析 Excel → 唯一性校验（项目编码）→ 写入 DB（FR-037, FR-039）。  
  - 导出：按查询条件导出当前项目数据（FR-038）。  
- Timecard 联动：
  - Timecard 项目搜索和自动填充使用该模块提供的项目查询 API，且只返回状态为 ACTIVE 的项目（FR-015～FR-017, FR-040）。  

### 6.8 Reports Sub-system

**Purpose**  
基于已审批的 Timecard 数据提供多种报表：总览、个人记录、详细记录、OT 专用记录等（US-6, FR-042～FR-046）。

**Inputs & Outputs**

- **Input**：
  - 报表查询条件（部门、项目、时间段、员工类型等）。  
  - `spms_process_data` 中 `form_type='TIMECARD'` 且状态为 APPROVED 的数据。  
  - 组织、项目等维度表。  
- **Output**：
  - Web 页面上的报表展示。  
  - 按条件导出的 Excel 文件。  

**Processing Logic（概述）**

- 报表类型：
  - Timecard Summary（按项目、部门、时间段聚合）（FR-042）。  
  - Individual Timecard Record（单员工维度）— 员工仅查自己，管理员可查所有（FR-043）。  
  - Timecard Details Report（详细条目与财务信息）（FR-044）。  
  - Perm Staff IT Record（永久员工/供应商 OT 记录）（FR-045）。  
- 过滤与安全：
  - 根据用户角色与组织层级限制可见数据（FR-046, FR-043）。  
- 性能要求：
  - 针对最多 10,000 条记录，在 10 秒内完成查询与导出（SC-006, SC-007）。  

---

## 7. Common System Features

- **公共模块与框架**：
  - 统一认证授权框架（SSO/OAuth2）。  
  - 通用错误处理与异常日志记录机制。  
  - 统一配置管理（`system_config`）。  
  - 通用 Excel 导入导出组件（广泛用于组织/项目/节假日/报表模块）。  
- **异常处理**：
  - 前端：统一错误提示组件，区分业务错误（如超截止期提交）与系统错误。  
  - 后端：统一异常拦截器，将异常映射为标准化 JSON 错误响应，并记录日志。  
- **设计与编程规范**：
  - 避免重复逻辑，将项目校验、节假日查询等封装为可复用服务。  
  - 接口命名与路径遵循 RESTful 风格与现有 SPMS 命名规范。  

---

## Appendices

（示例，可按需要扩展）

- **Appendix A – API 列表与字段说明**  
  - Timecard 相关 API（如：`GET /timecard/employee-info`, `POST /timecard/submit`, `GET /timecard/approval-tasks` 等）的请求/响应结构与校验规则。  
- **Appendix B – Timecard JSON Schema**  
  - 对 `employee_info`, `timecard_entries`, `summary` 中每个字段的定义、类型与校验规则的详细列举。  
- **Appendix C – 报表字段定义与 Excel 模板**  
  - 各类报表的列定义、含义与对应数据来源字段。  
- **Appendix D – 典型业务场景测试用例示例**  
  - 如“正常月份 160 小时报送”、“超过 160 小时但允许提交且黄色警示”、“截止日之后禁止提交”、“多级审批链路测试”等。


