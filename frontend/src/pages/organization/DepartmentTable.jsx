import React from 'react';
import { Box, Button, TextField, Stack, IconButton, Tooltip, Menu, MenuItem } from '@mui/material';
import { Add, Delete, MoreVert, Search, MoreHoriz } from '@mui/icons-material';
import { DataGrid } from '@mui/x-data-grid';
import { useTranslation } from 'react-i18next';
import { TableColumns } from './constants';

const DepartmentTable = ({ departments, loading, setSelectedTags, setIsTagsDialogOpen, onEdit }) => {
  const { t } = useTranslation();

  const columns = [
    { field: 'name', headerName: t('organization:department.name'), flex: 1 },
    { field: 'type', headerName: t('organization:department.type'), flex: 1 },
    { field: 'level', headerName: t('organization:department.level'), flex: 1 },
    { field: 'parent', headerName: t('organization:department.parent'), flex: 1 },
    {
      field: 'tags',
      headerName: t('organization:department.tags'),
      flex: 1,
      renderCell: (params) => (
        <Button
          size="small"
          onClick={() => {
            setSelectedTags(params.value);
            setIsTagsDialogOpen(true);
          }}
        >
          {t('organization:department.viewTags')}
        </Button>
      )
    },
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
        </Stack>)
    }
  ];

  console.log(departments)

  return (
    <div style={{ height: 400, width: '100%' }}>
      <DataGrid
        rows={departments}
        columns={columns}
        pagination
        pageSize={10}
        autoPageSize={false}
        rowsPerPageOptions={[10, 25, 50]}
        checkboxSelection
        loading={loading}
        disableSelectionOnClick
      />
    </div>
  );
};

export default DepartmentTable;
