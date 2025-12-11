import React, { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { 
  Dialog, DialogTitle, DialogContent, DialogActions,
  TextField, Button, Grid, FormControl
} from '@mui/material';
import UserSelector from '../../components/common/UserSelector';
import ConfirmationDialog from '../../components/ConfirmationDialog';

const ProcessForm = ({ open, onClose, onCreate, fullScreen = false, maxWidth = "sm" }) => {
  console.log(open)
  const { t } = useTranslation();
  const [formData, setFormData] = useState({
    name: '',
    key: '',
    description: '',
    owner: null,
    businessOwner: null
  });
  const [errors, setErrors] = useState({});

  const validate = () => {
    const newErrors = {};
    if (!formData.name) newErrors.name = t('process:form.errors.nameRequired');
    if (!formData.key) newErrors.key = t('process:form.errors.keyRequired');
    if (!formData.owner) newErrors.owner = t('process:form.errors.ownerRequired');
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const [showConfirmation, setShowConfirmation] = useState(false);

  const handleConfirmation = () => {
    setShowConfirmation(true);
  };

  const handleSubmitConfirmed = () => {
    if (validate()) {
      console.log("-----------------------123132",formData)
      onCreate({
        processName: formData.name,
        processKey: formData.key,
        processDescription: formData.description,
        owner: formData.owner,
        businessOwner: formData.businessOwner
      });
      onClose();
    }
    setShowConfirmation(false);
  };

  const handleSubmit = () => {
    if (validate()) {
      setShowConfirmation(true);
    }
  };

  const handleChange = (field, value) => {
    setFormData(prev => ({ ...prev, [field]: value }));
    
    if (errors[field]) setErrors(prev => ({ ...prev, [field]: null }));
  };

  return (
    <Dialog open={open} onClose={onClose} maxWidth={maxWidth} fullWidth fullScreen={fullScreen}>
      <DialogTitle>{t('process:form.createTitle')}</DialogTitle>
      <DialogContent>
        <Grid container spacing={2} sx={{ pt: 1 ,flexDirection: "column" }} >
          <Grid item xs={12}>
            <TextField
              label={t('process:form.name')}
              fullWidth
              value={formData.name}
              onChange={(e) => handleChange('name', e.target.value)}
              error={!!errors.name}
              helperText={errors.name}
            />
          </Grid>
          <Grid item xs={12}>
            <TextField
              label={t('process:form.key')}
              fullWidth
              value={formData.key}
              onChange={(e) => handleChange('key', e.target.value)}
              error={!!errors.key}
              helperText={errors.key}
            />
          </Grid>
          <Grid item xs={12}>
            <TextField
              label={t('process:form.description')}
              fullWidth
              multiline
              rows={3}
              value={formData.description}
              onChange={(e) => handleChange('description', e.target.value)}
            />
          </Grid>
          <Grid item xs={12} md={6}>
            <FormControl fullWidth error={!!errors.owner}>
              <UserSelector
                label={t('process:form.owner')}
                value={formData.owner}
                onChange={(user) => handleChange('owner', user.target.value)}
                error={!!errors.owner}
                helperText={errors.owner}
              />
            </FormControl>
          </Grid>
          <Grid item xs={12} md={6}>
            <UserSelector
              label={t('process:form.businessOwner')}
              value={formData.businessOwner}
              onChange={(user) => handleChange('businessOwner',  user.target.value)}
            />
          </Grid>
        </Grid>
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose}>{t('common:cancel')}</Button>
        <Button variant="contained" onClick={handleSubmit}>
          {t('common:create')}
        </Button>
      </DialogActions>

      <ConfirmationDialog
        
        open={showConfirmation}
        onClose={() => setShowConfirmation(false)}
        onConfirm={handleSubmitConfirmed}
        title={t('process:confirmation.title')}
        message={t('process:confirmation.createProcess')}
      />
    </Dialog>
  );
};

export default ProcessForm;
