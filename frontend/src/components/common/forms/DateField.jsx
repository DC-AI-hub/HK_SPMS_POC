import React from 'react';
import {
  TextField as MuiTextField,
  InputAdornment,
  FormHelperText,
  Box
} from '@mui/material';
import { DatePicker } from '@mui/x-date-pickers/DatePicker';
import { LocalizationProvider } from '@mui/x-date-pickers/LocalizationProvider';
import { AdapterDayjs } from '@mui/x-date-pickers/AdapterDayjs';
import dayjs from 'dayjs';

/**
 * DateField - A generic date input form field component
 * Provides consistent date field styling and functionality with date picker
 * 
 * @param {Object} props
 * @param {string} props.label - Field label
 * @param {Date|string} props.value - Current date value (Date object or ISO string)
 * @param {function} props.onChange - Change handler function
 * @param {string} [props.error] - Error message to display
 * @param {boolean} [props.required] - Whether the field is required
 * @param {boolean} [props.disabled] - Whether the field is disabled
 * @param {string} [props.placeholder] - Placeholder text
 * @param {Date|string} [props.minDate] - Minimum allowed date
 * @param {Date|string} [props.maxDate] - Maximum allowed date
 * @param {string} [props.format] - Date format string (default: 'YYYY-MM-DD')
 * @param {string} [props.className] - Additional CSS classes
 * @param {Object} [props.style] - Additional inline styles
 * @param {boolean} [props.fullWidth] - Whether the field should take full width
 * @param {string} [props.size] - Size of the field ('small' | 'medium')
 * @param {boolean} [props.showPicker] - Whether to show date picker (default: true)
 */
const DateField = ({
  label,
  value,
  onChange,
  error,
  required = false,
  disabled = false,
  placeholder = 'Select a date',
  minDate,
  maxDate,
  format = 'YYYY-MM-DD',
  className = '',
  style = {},
  fullWidth = true,
  size = 'medium',
  showPicker = true,
  ...props
}) => {
  const handleChange = (newValue) => {
    if (newValue && dayjs(newValue).isValid()) {
      // Return as ISO string for consistency
      onChange(newValue.toISOString());
    } else {
      onChange(null);
    }
  };

  const handleTextChange = (event) => {
    const inputValue = event.target.value;
    
    // Allow empty input
    if (inputValue === '') {
      onChange(null);
      return;
    }
    
    // Try to parse the input as a date
    const parsedDate = dayjs(inputValue, format, true);
    if (parsedDate.isValid()) {
      onChange(parsedDate.toISOString());
    } else {
      // If not valid, still update the text but don't change the date value
      // This allows partial input while typing
    }
  };

  const handleBlur = (event) => {
    const inputValue = event.target.value;
    
    // Validate on blur
    if (inputValue) {
      const parsedDate = dayjs(inputValue, format, true);
      if (!parsedDate.isValid()) {
        // Clear invalid input on blur
        onChange(null);
      }
    }
  };

  // Convert value to dayjs object for the date picker
  const dateValue = value ? dayjs(value) : null;
  const minDateValue = minDate ? dayjs(minDate) : null;
  const maxDateValue = maxDate ? dayjs(maxDate) : null;

  // Format display value for text input
  const displayValue = dateValue ? dateValue.format(format) : '';

  if (showPicker) {
    return (
      <LocalizationProvider dateAdapter={AdapterDayjs}>
        <Box sx={{ width: fullWidth ? '100%' : 'auto' }}>
          <DatePicker
            label={label}
            value={dateValue}
            onChange={handleChange}
            disabled={disabled}
            format={format}
            minDate={minDateValue}
            maxDate={maxDateValue}
            slotProps={{
              textField: {
                error: !!error,
                required,
                fullWidth,
                size,
                placeholder,
                className,
                style,
                helperText: error,
                onBlur: handleBlur,
                ...props
              }
            }}
            {...props}
          />
        </Box>
      </LocalizationProvider>
    );
  }

  // Fallback to text input without date picker
  return (
    <Box sx={{ width: fullWidth ? '100%' : 'auto' }}>
      <MuiTextField
        label={label}
        value={displayValue}
        onChange={handleTextChange}
        onBlur={handleBlur}
        error={!!error}
        required={required}
        disabled={disabled}
        placeholder={placeholder}
        fullWidth={fullWidth}
        size={size}
        className={className}
        style={style}
        InputProps={{
          endAdornment: (
            <InputAdornment position="end">
              <span style={{ color: 'text.disabled', fontSize: '0.875rem' }}>
                {format}
              </span>
            </InputAdornment>
          ),
          ...props.InputProps
        }}
        {...props}
      />
      
      {error && (
        <FormHelperText error sx={{ margin: 0, mt: 0.5 }}>
          {error}
        </FormHelperText>
      )}
    </Box>
  );
};

export default DateField;
