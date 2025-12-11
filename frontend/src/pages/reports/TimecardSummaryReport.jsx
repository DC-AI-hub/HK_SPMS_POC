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
import { mockSummaryReportData } from '../../data/timecard/data/mockData';
import ExportProgress from '../../components/reports/ExportProgress';

/**
 * TimecardSummaryReport Component
 * Year-To-Date summary report by project + task + activity
 */
const TimecardSummaryReport = () => {
  const [filters, setFilters] = useState({
    year: 2025,
    staffType: 'all',
    claimType: 'all'
  });
  const [showExportProgress, setShowExportProgress] = useState(false);

  const filteredData = useMemo(() => {
    return mockSummaryReportData.filter(item => {
      if (filters.staffType !== 'all' && item.staffType !== filters.staffType) {
        return false;
      }
      if (filters.claimType !== 'all' && item.claimType !== filters.claimType) {
        return false;
      }
      return true;
    });
  }, [filters]);

  const totalHours = useMemo(() => {
    return filteredData.reduce((sum, row) => sum + row.ytdManHours, 0);
  }, [filteredData]);

  const handleFilterChange = (field) => (event) => {
    setFilters({
      ...filters,
      [field]: event.target.value
    });
  };

  const handleExport = () => {
    setShowExportProgress(true);
    // Simulate export
    setTimeout(() => {
      setShowExportProgress(false);
      toast.success('Report exported successfully');
    }, 2000);
  };

  const getClaimTypeLabel = (claimType) => {
    return claimType.replace(/_/g, ' ');
  };

  return (
    <Card>
      <CardHeader
        title="Timecard Summary Report (YTD)"
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
            <InputLabel>Year</InputLabel>
            <Select
              value={filters.year}
              onChange={handleFilterChange('year')}
              label="Year"
            >
              <MenuItem value={2025}>2025</MenuItem>
              <MenuItem value={2024}>2024</MenuItem>
              <MenuItem value={2023}>2023</MenuItem>
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

          <FormControl size="small" fullWidth>
            <InputLabel>Claim Type</InputLabel>
            <Select
              value={filters.claimType}
              onChange={handleFilterChange('claimType')}
              label="Claim Type"
            >
              <MenuItem value="all">All Claim Types</MenuItem>
              <MenuItem value="NORMAL">Normal</MenuItem>
              <MenuItem value="OT_WORKING_DAYS">OT - Working Days</MenuItem>
              <MenuItem value="OT_HOLIDAYS">OT - Holidays</MenuItem>
            </Select>
          </FormControl>
        </Box>

        {/* Table */}
        <TableContainer component={Paper} variant="outlined">
          <Table>
            <TableHead>
              <TableRow>
                <TableCell>Project Code/Charge Code</TableCell>
                <TableCell>Task</TableCell>
                <TableCell>Activity</TableCell>
                <TableCell>Claim Type</TableCell>
                <TableCell>Staff Type</TableCell>
                <TableCell align="right">Year-To-Date Man-Hours</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {filteredData.length === 0 ? (
                <TableRow>
                  <TableCell colSpan={6} align="center" sx={{ py: 4 }}>
                    <Typography color="text.secondary">No data available</Typography>
                  </TableCell>
                </TableRow>
              ) : (
                <>
                  {filteredData.map((row, index) => (
                    <TableRow key={index} hover>
                      <TableCell>{row.projectCode}</TableCell>
                      <TableCell>{row.task}</TableCell>
                      <TableCell>{row.activity}</TableCell>
                      <TableCell>
                        <Chip
                          label={getClaimTypeLabel(row.claimType)}
                          size="small"
                          variant={row.claimType === 'NORMAL' ? 'secondary' : 'default'}
                        />
                      </TableCell>
                      <TableCell>{row.staffType}</TableCell>
                      <TableCell align="right">{row.ytdManHours.toLocaleString()}</TableCell>
                    </TableRow>
                  ))}
                  {/* Total row */}
                  <TableRow sx={{ bgcolor: 'grey.50' }}>
                    <TableCell colSpan={5} align="right">
                      <strong>Total</strong>
                    </TableCell>
                    <TableCell align="right">
                      <strong>{totalHours.toLocaleString()}</strong>
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
        reportType="Summary Report"
      />
    </Card>
  );
};

export default TimecardSummaryReport;

