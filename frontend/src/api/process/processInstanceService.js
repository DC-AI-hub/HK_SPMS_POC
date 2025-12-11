import apiConfig from "../apiConfig";

const api = apiConfig();

/**
 * @typedef {Object} ProcessInstanceDTO
 * @property {string} instanceId Process instance ID
 * @property {string} definitionId Process definition ID
 * @property {string} status Process status
 * @property {string} startTime Start timestamp
 * @property {string} [endTime] End timestamp (if completed)
 * @property {string} [businessKey] Business key associated with process instance
 * @property {Array<TaskDTO>} [activeTasks] Active tasks
 */

/**
 * @typedef {Object} TaskDTO
 * @property {string} taskId Task ID
 * @property {string} name Task name
 * @property {string} assignee Assignee ID
 */

/**
 * @typedef {Object} ProcessActivityDTO
 * @property {string} id Activity ID
 * @property {string} processInstanceId
 * @property {string} processDefinitionId
 * @property {string} startTime
 * @property {string} [endTime] End timestamp if completed
 * @property {number} durationInMillis
 * @property {string} activityId
 * @property {string} activityName
 * @property {string} activityType
 * @property {string} assignee
 */

/**
 * @typedef {Object} PageProcessActivity
 * @property {Array<ProcessActivityDTO>} content
 * @property {number} totalElements
 * @property {number} totalPages
 * @property {boolean} last
 * @property {boolean} first
 * @property {number} numberOfElements
 * @property {number} size
 * @property {number} number
 * @property {boolean} empty
 */

const processInstanceService = {
  /**
   * Starts a new process instance
   * 
   * @param {Object} data Process instance request data
   * @param {string} data.definitionId Process definition ID
   * @param {string} [data.formId] Optional form ID
   * @param {Object} [data.formVariable] Form variables
   * @param {Object} [data.variable] Additional process variables
   * @returns {Promise<ProcessInstanceDTO>} Process instance details
   */
  async startProcessInstance(data) {
    try {
      const response = await api.post('/process-instances', data);
      return response.data;
    } catch (error) {
      const { response } = error;
      if (response?.data) throw response.data;
      throw error;
    }
  },

  /**
   * Gets status of a process instance
   * 
   * @param {string} instanceId ID of the process instance
   * @returns {Promise<ProcessInstanceDTO>} Process instance details
   */
  async getProcessInstance(instanceId) {
    try {
      const response = await api.get(`/process-instances/${instanceId}`);
      return response.data;
    } catch (error) {
      const { response } = error;
      if (response?.data) throw response.data;
      throw error;
    }
  },

  /**
   * Retrieves all tasks for a process instance
   * 
   * @param {string} instanceId ID of the process instance
   * @returns {Promise<Array<TaskDTO>>} List of tasks
   */
  async getInstanceTasks(instanceId) {
    try {
      const response = await api.get(`/process-instances/${instanceId}/tasks`);
      return response.data;
    } catch (error) {
      const { response } = error;
      if (response?.data) throw response.data;
      throw error;
    }
  },

  /**
   * Completes a task in a process instance
   * 
   * @param {string} instanceId ID of the process instance
   * @param {string} taskId ID of the task to complete
   * @param {Object} data Task completion data (key-value map of variables)
   * @returns {Promise<void>}
   */
  async completeTask(instanceId, taskId, data) {
    try {
      await api.post(`/process-instances/${instanceId}/tasks/${taskId}/complete`, data);
    } catch (error) {
      const { response } = error;
      if (response?.data) throw response.data;
      throw error;
    }
  },

  /**
   * Rejects a task in a process instance
   * 
   * @param {string} instanceId ID of the process instance
   * @param {string} taskId ID of the task to reject
   * @param {Object} data Rejection data (key-value map of variables)
   * @returns {Promise<void>}
   */
  async rejectTask(instanceId, taskId, data) {
    try {
      await api.post(`/process-instances/${instanceId}/tasks/${taskId}/reject`, data);
    } catch (error) {
      const { response } = error;
      if (response?.data) throw response.data;
      throw error;
    }
  },

  /**
   * Saves draft data to a process instance without completing the task
   * 
   * @param {string} instanceId ID of the process instance
   * @param {string} taskId ID of the task
   * @param {Object} data Draft data (key-value map of variables)
   * @returns {Promise<void>}
   */
  async saveDraft(instanceId, taskId, data) {
    try {
      await api.post(`/process-instances/${instanceId}/tasks/${taskId}/draft`, data);
    } catch (error) {
      const { response } = error;
      if (response?.data) throw response.data;
      throw error;
    }
  },

  /**
   * Retrieves all process instances
   * 
   * @param {Object} pageable Pagination configuration
   * @param {number} pageable.page Page number (0-indexed)
   * @param {number} pageable.size Number of items per page
   * @param {string} pageable.sort Sort criteria (format: "property,direction")
   * @returns {Promise<Array<ProcessInstanceDTO>>} List of process instances
   */
  async getAllProcessInstances(pageable) {
    try {
      const response = await api.get('/process-instances', {
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
   * Retrieves process instances related to the current user
   * 
   * @param {Object} pageable Pagination configuration
   * @param {number} pageable.page Page number (0-indexed)
   * @param {number} pageable.size Number of items per page
   * @param {string} pageable.sort Sort criteria (format: "property,direction")
   * @returns {Promise<Array<ProcessInstanceDTO>>} List of process instances
   */
  async getUserRelatedProcessInstances(pageable) {
    try {
      const response = await api.get('/process-instances/user', {
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
   * Counts active (incomplete) tasks across all process instances
   * 
   * @returns {Promise<number>} count of incomplete tasks
   */
  async countIncompleteTasks() {
    try {
      const response = await api.get('/process-instances/stats/incomplete-tasks');
      return response.data;
    } catch (error) {
      const { response } = error;
      if (response?.data) throw response.data;
      throw error;
    }
  },

  /**
   * Counts completed tasks across all process instances
   * 
   * @returns {Promise<number>} count of completed tasks
   */
  async countCompletedTasks() {
    try {
      const response = await api.get('/process-instances/stats/completed-tasks');
      return response.data;
    } catch (error) {
      const { response } = error;
      if (response?.data) throw response.data;
      throw error;
    }
  },

  /**
   * Counts currently active (running) process instances
   * 
   * @returns {Promise<number>} count of active process instances
   */
  async countRunningProcesses() {
    try {
      const response = await api.get('/process-instances/stats/running-processes');
      return response.data;
    } catch (error) {
      const { response } = error;
      if (response?.data) throw response.data;
      throw error;
    }
  },

  /**
   * Retrieves paginated activity history for a process instance
   * 
   * @param {string} instanceId ID of the process instance
   * @param {Object} pageable Pagination configuration
   * @param {number} pageable.page Page number (0-indexed)
   * @param {number} pageable.size Number of items per page
   * @param {string} pageable.sort Sort criteria (format: "property,direction")
   * @returns {Promise<PageProcessActivity>} Page of activities with metadata
   */
  async getProcessActivities(instanceId, pageable) {
    try {
      const response = await api.get(`/process-instances/${instanceId}/activities`, {
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

export default processInstanceService;
