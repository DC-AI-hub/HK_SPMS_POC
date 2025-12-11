import React, { useState, useEffect } from 'react';
import { useTranslation } from 'react-i18next';
import Autocomplete from '@mui/material/Autocomplete';
import TextField from '@mui/material/TextField';
import CircularProgress from '@mui/material/CircularProgress';
import Alert from '@mui/material/Alert';
import formService from '../../api/process/formService';

/**
 * Form Key Selector Component
 * 
 * Provides a searchable selector for form keys using the getAllFormKeys API
 * 
 * Props:
 *   value: Currently selected form key
 *   onChange: Callback function when selection changes
 *   error: External validation error
 *   label: Custom label for the selector (optional)
 */
const FormKeySelector = ({ value, onChange, error, label }) => {
  const { t } = useTranslation();
  const [formKeys, setFormKeys] = useState([]);
  const [loading, setLoading] = useState(false);
  const [apiError, setApiError] = useState(null);

  useEffect(() => {
    const fetchFormKeys = async () => {
      setLoading(true);
      try {
        const keys = await formService.getAllFormKeys();
        setFormKeys(keys);
        setApiError(null);
      } catch (err) {
        setApiError(t('form:keySelector.error'));
        console.error('Error fetching form keys:', err);
      } finally {
        setLoading(false);
      }
    };

    fetchFormKeys();
  }, [t]);

  return (
    <div>
      <Autocomplete
        id="form-key-selector"
        sx={{ width: 300 }}
        options={formKeys}
        value={value}
        error={!apiError? apiError :false }
        onChange={(event, newValue) => onChange(newValue)}
        loading={loading}
        renderInput={(params) => (
          <TextField
            {...params}
            label={label || t('form:keySelector.placeholder')}
            error={!!error}
            helperText={error}
            InputProps={{
              ...params.InputProps,
              endAdornment: (
                <>
                  {loading ? <CircularProgress color="inherit" size={20} /> : null}
                  {params.InputProps.endAdornment}
                </>
              ),
            }}
          />
        )}
      />
    </div>
  );
};

export default FormKeySelector;
