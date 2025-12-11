import React, { useState, useEffect } from 'react';
import {
  Box,
  Paper,
  Typography,
  TextField,
  Button,
  Alert,
  CircularProgress,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Chip,
  IconButton,
  List,
  ListItem,
  ListItemText,
  ListItemSecondaryAction,
  Divider,
  Grid,
  Switch,
  FormControlLabel,
  OutlinedInput,
  Checkbox,
  ListItemText as MuiListItemText
} from '@mui/material';
import {
  Add as AddIcon,
  Edit as EditIcon,
  Delete as DeleteIcon,
  ExpandMore as ExpandMoreIcon,
  ExpandLess as ExpandLessIcon
} from '@mui/icons-material';
import { useConfig } from '../../hooks/useConfig';
import { useTranslation } from 'react-i18next';
import IconSelector from '../common/IconSelector';
import RoleChip from '../common/RoleChip';

/**
 * MenuConfigPanel - Component for managing menu configuration
 * Provides CRUD operations for menu items with hierarchy management
 */
export default function MenuConfigPanel() {
  const { t } = useTranslation('system');
  const {
    menuConfig,
    roles,
    loading,
    error,
    createMenuItem,
    updateMenuItem,
    deleteMenuItem,
    refreshConfig
  } = useConfig();

  const [dialogOpen, setDialogOpen] = useState(false);
  const [editingItem, setEditingItem] = useState(null);
  const [formData, setFormData] = useState({
    title: '',
    path: '',
    icon: '',
    displayOrder: 0,
    active: true,
    parentMenuId: null,
    allowedRoleIds: []
  });
  const [formErrors, setFormErrors] = useState({});
  const [expandedItems, setExpandedItems] = useState(new Set());
  const [saveStatus, setSaveStatus] = useState({ success: null, message: '' });
  const [submitting, setSubmitting] = useState(false);


  // Reset form when dialog opens/closes
  useEffect(() => {
    if (dialogOpen && editingItem) {
      setFormData({
        title: editingItem.title,
        path: editingItem.path,
        icon: editingItem.icon || '',
        displayOrder: editingItem.displayOrder,
        active: editingItem.active,
        parentMenuId: editingItem.parentMenuId || null,
        allowedRoleIds: editingItem.allowedRoleIds || []
      });
    } else if (!dialogOpen) {
      setFormData({
        title: '',
        path: '',
        icon: '',
        displayOrder: 0,
        active: true,
        parentMenuId: null,
        allowedRoleIds: []
      });
      setEditingItem(null);
      setFormErrors({});
    }
  }, [dialogOpen, editingItem]);

  // Validate form
  const validateForm = () => {
    const errors = {};
    
    if (!formData.title.trim()) {
      errors.title = t('menu.validation.titleRequired');
    } else if (formData.title.length > 100) {
      errors.title = t('menu.validation.titleTooLong');
    }

    if (!formData.path.trim()) {
      errors.path = t('menu.validation.pathRequired');
    }

    if (formData.displayOrder < 0) {
      errors.displayOrder = t('menu.validation.displayOrderNonNegative');
    }

    setFormErrors(errors);
    return Object.keys(errors).length === 0;
  };

  // Handle form input changes
  const handleInputChange = (field, value) => {
    setFormData(prev => ({ ...prev, [field]: value }));
    
    // Clear error when user starts typing
    if (formErrors[field]) {
      setFormErrors(prev => ({ ...prev, [field]: '' }));
    }
  };

  // Handle dialog open for new item
  const handleAddNew = () => {
    setEditingItem(null);
    setDialogOpen(true);
  };

  // Handle dialog open for editing
  const handleEdit = (item) => {
    setEditingItem(item);
    setDialogOpen(true);
  };

  // Handle dialog close
  const handleDialogClose = () => {
    setDialogOpen(false);
  };

  // Handle form submission
  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!validateForm()) {
      return;
    }

    setSubmitting(true);
    try {
      if (editingItem) {
        await updateMenuItem(editingItem.id, formData);
        setSaveStatus({ success: true, message: t('menu.status.updateSuccess') });
        setDialogOpen(false);
        refreshConfig();
      } else {
        await createMenuItem(formData);
        setSaveStatus({ success: true, message: t('menu.status.createSuccess') });
        setDialogOpen(false);
        refreshConfig();
      }
    } catch (error) {
      console.error('Error saving menu item:', error);
      setSaveStatus({
        success: false,
        message: error.response?.data?.message || t('menu.status.saveError')
      });
      // Keep dialog open to show error
    } finally {
      setSubmitting(false);
    }
  };

  // Handle delete
  const handleDelete = async (itemId) => {
    if (!window.confirm(t('menu.confirmDelete'))) {
      return;
    }

    try {
      await deleteMenuItem(itemId);
      setSaveStatus({ success: true, message: t('menu.status.deleteSuccess') });
      refreshConfig();
    } catch (error) {
      setSaveStatus({
        success: false,
        message: error.response?.data?.message || t('menu.status.deleteError')
      });
    }
  };

  // Toggle expanded state for hierarchy
  const toggleExpand = (itemId) => {
    setExpandedItems(prev => {
      const newSet = new Set(prev);
      if (newSet.has(itemId)) {
        newSet.delete(itemId);
      } else {
        newSet.add(itemId);
      }
      return newSet;
    });
  };

  // Get child items for a parent
  const getChildItems = (parentId) => {
    return menuConfig
      .filter(item => item.parentMenuId === parentId)
      .sort((a, b) => a.displayOrder - b.displayOrder);
  };

  // Get top-level items (no parent)
  const getTopLevelItems = () => {
    return menuConfig
      .filter(item => item.parentMenuId === null || item.parentMenuId === 0)
      .sort((a, b) => a.displayOrder - b.displayOrder);
  };

  // Render menu item with hierarchy
  const renderMenuItem = (item, depth = 0) => {
    const children = getChildItems(item.id);
    const isExpanded = expandedItems.has(item.id);
    const hasChildren = children.length > 0;

    return (
      <React.Fragment key={item.id}>
        <ListItem sx={{ pl: depth * 4 }}>
          <ListItemText
            primary={
              <Box display="flex" alignItems="center" gap={1}>
                <Typography variant="body1">{item.title}</Typography>
                {!item.active && (
                  <Chip label={t('menu.inactive')} size="small" color="default" variant="outlined" />
                )}
                <Chip label={item.path} size="small" color="primary" variant="outlined" />
              </Box>
            }
            secondary={
              <Box mt={1}>
                <Typography variant="body2" color="text.secondary">
                  {t('menu.order')}: {item.displayOrder} | {t('menu.icon')}: {item.icon || t('menu.iconNone')}
                </Typography>
                {item.allowedRoleIds && item.allowedRoleIds.length > 0 && (
                  <Box mt={0.5}>
                    <Typography variant="caption" color="text.secondary" component="span">
                      {t('menu.roles')}:
                    </Typography>
                    <Box display="flex" flexWrap="wrap" gap={0.5} mt={0.5}>
                      {item.allowedRoleIds.map(roleId => {
                        const role = roles.find(r => r.id === roleId);
                        return role ? (
                          <RoleChip 
                            key={roleId} 
                            role={role} 
                            showDelete={false}
                            onRemove={() => {}} // Empty function since showDelete=false
                            disabled={true}
                          />
                        ) : (
                          <Chip 
                            key={roleId}
                            label={`ID: ${roleId}`}
                            size="small"
                            variant="outlined"
                            color="default"
                          />
                        );
                      })}
                    </Box>
                  </Box>
                )}
              </Box>
            }
          />
          <ListItemSecondaryAction>
            <Box display="flex" gap={1}>
              {hasChildren && (
                <IconButton onClick={() => toggleExpand(item.id)} size="small">
                  {isExpanded ? <ExpandLessIcon /> : <ExpandMoreIcon />}
                </IconButton>
              )}
              <IconButton onClick={() => handleEdit(item)} size="small" color="primary">
                <EditIcon />
              </IconButton>
              <IconButton onClick={() => handleDelete(item.id)} size="small" color="error">
                <DeleteIcon />
              </IconButton>
            </Box>
          </ListItemSecondaryAction>
        </ListItem>
        
        {hasChildren && isExpanded && (
          <List component="div" disablePadding>
            {children.map(child => renderMenuItem(child, depth + 1))}
          </List>
        )}
        
        <Divider />
      </React.Fragment>
    );
  };

  if (loading && menuConfig.length === 0) {
    return (
      <Box display="flex" justifyContent="center" alignItems="center" minHeight="200px">
        <CircularProgress />
        <Typography variant="h6" sx={{ ml: 2 }}>
          {t('menu.loading')}
        </Typography>
      </Box>
    );
  }

  if (error) {
    return (
      <Alert severity="error" sx={{ mb: 2 }}>
        {t('errors.loadingError')}: {error}
      </Alert>
    );
  }

  return (
    <Box>
      <Paper sx={{ p: 3 }}>
        <Box display="flex" justifyContent="space-between" alignItems="center" mb={3}>
          <Typography variant="h5" component="h2">
            {t('menu.management')}
          </Typography>
          <Button
            variant="contained"
            startIcon={<AddIcon />}
            onClick={handleAddNew}
          >
            {t('menu.addMenuItem')}
          </Button>
        </Box>

        {saveStatus.success !== null && (
          <Alert 
            severity={saveStatus.success ? 'success' : 'error'} 
            sx={{ mb: 3 }}
            onClose={() => setSaveStatus({ success: null, message: '' })}
          >
            {saveStatus.message}
          </Alert>
        )}

        {menuConfig.length === 0 ? (
          <Typography variant="body1" color="text.secondary" textAlign="center" py={4}>
            {t('menu.noMenuItems')}
          </Typography>
        ) : (
          <List>
            {getTopLevelItems().map(item => renderMenuItem(item))}
          </List>
        )}
      </Paper>

      {/* Add/Edit Dialog */}
      <Dialog open={dialogOpen} onClose={handleDialogClose} maxWidth="md" fullWidth sx={{ '& .MuiDialog-paper': { minHeight: '600px' } }}>
        <DialogTitle sx={{ pb: 2 }}>
          <Typography variant="h6" component="div">
            {editingItem ? t('menu.editMenuItem') : t('menu.addNewMenuItem')}
          </Typography>
        </DialogTitle>
        <DialogContent dividers sx={{ py: 3 }}>
          <Box component="form" onSubmit={handleSubmit}>
            <Grid container spacing={4}>
              <Grid style={ { width: '100%' } }>
                <TextField
                  fullWidth
                  label={t('menu.title')}
                  value={formData.title}
                  onChange={(e) => handleInputChange('title', e.target.value)}
                  error={!!formErrors.title}
                  helperText={formErrors.title}
                  required
                />
              </Grid>
              
              <Grid style={ { width: '100%' } }>
                <TextField
                  fullWidth
                  label={t('menu.path')}
                  value={formData.path}
                  onChange={(e) => handleInputChange('path', e.target.value)}
                  error={!!formErrors.path}
                  helperText={formErrors.path}
                  required
                />
              </Grid>
              
              <Grid style={ { width: '100%' } }>
                <IconSelector
                  value={formData.icon}
                  onChange={(value) => handleInputChange('icon', value)}
                  label={t('menu.icon')}
                />
              </Grid>
              
              <Grid style={ { width: '100%' } }>
                <TextField
                  fullWidth
                  type="number"
                  label={t('menu.displayOrder')}
                  value={formData.displayOrder}
                  onChange={(e) => handleInputChange('displayOrder', parseInt(e.target.value) || 0)}
                  error={!!formErrors.displayOrder}
                  helperText={formErrors.displayOrder}
                />
              </Grid>
              
              <Grid style={ { width: '100%' } }>
                <FormControl fullWidth>
                  <InputLabel>{t('menu.parentMenu')}</InputLabel>
                  <Select
                    value={formData.parentMenuId || ''}
                    onChange={(e) => handleInputChange('parentMenuId', e.target.value || null)}
                    label={t('menu.parentMenu')}
                  >
                    <MenuItem value="">{t('menu.noParent')}</MenuItem>
                    {menuConfig
                      .filter(item => item.id !== editingItem?.id) // Don't allow self as parent
                      .map(item => (
                        <MenuItem key={item.id} value={item.id}>
                          {item.title}
                        </MenuItem>
                      ))}
                  </Select>
                </FormControl>
              </Grid>
              
              <Grid style={ { width: '100%' } }>
                <FormControlLabel
                  control={
                    <Switch
                      checked={formData.active}
                      onChange={(e) => handleInputChange('active', e.target.checked)}
                      color="primary"
                    />
                  }
                  label={t('menu.active')}
                />
              </Grid>
              
              <Grid style={ { width: '100%' } }>
                <FormControl fullWidth>
                  <InputLabel>{t('menu.allowedRoles')}</InputLabel>
                  <Select fullWidth
                    multiple
                    value={formData.allowedRoleIds}
                    onChange={(e) => handleInputChange('allowedRoleIds', e.target.value)}
                    input={<OutlinedInput label={t('menu.allowedRoles')} />}
                    renderValue={(selected) => selected.map(id => {
                      const role = roles.find(r => r.id === id);
                      return role ? role.name : id;
                    }).join(', ')}
                    
                  >
                    {loading ? (
                      <MenuItem disabled>
                        <CircularProgress size={20} sx={{ mr: 2 }} />
                        {t('menu.loadingRoles')}
                      </MenuItem>
                    ) : (
                      roles.map((role) => (
                        <MenuItem key={role.id} value={role.id}>
                          <Checkbox checked={formData.allowedRoleIds.indexOf(role.id) > -1} />
                          <MuiListItemText primary={role.name} secondary={`ID: ${role.id}`} />
                        </MenuItem>
                      ))
                    )}
                  </Select>
                </FormControl>
              </Grid>
            </Grid>
          </Box>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleDialogClose} disabled={submitting}>
            {t('theme.cancel')}
          </Button>
          <Button 
            onClick={handleSubmit} 
            variant="contained"
            disabled={submitting}
            startIcon={submitting ? <CircularProgress size={16} /> : null}
          >
            {submitting ? t('menu.saving') : (editingItem ? t('theme.updateTheme') : t('theme.createTheme'))}
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
}
