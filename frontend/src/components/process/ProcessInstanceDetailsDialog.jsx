import React, { useState, useEffect } from 'react';
import { 
  Dialog, DialogTitle, DialogContent, 
  DialogActions, Button, CircularProgress, Box, Typography 
} from '@mui/material';
import processInstanceService from '../../api/process/processInstanceService';


/**
 * ProcessInstanceDetailsDialog - Displays detailed information about a process instance
 * 
 * @param {Object} props - Component props
 * @param {boolean} props.open - Controls the visibility of the dialog
 * @param {Function} props.onClose - Callback when the dialog is closed
 * @param {Object} props.instance - The process instance object to display
 * @param {Function} props.t - Translation function
 */
const ProcessInstanceDetailsDialog = ({ open, onClose, instance, t }) => {
  const [activities, setActivities] = useState([]);
  const [activitiesLoading, setActivitiesLoading] = useState(false);
  
  // Fetch activities when instance changes
  useEffect(() => {
    if (!instance) return;
    
    const fetchActivities = async () => {
      setActivitiesLoading(true);
      try {
        const response = await processInstanceService.getProcessActivities(
          instance.instanceId,
          { page: 0, size: 100 }
        );
        setActivities(response.content);
      } catch (err) {
        console.error('Failed to fetch activities:', err);
      } finally {
        setActivitiesLoading(false);
      }
    };

    fetchActivities();
  }, [instance]);

  const formatDateTime = (dateString) => {
    if (!dateString) return t('process:processInstances.notCompleted');
    const date = new Date(dateString);
    return date.toLocaleString();
  };

  return (
    <Dialog open={open} onClose={onClose} maxWidth="md" fullWidth>
      <DialogTitle>
        {t('process:processInstances.dialog.title', { id: instance?.id })}
      </DialogTitle>
      <DialogContent>
        {instance && (
          <>
            <Typography variant="h6" gutterBottom>
              {t('process:processInstances.dialog.activitiesTitle')}
            </Typography>
            
            {activitiesLoading ? (
              <Box display="flex" justifyContent="center" my={2}>
                <CircularProgress size={24} />
              </Box>
            ) : activities.length > 0 ? (
              <Box mb={3}>
                {activities.map((activity, index) => (
                  <Box key={index} mb={1}>
                    <Typography>
                      <strong>{activity.activityName}:</strong> 
                      {activity.endTime 
                        ? ` ${t('process:processInstances.dialog.completedAt')} ${formatDateTime(activity.endTime)}`
                        : ` ${t('process:processInstances.dialog.inProgress')}`}
                    </Typography>
                  </Box>
                ))}
              </Box>
            ) : (
              <Typography variant="body2" color="textSecondary">
                {t('process:processInstances.dialog.noActivities')}
              </Typography>
            )}
            
            {instance.activeTasks && instance.activeTasks.length > 0 && (
              <>
                <Typography variant="h6" gutterBottom>
                  {t('process:processInstances.dialog.activeTasksTitle')}
                </Typography>
                <Typography>
                  {instance.activeTasks.map(task => task.name).join(', ')}
                </Typography>
              </>
            )}
          </>
        )}
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose}>
          {t('process:processInstances.dialog.closeButton')}
        </Button>
      </DialogActions>
    </Dialog>
  );
};

export default ProcessInstanceDetailsDialog;
