## Timecard模块重构项目功能需求规格说明书
---

# 引言

## 1.1 目的  
本文档旨在定义“Timecard模块重构”项目的功能需求规格说明书（SRS），为开发团队、测试团队及相关利益相关者提供清晰的需求指导。文档的目标受众包括系统管理员、财务人员、项目经理、本地/职能经理以及普通用户等主要角色，同时也适用于技术背景和非技术背景的业务人员。通过本文档，读者可以全面了解项目的范围、目标、核心功能模块及其交互逻辑。

## 1.2 范围  
本项目的核心目标是通过重新设计和开发SPMS系统的Timecard模块，提升交互友好性，优化跨部门的时间追踪、审批和报告流程，并支持高效的数据管理和分析。项目的主要功能模块包括：  
- **组织数据维护模块**：支持从SPMS迁移种子数据，并实现组织数据的CRUD操作及导入导出功能。  
- **Timecard填报模块**：提供员工工时填报界面，支持自动填充、颜色区分、批量填报等功能。  
- **Timecard审批模块**：实现灵活的审批路径配置及自动路由审批流程。  
- **假期管理模块**：支持节假日设置及多国节假日管理。  
- **项目管理模块**：提供项目列表管理页面，支持项目内容的导入导出及唯一性校验。  
- **报表展示模块**：生成多种类型的工时报表，支持按部门、项目、时间周期等维度筛选和分析数据。  

本项目的范围涵盖上述功能模块的设计、开发和集成，确保其能够满足不同用户角色的需求，并与现有系统无缝对接。

## 1.3 定义、缩写和术语  

| 术语 | 定义 |  
| :--- | :--- |  
| SPMS | 现有的系统名称，全称为“System for Project Management and Scheduling”，用于项目管理和调度。 |  
| Timecard | 工时卡，记录员工每日工作时间和任务分配的电子表格或系统模块。 |  
| CRUD | Create（创建）、Read（读取）、Update（更新）、Delete（删除）操作的简称，用于描述对数据的基本操作。 |  
| OT | Overtime，加班时间，包括工作日加班、节假日加班及法定节假日加班。 |  
| YTD | Year-To-Date，年度累计数据，通常用于统计某一时间段内的汇总信息。 |  

## 1.4 主要用户角色  
| Functional / Role or Group          | System Admin | Finance | MO/VMO | Local/Functional Manager | General User |
| :---------------------------------- | :----------: | :-----: | :----: | :----------------------: | :----------: |
| Organization Data Maintenance       |     [x]      |         |   [x]  |                          |              |
| Project Rate Code Management        |     [x]      |   [x]   |   [x]  |                          |              |
| Project Code/Charge Code Management |     [x]      |   [x]   |   [x]  |                          |              |
| Exchange Currency Rate Table        |     [x]      |   [x]   |   [x]  |                          |              |
| Timecard Submission                 |     [x]      |         |        |           [x]            |      [x]     |
| Timecard Approval                   |     [x]      |         |        |           [x]            |              |
| Timecard Monthly Submission         |     [x]      |         |   [x]  |           [x]            |              |
| Checking View                       |              |         |        |                          |              |
| Holiday Maintenance                 |     [x]      |         |        |                          |              |
| Reporting and Analytics (Dashboard) |     [x]      |   [x]   |   [x]  |                          |              |


---

# 功能需求

#### 1. 组织数据维护模块
| 需求ID | 需求描述 | 优先级 | 验收标准 | 来源/备注 |
| :--- | :--- | :--- | :--- | :--- |
| FR-001 | 系统应支持从SPMS迁移种子数据，包括组织架构的四级结构。 | 高 | 数据迁移完成后，验证四级结构完整性与一致性。 | Scope 1.a |
| FR-002 | 系统应支持对组织数据进行CRUD操作，并保持与SPMS一致的功能逻辑。 | 高 | 用户可成功执行增删改查操作，且数据同步无误。 | Scope 1.c |
| FR-003 | 系统应新增本地经理和职能经理汇报线字段，并支持编辑和查看。 | 中 | 在组织数据页面，用户可新增、编辑汇报线信息。 | Scope 1.d |
| FR-004 | 系统应支持导入和导出组织数据为Excel格式。 | 中 | 用户可通过界面上传Excel文件并成功导入；导出文件内容完整。 | Scope 1.e |
| FR-005 | 系统应记录所有组织数据的操作日志，包括操作人、时间及内容变更。 | 中 | 操作日志页面显示每次操作的详细信息。 | 功能补全（数据审计） |

---

#### 2. Timecard填报模块
| 需求ID | 需求描述 | 优先级 | 验收标准 | 来源/备注 |
| :--- | :--- | :--- | :--- | :--- |
| FR-006 | 系统应展示员工的基础信息，包括staff name(chinese), staff name(English), team, staff id, timecard month, staff type, department head, final approval。 | 高 | 员工登录后，基础信息正确显示在界面中。 | Function Requirement 1 |
| FR-007 | 系统应自动填充员工的基础信息，减少手动输入。 | 高 | 基础信息自动填充，无需用户重复输入。 | Function Requirement 2 |
| FR-008 | 系统应提供Calendar View界面，默认以月视图展示工时数据。 | 高 | 用户可切换至月视图，界面清晰展示每日工时。 | Function Requirement 3 |
| FR-009 | 系统应在Calendar View中使用不同颜色区分项目。 | 高 | 不同项目在日历中显示不同的颜色标记。 | Function Requirement 4 |
| FR-010 | 系统应在每日工时超过8小时或周末填写工时时显示红色背景警告，但不影响提交。 | 高 | 超过限制时，对应日期单元格背景变红，用户仍可提交。 | Function Requirement 5 |
| FR-011 | 系统应支持timecard summary功能，实时统计project code、project name、task number、activity以及total hours的总工时，并按照综合维度（Project Code + Task Number + Activity）进行数据行分组统计。 | 高 | 总工时统计准确，超出范围时显示黄色警告。 | 修改自FR-011-1到FR-011-5 |
| FR-012 | 当total hours的总工时大于160小时（20天工作日*8小时），则显示黄色警告，但不影响提交。 | 高 | 超过限制时，显示黄色警告，用户仍可提交。 | 修改自FR-011-1到FR-011-5 |
| FR-013 | 系统应在节假日日期显示绿色背景高亮。 | 高 | 节假日单元格背景为绿色。 | Function Requirement 6 |
| FR-014 | 系统应支持点击单个日历块后弹出项目列表供选择，项目列表包含project code, task, activity, claim type, hours, remark字段。 | 高 | 点击日历块后，项目列表弹窗正常显示。 | 修改自FR-014-1到FR-014-5 |
| FR-015 | 系统应支持手动填写project code，并判断是否存在于项目表中。如果存在，则自动填充后续字段（task, activity）。 | 高 | 手动填写project code后，系统验证其有效性并自动填充相关字段。 | 修改自FR-014-1到FR-014-5 |
| FR-016 | 系统应支持通过查找功能选择project code，并自动填充后续字段（task, activity）。 | 高 | 查找功能运行正常，字段自动填充无误。 | 修改自FR-014-1到FR-014-5 |
| FR-017 | 系统应支持claim type字段的下拉选择功能，选项包括normal, leave, ot（working days ot, holidays ot, statuory holidays ot)。 | 高 | 下拉选择功能运行正常，选项符合要求。 | 修改自FR-014-1到FR-014-5 |
| FR-018 | 系统应对hours字段进行范围限制，确保不能输入负数或字符串。 | 高 | 输入超出范围值时，系统提示错误。 | 修改自FR-014-1到FR-014-5 |
| FR-019 | 系统应确保所有字段均为必填项，未填写时提示错误。 | 高 | 提交时，未填写字段触发错误提示。 | 修改自FR-014-1到FR-014-5 |
| FR-020 | 系统应支持批量工时填报操作，按本周、所有工作日或空白日维度填写。 | 中 | 批量填写功能运行正常，数据准确。 | Function Requirement 10 |
| FR-021 | 系统应提供preview month adjust按钮用于修改上个月的工时内容，点击后弹出表单，字段包括project code，task number ，date,  activity, hours，只能用于修改上个月的工时内容。 | 中 | 用户可访问调整表并修改历史工时记录。 | Function Requirement 11 |
| FR-022 | 系统应实现项目代码的自动提示与验证功能。 | 高 | 输入错误代码时，系统提示无效并阻止提交。 | Function Requirement 12 |
| FR-023 | 系统应验证工时的输入范围，确保符合业务规则。 | 高 | 输入超出范围值时，系统提示错误。 | Function Requirement 13 |
| FR-024 | 系统应支持当月截止日期前重复提交工时卡，以最后提交的数据为准。 | 高 | 提交后，系统保存最新版本并记录历史提交。 | Function Requirement 14 |
| FR-025 | 系统应提供工时卡状态跟踪功能，显示当前状态（草稿、已提交、审批中、已批准）。 | 中 | 状态栏实时更新，用户可查看工时卡状态。 | 功能补全（用户体验增强） |

---

#### 3. Timecard审批模块
| 需求ID | 需求描述 | 优先级 | 验收标准 | 来源/备注 |
| :--- | :--- | :--- | :--- | :--- |
| FR-026 | 系统应支持审批员查看员工提交的工时信息，包含单个日历块的红色背景、total summary的黄色背景等颜色提醒信息。 | 高 | 审批员可查看完整的工时详情，包括颜色提醒信息。 | Function Requirement 1 |
| FR-027 | 系统应支持可配置的审批路径（如Submitter→Local Manager→Functional Manager）。 | 高 | 审批路径配置灵活，支持多种审批流。 | Function Requirement 2 |
| FR-028 | 系统应基于组织架构自动路由审批流程。 | 高 | 审批任务根据组织层级正确分配给相关人员。 | Function Requirement 3 |
| FR-029 | 系统应支持审批退回和修改意见反馈。 | 高 | 审批员可退回工时卡并附带反馈意见。 | Function Requirement 4 |
| FR-030 | 系统应支持自动发送审批提醒邮件与系统通知。 | 高 | 提醒邮件和通知按时发送，内容准确。 | Function Requirement 5 |

---

#### 4. 假期管理模块
| 需求ID | 需求描述 | 优先级 | 验收标准 | 来源/备注 |
| :--- | :--- | :--- | :--- | :--- |
| FR-031 | 系统应提供与Timecard相同的Calendar View管理界面进行节假日设置。 | 高 | 管理员可在日历中设置节假日。 | Scope 8 |
| FR-032 | 系统应支持多国节假日设置。 | 高 | 日历中显示多个国家的节假日信息。 | Scope 8 |
| FR-033 | 系统应在Timecard的Calendar View中使用绿色高亮显示节假日。 | 高 | 节假日日期背景为绿色。 | Scope 8 |
| FR-034 | 系统应支持Excel格式的节假日数据导入。 | 中 | 导入文件后，节假日数据正确加载。 | Scope 8 |
| FR-035 | 系统应支持节假日数据导出为Excel格式。 | 中 | 导出文件内容完整，格式正确。 | Scope 8 |

---

#### 5. 项目管理模块
| 需求ID | 需求描述 | 优先级 | 验收标准 | 来源/备注 |
| :--- | :--- | :--- | :--- | :--- |
| FR-036 | 系统应提供项目列表管理页面。 | 高 | 页面显示所有项目的详细信息。 | Scope 3 |
| FR-037 | 系统应支持项目内容（项目名称、类目、代码）的Excel导入功能。 | 中 | 导入文件后，项目数据正确加载。 | Scope 3 |
| FR-038 | 系统应支持项目内容（项目名称、类目、代码）的Excel导出功能。 | 中 | 导出文件内容完整，格式正确。 | Scope 3 |
| FR-039 | 系统应支持项目代码的唯一性校验，避免重复创建。 | 中 | 输入重复代码时，系统提示错误并阻止保存。 | 功能补全（数据完整性） |
| FR-040 | 系统应支持对项目管理模块进行CRUD操作，包括创建项目、修改项目，不允许删除项目。新增字段status，分为“正在进行/已结束”，只有正在进行的项目才能在填timecard时选项目的列表中展示。 | 高 | 用户可通过界面执行CRUD操作，数据更新无误。 | 新增需求 |
| FR-041 | 系统应支持用户通过project code搜索项目信息。 | 高 | 输入project code后，系统返回匹配的项目信息。 | 新增需求 |

---

#### 6. 报表展示模块
| 需求ID | 需求描述 | 优先级 | 验收标准 | 来源/备注 |
| :--- | :--- | :--- | :--- | :--- |
| FR-042 | 系统应生成Timecard Summary报表，按项目、部门、时间周期汇总工时数据。 | 高 | 报表数据准确，支持筛选条件。 | Scope 9 |
| FR-043 | 系统应生成Individual Timecard Record报表，包含员工详细工时记录。仅员工本人可查看自己的报表，管理员可查看所有员工的报表。 | 高 | 报表内容完整，支持按员工和时间范围筛选。 | Scope 9 |
| FR-044 | 系统应生成Timecard Details Report报表，提供工时明细及财务细节。 | 高 | 报表包含费率、成本计算及货币转换数据。 | Scope 9 |
| FR-045 | 系统应生成Perm Staff IT Record报表，提取正式员工或供应商员工的OT数据。 | 高 | 报表仅包含指定员工群体的OT数据。 | Scope 9 |
| FR-046 | 系统应支持每个报表通过特定字段进行搜索与筛选。 | 中 | 用户可通过字段组合筛选报表数据。 | 替代FR-041 |

---
#### 报表表头定义
以下是各报表的表头定义：
##### Individual Timecard Summary（个人工时汇总报表）
| Form ID | Timecard Status | Submission Time | Final Approval Time | Staff ID | Staff Name (Chinese) | Staff Name | Staff Type | Timecard Month | ITD-SZ\|SH Supervisor | Final Approver |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
##### Timecard Details Report（工时明细报表）
| Form ID | Staff ID | Staff Name (Chinese) | Staff Name | ITD-SZ\|SH Team | Timecard Month | Code Type | Project Code/Charge Code | Project Name / Charge Name | Task | Activity | Claim Type | Adjustment Month | Department | Charge Grade | Staff Type | ITD-SZ\|SH Supervisor | Final Approver | Man-Hours | Submission Time | Timecard Status | Final Approval Time |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
##### Timecard Summary（YTD）（年度工时汇总）
| Project Code/ Charge code | Task | Activity | Claim Type | Staff Type | Year-To-Date Man-Hours |
| :--- | :--- | :--- | :--- | :--- | :--- |
##### Perm Staff IT Record（正式员工或供应商员工IT记录报表）
| Form ID | Submission Time | Timecard Status | Final Approval Time | Staff ID | Staff Name (Chinese) | Staff Name | ITD-SZ\|SH Team | Timecard Month | Project Code/Charge Code | Adjustment Month | Claim Type | Staff Type | ITD-SZ\|SH Supervisor | Man-Hours |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |

---

# 数据需求

## 1. 数据元素定义

以下是系统中涉及的所有关键数据元素及其详细定义，包括现有系统可复用的元素和需要新创建的元素：

### 1.1 现有系统可复用数据元素

| 数据元素名称 | 描述 | 数据类型 | 长度 | 是否必填 | 默认值 | 约束条件或业务规则 | 来源表 |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| Staff ID | 员工唯一标识符 | 字符串 | 10 | 是 | 无 | 必须唯一且符合公司编码规则 | spms_user.id |
| Staff Name (Chinese) | 员工中文姓名 | 字符串 | 50 | 是 | 无 | 不允许重复 | spms_user.user_profiles |
| Staff Name (English) | 员工英文姓名 | 字符串 | 50 | 是 | 无 | 不允许重复 | spms_user.user_profiles |
| Team | 员工所属团队 | 字符串 | 50 | 是 | 无 | 必须存在于组织架构中 | spms_department.name |
| Project Code | 项目代码 | 字符串 | 8 | 是 | 无 | 必须与项目管理系统一致 | spms_project.project_code |
| Task Number | 任务编号 | 字符串 | 50 | 是 | 无 | 唯一标识符 | spms_task.task_number |
| Activity | 活动描述 | 字符串 | 255 | 是 | 无 | 必须从预定义列表中选择 | spms_task.activity |
| Department Head | 部门负责人 | 字符串 | 50 | 是 | 无 | 必须为有效用户 | spms_department.department_head_id |

### 1.2 需要新创建的数据元素

| 数据元素名称 | 描述 | 数据类型 | 长度 | 是否必填 | 默认值 | 约束条件或业务规则 | 目标表 |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| Local Manager | 本地经理 | 字符串 | 50 | 是 | 无 | 必须为有效用户 | spms_department (扩展) |
| Functional Manager | 职能经理 | 字符串 | 50 | 是 | 无 | 必须为有效用户 | spms_department (扩展) |
| Claim Type | 工时类型 | 枚举 | 无 | 是 | NORMAL | 可选值：NORMAL, LEAVE, OT_WORKING_DAYS, OT_HOLIDAYS, OT_STATUTORY_HOLIDAYS | spms_process_data |
| Hours | 工时数 | 浮点数 | 无 | 是 | 0 | 范围：0-24，精度：0.5小时 | spms_process_data |
| Timecard Month | 工时卡月份 | 字符串 | 7 | 是 | 当前月份 | 格式：YYYY-MM | spms_process_data |
| Submission Time | 提交时间 | 时间戳 | 无 | 是 | 当前时间 | 格式：Unix timestamp | spms_process_data |
| Final Approval Time | 最终审批时间 | 时间戳 | 无 | 否 | 无 | 格式：Unix timestamp | spms_process_data |
| Holiday Date | 节假日日期 | 日期 | 无 | 是 | 无 | 格式：YYYY-MM-DD | system_config |
| Holiday Country | 节假日国家 | 字符串 | 50 | 是 | 无 | 国家代码或名称 | system_config |
| Timecard Status | 工时卡状态 | 枚举 | 无 | 是 | DRAFT | 可选值：DRAFT, SUBMITTED, APPROVAL_PENDING, APPROVED, REJECTED | spms_process_data |
| Approval Status | 审批状态 | 枚举 | 无 | 是 | PENDING | 可选值：PENDING, APPROVED, REJECTED, RETURNED | spms_process_data |
| Manager Type | 经理类型 | 枚举 | 无 | 是 | LOCAL_MANAGER | 可选值：LOCAL_MANAGER, FUNCTIONAL_MANAGER | spms_department (扩展) |


---

## 2. 数据表结构设计

以下是核心数据表的设计，确保符合第三范式（3NF），并充分利用现有SPMS系统表结构。

### 2.1 现有系统可复用表结构

#### 2.1.1 用户管理表 (`spms_user`)

| 字段名称 | 数据类型 | 是否允许空值 | 默认值 | 主键/外键标识 | 索引字段 | 用途说明 |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| id | BIGSERIAL | 否 | 无 | 主键 | 是 | 员工唯一标识符 |
| username | VARCHAR(255) | 否 | 无 | 唯一约束 | 是 | 登录用户名 |
| email | VARCHAR(255) | 否 | 无 | 唯一约束 | 是 | 邮箱地址 |
| type | VARCHAR(20) | 否 | STAFF | 无 | 是 | 用户类型：STAFF, VENDOR, MACHINE |
| user_profiles | JSONB | 是 | 无 | 无 | 否 | 存储员工中英文姓名等扩展信息 |
| created_at | TIMESTAMP | 否 | 当前时间 | 无 | 是 | 创建时间 |
| updated_at | TIMESTAMP | 否 | 当前时间 | 无 | 是 | 更新时间 |

#### 2.1.2 部门管理表 (`spms_department`)

| 字段名称 | 数据类型 | 是否允许空值 | 默认值 | 主键/外键标识 | 索引字段 | 用途说明 |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| id | BIGSERIAL | 否 | 无 | 主键 | 是 | 部门唯一标识符 |
| name | VARCHAR(255) | 否 | 无 | 唯一约束 | 是 | 部门名称 |
| department_head_id | BIGINT | 是 | 无 | 外键 | 是 | 部门负责人ID |
| parent | BIGINT | 否 | 无 | 无 | 是 | 上级组织ID |
| type | VARCHAR(50) | 否 | COMPANY | 无 | 是 | 组织类型 |
| level | INTEGER | 否 | 1 | 无 | 是 | 组织层级 |
| active | BOOLEAN | 否 | true | 无 | 是 | 激活状态 |

#### 2.1.3 项目管理表 (`spms_project`)

| 字段名称 | 数据类型 | 是否允许空值 | 默认值 | 主键/外键标识 | 索引字段 | 用途说明 |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| id | BIGSERIAL | 否 | 无 | 主键 | 是 | 项目唯一标识符 |
| project_code | VARCHAR(50) | 否 | 无 | 唯一约束 | 是 | 项目代码 |
| project_name | VARCHAR(200) | 否 | 无 | 无 | 是 | 项目名称 |
| status | VARCHAR(50) | 否 | ACTIVE | 无 | 是 | 项目状态 |
| is_active | BOOLEAN | 否 | true | 无 | 是 | 激活状态 |

#### 2.1.4 任务管理表 (`spms_task`)

| 字段名称 | 数据类型 | 是否允许空值 | 默认值 | 主键/外键标识 | 索引字段 | 用途说明 |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| id | BIGSERIAL | 否 | 无 | 主键 | 是 | 任务唯一标识符 |
| task_number | VARCHAR(50) | 否 | 无 | 唯一约束 | 是 | 任务编号 |
| task_name | VARCHAR(200) | 否 | 无 | 无 | 是 | 任务名称 |
| project_id | BIGINT | 否 | 无 | 外键 | 是 | 所属项目ID |
| activity | VARCHAR(255) | 是 | 无 | 无 | 是 | 活动描述 |
| is_active | BOOLEAN | 否 | true | 无 | 是 | 激活状态 |

### 2.2 需要扩展的表结构

#### 2.2.1 部门表扩展 - 支持多类型经理

**扩展字段：**
- `local_manager_id` (BIGINT) - 本地经理用户ID
- `functional_manager_id` (BIGINT) - 职能经理用户ID
- `manager_type` (VARCHAR(20)) - 经理类型枚举

**实现方式：** 通过部门标签表 (`spms_department_tags`) 存储经理类型映射

#### 2.2.2 流程数据表 (`spms_process_data`) - Timecard数据存储

| 字段名称 | 数据类型 | 是否允许空值 | 默认值 | 主键/外键标识 | 索引字段 | 用途说明 |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| id | BIGSERIAL | 否 | 无 | 主键 | 是 | 流程数据唯一标识符 |
| process_instance_id | VARCHAR(255) | 是 | 无 | 无 | 是 | Flowable流程实例ID |
| user_id | BIGINT | 否 | 无 | 外键 | 是 | 提交用户ID |
| process_definition_key | VARCHAR(255) | 否 | 无 | 无 | 是 | 流程定义键 |
| form_type | VARCHAR(100) | 否 | TIMECARD | 无 | 是 | 表单类型 |
| form_data | JSONB | 是 | 无 | 无 | 否 | 工时卡数据存储 |
| business_data | JSONB | 是 | 无 | 无 | 否 | 业务数据存储 |
| status | VARCHAR(50) | 否 | DRAFT | 无 | 是 | 状态：DRAFT, SUBMITTED, APPROVED, REJECTED |
| approval_comment | TEXT | 是 | 无 | 无 | 否 | 审批意见 |
| version | INTEGER | 否 | 1 | 无 | 是 | 数据版本 |
| created_at | BIGINT | 否 | 当前时间 | 无 | 是 | 创建时间戳 |
| updated_at | BIGINT | 否 | 当前时间 | 无 | 是 | 更新时间戳 |

#### 2.2.3 系统配置表 (`system_config`) - 节假日管理

| 字段名称 | 数据类型 | 是否允许空值 | 默认值 | 主键/外键标识 | 索引字段 | 用途说明 |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| id | BIGSERIAL | 否 | 无 | 主键 | 是 | 配置唯一标识符 |
| key | VARCHAR(255) | 否 | 无 | 唯一约束 | 是 | 配置键 |
| value | TEXT | 是 | 无 | 无 | 否 | 配置值 |
| data_type | VARCHAR(50) | 否 | STRING | 无 | 是 | 数据类型 |
| category | VARCHAR(100) | 否 | HOLIDAY | 无 | 是 | 配置类别 |
| description | TEXT | 是 | 无 | 无 | 否 | 配置描述 |

**节假日数据存储格式：**
```json
{
  "holidays": [
    {
      "date": "2024-01-01",
      "country": "CN",
      "name": "元旦",
      "type": "PUBLIC_HOLIDAY"
    }
  ]
}
```

---

## 3. 数据流图（DFD）描述

### 3.1 高层数据流图

以下为Timecard模块的高层数据流图，展示系统主要组件间的数据流动：

```
+-------------------+       +-------------------+       +-------------------+
| 用户界面          | ----> | Timecard填报服务  | ----> | spms_process_data |
+-------------------+       +-------------------+       +-------------------+
                                |       ^                       |
                                v       |                       v
                        +-------------------+           +-------------------+
                        | Flowable审批引擎  | <-------> | 审批历史数据      |
                        +-------------------+           +-------------------+
                                |
                                v
                        +-------------------+
                        | 报表生成服务      |
                        +-------------------+
                                |
                                v
                        +-------------------+
                        | 外部系统接口      |
                        +-------------------+
```

### 3.2 详细数据流描述

#### 数据流1：用户提交工时卡
- **起点**：用户界面  
- **终点**：Timecard填报服务  
- **数据内容**：`Staff ID`, `Project Code`, `Task Number`, `Activity`, `Claim Type`, `Hours`, `Timecard Month`  
- **触发条件**：用户点击"提交"按钮  
- **数据验证**：
  - 验证项目代码有效性（查询`spms_project`表）
  - 验证任务编号有效性（查询`spms_task`表）
  - 验证工时范围（0-24小时）
  - 验证重复提交规则

#### 数据流2：工时数据存储
- **起点**：Timecard填报服务  
- **终点**：`spms_process_data`表  
- **数据内容**：JSON格式的工时卡数据，包含所有填报信息
- **存储格式**：
```json
{
  "timecard_entries": [
    {
      "date": "2024-01-15",
      "project_code": "PROJ001",
      "task_number": "T001",
      "activity": "开发",
      "claim_type": "NORMAL",
      "hours": 8.0,
      "remark": "日常开发工作"
    }
  ],
  "summary": {
    "total_hours": 160,
    "warning_flags": ["EXCEED_DAILY_LIMIT", "EXCEED_MONTHLY_LIMIT"]
  }
}
```

#### 数据流3：审批流程触发
- **起点**：Timecard填报服务  
- **终点**：Flowable审批引擎  
- **数据内容**：`Process Instance ID`, `Submitter ID`, `Local Manager ID`, `Functional Manager ID`  
- **触发条件**：工时卡状态更新为"SUBMITTED"  
- **审批路由**：基于组织架构自动路由到本地经理→职能经理

#### 数据流4：审批状态更新
- **起点**：Flowable审批引擎  
- **终点**：`spms_process_data`表  
- **数据内容**：`Approval Status`, `Approval Comments`, `Approver ID`, `Approval Time`  
- **触发条件**：审批任务完成

#### 数据流5：报表数据查询
- **起点**：报表生成服务  
- **终点**：`spms_process_data`表及相关表  
- **数据内容**：查询条件（部门、项目、时间范围等）  
- **关联查询**：
  - `spms_user` - 获取员工信息
  - `spms_department` - 获取部门信息
  - `spms_project` - 获取项目信息
  - `spms_task` - 获取任务信息

#### 数据流6：节假日数据同步
- **起点**：系统配置服务  
- **终点**：`system_config`表  
- **数据内容**：节假日配置数据（JSON格式）
- **数据格式**：
```json
{
  "holidays": [
    {
      "date": "2024-01-01",
      "country": "CN",
      "name": "元旦",
      "type": "PUBLIC_HOLIDAY"
    },
    {
      "date": "2024-02-10",
      "country": "CN",
      "name": "春节",
      "type": "PUBLIC_HOLIDAY"
    }
  ]
}
```

### 3.3 数据流处理规则

#### 数据验证规则
- **工时范围验证**：每日工时0-24小时，精度0.5小时
- **项目验证**：只能选择状态为"ACTIVE"的项目
- **重复提交验证**：同一员工同一月份只能有一个有效工时卡
- **节假日验证**：节假日自动标记为绿色背景

#### 业务规则处理
- **工时统计**：按项目+任务+活动维度分组统计总工时
- **警告规则**：
  - 每日超过8小时：红色背景警告
  - 月度超过160小时：黄色背景警告
  - 节假日填写工时：特殊标记
- **审批路由**：基于部门组织架构自动确定审批路径

#### 数据一致性保证
- **事务处理**：工时卡提交和审批状态更新在事务中完成
- **数据完整性**：外键约束确保关联数据有效性
- **审计追踪**：所有操作记录在`spms_user_activity`表中

---

## 4. 外部接口与数据交互

以下是系统与外部系统的数据交互方式：

- **接口名称**：组织数据Excel导入接口  
- **接口类型**：文件上传  
- **数据格式**：Excel (.xlsx)  
- **传输协议**：HTTP Multipart  
- **数据交互频率**：按需导入  
- **数据内容**：员工信息、部门架构、经理汇报线

- **接口名称**：项目数据Excel导入接口  
- **接口类型**：文件上传  
- **数据格式**：Excel (.xlsx)  
- **传输协议**：HTTP Multipart  
- **数据交互频率**：按需导入  
- **数据内容**：项目代码、项目名称、项目状态

- **接口名称**：节假日数据Excel导入接口  
- **接口类型**：文件上传  
- **数据格式**：Excel (.xlsx)  
- **传输协议**：HTTP Multipart  
- **数据交互频率**：按需导入  
- **数据内容**：节假日日期、国家、节假日名称

- **接口名称**：报表Excel导出接口  
- **接口类型**：文件下载  
- **数据格式**：Excel (.xlsx)  
- **传输协议**：HTTP下载  
- **数据交互频率**：按需导出  
- **数据内容**：工时汇总、详细记录、审批状态

- **接口名称**：Flowable BPMN流程引擎接口  
- **接口类型**：内部集成  
- **数据格式**：JSON  
- **传输协议**：内部调用  
- **数据交互频率**：实时  
- **数据内容**：流程实例、任务分配、审批状态

- **接口名称**：系统配置管理接口  
- **接口类型**：内部集成  
- **数据格式**：JSON  
- **传输协议**：内部调用  
- **数据交互频率**：按需配置  
- **数据内容**：节假日配置、审批路径配置、业务规则配置


---

# 表单布局

## 概述

本章节详细描述了"Timecard模块重构"项目的表单布局设计，基于已确认的Timecard Module Specification功能实现，采用与原系统一致的React + MUI框架。设计目标包括：

- 提供直观、高效且一致的界面体验
- 确保跨模块的设计一致性，减少学习成本
- 遵循格式塔原理、菲茨定律和无障碍设计原则
- 采用响应式设计，适配不同设备尺寸

========================================
Timecard工时管理系统 - 详细设计
========================================

本章节详细记录Timecard工时管理系统中每个TSX组件的设计风格、设计要素和实现细节。

========================================
二、Timecard模块（工时卡核心功能）
========================================

2.1 /components/timecard/TimecardForm.tsx - 工时卡表单主页
----------------------------------------

【设计定位】
工时卡模块的主控制器，协调日历、汇总、上月调整等子模块

【设计风格】
- 卡片堆叠布局：space-y-6 纵向间距
- 信息展示优先：Staff Information采用只读网格布局
- 时间线操作：月份切换 + 状态管理 + 批量操作

【设计要素】

A. 员工信息卡片（Staff Information Card）
- 布局：grid grid-cols-1 md:grid-cols-2 gap-4
- 字段清单（8个）：
  1. Staff ID（员工编号）
  2. Staff Name (English)（英文姓名）
  3. Staff Name (Chinese)（中文姓名）
  4. Team（团队）
  5. Staff Type（员工类型）
  6. Department Head（部门主管）
  7. Final Approval（最终审批人）
  8. Timecard Month（工时月份）
- 样式规范：
  * 标签：text-slate-600 text-sm
  * 内容：text-slate-900
- 数据源：mockStaffInfo

B. 状态管理
- currentMonth: Date - 当前查看月份（默认2025年11月）
- entries: TimecardEntry[] - 工时记录数组
- status: "DRAFT" | "SUBMITTED" - 表单状态
- showPreviousMonthAdjust: boolean - 上月调整弹窗显示状态

C. 月份导航
- 左右箭头按钮：ChevronLeft / ChevronRight图标
- 月份计算逻辑：
  * Previous: getMonth() - 1
  * Next: getMonth() + 1
  * 始终设置为每月1日

D. 操作按钮区域
- 布局：flex flex-wrap gap-3
- 三个核心按钮：
  1. Save Draft（保存草稿）
     * 图标：Save
     * 变体：outline
     * 禁用条件：status === "SUBMITTED"
  2. Submit Timecard（提交工时卡）
     * 图标：Send
     * 变体：default
     * 提交后更新状态并显示Toast
  3. Previous Month Adjust（上月调整）
     * 图标：FileEdit
     * 变体：outline
     * 触发弹窗

E. 子组件集成
- TimecardSummary：传入entries进行汇总
- CalendarView：双向绑定
  * 输入：currentMonth, entries
  * 输出：onEntriesUpdate回调
- PreviousMonthAdjust：Dialog模式

【数据流】
TimecardForm → CalendarView → ProjectEntryDialog
                              → BulkCopyDialog
           → TimecardSummary
           → PreviousMonthAdjust

----------------------------------------
2.2 /components/timecard/CalendarView.tsx - 日历视图
----------------------------------------

【设计定位】
核心交互界面，以月历形式展示和管理工时记录

【设计风格】
- 网格化日历：7列固定网格（周日-周六）
- 色彩警告系统：
  * 绿色（bg-green-50）：节假日
  * 红色（bg-red-50）：超时（>8h）或周末工作
  * 白色（bg-white）：正常工作日
- 悬停交互：hover:border-slate-400 + group机制

【设计要素】

A. 日历网格结构
- 容器：grid grid-cols-7 gap-0 border border-slate-200
- 表头：['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat']
  * 样式：bg-slate-100 p-2 text-center text-sm text-slate-700
- 日期单元格：aspect-square（正方形比例）

B. 日期单元格设计
- 顶部区域：
  * 左侧：日期数字（text-sm text-slate-900）
  * 右侧：总工时 + 操作按钮
    - 工时显示：{totalHours}h（text-xs text-slate-600）
    - Copy按钮：Copy图标，group-hover显示
    - Delete按钮：Trash2图标，红色高亮
- 内容区域：
  * 最多显示2个项目条目
  * 超出显示"+X more"
  * 条目样式：
    - 文本：text-xs px-1 py-0.5 rounded border
    - 颜色：根据projectCode动态分配（PROJECT_COLORS数组）
    - 内容：projectCode + hours

C. 颜色警告逻辑
- 节假日判断：mockHolidays.some(h => h.date === dateStr)
- 周末判断：dayOfWeek === 0 || dayOfWeek === 6
- 超时判断：totalHours > 8
- 优先级：节假日 > 超时/周末工作 > 正常

D. 交互功能
1. 点击日期：handleDayClick
   - 打开ProjectEntryDialog
   - 传递当前日期和已有entries

2. Copy按钮：handleCopyDay
   - 检查是否有entries（无则提示错误）
   - 打开BulkCopyDialog
   - 传递源日期和源entries

3. Delete按钮：handleDeleteDay
   - 确认有entries
   - 过滤掉当前日期的所有entries
   - 显示删除数量Toast

E. 图例说明
- Holiday：绿色块 + "Holiday"文字
- Excess Hours / Weekend：红色块 + 说明文字

F. 数据管理
- getEntriesForDate：按日期筛选entries
- getTotalHoursForDate：计算单日总工时
- getProjectColor：根据项目代码分配颜色（循环使用8种颜色）

【状态管理】
- selectedDate: string | null - 选中的日期
- showEntryDialog: boolean - 项目录入弹窗
- showBulkCopyDialog: boolean - 批量复制弹窗
- copySourceDate: string | null - 复制源日期

【布局尺寸】
- 空白单元格：startingDayOfWeek数量（月初对齐）
- 实际日期：1到daysInMonth循环生成

----------------------------------------
2.3 /components/timecard/ProjectEntryDialog.tsx - 项目录入弹窗
----------------------------------------

【设计定位】
单日多项目工时录入的专用表单Dialog

【设计风格】
- 多表单堆叠：动态Entry列表，每个Entry独立边框卡片
- 自动填充机制：输入项目代码后自动匹配项目信息
- 双模式搜索：手动输入 + Command搜索弹窗

【设计要素】

A. Dialog配置
- 尺寸：max-w-4xl max-h-[90vh] overflow-y-auto
- 标题：Project Entries - {格式化日期}
- 描述：Add or edit timecard entries for this date

B. Entry表单结构（循环渲染）
每个Entry包含以下字段：

1. 顶部标题栏
   - 左侧：Entry {index + 1}
   - 右侧：删除按钮（仅当entries.length > 1时显示）

2. 表单字段（grid grid-cols-1 md:grid-cols-2 gap-4）

   ① Project Code *（必填）
   - Input组件 + Search按钮
   - 手动输入触发handleProjectCodeChange：
     * 在mockProjects中查找匹配且status==='ACTIVE'的项目
     * 找到则自动填充projectName、taskNumber、activity
     * 未找到显示错误Toast
   - Search按钮打开Popover：
     * Command组件实现模糊搜索
     * CommandInput输入搜索关键词
     * CommandList显示activeProjects
     * 点击选中项触发handleProjectSelect

   ② Project Name（只读）
   - 灰色背景：bg-slate-50
   - 自动填充值

   ③ Task Number *（必填，只读）
   - 灰色背景：bg-slate-50
   - 自动填充值

   ④ Activity *（必填，只读）
   - 灰色背景：bg-slate-50
   - 自动填充值

   ⑤ Claim Type *（必填）
   - Select下拉框
   - 选项：CLAIM_TYPE_OPTIONS
     * NORMAL - Normal
     * LEAVE - Leave
     * OT_WORKING_DAYS - OT - Working Days
     * OT_HOLIDAYS - OT - Holidays
     * OT_STATUTORY_HOLIDAYS - OT - Statutory Holidays

   ⑥ Hours *（必填）
   - Input type="number"
   - 参数：step="0.5" min="0" max="24"
   - 占位符：0.0

   ⑦ Remark（可选）
   - Textarea组件
   - md:col-span-2（占据两列）
   - rows={2}
   - 占位符：Optional notes

C. 动态Entry管理
- 添加Entry：handleAddEntry
  * 按钮：<Plus /> Add Another Entry
  * 位置：所有Entry下方
  * 样式：variant="outline" w-full
  * 默认值：空表单，claimType='NORMAL'

- 删除Entry：handleRemoveEntry
  * 图标：<Trash2 /> 红色
  * 位置：Entry标题栏右侧
  * 限制：至少保留1个Entry

D. 初始化逻辑（useEffect）
- 依赖：[open, entries]
- 条件分支：
  * entries.length > 0：映射已有数据到localEntries
  * entries.length === 0：创建单个空Entry

E. 表单验证（validateAndSave）
校验规则：
1. 必填字段：projectCode, taskNumber, activity, hours
2. 小时数范围：0-24
3. 小时数格式：可转换为浮点数
4. 项目代码有效性：必须在mockProjects中存在且status='ACTIVE'

验证通过后：
- 生成id：`${date}-${idx}`
- 转换hours为number类型
- 调用onEntriesUpdate(savedEntries)
- 显示成功Toast
- 关闭Dialog

F. 状态管理
- localEntries: EntryForm[] - 本地表单数组
- searchOpen: number | null - 当前打开的搜索Popover索引

【交互细节】
- Project Code输入框失焦时不会清空，允许用户修改
- Search Popover与当前Entry索引绑定，确保多Entry场景下正确定位
- Command搜索支持模糊匹配projectCode

----------------------------------------
2.4 /components/timecard/TimecardSummary.tsx - 工时汇总
----------------------------------------

【设计定位】
按"项目代码+任务编号+活动"维度统计总工时，并提供超时警告

【设计风格】
- 表格化展示：清晰的列对齐和边框分隔
- 警告提示优先：超160小时显示黄色Alert
- 汇总行强调：bg-slate-50背景 + 粗体字

【设计要素】

A. 数据聚合逻辑（getSummaryData）
- 聚合维度：`${projectCode}|${taskNumber}|${activity}`
- 数据结构：
  * projectCode: string
  * projectName: string
  * taskNumber: string
  * activity: string
  * totalHours: number（累加）
- 实现：Map<string, SummaryType>，遍历entries累加hours

B. 超时警告系统
- 阈值：160小时（20工作日 × 8小时/天）
- 触发条件：totalHours > 160
- Alert组件：
  * 背景：bg-yellow-50
  * 边框：border-yellow-300
  * 图标：AlertCircle（黄色）
  * 文字：text-yellow-800
  * 内容：Total hours ({totalHours}h) exceed 160 hours...

C. 表格结构
- 表头（5列）：
  1. Project Code（左对齐）
  2. Project Name（左对齐）
  3. Task Number（左对齐）
  4. Activity（左对齐）
  5. Total Hours（右对齐）

- 数据行：
  * 边框：border-b border-slate-100
  * 文字：text-slate-900
  * 小时数格式：toFixed(1)（保留1位小数）

- 汇总行：
  * 背景：bg-slate-50
  * 合并单元格：colSpan={4}
  * 粗体：<strong>Total</strong> / <strong>{totalHours.toFixed(1)}</strong>
  * 对齐：右对齐

D. 空状态处理
- 条件：summaryData.length === 0
- 显示：text-slate-600 text-center py-8
- 文案：No timecard entries yet

E. 样式规范
- 表头：text-left py-2 px-4 text-slate-700, border-b border-slate-200
- 单元格：py-2 px-4
- 小时数列：text-right（右对齐）

【计算逻辑】
总工时 = summaryData.reduce((sum, item) => sum + item.totalHours, 0)

【数据流】
TimecardForm.entries → getSummaryData() → summaryData数组 → 表格渲染

----------------------------------------
2.5 /components/timecard/BulkCopyDialog.tsx - 批量复制弹窗
----------------------------------------

【设计定位】
将单日工时记录批量复制到多个目标日期的专用工具

【设计风格】
- 月历选择器：7×N网格，Checkbox交互
- 智能快捷选择：3个预设按钮 + 手动勾选
- 视觉标记系统：蓝色源、绿色节假日、橙色已有数据

【设计要素】

A. Dialog配置
- 尺寸：max-w-2xl
- 标题：Bulk Copy Timecard Entries
- 描述：Copy {sourceEntries.length} entries from {源日期} to selected dates

B. 快捷选择按钮（4个）
1. Select This Week（选择本周）
   - 逻辑：getWeekDates(sourceDate)
   - 范围：从周日到周六（0-6）
   - 排除：周末（dayOfWeek !== 0 && dayOfWeek !== 6）
   - 排除：源日期本身
   - 限制：仅当前月份内

2. Select All Working Days（选择所有工作日）
   - 条件：isWorkingDay (非周末 && 非节假日)
   - 排除：源日期

3. Select All Blank Days（选择所有空白日）
   - 条件：isBlankDay（无entries）&& 非周末
   - 排除：源日期
   - 关键：确保不选择周末

4. Clear All（清空选择）
   - 重置selectedDates为空Set

C. 日历网格
- 布局：grid grid-cols-7 gap-2
- 高度：ScrollArea h-[400px]
- 表头：['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat']

D. 日期单元格设计
- 基础样式：border rounded p-2 text-center relative

- 条件样式：
  ① 源日期（isSource）
     * 背景：bg-blue-100
     * 边框：border-blue-400
     * 内容：日期数字 + "Source"标签（text-xs text-blue-600）
     * 禁用选择

  ② 节假日（isHoliday）
     * 背景：bg-green-50

  ③ 周末（isWknd && !isHoliday）
     * 背景：bg-slate-100

  ④ 已有数据（hasEntries && !isSource）
     * 边框：border-orange-300
     * 标签："Has data"（text-xs text-orange-600）

- 可选日期内容：
  * 日期数字：text-sm
  * Checkbox：居中对齐
  * 状态标签：条件显示

E. 图例说明（3个）
- Source Date：蓝色块 + 边框
- Holiday：绿色块
- Has Entries：橙色边框

F. 选择状态显示
- 位置：图例下方
- 内容：Selected: {selectedDates.size} days

G. 底部操作按钮
1. Cancel（取消）
   - variant="outline"
   
2. Copy to X Days（复制到X天）
   - 禁用条件：selectedDates.size === 0
   - 文本动态：{selectedDates.size} Day/Days
   - 点击触发：handleCopy

H. 复制逻辑（handleCopy）
1. 验证：selectedDates.size > 0
2. 生成newEntries：
   - 双层循环：selectedDates × sourceEntries
   - 更新字段：
     * id: `${entry.id}-${targetDate}-${Date.now()}`
     * date: targetDate
   - 保留其他字段
3. 调用onCopyComplete(newEntries)
4. Toast提示：Copied {entries数量} entries to {日期数量} days
5. 关闭Dialog，重置selectedDates

I. 辅助函数
- getDaysInMonth()：生成当月所有日期对象数组
  * date: 日期字符串
  * day: 日期数字
  * dayOfWeek: 星期几

- isWeekend(dayOfWeek)：判断是否周末
- isWorkingDay(dayOfWeek, dateStr)：非周末且非节假日
- isBlankDay(dateStr)：无任何entries
- getWeekDates(targetDate)：获取目标日期所在周的工作日

【状态管理】
- selectedDates: Set<string> - 选中的日期集合

【数据流】
CalendarView → BulkCopyDialog → 生成newEntries → onCopyComplete → 更新CalendarView.entries

----------------------------------------
2.6 /components/timecard/PreviousMonthAdjust.tsx - 上月调整
----------------------------------------

【设计定位】
允许员工在当前月份补录上个月的工时记录

【设计风格】
- 紧凑表单：max-w-md对话框
- 日期优先：Calendar日期选择器
- 关联验证：项目选择后自动填充

【设计要素】

A. Dialog配置
- 尺寸：max-w-md
- 标题：Previous Month Adjust
- 描述：Adjust timecard entries from the previous month

B. 表单字段（6个，垂直排列 space-y-4）

1. Date *（必填）
   - 组件：Popover + Calendar
   - 触发器：
     * 样式：模拟Input（border border-slate-200 bg-white）
     * 图标：CalendarIcon
     * 文本：{date.toLocaleDateString()} 或 'Pick a date'
   - Calendar配置：
     * mode="single"
     * selected={date}
     * onSelect={setDate}

2. Project Code *（必填）
   - 组件：Select下拉框
   - 占位符：Select project
   - 选项来源：mockProjects.filter(p => p.status === 'ACTIVE')
   - 显示格式：{projectCode} - {projectName}
   - 变更触发：handleProjectCodeChange
     * 查找匹配项目
     * 自动填充taskNumber和activity

3. Task Number *（必填，只读）
   - Input组件
   - 背景：bg-slate-50
   - readOnly属性

4. Activity *（必填，只读）
   - Input组件
   - 背景：bg-slate-50
   - readOnly属性

5. Claim Type *（必填）
   - 组件：Select下拉框
   - 占位符：Select claim type
   - 选项来源：CLAIM_TYPE_OPTIONS
   - 显示格式：{option.label}

6. Hours *（必填）
   - Input type="number"
   - 参数：step="0.5" min="0" max="24"
   - 占位符：0.0

C. 表单验证（handleSubmit）
1. 必填字段检查：date, projectCode, taskNumber, activity, claimType, hours
2. 日期范围验证：
   - 计算上月起始日：lastMonth.setMonth(getMonth() - 1), setDate(1)
   - 计算上月结束日：lastMonthEnd
   - 条件：selectedDate >= lastMonth && selectedDate <= lastMonthEnd
   - 错误提示：Date must be in the previous month

3. 验证通过：
   - Toast提示：Previous month adjustment saved
   - 关闭Dialog

D. 底部操作按钮
1. Cancel（取消）
   - variant="outline"
   
2. Save Adjustment（保存调整）
   - 触发handleSubmit

【状态管理】
- date: Date | undefined
- projectCode: string
- taskNumber: string
- activity: string
- claimType: string
- hours: string

【设计意图】
防止员工遗漏上月工时，提供补录入口，同时限制日期范围以避免历史数据混乱。

========================================
三、Holiday模块（节假日设置）
========================================

3.1 /components/holiday/HolidaySettings.tsx - 节假日设置
----------------------------------------

【设计定位】
多国节假日可视化管理，支持增删查和Excel导入导出

【设计风格】
- 月历交互：正方形日历块，绿色高亮节假日
- 点击添加：任意日期点击打开添加弹窗
- 国家分类：支持CN/HK/US/UK等多国节假日

【设计要素】

A. 页面布局
- 卡片容器：Card组件
- 标题：Holiday Settings
- 描述：Manage holidays across different countries
- 操作区：右上角Import/Export按钮

B. 月份选择器
- 组件：Input type="month"
- 值格式：YYYY-MM（如"2025-01"）
- 默认值：2025-01（2025年1月）
- 变更触发：更新currentMonth状态

C. 日历网格
- 布局：grid grid-cols-7 gap-0 border border-slate-200
- 表头：['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat']
  * 样式：bg-slate-100 p-2 text-center text-sm text-slate-700

D. 日期单元格设计
- 尺寸：aspect-square（正方形）
- 基础样式：border border-slate-200 p-2 cursor-pointer hover:border-slate-400
- 节假日样式：bg-green-50

- 单元格内容：
  ① 顶部栏：
     - 左侧：日期数字（text-sm text-slate-900）
     - 右侧：删除按钮（仅节假日显示）
       * 图标：Trash2（红色）
       * 尺寸：h-6 w-6 p-0
       * 事件：e.stopPropagation() + handleRemoveHoliday

  ② 节假日信息（条件显示）：
     - 节假日名称：text-xs text-slate-900 truncate
     - 国家代码：text-xs text-slate-600

E. 交互功能
1. 点击日期：handleDayClick
   - 设置selectedDate
   - 初始化newHoliday表单（日期预填充）
   - 打开添加Dialog

2. 删除节假日：handleRemoveHoliday
   - 过滤掉指定日期的holiday
   - Toast提示：Holiday removed

3. 导出：handleExport
   - 生成CSV：['Date', 'Country', 'Name', 'Type']
   - Blob下载：holidays.csv
   - Toast提示：Holidays exported

4. 导入：handleImport
   - Toast提示：Excel import functionality（占位功能）

F. 添加节假日Dialog
- 标题：Add Holiday - {格式化日期}
- 表单字段（4个）：

  ① Date（只读）
     - 显示选中日期
     - 灰色背景

  ② Country *（必填）
     - Select下拉框
     - 选项：CN/HK/US/UK

  ③ Holiday Name *（必填）
     - Input组件
     - 占位符：e.g., New Year's Day

  ④ Type（节假日类型）
     - Select下拉框
     - 选项：PUBLIC_HOLIDAY / COMPANY_HOLIDAY

- 验证逻辑（handleAddHoliday）：
  1. 必填检查：date, name
  2. 重复检查：同一日期不允许重复添加
  3. 生成id：holiday-${Date.now()}
  4. 添加到holidays数组
  5. Toast提示：Holiday added successfully

G. 提示信息
- 位置：日历下方
- 样式：p-4 bg-green-50 border border-green-200 rounded
- 内容：Click on any date to add a holiday. Green highlighted dates are holidays.

【状态管理】
- holidays: Holiday[] - 节假日列表（初始值：initialHolidays）
- selectedDate: string | null - 选中日期
- showAddDialog: boolean - 添加弹窗显示状态
- currentMonth: Date - 当前查看月份
- newHoliday: 表单对象
  * date: string
  * country: string（默认'CN'）
  * name: string
  * type: 'PUBLIC_HOLIDAY' | 'COMPANY_HOLIDAY'

【数据源】
mockHolidays（初始节假日数据）：
- 2025-01-01: 中国新年
- 2025-01-28~30: 春节
- 2025-04-04: 清明节
- 2025-05-01: 劳动节
- 2025-10-01: 国庆节

【设计特点】
- 正方形日历块：aspect-square确保统一视觉
- 绿色标记：节假日一目了然
- 快速删除：悬停显示删除按钮
- 支持多国：country字段区分不同国家节假日

========================================
四、Project模块（项目管理）
========================================

4.1 /components/project/ProjectManagement.tsx - 项目管理
----------------------------------------

【设计定位】
项目、任务、活动的CRUD管理，支持状态切换和唯一性校验

【设计风格】
- 列表+表格：标准管理界面布局
- 搜索过滤：实时搜索 + 状态筛选
- 编辑优先：不支持删除，仅允许标记为COMPLETED

【设计要素】

A. 页面布局
- 卡片容器：Card组件
- 标题区域：
  * 主标题：Project Management
  * 副标题：Manage projects, tasks, and activities
- 操作按钮：Import / Export / Add Project

B. 筛选区域
- 布局：flex flex-col md:flex-row gap-4

1. 搜索框
   - 组件：Input with icon
   - 图标：Search（左侧绝对定位）
   - 占位符：Search by project code, name, or task number...
   - 样式：pl-10（为图标留空间）
   - 实时搜索：onChange更新searchTerm

2. 状态筛选
   - 组件：Select下拉框
   - 宽度：w-full md:w-48
   - 选项：
     * ALL - All Status
     * ACTIVE - Active
     * COMPLETED - Completed

C. 项目表格
- 列定义（6列）：
  1. Project Code（项目代码）
  2. Project Name（项目名称）
  3. Task Number（任务编号）
  4. Activity（活动）
  5. Status（状态）- Badge组件
     * ACTIVE: variant='default'
     * COMPLETED: variant='secondary'
  6. Actions（操作）- 编辑按钮
     * 图标：Pencil
     * variant='ghost'
     * size='sm'

- 样式规范：
  * 表头：border-b border-slate-200, text-left py-3 px-4 text-slate-700
  * 数据行：border-b border-slate-100 hover:bg-slate-50
  * 单元格：py-3 px-4 text-slate-900

D. 筛选逻辑（filteredProjects）
- 搜索匹配：
  * projectCode.toLowerCase().includes(searchTerm.toLowerCase())
  * OR projectName.toLowerCase().includes(...)
  * OR taskNumber.toLowerCase().includes(...)
- 状态匹配：
  * statusFilter === 'ALL' OR project.status === statusFilter

E. 空状态处理
- 条件：filteredProjects.length === 0
- 显示：colSpan={6} text-center py-8 text-slate-600
- 文案：No projects found

F. 统计信息
- 位置：表格下方
- 内容：Showing {filteredProjects.length} of {projects.length} projects

G. 添加/编辑Dialog
- 标题：
  * 添加模式：Add New Project
  * 编辑模式：Edit Project
- 描述：
  * 添加模式：Create a new project with tasks and activities
  * 编辑模式：Update project information

- 表单字段（5个）：

  ① Project Code *（必填）
     - Input组件
     - 占位符：e.g., PROJ001
     - 编辑模式：disabled（不允许修改项目代码）

  ② Project Name *（必填）
     - Input组件
     - 占位符：e.g., System Upgrade

  ③ Task Number *（必填）
     - Input组件
     - 占位符：e.g., T001

  ④ Activity *（必填）
     - Input组件
     - 占位符：e.g., Development

  ⑤ Status（状态）
     - Select下拉框
     - 选项：ACTIVE / COMPLETED

- 验证逻辑（handleSave）：
  1. 必填检查：projectCode, projectName, taskNumber, activity
  2. 唯一性检查：
     - 查找duplicate：projectCode相同 && id不同
     - 错误提示：Project code already exists
  3. 添加模式：
     - 生成id：project-${Date.now()}
     - 添加到projects数组
     - Toast：Project created successfully
  4. 编辑模式：
     - 更新projects数组中的对应项
     - Toast：Project updated successfully

H. 导入导出
- Export：
  * CSV格式：['Project Code', 'Project Name', 'Task Number', 'Activity', 'Status']
  * 文件名：projects.csv
  * Toast：Projects exported

- Import：
  * Toast：Excel import functionality（占位）

【状态管理】
- projects: Project[] - 项目列表（初始值：initialProjects）
- searchTerm: string - 搜索关键词
- statusFilter: 'ALL' | ProjectStatus - 状态筛选
- showDialog: boolean - Dialog显示状态
- editingProject: Project | null - 当前编辑的项目
- formData: 表单对象
  * projectCode: string
  * projectName: string
  * taskNumber: string
  * activity: string
  * status: ProjectStatus

【设计原则】
1. 不允许删除：避免历史数据关联问题，使用COMPLETED状态替代
2. 代码唯一性：projectCode作为业务主键，严格校验
3. 编辑锁定：编辑模式下projectCode不可修改
4. 实时搜索：输入即时过滤，无需点击搜索按钮

【数据源】
mockProjects（初始项目数据）：
1. PROJ001 - System Upgrade - T001 - Development - ACTIVE
2. PROJ002 - Mobile App - T002 - Testing - ACTIVE
3. PROJ003 - Database Migration - T003 - Analysis - ACTIVE
4. PROJ004 - UI Redesign - T004 - Design - COMPLETED
5. PROJ005 - API Integration - T005 - Development - ACTIVE

========================================
五、Reports模块（报表系统）
========================================

5.1 /components/reports/Reports.tsx - 报表主页
----------------------------------------

【设计定位】
报表模块路由容器，Tab方式切换四种报表

【设计风格】
- Tab切换：清晰的报表分类
- 响应式布局：移动端2列，桌面端4列

【设计要素】

A. 卡片容器
- 标题：Reports
- 描述：Generate and view various timecard reports

B. Tab配置
- 默认值：'summary'
- TabsList布局：grid w-full grid-cols-2 lg:grid-cols-4

- Tab列表（4个）：
  1. summary - Summary（汇总报表）
     * 组件：TimecardSummaryReport
  2. individual - Individual（个人报表）
     * 组件：IndividualTimecardReport
  3. details - Details（明细报表）
     * 组件：TimecardDetailsReport
  4. ot - OT Record（加班记录报表）
     * 组件：PermStaffOTReport

【布局逻辑】
- 小屏：2列（summary/individual 第一行，details/ot 第二行）
- 大屏：4列（一行展示所有Tab）

----------------------------------------
5.2 /components/reports/IndividualTimecardReport.tsx - 个人工时汇总报表
----------------------------------------

【设计定位】
Individual Timecard Summary - 按表单维度汇总个人提交记录

【设计风格】
- 筛选器优先：3个筛选条件横向排列
- 表格宽度：支持横向滚动，保留所有字段

【设计要素】

A. 筛选区域（3个）
- 布局：grid grid-cols-1 md:grid-cols-3 gap-4

1. Staff ID
   - Select下拉框
   - 占位符：All Staff
   - 选项：
     * all - All Staff
     * EMP001 - EMP001 - Wei Zhang
     * EMP002 - EMP002 - John Smith
     * EMP003 - EMP003 - Sarah Johnson

2. Month
   - Input type="month"
   - 默认值：2025-11

3. Status
   - Select下拉框
   - 占位符：All Status
   - 选项：
     * all - All Status
     * DRAFT - Draft
     * SUBMITTED - Submitted
     * APPROVED - Approved

B. 导出按钮
- 位置：右上角
- 图标：Download
- 文本：Export Report
- 触发：handleExport → Toast提示

C. 报表表格（11列）
- 表头字段：
  1. Form ID（表单ID）
  2. Timecard Status（工时卡状态）- Badge
  3. Submission Time（提交时间）
  4. Final Approval Time（最终审批时间）
  5. Staff ID（员工编号）
  6. Staff Name (Chinese)（员工中文名）
  7. Staff Name（员工英文名）
  8. Staff Type（员工类型）
  9. Timecard Month（工时月份）
  10. ITD-SZ|SH Supervisor（主管）
  11. Final Approver（最终审批人）

- 样式规范：
  * 表头：text-left py-3 px-4 text-slate-700 whitespace-nowrap
  * 数据行：border-b border-slate-100
  * 单元格：py-3 px-4 text-slate-900 whitespace-nowrap

- Badge样式：
  * APPROVED: variant='default'
  * 其他：variant='secondary'

D. Mock数据
- 2条示例记录：
  * TC-2025-11-001：张伟，2025-11，已审批
  * TC-2025-10-001：张伟，2025-10，已审批

【字段说明】
- Form ID：工时表单的唯一标识
- Timecard Status：表单审批状态
- Submission Time：员工提交时间戳
- Final Approval Time：最终审批通过时间戳

【设计目的】
汇总个人历史提交记录，便于查询审批流程和时间线。

----------------------------------------
5.3 /components/reports/TimecardDetailsReport.tsx - 工时明细报表
----------------------------------------

【设计定位】
Timecard Details Report - 最详细的工时记录明细，包含所有业务字段

【设计风格】
- 宽表格：22列，横向滚动
- 多维筛选：5个筛选条件
- 合计行：bg-slate-50强调总工时

【设计要素】

A. 筛选区域（5个）
- 布局：grid grid-cols-1 md:grid-cols-2 lg:grid-cols-5 gap-4

1. Department（部门）
   - Select下拉框
   - 占位符：All Departments
   - 选项：all / it / infra

2. Project（项目）
   - Select下拉框
   - 占位符：All Projects
   - 选项：all / PROJ001 / PROJ002

3. Staff Type（员工类型）
   - Select下拉框
   - 占位符：All Types
   - 选项：all / permanent / vendor

4. Date From（起始日期）
   - Input type="date"
   - 默认值：2025-11-01

5. Date To（结束日期）
   - Input type="date"
   - 默认值：2025-11-30

B. 导出按钮
- 位置：右上角
- 功能：同IndividualTimecardReport

C. 报表表格（22列）
- 表头字段（按顺序）：
  1. Form ID
  2. Staff ID
  3. Staff Name (Chinese)
  4. Staff Name
  5. ITD-SZ|SH Team
  6. Timecard Month
  7. Code Type（代码类型：Project Code / Charge Code）
  8. Project Code/Charge Code
  9. Project Name / Charge Name
  10. Task
  11. Activity
  12. Claim Type - Badge
  13. Adjustment Month（调整月份）
  14. Department
  15. Charge Grade（计费等级）
  16. Staff Type
  17. ITD-SZ|SH Supervisor
  18. Final Approver
  19. Man-Hours（工时）- 右对齐
  20. Submission Time
  21. Timecard Status - Badge
  22. Final Approval Time

- 数据行样式：
  * 边框：border-b border-slate-100
  * 单元格：py-3 px-4 text-slate-900 whitespace-nowrap

- Badge样式：
  * Claim Type:
    - NORMAL: variant='secondary'
    - 其他（OT相关）: variant='default'
    - 显示：replace(/_/g, ' ')（下划线转空格）
  * Timecard Status:
    - APPROVED: variant='default'
    - 其他: variant='secondary'

D. 合计行
- 样式：bg-slate-50
- 结构：
  * 前18列合并：colSpan={18}
  * 显示：<strong>Total</strong>
  * 第19列：<strong>{总工时}</strong>（右对齐）
  * 后3列：colSpan={3}（空白）

- 计算逻辑：
  mockData.reduce((sum, row) => sum + row.manHours, 0)

E. Mock数据
- 2条示例记录：
  1. TC-2025-11-001：张伟，PROJ001，开发，NORMAL，160小时
  2. TC-2025-11-002：李娜，CC-001，支持，OT_WORKING_DAYS，168小时

【字段说明】
- Code Type：区分Project Code（项目代码）和Charge Code（计费代码）
- Adjustment Month：标记是否为补录的上月工时
- Charge Grade：计费等级（A/B/C等）
- Man-Hours：该条记录的总工时数

【设计目的】
提供最完整的工时明细数据，支持财务核算、项目成本分析、人力资源统计等多种业务场景。

----------------------------------------
5.4 /components/reports/TimecardSummaryReport.tsx - 年度工时汇总
----------------------------------------

【设计定位】
Timecard Summary YTD (Year-To-Date) - 按项目+任务+活动维度的年度汇总

【设计风格】
- 精简表格：6列，聚焦核心统计
- 年度视角：YTD（年初至今）工时累计
- 分类汇总：按Claim Type和Staff Type分组

【设计要素】

A. 筛选区域（3个）
- 布局：grid grid-cols-1 md:grid-cols-3 gap-4

1. Year（年份）
   - Select下拉框
   - 选项：2025 / 2024 / 2023

2. Staff Type（员工类型）
   - Select下拉框
   - 占位符：All Types
   - 选项：all / permanent / vendor

3. Claim Type（计费类型）
   - Select下拉框
   - 占位符：All Claim Types
   - 选项：
     * all - All Claim Types
     * NORMAL - Normal
     * OT_WORKING_DAYS - OT - Working Days
     * OT_HOLIDAYS - OT - Holidays

B. 导出按钮
- 功能同上

C. 报表表格（6列）
- 表头字段：
  1. Project Code/ Charge code（项目代码/计费代码）
  2. Task（任务）
  3. Activity（活动）
  4. Claim Type - Badge
  5. Staff Type（员工类型）
  6. Year-To-Date Man-Hours（年度累计工时）- 右对齐

- 数据行样式：
  * 边框：border-b border-slate-100
  * 单元格：py-3 px-4 text-slate-900 whitespace-nowrap
  * 工时显示：toLocaleString()（千分位格式）

- Badge样式：
  * NORMAL: variant='secondary'
  * OT相关: variant='default'

D. 合计行
- 样式：bg-slate-50
- 结构：
  * 前5列合并：colSpan={5}
  * 显示：<strong>Total</strong>
  * 第6列：<strong>{总工时}</strong>（toLocaleString()）

- 计算逻辑：
  mockData.reduce((sum, row) => sum + row.ytdManHours, 0)

E. Mock数据
- 4条示例记录：
  1. PROJ001 - T001 - Development - NORMAL - Permanent - 1,280h
  2. PROJ001 - T001 - Development - OT_WORKING_DAYS - Permanent - 96h
  3. PROJ002 - T002 - Testing - NORMAL - Vendor - 960h
  4. CC-001 - T003 - Support - NORMAL - Permanent - 640h

【字段说明】
- Project Code/ Charge code：合并显示项目代码和计费代码
- Year-To-Date Man-Hours：从年初（1月1日）到当前日期的累计工时

【设计目的】
为年度项目成本核算、人力资源规划提供汇总数据，支持按年度查看不同项目的工时分布。

----------------------------------------
5.5 /components/reports/PermStaffOTReport.tsx - 正式员工加班记录报表
----------------------------------------

【设计定位】
Perm Staff IT Record - 正式员工（Permanent Staff）的加班（OT）记录专用报表

【设计风格】
- 加班专注：仅显示OT类型的Claim Type
- 员工类型筛选：支持查看Permanent/Vendor/All
- 详细记录：15列完整信息

【设计要素】

A. 筛选区域（3个）
- 布局：grid grid-cols-1 md:grid-cols-3 gap-4

1. Staff Type（员工类型）
   - Select下拉框
   - 默认值：PERMANENT
   - 选项：
     * ALL - All Staff
     * PERMANENT - Permanent Staff
     * VENDOR - Vendor Staff

2. Month（月份）
   - Input type="month"
   - 默认值：2025-11

3. Claim Type（计费类型）
   - Select下拉框
   - 占位符：All Claim Types
   - 选项：
     * all - All Claim Types
     * OT_WORKING_DAYS - OT - Working Days
     * OT_HOLIDAYS - OT - Holidays
     * OT_STATUTORY_HOLIDAYS - OT - Statutory Holidays

B. 导出按钮
- 功能同上

C. 报表表格（15列）
- 表头字段：
  1. Form ID
  2. Submission Time
  3. Timecard Status - Badge
  4. Final Approval Time
  5. Staff ID
  6. Staff Name (Chinese)
  7. Staff Name
  8. ITD-SZ|SH Team
  9. Timecard Month
  10. Project Code/Charge Code
  11. Adjustment Month
  12. Claim Type - Badge（variant='default'）
  13. Staff Type
  14. ITD-SZ|SH Supervisor
  15. Man-Hours - 右对齐

- 数据行样式：
  * 边框：border-b border-slate-100
  * 单元格：py-3 px-4 text-slate-900 whitespace-nowrap

- Badge样式：
  * Timecard Status:
    - APPROVED: variant='default'
    - 其他: variant='secondary'
  * Claim Type:
    - 统一：variant='default'（强调OT类型）
    - 显示：replace(/_/g, ' ')

D. 筛选逻辑（filteredData）
- 条件：
  * filters.staffType === 'ALL'：显示所有
  * 否则：row.staffType === filters.staffType

E. 合计行
- 样式：bg-slate-50
- 结构：
  * 前14列合并：colSpan={14}
  * 显示：<strong>Total</strong>
  * 第15列：<strong>{总工时}</strong>

- 计算逻辑：
  filteredData.reduce((sum, row) => sum + row.manHours, 0)

F. Mock数据
- 3条示例记录：
  1. TC-2025-11-001：张伟，PROJ001，OT_WORKING_DAYS，8小时
  2. TC-2025-11-002：李娜，PROJ002，OT_HOLIDAYS，12小时
  3. TC-2025-11-003：陈明，PROJ003，OT_WORKING_DAYS，6小时（Vendor）

【字段说明】
- IT Record：Internal Timecard Record（内部工时记录）
- Perm Staff：Permanent Staff（正式员工）
- OT：Overtime（加班）

【设计目的】
专门用于统计正式员工的加班情况，支持加班费核算、加班时长分析、合规性审查等HR场景。

========================================
六、数据层与类型系统
========================================

6.1 /types/index.ts - 类型定义
----------------------------------------

【设计定位】
全局TypeScript类型定义，确保类型安全

【类型清单】

A. ClaimType（计费类型）
- 定义：'NORMAL' | 'LEAVE' | 'OT_WORKING_DAYS' | 'OT_HOLIDAYS' | 'OT_STATUTORY_HOLIDAYS'
- 用途：标记工时记录的性质（正常/请假/加班）

B. ProjectStatus（项目状态）
- 定义：'ACTIVE' | 'COMPLETED'
- 用途：标记项目生命周期状态

C. TimecardStatus（工时卡状态）
- 定义：'DRAFT' | 'SUBMITTED' | 'APPROVAL_PENDING' | 'APPROVED' | 'REJECTED'
- 用途：标记表单审批流程状态

D. Project（项目实体）
- 字段：
  * id: string（唯一标识）
  * projectCode: string（项目代码，业务主键）
  * projectName: string（项目名称）
  * taskNumber: string（任务编号）
  * activity: string（活动）
  * status: ProjectStatus（状态）

E. TimecardEntry（工时记录实体）
- 字段：
  * id: string（唯一标识）
  * date: string（日期，格式：YYYY-MM-DD）
  * projectCode: string（项目代码）
  * projectName: string（项目名称）
  * taskNumber: string（任务编号）
  * activity: string（活动）
  * claimType: ClaimType（计费类型）
  * hours: number（工时数）
  * remark: string（备注）

F. Holiday（节假日实体）
- 字段：
  * id: string（唯一标识）
  * date: string（日期）
  * country: string（国家代码）
  * name: string（节假日名称）
  * type: 'PUBLIC_HOLIDAY' | 'COMPANY_HOLIDAY'（类型）

G. StaffInfo（员工信息实体）
- 字段：
  * staffId: string（员工编号）
  * staffNameChinese: string（中文姓名）
  * staffNameEnglish: string（英文姓名）
  * team: string（团队）
  * staffType: string（员工类型）
  * departmentHead: string（部门主管）
  * finalApproval: string（最终审批人）

H. TimecardSummary（工时汇总实体）
- 字段：
  * projectCode: string
  * projectName: string
  * taskNumber: string
  * activity: string
  * totalHours: number（总工时）

【设计原则】
- 业务实体与技术实体分离
- 枚举类型使用联合类型（Union Types）
- 所有ID使用string类型（便于生成UUID）
- 日期统一使用string格式（YYYY-MM-DD）

----------------------------------------
6.2 /data/mockData.ts - Mock数据源
----------------------------------------

【设计定位】
提供初始化数据和常量配置

【数据清单】

A. mockStaffInfo: StaffInfo
- 当前登录员工：张伟（Wei Zhang）
- 员工编号：EMP001
- 团队：IT Development
- 类型：Permanent
- 主管：John Smith
- 审批人：Sarah Johnson

B. mockProjects: Project[]
- 5个初始项目：
  1. PROJ001 - System Upgrade - T001 - Development - ACTIVE
  2. PROJ002 - Mobile App - T002 - Testing - ACTIVE
  3. PROJ003 - Database Migration - T003 - Analysis - ACTIVE
  4. PROJ004 - UI Redesign - T004 - Design - COMPLETED
  5. PROJ005 - API Integration - T005 - Development - ACTIVE

C. mockHolidays: Holiday[]
- 7个中国节假日（2025年）：
  1. 2025-01-01 - New Year
  2. 2025-01-28~30 - Chinese New Year（3天）
  3. 2025-04-04 - Qingming Festival
  4. 2025-05-01 - Labor Day
  5. 2025-10-01 - National Day

D. mockTimecardEntries: TimecardEntry[]
- 初始值：[]（空数组）
- 用途：占位，实际数据由用户创建

E. CLAIM_TYPE_OPTIONS
- 5个选项（label + value）：
  1. NORMAL - Normal
  2. LEAVE - Leave
  3. OT_WORKING_DAYS - OT - Working Days
  4. OT_HOLIDAYS - OT - Holidays
  5. OT_STATUTORY_HOLIDAYS - OT - Statutory Holidays

F. PROJECT_COLORS
- 8种预设颜色（Tailwind类）：
  1. bg-blue-100 border-blue-300
  2. bg-purple-100 border-purple-300
  3. bg-green-100 border-green-300
  4. bg-yellow-100 border-yellow-300
  5. bg-pink-100 border-pink-300
  6. bg-indigo-100 border-indigo-300
  7. bg-orange-100 border-orange-300
  8. bg-teal-100 border-teal-300
- 用途：日历视图中区分不同项目

【设计意图】
- 提供完整的初始化数据，无需后端即可演示
- CLAIM_TYPE_OPTIONS用于Select组件的选项渲染
- PROJECT_COLORS确保项目颜色的一致性和可识别性

========================================
七、设计系统与样式规范
========================================

7.1 全局样式规范
----------------------------------------

【颜色体系】
- 主色调：slate系列（专业、中性）
  * slate-50: 背景色
  * slate-100: 次级背景
  * slate-200: 边框色
  * slate-600: 次要文字
  * slate-700: 表头文字
  * slate-900: 主要文字

- 状态色：
  * 绿色（green-50/300）：节假日、成功状态
  * 红色（red-50/600）：超时警告、删除操作
  * 黄色（yellow-50/300/600/800）：月度超时警告
  * 蓝色（blue-100/400/600）：源日期标记、主按钮
  * 橙色（orange-300/600）：已有数据标记

【间距规范】
- 组件间距：space-y-6（标准）
- 卡片内间距：p-4 / px-4 py-6
- 表单字段间距：gap-4 / space-y-4
- 按钮组间距：gap-2 / gap-3

【字体规范】
- 标题：不显式设置（使用globals.css默认）
- 正文：text-slate-900（默认）
- 次要文字：text-slate-600
- 小号文字：text-sm / text-xs
- 表头：text-slate-700

【边框规范】
- 卡片边框：border border-slate-200
- 表格边框：
  * 表头：border-b border-slate-200
  * 数据行：border-b border-slate-100

【圆角规范】
- 按钮/输入框：rounded-md（默认）
- 卡片：rounded-lg
- Badge：rounded（小圆角）

【阴影规范】
- 卡片：无显式阴影（依赖shadcn/ui默认）
- 弹窗：无显式阴影（依赖Dialog组件）

【响应式规范】
- 断点：
  * sm: 640px（小屏）
  * md: 768px（中屏）
  * lg: 1024px（大屏）
- 网格布局：
  * grid-cols-1（移动端）
  * md:grid-cols-2（中屏）
  * lg:grid-cols-4（大屏）

【动画交互】
- hover状态：
  * border: hover:border-slate-400
  * background: hover:bg-slate-50 / hover:bg-slate-100
- transition: transition-colors（颜色过渡）
- 按钮显隐：opacity-0 group-hover:opacity-100

7.2 组件规范
----------------------------------------

【Card组件使用】
- 结构：Card > CardHeader + CardContent
- 标题区：CardTitle + CardDescription（可选）
- 内容区：CardContent（默认padding）

【Button组件规范】
- 变体：
  * default：主要操作（提交、保存）
  * outline：次要操作（取消、导出）
  * ghost：图标按钮（编辑、删除）
- 尺寸：
  * default：标准按钮
  * sm：小按钮（表格操作）
  * icon：图标按钮（h-5 w-5 / h-6 w-6）

【Input组件规范】
- 类型：
  * text：默认文本输入
  * number：数字输入（step, min, max）
  * date：日期输入
  * month：月份输入
- 只读：readOnly + bg-slate-50

【Select组件规范】
- 结构：Select > SelectTrigger + SelectContent > SelectItem
- 占位符：<SelectValue placeholder="..." />
- 选项：<SelectItem value="..." />

【Dialog组件规范】
- 结构：Dialog > DialogContent > DialogHeader + content + DialogFooter
- 标题：DialogTitle + DialogDescription
- 尺寸：max-w-md / max-w-2xl / max-w-4xl
- 滚动：max-h-[90vh] overflow-y-auto

【Table组件规范】
- 容器：overflow-x-auto
- 表格：<table className="w-full">
- 表头：border-b border-slate-200
- 数据行：border-b border-slate-100 hover:bg-slate-50
- 单元格：py-3 px-4（标准）/ py-2 px-4（紧凑）
- 空白占位：whitespace-nowrap

【Badge组件规范】
- 变体：
  * default：主要状态（ACTIVE, APPROVED）
  * secondary：次要状态（COMPLETED, DRAFT）
- 内容转换：replace(/_/g, ' ')（下划线转空格）

7.3 图标规范
----------------------------------------

【图标库】lucide-react

【常用图标映射】
- Calendar：日历/工时卡
- FileText：文档/节假日
- Briefcase：项目管理
- BarChart3：报表
- Save：保存草稿
- Send：提交
- FileEdit：上月调整
- Copy：复制
- Trash2：删除
- Plus：添加
- Pencil：编辑
- Search：搜索
- Upload：导入
- Download：导出
- ChevronLeft/Right：左右箭头
- AlertCircle：警告提示
- CalendarIcon：日期选择器

【图标尺寸】
- 标准：w-4 h-4
- 小号：w-3 h-3
- 按钮图标：mr-2（右边距）

========================================
八、交互流程与状态管理
========================================

8.1 核心交互流程
----------------------------------------

【工时录入流程】
1. 用户在CalendarView点击日期
2. 打开ProjectEntryDialog
3. 输入项目代码（手动或搜索）
4. 系统自动填充项目信息
5. 输入工时和备注
6. 可添加多个Entry
7. 点击Save Entries
8. 验证通过后更新CalendarView.entries
9. Toast提示成功

【批量复制流程】
1. 用户在CalendarView点击Copy按钮
2. 打开BulkCopyDialog
3. 显示当月日历，源日期标记为蓝色
4. 用户可选择：
   - 快捷按钮（本周/所有工作日/所有空白日）
   - 手动勾选日期
5. 点击"Copy to X Days"
6. 生成新entries并合并到原有entries
7. Toast提示复制成功
8. 关闭Dialog

【项目管理流程】
1. 用户在ProjectManagement点击Add Project
2. 打开添加Dialog
3. 输入5个字段
4. 点击Create Project
5. 系统验证：
   - 必填字段
   - 项目代码唯一性
6. 验证通过后添加到projects数组
7. Toast提示成功
8. 关闭Dialog

【节假日设置流程】
1. 用户在HolidaySettings点击日期
2. 打开添加Dialog，日期预填充
3. 选择国家和类型
4. 输入节假日名称
5. 点击Add Holiday
6. 系统验证：
   - 必填字段
   - 重复检查
7. 验证通过后添加到holidays数组
8. 日历单元格变为绿色
9. Toast提示成功

8.2 状态管理策略
----------------------------------------

【组件状态】
- 使用useState管理本地状态
- 状态命名规范：
  * 布尔值：show*, is*, has*
  * 数组：*s（复数）
  * 对象：单数名词
  * 临时表单：local*, form*

【状态提升】
- TimecardForm持有entries
- 通过props传递给子组件
- 通过回调函数更新父组件状态

【数据流向】
单向数据流：
Parent Component (state)
  ↓ props
Child Component (local state)
  ↓ callback
Parent Component (setState)

【副作用处理】
- useEffect依赖：[open, entries]
- 清理逻辑：Dialog关闭时重置状态
- Toast提示：操作成功/失败即时反馈

8.3 表单验证规范
----------------------------------------

【必填字段验证】
- 检查：!field || field === ''
- 提示：Toast.error('All fields are required')

【数值范围验证】
- 工时：0-24，步长0.5
- 提示：Hours must be between 0 and 24

【唯一性验证】
- 项目代码：projectCode不重复
- 日期：节假日日期不重复
- 提示：XXX already exists

【日期范围验证】
- 上月调整：必须在上月范围内
- 提示：Date must be in the previous month

【业务规则验证】
- 项目状态：仅ACTIVE项目可选
- 提示：Invalid project code

========================================
九、性能优化与最佳实践
========================================

9.1 性能优化
----------------------------------------

【列表渲染】
- 使用唯一key：entry.id / project.id / `${date}-${idx}`
- 避免index作为key（动态列表）

【条件渲染】
- 使用&&：{condition && <Component />}
- 使用三元：{condition ? <A /> : <B />}

【事件处理】
- 避免内联函数：提取到组件方法
- 使用箭头函数绑定this（类组件）
- 阻止冒泡：e.stopPropagation()

【数据过滤】
- 实时搜索：onChange即时更新
- 双重过滤：搜索 + 状态筛选

【空状态优化】
- 显示占位文本：No XXX found / No entries yet
- 居中显示：text-center py-8

9.2 可访问性（a11y）
----------------------------------------

【语义化HTML】
- 使用<button>而非<div>（可点击元素）
- 使用<label>关联表单字段

【ARIA属性】
- 由shadcn/ui组件自动处理
- 关键交互元素有focus ring

【键盘导航】
- Tab键切换焦点
- Enter键提交表单
- Esc键关闭Dialog

9.3 代码组织
----------------------------------------

【文件结构】
- 组件按功能模块分组：
  * /components/timecard/
  * /components/holiday/
  * /components/project/
  * /components/reports/
- 类型定义集中管理：/types/index.ts
- Mock数据独立文件：/data/mockData.ts

【组件拆分原则】
- 单一职责：每个组件专注一个功能
- 可复用性：Dialog、表单等独立封装
- 合理粒度：避免过度拆分

【命名规范】
- 组件：PascalCase（如TimecardForm）
- 函数：camelCase（如handleSubmit）
- 常量：UPPER_SNAKE_CASE（如CLAIM_TYPE_OPTIONS）
- 类型：PascalCase（如TimecardEntry）

========================================
十、技术栈与依赖
========================================

10.1 核心技术
----------------------------------------

【前端框架】
- React 18+
- TypeScript
- Tailwind CSS 4.0

【UI组件库】
- shadcn/ui（基于Radix UI）
  * 组件列表：Button, Card, Dialog, Input, Select, Table, Badge, Alert, Calendar, Command, Popover, Checkbox, ScrollArea, Tabs, Textarea, Label
- lucide-react（图标库）

【状态管理】
- React Hooks（useState, useEffect）
- 无全局状态管理库（Redux/Zustand）

【通知系统】
- sonner@2.0.3（Toast组件）

10.2 组件依赖关系
----------------------------------------

【依赖树】
App
├── Tabs (shadcn/ui)
├── TimecardForm
│   ├── Card (shadcn/ui)
│   ├── CalendarView
│   │   ├── ProjectEntryDialog
│   │   │   ├── Dialog (shadcn/ui)
│   │   │   ├── Command (shadcn/ui)
│   │   │   └── Popover (shadcn/ui)
│   │   └── BulkCopyDialog
│   │       ├── Dialog (shadcn/ui)
│   │       ├── Checkbox (shadcn/ui)
│   │       └── ScrollArea (shadcn/ui)
│   ├── TimecardSummary
│   │   └── Alert (shadcn/ui)
│   └── PreviousMonthAdjust
│       ├── Dialog (shadcn/ui)
│       ├── Calendar (shadcn/ui)
│       └── Popover (shadcn/ui)
├── HolidaySettings
│   ├── Dialog (shadcn/ui)
│   └── Select (shadcn/ui)
├── ProjectManagement
│   ├── Dialog (shadcn/ui)
│   ├── Badge (shadcn/ui)
│   └── Search (lucide-react)
└── Reports
    ├── Tabs (shadcn/ui)
    ├── IndividualTimecardReport
    ├── TimecardDetailsReport
    ├── TimecardSummaryReport
    └── PermStaffOTReport

========================================
十一、未来扩展建议
========================================

11.1 功能扩展
----------------------------------------

1. 后端集成
   - 替换mockData为API调用
   - 实现真实的CRUD操作
   - 添加用户认证和权限管理

2. 审批流程
   - 多级审批：主管审批 → 最终审批人
   - 审批历史记录
   - 审批意见备注

3. Excel导入导出
   - 项目列表批量导入
   - 节假日批量导入
   - 报表Excel导出（真实实现）

4. 数据可视化
   - 工时趋势图（Recharts）
   - 项目工时分布饼图
   - 加班统计柱状图

5. 高级筛选
   - 报表模块支持多条件组合筛选
   - 保存筛选条件为模板
   - 导出筛选结果

11.2 性能优化
----------------------------------------

1. 虚拟滚动
   - 大数据量表格使用react-window
   - 日历视图年度切换优化

2. 数据缓存
   - 使用React Query缓存API数据
   - 本地存储（LocalStorage）保存草稿

3. 懒加载
   - 路由懒加载（React.lazy）
   - 图片懒加载

11.3 用户体验
----------------------------------------

1. 快捷键支持
   - Ctrl+S保存草稿
   - Ctrl+Enter提交表单

2. 拖拽功能
   - 日历视图拖拽复制工时
   - 表格行拖拽排序

3. 离线支持
   - Service Worker缓存
   - 离线编辑，在线同步

4. 多语言
   - i18n国际化
   - 中英文切换

========================================
文档版本信息
========================================

文档版本：1.0
创建日期：2025年
适用系统：Timecard工时管理系统
技术栈：React 18 + TypeScript + Tailwind CSS 4.0 + shadcn/ui
文档作者：基于代码库分析生成

========================================
附录：快速参考
========================================

【核心页面路由】
- /timecard - 工时卡表单（默认页）
- /holidays - 节假日设置
- /projects - 项目管理
- /reports - 报表系统
  * /reports?tab=summary - 年度汇总
  * /reports?tab=individual - 个人汇总
  * /reports?tab=details - 明细报表
  * /reports?tab=ot - 加班记录

【核心数据类型】
- TimecardEntry：工时记录
- Project：项目信息
- Holiday：节假日
- StaffInfo：员工信息
- TimecardSummary：工时汇总

【关键常量】
- CLAIM_TYPE_OPTIONS：5种计费类型
- PROJECT_COLORS：8种项目颜色
- mockStaffInfo：当前员工信息
- mockProjects：初始项目列表
- mockHolidays：初始节假日列表

【颜色警告规则】
- 绿色（bg-green-50）：节假日
- 红色（bg-red-50）：日工时>8h 或 周末有工时
- 黄色（bg-yellow-50）：月总工时>160h

【验证规则】
- 工时范围：0-24小时，步长0.5
- 项目代码：必须唯一，仅ACTIVE项目可用
- 上月调整：日期必须在上个月范围内
- 必填字段：标记*的字段不能为空

【批量操作逻辑】
- 选择本周：周一至周五（排除周末和源日期）
- 选择所有工作日：非周末 + 非节假日
- 选择所有空白日：无工时记录 + 非周末

========================================
文档结束
========================================
