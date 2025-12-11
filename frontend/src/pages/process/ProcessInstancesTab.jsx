import React, { useState, useEffect } from 'react';
import { 
  Box,
  CircularProgress,
  Typography,
  IconButton
} from '@mui/material';
import { DataGrid } from '@mui/x-data-grid';
import VisibilityIcon from '@mui/icons-material/Visibility';
import ProcessActivitiesDialog from '../../components/process/ProcessActivitiesDialog';

import { useTheme } from '@mui/material/styles';
import { useTranslation } from 'react-i18next';
import processInstanceService from '../../api/process/processInstanceService';

/**
 * Component for displaying process instances
 * @returns {JSX.Element} Process instances content
 */
const ProcessInstancesTab = () => {
  const { t } = useTranslation();
  const theme = useTheme();
  const [instances, setInstances] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [dialogOpen, setDialogOpen] = useState(false);
  const [currentInstance, setCurrentInstance] = useState(null);
  const [activities, setActivities] = useState([]);
  const [activitiesLoading, setActivitiesLoading] = useState(false);
  const [activitiesError, setActivitiesError] = useState(null);
  
  const handleOpenDialog = async (instance) => {
    setCurrentInstance(instance)
    setDialogOpen(true);
    setActivitiesLoading(true);
    setActivitiesError(null);
    
    try {
      const data = await processInstanceService.getProcessActivities(currentInstance.instanceId, {page: 0, size: 50});
      setActivities(data.content);
    } catch (err) {
      setActivitiesError(t('process:errors.fetchActivitiesFailed'));
    } finally {
      setActivitiesLoading(false);
    }
  };

  useEffect(() => {
    const fetchInstances = async () => {
      try {
        const pageable = { page: 0, size: 10, sort: 'startTime,desc' };
        const data = await processInstanceService.getAllProcessInstances(pageable);
        setInstances(data || []);
        setLoading(false);
      } catch (err) {
        setError(t('process:errors.fetchInstancesFailed'));
        setLoading(false);
      }
    };

    fetchInstances();
  }, [t]);

  const columns = [
    { field: 'instanceId', headerName: t('process:instances.columns.id'), width: 200 },
    { field: 'definitionId', headerName: t('process:instances.columns.definitionId'), width: 200 },
    { field: 'status', headerName: t('process:instances.columns.status'), width: 150 },
    { field: 'startTime', headerName: t('process:instances.columns.startTime'), width: 200 },
    { field: 'businessKey', headerName: t('process:instances.columns.businessKey'), width: 150 },
    {
      field: 'actions',
      headerName: t('process:instances.columns.actions'),
      width: 100,
      renderCell: (params) => (
        <IconButton 
          onClick={() => handleOpenDialog(params.row)}
          size="small"
        >
          <VisibilityIcon />
        </IconButton>
      )
    }
  ];

  if (loading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', mt: 4 }}>
        <CircularProgress />
      </Box>
    );
  }

  if (error) {
    return (
      <Box sx={{ p: 2 }}>
        <Typography color="error">{error}</Typography>
      </Box>
    );
  }

  return (
    <React.Fragment>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
        <Typography variant="h6">
          {t('process:instances.title', 'Process Instances')}
        </Typography>
      </Box>
      <Box sx={{ height: 600, width: '100%' }}>
        <DataGrid
          rows={instances}
          columns={columns}
          pageSize={10}
          rowsPerPageOptions={[10]}
          disableSelectionOnClick
          getRowId={(row) => row.instanceId}
        />
      </Box>
      
      <ProcessActivitiesDialog 
        open={dialogOpen}
        onClose={() => setDialogOpen(false)}
        processInstance={currentInstance}
        activities={activities}
        loading={activitiesLoading}
        error={activitiesError}
      />
    </React.Fragment>
  );
};

export default ProcessInstancesTab;
