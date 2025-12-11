import apiConfig from "../apiConfig";

const api = apiConfig();

const handleError = (error) => {
  const { response } = error;
  if (response && response.data) {
    throw response.data;
  }
  throw error;
}

const handleResponse = (response) => {
  return response.data;
}

/**
 * Service for interacting with Process APIs
 * 
 * DTO Structures:
 * 
 * ProcessDefinitionDTO: {
 *   id: string,          // Generated definition ID
 *   name: string,        // Process name
 *   key: string,         // Process key
 *   status: string,      // Current status
 *   description: string, // Process description
 *   owner: {             // Owner reference
 *     id: string, 
 *     name: string 
 *   },
 *   businessOwner: {     // Business owner reference
 *     id: string,
 *     name: string
 *   },
 *   createdTime: string  // Creation timestamp
 * }
 * 
 * ProcessDefinitionVersionDTO: {
 *   id: string,          // Version ID
 *   name: string,        // Process name
 *   key: string,         // Process key
 *   version: string,     // Version string
 *   status: string,      // Version status (DRAFT, ACTIVE, etc.)
 *   bpmnXml: string      // BPMN XML content
 * }
 * 
 * ProcessInstanceDTO: {
 *   instanceId: string,  // Instance ID
 *   definitionId: string,// Definition ID
 *   status: string,      // Current status
 *   startTime: string,   // Start timestamp
 *   endTime: string,     // End timestamp (if completed)
 *   activeTasks: [       // Array of active tasks
 *     {
 *       taskId: string,  // Task ID
 *       name: string,    // Task name
 *       assignee: string // Assignee ID
 *     }
 *   ]
 * }
 * 
 * TaskDTO: {
 *   taskId: string,      // Task ID
 *   name: string,        // Task name
 *   assignee: string     // Assignee ID
 * }
 */
const processService = {
  /**
   * Creates a new process definition version (status: DRAFT)
   * 
   * @param {Object} data Process definition details:
   * @param {string} data.processName - Human-readable name
   * @param {string} data.processKey - Business key
   * @param {string} data.processDescription - Process description
   * @param {string} data.owner - ID of owner user
   * @param {string} data.businessOwner - ID of business owner
   * @returns {Promise<Object>} ProcessDefinitionDTO
   * @throws {Error} 400 for invalid request, 500 for server error
   */
  createProcessDefinition(data) {
    return api.post('/process/definitions', data)
      .then(handleResponse)
      .catch(handleError);
  },

  /**
   * Creates a new version for an existing process definition
   * 
   * @param {string} definitionId Process definition ID
   * @param {Object} data Version data:
   * @param {string} data.bpmnXml - Complete BPMN 2.0 XML
   * @param {string} data.version - Version string (e.g., "1.1")
   * @returns {Promise<Object>} ProcessDefinitionVersionDTO
   * @throws {Error} 404 if definition not found, 400 for invalid request
   */
  createProcessDefinitionVersion(definitionId, data) {
    return api.post(`/process/definitions/${definitionId}/versions`, data)
      .then(handleResponse)
      .catch(handleError);
  },
  
  /**
   * Get process definitions with optional search and pagination
   * 
   * @param {Object} params Query parameters:
   * @param {string} [params.search] - Optional search term
   * @param {number} [params.page] - Page number
   * @param {number} [params.size] - Page size
   * @param {string} [params.sort] - Sort criteria
   * @returns {Promise<Object>} Page of ProcessDefinitionDTO
   * @throws {Error} 400 for invalid parameters
   */
  getProcessDefinitions(params = {}) {
    return api.get('/process/definitions', { params })
      .then(handleResponse)
      .catch(handleError);
  },
  
  /**
   * Search process definitions by name or key
   * 
   * @param {string} searchTerm Search term
   * @param {Object} pagination Pagination config
   * @returns {Promise<Object>} Page of ProcessDefinitionDTO
   * @throws {Error} 400 for invalid parameters
   */
  searchProcessDefinitions(searchTerm, pagination) {
    return api.get('/process/definitions/search', { 
      params: { search: searchTerm, ...pagination } 
    })
      .then(handleResponse)
      .catch(handleError);
  },

  /**
   * Get process definition by ID
   * 
   * @param {string} definitionId Process definition ID
   * @returns {Promise<Object>} ProcessDefinitionDTO with version details
   * @throws {Error} 404 if definition not found
   */
  getProcessDefinition(definitionId) {
    return api.get(`/process/definitions/${definitionId}`)
      .then(handleResponse)
      .catch(handleError);
  },

  /**
   * Get all versions of a process definition
   * 
   * @param {string} definitionId Process definition ID
   * @param {Object} pagination Pagination config
   * @returns {Promise<Object>} Page of ProcessDefinitionVersionDTO
   * @throws {Error} 404 if definition not found
   */
  getDefinitionVersions(definitionId, pagination) {
    return api.get(`/process/definitions/${definitionId}/versions`, { params: pagination })
      .then(handleResponse)
      .catch(handleError);
  },

  getProcessVersionByDeploymentId(deploymentId) {
    return api.get(`/process/definitions/deployments/${deploymentId}`).then(handleResponse).catch(handleError);
  },

  //
  /**
   * Get specific version of a process definition
   * 
   * @param {string} definitionId Process definition ID
   * @param {string} versionId Version ID
   * @returns {Promise<Object>} ProcessDefinitionVersionDTO
   * @throws {Error} 404 if version not found
   */
  getDefinitionVersion(definitionId, versionId) {
    return api.get(`/process/definitions/${definitionId}/versions/${versionId}`)
      .then(handleResponse)
      .catch(handleError);
  },

  /**
   * Activates a specific version of a process definition
   * 
   * @param {string} definitionId Process definition ID
   * @param {string} version Version to activate
   * @returns {Promise} Empty response on success
   * @throws {Error} 404 if definition/version not found, 400 if activation fails
   */
  activateDefinitionVersion(definitionId, version) {
    return api.post(`/process/definitions/${definitionId}/versions/${version}/active`)
      .then(handleResponse)
      .catch(handleError);
  },

  /**
   * Deactivates a specific version of a process definition
   * 
   * @param {string} definitionId Process definition ID
   * @param {string} versionId Version ID to deactivate
   * @returns {Promise} Empty response on success
   * @throws {Error} 404 if definition/version not found, 400 if deactivation fails
   */
  deactivateDefinitionVersion(definitionId, versionId) {
    return api.delete(`/process/definitions/${definitionId}/versions/${versionId}/active`)
      .then(handleResponse)
      .catch(handleError);
  },

  /**
   * Starts a new process instance
   * 
   * @param {Object} data Instance request data:
   * @param {string} data.definitionId - Process definition ID
   * @param {string} [data.formId] - Optional form ID
   * @param {Object} [data.formVariable] - Form variables
   * @param {Object} [data.variable] - Additional process variables
   * @returns {Promise<Object>} ProcessInstanceDTO
   * @throws {Error} 404 if definition not found, 400 if process fails to start
   */
  startInstance(data) {
    return api.post('/process/instances', data)
      .then(handleResponse)
      .catch(handleError);
  },

  /**
   * Gets status of a process instance
   * 
   * @param {string} instanceId Instance ID
   * @returns {Promise<Object>} ProcessInstanceDTO
   * @throws {Error} 404 if instance not found
   */
  getInstanceStatus(instanceId) {
    return api.get(`/process/instances/${instanceId}`)
      .then(handleResponse)
      .catch(handleError);
  },

  /**
   * Retrieves all tasks for a process instance
   * 
   * @param {string} instanceId Instance ID
   * @returns {Promise<Array>} List of TaskDTO
   * @throws {Error} 404 if instance not found
   */
  getInstanceTasks(instanceId) {
    return api.get(`/process/instances/${instanceId}/tasks`)
      .then(handleResponse)
      .catch(handleError);
  },

  /**
   * Completes a task in a process instance
   * 
   * @param {string} instanceId Instance ID
   * @param {string} taskId Task ID
   * @param {Object} data Task completion data
   * @returns {Promise} Empty response on success
   * @throws {Error} 404 if task/instance not found, 400 if completion fails
   */
  completeTask(instanceId, taskId, data) {
    return api.post(`/process/instances/${instanceId}/tasks/${taskId}/complete`, data)
      .then(handleResponse)
      .catch(handleError);
  },

  /**
   * Updates an existing process definition version (only for DRAFT versions)
   * 
   * @param {string} definitionId Process definition ID
   * @param {string} versionId Version ID
   * @param {Object} data Version data to update:
   * @param {string} [data.formKey] - Form key
   * @param {string} [data.formVersion] - Form version
   * @param {string} [data.version] - Version string
   * @param {string} [data.bpmnXml] - BPMN XML content
   * @param {string} [data.description] - Version description
   * @returns {Promise<Object>} Updated ProcessDefinitionVersionDTO
   * @throws {Error} 404 if version not found, 400 for invalid request
   */
  updateProcessDefinitionVersion(definitionId, versionId, data) {
    return api.put(`/process/definitions/${definitionId}/versions/${versionId}`, data)
      .then(handleResponse)
      .catch(handleError);
  }
};

export default processService;
