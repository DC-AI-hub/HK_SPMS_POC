import { useState, useCallback } from 'react';

/**
 * Generic form handling hook
 * @param {Object} [initialValues={}] - Initial form values
 * @returns {Object} Form control functions and values
 */
const useForm = (initialValues = {}) => {
  const [values, setValues] = useState(initialValues);

  const setValue = useCallback((key, value) => {
    setValues(prev => ({ ...prev, [key]: value }));
  }, []);

  const setValuesDirect = useCallback(newValues => {
    setValues(newValues);
  }, []);

  const reset = useCallback(() => {
    setValues(initialValues);
  }, [initialValues]);

  const handleChange = useCallback((event) => {
    const { name, value, type, checked } = event.target;
    setValues(prev => ({
      ...prev,
      [name]: type === 'checkbox' ? checked : value
    }));
  }, []);

  return {
    values,
    setValue,
    setValues: setValuesDirect,
    reset,
    handleChange
  };
};

export default useForm;
