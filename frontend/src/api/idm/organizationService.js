import apiConfig from "../apiConfig";

const api = apiConfig();


export default {
  /**
   * Get all companies with pagination and search
   * @param {Object} params - Search parameters
   * @param {String} [params.search] - Search query
   * @param {String} [params.type] - type query
   * @param {Object} pageable - Pagination parameters
   * @returns {Promise} Resolves with paginated results
   */
  getCompanies: (params = {}, pageable = {}) => 
    api.get('/companies', { params: { ...params, ...pageable } }).then(x=>x.data),

  /**
   * Create a new company
   * @param {Object} company - Company data
   * @returns {Promise} Resolves with created company
   */
  createCompany: (company) => api.post('/companies', company),

  /**
   * Update a company
   * @param {Number} id - Company ID
   * @param {Object} company - Updated company data
   * @returns {Promise} Resolves with updated company
   */
  updateCompany: (id, company) => api.put(`/companies/${id}`, company),

  /**
   * Delete a company
   * @param {Number} id - Company ID
   * @returns {Promise} Resolves when deletion is complete
   */
  deleteCompany: (id) => api.delete(`/companies/${id}`),

  /**
   * Bulk delete companies
   * @param {Array} ids - Array of company IDs
   * @returns {Promise} Resolves when deletion is complete
   */
  bulkDeleteCompanies: (ids) => api.delete('/companies/bulk', { data: { ids } }),

  /**
   * Get all divisions with pagination and search
   * @param {Object} params - Search parameters
   * @param {String} [params.search] - Search query
   * @param {Object} pageable - Pagination parameters
   * @returns {Promise} Resolves with paginated results
   */
  getDivisions: (params = {}, pageable = {}) => 
    api.get('/divisions', { params: { ...params, ...pageable } }).then(x=>x.data),

  /**
   * Create a new division
   * @param {Object} division - Division data
   * @returns {Promise} Resolves with created division
   */
  createDivision: (division) => api.post('/divisions', division),

  /**
   * Update a division
   * @param {Number} id - Division ID
   * @param {Object} division - Updated division data
   * @returns {Promise} Resolves with updated division
   */
  updateDivision: (id, division) => api.put(`/divisions/${id}`, division),

  /**
   * Delete a division
   * @param {Number} id - Division ID
   * @returns {Promise} Resolves when deletion is complete
   */
  deleteDivision: (id) => api.delete(`/divisions/${id}`),

  /**
   * Bulk delete divisions
   * @param {Array} ids - Array of division IDs
   * @returns {Promise} Resolves when deletion is complete
   */
  bulkDeleteDivisions: (ids) => api.delete('/divisions/bulk', { data: { ids } }),

  /**
   * Get validate company Via Company Type 
   *  the companyType
   */
  getValidParentCompany: (type) => api.get("/companies/valid-parents?type="+ type),
  
  /**
   * Get all users
   * @returns {Promise} Resolves with list of users
   */
  getUsers: () => api.get('/users/search').then(x => x.data),

  /**
   * Get all departments with pagination and search
   * @param {Object} params - Search parameters
   * @param {String} [params.search] - Search query
   * @param {Object} pageable - Pagination parameters
   * @returns {Promise} Resolves with paginated results
   */
  getDepartments: (params = {}, pageable = {}) =>
    api.get('/departments', { params: { ...params, ...pageable } }).then(x=>x.data),

  /**
   * Create a new department
   * @param {Object} department - Department data
   * @returns {Promise} Resolves with created department
   */
  createDepartment: (department) => api.post('/departments', department),

  /**
   * Update a department
   * @param {Number} id - Department ID
   * @param {Object} department - Updated department data
   * @returns {Promise} Resolves with updated department
   */
  updateDepartment: (id, department) => api.put(`/departments/${id}`, department),

  /**
   * Delete a department
   * @param {Number} id - Department ID
   * @returns {Promise} Resolves when deletion is complete
   */
  deleteDepartment: (id) => api.delete(`/departments/${id}`),

  /**
   * Bulk delete departments
   * @param {Array} ids - Array of department IDs
   * @returns {Promise} Resolves when deletion is complete
   */
  bulkDeleteDepartments: (ids) => api.delete('/departments/bulk', { data: { ids } }),
  
  getOrganizationChart: (companyId, mode) => 
    api.get(`/companies/${companyId}/organization-chart`, { params: { mode } })
};
