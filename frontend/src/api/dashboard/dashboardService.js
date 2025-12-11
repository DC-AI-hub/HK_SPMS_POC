import axios from 'axios';
import { getApiConfig } from '../apiConfig';

const BASE_URL = '/api/v1/dashboard';

export const getDashboardStats = async () => {
  try {
    const config = getApiConfig();
    const response = await axios.get(`${BASE_URL}/stats`, config);
    return response.data;
  } catch (error) {
    console.error('Error fetching dashboard stats:', error);
    throw error;
  }
};

export const getRecentActivities = async () => {
  try {
    const config = getApiConfig();
    const response = await axios.get(`${BASE_URL}/activities`, config);
    return response.data;
  } catch (error) {
    console.error('Error fetching recent activities:', error);
    throw error;
  }
};
