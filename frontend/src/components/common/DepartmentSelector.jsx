import React, { useState, useEffect } from 'react';
import { Autocomplete, TextField, CircularProgress } from '@mui/material';
import departmentService from '../../api/idm/departmentService';
import { useTranslation } from 'react-i18next';

const DepartmentSelector = ({ label, departmentType, value, onChange, ...props }) => {
  const { t } = useTranslation();
  const [departments, setDepartments] = useState([]);
  const [loading, setLoading] = useState(false);
  const [searchTerm, setSearchTerm] = useState('');

  useEffect(() => {
    let active = true;
    
    const fetchDepartments = async () => {
      setLoading(true);
      try {
        const params = {
          search: searchTerm,
          type: departmentType
        };
        const response = await departmentService.list(params);
        if (active) {
          setDepartments(response.data.content || []);
        }
      } catch (error) {
        console.error('Error loading departments:', error);
      } finally {
        setLoading(false);
      }
    };
    
    const timerId = setTimeout(() => {
      fetchDepartments();
    }, 300);
    
    return () => {
      active = false;
      clearTimeout(timerId);
    };
  }, [searchTerm, departmentType]);

  //console.log("------" , value)

  return (
    <Autocomplete
      options={departments}
      loading={loading}
      getOptionLabel={(option) => option.name}
      value={value}
      onChange={(event, newValue) => {
        onChange(newValue);
      }}
      onInputChange={(event, newInputValue) => {
        setSearchTerm(newInputValue);
      }}
      renderInput={(params) => (
        <TextField
          {...params}
          label={label}
          InputProps={{
            ...params.InputProps,
            endAdornment: (
              <>
                {loading ? <CircularProgress color="inherit" size={20} /> : null}
                {params.InputProps.endAdornment}
              </>
            ),
          }}
        />
      )}
      {...props}
    />
  );
};

export default DepartmentSelector;
