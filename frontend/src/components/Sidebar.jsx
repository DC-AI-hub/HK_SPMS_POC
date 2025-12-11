import React, { useState } from 'react';
import { NavLink } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { TextField, useMediaQuery, CircularProgress, Alert } from '@mui/material';
import SearchIcon from '@mui/icons-material/Search';
import DashboardIcon from '@mui/icons-material/Dashboard';
import PeopleIcon from '@mui/icons-material/People';
import LockIcon from '@mui/icons-material/Lock';
import TimelineIcon from '@mui/icons-material/Timeline';
import AssignmentIcon from '@mui/icons-material/Assignment';
import SettingsIcon from '@mui/icons-material/Settings';
import AssessmentIcon from '@mui/icons-material/Assessment';
import WorkIcon from '@mui/icons-material/Work';
import { useTheme } from '../theme/ThemeContext';
import HKEXLogo from '../assets/HKEX_logo.svg';
import { useConfig } from '../hooks/useConfig';

/**
 * Sidebar component with navigation links
 * @param {Object} props - Component props
 * @param {boolean} props.isMobileContextMenu - Whether this is used in mobile context menu
 * @param {function} props.onItemClick - Function to call when a menu item is clicked (for mobile)
 * @returns {JSX.Element} Sidebar structure
 */
const Sidebar = ({ isMobileContextMenu = false, onItemClick }) => {
  const [searchTerm, setSearchTerm] = useState('');
  const { theme } = useTheme();
  const { t } = useTranslation();
  const isSmallScreen = useMediaQuery(theme => theme.breakpoints.down('sm'));
  const {
    getFilteredMenus,
    loading,
    error,
    isMenuConfigValid,
    getTransformedMenuConfig
  } = useConfig();

  // Get filtered menu items based on search term
  const filteredItems = getFilteredMenus(searchTerm);

  // Fallback to hardcoded menu items if no valid menu configuration exists
  const getFallbackMenuItems = () => {
    return [
      {
        path: '/dashboard',
        label: t('sidebar:dashboard'),
        icon: React.createElement(DashboardIcon, { className: "mr-3 h-6 w-6" })
      },
      {
        path: '/organization',
        label: t('sidebar:organization'),
        icon: React.createElement(PeopleIcon, { className: "mr-3 h-6 w-6" })
      },
      {
        path: '/access',
        label: t('sidebar:access'),
        icon: React.createElement(LockIcon, { className: "mr-3 h-6 w-6" })
      },
      {
        path: '/process',
        label: t('sidebar:process'),
        icon: React.createElement(TimelineIcon, { className: "mr-3 h-6 w-6" })
      },
      {
        path: '/user-process',
        label: t('sidebar:userProcess'),
        icon: React.createElement(AssignmentIcon, { className: "mr-3 h-6 w-6" })
      },
      {
        path: '/system-configuration',
        label: t('sidebar:systemConfiguration'),
        icon: React.createElement(SettingsIcon, { className: "mr-3 h-6 w-6" })
      },
      {
        path: '/reports',
        label: t('sidebar:reports'),
        icon: React.createElement(AssessmentIcon, { className: "mr-3 h-6 w-6" })
      },
      {
        path: '/project',
        label: t('sidebar:project'),
        icon: React.createElement(WorkIcon, { className: "mr-3 h-6 w-6" })
      }
    ];
  };

  // Use configurable menus if available, otherwise fallback to hardcoded
  const menuItemsToRender = isMenuConfigValid() ? filteredItems : getFallbackMenuItems();
  // Show loading state
  if (loading) {
    return (
      <div className="flex flex-col h-full justify-center items-center" style={{
        backgroundColor: theme.palette.primary.main,
        color: theme.palette.text.light,
        fontFamily: theme.typography.fontFamily
      }}>
        <CircularProgress size={40} style={{ color: theme.palette.text.light }} />
        <p className="mt-4 text-sm">{t('sidebar:loading')}</p>
      </div>
    );
  }

  // Show error state
  if (error) {
    return (
      <div className="flex flex-col h-full justify-center items-center p-4" style={{
        backgroundColor: theme.palette.primary.main,
        color: theme.palette.text.light,
        fontFamily: theme.typography.fontFamily
      }}>
        <Alert severity="error" className="w-full">
          {t('sidebar:configError')}
        </Alert>
      </div>
    );
  }

  return (
    <div className="flex flex-col h-full" style={{
      overflow: 'hidden',
      fontFamily: theme.typography.fontFamily
    }}>
      {/* Header: Logo + SPMS - Only show in desktop sidebar, not mobile context menu */}
      {!isMobileContextMenu && (
        <div className={`flex items-center ${isSmallScreen ? 'px-3 py-3' : 'px-8 py-6'} gap-3`}
          style={{ backgroundColor: theme.palette.primary.main}}
        >
          <img src={HKEXLogo} alt="HKEX Logo" width={120} height={120}  />
          {!isSmallScreen && (
            <span style={{  fontFamily: theme.typography.fontFamily, fontWeight: 700 }}>

            </span>
          )}
        </div>
      )}

      {/* Search moved below header - Only show in desktop sidebar */}
      {!isSmallScreen && !isMobileContextMenu && (
        <div className="px-4 overflow-hidden"
          style={{ backgroundColor: theme.palette.primary.main}}
        >
          <TextField
            fullWidth
            variant="outlined"
            size="small"
            placeholder={t('sidebar:search')}
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            InputProps={{
              startAdornment: <SearchIcon fontSize="small" className="mr-2" style={{
                color: theme.palette.text.secondary
              }}
              />,
              endAdornment: searchTerm && (
                <button
                  onClick={() => setSearchTerm('')}
                  style={{
                    color: theme.palette.text.secondary
                  }}
                >
                  ✕
                </button>
              ),
              style: {
                backgroundColor: theme.palette.background.paper
              }
            }}
            className="mb-4"
          />
        </div>
      )}
      <div className="flex flex-col flex-1 overflow-y-auto overflow-x-hidden" style={{
         backgroundColor: theme.palette.primary.main,
         color: theme.palette.text.light
         }}>
        <nav className={`flex-1 ${isSmallScreen && !isMobileContextMenu ? 'px-1' : 'px-4'} py-4 space-y-1`} style={{ backgroundColor: theme.palette.primary.main }}>
          {menuItemsToRender.length > 0 ? (
            menuItemsToRender.map((item) => (
              <NavLink
                key={item.path}
                to={item.path}
                className={({ isActive }) =>
                  `group flex items-center ${isSmallScreen && !isMobileContextMenu ? 'px-1 justify-center' : 'px-3'} py-3 text-sm font-medium rounded-md transition-colors duration-200`
                }
                style={({ isActive }) => ({
                  backgroundColor: isActive ? theme.palette.action.selected : 'transparent',
                  
                })}
                onClick={isMobileContextMenu ? onItemClick : undefined}
              >
                {item.icon && React.cloneElement(item.icon, {
                  className: isSmallScreen && !isMobileContextMenu ? 'h-6 w-6' : 'mr-3 h-6 w-6'
                })}
                {(!isSmallScreen || isMobileContextMenu) && (
                  <span className="transition-all duration-200 whitespace-nowrap" 
                  style={{ fontFamily: theme.typography.fontFamily

                   }}>
                    {item.label}
                  </span>
                )}
              </NavLink>
            ))
          ) : (
            <div className="px-2 py-2 text-sm">
              {t('sidebar:noItems')}
            </div>
          )}
        </nav>
      </div>
    </div>
  );
};

export default Sidebar;
