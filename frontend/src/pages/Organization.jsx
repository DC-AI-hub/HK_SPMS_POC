import React, { useState } from 'react';
import { Box, useTheme } from '@mui/material';
import { useTranslation } from 'react-i18next';
import { useScreenSize } from '../contexts/ScreenSizeContext';
import Company from './organization/Company';
import Division from './organization/Division';
import Department from './organization/Department';
import OrganizationChart from './organization/OrganizationChart';
import Users from './organization/Users';
import ResponsiveTabs from '../components/common/ResponsiveTabs';
// Import MUI icons for tabs
import BusinessIcon from '@mui/icons-material/Business';
import AccountTreeIcon from '@mui/icons-material/AccountTree';
import GroupsIcon from '@mui/icons-material/Groups';
import AccountTreeOutlinedIcon from '@mui/icons-material/AccountTreeOutlined';
import PeopleIcon from '@mui/icons-material/People';

/**
 * Organization page with tabbed interface for managing different organization-related features
 */
const Organization = () => {
  const { t } = useTranslation();
  const theme = useTheme();
  const { isMobileScreen } = useScreenSize();
  const [currentTab, setCurrentTab] = useState(0);

  const handleTabChange = (event, newValue) => {
    setCurrentTab(newValue);
  };

  // Tabs configuration for responsive navigation
  const tabs = [
    { icon: <BusinessIcon />, label: t('organization:tabs.companyManagement') },
    { icon: <AccountTreeIcon />, label:  t('organization:tabs.divisions' )},
    { icon: <GroupsIcon />, label: t('organization:department.title') },
    { icon: <AccountTreeOutlinedIcon />, label: t('organization:tabs.organizationChart') },
    { icon: <PeopleIcon />, label: t('organization:tabs.userManagement') }
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
        {currentTab === 0 && <Company />}
        {currentTab === 1 && <Division />}
        {currentTab === 2 && <Department />}
        {currentTab === 3 && <OrganizationChart />}
        {currentTab === 4 && <Users />}
      </Box>
    </Box>
  );
};

export default Organization;
