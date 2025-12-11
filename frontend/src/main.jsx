import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import { ThemeProvider as MuiThemeProvider } from '@mui/material/styles'
import { LocalizationProvider } from '@mui/x-date-pickers'
import { AdapterDayjs } from '@mui/x-date-pickers/AdapterDayjs'
import ThemeProvider, { useTheme } from './theme/ThemeContext.jsx'
import { ErrorProvider } from './contexts/ErrorContext'
import ValidationProvider from './contexts/ValidationContext.jsx'
import './index.css'
import './i18n/i18n'
import App from './App.jsx'

const ThemedApp = () => {
  const { theme } = useTheme();
  return (
    <MuiThemeProvider theme={theme}>
      <LocalizationProvider dateAdapter={AdapterDayjs}>
        <App />
      </LocalizationProvider>
    </MuiThemeProvider>
  );
};

createRoot(document.getElementById('root')).render(
  <StrictMode>
    <ThemeProvider>
      <ValidationProvider>
        <ErrorProvider>
          <ThemedApp />
        </ErrorProvider>
      </ValidationProvider>
    </ThemeProvider>
  </StrictMode>,
)
