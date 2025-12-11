import React, { useState, useEffect } from 'react';
import { useTranslation } from 'react-i18next';
import {
  Box,
  Paper,
  Typography,
  TextField,
  Switch,
  FormControlLabel,
  Button,
  Grid,
  Alert,
  CircularProgress,
  Divider,
  Chip
} from '@mui/material';
import { Save as SaveIcon, Refresh as RefreshIcon } from '@mui/icons-material';
import { useConfig } from '../../hooks/useConfig';

/**
 * SystemConfigPanel - Component for managing system configuration settings
 * Provides a form interface to view and update system settings with proper validation
 */
export default function SystemConfigPanel() {
  const { t } = useTranslation();
  const {
    systemConfig,
    loading,
    error,
    updateSystemSetting,
    refreshConfig
  } = useConfig();

  const [formData, setFormData] = useState({});
  const [errors, setErrors] = useState({});
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [hasChanges, setHasChanges] = useState(false);
  const [saveStatus, setSaveStatus] = useState({ success: null, message: '' });

  // Initialize form data when systemConfig changes
  useEffect(() => {
    if (systemConfig.length > 0) {
      const initialFormData = {};
      systemConfig.forEach(setting => {
        initialFormData[setting.key] = setting.value;
      });
      setFormData(initialFormData);
      setErrors({});
      setHasChanges(false);
    }
  }, [systemConfig]);

  // Handle input changes
  const handleInputChange = (key, value) => {
    const setting = systemConfig.find(s => s.key === key);
    if (!setting) return;

    // Validate based on data type
    let validatedValue = value;
    let newErrors = { ...errors };

    switch (setting.dataType) {
      case 'NUMBER':
        validatedValue = value === '' ? '' : Number(value);
        if (isNaN(validatedValue)) {
          newErrors[key] = t('system:validation.mustBeNumber');
        } else {
          delete newErrors[key];
        }
        break;
      case 'BOOLEAN':
        validatedValue = Boolean(value);
        delete newErrors[key];
        break;
      case 'JSON':
        try {
          if (value) JSON.parse(value);
          delete newErrors[key];
        } catch (e) {
          newErrors[key] = t('system:validation.invalidJson');
        }
        break;
      default:
        delete newErrors[key];
    }

    setFormData(prev => ({ ...prev, [key]: validatedValue }));
    setErrors(newErrors);
    setHasChanges(true);
  };

  // Handle form submission
  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (Object.keys(errors).length > 0) {
      setSaveStatus({ success: false, message: t('system:status.validationError') });
      return;
    }

    setIsSubmitting(true);
    setSaveStatus({ success: null, message: '' });

    try {
      // Update each changed setting
      const changedSettings = Object.entries(formData).filter(([key, value]) => {
        const originalSetting = systemConfig.find(s => s.key === key);
        return originalSetting && originalSetting.value !== value;
      });

      for (const [key, value] of changedSettings) {
        await updateSystemSetting(key, value);
      }

      setSaveStatus({ success: true, message: t('system:status.saveSuccess') });
      setHasChanges(false);
      refreshConfig(); // Refresh to get latest data
    } catch (error) {
      setSaveStatus({ 
        success: false, 
        message: error.response?.data?.message || t('system:status.saveError')
      });
    } finally {
      setIsSubmitting(false);
    }
  };

  // Reset form to original values
  const handleReset = () => {
    const originalFormData = {};
    systemConfig.forEach(setting => {
      originalFormData[setting.key] = setting.value;
    });
    setFormData(originalFormData);
    setErrors({});
    setHasChanges(false);
    setSaveStatus({ success: null, message: '' });
  };

  // Render appropriate input field based on data type
  const renderInputField = (setting) => {
    const value = formData[setting.key] || '';
    const error = errors[setting.key];

    switch (setting.dataType) {
      case 'BOOLEAN':
        return (
          <FormControlLabel
            control={
              <Switch
                checked={Boolean(value)}
                onChange={(e) => handleInputChange(setting.key, e.target.checked)}
                color="primary"
              />
            }
            label={value ? t('system:booleanStates.enabled') : t('system:booleanStates.disabled')}
          />
        );
      
      case 'NUMBER':
        return (
          <TextField
            fullWidth
            type="number"
            value={value}
            onChange={(e) => handleInputChange(setting.key, e.target.value)}
            error={!!error}
            helperText={error}
            size="small"
          />
        );
      
      case 'JSON':
        return (
          <TextField
            fullWidth
            multiline
            rows={4}
            value={typeof value === 'string' ? value : JSON.stringify(value, null, 2)}
            onChange={(e) => handleInputChange(setting.key, e.target.value)}
            error={!!error}
            helperText={error || t('system:jsonHelper')}
            size="small"
          />
        );
      
      default:
        return (
          <TextField
            fullWidth
            value={value}
            onChange={(e) => handleInputChange(setting.key, e.target.value)}
            error={!!error}
            helperText={error}
            size="small"
          />
        );
    }
  };

  if (loading && systemConfig.length === 0) {
    return (
      <Box display="flex" justifyContent="center" alignItems="center" minHeight="200px">
        <CircularProgress />
        <Typography variant="h6" sx={{ ml: 2 }}>
          {t('system:settings.loading')}
        </Typography>
      </Box>
    );
  }

  if (error) {
    return (
      <Alert severity="error" sx={{ mb: 2 }}>
        {t('system:errors.loadingError')}: {error}
      </Alert>
    );
  }

  return (
    <Box component="form" onSubmit={handleSubmit}>
      <Paper sx={{ p: 3 }}>
        <Box display="flex" justifyContent="space-between" alignItems="center" mb={3}>
          <Typography variant="h5" component="h2">
            {t('system:settings.title')}
          </Typography>
          <Chip
            label={hasChanges ? t('system:settings.unsavedChanges') : t('system:settings.allChangesSaved')}
            color={hasChanges ? 'warning' : 'success'}
            variant="outlined"
          />
        </Box>

        {saveStatus.success !== null && (
          <Alert 
            severity={saveStatus.success ? 'success' : 'error'} 
            sx={{ mb: 3 }}
            onClose={() => setSaveStatus({ success: null, message: '' })}
          >
            {saveStatus.message}
          </Alert>
        )}

        <Grid container spacing={3}>
          {systemConfig.map((setting) => (
            <Grid item xs={12} md={6} key={setting.key}>
              <Paper variant="outlined" sx={{ p: 2 }}>
                <Typography variant="subtitle2" gutterBottom color="text.secondary">
                  {setting.key}
                </Typography>
                <Typography variant="body2" color="text.secondary" gutterBottom>
                  {setting.description}
                </Typography>
                <Box mt={1}>
                  {renderInputField(setting)}
                </Box>
                <Typography variant="caption" color="text.secondary" sx={{ mt: 1, display: 'block' }}>
                  Type: {setting.dataType}
                </Typography>
              </Paper>
            </Grid>
          ))}
        </Grid>

        <Divider sx={{ my: 3 }} />

        <Box display="flex" gap={2} justifyContent="flex-end">
          <Button
            variant="outlined"
            onClick={handleReset}
            disabled={!hasChanges || isSubmitting}
            startIcon={<RefreshIcon />}
          >
            {t('system:buttons.reset')}
          </Button>
          <Button
            variant="contained"
            type="submit"
            disabled={!hasChanges || isSubmitting || Object.keys(errors).length > 0}
            startIcon={isSubmitting ? <CircularProgress size={16} /> : <SaveIcon />}
          >
            {isSubmitting ? t('system:buttons.saving') : t('system:buttons.saveChanges')}
          </Button>
        </Box>
      </Paper>
    </Box>
  );
}
