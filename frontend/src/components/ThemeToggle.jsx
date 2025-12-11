import React from 'react';
import { IconButton } from '@mui/material';
import Brightness4Icon from '@mui/icons-material/Brightness4';
import Brightness7Icon from '@mui/icons-material/Brightness7';
import { useTheme } from '../theme/ThemeContext';

/**
 * Theme toggle component that allows switching between light and dark modes
 * @returns {JSX.Element} Theme toggle button
 */
const ThemeToggle = () => {
  const { isDarkMode, toggleTheme, theme } = useTheme();
  return (
    <IconButton onClick={toggleTheme} color="inherit" 
      style={{
        color: theme.palette.text.primary,
        backgroundColor: theme.palette.background.default,
        '&:hover': {
          backgroundColor: theme.palette.action.hover
        }
      }}
    >
      {isDarkMode ? <Brightness7Icon /> : <Brightness4Icon />}
    </IconButton>
  );
};

export default ThemeToggle;
