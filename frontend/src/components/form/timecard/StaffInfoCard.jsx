import React from 'react';
import {
  Card,
  CardContent,
  Typography,
  Grid,
  Box,
  Skeleton
} from '@mui/material';

/**
 * Staff Information Card Component
 * Displays employee information in a read-only grid layout
 * 
 * @param {Object} staffInfo - Employee information object
 * @param {boolean} isLoading - Whether data is being loaded
 */
const StaffInfoCard = ({ staffInfo, isLoading = false }) => {
  // Handle loading state
  if (isLoading) {
    return (
      <Card sx={{ mb: 3 }}>
        <CardContent>
          <Typography variant="h6" gutterBottom>
            Staff Information
          </Typography>
          <Grid container spacing={3}>
            {[...Array(8)].map((_, index) => (
              <Grid item xs={12} sm={6} md={3} key={index}>
                <Skeleton variant="text" width="60%" />
                <Skeleton variant="text" width="80%" />
              </Grid>
            ))}
          </Grid>
        </CardContent>
      </Card>
    );
  }
  
  // Handle null or undefined staffInfo (data not available)
  if (!staffInfo) {
    return (
      <Card sx={{ mb: 3 }}>
        <CardContent>
          <Typography variant="h6" gutterBottom>
            Staff Information
          </Typography>
          <Typography variant="body2" color="text.secondary">
            Employee information not available
          </Typography>
        </CardContent>
      </Card>
    );
  }

  const {
    staffId,
    staffNameChinese,
    staffNameEnglish,
    team,
    staffType,
    departmentHead,
    finalApproval,
    timecardMonth
  } = staffInfo;

  const infoFields = [
    { label: 'Staff ID', value: staffId },
    { label: 'Staff Name (English)', value: staffNameEnglish },
    { label: 'Staff Name (Chinese)', value: staffNameChinese },
    { label: 'Team', value: team },
    { label: 'Staff Type', value: staffType },
    { label: 'Department Head', value: departmentHead },
    { label: 'Final Approval', value: finalApproval },
    { label: 'Timecard Month', value: timecardMonth }
  ];

  return (
    <Card sx={{ mb: 3 }}>
      <CardContent>
        <Typography variant="h6" gutterBottom>
          Staff Information
        </Typography>

        <Grid container spacing={3}>
          {infoFields.map((field, index) => (
            <Grid item xs={12} sm={6} md={3} key={index}>
              <Box>
                <Typography
                  variant="caption"
                  color="text.secondary"
                  display="block"
                  gutterBottom
                >
                  {field.label}
                </Typography>
                <Typography
                  variant="body2"
                  sx={{
                    fontWeight: 'medium',
                    color: 'text.primary',
                    wordBreak: 'break-word'
                  }}
                >
                  {field.value || '-'}
                </Typography>
              </Box>
            </Grid>
          ))}
        </Grid>
      </CardContent>
    </Card>
  );
};

export default StaffInfoCard;