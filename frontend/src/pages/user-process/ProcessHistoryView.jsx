import React, { useState, useEffect } from 'react';
import { 
  Box, 
  Table, 
  TableBody, 
  TableCell, 
  TableContainer, 
  TableHead, 
  TableRow, 
  Paper,
  CircularProgress,
  Typography,
  Switch,
  FormControlLabel
} from '@mui/material';
import { useTranslation } from 'react-i18next';
import processHistoryInstanceService from '../../api/process/processHistoryInstanceService';

/**
 * Component to display process history for the current user
 */
const ProcessHistoryView = () => {
  const { t } = useTranslation();
  const [historyData, setHistoryData] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [pagination, setPagination] = useState({
    page: 0,
    size: 10,
    totalElements: 0
  });
  const [isHandledByMeView, setIsHandledByMeView] = useState(true);

  // Fetch process history based on toggle view and pagination
  useEffect(() => {
    const fetchHistory = async () => {
      try {
        setLoading(true);
        const apiMethod = isHandledByMeView 
          ? processHistoryInstanceService.getHistoryHandledByCurrentUser
          : processHistoryInstanceService.getHistoryStartedByCurrentUser;
          
        const response = await apiMethod(
          { page: pagination.page, size: pagination.size }
        );
        
        setHistoryData(response.content);
        setPagination(prev => ({
          ...prev,
          totalElements: response.totalElements
        }));
      } catch (err) {
        setError(err.message || t('common:errors.generic'));
      } finally {
        setLoading(false);
      }
    };

    fetchHistory();
  }, [pagination.page, pagination.size, t, isHandledByMeView]);
  
  // Handle view toggle change
  const handleViewToggle = (event) => {
    setPagination(prev => ({ ...prev, page: 0 }));
    setIsHandledByMeView(event.target.checked);
  };

  const handlePageChange = (event, newPage) => {
    setPagination(prev => ({ ...prev, page: newPage }));
  };

  const handleRowsPerPageChange = (event) => {
    setPagination(prev => ({
      ...prev,
      page: 0,
      size: parseInt(event.target.value, 10)
    }));
  };

  if (loading) {
    return (
      <Box display="flex" justifyContent="center" mt={4}>
        <CircularProgress />
      </Box>
    );
  }

  if (error) {
    return (
      <Box display="flex" justifyContent="center" mt={4}>
        <Typography color="error">{error}</Typography>
      </Box>
    );
  }

  return (
    <Box mt={2}>
      <Box display="flex" justifyContent="flex-end" mb={2}>
        <FormControlLabel
          control={
            <Switch 
              checked={isHandledByMeView}
              onChange={handleViewToggle}
              color="primary"
            />
          }
          label={isHandledByMeView 
            ? t('userProcess:switch.handledByMe') 
            : t('userProcess:switch.startedByMe')}
        />
      </Box>
      <TableContainer component={Paper}>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>{t('userProcess:processInstances.table.instanceId')}</TableCell>
              <TableCell>{t('userProcess:processInstances.table.startTime')}</TableCell>
              <TableCell>{t('userProcess:processInstances.table.endTime')}</TableCell>
              <TableCell>{t('userProcess:processInstances.table.businessKey')}</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {historyData.map((item) => (
              <TableRow key={item.processInstanceId}>
                <TableCell>{item.processInstanceId}</TableCell>
                <TableCell>{item.startTime}</TableCell>
                <TableCell>{item.endTime || '-'}</TableCell>
                <TableCell>{item.businessKey || '-'}</TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>
      
      {/* Pagination would be implemented here in next iteration */}
    </Box>
  );
};

export default ProcessHistoryView;
