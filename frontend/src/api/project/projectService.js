import apiFactory from '../apiConfig'

const api = apiFactory()

export function listProjects(params) {
  return api.get('/projects', { params })
}

export function getActiveOptions() {
  return api.get('/projects/options')
}

// Force server API (no JSON fallback) – used to resolve real IDs for saving
export function getActiveOptionsServerOnly() {
  return api.get('/projects/options')
}

export function getByManager(managerId) {
  return api.get(`/projects/manager/${managerId}`)
}

export function searchByName(name) {
  return api.get('/projects/search', { params: { name } })
}


