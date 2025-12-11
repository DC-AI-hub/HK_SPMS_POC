import React from 'react';
import DashboardIcon from '@mui/icons-material/Dashboard';
import PeopleIcon from '@mui/icons-material/People';
import LockIcon from '@mui/icons-material/Lock';
import TimelineIcon from '@mui/icons-material/Timeline';
import AssignmentIcon from '@mui/icons-material/Assignment';
import SettingsIcon from '@mui/icons-material/Settings';

/**
 * Icon mapping from string names to React components
 * This provides a mapping between backend icon strings and actual icon components
 */
const iconMap = {
  'DashboardIcon': DashboardIcon,
  'PeopleIcon': PeopleIcon,
  'LockIcon': LockIcon,
  'TimelineIcon': TimelineIcon,
  'AssignmentIcon': AssignmentIcon,
  'SettingsIcon': SettingsIcon,
  // Add more icons as needed
};

/**
 * Transforms backend menu configuration to frontend format
 * @param {Array} menuConfig - Array of menu items from backend
 * @returns {Array} Transformed menu items for frontend use
 */
export const transformMenuConfig = (menuConfig) => {
  if (!menuConfig || !Array.isArray(menuConfig)) {
    return [];
  }

  return menuConfig
    .filter(item => item.active) // Only include active items
    .sort((a, b) => a.displayOrder - b.displayOrder) // Sort by display order
    .map(item => ({
      id: item.id,
      path: item.path,
      label: item.title, // Use title directly as label
      icon: getIconComponent(item.icon),
      displayOrder: item.displayOrder,
      parentMenuId: item.parentMenuId,
      allowedRoleIds: item.allowedRoleIds || []
    }));
};

/**
 * Gets the React icon component based on icon string
 * @param {string} iconString - Icon identifier from backend
 * @returns {React.Component|null} React icon component or null if not found
 */
export const getIconComponent = (iconString) => {
  if (!iconString) return null;
  
  const IconComponent = iconMap[iconString];
  if (!IconComponent) {
    console.warn(`Icon not found: ${iconString}`);
    return null;
  }
  
  return React.createElement(IconComponent);
};

/**
 * Filters menu items based on search term
 * @param {Array} menuItems - Array of menu items to filter
 * @param {string} searchTerm - Search term to filter by
 * @returns {Array} Filtered menu items
 */
export const filterMenusBySearch = (menuItems, searchTerm) => {
  if (!searchTerm) return menuItems;
  
  const lowerCaseSearch = searchTerm.toLowerCase();
  return menuItems.filter(item =>
    item.label.toLowerCase().includes(lowerCaseSearch)
  );
};

/**
 * Gets top-level menu items (items with no parent or parentId 0)
 * @param {Array} menuItems - Array of all menu items
 * @returns {Array} Top-level menu items
 */
export const getTopLevelMenus = (menuItems) => {
  return menuItems.filter(item => 
    item.parentMenuId === null || item.parentMenuId === 0
  );
};

/**
 * Checks if menu configuration is valid and contains items
 * @param {Array} menuConfig - Menu configuration array
 * @returns {boolean} True if menu configuration is valid and non-empty
 */
export const isValidMenuConfig = (menuConfig) => {
  return Array.isArray(menuConfig) && menuConfig.length > 0;
};
