import React from 'react';
import {
  FormControl,
  InputLabel,
  Box,
  Typography,
  ListItemIcon,
  ListItemText,
  Autocomplete,
  TextField
} from '@mui/material';
import {
  Dashboard as DashboardIcon,
  People as PeopleIcon,
  Settings as SettingsIcon,
  Business as BusinessIcon,
  Assignment as AssignmentIcon,
  Notifications as NotificationsIcon,
  BarChart as BarChartIcon,
  Email as EmailIcon,
  Folder as FolderIcon,
  Home as HomeIcon,
  List as ListIcon,
  Lock as LockIcon,
  Person as PersonIcon,
  Search as SearchIcon,
  Star as StarIcon,
  TrendingUp as TrendingUpIcon,
  Work as WorkIcon,
  ExitToApp as ExitToAppIcon
} from '@mui/icons-material';

// Curated list of commonly used MUI icons including "No icon" option
const iconOptions = [
  { value: '', label: 'No icon', icon: null },
  { value: 'DashboardIcon', label: 'Dashboard', icon: <DashboardIcon /> },
  { value: 'PeopleIcon', label: 'People', icon: <PeopleIcon /> },
  { value: 'SettingsIcon', label: 'Settings', icon: <SettingsIcon /> },
  { value: 'BusinessIcon', label: 'Business', icon: <BusinessIcon /> },
  { value: 'AssignmentIcon', label: 'Assignment', icon: <AssignmentIcon /> },
  { value: 'NotificationsIcon', label: 'Notifications', icon: <NotificationsIcon /> },
  { value: 'BarChartIcon', label: 'Analytics', icon: <BarChartIcon /> },
  { value: 'EmailIcon', label: 'Email', icon: <EmailIcon /> },
  { value: 'FolderIcon', label: 'Folder', icon: <FolderIcon /> },
  { value: 'HomeIcon', label: 'Home', icon: <HomeIcon /> },
  { value: 'ListIcon', label: 'List', icon: <ListIcon /> },
  { value: 'LockIcon', label: 'Security', icon: <LockIcon /> },
  { value: 'PersonIcon', label: 'Profile', icon: <PersonIcon /> },
  { value: 'SearchIcon', label: 'Search', icon: <SearchIcon /> },
  { value: 'StarIcon', label: 'Favorites', icon: <StarIcon /> },
  { value: 'TrendingUpIcon', label: 'Trends', icon: <TrendingUpIcon /> },
  { value: 'WorkIcon', label: 'Work', icon: <WorkIcon /> },
  { value: 'ExitToAppIcon', label: 'Logout', icon: <ExitToAppIcon /> }
];

/**
 * IconSelector - A reusable component for selecting MUI icons visually
 * @param {Object} props
 * @param {string} props.value - The currently selected icon value
 * @param {function} props.onChange - Function called when selection changes
 * @param {string} props.label - Label for the selector
 * @param {boolean} props.fullWidth - Whether to take full width
 * @param {Object} props.error - Error state
 * @param {string} props.helperText - Helper text for error display
 */
const IconSelector = ({
  value,
  onChange,
  label = 'Icon',
  fullWidth = true,
  error = false,
  helperText = ''
}) => {
  const selectedIcon = iconOptions.find(option => option.value === value);

  return (
    <FormControl fullWidth={fullWidth} error={error}>
      <Autocomplete
        options={iconOptions}
        value={iconOptions.find(option => option.value === value) || null}
        onChange={(event, newValue) => {
          onChange(newValue ? newValue.value : '');
        }}
        getOptionLabel={(option) => option.label}
        renderOption={(props, option) => (
          <li {...props}>
            <Box display="flex" alignItems="center" gap={1}>
              {option.icon && (
                <ListItemIcon>
                  {option.icon}
                </ListItemIcon>
              )}
              <ListItemText 
                primary={option.label} 
                secondary={option.value || 'No icon'} 
              />
            </Box>
          </li>
        )}
        renderInput={(params) => {
          const selectedValue = iconOptions.find(option => option.value === value);
          return (
            <TextField
              {...params}
              label={label}
              error={error}
              helperText={helperText}
              InputProps={{
                ...params.InputProps,
                startAdornment: selectedValue?.icon ? (
                  <Box sx={{ mr: 1, display: 'flex', alignItems: 'center' }}>
                    {selectedValue.icon}
                  </Box>
                ) : null,
              }}
            />
          );
        }}
        isOptionEqualToValue={(option, value) => option.value === value.value}
      />
    </FormControl>
  );
};

export default IconSelector;
