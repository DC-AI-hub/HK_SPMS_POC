import React from 'react'
import FormPreview from './FormPreview'
import { resolveCustomComponent } from './custom-registry.jsx'

// Decide how to render a form in preview: custom component (readOnly) or form-js viewer
export default function FormPreviewSwitch({ schema, data }) {
  if (schema?.type === 'custom' && schema?.componentKey) {
    const Custom = resolveCustomComponent(schema.componentKey)
    if (!Custom) return null
    return <Custom {...(schema.props || {})} readOnly initialData={schema.initialData ?? data} />
  }

  return <FormPreview schema={schema} data={data} readOnly />
}


