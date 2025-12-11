import React, { useState, useEffect } from 'react';
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  Select,
  MenuItem,
  Button,
  FormControl,
  InputLabel,
  Box
} from '@mui/material';
import { toast } from 'react-toastify';

/**
 * HolidayDialog Component
 * Dialog for adding/editing holidays
 */
const HolidayDialog = ({ open, onClose, date, holiday, onSave }) => {
  const [formData, setFormData] = useState({
    date: '',
    country: 'CN',
    name: '',
    type: 'PUBLIC_HOLIDAY'
  });

  useEffect(() => {
    if (open) {
      if (holiday) {
        // Edit mode
        setFormData({
          date: holiday.date || '',
          country: holiday.country || 'CN',
          name: holiday.name || '',
          type: holiday.type || 'PUBLIC_HOLIDAY'
        });
      } else if (date) {
        // Add mode with pre-filled date
        setFormData({
          date: date,
          country: 'CN',
          name: '',
          type: 'PUBLIC_HOLIDAY'
        });
      }
    }
  }, [open, date, holiday]);

  const handleChange = (field) => (event) => {
    setFormData({
      ...formData,
      [field]: event.target.value
    });
  };

  const handleSave = () => {
    if (!formData.date || !formData.name) {
      toast.error('Date and Holiday Name are required');
      return;
    }

    const holidayData = {
      ...formData,
      id: holiday?.id || `holiday-${Date.now()}`
    };

    onSave(holidayData);
    onClose();
  };

  return (
    <Dialog open={open} onClose={onClose} maxWidth="sm" fullWidth>
      <DialogTitle>
        {holiday ? 'Edit Holiday' : 'Add Holiday'} - {formData.date || date}
      </DialogTitle>
      <DialogContent>
        <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2, pt: 2 }}>
          <TextField
            label="Date"
            value={formData.date}
            disabled
            fullWidth
            sx={{ bgcolor: 'grey.50' }}
          />

          <FormControl fullWidth>
            <InputLabel>Country *</InputLabel>
            <Select
              value={formData.country}
              onChange={handleChange('country')}
              label="Country *"
            >
              <MenuItem value="CN">CN - China</MenuItem>
              <MenuItem value="HK">HK - Hong Kong</MenuItem>
              <MenuItem value="US">US - United States</MenuItem>
              <MenuItem value="UK">UK - United Kingdom</MenuItem>
            </Select>
          </FormControl>

          <TextField
            label="Holiday Name *"
            value={formData.name}
            onChange={handleChange('name')}
            placeholder="e.g., New Year's Day"
            fullWidth
            required
          />

          <FormControl fullWidth>
            <InputLabel>Type</InputLabel>
            <Select
              value={formData.type}
              onChange={handleChange('type')}
              label="Type"
            >
              <MenuItem value="PUBLIC_HOLIDAY">Public Holiday</MenuItem>
              <MenuItem value="COMPANY_HOLIDAY">Company Holiday</MenuItem>
            </Select>
          </FormControl>
        </Box>
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose}>Cancel</Button>
        <Button onClick={handleSave} variant="contained">
          {holiday ? 'Update' : 'Add'} Holiday
        </Button>
      </DialogActions>
    </Dialog>
  );
};

export default HolidayDialog;

