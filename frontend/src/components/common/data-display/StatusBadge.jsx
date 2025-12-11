import React from 'react';
import {
  Chip,
  Box,
  Typography,
  Tooltip
} from '@mui/material';
import {
  CheckCircle as SuccessIcon,
  Error as ErrorIcon,
  Warning as WarningIcon,
  Info as InfoIcon,
  Schedule as PendingIcon,
  Cancel as CanceledIcon
} from '@mui/icons-material';

/**
 * StatusBadge - A component for displaying status indicators with consistent styling
 * Provides standardized status badges with icons, colors, and tooltips
 * 
 * @param {Object} props
 * @param {string} props.status - Status value
 * @param {string} [props.variant] - Chip variant ('filled' | 'outlined')
 * @param {string} [props.size] - Size of the badge ('small' | 'medium')
 * @param {boolean} [props.showIcon] - Whether to show status icon
 * @param {boolean} [props.showLabel] - Whether to show status label
 * @param {string} [props.customLabel] - Custom label text (overrides default mapping)
 * @param {Object} [props.customColors] - Custom color mapping for specific statuses
 * @param {string} [props.className] - Additional CSS classes
 * @param {Object} [props.style] - Additional inline styles
 */
const StatusBadge = ({
  status,
  variant = 'filled',
  size = 'medium',
  showIcon = true,
  showLabel = true,
  customLabel,
  customColors = {},
  className = '',
  style = {},
  ...props
}) => {
  // Default status configuration
  const statusConfig = {
    // Success states
    success: {
      label: 'Success',
      icon: SuccessIcon,
      color: 'success',
      tooltip: 'Operation completed successfully'
    },
    completed: {
      label: 'Completed',
      icon: SuccessIcon,
      color: 'success',
      tooltip: 'Process completed successfully'
    },
    approved: {
      label: 'Approved',
      icon: SuccessIcon,
      color: 'success',
      tooltip: 'Item has been approved'
    },
    active: {
      label: 'Active',
      icon: SuccessIcon,
      color: 'success',
      tooltip: 'Currently active'
    },
    
    // Error states
    error: {
      label: 'Error',
      icon: ErrorIcon,
      color: 'error',
      tooltip: 'An error occurred'
    },
    failed: {
      label: 'Failed',
      icon: ErrorIcon,
      color: 'error',
      tooltip: 'Operation failed'
    },
    rejected: {
      label: 'Rejected',
      icon: ErrorIcon,
      color: 'error',
      tooltip: 'Item has been rejected'
    },
    
    // Warning states
    warning: {
      label: 'Warning',
      icon: WarningIcon,
      color: 'warning',
      tooltip: 'Warning condition'
    },
    pending: {
      label: 'Pending',
      icon: PendingIcon,
      color: 'warning',
      tooltip: 'Waiting for action or approval'
    },
    processing: {
      label: 'Processing',
      icon: PendingIcon,
      color: 'warning',
      tooltip: 'Currently being processed'
    },
    
    // Info states
    info: {
      label: 'Info',
      icon: InfoIcon,
      color: 'info',
      tooltip: 'Information'
    },
    draft: {
      label: 'Draft',
      icon: InfoIcon,
      color: 'info',
      tooltip: 'Draft version - not published'
    },
    
    // Neutral states
    canceled: {
      label: 'Canceled',
      icon: CanceledIcon,
      color: 'default',
      tooltip: 'Operation was canceled'
    },
    inactive: {
      label: 'Inactive',
      icon: CanceledIcon,
      color: 'default',
      tooltip: 'Currently inactive'
    }
  };

  // Get status configuration or default to info
  const config = statusConfig[status?.toLowerCase()] || {
    label: customLabel || status || 'Unknown',
    icon: InfoIcon,
    color: 'default',
    tooltip: `Status: ${status || 'Unknown'}`
  };

  // Override colors if custom colors provided
  const finalColor = customColors[status] || config.color;

  const IconComponent = config.icon;
  const labelText = customLabel || config.label;
  const tooltipText = config.tooltip;

  const badgeContent = (
    <Box
      sx={{
        display: 'flex',
        alignItems: 'center',
        gap: 0.5
      }}
    >
      {showIcon && <IconComponent fontSize={size === 'small' ? 'small' : 'medium'} />}
      {showLabel && (
        <Typography
          variant="caption"
          sx={{
            fontWeight: 'medium',
            textTransform: 'uppercase',
            fontSize: size === 'small' ? '0.7rem' : '0.75rem'
          }}
        >
          {labelText}
        </Typography>
      )}
    </Box>
  );

  return (
    <Tooltip title={tooltipText} arrow>
      <Chip
        variant={variant}
        color={finalColor}
        size={size}
        label={badgeContent}
        sx={{
          borderRadius: 2,
          fontWeight: 'medium',
          ...style
        }}
        className={className}
        {...props}
      />
    </Tooltip>
  );
};

export default StatusBadge;
