# Data Model: Timecard Module

**Feature Branch**: `002-functional-specification-md`  
**Created**: 2025-01-22  
**Status**: Draft  
**Related Documents**: [spec.md](./spec.md), [plan.md](./plan.md)

## Overview

本文档定义了 Timecard 模块的完整数据模型，包括数据库表结构、JSON 数据结构、实体关系和数据字典。Timecard 数据主要存储在 `spms_process_data` 表的 `form_data` 字段中（JSONB 格式），同时依赖现有的组织架构、项目和任务表。

## Database Tables

### Existing Tables (Reused)

#### 1. spms_process_data

Timecard 数据的主存储表，使用 `form_type='TIMECARD'` 标识。

| Column Name | Data Type | Nullable | Default | Key | Index | Description |
|------------|-----------|----------|---------|-----|-------|-------------|
| id | BIGSERIAL | No | - | PK | Yes | 流程数据唯一标识符 |
| process_instance_id | VARCHAR(255) | Yes | NULL | - | Yes | Flowable 流程实例 ID |
| user_id | BIGINT | No | - | FK | Yes | 提交用户 ID (关联 spms_user.id) |
| process_definition_key | VARCHAR(255) | No | - | - | Yes | 流程定义键 |
| form_type | VARCHAR(100) | No | 'TIMECARD' | - | Yes | 表单类型，Timecard 固定为 'TIMECARD' |
| form_data | JSONB | Yes | NULL | - | No | Timecard JSON 数据（见下方结构） |
| business_data | JSONB | Yes | NULL | - | No | 业务数据存储（可选） |
| status | VARCHAR(50) | No | 'DRAFT' | - | Yes | 状态：DRAFT, SUBMITTED, APPROVED, REJECTED |
| created_at | BIGINT | No | - | - | Yes | 创建时间（Unix 时间戳，毫秒） |
| updated_at | BIGINT | No | - | - | Yes | 更新时间（Unix 时间戳，毫秒） |
| created_by | VARCHAR(255) | No | - | - | No | 创建人 |
| updated_by | VARCHAR(255) | Yes | NULL | - | No | 更新人 |

**Indexes:**
- Primary Key: `pk_spms_process_data` on `id`
- Index: `idx_spms_process_data_user_id` on `user_id`
- Index: `idx_spms_process_data_process_instance_id` on `process_instance_id`
- Index: `idx_spms_process_data_form_type` on `form_type`
- Index: `idx_spms_process_data_status` on `status`
- Composite Index: `idx_spms_process_data_user_form_status` on `(user_id, form_type, status)`

**Constraints:**
- Foreign Key: `user_id` → `spms_user.id`
- Check: `status IN ('DRAFT', 'SUBMITTED', 'APPROVED', 'REJECTED')`
- Check: `form_type = 'TIMECARD'` for timecard records

#### 2. spms_user

员工信息表，用于获取员工基本信息和组织关系。

| Column Name | Data Type | Nullable | Description |
|------------|-----------|----------|-------------|
| id | BIGINT | No | 员工 ID（主键） |
| username | VARCHAR(255) | No | 用户名 |
| email | VARCHAR(255) | Yes | 邮箱 |
| user_profiles | JSONB | Yes | 用户配置文件，包含：<br>- `staff_name_chinese`: 中文姓名<br>- `staff_name_english`: 英文姓名<br>- `staff_type`: 员工类型 (PERMANENT/VENDOR) |

**Usage in Timecard:**
- `id` 作为 `spms_process_data.user_id` 的外键
- `user_profiles` 用于自动填充员工信息

#### 3. spms_department

组织架构表，用于审批路由和经理查找。

| Column Name | Data Type | Nullable | Description |
|------------|-----------|----------|-------------|
| id | BIGINT | No | 部门 ID（主键） |
| department_head_id | BIGINT | Yes | 部门负责人 ID |
| local_manager_id | BIGINT | Yes | 本地经理 ID（通过 tags 存储） |
| functional_manager_id | BIGINT | Yes | 职能经理 ID（通过 tags 存储） |

**Usage in Timecard:**
- 用于确定审批路由路径
- 通过 `local_manager_id` 和 `functional_manager_id` 查找审批人

#### 4. spms_project

项目定义表，用于项目代码验证和项目信息获取。

| Column Name | Data Type | Nullable | Default | Description |
|------------|-----------|----------|---------|-------------|
| id | BIGSERIAL | No | - | 项目 ID（主键） |
| project_code | VARCHAR(50) | No | - | 项目代码（唯一） |
| project_name | VARCHAR(200) | No | - | 项目名称 |
| status | VARCHAR(50) | No | 'ACTIVE' | 项目状态：ACTIVE, COMPLETED |
| is_active | BOOLEAN | No | true | 激活状态 |

**Usage in Timecard:**
- 仅 `status='ACTIVE'` 且 `is_active=true` 的项目可用于 Timecard 填报
- `project_code` 用于验证和自动填充

#### 5. spms_task

任务定义表，用于任务信息获取。

| Column Name | Data Type | Nullable | Description |
|------------|-----------|----------|-------------|
| id | BIGSERIAL | No | 任务 ID（主键） |
| task_number | VARCHAR(50) | No | 任务编号（唯一） |
| task_name | VARCHAR(200) | No | 任务名称 |
| project_id | BIGINT | No | 所属项目 ID（外键） |
| activity | VARCHAR(255) | Yes | 活动描述 |

**Usage in Timecard:**
- 通过 `project_code` 查找关联的任务
- `activity` 字段用于自动填充活动类型

#### 6. system_config

系统配置表，用于存储节假日和截止日期配置。

| Column Name | Data Type | Nullable | Description |
|------------|-----------|----------|-------------|
| id | BIGSERIAL | No | 配置 ID（主键） |
| key | VARCHAR(255) | No | 配置键，格式：<br>- `timecard.deadline.{YYYY-MM}`: 截止日期<br>- `holiday.{YYYY-MM}`: 节假日配置 |
| value | JSONB | Yes | 配置值（JSON 格式） |
| category | VARCHAR(100) | No | 配置类别：HOLIDAY, TIMECARD |

**Usage in Timecard:**
- 存储每月 Timecard 提交截止日期
- 存储节假日配置（可选，也可存储在 `spms_holiday` 表）

### New Tables (To Be Created)

#### 7. spms_holiday

节假日表，用于存储节假日信息。

| Column Name | Data Type | Nullable | Default | Key | Index | Description |
|------------|-----------|----------|---------|-----|-------|-------------|
| id | BIGSERIAL | No | - | PK | Yes | 节假日唯一标识符 |
| date | DATE | No | - | - | Yes | 节假日日期 |
| name | VARCHAR(200) | No | - | - | No | 节假日名称 |
| country | VARCHAR(10) | Yes | NULL | - | No | 国家代码（可选，支持多国家） |
| type | VARCHAR(50) | Yes | 'PUBLIC_HOLIDAY' | - | No | 类型：PUBLIC_HOLIDAY, COMPANY_HOLIDAY |
| created_at | BIGINT | No | - | - | Yes | 创建时间（Unix 时间戳，毫秒） |
| updated_at | BIGINT | No | - | - | Yes | 更新时间（Unix 时间戳，毫秒） |
| created_by | VARCHAR(255) | No | - | - | No | 创建人 |
| updated_by | VARCHAR(255) | Yes | NULL | - | No | 更新人 |

**Indexes:**
- Primary Key: `pk_spms_holiday` on `id`
- Unique Index: `uk_spms_holiday_date_country` on `(date, country)` (允许同一天不同国家的节假日)
- Index: `idx_spms_holiday_date` on `date` (用于按日期查询)

**Constraints:**
- Unique: `(date, country)` 组合唯一

## JSON Data Structure

### Timecard form_data Structure

Timecard 数据以 JSON 格式存储在 `spms_process_data.form_data` 字段中。

```json
{
  "employee_info": {
    "staff_id": "EMP001",
    "staff_name_chinese": "张伟",
    "staff_name_english": "Wei Zhang",
    "team": "IT Development",
    "department": "Technology",
    "department_head": "John Smith",
    "local_manager": "Jane Doe",
    "functional_manager": "Bob Wilson",
    "staff_type": "PERMANENT",
    "timecard_month": "2025-01"
  },
  "timecard_entries": [
    {
      "id": "2025-01-15-0-1705123456789",
      "date": "2025-01-15",
      "project_code": "PROJ001",
      "project_name": "Project Alpha",
      "task_number": "T001",
      "task_name": "Task 1",
      "activity": "开发",
      "claim_type": "NORMAL",
      "hours": 8.0,
      "remark": "日常开发工作"
    },
    {
      "id": "2025-01-15-1-1705123456790",
      "date": "2025-01-15",
      "project_code": "PROJ002",
      "project_name": "Project Beta",
      "task_number": "T002",
      "task_name": "Task 2",
      "activity": "测试",
      "claim_type": "OVERTIME",
      "hours": 2.0,
      "remark": "加班测试"
    }
  ],
  "summary": {
    "total_hours": 160.0,
    "total_entries": 20,
    "grouped_by_project_task_activity": [
      {
        "project_code": "PROJ001",
        "project_name": "Project Alpha",
        "task_number": "T001",
        "task_name": "Task 1",
        "activity": "开发",
        "total_hours": 120.0
      },
      {
        "project_code": "PROJ002",
        "project_name": "Project Beta",
        "task_number": "T002",
        "task_name": "Task 2",
        "activity": "测试",
        "total_hours": 40.0
      }
    ],
    "warnings": [
      "EXCEED_DAILY_LIMIT",
      "EXCEED_MONTHLY_LIMIT"
    ]
  },
  "version": 1,
  "created_by": "EMP001",
  "created_at": 1705123456789,
  "updated_by": "EMP001",
  "updated_at": 1705123456789,
  "submitted": false
}
```

### Field Definitions

#### employee_info

员工信息对象，包含员工基本信息和组织关系。

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| staff_id | string | Yes | 员工 ID |
| staff_name_chinese | string | Yes | 中文姓名 |
| staff_name_english | string | Yes | 英文姓名 |
| team | string | Yes | 团队名称 |
| department | string | Yes | 部门名称 |
| department_head | string | Yes | 部门负责人姓名 |
| local_manager | string | No | 本地经理姓名 |
| functional_manager | string | No | 职能经理姓名 |
| staff_type | string | Yes | 员工类型：PERMANENT, VENDOR |
| timecard_month | string | Yes | 时间卡月份，格式：YYYY-MM |

#### timecard_entries

时间卡条目数组，每个条目代表一天的一个项目工时记录。

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| id | string | Yes | 唯一标识符，格式：`{date}-{index}-{timestamp}` |
| date | string | Yes | 日期，格式：YYYY-MM-DD |
| project_code | string | Yes | 项目代码 |
| project_name | string | Yes | 项目名称（自动填充） |
| task_number | string | Yes | 任务编号 |
| task_name | string | Yes | 任务名称（自动填充） |
| activity | string | Yes | 活动描述（自动填充） |
| claim_type | string | Yes | 申请类型：NORMAL, OVERTIME, WEEKEND, HOLIDAY |
| hours | number | Yes | 工时数，范围：0-24，精度：0.5 |
| remark | string | No | 备注 |

#### summary

汇总信息对象，包含总计和分组统计。

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| total_hours | number | Yes | 总工时数 |
| total_entries | number | Yes | 总条目数 |
| grouped_by_project_task_activity | array | Yes | 按项目+任务+活动分组的汇总数组 |
| warnings | array | No | 警告标志数组：<br>- `EXCEED_DAILY_LIMIT`: 单日超过 8 小时<br>- `EXCEED_MONTHLY_LIMIT`: 月度超过 160 小时 |

#### grouped_by_project_task_activity Item

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| project_code | string | Yes | 项目代码 |
| project_name | string | Yes | 项目名称 |
| task_number | string | Yes | 任务编号 |
| task_name | string | Yes | 任务名称 |
| activity | string | Yes | 活动描述 |
| total_hours | number | Yes | 该组合的总工时数 |

## Data Dictionary

### Enumerated Types

#### ClaimType (申请类型)

| Value | Description | Usage |
|-------|-------------|-------|
| NORMAL | 正常工作 | 工作日正常工时 |
| OVERTIME | 加班 | 工作日超过 8 小时或非工作日 |
| WEEKEND | 周末 | 周末工作 |
| HOLIDAY | 节假日 | 节假日工作 |

#### ProjectStatus (项目状态)

| Value | Description | Usage |
|-------|-------------|-------|
| ACTIVE | 活跃 | 项目进行中，可用于 Timecard |
| COMPLETED | 已完成 | 项目已完成，不可用于新 Timecard |

#### TimecardStatus (Timecard 状态)

| Value | Description | Usage |
|-------|-------------|-------|
| DRAFT | 草稿 | 未提交的 Timecard |
| SUBMITTED | 已提交 | 已提交等待审批 |
| APPROVED | 已批准 | 审批通过 |
| REJECTED | 已拒绝 | 审批被拒绝 |

#### StaffType (员工类型)

| Value | Description | Usage |
|-------|-------------|-------|
| PERMANENT | 永久员工 | 正式员工 |
| VENDOR | 供应商员工 | 外包员工 |

#### HolidayType (节假日类型)

| Value | Description | Usage |
|-------|-------------|-------|
| PUBLIC_HOLIDAY | 公共假日 | 法定节假日 |
| COMPANY_HOLIDAY | 公司假日 | 公司内部假日 |

## Entity Relationships

### Entity Relationship Diagram

```
┌─────────────────┐
│  spms_user      │
│  (Employee)     │
└────────┬────────┘
         │
         │ 1:N
         │
┌────────▼─────────────────┐
│  spms_process_data       │
│  (Timecard)              │
│  - form_type='TIMECARD'  │
│  - form_data (JSONB)     │
└────────┬─────────────────┘
         │
         │ N:1
         │
┌────────▼────────┐
│ Flowable Process│
│ Instance         │
└──────────────────┘

┌─────────────────┐
│  spms_project   │
│  (Project)      │
└────────┬────────┘
         │
         │ 1:N
         │
┌────────▼────────┐
│  spms_task      │
│  (Task)         │
└─────────────────┘

┌─────────────────┐
│  spms_department│
│  (Organization)│
└─────────────────┘

┌─────────────────┐
│  spms_holiday   │
│  (Holiday)      │
└─────────────────┘
```

### Relationship Details

1. **spms_user → spms_process_data** (1:N)
   - 一个员工可以提交多个 Timecard
   - Foreign Key: `spms_process_data.user_id` → `spms_user.id`

2. **spms_process_data → Flowable Process Instance** (N:1)
   - 每个 Timecard 关联一个 Flowable 流程实例
   - Foreign Key: `spms_process_data.process_instance_id` → Flowable process instance

3. **spms_project → spms_task** (1:N)
   - 一个项目包含多个任务
   - Foreign Key: `spms_task.project_id` → `spms_project.id`

4. **spms_user → spms_department** (N:1)
   - 员工属于部门，部门包含经理信息
   - 用于审批路由

5. **Timecard Entry → Project/Task** (Reference)
   - Timecard 条目通过 `project_code` 和 `task_number` 引用项目和任务
   - 非外键关系，通过业务键关联

## Data Flow

### 1. Timecard Submission Flow

```
Frontend (TimecardForm)
    ↓ POST /api/v1/timecard/submit
TimecardController
    ↓
TimecardService
    ├─→ ProcessService → Flowable Engine (create process instance)
    └─→ ProcessDataRepository → Save to spms_process_data
```

**Data Transformation:**
- Frontend TimecardForm data → TimecardSubmitRequest DTO
- TimecardSubmitRequest → Timecard JSON structure
- Timecard JSON → `spms_process_data.form_data` (JSONB)

### 2. Timecard Retrieval Flow

```
Frontend (TimecardForm)
    ↓ GET /api/v1/process/process-instances/{id}/form-data
ProcessService
    ├─→ Flowable Engine (get process instance)
    └─→ ProcessDataRepository → Query spms_process_data
    ↓
Return Timecard JSON → Frontend renders
```

**Fallback:**
- If no Flowable data exists:
  - Frontend → `GET /api/v1/timecard/employee-info`
  - UserService → Query `spms_user` and `spms_department`
  - Return employee info for auto-population

### 3. Project Validation Flow

```
Frontend (ProjectEntryDialog)
    ↓ User enters project_code
    ↓ GET /api/v1/timecard/projects/validate/{projectCode}
TimecardController
    ↓
TimecardService
    ├─→ ProjectRepository → Query spms_project (by project_code)
    └─→ TaskRepository → Query spms_task (by project_id)
    ↓
Return ProjectValidationDTO → Frontend auto-fills task/activity
```

### 4. Approval Routing Flow

```
Flowable Engine (on timecard submission)
    ↓
ProcessService
    ↓
DepartmentService
    ├─→ Query spms_department (get local_manager_id, functional_manager_id)
    └─→ Flowable Engine (assign tasks to managers)
```

### 5. Report Generation Flow

```
Frontend (ReportPage)
    ↓ GET /api/v1/report/{type}?filters...
ReportController
    ↓
ReportService
    ├─→ ProcessDataRepository → Query spms_process_data (with filters)
    ├─→ Join spms_user (for employee info)
    ├─→ Join spms_department (for department info)
    └─→ Join spms_project (for project info)
    ↓
Generate Report Data → Return JSON or Excel
```

## Data Validation Rules

### Timecard Entry Validation

1. **Date Validation:**
   - Date must be within the timecard month
   - Date cannot be in the future
   - Date format: YYYY-MM-DD

2. **Hours Validation:**
   - Hours must be between 0 and 24
   - Hours precision: 0.5 (e.g., 0.5, 1.0, 1.5, 8.0)
   - Daily total hours cannot exceed 24

3. **Project Validation:**
   - Project code must exist in `spms_project`
   - Project status must be ACTIVE
   - Task number must exist for the project

4. **Monthly Validation:**
   - Total monthly hours cannot exceed 160 (warning threshold)
   - Submission deadline check (from `system_config`)

### Business Rules

1. **Resubmission:**
   - Latest version takes precedence
   - Previous versions remain in database for audit

2. **Approval Routing:**
   - Based on organizational hierarchy
   - Local manager → Functional manager → Department head

3. **Holiday Highlighting:**
   - Dates in `spms_holiday` are highlighted in green
   - Holidays are read-only (cannot enter timecard)

## Indexes and Performance

### Recommended Indexes

1. **spms_process_data:**
   - `idx_spms_process_data_user_form_month` on `(user_id, form_type, (form_data->>'timecard_month'))` - For monthly queries
   - `idx_spms_process_data_status_created` on `(status, created_at)` - For approval task queries

2. **spms_holiday:**
   - `idx_spms_holiday_date_range` on `(date)` - For calendar queries

3. **spms_project:**
   - `idx_spms_project_code_status` on `(project_code, status)` - For active project queries

### Query Optimization

- Use JSONB operators (`->`, `->>`, `@>`) for efficient JSON queries
- Use GIN indexes on JSONB columns for complex queries
- Cache active projects list (rarely changes)
- Cache holiday data (changes infrequently)

## Data Migration Considerations

### From Legacy System (if applicable)

1. **Timecard Data Migration:**
   - Map legacy timecard records to new JSON structure
   - Create Flowable process instances for existing approvals
   - Preserve historical data in `spms_process_data`

2. **Project/Task Migration:**
   - Ensure project codes are unique
   - Map legacy task numbers to new structure

3. **Holiday Data Migration:**
   - Import holidays from legacy system to `spms_holiday`
   - Or migrate to `system_config` if preferred

## Version History

| Version | Date | Changes | Author |
|---------|------|---------|--------|
| 1.0.0 | 2025-01-22 | Initial data model specification | - |

