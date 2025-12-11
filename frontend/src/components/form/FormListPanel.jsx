import React, { useState } from 'react';
import { 
  Box, 
  IconButton,
  useTheme
} from '@mui/material';
import { DataGrid, GridToolbar, GridActionsCellItem } from '@mui/x-data-grid';
import { useTranslation } from 'react-i18next';
import Chip from '@mui/material/Chip';
import { Add, History } from '@mui/icons-material';
import FormKeySelector from './FormKeySelector';

/**
 * Standalone form list panel component
 * @param {Object} props - Component properties
 * @param {Array} props.forms - List of form definitions
 * @param {boolean} props.loading - Loading state indicator
 * @param {Function} props.onFormSelect - Form selection handler
 * @param {Function} props.onCreateVersion - Version creation handler
 */
const FormListPanel = ({ 
  forms, 
  loading, 
  onFormSelect,
  onCreateVersion
}) => {
  const { t } = useTranslation();
  const theme = useTheme();
  const [selectedKey, setSelectedKey] = useState(null);

  // Form definition list columns
  const formListColumns = [
    { 
      field: 'key', 
      headerName: t('form:formKey'),
      flex: 1 
    },
    { 
      field: 'name', 
      headerName: t('form:formName'),
      flex: 1 
    },
    { 
      field: 'version', 
      headerName: t('form:latestVersion'),
      flex: 1 
    },
    {
      field: 'status',
      headerName: t('common:status'),
      flex: 1,
      renderCell: (params) => (
        <Chip 
          label={params.value === 'DEPRECATED' ? t('deprecated') : t('active')} 
          color={params.value === 'DEPRECATED' ? 'error' : 'success'} 
          size="small" 
        />
      )
    },
    {
      field: 'actions',
      type: 'actions',
      headerName: t('common:actions'),
      getActions: (params) => [
        <GridActionsCellItem
          icon={<Add />}
          label={t('createVersion')}
          onClick={() => onCreateVersion(params.row)}
          showInMenu
        />,
        <GridActionsCellItem
          icon={<History />}
          label={t('viewHistory')}
          onClick={() => onFormSelect(params.row)}
          showInMenu
        />
      ]
    },
  ];

  return (
    <Box 
      className="flex flex-col flex-basis w-1/2 p-4"
      sx={{        
        backgroundColor: theme.palette.background.paper,
        borderRadius: theme.shape.borderRadius
      }}
    >
      <Box  className="flex" sx={{ mb: theme.spacing(2) }}>
        <FormKeySelector
          value={selectedKey}
          onChange={(newKey) => {
            setSelectedKey(newKey);
            const form = forms.find(f => f.key === newKey);
            if (form) {
              onFormSelect(form);
            }
          }}
        />
        <IconButton 
          color="primary"
          onClick={() => onCreateVersion(null)}
          sx={{ ml: 1 }}
        >
          <Add />
        </IconButton>
      </Box>
      <DataGrid
        rows={forms}
        onRowClick={(params) => {
          setSelectedKey(params.row.key);
          onFormSelect(params.row);
        }}
        columns={formListColumns}
        pageSizeOptions={[5, 10, 25]}
        loading={loading}
        slots={{ toolbar: GridToolbar }}
      />
    </Box>
  );
};

export default FormListPanel;
