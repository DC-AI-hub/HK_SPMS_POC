import React, { useState, useEffect } from 'react';
import {
  Dialog,
  DialogContent,
  DialogTitle,
  DialogActions,
  Button,
  Grid,
  TextField,
  Select,
  MenuItem,
  FormControl,
  InputLabel,
  IconButton,
  Typography,
  Box,
  Divider
} from '@mui/material';
import {
  Add as AddIcon,
  Delete as DeleteIcon,
  Search as SearchIcon
} from '@mui/icons-material';
import { toast } from 'react-toastify';

// Import static configuration
import { CLAIM_TYPE_OPTIONS } from '../../../data/timecard/data/mockData';

// Import API services
import { getActiveProjects } from '../../../api/timecard/timecardService';

/**
 * Project Entry Dialog Component
 * Form for adding/editing project timecard entries for a specific date
 */
const ProjectEntryDialog = ({ open, onClose, date, entries, onEntriesUpdate }) => {
  const [localEntries, setLocalEntries] = useState([]);
  const [showProjectSearch, setShowProjectSearch] = useState(false);
  const [searchCurrentIndex, setSearchCurrentIndex] = useState(0);
  const [projects, setProjects] = useState([]);
  const [isLoadingProjects, setIsLoadingProjects] = useState(true);

  // Fetch active projects from backend API
  useEffect(() => {
    if (open) {
      const fetchProjects = async () => {
        try {
          setIsLoadingProjects(true);
          const response = await getActiveProjects();
          console.log('=== Fetched active projects ===', response.data);
          setProjects(response.data);
        } catch (error) {
          console.error('=== Error fetching projects ===', error);
          toast.error('Failed to load projects');
        } finally {
          setIsLoadingProjects(false);
        }
      };

      fetchProjects();
    }
  }, [open]);

  // Initialize form data
  useEffect(() => {
    if (open) {
      if (entries.length > 0) {
        // Map existing entries to form data
        setLocalEntries(entries.map(entry => ({
          projectCode: entry.projectCode || '',
          projectName: entry.projectName || '',
          taskNumber: entry.taskNumber || '',
          activity: entry.activity || '',
          claimType: entry.claimType || 'NORMAL',
          hours: entry.hours?.toString() || '',
          remark: entry.remark || ''
        })));
      } else {
        // Create empty entry
        setLocalEntries([{
          projectCode: '',
          projectName: '',
          taskNumber: '',
          activity: '',
          claimType: 'NORMAL',
          hours: '',
          remark: ''
        }]);
      }
    }
  }, [open, entries]);

  // Handle form field changes
  const handleEntryChange = (index, field, value) => {
    const updatedEntries = [...localEntries];
    updatedEntries[index][field] = value;

    // Auto-fill project info when project code changes
    if (field === 'projectCode' && value) {
      const project = projects.find(
        p => p.projectCode === value && p.status === 'ACTIVE'
      );
      if (project) {
        updatedEntries[index].projectName = project.projectName;
        updatedEntries[index].taskNumber = project.taskNumber;
        updatedEntries[index].activity = project.activity;
      }
    }

    setLocalEntries(updatedEntries);
  };

  // Add new entry
  const handleAddEntry = () => {
    setLocalEntries([...localEntries, {
      projectCode: '',
      projectName: '',
      taskNumber: '',
      activity: '',
      claimType: 'NORMAL',
      hours: '',
      remark: ''
    }]);
  };

  // Remove entry
  const handleRemoveEntry = (index) => {
    if (localEntries.length > 1) {
      const updatedEntries = localEntries.filter((_, i) => i !== index);
      setLocalEntries(updatedEntries);
    }
  };

  // Validate and save entries
  // Open project search dialog
  const handleOpenProjectSearch = (index) => {
    setSearchCurrentIndex(index);
    setShowProjectSearch(true);
  };

  // Select project from search
  const handleSelectProject = (project) => {
    const updatedEntries = [...localEntries];
    updatedEntries[searchCurrentIndex] = {
      ...updatedEntries[searchCurrentIndex],
      projectCode: project.projectCode,
      projectName: project.projectName,
      taskNumber: project.taskNumber,
      activity: project.activity
    };
    setLocalEntries(updatedEntries);
    setShowProjectSearch(false);
  };

  const handleSave = () => {
    // Validate all entries
    for (let i = 0; i < localEntries.length; i++) {
      const entry = localEntries[i];

      // Required fields validation
      if (!entry.projectCode || !entry.taskNumber || !entry.activity || !entry.hours) {
        toast.error(`Entry ${i + 1}: All required fields must be filled`);
        return;
      }

      // Hours validation
      const hoursNum = parseFloat(entry.hours);
      if (isNaN(hoursNum) || hoursNum < 0 || hoursNum > 24) {
        toast.error(`Entry ${i + 1}: Hours must be a number between 0 and 24`);
        return;
      }
    }

    // Convert to TimecardEntry format
    const savedEntries = localEntries.map((entry, index) => ({
      id: `${date}-${index}-${Date.now()}`,
      date: date,
      projectCode: entry.projectCode,
      projectName: entry.projectName,
      taskNumber: entry.taskNumber,
      activity: entry.activity,
      claimType: entry.claimType,
      hours: parseFloat(entry.hours),
      remark: entry.remark
    }));

    onEntriesUpdate(savedEntries);
    toast.success(`Saved ${savedEntries.length} entries for ${date}`);
    onClose();
  };

  const formatDate = (dateStr) => {
    return new Date(dateStr).toLocaleDateString('en-US', {
      weekday: 'long',
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    });
  };

  return (
    <>
    <Dialog
      open={open}
      onClose={onClose}
      maxWidth="lg"
      fullWidth
      PaperProps={{
        sx: { maxHeight: '90vh' }
      }}
    >
      <DialogTitle>
        Project Entries - {date ? formatDate(date) : ''}
      </DialogTitle>

      <DialogContent>
        <Typography variant="body2" color="text.secondary" gutterBottom>
          Add or edit timecard entries for this date
        </Typography>

        <Box sx={{ mt: 2 }}>
          {localEntries.map((entry, index) => (
            <Box key={index} sx={{ 
              mb: 3,
              p: 2,
              border: 1,
              borderColor: 'divider',
              borderRadius: 1,
              bgcolor: 'background.paper'
            }}>
              {/* Entry header */}
              <Box sx={{
                display: 'flex',
                justifyContent: 'space-between',
                alignItems: 'center',
                mb: 2
              }}>
                <Typography variant="subtitle1" fontWeight="bold">
                  Entry {index + 1}
                </Typography>
                {localEntries.length > 1 && (
                  <IconButton
                    onClick={() => handleRemoveEntry(index)}
                    color="error"
                    size="small"
                  >
                    <DeleteIcon fontSize="small" />
                  </IconButton>
                )}
              </Box>

              {/* Entry form */}
              <Grid container spacing={2}>
                {/* Project Code */}
                <Grid item xs={12} sm={6} md={4}>
                  <TextField
                    label="Project Code *"
                    value={entry.projectCode}
                    onChange={(e) => handleEntryChange(index, 'projectCode', e.target.value)}
                    fullWidth
                    size="small"
                    placeholder="e.g., PROJ001"
                    InputProps={{
                      endAdornment: (
                        <IconButton
                          size="small"
                          onClick={() => handleOpenProjectSearch(index)}
                          sx={{ mr: -1 }}
                        >
                          <SearchIcon fontSize="small" />
                        </IconButton>
                      )
                    }}
                  />
                </Grid>

                {/* Project Name */}
                <Grid item xs={12} sm={6} md={4}>
                  <TextField
                    label="Project Name"
                    value={entry.projectName}
                    onChange={(e) => handleEntryChange(index, 'projectName', e.target.value)}
                    fullWidth
                    size="small"
                    placeholder="Enter project name"
                    InputProps={{
                      readOnly: true
                    }}
                    sx={{
                      '& .MuiInputBase-input': {
                        bgcolor: 'grey.50'
                      }
                    }}
                  />
                </Grid>

                {/* Task Number */}
                <Grid item xs={12} sm={6} md={4}>
                  <TextField
                    label="Task Number *"
                    value={entry.taskNumber}
                    onChange={(e) => handleEntryChange(index, 'taskNumber', e.target.value)}
                    fullWidth
                    size="small"
                    placeholder="Enter task number"
                    InputProps={{
                      readOnly: true
                    }}
                    sx={{
                      '& .MuiInputBase-input': {
                        bgcolor: 'grey.50'
                      }
                    }}
                  />
                </Grid>

                {/* Activity */}
                <Grid item xs={12} sm={6} md={4}>
                  <TextField
                    label="Activity *"
                    value={entry.activity}
                    onChange={(e) => handleEntryChange(index, 'activity', e.target.value)}
                    fullWidth
                    size="small"
                    placeholder="Enter activity"
                    InputProps={{
                      readOnly: true
                    }}
                    sx={{
                      '& .MuiInputBase-input': {
                        bgcolor: 'grey.50'
                      }
                    }}
                  />
                </Grid>

                {/* Claim Type */}
                <Grid item xs={12} sm={6} md={4}>
                  <FormControl fullWidth size="small">
                    <InputLabel>Claim Type *</InputLabel>
                    <Select
                      value={entry.claimType}
                      label="Claim Type *"
                      onChange={(e) => handleEntryChange(index, 'claimType', e.target.value)}
                    >
                      {CLAIM_TYPE_OPTIONS.map(option => (
                        <MenuItem key={option.value} value={option.value}>
                          {option.label}
                        </MenuItem>
                      ))}
                    </Select>
                  </FormControl>
                </Grid>

                {/* Hours */}
                <Grid item xs={12} sm={6} md={4}>
                  <TextField
                    label="Hours *"
                    type="number"
                    value={entry.hours}
                    onChange={(e) => handleEntryChange(index, 'hours', e.target.value)}
                    fullWidth
                    size="small"
                    inputProps={{
                      step: 0.5,
                      min: 0,
                      max: 24
                    }}
                    placeholder="0.0"
                  />
                </Grid>

                {/* Remark */}
                <Grid item xs={12} >
                  <TextField
                    label="Remark"
                    value={entry.remark}
                    onChange={(e) => handleEntryChange(index, 'remark', e.target.value)}
                    fullWidth
                    size="small"
                    multiline
                    rows={2}
                    placeholder="Optional notes"
                  />
                </Grid>
              </Grid>

              {index < localEntries.length - 1 && (
                <Divider sx={{ mt: 2 }} />
              )}
            </Box>
          ))}

          {/* Add Another Entry button */}
          <Button
            startIcon={<AddIcon />}
            onClick={handleAddEntry}
            variant="outlined"
            fullWidth
            sx={{ mt: 2 }}
          >
            Add Another Entry
          </Button>
        </Box>
      </DialogContent>

      <DialogActions>
        <Button onClick={onClose}>
          Cancel
        </Button>
        <Button onClick={handleSave} variant="contained">
          Save Entries
        </Button>
      </DialogActions>
    </Dialog>

    {/* Project Search Dialog */}
    {showProjectSearch && (
    <Dialog
      open={showProjectSearch}
      onClose={() => setShowProjectSearch(false)}
      maxWidth="md"
      fullWidth
    >
      <DialogTitle>
        Select Project
      </DialogTitle>
      <DialogContent>
        <Typography variant="body2" color="text.secondary" gutterBottom>
          Choose from active projects
        </Typography>
        <Box sx={{ mt: 2, maxHeight: 400, overflow: 'auto' }}>
          {projects
            .filter(project => project.status === 'ACTIVE')
            .map(project => (
              <Box
                key={project.id}
                onClick={() => handleSelectProject(project)}
                sx={{
                  p: 2,
                  border: 1,
                  borderColor: 'divider',
                  borderRadius: 1,
                  mb: 1,
                  cursor: 'pointer',
                  '&:hover': {
                    bgcolor: 'grey.50'
                  }
                }}
              >
                <Typography variant="subtitle1" fontWeight="bold">
                  {project.projectCode} - {project.projectName}
                </Typography>
                <Typography variant="body2" color="text.secondary">
                  Task: {project.taskNumber} | Activity: {project.activity}
                </Typography>
              </Box>
            ))}
        </Box>
      </DialogContent>
      <DialogActions>
        <Button onClick={() => setShowProjectSearch(false)}>
          Cancel
        </Button>
      </DialogActions>
    </Dialog>
    )}
    </>
  );
};

export default ProjectEntryDialog;