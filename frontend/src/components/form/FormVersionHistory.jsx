import React, { useState, useEffect } from 'react';
import PropTypes from 'prop-types';
import { useTranslation } from 'react-i18next';
import {
  Tabs,
  Tab,
  Box,
  Stack,
  CircularProgress,
  Select,
  MenuItem,
  FormControl,
  InputLabel,
  Typography,
  useTheme,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Button,
  IconButton,
  AppBar,
  Toolbar,
  useMediaQuery
} from '@mui/material';
import Timeline from '@mui/lab/Timeline';
import TimelineItem from '@mui/lab/TimelineItem';
import TimelineSeparator from '@mui/lab/TimelineSeparator';
import TimelineConnector from '@mui/lab/TimelineConnector';
import TimelineContent from '@mui/lab/TimelineContent';
import TimelineDot from '@mui/lab/TimelineDot';
import DeprecationBadge from './DeprecationBadge';
import JsonDiffViewer from './JsonDiffViewer';

import formService from '../../api/process/formService';
import FullscreenIcon from '@mui/icons-material/Fullscreen';
import FullscreenExitIcon from '@mui/icons-material/FullscreenExit';
import CloseIcon from '@mui/icons-material/Close';
import FormPreview from './FormPreview';
import FormPreviewSwitch from './FormPreviewSwitch';

/**
 * Standalone component for form version history management
 * Displays timeline and comparison features
 */
const FormVersionHistory = ({ formKey, versions, onVersionCreated }) => {
  const { t } = useTranslation();
  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down('md'));
  const [activeTab, setActiveTab] = useState(0);
  const [selectedVersion, setSelectedVersion] = useState(null);
  const [compareVersion, setCompareVersion] = useState(null);
  const [loadingSchema, setLoadingSchema] = useState(false);
  const [versionSchemas, setVersionSchemas] = useState({});
  const [previewDialogOpen, setPreviewDialogOpen] = useState(false);
  const [previewVersion, setPreviewVersion] = useState(null);
  const [fullScreen, setFullScreen] = useState(false);

  // Load schemas when versions change
  useEffect(() => {
    const loadSchemas = async () => {
      if (!versions.length) return;

      setLoadingSchema(true);
      try {
        const schemas = {};
        for (const version of versions) {
          const schema = await formService.getVersion(formKey, version.version);
          schemas[version.version] = schema;
        }
        setVersionSchemas(schemas);
      } catch (error) {
        console.error('Error loading schemas:', error);
      } finally {
        setLoadingSchema(false);
      }
    };

    loadSchemas();
  }, [formKey, versions]);

  const handleTabChange = (event, newValue) => {
    setActiveTab(newValue);
  };

  const handleSelectVersion = (version) => {
    setSelectedVersion(version);
    if (activeTab === 1 && !compareVersion) {
      setCompareVersion(version);
    }
  };

  const handleCompareVersionChange = (event) => {
    setCompareVersion(event.target.value);
  };

  const handlePreviewVersion = (version) => {
    setPreviewVersion(version);
    setPreviewDialogOpen(true);
    setFullScreen(false);
  };

  const handleClosePreview = () => {
    setPreviewDialogOpen(false);
    setPreviewVersion(null);
  };

  const handleToggleFullScreen = () => {
    setFullScreen(!fullScreen);
  };

  if (!versions.length) {
    return (
      <Box sx={{ mt: 2, textAlign: 'center' }}>
        <Typography variant="body1" color="text.primary">
          {t('form:noVersions')}
        </Typography>
      </Box>
    );
  }

  const renderDialogContent = () => {
    return (
      <DialogContent>
        {previewVersion && versionSchemas[previewVersion.version] ? (
          (() => {
            const raw = versionSchemas[previewVersion.version].schema
            const parsed = typeof raw === 'string' ? JSON.parse(raw) : raw
            return (
              <FormPreviewSwitch schema={parsed} data={{}} />
            )
          })()
        ) : (
          <CircularProgress />
        )}
      </DialogContent>
    )
  };

  return (
    <Box sx={{ mt: 2 }}>
      <Tabs value={activeTab} onChange={handleTabChange}>
        <Tab label={t('form:timeline')} />
        <Tab label={t('form:compare')} />
      </Tabs>

      <Box sx={{ mt: 2 }}>
        {activeTab === 0 && (
          <Timeline position="alternate">
            {versions.map((version, index) => (
              <TimelineItem key={version.version}>
                <TimelineSeparator>
                  <TimelineDot color={version.status === 'ACTIVE' ? 'primary' : 'grey'} />
                  {index < versions.length - 1 && <TimelineConnector />}
                </TimelineSeparator>
                <TimelineContent>
                  <Box
                    sx={{
                      cursor: 'pointer',
                      padding: theme.spacing(1),
                      border: selectedVersion?.version === version.version
                        ? `2px solid ${theme.palette.primary.main}`
                        : `1px solid ${theme.palette.divider}`,
                      borderRadius: theme.shape.borderRadius
                    }}
                    onClick={() => {
                      handleSelectVersion(version);
                      handlePreviewVersion(version);
                    }}
                  >
                    <Typography variant="subtitle1" color="text.primary">{version.version}</Typography>
                    <Typography variant="body2" color="text.secondary">
                      {new Date(version.publishedDate).toLocaleDateString()}
                    </Typography>
                    <DeprecationBadge status={version.status} />
                  </Box>
                </TimelineContent>
              </TimelineItem>
            ))}
          </Timeline>
        )}

        {activeTab === 1 && (
          <Box>
            <Stack direction={isMobile ? "column" : "row"} spacing={2} sx={{ mb: 2 }}>
              <FormControl fullWidth>
                <InputLabel>{t('form:selectBaseVersion')}</InputLabel>
                <Select
                  value={selectedVersion?.version || ''}
                  onChange={(e) =>
                    setSelectedVersion(versions.find(v => v.version === e.target.value))
                  }
                  label={t('form:selectBaseVersion')}
                >
                  {versions.map(version => (
                    <MenuItem key={version.version} value={version.version}>
                      {version.version} ({version.status})
                    </MenuItem>
                  ))}
                </Select>
              </FormControl>

              <FormControl fullWidth>
                <InputLabel>{t('form:selectCompareVersion')}</InputLabel>
                <Select
                  value={compareVersion?.version || ''}
                  onChange={handleCompareVersionChange}
                  label={t('form:selectCompareVersion')}
                >
                  {versions
                    .filter(v => v.version !== selectedVersion?.version)
                    .map(version => (
                      <MenuItem key={version.version} value={version.version}>
                        {version.version} ({version.status})
                      </MenuItem>
                    ))}
                </Select>
              </FormControl>
            </Stack>

            {loadingSchema ? (
              <CircularProgress />
            ) : (
              selectedVersion && compareVersion && (
                <JsonDiffViewer
                  oldSchema={versionSchemas[compareVersion]}
                  newSchema={versionSchemas[selectedVersion.version]}
                />
              )
            )}
          </Box>
        )}
      </Box>

      {/* Form Version Preview Dialog */}
      <Dialog
        fullScreen={fullScreen}
        open={previewDialogOpen}
        onClose={handleClosePreview}
        fullWidth
        maxWidth="md"
      >
        {fullScreen ? (
          <AppBar position="relative" color="inherit" elevation={0}>
            <Toolbar>
              <IconButton edge="start" color="inherit" onClick={handleClosePreview} aria-label="close">
                <CloseIcon />
              </IconButton>
              <Typography variant="h6" sx={{ flexGrow: 1 }}>
                {t('form:versionPreview')} - {previewVersion?.version}
              </Typography>
              <IconButton color="inherit" onClick={handleToggleFullScreen}>
                <FullscreenExitIcon />
              </IconButton>
            </Toolbar>
          </AppBar>
        ) : (
          <DialogTitle>
            {t('form:versionPreview')} - {previewVersion?.version}
            <IconButton
              aria-label="full screen"
              onClick={handleToggleFullScreen}
              sx={{ position: 'absolute', right: 8, top: 8 }}
            >
              <FullscreenIcon />
            </IconButton>
          </DialogTitle>
        )}
        {renderDialogContent()}
        {!fullScreen && (
          <DialogActions>
            <Button onClick={handleClosePreview}>{t('common:close')}</Button>
          </DialogActions>
        )}
      </Dialog>
    </Box>
  );
};

FormVersionHistory.propTypes = {
  formKey: PropTypes.string.isRequired,
  versions: PropTypes.arrayOf(
    PropTypes.shape({
      version: PropTypes.string.isRequired,
      status: PropTypes.oneOf(['ACTIVE', 'DEPRECATED']).isRequired,
      createdAt: PropTypes.string.isRequired,
      schema: PropTypes.object
    })
  ).isRequired,
  onVersionCreated: PropTypes.func
};

export default FormVersionHistory;
