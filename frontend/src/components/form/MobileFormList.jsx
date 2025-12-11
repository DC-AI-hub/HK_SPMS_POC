import React from 'react';
import { 
  Box, 
  IconButton,
  useTheme,
  Typography,
  Chip
} from '@mui/material';
import { Add, History } from '@mui/icons-material';
import { useTranslation } from 'react-i18next';
import MobileEntityList from '../common/MobileEntityList';

/**
 * Mobile-optimized form list component using MobileEntityList
 * @param {Object} props - Component properties
 * @param {Array} props.forms - List of form definitions
 * @param {boolean} props.loading - Loading state indicator
 * @param {Function} props.onFormSelect - Form selection handler
 * @param {Function} props.onCreateVersion - Version creation handler
 */
const MobileFormList = ({ 
  forms, 
  loading, 
  onFormSelect,
  onCreateVersion
}) => {
  const { t } = useTranslation();
  const theme = useTheme();

  // Action configuration for mobile
  const actionConfig = [
    {
      key: 'viewHistory',
      label: 'form:viewHistory',
      icon: 'History',
      color: 'primary'
    },
    {
      key: 'createVersion',
      label: 'form:createVersion',
      icon: 'Add',
      color: 'primary'
    }
  ];

  // Handle mobile actions
  const handleAction = (actionKey, entity) => {
    switch (actionKey) {
      case 'viewHistory':
        onFormSelect(entity);
        break;
      case 'createVersion':
        onCreateVersion(entity);
        break;
      default:
        console.warn('Unknown action:', actionKey);
    }
  };

  // Custom render function for form items
  const renderFormItem = (form, openActionMenu) => (
    <Box sx={{ 
      display: 'flex', 
      alignItems: 'center', 
      justifyContent: 'space-between',
      width: '100%',
      py: 1
    }}>
      <Box sx={{ flexGrow: 1 }}>
        <Typography variant="subtitle1" component="div" sx={{ mb: 0.5 }}>
          {form.name || form.key}
        </Typography>
        <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
          <Typography variant="body2" color="text.secondary">
            {t('form:latestVersion')}: {form.version}
          </Typography>
          <Chip 
            label={form.status === 'DEPRECATED' ? t('deprecated') : t('active')} 
            size="small"
            color={form.status === 'DEPRECATED' ? 'error' : 'success'}
            variant="outlined"
          />
        </Box>
      </Box>
      <IconButton
        edge="end"
        aria-label="actions"
        onClick={(e) => {
          e.stopPropagation();
          openActionMenu(form);
        }}
        size="small"
      >
        <Add />
      </IconButton>
    </Box>
  );

  return (
    <Box sx={{ width: '100%' }}>
      <MobileEntityList
        entities={forms}
        loading={loading}
        onAction={handleAction}
        actionConfig={actionConfig}
        fieldMappings={{
          name: 'name',
          status: 'status',
          version: 'version'
        }}
        i18nKeys={{
          loading: 'common:loading',
          noItems: 'form:noForms',
          status: 'common:status'
        }}
        renderItem={renderFormItem}
      />
    </Box>
  );
};

export default MobileFormList;
