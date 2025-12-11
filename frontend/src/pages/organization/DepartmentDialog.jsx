import React, { useState } from 'react';
import { Dialog, DialogTitle, DialogContent, DialogActions, Button } from '@mui/material';
import DepartmentForm from './DepartmentForm';
import { useTranslation } from 'react-i18next';
import { useError } from '../../contexts/ErrorContext';

const DepartmentDialog = ({ open, onClose, department, onSubmit, maxWidth, fullWidth, fullScreen }) => {
  const { t } = useTranslation();
  const [isSubmitting, setIsSubmitting] = useState(false);
  const { addError } = useError();

  const handleSubmit = async (formData) => {
    setIsSubmitting(true);
    try {
      await onSubmit(formData);
      onClose();
    } catch (error) {
      addError(error?.message || t('common:submitFailed') || '提交失败');
      console.error('Error submitting department:', error);
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <Dialog 
      open={open} 
      onClose={onClose} 
      maxWidth={maxWidth || "sm"} 
      fullWidth={fullWidth !== undefined ? fullWidth : true}
      fullScreen={fullScreen}
    >
      <DialogTitle>
        {department ? t('organization:department.editDialog.title') : t('organization:department.createDialog.title')}
      </DialogTitle>
      <DialogContent>
        <DepartmentForm 
          department={department} 
          onSubmit={handleSubmit} 
          onCancel={onClose}
        />
      </DialogContent>
    </Dialog>
  );
};

export default DepartmentDialog;
