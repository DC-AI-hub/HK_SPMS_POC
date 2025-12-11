import React from 'react';
import { 
  Timeline,
  TimelineItem,
  TimelineSeparator,
  TimelineDot,
  TimelineConnector,
  TimelineContent,
  TimelineOppositeContent
} from '@mui/lab';
import { Typography, Chip, Box } from '@mui/material';
import { useTranslation } from 'react-i18next';
import DeprecationBadge from './DeprecationBadge';

const VersionTimeline = ({ versions, onSelectVersion }) => {
  const { t } = useTranslation();
  
  return (
    <Timeline position="alternate">
      {versions.map((version, index) => (
        <TimelineItem key={version.id}>
          <TimelineOppositeContent sx={{ m: 'auto 0' }} align="right">
            <Typography variant="body2" color="text.secondary">
              {new Date(version.createdAt).toLocaleDateString()}
            </Typography>
          </TimelineOppositeContent>
          <TimelineSeparator>
            <TimelineDot color={version.status === 'DEPRECATED' ? 'grey' : 'primary'} variant="outlined" />
            {index < versions.length - 1 && <TimelineConnector />}
          </TimelineSeparator>
          <TimelineContent sx={{ py: '12px', px: 2 }}>
            <Box onClick={() => onSelectVersion(version)} sx={{ cursor: 'pointer' }}>
              <Typography variant="h6" component="span">
                {version.version}
              </Typography>
              <DeprecationBadge status={version.status} sx={{ ml: 1 }} />
            </Box>
          </TimelineContent>
        </TimelineItem>
      ))}
    </Timeline>
  );
};

export default VersionTimeline;
