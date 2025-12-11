import React from 'react';
import { Drawer, IconButton } from '@mui/material';
import CloseIcon from '@mui/icons-material/Close';
import { useTheme } from '../theme/ThemeContext';
import Sidebar from './Sidebar';

/**
 * MobileContextMenu component for mobile navigation drawer
 * @param {Object} props - Component props
 * @param {boolean} props.open - Whether the menu is open
 * @param {function} props.onClose - Function to close the menu
 * @returns {JSX.Element} Mobile context menu drawer
 */
const MobileContextMenu = ({ open, onClose }) => {
  const { theme } = useTheme();

  return (
    <Drawer
      anchor="left"
      open={open}
      onClose={onClose}
      sx={{
        '& .MuiDrawer-paper': {
          width: 280,
          backgroundColor: theme.palette.primary.main,
          color: theme.palette.primary.main,
          boxSizing: 'border-box',
        },
      }}
    >
      {/* Close button */}
      <div className="flex justify-end p-2">
        <IconButton
          onClick={onClose}
          sx={{ color: theme.palette.text.light }}
          aria-label="close menu"
        >
          <CloseIcon />
        </IconButton>
      </div>

      {/* Sidebar content */}
      <div className="h-full overflow-y-auto">
        <Sidebar isMobileContextMenu={true} onItemClick={onClose} />
      </div>
    </Drawer>
  );
};

export default MobileContextMenu;
