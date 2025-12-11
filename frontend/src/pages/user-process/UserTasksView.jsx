import React, { useState, useEffect } from 'react';
import {
  Box, Typography, Table, TableBody,
  TableCell, TableContainer, TableHead,
  TableRow, Paper, Button, IconButton,
  Tooltip, LinearProgress, Pagination
} from '@mui/material';
import { useTranslation } from 'react-i18next';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import CancelIcon from '@mui/icons-material/Cancel';
import InfoIcon from '@mui/icons-material/Info';
import TaskDetailsDialog from '../../components/user-process/TaskDetailsDialog';
import DeclineTaskDialog from '../../components/user-process/DeclineTaskDialog';
import userProcessInstanceService from '../../api/process/userProcessInstanceService';
import formSchemaService from '../../api/process/formSchemaService';
import processInstanceService from '../../api/process/processInstanceService';

/**
 * User Tasks View - Displays tasks assigned to the user
 */
const UserTasksView = () => {
  const { t } = useTranslation('userProcess');
  const [tasks, setTasks] = useState([]);
  const [formSchemas, setFormSchemas] = useState({});
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [selectedTask, setSelectedTask] = useState(null);
  const [isDetailsDialogOpen, setIsDetailsDialogOpen] = useState(false);
  const [isDeclineDialogOpen, setIsDeclineDialogOpen] = useState(false);
  const [declineReason, setDeclineReason] = useState('');
  const [formSchemaLoading, setFormSchemaLoading] = useState(false);

  // Create enhanced tasks with form schemas
  const enhancedTasks = tasks.map(task => ({
    ...task,
    formSchema: formSchemas[task.taskId] || null
  }));

  // Pagination state
  const [pagination, setPagination] = useState({
    page: 0,
    size: 10,
    totalPages: 0,
    totalElements: 0
  });
  const fetchTasks = async () => {
    try {
      setLoading(true);
      const response = await userProcessInstanceService.getAssignedTasks({
        page: pagination.page,
        size: pagination.size,
        sort: 'taskId,asc'
      });

      setTasks(response.content);
      setPagination(prev => ({
        ...prev,
        totalPages: response.totalPages,
        totalElements: response.totalElements
      }));

      // Fetch form schemas for tasks
      setFormSchemaLoading(true);
      try {
        const schemaMap = {};
        for (const task of response.content) {
          const schema = await formSchemaService.getFormSchemaByTaskId(task.taskId);
          if (schema) {
            schemaMap[task.taskId] = schema;
          }
        }
        setFormSchemas(schemaMap);
      } catch (err) {
        console.error('Error fetching form schemas:', err);
      } finally {
        setFormSchemaLoading(false);
      }
    } catch (err) {
      setError(t('userTasks.fetchError'));
      console.error('Error fetching tasks:', err);
    } finally {
      setLoading(false);
    }
  };

  // Fetch tasks from API
  useEffect(() => {

    fetchTasks();
  }, [pagination.page, pagination.size, t]);

  const handlePageChange = (event, newPage) => {
    setPagination(prev => ({ ...prev, page: newPage - 1 }));
  };

  const handleDetailsClick = (task) => {
    setSelectedTask(task);
    setIsDetailsDialogOpen(true);
  };

  const handleDeclineClick = (task) => {
    setSelectedTask(task);
    setIsDeclineDialogOpen(true);
  };

  const handleCloseDetailsDialog = () => {
    setIsDetailsDialogOpen(false);
    setSelectedTask(null);
  };

  const handleCloseDeclineDialog = () => {
    setIsDeclineDialogOpen(false);
    setSelectedTask(null);
    setDeclineReason('');
  };

  // Handle task operations
  const handleCompleteTask = (e) => {
    console.log('=== Completing Task ===');
    console.log('Task ID:', e.taskId);
    console.log('Instance ID:', e.processInstanceId);
    console.log('FormData to submit:', e.formData);
    console.log('FormData keys:', Object.keys(e.formData || {}));
    
    // 验证必要的数据
    if (!e.processInstanceId || !e.taskId) {
      console.error('Missing required task information');
      return;
    }
    
    // 构建完成任务的数据
    const completeTaskData = {
      "rejectionReason": null,
      "formData": e.formData || {},
      "approve": "approve"
    };
    
    console.log('=== Complete Task Data ===', completeTaskData);
    
    setIsDetailsDialogOpen(false);
    //保存数据到引擎并推进到下一步
    processInstanceService.completeTask(
      e.processInstanceId,
      e.taskId,
      completeTaskData
    ).then(() => {
      console.log('Task completed successfully');
      setTimeout(() => {
        fetchTasks()  
      }, 500);
    }).catch((error) => {
      console.error('Failed to complete task:', error);
      // 可以在这里添加用户友好的错误提示
    });
  };

  const handleConfirmDecline = (reason) => {
    console.log(`Declining task: ${selectedTask.name} with reason: ${reason}`);
    handleCloseDeclineDialog();
    processInstanceService.rejectTask(selectedTask.processInstanceId, selectedTask.taskId, {
      "rejectionReason": reason,
      "formData": {},
      "approve": "reject"
    })
    setTimeout(() => {
      fetchTasks()  
    }, 500);
    
    // In real implementation, we would update API and refresh task list
  };

  return (
    <div>
      <Typography variant="h5" gutterBottom mb={2}>
        {t('userTasks.title')}
      </Typography>

      {(loading || formSchemaLoading) && <LinearProgress />}
      {error && (
        <Box mb={2} p={2} bgcolor="error.main" color="white" borderRadius={1}>
          <Typography>{error}</Typography>
        </Box>
      )}

      <TableContainer component={Paper} sx={{ mb: 2 }}>
        <Table sx={{ minWidth: 650 }} aria-label="user tasks table">
          <TableHead>
            <TableRow>
              <TableCell>{t('userTasks.table.taskTitle')}</TableCell>
              <TableCell>{t('userTasks.table.assignee')}</TableCell>
              <TableCell>{t('userTasks.table.actions')}</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {tasks.map((task) => (
              <TableRow key={task.taskId} hover>
                <TableCell>{task.name}</TableCell>
                <TableCell>{task.assignee || t('userTasks.unassigned')}</TableCell>
                <TableCell>
                  <Box display="flex" gap={1}>
                    <Tooltip title={t('userTasks.detailsTooltip')}>
                      <IconButton
                        color="primary"
                        onClick={() => handleDetailsClick(task)}
                      >
                        <InfoIcon />
                      </IconButton>
                    </Tooltip>
                    <Tooltip title={t('userTasks.completeTooltip')}>
                      <IconButton
                        color="success"
                        onClick={() => handleDetailsClick(task)}
                      >
                        <CheckCircleIcon />
                      </IconButton>
                    </Tooltip>
                    <Tooltip title={t('userTasks.declineTooltip')}>
                      <IconButton
                        color="error"
                        onClick={() => handleDeclineClick(task)}
                      >
                        <CancelIcon />
                      </IconButton>
                    </Tooltip>
                  </Box>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>

      {pagination.totalPages > 1 && (
        <Box display="flex" justifyContent="center">
          <Pagination
            count={pagination.totalPages}
            page={pagination.page + 1}
            onChange={handlePageChange}
            color="primary"
          />
        </Box>
      )}

      <TaskDetailsDialog
        open={isDetailsDialogOpen}
        task={selectedTask ? enhancedTasks.find(t => t.taskId === selectedTask.taskId) : null}
        onClose={handleCloseDetailsDialog}
        onComplete={handleCompleteTask}
      />

      <DeclineTaskDialog
        open={isDeclineDialogOpen}
        onClose={handleCloseDeclineDialog}
        onConfirm={handleConfirmDecline}
        task={selectedTask}
        translations={{
          title: t('userTasks.declineDialog.title'),
          description: t('userTasks.declineDialog.description'),
          reasonLabel: t('userTasks.declineDialog.reasonLabel'),
          cancelButton: t('userTasks.declineDialog.cancelButton'),
          confirmButton: t('userTasks.declineDialog.confirmButton')
        }}
      />
    </div>
  );
};

export default UserTasksView;
