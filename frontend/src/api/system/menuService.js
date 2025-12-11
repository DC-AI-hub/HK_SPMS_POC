import apiConfig from "../apiConfig";

const api = apiConfig();

/**
 * Menu Service - Handles menu-related API calls
 * 
 * DTO Definitions:
 * 
 * MenuRequestDTO (for create/update):
 * - title: string (required, 1-100 chars)
 * - path: string (required)
 * - icon: string (optional)
 * - displayOrder: number (optional, non-negative)
 * - active: boolean (optional)
 * - parentMenuId: number (optional)
 * - allowedRoleIds: array of numbers (optional)
 * 
 * MenuResponseDTO (returned from API):
 * - id: number
 * - title: string
 * - path: string
 * - icon: string
 * - displayOrder: number
 * - active: boolean
 * - parentMenuId: number
 * - createdBy: string
 * - modifiedBy: string
 * - createdAt: string (ISO date)
 * - updatedAt: string (ISO date)
 * - allowedRoleIds: array of numbers
 */

export default {
  /**
   * Create a new menu item
   * @param {Object} menuData - MenuRequestDTO data
   * @returns {Promise} Resolves with created MenuResponseDTO
   */
  createMenu: (menuData) => api.post('/menus', menuData),

  /**
   * Get menu by ID
   * @param {number} id - Menu ID
   * @returns {Promise} Resolves with MenuResponseDTO
   */
  getMenuById: (id) => api.get(`/menus/${id}`),

  /**
   * Get all menus with pagination
   * @param {Object} pageable - Pagination parameters (page, size, sort)
   * @returns {Promise} Resolves with Page<MenuResponseDTO>
   */
  getAllMenus: (pageable) => api.get('/menus', { params: pageable }),

  /**
   * Update menu item
   * @param {number} id - Menu ID
   * @param {Object} menuData - MenuRequestDTO data
   * @returns {Promise} Resolves with updated MenuResponseDTO
   */
  updateMenu: (id, menuData) => api.put(`/menus/${id}`, menuData),

  /**
   * Delete menu item
   * @param {number} id - Menu ID
   * @returns {Promise} Resolves when deletion is successful
   */
  deleteMenu: (id) => api.delete(`/menus/${id}`),

  /**
   * Get menus for current user based on their roles
   * @returns {Promise} Resolves with array of MenuResponseDTO
   */
  getMenusForCurrentUser: () => api.get('/menus/user/current'),

  /**
   * Get menus for specific user (admin only)
   * @param {number} userId - User ID
   * @returns {Promise} Resolves with array of MenuResponseDTO
   */
  getMenusForUser: (userId) => api.get(`/menus/user/${userId}`),

  /**
   * Add role to menu
   * @param {number} menuId - Menu ID
   * @param {number} roleId - Role ID
   * @returns {Promise} Resolves with updated MenuResponseDTO
   */
  addRoleToMenu: (menuId, roleId) => api.post(`/menus/${menuId}/roles/${roleId}`),

  /**
   * Remove role from menu
   * @param {number} menuId - Menu ID
   * @param {number} roleId - Role ID
   * @returns {Promise} Resolves with updated MenuResponseDTO
   */
  removeRoleFromMenu: (menuId, roleId) => api.delete(`/menus/${menuId}/roles/${roleId}`)
};
