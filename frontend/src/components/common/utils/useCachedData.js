import { useState, useEffect, useCallback } from 'react';

/**
 * Generic data fetching hook with caching
 * @param {string} key - Cache key for storing/retrieving data
 * @param {Function} fetchFunction - Async function to fetch data
 * @param {Array} [dependencies=[]] - Dependencies to trigger refetch
 * @returns {Object} Data, loading state, and refetch function
 */
const useCachedData = (key, fetchFunction, dependencies = []) => {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const fetchData = useCallback(async () => {
    setLoading(true);
    setError(null);
    
    try {
      // Check if we have cached data
      const cachedData = localStorage.getItem(`cache_${key}`);
      const cachedTimestamp = localStorage.getItem(`cache_timestamp_${key}`);
      
      // Use cached data if it's less than 5 minutes old
      if (cachedData && cachedTimestamp) {
        const cacheAge = Date.now() - parseInt(cachedTimestamp, 10);
        if (cacheAge < 5 * 60 * 1000) { // 5 minutes
          setData(JSON.parse(cachedData));
          setLoading(false);
          return;
        }
      }
      
      // Fetch fresh data
      const result = await fetchFunction();
      setData(result);
      
      // Cache the data
      localStorage.setItem(`cache_${key}`, JSON.stringify(result));
      localStorage.setItem(`cache_timestamp_${key}`, Date.now().toString());
    } catch (err) {
      setError(err);
      console.error(`Error fetching data for key ${key}:`, err);
    } finally {
      setLoading(false);
    }
  }, [key, fetchFunction]);

  const refetch = useCallback(() => {
    // Clear cache and fetch fresh data
    localStorage.removeItem(`cache_${key}`);
    localStorage.removeItem(`cache_timestamp_${key}`);
    fetchData();
  }, [key, fetchData]);

  const clearCache = useCallback(() => {
    localStorage.removeItem(`cache_${key}`);
    localStorage.removeItem(`cache_timestamp_${key}`);
    setData(null);
  }, [key]);

  useEffect(() => {
    fetchData();
  }, [fetchData, ...dependencies]);

  return {
    data,
    loading,
    error,
    refetch,
    clearCache
  };
};

export default useCachedData;
