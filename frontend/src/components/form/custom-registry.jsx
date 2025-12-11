// Registry for custom form components (JSX file because we render placeholders)
import React from 'react'
import { Alert } from '@mui/material'
import TimecardForm from './timecard/TimecardForm'

export const customFormRegistry = {
  // 自定义表单注册表
  'timecard-form': TimecardForm
}

export function resolveCustomComponent(componentKey) {
  return customFormRegistry[componentKey] || null
}

// Catalog for admin UI: list selectable custom forms with defaults and labels
export const customFormCatalog = [
  {
    key: 'timecard-form',
    label: 'Timecard Management Form',
    description: 'Complete timecard management system with calendar view, project entries, and summary reports',
    defaults: {
      props: {},
      initialData: {}
    }
  }
]
