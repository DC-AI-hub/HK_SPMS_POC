import React from 'react';
import { useTranslation } from 'react-i18next';
import { Box, Chip, Typography, useTheme, IconButton } from '@mui/material';
import { MoreVert } from '@mui/icons-material';
import MobileEntityList from '../common/MobileEntityList';

/**
 * Mobile-friendly list component for displaying roles using the generic MobileEntityList
 */
const MobileRoleList = ({ 
  roles, 
  onEdit, 
  onDelete,
  loading = false 
}) => {
  const { t } = useTranslation();
  const theme = useTheme();

  const handleAction = (actionKey, role) => {
    console.log('Action triggered:', actionKey, role);
    if (actionKey === 'edit' && typeof onEdit === 'function') {
      onEdit(role);
    } else if (actionKey === 'delete' && typeof onDelete === 'function') {
      onDelete(role);
    } else {
      console.warn('Action handler not available for:', actionKey);
    }
  };

  return (
    <MobileEntityList
      entities={roles}
      loading={loading}
      onAction={handleAction}
      fieldMappings={{
        name: 'name',
        description: 'description',
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
        noItems: 'common:noItems',
        description: 'common:description',
        lastModified: 'common:lastModified',
        actions: 'common:actions',
        chooseAction: 'common:chooseAction'
      }}
      renderItem={(role, openActionMenu) => (
        <Box sx={{ display: 'flex', alignItems: 'flex-start', width: '100%' }}>
          <Box sx={{ flexGrow: 1 }}>
            <Box sx={{ display: 'flex', alignItems: 'center' }}>
              <Typography 
                variant="subtitle1" 
                component="div" 
                sx={{ flexGrow: 1, color: theme.palette.primary.main, fontWeight: 'bold' }}
              >
                {role.name}
              </Typography>
            </Box>
            <Typography variant="body2" color="text.secondary">
              {role.description || t('common:noDescription')}
            </Typography>
          </Box>
          <IconButton 
            edge="end"
            aria-label="actions"
            onClick={(e) => {
              e.stopPropagation();
              openActionMenu(role);
            }}
            size="small"
            sx={{ ml: 1, alignSelf: 'center' }}
          >
            <MoreVert />
          </IconButton>
        </Box>
      )}
    />
  );
};

export default MobileRoleList;
