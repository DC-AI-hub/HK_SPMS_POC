import { createTheme } from '@mui/material/styles';

// Light theme
export const lightTheme = createTheme({
  palette: {
    mode: 'light',
    primary: { main: '#13426B' },
    error: { main: '#E6223E' },
    background: { 
      default: '#FFFFFF', 
      paper: '#FFFFFF', 
      header: '#E6E8E9', 
      footer: '#E6E8E9' 
    },
    text: { 
      primary: '#333D47', 
      secondary: '#5B636B' ,
      light: '#ffffff'
    }
  },
  shape: { borderRadius: 10 },
  typography: {
    fontFamily: "'FS Elliot Pro', Arial, 'Heiti TC', 'Heiti SC', 'Microsoft JhengHei', 'Microsoft Yahei', sans-serif",
    h1: { fontSize: '2.25rem', fontWeight: 700 },
    h2: { fontSize: '1.5rem', fontWeight: 700 },
    h3: { fontSize: '1.25rem', fontWeight: 600 }
  },
  components: {
    MuiOutlinedInput: {
      styleOverrides: {
        root: { backgroundColor: '#E6E8E9' }
      }
    },
    MuiButton: {
      styleOverrides: {
        root: { textTransform: 'none', borderRadius: 10 }
      }
    },
    MuiPaper: {
      styleOverrides: {
        root: { boxShadow: '0 1px 3px rgba(0,0,0,0.1), 0 1px 2px rgba(0,0,0,0.06)' }
      }
    },
    MuiLinearProgress: {
      styleOverrides: {
        colorPrimary: { backgroundColor: '#FDE9EC' },
        barColorPrimary: { backgroundColor: '#E6223E' },
        colorSecondary: { backgroundColor: '#FDE9EC' },
        barColorSecondary: { backgroundColor: '#E6223E' }
      }
    }
  }
});

// Dark theme
export const darkTheme = createTheme({
  palette: {
    mode: 'dark',
    primary: { main: '#13426B' },
    error: { main: '#E6223E' },
    background: { 
      default: '#1E1E1F', 
      paper: '#2B2B2C', 
      header: '#565657', 
      footer: '#565657' 
    },
    text: { 
      primary: '#F7F7F8', 
      secondary: '#C6C6C6' 
    }
  },
  shape: { borderRadius: 10 },
  typography: {
    fontFamily: "'FS Elliot Pro', Arial, 'Heiti TC', 'Heiti SC', 'Microsoft JhengHei', 'Microsoft Yahei', sans-serif"
  },
  components: {
    MuiOutlinedInput: { 
      styleOverrides: { 
        root: { backgroundColor: '#565657' } 
      } 
    },
    MuiButton: { 
      styleOverrides: { 
        root: { textTransform: 'none', borderRadius: 10 } 
      } 
    },
    MuiPaper: { 
      styleOverrides: { 
        root: { boxShadow: '0 1px 3px rgba(0,0,0,0.6), 0 1px 2px rgba(0,0,0,0.5)' } 
      } 
    },
    MuiLinearProgress: {
      styleOverrides: {
        colorPrimary: { backgroundColor: '#5C0E19' },
        barColorPrimary: { backgroundColor: '#E6223E' },
        colorSecondary: { backgroundColor: '#5C0E19' },
        barColorSecondary: { backgroundColor: '#E6223E' }
      }
    }
  }
});
