import React, { useEffect, useState } from 'react'
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Button,
  Box,
  TextField,
  CircularProgress
} from '@mui/material'
import { useTranslation } from 'react-i18next'
import ProcessDesigner from '../../components/bpmn/ProcessDesigner'
import VersionInput from '../../components/common/VersionInput'
import processService from '../../api/process/processService'

const VersionCloneDesignerDialog = ({ open, definitionId, baseVersion, onClose, onSaved }) => {
  const { t } = useTranslation()
  const [xml, setXml] = useState(baseVersion?.bpmnXml || '')
  const [version, setVersion] = useState('')
  const [description, setDescription] = useState('')
  const [errors, setErrors] = useState({})
  const [isSaving, setIsSaving] = useState(false)

  useEffect(() => {
    if (open) {
      setXml(baseVersion?.bpmnXml || '')
      setVersion('')
      setDescription('')
      setErrors({})
    }
  }, [open, baseVersion])

  async function handleSave() {
    const newErrors = {}
    if (!version) newErrors.version = t('process:version.errors.versionRequired')
    if (!xml) newErrors.designer = t('process:version.errors.designerRequired')
    if (version === baseVersion?.version) newErrors.version = t('process:version.errors.versionMustDiffer') || 'Version must differ from base version'
    if (Object.keys(newErrors).length) return setErrors(newErrors)

    try {
      setIsSaving(true)
      await processService.createProcessDefinitionVersion(definitionId, {
        name: baseVersion?.name || 'New Version',
        key: baseVersion?.key,
        version,
        bpmnXml: xml,
        description
      })
      onSaved?.()
      onClose?.()
    } finally {
      setIsSaving(false)
    }
  }

  return (
    <Dialog open={open} onClose={onClose} fullWidth maxWidth="xl" fullScreen>
      <DialogTitle>
        {t('process:cloneAndEditTitle') || `Edit ${baseVersion?.version} as new version`}
      </DialogTitle>
      <DialogContent dividers>
        <Box sx={{ mb: 2 }}>
          <VersionInput
            value={version}
            onChange={(e) => { setVersion(e.target.value); if (errors.version) setErrors({ ...errors, version: null }) }}
            error={!!errors.version}
            helperText={errors.version}
            required
            processId={definitionId}
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

        <Box sx={{ border: errors.designer ? '1px solid red' : '1px solid #ccc', height: '100%' }}>
          <ProcessDesigner initialXml={xml} onChange={(x) => { setXml(x); if (errors.designer) setErrors({ ...errors, designer: null }) }} />
        </Box>
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose} disabled={isSaving}>{t('common:cancel')}</Button>
        <Button variant="contained" onClick={handleSave} disabled={isSaving}>
          {isSaving ? <CircularProgress size={24} /> : t('common:save')}
        </Button>
      </DialogActions>
    </Dialog>
  )
}

export default VersionCloneDesignerDialog


