import React, { useState } from 'react';
import { 
  Button, Alert, 
  Box, CircularProgress
} from '@mui/material';
import UserSelector from '../../components/common/UserSelector';
import RoleSelector from '../../components/common/RoleSelector';
import RoleChip from '../../components/common/RoleChip';
import userService from '../../api/idm/userService';

/**
 * User-role mapping management tab component
 * @param {Object} props - Component props
 * @param {Function} props.t - Translation function
 * @param {Array} props.users - List of users
 * @param {Object} props.error - Error object
 * @param {Function} props.setError - Set error function
 * @returns {JSX.Element} User mapping tab content
 */
const UserMappingTab = ({ t, users, error, setError }) => {
  const [selectedUser, setSelectedUser] = useState(null);
  const [userRoles, setUserRoles] = useState([]);
  const [selectedRole, setSelectedRole] = useState(null);
  const [loading, setLoading] = useState(false);

  const fetchUser = async(uid) => {
     const userData = await userService.get(uid).then(x=>x.data);
     setUserRoles(userData.roles || []);
  }

  const handleUserChange = async (e) => {
    console.log(e)
    const userId = e.target.value;
    const user = users.find(u => u.id === userId);
    setSelectedUser(user);
    setSelectedRole('');
    setLoading(true);
    
      try {
      // Fetch user with roles
      await fetchUser(user.id);
    } catch (error) {
      setError(t('common:fetchError'));
    } finally {
      setLoading(false);
    }
  };

  // Handle role assignment
  const handleAssignRole = async () => {
    if (!selectedRole) return;
    
    const selectedRoleId = selectedRole.id;
    
    setLoading(true);
    try {
      await userService.assignRole(selectedUser.id, selectedRoleId);
      
      // Refresh user roles
      await fetchUser(selectedUser.id);
      setSelectedRoleId('');
    } catch (error) {
      setError(t('common:updateError'));
    } finally {
      setLoading(false);
    }
  };

  // Handle role removal
  const handleRemoveRole = async (roleId) => {
    setLoading(true);
    try {
      await userService.removeRole(selectedUser.id, roleId);
      
      // Refresh user roles
      await fetchUser(selectedUser.id);
    } catch (error) {
      setError(t('common:deleteError'));
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="mt-4">
      <div className="mb-6">
        <h2 className="text-xl font-medium text-gray-700 mb-2">
          {t('access:selectUserForRoles')}
        </h2>
        <div className="flex gap-2">
          <UserSelector
            value={selectedUser?.id || ''}
            onChange={handleUserChange}
            options={users}
            disableFetch={true}
            label={t('access:selectUser')}
          />
        </div>
      </div>
      
      {selectedUser && (
        <div>
          <h2 className="text-xl font-medium text-gray-700 mb-4">
            {t('access:rolesForUser')} {selectedUser.username}
          </h2>
          
          <div className="mb-6">
            <div className="flex gap-2 mb-4">
              <RoleSelector
                value={selectedRole?.id || ''}
                onChange={(e) => setSelectedRole(e.target.value ? {id: e.target.value} : null)}
                label={t('access:selectRoleToAssign')}
              />
              <Button
                variant="contained"
                color="primary"
                onClick={handleAssignRole}
                disabled={!selectedRole || loading}
              >
                {loading ? <CircularProgress size={24} /> : t('access:assignRole')}
              </Button>
            </div>
          </div>
          
          <div className="bg-white rounded-lg shadow-sm p-4">
            <h3 className="text-lg font-medium text-gray-700 mb-3">
              {t('access:currentRoles')}
            </h3>
            {userRoles.length === 0 ? (
              <p className="text-gray-500">{t('access:noRolesAssigned')}</p>
            ) : (
              <div className="flex flex-wrap gap-2">
                {userRoles.map((role) => (
                  <RoleChip 
                    key={role.id}
                    role={role}
                    onRemove={handleRemoveRole}
                    disabled={loading}
                  />
                ))}
              </div>
            )}
          </div>
        </div>
      )}
    </div>
  );
};

export default UserMappingTab;
