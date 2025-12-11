import React from 'react';
import { useTranslation } from 'react-i18next';
import { Box, Chip, Typography, useTheme, IconButton } from '@mui/material';
import { MoreVert } from '@mui/icons-material';
import { CompanyType } from '../../pages/organization/constants';
import MobileEntityList from '../common/MobileEntityList';

/**
 * Mobile-friendly list component for displaying companies using the generic MobileEntityList
 */
const MobileCompanyList = ({ 
  companies, 
  onEdit, 
  onDelete,
  loading = false 
}) => {
  const { t } = useTranslation();
  const theme = useTheme();

  const handleAction = (actionKey, company) => {
    console.log('Action triggered:', actionKey, company);
    if (actionKey === 'edit' && typeof onEdit === 'function') {
      onEdit(company);
    } else if (actionKey === 'delete' && typeof onDelete === 'function') {
      onDelete(company);
    } else {
      console.warn('Action handler not available for:', actionKey);
    }
  };
  // Custom render function for company type to handle translation
  const renderCompanyType = (company) => {
    if (company.companyType) {
      return t(`organization:company.types.${CompanyType[company.companyType]}`);
    }
    return '';
  };

  return (
    <MobileEntityList
      entities={companies}
      loading={loading}
      onAction={handleAction}
      fieldMappings={{
        name: 'name',
        status: 'active',
        description: 'description',
        type: 'companyType', // Will be handled by custom rendering
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
        noItems: 'organization:company.noCompanies',
        description: 'organization:company.description',
        lastModified: 'organization:company.lastModified',
        type: 'organization:company.companyType',
        actions: 'organization:company.actions',
        chooseAction: 'organization:company.chooseAction'
      }}
      renderItem={(company, openActionMenu) => (
        <Box sx={{ display: 'flex', alignItems: 'flex-start', width: '100%' }}>
          <Box sx={{ flexGrow: 1 }}>
            <Box sx={{ display: 'flex', alignItems: 'center' }}>
              <Typography 
                variant="subtitle1" 
                component="div" 
                sx={{ flexGrow: 1, color: theme.palette.primary.main, fontWeight: 'bold' }}
              >
                {company.name}
              </Typography>
             
            </Box>
            {company.companyType && (
              <Typography variant="body2"  color="text.secondary"  component="div" sx={{ mb: 0.5 }}>
                {renderCompanyType(company)}
              </Typography>
            )}
            <Typography variant="body2" color="text.secondary">
              {company.description || t('organization:company.noDescription')}
            </Typography>
          </Box>
           <Chip  style={{ alignSelf: 'center' }}
                label={(company.active ? t('common:active') : t('common:inactive')).toUpperCase()}
                size="small"
                color={company.active ? 'success' : 'default'}
                variant="outlined"
              />
          <IconButton style={{ alignSelf: 'center' }}
            edge="end"
            aria-label="actions"
            onClick={(e) => {
              openActionMenu(company)
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

export default MobileCompanyList;
