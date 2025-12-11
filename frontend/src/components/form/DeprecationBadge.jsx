import React from 'react';
import { Chip } from '@mui/material';
import { useTranslation } from 'react-i18next';

const DeprecationBadge = ({ status }) => {
  const { t } = useTranslation();
  
  return (
    <Chip 
      label={status === 'DEPRECATED' 
        ? t('form:deprecated') 
        : t('form:active')} 
      color={status === 'DEPRECATED' ? 'error' : 'success'} 
      size="small"
      variant="outlined"
      sx={{ 
        fontWeight: 'bold',
        borderWidth: 1.5,
        '& .MuiChip-label': { px: 1 }
      }}
    />
  );
};

export default DeprecationBadge;
