import React, { useState, useEffect } from 'react';
import MenuIcon from '@mui/icons-material/Menu';
import { useTranslation } from 'react-i18next';
import { useTheme } from '../theme/ThemeContext';
import { useMediaQuery, IconButton, Avatar } from '@mui/material';
import systemService from '../api/system/systemService';
import UserProfileCard from './UserProfileCard';
import LanguageSelectorWithFlags from './LanguageSelectorWithFlags';
import Logo from '../assets/HKEX_logo_blue.svg';


/**
 * Header component containing logo, user profile and notifications
 * @param {Object} props - Component props
 * @param {function} props.onToggleSidebar - Function to toggle desktop sidebar
 * @param {function} props.onToggleMobileMenu - Function to toggle mobile context menu
 * @param {boolean} props.isMobileScreen - Whether the screen is mobile size
 * @returns {JSX.Element} Header structure
 */
const Header = ({ onToggleSidebar, onToggleMobileMenu, isMobileScreen }) => {
  const { t } = useTranslation();
  const { theme } = useTheme();
  // Remove the local media query since we get isMobileScreen from parent
  const isSmallScreen = useMediaQuery(theme => theme.breakpoints.down('sm'));
  const [userInfo, setUserInfo] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [showProfileCard, setShowProfileCard] = useState(false);

  useEffect(() => {
    const fetchUserInfo = async () => {
      try {
        const response = await systemService.getLoginInfo();
        setUserInfo(response.data);
      } catch (err) {
        setError(err.message || 'Failed to fetch user information');
      } finally {
        setLoading(false);
      }
    };

    fetchUserInfo();
  }, []);

  return (
    <header style={{ backgroundColor: theme.palette.background.header }}>
      <div className="mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex justify-between items-center py-4">
          {/* Left Section */}
          <div className="flex items-center">
            {/* Menu Toggle Button - shows mobile menu on mobile, sidebar on desktop */}
            <button
              onClick={isMobileScreen ? onToggleMobileMenu : onToggleSidebar}
              className="p-2 focus:outline-none"
              style={{ color: theme.palette.text.primary }}
              aria-label={isMobileScreen ? "Open mobile menu" : "Toggle sidebar"}
            >
              <MenuIcon className="h-6 w-6" />
            </button>
            
            {/* Logo - only show on mobile screens */}
            {isMobileScreen && (
              <div className="ml-3">
                <img src={Logo} alt="App Logo" className="h-8 w-auto" />
              </div>
            )}
          </div>

          {/* Right Section */}
          <div className="flex items-center space-x-4">
            {/* Theme Toggle */}
            {/*<ThemeToggle />*/}
            {/* Language Selector */}
            <LanguageSelectorWithFlags />

            {/* Notifications */}
            <button
              type="button"
              style={{ color: theme.palette.text.secondary }}
              className="p-1 rounded-full hover:bg-gray-100 dark:hover:bg-gray-700 focus:outline-none"
            >
              <span className="sr-only">View notifications</span>
              <svg
                className="h-6 w-6"
                xmlns="http://www.w3.org/2000/svg"
                fill="none"
                viewBox="0 0 24 24"
                stroke="currentColor"
              >
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth={2}
                  d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9"
                />
              </svg>
            </button>

            {/* User Profile */}
            <div className="flex items-center relative">
              {isSmallScreen ? (
                <IconButton
                  onClick={() => setShowProfileCard(!showProfileCard)}
                  size="small"
                  aria-label="user profile"
                >
                  <Avatar 
                    sx={{ 
                      bgcolor: theme.palette.primary.main,
                      width: 32,
                      height: 32 
                    }}
                  >
                    {userInfo ? 
                      `${userInfo.firstName[0]}${userInfo.lastName[0]}` : 
                      'U'
                    }
                  </Avatar>
                </IconButton>
              ) : (
                <button
                  onClick={() => setShowProfileCard(!showProfileCard)}
                  className="ml-3 focus:outline-none"
                >
                  {loading ? (
                    <div className="text-sm text-gray-500">{t('common:loading')}</div>
                  ) : error ? (
                    <div className="text-sm text-red-500">{error}</div>
                  ) : (
                    <div className="text-sm font-medium" style={{ color: theme.palette.text.primary }}>
                      {userInfo?.firstName} {userInfo?.lastName}
                    </div>
                  )}
                </button>
              )}
              {showProfileCard && userInfo && (
                <UserProfileCard
                  userInfo={userInfo}
                  onClose={() => setShowProfileCard(false)}
                />
              )}
            </div>
          </div>
        </div>
      </div>
    </header>
  );
};

export default Header;
