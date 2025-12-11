import React from 'react';
import { Grid, Box } from '@mui/material';

/**
 * GridLayout - A responsive grid layout component
 * Provides a flexible grid system with consistent spacing and responsive behavior
 * 
 * @param {Object} props
 * @param {React.ReactNode} props.children - Grid items
 * @param {number} [props.spacing] - Spacing between grid items (0-10)
 * @param {number} [props.columns] - Number of columns for desktop view
 * @param {number} [props.tabletColumns] - Number of columns for tablet view
 * @param {number} [props.mobileColumns] - Number of columns for mobile view
 * @param {boolean} [props.centered] - Whether to center grid items
 * @param {string} [props.className] - Additional CSS classes
 * @param {Object} [props.style] - Additional inline styles
 */
const GridLayout = ({
  children,
  spacing = 2,
  columns = 3,
  tabletColumns = 2,
  mobileColumns = 1,
  centered = false,
  className = '',
  style = {},
  ...props
}) => {
  return (
    <Box
      sx={{
        width: '100%',
        display: 'flex',
        justifyContent: centered ? 'center' : 'flex-start'
      }}
      className={className}
      style={style}
      {...props}
    >
      <Grid
        container
        spacing={spacing}
        sx={{
          width: '100%',
          maxWidth: '100%'
        }}
      >
        {React.Children.map(children, (child, index) => (
          <Grid
            item
            xs={12}
            sm={12 / mobileColumns}
            md={12 / tabletColumns}
            lg={12 / columns}
            sx={{
              display: 'flex',
              flexDirection: 'column'
            }}
          >
            {child}
          </Grid>
        ))}
      </Grid>
    </Box>
  );
};

export default GridLayout;
