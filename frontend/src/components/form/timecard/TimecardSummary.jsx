import React from 'react';
import {
  Card,
  CardContent,
  Typography,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  Alert,
  Box
} from '@mui/material';

/**
 * Timecard Summary Component
 * Displays aggregated timecard data with overtime warning
 */
const TimecardSummary = ({ entries }) => {
  // Aggregate entries by project code + task number + activity
  const getSummaryData = () => {
    const summaryMap = new Map();

    entries.forEach(entry => {
      const key = `${entry.projectCode}|${entry.taskNumber}|${entry.activity}`;
      if (summaryMap.has(key)) {
        const existing = summaryMap.get(key);
        existing.totalHours += entry.hours;
      } else {
        summaryMap.set(key, {
          projectCode: entry.projectCode,
          projectName: entry.projectName,
          taskNumber: entry.taskNumber,
          activity: entry.activity,
          totalHours: entry.hours
        });
      }
    });

    return Array.from(summaryMap.values());
  };

  const summaryData = getSummaryData();
  const totalHours = summaryData.reduce((sum, item) => sum + item.totalHours, 0);

  // Check for overtime warning (160 hours threshold)
  const hasOvertimeWarning = totalHours > 160;

  return (
    <Card>
      <CardContent>
        <Typography variant="h6" gutterBottom>
          Timecard Summary
        </Typography>

        {/* Overtime Warning */}
        {hasOvertimeWarning && (
          <Alert severity="warning" sx={{ mb: 2 }}>
            Total hours ({totalHours.toFixed(1)}h) exceed 160 hours for this month.
            Please review your timecard entries.
          </Alert>
        )}

        {/* Summary Table */}
        {summaryData.length > 0 ? (
          <TableContainer component={Paper} variant="outlined">
            <Table>
              <TableHead>
                <TableRow>
                  <TableCell sx={{ fontWeight: 'bold' }}>Project Code</TableCell>
                  <TableCell sx={{ fontWeight: 'bold' }}>Project Name</TableCell>
                  <TableCell sx={{ fontWeight: 'bold' }}>Task Number</TableCell>
                  <TableCell sx={{ fontWeight: 'bold' }}>Activity</TableCell>
                  <TableCell align="right" sx={{ fontWeight: 'bold' }}>Total Hours</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {summaryData.map((item, index) => (
                  <TableRow key={index}>
                    <TableCell>{item.projectCode}</TableCell>
                    <TableCell>{item.projectName}</TableCell>
                    <TableCell>{item.taskNumber}</TableCell>
                    <TableCell>{item.activity}</TableCell>
                    <TableCell align="right">
                      {item.totalHours.toFixed(1)}
                    </TableCell>
                  </TableRow>
                ))}
                {/* Total Row */}
                <TableRow sx={{ bgcolor: 'grey.50' }}>
                  <TableCell colSpan={4} sx={{ fontWeight: 'bold' }}>
                    Total
                  </TableCell>
                  <TableCell align="right" sx={{ fontWeight: 'bold' }}>
                    {totalHours.toFixed(1)}
                  </TableCell>
                </TableRow>
              </TableBody>
            </Table>
          </TableContainer>
        ) : (
          <Box sx={{ textAlign: 'center', py: 4 }}>
            <Typography color="text.secondary">
              No timecard entries yet
            </Typography>
          </Box>
        )}
      </CardContent>
    </Card>
  );
};

export default TimecardSummary;