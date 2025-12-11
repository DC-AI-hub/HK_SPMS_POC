import React from 'react';
import { useTranslation } from 'react-i18next';
import { Box, Chip, Typography, useTheme, IconButton } from '@mui/material';
import { MoreVert } from '@mui/icons-material';
import MobileEntityList from '../common/MobileEntityList';

/**
 * Mobile-friendly list component for displaying users using the generic MobileEntityList
 */
const MobileUserList = ({ 
  users, 
  onEdit, 
  onDelete,
  loading = false 
}) => {
  const { t } = useTranslation();
  const theme = useTheme();

  const handleAction = (actionKey, user) => {
    console.log('Action triggered:', actionKey, user);
    if (actionKey === 'edit' && typeof onEdit === 'function') {
      onEdit(user);
    } else if (actionKey === 'delete' && typeof onDelete === 'function') {
      onDelete(user);
    } else {
      console.warn('Action handler not available for:', actionKey);
    }
  };

  // Custom render function for user type to handle translation
  const renderUserType = (user) => {
    if (user.type) {
      switch(user.type) {
        case 'STAFF': return t('organization:user.form.typeStaff');
        case 'VENDOR': return t('organization:user.form.typeVendor');
        case 'MACHINE': return t('organization:user.form.typeMachine');
        default: return user.type;
      }
    }
    return '';
  };

  return (
    <MobileEntityList
      entities={users}
      loading={loading}
      onAction={handleAction}
      fieldMappings={{
        name: 'username',
        status: 'active',
        description: 'description',
        type: 'type', // Will be handled by custom rendering
        email: 'email'
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
        noItems: 'organization:user.noUsers',
        description: 'organization:user.form.description',
        lastModified: 'organization:user.lastModified',
        type: 'organization:user.form.type',
        actions: 'organization:user.actions',
        chooseAction: 'organization:user.chooseAction'
      }}
      renderItem={(user, openActionMenu) => (
        <Box sx={{ display: 'flex', alignItems: 'flex-start', width: '100%' }}>
          <Box sx={{ flexGrow: 1 }}>
            <Box sx={{ display: 'flex', alignItems: 'center' }}>
              <Typography 
                variant="subtitle1" 
                component="div" 
                sx={{ flexGrow: 1, color: theme.palette.primary.main, fontWeight: 'bold' }}
              >
                {user.username}
              </Typography>
             
            </Box>
            {user.email && (
              <Typography variant="body2" color="text.secondary" component="div" sx={{ mb: 0.5 }}>
                {user.email}
              </Typography>
            )}
            {user.type && (
              <Typography variant="body2" color="text.secondary" component="div" sx={{ mb: 0.5 }}>
                {renderUserType(user)}
              </Typography>
            )}
            <Typography variant="body2" color="text.secondary">
              {user.description || t('organization:user.noDescription')}
            </Typography>
          </Box>
           <Chip  style={{ alignSelf: 'center' }}
                label={(user.active ? t('common:active') : t('common:inactive')).toUpperCase()}
                size="small"
                color={user.active ? 'success' : 'default'}
                variant="outlined"
              />
          <IconButton style={{ alignSelf: 'center' }}
            edge="end"
            aria-label="actions"
            onClick={(e) => {
              openActionMenu(user)
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

export default MobileUserList;
