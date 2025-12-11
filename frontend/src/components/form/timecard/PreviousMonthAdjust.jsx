import React, { useState } from 'react';
import {
  Dialog,
  DialogContent,
  DialogTitle,
  DialogActions,
  Button,
  TextField,
  Select,
  MenuItem,
  FormControl,
  InputLabel,
  Box,
  Typography,
  Popover,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  Card,
  CardContent,
  IconButton
} from '@mui/material';
import { CalendarToday as CalendarIcon, Delete as DeleteIcon } from '@mui/icons-material';
import { toast } from 'react-toastify';

// Import static configuration
import { CLAIM_TYPE_OPTIONS } from '../../../data/timecard/data/mockData';

// Import API services
import { getActiveProjects } from '../../../api/timecard/timecardService';

/**
 * Previous Month Adjustment Component
 * Allows adding timecard entries for the previous month
 */
const PreviousMonthAdjust = ({ open, onClose, currentMonth, onEntriesUpdate, entries = [] }) => {
  const [date, setDate] = useState();
  const [projectCode, setProjectCode] = useState('');
  const [taskNumber, setTaskNumber] = useState('');
  const [activity, setActivity] = useState('');
  const [claimType, setClaimType] = useState('');
  const [hours, setHours] = useState('');
  const [anchorEl, setAnchorEl] = useState(null);
  const [projects, setProjects] = useState([]);
  const [isLoadingProjects, setIsLoadingProjects] = useState(true);

  // Fetch active projects from backend API
  React.useEffect(() => {
    if (open) {
      const fetchProjects = async () => {
        try {
          setIsLoadingProjects(true);
          const response = await getActiveProjects();
          console.log('=== Fetched active projects for adjustment ===', response.data);
          setProjects(response.data);
        } catch (error) {
          console.error('=== Error fetching projects ===', error);
          toast.error('Failed to load projects');
        } finally {
          setIsLoadingProjects(false);
        }
      };

      fetchProjects();
    }
  }, [open]);

  // Reset form when dialog opens/closes
  React.useEffect(() => {
    if (open) {
      setDate(undefined);
      setProjectCode('');
      setTaskNumber('');
      setActivity('');
      setClaimType('');
      setHours('');
    }
  }, [open]);

  // Handle project code change
  const handleProjectCodeChange = (value) => {
    setProjectCode(value);

    // Auto-fill project info
    if (value) {
      const project = projects.find(
        p => p.projectCode === value && p.status === 'ACTIVE'
      );
      if (project) {
        setTaskNumber(project.taskNumber);
        setActivity(project.activity);
      } else {
        setTaskNumber('');
        setActivity('');
      }
    }
  };

  // Validate and submit form
  const handleSubmit = () => {
    // Required fields validation
    if (!date || !projectCode || !taskNumber || !activity || !claimType || !hours) {
      toast.error('All required fields must be filled');
      return;
    }

    // Date range validation - must be in previous month
    const currentYear = currentMonth.getFullYear();
    const currentMonthNum = currentMonth.getMonth();

    const lastMonthStart = new Date(currentYear, currentMonthNum - 1, 1);
    const lastMonthEnd = new Date(currentYear, currentMonthNum, 0);

    if (date < lastMonthStart || date > lastMonthEnd) {
      toast.error('Date must be in the previous month');
      return;
    }

    // Hours validation
    const hoursNum = parseFloat(hours);
    if (isNaN(hoursNum) || hoursNum < 0 || hoursNum > 24) {
      toast.error('Hours must be a number between 0 and 24');
      return;
    }

    // Create new entry
    const project = projects.find(p => p.projectCode === projectCode);
    const newEntry = {
      id: `adjustment-${date.toISOString().split('T')[0]}-${Date.now()}`,
      date: date.toISOString().split('T')[0],
      projectCode: projectCode,
      projectName: project?.projectName || '',
      taskNumber: taskNumber,
      activity: activity,
      claimType: claimType,
      hours: hoursNum,
      remark: 'Previous month adjustment'
    };

    // Append new entry to existing entries
    const updatedEntries = [...entries, newEntry];
    onEntriesUpdate(updatedEntries);
    toast.success('Previous month adjustment saved');
    onClose();
  };

  const formatDate = (date) => {
    return date ? date.toLocaleDateString('en-US') : 'Pick a date';
  };

  const handleDateClick = (event) => {
    setAnchorEl(event.currentTarget);
  };

  const handleDateClose = () => {
    setAnchorEl(null);
  };

  const handleDateSelect = (selectedDate) => {
    setDate(selectedDate);
    handleDateClose();
  };

  // Get previous month adjustment entries
  const getPreviousMonthEntries = () => {
    const currentYear = currentMonth.getFullYear();
    const currentMonthNum = currentMonth.getMonth();
    const lastMonthStart = new Date(currentYear, currentMonthNum - 1, 1);
    const lastMonthEnd = new Date(currentYear, currentMonthNum, 0);

    return entries.filter(entry => {
      const entryDate = new Date(entry.date);
      return entryDate >= lastMonthStart && entryDate <= lastMonthEnd;
    });
  };

  const previousMonthEntries = getPreviousMonthEntries();

  // Handle delete entry
  const handleDeleteEntry = (entryId) => {
    const updatedEntries = entries.filter(entry => entry.id !== entryId);
    onEntriesUpdate(updatedEntries);
    toast.success('Entry deleted successfully');
  };

  return (
    <Dialog
      open={open}
      onClose={onClose}
      maxWidth="sm"
      fullWidth
    >
      <DialogTitle>
        Previous Month Adjust
      </DialogTitle>

      <DialogContent>
        <Typography variant="body2" color="text.secondary" gutterBottom>
          Adjust timecard entries from the previous month
        </Typography>

        <Box sx={{ mt: 2, display: 'flex', flexDirection: 'column', gap: 3 }}>
          {/* Date Picker */}
          <Box>
            <Typography variant="body2" gutterBottom>
              Date *
            </Typography>
            <Button
              variant="outlined"
              fullWidth
              startIcon={<CalendarIcon />}
              onClick={handleDateClick}
              sx={{
                justifyContent: 'flex-start',
                textTransform: 'none'
              }}
            >
              {formatDate(date)}
            </Button>
          </Box>

          {/* Project Code */}
          <FormControl fullWidth>
            <InputLabel>Project Code *</InputLabel>
            <Select
              value={projectCode}
              label="Project Code *"
              onChange={(e) => handleProjectCodeChange(e.target.value)}
            >
              {projects
                .filter(project => project.status === 'ACTIVE')
                .map(project => (
                  <MenuItem key={project.id} value={project.projectCode}>
                    {project.projectCode} - {project.projectName}
                  </MenuItem>
                ))}
            </Select>
          </FormControl>

          {/* Task Number (read-only) */}
          <TextField
            label="Task Number *"
            value={taskNumber}
            fullWidth
            InputProps={{
              readOnly: true
            }}
            sx={{
              '& .MuiInputBase-input': {
                bgcolor: 'grey.50'
              }
            }}
          />

          {/* Activity (read-only) */}
          <TextField
            label="Activity *"
            value={activity}
            fullWidth
            InputProps={{
              readOnly: true
            }}
            sx={{
              '& .MuiInputBase-input': {
                bgcolor: 'grey.50'
              }
            }}
          />

          {/* Claim Type */}
          <FormControl fullWidth>
            <InputLabel>Claim Type *</InputLabel>
            <Select
              value={claimType}
              label="Claim Type *"
              onChange={(e) => setClaimType(e.target.value)}
            >
              {CLAIM_TYPE_OPTIONS.map(option => (
                <MenuItem key={option.value} value={option.value}>
                  {option.label}
                </MenuItem>
              ))}
            </Select>
          </FormControl>

          {/* Hours */}
          <TextField
            label="Hours *"
            type="number"
            value={hours}
            onChange={(e) => setHours(e.target.value)}
            fullWidth
            inputProps={{
              step: 0.5,
              min: 0,
              max: 24
            }}
            placeholder="0.0"
          />
        </Box>

        {/* Date Picker Popover */}
        <Popover
          open={Boolean(anchorEl)}
          anchorEl={anchorEl}
          onClose={handleDateClose}
          anchorOrigin={{
            vertical: 'bottom',
            horizontal: 'left',
          }}
        >
          <Box sx={{ p: 2 }}>
            <Typography variant="body2" gutterBottom>
              Select a date from previous month
            </Typography>
            <TextField
              type="date"
              value={date ? date.toISOString().split('T')[0] : ''}
              onChange={(e) => handleDateSelect(new Date(e.target.value))}
              fullWidth
            />
          </Box>
        </Popover>
      </DialogContent>

      <DialogActions>
        <Button onClick={onClose}>
          Cancel
        </Button>
        <Button onClick={handleSubmit} variant="contained">
          Save Adjustment
        </Button>
      </DialogActions>
    </Dialog>
  );
};

/**
 * Previous Month Adjust Table Component
 * Displays all previous month adjustment entries in a table
 */
export const PreviousMonthAdjustTable = ({ currentMonth, entries, onEntriesUpdate, readOnly = false }) => {
  // Get previous month adjustment entries
  const getPreviousMonthEntries = () => {
    const currentYear = currentMonth.getFullYear();
    const currentMonthNum = currentMonth.getMonth();
    const lastMonthStart = new Date(currentYear, currentMonthNum - 1, 1);
    const lastMonthEnd = new Date(currentYear, currentMonthNum, 0);

    return entries.filter(entry => {
      const entryDate = new Date(entry.date);
      return entryDate >= lastMonthStart && entryDate <= lastMonthEnd;
    });
  };

  const previousMonthEntries = getPreviousMonthEntries();

  // Handle delete entry
  const handleDeleteEntry = (entryId) => {
    const updatedEntries = entries.filter(entry => entry.id !== entryId);
    onEntriesUpdate(updatedEntries);
    toast.success('Entry deleted successfully');
  };

  if (previousMonthEntries.length === 0) {
    return (
      <Card>
        <CardContent>
          <Box sx={{ textAlign: 'center', py: 4 }}>
            <Typography color="text.secondary">
              No previous month adjustments yet
            </Typography>
          </Box>
        </CardContent>
      </Card>
    );
  }

  return (
    <Card>
      <CardContent>
        <TableContainer component={Paper} variant="outlined">
          <Table>
            <TableHead>
              <TableRow>
                <TableCell sx={{ fontWeight: 'bold' }}>Date</TableCell>
                <TableCell sx={{ fontWeight: 'bold' }}>Project Code</TableCell>
                <TableCell sx={{ fontWeight: 'bold' }}>Project Name</TableCell>
                <TableCell sx={{ fontWeight: 'bold' }}>Task Number</TableCell>
                <TableCell sx={{ fontWeight: 'bold' }}>Activity</TableCell>
                <TableCell sx={{ fontWeight: 'bold' }}>Claim Type</TableCell>
                <TableCell align="right" sx={{ fontWeight: 'bold' }}>Hours</TableCell>
                {!readOnly && (
                  <TableCell align="center" sx={{ fontWeight: 'bold' }}>Actions</TableCell>
                )}
              </TableRow>
            </TableHead>
            <TableBody>
              {previousMonthEntries.map((entry) => (
                <TableRow key={entry.id}>
                  <TableCell>{entry.date}</TableCell>
                  <TableCell>{entry.projectCode}</TableCell>
                  <TableCell>{entry.projectName}</TableCell>
                  <TableCell>{entry.taskNumber}</TableCell>
                  <TableCell>{entry.activity}</TableCell>
                  <TableCell>{entry.claimType}</TableCell>
                  <TableCell align="right">{entry.hours.toFixed(1)}</TableCell>
                  {!readOnly && (
                    <TableCell align="center">
                      <IconButton
                        size="small"
                        onClick={() => handleDeleteEntry(entry.id)}
                        color="error"
                      >
                        <DeleteIcon fontSize="small" />
                      </IconButton>
                    </TableCell>
                  )}
                </TableRow>
              ))}
              {/* Total Row */}
              <TableRow sx={{ bgcolor: 'grey.50' }}>
                <TableCell colSpan={6} sx={{ fontWeight: 'bold' }}>
                  Total
                </TableCell>
                <TableCell align="right" sx={{ fontWeight: 'bold' }}>
                  {previousMonthEntries.reduce((sum, entry) => sum + entry.hours, 0).toFixed(1)}
                </TableCell>
                {!readOnly && <TableCell />}
              </TableRow>
            </TableBody>
          </Table>
        </TableContainer>
      </CardContent>
    </Card>
  );
};

export default PreviousMonthAdjust;