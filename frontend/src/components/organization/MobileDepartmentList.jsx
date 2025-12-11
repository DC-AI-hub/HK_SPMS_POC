import React from 'react';
import { useTranslation } from 'react-i18next';
import { Box, Chip, Typography, useTheme, IconButton } from '@mui/material';
import { MoreVert } from '@mui/icons-material';
import MobileEntityList from '../common/MobileEntityList';

/**
 * Mobile-friendly list component for displaying departments using the generic MobileEntityList
 */
const MobileDepartmentList = ({ 
  departments, 
  onEdit, 
  onDelete,
  loading = false 
}) => {
  const { t } = useTranslation();
  const theme = useTheme();

  const handleAction = (actionKey, department) => {
    console.log('Action triggered:', actionKey, department);
    if (actionKey === 'edit' && typeof onEdit === 'function') {
      onEdit(department);
    } else if (actionKey === 'delete' && typeof onDelete === 'function') {
      onDelete(department);
    } else {
      console.warn('Action handler not available for:', actionKey);
    }
  };

  // Custom render function for department type
  const renderDepartmentType = (department) => {
    if (department.type) {
      return department.type;
    }
    return t('common:none');
  };

  // Custom render function for department level
  const renderDepartmentLevel = (department) => {
    if (department.level) {
      return department.level;
    }
    return t('common:none');
  };

  // Custom render function for department head
  const renderDepartmentHead = (department) => {
    if (department.departmentHead) {
      return department.departmentHead.username || department.departmentHead.name || t('common:none');
    } else if (department.departmentHeadId) {
      // If only ID is available, we might need to fetch user details
      // For now, just show that a head is assigned
      return t('organization:department.headAssigned');
    }
    return t('common:none');
  };

  // Custom render function for tags count
  const renderTagsCount = (department) => {
    if (department.tags && Object.keys(department.tags).length > 0) {
      return `${Object.keys(department.tags).length} ${t('organization:department.tags')}`;
    }
    return t('organization:department.noTags');
  };

  return (
    <MobileEntityList
      entities={departments}
      loading={loading}
      onAction={handleAction}
      fieldMappings={{
        name: 'name',
        status: 'active',
        description: 'description',
        type: 'type',
        lastModified: 'lastModified'
      }}
      actionConfig={[
        {
          key: 'edit',
          label: 'common:edit',
          icon: 'Edit',
          color: 'primary'
        },
        {
          key: 'delete',
          label: 'common:delete',
          icon: 'Delete',
          color: 'error'
        }
      ]}
      i18nKeys={{
        noItems: 'organization:department.noDepartments',
        description: 'organization:department.description',
        lastModified: 'organization:department.lastModified',
        type: 'organization:department.type',
        actions: 'organization:department.actions',
        chooseAction: 'organization:department.chooseAction'
      }}
      renderItem={(department, openActionMenu) => (
        <Box sx={{ display: 'flex', alignItems: 'flex-start', width: '100%' }}>
          <Box sx={{ flexGrow: 1 }}>
            <Box sx={{ display: 'flex', alignItems: 'center' }}>
              <Typography 
                variant="subtitle1" 
                component="div" 
                sx={{ flexGrow: 1, color: theme.palette.primary.main, fontWeight: 'bold' }}
              >
                {department.name}
              </Typography>
            </Box>
            {department.type && (
              <Typography variant="body2" component="div" sx={{ mb: 0.5 } } color="text.secondary" >
                <strong>{t('organization:department.type')}:</strong> {renderDepartmentType(department)}
              </Typography>
            )}
            {department.level && (
              <Typography variant="body2" component="div" sx={{ mb: 0.5 }} color="text.secondary" >
                <strong>{t('organization:department.level')}:</strong> {renderDepartmentLevel(department)}
              </Typography>
            )}
            {(department.departmentHead || department.departmentHeadId) && (
              <Typography variant="body2" color="text.secondary" sx={{ mb: 0.5 }}>
                <strong>{t('organization:department.head')}:</strong> {renderDepartmentHead(department)}
              </Typography>
            )}
            <Typography variant="body2" color="text.secondary" sx={{ mb: 0.5 }}>
              <strong>{t('organization:department.tags')}:</strong> {renderTagsCount(department)}
            </Typography>
            <Typography variant="body2" color="text.secondary">
              {department.description || t('organization:department.noDescription')}
            </Typography>
          </Box>
          <Chip
            style={{ alignSelf: 'center' }}
            label={(department.active ? t('common:active') : t('common:inactive')).toUpperCase()}
            size="small"
            color={department.active ? 'success' : 'default'}
            variant="outlined"
          />
          <IconButton
            style={{ alignSelf: 'center' }}
            edge="end"
            aria-label="actions"
            onClick={(e) => {
              openActionMenu(department)
            }}
            size="small"
            sx={{ ml: 1 }}
          >
            <MoreVert />
          </IconButton>
        </Box>
      )}
    />
  );
};

export default MobileDepartmentList;
