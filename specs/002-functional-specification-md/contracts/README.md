# Timecard Module API Contracts

本目录包含 Timecard 模块的 OpenAPI 3.0 格式的 API 契约文档。

## 文件说明

### timecard-api.yaml
Timecard 核心 API，包括：
- 员工信息获取
- Timecard 提交
- 项目验证
- 活跃项目列表
- 月份调整
- 审批任务查询

**Base Path**: `/api/v1/timecard`

### holiday-api.yaml
节假日管理 API，包括：
- 节假日 CRUD 操作
- 节假日导入/导出
- 按月份/年份查询

**Base Path**: `/api/v1/timecard/holidays`

### project-task-api.yaml
项目-任务-活动组合管理 API，包括：
- 项目任务活动 CRUD 操作
- 按项目代码/任务号筛选

**Base Path**: `/api/v1/timecard/projects/tasks`

### excel-api.yaml
Excel 导入导出 API，包括：
- 项目数据导入/导出
- 节假日数据导入/导出

**Base Path**: `/api/v1/timecard`

### report-api.yaml
报表生成 API，包括：
- Timecard 汇总报表
- 个人 Timecard 记录报表
- Timecard 明细报表
- 永久员工 IT 记录报表（加班报表）

**Base Path**: `/api/v1/report`

## 使用说明

这些 OpenAPI 契约文档可以用于：
1. **API 文档生成**：使用 Swagger UI 或 Redoc 生成交互式 API 文档
2. **代码生成**：使用 OpenAPI Generator 生成客户端 SDK 或服务端代码
3. **API 测试**：使用 Postman 或其他工具导入 OpenAPI 规范进行 API 测试
4. **契约测试**：确保实现与契约保持一致

## 验证

可以使用以下工具验证 OpenAPI 规范：
- [Swagger Editor](https://editor.swagger.io/)
- [Redoc](https://redocly.github.io/redoc/)
- OpenAPI CLI: `npx @redocly/cli lint <file>.yaml`

## 更新说明

当 API 发生变化时，请同步更新对应的 YAML 文件，确保契约文档与实际实现保持一致。

