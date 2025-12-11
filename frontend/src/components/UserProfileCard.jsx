import React from 'react';
import PropTypes from 'prop-types';
import { useTranslation } from 'react-i18next';
import { useTheme } from '../theme/ThemeContext';
import systemService from '../api/system/systemService';
import Chip from '@mui/material/Chip';
import Box from '@mui/material/Box';

/**
 * UserProfileCard component displays user information and actions
 * @param {Object} props - Component props
 * @param {Object} props.userInfo - User information object
 * @param {Function} props.onClose - Function to close the profile card
 * @returns {JSX.Element} User profile card component
 */
const UserProfileCard = ({ userInfo, onClose }) => {
  const { t } = useTranslation();
  const { theme } = useTheme();
  const handleLogout = () => {
    // Construct OIDC logout URL
    const oidcLogoutUrl = `logout`;
    // Redirect to OIDC logout endpoint
    window.location.href = oidcLogoutUrl;
  };

  const handleChangePassword = () => {
    // TODO: Implement change password flow
    onClose();
  };

  return (
    <div className="absolute 
       right-0 w-64 top-11
       rounded-lg shadow-lg border z-50"
       style={{
         backgroundColor: theme.palette.background.paper,
         borderColor: theme.palette.divider
       }}>
      <div className="p-4">
        {/* User Information */}
        <div className="mb-4">
          <h3 className="text-lg font-semibold" style={{ color: theme.palette.text.primary }}>
            {userInfo?.firstName} {userInfo?.lastName}
          </h3>
          <div className="space-y-1">
            <p className="text-sm" style={{ color: theme.palette.text.secondary }}>
              <span className="font-medium">{t('common:username')}: </span>
              {userInfo?.username}
            </p>
            <p className="text-sm" style={{ color: theme.palette.text.secondary }}>
              <span className="font-medium">{t('common:email')}: </span>
              {userInfo?.email}
            </p>
          </div>
          <div className="mt-2">
            <p className="text-sm font-medium mb-1" style={{ color: theme.palette.text.secondary }}>
              {t('common:roles')}:
            </p>
            <Box className="flex flex-wrap gap-2">
              {userInfo?.roles?.map((role, index) => (
                <Chip
                  key={index}
                  label={role.name}
                  size="small"
                  className="text-xs"
                  color="primary"
                  variant="outlined"
                />
              ))}
            </Box>
          </div>
        </div>

        {/* Action Buttons */}
        <div className="space-y-2">
          <button
            onClick={handleChangePassword}
            className="w-full px-4 py-2 text-sm text-left rounded-md"
            style={{
              color: theme.palette.text.primary,
              backgroundColor: theme.palette.background.default,
              '&:hover': {
                backgroundColor: theme.palette.action.hover
              }
            }}
          >
            {t('common:changePassword')}
          </button>
          <button
            onClick={handleLogout}
            className="w-full px-4 py-2 text-sm text-left rounded-md"
            style={{
              color: theme.palette.error.main,
              backgroundColor: theme.palette.background.default,
              '&:hover': {
                backgroundColor: theme.palette.error.light
              }
            }}
          >
            {t('common:logout')}
          </button>
        </div>
      </div>
    </div>
  );
};

UserProfileCard.propTypes = {
  userInfo: PropTypes.shape({
    firstName: PropTypes.string,
    lastName: PropTypes.string,
    username: PropTypes.string,
    email: PropTypes.string,
    roles: PropTypes.arrayOf(
      PropTypes.shape({
        name: PropTypes.string
      })
    )
  }).isRequired,
  onClose: PropTypes.func.isRequired,
};

export default UserProfileCard;
