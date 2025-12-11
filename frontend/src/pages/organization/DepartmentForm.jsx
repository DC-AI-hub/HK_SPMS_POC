import React, { useEffect, useState } from 'react';
import { useForm, Controller } from 'react-hook-form';
import {
  TextField,
  Select,
  MenuItem,
  FormControl,
  InputLabel,
  Button,
  Box,
  FormControlLabel,
  Checkbox,
  CircularProgress,
  Alert,
  Typography,
  IconButton
} from '@mui/material';
import AddIcon from '@mui/icons-material/Add';
import DeleteIcon from '@mui/icons-material/Delete';
import { useTranslation } from 'react-i18next';
import orgService from '../../api/idm/organizationService';
import userService from "../../api/idm/userService";

const DepartmentForm = ({ department, onSubmit, onCancel }) => {
  const { t } = useTranslation();
  const { control, handleSubmit, watch, formState: { errors } } = useForm({
    defaultValues: department || {
      name: '',
      type: '',
      level: 1,
      parent: '',
      tags: null,
      active: true,
      departmentHeadId: null
    }
  });

  const [departments, setDepartments] = useState([]);
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const departmentType = watch('type');
  const departmentLevel = watch('level');

  // Fetch users for department head selector
  useEffect(() => {
    const fetchUsers = async () => {
      try {
        const usersResponse = await userService.search({ "query": ""});
        setUsers(usersResponse?.data || []);
      } catch (err) {
        console.error('Error fetching users:', err);
      }
    };

    fetchUsers();
  }, []);

  useEffect(() => {
    const fetchDepartments = async () => {
      try {
        setLoading(true);
        let departmentsData = [];

        if (departmentLevel === 1 && departmentType === "FUNCTIONAL") {
          const divisionsResponse = await orgService.getDivisions({ search: "" });//search=
          departmentsData = divisionsResponse?.content || [];
          console.log(divisionsResponse)
        } else if(departmentLevel === 1 && departmentType === "LOCAL"){
          const localEntity = await orgService.getCompanies({search: "", type:"BUSINESS_ENTITY"})
          console.log(localEntity);
          departmentsData = localEntity?.content || [];
        }
        else{
          const departmentsResponse = await orgService.getDepartments();
          departmentsData = departmentsResponse?.content || [];
        }
        setDepartments(departmentsData);
        setError(null);
      } catch (err) {
        setError(t('organization:department.fetchError'));
      } finally {
        setLoading(false);
      }
    };

    fetchDepartments();
  }, [departmentLevel, departmentType]);

  const getFilteredDepartments = () => {
    if (!departments.length) return [];
    if (departmentLevel === 1 && departmentType === "FUNCTIONAL") {
      return departments;
    }
    if(departmentLevel === 1 && departmentType === "LOCAL") {
      return departments;
    }
    return departments.filter(dept => {


      if (departmentType === 'FUNCTIONAL') {
        // For functional departments, only allow divisions (level 1) or lower level departments
        return dept.level === 1 || dept.level < departmentLevel;
      }
      // For other types, allow any valid parent department
      return dept.level < departmentLevel;
    });
  };

  return (
    <form component="form" noValidate onSubmit={handleSubmit(onSubmit)} sx={{ mt: 2 }}>
      {/* Basic Information Section */}

      <Controller
        name="name"
        control={control}
        rules={{ required: true }}
        render={({ field }) => (
          <TextField
            {...field}
            fullWidth
            label={t('organization:department.name')}
            margin="normal"
            required
            error={!!errors.name}
            helperText={errors.name && t('common:requiredField')}
          />
        )}
      />

      {/* Type and Level Section */}

      <Controller
        name="type"
        control={control}
        rules={{ required: true }}
        render={({ field }) => (
          <FormControl fullWidth margin="normal">
            <InputLabel>{t('organization:department.type')}</InputLabel>
            <Select
              {...field}
              label={t('organization:department.type')}
              required
              error={!!errors.type}
            >
              <MenuItem value="FUNCTIONAL">{t('organization:department.typeFunctional')}</MenuItem>
              <MenuItem value="LOCAL">{t('organization:department.typeLocal')}</MenuItem>
              <MenuItem value="TEAM">{t('organization:department.typeTeam')}</MenuItem>
              <MenuItem value="OTHER">{t('organization:department.typeOther')}</MenuItem>
            </Select>
          </FormControl>
        )}
      />

      <Controller
        name="level"
        control={control}
        rules={{
          required: true,
          min: 1,
          max: 10,
          valueAsNumber: true
        }}
        render={({ field }) => (
          <TextField
            {...field}
            fullWidth
            label={t('organization:department.level')}
            type="number"
            margin="normal"
            required
            error={!!errors.level}
            helperText={errors.level && t('common:invalidLevel')}
            inputProps={{ min: 1, max: 10 }}
          />
        )}
      />

      {/* Relationships Section */}

      {loading ? (
        <Box display="flex" justifyContent="center" my={2}>
          <CircularProgress size={24} />
        </Box>
      ) : error ? (
        <Alert severity="error" sx={{ my: 2 }}>
          {error}
        </Alert>
      ) : (
        <Controller
          name="parent"
          control={control}
          rules={{
            required: false,
            valueAsNumber: true
          }}
          render={({ field }) => (
            <FormControl fullWidth margin="normal">
              <InputLabel>{t('organization:department.parent')}</InputLabel>
              <Select
                {...field}
                label={t('organization:department.parent')}
                disabled={loading || !!error}
              >
                {getFilteredDepartments().map(dept => (
                  <MenuItem key={dept.id} value={dept.id}>
                    {dept.name} ({t(`organization.department.type${dept.type}`)})
                  </MenuItem>
                ))}
              </Select>
            </FormControl>
          )}
        />
      )}

      <Controller
        name="tags"
        control={control}
        render={({ field: { onChange, value } }) => {
          const tags = Array.isArray(value) ? value : [];

          const handleTagChange = (id, field, newValue) => {
            const updatedTags = tags.map(tag =>
              tag.id === id ? { ...tag, [field]: newValue } : tag
            );
            onChange(updatedTags);
          };

          const handleAddTag = () => {
            onChange([...tags, { id: Date.now(), key: '', value: '' }]);
          };

          const handleRemoveTag = (id) => {
            onChange(tags.filter(tag => tag.id !== id));
          };

          return (
            <Box sx={{ mt: 2 }}>
              <Typography variant="subtitle1" gutterBottom>
                {t('organization:department.tags')}
              </Typography>
              {tags.map(tag => (
                <Box key={tag.id} sx={{ display: 'flex', gap: 1, mb: 1 }}>
                  <TextField
                    value={tag.key}
                    onChange={(e) => handleTagChange(tag.id, 'key', e.target.value)}
                    label={t('organization:department.tagKey')}
                    size="small"
                    sx={{ flex: 1 }}
                  />
                  <TextField
                    value={tag.value}
                    onChange={(e) => handleTagChange(tag.id, 'value', e.target.value)}
                    label={t('organization:department.tagValue')}
                    size="small"
                    sx={{ flex: 1 }}
                  />
                  <IconButton onClick={() => handleRemoveTag(tag.id)}>
                    <DeleteIcon sx={{ color: '#E6223E' }} />
                  </IconButton>
                </Box>
              ))}
              <Button
                variant="outlined"
                onClick={handleAddTag}
                startIcon={<AddIcon />}
              >
                {t('organization:department.addTag')}
              </Button>
            </Box>
          );
        }}
      />

      <Controller
        name="departmentHeadId"
        control={control}
        defaultValue={department?.departmentHeadId??""}
        rules={{
          required: false,
          valueAsNumber: true
        }}
        render={({ field }) => (
          <FormControl fullWidth margin="normal">
            <InputLabel>{t('organization:department.head')}</InputLabel>
            <Select {...field} label={t('organization:department.head')} >
              {users?.size ==0 &&  <MenuItem value="">{t('common:none')}</MenuItem>}
              {users.map(user => (
                <MenuItem key={user.id} value={user.id}>
                  @{user.username} - ({user.email})
                </MenuItem>
              ))}
            </Select>
          </FormControl>
        )}
      />


      {/* Status Section */}

      <Controller
        name="active"
        control={control}
        render={({ field }) => (
          <FormControlLabel
            control={<Checkbox {...field} checked={field.value} />}
            label={t('organization:department.active')}
          />
        )}
      />


      {/* Action Buttons */}
      <Box sx={{ mt: 2, display: 'flex', gap: 2 }}>
        <Button variant="outlined" onClick={onCancel}>
          {t('common:cancel')}
        </Button>
        <Button variant="contained" type="submit">
          {t('common:save')}
        </Button>
      </Box>
    </form>
  );
};

export default DepartmentForm;
