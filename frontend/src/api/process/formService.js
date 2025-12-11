import apiConfig from "../apiConfig";

const api = apiConfig();

const handleResponse=(response) => {
  return response.data;
}



const handleError = (error) => {
  const { response } = error;
  if (response && response.data) {
    throw response.data;
  }
  throw error;
}


/**
 * Service for interacting with Form APIs
 */
const formService = {
  /**
   * Gets all distinct form keys
   * @returns {Promise<string[]>} List of form keys
   */
  getAllFormKeys() {
    return api.get('/forms')
      .then(handleResponse)
      .catch(handleError);
  },
  
  /**
   * Creates a new form version
   * @param {string} key Form definition key
   * @param {Object} data Form definition data
   * @returns {Promise<Object>} Created form version
   * @throws ValidationException if input validation fails (400)
   * @throws NotFoundException if form not found (404)
   */
  createNewVersion(key, data) {
    return api.post(`forms/${key}/versions`, data)
      .then(handleResponse)
      .catch(error => {
        if (error.response?.status === 400) {
          throw new ValidationException(error.response.data.detail);
        }
        if (error.response?.status === 404) {
          throw new NotFoundException(`Form ${key} not found`);
        }
        handleError(error);
      });
  },

  /**
   * Gets the latest version of a form
   * @param {string} key Form definition key
   * @returns {Promise<Object>} Latest form version
   * @throws NotFoundException if form definition not found
   */
  getLatestVersion(key) {
    return api.get(`forms/${key}/versions/latest`)
      .then(handleResponse)
      .catch(error => {
        if (error.response?.status === 404) {
          throw new NotFoundException(`Form ${key} not found`);
        }
        handleError(error);
      });
  },

  /**
   * Gets a specific version of a form
   * @param {string} key @NotBlank Form definition key
   * @param {string} version @Pattern(regexp="[a-zA-Z0-9\\-\\.]+") Version identifier
   * @returns {Promise} Axios promise
   * @throws NotFoundException if form or version not found
   */
  getVersion(key, version) {
    return api.get(`forms/${key}/versions/${version}`)
      .then(handleResponse)
      .catch(error => {
        if (error.response?.status === 404) {
          throw new NotFoundException(`Version ${version} of form ${key} not found`);
        }
        handleError(error);
      });
  },

  /**
   * Lists all versions of a form
   * @param {string} key @NotBlank Form definition key
   * @returns {Promise} Axios promise
   * @throws NotFoundException if form definition not found
   */
  listVersions(key) {
    return api.get(`forms/${key}/versions`)
      .then(handleResponse)
      .catch(error => {
        if (error.response?.status === 404) {
          throw new NotFoundException(`Form ${key} not found`);
        }
        handleError(error);
      });
  },

  /**
   * Deprecates a form version
   * @param {string} key @NotBlank Form definition key
   * @param {string} version @Pattern(regexp="[a-zA-Z0-9\\-\\.]+") Version identifier
   * @returns {Promise} Axios promise
   * @throws NotFoundException if form or version not found
   * @throws ValidationException if version is already deprecated
   */
  deprecateVersion(key, version) {
    return api.post(`forms/${key}/versions/${version}/deprecate`)
      .then(handleResponse)
      .catch(error => {
        if (error.response?.status === 404) {
          throw new NotFoundException(`Version ${version} of form ${key} not found`);
        }
        if (error.response?.status === 400) {
          throw new ValidationException(error.response.data.detail);
        }
        handleError(error);
      });
  }
};

// Custom exceptions
export class ValidationException extends Error {
  constructor(message) {
    super(message);
    this.name = 'ValidationException';
  }
}

export class NotFoundException extends Error {
  constructor(message) {
    super(message);
    this.name = 'NotFoundException';
  }
}

export default formService;
