import React from 'react';
import { useTranslation } from 'react-i18next';
import { Box, Chip, Typography, useTheme, IconButton } from '@mui/material';
import { MoreVert } from '@mui/icons-material';
import MobileEntityList from '../common/MobileEntityList';

/**
 * Mobile-friendly list component for displaying processes using the generic MobileEntityList
 */
const MobileProcessList = ({ 
  processes, 
  onEditVersion,
  onActivateVersion,
  loading = false 
}) => {
  const { t } = useTranslation();
  const theme = useTheme();

  const handleAction = (actionKey, process) => {
    console.log('Action triggered:', actionKey, process);
    if (actionKey === 'createVersion' && typeof onEditVersion === 'function') {
      onEditVersion(process);
    } else if (actionKey === 'activate' && typeof onActivateVersion === 'function') {
      onActivateVersion(process);
    } else {
      console.warn('Action handler not available for:', actionKey);
    }
  };

  const renderVersions = (process) => {
    if (process.versions && process.versions.length > 0) {
      return (
        <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 0.5, mt: 1 }}>
          {process.versions.map(version => (
            <Chip
              key={version.id}
              label={`v${version.version} (${t(`process:status.${version.status}`)})`}
              size="small"
              color={version.status === 'DEPLOYED' ? 'success' : version.status === 'DRAFT' ? 'primary' : 'default'}
              variant="outlined"
            />
          ))}
        </Box>
      );
    } else {
      return (
        <Chip
          label={t('process:status.no-version-defined')}
          color="error"
          size="small"
          variant="outlined"
          sx={{
            minWidth: 80,
            fontWeight: 'medium',
            borderWidth: 1.5,
            mt: 1
          }}
        />
      );
    }
  };

  return (
    <MobileEntityList
      entities={processes}
      loading={loading}
      onAction={handleAction}
      fieldMappings={{
        name: 'processName',
        key: 'processKey',
        owner: 'owner',
        businessOwner: 'business',
        versions: 'versions'
      }}
      actionConfig={[
        {
          key: 'createVersion',
          label: 'process:table.createVersion',
          icon: 'PostAdd',
          color: 'primary'
        },
        {
          key: 'activate',
          label: 'process:table.activate',
          icon: 'CheckCircle',
          color: 'success',
          disabled: (process) => { 
            console.log(process?.versions?.length)
            return !(process?.versions?.length >0) ;
          }
        }
      ]}
      i18nKeys={{
        noItems: 'process:noProcesses',
        key: 'process:key',
        owner: 'process:owner',
        businessOwner: 'process:businessOwner',
        versions: 'process:versions',
        actions: 'common:actions',
        chooseAction: 'process:chooseAction'
      }}
      renderItem={(process, openActionMenu) => (
        <Box sx={{ display: 'flex', alignItems: 'flex-start', width: '100%' }}>
          <Box sx={{ flexGrow: 1 }}>
            <Box sx={{ display: 'flex', alignItems: 'center' }}>
              <Typography 
                variant="subtitle1" 
                component="div" 
                sx={{ flexGrow: 1, color: theme.palette.primary.main, fontWeight: 'bold' }}
              >
                {process.processName}
              </Typography>
            </Box>
            <Typography variant="body2" color="text.secondary">
              {t('process:key')}: {process.processKey}
            </Typography>
            {process.owner && (
              <Typography variant="body2" color="text.secondary">
                {t('process:owner')}: {process.owner.username}
              </Typography>
            )}
            {process.business && (
              <Typography variant="body2" color="text.secondary">
                {t('process:businessOwner')}: {process.business.username}
              </Typography>
            )}
            {renderVersions(process)}
          </Box>
          <IconButton
            edge="end"
            aria-label="actions"
            onClick={(e) => {
              openActionMenu(process);
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

export default MobileProcessList;
