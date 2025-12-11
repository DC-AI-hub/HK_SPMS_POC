import React from 'react';
import {
  Button,
  Box,
  CircularProgress
} from '@mui/material';
import BaseModal from './BaseModal';

/**
 * FormModal - A modal for displaying and handling forms
 * Provides consistent form modal styling and behavior with validation support
 * 
 * @param {Object} props
 * @param {boolean} props.open - Whether the modal is open
 * @param {function} props.onClose - Function to call when modal is closed
 * @param {function} props.onSubmit - Function to call when form is submitted
 * @param {string} props.title - Modal title
 * @param {React.ReactNode} props.children - Form content
 * @param {string} [props.submitText] - Text for submit button (default: 'Submit')
 * @param {string} [props.cancelText] - Text for cancel button (default: 'Cancel')
 * @param {boolean} [props.loading] - Whether the form submission is in progress
 * @param {boolean} [props.disabled] - Whether the form is disabled
 * @param {string} [props.size] - Modal size ('xs', 'sm', 'md', 'lg', 'xl')
 * @param {boolean} [props.fullScreen] - Whether the modal should be full screen
 * @param {Object} [props.formProps] - Additional props to pass to the form element
 */
const FormModal = ({
  open,
  onClose,
  onSubmit,
  title,
  children,
  submitText = 'Submit',
  cancelText = 'Cancel',
  loading = false,
  disabled = false,
  size = 'md',
  fullScreen = false,
  formProps = {},
  ...props
}) => {
  const handleSubmit = (event) => {
    event.preventDefault();
    if (!loading && !disabled) {
      onSubmit();
    }
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
        type="submit"
        form="form-modal-form"
        disabled={disabled || loading}
        variant="contained"
        color="primary"
        startIcon={loading ? <CircularProgress size={16} /> : null}
      >
        {loading ? 'Processing...' : submitText}
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
      fullScreen={fullScreen}
      disableBackdropClick={loading}
      disableEscapeKey={loading}
      {...props}
    >
      <Box
        component="form"
        id="form-modal-form"
        onSubmit={handleSubmit}
        sx={{
          display: 'flex',
          flexDirection: 'column',
          gap: 2,
          minHeight: fullScreen ? 'auto' : '200px'
        }}
        {...formProps}
      >
        {children}
      </Box>
    </BaseModal>
  );
};

export default FormModal;
