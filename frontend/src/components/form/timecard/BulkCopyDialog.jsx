import React, { useState, useEffect } from 'react';
import {
  Dialog,
  DialogContent,
  DialogTitle,
  DialogActions,
  Button,
  Grid,
  Typography,
  Box,
  Checkbox,
  FormControlLabel,
  Paper
} from '@mui/material';
import { toast } from 'react-toastify';

/**
 * Bulk Copy Dialog Component
 * Allows copying timecard entries from one date to multiple target dates
 */
const BulkCopyDialog = ({
  open,
  onClose,
  sourceDate,
  sourceEntries,
  currentMonth,
  holidays,
  existingEntries,
  onCopyComplete
}) => {
  const [selectedDates, setSelectedDates] = useState(new Set());

  // Reset selection when dialog opens/closes
  useEffect(() => {
    if (open) {
      setSelectedDates(new Set());
    }
  }, [open]);

  // Generate all dates in the current month
  const getDaysInMonth = () => {
    const year = currentMonth.getFullYear();
    const month = currentMonth.getMonth();
    const daysInMonth = new Date(year, month + 1, 0).getDate();
    const startingDayOfWeek = new Date(year, month, 1).getDay();

    const days = [];
    for (let day = 1; day <= daysInMonth; day++) {
      const dateStr = `${year}-${String(month + 1).padStart(2, '0')}-${String(day).padStart(2, '0')}`;
      const date = new Date(year, month, day);
      days.push({
        date: dateStr,
        day: day,
        dayOfWeek: date.getDay()
      });
    }

    return days;
  };

  const daysInMonth = getDaysInMonth();

  // Helper functions
  const isWeekend = (dayOfWeek) => {
    return dayOfWeek === 0 || dayOfWeek === 6;
  };

  const isHoliday = (dateStr) => {
    return holidays.some(holiday => holiday.date === dateStr);
  };

  const isWorkingDay = (dayOfWeek, dateStr) => {
    return !isWeekend(dayOfWeek) && !isHoliday(dateStr);
  };

  const isBlankDay = (dateStr) => {
    return !existingEntries.some(entry => entry.date === dateStr);
  };

  const isSource = (dateStr) => {
    return dateStr === sourceDate;
  };

  const hasEntries = (dateStr) => {
    return existingEntries.some(entry => entry.date === dateStr);
  };

  // Quick selection handlers
  const handleSelectThisWeek = () => {
    if (!sourceDate) return;

    const sourceDay = new Date(sourceDate).getDay();
    const sourceDateObj = new Date(sourceDate);
    const weekStart = new Date(sourceDateObj);
    weekStart.setDate(sourceDateObj.getDate() - sourceDay);

    const weekDates = new Set();
    for (let i = 0; i < 7; i++) {
      const date = new Date(weekStart);
      date.setDate(weekStart.getDate() + i);
      const dateStr = date.toISOString().split('T')[0];

      // Only select working days in current month, exclude source date
      if (
        date.getMonth() === currentMonth.getMonth() &&
        date.getFullYear() === currentMonth.getFullYear() &&
        isWorkingDay(i, dateStr) &&
        !isSource(dateStr)
      ) {
        weekDates.add(dateStr);
      }
    }

    setSelectedDates(weekDates);
  };

  const handleSelectAllWorkingDays = () => {
    const workingDays = new Set();
    daysInMonth.forEach(day => {
      if (isWorkingDay(day.dayOfWeek, day.date) && !isSource(day.date)) {
        workingDays.add(day.date);
      }
    });
    setSelectedDates(workingDays);
  };

  const handleSelectAllBlankDays = () => {
    const blankDays = new Set();
    daysInMonth.forEach(day => {
      if (
        isBlankDay(day.date) &&
        isWorkingDay(day.dayOfWeek, day.date) &&
        !isSource(day.date)
      ) {
        blankDays.add(day.date);
      }
    });
    setSelectedDates(blankDays);
  };

  const handleClearAll = () => {
    setSelectedDates(new Set());
  };

  // Individual date selection
  const handleDateToggle = (dateStr) => {
    const newSelectedDates = new Set(selectedDates);
    if (newSelectedDates.has(dateStr)) {
      newSelectedDates.delete(dateStr);
    } else {
      newSelectedDates.add(dateStr);
    }
    setSelectedDates(newSelectedDates);
  };

  // Copy operation
  const handleCopy = () => {
    if (selectedDates.size === 0) {
      toast.error('Please select at least one target date');
      return;
    }

    const newEntries = [];
    selectedDates.forEach(targetDate => {
      sourceEntries.forEach(sourceEntry => {
        newEntries.push({
          ...sourceEntry,
          id: `${sourceEntry.id}-${targetDate}-${Date.now()}`,
          date: targetDate
        });
      });
    });

    onCopyComplete(newEntries);
    toast.success(`Copied ${sourceEntries.length} entries to ${selectedDates.size} days`);
  };

  const formatDate = (dateStr) => {
    return new Date(dateStr).toLocaleDateString('en-US', {
      month: 'short',
      day: 'numeric'
    });
  };

  return (
    <Dialog
      open={open}
      onClose={onClose}
      maxWidth="md"
      fullWidth
    >
      <DialogTitle>
        Bulk Copy Timecard Entries
      </DialogTitle>

      <DialogContent>
        <Typography variant="body2" color="text.secondary" gutterBottom>
          Copy {sourceEntries.length} entries from {sourceDate ? formatDate(sourceDate) : ''} to selected dates
        </Typography>

        {/* Quick Selection Buttons */}
        <Box sx={{ mb: 3, display: 'flex', gap: 1, flexWrap: 'wrap' }}>
          <Button
            variant="outlined"
            size="small"
            onClick={handleSelectThisWeek}
          >
            Select This Week
          </Button>
          <Button
            variant="outlined"
            size="small"
            onClick={handleSelectAllWorkingDays}
          >
            Select All Working Days
          </Button>
          <Button
            variant="outlined"
            size="small"
            onClick={handleSelectAllBlankDays}
          >
            Select All Blank Days
          </Button>
          <Button
            variant="outlined"
            size="small"
            onClick={handleClearAll}
            color="error"
          >
            Clear All
          </Button>
        </Box>

        {/* Calendar Grid */}
        <Paper variant="outlined" sx={{ p: 2, maxHeight: 400, overflow: 'auto' }}>
          {/* Weekday headers */}
          <Grid container spacing={1} sx={{ mb: 1 }}>
            {['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'].map(day => (
              <Grid item xs={1} key={day}>
                <Typography
                  variant="caption"
                  fontWeight="bold"
                  align="center"
                  display="block"
                >
                  {day}
                </Typography>
              </Grid>
            ))}
          </Grid>

          {/* Calendar dates */}
          <Grid container spacing={1}>
            {daysInMonth.map(day => {
              const isSourceDay = isSource(day.date);
              const isHolidayDay = isHoliday(day.date);
              const isWeekendDay = isWeekend(day.dayOfWeek);
              const hasEntriesDay = hasEntries(day.date);

              let bgColor = 'background.paper';
              let borderColor = 'divider';

              if (isSourceDay) {
                bgColor = 'primary.light';
                borderColor = 'primary.main';
              } else if (isHolidayDay) {
                bgColor = 'success.light';
              } else if (isWeekendDay) {
                bgColor = 'grey.100';
              }

              if (hasEntriesDay && !isSourceDay) {
                borderColor = 'warning.main';
              }

              return (
                <Grid item xs={1} key={day.date}>
                  <Paper
                    elevation={1}
                    sx={{
                      p: 1,
                      textAlign: 'center',
                      bgcolor: bgColor,
                      border: 2,
                      borderColor: borderColor,
                      cursor: isSourceDay ? 'default' : 'pointer',
                      opacity: isSourceDay ? 0.7 : 1
                    }}
                    onClick={isSourceDay ? undefined : () => handleDateToggle(day.date)}
                  >
                    <Typography variant="body2" fontWeight="bold">
                      {day.day}
                    </Typography>

                    {!isSourceDay && (
                      <Checkbox
                        size="small"
                        checked={selectedDates.has(day.date)}
                        onChange={() => handleDateToggle(day.date)}
                        sx={{ p: 0 }}
                      />
                    )}

                    {/* Status indicators */}
                    <Box sx={{ mt: 0.5 }}>
                      {isSourceDay && (
                        <Typography variant="caption" color="primary.main" fontWeight="bold">
                          Source
                        </Typography>
                      )}
                      {hasEntriesDay && !isSourceDay && (
                        <Typography variant="caption" color="warning.main">
                          Has data
                        </Typography>
                      )}
                    </Box>
                  </Paper>
                </Grid>
              );
            })}
          </Grid>
        </Paper>

        {/* Legend */}
        <Box sx={{ mt: 2, display: 'flex', gap: 2, flexWrap: 'wrap' }}>
          <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.5 }}>
            <Box sx={{ width: 12, height: 12, bgcolor: 'primary.light', border: 2, borderColor: 'primary.main' }} />
            <Typography variant="caption">Source Date</Typography>
          </Box>
          <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.5 }}>
            <Box sx={{ width: 12, height: 12, bgcolor: 'success.light' }} />
            <Typography variant="caption">Holiday</Typography>
          </Box>
          <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.5 }}>
            <Box sx={{ width: 12, height: 12, border: 2, borderColor: 'warning.main' }} />
            <Typography variant="caption">Has Entries</Typography>
          </Box>
        </Box>

        {/* Selection count */}
        <Typography variant="body2" sx={{ mt: 2, fontWeight: 'bold' }}>
          Selected: {selectedDates.size} days
        </Typography>
      </DialogContent>

      <DialogActions>
        <Button onClick={onClose}>
          Cancel
        </Button>
        <Button
          onClick={handleCopy}
          variant="contained"
          disabled={selectedDates.size === 0}
        >
          Copy to {selectedDates.size} Day{selectedDates.size !== 1 ? 's' : ''}
        </Button>
      </DialogActions>
    </Dialog>
  );
};

export default BulkCopyDialog;