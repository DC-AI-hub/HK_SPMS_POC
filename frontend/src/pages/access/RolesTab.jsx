import React from 'react';
import { 
  TextField, Button, Alert, IconButton, Tooltip, Stack, Box, Fab
} from '@mui/material';
import { Add, Edit, Delete, Search } from '@mui/icons-material';
import { DataGrid, GridToolbar } from '@mui/x-data-grid';
import { useScreenSize } from '../../contexts/ScreenSizeContext';
import MobileRoleList from '../../components/organization/MobileRoleList';

/**
 * Roles management tab component
 * @param {Object} props - Component props
 * @param {Function} props.t - Translation function
 * @param {Array} props.roles - List of roles
 * @param {boolean} props.loading - Loading state
 * @param {Object} props.error - Error object
 * @param {Object} props.pagination - Pagination state
 * @param {Function} props.setPagination - Set pagination function
 * @param {string} props.searchQuery - Search query
 * @param {Function} props.setSearchQuery - Set search query function
 * @param {Function} props.handleCreateRole - Create role handler
 * @param {Function} props.handleEditRole - Edit role handler
 * @param {Function} props.handleDeleteRole - Delete role handler
 * @returns {JSX.Element} Roles tab content
 */
const RolesTab = ({ 
  t, 
  roles, 
  loading, 
  error, 
  pagination, 
  setPagination, 
  searchQuery, 
  setSearchQuery,
  handleCreateRole,
  handleEditRole,
  handleDeleteRole
}) => {
  const { isMobileScreen } = useScreenSize();
  const roleColumns = [
    { field: 'id', headerName: t('access:columns.id'), width: 100 },
    { field: 'name', headerName: t('access:columns.name'), width: 200 },
    { field: 'description', headerName: t('access:columns.description'), width: 300 },
    {
      field: 'actions',
      headerName: t('access:columns.actions'),
      width: 150,
      sortable: false,
      renderCell: (params) => (
        <div>
          <Tooltip title={t('common.edit')}>
            <IconButton onClick={() => handleEditRole(params.row)}>
              <Edit fontSize="small" />
            </IconButton>
          </Tooltip>
          <Tooltip title={t('common.delete')}>
            <IconButton onClick={() => handleDeleteRole(params.row.id)}>
              <Delete fontSize="small" />
            </IconButton>
          </Tooltip>
        </div>
      )
    }
  ];

  return (
    <Box sx={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
      {/* Responsive Action Buttons */}
      {isMobileScreen ? (
        // Mobile button layout - simplified with FAB
        <Box sx={{ position: 'relative', mb: 2 }}>
          <TextField
            size="small"
            placeholder={t('access:searchPlaceholder')}
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            fullWidth
            sx={{ mb: 2 }}
          />
          <Fab
            color="primary"
            aria-label="add"
            onClick={handleCreateRole}
            sx={{
              position: 'fixed',
              bottom: 60,
              right: 16,
              zIndex: 1000
            }}
          >
            <Add />
          </Fab>
        </Box>
      ) : (
        // Desktop button layout
        <Stack direction="row" spacing={2} sx={{ mb: 2 }}>
          <TextField
            size="small"
            placeholder={t('access:searchPlaceholder')}
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
          />
          <Button
            variant="contained"
            startIcon={<Add />}
            onClick={handleCreateRole}
          >
            {t('access:createRoleButton')}
          </Button>
        </Stack>
      )}
      
      {error && (
        <Alert severity="error" sx={{ mb: 2 }}>
          {error}
        </Alert>
      )}
      
      {/* Conditional rendering of role list */}
      {isMobileScreen ? (
        <MobileRoleList
          roles={roles}
          onEdit={handleEditRole}
          onDelete={(role) => handleDeleteRole(role.id)}
          loading={loading}
        />
      ) : (
        <Box sx={{ flex: 1, width: '100%' }}>
          <DataGrid
            rows={roles}
            columns={roleColumns}
            loading={loading}
            pagination
            paginationMode="server"
            rowCount={pagination.total}
            page={pagination.page}
            pageSize={pagination.pageSize}
            onPageChange={(newPage) => setPagination(prev => ({ ...prev, page: newPage }))}
            onPageSizeChange={(newSize) => setPagination(prev => ({ ...prev, pageSize: newSize }))}
            pageSizeOptions={[5, 10, 25]}
            slots={{ toolbar: GridToolbar }}
            slotProps={{
              toolbar: {
                showQuickFilter: true,
                quickFilterProps: { debounceMs: 500 },
              },
            }}
          />
        </Box>
      )}
    </Box>
  );
};

export default RolesTab;
