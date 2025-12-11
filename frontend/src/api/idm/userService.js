import apiConfig from "../apiConfig";

const api = apiConfig();

/**
 * User Service - Handles all user-related API calls
 */

/**
 * @typedef {Object} UserDTO
 * @property {number} id - User ID (auto-generated)
 * @property {string} username - Unique username (required)
 * @property {string} email - User email (required)
 * @property {string} [description] - Optional user description
 * @property {string} type - User type (e.g., 'ADMIN', 'USER')
 * @property {Object.<string, string>} userProfiles - Key-value profile metadata
 * @property {string} userProfiles.firstName - User's first name
 * @property {string} userProfiles.lastName - User's last name
 * @property {string} userProfiles.avatarUrl - Profile image URL
 * @property {string} createdAt - Creation timestamp (ISO format)
 * @property {string} updatedAt - Last update timestamp (ISO format)
 * @property {string} createdBy - Creator username
 * @property {string} modifiedBy - Last modifier username
 * @property {Object} [functionalDepartment] - Primary department (DepartmentDTO)
 * @property {Object} [localDepartment] - Local department (DepartmentDTO)
 * @property {Object[]} allDepartments - All assigned departments (DepartmentDTO[])
 */

export default {
  /**
   * Create a new user
   * @param {UserDTO} user - User creation data
   * @returns {Promise<UserDTO>} Created user data
   */
  create: (user) => api.post('/users', user),

  /**
   * Get a user by ID
   * @param {number} id - User ID
   * @returns {Promise<UserDTO>} Full user data
   */
  get: (id) => api.get(`/users/${id}`),

  /**
   * Update a user
   * @param {number} id - User ID
   * @param {UserDTO} user - Updated user data
   * @returns {Promise<UserDTO>} Updated user data
   */
  update: (id, user) => api.put(`/users/${id}`, user),

  /**
   * Delete a user
   * @param {number} id - User ID
   * @returns {Promise<void>} Resolves when deletion completes
   */
  delete: (id) => api.delete(`/users/${id}`),

  /**
   * Assign a role to a user
   * @param {number} userId - User ID
   * @param {number} roleId - Role ID
   * @returns {Promise<UserDTO>} Updated user data
   */
  assignRole: (userId, roleId) => api.post(`/users/${userId}/roles/${roleId}`),

  /**
   * Remove a role from a user
   * @param {number} userId - User ID
   * @param {number} roleId - Role ID
   * @returns {Promise<UserDTO>} Updated user data
   */
  removeRole: (userId, roleId) => api.delete(`/users/${userId}/roles/${roleId}`),

  /**
   * Search users with pagination
   * @param {Object} params - Search/pagination parameters
   * @param {string} params.query - Search query string
   * @param {number} [params.page=0] - Page number (Spring Pageable)
   * @param {number} [params.size=20] - Page size (Spring Pageable)
   * @param {string} [params.sort] - Sort criteria (field,direction)
   * @returns {Promise<UserDTO[]>} Paginated list of matching users
   */
  search: (params) => api.get('/users/search', { params })
  ,
  /**
   * Optional helper: get user by username if backend supports such endpoint
   * Fallback will be handled by caller when 404
   */
  getByUsername: (username) => api.get(`/users/search`, { params: { query: username, page: 0, size: 1 } }).then(res => ({ data: (res?.data?.data?.[0] || res?.data?.[0] || null) }))
};
