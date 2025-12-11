import React, { useState, useEffect } from 'react';
import { TextField, Tooltip, IconButton } from '@mui/material';
import { HelpOutline as HelpIcon } from '@mui/icons-material';
import { useTranslation } from 'react-i18next';
import processService from '../../api/process/processService';

/**
 * Enhanced version input component with validation and suggestions
 * @param {Object} props Component props
 * @param {String} props.value Current version value
 * @param {Function} props.onChange Change handler
 * @param {Boolean} [props.error] Validation error state
 * @param {String} [props.helperText] Helper/error text
 * @param {Boolean} [props.required] Marks field as required
 * @param {Boolean} [props.disabled] Disables the input
 * @param {String} [props.processId] Process ID for version suggestions
 * @returns {JSX.Element} Version input component
 */
const VersionInput = ({ 
  value, 
  onChange,
  error,
  helperText,
  required,
  disabled,
  processId
}) => {
  const { t } = useTranslation();
  const [suggestions, setSuggestions] = useState([]);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (processId) {
      const fetchVersions = async () => {
        setLoading(true);
        try {
          const response = await processService.getDefinitionVersions(processId, {});
          // Handle both possible response structures
          const content = response.data?.content || response.data?.data?.content;
          setSuggestions(content?.map(v => v.version) || []);
        } catch (error) {
          console.error('Error fetching versions:', error);
          setSuggestions([]);
        } finally {
          setLoading(false);
        }
      };
      fetchVersions();
    } else {
      setSuggestions([]);
    }
  }, [processId]);

  const validateVersion = (version) => {
    if (!version) return t('validation.required');
    if (!/^\d+\.\d+\.\d+$/.test(version)) {
      return t('process:versionFormatError');
    }
    return '';
  };

  const getNextVersion = (currentVersion) => {
    if (!currentVersion || !/^\d+\.\d+\.\d+$/.test(currentVersion)) {
      return ['1.0.0'];
    }
    
    const [major, minor, patch] = currentVersion.split('.').map(Number);
    return [
      `${major + 1}.0.0`,
      `${major}.${minor + 1}.0`,
      `${major}.${minor}.${patch + 1}`
    ];
  };

  const handleChange = (e) => {
    onChange(e);
  };

  const versions = getNextVersion(value);

  return (
    <TextField
      fullWidth
      label={t('common:version')}
      name="version"
      value={value}
      onChange={handleChange}
      error={!!error}
      helperText={helperText || (
        <span>
          {t('common:versionHelper')}
          {versions?.map((v, i) => (
            <span key={v}>
              {i > 0 && ', '}
              <Tooltip title={t(`common.version${i === 0 ? 'Major' : i === 1 ? 'Minor' : 'Patch'}Helper`)}>
                <span 
                  style={{ 
                    cursor: 'pointer',
                    textDecoration: 'underline',
                    color: '#1976d2' 
                  }}
                  onClick={() => onChange({
                    target: {
                      name: 'version',
                      value: v
                    }
                  })}
                >
                  {v}
                </span>
              </Tooltip>
            </span>
          ))}
        </span>
      )}
      required={required}
      disabled={disabled}
      InputProps={{
        endAdornment: (
          <Tooltip title={t('process:versionTooltip')}>
            <IconButton size="small">
              <HelpIcon fontSize="small" />
            </IconButton>
          </Tooltip>
        ),
      }}
    />
  );
};

export default VersionInput;
