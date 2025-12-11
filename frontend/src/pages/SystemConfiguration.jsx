import React, { useState } from 'react';
import { useTranslation } from 'react-i18next';
import {
  Box,
  Tabs,
  Tab,
  Typography,
  Paper,
  Container,
  Alert,
  CircularProgress,
  useTheme
} from '@mui/material';
import { useConfig } from '../contexts/ConfigContext';
import SystemConfigPanel from '../components/system/SystemConfigPanel';
import MenuConfigPanel from '../components/system/MenuConfigPanel';
import ThemeConfigPanel from '../components/system/ThemeConfigPanel';

function TabPanel({ children, value, index, ...other }) {
  return (
    <div
      role="tabpanel"
      hidden={value !== index}
      id={`system-config-tabpanel-${index}`}
      aria-labelledby={`system-config-tab-${index}`}
      {...other}
    >
      {value === index && (
        <Box sx={{ py: 3 }}>
          {children}
        </Box>
      )}
    </div>
  );
}

function a11yProps(index) {
  return {
    id: `system-config-tab-${index}`,
    'aria-controls': `system-config-tabpanel-${index}`,
  };
}

export default function SystemConfiguration() {
  const [activeTab, setActiveTab] = useState(0);
  const { loading, error } = useConfig();
  const theme = useTheme();
  const { t } = useTranslation();



  const handleTabChange = (event, newValue) => {
    setActiveTab(newValue);
  };

  if (loading) {
    return (
      <Container maxWidth="lg" sx={{ py: 4 }}>
        <Box display="flex" justifyContent="center" alignItems="center" minHeight="400px"  style={{ color: theme.palette.text.primary, fontWeight: 'bold' }}>
          <CircularProgress />
          <Typography variant="h6" sx={{ ml: 2 }}>
            {t('system:settings.loading')}
          </Typography>
        </Box>
      </Container>
    );
  }

  if (error) {
    return (
      <Container maxWidth="lg" sx={{ py: 4 }}>
        <Alert severity="error" sx={{ mb: 3 }}  style={{ color: theme.palette.text.primary, fontWeight: 'bold' }}>
          {t('system:errors.loadingError')}: {error}
        </Alert>
      </Container>
    );
  }

  return (
    <Container maxWidth="lg" sx={{ py: 4 }} >
      <Typography variant="h4" component="h1" gutterBottom  style={{ color: theme.palette.text.primary, fontWeight: 'bold' }}>
        {t('system:pageTitles.systemConfiguration')}
      </Typography>
      <Typography variant="body1" color="text.secondary" sx={{ mb: 4 }}>
        {t('system:descriptions.systemConfig')}
      </Typography>

      <Paper sx={{ width: '100%' }}>
        <Box sx={{ borderBottom: 1, borderColor: 'divider' }}>
          <Tabs
            value={activeTab}
            onChange={handleTabChange}
            aria-label="system configuration tabs"
            variant="fullWidth"
          >
            <Tab label={t('system:settings.title')} {...a11yProps(0)} />
            <Tab label={t('system:menu.management')} {...a11yProps(1)} />
            <Tab label={t('system:theme.management')} {...a11yProps(2)} />
          </Tabs>
        </Box>

        <TabPanel value={activeTab} index={0}>
          <SystemConfigPanel />
        </TabPanel>

        <TabPanel value={activeTab} index={1}>
          <MenuConfigPanel />
        </TabPanel>

        <TabPanel value={activeTab} index={2}>
          <ThemeConfigPanel />
        </TabPanel>
      </Paper>
    </Container>
  );
}
