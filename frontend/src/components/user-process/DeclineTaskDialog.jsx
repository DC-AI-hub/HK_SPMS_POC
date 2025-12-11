import React, { useState } from 'react';
import PropTypes from 'prop-types';
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogContentText,
  DialogActions,
  Button,
  TextField
} from '@mui/material';
import CancelIcon from '@mui/icons-material/Cancel';

/**
 * Decline Task Dialog - Allows user to provide reason for declining a task
 * 
 * @param {Object} props - Component properties
 * @param {boolean} props.open - Controls dialog visibility
 * @param {Function} props.onClose - Callback when dialog closes
 * @param {Function} props.onConfirm - Callback when decline is confirmed
 * @param {Object} [props.task] - Task being declined
 * @param {Object} props.translations - Translation strings
 * @param {string} props.translations.title - Dialog title
 * @param {string} props.translations.description - Dialog description
 * @param {string} props.translations.reasonLabel - Reason field label
 * @param {string} props.translations.cancelButton - Cancel button text
 * @param {string} props.translations.confirmButton - Confirm button text
 */
const DeclineTaskDialog = ({ 
  open, 
  onClose, 
  onConfirm, 
  task, 
  translations 
}) => {
  const [declineReason, setDeclineReason] = useState('');

  const handleClose = () => {
    setDeclineReason('');
    onClose();
  };

  const handleConfirm = () => {
    onConfirm(declineReason);
    handleClose();
  };

  return (
    <Dialog open={open} onClose={handleClose}>
      <DialogTitle>
        {translations.title.replace('{title}', task?.title || '')}
      </DialogTitle>
      <DialogContent>
        <DialogContentText mb={2}>
          {translations.description}
        </DialogContentText>
        <TextField
          autoFocus
          margin="dense"
          label={translations.reasonLabel}
          type="text"
          fullWidth
          variant="outlined"
          value={declineReason}
          onChange={(e) => setDeclineReason(e.target.value)}
          multiline
          rows={3}
        />
      </DialogContent>
      <DialogActions>
        <Button onClick={handleClose}>
          {translations.cancelButton}
        </Button>
        <Button 
          onClick={handleConfirm}
          variant="contained"
          color="error"
          startIcon={<CancelIcon />}
          disabled={!declineReason.trim()}
        >
          {translations.confirmButton}
        </Button>
      </DialogActions>
    </Dialog>
  );
};

DeclineTaskDialog.propTypes = {
  open: PropTypes.bool.isRequired,
  onClose: PropTypes.func.isRequired,
  onConfirm: PropTypes.func.isRequired,
  task: PropTypes.object,
  translations: PropTypes.shape({
    title: PropTypes.string.isRequired,
    description: PropTypes.string.isRequired,
    reasonLabel: PropTypes.string.isRequired,
    cancelButton: PropTypes.string.isRequired,
    confirmButton: PropTypes.string.isRequired
  }).isRequired
};

export default DeclineTaskDialog;
