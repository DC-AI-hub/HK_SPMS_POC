import React from 'react';
import { Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Paper, CircularProgress, Box, Typography } from '@mui/material';

export default function ReportTable({ columns, data, loading, emptyMessage = '暂无数据', onRowClick }) {
  if (loading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', p: 4 }}>
        <CircularProgress />
      </Box>
    );
  }

  return (
    <TableContainer component={Paper}>
      <Table stickyHeader>
        <TableHead>
          <TableRow>
            {columns.map((column) => (
              <TableCell key={column.id} sx={{ width: column.width, fontWeight: 'bold', textAlign: column.align || 'left' }}>
                {column.label}
              </TableCell>
            ))}
          </TableRow>
        </TableHead>
        <TableBody>
          {(!data || data.length === 0) ? (
            <TableRow>
              <TableCell colSpan={columns.length} sx={{ textAlign: 'center', color: 'text.secondary' }}>
                {emptyMessage}
              </TableCell>
            </TableRow>
          ) : data.map((row, index) => (
            <TableRow key={row.id || index} hover sx={{ cursor: onRowClick ? 'pointer' : 'default' }} onClick={onRowClick ? () => onRowClick(row) : undefined}>
              {columns.map((column) => (
                <TableCell key={column.id} sx={{ textAlign: column.align || 'left' }}>
                  {column.render ? column.render(row[column.id], row) : (row[column.id] ?? '-')}
                </TableCell>
              ))}
            </TableRow>
          ))}
        </TableBody>
      </Table>
    </TableContainer>
  );
}


