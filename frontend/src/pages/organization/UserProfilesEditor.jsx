import React, { useState } from 'react';
import { 
  TextField,
  Button,
  Stack,
  Box,
  Typography,
  IconButton
} from '@mui/material';
import { Add, Delete } from '@mui/icons-material';
import { useTranslation } from 'react-i18next';

/**
 * Component for editing user profiles in key-value format
 */
const UserProfilesEditor = ({ profiles, onChange }) => {
  const { t } = useTranslation();
  const [newKey, setNewKey] = useState('');
  const [newValue, setNewValue] = useState('');

  const handleAddProfile = () => {
    if (!newKey.trim()) return;
    onChange({
      ...profiles,
      [newKey]: newValue
    });
    setNewKey('');
    setNewValue('');
  };

  const handleRemoveProfile = (key) => {
    const updated = { ...profiles };
    delete updated[key];
    onChange(updated);
  };

  const handleEditProfile = (key, value) => {
    onChange({
      ...profiles,
      [key]: value
    });
  };

  return (
    <Box>
      <Typography variant="subtitle1" sx={{ mb: 1 }}>
        {t('organization:user.form.profiles')}
      </Typography>
      
      <Stack spacing={2} sx={{ mb: 2 }}>
        {Object.entries(profiles).map(([key, value]) => (
          <Stack key={key} direction="row" spacing={1} alignItems="center">
            <TextField
              size="small"
              value={key}
              label={t('organization:user.form.profileKey')}
              onChange={(e) => {
                const newVal = e.target.value;
                handleEditProfile(newVal, value);
                handleRemoveProfile(key);
              }}
              fullWidth
            />
            <TextField
              size="small"
              value={value}
              label={t('organization:user.form.profileValue')}
              onChange={(e) => handleEditProfile(key, e.target.value)}
              fullWidth
            />
            <IconButton 
              onClick={() => handleRemoveProfile(key)}
              color="error"
              sx={{ height: '40px' }}
            >
              <Delete />
            </IconButton>
          </Stack>
        ))}
      </Stack>

      <Stack direction="row" spacing={1} alignItems="center">
        <TextField
          size="small"
          value={newKey}
          label={t('organization:user.form.newKey')}
          onChange={(e) => setNewKey(e.target.value)}
          fullWidth
        />
        <TextField
          size="small"
          value={newValue}
          label={t('organization:user.form.newValue')}
          onChange={(e) => setNewValue(e.target.value)}
          fullWidth
        />
        <Button
          variant="outlined"
          startIcon={<Add />}
          onClick={handleAddProfile}
          disabled={!newKey.trim()}
          sx={{ height: '40px' }}
        >
          {t('common:add')}
        </Button>
      </Stack>
    </Box>
  );
};

export default UserProfilesEditor;
