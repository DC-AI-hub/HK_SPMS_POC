import React, { useState, useRef } from 'react';
import { useTranslation } from 'react-i18next';
import {
  Dialog, DialogTitle, DialogContent, DialogActions,
  TextField, Button, Box, CircularProgress, Tooltip, Typography
} from '@mui/material';
import DescriptionIcon from '@mui/icons-material/Description';
import AccountTreeIcon from '@mui/icons-material/AccountTree';
import LockIcon from '@mui/icons-material/Lock';
import ProcessDesigner from '../../components/bpmn/ProcessDesigner';
import ConfirmationDialog from '../../components/ConfirmationDialog';
import VersionInput from '../../components/common/VersionInput';
import FormSelector from '../../components/process/FormSelector';

const VersionEditorDialog = ({ open, version, onClose, onSave, fullScreen = false, maxWidth = "xl" }) => {
  const { t } = useTranslation();
  const [formKey, setFormKey] = useState(version?.formKey || '');
  const [formVersion, setFormVersion] = useState(version?.formVersion || '');
  const [newVersion, setNewVersion] = useState(version?.version || '');
  const [bpmnXml, setBpmnXml] = useState(version?.bpmnXml || '');
  const [description, setDescription] = useState(version?.description || '');
  const [loading, setLoading] = useState(false);
  const [errors, setErrors] = useState({});
  const [showConfirmation, setShowConfirmation] = useState(false);
  const [isDesignerFullScreen, setIsDesignerFullScreen] = useState(false);

  const validateBpmnXml = (xml) => {
    try {
      const parser = new DOMParser();
      const xmlDoc = parser.parseFromString(xml, "text/xml");
      const parserErrors = xmlDoc.getElementsByTagName("parsererror");
      if (parserErrors.length > 0) return t('process:version.errors.invalidXml');
      
      const processes = xmlDoc.getElementsByTagName("bpmn:process");
      if (processes.length === 0) return t('process:version.errors.missingProcess');
      
      const startEvents = xmlDoc.getElementsByTagName("bpmn:startEvent");
      if (startEvents.length === 0) return t('process:version.errors.missingStartEvent');
      
      return null;
    } catch (error) {
      return t('process:version.errors.invalidXml');
    }
  };

  const handleSave = () => {
    const newErrors = {};
    if (!newVersion) newErrors.version = t('process:version.errors.versionRequired');
    if (!bpmnXml) newErrors.designer = t('process:version.errors.designerRequired');

    const bpmnError = validateBpmnXml(bpmnXml);
    if (bpmnError) newErrors.bpmnStructure = bpmnError;

    if (Object.keys(newErrors).length > 0) {
      setErrors(newErrors);
      return;
    }

    setShowConfirmation(true);
  };

  const handleSaveConfirmed = async () => {
    setLoading(true);
    try {
      await onSave({
        ...version,
        formKey,
        formVersion,
        version: newVersion,
        bpmnXml,
        description
      });
      onClose();
    } finally {
      setLoading(false);
      setShowConfirmation(false);
    }
  };

  const handleDesignerChange = (xml) => {
    setBpmnXml(xml);
    if (errors.designer) setErrors(prev => ({ ...prev, designer: null }));
  };

  const handleVersionChange = (e) => {
    setNewVersion(e.target.value);
    if (errors.version) setErrors(prev => ({ ...prev, version: null }));
  };

  const renderReadOnlyField = (label, value) => (
    <Box sx={{ mb: 2 }}>
      <TextField
        label={label}
        value={value || ''}
        fullWidth
        margin="normal"
        InputProps={{
          readOnly: true,
          startAdornment: (
            <Tooltip title={t('process:version.readOnlyTooltip')}>
              <LockIcon fontSize="small" sx={{ mr: 1, color: 'text.secondary' }} />
            </Tooltip>
          )
        }}
      />
    </Box>
  );

  return (
    <Dialog open={open} onClose={onClose} fullWidth maxWidth={maxWidth} fullScreen={fullScreen}>
      <DialogTitle>
        <Box display="flex" alignItems="center">
          <Typography variant="h6" component="span">
            {t('process:version.editTitle')}
          </Typography>
          <Box sx={{ flexGrow: 1 }} />
          <Tooltip title={formVersion ? t('process:version.formSelected') : t('process:version.formNotSelected')}>
            <DescriptionIcon 
              sx={{ ml: 2 }} 
              color={formVersion ? "success" : "disabled"} 
            />
          </Tooltip>
          <Tooltip title={bpmnXml ? t('process:version.processDefined') : t('process:version.processNotDefined')}>
            <AccountTreeIcon 
              sx={{ ml: 1 }} 
              color={bpmnXml ? "success" : "disabled"} 
            />
          </Tooltip>
        </Box>
      </DialogTitle>
      <DialogContent dividers>
        {!isDesignerFullScreen && (
          <>
            {renderReadOnlyField(t('process:version.name'), version?.name)}
            {renderReadOnlyField(t('process:version.key'), version?.key)}
            
            <Box sx={{ mb: 2 }}>
              <FormSelector 
                selectedKey={formKey}
                selectedVersion={formVersion}
                onKeySelected={setFormKey}
                onVersionSelected={setFormVersion}
              />
            </Box>
            
            <Box sx={{ mb: 2 }}>
              <VersionInput
                value={newVersion}
                onChange={handleVersionChange}
                error={!!errors.version}
                helperText={errors.version}
                required
                processId={version?.definitionId}
              />
            </Box>
            
            <Box sx={{ mb: 2 }}>
              <TextField
                label={t('process:version.description')}
                value={description}
                onChange={(e) => setDescription(e.target.value)}
                multiline
                rows={2}
                fullWidth
                margin="normal"
              />
            </Box>
          </>
        )}
        
        {isDesignerFullScreen && (
          <Box sx={{ border: errors.designer ? '1px solid red' : '1px solid #ccc', height: "100%" }}>
            <ProcessDesigner
              initialXml={bpmnXml}
              onChange={handleDesignerChange}
              onfullScreen={() => setIsDesignerFullScreen(false)}
            />
          </Box>
        )}
        
        {errors.designer && (
          <Box sx={{ color: 'error.main', mt: 1 }}>{errors.designer}</Box>
        )}
        {errors.bpmnStructure && (
          <Box sx={{ color: 'error.main', mt: 1 }}>{errors.bpmnStructure}</Box>
        )}
      </DialogContent>
      <DialogActions>
        <Button onClick={() => setIsDesignerFullScreen(true)} disabled={isDesignerFullScreen || loading}>
          {t('process:open-designer')}
        </Button>
        <Button onClick={onClose} disabled={loading}>
          {t('common:cancel')}
        </Button>
        <Button
          variant="contained"
          onClick={handleSave}
          disabled={loading}>
          {loading ? <CircularProgress size={24} /> : t('common:save')}
        </Button>
      </DialogActions>

      <ConfirmationDialog
        open={showConfirmation}
        onClose={() => setShowConfirmation(false)}
        onConfirm={handleSaveConfirmed}
        title={t('confirmation.title')}
        message={t('confirmation.updateVersion')}
      />
    </Dialog>
  );
};

export default VersionEditorDialog;
