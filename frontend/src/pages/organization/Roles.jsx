import React, { useState, useEffect } from 'react';
import {
  Button,
  Stack,
  TextField,
  Dialog,
  DialogTitle,
  DialogContent,
  Box,
  Fab
} from '@mui/material';
import { Add } from '@mui/icons-material';
import roleService from '../../api/idm/roleService';
import { useTranslation } from 'react-i18next';
import { DataGrid, GridToolbar } from '@mui/x-data-grid';
import { useScreenSize } from '../../contexts/ScreenSizeContext';
import MobileRoleList from '../../components/organization/MobileRoleList';

/**
 * Role management component
 */
const Roles = () => {
  const { t } = useTranslation();
  const { isMobileScreen } = useScreenSize();
  const [roles, setRoles] = useState([]);
  const [loading, setLoading] = useState(true);
  const [paginationModel, setPaginationModel] = useState({
    page: 0,
    pageSize: 10,
  });
  const [searchQuery, setSearchQuery] = useState('');
  const [isCreateDialogOpen, setIsCreateDialogOpen] = useState(false);
  const [isEditDialogOpen, setIsEditDialogOpen] = useState(false);
  const [isDeleteDialogOpen, setIsDeleteDialogOpen] = useState(false);
  const [selectedRole, setSelectedRole] = useState(null);

  useEffect(() => {
    const fetchRoles = async () => {
      try {
        const response = await roleService.getAll();
        setRoles(response.data.content);
      } catch (error) {
        console.error('Error fetching roles:', error);
      } finally {
        setLoading(false);
      }
    };
    fetchRoles();
  }, []);

  const columns = [
    { 
      field: 'name', 
      headerName: t('common:name'),
      flex: 2 
    },
    {
      field: 'description',
      headerName: t('common:description'),
      flex: 3,
      valueGetter: (params) => params.value?.substring(0, 50) + (params.value?.length > 50 ? '...' : '')
    },
    // Only show actions column on desktop - mobile actions are handled through MobileRoleList
    ...(isMobileScreen ? [] : [
      {
        field: 'actions',
        headerName: t('common:actions'),
        sortable: false,
        flex: 2,
        renderCell: (params) => (
          <Box>
            <Button
              size="small"
              onClick={() => handleEdit(params.row)}
              sx={{ mr: 1 }}
            >
              {t('common:edit')}
            </Button>
            <Button
              size="small"
              color="error"
              onClick={() => handleDelete(params.row)}
            >
              {t('common:delete')}
            </Button>
          </Box>
        ),
      }
    ])
  ];

  const handleCreate = async (roleData) => {
    try {
      const createdRole = await roleService.create(roleData);
      setRoles([...roles, createdRole.data]);
      setIsCreateDialogOpen(false);
    } catch (error) {
      console.error('Error creating role:', error);
    }
  };

  const handleEdit = (role) => {
    setSelectedRole(role);
    setIsEditDialogOpen(true);
  };

  const handleUpdate = async (roleData) => {
    try {
      const updatedRole = await roleService.update(roleData.id, roleData);
      setRoles(roles.map(role => 
        role.id === updatedRole.data.id ? updatedRole.data : role
      ));
      setIsEditDialogOpen(false);
    } catch (error) {
      console.error('Error updating role:', error);
    }
  };

  const handleDelete = (role) => {
    setSelectedRole(role);
    setIsDeleteDialogOpen(true);
  };

  const confirmDelete = async () => {
    try {
      await roleService.delete(selectedRole.id);
      setRoles(roles.filter(role => role.id !== selectedRole.id));
      setIsDeleteDialogOpen(false);
    } catch (error) {
      console.error('Error deleting role:', error);
    }
  };

  return (
    <div style={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
      {/* Responsive Action Buttons */}
      {isMobileScreen ? (
        // Mobile button layout - simplified with FAB
        <Box sx={{ position: 'relative', mb: 2 }}>
          <TextField
            size="small"
            placeholder={t('common:search')}
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            fullWidth
            sx={{ mb: 2 }}
          />
          <Fab
            color="primary"
            aria-label="add"
            onClick={() => setIsCreateDialogOpen(true)}
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
            placeholder={t('common:search')}
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
          />
          <Button
            variant="contained"
            startIcon={<Add />}
            onClick={() => setIsCreateDialogOpen(true)}
          >
            {t('common:create')}
          </Button>
        </Stack>
      )}

      {/* Conditional rendering of role list */}
      {isMobileScreen ? (
        <MobileRoleList
          roles={roles}
          onEdit={handleEdit}
          onDelete={handleDelete}
          loading={loading}
        />
      ) : (
        <DataGrid
          rows={roles}
          columns={columns}
          pageSizeOptions={[5, 10, 25]}
          paginationModel={paginationModel}
          onPaginationModelChange={setPaginationModel}
          loading={loading}
          slots={{ toolbar: GridToolbar }}
          slotProps={{
            toolbar: {
              showQuickFilter: true,
              quickFilterProps: { debounceMs: 500 },
            },
          }}
        />
      )}

      {/* Create Role Dialog */}
      <Dialog
        open={isCreateDialogOpen}
        onClose={() => setIsCreateDialogOpen(false)}
        maxWidth={isMobileScreen ? false : "sm"}
        fullWidth
        fullScreen={isMobileScreen}
      >
        <DialogTitle>{t('common:create')}</DialogTitle>
        <DialogContent>
          <RoleForm 
            onSubmit={handleCreate}
            onCancel={() => setIsCreateDialogOpen(false)}
          />
        </DialogContent>
      </Dialog>

      {/* Edit Role Dialog */}
      <Dialog
        open={isEditDialogOpen}
        onClose={() => setIsEditDialogOpen(false)}
        maxWidth={isMobileScreen ? false : "sm"}
        fullWidth
        fullScreen={isMobileScreen}
      >
        <DialogTitle>{t('common:edit')}</DialogTitle>
        <DialogContent>
          <RoleForm 
            role={selectedRole}
            onSubmit={handleUpdate}
            onCancel={() => setIsEditDialogOpen(false)}
            isEditMode={true}
          />
        </DialogContent>
      </Dialog>

      {/* Delete Confirmation Dialog */}
      <Dialog
        open={isDeleteDialogOpen}
        onClose={() => setIsDeleteDialogOpen(false)}
        maxWidth={isMobileScreen ? false : "sm"}
        fullWidth
        fullScreen={isMobileScreen}
      >
        <DialogTitle>{t('common:delete')}</DialogTitle>
        <DialogContent>
          <Box>
            {t('role.deleteDialog.confirm', { name: selectedRole?.name })}
          </Box>
          <Box sx={{ 
            mt: 2, 
            display: 'flex', 
            justifyContent: 'flex-end', 
            gap: 1,
            flexDirection: isMobileScreen ? 'column' : 'row'
          }}>
            <Button
              variant="contained"
              color="error"
              onClick={confirmDelete}
              fullWidth={isMobileScreen}
            >
              {t('common:confirm')}
            </Button>
            <Button
              variant="outlined"
              onClick={() => setIsDeleteDialogOpen(false)}
              fullWidth={isMobileScreen}
            >
              {t('common:cancel')}
            </Button>
          </Box>
        </DialogContent>
      </Dialog>
    </div>
  );
};

export default Roles;
