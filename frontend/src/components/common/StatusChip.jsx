import React from 'react';
import { Chip } from '@mui/material';

const StatusChip = ({ status, value, onClick, clickable }) => {
  const getColor = () => {
    switch (status) {
      case 'DRAFT': 
        return 'primary';
      case 'DEPLOYED': 
        return 'success';
      case 'ACTIVE' , 'created':
        return 'info';
      case 'INACTIVE': 
        return 'error';
      default: 
        return 'default';
    }
  };

  return (
    <Chip
      label={value.toUpperCase()}
      color={getColor()}
      size="small"
      variant="outlined"
      onClick={clickable ? onClick : undefined}
      sx={{
        minWidth: 80,
        fontWeight: 'medium',
        borderWidth: 1.5,
        cursor: clickable ? 'pointer' : 'default'
      }}
    />
  );
};

export default StatusChip;
