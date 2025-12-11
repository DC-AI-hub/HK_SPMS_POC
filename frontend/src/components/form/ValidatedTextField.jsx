import React, { useEffect } from 'react'
import { TextField } from '@mui/material'
import { useValidation } from '../../contexts/ValidationContext'

function matchBackendUniqueError(error) {
  const raw = error?.response?.data
  const msg = error?.response?.data?.message || error?.message || ''
  const text = typeof raw === 'string' ? raw : JSON.stringify(raw || {})
  const status = error?.response?.status
  const hit = [msg, text].filter(Boolean).some(s => /已存在|duplicate key|唯一|23505/i.test(s))
  return hit || status === 409 || status === 422 || status === 500
}

export default function ValidatedTextField({
  formKey,
  field,
  register,
  setError,
  setFocus,
  errors,
  label,
  required,
  unique,
  serverError,
  ...rest
}) {
  const { getFormRules, messages } = useValidation()
  const rulesForField = (getFormRules(formKey)[field] || {})

  const isRequired = required ?? rulesForField.required
  const isUnique = unique ?? rulesForField.unique

  useEffect(() => {
    if (!serverError || !isUnique) return
    if (matchBackendUniqueError(serverError)) {
      setError(field, { type: 'unique', message: messages.unique })
      setFocus && setFocus(field)
    }
  }, [serverError, isUnique, field, setError, setFocus, messages])

  const hasError = !!errors?.[field]
  const helperText = errors?.[field]?.message || (hasError && isRequired ? messages.required : '')

  return (
    <TextField
      label={label}
      required={isRequired}
      error={hasError}
      helperText={helperText}
      {...rest}
      {...register?.(field)}
    />
  )
}


