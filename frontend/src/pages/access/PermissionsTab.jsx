import React, { useState } from 'react';
import { 
  TextField, Button, Alert, IconButton, 
  Box, CircularProgress
} from '@mui/material';
import RoleSelector from '../../components/common/RoleSelector';
import { Delete } from '@mui/icons-material';
import roleService from '../../api/idm/roleService';

/**
 * Permissions management tab component
 * @param {Object} props - Component props
 * @param {Function} props.t - Translation function
 * @param {Array} props.roles - List of roles
 * @param {Object} props.error - Error object
 * @param {Function} props.setError - Set error function
 * @returns {JSX.Element} Permissions tab content
 */
const PermissionsTab = ({ t, roles, error, setError }) => {
  const [selectedRole, setSelectedRole] = useState(null);
  const [selectedRoleId, setSelectedRoleId] = useState('');
  const [permissions, setPermissions] = useState([]);
  const [newPermission, setNewPermission] = useState('');
  const [loading, setLoading] = useState(false);

  const handleRoleChange = async (e) => {
    const roleId = e.target.value;
    const role = roles.find(r => r.id === roleId);
    setSelectedRole(role);
    setLoading(true);
    try {
      const response = await roleService.getPermissions(roleId);
      setPermissions(response.data);
      setError(null);
    } catch (err) {
      setError(t('access:fetchPermissionsError'));
      setPermissions([]);
    } finally {
      setLoading(false);
    }
  };

  const handleAddPermission = async () => {
    if (!newPermission.trim() || !selectedRole) return;
    
    setLoading(true);
    try {
      await roleService.addPermission(selectedRole.id, newPermission.trim());
      setPermissions([...permissions, newPermission.trim()]);
      setNewPermission('');
      setError(null);
    } catch (err) {
      setError(t('access:addPermissionError'));
    } finally {
      setLoading(false);
    }
  };

  const handleRemovePermission = async (permission) => {
    if (!selectedRole) return;
    
    setLoading(true);
    try {
      await roleService.removePermission(selectedRole.id, permission);
      setPermissions(permissions.filter(p => p !== permission));
      setError(null);
    } catch (err) {
      setError(t('access:removePermissionError'));
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="mt-4">
      <div className="mb-6">
        <h2 className="text-xl font-medium text-gray-700 mb-2">
          {t('access:selectRoleForPermissions')}
        </h2>
        <div className="flex gap-2">
          <RoleSelector
            value={selectedRole?.id || ''}
            onChange={handleRoleChange}
            disableFetch={true}
            options={roles}
            label={t('access:selectRole')}
          />
        </div>
      </div>
      
      {selectedRole && (
        <div>
          <h2 className="text-xl font-medium text-gray-700 mb-4">
            {t('access:permissionsFor')} {selectedRole.name}
          </h2>
          
          {error && (
            <Alert severity="error" className="mb-4">
              {error}
            </Alert>
          )}
          
          <div className="mb-6">
          <div className="flex gap-2 mb-4">
            <TextField
              fullWidth
              size="small"
              label={t('access:newPermission')}
              value={newPermission}
              onChange={(e) => setNewPermission(e.target.value)}
              disabled={loading}
            />
            <Button
              variant="contained"
              color="primary"
              onClick={handleAddPermission}
              disabled={loading || !selectedRole}
              startIcon={loading ? <CircularProgress size={20} /> : null}
            >
              {t('access:addPermission')}
            </Button>
          </div>
          </div>
          
          <div className="bg-white rounded-lg shadow-sm p-4">
            <h3 className="text-lg font-medium text-gray-700 mb-3">
              {t('access:currentPermissions')}
            </h3>
            {permissions.length === 0 ? (
              <p className="text-gray-500">{t('access:noPermissions')}</p>
            ) : (
              <div className="flex flex-wrap gap-2">
                {permissions.map((permission, index) => (
                  <Box 
                    key={index}
                    className="flex items-center bg-gray-100 rounded-full px-3 py-1"
                  >
                    <span className="mr-2">{permission}</span>
                    <IconButton 
                      size="small"
                      onClick={() => handleRemovePermission(permission)}
                    >
                      <Delete fontSize="small" />
                    </IconButton>
                  </Box>
                ))}
              </div>
            )}
          </div>
        </div>
      )}
    </div>
  );
};

export default PermissionsTab;
