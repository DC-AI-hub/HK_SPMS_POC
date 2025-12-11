import React, { useState } from 'react';
import { 
  Box,
  useTheme
} from '@mui/material';
import { useTranslation } from 'react-i18next';
import { useScreenSize } from '../contexts/ScreenSizeContext';
import FormsTab from './process/FormsTab';
import ProcessTab from './process/ProcessTab';
import ProcessInstancesTab from './process/ProcessInstancesTab';
import HolidaySettingTab from './process/HolidaySettingTab';
import ResponsiveTabs from '../components/common/ResponsiveTabs';
// Import MUI icons for tabs
import SettingsIcon from '@mui/icons-material/Settings';
import DescriptionIcon from '@mui/icons-material/Description';
import HistoryIcon from '@mui/icons-material/History';
import EventIcon from '@mui/icons-material/Event';

/**
 * Process management page component
 * @returns {JSX.Element} Process content
 */
const Process = () => {
  const { t } = useTranslation();
  const theme = useTheme();
  const { isMobileScreen } = useScreenSize();
  const [currentTab, setCurrentTab] = useState(0);

  const handleTabChange = (event, newValue) => {
    setCurrentTab(newValue);
  };

  // Tabs configuration for responsive navigation
  const tabs = [
    { icon: <SettingsIcon />, label: t('process:tabs.management') },
    { icon: <DescriptionIcon />, label: t('process:tabs.forms') },
    { icon: <HistoryIcon />, label: t('process:tabs.instances') },
    { icon: <EventIcon />, label: t('process:tabs.holidaySetting') }
  ];

  return (
    <Box sx={{ 
      width: '100%',
      p: theme.spacing(3),
      backgroundColor: theme.palette.background.default
    }}>
      <ResponsiveTabs 
        tabs={tabs} 
        currentTab={currentTab} 
        onTabChange={handleTabChange} 
      />

      <Box sx={{
        backgroundColor: theme.palette.background.paper,
        borderRadius: theme.shape.borderRadius,
        pb: isMobileScreen ? theme.spacing(10) : theme.spacing(3) // Add extra padding at bottom for mobile to avoid bottom nav overlap
      }}>
        {currentTab === 0 && <ProcessTab/>}
        {currentTab === 1 && <FormsTab />}
        {currentTab === 2 && <ProcessInstancesTab />}
        {currentTab === 3 && <HolidaySettingTab />}
      </Box>
    </Box>
  );
};

export default Process;
