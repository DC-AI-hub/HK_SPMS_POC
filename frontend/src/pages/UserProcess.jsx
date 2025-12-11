import React, { useState } from 'react';
import { Box, useTheme } from '@mui/material';
import { useScreenSize } from '../contexts/ScreenSizeContext';
import ResponsiveTabs from '../components/common/ResponsiveTabs';
import DescriptionIcon from '@mui/icons-material/Description';
import PlayArrowIcon from '@mui/icons-material/PlayArrow';
import TaskIcon from '@mui/icons-material/Task';
import HistoryIcon from '@mui/icons-material/History';
import ProcessDefinitionsView from './user-process/ProcessDefinitionsView';
import ProcessInstancesView from './user-process/ProcessInstancesView';
import UserTasksView from './user-process/UserTasksView';
import ProcessHistoryView from './user-process/ProcessHistoryView';

/**
 * Main user process page with tab navigation for process definitions, instances, and user tasks
 */
const UserProcess = () => {
  const theme = useTheme();
  const { isMobileScreen } = useScreenSize();
  const [tabValue, setTabValue] = useState(0);

  const handleTabChange = (event, newValue) => {
    setTabValue(newValue);
  };

  // Tabs configuration for responsive navigation
  const tabs = [
    { icon: <DescriptionIcon />, label: 'userProcess:tabs.processDefinitions' },
    { icon: <PlayArrowIcon />, label: 'userProcess:tabs.processInstances' },
    { icon: <TaskIcon />, label: 'userProcess:tabs.userTasks' },
    { icon: <HistoryIcon />, label: 'userProcess:tabs.processHistory' }
  ];

  return (
    <Box sx={{ 
      width: '100%',
      p: theme.spacing(3),
      backgroundColor: theme.palette.background.default
    }}>
      <ResponsiveTabs 
        tabs={tabs} 
        currentTab={tabValue} 
        onTabChange={handleTabChange} 
      />

      <Box sx={{
        backgroundColor: theme.palette.background.paper,
        borderRadius: theme.shape.borderRadius,
        pb: isMobileScreen ? theme.spacing(10) : theme.spacing(3) // Add extra padding at bottom for mobile to avoid bottom nav overlap
      }}>
        {tabValue === 0 && <ProcessDefinitionsView />}
        {tabValue === 1 && <ProcessInstancesView />}
        {tabValue === 2 && <UserTasksView />}
        {tabValue === 3 && <ProcessHistoryView />}
      </Box>
    </Box>
  );
};

export default UserProcess;
