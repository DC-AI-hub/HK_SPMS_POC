import React from 'react';
import { HashRouter as Router, Routes, Route } from 'react-router-dom';
import AdminLayout from './layouts/AdminLayout';
import Dashboard from './pages/Dashboard';
import Organization from './pages/Organization';
import Access from './pages/Access';
import Process from './pages/Process';
import FormComponent from './components/form/FormComponent';
import UserProcess from './pages/UserProcess';
import SystemConfiguration from './pages/SystemConfiguration';
import Reports from './pages/reports/Reports';
import Project from './pages/Project';
import { ScreenSizeProvider } from './contexts/ScreenSizeContext';
import { ConfigProvider } from './contexts/ConfigContext';

/**
 * Main application component
 * @returns {JSX.Element} Application structure
 */
function App() {
  return (
    <ScreenSizeProvider>
      <ConfigProvider>
        <Router>
          <Routes>
            <Route path="*" element={
              <AdminLayout>
                <Routes>
                  <Route path="/dashboard" element={<Dashboard />} />
                  <Route path="/organization" element={<Organization />} />
                  <Route path="/access" element={<Access />} />
                  <Route path="/process" element={<Process />} />
                  <Route path="/user-process" element={<UserProcess />} />
                  <Route path="/form" element={<FormComponent />} />
                  <Route path="/system-configuration" element={<SystemConfiguration />} />
                  <Route path="/reports" element={<Reports />} />
                  <Route path="/project" element={<Project />} />
                  <Route path="*" element={<Dashboard />} />      
                </Routes>
              </AdminLayout>
            } />
          </Routes>
        </Router>
      </ConfigProvider>
    </ScreenSizeProvider>
  );
}

export default App;
