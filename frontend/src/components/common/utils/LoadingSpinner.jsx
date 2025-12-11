import React from 'react';
import { CircularProgress, Box } from '@mui/material';

/**
 * Generic loading spinner component
 * @param {Object} props
 * @param {string} [props.size] - Size of the spinner ('small', 'medium', 'large')
 * @param {string} [props.color] - Color of the spinner ('primary', 'secondary', 'inherit')
 * @param {boolean} [props.center] - Whether to center the spinner in its container
 * @param {string} [props.message] - Optional loading message to display
 */
const LoadingSpinner = ({ 
  size = 'medium', 
  color = 'primary', 
  center = false, 
  message = null 
}) => {
  const sizeMap = {
    small: 20,
    medium: 40,
    large: 60
  };

  const spinner = (
    <CircularProgress 
      size={sizeMap[size] || sizeMap.medium} 
      color={color}
    />
  );

  if (center) {
    return (
      <Box
        display="flex"
        flexDirection="column"
        alignItems="center"
        justifyContent="center"
        minHeight={message ? '120px' : '100px'}
        gap={message ? 2 : 0}
      >
        {spinner}
        {message && (
          <Box color="text.secondary" fontSize="0.875rem">
            {message}
          </Box>
        )}
      </Box>
    );
  }

  if (message) {
    return (
      <Box display="flex" alignItems="center" gap={2}>
        {spinner}
        <Box color="text.secondary" fontSize="0.875rem">
          {message}
        </Box>
      </Box>
    );
  }

  return spinner;
};

export default LoadingSpinner;
