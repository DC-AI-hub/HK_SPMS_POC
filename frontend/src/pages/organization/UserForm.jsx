import React, { useState, useEffect } from 'react';
import {
  Button,
  TextField,
  Stack,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  FormHelperText
} from '@mui/material';
import { useTranslation } from 'react-i18next';
import UserProfilesEditor from './UserProfilesEditor';
import roleService from '../../api/idm/roleService';
import DepartmentSelector from '../../components/common/DepartmentSelector';

/**
 * Form component for creating/editing users
 */
const UserForm = ({ user, onSubmit, onCancel, isEditMode = false }) => {

  console.log("-----", user)
  const { t } = useTranslation();
  const [formData, setFormData] = useState({
    id: user?.id || '',
    username: user?.username || '',
    email: user?.email || '',
    type: user?.type || 'STAFF',
    description: user?.description || '',
    userProfiles: user?.userProfiles || {},
    roles: user?.roles || [],
    functionalDepartment: user?.functionalDepartment || null,
    localDepartment: user?.localDepartment || null
  });
  const [availableRoles, setAvailableRoles] = useState([]);
  const [rolesLoading, setRolesLoading] = useState(false);

  useEffect(() => {
    const fetchRoles = async () => {
      setRolesLoading(true);
      try {
        const roles = await roleService.getAll();
        setAvailableRoles(roles.content || []);
      } catch (error) {
        // Error handled by the service
      } finally {
        setRolesLoading(false);
      }
    };
    fetchRoles();
  }, []);

  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: type === 'checkbox' ? checked : value
    }));
  };

  const [errors, setErrors] = useState({})

  const validate = () => {
    const next = {}
    if (!formData.username?.trim()) next.username = t('common:requiredField') || '必填项'
    if (!formData.email?.trim()) next.email = t('common:requiredField') || '必填项'
    if (!formData.type) next.type = t('common:requiredField') || '必填项'
    setErrors(next)
    return Object.keys(next).length === 0
  }

  const handleSubmit = (e) => {
    e.preventDefault()
    if (!validate()) return
    onSubmit(formData)
  }

  return (
    <form noValidate onSubmit={handleSubmit}>
      <Stack spacing={3}>
        <TextField
          name="username"
          label={t('organization:user.form.username')}
          value={formData.username}
          onChange={handleChange}
          required
          error={!!errors.username}
          helperText={errors.username}
          fullWidth
        />

        <TextField
          name="email"
          label={t('organization:user.form.email')}
          type="email"
          value={formData.email}
          onChange={handleChange}
          required
          error={!!errors.email}
          helperText={errors.email}
          fullWidth
        />

        <FormControl fullWidth error={!!errors.type}>
          <InputLabel>{t('organization:user.form.type')}</InputLabel>
          <Select
            name="type"
            value={formData.type}
            label={t('organization:user.form.type')}
            onChange={handleChange}
            required
          >
            <MenuItem value="STAFF">{t('organization:user.form.typeStaff')}</MenuItem>
            <MenuItem value="VENDOR">{t('organization:user.form.typeVendor')}</MenuItem>
            <MenuItem value="MACHINE">{t('organization:user.form.typeMachine')}</MenuItem>
          </Select>
          {errors.type && <FormHelperText>{errors.type}</FormHelperText>}
        </FormControl>

        <TextField
          name="description"
          label={t('organization:user.form.description')}
          value={formData.description}
          onChange={handleChange}
          multiline
          rows={3}
          fullWidth
        />

        <UserProfilesEditor 
          profiles={formData.userProfiles}
          onChange={(profiles) => setFormData(prev => ({
            ...prev,
            userProfiles: profiles
          }))}
        />
        
        <DepartmentSelector
          label={t('organization:user.form.functionalDept')}
          departmentType="FUNCTIONAL"
          value={formData.functionalDepartment}
          onChange={(dept) => setFormData(prev => ({
            ...prev,
            functionalDepartment: dept
          }))}
        />
        
        <DepartmentSelector
          label={t('organization:user.form.localDept')}
          departmentType="LOCAL"
          value={formData.localDepartment}
          onChange={(dept) => setFormData(prev => ({
            ...prev,
            localDepartment: dept
          }))}
        />

        {!rolesLoading && (
          <FormControl fullWidth>
            <InputLabel>{t('organization:user.form.roles')}</InputLabel>
            <Select
              multiple
              value={formData.roles}
              onChange={(e) => setFormData(prev => ({
                ...prev,
                roles: e.target.value
              }))}
              renderValue={(selected) => selected.join(', ')}
            >
              {availableRoles.map((role) => (
                <MenuItem key={role.id} value={role.name}>
                  {role.name}
                </MenuItem>
              ))}
            </Select>
          </FormControl>
        )}

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

export default UserForm;
