import React, { useCallback, useEffect, useMemo, useState } from 'react'
import { Box, CircularProgress, Alert } from '@mui/material'
import FormRuntimeSwitch from './FormRuntimeSwitch'
import formSchemaService from '../../api/process/formSchemaService'
import formService from '../../api/process/formService'
import processInstanceService from '../../api/process/processInstanceService'

/**
 * Unified dynamic form renderer with two data sources and two submission modes
 * Data sources: taskId or formKey(+version)
 * Submit modes: start(process) or task(complete)
 */
export default function DynamicFormRenderer({
  taskId,
  formKey,
  formVersion,
  mode = 'start', // 'start' | 'task'
  definitionId, // required for start
  instanceId, // required for task
  initialData,
  onSuccess,
  onError
}) {
  const [schema, setSchema] = useState(null)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)

  useEffect(() => {
    let cancelled = false
    async function load() {
      try {
        setLoading(true)
        setError(null)
        if (taskId) {
          const res = await formSchemaService.getFormSchemaByTaskId(taskId)
          if (!cancelled) setSchema(res?.data ?? res)
          return
        }
        if (formKey) {
          const res = formVersion
            ? await formService.getVersion(formKey, formVersion)
            : await formService.getLatestVersion(formKey)
          if (!cancelled) setSchema(res?.schema ? JSON.parse(res.schema) : (res?.data?.schema ? JSON.parse(res.data.schema) : res))
          return
        }
        setError('缺少 taskId 或 formKey')
      } catch (e) {
        if (!cancelled) setError(e?.message || '表单加载失败')
      } finally {
        if (!cancelled) setLoading(false)
      }
    }
    load()
    return () => { cancelled = true }
  }, [taskId, formKey, formVersion])

  const handleSubmit = useCallback(async (data) => {
    try {
      if (mode === 'start') {
        if (!definitionId) throw new Error('缺少 definitionId')
        await processInstanceService.startProcessInstance({ definitionId, formVariable: data, variable: null })
      } else if (mode === 'task') {
        if (!instanceId || !taskId) throw new Error('缺少 instanceId 或 taskId')
        await processInstanceService.completeTask(instanceId, taskId, data)
      } else {
        throw new Error(`未知模式: ${mode}`)
      }
      onSuccess && onSuccess(data)
    } catch (e) {
      setError(e?.message || '提交失败')
      onError && onError(e)
    }
  }, [mode, definitionId, instanceId, taskId, onSuccess, onError])

  if (loading) return (
    <Box className='w-full h-full flex items-center justify-center'>
      <CircularProgress />
    </Box>
  )
  if (error) return <Alert severity='error'>{error}</Alert>
  if (!schema) return null

  return (
    <FormRuntimeSwitch schema={schema} initialData={initialData} onSubmit={handleSubmit} />
  )
}


