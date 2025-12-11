import React from 'react';
import { Tabs, Tab, BottomNavigation, BottomNavigationAction } from '@mui/material';
import { useTranslation } from 'react-i18next';
import { useScreenSize } from '../../contexts/ScreenSizeContext';

/**
 * Generic responsive navigation component that displays tabs on desktop and bottom navigation on mobile
 * Can be used across different modules with customizable tab configurations
 * 
 * @param {Object} props - Component props
 * @param {Array} props.tabs - Array of tab objects with configuration
 * @param {number} props.currentTab - Current active tab index
 * @param {function} props.onTabChange - Callback function when tab changes
 * @param {string} props.mobilePosition - Position of mobile navigation ('bottom' or 'top')
 * @returns {JSX.Element} Responsive tabs component
 */
const ResponsiveTabs = ({ 
  tabs, 
  currentTab, 
  onTabChange, 
  mobilePosition = 'bottom' 
}) => {
  const { t } = useTranslation();
  const { isMobileScreen } = useScreenSize();

  if (isMobileScreen) {
    // Mobile: Bottom Navigation with icons
    return (
      <BottomNavigation
        value={currentTab}
        onChange={(event, newValue) => onTabChange(event, newValue)}
        showLabels={false}
        sx={{
          width: '100%',
          position: 'fixed',
          left: 0,
          [mobilePosition]: 0,
          zIndex: 1000,
          backgroundColor: 'background.paper',
          borderTop: mobilePosition === 'bottom' ? 1 : 0,
          borderBottom: mobilePosition === 'top' ? 1 : 0,
          borderColor: 'divider'
        }}
      >
        {tabs.map((tab, index) => (
          <BottomNavigationAction
            key={index}
            icon={tab.icon}
            aria-label={typeof tab.label === 'string' ? tab.label : t(tab.label)}
          />
        ))}
      </BottomNavigation>
    );
  }

  // Desktop: Tabs with labels
  return (
    <Tabs
      value={currentTab}
      onChange={onTabChange}
      aria-label="responsive tabs"
      sx={{ 
        mb: 3,
        '& .MuiTabs-indicator': {
          backgroundColor: 'primary.main'
        }
      }}
    >
      {tabs.map((tab, index) => (
        <Tab
          key={index}
          label={typeof tab.label === 'string' ? tab.label : t(tab.label)}
        />
      ))}
    </Tabs>
  );
};

export default ResponsiveTabs;
