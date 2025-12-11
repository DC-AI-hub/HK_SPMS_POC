import React, { useState, useEffect } from 'react';
import {
  Button,
  Stack,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Chip,
  IconButton,
  Tooltip,
  Typography
} from '@mui/material';
import { DataGrid } from '@mui/x-data-grid';
import { useTranslation } from 'react-i18next';
import ContentCopyIcon from '@mui/icons-material/ContentCopy';
import processService from '../../api/process/processService';
import dayjs from 'dayjs';
import VersionCloneDesignerDialog from './VersionCloneDesignerDialog';

/**
 * Dialog for managing process versions
 * @param {Object} props Component props
 * @param {Object} props.process The process to manage versions for
 * @param {Boolean} props.open Whether the dialog is open
 * @param {Function} props.onClose Close handler
 * @returns {JSX.Element} Version management dialog
 */
const ProcessVersionDialog = ({ process, open, onClose }) => {
  const { t } = useTranslation();
  const [versions, setVersions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [selectedVersion, setSelectedVersion] = useState(null);
  const [cloning, setCloning] = useState(false);
  const [cloneDesignerOpen, setCloneDesignerOpen] = useState(false);
  const [baseVersionForClone, setBaseVersionForClone] = useState(null);

  useEffect(() => {
    if (open) {
      fetchVersions();
    }
  }, [open, process.id]);

  const fetchVersions = async () => {
    setLoading(true);
    try {
      const response = await processService.getDefinitionVersions(process.id, { 'include-bpmn': true });
      setVersions(response.data.content);
    } catch (error) {
      console.error('Error fetching versions:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleActivateVersion = async (versionId) => {
    try {
      await processService.activateDefinitionVersion(process.id, versionId);
      const updatedVersions = versions.map(v => ({
        ...v,
        status: v.id === versionId ? 'ACTIVE' : v.status === 'ACTIVE' ? 'INACTIVE' : v.status
      }));
      setVersions(updatedVersions);
    } catch (error) {
      console.error('Error activating version:', error);
    }
  };

  const handleCloneVersion = async (versionId) => {
    try {
      setCloning(true);
      const version = versions.find(v => v.id === versionId);
      
      // Increment patch version (1.0.0 → 1.0.1)
      const versionParts = version.version.split('.').map(Number);
      versionParts[versionParts.length - 1] += 1;
      const newVersion = versionParts.join('.');
      
      await processService.createProcessDefinitionVersion(process.id, {
        version: newVersion,
        bpmnXml: version.bpmnXml
      });
      
      // Refresh versions after cloning
      await fetchVersions();
    } catch (error) {
      console.error('Error cloning version:', error);
    } finally {
      setCloning(false);
    }
  };

  const columns = [
    { 
      field: 'version', 
      headerName: t('process:version'), 
      flex: 1 
    },
    { 
      field: 'status', 
      headerName: t('process:status'), 
      flex: 1,
      renderCell: (params) => (
        <Chip 
          label={params.value}
          color={params.value === 'ACTIVE' ? 'success' : 'default'}
          size="small"
        />
      )
    },
    { 
      field: 'createdAt', 
      headerName: t('process:createdAt'), 
      flex: 1,
      valueFormatter: (params) => dayjs(params.value).format('YYYY-MM-DD HH:mm')
    },
    { 
      field: 'actions', 
      headerName: t('common:actions'), 
      flex: 2,
      sortable: false,
      renderCell: (params) => (
        <Stack direction="row" spacing={1}>
          <Tooltip title={t('process:viewDetails')}>
            <Button
              size="small"
              variant="outlined"
              onClick={() => setSelectedVersion(params.row)}
            >
              {t('common:view')}
            </Button>
          </Tooltip>
          
          <Tooltip title={t('process:editInDesignerAsNew') || 'Edit in Designer (new version)'}>
            <Button
              size="small"
              variant="outlined"
              onClick={() => {
                setBaseVersionForClone(params.row);
                setCloneDesignerOpen(true);
              }}
            >
              {t('process:editInDesigner') || 'Edit'}
            </Button>
          </Tooltip>

          <Tooltip title={t('process:cloneVersion')}>
            <IconButton
              size="small"
              disabled={cloning}
              onClick={() => handleCloneVersion(params.row.id)}
            >
              <ContentCopyIcon fontSize="small" />
            </IconButton>
          </Tooltip>
          
          {params.row.status !== 'ACTIVE' && (
            <Tooltip title={t('process:activate')}>
              <Button
                size="small"
                variant="contained"
                onClick={() => handleActivateVersion(params.row.id)}
              >
                {t('process:activate')}
              </Button>
            </Tooltip>
          )}
        </Stack>
      )
    }
  ];

  return (
    <Dialog
      open={open}
      onClose={onClose}
      fullWidth
      maxWidth="lg"
    >
      <DialogTitle>
        {t('process:versionManagement', { name: process.name })}
      </DialogTitle>
      <DialogContent>
        {versions.length === 0 && !loading ? (
          <Typography variant="body1" sx={{ p: 3, textAlign: 'center' }}>
            {t('process:noVersions')}
          </Typography>
        ) : (
          <div style={{ height: 500, width: '100%', marginTop: 16 }}>
            <DataGrid
              rows={versions}
              columns={columns}
              loading={loading || cloning}
              autoHeight={false}
              pageSizeOptions={[5, 10]}
              initialState={{
                pagination: {
                  paginationModel: { pageSize: 5 }
                }
              }}
            />
          </div>
        )}

        {selectedVersion && (
          <Dialog
            open={!!selectedVersion}
            onClose={() => setSelectedVersion(null)}
            fullWidth
            maxWidth="sm"
          >
            <DialogTitle>
              {t('process:versionDetails.title', { version: selectedVersion.version })}
            </DialogTitle>
            <DialogContent>
              <Stack spacing={2} sx={{ mt: 2 }}>
                <div><strong>{t('process:status')}:</strong> {selectedVersion.status}</div>
                <div><strong>{t('process:createdAt')}:</strong> {dayjs(selectedVersion.createdAt).format('YYYY-MM-DD HH:mm')}</div>
                <div><strong>{t('process:createdBy')}:</strong> {selectedVersion.createdBy}</div>
              </Stack>
            </DialogContent>
            <DialogActions>
              <Button onClick={() => setSelectedVersion(null)}>
                {t('common:close')}
              </Button>
            </DialogActions>
          </Dialog>
        )}
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose}>
          {t('common:close')}
        </Button>
      </DialogActions>

      {cloneDesignerOpen && baseVersionForClone && (
        <VersionCloneDesignerDialog
          open={cloneDesignerOpen}
          definitionId={process.id}
          baseVersion={baseVersionForClone}
          onClose={() => setCloneDesignerOpen(false)}
          onSaved={fetchVersions}
        />
      )}
    </Dialog>
  );
};

export default ProcessVersionDialog;
