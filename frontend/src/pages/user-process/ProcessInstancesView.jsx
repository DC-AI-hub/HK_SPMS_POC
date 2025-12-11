import React, { useState, useEffect } from 'react';
import userProcessInstanceService from '../../api/process/userProcessInstanceService';
import { 
  Box, Typography, Table, TableBody, 
  TableCell, TableContainer, TableHead, 
  TableRow, Paper, Chip, CircularProgress, Alert 
} from '@mui/material';
import { useTranslation } from 'react-i18next';
import ProcessInstanceDetailsDialog from '../../components/process/ProcessInstanceDetailsDialog';


/**
 * Process Instances View - Displays process instances in a table
 */
const ProcessInstancesView = () => {
  const { t } = useTranslation();
  const [selectedInstance, setSelectedInstance] = useState(null);
  const [isDialogOpen, setIsDialogOpen] = useState(false);
  const [processInstances, setProcessInstances] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [pagination, setPagination] = useState({ page: 0, size: 10 });
  
  // Fetch active process instances
  useEffect(() => {
    const fetchData = async () => {
      setLoading(true);
      try {
        const response = await userProcessInstanceService.getActiveInstances({
          page: pagination.page,
          size: pagination.size,
          sort: 'startTime,desc'
        });
        setProcessInstances(response.content);
      } catch (err) {
        setError(err.message || t('userProcess:userProcess.fetchError'));
      } finally {
        setLoading(false);
      }
    };

    fetchData();
    
    // Set up 30s refresh
    const intervalId = setInterval(fetchData, 30000);
    
    return () => clearInterval(intervalId);
  }, [pagination.page, pagination.size]);


  const handleRowClick = async (instance) => {
    setSelectedInstance(instance);
    setIsDialogOpen(true);
    setActivities([]); // Reset activities
  };

  const handleCloseDialog = () => {
    setIsDialogOpen(false);
    setSelectedInstance(null);
  };

  const formatDateTime = (dateString) => {
    if (!dateString) return t('userProcess:processInstances.notCompleted');
    const date = new Date(dateString);
    return date.toLocaleString();
  };

  const getStatusColor = (status) => {
    switch (status) {
      case 'Running': return 'primary';
      case 'Completed': return 'success';
      case 'Canceled': return 'error';
      default: return 'default';
    }
  };

  // Render loading state
  if (loading) {
    return (
      <Box display="flex" justifyContent="center" mt={4}>
        <CircularProgress />
      </Box>
    );
  }

  // Render error state
  if (error) {
    return (
      <Box mt={2}>
        <Alert severity="error">
          {error}
          <Button onClick={() => setPagination({...pagination})} sx={{ ml: 2 }}>
            {t('userProcess:common.retry')}
          </Button>
        </Alert>
      </Box>
    );
  }

  // Render empty state
  if (!loading && processInstances.length === 0) {
    return (
      <Box mt={2}>
        <Alert severity="info">
          {t('userProcess:userProcess.noActiveInstances')}
        </Alert>
      </Box>
    );
  }

  return (
    <div>
      <Typography variant="h5" gutterBottom mb={3}>
        {t('userProcess:processInstances.title')}
      </Typography>
      
      <TableContainer component={Paper}>
        <Table sx={{ minWidth: 650 }} aria-label="process instances table">
          <TableHead>
            <TableRow>
              <TableCell>{t('userProcess:processInstances.table.instanceId')}</TableCell>
              <TableCell>{t('userProcess:processInstances.table.businessKey')}</TableCell>
              <TableCell>{t('userProcess:processInstances.table.processName')}</TableCell>
              <TableCell>{t('userProcess:processInstances.table.startTime')}</TableCell>
              <TableCell>{t('userProcess:processInstances.table.status')}</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {processInstances.map((instance) => (
              <TableRow 
                key={instance.instanceId}
                hover
                onClick={() => handleRowClick(instance)}
                sx={{ cursor: 'pointer' }}
              >
                <TableCell>{instance.instanceId}</TableCell>
                <TableCell>{instance.businessKey}</TableCell>
                <TableCell>
                  {instance.definitionId}
                  {/* TODO: Replace with actual process name when API provides it */}
                </TableCell>
                <TableCell>{formatDateTime(instance.startTime)}</TableCell>
                <TableCell>
                  <Chip 
                    label={instance.status} 
                    color={getStatusColor(instance.status)}
                    size="small"
                  />
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>

      <ProcessInstanceDetailsDialog 
        open={isDialogOpen}
        onClose={handleCloseDialog}
        instance={selectedInstance}
        t={t}
      />
    </div>
  );
};

export default ProcessInstancesView;
