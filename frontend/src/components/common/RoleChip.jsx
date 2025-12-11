import React from 'react';
import { Box, IconButton, Tooltip } from '@mui/material';
import { Delete } from '@mui/icons-material';

/**
 * Reusable chip component for displaying roles with remove functionality and detailed tooltip
 * @param {Object} props - Component props
 * @param {Object} props.role - Role object { id: number, name: string, description: string, permissions: Array<string>, parentRoles: Array<Object>, childRoles: Array<Object>, active: boolean, lastModified: number, createdBy: string, updatedBy: string, createdTime: number }
 * @param {Function} props.onRemove - Remove handler function (roleId: string) => void
 * @param {boolean} props.disabled - Disabled state
 * @param {boolean} props.showDelete - Whether to show delete icon, defaults to true
 * @returns {JSX.Element} Role chip component
 */
const RoleChip = ({ role, onRemove, disabled, showDelete = true }) => {
  // Generate tooltip content based on role details
  const generateTooltipContent = () => {
    const lines = [];
    
    if (role.description) {
      lines.push(`Description: ${role.description}`);
    }
    
    if (role.permissions && role.permissions.length > 0) {
      lines.push(`Permissions: ${role.permissions.join(', ')}`);
    }
    
    lines.push(`Active: ${role.active ? 'Yes' : 'No'}`);
    
    if (role.createdBy) {
      lines.push(`Created by: ${role.createdBy}`);
    }
    
    if (role.createdTime) {
      const date = new Date(role.createdTime);
      lines.push(`Created: ${date.toLocaleDateString()}`);
    }
    
    if (role.updatedBy) {
      lines.push(`Updated by: ${role.updatedBy}`);
    }
    
    if (role.lastModified) {
      const date = new Date(role.lastModified);
      lines.push(`Last modified: ${date.toLocaleDateString()}`);
    }
    
    return lines.join('\n');
  };

  const tooltipContent = generateTooltipContent();

  return (
    <Box 
      className=" bg-gray-100 rounded-full px-3 py-1"
      data-testid="role-chip"
    >
      <Tooltip title={tooltipContent} arrow placement="top">
        <span className="mr-2">{role.name}</span>
      </Tooltip>
      {showDelete && (
        <IconButton 
          size="small" 
          onClick={() => onRemove(role.id)}
          disabled={disabled}
          aria-label={`Remove ${role.name} role`}
        >
          <Delete fontSize="small" />
        </IconButton>
      )}
    </Box>
  );
};

export default RoleChip;
