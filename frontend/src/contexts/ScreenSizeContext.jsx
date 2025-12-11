import React, { createContext, useContext, useState, useEffect } from 'react';
import { useTheme, useMediaQuery } from '@mui/material';

/**
 * ScreenSizeContext for managing responsive screen size detection
 */
const ScreenSizeContext = createContext();

/**
 * ScreenSizeProvider component that provides screen size context to children
 * @param {Object} props - Component props
 * @param {ReactNode} props.children - Child components
 * @returns {JSX.Element} ScreenSizeProvider component
 */
export const ScreenSizeProvider = ({ children }) => {
  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down('md')); // Below 900px
  const isTablet = useMediaQuery(theme.breakpoints.between('md', 'lg')); // 900px - 1200px
  const isDesktop = useMediaQuery(theme.breakpoints.up('lg')); // 1200px and above
  const [screenSize, setScreenSize] = useState({ 
    isMobile, 
    isTablet, 
    isDesktop,
    // Maintain backward compatibility with existing code
    isMobileScreen: isMobile 
  });

  useEffect(() => {
    setScreenSize({ 
      isMobile, 
      isTablet, 
      isDesktop,
      isMobileScreen: isMobile 
    });
  }, [isMobile, isTablet, isDesktop]);

  return (
    <ScreenSizeContext.Provider value={screenSize}>
      {children}
    </ScreenSizeContext.Provider>
  );
};

/**
 * Custom hook to use screen size context
 * @returns {Object} Screen size context values with isMobile, isTablet, isDesktop, and isMobileScreen (backward compatibility) flags
 */
export const useScreenSize = () => {
  const context = useContext(ScreenSizeContext);
  if (!context) {
    throw new Error('useScreenSize must be used within a ScreenSizeProvider');
  }
  return context;
};
