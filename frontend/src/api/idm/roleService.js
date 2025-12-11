import apiConfig from "../apiConfig";

const api = apiConfig();
/**
 * Role Service - Handles all role-related API calls.
 * 
 * The RoleDTO structure used in requests and responses:
 *   {
 *     id: number,                // Role ID
 *     name: string,               // Role name
 *     description: string,        // Role description
 *     permissions: Array<string>, // List of permissions
 *     parentRoles: Array<Object>, // Parent roles (nested RoleDTO structure)
 *     childRoles: Array<Object>,  // Child roles (nested RoleDTO structure)
 *     active: boolean,            // Active status
 *     lastModified: number,       // Timestamp of last modification
 *     createdBy: string,          // Creator username
 *     updatedBy: string,          // Last updater username
 *     createdTime: number         // Creation timestamp
 *   }
 * 
 * Note: For methods that return paginated results, the response structure is:
 *   {
 *     content: Array<Object>,     // Page of roles (each object is a RoleDTO)
 *     totalElements: number,       // Total number of roles
 *     ...                          // Other pagination properties
 *   }
 */

export default {
  /**
   * Create a new role
   * @param {Object} role - Role data (conforming to RoleDTO structure)
   * @returns {Promise} Resolves with created role (RoleDTO)
   */
  create: (role) => api.post('/roles', role),

  /**
   * Get a role by ID
   * @param {number} id - Role ID
   * @returns {Promise} Resolves with role data (RoleDTO)
   */
  get: (id) => api.get(`/roles/${id}`).then(x=>x.data),

  /**
   * Get all roles with pagination
   * @param {Object} [pageable] - Pagination parameters (page, size, sort)
   * @returns {Promise} Resolves with paginated results (Page<RoleDTO>)
   */
  getAll: (pageable = {}) => api.get('/roles', { params: pageable }).then(x=>x.data),

  /**
   * Update a role
   * @param {number} id - Role ID
   * @param {Object} role - Updated role data (RoleDTO structure)
   * @returns {Promise} Resolves with updated role (RoleDTO)
   */
  update: (id, role) => api.put(`/roles/${id}`, role),

  /**
   * Delete a role
   * @param {number} id - Role ID
   * @returns {Promise} Resolves when deletion is complete
   */
  delete: (id) => api.delete(`/roles/${id}`),

  /**
   * Add permission to a role
   * @param {number} id - Role ID
   * @param {string} permission - Permission to add
   * @returns {Promise} Resolves with updated role (RoleDTO)
   */
  addPermission: (id, permission) => 
    api.post(`/roles/${id}/permissions`, null, { params: { permission } }),

  /**
   * Remove permission from a role
   * @param {number} id - Role ID
   * @param {string} permission - Permission to remove
   * @returns {Promise} Resolves with updated role (RoleDTO)
   */
  removePermission: (id, permission) => 
    api.delete(`/roles/${id}/permissions`, { params: { permission } }),

  /**
   * Get permissions for a role
   * @param {number} id - Role ID
   * @returns {Promise} Resolves with array of permissions
   */
  getPermissions: (id) => api.get(`/roles/${id}/permissions`),

  /**
   * Add parent role to a role
   * @param {number} id - Child role ID
   * @param {number} parentId - Parent role ID
   * @returns {Promise} Resolves with updated role (RoleDTO)
   */
  addParent: (id, parentId) => 
    api.post(`/roles/${id}/parents`, null, { params: { parentId } }),

  /**
   * Remove parent role from a role
   * @param {number} id - Child role ID
   * @param {number} parentId - Parent role ID
   * @returns {Promise} Resolves with updated role (RoleDTO)
   */
  removeParent: (id, parentId) => 
    api.delete(`/roles/${id}/parents`, { params: { parentId } }),

  /**
   * Search roles with filters
   * @param {Object} [params] - Search parameters
   * @param {string} [params.name] - Role name filter
   * @param {string} [params.description] - Role description filter
   * @param {Object} [pageable] - Pagination parameters
   * @returns {Promise} Resolves with paginated results (Page<RoleDTO>)
   */
  search: (params = {}, pageable = {}) => 
    api.get('/roles/search', { params: { ...params, ...pageable } })
};
