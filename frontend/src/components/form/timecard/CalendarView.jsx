import React, { useState } from 'react';
import {
  Card,
  CardContent,
  Typography,
  Box,
  IconButton,
  Chip
} from '@mui/material';
import {
  ContentCopy as CopyIcon,
  Delete as DeleteIcon
} from '@mui/icons-material';
import { toast } from 'react-toastify';

// Import mock data
import { PROJECT_COLORS, PROJECT_COLORS_BORDER } from '../../../data/timecard/data/mockData';

// Import sub-components
import ProjectEntryDialog from './ProjectEntryDialog';
import BulkCopyDialog from './BulkCopyDialog';

/**
 * Calendar View Component
 * Monthly calendar display with timecard entries and interactive features
 */
const CalendarView = ({ currentMonth, entries, holidays, onEntriesUpdate }) => {
  const [selectedDate, setSelectedDate] = useState(null);
  const [showEntryDialog, setShowEntryDialog] = useState(false);
  const [showBulkCopyDialog, setShowBulkCopyDialog] = useState(false);
  const [copySourceDate, setCopySourceDate] = useState(null);

  // Calendar configuration
  const weekDays = ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'];
  const year = currentMonth.getFullYear();
  const month = currentMonth.getMonth();

  // Get days in month
  const daysInMonth = new Date(year, month + 1, 0).getDate();
  const startingDayOfWeek = new Date(year, month, 1).getDay();

  // Helper functions
  const getEntriesForDate = (dateStr) => {
    return entries.filter(entry => entry.date === dateStr);
  };

  const getTotalHoursForDate = (dateStr) => {
    const dateEntries = getEntriesForDate(dateStr);
    return dateEntries.reduce((sum, entry) => sum + entry.hours, 0);
  };

  const getProjectColor = (projectCode) => {
    const projectCodes = [...new Set(entries.map(e => e.projectCode))];
    const index = projectCodes.indexOf(projectCode);
    return index >= 0 ? PROJECT_COLORS[index % PROJECT_COLORS.length] : PROJECT_COLORS[0];
  };

  const getProjectBorderColor = (projectCode) => {
    const projectCodes = [...new Set(entries.map(e => e.projectCode))];
    const index = projectCodes.indexOf(projectCode);
    return index >= 0 ? PROJECT_COLORS_BORDER[index % PROJECT_COLORS_BORDER.length] : PROJECT_COLORS_BORDER[0];
  };

  const isHoliday = (dateStr) => {
    return holidays.some(holiday => holiday.date === dateStr);
  };

  const isWeekend = (dayOfWeek) => {
    return dayOfWeek === 0 || dayOfWeek === 6;
  };

  const hasExcessHours = (dateStr) => {
    return getTotalHoursForDate(dateStr) > 8;
  };

  // Event handlers
  const handleDayClick = (dateStr) => {
    setSelectedDate(dateStr);
    setShowEntryDialog(true);
  };

  const handleCopyDay = (dateStr, e) => {
    e.stopPropagation();
    const dateEntries = getEntriesForDate(dateStr);
    if (dateEntries.length === 0) {
      toast.error('No entries to copy for this date');
      return;
    }
    setCopySourceDate(dateStr);
    setShowBulkCopyDialog(true);
  };

  const handleDeleteDay = (dateStr, e) => {
    e.stopPropagation();
    const dateEntries = getEntriesForDate(dateStr);
    if (dateEntries.length === 0) {
      toast.error('No entries to delete for this date');
      return;
    }

    const newEntries = entries.filter(entry => entry.date !== dateStr);
    onEntriesUpdate(newEntries);
    toast.success(`Deleted ${dateEntries.length} entries for ${dateStr}`);
  };

  const handleEntriesUpdate = (dateStr, newEntries) => {
    // Remove existing entries for this date
    const filteredEntries = entries.filter(entry => entry.date !== dateStr);
    // Add new entries
    const updatedEntries = [...filteredEntries, ...newEntries];
    onEntriesUpdate(updatedEntries);
  };

  const handleCopyComplete = (newEntries) => {
    const updatedEntries = [...entries, ...newEntries];
    onEntriesUpdate(updatedEntries);
    setShowBulkCopyDialog(false);
    setCopySourceDate(null);
  };

  // Generate calendar cells
  const calendarCells = [];

  // Empty cells for starting day alignment
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

  // Actual date cells
  for (let day = 1; day <= daysInMonth; day++) {
    const dateStr = `${year}-${String(month + 1).padStart(2, '0')}-${String(day).padStart(2, '0')}`;
    const dayOfWeek = new Date(year, month, day).getDay();
    const dateEntries = getEntriesForDate(dateStr);
    const totalHours = getTotalHoursForDate(dateStr);
    const isHolidayDay = isHoliday(dateStr);
    const isWeekendDay = isWeekend(dayOfWeek);
    const hasExcessHoursDay = hasExcessHours(dateStr);
    
    // Check if this is the first row (day <= 7 - startingDayOfWeek)
    const isFirstRow = day <= (7 - startingDayOfWeek);

    // Determine cell background color - using lighter colors
    let bgColor = 'background.paper';
    if (isHolidayDay) {
      bgColor = 'rgba(76, 175, 80, 0.1)'; // Light green
    } else if (hasExcessHoursDay || (isWeekendDay && dateEntries.length > 0)) {
      bgColor = 'rgba(244, 67, 54, 0.1)'; // Light red
    } else if (isWeekendDay) {
      bgColor = 'grey.100';
    }

    calendarCells.push(
      <Box
        key={dateStr}
        onClick={() => handleDayClick(dateStr)}
        sx={{
          aspectRatio: '1',
          minHeight: 120,
          border: 1,
          borderColor: 'divider',
          borderTop: isFirstRow ? 0 : undefined,
          bgcolor: bgColor,
          p: 1.5,
          cursor: 'pointer',
          position: 'relative',
          display: 'flex',
          flexDirection: 'column',
          '&:hover': {
            borderColor: 'primary.main',
            '& .action-buttons': {
              opacity: 1
            }
          }
        }}
      >
          {/* Action buttons - fixed at top right */}
          {dateEntries.length > 0 && (
            <Box 
              className="action-buttons" 
              sx={{ 
                position: 'absolute',
                top: 4,
                right: 4,
                opacity: 0.6,
                transition: 'opacity 0.2s',
                display: 'flex',
                gap: 0.5,
                zIndex: 1,
                '&:hover': {
                  opacity: 1
                }
              }}
            >
              <IconButton
                size="small"
                onClick={(e) => handleCopyDay(dateStr, e)}
                sx={{ 
                  p: 0.5,
                  bgcolor: 'background.paper',
                  '&:hover': {
                    bgcolor: 'action.hover'
                  }
                }}
              >
                <CopyIcon fontSize="small" />
              </IconButton>
              <IconButton
                size="small"
                onClick={(e) => handleDeleteDay(dateStr, e)}
                sx={{ 
                  p: 0.5, 
                  color: 'error.main',
                  bgcolor: 'background.paper',
                  '&:hover': {
                    bgcolor: 'action.hover'
                  }
                }}
              >
                <DeleteIcon fontSize="small" />
              </IconButton>
            </Box>
          )}

          {/* Date number */}
          <Typography variant="body2" sx={{ fontWeight: 'bold', mb: 0.5 }}>
            {day}
          </Typography>

          {/* Total hours */}
          {totalHours > 0 && (
            <Typography variant="caption" color="text.secondary" sx={{ mb: 0.5 }}>
              {totalHours}h
            </Typography>
          )}

          {/* Project entries - fixed height area */}
          <Box sx={{ 
            flex: 1,
            minHeight: 0,
            overflow: 'hidden',
            display: 'flex',
            flexDirection: 'column',
            gap: 0.5
          }}>
            {dateEntries.slice(0, 2).map((entry, index) => (
              <Chip
                key={entry.id}
                label={`${entry.projectCode} ${entry.hours}h`}
                size="small"
                sx={{
                  fontSize: '0.6rem',
                  height: 20,
                  bgcolor: getProjectColor(entry.projectCode),
                  border: `1px solid ${getProjectBorderColor(entry.projectCode)}`,
                  display: 'block',
                  width: '100%',
                  '& .MuiChip-label': {
                    px: 0.5
                  }
                }}
              />
            ))}
            {dateEntries.length > 2 && (
              <Typography variant="caption" color="text.secondary">
                +{dateEntries.length - 2} more
              </Typography>
            )}
          </Box>

          {/* Status indicators */}
          {isHolidayDay && (
            <Typography
              variant="caption"
              sx={{
                position: 'absolute',
                bottom: 2,
                left: 2,
                color: 'success.dark',
                fontWeight: 'bold'
              }}
            >
              Holiday
            </Typography>
          )}
          {hasExcessHoursDay && !isHolidayDay && (
            <Typography
              variant="caption"
              sx={{
                position: 'absolute',
                bottom: 2,
                left: 2,
                color: 'error.dark',
                fontWeight: 'bold'
              }}
            >
              Excess
            </Typography>
          )}
        </Box>
    );
  }

  // Format month for display
  const formatMonth = (date) => {
    return date.toLocaleDateString('en-US', { year: 'numeric', month: 'long' });
  };

  return (
    <Card>
      <CardContent>
        <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mb: 2 }}>
          <Typography variant="h6">
            Calendar View
          </Typography>
          <Typography variant="h6" color="text.secondary">
            - {formatMonth(currentMonth)}
          </Typography>
        </Box>

        {/* Weekday headers - using CSS Grid for perfect alignment */}
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

        {/* Calendar grid - using CSS Grid for perfect alignment */}
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
          <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
            <Box sx={{ width: 16, height: 16, bgcolor: 'rgba(244, 67, 54, 0.1)', border: 1, borderColor: 'divider' }} />
            <Typography variant="caption">Excess Hours / Weekend Work</Typography>
          </Box>
        </Box>
      </CardContent>

      {/* Dialogs */}
      <ProjectEntryDialog
        open={showEntryDialog}
        onClose={() => setShowEntryDialog(false)}
        date={selectedDate}
        entries={selectedDate ? getEntriesForDate(selectedDate) : []}
        onEntriesUpdate={(newEntries) => handleEntriesUpdate(selectedDate, newEntries)}
      />

      <BulkCopyDialog
        open={showBulkCopyDialog}
        onClose={() => setShowBulkCopyDialog(false)}
        sourceDate={copySourceDate}
        sourceEntries={copySourceDate ? getEntriesForDate(copySourceDate) : []}
        currentMonth={currentMonth}
        holidays={holidays}
        existingEntries={entries}
        onCopyComplete={handleCopyComplete}
      />
    </Card>
  );
};

export default CalendarView;