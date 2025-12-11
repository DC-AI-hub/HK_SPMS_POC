# Holiday Setting、Project、Reports 界面开发计划（更新版）

## 技术栈说明

- **前端框架**: React 18 + MUI (Material-UI) v5
- **设计规范**: 遵循现有MUI设计风格，使用MUI组件（Card, Table, Button, Select, Dialog, Chip等）
- **样式方案**: MUI Theme + sx prop

## 一、Holiday Setting 界面开发

### 1.1 在 Process 页面添加 Holiday Setting Tab

- **文件**: `frontend/src/pages/Process.jsx`
- **修改内容**:
  - 在 `tabs` 数组中添加第4个tab：`{ icon: <EventIcon />, label: t('process:tabs.holidaySetting') }`
  - 添加 `HolidaySettingTab` 组件的导入和条件渲染
  - 更新 `currentTab === 3` 的条件判断

### 1.2 创建 HolidaySettingTab 组件

- **文件**: `frontend/src/pages/process/HolidaySettingTab.jsx`（新建）
- **技术**: 使用MUI组件（Card, CardContent, Box, TextField, Button等）
- **功能**:
  - 复用 `CalendarView` 组件（`frontend/src/components/form/timecard/CalendarView.jsx`）
  - 适配为节假日设置功能：
    - 点击日期打开添加/编辑节假日对话框（MUI Dialog）
    - 显示已有节假日（绿色背景）
    - 支持删除节假日（点击删除按钮）
  - 集成后端API：
    - `getHolidays(year, month)` - 获取节假日列表
    - `importHolidays(file)` - 导入节假日
    - `exportHolidays(year)` - 导出节假日
  - 添加月份选择器（MUI TextField type="month"）
  - 添加导入/导出按钮（MUI Button + Upload/Download图标）

### 1.3 创建 HolidayDialog 组件

- **文件**: `frontend/src/components/holiday/HolidayDialog.jsx`（新建）
- **技术**: 使用MUI Dialog, TextField, Select, Button组件
- **功能**:
  - 添加/编辑节假日表单
  - 字段：
    - Date（只读，MUI TextField disabled）
    - Country（MUI Select，选项：CN/HK/US/UK）
    - Holiday Name（MUI TextField）
    - Type（MUI Select，选项：PUBLIC_HOLIDAY/COMPANY_HOLIDAY）
  - 表单验证和提交

### 1.4 更新 i18n 配置

- **文件**: `frontend/src/i18n/locales/en/process.json` 和 `frontend/src/i18n/locales/zh/process.json`
- **添加**: `"holidaySetting": "Holiday Setting"` / `"holidaySetting": "节假日设置"`

## 二、Project 界面开发

### 2.1 在 Sidebar 添加 Project 按钮

- **文件**: `frontend/src/components/Sidebar.jsx`
- **修改内容**:
  - 在 `getFallbackMenuItems()` 函数中添加新的菜单项
  - 导入 `WorkIcon` from `@mui/icons-material`

### 2.2 更新 i18n 配置

- **文件**: `frontend/src/i18n/locales/en/sidebar.json` 和 `frontend/src/i18n/locales/zh/sidebar.json`
- **添加**: `"project": "Project"` / `"project": "项目"`

### 2.3 创建 Project 页面

- **文件**: `frontend/src/pages/Project.jsx`（新建）
- **技术**: 使用MUI组件（Card, Table, TextField, Select, Button, Dialog, Chip等）
- **功能**: 根据 Design.md 中的 ProjectManagement 组件设计实现
  - 项目列表表格（MUI Table，6列：Project Code, Project Name, Task Number, Activity, Status, Actions）
  - 搜索框（MUI TextField with SearchIcon）
  - 状态筛选（MUI Select：ALL/ACTIVE/COMPLETED）
  - 添加/编辑项目对话框（MUI Dialog）
  - 导入/导出功能（MUI Button + Upload/Download图标）
  - 状态显示（MUI Chip：ACTIVE=default, COMPLETED=secondary）
  - 集成后端API：
    - `getActiveProjects(search)` - 获取项目列表
    - `importProjects(file)` - 导入项目
    - `exportProjects(status)` - 导出项目

### 2.4 添加路由配置

- **文件**: `frontend/src/App.jsx`
- **修改内容**:
  - 导入 `Project` 组件
  - 添加路由：`<Route path="/project" element={<Project />} />`

## 三、Reports 界面开发

### 3.1 更新 Reports 页面

- **文件**: `frontend/src/pages/reports/Reports.jsx`
- **技术**: 使用MUI ResponsiveTabs组件
- **功能**: 根据 Design.md 实现4种报表类型
  - 使用 ResponsiveTabs 组件实现Tab切换
  - 4个Tab：

    1. Summary Report（年度汇总报表）
    2. Individual Report（个人工时汇总报表）
    3. Details Report（工时明细报表）
    4. OT Record Report（正式员工加班记录报表）

### 3.2 创建报表组件（根据Design.md）

#### 3.2.1 TimecardSummaryReport（年度工时汇总报表）

- **文件**: `frontend/src/pages/reports/TimecardSummaryReport.jsx`（新建）
- **技术**: MUI Card, Table, Select, Button, Chip组件
- **筛选器**（3个）:

  1. Year（MUI Select：2025/2024/2023）
  2. Staff Type（MUI Select：all/permanent/vendor）
  3. Claim Type（MUI Select：all/NORMAL/OT_WORKING_DAYS/OT_HOLIDAYS）

- **表格字段**（6列）:

  1. Project Code/Charge Code
  2. Task
  3. Activity
  4. Claim Type（MUI Chip：NORMAL=secondary, OT=default）
  5. Staff Type
  6. Year-To-Date Man-Hours（右对齐，千分位格式）

- **合计行**: 前5列合并显示"Total"，第6列显示总工时

#### 3.2.2 IndividualTimecardReport（个人工时汇总报表）

- **文件**: `frontend/src/pages/reports/IndividualTimecardReport.jsx`（新建）
- **技术**: MUI Card, Table, Select, TextField, Button, Chip组件
- **筛选器**（3个）:

  1. Staff ID（MUI Select：all/EMP001/EMP002/EMP003）
  2. Month（MUI TextField type="month"，默认2025-11）
  3. Status（MUI Select：all/DRAFT/SUBMITTED/APPROVED）

- **表格字段**（11列）:

  1. Form ID
  2. Timecard Status（MUI Chip：APPROVED=default, 其他=secondary）
  3. Submission Time
  4. Final Approval Time
  5. Staff ID
  6. Staff Name (Chinese)
  7. Staff Name
  8. Staff Type
  9. Timecard Month
  10. ITD-SZ|SH Supervisor
  11. Final Approver

#### 3.2.3 TimecardDetailsReport（工时明细报表）

- **文件**: `frontend/src/pages/reports/TimecardDetailsReport.jsx`（新建）
- **技术**: MUI Card, Table, Select, TextField, Button, Chip组件
- **筛选器**（5个）:

  1. Department（MUI Select：all/it/infra）
  2. Project（MUI Select：all/PROJ001/PROJ002）
  3. Staff Type（MUI Select：all/permanent/vendor）
  4. Date From（MUI TextField type="date"，默认2025-11-01）
  5. Date To（MUI TextField type="date"，默认2025-11-30）

- **表格字段**（22列，支持横向滚动）:

  1. Form ID
  2. Staff ID
  3. Staff Name (Chinese)
  4. Staff Name
  5. ITD-SZ|SH Team
  6. Timecard Month
  7. Code Type（Project Code/Charge Code）
  8. Project Code/Charge Code
  9. Project Name/Charge Name
  10. Task
  11. Activity
  12. Claim Type（MUI Chip：NORMAL=secondary, OT=default）
  13. Adjustment Month
  14. Department
  15. Charge Grade
  16. Staff Type
  17. ITD-SZ|SH Supervisor
  18. Final Approver
  19. Man-Hours（右对齐）
  20. Submission Time
  21. Timecard Status（MUI Chip：APPROVED=default, 其他=secondary）
  22. Final Approval Time

- **合计行**: 前18列合并显示"Total"，第19列显示总工时，后3列空白

#### 3.2.4 PermStaffOTReport（正式员工加班记录报表）

- **文件**: `frontend/src/pages/reports/PermStaffOTReport.jsx`（新建）
- **技术**: MUI Card, Table, Select, TextField, Button, Chip组件
- **筛选器**（3个）:

  1. Staff Type（MUI Select，默认PERMANENT：ALL/PERMANENT/VENDOR）
  2. Month（MUI TextField type="month"，默认2025-11）
  3. Claim Type（MUI Select：all/OT_WORKING_DAYS/OT_HOLIDAYS/OT_STATUTORY_HOLIDAYS）

- **表格字段**（15列）:

  1. Form ID
  2. Submission Time
  3. Timecard Status（MUI Chip：APPROVED=default, 其他=secondary）
  4. Final Approval Time
  5. Staff ID
  6. Staff Name (Chinese)
  7. Staff Name
  8. ITD-SZ|SH Team
  9. Timecard Month
  10. Project Code/Charge Code
  11. Adjustment Month
  12. Claim Type（MUI Chip：统一default，显示时下划线转空格）
  13. Staff Type
  14. ITD-SZ|SH Supervisor
  15. Man-Hours（右对齐）

- **合计行**: 前14列合并显示"Total"，第15列显示总工时

### 3.3 更新 mockData.js

- **文件**: `frontend/src/data/timecard/data/mockData.js`
- **添加内容**: 4个报表的Mock数据
  - `mockSummaryReportData` - 年度汇总报表数据（6字段）
  - `mockIndividualReportData` - 个人工时汇总报表数据（11字段）
  - `mockDetailsReportData` - 工时明细报表数据（22字段）
  - `mockOTReportData` - 加班记录报表数据（15字段）

## 四、技术细节

### 4.1 CalendarView 适配

- CalendarView 当前用于工时录入，需要适配为节假日设置：
  - 移除工时相关的交互（Copy、Delete按钮改为删除节假日）
  - 点击日期打开节假日添加对话框（MUI Dialog）
  - 保留日历网格布局和节假日标记（绿色背景）
  - 使用MUI组件保持设计一致性

### 4.2 API 集成

- 所有界面需要集成已存在的后端API：
  - Holiday API: `getHolidays`, `importHolidays`, `exportHolidays`
  - Project API: `getActiveProjects`, `importProjects`, `exportProjects`
  - Reports API: **暂时使用Mock数据**（后端API待实现）

### 4.3 组件复用

- 复用现有的通用组件：
  - `ReportFilters.jsx` - 报表筛选器（MUI组件）
  - `ReportTable.jsx` - 报表表格（MUI Table）
  - `ReportPagination.jsx` - 分页（MUI Pagination）
  - `ExportProgress.jsx` - 导出进度（MUI Dialog + LinearProgress）

### 4.4 MUI组件使用规范

- **Card**: 使用MUI Card, CardContent, CardHeader
- **Table**: 使用MUI Table, TableHead, TableBody, TableRow, TableCell
- **Button**: 使用MUI Button（variant: contained/outlined/text）
- **Select**: 使用MUI Select, MenuItem
- **TextField**: 使用MUI TextField（type: text/date/month/number）
- **Chip**: 使用MUI Chip（variant: default/secondary，用于状态显示）
- **Dialog**: 使用MUI Dialog, DialogTitle, DialogContent, DialogActions
- **Badge**: 使用MUI Chip替代（因为MUI没有Badge组件）

## 五、Mock数据格式定义（更新mockData.js）

### 5.1 TimecardSummaryReport Mock数据

```javascript
export const mockSummaryReportData = [
  {
    projectCode: "PROJ001",
    task: "T001",
    activity: "Development",
    claimType: "NORMAL",
    staffType: "Permanent",
    ytdManHours: 1280
  },
  // ... 更多数据
];
```

### 5.2 IndividualTimecardReport Mock数据

```javascript
export const mockIndividualReportData = [
  {
    formId: "TC-2025-11-001",
    timecardStatus: "APPROVED",
    submissionTime: "2025-11-15T10:30:00",
    finalApprovalTime: "2025-11-16T14:20:00",
    staffId: "EMP001",
    staffNameChinese: "张伟",
    staffName: "Wei Zhang",
    staffType: "Permanent",
    timecardMonth: "2025-11",
    supervisor: "John Smith",
    finalApprover: "Sarah Johnson"
  },
  // ... 更多数据
];
```

### 5.3 TimecardDetailsReport Mock数据

```javascript
export const mockDetailsReportData = [
  {
    formId: "TC-2025-11-001",
    staffId: "EMP001",
    staffNameChinese: "张伟",
    staffName: "Wei Zhang",
    team: "IT Development",
    timecardMonth: "2025-11",
    codeType: "Project Code",
    projectCode: "PROJ001",
    projectName: "System Upgrade",
    task: "T001",
    activity: "Development",
    claimType: "NORMAL",
    adjustmentMonth: null,
    department: "IT",
    chargeGrade: "A",
    staffType: "Permanent",
    supervisor: "John Smith",
    finalApprover: "Sarah Johnson",
    manHours: 160,
    submissionTime: "2025-11-15T10:30:00",
    timecardStatus: "APPROVED",
    finalApprovalTime: "2025-11-16T14:20:00"
  },
  // ... 更多数据
];
```

### 5.4 PermStaffOTReport Mock数据

```javascript
export const mockOTReportData = [
  {
    formId: "TC-2025-11-001",
    submissionTime: "2025-11-15T10:30:00",
    timecardStatus: "APPROVED",
    finalApprovalTime: "2025-11-16T14:20:00",
    staffId: "EMP001",
    staffNameChinese: "张伟",
    staffName: "Wei Zhang",
    team: "IT Development",
    timecardMonth: "2025-11",
    projectCode: "PROJ001",
    adjustmentMonth: null,
    claimType: "OT_WORKING_DAYS",
    staffType: "Permanent",
    supervisor: "John Smith",
    manHours: 8
  },
  // ... 更多数据
];
```

## 六、文件清单

### 新建文件

1. `frontend/src/pages/process/HolidaySettingTab.jsx`
2. `frontend/src/components/holiday/HolidayDialog.jsx`
3. `frontend/src/pages/Project.jsx`
4. `frontend/src/pages/reports/TimecardSummaryReport.jsx`
5. `frontend/src/pages/reports/IndividualTimecardReport.jsx`
6. `frontend/src/pages/reports/TimecardDetailsReport.jsx`
7. `frontend/src/pages/reports/PermStaffOTReport.jsx`

### 修改文件

1. `frontend/src/pages/Process.jsx`
2. `frontend/src/components/Sidebar.jsx`
3. `frontend/src/App.jsx`
4. `frontend/src/pages/reports/Reports.jsx`
5. `frontend/src/data/timecard/data/mockData.js`（添加4个报表的Mock数据）
6. `frontend/src/i18n/locales/en/process.json`
7. `frontend/src/i18n/locales/zh/process.json`
8. `frontend/src/i18n/locales/en/sidebar.json`
9. `frontend/src/i18n/locales/zh/sidebar.json`

## 七、注意事项

1. **MUI组件使用**: 所有界面必须使用MUI组件，保持设计风格一致性
2. **Reports Mock数据**: Reports界面暂时使用Mock数据，待后端API实现后再替换
3. **字段完整性**: 4个报表的所有字段必须按照Design.md中的定义完整实现
4. **国际化**: 所有文本需要支持中英文切换（使用react-i18next）
5. **错误处理**: 所有API调用需要添加错误处理和Toast提示（使用react-toastify或MUI Snackbar）
6. **响应式设计**: 所有界面需要支持移动端和桌面端响应式布局
7. **Badge显示**: 使用MUI Chip组件替代Badge，variant=default表示主要状态，variant=secondary表示次要状态

## 八、后端开发扩展

### 8.1 Holiday 相关后端扩展

#### 8.1.1 扩展 HolidayEntity 实体类

- **文件**: `backend/src/main/java/com/spms/backend/repository/entities/timecard/HolidayEntity.java`
- **需要添加的字段**:
  - `country` (String, VARCHAR(10)) - 国家代码（CN/HK/US/UK）
  - `name` (String, VARCHAR(200)) - 节假日名称
  - `type` (String, VARCHAR(50)) - 节假日类型（PUBLIC_HOLIDAY/COMPANY_HOLIDAY）
- **数据库迁移**: 需要创建 Flyway migration 脚本添加这3个字段到 `spms_holiday` 表

#### 8.1.2 扩展 HolidayDTO

- **文件**: `backend/src/main/java/com/spms/backend/controller/dto/timecard/HolidayDTO.java`
- **需要添加的字段**:
  - `country` (String) - 国家代码
  - `name` (String) - 节假日名称
  - `type` (String) - 节假日类型
- **字段说明**:
  - `date` 字段需要支持 LocalDate 类型，格式为 YYYY-MM-DD
  - 所有字段都需要在 DTO 中体现

#### 8.1.3 扩展 HolidayService 接口和实现

- **文件**: 
  - `backend/src/main/java/com/spms/backend/service/timecard/HolidayService.java`
  - `backend/src/main/java/com/spms/backend/service/timecard/impl/HolidayServiceImpl.java`
- **需要添加的方法**:
  - `createHoliday(HolidayDTO holidayDTO)` - 创建节假日
  - `updateHoliday(Long id, HolidayDTO holidayDTO)` - 更新节假日
  - `deleteHoliday(Long id)` - 删除节假日（软删除，设置 is_active = false）
- **业务逻辑**:
  - 创建时检查同一日期、同一国家是否已存在节假日
  - 更新时验证日期格式和字段有效性
  - 删除时使用软删除，保留历史数据
  - 所有操作需要记录 createdBy 和 updatedBy（从 SecurityContext 获取当前用户ID）

#### 8.1.4 扩展 HolidayController

- **文件**: `backend/src/main/java/com/spms/backend/controller/timecard/HolidayController.java`
- **需要添加的端点**:
  - `POST /api/v1/timecard/holidays` - 创建节假日
    - 请求体: `{date, country, name, type}`
    - 响应: 201 Created + HolidayDTO
  - `PUT /api/v1/timecard/holidays/{id}` - 更新节假日
    - 路径参数: id (Long)
    - 请求体: `{date, country, name, type}`（部分字段可选）
    - 响应: 200 OK + HolidayDTO
  - `DELETE /api/v1/timecard/holidays/{id}` - 删除节假日
    - 路径参数: id (Long)
    - 响应: 204 No Content
- **现有端点扩展**:
  - `GET /api/v1/timecard/holidays` - 需要返回完整的 HolidayDTO（包含 country, name, type 字段）

#### 8.1.5 扩展 HolidayRepository

- **文件**: `backend/src/main/java/com/spms/backend/repository/timecard/HolidayRepository.java`
- **需要添加的查询方法**:
  - `findByCountry(String country)` - 按国家查询
  - `existsByHolidayDateAndCountry(LocalDate date, String country)` - 检查指定日期和国家是否已存在节假日
  - `findByYearAndCountry(Integer year, String country)` - 按年份和国家查询

#### 8.1.6 Holiday 数据同步到 Timecard 表单

- **实现方式**: 
  - TimecardForm 组件已经通过 `getHolidays(year, month)` API 获取节假日数据
  - 后端 `GET /api/v1/timecard/holidays` 端点返回的数据会自动传递到 CalendarView 组件
  - CalendarView 使用 `holidays` prop 来标记节假日日期（绿色背景）
  - **无需额外API**，现有查询接口已满足需求
- **数据格式要求**:
  - 后端返回的 HolidayDTO.date 字段格式必须为 YYYY-MM-DD（LocalDate 会自动序列化为该格式）
  - 前端 CalendarView 通过 `holidays.some(h => h.date === dateStr)` 判断是否为节假日

### 8.2 Project 页面 CRUD 后端扩展

#### 8.2.1 创建 ProjectTaskActivityController

- **文件**: `backend/src/main/java/com/spms/backend/controller/timecard/ProjectTaskActivityController.java`（新建）
- **功能**: 提供项目-任务-活动组合的 CRUD API
- **端点设计**:
  - `GET /api/v1/timecard/projects/tasks` - 获取项目-任务-活动列表（支持搜索和状态筛选）
    - 查询参数: `search` (String, 可选), `status` (String, 可选: ACTIVE/COMPLETED/ALL)
    - 响应: `List<ProjectTaskDTO>`
  - `POST /api/v1/timecard/projects/tasks` - 创建项目-任务-活动组合
    - 请求体: `{projectCode, projectName, taskNumber, activity, status}`
    - 响应: 201 Created + ProjectTaskDTO
  - `PUT /api/v1/timecard/projects/tasks/{id}` - 更新项目-任务-活动组合
    - 路径参数: id (String, 格式: projectId-taskId)
    - 请求体: `{projectCode, projectName, taskNumber, activity, status}`（projectCode 不可修改）
    - 响应: 200 OK + ProjectTaskDTO
  - `DELETE /api/v1/timecard/projects/tasks/{id}` - 删除项目-任务-活动组合（软删除，标记任务为 COMPLETED）
    - 路径参数: id (String)
    - 响应: 204 No Content

#### 8.2.2 创建 ProjectTaskActivityService

- **文件**: 
  - `backend/src/main/java/com/spms/backend/service/timecard/ProjectTaskActivityService.java`（新建接口）
  - `backend/src/main/java/com/spms/backend/service/timecard/impl/ProjectTaskActivityServiceImpl.java`（新建实现）
- **方法定义**:
  - `List<ProjectTaskDTO> getProjectTasks(String search, String status)` - 获取项目-任务列表（支持搜索和状态筛选）
  - `ProjectTaskDTO createProjectTask(ProjectTaskCreateDTO createDTO)` - 创建项目-任务-活动组合
  - `ProjectTaskDTO updateProjectTask(String id, ProjectTaskUpdateDTO updateDTO)` - 更新项目-任务-活动组合
  - `void deleteProjectTask(String id)` - 删除项目-任务-活动组合（软删除）
- **业务逻辑**:
  - 创建时：
    - 如果项目不存在，创建新项目（ProjectEntity）
    - 如果任务不存在，创建新任务（TaskEntity），关联到项目
    - 设置任务的 activity 字段
    - 返回 ProjectTaskDTO（扁平化格式）
  - 更新时：
    - 解析 id（格式：projectId-taskId）获取项目ID和任务ID
    - 更新项目名称（如果提供）
    - 更新任务编号、活动（如果提供）
    - 更新项目状态（如果提供）
  - 删除时：
    - 将项目状态设置为 COMPLETED（软删除）
    - 或者将任务标记为 isActive = false

#### 8.2.3 创建 ProjectTaskCreateDTO 和 ProjectTaskUpdateDTO

- **文件**: 
  - `backend/src/main/java/com/spms/backend/controller/dto/timecard/ProjectTaskCreateDTO.java`（新建）
  - `backend/src/main/java/com/spms/backend/controller/dto/timecard/ProjectTaskUpdateDTO.java`（新建）
- **ProjectTaskCreateDTO 字段**:
  - `projectCode` (String, 必填) - 项目代码
  - `projectName` (String, 必填) - 项目名称
  - `taskNumber` (String, 必填) - 任务编号
  - `activity` (String, 必填) - 活动类型
  - `status` (String, 可选, 默认 ACTIVE) - 项目状态
- **ProjectTaskUpdateDTO 字段**:
  - `projectName` (String, 可选) - 项目名称
  - `taskNumber` (String, 可选) - 任务编号
  - `activity` (String, 可选) - 活动类型
  - `status` (String, 可选) - 项目状态

#### 8.2.4 扩展 ExcelImportExportService

- **文件**: `backend/src/main/java/com/spms/backend/service/timecard/impl/ExcelImportExportServiceImpl.java`
- **需要更新的方法**:
  - `importProjects()` - 需要支持导入项目-任务-活动组合数据
    - Excel 格式: Project Code, Project Name, Task Number, Activity, Status
    - 导入逻辑需要创建或更新项目、任务，并设置任务的 activity 字段
  - `exportProjects()` - 需要导出项目-任务-活动组合数据
    - 导出格式: Project Code, Project Name, Task Number, Activity, Status
    - 数据来源: 查询所有活跃项目及其任务，扁平化为一条记录

### 8.3 数据库迁移脚本

#### 8.3.1 Holiday 表字段扩展

- **文件**: `backend/src/main/resources/db/migration/V{timestamp}__Add_holiday_fields.sql`（新建）
- **SQL 内容**:
```sql
ALTER TABLE spms_holiday 
ADD COLUMN country VARCHAR(10) DEFAULT 'CN',
ADD COLUMN name VARCHAR(200),
ADD COLUMN type VARCHAR(50) DEFAULT 'PUBLIC_HOLIDAY';

-- 更新现有数据的默认值
UPDATE spms_holiday 
SET country = 'CN', 
    name = CONCAT('Holiday ', holiday_date::text),
    type = 'PUBLIC_HOLIDAY'
WHERE country IS NULL OR name IS NULL OR type IS NULL;

-- 添加约束
ALTER TABLE spms_holiday 
ALTER COLUMN country SET NOT NULL,
ALTER COLUMN name SET NOT NULL,
ALTER COLUMN type SET NOT NULL;

-- 添加唯一约束（同一日期、同一国家不能重复）
CREATE UNIQUE INDEX IF NOT EXISTS idx_holiday_date_country 
ON spms_holiday(holiday_date, country);
```

### 8.4 API 端点总结

#### 8.4.1 Holiday API（扩展后）

- `GET /api/v1/timecard/holidays?year={year}&month={month}` - 获取节假日列表（已实现，需扩展返回字段）
- `POST /api/v1/timecard/holidays` - 创建节假日（**待实现**）
- `PUT /api/v1/timecard/holidays/{id}` - 更新节假日（**待实现**）
- `DELETE /api/v1/timecard/holidays/{id}` - 删除节假日（**待实现**）
- `POST /api/v1/timecard/holidays/import` - 导入节假日（已实现，需扩展支持 country/name/type）
- `GET /api/v1/timecard/holidays/export?year={year}` - 导出节假日（已实现，需扩展导出字段）

#### 8.4.2 Project-Task-Activity API（新增）

- `GET /api/v1/timecard/projects/tasks?search={search}&status={status}` - 获取项目-任务-活动列表（**待实现**）
- `POST /api/v1/timecard/projects/tasks` - 创建项目-任务-活动组合（**待实现**）
- `PUT /api/v1/timecard/projects/tasks/{id}` - 更新项目-任务-活动组合（**待实现**）
- `DELETE /api/v1/timecard/projects/tasks/{id}` - 删除项目-任务-活动组合（**待实现**）
- `POST /api/v1/timecard/projects/import` - 导入项目数据（已实现，需扩展支持 activity 字段）
- `GET /api/v1/timecard/projects/export?status={status}` - 导出项目数据（已实现，需扩展导出 activity 字段）

### 8.5 前端 API 调用更新

#### 8.5.1 更新 timecardService.js

- **文件**: `frontend/src/api/timecard/timecardService.js`
- **需要添加的函数**:
  - `createProjectTaskActivity(data)` - 创建项目-任务-活动组合
  - `updateProjectTaskActivity(id, data)` - 更新项目-任务-活动组合
  - `deleteProjectTaskActivity(id)` - 删除项目-任务-活动组合
- **已添加的函数**（需要后端实现后启用）:
  - `createHoliday(holidayData)` - 创建节假日
  - `updateHoliday(id, holidayData)` - 更新节假日
  - `deleteHoliday(id)` - 删除节假日

#### 8.5.2 更新 Project.jsx

- **文件**: `frontend/src/pages/Project.jsx`
- **需要更新的方法**:
  - `handleSave()` - 调用 `createProjectTaskActivity()` 或 `updateProjectTaskActivity()` API
  - `handleDelete()` - 调用 `deleteProjectTaskActivity()` API（如果需要删除功能）

### 8.6 Reports 后端 API（暂不实现）

- **说明**: Reports 相关的后端 API 将在后续开发中根据详细 spec 进行实现
- **当前状态**: 前端使用 Mock 数据，待后端 API 实现后再替换

### 8.7 技术要点

1. **数据一致性**: 
   - Holiday 创建/更新时需要验证日期格式和唯一性约束
   - Project-Task-Activity 创建时需要确保项目、任务、活动的关联关系正确

2. **软删除策略**:
   - Holiday 删除使用软删除（is_active = false）
   - Project 删除通过设置状态为 COMPLETED 实现
   - Task 删除通过设置 isActive = false 实现

3. **审计字段**:
   - 所有创建/更新操作需要记录 createdBy 和 updatedBy（从 SecurityContext 获取）
   - 时间戳使用 System.currentTimeMillis() 记录

4. **错误处理**:
   - 验证失败返回 400 Bad Request
   - 资源不存在返回 404 Not Found
   - 唯一性冲突返回 409 Conflict
   - 使用统一的 ApiResponse 格式返回错误信息