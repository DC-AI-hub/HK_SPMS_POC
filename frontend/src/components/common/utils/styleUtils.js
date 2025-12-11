import { useTheme } from '@mui/material/styles';

/**
 * Style utilities for consistent component styling
 * These utilities work with the existing theme system
 */

/**
 * Hook to get common spacing values from theme
 * @returns {Object} Spacing utilities
 */
export const useSpacing = () => {
  const theme = useTheme();
  
  return {
    xs: theme.spacing(0.5),
    sm: theme.spacing(1),
    md: theme.spacing(2),
    lg: theme.spacing(3),
    xl: theme.spacing(4),
    xxl: theme.spacing(6)
  };
};

/**
 * Hook to get common border radius values from theme
 * @returns {Object} Border radius utilities
 */
export const useBorderRadius = () => {
  const theme = useTheme();
  
  return {
    sm: theme.shape.borderRadius / 2,
    md: theme.shape.borderRadius,
    lg: theme.shape.borderRadius * 1.5,
    xl: theme.shape.borderRadius * 2,
    round: '50%'
  };
};

/**
 * Hook to get common shadow values
 * @returns {Object} Shadow utilities
 */
export const useShadows = () => {
  const theme = useTheme();
  
  return {
    sm: theme.shadows[1],
    md: theme.shadows[2],
    lg: theme.shadows[4],
    xl: theme.shadows[8],
    none: 'none'
  };
};

/**
 * Hook to get common color utilities
 * @returns {Object} Color utilities
 */
export const useColors = () => {
  const theme = useTheme();
  
  return {
    primary: theme.palette.primary.main,
    error: theme.palette.error.main,
    background: {
      default: theme.palette.background.default,
      paper: theme.palette.background.paper,
      header: theme.palette.background.header,
      footer: theme.palette.background.footer
    },
    text: {
      primary: theme.palette.text.primary,
      secondary: theme.palette.text.secondary,
      light: theme.palette.text.light
    }
  };
};

/**
 * Common component styles that can be reused
 */
export const commonStyles = {
  // Form styles
  formContainer: (theme) => ({
    display: 'flex',
    flexDirection: 'column',
    gap: theme.spacing(2),
    width: '100%'
  }),
  
  formField: (theme) => ({
    marginBottom: theme.spacing(2)
  }),
  
  // Card styles
  card: (theme) => ({
    borderRadius: theme.shape.borderRadius,
    boxShadow: theme.shadows[2],
    padding: theme.spacing(3)
  }),
  
  // Button styles
  button: (theme) => ({
    borderRadius: theme.shape.borderRadius,
    textTransform: 'none',
    fontWeight: 600
  }),
  
  // Layout styles
  container: (theme) => ({
    padding: theme.spacing(3),
    maxWidth: 1200,
    margin: '0 auto'
  }),
  
  section: (theme) => ({
    marginBottom: theme.spacing(4)
  }),
  
  // Responsive breakpoints
  responsive: {
    mobile: '@media (max-width: 600px)',
    tablet: '@media (max-width: 900px)',
    desktop: '@media (min-width: 901px)'
  }
};

/**
 * Utility to merge styles with theme
 * @param {Function} styleFunction - Style function that accepts theme
 * @param {Object} additionalStyles - Additional styles to merge
 * @returns {Function} Merged style function
 */
export const mergeStyles = (styleFunction, additionalStyles) => (theme) => ({
  ...styleFunction(theme),
  ...additionalStyles
});

/**
 * Utility to create responsive styles
 * @param {Object} styles - Styles for different breakpoints
 * @returns {Object} Responsive styles object
 */
export const responsiveStyles = (styles) => {
  const { mobile, tablet, desktop, ...baseStyles } = styles;
  
  return {
    ...baseStyles,
    [commonStyles.responsive.mobile]: mobile || {},
    [commonStyles.responsive.tablet]: tablet || {},
    [commonStyles.responsive.desktop]: desktop || {}
  };
};
