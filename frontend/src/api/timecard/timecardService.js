import apiFactory from '../apiConfig'

const api = apiFactory()

/**
 * 获取活跃项目列表（用于 Timecard 填报）
 * @param {string} search - 搜索关键词（可选）
 * @returns {Promise} 项目任务列表
 */
export function getActiveProjects(search = '') {
  return api.get('/timecard/projects/active', {
    params: search ? { search } : {}
  })
}

/**
 * 获取项目-任务-活动列表（支持所有状态）
 * @param {string} search - 搜索关键词（可选）
 * @param {string} status - 状态筛选（可选：ACTIVE/COMPLETED/ALL）
 * @returns {Promise} 项目任务列表（包装在 ApiResponse 中）
 */
export function getProjectTasks(search = '', status = 'ALL') {
  return api.get('/timecard/projects/tasks', {
    params: {
      ...(search ? { search } : {}),
      ...(status ? { status } : {})
    }
  })
}

/**
 * 验证项目代码并获取项目信息
 * @param {string} projectCode - 项目代码
 * @returns {Promise} 项目验证结果
 */
export function validateProjectCode(projectCode) {
  return api.get(`/timecard/projects/validate/${projectCode}`)
}

/**
 * 获取节假日列表
 * @param {number} year - 年份（可选）
 * @param {number} month - 月份（可选）
 * @returns {Promise} 节假日列表
 */
export function getHolidays(year, month) {
  const params = {}
  if (year) params.year = year
  if (month) params.month = month
  return api.get('/timecard/holidays', { params })
}

/**
 * 导入项目数据
 * @param {File} file - Excel 文件
 * @returns {Promise} 导入结果
 */
export function importProjects(file) {
  const formData = new FormData()
  formData.append('file', file)
  return api.post('/timecard/projects/import', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * 导出项目数据
 * @param {string} status - 项目状态筛选（可选）
 * @returns {Promise} Excel 文件
 */
export function exportProjects(status) {
  return api.get('/timecard/projects/export', {
    params: status ? { status } : {},
    responseType: 'blob'
  })
}

/**
 * 导入节假日数据
 * @param {File} file - Excel 文件
 * @returns {Promise} 导入结果
 */
export function importHolidays(file) {
  const formData = new FormData()
  formData.append('file', file)
  return api.post('/timecard/holidays/import', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * 导出节假日数据
 * @param {number} year - 年份筛选（可选）
 * @returns {Promise} Excel 文件
 */
export function exportHolidays(year) {
  return api.get('/timecard/holidays/export', {
    params: year ? { year } : {},
    responseType: 'blob'
  })
}

/**
 * 创建节假日
 * @param {Object} holidayData - 节假日数据 {date, country, name, type}
 * @returns {Promise} 创建的节假日
 * @note 后端API待实现
 */
export function createHoliday(holidayData) {
  return api.post('/timecard/holidays', holidayData)
}

/**
 * 更新节假日
 * @param {number|string} id - 节假日ID
 * @param {Object} holidayData - 节假日数据
 * @returns {Promise} 更新后的节假日
 * @note 后端API待实现
 */
export function updateHoliday(id, holidayData) {
  return api.put(`/timecard/holidays/${id}`, holidayData)
}

/**
 * 删除节假日
 * @param {number|string} id - 节假日ID
 * @returns {Promise}
 * @note 后端API待实现
 */
export function deleteHoliday(id) {
  return api.delete(`/timecard/holidays/${id}`)
}

/**
 * 创建项目-任务-活动组合
 * @param {Object} projectTaskData - 项目任务数据 {projectCode, projectName, taskNumber, activity, status}
 * @returns {Promise} 创建的项目任务
 */
export function createProjectTask(projectTaskData) {
  return api.post('/timecard/projects/tasks', projectTaskData)
}

/**
 * 更新项目-任务-活动组合
 * @param {string} id - 唯一标识（格式：projectId-taskId）
 * @param {Object} projectTaskData - 项目任务数据 {projectName, taskNumber, activity, status}
 * @returns {Promise} 更新后的项目任务
 */
export function updateProjectTask(id, projectTaskData) {
  return api.put(`/timecard/projects/tasks/${id}`, projectTaskData)
}

/**
 * 删除项目-任务-活动组合（软删除）
 * @param {string} id - 唯一标识（格式：projectId-taskId）
 * @returns {Promise}
 */
export function deleteProjectTask(id) {
  return api.delete(`/timecard/projects/tasks/${id}`)
}

