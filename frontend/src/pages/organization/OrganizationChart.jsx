import React, { useState, useEffect } from 'react';
import { Box, Select, MenuItem, FormControl, Switch, Typography } from '@mui/material';
import OrganizationChartVisualization from '../../components/OrganizationChartVisualization';
import organizationService from '../../api/idm/organizationService';
import { useTranslation } from 'react-i18next';
import { useScreenSize } from '../../contexts/ScreenSizeContext';

const OrganizationChart = () => {
  const { t } = useTranslation();
  const { isMobileScreen: isMobile } = useScreenSize();
  const [companies, setCompanies] = useState([]);
  const [selectedCompany, setSelectedCompany] = useState('');
  const [chartMode, setChartMode] = useState('REALISTIC');
  const [chartData, setChartData] = useState(null);

  useEffect(() => {
    fetchCompanies();
  }, []);

  const fetchCompanies = async () => {
    try {
      const response = await organizationService.getCompanies({ type: 'GROUP' });
      setCompanies(response.content);
      if (response.content.length > 0) {
        setSelectedCompany(response.content[0].id);
      }
    } catch (error) {
      console.error('Error fetching companies:', error);
    }
  };

  const handleCompanyChange = (event) => {
    setSelectedCompany(event.target.value);
  };

  const handleModeChange = (event) => {
    setChartMode(event.target.checked ? 'FUNCTIONAL' : 'REALISTIC');
  };

  useEffect(() => {
    if (selectedCompany) {
      fetchOrganizationChart();
    }
  }, [selectedCompany, chartMode]);

  const fetchOrganizationChart = async () => {
    try {
      const response = await organizationService.getOrganizationChart(selectedCompany, chartMode);
      setChartData(response.data);
    } catch (error) {
      console.error('Error fetching organization chart:', error);
    }
  };

  return (
    <Box>
      <Box sx={{ 
        display: 'flex', 
        flexDirection: isMobile ? 'column' : 'row',
        gap: 2, 
        mb: 3, 
        p: 2,
        alignItems: isMobile ? 'stretch' : 'center'
      }}>
        <FormControl size="small" sx={{ minWidth: isMobile ? '100%' : 200 }}>
          <Select
            value={selectedCompany}
            onChange={handleCompanyChange}
            displayEmpty
          >
            {companies.map((company) => (
              <MenuItem key={company.id} value={company.id}>
                {company.name}
              </MenuItem>
            ))}
          </Select>
        </FormControl>
        
        <Box sx={{ 
          display: 'flex', 
          alignItems: 'center',
          justifyContent: isMobile ? 'space-between' : 'flex-start',
          width: isMobile ? '100%' : 'auto'
        }}>
          <Typography variant="body2" sx={{ mr: 1, color: 'text.primary' }}>
            {t('organization:chart.realistic')}
          </Typography>
          <Switch
            checked={chartMode === 'FUNCTIONAL'}
            onChange={handleModeChange}
            color="primary"
          />
          <Typography variant="body2" sx={{ ml: 1, color: 'text.primary' }}>
            {t('organization:chart.functional')}
          </Typography>
        </Box>
      </Box>

      {/* Chart visualization area */}
      <Box sx={{ 
        height: isMobile ? '400px' : '600px', 
        border: 1, 
        borderColor: 'divider',
        borderRadius: 1,
        bgcolor: 'background.paper'
      }}>
        {chartData ? (
          <OrganizationChartVisualization chartData={chartData} mode={chartMode} isMobile={isMobile} />
        ) : (
          <Typography variant="h6" align="center" sx={{ mt: 4, color: 'text.primary' }}>
            {t('organization:chart.loading')}
          </Typography>
        )}
      </Box>
    </Box>
  );
};

export default OrganizationChart;
