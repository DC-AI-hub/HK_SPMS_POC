import React, { useState, useEffect } from 'react';
import { Button, Stack, TextField, Dialog, DialogTitle, DialogContent, Box, Fab } from '@mui/material';
import { Add } from '@mui/icons-material';
import DivisionTable from './DivisionTable';
import DivisionForm from './DivisionForm';
import organizationService from '../../api/idm/organizationService';
import { useTranslation } from 'react-i18next';
import dayjs from 'dayjs';
import { useScreenSize } from '../../contexts/ScreenSizeContext';
import MobileDivisionList from '../../components/organization/MobileDivisionList';

const Division = () => {
  const { t } = useTranslation();
  const { isMobileScreen } = useScreenSize();
  const [divisions, setDivisions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [selectedIds, setSelectedIds] = useState([]);
  const [isCreateDialogOpen, setIsCreateDialogOpen] = useState(false);
  const [isEditDialogOpen, setIsEditDialogOpen] = useState(false);
  const [isDeleteDialogOpen, setIsDeleteDialogOpen] = useState(false);
  const [selectedDivision, setSelectedDivision] = useState(null);
  const [divisionToDelete, setDivisionToDelete] = useState(null);

  const [searchQuery, setSearchQuery] = useState('');

  useEffect(() => {
    const fetchDivisions = async () => {
      setLoading(true);
      try {
        const response = await organizationService.getDivisions({ search: searchQuery });
        setDivisions(response.content);
      } catch (error) {
        console.error('Error fetching divisions:', error);
      } finally {
        setLoading(false);
      }
    };
    
    const debounceTimer = setTimeout(() => {
      fetchDivisions();
    }, 300);
    
    return () => clearTimeout(debounceTimer);
  }, [searchQuery]);

  const handleCreate = async (divisionData) => {
    try {
      const createdDivision = await organizationService.createDivision(divisionData);
      setDivisions([...divisions, createdDivision.data]);
      setIsCreateDialogOpen(false);
    } catch (error) {
        throw error; // 让表单处理字段错误
    }
  };

  const handleEdit = (division) => {
    setSelectedDivision(division);
    setIsEditDialogOpen(true);
  };

  const handleUpdate = async (divisionData) => {
    try {
      const updatedDivision = await organizationService.updateDivision(divisionData.id, divisionData);
      setDivisions(divisions.map(division => 
        division.id === updatedDivision.data.id ? updatedDivision.data : division
      ));
      setIsEditDialogOpen(false);
    } catch (error) {
      throw error;
    }
  };

  const handleDelete = (division) => {
    setDivisionToDelete(division);
    setIsDeleteDialogOpen(true);
  };

  const confirmDelete = async () => {
    try {
      await organizationService.deleteDivision(divisionToDelete.id);
      setDivisions(divisions.filter(division => division.id !== divisionToDelete.id));
      setIsDeleteDialogOpen(false);
    } catch (error) {
      console.error('Error deleting division:', error);
    }
  };

  const handleBulkDelete = async () => {
    try {
      await organizationService.bulkDeleteDivisions(selectedIds);
      setDivisions(divisions.filter(division => !selectedIds.includes(division.id)));
      setSelectedIds([]);
    } catch (error) {
      console.error('Error deleting divisions:', error);
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
            placeholder={t('organization:division.search')}
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
            placeholder={t('organization:division.search')}
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
          />
          <Button
            variant="contained"
            startIcon={<Add />}
            onClick={() => setIsCreateDialogOpen(true)}
          >
            {t('organization:division.create')}
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

      {/* Conditional rendering of division list */}
      {isMobileScreen ? (
        <MobileDivisionList
          divisions={divisions}
          onEdit={handleEdit}
          onDelete={handleDelete}
          loading={loading}
        />
      ) : (
        <DivisionTable
          divisions={divisions}
          loading={loading}
          onEdit={handleEdit}
          onDelete={handleDelete}
          onSelectionChange={setSelectedIds}
        />
      )}

      <Dialog
        open={isCreateDialogOpen}
        onClose={() => setIsCreateDialogOpen(false)}
        maxWidth={isMobileScreen ? false : "sm"}
        fullWidth
        fullScreen={isMobileScreen}
      >
        <DialogTitle>{t('organization:division.create')}</DialogTitle>
        <DialogContent>
          <DivisionForm 
            onSubmit={handleCreate} 
            onCancel={() => setIsCreateDialogOpen(false)}
          />
        </DialogContent>
      </Dialog>

      <Dialog
        open={isEditDialogOpen}
        onClose={() => setIsEditDialogOpen(false)}
        maxWidth={isMobileScreen ? false : "sm"}
        fullWidth
        fullScreen={isMobileScreen}
      >
        <DialogTitle>{t('common:edit')}</DialogTitle>
        <DialogContent>
          <DivisionForm 
            division={selectedDivision}
            onSubmit={handleUpdate} 
            onCancel={() => setIsEditDialogOpen(false)}
            isEditMode={true}
          />
        </DialogContent>
      </Dialog>

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
            {t('common:confirmDelete', { name: divisionToDelete?.name })}
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

export default Division;
