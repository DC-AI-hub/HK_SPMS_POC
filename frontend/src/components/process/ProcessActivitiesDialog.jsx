import React, { useState, useEffect } from 'react';
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Button,
  Box,
  Typography,
  CircularProgress,
  Grid
} from '@mui/material';
import {
  Timeline,
  TimelineItem,
  TimelineSeparator,
  TimelineDot,
  TimelineConnector,
  TimelineContent,
  TimelineOppositeContent
} from '@mui/lab';
import { useTranslation } from 'react-i18next';
import processService from '../../api/process/processService';
import processInstanceService from '../../api/process/processInstanceService';
import StatusChip from '../common/StatusChip';

/**
 * Dialog component to display process activities and metadata in a split view
 * 
 * @param {Object} props Component properties
 * @param {boolean} props.open Whether dialog is open
 * @param {Function} props.onClose Callback when dialog closes
 * @param {Object} props.processInstance processInstance
 * @param {Array<Object>} props.activities List of activity objects
 * @param {boolean} props.loading Whether activities are loading
 * @param {string|null} props.error Error message if any
 */
const ProcessActivitiesDialog = ({ 
  open, 
  onClose, 
  processInstance,
  activities, 
  loading, 
  error 
}) => {
  const { t } = useTranslation();
  const [definition, setDefinition] = useState(null);
  const [instance, setInstance] = useState(null);
  const [metaLoading, setMetaLoading] = useState(false);
  const [metaError, setMetaError] = useState(null);

  // Fetch metadata when dialog opens
  useEffect(() => {
    if (!open) return;

    const fetchMetadata = async () => {
      setMetaLoading(true);
      setMetaError(null);
      
      try {
        var defData = await processService.getProcessVersionByDeploymentId(processInstance.deploymentId);
        setDefinition(defData);
        setInstance(processInstance);
      } catch (err) {
        setMetaError(t('process:errors.fetchMetadataFailed'));
      } finally {
        setMetaLoading(false);
      }
    };

    fetchMetadata();
  }, [open,processInstance, t]);

  // Calculate activity duration
  const calculateDuration = (activity) => {
    if (!activity.endTime) return t('process:activity.ongoing');
    const diff = new Date(activity.endTime) - new Date(activity.startTime);
    const mins = Math.floor(diff / 60000);
    return `${mins} ${t('common:minutes')}`;
  };

  return (
    <Dialog 
      open={open} 
      onClose={onClose}
      fullWidth
      maxWidth="md"
      sx={{ '& .MuiDialog-paper': { minHeight: '60vh' } }}
    >
      <DialogTitle>{t('process:instances.dialog.title')}</DialogTitle> 
      <DialogContent dividers>
        <Grid container spacing={3}>
          {/* Left Panel - Metadata */}
          <Grid item xs={12} md={5}>
            {metaLoading && (
              <Box sx={{ display: 'flex', justifyContent: 'center', p: 4 }}>
                <CircularProgress />
              </Box>
            )}
            
            {metaError && (
              <Typography color="error">{metaError}</Typography>
            )}
            
            {/* Definition Metadata */}
            {!metaLoading && !metaError && definition && (
              <Box sx={{ mb: 2, p: 2, border: '1px solid', borderColor: 'divider', borderRadius: 1 }}>
                <Typography variant="h6" component="div" gutterBottom>
                  {t('process:definition.title')}
                </Typography>
                <Typography variant="body2">
                  <strong>{t('process:definition.name')}:</strong> {definition.name}
                </Typography>
                <Typography variant="body2">
                  <strong>{t('process:definition.key')}:</strong> {definition.key}
                </Typography>
                <Typography variant="body2">
                  <strong>{t('process:definition.version')}:</strong> {definition.version}
                </Typography>
              </Box>
            )}
            
            {/* Instance Data */}
            {!metaLoading && !metaError && instance && (
              <Box sx={{ p: 2, border: '1px solid', borderColor: 'divider', borderRadius: 1 }}>
                <Typography variant="h6" component="div" gutterBottom>
                  {t('process:instance.title')}
                </Typography>
                <Typography variant="body2">
                  <strong>{t('process:instance.id')}:</strong> {instance.instanceId}
                </Typography>
                <Typography variant="body2">
                  <strong>{t('process:instance.status')}:</strong> <StatusChip status={instance.status??"ACTIVE"} value={instance.status??"ACTIVE"} />
                </Typography>
                <Typography variant="body2">
                  <strong>{t('process:instance.startTime')}:</strong> {new Date(instance.startTime).toLocaleString()}
                </Typography>
              </Box>
            )}
          </Grid>
          
          {/* Right Panel - Activities */}
          <Grid item xs={12} md={7}>
            {loading && (
              <Box sx={{ display: 'flex', justifyContent: 'center', p: 4 }}>
                <CircularProgress />
              </Box>
            )}
            
            {error && (
              <Typography color="error">{error}</Typography>
            )}
            
            {!loading && !error && activities.length === 0 && (
              <Typography>{t('process:instances.dialog.noActivities')}</Typography>
            )}
            
            {!loading && !error && activities.length > 0 && (
              <Box sx={{ maxHeight: 400, overflow: 'auto', pr: 1 }}>
                <Timeline position="right">
                  {activities.map((activity, index) => (
                    <TimelineItem key={activity.id}>
                      {index % 2 === 0 ? (
                        <>
                          <TimelineOppositeContent color="textSecondary" sx={{ flex: 0.3 }}>
                            {new Date(activity.startTime).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
                            {activity.endTime && (
                              <div>- {new Date(activity.endTime).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}</div>
                            )}
                          </TimelineOppositeContent>
                          <TimelineSeparator>
                            <TimelineDot color={activity.endTime ? "success" : "primary"} />
                            <TimelineConnector />
                          </TimelineSeparator>
                          <TimelineContent>
                            <Typography variant="subtitle2">{activity.activityName}</Typography>
                            <Typography variant="body2" color="textSecondary">
                              {t('process:activity.duration')}: {calculateDuration(activity)}
                            </Typography>
                            <Typography variant="caption" display="block">
                              {activity.activityType}
                            </Typography>
                          </TimelineContent>
                        </>
                      ) : (
                        <>
                          <TimelineContent>
                            <Typography variant="subtitle2">{activity.activityName}</Typography>
                            <Typography variant="body2" color="textSecondary">
                              {t('process:activity.duration')}: {calculateDuration(activity)}
                            </Typography>
                            <Typography variant="caption" display="block">
                              {activity.activityType}
                            </Typography>
                          </TimelineContent>
                          <TimelineSeparator>
                            <TimelineDot color={activity.endTime ? "success" : "primary"} />
                            <TimelineConnector />
                          </TimelineSeparator>
                          <TimelineOppositeContent color="textSecondary" sx={{ flex: 0.3 }}>
                            {new Date(activity.startTime).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
                            {activity.endTime && (
                              <div>- {new Date(activity.endTime).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}</div>
                            )}
                          </TimelineOppositeContent>
                        </>
                      )}
                    </TimelineItem>
                  ))}
                </Timeline>
              </Box>
            )}
          </Grid>
        </Grid>
      </DialogContent>
      
      <DialogActions>
        <Button onClick={onClose}>
          {t('common:close')}
        </Button>
      </DialogActions>
    </Dialog>
  );
};

export default ProcessActivitiesDialog;
