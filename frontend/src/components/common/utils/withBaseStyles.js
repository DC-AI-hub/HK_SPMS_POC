import React from 'react';
import { useTheme } from '@mui/material/styles';
import { useSpacing, useBorderRadius, useColors } from './styleUtils';

/**
 * Higher-Order Component that provides base styling utilities
 * @param {React.ComponentType} WrappedComponent - Component to wrap
 * @returns {React.ComponentType} Enhanced component with style utilities
 */
const withBaseStyles = (WrappedComponent) => {
  const WithBaseStyles = (props) => {
    const theme = useTheme();
    const spacing = useSpacing();
    const borderRadius = useBorderRadius();
    const colors = useColors();

    // Base style utilities that can be used by the wrapped component
    const styleUtils = {
      theme,
      spacing,
      borderRadius,
      colors,
      
      // Common style patterns
      getCardStyles: () => ({
        borderRadius: borderRadius.md,
        boxShadow: theme.shadows[2],
        padding: spacing.md,
        backgroundColor: colors.background.paper
      }),
      
      getFormStyles: () => ({
        display: 'flex',
        flexDirection: 'column',
        gap: spacing.md,
        width: '100%'
      }),
      
      getButtonStyles: () => ({
        borderRadius: borderRadius.md,
        textTransform: 'none',
        fontWeight: 600
      }),
      
      getContainerStyles: () => ({
        padding: spacing.lg,
        maxWidth: 1200,
        margin: '0 auto'
      }),
      
      // Responsive utilities
      responsive: {
        mobile: '@media (max-width: 600px)',
        tablet: '@media (max-width: 900px)',
        desktop: '@media (min-width: 901px)'
      }
    };

    return <WrappedComponent {...props} styleUtils={styleUtils} />;
  };

  // Set display name for easier debugging
  const displayName = WrappedComponent.displayName || WrappedComponent.name || 'Component';
  WithBaseStyles.displayName = `WithBaseStyles(${displayName})`;

  return WithBaseStyles;
};

export default withBaseStyles;

/**
 * Hook version for functional components
 * @returns {Object} Style utilities object
 */
export const useBaseStyles = () => {
  const theme = useTheme();
  const spacing = useSpacing();
  const borderRadius = useBorderRadius();
  const colors = useColors();

  return {
    theme,
    spacing,
    borderRadius,
    colors,
    
    getCardStyles: () => ({
      borderRadius: borderRadius.md,
      boxShadow: theme.shadows[2],
      padding: spacing.md,
      backgroundColor: colors.background.paper
    }),
    
    getFormStyles: () => ({
      display: 'flex',
      flexDirection: 'column',
      gap: spacing.md,
      width: '100%'
    }),
    
    getButtonStyles: () => ({
      borderRadius: borderRadius.md,
      textTransform: 'none',
      fontWeight: 600
    }),
    
    getContainerStyles: () => ({
      padding: spacing.lg,
      maxWidth: 1200,
      margin: '0 auto'
    }),
    
    responsive: {
      mobile: '@media (max-width: 600px)',
      tablet: '@media (max-width: 900px)',
      desktop: '@media (min-width: 901px)'
    }
  };
};
