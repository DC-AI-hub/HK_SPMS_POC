import React, { useState, useEffect, useRef } from 'react';
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Button,
  TextField,
  Box,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  FormControlLabel,
  Switch,
  Alert,
  CircularProgress,
  Grid
} from '@mui/material';
import { useTranslation } from 'react-i18next';
import FormEditor from '../../components/form/FormEditor';
import { Tabs, Tab } from '@mui/material';
import { customFormCatalog } from '../../components/form/custom-registry.jsx';
import formService, { ValidationException, NotFoundException } from '../../api/process/formService';
import FormKeyInput from '../../components/form/FormKeyInput';

// Regex for version validation: alphanumeric, hyphens, dots
const VERSION_REGEX = /^[a-zA-Z0-9\-\\.]+$/;

const initialSchema = {
  "components": [
    {
      "type": "text",
      "text": "# Invoice\nLorem _ipsum_ __dolor__ `sit`.\n  \n  \nA list of BPMN symbols:\n* Start Event\n* Task\nLearn more about [forms](https://bpmn.io).\n  \n"
    }
  ],
  "type": "default"
}
/**
 * Enhanced form creator with version metadata and base version selection
 */
const FormCreator = ({ formKey, open, onClose, onSuccess }) => {

  const editorRef = useRef(null);
  const { t } = useTranslation();
  const [schema, setSchema] = useState(null);
  const [creatorTab, setCreatorTab] = useState('designer'); // 'designer' | 'custom'
  const [selectedTemplateKey, setSelectedTemplateKey] = useState(customFormCatalog?.[0]?.key || 'custom-form');
  const [loading, setLoading] = useState(false);
  const [loadingVersions, setLoadingVersions] = useState(false);
  const [versions, setVersions] = useState([]);
  const [selectedBaseVersion, setSelectedBaseVersion] = useState('');
  const [newVersionId, setNewVersionId] = useState('');
  const [versionError, setVersionError] = useState('');
  const [isDeprecated, setIsDeprecated] = useState(false);
  const [error, setError] = useState(null);
  const [isFullScreen, setFullSecreen] = useState(false);
  const [newFormkey, setNewFormKey] = useState('');


  // Load available versions when dialog opens
  useEffect(() => {
    if (open) {
      const fetchVersions = async () => {
        setLoadingVersions(true);
        try {
          const versions = await formService.listVersions(formKey);

          setVersions(versions);

        } catch (err) {
          console.error('Error fetching versions:', err);
        } finally {
          setLoadingVersions(false);
        }
      };

      fetchVersions();
    }
  }, [open, formKey]);

  const fullScreen = () => {
    setFullSecreen(true);
  }

  // Validate version ID format
  const validateVersion = (version) => {
    if (!VERSION_REGEX.test(version)) {
      return t('form.errors.invalidVersionFormat');
    }
    return '';
  };

  // Load schema when base version is selected
  useEffect(() => {
    if (selectedBaseVersion) {
      const loadBaseVersionSchema = async () => {
        try {
          const versionData = await formService.getVersion(formKey, selectedBaseVersion);
          //console.log(versions);
          setSchema(JSON.parse(versionData.schema));
          //console.log(versionData.schema)
        } catch (err) {
          console.error('Error loading base version:', err);
          setError(t('form.errors.failedToLoadBase'));
        }
      };
      loadBaseVersionSchema();
    }
  }, [selectedBaseVersion]);

  // Handle version ID change with validation
  const handleVersionChange = (e) => {
    const value = e.target.value;
    setNewVersionId(value);
    setVersionError(validateVersion(value));
  };

  const formDesignerChange = (f) => {
    console.log(f)
    setSchema(f)
  }

  // Save new version
  const handleSave = async () => {
    const validationError = validateVersion(newVersionId);
    if (validationError) {
      setVersionError(validationError);
      return;
    }
    setLoading(true);
    setError(null);
    try {
      const finalSchema = creatorTab === 'custom'
        ? (() => {
            const tpl = customFormCatalog.find(x => x.key === selectedTemplateKey) || customFormCatalog[0]
            return { type: 'custom', componentKey: tpl.key, props: tpl?.defaults?.props ?? {}, initialData: tpl?.defaults?.initialData ?? {} }
          })()
        : schema
      const newVersion = await formService.createNewVersion(formKey ?? newFormkey, {
        schema: JSON.stringify(finalSchema),
        version: newVersionId,
        deprecated: isDeprecated,
        name: "test",
        description: "test description"
      });
      onSuccess?.(newVersion);
      onClose();
    } catch (err) {
      if (err instanceof ValidationException) {
        setError(err.message);
      } else if (err instanceof NotFoundException) {
        setError(t('form:errors.baseVersionNotFound'));
      } else {
        setError(t('form:errors.genericError'));
        console.error('Error creating form version:', err);
      }
    } finally {
      setLoading(false);
    }


  };

  const renderFormMeta = () => {
    if (formKey) {
      return (
        <FormControl fullWidth sx={{ minWidth: 250 }}>
          <InputLabel>{t('form:baseVersion')}</InputLabel>
          <Select
            value={selectedBaseVersion}
            onChange={(e) => setSelectedBaseVersion(e.target.value)}
            label={t('form.baseVersion')}
            disabled={loadingVersions}
          >
            <MenuItem value="">
              {t('form.noBaseVersion')}
            </MenuItem>
            {versions.map(version => (
              <MenuItem key={version.id} value={version.version}>
                {version.version}
              </MenuItem>
            ))}
          </Select>
          {loadingVersions && <CircularProgress size={20} sx={{ position: 'absolute', right: 10, top: 15 }} />}
        </FormControl>)
    } else {
      return (
        <FormControl fullWidth >
          <FormKeyInput value={newFormkey} onChange={setNewFormKey} />
        </FormControl>)
    }
  }

  return (
    <Dialog
      open={open}
      onClose={onClose}
      fullWidth
      maxWidth={false}
      fullScreen={isFullScreen}
    >
      <DialogTitle>{t('form:createVersionTitle')}</DialogTitle>
      <DialogContent>
        {error && (
          <Alert severity="error" sx={{ mb: 2 }}>
            {error}
          </Alert>
        )}

        {/* Version Metadata Section */}
        <Box sx={{ mb: 2, p: 2, border: '1px solid', borderColor: 'divider', borderRadius: 1 }}>
          <Grid container spacing={2} className="items-center">
            <Grid item xs={6}>
              {renderFormMeta()}
            </Grid>
            <Grid item xs={6}>
              <TextField
                fullWidth
                label={t('form:newVersionId')}
                value={newVersionId}
                onChange={handleVersionChange}
                error={!!versionError}
              />
            </Grid>

            <Grid item xs={12}>
              <FormControlLabel
                control={
                  <Switch
                    checked={isDeprecated}
                    onChange={(e) => setIsDeprecated(e.target.checked)}
                  />
                }
                label={t('form:deprecated')}
              />
            </Grid>
          </Grid>
        </Box>

        {/* Form Editor / Custom Component */}
        <Box>
          <Tabs value={creatorTab} onChange={(_, v) => setCreatorTab(v)} sx={{ mb: 2 }}>
            <Tab value="designer" label={t('form:designer')} />
            <Tab value="custom" label={t('form:customComponent')} />
          </Tabs>

          {creatorTab === 'designer' && (
            <FormEditor
              initialSchema={schema || initialSchema}
              onSchemaChange={formDesignerChange}
            />
          )}

          {creatorTab === 'custom' && (
            <Box className="flex flex-col space-y-3">
              <FormControl fullWidth>
                <InputLabel>Template</InputLabel>
                <Select label="Template" value={selectedTemplateKey} onChange={(e) => setSelectedTemplateKey(e.target.value)}>
                  {customFormCatalog.map(item => (
                    <MenuItem key={item.key} value={item.key}>{item.label}</MenuItem>
                  ))}
                </Select>
              </FormControl>
              <Box sx={{ color: 'text.secondary' }}>
                {customFormCatalog.find(x => x.key === selectedTemplateKey)?.description}
              </Box>
            </Box>
          )}
        </Box>
      </DialogContent>
      <DialogActions>
        < Button onClick={fullScreen} disabled={isFullScreen} >
          {t('common:fullscreen')}
        </Button>
        <Button onClick={onClose} disabled={loading}>
          {t('common:cancel')}
        </Button>
        <Button
          variant="contained"
          onClick={handleSave}
          disabled={loading || !!versionError}
        >
          {loading ? <CircularProgress size={24} /> : t('common:save')}
        </Button>
      </DialogActions>
    </Dialog>
  );
};

export default FormCreator;

function safeParse(text) {
  try {
    if (!text) return undefined;
    return JSON.parse(text);
  } catch (_) {
    return undefined;
  }
}
