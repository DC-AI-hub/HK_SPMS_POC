import apiConfig from "../apiConfig";

const api = apiConfig();

/**
 * Department Service - Handles department-related API calls
 */
export default {
  /**
   * Retrieve a paginated list of departments
   * @param {Object} params - Pagination and search parameters
   * @param {number} params.page - Page number
   * @param {number} params.size - Page size
   * @param {string} params.search - Optional search term
   * @returns {Promise} Resolves with paginated department data
   */
  list: (params) => api.get('/departments', { params }),

  /**
   * Create a new department
   * @param {Object} departmentData - Department creation data
   * @param {string} departmentData.name - Department name
   * @param {number} departmentData.parent - Parent department ID
   * @param {string} departmentData.type - Department type
   * @param {string[]} departmentData.tags - Department tags
   * @param {number} departmentData.level - Department level
   * @param {number} departmentData.departmentHeadId - Department head user ID
   * @returns {Promise} Resolves with created department data
   */
  create: (departmentData) => api.post('/departments', departmentData),

  /**
   * Add users to a department
   * @param {number} departmentId - Target department ID
   * @param {Object} userData - User assignment data
   * @param {number[]} userData.userIds - List of user IDs to add
   * @returns {Promise} Resolves when operation completes
   */
  addUsers: (departmentId, userData) => 
    api.post(`/departments/${departmentId}/users`, userData),

  /**
   * Remove users from a department
   * @param {number} departmentId - Target department ID
   * @param {Object} userData - User removal data
   * @param {number[]} userData.userIds - List of user IDs to remove
   * @returns {Promise} Resolves when operation completes
   */
  removeUsers: (departmentId, userData) => 
    api.delete(`/departments/${departmentId}/users`, { data: userData })
};
