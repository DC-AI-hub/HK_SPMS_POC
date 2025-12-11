import React from 'react';
import { Autocomplete, TextField, CircularProgress } from '@mui/material';
import { useTranslation } from 'react-i18next';
import userService from '../../api/idm/userService';

/**
 * Common user selection component with API integration
 * @param {Object} props Component props
 * @param {String} props.value Selected user ID
 * @param {Function} props.onChange Selection change handler
 * @param {Boolean} [props.error] Validation error state
 * @param {String} [props.helperText] Helper/error text
 * @param {Boolean} [props.required] Marks field as required
 * @param {Boolean} [props.disableFetch=false] Disable API fetching
 * @param {Array} [props.options=[]] Preloaded user options
 * @returns {JSX.Element} User selector component
 */
const UserSelector = ({ 
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
    const [users, setUsers] = React.useState(options);
    const [loading, setLoading] = React.useState(false);

    const fetchUsers = async (query) => {
        if (disableFetch) return;
        
        setLoading(true);
        try {
            const response = await userService.search({query:query});
            setUsers(response.data);
        } catch (error) {
            console.error('Error fetching users:', error);
        } finally {
            setLoading(false);
        }
    };
    
    React.useEffect(() => {
        if (!disableFetch) {
            fetchUsers('');
        } else {
            setUsers(options);
        }
    }, []);

    const onTextChanged = async (e) => {
        if (!loading) {
            fetchUsers(e.target.value);
            //TODO: needs backend support enhance.
        }
    }

    return (
        <Autocomplete
            fullWidth
            options={disableFetch ? options : users}
            getOptionLabel={(user) => `${user.username} (${user.email})`}
            value={users.find(user => user.id === value) || null}
            onChange={(e, newValue) => {
                onChange({
                    id: newValue?.id,
                    target: {
                        name: 'businessOwnerId',
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
                    onChange={onTextChanged}
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

export default UserSelector;
