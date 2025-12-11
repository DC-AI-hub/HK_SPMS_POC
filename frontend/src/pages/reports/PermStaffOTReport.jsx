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
import { mockOTReportData } from '../../data/timecard/data/mockData';
import ExportProgress from '../../components/reports/ExportProgress';

/**
 * PermStaffOTReport Component
 * Permanent Staff Overtime Record Report
 */
const PermStaffOTReport = () => {
  const [filters, setFilters] = useState({
    staffType: 'PERMANENT',
    month: '2025-11',
    claimType: 'all'
  });
  const [showExportProgress, setShowExportProgress] = useState(false);

  const filteredData = useMemo(() => {
    return mockOTReportData.filter(item => {
      if (filters.staffType !== 'ALL' && item.staffType !== filters.staffType) {
        return false;
      }
      if (filters.month && item.timecardMonth !== filters.month) {
        return false;
      }
      if (filters.claimType !== 'all' && item.claimType !== filters.claimType) {
        return false;
      }
      return true;
    });
  }, [filters]);

  const totalHours = useMemo(() => {
    return filteredData.reduce((sum, row) => sum + row.manHours, 0);
  }, [filteredData]);

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

  const getClaimTypeLabel = (claimType) => {
    return claimType.replace(/_/g, ' ');
  };

  return (
    <Card>
      <CardHeader
        title="Perm Staff OT Record Report"
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
            <InputLabel>Staff Type</InputLabel>
            <Select
              value={filters.staffType}
              onChange={handleFilterChange('staffType')}
              label="Staff Type"
            >
              <MenuItem value="ALL">All Staff</MenuItem>
              <MenuItem value="Permanent">Permanent Staff</MenuItem>
              <MenuItem value="Vendor">Vendor Staff</MenuItem>
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
            <InputLabel>Claim Type</InputLabel>
            <Select
              value={filters.claimType}
              onChange={handleFilterChange('claimType')}
              label="Claim Type"
            >
              <MenuItem value="all">All Claim Types</MenuItem>
              <MenuItem value="OT_WORKING_DAYS">OT - Working Days</MenuItem>
              <MenuItem value="OT_HOLIDAYS">OT - Holidays</MenuItem>
              <MenuItem value="OT_STATUTORY_HOLIDAYS">OT - Statutory Holidays</MenuItem>
            </Select>
          </FormControl>
        </Box>

        {/* Table */}
        <TableContainer component={Paper} variant="outlined">
          <Table>
            <TableHead>
              <TableRow>
                <TableCell>Form ID</TableCell>
                <TableCell>Submission Time</TableCell>
                <TableCell>Timecard Status</TableCell>
                <TableCell>Final Approval Time</TableCell>
                <TableCell>Staff ID</TableCell>
                <TableCell>Staff Name (Chinese)</TableCell>
                <TableCell>Staff Name</TableCell>
                <TableCell>ITD-SZ|SH Team</TableCell>
                <TableCell>Timecard Month</TableCell>
                <TableCell>Project Code/Charge Code</TableCell>
                <TableCell>Adjustment Month</TableCell>
                <TableCell>Claim Type</TableCell>
                <TableCell>Staff Type</TableCell>
                <TableCell>ITD-SZ|SH Supervisor</TableCell>
                <TableCell align="right">Man-Hours</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {filteredData.length === 0 ? (
                <TableRow>
                  <TableCell colSpan={15} align="center" sx={{ py: 4 }}>
                    <Typography color="text.secondary">No data available</Typography>
                  </TableCell>
                </TableRow>
              ) : (
                <>
                  {filteredData.map((row, index) => (
                    <TableRow key={index} hover>
                      <TableCell>{row.formId}</TableCell>
                      <TableCell>{formatDateTime(row.submissionTime)}</TableCell>
                      <TableCell>
                        <Chip
                          label={row.timecardStatus}
                          size="small"
                          variant={row.timecardStatus === 'APPROVED' ? 'default' : 'secondary'}
                        />
                      </TableCell>
                      <TableCell>{formatDateTime(row.finalApprovalTime)}</TableCell>
                      <TableCell>{row.staffId}</TableCell>
                      <TableCell>{row.staffNameChinese}</TableCell>
                      <TableCell>{row.staffName}</TableCell>
                      <TableCell>{row.team}</TableCell>
                      <TableCell>{row.timecardMonth}</TableCell>
                      <TableCell>{row.projectCode}</TableCell>
                      <TableCell>{row.adjustmentMonth || '-'}</TableCell>
                      <TableCell>
                        <Chip
                          label={getClaimTypeLabel(row.claimType)}
                          size="small"
                          variant="default"
                        />
                      </TableCell>
                      <TableCell>{row.staffType}</TableCell>
                      <TableCell>{row.supervisor}</TableCell>
                      <TableCell align="right">{row.manHours}</TableCell>
                    </TableRow>
                  ))}
                  {/* Total row */}
                  <TableRow sx={{ bgcolor: 'grey.50' }}>
                    <TableCell colSpan={14} align="right">
                      <strong>Total</strong>
                    </TableCell>
                    <TableCell align="right">
                      <strong>{totalHours}</strong>
                    </TableCell>
                  </TableRow>
                </>
              )}
            </TableBody>
          </Table>
        </TableContainer>
      </CardContent>

      <ExportProgress
        open={showExportProgress}
        onClose={() => setShowExportProgress(false)}
        reportType="OT Record Report"
      />
    </Card>
  );
};

export default PermStaffOTReport;

