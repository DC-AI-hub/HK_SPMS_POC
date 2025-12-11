import React, { useState, useEffect } from 'react';
import { DataGrid, GridToolbar } from '@mui/x-data-grid';
import { Button, Stack, TextField, Dialog, DialogTitle, DialogContent, Box, Fab } from '@mui/material';
import CompanyProfileTable from './CompanyProfileTable';
import dayjs from 'dayjs';
import { Add, MoreVert } from '@mui/icons-material';
import CompanyForm from './CompanyForm';
import CompanyImportDialog from './CompanyImportDialog';
import organizationService from '../../api/idm/organizationService';
import { CompanyType, TableColumns } from './constants';
import { useTranslation } from 'react-i18next';
import { useScreenSize } from '../../contexts/ScreenSizeContext';
import MobileCompanyList from '../../components/organization/MobileCompanyList';

/**
 * Company management component handling all company-related operations
 * including listing, searching, creating, and deleting companies
 */
const Company = () => {
  const { t } = useTranslation();
  const { isMobileScreen } = useScreenSize();
  const [companies, setCompanies] = useState([]);
  const [loading, setLoading] = useState(true);
  const [paginationModel, setPaginationModel] = useState({
    page: 0,
    pageSize: 10,
  });
  const [searchQuery, setSearchQuery] = useState('');
  const [selectedIds, setSelectedIds] = useState([]);
  const [isCreateDialogOpen, setIsCreateDialogOpen] = useState(false);
  const [isEditDialogOpen, setIsEditDialogOpen] = useState(false);
  const [isProfileDialogOpen, setIsProfileDialogOpen] = useState(false);
  const [isDeleteDialogOpen, setIsDeleteDialogOpen] = useState(false);
  const [isImportDialogOpen, setIsImportDialogOpen] = useState(false);
  const [selectedCompany, setSelectedCompany] = useState(null);
  const [companyToDelete, setCompanyToDelete] = useState(null);
  const [selectedProfiles, setSelectedProfiles] = useState({});
  const [tableKey, setTableKey] = useState(0);

  useEffect(() => {
    const fetchCompanies = async () => {
      try {
        const response = await organizationService.getCompanies();
        if (response && response.content) {
          setCompanies(response.content);
          
        } else {
          console.error('Invalid API response structure:', response);
          setCompanies([]);
        }
      } catch (error) {
        console.error('Error fetching companies:', error);
      } finally {
        setLoading(false);
      }
    };
    fetchCompanies();
  }, []);
  

  const columns = [
    { 
      field: 'name', 
      headerName: t(TableColumns.COMPANY_NAME),
      flex: 2 
    },
    {
      field: 'active',
      headerName: t('organization:company.active'),
      type: 'boolean',
      flex: 1
    },
    {
      field: 'lastModified',
      headerName: t('organization:company.lastModified'),
      type: 'dateTime',
      flex: 1,
      valueFormatter: (params) => {
        return dayjs(params?? "").format('YYYY-MM-DD HH:mm')
      }
    },
    { 
      field: 'description',
      headerName: t('organization:company.description'),
      flex: 2
    },
    { 
      field: 'companyType', 
      headerName: t(TableColumns.COMPANY_TYPE),
      flex: 1,
      renderCell: (params)=>{
        if(params.row.companyType){
          return t(`organization:company.types.${CompanyType[params.row.companyType]}`)
        }
        return "";
      }
    },
    {
      field: 'languageTags.en',
      headerName: t('organization:company.language.en'),
      flex: 1,
      renderCell: (params) =>{ 
        return (
          params.row.languageTags?.en
        )
      }
    },
    {
      field: 'languageTags.zh',
      headerName: t('organization:company.language.zh'),
      renderCell: (params) =>{ 
        return (
          params.row.languageTags?.zh
        )
      }
    },
    {
      field: 'languageTags.zh-TR',
      headerName: t('organization:company.language.zh-TR'),
      flex: 1,
      renderCell: (params) =>{ 
        if( params.row.languageTags){
          return params.row.languageTags["zh-TR"];
        }
        return (
          ""
        )
      }
    },
    {
      field: 'companyProfiles',
      headerName: t('organization:company.profiles'),
      flex: 1,
      renderCell: (params) =>{
        return ( 
          <Box>
            <Button
              size="small"
              onClick={() => {
                setSelectedProfiles(params.value);
                setIsProfileDialogOpen(true);
              }}
            >
              {t('organization:company.viewMore')}
            </Button>
          </Box>
        )
      } 
    },
    {
      field: 'actions',
      headerName: t(TableColumns.ACTIONS),
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
    },
  ];

  const handleCreate = async (companyData) => {
    try {
      const createdCompany = await organizationService.createCompany(companyData);
      setCompanies([...companies, createdCompany.data]);
      setTableKey(prev => prev + 1);
      setIsCreateDialogOpen(false);
    } catch (error) {
      throw error; // 冒泡到表单，由表单定位字段错误
    }
  };

  const handleEdit = (company) => {
    setSelectedCompany(company);
    setIsEditDialogOpen(true);
  };

  const handleUpdate = async (companyData) => {
    try {
      const updatedCompany = await organizationService.updateCompany(companyData.id, companyData);
      setCompanies(companies.map(company => 
        company.id === updatedCompany.data.id ? updatedCompany.data : company
      ));
      setTableKey(prev => prev + 1);
      setIsEditDialogOpen(false);
    } catch (error) {
      throw error;
    }
  };

  const handleDelete = (company) => {
    setCompanyToDelete(company);
    setIsDeleteDialogOpen(true);
  };

  const confirmDelete = async () => {
    try {
      await organizationService.deleteCompany(companyToDelete.id);
      setCompanies(companies.filter(company => company.id !== companyToDelete.id));
      setTableKey(prev => prev + 1);
      setIsDeleteDialogOpen(false);
    } catch (error) {
      console.error('Error deleting company:', error);
    }
  };

  const handleBulkDelete = async () => {
    try {
      await organizationService.bulkDeleteCompanies(selectedIds);
      setCompanies(companies.filter(company => !selectedIds.includes(company.id)));
      setSelectedIds([]);
    } catch (error) {
      console.error('Error deleting companies:', error);
    }
  };

  console.log(companies)

  return (
    <div style={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
      {/* Responsive Action Buttons */}
      {isMobileScreen ? (
        // Mobile button layout - simplified with FAB
        <Box sx={{ position: 'relative', mb: 2 }}>
          <TextField
            size="small"
            placeholder={t('organization:company.search')}
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
            placeholder={t('organization:company.search')}
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
          />
          <Button
            variant="contained"
            startIcon={<Add />}
            onClick={() => setIsCreateDialogOpen(true)}
          >
            {t('organization:company.create')}
          </Button>
          <Button
            variant="outlined"
            color="secondary"
            onClick={() => setIsImportDialogOpen(true)}
            sx={{
              color: '#E6223E',
              borderColor: '#E6223E',
              '&:hover': {
                borderColor: '#E6223E',
                backgroundColor: 'rgba(230, 34, 62, 0.08)'
              }
            }}
          >
            {t('organization:company.import')}
          </Button>
          <Button
            variant="outlined"
            color="secondary"
            disabled={!selectedIds.length}
            onClick={() => {/* TODO: Implement operation functionality */}}
          >
            {t('organization:company.operation')}
          </Button>
          <Button
            variant="outlined"
            color="error"
            disabled={!selectedIds.length}
            onClick={handleBulkDelete}
          >
            {t('organization:company.bulkDelete')}
          </Button>
        </Stack>
      )}

      {/* Conditional rendering of company list */}
      {isMobileScreen ? (
        <MobileCompanyList
          companies={companies}
          onEdit={handleEdit}
          onDelete={handleDelete}
          loading={loading}
        />
      ) : (
        <DataGrid
          key={tableKey}
          rows={companies}
          columns={columns}
          pageSizeOptions={[5, 10, 25]}
          paginationModel={paginationModel}
          onPaginationModelChange={setPaginationModel}
          checkboxSelection
          onRowSelectionModelChange={(ids) => setSelectedIds(ids)}
          loading={loading}
          slots={{ toolbar: GridToolbar }}
          slotProps={{
            toolbar: {
              showQuickFilter: true,
              quickFilterProps: { debounceMs: 500 },
            },
          }}
          initialState={{
            sorting: {
              sortModel: [{ field: 'lastModified', sort: 'desc' }],
            },
          }}
          getRowId={(row)=>{
            if(!row.id) {
              return Math.random();
            }
            return row.id
          }}
        />
      )}
      <Dialog
        open={isCreateDialogOpen}
        onClose={() => setIsCreateDialogOpen(false)}
        maxWidth={isMobileScreen ? false : "sm"}
        fullWidth
        fullScreen={isMobileScreen}
      >
        <DialogTitle>{t('organization:company.createDialog.title')}</DialogTitle>
        <DialogContent>
          <CompanyForm onSubmit={handleCreate} onCancel={() => setIsCreateDialogOpen(false)} />
        </DialogContent>
      </Dialog>

      <Dialog
        open={isProfileDialogOpen}
        onClose={() => setIsProfileDialogOpen(false)}
        maxWidth={isMobileScreen ? false : "sm"}
        fullWidth
        fullScreen={isMobileScreen}
      >
        <DialogTitle>{t('organization:company.profileDialog.title')}</DialogTitle>
        <DialogContent>
          <CompanyProfileTable profiles={selectedProfiles} />
          <Button
            variant="contained"
            onClick={() => setIsProfileDialogOpen(false)}
            sx={{ mt: 2 }}
            fullWidth={isMobileScreen}
          >
            {t('common:close')}
          </Button>
        </DialogContent>
      </Dialog>

      <Dialog
        open={isEditDialogOpen}
        onClose={() => setIsEditDialogOpen(false)}
        maxWidth={isMobileScreen ? false : "sm"}
        fullWidth
        fullScreen={isMobileScreen}
      >
        <DialogTitle>{t('organization:company.editDialog.title')}</DialogTitle>
        <DialogContent>
          <CompanyForm 
            company={selectedCompany}
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
        <DialogTitle>{t('organization:company.deleteDialog.title')}</DialogTitle>
        <DialogContent>
          <Box>
            {t('organization:company.deleteDialog.confirm', { companyName: companyToDelete?.name })}
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

      {isImportDialogOpen && (
        <CompanyImportDialog onClose={() => setIsImportDialogOpen(false)} />
      )}
    </div>
  );
};

export default Company;
