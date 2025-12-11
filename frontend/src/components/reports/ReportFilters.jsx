import React from 'react';
import { Box, TextField, Button } from '@mui/material';
import { DatePicker } from '@mui/x-date-pickers';
import dayjs from 'dayjs';

export default function ReportFilters({ filters, onFiltersChange, onSearch }) {
  const handleFilterChange = (field, value) => {
    onFiltersChange(prev => ({ ...prev, [field]: value }));
  };

  return (
    <Box sx={{ display: 'flex', gap: 2, flexWrap: 'wrap', alignItems: 'center' }}>
      <DatePicker
        label="Report Month"
        value={filters.reportMonth ? dayjs(filters.reportMonth) : null}
        onChange={(date) => handleFilterChange('reportMonth', date?.format('YYYY-MM'))}
        views={['year', 'month']}
        format="YYYY-MM"
        slotProps={{ textField: { size: 'small' } }}
      />
      <TextField size="small" label="Staff Name" value={filters.staffName || ''} onChange={(e)=>handleFilterChange('staffName', e.target.value)} />
      <TextField size="small" label="FormID" type="number" value={filters.formId || ''} onChange={(e)=>handleFilterChange('formId', e.target.value ? Number(e.target.value) : undefined)} sx={{ width: 140 }} />
      <TextField size="small" label="StaffID" type="number" value={filters.staffId || ''} onChange={(e)=>handleFilterChange('staffId', e.target.value ? Number(e.target.value) : undefined)} sx={{ width: 140 }} />
      <TextField size="small" label="Team" value={filters.team || ''} onChange={(e)=>handleFilterChange('team', e.target.value)} />
      <Button variant="contained" onClick={onSearch}>查询</Button>
    </Box>
  );
}
