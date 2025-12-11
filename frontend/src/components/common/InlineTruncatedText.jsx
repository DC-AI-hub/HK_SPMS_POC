import React from 'react';
import { Tooltip, Typography } from '@mui/material';
import { useTranslation } from 'react-i18next';

/**
 * InlineTruncatedText - Displays text truncated to a single line with tooltip
 * 
 * @param {Object} props - Component properties
 * @param {string} props.text - Text content to display
 * @param {number} [props.maxChars=50] - Maximum characters before truncation
 * @param {string} [props.tooltipPlacement='top'] - Tooltip position
 * @returns {JSX.Element} Truncated text element with tooltip
 */
const InlineTruncatedText = ({ text, maxChars = 50, tooltipPlacement = 'top' }) => {
  const { t } = useTranslation();
  
  if (!text) {
    return <Typography variant="body1">{t('common:noText')}</Typography>;
  }

  // Truncate text if it exceeds maxChars
  const isTruncated = text.length > maxChars;
  const displayText = isTruncated 
    ? text.substring(0, maxChars) + '...' 
    : text;

  return (
    <Tooltip 
      title={text} 
      placement={tooltipPlacement}
      disableHoverListener={!isTruncated}
    >
      <Typography 
        variant="body1" 
        sx={{
          whiteSpace: 'nowrap',
          overflow: 'hidden',
          textOverflow: 'ellipsis',
          maxWidth: '100%'
        }}
      >
        {displayText}
      </Typography>
    </Tooltip>
  );
};

export default InlineTruncatedText;
