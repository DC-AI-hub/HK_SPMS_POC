import React from 'react';
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  IconButton,
  Box,
  Typography
} from '@mui/material';
import { Close as CloseIcon } from '@mui/icons-material';

/**
 * BaseModal - A generic modal component with consistent styling and behavior
 * Provides a foundation for all modal dialogs in the application
 * 
 * @param {Object} props
 * @param {boolean} props.open - Whether the modal is open
 * @param {function} props.onClose - Function to call when modal is closed
 * @param {string} props.title - Modal title
 * @param {React.ReactNode} props.children - Modal content
 * @param {React.ReactNode} [props.actions] - Action buttons (footer content)
 * @param {string} [props.size] - Modal size ('xs', 'sm', 'md', 'lg', 'xl')
 * @param {boolean} [props.fullScreen] - Whether the modal should be full screen
 * @param {boolean} [props.disableBackdropClick] - Whether to disable closing on backdrop click
 * @param {boolean} [props.disableEscapeKey] - Whether to disable closing on escape key
 * @param {string} [props.className] - Additional CSS classes
 * @param {Object} [props.style] - Additional inline styles
 * @param {boolean} [props.showCloseButton] - Whether to show the close button in header
 */
const BaseModal = ({
  open,
  onClose,
  title,
  children,
  actions,
  size = 'md',
  fullScreen = false,
  disableBackdropClick = false,
  disableEscapeKey = false,
  className = '',
  style = {},
  showCloseButton = true,
  ...props
}) => {
  const handleClose = (event, reason) => {
    if (
      (reason === 'backdropClick' && disableBackdropClick) ||
      (reason === 'escapeKeyDown' && disableEscapeKey)
    ) {
      return;
    }
    onClose();
  };

  return (
    <Dialog
      open={open}
      onClose={handleClose}
      maxWidth={size}
      fullWidth={true}
      fullScreen={fullScreen}
      className={className}
      style={style}
      {...props}
    >
      <DialogTitle
        sx={{
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'space-between',
          padding: '16px 24px',
          borderBottom: '1px solid',
          borderBottomColor: 'divider',
          backgroundColor: 'background.paper'
        }}
      >
        <Typography variant="h6" component="h2">
          {title}
        </Typography>
        
        {showCloseButton && (
          <IconButton
            aria-label="close"
            onClick={onClose}
            sx={{
              color: 'text.secondary',
              '&:hover': {
                color: 'text.primary',
                backgroundColor: 'action.hover'
              }
            }}
          >
            <CloseIcon />
          </IconButton>
        )}
      </DialogTitle>

      <DialogContent
        sx={{
          padding: '24px',
          backgroundColor: 'background.paper',
          '&:first-of-type': {
            paddingTop: '24px'
          }
        }}
      >
        <Box sx={{ minHeight: fullScreen ? 'auto' : '200px' }}>
          {children}
        </Box>
      </DialogContent>

      {actions && (
        <DialogActions
          sx={{
            padding: '16px 24px',
            borderTop: '1px solid',
            borderTopColor: 'divider',
            backgroundColor: 'background.paper',
            gap: 1
          }}
        >
          {actions}
        </DialogActions>
      )}
    </Dialog>
  );
};

export default BaseModal;
