import React, { useState, useEffect } from 'react';
import { 
  Box, 
  Dialog, 
  Snackbar, 
  Alert,
  Stack,
  Typography,
  useTheme,
  IconButton
} from '@mui/material';
import { History, ArrowBack } from '@mui/icons-material';
import FormCreator from './FormCreator';
import formService, { ValidationException, NotFoundException } from '../../api/process/formService';
import FormVersionHistory from '../../components/form/FormVersionHistory';
import FormListPanel from '../../components/form/FormListPanel';
import MobileFormList from '../../components/form/MobileFormList';
import { useTranslation } from 'react-i18next';
import { useScreenSize } from '../../contexts/ScreenSizeContext';

/**
 * Forms version management tab component with master-detail layout
 */
const FormsTab = () => {
  const { t } = useTranslation();
  const { isMobileScreen } = useScreenSize();
  const [formDefinitions, setFormDefinitions] = useState([]);
  const [selectedForm, setSelectedForm] = useState(null);
  const [versions, setVersions] = useState([]);
  const [loadingForms, setLoadingForms] = useState(false);
  const [loadingVersions, setLoadingVersions] = useState(false);
  const [isCreateDialogOpen, setIsCreateDialogOpen] = useState(false);
  const [notification, setNotification] = useState({
    open: false,
    message: '',
    severity: 'success'
  });
  // Removed unused state variables for version management

  const theme = useTheme();

  // Fetch form definitions on component mount
  useEffect(() => {
    const fetchFormDefinitions = async () => {
      setLoadingForms(true);
      try {
        const keys = await formService.getAllFormKeys();
        const forms = [];
        console.log(keys)
      
        for(var k in keys){
          await formService.getLatestVersion(keys[k]).then((form) => {
            forms.push(form);
          });
        }

        // In a real implementation, this would call formService.listForms()
        // For now, we'll mock some sample data

        setFormDefinitions(forms);
      } catch (error) {
        handleServiceError(error);
      } finally {
        setLoadingForms(false);
      }
    };

    fetchFormDefinitions();
  }, []);

  // Fetch versions when a form is selected
  useEffect(() => {
    if (selectedForm) {
      setLoadingVersions(true);
      formService.listVersions(selectedForm.key)
      .then((versions) => {
        setVersions(versions);
      })
      .catch((error) => {
        handleServiceError(error);
      })
      .finally(() => {
        setLoadingVersions(false);
      });
    }
  }, [selectedForm]);

  const handleCreateVersion = (newVersion) => {
    setVersions(prev => [...prev, newVersion]);
    setIsCreateDialogOpen(false);
    showNotification(t('form:versionCreated'), 'success');
  };

  const handleDeprecate = async (version) => {
    try {
      // In a real implementation, this would call formService.deprecateVersion()
      showNotification(t('form:versionDeprecated', { version }), 'success');
    } catch (error) {
      handleServiceError(error);
    }
  };

  const handleServiceError = (error) => {
    let message = t('common:genericError');
    
    if (error instanceof ValidationException) {
      message = t('form.errors.validationError', { detail: error.message });
    } else if (error instanceof NotFoundException) {
      message = t('form.errors.formNotFound');
    } else {
      console.error('Service error:', error);
    }
    
    showNotification(message, 'error');
  };

  const showNotification = (message, severity) => {
    setNotification({ open: true, message, severity });
  };

  const handleCloseNotification = () => {
    setNotification(prev => ({ ...prev, open: false }));
  };
  
  

  return (
    <Box sx={{ 
      height: '100%', 
      display: 'flex', 
      flexDirection: isMobileScreen ? 'column' : 'row',
      gap: isMobileScreen ? 2 : 3
    }}>
      {/* Form Definition List and Version Management */}
      <Box sx={{ 
        display: 'flex', 
        flexDirection: isMobileScreen ? 'column' : 'row',
        width: '100%',
        gap: isMobileScreen ? 2 : 3
      }}>
        <Box sx={{ 
          flex: isMobileScreen ? 'none' : 1,
          width: isMobileScreen ? '100%' : 'auto',
          display: selectedForm && isMobileScreen ? 'none' : 'block'
        }}>
          {isMobileScreen ? (
            <MobileFormList
              forms={formDefinitions}
              loading={loadingForms}
              onFormSelect={setSelectedForm}
              onCreateVersion={(form) => {
                setSelectedForm(form);
                setIsCreateDialogOpen(true);
              }}
            />
          ) : (
            <FormListPanel
              forms={formDefinitions}
              loading={loadingForms}
              onFormSelect={setSelectedForm}
              onCreateVersion={(form) => {
                setSelectedForm(form);
                setIsCreateDialogOpen(true);
              }}
            />
          )}
        </Box>
        
        {/* Version Management Panel - Show only when form is selected on mobile */}
        {(selectedForm || !isMobileScreen) && (
          <Box 
            sx={{
              flex: isMobileScreen ? 'none' : 1,
              width: isMobileScreen ? '100%' : 'auto',
              p: 2,
              backgroundColor: theme.palette.background.paper,
              borderRadius: theme.shape.borderRadius
            }}
          >
            {selectedForm ? (
              <Box sx={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
                <Stack 
                  direction="row" 
                  spacing={theme.spacing(2)} 
                  sx={{ mb: theme.spacing(2) }} 
                  alignItems="center"
                >
                  {isMobileScreen && (
                    <IconButton
                      onClick={() => setSelectedForm(null)}
                      sx={{ mr: 1 }}
                    >
                      <ArrowBack />
                    </IconButton>
                  )}
                  <Box sx={{ flexGrow: 1, mb: theme.spacing(2) }}>
                    <Typography variant="h6" component="div" color='text.primary'>
                      {selectedForm.name?? selectedForm.key}
                    </Typography>
                    <Typography variant="subtitle1" color="text.secondary">
                      {t('common:version')}: {selectedForm.version}
                    </Typography>
                  </Box>
                </Stack>
                
                <FormVersionHistory 
                  formKey={selectedForm.key}
                  versions={versions}
                />
              </Box>
            ) : (
              <Box sx={{ 
                height: '100%', 
                display: 'flex', 
                alignItems: 'center', 
                justifyContent: 'center',
                border: `1px dashed ${theme.palette.divider}`,
                borderRadius: theme.shape.borderRadius
              }}>
                <Box sx={{ textAlign: 'center', color: theme.palette.text.secondary }}>
                  {t('form.selectFormPrompt')}
                </Box>
              </Box>
            )}
          </Box>
        )}
      </Box>

      {/* Form Creator Dialog */}
    
        <FormCreator 
          formKey={selectedForm?.key}
          open={isCreateDialogOpen}
          onClose={() => setIsCreateDialogOpen(false)}
          onSuccess={handleCreateVersion}
        />

      {/* Notification Snackbar */}
      <Snackbar
        open={notification.open}
        autoHideDuration={6000}
        onClose={handleCloseNotification}
        anchorOrigin={{ vertical: 'top', horizontal: 'center' }}
      >
        <Alert 
          onClose={handleCloseNotification} 
          severity={notification.severity}
          sx={{ width: '100%' }}
        >
          {notification.message}
        </Alert>
      </Snackbar>
    </Box>
  );
};

export default FormsTab;
