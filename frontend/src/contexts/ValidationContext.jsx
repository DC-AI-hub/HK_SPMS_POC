import React, { createContext, useContext, useMemo, useState, useCallback, useEffect } from 'react'
import { useTranslation } from 'react-i18next'

const defaultMessages = {
  required: '必填项',
  pattern: '格式不正确',
  minLength: '长度太短',
  maxLength: '长度太长',
  unique: '该名称已存在'
}

const defaultRules = {
  company: {
    name: { required: true, unique: true }
  },
  division: {
    name: { required: true, unique: true }
  }
}

const ValidationContext = createContext()

export function ValidationProvider({ children, initialRules, initialMessages }) {
  const [rulesByForm, setRulesByForm] = useState(initialRules || defaultRules)
  const { t, i18n } = useTranslation()
  const [messages, setMessages] = useState(initialMessages || defaultMessages)

  // sync i18n messages
  useEffect(() => {
    setMessages(prev => ({
      ...prev,
      required: t('common:requiredField', { defaultValue: prev.required || '必填项' }),
      pattern: t('common:invalidFormat', { defaultValue: prev.pattern || '格式不正确' }),
      minLength: t('common:minLength', { defaultValue: prev.minLength || '长度太短' }),
      maxLength: t('common:maxLength', { defaultValue: prev.maxLength || '长度太长' }),
      unique: t('common:alreadyExists', { defaultValue: prev.unique || '该名称已存在' })
    }))
  }, [t, i18n.language])

  const registerRules = useCallback((formKey, rules) => {
    setRulesByForm(prev => ({ ...prev, [formKey]: { ...(prev[formKey] || {}), ...rules } }))
  }, [])

  const updateMessages = useCallback((next) => {
    setMessages(prev => ({ ...prev, ...next }))
  }, [])

  const getFormRules = useCallback((formKey) => rulesByForm[formKey] || {}, [rulesByForm])

  const value = useMemo(() => ({
    getFormRules,
    registerRules,
    updateMessages,
    messages
  }), [getFormRules, registerRules, updateMessages, messages])

  return (
    <ValidationContext.Provider value={value}>
      {children}
    </ValidationContext.Provider>
  )
}

export function useValidation() {
  const ctx = useContext(ValidationContext)
  if (!ctx) throw new Error('useValidation must be used within ValidationProvider')
  return ctx
}

export default ValidationProvider


