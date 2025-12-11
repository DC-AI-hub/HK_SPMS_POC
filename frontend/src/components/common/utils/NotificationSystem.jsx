import React, { createContext, useContext, useMemo, useState } from 'react'
import { Snackbar, Alert } from '@mui/material'

const ToastContext = createContext({ success: () => {}, error: () => {}, warn: () => {} })

export function ToastProvider({ children }) {
  const [state, setState] = useState({ open: false, message: '', severity: 'success' })
  const api = useMemo(() => ({
    success: (m) => setState({ open: true, message: m, severity: 'success' }),
    error: (m) => setState({ open: true, message: m, severity: 'error' }),
    warn: (m) => setState({ open: true, message: m, severity: 'warning' })
  }), [])
  return (
    <ToastContext.Provider value={api}>
      {children}
      <Snackbar open={state.open} autoHideDuration={2500} onClose={() => setState(s => ({ ...s, open: false }))} anchorOrigin={{ vertical: 'top', horizontal: 'right' }}>
        <Alert severity={state.severity} variant='filled'>{state.message}</Alert>
      </Snackbar>
    </ToastContext.Provider>
  )
}

export function useToast() { return useContext(ToastContext) }


