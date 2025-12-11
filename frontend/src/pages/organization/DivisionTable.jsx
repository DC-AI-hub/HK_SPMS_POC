import React, { useState } from 'react';
import { DataGrid } from '@mui/x-data-grid';
import { Box, Button, TextField, Stack, IconButton, Tooltip, Menu, MenuItem } from '@mui/material';
import { Add, Delete, MoreVert, Search, MoreHoriz } from '@mui/icons-material';
import { useTranslation } from 'react-i18next';
import ConfirmationDialog from '../../components/ConfirmationDialog';
import { TableColumns } from './constants';
import dayjs from 'dayjs';

const DivisionTable = ({ divisions, loading, onSearch, onCreate, onBulkDelete, onEdit }) => {
  const { t } = useTranslation();
  const [searchText, setSearchText] = useState('');
  const [selectedIds, setSelectedIds] = useState([]);
  const [deleteConfirmOpen, setDeleteConfirmOpen] = useState(false);
  const [operationAnchorEl, setOperationAnchorEl] = useState(null);
  const isOperationMenuOpen = Boolean(operationAnchorEl);

  const columns = [
    { field: 'name', headerName: t('organization:division.name'), width: 200, sortable: false },
    { field: 'type', headerName: t('organization:division.type'), width: 150, sortable: false },
    { 
      field: 'divisionHead', 
      headerName: t('organization:division.head'), 
      width: 200,
      valueGetter: (params) => params?.row?.divisionHead?.name || t('common:none')
    },
    { 
      field: 'active', 
      headerName: t('organization:company.active'), 
      width: 100
    },
    { 
      field: 'lastModified', 
      headerName: t('organization:division.lastModified'), 
      width: 200,
      valueFormatter: (params) => {
        return dayjs(params?? "").format('YYYY-MM-DD HH:mm')
      }
    },
    { field: 'company', headerName: t('organization:division.relatedTo'), width: 200, sortable: false },
    {
      field: 'actions',
      headerName: t(TableColumns.ACTIONS),
      width: 120,
      renderCell: (params) => (
        <Stack direction="row" spacing={1}>
          <Tooltip title={t('common:edit')}>
            <IconButton size="small" onClick={() => onEdit(params.row)}>
              <MoreVert />
            </IconButton>
          </Tooltip>
          <Tooltip title={t('common:delete')}>
            <IconButton size="small" color="error" onClick={() => handleDelete(params.row.id)}>
              <Delete />
            </IconButton>
          </Tooltip>
        </Stack>
      ),
    },
  ];

  const handleSearch = (e) => {
    const value = e.target.value.trim();
    setSearchText(value);
    // Add debounce for better performance
    const debouncedSearch = setTimeout(() => {
      onSearch(value);
    }, 300);
    if (selectedIds.length > 0) {
      setDeleteConfirmOpen(true);
    }
  };

  const handleDelete = (id) => {
    setSelectedIds([id]);
    setDeleteConfirmOpen(true);
  };

  const confirmDelete = () => {
    onBulkDelete(selectedIds);
    setDeleteConfirmOpen(false);
    setSelectedIds([]);
  };

  return (
    <Box sx={{ height: 500, width: '100%' }}>
      <DataGrid
        rows={divisions}
        columns={columns}
        pageSize={10}
        rowsPerPageOptions={[10, 25, 50]}
        checkboxSelection
        onSelectionModelChange={(ids) => setSelectedIds(ids)}
        pagination
        autoPageSize={false}
        disableColumnMenu
        disableColumnFilter
        disableColumnSelector
        disableDensitySelector
        disableExtendRowFullWidth
        disableMultipleSelection
        disableVirtualization
        loading={loading}
      />
      <ConfirmationDialog
        open={deleteConfirmOpen}
        onClose={() => setDeleteConfirmOpen(false)}
        onConfirm={confirmDelete}
        title={t('organization:division.deleteConfirmTitle')}
        message={t('organization:division.deleteConfirmMessage')}
      />
    </Box>
  );
};

export default DivisionTable;
