import React from 'react';
import { Card, CardContent, CardHeader, Box } from '@mui/material';

/**
 * CardLayout - A generic card container component
 * Provides consistent card styling with header, content, and actions areas
 * 
 * @param {Object} props
 * @param {React.ReactNode} props.children - Card content
 * @param {string} [props.title] - Card title
 * @param {React.ReactNode} [props.subheader] - Card subheader content
 * @param {React.ReactNode} [props.action] - Action elements (buttons, icons, etc.)
 * @param {string} [props.variant] - Card variant ('elevated' | 'outlined')
 * @param {number} [props.elevation] - Card elevation (0-24)
 * @param {boolean} [props.padding] - Whether to add padding to card content
 * @param {string} [props.className] - Additional CSS classes
 * @param {Object} [props.style] - Additional inline styles
 */
const CardLayout = ({
  children,
  title,
  subheader,
  action,
  variant = 'elevated',
  elevation = 1,
  padding = true,
  className = '',
  style = {},
  ...props
}) => {
  return (
    <Card
      variant={variant}
      elevation={variant === 'elevated' ? elevation : 0}
      sx={{
        width: '100%',
        height: '100%',
        display: 'flex',
        flexDirection: 'column'
      }}
      className={className}
      style={style}
      {...props}
    >
      {(title || subheader || action) && (
        <CardHeader
          title={title}
          subheader={subheader}
          action={action}
          sx={{
            pb: 1,
            '& .MuiCardHeader-content': {
              overflow: 'hidden'
            }
          }}
        />
      )}
      
      <CardContent
        sx={{
          flex: 1,
          p: padding ? 2 : 0,
          '&:last-child': {
            pb: padding ? 2 : 0
          }
        }}
      >
        {children}
      </CardContent>
    </Card>
  );
};

export default CardLayout;
