import React, { useState, useEffect } from 'react';
import { useTranslation } from 'react-i18next';
import { useError } from '../../contexts/ErrorContext';
import { useScreenSize } from '../../contexts/ScreenSizeContext';
import {
  Box, Button, Typography,
  Chip,
  FormControl,
  InputLabel,
  Select,
  OutlinedInput,
  MenuItem,
  Checkbox,
  ListItemText,
  Tooltip,
  Fab
} from '@mui/material';

import { DataGrid, GridActionsCellItem } from '@mui/x-data-grid';
import AddIcon from '@mui/icons-material/Add';
import PostAddIcon from '@mui/icons-material/PostAdd';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import ProcessForm from './ProcessForm';
import ProcessVersionForm from './ProcessVersionForm';
import VersionDialog from './VersionDialog';
import VersionEditorDialog from './VersionEditorDialog'; // NEW
import processService from '../../api/process/processService';
import StatusChip from '../../components/common/StatusChip';
import MobileProcessList from '../../components/process/MobileProcessList';

const ProcessTab = () => {
  const { t } = useTranslation();
  const { addError } = useError();
  const { isMobileScreen } = useScreenSize();
  const [processes, setProcesses] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showForm, setShowForm] = useState(false);
  const [showVersionForm, setShowVersionForm] = useState(false);
  const [selectedProcess, setSelectedProcess] = useState(null);
  const [versionDialogOpen, setVersionDialogOpen] = useState(false);
  const [selectedProcessVersions, setSelectedProcessVersions] = useState([]);
  const [paginationModel, setPaginationModel] = useState({
    page: 0,
    pageSize: 10,
  });
  const [statusFilter, setStatusFilter] = useState([]);
  const [editorDialogOpen, setEditorDialogOpen] = useState(false); // NEW
  const [editingVersion, setEditingVersion] = useState(null); // NEW

  const fetchProcesses = async () => {
    setLoading(true);
    try {
      const response = await processService.getProcessDefinitions({
        page: paginationModel.page,
        size: paginationModel.pageSize,
        status: statusFilter.length > 0 ? statusFilter.join(',') : undefined
      });
      setProcesses(response.content);
    } catch (error) {
      addError(t('process:errors.fetchFailed', { error: error.message }));
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchProcesses();
  }, [paginationModel, statusFilter]);

  const handleStatusFilterChange = (event) => {
    const {
      target: { value },
    } = event;
    setStatusFilter(
      typeof value === 'string' ? value.split(',') : value,
    );
  };

  useEffect(() => {
    fetchProcesses();
  }, [paginationModel]);

  const handleCreateProcess = async (data) => {
    try {
      await processService.createProcessDefinition(data);
      fetchProcesses();
    } catch (error) {
      addError(t('process:errors.createFailed', { error: error.message }));
    }
  };

  const handleCreateVersion = async (definitionId, data) => {
    try {
      await processService.createProcessDefinitionVersion(definitionId, data);
      fetchProcesses();
    } catch (error) {
      addError(t('process:errors.versionCreateFailed', { error: error.message }));
    }
  };

  const columns = [
    { field: 'processName', headerName: t('process:name'), flex: 1 },
    { field: 'processKey', headerName: t('process:key'), flex: 1 },
    {
      field: 'owner',
      headerName: t('process:owner'),
      flex: 1,
      valueGetter: (params) => {
        return params?.username || ''
      }
    }, {
      field: 'business',
      headerName: t('process:businessOwner'),
      flex: 1,
      valueGetter: (params) => {
        return params?.username || ''
      }
    },
    {
      field: 'versions',
      headerName: t('process:versions'),
      flex: 1,
      renderCell: (params) => {
          console.log(params)
        if (params.value && params.value.length > 0) {
          return params.value.map(x => (
            <StatusChip 
              key={x.id} 
              status={x.status} 
              value={x.version}
              onClick={() => x.status === 'DRAFT' && handleVersionClick(x,params.row.id)}
              clickable={x.status === 'DRAFT'}
            />
          ));
        } else {
          return (<Chip
            label={t(`process:status.no-version-defined`)}
            color={"error"}
            size="small"
            variant="outlined"
            sx={{
              minWidth: 80,
              fontWeight: 'medium',
              borderWidth: 1.5
            }}
          />)
        }
      }
    },
    {
      field: 'actions',
      type: 'actions',
      headerName: t('common:actions'),
      flex: 1,
      getActions: (params) => [
        <GridActionsCellItem
          icon={<PostAddIcon />}
          label={t('process:table.createVersion')}
          onClick={() => {
            setSelectedProcess(params.row);
            setShowVersionForm(true);
          }} 
        />,
    <Tooltip 
      title={!params.row.versions?.length ? t('process:noVersionTooltip') : ''}
      disableHoverListener={!!params.row.versions?.length}
    >
      <span>
        <GridActionsCellItem
          icon={<CheckCircleIcon />}
          label={t('process:table.activate')}
          onClick={() => {
            setSelectedProcess(params.row)
            setSelectedProcessVersions(params.row.versions || []);
            setVersionDialogOpen(true);
          }}
          disabled={params.row.status === 'DEPLOYED' || !params.row.versions?.length}
        />
      </span>
    </Tooltip>
      ]
    }
  ];

  const handleActivateVersion = async (version,id) => {
    try {
      setLoading(true);
      await processService.activateDefinitionVersion(id, version.id);
      fetchProcesses();
    } catch (error) {
      addError(t('process:errors.activationFailed', { error: error.message }));
    } finally {
      setLoading(false);
    }
  };

  // NEW: Handle version click to open editor
  const handleVersionClick = (version, processId) => {
    setEditingVersion({
      ...version,
      definitionId: processId
    });
    setEditorDialogOpen(true);
  };

  // NEW: Handle version update
  const handleVersionUpdate = async (data) => {
    try {
      console.log(data)
      setLoading(true);
      await processService.updateProcessDefinitionVersion(
        data.definitionId, 
        data.id, 
        {
          formKey: data.formKey,
          formVersion: data.formVersion,
          version: data.version,
          bpmnXml: data.bpmnXml,
          description: data.description
        }
      );
      fetchProcesses();
    } catch (error) {
      addError(t('process:errors.updateVersionFailed', { error: error.message }));
    } finally {
      setLoading(false);
    }
  };

  return (
    <Box sx={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
      {/* Responsive Action Buttons */}
      {isMobileScreen ? (
        // Mobile button layout - simplified with FAB
        <Box sx={{ position: 'relative', mb: 2 }}>
          <FormControl sx={{ minWidth: 120, mb: 2 }} size="small">
            <InputLabel>{t('process:filters.status')}</InputLabel>
            <Select
              multiple
              value={statusFilter}
              onChange={handleStatusFilterChange}
              input={<OutlinedInput label={t('process:filters.status')} />}
              renderValue={(selected) => selected.map(s => t(`process:statusFilters.${s}`)).join(', ')}
            >
              {['DEPLOYED', 'INACTIVE', 'DRAFT'].map((status) => (
                <MenuItem key={status} value={status}>
                  <Checkbox checked={statusFilter.indexOf(status) > -1} />
                  <ListItemText primary={t(`process:statusFilters.${status}`)} />
                </MenuItem>
              ))}
            </Select>
          </FormControl>
          <Fab
            color="primary"
            aria-label="add"
            onClick={() => setShowForm(true)}
            sx={{
              position: 'fixed',
              bottom: 60,
              right: 16,
              zIndex: 1000
            }}
          >
            <AddIcon />
          </Fab>
        </Box>
      ) : (
        // Desktop button layout
        <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 2, alignItems: 'center' }}>
          <Box sx={{ display: 'flex', gap: 2 }}>
            <FormControl sx={{ minWidth: 200 }}>
              <InputLabel>{t('process:filters.status')}</InputLabel>
              <Select
                multiple
                value={statusFilter}
                onChange={handleStatusFilterChange}
                input={<OutlinedInput label={t('process:filters.status')} />}
                renderValue={(selected) => selected.map(s => t(`process:statusFilters.${s}`)).join(', ')}
              >
                {['DEPLOYED', 'INACTIVE', 'DRAFT'].map((status) => (
                  <MenuItem key={status} value={status}>
                    <Checkbox checked={statusFilter.indexOf(status) > -1} />
                    <ListItemText primary={t(`process:statusFilters.${status}`)} />
                  </MenuItem>
                ))}
              </Select>
            </FormControl>

            <Button
              variant="contained"
              startIcon={<AddIcon />}
              onClick={() => {
                setShowForm(true)
              }}
            >
              {t('process:create')}
            </Button>
          </Box>
        </Box>
      )}

      {/* Conditional rendering of process list */}
      {isMobileScreen ? (
        <MobileProcessList
          processes={processes}
          onEditVersion={(process) => {
            setSelectedProcess(process);
            setShowVersionForm(true);
          }}
          onActivateVersion={(process) => {
            setSelectedProcess(process);
            setSelectedProcessVersions(process.versions || []);
            setVersionDialogOpen(true);
          }}
          loading={loading}
        />
      ) : (
        <DataGrid
          rows={processes}
          columns={columns}
          loading={loading}
          pageSizeOptions={[10, 25, 50]}
          paginationModel={paginationModel}
          onPaginationModelChange={setPaginationModel}
          autoHeight
          disableRowSelectionOnClick
          getRowId={(row) => row.id}
          localeText={{
            noRowsLabel: t('common:noData')
          }}
        />
      )}

      <ProcessForm
        open={showForm}
        onClose={() => setShowForm(false)}
        onCreate={handleCreateProcess}
        fullScreen={isMobileScreen}
        maxWidth={isMobileScreen ? false : "sm"}
      />

      {selectedProcess && (
        <ProcessVersionForm
          open={showVersionForm}
          onClose={() => setShowVersionForm(false)}
          definitionId={selectedProcess.id}
          onCreate={handleCreateVersion}
          fullScreen={isMobileScreen}
          maxWidth={isMobileScreen ? false : "sm"}
        />
      )}

      <VersionDialog
        open={versionDialogOpen}
        versions={selectedProcessVersions}
        definitionId={selectedProcess?.id}
        onClose={() => setVersionDialogOpen(false)}
        onActivate={(v)=> handleActivateVersion(v,selectedProcess.id )}
        fullScreen={isMobileScreen}
        maxWidth={isMobileScreen ? false : "sm"}
      />
      
      {/* NEW: Version editor dialog */}
      {editingVersion && (
        <VersionEditorDialog
          open={editorDialogOpen}
          version={editingVersion}
          onClose={() => setEditorDialogOpen(false)}
          onSave={handleVersionUpdate}
          fullScreen={isMobileScreen }
          maxWidth={isMobileScreen ? false : "sm"}
        />
      )}
    </Box>
  );
};

export default ProcessTab;
