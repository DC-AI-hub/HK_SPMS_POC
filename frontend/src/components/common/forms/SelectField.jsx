import React from 'react';
import {
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  FormHelperText,
  Box
} from '@mui/material';

/**
 * SelectField - A generic select/dropdown form field component
 * Provides consistent select field styling and functionality
 * 
 * @param {Object} props
 * @param {string} props.label - Field label
 * @param {any} props.value - Current value
 * @param {function} props.onChange - Change handler function
 * @param {Array} props.options - Array of option objects { value: any, label: string }
 * @param {string} [props.error] - Error message to display
 * @param {boolean} [props.required] - Whether the field is required
 * @param {boolean} [props.disabled] - Whether the field is disabled
 * @param {string} [props.placeholder] - Placeholder text when no value is selected
 * @param {string} [props.className] - Additional CSS classes
 * @param {Object} [props.style] - Additional inline styles
 * @param {boolean} [props.fullWidth] - Whether the field should take full width
 * @param {string} [props.size] - Size of the field ('small' | 'medium')
 */
const SelectField = ({
  label,
  value,
  onChange,
  options = [],
  error,
  required = false,
  disabled = false,
  placeholder = 'Select an option',
  className = '',
  style = {},
  fullWidth = true,
  size = 'medium',
  ...props
}) => {
  const handleChange = (event) => {
    onChange(event.target.value);
  };

  const labelId = `${label?.replace(/\s+/g, '-').toLowerCase()}-label`;

  return (
    <FormControl
      fullWidth={fullWidth}
      error={!!error}
      disabled={disabled}
      size={size}
      className={className}
      style={style}
      {...props}
    >
      {label && (
        <InputLabel id={labelId} required={required}>
          {label}
        </InputLabel>
      )}
      
      <Select
        labelId={labelId}
        value={value || ''}
        onChange={handleChange}
        label={label}
        displayEmpty
        renderValue={(selected) => {
          if (!selected) {
            return (
              <Box component="span" sx={{ color: 'text.disabled' }}>
                {placeholder}
              </Box>
            );
          }
          
          const selectedOption = options.find(opt => opt.value === selected);
          return selectedOption?.label || selected;
        }}
      >
        <MenuItem value="" disabled>
          <em>{placeholder}</em>
        </MenuItem>
        
        {options.map((option) => (
          <MenuItem key={option.value} value={option.value}>
            {option.label}
          </MenuItem>
        ))}
      </Select>
      
      {error && (
        <FormHelperText error>{error}</FormHelperText>
      )}
    </FormControl>
  );
};

export default SelectField;
