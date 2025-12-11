import React, { useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Button,
  Grid,
  Card,
  CardContent,
  Typography,
  Box
} from '@mui/material';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import StatusChip from '../../components/common/StatusChip';
import VersionCloneDesignerDialog from './VersionCloneDesignerDialog';
import processService from '../../api/process/processService';

const VersionDialog = ({ open, versions, definitionId, onClose, onActivate, fullScreen = false, maxWidth = "md" }) => {
  const { t } = useTranslation();
  const [versionsData, setVersionsData] = useState(versions || []);
  const [loading, setLoading] = useState(false);
  const [cloneOpen, setCloneOpen] = useState(false);
  const [baseVersion, setBaseVersion] = useState(null);

  useEffect(() => {
    setVersionsData(versions || []);
  }, [versions]);

  useEffect(() => {
    if (!open) return;
    const needFetch = !(versions || []).every(v => !!v.bpmnXml);
    if (!needFetch || !definitionId) return;
    (async () => {
      try {
        setLoading(true);
        const resp = await processService.getDefinitionVersions(definitionId, { 'include-bpmn': true });
        const content = resp.data?.content || resp.content || [];
        setVersionsData(content);
      } catch (e) {
        // ignore, fallback to original versions
      } finally {
        setLoading(false);
      }
    })();
  }, [open, definitionId, versions]);

  return (
    <Dialog open={open} onClose={onClose} fullWidth maxWidth={maxWidth} fullScreen={fullScreen}>
      <DialogTitle>{t('process:versionDialog.title')}</DialogTitle>
      <DialogContent>
        <Grid container spacing={2}>
          {versionsData?.map(version => (
            <Grid item xs={12} sm={6} md={4} key={version.id}>
              <Card 
                sx={{ 
                  height: '100%',
                  display: 'flex',
                  flexDirection: 'column',
                  cursor: version.status === 'DRAFT' ? 'pointer' : 'default',
                  opacity: version.status === 'INACTIVE' ? 0.6 : 1,
                  border: version.status === 'ACTIVE' ? '2px solid #4caf50' : '1px solid #e0e0e0',
                  '&:hover': {
                    boxShadow: version.status === 'DRAFT' ? 3 : 0
                  }
                }}
                onClick={() => version.status === 'DRAFT' && onActivate(version)}
              >
                <CardContent sx={{ flexGrow: 1 }}>
                  <Box display="flex" justifyContent="space-between" alignItems="center" mb={1}>
                    <Typography variant="h6">v{version.version}</Typography>
                    {version.status === 'ACTIVE' && <CheckCircleIcon color="success" />}
                  </Box>
                  
                  <Box mb={1.5}>
                    <StatusChip status={version.status}  value={version.status}/>
                  </Box>
                  
                  <Typography variant="body2" color="text.secondary" mb={1.5}>
                    {t('process:versionDialog.createdAt')}: {new Date(version.createdAt).toLocaleDateString()}
                  </Typography>
                  
                  <Button 
                    variant="outlined" 
                    fullWidth 
                    sx={{ mb: 1 }}
                    onClick={(e) => {
                      e.stopPropagation();
                      setBaseVersion(version);
                      setCloneOpen(true);
                    }}
                  >
                    {t('process:editInDesigner') || 'Edit in Designer'}
                  </Button>

                  {version.status === 'DRAFT' && (
                    <Button 
                      variant="outlined" 
                      fullWidth 
                      startIcon={<CheckCircleIcon />}
                      onClick={(e) => {
                        e.stopPropagation();
                        onActivate(version);
                      }}
                    >
                      {t('process:versionDialog.activate')}
                    </Button>
                  )}
                </CardContent>
              </Card>
            </Grid>
          ))}
        </Grid>
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose}>{t('common:close')}</Button>
      </DialogActions>

      {cloneOpen && baseVersion && (
        <VersionCloneDesignerDialog
          open={cloneOpen}
          definitionId={definitionId}
          baseVersion={baseVersion}
          onClose={() => setCloneOpen(false)}
          onSaved={async () => {
            try {
              setLoading(true)
              const resp = await processService.getDefinitionVersions(definitionId, { 'include-bpmn': true })
              const content = resp.data?.content || resp.content || []
              setVersionsData(content)
            } finally {
              setLoading(false)
              setCloneOpen(false)
            }
          }}
        />
      )}
    </Dialog>
  );
};

export default VersionDialog;
