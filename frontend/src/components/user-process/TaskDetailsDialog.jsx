import React, { useState, useEffect, useMemo } from 'react';
import {
  Dialog, DialogTitle, DialogContent,
  DialogActions, Button, Typography,
  Grid, Box, CircularProgress
} from '@mui/material';
import { toast } from 'react-toastify';
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
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import FormRuntimeSwitch from '../form/FormRuntimeSwitch';
import StatusChip from '../common/StatusChip';
import processInstanceService from '../../api/process/processInstanceService';
import processService from '../../api/process/processService';

/**
 * Standalone Task Details Dialog Component
 * 
 * Displays detailed information about a task and provides completion functionality.
 * 
 * @param {Object} props - Component properties
 * @param {boolean} props.open - Controls dialog visibility
 * @param {Object} props.task - Selected task data
 * @param {Function} props.onClose - Close handler
 * @param {Function} props.onComplete - Task completion handler
 */
const TaskDetailsDialog = ({ open, task, onClose, onComplete }) => {
  const { t } = useTranslation(['userProcess', 'common']);
  const [formData, setFormData] = useState({});
  const [processInstance, setProcessInstance] = useState(null);
  const [activities, setActivities] = useState([]);
  const [metadataLoading, setMetadataLoading] = useState(false);
  const [activitiesLoading, setActivitiesLoading] = useState(false);
  const [metadataError, setMetadataError] = useState(null);
  const [activitiesError, setActivitiesError] = useState(null);
  const [processDef ,setProcessDef] = useState(null);
  const [formVersion , setFormVersion] = useState(null);

  // Fetch process instance when task changes OR when dialog opens
  useEffect(() => {
    if (!open || !task?.processInstanceId) {
      if (!open) {
        // Reset formData when dialog closes
        setFormData({});
      }
      setProcessInstance(null);
      return;
    }

    const fetchProcessInstance = async () => {
      setMetadataLoading(true);
      setMetadataError(null);
      try {
        console.log('=== TaskDetailsDialog: Loading Process Instance ===');
        console.log('Process Instance ID:', task.processInstanceId);
        
        const instanceData = await processInstanceService.getProcessInstance(task.processInstanceId);
        const processDef = await processService.getProcessVersionByDeploymentId(instanceData.deploymentId);
        var contextData = instanceData.contextValue;
        
        console.log('=== TaskDetailsDialog: Process Instance Data Loaded ===');
        console.log('Context Data:', contextData);
        console.log('Has FormData:', !!contextData?.formData);
        if (contextData?.formData) {
          console.log('FormData Keys:', Object.keys(contextData.formData));
          console.log('EmployeeInfo:', contextData.formData.employeeInfo);
          console.log('TimecardEntries:', contextData.formData.timecardEntries);
        }
        
        // 处理表单数据
        let newFormData = {};
        if (contextData?.formData) {
          if(contextData.rejectionReason){
            newFormData = {
              ...contextData.formData,
              submited: "false",
            };
          } else {
            newFormData = {
              ...contextData.formData,
            };
          }
        }
        
        console.log('=== TaskDetailsDialog: Setting FormData ===');
        console.log('New FormData:', newFormData);
        
        setFormData(newFormData);
        setProcessDef(processDef);
        setProcessInstance(instanceData);
        setFormVersion(processDef.formVersion);
      } catch (err) {
        console.error('Failed to fetch process instance:', err);
        setMetadataError(t('errors.fetchMetadataFailed'));
      } finally {
        setMetadataLoading(false);
      }
    };

    fetchProcessInstance();
  }, [open, task, t]);

  // Fetch activities when process instance is available
  useEffect(() => {
    if (!processInstance?.instanceId) {
      setActivities([]);
      return;
    }

    const fetchActivities = async () => {
      setActivitiesLoading(true);
      setActivitiesError(null);
      try {
        const activitiesData = await processInstanceService.getProcessActivities(
          processInstance.instanceId,
          { page: 0, size: 10, sort: 'startTime,desc' }
        );
        setActivities(activitiesData.content);
      } catch (err) {
        setActivitiesError(t('errors.fetchActivitiesFailed'));
      } finally {
        setActivitiesLoading(false);
      }
    };

    fetchActivities();
  }, [processInstance, t]);

  // Calculate activity duration
  const calculateDuration = (activity) => {
    if (!activity.endTime) return t('common:activity.ongoing');
    const diff = new Date(activity.endTime) - new Date(activity.startTime);
    const mins = Math.floor(diff / 60000);
    return `${mins} ${t('common:minutes')}`;
  };

  const handleFormSubmit = async (data) => {
    console.log('=== TaskDetailsDialog: Save Draft Button Clicked ===');
    console.log('Process Instance ID:', task?.processInstanceId);
    console.log('Task ID:', task?.taskId);
    console.log('Form Data to save:', data);
    console.log('Form Data Keys:', Object.keys(data || {}));
    console.log('EmployeeInfo:', data?.employeeInfo);
    console.log('TimecardEntries:', data?.timecardEntries);
    console.log('TimecardEntries Count:', data?.timecardEntries?.length || 0);
    
    if (!task?.processInstanceId || !task?.taskId) {
      console.warn('Missing processInstanceId or taskId, cannot save draft');
      toast.error('Cannot save: Missing process instance information');
      setFormData(data);
      return;
    }

    try {
      // Save draft to process engine without completing the task
      const draftData = {
        formData: data,
        submited: "false"
      };
      
      console.log('=== TaskDetailsDialog: Calling saveDraft API ===');
      console.log('Draft Data:', draftData);
      
      await processInstanceService.saveDraft(task.processInstanceId, task.taskId, draftData);
      
      console.log('=== TaskDetailsDialog: Draft Saved Successfully ===');
      toast.success('Draft saved successfully');
      
      // Update local state
      setFormData(data);
      
      // Also update processInstance state so it reflects the saved data
      if (processInstance) {
        const updatedContextValue = {
          ...processInstance.contextValue,
          formData: data
        };
        setProcessInstance({
          ...processInstance,
          contextValue: updatedContextValue
        });
        console.log('=== TaskDetailsDialog: Local State Updated ===');
      }
    } catch (error) {
      console.error('=== TaskDetailsDialog: Failed to Save Draft ===', error);
      toast.error('Failed to save draft');
      setFormData(data);
    }
  };

  const handleComplete = () => {
    onComplete({ ...task, formData });
  };

  // Memoize parsed schema to avoid repeated JSON parsing
  const parsedSchema = useMemo(() => {
    if (!formVersion?.schema) return null;
    
    try {
      const schema = JSON.parse(formVersion.schema);
      
      // Inject processInstanceId into props for data isolation
      if (schema.props) {
        schema.props.processInstanceId = task?.processInstanceId;
    } else {
        schema.props = { processInstanceId: task?.processInstanceId };
      }
      
      return schema;
    } catch (error) {
      console.error('Failed to parse form schema:', error);
      return null;
    }
  }, [formVersion?.schema, task?.processInstanceId]);

  return (
    <Dialog open={open} onClose={onClose} maxWidth="md" fullWidth fullScreen > 
      <DialogTitle>
        {t('userTasks.dialog.title', { title: task?.name })}
      </DialogTitle>
      <DialogContent>
        {task && (
          <div className="flex-row w-full h-full flex justify-between">
  
              {parsedSchema && (
                <FormRuntimeSwitch
                  key={`form-${task?.taskId}-${open}`}
                  schema={parsedSchema}
                  initialData={formData}
                  onSubmit={handleFormSubmit}
                />
              )}
  
            {/* Process Instance Section */}
            <div className='flex-col flex justify-between h-full'>

                {/* Metadata column */}
                <Grid item xs={12} md={5} >
                  {metadataLoading ? (
                    <Box sx={{ display: 'flex', justifyContent: 'center', p: 2 }}>
                      <CircularProgress />
                    </Box>
                  ) : metadataError ? (
                    <Typography color="error">{metadataError}</Typography>
                  ) : processInstance ? (
                    <Box sx={{ p: 2, border: 1, borderColor: 'divider', borderRadius: 1 }}>
                      <Typography variant="subtitle1" gutterBottom>
                        {t('userTasks.dialog.taskId')}: {task.taskId}
                      </Typography>
                      <Typography variant="subtitle1" gutterBottom>
                        {t('userTasks.dialog.assignee')}: {task.assignee || t('userTasks.unassigned')}
                      </Typography>
                      <Typography variant="subtitle1" gutterBottom>
                        {t('userTasks.dialog.instanceId')}: {processInstance.instanceId}
                      </Typography>
                      <Typography variant="subtitle1" gutterBottom>
                        {t('userTasks.dialog.status')}: <StatusChip status={task.status} value={task.status} />
                      </Typography>
                      <Typography variant="subtitle1" gutterBottom>
                        {t('userTasks.dialog.startTime')}: {new Date(processInstance.startTime).toLocaleString()}
                      </Typography>
                    </Box>
                  ) : (
                    <Typography>{t('userTasks.dialog.noInstance')}</Typography>
                  )}
                </Grid>
                {/* Activities column */}
                <Grid item xs={12} md={7} className="h-full overflow-scroll">
                  {activitiesLoading ? (
                    <Box sx={{ display: 'flex', justifyContent: 'center', p: 2 }}>
                      <CircularProgress />
                    </Box>
                  ) : activitiesError ? (
                    <Typography color="error">{activitiesError}</Typography>
                  ) : activities.length > 0 ? (
                    <Box sx={{ overflow: 'auto', pr: 1 }}>
                      <Timeline position="right">
                        {activities.filter(activity => {
                          return activity.activityType !== "sequenceFlow";
                        })
                          .map((activity, index) => (
                            <TimelineItem key={activity.id}>
                              <TimelineOppositeContent color="textSecondary" sx={{ flex: 0.3 }}>
                                {activity.endTime && (
                                  <div> {new Date(activity.endTime).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}</div>
                                )}
                              </TimelineOppositeContent>
                              <TimelineSeparator>
                                <TimelineDot color={activity.endTime ? "success" : "primary"} />
                                <TimelineConnector />
                              </TimelineSeparator>
                              <TimelineContent>
                                <Typography variant="subtitle2">{activity.activityName}</Typography>
                                <Typography variant="body2" color="textSecondary">
                                  {t('common:activity.duration')}: {calculateDuration(activity)}
                                </Typography>
                                <Typography variant="caption" display="block">
                                  {activity.activityType}
                                </Typography>
                              </TimelineContent>
                            </TimelineItem>
                          ))}
                      </Timeline>
                    </Box>
                  ) : (
                    <Typography>{t('common:noActivities')}</Typography>
                  )}
                </Grid>
            </div>
          </div>
        )}
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose}>
          {t('userTasks.dialog.cancelButton')}
        </Button>
        <Button
          onClick={handleComplete}
          variant="contained"
          color="success"
          startIcon={<CheckCircleIcon />}
        >
          {t('userTasks.dialog.completeButton')}
        </Button>
      </DialogActions>
    </Dialog>
  );
};

export default TaskDetailsDialog;
