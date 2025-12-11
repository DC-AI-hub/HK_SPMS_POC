import React, { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { useTheme } from '../theme/ThemeContext';
import { IconButton, Menu, MenuItem, ListItemIcon } from '@mui/material';
import { SvgIcon } from '@mui/material';

const flags = {
  en: () => (
    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 60 30" width="24" height="12">
      <clipPath id="s">
        <path d="M0,0 v30 h60 v-30 z"/>
      </clipPath>
      <clipPath id="t">
        <path d="M30,15 h30 v15 z v15 h-30 z h-30 v-15 z v-15 h30 z"/>
      </clipPath>
      <g clipPath="url(#s)">
        <path d="M0,0 v30 h60 v-30 z" fill="#012169"/>
        <path d="M0,0 L60,30 M60,0 L0,30" stroke="#fff" strokeWidth="6"/>
        <path d="M0,0 L60,30 M60,0 L0,30" clipPath="url(#t)" stroke="#C8102E" strokeWidth="4"/>
        <path d="M30,0 v30 M0,15 h60" stroke="#fff" strokeWidth="10"/>
        <path d="M30,0 v30 M0,15 h60" stroke="#C8102E" strokeWidth="6"/>
      </g>
    </svg>
  ),
  zh: () => (
    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 900 600" width="24" height="16">
      <rect width="900" height="600" fill="#de2910"/>
      <path fill="#ffde00" d="M450,250.1l-84.1,61.1l32.1-99.3l-84.1,61.3l103.9-0.8l32.1-99.3l32.1,99.3l103.9,0.8l-84.1-61.3l32.1,99.3L450,250.1z"/>
    </svg>
  ),
  'zh-TR': () => (
    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 900 600" width="24" height="16">
      <rect width="900" height="600" fill="#de2910"/>
      <path fill="#ffde00" d="M450,250.1l-84.1,61.1l32.1-99.3l-84.1,61.3l103.9-0.8l32.1-99.3l32.1,99.3l103.9,0.8l-84.1-61.3l32.1,99.3L450,250.1z"/>
    </svg>
  )
};

const LanguageSelectorWithFlags = () => {
  const { i18n } = useTranslation();
  const { theme } = useTheme();
  const [anchorEl, setAnchorEl] = useState(null);
  const open = Boolean(anchorEl);

  const handleMenu = (event) => {
    setAnchorEl(event.currentTarget);
  };

  const handleClose = () => {
    setAnchorEl(null);
  };

  const changeLanguage = (language) => {
    i18n.changeLanguage(language);
    if (typeof document !== 'undefined') {
      document.documentElement.setAttribute('lang', language);
    }
    handleClose();
  };

  const CurrentFlag = flags[i18n.language] || flags.en;

  return (
    <div>
      <IconButton 
        onClick={handleMenu}
        size="small"
        aria-label="select language"
        aria-controls="language-menu"
        aria-haspopup="true"
      >
        <SvgIcon component={CurrentFlag} viewBox="0 0 24 16" />
      </IconButton>
      <Menu
        id="language-menu"
        anchorEl={anchorEl}
        keepMounted
        open={open}
        onClose={handleClose}
        PaperProps={{
          style: {
            backgroundColor: theme.palette.background.paper,
            color: theme.palette.text.primary
          }
        }}
      >
        <MenuItem onClick={() => changeLanguage('en')} selected={i18n.language === 'en'}>
          <ListItemIcon>
            <SvgIcon component={flags.en} viewBox="0 0 24 16" />
          </ListItemIcon>
          English
        </MenuItem>
        <MenuItem onClick={() => changeLanguage('zh')} selected={i18n.language === 'zh'}>
          <ListItemIcon>
            <SvgIcon component={flags.zh} viewBox="0 0 24 16" />
          </ListItemIcon>
          简体中文
        </MenuItem>
        <MenuItem onClick={() => changeLanguage('zh-TR')} selected={i18n.language === 'zh-TR'}>
          <ListItemIcon>
            <SvgIcon component={flags['zh-TR']} viewBox="0 0 24 16" />
          </ListItemIcon>
          繁體中文
        </MenuItem>
      </Menu>
    </div>
  );
};

export default LanguageSelectorWithFlags;
