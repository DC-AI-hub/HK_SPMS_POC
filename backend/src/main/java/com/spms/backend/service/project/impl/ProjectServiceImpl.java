package com.spms.backend.service.project.impl;

import com.spms.backend.controller.dto.project.ProjectDTO;
import com.spms.backend.controller.dto.project.ProjectCreateDTO;
import com.spms.backend.controller.dto.project.ProjectOptionDTO;
import com.spms.backend.model.ProjectStatus;
import com.spms.backend.repository.entities.project.ProjectEntity;
import com.spms.backend.repository.entities.project.TaskEntity;
import com.spms.backend.repository.entities.idm.User;
import com.spms.backend.repository.project.ProjectRepository;
import com.spms.backend.repository.project.TaskRepository;
import com.spms.backend.repository.idm.UserRepository;
import com.spms.backend.service.project.ProjectService;
import com.spms.backend.service.exception.NotFoundException;
import com.spms.backend.service.exception.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 项目服务实现类
 * 实现项目相关的业务逻辑
 */
@Service
@Transactional
public class ProjectServiceImpl implements ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Override
    public ProjectDTO createProject(ProjectCreateDTO createDTO) {
        // 检查项目代码是否已存在
        if (projectRepository.existsByProjectCode(createDTO.projectCode())) {
            throw new ValidationException("项目代码已存在: " + createDTO.projectCode());
        }

        // 创建项目实体
        ProjectEntity entity = new ProjectEntity();
        entity.setProjectCode(createDTO.projectCode());
        entity.setProjectName(createDTO.projectName());
        entity.setDescription(createDTO.description());
        entity.setStatus(createDTO.status() != null ? createDTO.status() : ProjectStatus.ACTIVE);

        // 设置项目经理
        if (createDTO.projectManagerId() != null) {
            User manager = userRepository.findById(createDTO.projectManagerId())
                .orElseThrow(() -> new NotFoundException("项目经理不存在: " + createDTO.projectManagerId()));
            entity.setProjectManager(manager);
        }

        // 设置审计字段
        long currentTime = System.currentTimeMillis();
        entity.setCreatedAt(currentTime);
        entity.setUpdatedAt(currentTime);
        // TODO: 设置当前用户ID
        entity.setCreatedById(1L); // 临时硬编码，需要从安全上下文获取
        entity.setUpdatedById(1L);

        ProjectEntity savedEntity = projectRepository.save(entity);
        return convertToDTO(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public ProjectDTO getProjectById(Long id) {
        ProjectEntity entity = projectRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("项目不存在: " + id));
        return convertToDTO(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public ProjectDTO getProjectByCode(String projectCode) {
        ProjectEntity entity = projectRepository.findByProjectCode(projectCode)
            .orElseThrow(() -> new NotFoundException("项目不存在: " + projectCode));
        return convertToDTO(entity);
    }

    @Override
    public ProjectDTO updateProject(Long id, ProjectDTO projectDTO) {
        ProjectEntity entity = projectRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("项目不存在: " + id));

        // 检查项目代码是否被其他项目使用
        if (!entity.getProjectCode().equals(projectDTO.projectCode()) &&
            projectRepository.existsByProjectCode(projectDTO.projectCode())) {
            throw new ValidationException("项目代码已存在: " + projectDTO.projectCode());
        }

        // 更新字段
        entity.setProjectCode(projectDTO.projectCode());
        entity.setProjectName(projectDTO.projectName());
        entity.setDescription(projectDTO.description());
        entity.setStatus(projectDTO.status());

        // 更新项目经理
        if (projectDTO.projectManagerId() != null) {
            User manager = userRepository.findById(projectDTO.projectManagerId())
                .orElseThrow(() -> new NotFoundException("项目经理不存在: " + projectDTO.projectManagerId()));
            entity.setProjectManager(manager);
        } else {
            entity.setProjectManager(null);
        }

        // 更新审计字段
        entity.setUpdatedAt(System.currentTimeMillis());
        entity.setUpdatedById(1L); // 临时硬编码，需要从安全上下文获取

        ProjectEntity savedEntity = projectRepository.save(entity);
        return convertToDTO(savedEntity);
    }

    @Override
    public void deleteProject(Long id) {
        ProjectEntity entity = projectRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("项目不存在: " + id));
        
        // TODO: 检查是否有关联的任务或工时记录
        
        projectRepository.delete(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProjectDTO> getProjects(Pageable pageable) {
        Page<ProjectEntity> entities = projectRepository.findAll(pageable);
        return entities.map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectDTO> getProjectsByStatus(ProjectStatus status) {
        List<ProjectEntity> entities = projectRepository.findByStatus(status);
        return entities.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectDTO> getProjectsByManagerId(Long managerId) {
        List<ProjectEntity> entities = projectRepository.findByProjectManagerId(managerId);
        return entities.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectDTO> searchProjectsByName(String projectName) {
        List<ProjectEntity> entities = projectRepository.findByProjectNameContaining(projectName);
        return entities.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectOptionDTO> getActiveProjectOptions() {
        List<ProjectEntity> projects = projectRepository.findActiveProjects();
        if (projects.isEmpty()) return List.of();

        // 批量按项目查询任务，避免逐项目N+1
        List<ProjectOptionDTO> result = new ArrayList<>();
        for (ProjectEntity p : projects) {
            List<TaskEntity> tasks = taskRepository.findByProjectId(p.getId());
            List<com.spms.backend.controller.dto.project.TaskOptionDTO> taskOptions = tasks.stream()
                .map(t -> new com.spms.backend.controller.dto.project.TaskOptionDTO(
                    t.getId(),
                    t.getTaskNumber(),
                    t.getTaskName(),
                    extractActivities(t)
                ))
                .collect(Collectors.toList());
            result.add(new ProjectOptionDTO(
                p.getId(),
                p.getProjectCode(),
                p.getProjectName(),
                taskOptions
            ));
        }
        return result;
    }

    private List<String> extractActivities(TaskEntity t) {
        try {
            // 1) 优先读取列 activity（支持逗号/分号/竖线分隔）
            String act = t.getActivity();
            if (act != null && !act.isBlank()) {
                return java.util.Arrays.stream(act.split("[;,|]"))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .toList();
            }
            // 2) 兼容 custom_fields.activities
            Object v = t.getCustomFields() != null ? t.getCustomFields().get("activities") : null;
            if (v == null) return List.of();
            if (v instanceof java.util.List<?>) {
                return ((java.util.List<?>) v).stream()
                    .filter(x -> x != null)
                    .map(String::valueOf)
                    .filter(s -> !s.isBlank())
                    .toList();
            }
            String s = String.valueOf(v);
            if (s.isBlank()) return List.of();
            return java.util.Arrays.stream(s.split(","))
                .map(String::trim)
                .filter(x -> !x.isEmpty())
                .toList();
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByProjectCode(String projectCode) {
        return projectRepository.existsByProjectCode(projectCode);
    }

    /**
     * 将实体转换为DTO
     */
    private ProjectDTO convertToDTO(ProjectEntity entity) {
        String managerName = null;
        if (entity.getProjectManager() != null) {
            // 从用户的userProfiles中获取姓名，如果没有则使用用户名
            User manager = entity.getProjectManager();
            if (manager.getUserProfiles() != null) {
                String firstName = manager.getUserProfiles().get("firstName");
                String lastName = manager.getUserProfiles().get("lastName");
                if (firstName != null && lastName != null) {
                    managerName = firstName + " " + lastName;
                } else {
                    managerName = manager.getUsername();
                }
            } else {
                managerName = manager.getUsername();
            }
        }

        return new ProjectDTO(
            entity.getId(),
            entity.getProjectCode(),
            entity.getProjectName(),
            entity.getDescription(),
            entity.getProjectManager() != null ? entity.getProjectManager().getId() : null,
            managerName,
            entity.getStatus(),
            entity.getCreatedAt(),
            entity.getUpdatedAt(),
            entity.getCreatedById(),
            entity.getUpdatedById()
        );
    }
}
