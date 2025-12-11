import React from 'react';
import { Box, Pagination, FormControl, Select, MenuItem, Typography } from '@mui/material';

export default function ReportPagination({ pagination, onPageChange, onSizeChange }) {
  const handlePageChange = (event, page) => {
    onPageChange(page - 1);
  };

  const handleSizeChange = (event) => {
    onSizeChange(event.target.value);
  };

  const totalPages = Math.ceil((pagination.total || 0) / (pagination.size || 10));

  return (
    <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mt: 2 }}>
      <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
        <Typography variant="body2" color="text.secondary">
          共 {pagination.total || 0} 条记录
        </Typography>
        <FormControl size="small" sx={{ minWidth: 80 }}>
          <Select value={pagination.size || 10} onChange={handleSizeChange}>
            <MenuItem value={10}>10条/页</MenuItem>
            <MenuItem value={20}>20条/页</MenuItem>
            <MenuItem value={50}>50条/页</MenuItem>
            <MenuItem value={100}>100条/页</MenuItem>
          </Select>
        </FormControl>
      </Box>

      {totalPages > 1 && (
        <Pagination count={totalPages} page={(pagination.page || 0) + 1} onChange={handlePageChange} color="primary" size="medium" />
      )}
    </Box>
  );
}


