import React, { useState } from 'react';
import {
  Button,
  TextField,
  Stack,
  FormControl
} from '@mui/material';
import { useTranslation } from 'react-i18next';

/**
 * Form component for creating/editing roles
 */
const RoleForm = ({ role, onSubmit, onCancel, isEditMode = false }) => {
  const { t } = useTranslation();
  const [formData, setFormData] = useState({
    name: role?.name || '',
    description: role?.description || ''
  });

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    onSubmit(formData);
  };

  return (
    <form onSubmit={handleSubmit}>
      <Stack spacing={3}>
        <TextField
          name="name"
          label={t('common:name')}
          value={formData.name}
          onChange={handleChange}
          required
          fullWidth
        />

        <TextField
          name="description"
          label={t('common:description')}
          value={formData.description}
          onChange={handleChange}
          multiline
          rows={3}
          fullWidth
        />

        <Stack direction="row" spacing={2} justifyContent="flex-end">
          <Button
            variant="outlined"
            onClick={onCancel}
          >
            {t('common:cancel')}
          </Button>
          <Button
            type="submit"
            variant="contained"
          >
            {isEditMode ? t('common:save') : t('common:create')}
          </Button>
        </Stack>
      </Stack>
    </form>
  );
};

export default RoleForm;
