import apiConfig from "../apiConfig";

const api = apiConfig();

/**
 * @typedef {Object} ProcessInstanceDTO
 * @property {string} instanceId Process instance ID
 * @property {string} definitionId Process definition ID
 * @property {string} status Process status
 * @property {number} startTime Start timestamp (milliseconds)
 * @property {number} [endTime] End timestamp (milliseconds)
 * @property {string} [businessKey] Business key
 * @property {string} [deploymentId] Deployment ID
 * @property {Array<TaskDTO>} [activeTasks] Active tasks
 */

/**
 * @typedef {Object} TaskDTO
 * @property {string} taskId Task ID
 * @property {string} name Task name
 * @property {string} assignee Assignee ID
 * @property {string} processInstanceId Process instance ID
 * @property {Object.<string, *>} [context] Task context variables
 */

/**
 * @typedef {Object} Page
 * @property {Array} content Page content
 * @property {number} totalElements Total elements count
 * @property {number} totalPages Total pages count
 * @property {boolean} last Whether this is the last page
 * @property {boolean} first Whether this is the first page
 * @property {number} numberOfElements Number of elements in current page
 * @property {number} size Page size
 * @property {number} number Page number (0-indexed)
 * @property {boolean} empty Whether page is empty
 */

const userProcessInstanceService = {
  /**
   * Retrieves active process instances related to the current user
   * 
   * @param {Object} pageable Pagination configuration
   * @param {number} pageable.page Page number (0-indexed)
   * @param {number} pageable.size Number of items per page
   * @param {string} pageable.sort Sort criteria (format: "property,direction")
   * @returns {Promise<Page<ProcessInstanceDTO>>} Page of active process instances
   */
  async getActiveInstances(pageable) {
    try {
      const response = await api.get('/user/process-instances/active', {
        params: pageable
      });
      return response.data;
    } catch (error) {
      const { response } = error;
      if (response?.status === 400) {
        throw new Error('Bad request: Invalid parameters');
      } else if (response?.data) {
        throw response.data;
      }
      throw error;
    }
  },

  /**
   * Retrieves assigned running tasks for the current user
   * 
   * @param {Object} pageable Pagination configuration
   * @param {number} pageable.page Page number (0-indexed)
   * @param {number} pageable.size Number of items per page
   * @param {string} pageable.sort Sort criteria (format: "property,direction")
   * @returns {Promise<Page<TaskDTO>>} Page of assigned tasks
   */
  async getAssignedTasks(pageable) {
    try {
      const response = await api.get('/user/process-instances/tasks', {
        params: pageable
      });
      return response.data;
    } catch (error) {
      const { response } = error;
      if (response?.status === 400) {
        throw new Error('Bad request: Invalid parameters');
      } else if (response?.data) {
        throw response.data;
      }
      throw error;
    }
  },

  /**
   * Retrieves historical process instances related to the current user
   * 
   * @param {Object} pageable Pagination configuration
   * @param {number} pageable.page Page number (0-indexed)
   * @param {number} pageable.size Number of items per page
   * @param {string} pageable.sort Sort criteria (format: "property,direction")
   * @returns {Promise<Page<ProcessInstanceDTO>>} Page of historical process instances
   */
  async getHistoricalInstances(pageable) {
    try {
      const response = await api.get('/user/process-instances/history', {
        params: pageable
      });
      return response.data;
    } catch (error) {
      const { response } = error;
      if (response?.status === 400) {
        throw new Error('Bad request: Invalid parameters');
      } else if (response?.data) {
        throw response.data;
      }
      throw error;
    }
  }
};

export default userProcessInstanceService;
