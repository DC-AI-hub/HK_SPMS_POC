// Axios interceptor for request/response inspection
const inspectRequest = (config) => {
  console.log('Request:', {
    url: config.url,
    method: config.method,
    params: config.params,
    data: config.data,
    headers: config.headers
  });
  return config;
};

const inspectResponse = (response) => {
  console.log('Response:', {
    status: response.status,
    data: response.data,
    headers: response.headers
  });
  return response;
};


var isRedirecting = false;

const inspectError = (error) => {
  if(error.response.status === 401 && !isRedirecting){
    isRedirecting = true;
    document.location.href = "/spms/oauth2/authorization/keycloak"
  }

  console.error('API Error:', {
    message: error.message,
    config: error.config,
    response: error.response
  });
  return Promise.reject(error);
};

export const handleResponse = (response) => {
  if (response.status >= 200 && response.status < 300) {
    return response.data;
  }
  return Promise.reject(response);
};

export const handleError = (error) => {
  console.error('API Error:', error);
  return Promise.reject(error);
};

export const setupAxiosInspector = (axiosInstance) => {
  //axiosInstance.response.use(handleResponse, handleError);
  
  axiosInstance.interceptors.request.use(inspectRequest);
  axiosInstance.interceptors.response.use(inspectResponse, inspectError);
  
};
