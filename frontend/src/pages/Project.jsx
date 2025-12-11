import React, { useState, useEffect } from 'react';
import {
  Card,
  CardContent,
  CardHeader,
  Box,
  TextField,
  Select,
  MenuItem,
  FormControl,
  InputLabel,
  Button,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  Typography,
  IconButton,
  Chip,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  InputAdornment
} from '@mui/material';
import {
  Search as SearchIcon,
  Edit as EditIcon,
  Add as AddIcon,
  Upload as UploadIcon,
  Download as DownloadIcon
} from '@mui/icons-material';
import { toast } from 'react-toastify';
import { getActiveProjects, getProjectTasks, importProjects, exportProjects, createProjectTask, updateProjectTask } from '../api/timecard/timecardService';

/**
 * Project Management Page
 * Manage projects, tasks, and activities
 */
const Project = () => {
  const [projects, setProjects] = useState([]);
  const [loading, setLoading] = useState(false);
  const [searchTerm, setSearchTerm] = useState('');
  const [statusFilter, setStatusFilter] = useState('ALL');
  const [showDialog, setShowDialog] = useState(false);
  const [editingProject, setEditingProject] = useState(null);
  const [formData, setFormData] = useState({
    projectCode: '',
    projectName: '',
    taskNumber: '',
    activity: '',
    status: 'ACTIVE'
  });

  useEffect(() => {
    fetchProjects();
  }, []);

  // Refetch when search term changes
  useEffect(() => {
    const timeoutId = setTimeout(() => {
      fetchProjects();
    }, 300); // Debounce search
    return () => clearTimeout(timeoutId);
  }, [searchTerm]);

  const fetchProjects = async () => {
    try {
      setLoading(true);
      // Use getProjectTasks API which supports all statuses (ACTIVE, COMPLETED, ALL)
      const response = await getProjectTasks(searchTerm, 'ALL');
      
      // API returns ApiResponse format: { status: "SUCCESS", message: "...", data: [...] }
      const projectTasks = response.data?.data || [];
      
      // Transform to match our format, preserving the id from backend (format: projectId-taskId)
      const projectMap = new Map();
      projectTasks.forEach(item => {
        const key = `${item.projectCode}-${item.taskNumber}-${item.activity}`;
        if (!projectMap.has(key)) {
          projectMap.set(key, {
            id: item.id || key, // Backend returns id in format: "projectId-taskId"
            projectCode: item.projectCode,
            projectName: item.projectName,
            taskNumber: item.taskNumber,
            activity: item.activity,
            status: item.status || 'ACTIVE'
          });
        }
      });
      setProjects(Array.from(projectMap.values()));
    } catch (error) {
      console.error('Error fetching projects:', error);
      toast.error('Failed to load projects');
    } finally {
      setLoading(false);
    }
  };

  const filteredProjects = projects.filter(project => {
    const matchesSearch =
      project.projectCode.toLowerCase().includes(searchTerm.toLowerCase()) ||
      project.projectName.toLowerCase().includes(searchTerm.toLowerCase()) ||
      project.taskNumber.toLowerCase().includes(searchTerm.toLowerCase());

    const matchesStatus = statusFilter === 'ALL' || project.status === statusFilter;

    return matchesSearch && matchesStatus;
  });

  const handleSearchChange = (event) => {
    setSearchTerm(event.target.value);
  };

  const handleStatusFilterChange = (event) => {
    setStatusFilter(event.target.value);
  };

  const handleAdd = () => {
    setEditingProject(null);
    setFormData({
      projectCode: '',
      projectName: '',
      taskNumber: '',
      activity: '',
      status: 'ACTIVE'
    });
    setShowDialog(true);
  };

  const handleEdit = (project) => {
    setEditingProject(project);
    setFormData({
      projectCode: project.projectCode,
      projectName: project.projectName,
      taskNumber: project.taskNumber,
      activity: project.activity,
      status: project.status
    });
    setShowDialog(true);
  };

  const handleSave = async () => {
    // Validation
    if (!formData.projectCode || !formData.projectName || !formData.taskNumber || !formData.activity) {
      toast.error('All fields are required');
      return;
    }

    // Check for duplicate project code (only in add mode)
    if (!editingProject) {
      const duplicate = projects.find(
        p => p.projectCode === formData.projectCode &&
        p.taskNumber === formData.taskNumber &&
        p.activity === formData.activity
      );
      if (duplicate) {
        toast.error('Project code already exists');
        return;
      }
    }

    try {
      if (editingProject) {
        // Update existing project-task-activity
        // id format from backend: "projectId-taskId"
        if (!editingProject.id || !editingProject.id.includes('-')) {
          toast.error('Invalid project ID format');
          return;
        }
        
        const updateData = {
          projectName: formData.projectName,
          taskNumber: formData.taskNumber,
          activity: formData.activity,
          status: formData.status
        };
        
        const response = await updateProjectTask(editingProject.id, updateData);
        
        // Response format: { status: "SUCCESS", message: "...", data: {...} }
        if (response.data && response.data.status === 'SUCCESS') {
          toast.success('Project updated successfully');
          setShowDialog(false);
          setEditingProject(null);
          // Reset form
          setFormData({
            projectCode: '',
            projectName: '',
            taskNumber: '',
            activity: '',
            status: 'ACTIVE'
          });
          // Refresh list after save
          await fetchProjects();
        } else {
          toast.error(response.data?.message || 'Failed to update project');
        }
      } else {
        // Create new project-task-activity
        const createData = {
          projectCode: formData.projectCode,
          projectName: formData.projectName,
          taskNumber: formData.taskNumber,
          activity: formData.activity,
          status: formData.status || 'ACTIVE'
        };
        
        const response = await createProjectTask(createData);
        
        // Response format: { status: "SUCCESS", message: "...", data: {...} }
        if (response.data && response.data.status === 'SUCCESS') {
          toast.success('Project created successfully');
          setShowDialog(false);
          // Reset form
          setFormData({
            projectCode: '',
            projectName: '',
            taskNumber: '',
            activity: '',
            status: 'ACTIVE'
          });
          // Refresh list after save
          await fetchProjects();
        } else {
          toast.error(response.data?.message || 'Failed to create project');
        }
      }
    } catch (error) {
      console.error('Error saving project:', error);
      const errorMessage = error.response?.data?.message || error.message || 'Failed to save project';
      toast.error(errorMessage);
    }
  };

  const handleImport = async (event) => {
    const file = event.target.files?.[0];
    if (!file) return;

    try {
      const response = await importProjects(file);
      toast.success('Projects imported successfully');
      fetchProjects();
    } catch (error) {
      console.error('Error importing projects:', error);
      toast.error('Failed to import projects');
    }
  };

  const handleExport = async () => {
    try {
      const response = await exportProjects(statusFilter !== 'ALL' ? statusFilter : undefined);
      
      // Create blob and download
      const url = window.URL.createObjectURL(new Blob([response.data]));
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', `projects-${new Date().toISOString().split('T')[0]}.xlsx`);
      document.body.appendChild(link);
      link.click();
      link.remove();
      
      toast.success('Projects exported successfully');
    } catch (error) {
      console.error('Error exporting projects:', error);
      toast.error('Failed to export projects');
    }
  };

  return (
    <Box sx={{ p: 3 }}>
      <Card>
        <CardHeader
          title="Project Management"
          subheader="Manage projects, tasks, and activities"
          action={
            <Box sx={{ display: 'flex', gap: 1 }}>
              <Button
                variant="outlined"
                startIcon={<UploadIcon />}
                component="label"
                size="small"
              >
                Import
                <input
                  type="file"
                  hidden
                  accept=".xlsx,.xls"
                  onChange={handleImport}
                />
              </Button>
              <Button
                variant="outlined"
                startIcon={<DownloadIcon />}
                onClick={handleExport}
                size="small"
              >
                Export
              </Button>
              <Button
                variant="contained"
                startIcon={<AddIcon />}
                onClick={handleAdd}
                size="small"
              >
                Add Project
              </Button>
            </Box>
          }
        />
        <CardContent>
          {/* Filter area */}
          <Box sx={{ display: 'flex', flexDirection: { xs: 'column', md: 'row' }, gap: 2, mb: 3 }}>
            <TextField
              placeholder="Search by project code, name, or task number..."
              value={searchTerm}
              onChange={handleSearchChange}
              fullWidth
              size="small"
              InputProps={{
                startAdornment: (
                  <InputAdornment position="start">
                    <SearchIcon />
                  </InputAdornment>
                ),
              }}
            />
            <FormControl sx={{ minWidth: { xs: '100%', md: 200 } }} size="small">
              <InputLabel>Status</InputLabel>
              <Select
                value={statusFilter}
                onChange={handleStatusFilterChange}
                label="Status"
              >
                <MenuItem value="ALL">All Status</MenuItem>
                <MenuItem value="ACTIVE">Active</MenuItem>
                <MenuItem value="COMPLETED">Completed</MenuItem>
              </Select>
            </FormControl>
          </Box>

          {/* Projects table */}
          <TableContainer component={Paper} variant="outlined">
            <Table>
              <TableHead>
                <TableRow>
                  <TableCell>Project Code</TableCell>
                  <TableCell>Project Name</TableCell>
                  <TableCell>Task Number</TableCell>
                  <TableCell>Activity</TableCell>
                  <TableCell>Status</TableCell>
                  <TableCell align="right">Actions</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {loading ? (
                  <TableRow>
                    <TableCell colSpan={6} align="center" sx={{ py: 4 }}>
                      <Typography color="text.secondary">Loading...</Typography>
                    </TableCell>
                  </TableRow>
                ) : filteredProjects.length === 0 ? (
                  <TableRow>
                    <TableCell colSpan={6} align="center" sx={{ py: 4 }}>
                      <Typography color="text.secondary">No projects found</Typography>
                    </TableCell>
                  </TableRow>
                ) : (
                  filteredProjects.map(project => (
                    <TableRow key={project.id} hover>
                      <TableCell>{project.projectCode}</TableCell>
                      <TableCell>{project.projectName}</TableCell>
                      <TableCell>{project.taskNumber}</TableCell>
                      <TableCell>{project.activity}</TableCell>
                      <TableCell>
                        <Chip
                          label={project.status === 'ACTIVE' ? 'Active' : 'Completed'}
                          color={project.status === 'ACTIVE' ? 'primary' : 'default'}
                          variant={project.status === 'ACTIVE' ? 'default' : 'secondary'}
                          size="small"
                        />
                      </TableCell>
                      <TableCell align="right">
                        <IconButton
                          size="small"
                          onClick={() => handleEdit(project)}
                          color="primary"
                        >
                          <EditIcon fontSize="small" />
                        </IconButton>
                      </TableCell>
                    </TableRow>
                  ))
                )}
              </TableBody>
            </Table>
          </TableContainer>

          {/* Statistics */}
          <Box sx={{ mt: 2, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <Typography variant="body2" color="text.secondary">
              Showing {filteredProjects.length} of {projects.length} projects
            </Typography>
          </Box>
        </CardContent>
      </Card>

      {/* Add/Edit Dialog */}
      <Dialog open={showDialog} onClose={() => setShowDialog(false)} maxWidth="sm" fullWidth>
        <DialogTitle>
          {editingProject ? 'Edit Project' : 'Add New Project'}
        </DialogTitle>
        <DialogContent>
          <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2, pt: 2 }}>
            <TextField
              label="Project Code *"
              value={formData.projectCode}
              onChange={(e) => setFormData({ ...formData, projectCode: e.target.value })}
              placeholder="e.g., PROJ001"
              fullWidth
              disabled={!!editingProject}
              required
            />
            <TextField
              label="Project Name *"
              value={formData.projectName}
              onChange={(e) => setFormData({ ...formData, projectName: e.target.value })}
              placeholder="e.g., System Upgrade"
              fullWidth
              required
            />
            <TextField
              label="Task Number *"
              value={formData.taskNumber}
              onChange={(e) => setFormData({ ...formData, taskNumber: e.target.value })}
              placeholder="e.g., T001"
              fullWidth
              required
            />
            <TextField
              label="Activity *"
              value={formData.activity}
              onChange={(e) => setFormData({ ...formData, activity: e.target.value })}
              placeholder="e.g., Development"
              fullWidth
              required
            />
            <FormControl fullWidth>
              <InputLabel>Status</InputLabel>
              <Select
                value={formData.status}
                onChange={(e) => setFormData({ ...formData, status: e.target.value })}
                label="Status"
              >
                <MenuItem value="ACTIVE">Active</MenuItem>
                <MenuItem value="COMPLETED">Completed</MenuItem>
              </Select>
            </FormControl>
          </Box>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setShowDialog(false)}>Cancel</Button>
          <Button onClick={handleSave} variant="contained">
            {editingProject ? 'Update' : 'Create'} Project
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
};

export default Project;

