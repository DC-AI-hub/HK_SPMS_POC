import React, { useState, useEffect, useCallback } from 'react';
import {
  Box,
  Paper,
  Typography,
  TextField,
  Button,
  Alert,
  CircularProgress,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Grid,
  Chip,
  IconButton,
  FormControlLabel,
  Switch,
  Divider,
  Card,
  CardContent
} from '@mui/material';
import {
  Add as AddIcon,
  Edit as EditIcon,
  Delete as DeleteIcon,
  Palette as PaletteIcon,
  Visibility as VisibilityIcon
} from '@mui/icons-material';
import { useConfig } from '../../hooks/useConfig';
import { useTranslation } from 'react-i18next';

/**
 * ThemeConfigPanel - Component for managing theme configurations
 * Provides color management, preview functionality, and theme switching
 */
export default function ThemeConfigPanel() {
  const { t } = useTranslation('system');
  const {
    themeConfig,
    loading,
    error,
    setActiveTheme,
    refreshConfig
  } = useConfig();

  const [dialogOpen, setDialogOpen] = useState(false);
  const [editingTheme, setEditingTheme] = useState(null);
  const [previewTheme, setPreviewTheme] = useState(null);
  const initialFormData = {
    name: '',
    primaryColor: '#1976d2',
    secondaryColor: '#dc004e',
    backgroundColor: '#ffffff',
    textColor: '#000000',
    isDefault: false
  };
  // Check if a string is a valid hex color
  const isValidHexColor = (color) => {
    return /^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$/.test(color);
  };

  // Validate form function
  const validateForm = (data) => {
    const errors = {};
    
    if (!data?.name?.trim()) {
      errors.name = t('theme.validation.nameRequired');
    } else if (data.name.length > 50) {
      errors.name = t('theme.validation.nameTooLong');
    }

    if (!isValidHexColor(data?.primaryColor)) {
      errors.primaryColor = t('theme.validation.invalidHex');
    }

    if (!isValidHexColor(data?.secondaryColor)) {
      errors.secondaryColor = t('theme.validation.invalidHex');
    }

    if (!isValidHexColor(data?.backgroundColor)) {
      errors.backgroundColor = t('theme.validation.invalidHex');
    }

    if (!isValidHexColor(data?.textColor)) {
      errors.textColor = t('theme.validation.invalidHex');
    }

    return errors;
  };

  const [formData, setFormData] = useState(initialFormData);
  const [formErrors, setFormErrors] = useState(validateForm(initialFormData));
  const [saveStatus, setSaveStatus] = useState({ success: null, message: '' });

  // Reset form when dialog opens/closes
  useEffect(() => {
    if (dialogOpen && editingTheme) {
      const newFormData = {
        name: editingTheme.name,
        primaryColor: editingTheme.primaryColor,
        secondaryColor: editingTheme.secondaryColor,
        backgroundColor: editingTheme.backgroundColor,
        textColor: editingTheme.textColor,
        isDefault: editingTheme.isDefault
      };
      setFormData(newFormData);
    } else if (!dialogOpen) {
      setFormData(initialFormData);
      setEditingTheme(null);
    }
  }, [dialogOpen, editingTheme]);

  // Handle form input changes
  const handleInputChange = (field, value) => {
    const newFormData = { ...formData, [field]: value };
    setFormData(newFormData);
    
    // Validate the specific field immediately
    const errors = validateForm(newFormData);
    setFormErrors(errors);
  };

  // Handle color input changes with validation
  const handleColorChange = (field, value) => {
    // Add # if missing
    if (value && !value.startsWith('#')) {
      value = '#' + value;
    }
    
    handleInputChange(field, value);
  };

  // Handle dialog open for new theme
  const handleAddNew = () => {
    setEditingTheme(null);
    setDialogOpen(true);
  };

  // Handle dialog open for editing
  const handleEdit = (theme) => {
    setEditingTheme(theme);
    setDialogOpen(true);
  };

  // Handle dialog close
  const handleDialogClose = () => {
    setDialogOpen(false);
    setPreviewTheme(null);
  };

  // Handle preview theme
  const handlePreview = () => {
    setPreviewTheme(formData);
  };

  // Handle set active theme
  const handleSetActive = async (themeId) => {
    try {
      await setActiveTheme(themeId);
      setSaveStatus({ success: true, message: t('theme.activateSuccess') });
    } catch (error) {
      setSaveStatus({
        success: false,
        message: error.response?.data?.message || t('theme.activateError')
      });
    }
  };

  // Render theme preview card
  const renderThemePreview = (theme) => {
    const previewStyle = {
      backgroundColor: theme.backgroundColor,
      color: theme.textColor,
      padding: '16px',
      borderRadius: '8px',
      border: '2px solid #ccc'
    };

    return (
      <Card sx={{ mb: 2 }}>
        <CardContent>
          <Typography variant="h6" gutterBottom>
            {theme.name} {theme.isDefault && <Chip label="Default" size="small" color="primary" />}
          </Typography>
          
          <Box sx={previewStyle}>
            <Typography variant="body1" gutterBottom>
              This is a preview text with the theme colors.
            </Typography>
            
            <Box sx={{ display: 'flex', gap: 1, flexWrap: 'wrap', mt: 2 }}>
              <Box
                sx={{
                  backgroundColor: theme.primaryColor,
                  color: 'white',
                  padding: '8px 12px',
                  borderRadius: '4px',
                  fontSize: '14px'
                }}
              >
                Primary Color
              </Box>
              
              <Box
                sx={{
                  backgroundColor: theme.secondaryColor,
                  color: 'white',
                  padding: '8px 12px',
                  borderRadius: '4px',
                  fontSize: '14px'
                }}
              >
                Secondary Color
              </Box>
              
              <Box
                sx={{
                  backgroundColor: theme.backgroundColor,
                  color: theme.textColor,
                  padding: '8px 12px',
                  borderRadius: '4px',
                  border: '1px solid #ccc',
                  fontSize: '14px'
                }}
              >
                Background
              </Box>
            </Box>
          </Box>
          
          <Box sx={{ mt: 2, display: 'flex', gap: 1, flexWrap: 'wrap' }}>
            <Typography variant="caption" sx={{ mr: 1 }}>
              Primary: {theme.primaryColor}
            </Typography>
            <Typography variant="caption" sx={{ mr: 1 }}>
              Secondary: {theme.secondaryColor}
            </Typography>
            <Typography variant="caption" sx={{ mr: 1 }}>
              Background: {theme.backgroundColor}
            </Typography>
            <Typography variant="caption">
              Text: {theme.textColor}
            </Typography>
          </Box>
        </CardContent>
      </Card>
    );
  };

  if (loading && themeConfig.length === 0) {
    return (
      <Box display="flex" justifyContent="center" alignItems="center" minHeight="200px">
        <CircularProgress />
        <Typography variant="h6" sx={{ ml: 2 }}>
          {t('theme.loading')}
        </Typography>
      </Box>
    );
  }

  if (error) {
    return (
      <Alert severity="error" sx={{ mb: 2 }}>
        {t('errors.loadingError')}: {error}
      </Alert>
    );
  }

  return (
    <Box>
      <Paper sx={{ p: 3 }}>
        <Box display="flex" justifyContent="space-between" alignItems="center" mb={3}>
          <Typography variant="h5" component="h2">
            {t('theme.management')}
          </Typography>
          <Button
            variant="contained"
            startIcon={<AddIcon />}
            onClick={handleAddNew}
          >
            {t('theme.addTheme')}
          </Button>
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

        {themeConfig.length === 0 ? (
          <Typography variant="body1" color="text.secondary" textAlign="center" py={4}>
            {t('theme.noThemes')}
          </Typography>
        ) : (
          <Grid container spacing={3}>
            {themeConfig.map((theme) => (
              <Grid item xs={12} md={6} key={theme.id}>
                {renderThemePreview(theme)}
                
                <Box display="flex" gap={1} justifyContent="flex-end">
                  <IconButton 
                    onClick={() => handleEdit(theme)} 
                    size="small" 
                    color="primary"
                  >
                    <EditIcon />
                  </IconButton>
                  
                  {!theme.isDefault && (
                    <Button
                      variant="outlined"
                      size="small"
                      onClick={() => handleSetActive(theme.id)}
                    >
                      {t('theme.setActive')}
                    </Button>
                  )}
                  
                  {theme.isDefault && (
                    <Chip label={t('theme.activeChip')} color="success" size="small" />
                  )}
                </Box>
              </Grid>
            ))}
          </Grid>
        )}
      </Paper>

      {/* Add/Edit Dialog */}
      <Dialog open={dialogOpen} onClose={handleDialogClose} maxWidth="md" fullWidth>
        <DialogTitle>
          {editingTheme ? t('theme.editTheme') : t('theme.addNewTheme')}
        </DialogTitle>
        <DialogContent>
          <Box sx={{ mt: 2 }}>
            <Grid container spacing={2}>
              <Grid item xs={12}>
                <TextField
                  fullWidth
                  label={t('theme.themeName')}
                  value={formData.name}
                  onChange={(e) => handleInputChange('name', e.target.value)}
                  error={!!formErrors.name}
                  helperText={formErrors.name}
                  required
                />
              </Grid>
              
              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  label={t('theme.primaryColor')}
                  value={formData.primaryColor}
                  onChange={(e) => handleColorChange('primaryColor', e.target.value)}
                  error={!!formErrors.primaryColor}
                  helperText={formErrors.primaryColor || t('theme.validation.hexFormat')}
                  InputProps={{
                    startAdornment: (
                      <Box
                        sx={{
                          width: 20,
                          height: 20,
                          backgroundColor: formData.primaryColor,
                          border: '1px solid #ccc',
                          borderRadius: '4px',
                          mr: 1
                        }}
                      />
                    )
                  }}
                />
              </Grid>
              
              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  label={t('theme.secondaryColor')}
                  value={formData.secondaryColor}
                  onChange={(e) => handleColorChange('secondaryColor', e.target.value)}
                  error={!!formErrors.secondaryColor}
                  helperText={formErrors.secondaryColor || t('theme.validation.hexFormat')}
                  InputProps={{
                    startAdornment: (
                      <Box
                        sx={{
                          width: 20,
                          height: 20,
                          backgroundColor: formData.secondaryColor,
                          border: '1px solid #ccc',
                          borderRadius: '4px',
                          mr: 1
                        }}
                      />
                    )
                  }}
                />
              </Grid>
              
              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  label={t('theme.backgroundColor')}
                  value={formData.backgroundColor}
                  onChange={(e) => handleColorChange('backgroundColor', e.target.value)}
                  error={!!formErrors.backgroundColor}
                  helperText={formErrors.backgroundColor || t('theme.validation.hexFormat')}
                  InputProps={{
                    startAdornment: (
                      <Box
                        sx={{
                          width: 20,
                          height: 20,
                          backgroundColor: formData.backgroundColor,
                          border: '1px solid #ccc',
                          borderRadius: '4px',
                          mr: 1
                        }}
                      />
                    )
                  }}
                />
              </Grid>
              
              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  label={t('theme.textColor')}
                  value={formData.textColor}
                  onChange={(e) => handleColorChange('textColor', e.target.value)}
                  error={!!formErrors.textColor}
                  helperText={formErrors.textColor || t('theme.validation.hexFormat')}
                  InputProps={{
                    startAdornment: (
                      <Box
                        sx={{
                          width: 20,
                          height: 20,
                          backgroundColor: formData.textColor,
                          border: '1px solid #ccc',
                          borderRadius: '4px',
                          mr: 1
                        }}
                      />
                    )
                  }}
                />
              </Grid>
              
              <Grid item xs={12}>
                <FormControlLabel
                  control={
                    <Switch
                      checked={formData.isDefault}
                      onChange={(e) => handleInputChange('isDefault', e.target.checked)}
                      color="primary"
                    />
                  }
                  label={t('theme.setAsDefault')}
                />
              </Grid>
            </Grid>

            <Divider sx={{ my: 3 }} />

            <Box display="flex" justifyContent="space-between" alignItems="center" mb={2}>
              <Typography variant="h6">{t('theme.preview')}</Typography>
              <Button
                variant="outlined"
                startIcon={<VisibilityIcon />}
                onClick={handlePreview}
                size="small"
              >
                {t('theme.updatePreview')}
              </Button>
            </Box>

            {previewTheme && renderThemePreview(previewTheme)}
          </Box>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleDialogClose}>{t('theme.cancel')}</Button>
          <Button 
            variant="contained" 
            onClick={handlePreview}
            startIcon={<PaletteIcon />}
          >
            {t('theme.preview')}
          </Button>
          <Button 
            variant="contained" 
            color="primary"
            disabled={!validateForm()}
          >
            {editingTheme ? t('theme.updateTheme') : t('theme.createTheme')}
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
}
