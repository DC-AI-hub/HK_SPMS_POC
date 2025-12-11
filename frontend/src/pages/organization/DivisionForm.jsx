import React from 'react';
import { useForm, Controller } from 'react-hook-form';
import { useTranslation } from 'react-i18next';
import { TextField, Select, MenuItem, FormControl, InputLabel, Button, Box, Checkbox, FormControlLabel, FormHelperText } from '@mui/material';
import organizationService from '../../api/idm/organizationService';
import { DivisionType } from './DivisionType';
import ValidatedTextField from '../../components/form/ValidatedTextField';
import ValidatedSelect from '../../components/form/ValidatedSelect';
import { useError } from '../../contexts/ErrorContext';

export default function DivisionForm({ division, onSubmit, onCancel, isEditMode }) {
  const { control, handleSubmit, reset, setError, setFocus, formState: { errors } } = useForm();
  const { t } = useTranslation();
  const [companies, setCompanies] = React.useState([]);
  const [users, setUsers] = React.useState([]);
  const { addError } = useError();

  React.useEffect(() => {
    // Fetch only GROUP type companies
    organizationService.getCompanies( {type: "GROUP"} )
      .then(response =>{ 
        setCompanies(response.content || [])
        console.log(response)
      })
      .catch(error => console.error('Error fetching companies:', error));
      
    // Fetch users for Division Head selector
    organizationService.getUsers()
      .then(response => setUsers(response.content || []))
      .catch(error => console.error('Error fetching users:', error));
  }, []);

  /*
  React.useEffect(() => {
    if (division) {
      reset(division);
    }
  }, [division, reset]);*/

  const [submitError, setSubmitError] = React.useState(null)

  return (
    <form noValidate onSubmit={handleSubmit(async (data) => {
      try {
        await onSubmit(data);
      } catch (err) {
        setSubmitError(err)
        const raw = err?.response?.data;
        const msg = err?.response?.data?.message || err?.message || '';
        const text = typeof raw === 'string' ? raw : JSON.stringify(raw || {});
        const uniqueHit = [msg, text]
          .filter(Boolean)
          .some(s => /已存在|duplicate key|唯一|23505/i.test(s));
        const status = err?.response?.status;
        if (uniqueHit || status === 409 || status === 422 || status === 500) {
          setError('name', { type: 'unique', message: t('common:alreadyExists') || '该名称已存在' });
          setFocus('name');
        }
        throw err;
      }
    })}>
      <Controller
        name="name"
        control={control}
        defaultValue=""
        rules={{ required: true }}
        render={({ field }) => (
          <ValidatedTextField
            formKey="division"
            field="name"
            label={t('organization:division.name')}
            fullWidth
            margin="normal"
            disabled={isEditMode}
            errors={errors}
            setError={setError}
            setFocus={setFocus}
            serverError={submitError}
            value={field.value}
            onChange={field.onChange}
          />
        )}
      />

      <Controller
        name="type"
        control={control}
        defaultValue="BUSINESS"
        rules={{ 
          required: true,
            validate: value => Object.values(DivisionType).includes(value)
        }}
        render={({ field }) => (
          <ValidatedSelect
            formKey="division"
            field="type"
            label={t('organization:division.type')}
            errors={errors}
            value={field.value}
            onChange={field.onChange}
          >
            <MenuItem value={DivisionType.CORE}>{t('organization:division.types.core')}</MenuItem>
            <MenuItem value={DivisionType.BUSINESS}>{t('organization:division.types.business')}</MenuItem>
            <MenuItem value={DivisionType.TECHNOLOGY}>{t('organization:division.types.technology')}</MenuItem>
            <MenuItem value={DivisionType.STRATEGY}>{t('organization:division.types.strategy')}</MenuItem>
            <MenuItem value={DivisionType.SUPPORT}>{t('organization:division.types.support')}</MenuItem>
          </ValidatedSelect>
        )}
      />

      <Controller
        name="companyId"
        control={control}
        defaultValue=""
        rules={{ required: true }}
        render={({ field }) => (
          <ValidatedSelect
            formKey="division"
            field="companyId"
            label={t('organization:company.select')}
            errors={errors}
            value={field.value}
            onChange={field.onChange}
          >
            {companies.map(company => (
              <MenuItem key={company.id} value={company.id}>
                {company.name}
              </MenuItem>
            ))}
          </ValidatedSelect>
        )}
      />

      <Controller
        name="active"
        control={control}
        defaultValue={true}
        render={({ field }) => (
          <FormControlLabel
            control={<Checkbox {...field} checked={field.value} />}
            label={t('organization:division.active')}
          />
        )}
      />

      <Controller
        name="divisionHeadId"
        control={control}
        defaultValue=""
        render={({ field }) => (
          <FormControl fullWidth margin="normal">
            <InputLabel>{t('organization:division.head')}</InputLabel>
            <Select {...field} label={t('organization:division.head')}>
              <MenuItem value="">{t('common:none')}</MenuItem>
              {users.map(user => (
                <MenuItem key={user.id} value={user.id}>
                  {user.name}
                </MenuItem>
              ))}
            </Select>
          </FormControl>
        )}
      />

      <Box sx={{ mt: 2, display: 'flex', gap: 2 }}>
        <Button variant="contained" type="submit">
          {t('common:save')}
        </Button>
        <Button variant="outlined" onClick={onCancel}>
          {t('common:cancel')}
        </Button>
      </Box>
    </form>
  );
}
