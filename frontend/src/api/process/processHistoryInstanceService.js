import apiConfig from "../apiConfig";

const api = apiConfig();

/**
 * @typedef {Object} ProcessHistoryDTO
 * @property {string} processInstanceId Process instance ID
 * @property {string} startTime Start timestamp
 * @property {string} [endTime] End timestamp (if completed)
 * @property {string} startUserId ID of user who started the process
 * @property {string} [businessKey] Business key associated with process instance
 */

/**
 * @typedef {Object} PageProcessHistory
 * @property {Array<ProcessHistoryDTO>} content
 * @property {number} totalElements
 * @property {number} totalPages
 * @property {boolean} last
 * @property {boolean} first
 * @property {number} numberOfElements
 * @property {number} size
 * @property {number} number
 * @property {boolean} empty
 */

const processHistoryInstanceService = {
  /**
   * Retrieves paginated history of processes started by a specific user
   * 
   * @param {string} userId ID of the user who started the processes
   * @param {Object} pageable Pagination configuration
   * @param {number} pageable.page Page number (0-indexed)
   * @param {number} pageable.size Number of items per page
   * @param {string} pageable.sort Sort criteria (format: "property,direction")
   * @returns {Promise<PageProcessHistory>} Paginated process history
   */
  async getHistoryStartedByUser(userId, pageable) {
    try {
      const response = await api.get('process/history/started-by-user', {
        params: { userId, ...pageable }
      });
      return response.data;
    } catch (error) {
      const { response } = error;
      if (response?.data) throw response.data;
      throw error;
    }
  },

  /**
   * Retrieves paginated history of processes handled by a specific user
   * 
   * @param {string} userId ID of the user who handled the processes
   * @param {Object} pageable Pagination configuration
   * @param {number} pageable.page Page number (0-indexed)
   * @param {number} pageable.size Number of items per page
   * @param {string} pageable.sort Sort criteria (format: "property,direction")
   * @returns {Promise<PageProcessHistory>} Paginated process history
   */
  async getHistoryHandledByUser(userId, pageable) {
    try {
      const response = await api.get('process/history/handled-by-user', {
        params: { userId, ...pageable }
      });
      return response.data;
    } catch (error) {
      const { response } = error;
      if (response?.data) throw response.data;
      throw error;
    }
  },

  /**
   * Retrieves paginated history of processes handled by the current user
   * 
   * @param {Object} pageable Pagination configuration
   * @param {number} pageable.page Page number (0-indexed)
   * @param {number} pageable.size Number of items per page
   * @param {string} pageable.sort Sort criteria (format: "property,direction")
   * @returns {Promise<PageProcessHistory>} Paginated process history
   */
  async getHistoryHandledByCurrentUser(pageable) {
    try {
      const response = await api.get('process/history/current-user/handle', {
        params: pageable
      });
      return response.data;
    } catch (error) {
      const { response } = error;
      if (response?.data) throw response.data;
      throw error;
    }
  },

  /**
   * Retrieves paginated history of processes started by the current user
   * 
   * @param {Object} pageable Pagination configuration
   * @param {number} pageable.page Page number (0-indexed)
   * @param {number} pageable.size Number of items per page
   * @param {string} pageable.sort Sort criteria (format: "property,direction")
   * @returns {Promise<PageProcessHistory>} Paginated process history
   */
  async getHistoryStartedByCurrentUser(pageable) {
    try {
      const response = await api.get('process/history/current-user/started', {
        params: pageable
      });
      return response.data;
    } catch (error) {
      const { response } = error;
      if (response?.data) throw response.data;
      throw error;
    }
  }
};

export default processHistoryInstanceService;
