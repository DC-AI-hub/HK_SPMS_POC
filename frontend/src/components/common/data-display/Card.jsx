import React from 'react';
import {
  Paper,
  CardContent,
  CardHeader,
  CardActions,
  Typography,
  Box,
  IconButton,
  Divider
} from '@mui/material';
import { MoreVert as MoreVertIcon } from '@mui/icons-material';

/**
 * Card - A generic card component for displaying content with consistent styling
 * Provides flexible card layouts with header, content, and actions sections
 * 
 * @param {Object} props
 * @param {string} props.title - Card title
 * @param {React.ReactNode} props.children - Card content
 * @param {React.ReactNode} [props.actions] - Action buttons or elements
 * @param {React.ReactNode} [props.avatar] - Avatar or icon for the header
 * @param {React.ReactNode} [props.subheader] - Subheader text
 * @param {function} [props.onMenuClick] - Function to call when menu button is clicked
 * @param {string} [props.variant] - Card variant ('elevation' | 'outlined')
 * @param {number} [props.elevation] - Elevation level (0-24)
 * @param {boolean} [props.hover] - Whether to show hover effect
 * @param {string} [props.size] - Card size ('small' | 'medium' | 'large')
 * @param {boolean} [props.divider] - Whether to show divider between header and content
 * @param {string} [props.className] - Additional CSS classes
 * @param {Object} [props.style] - Additional inline styles
 */
const Card = ({
  title,
  children,
  actions,
  avatar,
  subheader,
  onMenuClick,
  variant = 'elevation',
  elevation = 1,
  hover = false,
  size = 'medium',
  divider = false,
  className = '',
  style = {},
  ...props
}) => {
  const getPadding = () => {
    switch (size) {
      case 'small':
        return 1.5;
      case 'large':
        return 3;
      case 'medium':
      default:
        return 2;
    }
  };

  return (
    <Paper
      variant={variant}
      elevation={variant === 'elevation' ? elevation : 0}
      sx={{
        width: '100%',
        transition: hover ? 'box-shadow 0.2s ease-in-out, transform 0.2s ease-in-out' : 'none',
        '&:hover': hover ? {
          boxShadow: 4,
          transform: 'translateY(-2px)'
        } : {},
        ...style
      }}
      className={className}
      {...props}
    >
      {(title || avatar || onMenuClick) && (
        <>
          <CardHeader
            avatar={avatar}
            action={
              onMenuClick && (
                <IconButton aria-label="settings" onClick={onMenuClick}>
                  <MoreVertIcon />
                </IconButton>
              )
            }
            title={
              <Typography variant="h6" component="h3">
                {title}
              </Typography>
            }
            subheader={subheader && (
              <Typography variant="body2" color="text.secondary">
                {subheader}
              </Typography>
            )}
            sx={{
              padding: getPadding(),
              '& .MuiCardHeader-content': {
                overflow: 'hidden'
              }
            }}
          />
          {divider && <Divider />}
        </>
      )}

      <CardContent sx={{ padding: getPadding() }}>
        {children}
      </CardContent>

      {actions && (
        <>
          {divider && <Divider />}
          <CardActions sx={{ padding: getPadding(), pt: divider ? getPadding() : undefined }}>
            {actions}
          </CardActions>
        </>
      )}
    </Paper>
  );
};

export default Card;
