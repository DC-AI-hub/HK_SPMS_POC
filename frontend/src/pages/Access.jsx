import React, { useState, useEffect } from 'react';
import { 
  Box, useTheme
} from '@mui/material';
import RoleDialog from './access/components/RoleDialog';
import roleService from '../api/idm/roleService';
import userService from '../api/idm/userService';
import { useTranslation } from 'react-i18next';
import RolesTab from './access/RolesTab';
import PermissionsTab from './access/PermissionsTab';
import HierarchyTab from './access/HierarchyTab';
import UserMappingTab from './access/UserMappingTab';
import ResponsiveTabs from '../components/common/ResponsiveTabs';
import { useScreenSize } from '../contexts/ScreenSizeContext';
// Import MUI icons for tabs
import PeopleIcon from '@mui/icons-material/People';
import LockIcon from '@mui/icons-material/Lock';
import AccountTreeIcon from '@mui/icons-material/AccountTree';
import LinkIcon from '@mui/icons-material/Link';

/**
 * Access management page with role-focused tabs
 * @returns {JSX.Element} Access content
 */
const Access = () => {
  const { t } = useTranslation();
  const theme = useTheme();
  const { isMobileScreen } = useScreenSize();
  const [activeTab, setActiveTab] = useState(0);

  // Tabs configuration for responsive navigation
  const tabs = [
    { icon: <PeopleIcon />, label: t('access:tabs.roles') },
    { icon: <LockIcon />, label: t('access:tabs.permissions') },
    { icon: <AccountTreeIcon />, label: t('access:tabs.hierarchy') },
    { icon: <LinkIcon />, label: t('access:tabs.userMapping') }
  ];
  const [roles, setRoles] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [openDialog, setOpenDialog] = useState(false);
  const [currentRole, setCurrentRole] = useState(null);
  const [users, setUsers] = useState([]);
  // Removed unused state variables
  const [searchQuery, setSearchQuery] = useState('');
  const [pagination, setPagination] = useState({
    page: 0,
    pageSize: 10,
    total: 0
  });

  // Fetch roles from API
  const fetchRoles = async () => {
    setLoading(true);
    try {
      const params = { 
        page: pagination.page,
        size: pagination.pageSize,
        ...(searchQuery && { name: searchQuery, description: searchQuery })
      };
      
      const response = await roleService.search(params);
      setRoles(response.data.content);
      setPagination(prev => ({ ...prev, total: response.data.totalElements }));
    } catch (err) {
      setError(t('access:errors.fetchRoles'));
    } finally {
      setLoading(false);
    }
  };

  // Parent-related functions removed (now in HierarchyTab)

  const fetchUsers = async () => {
    try {
      const response = await userService.search({query:""});
      setUsers(response.data);
    } catch (err) {
      setError(t('access:errors.fetchUsers'));
    }
  };

  useEffect(() => {
    fetchRoles();
    fetchUsers();
  }, [pagination.page, pagination.pageSize, searchQuery]);

  const handleTabChange = (event, newValue) => {
    setActiveTab(newValue);
  };

  const handleCreateRole = () => {
    setCurrentRole(null);
    setOpenDialog(true);
  };

  const handleEditRole = (role) => {
    setCurrentRole(role);
    setOpenDialog(true);
  };

  const handleDeleteRole = async (id) => {
    try {
      await roleService.delete(id);
      fetchRoles();
    } catch (err) {
      setError(t('access:errors.deleteRole'));
    }
  };

  const handleSubmitRole = async (roleData) => {
    try {
      if (currentRole) {
        await roleService.update(currentRole.id, roleData);
      } else {
        await roleService.create(roleData);
      }
      setOpenDialog(false);
      fetchRoles();
    } catch (err) {
      setError(currentRole 
        ? t('access:errors.updateRole') 
        : t('access:errors.createRole'));
    }
  };

  // Permission-related functions removed (now in PermissionsTab)

  // roleColumns removed (now in RolesTab)

  return (
    <Box sx={{ 
      width: '100%',
      p: theme.spacing(3),
      backgroundColor: theme.palette.background.default
    }}>
      <ResponsiveTabs 
        tabs={tabs} 
        currentTab={activeTab} 
        onTabChange={handleTabChange} 
      />

      <Box sx={{
        backgroundColor: theme.palette.background.paper,
        borderRadius: theme.shape.borderRadius,
        pb: isMobileScreen ? theme.spacing(10) : theme.spacing(3) // Add extra padding at bottom for mobile to avoid bottom nav overlap
      }}>
        {activeTab === 0 && (
          <RolesTab 
            t={t}
            roles={roles}
            loading={loading}
            error={error}
            pagination={pagination}
            setPagination={setPagination}
            searchQuery={searchQuery}
            setSearchQuery={setSearchQuery}
            handleCreateRole={handleCreateRole}
            handleEditRole={handleEditRole}
            handleDeleteRole={handleDeleteRole}
          />
        )}
        
        {activeTab === 1 && (
          <PermissionsTab 
            t={t}
            roles={roles}
            error={error}
            setError={setError}
          />
        )}
        
        {activeTab === 2 && (
          <HierarchyTab 
            t={t}
            roles={roles}
            error={error}
            setError={setError}
          />
        )}
        
        {activeTab === 3 && (
          <UserMappingTab 
            t={t}
            users={users}
            error={error}
            setError={setError}
          />
        )}
      </Box>
      
      {/* Role Create/Edit Dialog */}
      <RoleDialog
        open={openDialog}
        onClose={() => setOpenDialog(false)}
        currentRole={currentRole}
        onSubmit={handleSubmitRole}
        t={t}
      />
    </Box>
  );
};

export default Access;
