package com.spms.backend.service.project.impl;

import com.spms.backend.controller.dto.project.TaskDTO;
import com.spms.backend.controller.dto.project.TaskCreateDTO;
import com.spms.backend.controller.dto.project.TaskOptionDTO;
import com.spms.backend.repository.entities.project.TaskEntity;
import com.spms.backend.repository.entities.project.ProjectEntity;
import com.spms.backend.repository.project.TaskRepository;
import com.spms.backend.repository.project.ProjectRepository;
import com.spms.backend.service.project.TaskService;
import com.spms.backend.service.exception.NotFoundException;
import com.spms.backend.service.exception.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 任务服务实现类
 * 实现任务相关的业务逻辑
 */
@Service
@Transactional
public class TaskServiceImpl implements TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Override
    public TaskDTO createTask(TaskCreateDTO createDTO) {
        // 检查任务编号是否已存在
        if (taskRepository.existsByTaskNumber(createDTO.taskNumber())) {
            throw new ValidationException("任务编号已存在: " + createDTO.taskNumber());
        }

        // 检查项目是否存在
        ProjectEntity project = projectRepository.findById(createDTO.projectId())
            .orElseThrow(() -> new NotFoundException("项目不存在: " + createDTO.projectId()));

        // 创建任务实体
        TaskEntity entity = new TaskEntity();
        entity.setTaskNumber(createDTO.taskNumber());
        entity.setTaskName(createDTO.taskName());
        entity.setDescription(createDTO.description());
        entity.setProject(project);

        // 设置审计字段
        long currentTime = System.currentTimeMillis();
        entity.setCreatedAt(currentTime);
        entity.setUpdatedAt(currentTime);
        // TODO: 设置当前用户ID
        entity.setCreatedById(1L); // 临时硬编码，需要从安全上下文获取
        entity.setUpdatedById(1L);

        TaskEntity savedEntity = taskRepository.save(entity);
        return convertToDTO(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public TaskDTO getTaskById(Long id) {
        TaskEntity entity = taskRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("任务不存在: " + id));
        return convertToDTO(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public TaskDTO getTaskByNumber(String taskNumber) {
        TaskEntity entity = taskRepository.findByTaskNumber(taskNumber)
            .orElseThrow(() -> new NotFoundException("任务不存在: " + taskNumber));
        return convertToDTO(entity);
    }

    @Override
    public TaskDTO updateTask(Long id, TaskDTO taskDTO) {
        TaskEntity entity = taskRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("任务不存在: " + id));

        // 检查任务编号是否被其他任务使用
        if (!entity.getTaskNumber().equals(taskDTO.taskNumber()) &&
            taskRepository.existsByTaskNumber(taskDTO.taskNumber())) {
            throw new ValidationException("任务编号已存在: " + taskDTO.taskNumber());
        }

        // 检查项目是否存在
        if (!entity.getProject().getId().equals(taskDTO.projectId())) {
            ProjectEntity project = projectRepository.findById(taskDTO.projectId())
                .orElseThrow(() -> new NotFoundException("项目不存在: " + taskDTO.projectId()));
            entity.setProject(project);
        }

        // 更新字段
        entity.setTaskNumber(taskDTO.taskNumber());
        entity.setTaskName(taskDTO.taskName());
        entity.setDescription(taskDTO.description());

        // 更新审计字段
        entity.setUpdatedAt(System.currentTimeMillis());
        entity.setUpdatedById(1L); // 临时硬编码，需要从安全上下文获取

        TaskEntity savedEntity = taskRepository.save(entity);
        return convertToDTO(savedEntity);
    }

    @Override
    public void deleteTask(Long id) {
        TaskEntity entity = taskRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("任务不存在: " + id));
        
        // TODO: 检查是否有关联的工时记录
        
        taskRepository.delete(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TaskDTO> getTasks(Pageable pageable) {
        Page<TaskEntity> entities = taskRepository.findAll(pageable);
        return entities.map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskDTO> getTasksByProjectId(Long projectId) {
        List<TaskEntity> entities = taskRepository.findByProjectId(projectId);
        return entities.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskDTO> getTasksByProjectCode(String projectCode) {
        List<TaskEntity> entities = taskRepository.findByProjectCode(projectCode);
        return entities.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskDTO> searchTasksByName(String taskName) {
        List<TaskEntity> entities = taskRepository.findByTaskNameContaining(taskName);
        return entities.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskOptionDTO> getTaskOptionsByProjectId(Long projectId) {
        List<TaskEntity> tasks = taskRepository.findTaskOptionsByProjectId(projectId);
        return tasks.stream()
            .map(t -> new TaskOptionDTO(
                t.getId(),
                t.getTaskNumber(),
                t.getTaskName(),
                extractActivities(t)
            ))
            .collect(Collectors.toList());
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
            if (v instanceof List<?>) {
                return ((List<?>) v).stream()
                    .filter(x -> x != null)
                    .map(String::valueOf)
                    .filter(s -> !s.isBlank())
                    .toList();
            }
            // 单字符串或逗号分隔
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
    public boolean existsByTaskNumber(String taskNumber) {
        return taskRepository.existsByTaskNumber(taskNumber);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getTaskCountByProjectId(Long projectId) {
        return taskRepository.countByProjectId(projectId);
    }

    /**
     * 将实体转换为DTO
     */
    private TaskDTO convertToDTO(TaskEntity entity) {
        return new TaskDTO(
            entity.getId(),
            entity.getTaskNumber(),
            entity.getTaskName(),
            entity.getDescription(),
            entity.getProject().getId(),
            entity.getProject().getProjectCode(),
            entity.getProject().getProjectName(),
            entity.getCreatedAt(),
            entity.getUpdatedAt(),
            entity.getCreatedById(),
            entity.getUpdatedById()
        );
    }
}
