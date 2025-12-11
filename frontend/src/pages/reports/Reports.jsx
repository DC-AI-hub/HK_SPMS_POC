import React, { useState } from 'react';
import { Box } from '@mui/material';
import ResponsiveTabs from '../../components/common/ResponsiveTabs';
import TimecardSummaryReport from './TimecardSummaryReport';
import IndividualTimecardReport from './IndividualTimecardReport';
import TimecardDetailsReport from './TimecardDetailsReport';
import PermStaffOTReport from './PermStaffOTReport';
import SummarizeIcon from '@mui/icons-material/Summarize';
import PersonIcon from '@mui/icons-material/Person';
import DescriptionIcon from '@mui/icons-material/Description';
import WorkHistoryIcon from '@mui/icons-material/WorkHistory';

export default function Reports() {
  const [activeTab, setActiveTab] = useState(0);
  const handleTabChange = (e, v) => setActiveTab(v);

  const tabs = [
    { icon: <SummarizeIcon />, label: 'Summary Report' },
    { icon: <PersonIcon />, label: 'Individual Report' },
    { icon: <DescriptionIcon />, label: 'Details Report' },
    { icon: <WorkHistoryIcon />, label: 'OT Record Report' }
  ];

  return (
    <Box sx={{ width: '100%', p: 3 }}>
      <ResponsiveTabs
        tabs={tabs}
        currentTab={activeTab}
        onTabChange={handleTabChange}
        mobilePosition="top"
      />
      <Box sx={{ mt: 2 }}>
        {activeTab === 0 && <TimecardSummaryReport />}
        {activeTab === 1 && <IndividualTimecardReport />}
        {activeTab === 2 && <TimecardDetailsReport />}
        {activeTab === 3 && <PermStaffOTReport />}
      </Box>
    </Box>
  );
}
