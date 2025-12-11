import React from 'react';
import {
  TextField as MuiTextField,
  InputAdornment,
  FormHelperText,
  Box
} from '@mui/material';

/**
 * NumberField - A generic number input form field component with validation
 * Provides consistent number field styling and functionality with validation
 * 
 * @param {Object} props
 * @param {string} props.label - Field label
 * @param {number} props.value - Current numeric value
 * @param {function} props.onChange - Change handler function
 * @param {string} [props.error] - Error message to display
 * @param {boolean} [props.required] - Whether the field is required
 * @param {boolean} [props.disabled] - Whether the field is disabled
 * @param {string} [props.placeholder] - Placeholder text
 * @param {number} [props.min] - Minimum allowed value
 * @param {number} [props.max] - Maximum allowed value
 * @param {number} [props.step] - Step increment for arrows
 * @param {React.ReactNode} [props.startAdornment] - Start adornment (icon, etc.)
 * @param {React.ReactNode} [props.endAdornment] - End adornment (icon, etc.)
 * @param {string} [props.className] - Additional CSS classes
 * @param {Object} [props.style] - Additional inline styles
 * @param {boolean} [props.fullWidth] - Whether the field should take full width
 * @param {string} [props.size] - Size of the field ('small' | 'medium')
 * @param {boolean} [props.allowDecimal] - Whether to allow decimal values
 * @param {number} [props.decimalPlaces] - Number of decimal places to allow
 */
const NumberField = ({
  label,
  value,
  onChange,
  error,
  required = false,
  disabled = false,
  placeholder = 'Enter a number',
  min,
  max,
  step = 1,
  startAdornment,
  endAdornment,
  className = '',
  style = {},
  fullWidth = true,
  size = 'medium',
  allowDecimal = true,
  decimalPlaces = 2,
  ...props
}) => {
  const handleChange = (event) => {
    let inputValue = event.target.value;
    
    // Allow empty input for clearing the field
    if (inputValue === '') {
      onChange(null);
      return;
    }
    
    // Validate numeric input
    const numericRegex = allowDecimal ? /^-?\d*\.?\d*$/ : /^-?\d*$/;
    if (!numericRegex.test(inputValue)) {
      return; // Don't update if invalid
    }
    
    // Convert to number and apply constraints
    let numericValue = parseFloat(inputValue);
    
    // Handle NaN case (shouldn't happen with regex, but just in case)
    if (isNaN(numericValue)) {
      return;
    }
    
    // Apply min/max constraints
    if (min !== undefined && numericValue < min) {
      numericValue = min;
    }
    if (max !== undefined && numericValue > max) {
      numericValue = max;
    }
    
    // Round to specified decimal places if needed
    if (!allowDecimal) {
      numericValue = Math.round(numericValue);
    } else if (decimalPlaces !== undefined) {
      numericValue = parseFloat(numericValue.toFixed(decimalPlaces));
    }
    
    onChange(numericValue);
  };

  const handleBlur = (event) => {
    const inputValue = event.target.value;
    
    // Format the value on blur to ensure proper decimal formatting
    if (inputValue && !isNaN(parseFloat(inputValue))) {
      let numericValue = parseFloat(inputValue);
      
      // Apply min/max constraints
      if (min !== undefined && numericValue < min) {
        numericValue = min;
      }
      if (max !== undefined && numericValue > max) {
        numericValue = max;
      }
      
      // Round to specified decimal places
      if (allowDecimal && decimalPlaces !== undefined) {
        numericValue = parseFloat(numericValue.toFixed(decimalPlaces));
      } else if (!allowDecimal) {
        numericValue = Math.round(numericValue);
      }
      
      // Only update if the value actually changed (to avoid infinite loops)
      if (numericValue !== value) {
        onChange(numericValue);
      }
    }
  };

  // Format the display value with proper decimal places
  const displayValue = value !== null && value !== undefined 
    ? (allowDecimal && decimalPlaces ? value.toFixed(decimalPlaces) : value.toString())
    : '';

  return (
    <Box sx={{ width: fullWidth ? '100%' : 'auto' }}>
      <MuiTextField
        label={label}
        value={displayValue}
        onChange={handleChange}
        onBlur={handleBlur}
        error={!!error}
        required={required}
        disabled={disabled}
        placeholder={placeholder}
        type="text" // Use text type to handle formatting ourselves
        fullWidth={fullWidth}
        size={size}
        className={className}
        style={style}
        inputProps={{
          inputMode: 'decimal',
          pattern: allowDecimal ? '-?[0-9]*\.?[0-9]*' : '-?[0-9]*',
          min: min,
          max: max,
          step: step,
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
      
      {error && (
        <FormHelperText error sx={{ margin: 0, mt: 0.5 }}>
          {error}
        </FormHelperText>
      )}
      
      {(min !== undefined || max !== undefined) && !error && (
        <FormHelperText sx={{ margin: 0, mt: 0.5, fontSize: '0.75rem' }}>
          {min !== undefined && `Min: ${min}`}
          {min !== undefined && max !== undefined && ' • '}
          {max !== undefined && `Max: ${max}`}
        </FormHelperText>
      )}
    </Box>
  );
};

export default NumberField;
