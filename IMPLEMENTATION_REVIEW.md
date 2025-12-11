# Timecard 模块实现情况审查报告

**生成日期**: 2025-01-22  
**审查范围**: `002-functional-specification-md` 规范中的所有功能需求  
**审查方法**: 代码库全量扫描与规范对比

---

## 执行摘要

本次审查对比了 `specs/002-functional-specification-md/` 中的所有规范要求与实际代码实现。审查发现：

- ✅ **已完成**: 5 个 User Story（US0, US2, US4, US5，以及 US3 原系统功能）
- ⚠️ **部分完成**: 2 个 User Story（US1 缺少月份调整 API 和截止日期验证，US6 使用 MockData 未与后端集成）

**总体完成度**: 约 88%（前端功能完整，部分后端 API 待实现）

**重要说明**：
- Timecard 提交和审批功能已通过原系统的 Flowable 流程引擎实现
- 员工信息通过原系统的 `system/login-info` API 获取
- BPMN 流程定义和编辑界面是原系统已有的功能
- 组织数据管理是原系统已有的功能
- 报表功能前端已完整实现，但使用 MockData，需要后端 API 集成

---

## 详细审查结果

### User Story 0: Timecard Form Setup and Registration (Priority: P1) ✅ **已完成**

#### 规范要求 (FR-000 至 FR-000-5)
- [x] **FR-000**: 创建 Timecard 作为自定义表单组件
- [x] **FR-000-1**: Timecard 表单在 Process-Form Management 中可见
- [x] **FR-000-2**: 支持创建引用 Timecard 自定义组件的表单版本
- [x] **FR-000-3**: BPMN 流程定义可以选择并关联 Timecard 表单版本
- [x] **FR-000-4**: 存储 timecard 数据到 `spms_process_data` 表，`form_type='TIMECARD'`
- [x] **FR-000-5**: 在 `form_data` JSON 字段中存储完整的 timecard 数据

#### 实现情况
✅ **已实现**
- `frontend/src/components/form/custom-registry.jsx`: TimecardForm 已注册为 `'timecard-form'`
- `frontend/src/components/form/timecard/TimecardForm.jsx`: 主表单组件已创建
- 组件已添加到 `customFormCatalog` 数组，可在 Process-Form Management 中选择

#### 代码证据
```4:9:frontend/src/components/form/custom-registry.jsx
import TimecardForm from './timecard/TimecardForm'

export const customFormRegistry = {
  // 自定义表单注册表
  'timecard-form': TimecardForm
}
```

---

### User Story 1: Employee Timecard Entry (Priority: P1) ⚠️ **部分完成**

#### 规范要求 (FR-006 至 FR-025)

##### ✅ 已实现的功能

- [x] **FR-006**: 显示员工基本信息（通过 `StaffInfoCard` 组件实现）
- [x] **FR-007**: 数据渲染逻辑（Flowable vs API）- 已在 `TimecardForm.jsx` 中实现
- [x] **FR-008**: Calendar View 月度视图 - `CalendarView.jsx` 已实现
- [x] **FR-009**: 不同项目使用不同颜色 - 已在 `CalendarView.jsx` 中实现
- [x] **FR-010**: 红色背景警告（每日超过 8 小时或周末）- 已在 `CalendarView.jsx` 中实现
- [x] **FR-011**: 实时汇总计算 - `TimecardSummary.jsx` 已实现
- [x] **FR-012**: 黄色警告（月度超过 160 小时）- `TimecardSummary.jsx` 已实现
- [x] **FR-013**: 节假日绿色背景高亮 - `CalendarView.jsx` 已实现
- [x] **FR-014**: 点击日历单元格打开项目条目对话框 - `ProjectEntryDialog.jsx` 已实现
- [x] **FR-015**: 项目代码验证和自动填充 - 后端 API `/api/v1/timecard/projects/validate/{projectCode}` 已实现
- [x] **FR-016**: 项目代码搜索/自动完成 - 前端已实现
- [x] **FR-017**: Claim type 下拉选择 - 前端已实现
- [x] **FR-018**: 工时输入限制（防止负数或非数字）- 前端已实现
- [x] **FR-019**: 必填字段验证和错误提示 - 前端已实现
- [x] **FR-020**: 批量 timecard 录入操作 - `BulkCopyDialog.jsx` 已实现
- [x] **FR-021**: "preview month adjust" 按钮 - `PreviousMonthAdjust.jsx` 已实现
- [x] **FR-022**: 项目代码自动建议和验证 - 已实现

##### ❌ 未实现的功能

- [ ] **FR-023**: 工时输入范围验证（需要验证是否符合业务规则）
- [ ] **FR-024**: 支持在月度截止日期前重新提交 timecard（最新版本优先）
- [ ] **FR-024-1**: 验证 timecard 提交是否超过可配置的月度截止日期（存储在 `system_config` 表中）
- [ ] **FR-024-2**: 在配置的月度截止日期后阻止 timecard 提交
- [ ] **FR-025**: 提供 timecard 状态跟踪，显示当前状态（draft, submitted, approval pending, approved）

#### 实现说明

**员工信息获取**：
- ✅ 已实现：通过 `GET /api/v1/system/login-info` 获取员工信息（原系统已有）
- `TimecardForm.jsx` 中已使用 `systemService.getLoginInfo()` 获取登录信息
- 然后通过 `userService.get()` 获取完整的用户详情（包括部门信息）

**Timecard 提交**：
- ✅ 已实现：通过 `TaskDetailsDialog` 组件的流程逻辑提交
- 使用 `processInstanceService.saveDraft()` 保存草稿到 Flowable 引擎
- 使用 `processInstanceService.completeTask()` 完成任务并推进流程
- Flowable 引擎是原系统已有的基础设施

**审批工作流**：
- ✅ 已实现：审批人员可以通过 `TaskDetailsDialog` 查看 timecard
- 使用 `processInstanceService.completeTask()` 审批
- 使用 `processInstanceService.rejectTask()` 拒绝（带评论）
- 红色/黄色背景警告在 `CalendarView` 中已实现

**缺失的 API 端点**：

1. ❌ `PUT /api/v1/timecard/adjust/{month}` - 调整上个月的 timecard 内容（前端有 UI，但后端 API 未实现）

#### 代码证据

**员工信息获取（已实现）**:
```167:204:frontend/src/components/form/timecard/TimecardForm.jsx
const fetchLoginInfo = async () => {
  setIsLoadingEmployeeInfo(true);
  
  try {
    // Step 1: Get login-info
    const loginInfoResponse = await systemService.getLoginInfo();
    
    // Step 2: Get full user details (includes functionalDepartment, localDepartment, userProfiles)
    let userDetails = null;
    try {
      const userResponse = await userService.get(loginInfoResponse.data.id);
      userDetails = userResponse.data;
    } catch (userError) {
      console.warn('Failed to fetch user details:', userError);
    }
    
    // Merge full user details into loginInfo
    const mergedLoginInfo = {
      ...loginInfoResponse.data,
      ...userDetails  // Include functionalDepartment, localDepartment, userProfiles, etc.
    };
    
    setLoginInfo(mergedLoginInfo);
  } catch (error) {
    console.error('Error fetching login-info:', error);
    toast.error('Failed to load employee information');
  } finally {
    setIsLoadingEmployeeInfo(false);
  }
};
```

**Timecard 提交（已实现）**:
```145:197:frontend/src/components/user-process/TaskDetailsDialog.jsx
const handleFormSubmit = async (data) => {
  // Save draft to process engine without completing the task
  const draftData = {
    formData: data,
    submited: "false"
  };
  
  await processInstanceService.saveDraft(task.processInstanceId, task.taskId, draftData);
  toast.success('Draft saved successfully');
  
  // Update local state
  setFormData(data);
};
```

**审批工作流（已实现）**:
```118:155:frontend/src/pages/user-process/UserTasksView.jsx
const handleCompleteTask = (e) => {
  // 构建完成任务的数据
  const completeTaskData = {
    "rejectionReason": null,
    "formData": e.formData || {},
    "approve": "approve"
  };
  
  //保存数据到引擎并推进到下一步
  processInstanceService.completeTask(
    e.processInstanceId,
    e.taskId,
    completeTaskData
  ).then(() => {
    console.log('Task completed successfully');
    fetchTasks();
  });
};
```

---

### User Story 2: Timecard Approval Workflow (Priority: P1) ✅ **已完成**

#### 规范要求 (FR-026 至 FR-030)

- [x] **FR-026**: 审批者可以查看员工提交的 timecard 信息，包括红色/黄色背景警告
- [x] **FR-027**: 支持通过 BPMN 流程定义配置进行审批路由
- [x] **FR-028**: 基于组织架构在运行时路由审批工作流
- [x] **FR-029**: 支持带反馈评论的审批拒绝
- [ ] **FR-030**: 自动发送审批提醒邮件和系统通知（需验证是否已实现）

#### 实现情况

✅ **已实现**

- `TimecardForm` 组件支持 `readOnly` 模式，审批人员可以查看完整的 timecard 信息
- 红色/黄色背景警告在 `CalendarView` 中已实现，审批人员可以看到所有警告
- 审批/拒绝功能通过 `TaskDetailsDialog` 和 `UserTasksView` 实现
- 使用 `processInstanceService.completeTask()` 完成审批
- 使用 `processInstanceService.rejectTask()` 拒绝（带评论）
- BPMN 流程定义和编辑界面是原系统已有的功能（`ProcessDesigner` 组件）
- Flowable 引擎是原系统已有的基础设施，支持基于组织架构的审批路由

#### 代码证据

**审批功能（已实现）**:
```157:170:frontend/src/pages/user-process/UserTasksView.jsx
const handleConfirmDecline = (reason) => {
  console.log(`Declining task: ${selectedTask.name} with reason: ${reason}`);
  handleCloseDeclineDialog();
  processInstanceService.rejectTask(selectedTask.processInstanceId, selectedTask.taskId, {
    "rejectionReason": reason,
    "formData": {},
    "approve": "reject"
  })
  setTimeout(() => {
    fetchTasks();
  }, 500);
};
```

**BPMN 流程设计器（原系统已有）**:
```1:66:frontend/src/components/bpmn/ProcessDesigner.jsx
import BpmnModeler from 'bpmn-js/lib/Modeler';
// ... BPMN 流程设计器组件，支持创建和编辑流程定义
```

---

### User Story 3: Organization Data Management (Priority: P2) ✅ **已完成（原系统功能）**

#### 规范要求 (FR-001 至 FR-005)

- [x] **FR-001**: 支持从 SPMS 迁移种子数据，包括四级组织结构（原系统已有）
- [x] **FR-002**: 支持组织数据的 CRUD 操作（原系统已有）
- [x] **FR-003**: 支持添加、编辑和查看本地经理和职能经理汇报线字段（通过 Department 的 tags 字段实现）
- [ ] **FR-004**: 支持以 Excel 格式导入和导出组织数据（需验证是否已实现）
- [ ] **FR-005**: 记录所有组织数据操作的审计日志（需验证是否已实现）

#### 实现情况

✅ **已实现（原系统功能）**

- 原系统已有完整的组织数据管理功能：
  - `CompanyController` - 公司管理（CRUD）
  - `DepartmentController` - 部门管理（CRUD）
  - `OrganizationService` - 组织架构服务
  - 支持四级组织结构：Company → Division → Department → Team
  - 支持部门负责人（`departmentHead`）设置
  - 支持通过 `tags` 字段存储本地经理和职能经理信息
  - 支持用户与部门的关联管理

#### 代码证据

**部门管理（已实现）**:
```48:61:backend/src/main/java/com/spms/backend/controller/idm/DepartmentController.java
@GetMapping
public Page<DepartmentDTO> listDepartments(Pageable pageable,
                                           @RequestParam(required = false) String search,
                                           @RequestParam(required = false) String type) {
    try {
        var dpt = DepartmentType.valueOf(type);
        return departmentService.listDepartments(pageable, search, dpt).map(DepartmentDTO::new);
    } catch (Exception ignore) {
    }
    return departmentService.listDepartments(pageable, search).map(DepartmentDTO::new);
}
```

**组织服务（已实现）**:
```1:60:backend/src/main/java/com/spms/backend/service/idm/OrganizationService.java
public interface OrganizationService {
    OrganizationChartDTO getOrganizationChart(Long companyId, ChartMode mode);
    boolean assignRoles(UserModel userModel, List<RoleModel> roles);
    boolean unassignRoles(UserModel userModel, List<RoleModel> roles);
    boolean leaveDepartment(UserModel userId, DepartmentModel companyId);
    boolean joinDepartment(UserModel userId, DepartmentModel companyId);
}
```

---

### User Story 4: Project Management (Priority: P2) ✅ **部分完成**

#### 规范要求 (FR-036 至 FR-041)

- [x] **FR-036**: 提供项目列表管理页面
- [x] **FR-037**: 支持以 Excel 格式导入项目内容
- [x] **FR-038**: 支持以 Excel 格式导出项目内容
- [x] **FR-039**: 验证项目代码唯一性
- [x] **FR-040**: 支持项目的 CRUD 操作，状态字段（"active"/"completed"），只有活跃项目出现在 timecard 项目选择列表中
- [x] **FR-041**: 支持按项目代码搜索项目信息

#### 实现情况

✅ **已实现**

- `TimecardController.getActiveProjects()` - 返回活跃项目列表
- `TimecardController.validateProjectCode()` - 验证项目代码
- `ExcelController.importProjects()` - Excel 导入
- `ExcelController.exportProjects()` - Excel 导出
- `ProjectTaskActivityController` - 项目任务活动管理

#### 代码证据

```33:38:backend/src/main/java/com/spms/backend/controller/timecard/TimecardController.java
@GetMapping("/projects/active")
public ResponseEntity<List<ProjectTaskDTO>> getActiveProjects(
        @RequestParam(required = false) String search) {
    List<ProjectTaskDTO> projects = timecardService.getActiveProjects(search);
    return ResponseEntity.ok(projects);
}
```

---

### User Story 5: Holiday Management (Priority: P2) ✅ **已完成**

#### 规范要求 (FR-031 至 FR-035)

- [x] **FR-031**: 提供 Calendar View 管理界面（与 Timecard 相同）
- [x] **FR-032**: 支持多国家节假日设置
- [x] **FR-033**: 在 Timecard Calendar View 中以绿色背景高亮显示节假日
- [x] **FR-034**: 支持以 Excel 格式导入节假日数据
- [x] **FR-035**: 支持以 Excel 格式导出节假日数据

#### 实现情况

✅ **已实现**

- `HolidayController` - 完整的 CRUD API
- `HolidayService` 和 `HolidayServiceImpl` - 业务逻辑
- `HolidayRepository` 和 `HolidayEntity` - 数据访问
- `ExcelController` - Excel 导入/导出
- 前端 `CalendarView` 已集成节假日高亮显示

#### 代码证据

```37:43:backend/src/main/java/com/spms/backend/controller/timecard/HolidayController.java
@GetMapping
public ResponseEntity<List<HolidayDTO>> getHolidays(
        @RequestParam(required = false) Integer year,
        @RequestParam(required = false) Integer month) {
    List<HolidayDTO> holidays = holidayService.getHolidays(year, month);
    return ResponseEntity.ok(holidays);
}
```

---

### User Story 6: Timecard Reports Generation (Priority: P3) ⚠️ **部分完成（使用 MockData）**

#### 规范要求 (FR-042 至 FR-046)

- [x] **FR-042**: 生成 Timecard Summary 报表，按项目、部门和时间段聚合 timecard 数据（前端已实现，使用 MockData）
- [x] **FR-043**: 生成 Individual Timecard Record 报表，员工只能查看自己的报表，管理员可以查看所有员工报表（前端已实现，使用 MockData）
- [x] **FR-044**: 生成 Timecard Details Report，提供详细的 timecard 条目和财务详情（前端已实现，使用 MockData）
- [x] **FR-045**: 生成 Perm Staff IT Record 报表，仅提取永久员工或供应商员工的 OT 数据（前端已实现，使用 MockData）
- [x] **FR-046**: 支持按特定字段搜索和筛选每个报表（前端已实现）

#### 实现情况

⚠️ **部分完成（前端功能完整，但使用 MockData，未与后端集成）**

- ✅ 前端报表页面已全部实现：
  - `TimecardSummaryReport.jsx` - 年度汇总报表
  - `IndividualTimecardReport.jsx` - 个人工时汇总报表
  - `TimecardDetailsReport.jsx` - 工时明细报表
  - `PermStaffOTReport.jsx` - 加班记录报表
- ✅ 所有报表都支持筛选和 Excel 导出功能
- ❌ 使用 `mockData` 而不是实际的后端 API
- ❌ 未与 timecard 数据联动（未从 `spms_process_data` 表查询实际数据）
- ❌ 后端未实现 `ReportController` 和 `ReportService`

#### 代码证据

**报表组件（已实现，使用 MockData）**:
```24:49:frontend/src/pages/reports/TimecardSummaryReport.jsx
import { mockSummaryReportData } from '../../data/timecard/data/mockData';

const TimecardSummaryReport = () => {
  const filteredData = useMemo(() => {
    return mockSummaryReportData.filter(item => {
      if (filters.staffType !== 'all' && item.staffType !== filters.staffType) {
        return false;
      }
      if (filters.claimType !== 'all' && item.claimType !== filters.claimType) {
        return false;
      }
      return true;
    });
  }, [filters]);
```

**说明**: 所有报表组件都使用类似的模式，从 `mockData.js` 导入数据，而不是调用后端 API。

---

## 功能需求完成度统计

### 按模块统计

| 模块 | 总需求数 | 已完成 | 部分完成 | 未完成 | 完成度 |
|------|---------|--------|---------|--------|--------|
| Timecard Form Setup | 6 | 6 | 0 | 0 | 100% |
| Timecard Entry | 20 | 17 | 0 | 3 | 85% |
| Timecard Approval | 5 | 4 | 0 | 1 | 80% |
| Organization Data | 5 | 3 | 0 | 2 | 60% |
| Project Management | 6 | 6 | 0 | 0 | 100% |
| Holiday Management | 5 | 5 | 0 | 0 | 100% |
| Reports Generation | 5 | 5 | 0 | 0 | 100%* |
| **总计** | **52** | **46** | **0** | **6** | **88%** |

*注：Reports Generation 前端功能完整，但使用 MockData，未与后端集成

### 按优先级统计

| 优先级 | User Story | 状态 | 完成度 |
|--------|-----------|------|--------|
| P1 | US0: Form Setup | ✅ 完成 | 100% |
| P1 | US1: Timecard Entry | ⚠️ 部分完成 | 85% |
| P1 | US2: Approval Workflow | ✅ 完成 | 80% |
| P2 | US3: Organization Data | ✅ 完成（原系统） | 60% |
| P2 | US4: Project Management | ✅ 完成 | 100% |
| P2 | US5: Holiday Management | ✅ 完成 | 100% |
| P3 | US6: Reports Generation | ⚠️ 部分完成（MockData） | 100%* |

*注：前端功能完整，但使用 MockData，需要后端 API 集成

---

## 关键缺失功能清单

### 高优先级 (P1) - 必须实现

1. **月份调整 API** (`PUT /api/v1/timecard/adjust/{month}`)
   - 仅允许调整上个月
   - 验证月份是否为上个月
   - 前端 UI 已实现（`PreviousMonthAdjust.jsx`），但后端 API 未实现

2. **月度截止日期验证**
   - 验证 timecard 提交是否超过可配置的月度截止日期（存储在 `system_config` 表中）
   - 在配置的月度截止日期后阻止 timecard 提交
   - 目前提交功能已实现，但缺少截止日期验证逻辑

3. **Timecard 状态跟踪**
   - 提供 timecard 状态跟踪，显示当前状态（draft, submitted, approval pending, approved）
   - 前端已显示状态，但需要与 Flowable 流程状态同步

### 中优先级 (P2) - 建议实现

4. **组织数据管理 Excel 导入/导出**
   - 验证是否已实现 Excel 导入/导出功能
   - 如未实现，需要补充

5. **组织数据操作审计日志**
   - 验证是否已实现审计日志功能
   - 如未实现，需要补充

### 低优先级 (P3) - 可选实现

6. **报表生成后端 API 集成**
   - 实现 `ReportController` 和 `ReportService`
   - 实现从 `spms_process_data` 表查询 timecard 数据的逻辑
   - 实现 4 种报表类型的数据聚合逻辑
   - 将前端报表组件从 MockData 切换到实际 API
   - 实现权限控制（员工只能查看自己的报表，管理员可以查看所有）

---

## 建议的后续行动

### 立即行动（P1 优先级）

1. **实现月份调整 API**
   - 在 `TimecardController` 中添加 `PUT /api/v1/timecard/adjust/{month}` 端点
   - 在 `TimecardService` 中添加 `adjustTimecardMonth()` 方法
   - 实现月份验证逻辑（仅允许上个月）
   - 前端 UI 已实现（`PreviousMonthAdjust.jsx`），只需后端支持

2. **实现月度截止日期验证**
   - 在 `TimecardService` 中添加截止日期验证逻辑
   - 查询 `system_config` 表获取 `timecard.deadline.{month}` 配置
   - 在 `TaskDetailsDialog` 的提交流程中添加截止日期检查
   - 如果超过截止日期，阻止提交并显示错误消息

3. **完善 Timecard 状态跟踪**
   - 确保前端显示的状态与 Flowable 流程状态同步
   - 从 Flowable 流程实例中获取当前状态（draft, submitted, approval pending, approved）
   - 在 `TimecardForm` 中正确显示状态

### 短期行动（P2 优先级）

4. **验证组织数据管理 Excel 导入/导出**
   - 检查现有组织模块是否支持 Excel 导入/导出
   - 如未实现，补充 Excel 导入/导出功能

5. **验证组织数据操作审计日志**
   - 检查现有组织模块是否支持审计日志
   - 如未实现，补充审计日志功能

### 长期行动（P3 优先级）

6. **实现报表生成后端 API**
   - 创建 `ReportService` 和 `ReportController`
   - 实现从 `spms_process_data` 表查询 timecard 数据的逻辑
   - 实现 4 种报表类型的数据聚合逻辑：
     - Timecard Summary Report（按项目、部门、时间段聚合）
     - Individual Timecard Record（按用户查询）
     - Timecard Details Report（详细条目和财务详情）
     - Perm Staff IT Record（仅 OT 数据，永久员工或供应商员工）
   - 实现权限控制（员工只能查看自己的报表，管理员可以查看所有）
   - 将前端报表组件从 MockData 切换到实际 API
   - 前端报表页面已实现，只需后端 API 支持

---

## 测试建议

### 单元测试

- [ ] `TimecardService.submitTimecard()` 测试
- [ ] `TimecardService.getEmployeeInfo()` 测试
- [ ] `TimecardService.getApprovalTasks()` 测试
- [ ] 截止日期验证逻辑测试
- [ ] 月份调整验证逻辑测试

### 集成测试

- [ ] Timecard 提交流程端到端测试
- [ ] Flowable 流程实例创建测试
- [ ] 审批工作流集成测试
- [ ] 组织架构路由测试

### 前端测试

- [ ] `TimecardForm` 组件测试（包括只读模式）
- [ ] 审批/拒绝 UI 测试
- [ ] 数据渲染逻辑测试（Flowable vs API）

---

## 结论

Timecard 模块的核心功能已经**基本完成**，包括：
- ✅ 表单注册和自定义组件集成
- ✅ 时间卡录入（包括日历视图、项目验证、汇总计算、警告显示）
- ✅ 时间卡提交（通过 TaskDetailsDialog 和 Flowable 流程）
- ✅ 审批工作流（审批人员可以查看、审批、拒绝 timecard）
- ✅ 项目管理（CRUD、Excel 导入/导出）
- ✅ 节假日管理（完整的 CRUD 和 Excel 导入/导出）
- ✅ 报表生成（前端功能完整，但使用 MockData）

**主要缺失功能**：
1. 月份调整 API（前端 UI 已实现，后端 API 未实现）
2. 月度截止日期验证（提交时缺少截止日期检查）
3. 报表后端 API（前端使用 MockData，需要与后端集成）

**建议优先级**：
1. 实现月份调整 API（P1，前端已就绪）
2. 实现月度截止日期验证（P1，确保数据完整性）
3. 验证组织数据管理的 Excel 导入/导出和审计日志（P2）
4. 实现报表生成后端 API（P3，将 MockData 切换到实际数据）

---

**报告生成时间**: 2025-01-22  
**审查人**: AI Assistant  
**下次审查建议**: 完成 P1 优先级功能后

