package com.spms.backend.service.timecard.impl;

import com.spms.backend.controller.dto.project.ProjectCreateDTO;
import com.spms.backend.controller.dto.project.ProjectDTO;
import com.spms.backend.controller.dto.project.TaskCreateDTO;
import com.spms.backend.controller.dto.project.TaskDTO;
import com.spms.backend.controller.dto.timecard.ProjectTaskDTO;
import com.spms.backend.controller.dto.timecard.ProjectTaskCreateDTO;
import com.spms.backend.controller.dto.timecard.ProjectTaskUpdateDTO;
import com.spms.backend.model.ProjectStatus;
import com.spms.backend.repository.entities.project.ProjectEntity;
import com.spms.backend.repository.entities.project.TaskEntity;
import com.spms.backend.repository.project.ProjectRepository;
import com.spms.backend.repository.project.TaskRepository;
import com.spms.backend.service.project.ProjectService;
import com.spms.backend.service.project.TaskService;
import com.spms.backend.service.timecard.ProjectTaskActivityService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 项目-任务-活动服务实现类
 * 提供项目-任务-活动组合的CRUD功能
 */
@Service
public class ProjectTaskActivityServiceImpl implements ProjectTaskActivityService {
    
    private final ProjectService projectService;
    private final TaskService taskService;
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    
    @Autowired
    public ProjectTaskActivityServiceImpl(
            ProjectService projectService,
            TaskService taskService,
            ProjectRepository projectRepository,
            TaskRepository taskRepository) {
        this.projectService = projectService;
        this.taskService = taskService;
        this.projectRepository = projectRepository;
        this.taskRepository = taskRepository;
    }
    
    /**
     * 获取项目-任务-活动列表（支持搜索和状态筛选）
     */
    @Override
    @Transactional(readOnly = true)
    public List<ProjectTaskDTO> getProjectTasks(String search, String status) {
        // 获取所有激活的项目
        List<ProjectEntity> projects = projectRepository.findAll();
        
        // 应用状态筛选
        if (status != null && !status.equals("ALL")) {
            ProjectStatus projectStatus = ProjectStatus.valueOf(status);
            projects = projects.stream()
                    .filter(p -> p.getStatus() == projectStatus && p.getIsActive())
                    .collect(Collectors.toList());
        } else {
            projects = projects.stream()
                    .filter(ProjectEntity::getIsActive)
                    .collect(Collectors.toList());
        }
        
        // 应用搜索筛选
        if (search != null && !search.trim().isEmpty()) {
            String searchLower = search.trim().toLowerCase();
            projects = projects.stream()
                    .filter(p -> 
                        p.getProjectCode().toLowerCase().contains(searchLower) ||
                        p.getProjectName().toLowerCase().contains(searchLower))
                    .collect(Collectors.toList());
        }
        
        // 转换为扁平化的ProjectTaskDTO列表
        return projects.stream()
                .flatMap(project -> {
                    // 获取项目的所有激活任务
                    List<TaskEntity> tasks = taskRepository.findByProjectIdAndIsActiveTrue(project.getId());
                    
                    if (tasks.isEmpty()) {
                        // 如果没有任务，返回只有项目的DTO
                        return java.util.stream.Stream.of(createProjectTaskDTO(project, null));
                    } else {
                        // 如果有任务，为每个任务创建一个DTO
                        // 如果搜索关键字包含任务编号，进行过滤
                        if (search != null && !search.trim().isEmpty()) {
                            String searchLower = search.trim().toLowerCase();
                            tasks = tasks.stream()
                                    .filter(t -> t.getTaskNumber().toLowerCase().contains(searchLower))
                                    .collect(Collectors.toList());
                        }
                        
                        return tasks.stream()
                                .map(task -> createProjectTaskDTO(project, task));
                    }
                })
                .collect(Collectors.toList());
    }
    
    /**
     * 创建项目-任务-活动组合
     */
    @Override
    @Transactional
    public ProjectTaskDTO createProjectTask(ProjectTaskCreateDTO createDTO) {
        // 1. 查找或创建项目
        ProjectEntity project;
        Optional<ProjectEntity> existingProject = projectRepository.findByProjectCode(createDTO.getProjectCode());
        
        if (existingProject.isPresent()) {
            project = existingProject.get();
            // 如果项目存在但状态不是ACTIVE，更新状态
            if (createDTO.getStatus() != null && !createDTO.getStatus().equals(project.getStatus().name())) {
                project.setStatus(ProjectStatus.valueOf(createDTO.getStatus()));
                projectRepository.save(project);
            }
            // 如果项目名称不同，更新项目名称
            if (createDTO.getProjectName() != null && !createDTO.getProjectName().equals(project.getProjectName())) {
                project.setProjectName(createDTO.getProjectName());
                projectRepository.save(project);
            }
        } else {
            // 创建新项目
            ProjectCreateDTO projectCreateDTO = new ProjectCreateDTO(
                    createDTO.getProjectCode(),
                    createDTO.getProjectName(),
                    null, // description
                    null, // projectManagerId
                    ProjectStatus.valueOf(createDTO.getStatus())
            );
            
            ProjectDTO projectDTO = projectService.createProject(projectCreateDTO);
            project = projectRepository.findById(projectDTO.id())
                    .orElseThrow(() -> new EntityNotFoundException("项目创建失败"));
        }
        
        // 2. 查找或创建任务
        TaskEntity task;
        Optional<TaskEntity> existingTask = taskRepository.findByTaskNumber(createDTO.getTaskNumber());
        
        if (existingTask.isPresent()) {
            task = existingTask.get();
            // 检查任务是否属于当前项目
            if (!task.getProject().getId().equals(project.getId())) {
                throw new IllegalArgumentException(
                        String.format("任务编号 %s 已存在，但属于其他项目", createDTO.getTaskNumber()));
            }
            // 更新任务的activity字段
            if (createDTO.getActivity() != null && !createDTO.getActivity().equals(task.getActivity())) {
                task.setActivity(createDTO.getActivity());
                taskRepository.save(task);
            }
        } else {
            // 创建新任务
            TaskCreateDTO taskCreateDTO = new TaskCreateDTO(
                    createDTO.getTaskNumber(),
                    createDTO.getTaskNumber(), // 使用任务编号作为任务名称
                    null, // description
                    project.getId()
            );
            
            TaskDTO taskDTO = taskService.createTask(taskCreateDTO);
            task = taskRepository.findById(taskDTO.id())
                    .orElseThrow(() -> new EntityNotFoundException("任务创建失败"));
            
            // 设置activity字段
            task.setActivity(createDTO.getActivity());
            taskRepository.save(task);
        }
        
        // 3. 返回DTO
        return createProjectTaskDTO(project, task);
    }
    
    /**
     * 更新项目-任务-活动组合
     */
    @Override
    @Transactional
    public ProjectTaskDTO updateProjectTask(String id, ProjectTaskUpdateDTO updateDTO) {
        // 解析id（格式：projectId-taskId）
        String[] parts = id.split("-");
        if (parts.length != 2) {
            throw new IllegalArgumentException("无效的ID格式，应为 projectId-taskId");
        }
        
        Long projectId = Long.parseLong(parts[0]);
        Long taskId = Long.parseLong(parts[1]);
        
        // 查找项目
        ProjectEntity project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("项目不存在: ID = " + projectId));
        
        // 查找任务
        TaskEntity task = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("任务不存在: ID = " + taskId));
        
        // 验证任务属于项目
        if (!task.getProject().getId().equals(project.getId())) {
            throw new IllegalArgumentException("任务不属于指定的项目");
        }
        
        // 更新项目字段
        if (updateDTO.getProjectName() != null && !updateDTO.getProjectName().trim().isEmpty()) {
            project.setProjectName(updateDTO.getProjectName().trim());
        }
        if (updateDTO.getStatus() != null && !updateDTO.getStatus().trim().isEmpty()) {
            project.setStatus(ProjectStatus.valueOf(updateDTO.getStatus()));
        }
        projectRepository.save(project);
        
        // 更新任务字段
        if (updateDTO.getTaskNumber() != null && !updateDTO.getTaskNumber().trim().isEmpty()) {
            // 检查任务编号是否与其他任务冲突
            Optional<TaskEntity> existingTask = taskRepository.findByTaskNumber(updateDTO.getTaskNumber());
            if (existingTask.isPresent() && !existingTask.get().getId().equals(taskId)) {
                throw new IllegalArgumentException("任务编号已存在: " + updateDTO.getTaskNumber());
            }
            task.setTaskNumber(updateDTO.getTaskNumber().trim());
        }
        if (updateDTO.getActivity() != null) {
            task.setActivity(updateDTO.getActivity().trim());
        }
        taskRepository.save(task);
        
        // 返回更新后的DTO
        return createProjectTaskDTO(project, task);
    }
    
    /**
     * 删除项目-任务-活动组合（软删除）
     */
    @Override
    @Transactional
    public void deleteProjectTask(String id) {
        // 解析id（格式：projectId-taskId）
        String[] parts = id.split("-");
        if (parts.length != 2) {
            throw new IllegalArgumentException("无效的ID格式，应为 projectId-taskId");
        }
        
        Long projectId = Long.parseLong(parts[0]);
        Long taskId = Long.parseLong(parts[1]);
        
        // 查找项目
        ProjectEntity project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("项目不存在: ID = " + projectId));
        
        // 查找任务
        TaskEntity task = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("任务不存在: ID = " + taskId));
        
        // 软删除：将项目状态设置为COMPLETED，任务设置为isActive=false
        project.setStatus(ProjectStatus.COMPLETED);
        projectRepository.save(project);
        
        task.setIsActive(false);
        taskRepository.save(task);
    }
    
    /**
     * 创建 ProjectTaskDTO
     */
    private ProjectTaskDTO createProjectTaskDTO(ProjectEntity project, TaskEntity task) {
        ProjectTaskDTO dto = new ProjectTaskDTO();
        
        // 设置 ID（格式：projectId-taskId）
        if (task != null) {
            dto.setId(project.getId() + "-" + task.getId());
            dto.setTaskNumber(task.getTaskNumber());
            dto.setActivity(task.getActivity() != null ? task.getActivity() : "");
        } else {
            dto.setId(String.valueOf(project.getId()));
            dto.setTaskNumber("");
            dto.setActivity("");
        }
        
        dto.setProjectCode(project.getProjectCode());
        dto.setProjectName(project.getProjectName());
        dto.setStatus(project.getStatus().name());
        
        return dto;
    }
}

