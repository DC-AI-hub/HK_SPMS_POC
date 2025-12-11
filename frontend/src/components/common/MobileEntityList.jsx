import React, { useState } from 'react';
import {
  List,
  ListItem,
  ListItemText,
  ListItemSecondaryAction,
  IconButton,
  Box,
  Chip,
  Typography,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Button,
  useTheme
} from '@mui/material';
import { MoreVert, Edit, Delete } from '@mui/icons-material';
import { useTranslation } from 'react-i18next';
import dayjs from 'dayjs';

/**
 * Generic mobile-friendly list component for displaying entities with touch interactions
 * Supports configurable field mappings, actions, and internationalization
 */
const MobileEntityList = ({
  entities = [],
  loading = false,
  onAction,
  fieldMappings = {},
  actionConfig = [],
  i18nKeys = {},
  renderItem
}) => {
  const { t } = useTranslation();
  const theme = useTheme();
  const [selectedEntity, setSelectedEntity] = useState(null);
  const [detailDialogOpen, setDetailDialogOpen] = useState(false);
  const [actionDialogOpen, setActionDialogOpen] = useState(false);
  const [longPressTimer, setLongPressTimer] = useState(null);

  // Default field mappings if not provided
  const defaultFieldMappings = {
    name: 'name',
    status: 'active',
    description: 'description',
    type: 'type',
    lastModified: 'lastModified',
    ...fieldMappings
  };

  // Default i18n keys if not provided
  const defaultI18nKeys = {
    loading: 'common:loading',
    noItems: 'common:noItems',
    status: 'common:status',
    description: 'common:description',
    lastModified: 'common:lastModified',
    type: 'common:type',
    actions: 'common:actions',
    chooseAction: 'common:chooseAction',
    close: 'common:close',
    cancel: 'common:cancel',
    ...i18nKeys
  };

  // Default action config if not provided
  const defaultActionConfig = actionConfig.length > 0 ? actionConfig : [
    {
      key: 'edit',
      label: 'common:edit',
      icon: 'Edit',
      color: 'primary'
    },
    {
      key: 'delete',
      label: 'common:delete',
      icon: 'Delete',
      color: 'error'
    }
  ];

  const handleLongPress = (entity, event) => {
    event.preventDefault();
    setSelectedEntity(entity);
    setDetailDialogOpen(true);
  };

  const handleTouchStart = (entity) => {
    const timer = setTimeout(() => {
      setSelectedEntity(entity);
      setDetailDialogOpen(true);
    }, 500); // 500ms for long press
    setLongPressTimer(timer);
  };

  const handleTouchEnd = () => {
    if (longPressTimer) {
      clearTimeout(longPressTimer);
      setLongPressTimer(null);
    }
  };

  const handleActionMenu = (entity, event) => {
    event.stopPropagation();
    setSelectedEntity(entity);
    setActionDialogOpen(true);
  };

  const handleAction = (actionKey) => {
    if (typeof onAction === 'function' && selectedEntity) {
      onAction(actionKey, selectedEntity);
    } else {
      console.warn('Action not handled: onAction is not a function or no entity selected');
    }
    setActionDialogOpen(false);
    setSelectedEntity(null);
  };

  const openActionMenu = (entity) => {
    console.log('Opening action menu for entity:', entity);
    setSelectedEntity(entity);
    setActionDialogOpen(true);
  };

  const formatDate = (dateString) => {
    return dayjs(dateString).format('YYYY-MM-DD HH:mm');
  };

  const getStatusColor = (status) => {
    if (typeof status === 'boolean') {
      return status ? 'success' : 'default';
    }
    if (typeof status === 'string') {
      switch (status.toUpperCase()) {
        case 'ACTIVE':
        case 'TRUE':
        case 'SUCCESS':
          return 'success';
        case 'INACTIVE':
        case 'FALSE':
        case 'ERROR':
          return 'error';
        case 'DRAFT':
          return 'primary';
        case 'PENDING':
          return 'warning';
        default:
          return 'default';
      }
    }
    return 'default';
  };

  const getStatusLabel = (status) => {
    if (typeof status === 'boolean') {
      return status ? t('common:active') : t('common:inactive');
    }
    if (typeof status === 'string') {
      return t(`common:${status.toLowerCase()}`, status);
    }
    return String(status);
  };

  if (loading) {
    return (
      <Box sx={{ p: 2 }}>
        <Typography variant="body2" color="text.secondary">
          {t(defaultI18nKeys.loading)}
        </Typography>
      </Box>
    );
  }

  if (entities.length === 0) {
    return (
      <Box sx={{ p: 2, textAlign: 'center' }}>
        <Typography variant="body2" color="text.secondary">
          {t(defaultI18nKeys.noItems)}
        </Typography>
      </Box>
    );
  }

  // Custom render function if provided
  if (renderItem) {
    return (
      <>
        <List sx={{ width: '100%', bgcolor: 'background.paper' }}>
          {entities.map((entity, index) => (
            <ListItem style={{ borderBottom: '1px solid', borderColor: theme.palette.divider }}
              key={entity.id || index}
              onTouchStart={() => handleTouchStart(entity)}
              onTouchEnd={handleTouchEnd}
              onContextMenu={(e) => handleLongPress(entity, e)}
            >
              {renderItem(entity, openActionMenu)}
            </ListItem>
          ))}

        </List>
        <Dialog
          open={detailDialogOpen}
          onClose={() => setDetailDialogOpen(false)}
          fullScreen
        >
          <DialogTitle>
            {selectedEntity?.[defaultFieldMappings.name] || t('common:unnamed')}
          </DialogTitle>
          <DialogContent>
            {selectedEntity && (
              <Box sx={{ mt: 2 }}>
                {selectedEntity[defaultFieldMappings.status] !== undefined && (
                  <Typography variant="body2" gutterBottom>
                    <strong>{t(defaultI18nKeys.status)}:</strong>{' '}
                    {getStatusLabel(selectedEntity[defaultFieldMappings.status])}
                  </Typography>
                )}
                {selectedEntity[defaultFieldMappings.description] && (
                  <Typography variant="body2" gutterBottom>
                    <strong>{t(defaultI18nKeys.description)}:</strong>{' '}
                    {selectedEntity[defaultFieldMappings.description]}
                  </Typography>
                )}
                {selectedEntity[defaultFieldMappings.lastModified] && (
                  <Typography variant="body2" gutterBottom>
                    <strong>{t(defaultI18nKeys.lastModified)}:</strong>{' '}
                    {formatDate(selectedEntity[defaultFieldMappings.lastModified])}
                  </Typography>
                )}
                {selectedEntity[defaultFieldMappings.type] && (
                  <Typography variant="body2" gutterBottom>
                    <strong>{t(defaultI18nKeys.type)}:</strong>{' '}
                    {selectedEntity[defaultFieldMappings.type]}
                  </Typography>
                )}
              </Box>
            )}
          </DialogContent>
          <DialogActions>
            <Button onClick={() => setDetailDialogOpen(false)}>
              {t(defaultI18nKeys.close)}
            </Button>
          </DialogActions>
        </Dialog>

        {/* Action Dialog */}
        <Dialog
          fullWidth
          open={actionDialogOpen}
          onClose={() => setActionDialogOpen(false)}
        >
          <DialogTitle>
            {t(defaultI18nKeys.actions)}
          </DialogTitle>
          <DialogContent>
            <Typography variant="body2">
              {t(defaultI18nKeys.chooseAction)}
            </Typography>
          </DialogContent>
          <DialogActions sx={{ flexDirection: 'column', gap: 1 }}>
            {defaultActionConfig.map((action) => {
              let IconComponent = null;
              if (action.icon === 'Edit') {
                IconComponent = Edit;
              } else if (action.icon === 'Delete') {
                IconComponent = Delete;
              }

              return (
                <Button
                  key={action.key}
                  disabled={ action.disabled ? action.disabled(selectedEntity) : false}
                  fullWidth
                  variant="outlined"
                  color={action.color || 'primary'}
                  onClick={() => handleAction(action.key)}
                  startIcon={IconComponent ? <IconComponent /> : null}
                >
                  {t(action.label)}
                </Button>
              );
            })}
            <Button
              fullWidth
              variant="text"
              onClick={() => setActionDialogOpen(false)}
            >
              {t(defaultI18nKeys.cancel)}
            </Button>
          </DialogActions>
        </Dialog>
      </>
    );
  }

  return (
    <>
      <List sx={{ width: '100%', bgcolor: 'background.paper' }}>
        {entities.map((entity) => (
          <ListItem
            key={entity.id}
            alignItems="flex-start"
            sx={{
              borderBottom: 1,
              borderColor: 'divider',
              '&:active': {
                backgroundColor: 'action.hover'
              }
            }}
            onTouchStart={() => handleTouchStart(entity)}
            onTouchEnd={handleTouchEnd}
            onContextMenu={(e) => handleLongPress(entity, e)}
          >
            <ListItemText
              primary={
                <Box sx={{ display: 'flex', alignItems: 'center', mb: 1 }}>
                  <Typography variant="subtitle1" component="div" sx={{ flexGrow: 1 }} style={{ color: theme.palette.primary.main }}>
                    {entity[defaultFieldMappings.name] || t('common:unnamed')}
                  </Typography>
                  {entity[defaultFieldMappings.status] !== undefined && (
                    <Chip
                      label={getStatusLabel(entity[defaultFieldMappings.status])}
                      size="small"
                      color={getStatusColor(entity[defaultFieldMappings.status])}
                      variant="outlined"
                    />
                  )}
                </Box>
              }
              secondary={
                <Box>
                  {entity[defaultFieldMappings.type] && (
                    <Typography variant="body2" component="span" sx={{ display: 'block', mb: 0.5 }}>
                      {entity[defaultFieldMappings.type]}
                    </Typography>
                  )}
                  <Typography variant="body2" color="text.secondary">
                    {entity[defaultFieldMappings.description] || t('common:noDescription')}
                  </Typography>
                </Box>
              }
              secondaryTypographyProps={{ component: 'div' }}
            />
          </ListItem>
        ))}
      </List>
      <ListItem style={{ alignItems: 'center' }}>
        <IconButton
          edge="end"
          aria-label="actions"
          onClick={(e) => handleActionMenu(entity, e)}
          size="small"
        >
          <MoreVert />
        </IconButton>
      </ListItem>

      {/* Detail Dialog */}
      <Dialog
        open={detailDialogOpen}
        onClose={() => setDetailDialogOpen(false)}
        fullScreen
      >
        <DialogTitle>
          {selectedEntity?.[defaultFieldMappings.name] || t('common:unnamed')}
        </DialogTitle>
        <DialogContent>
          {selectedEntity && (
            <Box sx={{ mt: 2 }}>
              {selectedEntity[defaultFieldMappings.status] !== undefined && (
                <Typography variant="body2" gutterBottom>
                  <strong>{t(defaultI18nKeys.status)}:</strong>{' '}
                  {getStatusLabel(selectedEntity[defaultFieldMappings.status])}
                </Typography>
              )}
              {selectedEntity[defaultFieldMappings.description] && (
                <Typography variant="body2" gutterBottom>
                  <strong>{t(defaultI18nKeys.description)}:</strong>{' '}
                  {selectedEntity[defaultFieldMappings.description]}
                </Typography>
              )}
              {selectedEntity[defaultFieldMappings.lastModified] && (
                <Typography variant="body2" gutterBottom>
                  <strong>{t(defaultI18nKeys.lastModified)}:</strong>{' '}
                  {formatDate(selectedEntity[defaultFieldMappings.lastModified])}
                </Typography>
              )}
              {selectedEntity[defaultFieldMappings.type] && (
                <Typography variant="body2" gutterBottom>
                  <strong>{t(defaultI18nKeys.type)}:</strong>{' '}
                  {selectedEntity[defaultFieldMappings.type]}
                </Typography>
              )}
            </Box>
          )}
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setDetailDialogOpen(false)}>
            {t(defaultI18nKeys.close)}
          </Button>
        </DialogActions>
      </Dialog>

      {/* Action Dialog */}
      <Dialog
        fullWidth
        open={actionDialogOpen}
        onClose={() => setActionDialogOpen(false)}
      >
        <DialogTitle>
          {t(defaultI18nKeys.actions)}
        </DialogTitle>
        <DialogContent>
          <Typography variant="body2">
            {t(defaultI18nKeys.chooseAction)}
          </Typography>
        </DialogContent>
        <DialogActions sx={{ flexDirection: 'column', gap: 1 }}>
          {defaultActionConfig.map((action) => {
            let IconComponent = null;
            if (action.icon === 'Edit') {
              IconComponent = Edit;
            } else if (action.icon === 'Delete') {
              IconComponent = Delete;
            }

            return (
              <Button
                key={action.key}
                fullWidth
                disabled={ action.disabled ? action.disabled(selectedEntity) : false}
                variant="outlined"
                color={action.color || 'primary'}
                onClick={() => handleAction(action.key)}
                startIcon={IconComponent ? <IconComponent /> : null}
              >
                {t(action.label)}
              </Button>
            );
          })}
          <Button
            fullWidth
            variant="text"
            onClick={() => setActionDialogOpen(false)}
          >
            {t(defaultI18nKeys.cancel)}
          </Button>
        </DialogActions>
      </Dialog>
    </>
  );
};

export default MobileEntityList;
