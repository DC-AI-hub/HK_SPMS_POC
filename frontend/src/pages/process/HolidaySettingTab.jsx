import React, { useState, useEffect } from 'react';
import {
  Card,
  CardContent,
  CardHeader,
  Box,
  TextField,
  Button,
  Typography,
  IconButton,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogContentText,
  DialogActions
} from '@mui/material';
import {
  Upload as UploadIcon,
  Download as DownloadIcon,
  Delete as DeleteIcon
} from '@mui/icons-material';
import { toast } from 'react-toastify';
import { getHolidays, importHolidays, exportHolidays, createHoliday, updateHoliday, deleteHoliday } from '../../api/timecard/timecardService';
import HolidayDialog from '../../components/holiday/HolidayDialog';

/**
 * HolidaySettingTab Component
 * Tab component for managing holidays in Process page
 */
const HolidaySettingTab = () => {
  const [currentMonth, setCurrentMonth] = useState(new Date());
  const [holidays, setHolidays] = useState([]);
  const [loading, setLoading] = useState(false);
  const [showDialog, setShowDialog] = useState(false);
  const [selectedDate, setSelectedDate] = useState(null);
  const [selectedHoliday, setSelectedHoliday] = useState(null);
  const [showDeleteConfirm, setShowDeleteConfirm] = useState(false);
  const [holidayToDelete, setHolidayToDelete] = useState(null);
  const [monthInput, setMonthInput] = useState(
    `${new Date().getFullYear()}-${String(new Date().getMonth() + 1).padStart(2, '0')}`
  );

  // Fetch holidays when month changes
  useEffect(() => {
    fetchHolidays();
  }, [currentMonth]);

  const fetchHolidays = async () => {
    try {
      setLoading(true);
      const year = currentMonth.getFullYear();
      const month = currentMonth.getMonth() + 1;
      const response = await getHolidays(year, month);
      // Transform backend response to match frontend format
      // Backend returns {id, date}, frontend needs {id, date, country, name, type}
      const transformedHolidays = (response.data || []).map(h => ({
        id: h.id,
        date: h.date || (h.holidayDate ? h.holidayDate.toString() : null),
        country: h.country || 'CN', // Default if not provided by backend
        name: h.name || `Holiday ${h.date || h.holidayDate}`, // Default name
        type: h.type || 'PUBLIC_HOLIDAY' // Default type
      }));
      setHolidays(transformedHolidays);
    } catch (error) {
      console.error('Error fetching holidays:', error);
      toast.error('Failed to load holidays');
    } finally {
      setLoading(false);
    }
  };

  const handleMonthChange = (event) => {
    const value = event.target.value;
    setMonthInput(value);
    if (value) {
      const [year, month] = value.split('-').map(Number);
      setCurrentMonth(new Date(year, month - 1, 1));
    }
  };

  const handleDayClick = (dateStr) => {
    // Find existing holiday for this date
    const existingHoliday = holidays.find(h => h.date === dateStr);
    setSelectedDate(dateStr);
    setSelectedHoliday(existingHoliday || null);
    setShowDialog(true);
  };

  const handleSaveHoliday = async (holidayData) => {
    try {
      // Prepare data for backend API - send complete holiday data
      const backendData = {
        date: holidayData.date,
        country: holidayData.country,
        name: holidayData.name,
        type: holidayData.type
      };

      if (selectedHoliday && selectedHoliday.id) {
        // Update existing holiday
        try {
          await updateHoliday(selectedHoliday.id, backendData);
          toast.success('Holiday updated successfully');
          fetchHolidays(); // Refresh list
        } catch (apiError) {
          // If API not implemented, fallback to local update
          console.warn('Update API not available, using local update:', apiError);
          const updatedHolidays = holidays.map(h =>
            h.id === selectedHoliday.id ? holidayData : h
          );
          setHolidays(updatedHolidays);
          toast.success('Holiday updated successfully (local)');
        }
      } else {
        // Add new holiday
        try {
          await createHoliday(backendData);
          toast.success('Holiday added successfully');
          fetchHolidays(); // Refresh list
        } catch (apiError) {
          // If API not implemented, fallback to local add
          console.warn('Create API not available, using local add:', apiError);
          setHolidays([...holidays, holidayData]);
          toast.success('Holiday added successfully (local)');
        }
      }
    } catch (error) {
      console.error('Error saving holiday:', error);
      toast.error('Failed to save holiday');
    }
  };

  const handleDeleteHoliday = (dateStr) => {
    // Show confirmation dialog
    const holiday = holidays.find(h => h.date === dateStr);
    setHolidayToDelete({ dateStr, holiday });
    setShowDeleteConfirm(true);
  };

  const confirmDeleteHoliday = async () => {
    if (!holidayToDelete) return;

    try {
      const { dateStr, holiday } = holidayToDelete;
      if (holiday && holiday.id) {
        try {
          await deleteHoliday(holiday.id);
          toast.success('Holiday removed');
          fetchHolidays(); // Refresh list
        } catch (apiError) {
          // If API not implemented, fallback to local delete
          console.warn('Delete API not available, using local delete:', apiError);
          const updatedHolidays = holidays.filter(h => h.date !== dateStr);
          setHolidays(updatedHolidays);
          toast.success('Holiday removed (local)');
        }
      } else {
        // Local delete if no ID
        const updatedHolidays = holidays.filter(h => h.date !== dateStr);
        setHolidays(updatedHolidays);
        toast.success('Holiday removed');
      }
    } catch (error) {
      console.error('Error deleting holiday:', error);
      toast.error('Failed to delete holiday');
    } finally {
      setShowDeleteConfirm(false);
      setHolidayToDelete(null);
    }
  };

  const handleImport = async (event) => {
    const file = event.target.files?.[0];
    if (!file) return;

    try {
      const response = await importHolidays(file);
      toast.success('Holidays imported successfully');
      fetchHolidays();
    } catch (error) {
      console.error('Error importing holidays:', error);
      toast.error('Failed to import holidays');
    }
  };

  const handleExport = async () => {
    try {
      const year = currentMonth.getFullYear();
      const response = await exportHolidays(year);
      
      // Create blob and download
      const url = window.URL.createObjectURL(new Blob([response.data]));
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', `holidays-${year}.xlsx`);
      document.body.appendChild(link);
      link.click();
      link.remove();
      
      toast.success('Holidays exported successfully');
    } catch (error) {
      console.error('Error exporting holidays:', error);
      toast.error('Failed to export holidays');
    }
  };

  // Create a custom calendar view adapter for holidays
  const HolidayCalendarView = ({ currentMonth, holidays, onDayClick, onDeleteHoliday }) => {
    const weekDays = ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'];
    const year = currentMonth.getFullYear();
    const month = currentMonth.getMonth();
    const daysInMonth = new Date(year, month + 1, 0).getDate();
    const startingDayOfWeek = new Date(year, month, 1).getDay();

    const isHoliday = (dateStr) => {
      return holidays.some(h => h.date === dateStr);
    };

    const getHolidayInfo = (dateStr) => {
      return holidays.find(h => h.date === dateStr);
    };

    const formatMonth = (date) => {
      return date.toLocaleDateString('en-US', { year: 'numeric', month: 'long' });
    };

    const calendarCells = [];

    // Empty cells
    for (let i = 0; i < startingDayOfWeek; i++) {
      calendarCells.push(
        <Box
          key={`empty-${i}`}
          sx={{
            aspectRatio: '1',
            minHeight: 120,
            border: 1,
            borderColor: 'divider',
            borderTop: 0,
            bgcolor: 'background.paper'
          }}
        />
      );
    }

    // Date cells
    for (let day = 1; day <= daysInMonth; day++) {
      const dateStr = `${year}-${String(month + 1).padStart(2, '0')}-${String(day).padStart(2, '0')}`;
      const dayOfWeek = new Date(year, month, day).getDay();
      const isHolidayDay = isHoliday(dateStr);
      const holidayInfo = getHolidayInfo(dateStr);
      const isFirstRow = day <= (7 - startingDayOfWeek);

      calendarCells.push(
        <Box
          key={dateStr}
          onClick={() => onDayClick(dateStr)}
          sx={{
            aspectRatio: '1',
            minHeight: 120,
            border: 1,
            borderColor: 'divider',
            borderTop: isFirstRow ? 0 : undefined,
            bgcolor: isHolidayDay ? 'rgba(76, 175, 80, 0.1)' : 'background.paper',
            p: 1.5,
            cursor: 'pointer',
            position: 'relative',
            display: 'flex',
            flexDirection: 'column',
            '&:hover': {
              borderColor: 'primary.main',
              '& .delete-button': {
                opacity: 1
              }
            }
          }}
        >
          {/* Delete button for holidays */}
          {isHolidayDay && (
            <IconButton
              className="delete-button"
              size="small"
              onClick={(e) => {
                e.stopPropagation();
                onDeleteHoliday(dateStr);
              }}
              sx={{
                position: 'absolute',
                top: 4,
                right: 4,
                opacity: 0.6,
                transition: 'opacity 0.2s',
                color: 'error.main',
                bgcolor: 'background.paper',
                '&:hover': {
                  opacity: 1,
                  bgcolor: 'action.hover'
                }
              }}
            >
              <DeleteIcon fontSize="small" />
            </IconButton>
          )}

          {/* Date number */}
          <Typography variant="body2" sx={{ fontWeight: 'bold', mb: 0.5 }}>
            {day}
          </Typography>

          {/* Holiday info */}
          {isHolidayDay && holidayInfo && (
            <Box sx={{ flex: 1, display: 'flex', flexDirection: 'column', gap: 0.5 }}>
              <Typography variant="caption" sx={{ fontWeight: 'bold', color: 'success.dark' }}>
                {holidayInfo.name}
              </Typography>
              <Typography variant="caption" color="text.secondary">
                {holidayInfo.country}
              </Typography>
            </Box>
          )}
        </Box>
      );
    }

    return (
      <Card>
        <CardContent>
          <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mb: 2 }}>
            <Typography variant="h6">Calendar View</Typography>
            <Typography variant="h6" color="text.secondary">
              - {formatMonth(currentMonth)}
            </Typography>
          </Box>

          {/* Weekday headers */}
          <Box
            sx={{
              display: 'grid',
              gridTemplateColumns: 'repeat(7, 1fr)',
              gap: 0,
              mb: 0
            }}
          >
            {weekDays.map(day => (
              <Box
                key={day}
                sx={{
                  textAlign: 'center',
                  py: 1.5,
                  bgcolor: 'grey.100',
                  border: 1,
                  borderColor: 'divider',
                  borderBottom: 0,
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center'
                }}
              >
                <Typography variant="caption" fontWeight="bold">
                  {day}
                </Typography>
              </Box>
            ))}
          </Box>

          {/* Calendar grid */}
          <Box
            sx={{
              display: 'grid',
              gridTemplateColumns: 'repeat(7, 1fr)',
              gap: 0
            }}
          >
            {calendarCells}
          </Box>

          {/* Legend */}
          <Box sx={{ mt: 2, display: 'flex', gap: 2, flexWrap: 'wrap' }}>
            <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
              <Box sx={{ width: 16, height: 16, bgcolor: 'rgba(76, 175, 80, 0.1)', border: 1, borderColor: 'divider' }} />
              <Typography variant="caption">Holiday</Typography>
            </Box>
          </Box>
        </CardContent>
      </Card>
    );
  };

  return (
    <Box sx={{ p: 3 }}>
      <Card>
        <CardHeader
          title="Holiday Settings"
          subheader="Manage holidays across different countries"
          action={
            <Box sx={{ display: 'flex', gap: 1 }}>
              <Button
                variant="outlined"
                startIcon={<UploadIcon />}
                component="label"
                size="small"
              >
                Import
                <input
                  type="file"
                  hidden
                  accept=".xlsx,.xls"
                  onChange={handleImport}
                />
              </Button>
              <Button
                variant="outlined"
                startIcon={<DownloadIcon />}
                onClick={handleExport}
                size="small"
              >
                Export
              </Button>
            </Box>
          }
        />
        <CardContent>
          <Box sx={{ mb: 3 }}>
            <TextField
              label="Select Month"
              type="month"
              value={monthInput}
              onChange={handleMonthChange}
              InputLabelProps={{
                shrink: true,
              }}
              sx={{ minWidth: 200 }}
            />
          </Box>

          <HolidayCalendarView
            currentMonth={currentMonth}
            holidays={holidays}
            onDayClick={handleDayClick}
            onDeleteHoliday={handleDeleteHoliday}
          />

          <Box sx={{ mt: 2, p: 2, bgcolor: 'rgba(76, 175, 80, 0.1)', border: 1, borderColor: 'success.main', borderRadius: 1 }}>
            <Typography variant="body2" color="success.dark">
              Click on any date to add a holiday. Green highlighted dates are holidays.
            </Typography>
          </Box>
        </CardContent>
      </Card>

      <HolidayDialog
        open={showDialog}
        onClose={() => {
          setShowDialog(false);
          setSelectedDate(null);
          setSelectedHoliday(null);
        }}
        date={selectedDate}
        holiday={selectedHoliday}
        onSave={handleSaveHoliday}
      />

      {/* Delete confirmation dialog */}
      <Dialog
        open={showDeleteConfirm}
        onClose={() => {
          setShowDeleteConfirm(false);
          setHolidayToDelete(null);
        }}
      >
        <DialogTitle>Confirm Delete</DialogTitle>
        <DialogContent>
          <DialogContentText>
            Are you sure you want to delete the holiday "{holidayToDelete?.holiday?.name || holidayToDelete?.dateStr}"?
            This action cannot be undone.
          </DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => {
            setShowDeleteConfirm(false);
            setHolidayToDelete(null);
          }} color="inherit">
            Cancel
          </Button>
          <Button onClick={confirmDeleteHoliday} color="error" variant="contained">
            Delete
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
};

export default HolidaySettingTab;

