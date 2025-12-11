import React, { useState, useMemo } from 'react';
import {
  Card,
  CardContent,
  CardHeader,
  Box,
  Select,
  MenuItem,
  FormControl,
  InputLabel,
  TextField,
  Button,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  Typography,
  Chip
} from '@mui/material';
import { Download as DownloadIcon } from '@mui/icons-material';
import { toast } from 'react-toastify';
import { mockIndividualReportData } from '../../data/timecard/data/mockData';
import ExportProgress from '../../components/reports/ExportProgress';

/**
 * IndividualTimecardReport Component
 * Individual timecard summary report by form
 */
const IndividualTimecardReport = () => {
  const [filters, setFilters] = useState({
    staffId: 'all',
    month: '2025-11',
    status: 'all'
  });
  const [showExportProgress, setShowExportProgress] = useState(false);

  const filteredData = useMemo(() => {
    return mockIndividualReportData.filter(item => {
      if (filters.staffId !== 'all' && item.staffId !== filters.staffId) {
        return false;
      }
      if (filters.month && item.timecardMonth !== filters.month) {
        return false;
      }
      if (filters.status !== 'all' && item.timecardStatus !== filters.status) {
        return false;
      }
      return true;
    });
  }, [filters]);

  const handleFilterChange = (field) => (event) => {
    setFilters({
      ...filters,
      [field]: event.target.value
    });
  };

  const handleExport = () => {
    setShowExportProgress(true);
    setTimeout(() => {
      setShowExportProgress(false);
      toast.success('Report exported successfully');
    }, 2000);
  };

  const formatDateTime = (dateTimeStr) => {
    if (!dateTimeStr) return '-';
    const date = new Date(dateTimeStr);
    return date.toLocaleString('en-US', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit'
    });
  };

  return (
    <Card>
      <CardHeader
        title="Individual Timecard Summary Report"
        action={
          <Button
            variant="outlined"
            startIcon={<DownloadIcon />}
            onClick={handleExport}
            size="small"
          >
            Export Report
          </Button>
        }
      />
      <CardContent>
        {/* Filters */}
        <Box sx={{ display: 'grid', gridTemplateColumns: { xs: '1fr', md: 'repeat(3, 1fr)' }, gap: 2, mb: 3 }}>
          <FormControl size="small" fullWidth>
            <InputLabel>Staff ID</InputLabel>
            <Select
              value={filters.staffId}
              onChange={handleFilterChange('staffId')}
              label="Staff ID"
            >
              <MenuItem value="all">All Staff</MenuItem>
              <MenuItem value="EMP001">EMP001 - Wei Zhang</MenuItem>
              <MenuItem value="EMP002">EMP002 - John Smith</MenuItem>
              <MenuItem value="EMP003">EMP003 - Sarah Johnson</MenuItem>
            </Select>
          </FormControl>

          <TextField
            label="Month"
            type="month"
            value={filters.month}
            onChange={handleFilterChange('month')}
            InputLabelProps={{ shrink: true }}
            size="small"
            fullWidth
          />

          <FormControl size="small" fullWidth>
            <InputLabel>Status</InputLabel>
            <Select
              value={filters.status}
              onChange={handleFilterChange('status')}
              label="Status"
            >
              <MenuItem value="all">All Status</MenuItem>
              <MenuItem value="DRAFT">Draft</MenuItem>
              <MenuItem value="SUBMITTED">Submitted</MenuItem>
              <MenuItem value="APPROVED">Approved</MenuItem>
            </Select>
          </FormControl>
        </Box>

        {/* Table */}
        <TableContainer component={Paper} variant="outlined">
          <Table>
            <TableHead>
              <TableRow>
                <TableCell>Form ID</TableCell>
                <TableCell>Timecard Status</TableCell>
                <TableCell>Submission Time</TableCell>
                <TableCell>Final Approval Time</TableCell>
                <TableCell>Staff ID</TableCell>
                <TableCell>Staff Name (Chinese)</TableCell>
                <TableCell>Staff Name</TableCell>
                <TableCell>Staff Type</TableCell>
                <TableCell>Timecard Month</TableCell>
                <TableCell>ITD-SZ|SH Supervisor</TableCell>
                <TableCell>Final Approver</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {filteredData.length === 0 ? (
                <TableRow>
                  <TableCell colSpan={11} align="center" sx={{ py: 4 }}>
                    <Typography color="text.secondary">No data available</Typography>
                  </TableCell>
                </TableRow>
              ) : (
                filteredData.map((row, index) => (
                  <TableRow key={index} hover>
                    <TableCell>{row.formId}</TableCell>
                    <TableCell>
                      <Chip
                        label={row.timecardStatus}
                        size="small"
                        variant={row.timecardStatus === 'APPROVED' ? 'default' : 'secondary'}
                      />
                    </TableCell>
                    <TableCell>{formatDateTime(row.submissionTime)}</TableCell>
                    <TableCell>{formatDateTime(row.finalApprovalTime)}</TableCell>
                    <TableCell>{row.staffId}</TableCell>
                    <TableCell>{row.staffNameChinese}</TableCell>
                    <TableCell>{row.staffName}</TableCell>
                    <TableCell>{row.staffType}</TableCell>
                    <TableCell>{row.timecardMonth}</TableCell>
                    <TableCell>{row.supervisor}</TableCell>
                    <TableCell>{row.finalApprover}</TableCell>
                  </TableRow>
                ))
              )}
            </TableBody>
          </Table>
        </TableContainer>
      </CardContent>

      <ExportProgress
        open={showExportProgress}
        onClose={() => setShowExportProgress(false)}
        reportType="Individual Report"
      />
    </Card>
  );
};

export default IndividualTimecardReport;

