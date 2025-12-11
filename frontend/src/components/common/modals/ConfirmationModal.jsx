import React from 'react';
import {
  Button,
  Typography,
  Box
} from '@mui/material';
import BaseModal from './BaseModal';

/**
 * ConfirmationModal - A modal for confirming user actions
 * Provides consistent confirmation dialog styling and behavior
 * 
 * @param {Object} props
 * @param {boolean} props.open - Whether the modal is open
 * @param {function} props.onClose - Function to call when modal is closed
 * @param {function} props.onConfirm - Function to call when user confirms
 * @param {string} props.title - Modal title
 * @param {string} props.message - Confirmation message
 * @param {string} [props.confirmText] - Text for confirm button (default: 'Confirm')
 * @param {string} [props.cancelText] - Text for cancel button (default: 'Cancel')
 * @param {string} [props.confirmColor] - Color for confirm button ('primary', 'secondary', 'error', etc.)
 * @param {boolean} [props.danger] - Whether this is a dangerous action (uses error color)
 * @param {boolean} [props.loading] - Whether the confirmation is in progress (disables buttons)
 * @param {string} [props.size] - Modal size ('xs', 'sm', 'md', 'lg', 'xl')
 * @param {React.ReactNode} [props.children] - Additional content to display
 */
const ConfirmationModal = ({
  open,
  onClose,
  onConfirm,
  title,
  message,
  confirmText = 'Confirm',
  cancelText = 'Cancel',
  confirmColor = 'primary',
  danger = false,
  loading = false,
  size = 'sm',
  children,
  ...props
}) => {
  const handleConfirm = () => {
    onConfirm();
  };

  const actions = (
    <>
      <Button
        onClick={onClose}
        disabled={loading}
        variant="outlined"
        color="inherit"
      >
        {cancelText}
      </Button>
      <Button
        onClick={handleConfirm}
        disabled={loading}
        variant="contained"
        color={danger ? 'error' : confirmColor}
        autoFocus
      >
        {loading ? 'Processing...' : confirmText}
      </Button>
    </>
  );

  return (
    <BaseModal
      open={open}
      onClose={onClose}
      title={title}
      actions={actions}
      size={size}
      disableBackdropClick={loading}
      disableEscapeKey={loading}
      {...props}
    >
      <Box sx={{ textAlign: 'center', py: 2 }}>
        <Typography variant="body1" sx={{ mb: 3 }}>
          {message}
        </Typography>
        
        {children && (
          <Box sx={{ mt: 2 }}>
            {children}
          </Box>
        )}
      </Box>
    </BaseModal>
  );
};

export default ConfirmationModal;
