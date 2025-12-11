import React, { useState, useEffect } from 'react';
import { 
  Button, Alert, IconButton, Box
} from '@mui/material';
import RoleSelector from '../../components/common/RoleSelector';
import roleService from '../../api/idm/roleService';
import { Delete } from '@mui/icons-material';

/**
 * Role hierarchy management tab component
 * @param {Object} props - Component props
 * @param {Function} props.t - Translation function
 * @param {Array} props.roles - List of roles
 * @param {Object} props.error - Error object
 * @param {Function} props.setError - Set error function
 * @returns {JSX.Element} Hierarchy tab content
 */
const HierarchyTab = ({ t, roles, error, setError }) => {
  const [selectedRole, setSelectedRole] = useState(null);
  const [parentRoles, setParentRoles] = useState(null);
  const [childRoles, setChildRoles] = useState(null);
  const [selectedParentRole, setSelectedParentRole] = useState(null);

  const handleRoleChange = (e) => {
    const roleId = e.target.value;
    const role = roles.find(r => r.id === roleId);
    setSelectedRole(role);
    setParentRoles(null);
    setChildRoles(null);
    if (roleId) {
      roleService.get(roleId)
        .then(roleData => {
          console.log(roleData)
          setParentRoles(roleData.parentRoles || []);
          setChildRoles(roleData.childRoles || []);
          setError(null);
        })
        .catch(err => setError(err.message));
    }
  };

  const handleAddParent = () => {
    if (!selectedParentRole || !selectedRole?.id) return;
    const selectedParent = selectedParentRole.id;
    
    roleService.addParent(selectedRole.id, selectedParent)
      .then(updatedRole => {
        setParentRoles(updatedRole.parentRoles || []);
        setSelectedParent('');
        setError(null);
      })
      .catch(err => setError(err.message));
  };

  const handleRemoveParent = (parentId) => {
    if (!selectedRole?.id) return;
    
    roleService.removeParent(selectedRole.id, parentId)
      .then(updatedRole => {
        setParentRoles(updatedRole.parentRoles || []);
        setError(null);
      })
      .catch(err => setError(err.message));
  };

  return (
    <div className="mt-4">
      <div className="mb-6">
        <h2 className="text-xl font-medium text-gray-700 mb-2">
          {t('access:selectRoleForHierarchy')}
        </h2>
        <div className="flex gap-2">
          <RoleSelector
            value={selectedRole?.id || ''}
            onChange={(e) => handleRoleChange(e)}
            disableFetch={true}
            options={roles}
            label={t('access:selectRole')}
          />
        </div>
      </div>
      
      {selectedRole && (
        <div>
          <h2 className="text-xl font-medium text-gray-700 mb-4">
            {t('access:parentRolesFor')} {selectedRole.name}
          </h2>
          
          {error && (
            <Alert severity="error" className="mb-4">
              {error}
            </Alert>
          )}
          
          <div className="mb-6">
            <div className="flex gap-2 mb-4">
              <RoleSelector
                value={selectedParentRole?.id || ''}
                onChange={(e) => setSelectedParentRole(e.target.value ? {id: e.target.value} : null)}
                disableFetch={true}
                options={roles.filter(r => r.id !== selectedRole?.id)}
                label={t('access:selectParentRole')}
              />
              <Button
                variant="contained"
                color="primary"
                disabled={!selectedParentRole}
                onClick={handleAddParent}
              >
                {t('access:addParent')}
              </Button>
            </div>
          </div>
          
          <div className="bg-white rounded-lg shadow-sm p-4">
            <h3 className="text-lg font-medium text-gray-700 mb-3">
              {t('access:currentParents')}
            </h3>
            {parentRoles === null ? (
              <p className="text-gray-500">{t('access:loadingParents')}</p>
            ) : parentRoles.length === 0 ? (
              <p className="text-gray-500">{t('access:noParents')}</p>
            ) : (
              <div className="flex flex-wrap gap-2">
                {parentRoles.map((role, index) => (
                  <Box 
                    key={index}
                    className="flex items-center bg-gray-100 rounded-full px-3 py-1"
                  >
                    <span className="mr-2">{role.name}</span>
                    <IconButton 
                      size="small"
                      onClick={() => handleRemoveParent(role.id)}
                    >
                      <Delete fontSize="small" />
                    </IconButton>
                  </Box>
                ))}
              </div>
            )}
          </div>

          <div className="mt-6 bg-white rounded-lg shadow-sm p-4">
            <h3 className="text-lg font-medium text-gray-700 mb-3">
              {t('access:currentChildren')}
            </h3>
            {childRoles === null ? (
              <p className="text-gray-500">{t('access:loadingChildren')}</p>
            ) : childRoles.length === 0 ? (
              <p className="text-gray-500">{t('access:noChildren')}</p>
            ) : (
              <div className="flex flex-wrap gap-2">
                {childRoles.map((role, index) => (
                  <Box 
                    key={index}
                    className="flex items-center bg-gray-100 rounded-full px-3 py-1"
                  >
                    <span className="mr-2">{role.name}</span>
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

export default HierarchyTab;
