import apiFactory from '../apiConfig'

const api = apiFactory()

export function listTasks(params) {
  return api.get('/tasks', { params })
}

export function getByProject(projectId) {
  return api.get(`/tasks/project/${projectId}`)
}

export function getOptionsByProject(projectId) {
  return api.get(`/tasks/project/${projectId}/options`)
}

// Removed JSON fallback; tasks options must come from backend

export function searchByName(name) {
  return api.get('/tasks/search', { params: { name } })
}


