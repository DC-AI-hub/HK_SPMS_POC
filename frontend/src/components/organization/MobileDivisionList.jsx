import React from 'react';
import { useTranslation } from 'react-i18next';
import { Box, Chip, Typography, useTheme, IconButton } from '@mui/material';
import { MoreVert } from '@mui/icons-material';
import MobileEntityList from '../common/MobileEntityList';

/**
 * Mobile-friendly list component for displaying divisions using the generic MobileEntityList
 */
const MobileDivisionList = ({ 
  divisions, 
  onEdit, 
  onDelete,
  loading = false 
}) => {
  const { t } = useTranslation();
  const theme = useTheme();

  const handleAction = (actionKey, division) => {
    console.log('Action triggered:', actionKey, division);
    if (actionKey === 'edit' && typeof onEdit === 'function') {
      onEdit(division);
    } else if (actionKey === 'delete' && typeof onDelete === 'function') {
      onDelete(division);
    } else {
      console.warn('Action handler not available for:', actionKey);
    }
  };

  // Custom render function for division head
  const renderDivisionHead = (division) => {
    if (division.divisionHead) {
      return division.divisionHead.name || t('common:none');
    }
    return t('common:none');
  };

  // Custom render function for company
  const renderCompany = (division) => {
    if (division.company) {
      return division.company.name || t('common:none');
    }
    return t('common:none');
  };

  return (
    <MobileEntityList
      entities={divisions}
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
        noItems: 'organization:division.noDivisions',
        description: 'organization:division.description',
        lastModified: 'organization:division.lastModified',
        type: 'organization:division.type',
        actions: 'organization:division.actions',
        chooseAction: 'organization:division.chooseAction'
      }}
      renderItem={(division, openActionMenu) => (
        <Box sx={{ display: 'flex', alignItems: 'flex-start', width: '100%' }}>
          <Box sx={{ flexGrow: 1 }}>
            <Box sx={{ display: 'flex', alignItems: 'center' }}>
              <Typography 
                variant="subtitle1" 
                component="div" 
                sx={{ flexGrow: 1, color: theme.palette.primary.main, fontWeight: 'bold' }}
              >
                {division.name}
              </Typography>
            </Box>
            {division.type && (
              <Typography variant="body2" color="text.secondary"  component="div" sx={{ mb: 0.5 }}>
                {division.type}
              </Typography>
            )}
            <Typography variant="body2" color="text.secondary" sx={{ mb: 0.5 }}>
              <strong>{t('organization:division.head')}:</strong> {renderDivisionHead(division)}
            </Typography>
            <Typography variant="body2" color="text.secondary" sx={{ mb: 0.5 }}>
              <strong>{t('organization:division.relatedTo')}:</strong> {renderCompany(division)}
            </Typography>
            <Typography variant="body2" color="text.secondary">
              {division.description || t('organization:division.noDescription')}
            </Typography>
          </Box>
          <Chip
            style={{ alignSelf: 'center' }}
            label={(division.active ? t('common:active') : t('common:inactive')).toUpperCase()}
            size="small"
            color={division.active ? 'success' : 'default'}
            variant="outlined"
          />
          <IconButton
            style={{ alignSelf: 'center' }}
            edge="end"
            aria-label="actions"
            onClick={(e) => {
              openActionMenu(division)
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

export default MobileDivisionList;
