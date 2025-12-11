import React from 'react';
import {
  TextField as MuiTextField,
  InputAdornment,
  FormHelperText,
  Box
} from '@mui/material';

/**
 * TextField - A generic text input form field component
 * Provides consistent text field styling and functionality
 * 
 * @param {Object} props
 * @param {string} props.label - Field label
 * @param {any} props.value - Current value
 * @param {function} props.onChange - Change handler function
 * @param {string} [props.error] - Error message to display
 * @param {boolean} [props.required] - Whether the field is required
 * @param {boolean} [props.disabled] - Whether the field is disabled
 * @param {string} [props.placeholder] - Placeholder text
 * @param {string} [props.type] - Input type ('text', 'email', 'password', etc.)
 * @param {React.ReactNode} [props.startAdornment] - Start adornment (icon, etc.)
 * @param {React.ReactNode} [props.endAdornment] - End adornment (icon, etc.)
 * @param {number} [props.maxLength] - Maximum character length
 * @param {string} [props.className] - Additional CSS classes
 * @param {Object} [props.style] - Additional inline styles
 * @param {boolean} [props.fullWidth] - Whether the field should take full width
 * @param {string} [props.size] - Size of the field ('small' | 'medium')
 * @param {boolean} [props.multiline] - Whether the field is multiline
 * @param {number} [props.rows] - Number of rows for multiline field
 */
const TextField = ({
  label,
  value,
  onChange,
  error,
  required = false,
  disabled = false,
  placeholder,
  type = 'text',
  startAdornment,
  endAdornment,
  maxLength,
  className = '',
  style = {},
  fullWidth = true,
  size = 'medium',
  multiline = false,
  rows = 3,
  ...props
}) => {
  const handleChange = (event) => {
    let newValue = event.target.value;
    
    // Handle max length if specified
    if (maxLength && newValue.length > maxLength) {
      newValue = newValue.slice(0, maxLength);
    }
    
    onChange(newValue);
  };

  return (
    <Box sx={{ width: fullWidth ? '100%' : 'auto' }}>
      <MuiTextField
        label={label}
        value={value || ''}
        onChange={handleChange}
        error={!!error}
        required={required}
        disabled={disabled}
        placeholder={placeholder}
        type={type}
        fullWidth={fullWidth}
        size={size}
        multiline={multiline}
        rows={multiline ? rows : undefined}
        className={className}
        style={style}
        inputProps={{
          maxLength: maxLength,
          ...props.inputProps
        }}
        InputProps={{
          startAdornment: startAdornment ? (
            <InputAdornment position="start">
              {startAdornment}
            </InputAdornment>
          ) : null,
          endAdornment: endAdornment ? (
            <InputAdornment position="end">
              {endAdornment}
            </InputAdornment>
          ) : null,
          ...props.InputProps
        }}
        {...props}
      />
      
      {(error || maxLength) && (
        <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mt: 0.5 }}>
          {error && (
            <FormHelperText error sx={{ margin: 0 }}>
              {error}
            </FormHelperText>
          )}
          
          {maxLength && (
            <FormHelperText sx={{ margin: 0, ml: 'auto' }}>
              {value?.length || 0}/{maxLength}
            </FormHelperText>
          )}
        </Box>
      )}
    </Box>
  );
};

export default TextField;
