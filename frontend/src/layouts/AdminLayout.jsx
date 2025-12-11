import React, { useState, useEffect } from 'react';
import Header from '../components/Header';
import Footer from '../components/Footer';
import Sidebar from '../components/Sidebar';
import MobileContextMenu from '../components/MobileContextMenu';
import { useTheme } from '../theme/ThemeContext';
import { useScreenSize } from '../contexts/ScreenSizeContext';

/**
 * Main admin layout component
 * @param {Object} props - Component props
 * @param {ReactNode} props.children - Content to be rendered in main area
 * @returns {JSX.Element} Admin layout structure
 */
const AdminLayout = ({ children }) => {
  const [sidebarOpen, setSidebarOpen] = useState(false);
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);
  const { theme } = useTheme();
  const { isMobileScreen } = useScreenSize();

  useEffect(() => {
    const savedState = localStorage.getItem('sidebarOpen');
    if (savedState !== null) {
      setSidebarOpen(savedState === 'true');
    }
  }, []);

  useEffect(() => {
    localStorage.setItem('sidebarOpen', sidebarOpen);
  }, [sidebarOpen]);

  const toggleSidebar = () => {
    setSidebarOpen(!sidebarOpen);
  };

  const toggleMobileMenu = () => {
    setMobileMenuOpen(!mobileMenuOpen);
  };

  const closeMobileMenu = () => {
    setMobileMenuOpen(false);
  };

  return (
    <div className="flex h-screen" style={{ backgroundColor: theme.palette.background.default }} >
      {/* Desktop Sidebar - only show on larger screens */}
      {!isMobileScreen && (
        <div className={`transition-all duration-300 ${sidebarOpen ? '' : 'w-0'}`}>
          <Sidebar />
        </div>
      )}
      
      <div className="flex flex-1 flex-col overflow-hidden" >
        {/* Header */}
        <Header 
          onToggleSidebar={toggleSidebar} 
          onToggleMobileMenu={toggleMobileMenu}
          isMobileScreen={isMobileScreen}
          className="z-10" 
        />
        
        {/* Mobile Context Menu */}
        {isMobileScreen && (
          <MobileContextMenu 
            open={mobileMenuOpen} 
            onClose={closeMobileMenu} 
          />
        )}
        
        {/* Main Content */}
        <main className="flex-1 overflow-x-hidden overflow-y-auto" >
          {children}
        </main>
        <Footer />
      </div>
    </div>
  );
};

export default AdminLayout;
