import React from 'react';
import { Box, Paper, Typography } from '@mui/material';

/**
 * FormLayout - A generic layout component for forms
 * Provides consistent styling and structure for form layouts
 * 
 * @param {Object} props
 * @param {React.ReactNode} props.children - Form content
 * @param {string} [props.title] - Optional form title
 * @param {string} [props.subtitle] - Optional form subtitle
 * @param {number} [props.maxWidth] - Maximum width of the form (px)
 * @param {boolean} [props.elevation] - Paper elevation (0-24)
 * @param {string} [props.className] - Additional CSS classes
 * @param {Object} [props.style] - Additional inline styles
 */
const FormLayout = ({
  children,
  title,
  subtitle,
  maxWidth = 600,
  elevation = 2,
  className = '',
  style = {},
  ...props
}) => {
  return (
    <Box
      sx={{
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
        width: '100%',
        p: 2
      }}
      className={className}
      style={style}
      {...props}
    >
      <Paper
        elevation={elevation}
        sx={{
          width: '100%',
          maxWidth: maxWidth,
          p: 3,
          display: 'flex',
          flexDirection: 'column',
          gap: 2
        }}
      >
        {(title || subtitle) && (
          <Box sx={{ mb: 2 }}>
            {title && (
              <Typography variant="h5" component="h2" gutterBottom>
                {title}
              </Typography>
            )}
            {subtitle && (
              <Typography variant="body2" color="text.secondary">
                {subtitle}
              </Typography>
            )}
          </Box>
        )}
        
        {children}
      </Paper>
    </Box>
  );
};

export default FormLayout;
