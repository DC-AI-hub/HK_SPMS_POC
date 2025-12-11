import React from 'react';
import { List, ListItem, ListItemText, Typography, Divider } from '@mui/material';
import { formatDistanceToNow } from 'date-fns';

const ActivityList = ({ activities }) => {
  if (!activities || activities.length === 0) {
    return (
      <Typography variant="body2" color="textSecondary">
        No recent activities
      </Typography>
    );
  }

  return (
    <List>
      {activities.map((activity, index) => (
        <React.Fragment key={activity.id}>
          <ListItem>
            <ListItemText
              primary={`${activity.actionType} ${activity.entityType}`}
              secondary={
                <>
                  <Typography
                    component="span"
                    variant="body2"
                    color="textPrimary"
                  >
                    {formatDistanceToNow(new Date(activity.createdAt))} ago
                  </Typography>
                  {activity.entityId && ` • ID: ${activity.entityId}`}
                </>
              }
            />
          </ListItem>
          {index < activities.length - 1 && <Divider component="li" />}
        </React.Fragment>
      ))}
    </List>
  );
};

export default ActivityList;
