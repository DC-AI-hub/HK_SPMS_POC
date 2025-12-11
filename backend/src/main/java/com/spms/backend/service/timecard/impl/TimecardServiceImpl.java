package com.spms.backend.service.timecard.impl;

import com.spms.backend.controller.dto.timecard.*;
import com.spms.backend.repository.entities.project.ProjectEntity;
import com.spms.backend.repository.entities.project.TaskEntity;
import com.spms.backend.repository.project.ProjectRepository;
import com.spms.backend.repository.project.TaskRepository;
import com.spms.backend.service.timecard.TimecardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Timecard 服务实现类
 * 提供员工信息、项目查询、项目验证等核心功能
 */
@Service
@Transactional(readOnly = true)
public class TimecardServiceImpl implements TimecardService {
    
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    
    @Autowired
    public TimecardServiceImpl(
            ProjectRepository projectRepository,
            TaskRepository taskRepository) {
        this.projectRepository = projectRepository;
        this.taskRepository = taskRepository;
    }
    
    /**
     * 获取活跃项目列表（用于 Timecard 填报）
     * 
     * @param search 搜索关键词（可选）
     * @return 项目任务列表
     */
    @Override
    public List<ProjectTaskDTO> getActiveProjects(String search) {
        List<ProjectEntity> projects;
        
        // 根据搜索条件查询活跃项目
        if (search != null && !search.trim().isEmpty()) {
            projects = projectRepository.searchActiveProjects(search.trim());
        } else {
            projects = projectRepository.findAllActiveProjects();
        }
        
        // 构建扁平化的项目-任务列表
        List<ProjectTaskDTO> result = new ArrayList<>();
        
        for (ProjectEntity project : projects) {
            // 查询该项目的所有活跃任务
            List<TaskEntity> tasks = taskRepository.findActiveTasksByProjectId(project.getId());
            
            if (tasks.isEmpty()) {
                // 如果项目没有任务，仍然返回项目信息（任务信息为空）
                ProjectTaskDTO dto = createProjectTaskDTO(project, null);
                result.add(dto);
            } else {
                // 为每个任务创建一条记录
                for (TaskEntity task : tasks) {
                    ProjectTaskDTO dto = createProjectTaskDTO(project, task);
                    result.add(dto);
                }
            }
        }
        
        return result;
    }
    
    /**
     * 验证项目代码并获取项目信息
     * 
     * @param projectCode 项目代码
     * @return 项目验证结果 DTO
     */
    @Override
    public ProjectValidationDTO validateProjectCode(String projectCode) {
        ProjectValidationDTO validation = new ProjectValidationDTO();
        validation.setProjectCode(projectCode);
        
        // 查询活跃项目
        Optional<ProjectEntity> projectOpt = projectRepository.findActiveProjectByCode(projectCode);
        
        if (projectOpt.isEmpty()) {
            validation.setValid(false);
            validation.setMessage("项目代码不存在或已关闭");
            return validation;
        }
        
        ProjectEntity project = projectOpt.get();
        validation.setValid(true);
        validation.setProjectName(project.getProjectName());
        
        // 查询该项目的所有活跃任务
        List<TaskEntity> tasks = taskRepository.findActiveTasksByProjectId(project.getId());
        
        List<ProjectValidationDTO.TaskInfoDTO> taskInfos = tasks.stream()
                .map(task -> {
                    ProjectValidationDTO.TaskInfoDTO taskInfo = new ProjectValidationDTO.TaskInfoDTO();
                    taskInfo.setTaskNumber(task.getTaskNumber());
                    taskInfo.setTaskName(task.getTaskName());
                    taskInfo.setActivity(task.getActivity());
                    return taskInfo;
                })
                .collect(Collectors.toList());
        
        validation.setTasks(taskInfos);
        
        return validation;
    }
    
    // ==================== 私有辅助方法 ====================
    
    /**
     * 创建 ProjectTaskDTO
     * 
     * @param project 项目实体
     * @param task 任务实体（可以为 null）
     * @return ProjectTaskDTO
     */
    private ProjectTaskDTO createProjectTaskDTO(ProjectEntity project, TaskEntity task) {
        ProjectTaskDTO dto = new ProjectTaskDTO();
        
        // 设置 ID（格式：projectId-taskId）
        if (task != null) {
            dto.setId(project.getId() + "-" + task.getId());
            dto.setTaskNumber(task.getTaskNumber());
            dto.setActivity(task.getActivity());
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

