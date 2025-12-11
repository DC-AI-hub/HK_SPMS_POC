import React, { useState, useEffect } from 'react';
import { useTranslation } from 'react-i18next';
import { 
  Box, 
  FormControl, 
  InputLabel, 
  Select, 
  MenuItem,
  CircularProgress
} from '@mui/material';
import formService from '../../api/process/formService';

const FormSelector = ({ onKeySelected, onVersionSelected }) => {
  const { t } = useTranslation();
  const [formKeys, setFormKeys] = useState([]);
  const [versions, setVersions] = useState([]);
  const [selectedKey, setSelectedKey] = useState('');
  const [selectedVersion, setSelectedVersion] = useState('');
  const [loadingKeys, setLoadingKeys] = useState(false);
  const [loadingVersions, setLoadingVersions] = useState(false);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchFormKeys = async () => {
      try {
        setLoadingKeys(true);
        const keys = await formService.getAllFormKeys();
        setFormKeys(keys);
      } catch (err) {
        setError(t('process:formSelector.errorLoadingKeys'));
        console.error('Error loading form keys:', err);
      } finally {
        setLoadingKeys(false);
      }
    };

    fetchFormKeys();
  }, [t]);

  useEffect(() => {
    const fetchVersions = async () => {
      if (!selectedKey) return;
      
      try {
        setLoadingVersions(true);
        const versions = await formService.listVersions(selectedKey);
        setVersions(versions);
      } catch (err) {
        setError(t('process:formSelector.errorLoadingVersions', { key: selectedKey }));
        console.error(`Error loading versions for ${selectedKey}:`, err);
      } finally {
        setLoadingVersions(false);
      }
    };

    fetchVersions();
  }, [selectedKey, t]);

  const handleKeyChange = (event) => {
    const key = event.target.value;
    setSelectedKey(key);
    setSelectedVersion('');
    setVersions([]);
    onKeySelected(key);
    onVersionSelected('');
  };

  const handleVersionChange = (event) => {
    const version = event.target.value;
    setSelectedVersion(version);
    onVersionSelected(version);
  };

  return (
    <Box sx={{ display: 'flex', gap: 2, mb: 2 }}>
      <FormControl fullWidth>
        <InputLabel>{t('process:formSelector.selectKey')}</InputLabel>
        <Select
          value={selectedKey}
          onChange={handleKeyChange}
          label={t('process:formSelector.selectKey')}
          disabled={loadingKeys}
        >
          {loadingKeys && (
            <MenuItem value="">
              <CircularProgress size={24} />
            </MenuItem>
          )}
          {formKeys.map((key) => (
            <MenuItem key={key} value={key}>
              {key}
            </MenuItem>
          ))}
        </Select>
      </FormControl>

      <FormControl fullWidth>
        <InputLabel>{t('process:formSelector.selectVersion')}</InputLabel>
        <Select
          value={selectedVersion}
          onChange={handleVersionChange}
          label={t('process:formSelector.selectVersion')}
          disabled={!selectedKey || loadingVersions}
        >
          {loadingVersions && (
            <MenuItem value="">
              <CircularProgress size={24} />
            </MenuItem>
          )}
          {versions.map((version) => (
            <MenuItem key={version.version} value={version.version}>
              {version.version}
            </MenuItem>
          ))}
        </Select>
      </FormControl>
    </Box>
  );
};

export default FormSelector;
