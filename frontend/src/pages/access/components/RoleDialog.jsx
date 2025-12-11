import React from 'react';
import {
  Dialog, DialogTitle, DialogContent, 
  DialogActions, TextField, Button
} from '@mui/material';

/**
 * Standalone role creation/edit dialog component
 * 
 * @param {boolean} open - Controls dialog visibility
 * @param {function} onClose - Function to close dialog
 * @param {object|null} currentRole - Role data for editing
 * @param {function} onSubmit - Function to submit form data
 * @param {function} t - Translation function
 * @returns {JSX.Element} Role dialog component
 */
const RoleDialog = ({ open, onClose, currentRole, onSubmit, t }) => {
  return (
    <Dialog open={open} onClose={onClose}>
      <DialogTitle>
        {currentRole ? t('access:editRoleTitle') : t('access:createRoleTitle')}
      </DialogTitle>
      <DialogContent>
        <form id="role-form" onSubmit={(e) => {
          e.preventDefault();
          const formData = new FormData(e.target);
          onSubmit(Object.fromEntries(formData.entries()));
        }}>
          <TextField
            autoFocus
            margin="dense"
            name="name"
            label={t('access:form.name')}
            type="text"
            fullWidth
            variant="standard"
            defaultValue={currentRole?.name || ''}
            required
          />
          <TextField
            margin="dense"
            name="description"
            label={t('access:form.description')}
            type="text"
            fullWidth
            variant="standard"
            defaultValue={currentRole?.description || ''}
            multiline
            rows={3}
          />
        </form>
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose}>
          {t('common.cancel')}
        </Button>
        <Button type="submit" form="role-form" variant="contained">
          {currentRole ? t('common.save') : t('common.create')}
        </Button>
      </DialogActions>
    </Dialog>
  );
};

export default RoleDialog;
