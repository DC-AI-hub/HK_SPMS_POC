# Timecard 模块集成规格说明书（修订版）

## 目录
1. [项目背景](#项目背景)
2. [目标与范围](#目标与范围)
3. [架构设计决策](#架构设计决策)
4. [数据模型](#数据模型)
5. [Mock数据策略](#Mock数据策略)
6. [组件设计](#组件设计)
7. [API服务层](#API服务层)
8. [自定义组件集成方案](#自定义组件集成方案)
9. [实施步骤](#实施步骤)
10. [验证标准](#验证标准)

---

## 1. 项目背景

### 1.1 现有系统状态
- **SPMS系统**：基于Spring Boot + React的企业级流程管理系统
- **前端架构**：React 18 + MUI v7 + React Router v7
- **流程引擎**：Flowable BPMN 7.1.0
- **表单系统**：动态表单系统，支持自定义组件注册

### 1.2 Timecard需求来源
- 客户需求文档：`Functional Specification.md`
- Figma设计确认：已完成所有功能需求和设计确认
- 现有规格：Timecard Module Specification 已基本实现所有功能

### 1.3 关键业务逻辑（基于TaskDetailsDialog分析）

```
数据流动路径：

processInstance.contextValue.formData
    ↓ (作为initialData传入)
TimecardForm
    ↓ (用户在表单内编辑)
用户点击Save按钮 → 触发onSubmit(updatedData)
    ↓
TaskDetailsDialog接收updatedData → setFormData(updatedData)
    ↓
用户点击Complete按钮 → onComplete({ formData })
    ↓
Flowable引擎接收 → 传递给审批员任务
    ↓
审批员打开任务 → processInstance.contextValue.formData作为initialData
```

**关键点**：
- TimecardForm是Pure React Component，不直接与后端交互
- 所有数据保存在TaskDetailsDialog的state中，通过props传递
- 点击Complete才真正进入Flowable引擎
- 无需管理draft/submitted/approved状态（由Flowable BPMN管理）

---

## 2. 目标与范围

### 2.1 核心目标
**主要目标**：在2周内完成Timecard模块与SPMS流程管理系统的集成，支持端到端demo演示

**具体目标**：
1. 将Timecard表单改造为符合SPMS流程管理架构的自定义组件
2. 实现Mock数据驱动，支持无后端环境的完整功能验证
3. 注册为自定义组件，可被流程管理系统选择和展示
4. 集成到Flowable流程引擎中，支持完整的填报-审批流程

### 2.2 功能范围

**包含的功能**：
- ✅ 员工信息显示（从current user获取）
- ✅ Calendar View（月视图展示当月工时）
- ✅ 颜色区分项目（不同project code显示不同颜色）
- ✅ 红色背景警告（每日>8h或周末工作）
- ✅ 绿色背景高亮（节假日）
- ✅ 黄色警告（月度总工时>160h，在汇总显示）
- ✅ Project Entry对话框（添加/编辑项目条目）
- ✅ Project Code验证（输入或选择项目代码）
- ✅ Task选择（根据项目自动加载任务列表）
- ✅ Activity自动填充（从任务获取）
- ✅ Claim Type下拉选择（5种类型）
- ✅ Hours字段范围限制（0-24，精度0.5）
- ✅ 必填字段验证
- ✅ 批量操作（按本周/工作日/空白日复制）
- ✅ 工时汇总（按project+task+activity分组统计）

**不包含的功能**（由Flowable管理）：
- ❌ 草稿状态管理
- ❌ 提交状态管理
- ❌ 审批状态管理
- ❌ 审批人分配
- ❌ 审批流程路由

---

## 3. 架构设计决策

### 3.1 技术栈选择

**前端技术栈（与SPMS保持一致）**:
- React 18.3.1
- Material-UI v7
- React Router v7
- React Context（状态管理）
- Axios（HTTP客户端）

### 3.2 自定义组件模式

**采用方式：FormRuntimeSwitch自定义组件模式**

```javascript
// TaskDetailsDialog.jsx
<FormRuntimeSwitch
  schema={{
    type: 'custom',
    componentKey: 'TimecardForm',       // 自定义组件注册名
    initialData: formData,              // 来自processInstance.contextValue
    props: { mode: 'edit' }             // 传递给组件的props
  }}
  onSubmit={(updatedData) => {
    // Save按钮触发，仅更新state
    setFormData(updatedData);
  }}
/>
```

**注册方式**:
```javascript
// custom-registry.jsx
export const customFormRegistry = {
  TimecardForm: TimecardForm,  // 注册为自定义组件
  // 其他自定义组件...
}
```

**优势**：
- ✅ 完全符合现有FormRuntimeSwitch架构
- ✅ 无需修改流程引擎代码
- ✅ 与TaskDetailsDialog无缝集成
- ✅ 数据流清晰（props in → onSubmit out）
- ✅ 支持readOnly模式（审批时）

### 3.3 数据流设计

```
TaskDetailsDialog (State管理)
    ↓ initialData={formData}
TimecardForm (Pure Component)
    ← onSubmit(updatedData)
TaskDetailsDialog.setFormData(updatedData)
    ↓ 点击Complete按钮
onComplete({ formData: updatedData })
    ↓
Flowable引擎接收
    ↓
下一个任务节点
    ↓
TaskDetailsDialog (审批员)
    ↓ initialData={formData} (只读)
TimecardForm (approval模式)
```

### 3.4 组件职责划分

**TimecardForm（Pure React Component）**:
- 接收`initialData`（Timecard结构）
- 在内部state管理编辑状态
- 调用`onSubmit(updatedData)`传递完整Timecard数据
- 支持`readOnly`模式（审批时只读）
- 不直接调用任何API，不保存到后端
- 不管理状态（draft/submitted等）

**TaskDetailsDialog（状态管理）**:
- 维护`formData` state
- 从`processInstance.contextValue.formData`读取初始数据
- 传递给TimecardForm的`initialData`
- 接收TimecardForm的`onSubmit`更新state
- 点击Complete提交到Flowable

**API Mock（开发支持）**:
- 提供项目列表、节假日等静态数据
- 实际不调用（仅用于初始数据获取）

---

## 4. 数据模型

### 4.1 顶层数据结构（保存在formData中）

```typescript
interface TimecardFormData {
  // 员工信息（复合对象，区分填报和审批）
  employee_info: {
    staff_id: string;
    staff_name_chinese: string;
    staff_name_english: string;
    team: string;
    department: string;
    department_head: string;
    local_manager?: string;
    functional_manager?: string;
    staff_type: 'PERMANENT' | 'VENDOR';
  };

  // 工时卡信息
  timecard_month: string; // YYYY-MM

  // 工时条目（用户填写）
  timecard_entries: TimecardEntry[];

  // 统计信息（自动计算）
  summary: {
    total_hours: number;
    project_summary: ProjectSummary[]; // 按project+task+activity分组
    warnings: string[]; // 'EXCEED_DAILY_LIMIT', 'EXCEED_MONTHLY_LIMIT'
  };

  // 系统字段
  version: number;
  created_by: string;
  created_at: number;
  updated_by?: string;
  updated_at?: number;
  submitted?: boolean;
}

// ==================== 员工信息子结构 ====================

export interface EmployeeInfo {
  staff_id: string;
  staff_name_chinese: string;
  staff_name_english: string;
  team: string;
  department: string;
  department_head: string;
  local_manager?: string;
  functional_manager?: string;
  staff_type: 'PERMANENT' | 'VENDOR';
}
```

**重要设计决策**：
- `employee_info`作为独立对象封装，便于区分填报人和审批人视角
- 审批员打开时，`employee_info`应为原始填报人的信息（从formData读取）
- `timecard_entries`重命名以明确数据含义
- `submitted`标记用于流程状态判断

### 4.2 工时条目结构

```typescript
export interface TimecardEntry {
  // 标识
  id: string;
  date: string; // YYYY-MM-DD

  // 项目信息（用户填写）
  project_code: string;    // 项目代码（手动输入或选择）
  project_name: string;    // 自动填充
  task_number: string;     // 任务编号（选择）
  activity: string;        // 活动（自动填充）

  // 工时信息
  claim_type: 'NORMAL' | 'LEAVE' | 'OT_WORKING_DAYS' | 'OT_HOLIDAYS' | 'OT_STATUTORY_HOLIDAYS';
  hours: number;           // 工时（0-24，精度0.5）
  remark?: string;         // 备注

  // 验证信息（前端计算）
  warnings?: string[];     // 该条目的警告
}
```

### 4.3 项目汇总结构（自动计算）

```typescript
interface ProjectSummary {
  project_code: string;
  project_name: string;
  task_number: string;
  activity: string;
  claim_type: string;
  total_hours: number;
}
```

**重要**：整个TimecardFormData对象保存在`processInstance.contextValue.formData`中

---

## 5. Mock数据策略

### 5.1 目录结构

```
frontend/src/
├── data/
│   └── timecard/
│       ├── mockSchemas.ts      # TypeScript接口定义
│       └── mockData.ts         # Mock数据源
└── api/
    └── timecard/
        ├── timecardService.js    # API服务（少量调用）
        └── timecardMock.js       # Mock拦截器
```

### 5.2 Mock数据说明

**仅在初始数据加载时使用**（第一次打开表单）:
- 当前员工信息（从session/mock获取）
- 项目列表（可选，用于Project Code选择器）
- 节假日列表（用于日历高亮）

**实际不调用API的场景**:
- 保存数据（仅在state中更新）
- 提交审批（通过Flowable完成）
- 审批操作（在Flowable任务中完成）

### 5.3 Mock数据文件示例

```typescript
// mockData.ts - 完整数据结构示例

export const mockTimecardData: TimecardFormData = {
  // 员工信息（复合对象）
  employee_info: {
    staff_id: 'EMP001',
    staff_name_chinese: '张伟',
    staff_name_english: 'Zhang Wei',
    team: '开发团队A',
    department: 'IT技术部',
    department_head: '部门经理',
    local_manager: '本地经理王',
    functional_manager: '职能经理李',
    staff_type: 'PERMANENT'
  },

  // 工时卡信息
  timecard_month: '2025-11',

  // 工时条目（示例数据，实际初始为空）
  timecard_entries: [
    {
      id: 'entry-001',
      date: '2025-11-03',
      project_code: 'PROJ001',
      project_name: '电商平台重构项目',
      task_number: 'T001',
      activity: '开发',
      claim_type: 'NORMAL',
      hours: 8,
      remark: '用户界面开发'
    }
  ],

  // 统计信息（自动计算）
  summary: {
    total_hours: 84,
    project_summary: [
      {
        project_code: 'PROJ001',
        project_name: '电商平台重构项目',
        task_number: 'T001',
        activity: '开发',
        claim_type: 'NORMAL',
        total_hours: 26
      }
    ],
    warnings: []
  },

  // 系统字段
  version: 1,
  created_by: 'EMP001',
  created_at: 1731292800000,
  updated_by: 'EMP001',
  updated_at: 1731379200000,
  submitted: false
};
```

**员工信息Mock数据**（用于初次填报）：

```typescript
export const mockEmployeeData: EmployeeInfo = {
  staff_id: 'EMP001',
  staff_name_chinese: '张伟',
  staff_name_english: 'Zhang Wei',
  team: '开发团队A',
  department: 'IT技术部',
  department_head: '部门经理',
  local_manager: '本地经理王',
  functional_manager: '职能经理李',
  staff_type: 'PERMANENT'
};
```

---

## 6. 组件设计

### 6.1 目录结构

```
frontend/src/
├── components/
│   └── timecard/
│       ├── TimecardForm.jsx           # 主表单组件（自定义组件入口）
│       ├── CalendarView.jsx           # 日历视图组件
│       ├── TimecardSummary.jsx        # 工时汇总组件
│       ├── ProjectEntryDialog.jsx     # 项目条目对话框
│       ├── EmployeeInfo.jsx           # 员工信息展示组件
│       ├── BulkOperations.jsx         # 批量操作组件
│       ├── ProjectSelector.jsx        # 项目选择器组件
│       └── hooks/
│           └── useTimecardValidation.js # 验证Hook
│           └── useProjectData.js       # 项目数据Hook
```

### 6.2 主表单组件 (TimecardForm.jsx)

```jsx
/**
 * Timecard表单主组件
 * 作为自定义组件在Flowable流程中使用
 *
 * 业务逻辑：
 * 1. 从initialData读取Timecard数据（来自processInstance.contextValue.formData）
 * 2. 用户在表单内编辑数据
 * 3. 点击Save按钮 → 触发onSubmit(updatedData) → 更新TaskDetailsDialog的formData状态
 * 4. 点击TaskDetailsDialog的Complete按钮 → 数据进入Flowable引擎 → 传递给审批员
 */

import React, { useState, useEffect } from 'react';
import {
  Box, CircularProgress, Alert, Snackbar,
  Button, Divider
} from '@mui/material';
import { Save as SaveIcon } from '@mui/icons-material';

import EmployeeInfo from './EmployeeInfo';
import CalendarView from './CalendarView';
import TimecardSummary from './TimecardSummary';
import ProjectEntryDialog from './ProjectEntryDialog';
import timecardService from '../../api/timecard/timecardService';

// 工具函数
import {
  getEntriesForDate,
  calculateProjectSummary,
  validateTimecard
} from '../../api/timecard/timecardUtils';

const TimecardForm = ({
  mode = 'edit',           // 'edit', 'view', 'approval' - 由流程节点配置决定
  initialData,             // 来自processInstance.contextValue.formData
  onSubmit,                // Save按钮触发，更新formData状态
  readOnly = false         // 只读模式（审批时）
}) => {
  const [timecardData, setTimecardData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [errors, setErrors] = useState({});
  const [warnings, setWarnings] = useState([]);
  const [dialogOpen, setDialogOpen] = useState(false);
  const [selectedDate, setSelectedDate] = useState(null);
  const [saveSuccess, setSaveSuccess] = useState(false);

  // 初始化数据
  useEffect(() => {
    if (initialData && initialData.timecard_month) {
      // 如果initialData已有数据，直接使用（审批场景）
      setTimecardData(initialData);
      setLoading(false);
    } else {
      // 如果没有数据，加载初始数据（填报场景）
      loadInitialData();
    }
  }, [initialData]);

  // 验证逻辑
  useEffect(() => {
    if (timecardData) {
      const validation = validateTimecard(timecardData);
      setWarnings(validation.warnings);
      setErrors(validation.errors);
    }
  }, [timecardData]);

  const loadInitialData = async () => {
    try {
      setLoading(true);

      // 员工信息加载优先级：formData.employee_info > mockAPI
      // 确保审批员看到填报人的信息
      const employeeInfo = initialData?.employee_info || await timecardService.getCurrentEmployee();

      const projects = await timecardService.getProjectOptions();
      const holidays = await timecardService.getHolidays(2025);

      // 创建初始数据
      const initialTimecardData = {
        // 员工信息（从formData或mockAPI获取）
        employee_info: employeeInfo,

        // 工时卡信息
        timecard_month: new Date().toISOString().slice(0, 7),

        // 初始空条目
        timecard_entries: [],

        // 初始统计
        summary: {
          total_hours: 0,
          project_summary: [],
          warnings: []
        },

        // 系统字段
        version: 1,
        created_by: employeeInfo.staff_id,
        created_at: Date.now()
      };

      setTimecardData(initialTimecardData);
    } catch (error) {
      console.error('加载初始数据失败:', error);
      setErrors({ load: '加载初始数据失败，请刷新页面重试' });
    } finally {
      setLoading(false);
    }
  };

  // 处理日期点击
  const handleDateClick = (dateStr) => {
    if (readOnly) return;

    setSelectedDate(dateStr);
    setDialogOpen(true);
  };

  // 关闭对话框
  const handleDialogClose = () => {
    setDialogOpen(false);
  };

  // 保存项目条目
  const handleEntriesSave = (updatedEntries) => {
    // 更新条目
    const newEntries = timecardData.timecard_entries.filter(e => e.date !== selectedDate);
    newEntries.push(...updatedEntries);

    // 重新计算汇总
    const projectSummary = calculateProjectSummary(newEntries);
    const totalHours = newEntries.reduce((sum, entry) => sum + entry.hours, 0);

    const updatedTimecardData = {
      ...timecardData,
      timecard_entries: newEntries,
      summary: {
        total_hours: totalHours,
        project_summary: projectSummary,
        warnings: []
      }
    };

    setTimecardData(updatedTimecardData);
  };

  // 保存Timecard（点击Save按钮）
  const handleSave = () => {
    if (!timecardData) {
      setErrors({ general: '没有可保存的数据' });
      return;
    }

    // 验证至少有一条记录
    if (timecardData.timecard_entries.length === 0) {
      setErrors({ general: '请至少填写一条工时记录' });
      return;
    }

    // 调用onSubmit回调，将更新后的数据传递给TaskDetailsDialog
    // TaskDetailsDialog会更新其formData state
    onSubmit(timecardData);

    // 显示成功提示
    setSaveSuccess(true);
    setTimeout(() => setSaveSuccess(false), 3000);
  };

  // 渲染加载状态
  if (loading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', p: 4 }}>
        <CircularProgress />
      </Box>
    );
  }

  // 渲染错误状态
  if (errors.load || errors.general) {
    return (
      <Box sx={{ p: 4 }}>
        <Alert severity="error">
          {errors.load || errors.general}
        </Alert>
      </Box>
    );
  }

  return (
    <Box sx={{ p: 3 }}>
      {/* 员工信息（顶部卡片） */}
      <EmployeeInfo staff={timecardData.employee_info} />

      <Divider sx={{ my: 3 }} />

      {/* 警告信息（如果有） */}
      {warnings.length > 0 && (
        <Alert severity="warning" sx={{ mb: 2 }}>
          存在工时异常，请检查
        </Alert>
      )}

      {/* 工时汇总（中间卡片） */}
      <TimecardSummary summary={timecardData.summary} />

      <Divider sx={{ my: 3 }} />

      {/* 日历视图（主要交互区域） */}
      <CalendarView
        entries={timecardData.timecard_entries}
        month={timecardData.timecard_month}
        holidays={timecardData.holidays || []}
        readOnly={readOnly}
        onDateClick={handleDateClick}
      />

      {/* 保存按钮（底部，居中） */}
      {!readOnly && (
        <Box sx={{ mt: 3, display: 'flex', justifyContent: 'center' }}>
          <Button
            variant="contained"
            size="large"
            startIcon={<SaveIcon />}
            onClick={handleSave}
            disabled={timecardData.timecard_entries.length === 0}
          >
            保存工时卡
          </Button>
        </Box>
      )}

      {/* 保存成功提示 */}
      <Snackbar
        open={saveSuccess}
        autoHideDuration={3000}
        message="工时卡已保存"
        anchorOrigin={{ vertical: 'bottom', horizontal: 'center' }}
      />

      {/* 项目条目对话框（点击日期弹出） */}
      <ProjectEntryDialog
        open={dialogOpen}
        date={selectedDate}
        entries={selectedDate ? getEntriesForDate(timecardData.timecard_entries, selectedDate) : []}
        onClose={handleDialogClose}
        onSave={handleEntriesSave}
        readOnly={readOnly}
      />
    </Box>
  );
};

export default TimecardForm;
```

### 6.3 EmployeeInfo 组件 (EmployeeInfo.jsx)

```jsx
/**
 * 员工信息展示组件
 * 功能：显示员工的基本信息卡片
 */

import React from 'react';
import {
  Card, CardHeader, CardContent,
  Grid, Typography, Box
} from '@mui/material';
import { Person as PersonIcon, Business as BusinessIcon } from '@mui/icons-material';

const EmployeeInfo = ({ staff }) => {
  if (!staff) return null;

  return (
    <Card>
      <CardHeader
        title={
          <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
            <PersonIcon color="primary" />
            <Typography variant="h6">员工信息</Typography>
          </Box>
        }
      />
      <CardContent>
        <Grid container spacing={3}>
          <Grid item xs={12} md={6} lg={4}>
            <Typography variant="body2" color="text.secondary">员工ID</Typography>
            <Typography variant="body1" fontWeight="bold">{staff.staff_id}</Typography>
          </Grid>
          <Grid item xs={12} md={6} lg={4}>
            <Typography variant="body2" color="text.secondary">中文姓名</Typography>
            <Typography variant="body1" fontWeight="bold">{staff.staff_name_chinese}</Typography>
          </Grid>
          <Grid item xs={12} md={6} lg={4}>
            <Typography variant="body2" color="text.secondary">英文姓名</Typography>
            <Typography variant="body1" fontWeight="bold">{staff.staff_name_english}</Typography>
          </Grid>
          <Grid item xs={12} md={6} lg={4}>
            <Typography variant="body2" color="text.secondary">团队</Typography>
            <Typography variant="body1">{staff.team}</Typography>
          </Grid>
          <Grid item xs={12} md={6} lg={4}>
            <Typography variant="body2" color="text.secondary">部门</Typography>
            <Typography variant="body1">{staff.department}</Typography>
          </Grid>
          <Grid item xs={12} md={6} lg={4}>
            <Typography variant="body2" color="text.secondary">部门负责人</Typography>
            <Typography variant="body1">{staff.department_head}</Typography>
          </Grid>
        </Grid>
      </CardContent>
    </Card>
  );
};

export default EmployeeInfo;
```

### 6.4 CalendarView 组件 (CalendarView.jsx)

```jsx
/**
 * 日历视图组件
 * 功能：月视图展示、颜色编码、项目条目显示、批量操作
 */

import React, { useState } from 'react';
import {
  Card, CardHeader, CardContent,
  Grid, Typography, Box, IconButton,
  Menu, MenuItem, Button
} from '@mui/material';
import {
  ContentCopy as CopyIcon,
  ChevronLeft as ChevronLeftIcon,
  ChevronRight as ChevronRightIcon
} from '@mui/icons-material';
import { formatDate } from 'date-fns';

const CalendarView = ({
  entries,
  currentMonth = new Date(),
  holidays = [],
  readOnly = false,
  onDateClick
}) => {
  const [anchorEl, setAnchorEl] = useState(null);
  const [bulkCopyDate, setBulkCopyDate] = useState(null);
  const [displayMonth, setDisplayMonth] = useState(currentMonth);

  // 获取当月天数
  const getDaysInMonth = (date) => {
    return new Date(date.getFullYear(), date.getMonth() + 1, 0).getDate();
  };

  // 获取当月第一天是星期几
  const getFirstDayOfMonth = (date) => {
    return new Date(date.getFullYear(), date.getMonth(), 1).getDay();
  };

  // 格式化日期字符串
  const formatDateString = (year, month, day) => {
    const m = String(month + 1).padStart(2, '0');
    const d = String(day).padStart(2, '0');
    return `${year}-${m}-${d}`;
  };

  // 获取指定日期的条目
  const getEntriesForDate = (dateStr) => {
    return entries.filter(entry => entry.date === dateStr);
  };

  // 判断是否为节假日
  const isHoliday = (dateStr) => {
    return holidays.find(h => h.date === dateStr);
  };

  // 判断是否为周末
  const isWeekend = (dateStr) => {
    const day = new Date(dateStr).getDay();
    return day === 0 || day === 6;
  };

  // 获取日期背景色
  const getDateBackground = (dateStr, dayEntries) => {
    const isHol = isHoliday(dateStr);
    const totalHours = dayEntries.reduce((sum, entry) => sum + entry.hours, 0);
    const hasExcess = totalHours > 8;
    const isWknd = isWeekend(dateStr);

    if (isHol) {
      return 'success.light';      // 节假日：绿色
    } else if (hasExcess || isWknd) {
      return 'error.light';        // 超时或周末：红色
    }
    return 'background.paper';     // 正常：白色
  };

  // 处理日期点击
  const handleDateClick = (day, event) => {
    if (readOnly) return;

    const dateStr = formatDateString(
      displayMonth.getFullYear(),
      displayMonth.getMonth(),
      day
    );

    onDateClick(dateStr);
  };

  // 处理批量复制
  const handleBulkCopy = (type) => {
    if (!bulkCopyDate) return;

    const sourceEntries = getEntriesForDate(bulkCopyDate);
    if (sourceEntries.length === 0) return;

    // 计算目标日期
    let targetDates = [];
    const year = displayMonth.getFullYear();
    const month = displayMonth.getMonth();

    switch (type) {
      case 'this-week':
        // 复制到本周所有空白日
        const sourceDate = new Date(bulkCopyDate);
        const dayOfWeek = sourceDate.getDay();
        const startOfWeek = new Date(sourceDate);
        startOfWeek.setDate(sourceDate.getDate() - dayOfWeek);

        for (let i = 0; i < 7; i++) {
          const targetDate = new Date(startOfWeek);
          targetDate.setDate(startOfWeek.getDate() + i);
          const dateStr = formatDateString(targetDate.getFullYear(), targetDate.getMonth(), targetDate.getDate());

          // 只复制到空白日
          if (getEntriesForDate(dateStr).length === 0) {
            targetDates.push(dateStr);
          }
        }
        break;

      case 'workdays':
        // 复制到所有工作日（周一到周五）
        const daysInMonth = getDaysInMonth(displayMonth);
        for (let day = 1; day <= daysInMonth; day++) {
          const dateStr = formatDateString(year, month, day);
          const dayOfWeek = new Date(dateStr).getDay();

          // 只复制到工作日且空白
          if (dayOfWeek >= 1 && dayOfWeek <= 5 && getEntriesForDate(dateStr).length === 0) {
            targetDates.push(dateStr);
          }
        }
        break;

      case 'empty-days':
        // 复制到所有空白日
        const totalDays = getDaysInMonth(displayMonth);
        for (let day = 1; day <= totalDays; day++) {
          const dateStr = formatDateString(year, month, day);
          if (getEntriesForDate(dateStr).length === 0) {
            targetDates.push(dateStr);
          }
        }
        break;
    }

    // 为每个目标日期创建相同的条目
    let newEntries = [...entries];
    targetDates.forEach(targetDate => {
      sourceEntries.forEach(sourceEntry => {
        newEntries.push({
          ...sourceEntry,
          id: `entry-${Date.now()}-${Math.random()}`,
          date: targetDate
        });
      });
    });

    // 触发回调，更新数据（通过TimecardForm的状态管理）
    onBulkCopy(newEntries);
    setAnchorEl(null);
  };

  // 打开批量操作菜单
  const openBulkMenu = (event, dateStr) => {
    setAnchorEl(event.currentTarget);
    setBulkCopyDate(dateStr);
  };

  const daysInMonth = getDaysInMonth(displayMonth);
  const firstDay = getFirstDayOfMonth(displayMonth);
  const year = displayMonth.getFullYear();
  const month = displayMonth.getMonth();

  return (
    <Card>
      <CardHeader
        title={
          <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
            <Typography variant="h6">日历视图</Typography>
            <Typography variant="body2" color="text.secondary">
              ({displayMonth.getFullYear()}年{displayMonth.getMonth() + 1}月)
            </Typography>
          </Box>
        }
        action={
          <Box sx={{ display: 'flex', gap: 1 }}>
            <IconButton
              size="small"
              onClick={() => setDisplayMonth(prev =>
                new Date(prev.getFullYear(), prev.getMonth() - 1, 1)
              )}
            >
              <ChevronLeftIcon />
            </IconButton>
            <IconButton
              size="small"
              onClick={() => setDisplayMonth(prev =>
                new Date(prev.getFullYear(), prev.getMonth() + 1, 1)
              )}
            >
              <ChevronRightIcon />
            </IconButton>
          </Box>
        }
      />

      <CardContent>
        {/* 图例说明 */}
        <Box sx={{ display: 'flex', gap: 3, mb: 2, p: 2, bgcolor: 'grey.50', borderRadius: 1 }}>
          <LegendItem color="success.light" label="节假日" />
          <LegendItem color="error.light" label="超时/周末" />
          <LegendItem color="primary.light" label="项目占用" />
        </Box>

        {/* 日历网格 */}
        <Grid container spacing={0} sx={{
          border: 1,
          borderColor: 'grey.200',
          borderRadius: 1,
          overflow: 'hidden'
        }}>
          {/* 星期标题 */}
          {['日', '一', '二', '三', '四', '五', '六'].map(day => (
            <Grid
              item
              xs
              key={day}
              sx={{
                bgcolor: 'grey.100',
                p: 2,
                textAlign: 'center',
                borderBottom: 1,
                borderColor: 'grey.200',
                fontWeight: 'bold'
              }}
            >
              <Typography variant="body2" color="text.secondary">
                {day}
              </Typography>
            </Grid>
          ))}

          {/* 空白填充（月初） */}
          {Array.from({ length: firstDay }).map((_, i) => (
            <Grid
              item
              xs
              key={`empty-${i}`}
              sx={{
                aspectRatio: '1',
                border: 1,
                borderColor: 'grey.100'
              }}
            />
          ))}

          {/* 日期单元格 */}
          {Array.from({ length: daysInMonth }, (_, i) => i + 1).map(day => {
            const dateStr = formatDateString(year, month, day);
            const dayEntries = getEntriesForDate(dateStr);
            const bgColor = getDateBackground(dateStr, dayEntries);
            const totalHours = dayEntries.reduce((sum, entry) => sum + entry.hours, 0);
            const hasEntries = dayEntries.length > 0;

            return (
              <Grid
                item
                xs
                key={day}
                onClick={(e) => handleDateClick(day, e)}
                sx={{
                  aspectRatio: '1',
                  border: 1,
                  borderColor: 'grey.100',
                  p: 1,
                  cursor: readOnly ? 'default' : 'pointer',
                  bgcolor: bgColor,
                  '&:hover': {
                    borderColor: 'grey.400',
                    boxShadow: !readOnly ? 1 : 'none'
                  },
                  display: 'flex',
                  flexDirection: 'column',
                  transition: 'all 0.2s'
                }}
              >
                {/* 日期和操作 */}
                <Box sx={{
                  display: 'flex',
                  justifyContent: 'space-between',
                  alignItems: 'flex-start',
                  mb: 0.5
                }}>
                  <Typography variant="body2" fontWeight="bold">
                    {day}
                  </Typography>

                  {/* 批量操作按钮（悬停显示） */}
                  {hasEntries && !readOnly && (
                    <IconButton
                      size="small"
                      sx={{
                        opacity: 0,
                        '&:hover': { opacity: 1 }
                      }}
                      onClick={(e) => {
                        e.stopPropagation();
                        openBulkMenu(e, dateStr);
                      }}
                    >
                      <CopyIcon fontSize="small" />
                    </IconButton>
                  )}
                </Box>

                {/* 工时总计 */}
                {totalHours > 0 && (
                  <Typography
                    variant="caption"
                    sx={{
                      bgcolor: 'primary.main',
                      color: 'white',
                      px: 0.5,
                      py: 0.25,
                      borderRadius: 1,
                      fontWeight: 'bold',
                      mb: 0.5,
                      alignSelf: 'flex-start'
                    }}
                  >
                    {totalHours.toFixed(1)}h
                  </Typography>
                )}

                {/* 项目条目（最多显示2个） */}
                <Box sx={{ flex: 1, overflow: 'hidden' }}>
                  {dayEntries.slice(0, 2).map((entry, idx) => (
                    <Box
                      key={`${entry.id}-${idx}`}
                      sx={{
                        fontSize: '0.7rem',
                        px: 0.5,
                        py: 0.25,
                        mb: 0.25,
                        borderRadius: 1,
                        border: 1,
                        borderColor: 'primary.light',
                        bgcolor: 'primary.light',
                        color: 'primary.contrastText',
                        overflow: 'hidden'
                      }}
                    >
                      <Box sx={{ display: 'flex', justifyContent: 'space-between' }}>
                        <Typography variant="caption" noWrap sx={{ fontWeight: 'bold' }}>
                          {entry.project_code}
                        </Typography>
                        <Typography variant="caption">
                          {entry.hours.toFixed(1)}h
                        </Typography>
                      </Box>
                      {entry.claim_type !== 'NORMAL' && (
                        <Typography variant="caption" sx={{ fontStyle: 'italic' }}>
                          {entry.claim_type.replace(/_/g, ' ')}
                        </Typography>
                      )}
                    </Box>
                  ))}

                  {dayEntries.length > 2 && (
                    <Typography variant="caption" color="text.secondary" sx={{ fontStyle: 'italic' }}>
                      +{dayEntries.length - 2} more
                    </Typography>
                  )}
                </Box>
              </Grid>
            );
          })}
        </Grid>
      </CardContent>

      {/* 批量操作菜单 */}
      <Menu
        anchorEl={anchorEl}
        open={Boolean(anchorEl)}
        onClose={() => setAnchorEl(null)}
      >
        <MenuItem onClick={() => handleBulkCopy('this-week')}>
          <CopyIcon sx={{ mr: 1 }} />
          复制到本周空白日
        </MenuItem>
        <MenuItem onClick={() => handleBulkCopy('workdays')}>
          <CopyIcon sx={{ mr: 1 }} />
          复制到所有工作日
        </MenuItem>
        <MenuItem onClick={() => handleBulkCopy('empty-days')}>
          <CopyIcon sx={{ mr: 1 }} />
          复制到所有空白日
        </MenuItem>
      </Menu>
    </Card>
  );
};

// 图例项组件
const LegendItem = ({ color, label }) => (
  <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
    <Box
      sx={{
        width: 16,
        height: 16,
        bgcolor: color,
        border: 1,
        borderColor: 'grey.300',
        borderRadius: 0.5
      }}
    />
    <Typography variant="body2">{label}</Typography>
  </Box>
);

export default CalendarView;
```

### 6.5 TimecardSummary 组件 (TimecardSummary.jsx)

```jsx
/**
 * 工时汇总组件
 * 功能：实时统计项目工时，显示警告信息
 */

import React, { useState, useEffect } from 'react';
import {
  Card, CardHeader, CardContent,
  Table, TableBody, TableCell, TableContainer,
  TableHead, TableRow, Paper,
  Typography, Box, Alert, AlertTitle, Grid
} from '@mui/material';
import { Warning as WarningIcon } from '@mui/icons-material';
import { format } from 'date-fns';

const TimecardSummary = ({ summary }) => {
  // 计算警告状态
  const [showMonthlyWarning, setShowMonthlyWarning] = useState(false);

  useEffect(() => {
    if (summary.total_hours > 160) {
      setShowMonthlyWarning(true);
    } else {
      setShowMonthlyWarning(false);
    }
  }, [summary.total_hours]);

  // 如果没有项目数据，显示友好提示
  if (!summary.project_summary || summary.project_summary.length === 0) {
    return (
      <Card>
        <CardHeader title="工时汇总" />
        <CardContent>
          <Typography variant="body1" color="text.secondary" align="center" sx={{ py: 4 }}>
            暂无工时记录
          </Typography>
        </CardContent>
      </Card>
    );
  }

  return (
    <Card>
      <CardHeader
        title={
          <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <Typography variant="h6">工时汇总</Typography>
            <Box sx={{ textAlign: 'right' }}>
              <Typography variant="h4" color="primary">
                {summary.total_hours.toFixed(1)}h
              </Typography>
              <Typography variant="caption" color="text.secondary">
                总工时
              </Typography>
            </Box>
          </Box>
        }
      />

      <CardContent>
        {/* 月度警告 */}
        {showMonthlyWarning && (
          <Alert severity="warning" sx={{ mb: 2 }}>
            <AlertTitle>月度工时警告</AlertTitle>
            本月总工时 ({summary.total_hours.toFixed(1)}h) 已超过160小时限制
          </Alert>
        )}

        {/* 汇总表格 */}
        <TableContainer component={Paper} variant="outlined">
          <Table size="small">
            <TableHead>
              <TableRow sx={{ bgcolor: 'grey.50' }}>
                <TableCell>项目代码</TableCell>
                <TableCell>项目名称</TableCell>
                <TableCell>任务</TableCell>
                <TableCell>活动</TableCell>
                <TableCell>工时类型</TableCell>
                <TableCell align="right">总工时</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {summary.project_summary.map((row, idx) => (
                <TableRow
                  key={idx}
                  hover
                  sx={{ '&:last-child td, &:last-child th': { border: 0 } }}
                >
                  <TableCell sx={{ fontWeight: 'bold' }}>{row.project_code}</TableCell>
                  <TableCell>{row.project_name}</TableCell>
                  <TableCell>{row.task_number}</TableCell>
                  <TableCell>{row.activity}</TableCell>
                  <TableCell>
                    <Typography
                      variant="caption"
                      sx={{
                        px: 1,
                        py: 0.5,
                        borderRadius: 1,
                        bgcolor:
                          row.claim_type === 'NORMAL' ? 'grey.100' :
                          row.claim_type.startsWith('OT_') ? 'error.light' : 'warning.light'
                      }}
                    >
                      {getClaimTypeLabel(row.claim_type)}
                    </Typography>
                  </TableCell>
                  <TableCell align="right" sx={{ fontWeight: 'bold' }}>
                    {row.total_hours.toFixed(1)}h
                  </TableCell>
                </TableRow>
              ))}

              {/* 总计行 */}
              <TableRow sx={{ bgcolor: 'grey.50' }}>
                <TableCell colSpan={5}>
                  <Typography fontWeight="bold" textAlign="right">
                    总计
                  </Typography>
                </TableCell>
                <TableCell align="right">
                  <Typography fontWeight="bold" color="primary">
                    {summary.total_hours.toFixed(1)}h
                  </Typography>
                </TableCell>
              </TableRow>
            </TableBody>
          </Table>
        </TableContainer>

        {/* 统计信息 */}
        <Box sx={{ mt: 2, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <Typography variant="body2" color="text.secondary">
            共 {summary.project_summary.length} 个项目任务组合
          </Typography>
          <Box sx={{ display: 'flex', gap: 2 }}>
            <Typography variant="body2">
              正常工时: {calculateHoursByType('NORMAL')}h
            </Typography>
            <Typography variant="body2">
              加班工时: {calculateHoursByType('OT_')}h
            </Typography>
          </Box>
        </Box>
      </CardContent>
    </Card>
  );
};

// 获取工时类型标签
const getClaimTypeLabel = (claimType) => {
  const labels = {
    NORMAL: '正常工时',
    LEAVE: '请假',
    OT_WORKING_DAYS: '加班-工作日',
    OT_HOLIDAYS: '加班-节假日',
    OT_STATUTORY_HOLIDAYS: '加班-法定节假日'
  };
  return labels[claimType] || claimType;
};

// 计算按类型的工时（需要在组件内部使用summary数据）
// 这里简化为在useEffect中计算
export default TimecardSummary;
```

### 6.6 ProjectEntryDialog 组件 (ProjectEntryDialog.jsx)

```jsx
/**
 * 项目条目对话框组件
 * 功能：添加/编辑项目工时条目、自动填充项目信息
 */

import React, { useState, useEffect } from 'react';
import {
  Dialog, DialogTitle, DialogContent, DialogActions,
  Card, CardHeader, CardContent,
  TextField, FormControl, InputLabel, Select, MenuItem,
  Grid, Box, Typography, IconButton, Button,
  Alert, Chip, Autocomplete
} from '@mui/material';
import {
  Add as AddIcon,
  Delete as DeleteIcon,
  Search as SearchIcon
} from '@mui/icons-material';
import timecardService from '../../api/timecard/timecardService';
import { generateEntryId } from '../../api/timecard/timecardUtils';

const ProjectEntryDialog = ({
  open,
  onClose,
  date,
  entries = [],
  readOnly = false,
  onSave,
  projects = []
}) => {
  const [localEntries, setLocalEntries] = useState([]);
  const [projectOptions, setProjectOptions] = useState([]);
  const [taskOptions, setTaskOptions] = useState({});
  const [errors, setErrors] = useState({});
  const [loading, setLoading] = useState(false);

  // 初始化条目
  useEffect(() => {
    if (open) {
      setLocalEntries(
        entries.length > 0
          ? entries.map(e => ({ ...e, isSaved: true }))
          : [createEmptyEntry()]
      );
      loadProjectOptions();
    }
  }, [open, entries]);

  const createEmptyEntry = () => ({
    id: generateEntryId(),
    date,
    project_code: '',
    project_name: '',
    task_number: '',
    activity: '',
    claim_type: 'NORMAL',
    hours: '',
    remark: '',
    isSaved: false
  });

  // 加载项目选项
  const loadProjectOptions = async () => {
    try {
      setLoading(true);
      const response = await timecardService.getProjectOptions();
      setProjectOptions(response.data || []);
    } catch (error) {
      console.error('加载项目选项失败:', error);
    } finally {
      setLoading(false);
    }
  };

  // 处理项目代码变更
  const handleProjectCodeChange = async (index, code) => {
    const updatedEntries = [...localEntries];
    updatedEntries[index].project_code = code;

    // 验证项目代码并自动填充
    if (code) {
      try {
        const response = await timecardService.validateProjectCode(code);
        if (response.success && response.data) {
          const project = response.data;
          updatedEntries[index].project_name = project.project_name;

          // 加载任务选项
          const taskResponse = await timecardService.getTaskOptions(code);
          setTaskOptions(prev => ({
            ...prev,
            [code]: taskResponse.data || []
          }));

          // 如果只有一个任务，自动选择
          if (project.tasks && project.tasks.length === 1) {
            const firstTask = project.tasks[0];
            updatedEntries[index].task_number = firstTask.task_number;
            updatedEntries[index].activity = firstTask.activity;
            updatedEntries[index].isSaved = false;
          }
        } else {
          updatedEntries[index].project_name = '';
          setErrors(prev => ({
            ...prev,
            [`${index}-project_code`]: '项目代码不存在'
          }));
        }
      } catch (error) {
        console.error('验证项目代码失败:', error);
      }
    }

    setLocalEntries(updatedEntries);
  };

  // 处理字段变更
  const handleFieldChange = (index, field, value) => {
    const updatedEntries = [...localEntries];

    // 工时字段验证
    if (field === 'hours') {
      const numValue = parseFloat(value);
      if (isNaN(numValue) || numValue < 0 || numValue > 24) {
        setErrors(prev => ({
          ...prev,
          [`${index}-hours`]: '工时必须在0-24之间'
        }));
      } else {
        setErrors(prev => {
          const newErrors = { ...prev };
          delete newErrors[`${index}-hours`];
          return newErrors;
        });
      }
    }

    updatedEntries[index][field] = value;
    updatedEntries[index].isSaved = false;
    setLocalEntries(updatedEntries);
  };

  // 添加新条目
  const handleAddEntry = () => {
    setLocalEntries([...localEntries, createEmptyEntry()]);
  };

  // 删除条目
  const handleRemoveEntry = (index) => {
    if (localEntries.length <= 1) {
      setErrors({ general: '至少需要保留一个条目' });
      return;
    }
    setLocalEntries(localEntries.filter((_, i) => i !== index));
  };

  // 验证所有条目
  const validateEntries = () => {
    const newErrors = {};

    localEntries.forEach((entry, index) => {
      // 验证必填字段
      if (!entry.project_code) {
        newErrors[`${index}-project_code`] = '项目代码必填';
      }
      if (!entry.task_number) {
        newErrors[`${index}-task_number`] = '任务编号必填';
      }
      if (!entry.hours || entry.hours === '') {
        newErrors[`${index}-hours`] = '工时必填';
      }
    });

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  // 保存条目
  const handleSave = () => {
    if (!validateEntries()) {
      return;
    }

    // 清理数据，只保留必要字段
    const cleanEntries = localEntries.map(entry => {
      const { isSaved, ...cleanEntry } = entry;
      return {
        ...cleanEntry,
        hours: parseFloat(cleanEntry.hours) || 0
      };
    });

    onSave(cleanEntries);
    onClose();
  };

  const isReadonly = readOnly;

  return (
    <Dialog
      open={open}
      onClose={onClose}
      maxWidth="lg"
      fullWidth
      scroll="paper"
    >
      <DialogTitle>
        <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
          <Typography variant="h6">
            {readOnly ? '查看' : '编辑'}项目条目
          </Typography>
        </Box>
        <Typography variant="subtitle2" color="text.secondary">
          {date ? format(new Date(date), 'yyyy年MM月dd日 (EEEE)', { locale: zhCN }) : ''}
        </Typography>
      </DialogTitle>

      <DialogContent dividers>
        {errors.general && (
          <Alert severity="error" sx={{ mb: 2 }}>
            {errors.general}
          </Alert>
        )}

        <Box sx={{ mt: 1 }}>
          {localEntries.map((entry, index) => (
            <Card
              key={index}
              sx={{
                mb: 2,
                p: 2,
                border: 1,
                borderColor: errors.general ? 'error.main' : 'grey.300',
                borderRadius: 2
              }}
              variant="outlined"
            >
              <CardContent sx={{ p: 0 }}>
                {/* 条目标题 */}
                <Box sx={{
                  display: 'flex',
                  justifyContent: 'space-between',
                  alignItems: 'center',
                  mb: 2,
                  pb: 1,
                  borderBottom: 1,
                  borderColor: 'divider'
                }}>
                  <Typography variant="subtitle1" fontWeight="bold">
                    条目 {index + 1}
                  </Typography>

                  {localEntries.length > 1 && !isReadonly && (
                    <IconButton
                      size="small"
                      color="error"
                      onClick={() => handleRemoveEntry(index)}
                    >
                      <DeleteIcon fontSize="small" />
                    </IconButton>
                  )}
                </Box>

                {/* 表单字段 */}
                <Grid container spacing={2}>
                  {/* 项目代码 */}
                  <Grid item xs={12} md={6}>
                    <Autocomplete
                      options={projectOptions}
                      getOptionLabel={(option) =>
                        typeof option === 'string' ? option : `${option.project_code} - ${option.project_name}`
                      }
                      value={entry.project_code || ''}
                      onInputChange={(e, value) => handleProjectCodeChange(index, value)}
                      disabled={loading || isReadonly}
                      renderInput={(params) => (
                        <TextField
                          {...params}
                          label="项目代码 *"
                          error={!!errors[`${index}-project_code`]}
                          helperText={errors[`${index}-project_code`]}
                          InputProps={{
                            ...params.InputProps,
                            startAdornment: (
                              <InputAdornment position="start">
                                <SearchIcon color="action" />
                              </InputAdornment>
                            ),
                            endAdornment: (
                              <>
                                {loading ? <CircularProgress size={20} /> : null}
                                {params.InputProps.endAdornment}
                              </>
                            )
                          }}
                        />
                      )}
                    />
                  </Grid>

                  {/* 项目名称（只读） */}
                  <Grid item xs={12} md={6}>
                    <TextField
                      fullWidth
                      label="项目名称"
                      value={entry.project_name}
                      InputProps={{ readOnly: true }}
                      sx={{ bgcolor: isReadonly ? 'transparent' : 'grey.50' }}
                      helperText="自动填充"
                    />
                  </Grid>

                  {/* 任务编号 */}
                  <Grid item xs={12} md={6}>
                    <FormControl
                      fullWidth
                      error={!!errors[`${index}-task_number`]}
                      disabled={loading || isReadonly}
                    >
                      <InputLabel>任务编号 *</InputLabel>
                      <Select
                        value={entry.task_number}
                        label="任务编号 *"
                        onChange={(e) => handleFieldChange(index, 'task_number', e.target.value)}
                      >
                        {entry.project_code && taskOptions[entry.project_code] && (
                          taskOptions[entry.project_code].map(task => (
                            <MenuItem key={task.value} value={task.value}>
                              {task.label}
                            </MenuItem>
                          ))
                        )}
                        {!entry.project_code && (
                          <MenuItem disabled>请先选择项目代码</MenuItem>
                        )}
                      </Select>
                      {errors[`${index}-task_number`] && (
                        <Alert severity="error">{errors[`${index}-task_number`]}</Alert>
                      )}
                    </FormControl>
                  </Grid>

                  {/* 活动 */}
                  <Grid item xs={12} md={6}>
                    <TextField
                      fullWidth
                      label="活动"
                      value={entry.activity}
                      InputProps={{ readOnly: true }}
                      sx={{ bgcolor: isReadonly ? 'transparent' : 'grey.50' }}
                      helperText="从任务自动填充"
                    />
                  </Grid>

                  {/* 工时类型 */}
                  <Grid item xs={12} md={6}>
                    <FormControl fullWidth disabled={isReadonly}>
                      <InputLabel>工时类型 *</InputLabel>
                      <Select
                        value={entry.claim_type}
                        label="工时类型 *"
                        onChange={(e) => handleFieldChange(index, 'claim_type', e.target.value)}
                      >
                        <MenuItem value="NORMAL">正常工时</MenuItem>
                        <MenuItem value="LEAVE">请假</MenuItem>
                        <MenuItem value="OT_WORKING_DAYS">加班 - 工作日</MenuItem>
                        <MenuItem value="OT_HOLIDAYS">加班 - 节假日</MenuItem>
                        <MenuItem value="OT_STATUTORY_HOLIDAYS">加班 - 法定节假日</MenuItem>
                      </Select>
                    </FormControl>
                  </Grid>

                  {/* 工时 */}
                  <Grid item xs={12} md={6}>
                    <TextField
                      fullWidth
                      label="工时 *"
                      type="number"
                      value={entry.hours}
                      onChange={(e) => handleFieldChange(index, 'hours', e.target.value)}
                      error={!!errors[`${index}-hours`]}
                      helperText={errors[`${index}-hours`] || '0-24小时，精度0.5'}
                      disabled={isReadonly}
                      inputProps={{ step: 0.5, min: 0, max: 24 }}
                      InputProps={{
                        endAdornment: <InputAdornment position="end">小时</InputAdornment>
                      }}
                    />
                  </Grid>

                  {/* 备注 */}
                  <Grid item xs={12}>
                    <TextField
                      fullWidth
                      label="备注"
                      multiline
                      rows={2}
                      value={entry.remark}
                      onChange={(e) => handleFieldChange(index, 'remark', e.target.value)}
                      disabled={isReadonly}
                      placeholder="可选：添加工作备注"
                    />
                  </Grid>
                </Grid>
              </CardContent>
            </Card>
          ))}

          {/* 添加新条目按钮 */}
          {!isReadonly && (
            <Button
              variant="outlined"
              startIcon={<AddIcon />}
              onClick={handleAddEntry}
              fullWidth
              sx={{ mt: 2, py: 1.5 }}
            >
              添加另一个条目
            </Button>
          )}
        </Box>
      </DialogContent>

      <DialogActions>
        <Button onClick={onClose}>
          {isReadonly ? '关闭' : '取消'}
        </Button>

        {!isReadonly && (
          <Button
            variant="contained"
            onClick={handleSave}
            disabled={Object.keys(errors).length > 0}
          >
            保存条目
          </Button>
        )}
      </DialogActions>
    </Dialog>
  );
};

export default ProjectEntryDialog;
```

## 7. 自定义组件注册

### 7.1 注册配置

```javascript
// frontend/src/components/form/custom-registry.jsx

import React from 'react';
import { Alert } from '@mui/material';
import TimecardForm from '../timecard/TimecardForm';

// 自定义表单注册表
export const customFormRegistry = {
  // 注册Timecard表单
  TimecardForm: TimecardForm,

  // 其他自定义组件...
};

/**
 * 解析自定义组件
 */
export function resolveCustomComponent(componentKey) {
  return customFormRegistry[componentKey] || null;
}

// 自定义表单目录（用于管理界面）
export const customFormCatalog = [
  {
    key: 'TimecardForm',
    name: '工时卡表单',
    description: '用于员工工时填报的标准工时卡表单',
    category: 'Form',
    icon: 'EventNote',
    defaultProps: {
      mode: 'edit'
    }
  },
  // 其他自定义组件...
];
```

### 7.2 在流程表单中配置

**方式1：通过表单编辑器配置**

```json
{
  "type": "custom",
  "componentKey": "TimecardForm",
  "initialData": "{{formData}}",  // 从processInstance.contextValue获取
  "props": {
    "mode": "edit"              // 或 "view", "approval"
  }
}
```

**方式2：直接在表单版本定义中配置**

```json
{
  "key": "timecard-form-v1",
  "name": "Timecard Form",
  "schema": {
    "type": "custom",
    "componentKey": "TimecardForm",
    "initialData": "{{formData}}",
    "props": {
      "mode": "{{mode}}"   // 根据流程节点动态设置
    }
  }
}
```

---

## 8. 实施步骤

### Phase 1: Mock数据和服务层 (Day 1)

**任务清单**:
- [ ] 创建 `frontend/src/data/timecard/` 目录
- [ ] 创建 `mockSchemas.ts`（TypeScript接口）
- [ ] 创建 `mockData.ts`（模拟数据）
- [ ] 创建 `frontend/src/api/timecard/` 目录
- [ ] 创建 `timecardService.js`（服务层，获取初始数据）
- [ ] 创建 `timecardUtils.js`（工具函数：计算、验证等）

**交付物**:
- 完整的数据类型定义
- Mock数据源
- API服务层（支持Mock/真实切换）
- 工具函数（计算汇总、验证等）

### Phase 2: 核心组件实现 (Day 2-4)

**任务清单**:
- [ ] 创建 `frontend/src/components/timecard/` 目录
- [ ] 实现 `EmployeeInfo.jsx`（员工信息展示）
- [ ] 实现 `CalendarView.jsx`（日历视图）
- [ ] 实现 `TimecardSummary.jsx`（工时汇总）
- [ ] 实现 `ProjectEntryDialog.jsx`（项目条目对话框）
- [ ] 实现 `TimecardForm.jsx`（主组件）

**交付物**:
- 所有核心组件
- 完整的表单功能
- 支持edit/view/approval三种模式

### Phase 3: 自定义组件注册和测试 (Day 5)

**任务清单**:
- [ ] 更新 `custom-registry.jsx`，注册TimecardForm
- [ ] 创建测试流程定义
- [ ] 配置TimecardForm到表单版本
- [ ] 测试表单渲染
- [ ] 测试数据保存（onSubmit）
- [ ] 测试数据流向Flowable

**交付物**:
- 注册代码
- 测试用流程定义
- 测试报告

### Phase 4: 功能验证和优化 (Day 6-7)

**任务清单**:
- [ ] 验证所有功能清单
- [ ] 性能优化
- [ ] 用户体验优化
- [ ] 编写使用文档

**交付物**:
- 功能验证报告
- 优化后的代码
- 使用文档

---

## 9. 验证标准

### 9.1 功能验证清单

| 功能模块 | 功能点 | 验证标准 | 状态 |
|---------|-------|---------|------|
| **员工信息** | 自动显示当前用户信息 | 打开表单时自动加载，无需手动输入 | ⬜ |
| | 信息正确性 | 显示staff_id、姓名、部门等 | ⬜ |
| **Calendar View** | 月视图展示 | 显示当月日历，有工时单元格有颜色标记 | ⬜ |
| | 月份切换 | 可以切换月份查看 | ⬜ |
| | 颜色编码 | 节假日绿色、超时/周末红色 | ⬜ |
| | 项目显示 | 最多显示2个项目代码+小时数 | ⬜ |
| | 点击日期 | 弹出ProjectEntryDialog | ⬜ |
| **Project Entry** | 对话框显示 | 点击日期后正确弹出 | ⬜ |
| | Project Code输入 | 支持手动输入和搜索 | ⬜ |
| | 自动填充 | 输入有效code后自动填充name/task/activity | ⬜ |
| | Task选择 | 下拉选择任务 | ⬜ |
| | Claim Type选择 | 5种类型可选 | ⬜ |
| | Hours输入 | 0-24范围，0.5精度 | ⬜ |
| | Remark输入 | 支持多行文本 | ⬜ |
| | 添加条目 | 可以添加多个项目条目 | ⬜ |
| | 删除条目 | 可以删除条目 | ⬜ |
| | 必填验证 | project_code, task, hours必填 | ⬜ |
| | 保存条目 | 点击保存后更新到日历 | ⬜ |
| **工时汇总** | 自动计算 | 根据条目自动计算总工时 | ⬜ |
| | Project分组 | 按project+task+activity分组 | ⬜ |
| | 警告显示 | 总工时>160h显示警告 | ⬜ |
| **批量操作** | 复制到本周 | 将选中日期复制到本周空白日 | ⬜ |
| | 复制到工作日 | 复制到所有工作日 | ⬜ |
| | 复制到空白日 | 仅复制到没有工时的日期 | ⬜ |
| **Save功能** | Save按钮 | 底部居中显示Save按钮 | ⬜ |
| | 点击Save | 触发onSubmit，更新formData state | ⬜ |
| **数据流向** | initialData读取 | 正确从processInstance读取 | ⬜ |
| | onSubmit回调 | 调用后更新TaskDetailsDialog state | ⬜ |
| | Complete按钮 | 提交到Flowable引擎 | ⬜ |
| | 审批员查看 | 审批员打开时显示相同数据（只读） | ⬜ |
| **自定义组件** | 注册成功 | TimecardForm在registry中 | ⬜ |
| | FormRuntimeSwitch | 能正确解析和渲染 | ⬜ |
| | 多次编辑 | 多次Save都能正确更新数据 | ⬜ |

### 9.2 集成验证清单

| 验证项 | 验证标准 | 状态 |
|-------|---------|------|
| **表单配置** | 可以在表单编辑器中选择TimecardForm | ⬜ |
| | 可以配置props（mode等） | ⬜ |
| | 可以设置initialData映射 | ⬜ |
| **流程集成** | 填报节点使用edit模式 | ⬜ |
| | 审批节点使用approval模式 | ⬜ |
| | 数据在节点间正确传递 | ⬜ |
| **端到端流程** | 员工填报 → Save → Complete → Flowable | ⬜ |
| | 审批员查看 → 显示正确数据 → 只读 | ⬜ |
| | 审批通过 → 流程结束 | ⬜ |

---

## 10. 技术依赖

### 10.1 现有依赖（SPMS系统已包含）

| 依赖 | 版本 | 用途 |
|-----|------|------|
| react | 18.3.1 | 核心框架 |
| @mui/material | 7.x | UI组件库 |
| @mui/icons-material | 7.x | 图标 |
| date-fns | 3.x | 日期处理 |
| react-hook-form | 7.x | 表单管理 |

### 10.2 Mock依赖

| 依赖 | 版本 | 用途 |
|-----|------|------|
| @testing-library/react | 14.x | 单元测试 |

---

## 11. 附录

### 11.1 组件Props接口

```typescript
interface TimecardFormProps {
  mode: 'edit' | 'view' | 'approval';
  initialData?: TimecardFormData;
  onSubmit: (data: TimecardFormData) => void;
  readOnly?: boolean;
}
```

### 11.2 使用示例

**填报任务**:
```jsx
<FormRuntimeSwitch
  schema={{
    type: 'custom',
    componentKey: 'TimecardForm',
    initialData: formData,
    props: { mode: 'edit' }
  }}
  onSubmit={(updatedData) => setFormData(updatedData)}
/>
```

**审批任务**:
```jsx
<FormRuntimeSwitch
  schema={{
    type: 'custom',
    componentKey: 'TimecardForm',
    initialData: formData,
    props: { mode: 'approval' }
  }}
  readOnly={true}
/>
```

---

*本文档为Timecard模块与SPMS流程管理系统集成的修订规格，基于实际业务逻辑（TaskDetailsDialog）重新设计，确保与现有架构完全兼容。*
