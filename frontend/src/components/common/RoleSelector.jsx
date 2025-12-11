import React from 'react';
import { Autocomplete, TextField, CircularProgress } from '@mui/material';
import { useTranslation } from 'react-i18next';
import roleService from '../../api/idm/roleService';

/**
 * Common role selection component with API integration
 * @param {Object} props Component props
 * @param {String} props.value Selected role ID
 * @param {Function} props.onChange Selection change handler
 * @param {Boolean} [props.error] Validation error state
 * @param {String} [props.helperText] Helper/error text
 * @param {Boolean} [props.required] Marks field as required
 * @param {Boolean} [props.disableFetch=false] Disable API fetching
 * @param {Array} [props.options=[]] Preloaded role options
 * @returns {JSX.Element} Role selector component
 */
const RoleSelector = ({ 
  label, 
  value, 
  onChange, 
  error, 
  helperText, 
  required,
  disableFetch = false,
  options = []
}) => {
  const { t } = useTranslation();
  const [roles, setRoles] = React.useState(options);
  const [loading, setLoading] = React.useState(false);

  const fetchRoles = async (query) => {
    if (disableFetch) return;
    
    setLoading(true);
    try {
      const response = await roleService.search({ query });
      setRoles(response.data.content || response.data);
    } catch (error) {
      console.error('Error fetching roles:', error);
    } finally {
      setLoading(false);
    }
  };
  
  React.useEffect(() => {
    if (!disableFetch) {
      fetchRoles('');
    } else {
      setRoles(options);
    }
  }, []);

  return (
    <Autocomplete
      fullWidth
      options={disableFetch ? options : roles}
      getOptionLabel={(role) => role.name}
      value={roles.find(role => role.id === value) || null}
      onChange={(e, newValue) => {
        onChange({
          target: {
            value: newValue?.id || ''
          }
        });
      }}
      loading={loading}
      renderInput={(params) => (
        <TextField
          {...params}
          label={label}
          error={error}
          helperText={helperText}
          required={required}
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
  );
};

export default RoleSelector;
