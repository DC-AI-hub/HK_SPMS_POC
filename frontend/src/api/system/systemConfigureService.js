import apiConfig from "../apiConfig";

const api = apiConfig();

/**
 * System Configuration Service - Handles system configuration API calls
 * 
 * DTO Definitions:
 * 
 * SystemConfigResponseDTO (returned from API):
 * - id: number
 * - key: string (unique identifier for the setting)
 * - value: any (the setting value, type depends on dataType)
 * - dataType: string (enum: 'STRING', 'NUMBER', 'BOOLEAN', 'JSON', 'ARRAY')
 * - description: string (human-readable description of the setting)
 * - category: string (optional, grouping category)
 * - isReadOnly: boolean (if true, setting cannot be modified via UI)
 * - defaultValue: any (optional, default value for the setting)
 * - validationRules: string (optional, validation rules in JSON format)
 * - createdBy: string
 * - modifiedBy: string
 * - createdAt: string (ISO date)
 * - updatedAt: string (ISO date)
 * 
 * SystemConfigUpdateRequestDTO (for update operations):
 * - value: any (the new value for the setting)
 * - modifiedBy: string (optional, user making the change)
 */

export default {
  /**
   * Get all system configuration settings
   * @returns {Promise} Resolves with array of SystemConfigResponseDTO
   */
  getAllSystemConfig: () => api.get('/system/settings'),

  /**
   * Get system configuration setting by key
   * @param {string} key - The setting key to retrieve
   * @returns {Promise} Resolves with SystemConfigResponseDTO
   */
  getSystemConfigByKey: (key) => api.get(`/system/settings/${key}`),

  /**
   * Get system configuration settings by category
   * @param {string} category - The category to filter by
   * @returns {Promise} Resolves with array of SystemConfigResponseDTO
   */
  getSystemConfigByCategory: (category) => api.get('/system/settings', { 
    params: { category } 
  }),

  /**
   * Update a system configuration setting
   * @param {string} key - The setting key to update
   * @param {Object} configData - SystemConfigUpdateRequestDTO data
   * @returns {Promise} Resolves with updated SystemConfigResponseDTO
   */
  updateSystemConfig: (key, configData) => api.put(`/system/settings/${key}`, configData),

  /**
   * Batch update multiple system configuration settings
   * @param {Array} updates - Array of { key: string, value: any } objects
   * @returns {Promise} Resolves with array of updated SystemConfigResponseDTO
   */
  batchUpdateSystemConfig: (updates) => api.patch('/system/settings/batch', { updates }),

  /**
   * Reset system configuration setting to default value
   * @param {string} key - The setting key to reset
   * @returns {Promise} Resolves with reset SystemConfigResponseDTO
   */
  resetSystemConfig: (key) => api.post(`/system/settings/${key}/reset`),

  /**
   * Get system configuration settings history
   * @param {string} key - The setting key to get history for
   * @param {Object} pageable - Pagination parameters (page, size, sort)
   * @returns {Promise} Resolves with Page<SystemConfigHistoryDTO>
   */
  getSystemConfigHistory: (key, pageable) => api.get(`/system/settings/${key}/history`, { 
    params: pageable 
  }),

  /**
   * Validate system configuration value
   * @param {string} key - The setting key to validate
   * @param {any} value - The value to validate
   * @returns {Promise} Resolves with validation result { isValid: boolean, message: string }
   */
  validateSystemConfig: (key, value) => api.post(`/system/settings/${key}/validate`, { value }),

  /**
   * Get system configuration categories
   * @returns {Promise} Resolves with array of category strings
   */
  getSystemConfigCategories: () => api.get('/system/settings/categories'),

  /**
   * Export system configuration to file
   * @param {string} format - Export format ('json', 'yaml', 'properties')
   * @returns {Promise} Resolves with export file data
   */
  exportSystemConfig: (format = 'json') => api.get('/system/settings/export', { 
    params: { format },
    responseType: 'blob'
  }),

  /**
   * Import system configuration from file
   * @param {File} file - Configuration file to import
   * @param {boolean} overwrite - Whether to overwrite existing settings
   * @returns {Promise} Resolves with import result { imported: number, skipped: number, errors: array }
   */
  importSystemConfig: (file, overwrite = false) => {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('overwrite', overwrite.toString());
    
    return api.post('/system/settings/import', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    });
  }
};
