import React, { useState, useEffect } from 'react';
import { useTranslation } from 'react-i18next';
import TextField from '@mui/material/TextField';
import CircularProgress from '@mui/material/CircularProgress';
import Alert from '@mui/material/Alert';
import formService from '../../api/process/formService';

/**
 * Form Key Input Component
 * 
 * Provides a text input for form keys with real-time validation against existing keys
 * 
 * Props:
 *   value: Current form key value
 *   onChange: Callback function when value changes
 *   error: External validation error
 *   label: Custom label for the input (optional)
 */
const FormKeyInput = ({ value, onChange, error, label }) => {
  const { t } = useTranslation("form");
  const [loading, setLoading] = useState(false);
  const [apiError, setApiError] = useState(null);
  const [existingKeys, setExistingKeys] = useState([]);
  const [keyExists, setKeyExists] = useState(false);

  // Fetch existing form keys on mount
  useEffect(() => {
    const fetchFormKeys = async () => {
      setLoading(true);
      try {
        console.log("--------------------fetching form keys-------------------")
        const keys = await formService.getAllFormKeys();
        console.log(keys);
        setExistingKeys(keys);
        setApiError(null);
      } catch (err) {
        setApiError(t('keyInput.error'));
        console.error('Error fetching form keys:', err);
      } finally {
        setLoading(false);
      }
    };

    fetchFormKeys();
  }, [t]);

  // Validate key against existing keys
  useEffect(() => {
    if (!value || value.length === 0) {
      setKeyExists(false);
      return;
    }

    // Check if key exists in the fetched list
    const exists = existingKeys.some(key => key === value);
    setKeyExists(exists);
  }, [value, existingKeys]);

  const handleChange = (e) => {
    // Limit to 64 characters
    const newValue = e.target.value.slice(0, 64);
    onChange(newValue);
  };

  const helperTextRender = () => {
    if (loading) {
      return t('keyInput.loading');
    }
    if (apiError) {
      return apiError;
    }
    if (keyExists) {
      return t('keyInput.existsWarning');
    }
    return null;
  };

  return (
    <div>
      <TextField
        fullWidth
        value={value || ''}
        onChange={handleChange}
        label={label || t('keyInput.label')}
        error={apiError || keyExists}
        helperText={helperTextRender()}
        slotProps={{
          input: {
            endAdornment: loading ? <CircularProgress size={20} /> : null
          }
        }}
      />
    </div>
  );
};

export default FormKeyInput;
