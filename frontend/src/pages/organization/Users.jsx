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
import UserForm from './UserForm';
import { Add } from '@mui/icons-material';
import userService from '../../api/idm/userService';
import { useTranslation } from 'react-i18next';
import { DataGrid, GridToolbar } from '@mui/x-data-grid';
import { useScreenSize } from '../../contexts/ScreenSizeContext';
import MobileUserList from '../../components/organization/MobileUserList';

/**
 * User management component handling all user-related operations
 */
const Users = () => {
  const { t } = useTranslation();
  const { isMobileScreen } = useScreenSize();
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [paginationModel, setPaginationModel] = useState({
    page: 0,
    pageSize: 10,
  });
  const [searchQuery, setSearchQuery] = useState('');
  const [selectedIds, setSelectedIds] = useState([]);
  const [isCreateDialogOpen, setIsCreateDialogOpen] = useState(false);
  const [isEditDialogOpen, setIsEditDialogOpen] = useState(false);
  const [isDeleteDialogOpen, setIsDeleteDialogOpen] = useState(false);
  const [selectedUser, setSelectedUser] = useState(null);
  const [userToDelete, setUserToDelete] = useState(null);

  useEffect(() => {
    const fetchUsers = async () => {
      try {
        const response = await userService.search({query: searchQuery});
        setUsers(response.data);
      } catch (error) {
        console.error('Error fetching users:', error);
      } finally {
        setLoading(false);
      }
    };
    fetchUsers();
  }, []);

  const columns = [
    { 
      field: 'username', 
      headerName: t('organization:user.username'),
      flex: 2 
    },
    {
      field: 'email',
      headerName: t('organization:user.email'),
      flex: 2
    },
    {
      field: 'type',
      headerName: t('organization:user.form.type'),
      flex: 1,
      valueGetter: (params) => {
        switch(params) {
          case 'STAFF': return t('organization:user.form.typeStaff');
          case 'VENDOR': return t('organization:user.form.typeVendor');
          case 'MACHINE': return t('organization:user.form.typeMachine');
          default: return params;
        }
      }
    },
    {
      field: 'description',
      headerName: t('organization:user.form.description'),
      flex: 2,
      valueGetter: (params) => {
        return params?.substring(0, 50) + (params?.length > 50 ? '...' : '')
      }
    },
    {
      field: 'roles',
      headerName: t('organization:user.form.roles'),
      flex: 2,
      valueGetter: (params) => (params?.value??[]).join(', ')
    },
    {
      field: 'active',
      headerName: t('organization:user.active'),
      type: 'boolean',
      flex: 1
    },
    {
      field: 'actions',
      headerName: t('common:actions'),
      sortable: false,
      flex: 2,
      renderCell: (params) => { 
        return(
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
      )},
    },
  ];

  const handleCreate = async (userData) => {
    try {
      const createdUser = await userService.create(userData);
      setUsers([...users, createdUser.data]);
      setIsCreateDialogOpen(false);
    } catch (error) {
      console.error('Error creating user:', error);
    }
  };

  const handleEdit = (user) => {
    setSelectedUser(user);
    setIsEditDialogOpen(true);
  };

  const handleUpdate = async (userData) => {
    try {
      await userService.update(userData.id, userData);
      const result = await userService.get(userData.id);
      console.log(result)
      setUsers(users.map(user => 
        user.id === result.data.id ? result.data : user
      ));
      setIsEditDialogOpen(false);
    } catch (error) {
      console.error('Error updating user:', error);
    }
  };

  const handleDelete = (user) => {
    setUserToDelete(user);
    setIsDeleteDialogOpen(true);
  };

  const confirmDelete = async () => {
    try {
      await userService.delete(userToDelete.id);
      setUsers(users.filter(user => user.id !== userToDelete.id));
      setIsDeleteDialogOpen(false);
    } catch (error) {
      console.error('Error deleting user:', error);
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
            placeholder={t('organization:user.search')}
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
            placeholder={t('organization:user.search')}
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
          />
          <Button
            variant="contained"
            startIcon={<Add />}
            onClick={() => setIsCreateDialogOpen(true)}
          >
            {t('organization:user.create')}
          </Button>
        </Stack>
      )}

      {/* Conditional rendering of user list */}
      {isMobileScreen ? (
        <MobileUserList
          users={users}
          onEdit={handleEdit}
          onDelete={handleDelete}
          loading={loading}
        />
      ) : (
        <DataGrid
          rows={users}
          columns={columns}
          pageSizeOptions={[5, 10, 25]}
          paginationModel={paginationModel}
          onPaginationModelChange={setPaginationModel}
          checkboxSelection
          onRowSelectionModelChange={(ids) => setSelectedIds(ids)}
          loading={loading}
          slots={{ toolbar: GridToolbar }}
        />
      )}

      {/* Create User Dialog */}
      <Dialog
        open={isCreateDialogOpen}
        onClose={() => setIsCreateDialogOpen(false)}
        maxWidth={isMobileScreen ? false : "sm"}
        fullWidth
        fullScreen={isMobileScreen}
      >
        <DialogTitle>{t('organization:user.createDialog.title')}</DialogTitle>
        <DialogContent>
          <UserForm
            onSubmit={handleCreate}
            onCancel={() => setIsCreateDialogOpen(false)}
          />
        </DialogContent>
      </Dialog>

      {/* Edit User Dialog */}
      <Dialog
        open={isEditDialogOpen}
        onClose={() => setIsEditDialogOpen(false)}
        maxWidth={isMobileScreen ? false : "sm"}
        fullWidth
        fullScreen={isMobileScreen}
      >
        <DialogTitle>{t('organization:user.editDialog.title')}</DialogTitle>
        <DialogContent>
          <UserForm
            user={selectedUser}
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
        <DialogTitle>{t('organization:user.deleteDialog.title')}</DialogTitle>
        <DialogContent>
          <Box>
            {t('organization:user.deleteDialog.confirm', { username: userToDelete?.username })}
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

export default Users;
