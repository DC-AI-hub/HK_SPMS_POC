import React from 'react'
import { FormControl, InputLabel, Select, FormHelperText } from '@mui/material'
import { useValidation } from '../../contexts/ValidationContext'

export default function ValidatedSelect({
  formKey,
  field,
  label,
  errors,
  required,
  children,
  value,
  onChange,
  fullWidth = true,
  margin = 'normal',
  disabled,
  ...rest
}) {
  const { getFormRules, messages } = useValidation()
  const rulesForField = (getFormRules(formKey)[field] || {})
  const isRequired = required ?? rulesForField.required
  const hasError = !!errors?.[field]
  const helperText = errors?.[field]?.message || (hasError ? (messages.required || '') : '')

  return (
    <FormControl fullWidth={fullWidth} margin={margin} error={hasError} disabled={disabled}>
      <InputLabel>{label}</InputLabel>
      <Select
        label={label}
        value={value}
        onChange={onChange}
        required={isRequired}
        {...rest}
      >
        {children}
      </Select>
      {hasError && <FormHelperText>{helperText}</FormHelperText>}
    </FormControl>
  )
}


