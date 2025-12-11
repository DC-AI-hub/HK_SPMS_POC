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
import { mockDetailsReportData } from '../../data/timecard/data/mockData';
import ExportProgress from '../../components/reports/ExportProgress';

/**
 * TimecardDetailsReport Component
 * Detailed timecard report with all business fields
 */
const TimecardDetailsReport = () => {
  const [filters, setFilters] = useState({
    department: 'all',
    project: 'all',
    staffType: 'all',
    dateFrom: '2025-11-01',
    dateTo: '2025-11-30'
  });
  const [showExportProgress, setShowExportProgress] = useState(false);

  const filteredData = useMemo(() => {
    return mockDetailsReportData.filter(item => {
      if (filters.department !== 'all' && item.department !== filters.department) {
        return false;
      }
      if (filters.project !== 'all' && item.projectCode !== filters.project) {
        return false;
      }
      if (filters.staffType !== 'all' && item.staffType !== filters.staffType) {
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
        title="Timecard Details Report"
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
        <Box sx={{ display: 'grid', gridTemplateColumns: { xs: '1fr', md: 'repeat(2, 1fr)', lg: 'repeat(5, 1fr)' }, gap: 2, mb: 3 }}>
          <FormControl size="small" fullWidth>
            <InputLabel>Department</InputLabel>
            <Select
              value={filters.department}
              onChange={handleFilterChange('department')}
              label="Department"
            >
              <MenuItem value="all">All Departments</MenuItem>
              <MenuItem value="IT">IT</MenuItem>
              <MenuItem value="Infra">Infra</MenuItem>
            </Select>
          </FormControl>

          <FormControl size="small" fullWidth>
            <InputLabel>Project</InputLabel>
            <Select
              value={filters.project}
              onChange={handleFilterChange('project')}
              label="Project"
            >
              <MenuItem value="all">All Projects</MenuItem>
              <MenuItem value="PROJ001">PROJ001</MenuItem>
              <MenuItem value="PROJ002">PROJ002</MenuItem>
            </Select>
          </FormControl>

          <FormControl size="small" fullWidth>
            <InputLabel>Staff Type</InputLabel>
            <Select
              value={filters.staffType}
              onChange={handleFilterChange('staffType')}
              label="Staff Type"
            >
              <MenuItem value="all">All Types</MenuItem>
              <MenuItem value="Permanent">Permanent</MenuItem>
              <MenuItem value="Vendor">Vendor</MenuItem>
            </Select>
          </FormControl>

          <TextField
            label="Date From"
            type="date"
            value={filters.dateFrom}
            onChange={handleFilterChange('dateFrom')}
            InputLabelProps={{ shrink: true }}
            size="small"
            fullWidth
          />

          <TextField
            label="Date To"
            type="date"
            value={filters.dateTo}
            onChange={handleFilterChange('dateTo')}
            InputLabelProps={{ shrink: true }}
            size="small"
            fullWidth
          />
        </Box>

        {/* Table with horizontal scroll */}
        <Box sx={{ overflowX: 'auto' }}>
          <TableContainer component={Paper} variant="outlined">
            <Table sx={{ minWidth: 2000 }}>
              <TableHead>
                <TableRow>
                  <TableCell>Form ID</TableCell>
                  <TableCell>Staff ID</TableCell>
                  <TableCell>Staff Name (Chinese)</TableCell>
                  <TableCell>Staff Name</TableCell>
                  <TableCell>ITD-SZ|SH Team</TableCell>
                  <TableCell>Timecard Month</TableCell>
                  <TableCell>Code Type</TableCell>
                  <TableCell>Project Code/Charge Code</TableCell>
                  <TableCell>Project Name/Charge Name</TableCell>
                  <TableCell>Task</TableCell>
                  <TableCell>Activity</TableCell>
                  <TableCell>Claim Type</TableCell>
                  <TableCell>Adjustment Month</TableCell>
                  <TableCell>Department</TableCell>
                  <TableCell>Charge Grade</TableCell>
                  <TableCell>Staff Type</TableCell>
                  <TableCell>ITD-SZ|SH Supervisor</TableCell>
                  <TableCell>Final Approver</TableCell>
                  <TableCell align="right">Man-Hours</TableCell>
                  <TableCell>Submission Time</TableCell>
                  <TableCell>Timecard Status</TableCell>
                  <TableCell>Final Approval Time</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {filteredData.length === 0 ? (
                  <TableRow>
                    <TableCell colSpan={22} align="center" sx={{ py: 4 }}>
                      <Typography color="text.secondary">No data available</Typography>
                    </TableCell>
                  </TableRow>
                ) : (
                  <>
                    {filteredData.map((row, index) => (
                      <TableRow key={index} hover>
                        <TableCell>{row.formId}</TableCell>
                        <TableCell>{row.staffId}</TableCell>
                        <TableCell>{row.staffNameChinese}</TableCell>
                        <TableCell>{row.staffName}</TableCell>
                        <TableCell>{row.team}</TableCell>
                        <TableCell>{row.timecardMonth}</TableCell>
                        <TableCell>{row.codeType}</TableCell>
                        <TableCell>{row.projectCode}</TableCell>
                        <TableCell>{row.projectName}</TableCell>
                        <TableCell>{row.task}</TableCell>
                        <TableCell>{row.activity}</TableCell>
                        <TableCell>
                          <Chip
                            label={getClaimTypeLabel(row.claimType)}
                            size="small"
                            variant={row.claimType === 'NORMAL' ? 'secondary' : 'default'}
                          />
                        </TableCell>
                        <TableCell>{row.adjustmentMonth || '-'}</TableCell>
                        <TableCell>{row.department}</TableCell>
                        <TableCell>{row.chargeGrade}</TableCell>
                        <TableCell>{row.staffType}</TableCell>
                        <TableCell>{row.supervisor}</TableCell>
                        <TableCell>{row.finalApprover}</TableCell>
                        <TableCell align="right">{row.manHours}</TableCell>
                        <TableCell>{formatDateTime(row.submissionTime)}</TableCell>
                        <TableCell>
                          <Chip
                            label={row.timecardStatus}
                            size="small"
                            variant={row.timecardStatus === 'APPROVED' ? 'default' : 'secondary'}
                          />
                        </TableCell>
                        <TableCell>{formatDateTime(row.finalApprovalTime)}</TableCell>
                      </TableRow>
                    ))}
                    {/* Total row */}
                    <TableRow sx={{ bgcolor: 'grey.50' }}>
                      <TableCell colSpan={18} align="right">
                        <strong>Total</strong>
                      </TableCell>
                      <TableCell align="right">
                        <strong>{totalHours}</strong>
                      </TableCell>
                      <TableCell colSpan={3} />
                    </TableRow>
                  </>
                )}
              </TableBody>
            </Table>
          </TableContainer>
        </Box>
      </CardContent>

      <ExportProgress
        open={showExportProgress}
        onClose={() => setShowExportProgress(false)}
        reportType="Details Report"
      />
    </Card>
  );
};

export default TimecardDetailsReport;

