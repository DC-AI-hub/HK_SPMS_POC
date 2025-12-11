import React from 'react'
import FormComponent from './FormComponent'
import { resolveCustomComponent } from './custom-registry.jsx'

// Decide how to render a form at runtime: custom component or form-js
export default function FormRuntimeSwitch({ schema, initialData, onSubmit, readOnly }) {
  // 1) 顶层自定义表单
  if (schema?.type === 'custom' && schema?.componentKey) {
    const Custom = resolveCustomComponent(schema.componentKey)
    if (!Custom) return null
    // Priority: passed initialData > schema.initialData
    const finalInitialData = initialData && Object.keys(initialData).length > 0 
      ? initialData 
      : (schema.initialData || {})
    return (
      <Custom
        {...(schema.props || {})}
        readOnly={!!readOnly}
        initialData={finalInitialData}
        onSubmit={onSubmit}
      />
    )
  }

  // 2) form-js schema 内部存在自定义字段（如 fields/components 中有 { type: 'custom', componentKey }）
  const nestedFields = [
    ...(Array.isArray(schema?.fields) ? schema.fields : []),
    ...(Array.isArray(schema?.components) ? schema.components : [])
  ]
  const customField = nestedFields.find(f => f && (f.type === 'custom' || f.fieldType === 'custom') && (f.componentKey || f.key))
  if (customField) {
    const componentKey = customField.componentKey || customField.key
    const Custom = resolveCustomComponent(componentKey)
    if (!Custom) return null
    // Priority: passed initialData > customField.initialData > schema.initialData
    const finalInitialData = initialData && Object.keys(initialData).length > 0 
      ? initialData 
      : (customField.initialData ?? schema?.initialData ?? {})
    return (
      <Custom
        {...(customField.props || {})}
        readOnly={!!readOnly}
        initialData={finalInitialData}
        onSubmit={onSubmit}
      />
    )
  }

  return <FormComponent schema={schema} data={initialData} onSubmit={onSubmit} />
}


