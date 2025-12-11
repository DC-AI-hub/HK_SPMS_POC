import React, { useState, useEffect, useMemo, useRef } from 'react';
import {
  Box,
  Paper,
  Typography,
  Button,
  Divider,
  Alert,
  CircularProgress
} from '@mui/material';
import {
  Save as SaveIcon,
  Edit as EditIcon
} from '@mui/icons-material';
import { toast } from 'react-toastify';

// Import static configuration and types
import {
  initialTimecardEntries,
  CLAIM_TYPE_OPTIONS
} from '../../../data/timecard/data/mockData';
import { TimecardStatus } from '../../../data/timecard/types';

// Import API services
import { getHolidays } from '../../../api/timecard/timecardService';
import systemService from '../../../api/system/systemService';
import userService from '../../../api/idm/userService';

// Import sub-components
import StaffInfoCard from './StaffInfoCard';
import CalendarView from './CalendarView';
import TimecardSummary from './TimecardSummary';
import PreviousMonthAdjust, { PreviousMonthAdjustTable } from './PreviousMonthAdjust';

/**
 * Main Timecard Form Component - Simplified Architecture (Plan D)
 * 
 * Data Flow Philosophy:
 * - Single Source of Truth: Process engine formData
 * - New Form: Fetch login-info → render → save to formData
 * - Existing Form: Use initialData.employeeInfo directly
 * - No intermediate state, no localStorage
 *
 * @param {Object} props
 * @param {Function} props.onSubmit - Callback when form is submitted
 * @param {boolean} props.readOnly - Whether the form is in read-only mode
 * @param {Object} props.initialData - Initial data from process engine formData
 * @param {string} props.processInstanceId - Process instance ID for isolation
 */
const TimecardForm = ({ onSubmit, readOnly = false, initialData = {}, processInstanceId = null }) => {
  // ====== UI State Management ======
  const [entries, setEntries] = useState([]);
  const [currentMonth, setCurrentMonth] = useState(new Date());
  const [status, setStatus] = useState(TimecardStatus.DRAFT);
  const [holidays, setHolidays] = useState([]);
  const [showPreviousMonthAdjust, setShowPreviousMonthAdjust] = useState(false);
  
  // ====== Employee Info State (only for new form mode) ======
  const [loginInfo, setLoginInfo] = useState(null);
  const [isLoadingEmployeeInfo, setIsLoadingEmployeeInfo] = useState(false);
  const [isLoadingHolidays, setIsLoadingHolidays] = useState(false);
  
  // ====== Prevent duplicate holidays fetch ======
  const holidaysFetchedRef = useRef(false);

  // ====== Helper: Convert login-info to employee info format ======
  const convertLoginInfoToEmployeeInfo = (loginInfo) => {
    if (!loginInfo) return null;

    const now = new Date();
    const timecardMonth = `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}`;

    // Helper function to get user display name
    const getUserDisplayName = (user) => {
      if (!user) return null;
      if (user.userProfiles && user.userProfiles['Chinese Name']) {
        return user.userProfiles['Chinese Name'];
      }
      return user.username || null;
    };

    // Extract department information (comes from /users/:id API, merged into loginInfo)
    const localDept = loginInfo.localDepartment;
    const functionalDept = loginInfo.functionalDepartment;

    return {
      staffId: String(loginInfo.id),
      staffNameChinese: loginInfo.userProfiles?.['Chinese Name'] || null,
      staffNameEnglish: loginInfo.username,
      team: functionalDept?.name || null, // Functional department is the team
      staffType: loginInfo.type || 'STAFF',
      departmentHead: localDept?.departmentHead ? getUserDisplayName(localDept.departmentHead) : null,
      finalApproval: functionalDept?.departmentHead ? getUserDisplayName(functionalDept.departmentHead) : null,
      timecardMonth: timecardMonth
    };
  };

  // ====== Computed: Employee Info (no state!) ======
  const employeeInfo = useMemo(() => {
    // Priority 1: Use initialData.employeeInfo (approval/draft mode)
    if (initialData?.employeeInfo) {
      return initialData.employeeInfo;
    }
    
    // Priority 2: Convert from loginInfo (new form mode)
    if (loginInfo) {
      return convertLoginInfoToEmployeeInfo(loginInfo);
    }
    
    return null;
  }, [initialData?.employeeInfo, loginInfo]);

  // ====== Effect: Initialize Form Data ======
  useEffect(() => {
    console.log('=== TimecardForm: useEffect Triggered ===');
    console.log('ProcessInstanceId:', processInstanceId);
    console.log('InitialData:', initialData);
    console.log('Has initialData.employeeInfo:', !!initialData?.employeeInfo);
    console.log('Has initialData.timecardEntries:', !!initialData?.timecardEntries);
    console.log('TimecardEntries count:', initialData?.timecardEntries?.length || 0);
    
    // Clean up legacy localStorage (one-time cleanup)
    if (localStorage.getItem('timecardFormData')) {
      localStorage.removeItem('timecardFormData');
      console.log('Cleaned up legacy localStorage');
    }

    // 核心逻辑：先检查流程引擎是否有数据
    // 如果有数据（employeeInfo 或 timecardEntries），直接使用引擎数据渲染
    if (initialData?.employeeInfo || (initialData?.timecardEntries && initialData.timecardEntries.length > 0)) {
      console.log('=== TimecardForm: Using Engine Data (Draft/Approval Mode) ===');
      
      // Set entries
      if (initialData.timecardEntries && Array.isArray(initialData.timecardEntries)) {
        setEntries(initialData.timecardEntries);
        console.log('Set entries:', initialData.timecardEntries.length);
        
        // Determine month from first entry date
        if (initialData.timecardEntries.length > 0 && initialData.timecardEntries[0].date) {
          const [year, month] = initialData.timecardEntries[0].date.split('-');
          setCurrentMonth(new Date(parseInt(year), parseInt(month) - 1, 1));
        }
      } else {
         //setEntries([]);
      }
      
      // Set status
      if (initialData.summary?.status) {
        setStatus(initialData.summary.status);
      }
      
      // Set month from summary or employeeInfo if no entries
      if (!initialData.timecardEntries || initialData.timecardEntries.length === 0) {
        if (initialData.employeeInfo?.timecardMonth) {
          const [year, month] = initialData.employeeInfo.timecardMonth.split('-');
          setCurrentMonth(new Date(parseInt(year), parseInt(month) - 1, 1));
        }
      }
      
      // 清除 loginInfo，确保使用引擎数据
      setLoginInfo(null);
      return;
    }
    
    // 如果引擎没有数据，调用 login-info 获取当前用户信息（新表单模式）
    console.log('=== TimecardForm: No Engine Data - Fetching Login Info (New Form Mode) ===');
    
    const fetchLoginInfo = async () => {
      setIsLoadingEmployeeInfo(true);
      
      try {
        // Step 1: Get login-info
        const loginInfoResponse = await systemService.getLoginInfo();
        
        // Step 2: Get full user details (includes functionalDepartment, localDepartment, userProfiles)
        let userDetails = null;
        try {
          const userResponse = await userService.get(loginInfoResponse.data.id);
          userDetails = userResponse.data;
        } catch (userError) {
          console.warn('Failed to fetch user details:', userError);
        }
        
        // Merge full user details into loginInfo
        const mergedLoginInfo = {
          ...loginInfoResponse.data,
          ...userDetails  // Include functionalDepartment, localDepartment, userProfiles, etc.
        };
        
        setLoginInfo(mergedLoginInfo);
   

        //setStatus(TimecardStatus.DRAFT);
        
      } catch (error) {
        console.error('Error fetching login-info:', error);
        toast.error('Failed to load employee information');
      } finally {
        setIsLoadingEmployeeInfo(false);
      }
    };
    
    fetchLoginInfo();
  }, [processInstanceId, initialData]); // 依赖 processInstanceId 和完整的 initialData

  // ====== Effect: Fetch Holidays ======
  useEffect(() => {
    // Only fetch once per unique year-month combination
    const year = currentMonth.getFullYear();
    const month = currentMonth.getMonth() + 1;
    const monthKey = `${year}-${month}`;
    
    if (holidaysFetchedRef.current === monthKey) {
      return; // Already fetched for this month
    }
    
    const fetchHolidays = async () => {
      setIsLoadingHolidays(true);
      
      try {
        const response = await getHolidays(year, month);
        setHolidays(response.data);
        holidaysFetchedRef.current = monthKey;
      } catch (error) {
        console.error('Error fetching holidays:', error);
        toast.error('Failed to load holidays');
      } finally {
        setIsLoadingHolidays(false);
      }
    };

    fetchHolidays();
  }, [currentMonth]);

  // ====== Handler: Save Draft ======
  const handleSaveDraft = () => {
    if (!employeeInfo) {
      toast.error('Employee information not loaded');
      return;
    }
    
    
    const timecardData = {
      employeeInfo,
      timecardEntries: entries,
      summary: {
        totalHours: entries.reduce((sum, entry) => sum + entry.hours, 0),
        totalEntries: entries.length,
        month: currentMonth.toISOString().split('T')[0].substring(0, 7),  // THIS FILED IS USE FOR DISPLAY , DO NOT USE FOR DATA TRACING 
        status: status
      },
      timestamp: new Date().toISOString()
    };

    // Directly call onSubmit - data will be saved to process engine formData
    if (onSubmit) {
      onSubmit(timecardData);
    }

    toast.success('Timecard draft saved successfully');
  };

  // ====== Handler: Entries Update ======
  const handleEntriesUpdate = (newEntries) => {
    setEntries(newEntries);
  };

  const getEnterties = ()=>{
    console.log("========->",entries)
    return entries;
  }

  // ====== Render: Loading State ======
  if (isLoadingEmployeeInfo) {
    return (
      <Box sx={{ 
        display: 'flex', 
        flexDirection: 'column',
        justifyContent: 'center', 
        alignItems: 'center', 
        minHeight: '400px',
        gap: 2
      }}>
        <CircularProgress size={60} />
        <Typography variant="h6" color="text.secondary">
          Loading employee information...
        </Typography>
      </Box>
    );
  }

  // ====== Main Render ======
  return (
    <Box sx={{ minHeight: '100vh', bgcolor: 'grey.50', width: '100%', p: 0 }}>
      <Paper
        elevation={0}
        sx={{
          width: '100%',
          mx: 0,
          bgcolor: 'background.paper',
          borderRadius: 0,
          minHeight: '100vh'
        }}
      >
        {/* Header Section */}
        <Box sx={{ p: 3, borderBottom: 1, borderColor: 'divider' }}>
          <Typography variant="h4" component="h1" gutterBottom>
            Timecard Management System
          </Typography>
          <Typography variant="subtitle1" color="text.secondary">
            Track hours, manage projects, and generate reports
          </Typography>
        </Box>

        {/* Main Content */}
        <Box sx={{ p: 3, width: '100%' }}>
          {/* Staff Information Card */}
          <StaffInfoCard staffInfo={employeeInfo} isLoading={isLoadingEmployeeInfo} />

          {/* Timecard Summary */}
          <Box sx={{ my: 3 }}>
            <TimecardSummary entries={entries} />
          </Box>

          {/* Action Buttons */}
          {!readOnly && (
            <Box sx={{
              display: 'flex',
              flexWrap: 'wrap',
              gap: 2,
              mb: 3,
              justifyContent: { xs: 'center', md: 'flex-start' }
            }}>
              <Button
                startIcon={<SaveIcon />}
                onClick={handleSaveDraft}
                variant="outlined"
                disabled={status === TimecardStatus.SUBMITTED || !employeeInfo}
              >
                Save Draft
              </Button>
            </Box>
          )}

          {/* Status Alert */}
          {status === TimecardStatus.SUBMITTED && (
            <Alert severity="success" sx={{ mb: 3 }}>
              Timecard has been submitted successfully and is pending approval.
            </Alert>
          )}

          <Divider sx={{ my: 3 }} />

          {/* Calendar View */}
          <CalendarView
            key={`calendar-${currentMonth.getTime()}-${entries.length}`}
            currentMonth={currentMonth}
            entries={entries}
            holidays={holidays}
            onEntriesUpdate={handleEntriesUpdate}
            readOnly={readOnly}
          />

          <Divider sx={{ my: 3 }} />

          {/* Previous Month Adjust Section */}
          <Box sx={{ mb: 3 }}>
            <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
              <Typography variant="h6">
                Previous Month Adjust
              </Typography>
              {!readOnly && (
                <Button
                  startIcon={<EditIcon />}
                  onClick={() => setShowPreviousMonthAdjust(true)}
                  variant="outlined"
                >
                  Add Adjustment
                </Button>
              )}
            </Box>
            <PreviousMonthAdjustTable
              currentMonth={currentMonth}
              entries={getEnterties()}
              onEntriesUpdate={handleEntriesUpdate}
              readOnly={readOnly}
            />
          </Box>

          {/* Previous Month Adjustment Dialog */}
          <PreviousMonthAdjust
            open={showPreviousMonthAdjust}
            onClose={() => setShowPreviousMonthAdjust(false)}
            currentMonth={currentMonth}
            onEntriesUpdate={handleEntriesUpdate}
            entries={entries}
          />
        </Box>
      </Paper>
    </Box>
  );
};

export default TimecardForm;
