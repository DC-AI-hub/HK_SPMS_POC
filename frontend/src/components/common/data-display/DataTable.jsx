import React, { useState, useMemo } from 'react';
import {
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  TablePagination,
  TableSortLabel,
  Paper,
  Box,
  Typography,
  IconButton,
  Tooltip,
  Chip
} from '@mui/material';
import {
  FirstPage as FirstPageIcon,
  LastPage as LastPageIcon,
  KeyboardArrowLeft,
  KeyboardArrowRight,
  FilterList as FilterListIcon
} from '@mui/icons-material';

/**
 * DataTable - A generic data table component with sorting, pagination, and filtering
 * Provides consistent data table styling and functionality
 * 
 * @param {Object} props
 * @param {Array} props.columns - Array of column configuration objects
 * @param {Array} props.data - Array of data objects to display
 * @param {number} [props.totalCount] - Total number of items (for server-side pagination)
 * @param {function} [props.onPageChange] - Callback for page changes
 * @param {function} [props.onSortChange] - Callback for sort changes
 * @param {boolean} [props.loading] - Whether data is loading
 * @param {string} [props.emptyMessage] - Message to display when no data is available
 * @param {Object} [props.pagination] - Pagination configuration
 * @param {Object} [props.sorting] - Sorting configuration
 * @param {boolean} [props.selectable] - Whether rows are selectable
 * @param {function} [props.onRowClick] - Callback for row clicks
 * @param {string} [props.size] - Table size ('small' | 'medium')
 * @param {boolean} [props.stickyHeader] - Whether to use sticky header
 */
const DataTable = ({
  columns = [],
  data = [],
  totalCount,
  onPageChange,
  onSortChange,
  loading = false,
  emptyMessage = 'No data available',
  pagination = {
    page: 0,
    rowsPerPage: 10,
    rowsPerPageOptions: [5, 10, 25, 50]
  },
  sorting = {
    field: '',
    order: 'asc'
  },
  selectable = false,
  onRowClick,
  size = 'medium',
  stickyHeader = false,
  ...props
}) => {
  const [page, setPage] = useState(pagination.page || 0);
  const [rowsPerPage, setRowsPerPage] = useState(pagination.rowsPerPage || 10);
  const [orderBy, setOrderBy] = useState(sorting.field || '');
  const [order, setOrder] = useState(sorting.order || 'asc');

  const handleRequestSort = (property) => {
    const isAsc = orderBy === property && order === 'asc';
    setOrder(isAsc ? 'desc' : 'asc');
    setOrderBy(property);
    
    if (onSortChange) {
      onSortChange(property, isAsc ? 'desc' : 'asc');
    }
  };

  const handleChangePage = (event, newPage) => {
    setPage(newPage);
    if (onPageChange) {
      onPageChange(newPage, rowsPerPage);
    }
  };

  const handleChangeRowsPerPage = (event) => {
    const newRowsPerPage = parseInt(event.target.value, 10);
    setRowsPerPage(newRowsPerPage);
    setPage(0);
    
    if (onPageChange) {
      onPageChange(0, newRowsPerPage);
    }
  };

  const sortedData = useMemo(() => {
    if (!orderBy) return data;
    
    return [...data].sort((a, b) => {
      let aValue = a[orderBy];
      let bValue = b[orderBy];
      
      // Handle nested properties
      if (orderBy.includes('.')) {
        aValue = orderBy.split('.').reduce((obj, key) => obj?.[key], a);
        bValue = orderBy.split('.').reduce((obj, key) => obj?.[key], b);
      }
      
      if (aValue === null || aValue === undefined) return 1;
      if (bValue === null || bValue === undefined) return -1;
      
      if (typeof aValue === 'string') {
        return order === 'asc' 
          ? aValue.localeCompare(bValue)
          : bValue.localeCompare(aValue);
      }
      
      return order === 'asc' ? aValue - bValue : bValue - aValue;
    });
  }, [data, orderBy, order]);

  const paginatedData = useMemo(() => {
    if (onPageChange) {
      // Server-side pagination - use the data as is
      return data;
    }
    // Client-side pagination
    return sortedData.slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage);
  }, [sortedData, page, rowsPerPage, onPageChange, data]);

  const totalItems = totalCount !== undefined ? totalCount : data.length;

  return (
    <Paper sx={{ width: '100%', overflow: 'hidden' }} {...props}>
      <TableContainer sx={{ maxHeight: stickyHeader ? 440 : 'auto' }}>
        <Table 
          size={size} 
          stickyHeader={stickyHeader}
          aria-label="data table"
        >
          <TableHead>
            <TableRow>
              {columns.map((column) => (
                <TableCell
                  key={column.field}
                  align={column.align || 'left'}
                  sortDirection={orderBy === column.field ? order : false}
                  sx={{
                    fontWeight: 'bold',
                    backgroundColor: 'background.paper',
                    ...column.headerStyle
                  }}
                >
                  {column.sortable !== false ? (
                    <TableSortLabel
                      active={orderBy === column.field}
                      direction={orderBy === column.field ? order : 'asc'}
                      onClick={() => handleRequestSort(column.field)}
                    >
                      {column.headerName}
                    </TableSortLabel>
                  ) : (
                    column.headerName
                  )}
                  
                  {column.filterable && (
                    <Tooltip title="Filter">
                      <IconButton size="small" sx={{ ml: 0.5 }}>
                        <FilterListIcon fontSize="small" />
                      </IconButton>
                    </Tooltip>
                  )}
                </TableCell>
              ))}
            </TableRow>
          </TableHead>

          <TableBody>
            {loading ? (
              // Loading skeleton
              Array.from({ length: rowsPerPage }).map((_, index) => (
                <TableRow key={`loading-${index}`}>
                  {columns.map((column) => (
                    <TableCell key={column.field} align={column.align || 'left'}>
                      <Box
                        sx={{
                          height: 20,
                          backgroundColor: 'action.hover',
                          borderRadius: 1,
                          animation: 'pulse 1.5s ease-in-out infinite'
                        }}
                      />
                    </TableCell>
                  ))}
                </TableRow>
              ))
            ) : paginatedData.length === 0 ? (
              // Empty state
              <TableRow>
                <TableCell colSpan={columns.length} align="center" sx={{ py: 4 }}>
                  <Typography variant="body2" color="text.secondary">
                    {emptyMessage}
                  </Typography>
                </TableCell>
              </TableRow>
            ) : (
              // Data rows
              paginatedData.map((row, rowIndex) => (
                <TableRow
                  key={row.id || rowIndex}
                  hover={!!onRowClick}
                  onClick={onRowClick ? () => onRowClick(row) : undefined}
                  sx={{
                    cursor: onRowClick ? 'pointer' : 'default',
                    '&:last-child td, &:last-child th': { border: 0 }
                  }}
                >
                  {columns.map((column) => {
                    let cellValue = row[column.field];
                    
                    // Handle nested properties
                    if (column.field.includes('.')) {
                      cellValue = column.field.split('.').reduce((obj, key) => obj?.[key], row);
                    }
                    
                    // Apply value formatter if provided
                    if (column.valueFormatter) {
                      cellValue = column.valueFormatter(cellValue, row);
                    }
                    
                    return (
                      <TableCell
                        key={column.field}
                        align={column.align || 'left'}
                        sx={column.cellStyle}
                      >
                        {column.renderCell ? (
                          column.renderCell(cellValue, row)
                        ) : (
                          <Typography variant="body2">
                            {cellValue ?? '-'}
                          </Typography>
                        )}
                      </TableCell>
                    );
                  })}
                </TableRow>
              ))
            )}
          </TableBody>
        </Table>
      </TableContainer>

      {(pagination.rowsPerPageOptions?.length > 0 || totalItems > 10) && (
        <TablePagination
          rowsPerPageOptions={pagination.rowsPerPageOptions}
          component="div"
          count={totalItems}
          rowsPerPage={rowsPerPage}
          page={page}
          onPageChange={handleChangePage}
          onRowsPerPageChange={handleChangeRowsPerPage}
          sx={{
            borderTop: '1px solid',
            borderTopColor: 'divider'
          }}
        />
      )}
    </Paper>
  );
};

export default DataTable;
