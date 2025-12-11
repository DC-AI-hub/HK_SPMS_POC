import React, { useState, useEffect } from 'react';
import { Box, Button, Dialog, DialogContent, DialogTitle, Stack, TextField, Fab } from '@mui/material';
import DepartmentTable from './DepartmentTable';
import DepartmentDialog from './DepartmentDialog';
import TagsTable from './TagsTable';
import organizationService from '../../api/idm/organizationService';
import { useTranslation } from 'react-i18next';
import { Add } from '@mui/icons-material';
import { useScreenSize } from '../../contexts/ScreenSizeContext';
import MobileDepartmentList from '../../components/organization/MobileDepartmentList';

const Department = () => {
  const { t } = useTranslation();
  const { isMobileScreen } = useScreenSize();
  const [departments, setDepartments] = useState([]);
  const [loading, setLoading] = useState(false);
  const [showDialog, setShowDialog] = useState(false);
  const [selectedDepartment, setSelectedDepartment] = useState(null);
  const [editingDepartment, setEditingDepartment] = useState(null); // Added for edit functionality
  const [searchQuery, setSearchQuery] = useState('');
  const [selectedIds, setSelectedIds] = useState([]);
  const [isCreateDialogOpen, setIsCreateDialogOpen] = useState(false);
  const [isEditDialogOpen, setIsEditDialogOpen] = useState(false); // Added for edit dialog
  const [isTagsDialogOpen, setIsTagsDialogOpen] = useState(false);
  const [selectedTags, setSelectedTags] = useState({});

  useEffect(() => {
    const fetchDepartments = async () => {
      setLoading(true);
      try {
        const response = await organizationService.getDepartments({ search: searchQuery });
        setDepartments(response.content);
      } catch (error) {
        console.error(t('organization:department.fetchError'), error);
      } finally {
        setLoading(false);
      }
    };
    
    const debounceTimer = setTimeout(() => {
      fetchDepartments();
    }, 300);
    
    return () => clearTimeout(debounceTimer);
  }, [searchQuery]);


  const handleBulkDelete = async () => {
      //TODO: implement the bulk Delete
      
  };

  const handleCreate = async (departmentData) => {
    try {
      await organizationService.createDepartment(departmentData);
      // Trigger refetch by updating search query (which will trigger the useEffect)
      setSearchQuery(prev => prev + ' '); // Add space to force re-fetch
      setIsCreateDialogOpen(false);
    } catch (error) {
      console.error(t('organization:department.createError'), error);
    }
  };

  // Handle department edit
  const handleEdit = (department) => {
    setEditingDepartment(department);
    setIsEditDialogOpen(true);
  };

  // Handle department update
  const handleUpdate = async (departmentData) => {
    try {
      await organizationService.updateDepartment(editingDepartment.id, departmentData);
      // Trigger refetch by updating search query (which will trigger the useEffect)
      setSearchQuery(prev => prev + ' '); // Add space to force re-fetch
      setIsEditDialogOpen(false);
      setEditingDepartment(null);
    } catch (error) {
      console.error(t('organization:department.updateError'), error);
    }
  };

  // Handle department delete
  const handleDelete = (department) => {
    // TODO: Implement delete functionality
    console.log(t('organization:department.deleteError'), department);
  };

  return (
    <Box style={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
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
            {t('organization:department.create')}
          </Button>
          <Button
            variant="outlined"
            color="error"
            disabled={!selectedIds.length}
            onClick={handleBulkDelete}
          >
            {t('common:delete')}
          </Button>
        </Stack>
      )}

      {/* Conditional rendering of department list */}
      {isMobileScreen ? (
        <MobileDepartmentList
          departments={departments}
          onEdit={handleEdit}
          onDelete={handleDelete}
          loading={loading}
        />
      ) : (
        <DepartmentTable 
          departments={departments}
          loading={loading}
          setSelectedTags={setSelectedTags}
          setIsTagsDialogOpen={setIsTagsDialogOpen}
          onEdit={handleEdit}
        />
      )}

      {/* Create Department Dialog */}
      <DepartmentDialog
        open={isCreateDialogOpen}
        onClose={() => setIsCreateDialogOpen(false)}
        department={null}
        onSubmit={handleCreate}
        maxWidth={isMobileScreen ? false : "sm"}
        fullWidth
        fullScreen={isMobileScreen}
      />

      {/* Edit Department Dialog */}
      <DepartmentDialog
        open={isEditDialogOpen}
        onClose={() => {
          setIsEditDialogOpen(false);
          setEditingDepartment(null);
        }}
        department={editingDepartment}
        onSubmit={handleUpdate}
        maxWidth={isMobileScreen ? false : "sm"}
        fullWidth
        fullScreen={isMobileScreen}
      />

      <Dialog
        open={isTagsDialogOpen}
        onClose={() => setIsTagsDialogOpen(false)}
        maxWidth={isMobileScreen ? false : "sm"}
        fullWidth
        fullScreen={isMobileScreen}
      >
        <DialogTitle>{t('organization:department.tagsDialog.title')}</DialogTitle>
        <DialogContent>
          <TagsTable tags={selectedTags} />
          <Button
            variant="contained"
            onClick={() => setIsTagsDialogOpen(false)}
            sx={{ mt: 2 }}
            fullWidth={isMobileScreen}
          >
            {t('common:close')}
          </Button>
        </DialogContent>
      </Dialog>
    </Box>
  );
};

export default Department;
