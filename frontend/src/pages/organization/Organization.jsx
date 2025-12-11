import React, { useState } from 'react';
import { Box, Tab, Tabs } from '@mui/material';
import Company from './Company';
import Users from './Users';
import Roles from './Roles';
import { useTranslation } from 'react-i18next';

/**
 * Main organization page with tab navigation for different organization entities
 */
const Organization = () => {
  const { t } = useTranslation();
  const [tabValue, setTabValue] = useState(0);

  const handleTabChange = (event, newValue) => {
    setTabValue(newValue);
  };

  return (
    <Box sx={{ width: '100%' }}>
      <Box sx={{ borderBottom: 1, borderColor: 'divider' }}>
        <Tabs value={tabValue} onChange={handleTabChange}>
          <Tab label={t('organization:tabs.companies')} />
          <Tab label={t('organization:tabs.users')} />
          <Tab label={t('organization:tabs.roles')} />
        </Tabs>
      </Box>
      <Box sx={{ pt: 3 }}>
        {tabValue === 0 && <Company />}
        {tabValue === 1 && <Users />}
        {tabValue === 2 && <Roles />}
      </Box>
    </Box>
  );
};

export default Organization;
