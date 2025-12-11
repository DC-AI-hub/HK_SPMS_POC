package com.spms.backend.service.timecard;

import com.spms.backend.controller.dto.timecard.ProjectTaskDTO;
import com.spms.backend.controller.dto.timecard.ProjectTaskCreateDTO;
import com.spms.backend.controller.dto.timecard.ProjectTaskUpdateDTO;

import java.util.List;

/**
 * 项目-任务-活动服务接口
 * 提供项目-任务-活动组合的CRUD功能
 */
public interface ProjectTaskActivityService {
    
    /**
     * 获取项目-任务-活动列表（支持搜索和状态筛选）
     * 
     * @param search 搜索关键字（可选，搜索项目代码、项目名称、任务编号）
     * @param status 状态筛选（可选：ACTIVE/COMPLETED/ALL）
     * @return 项目-任务-活动列表（扁平化格式）
     */
    List<ProjectTaskDTO> getProjectTasks(String search, String status);
    
    /**
     * 创建项目-任务-活动组合
     * 
     * @param createDTO 创建数据
     * @return 创建的项目-任务-活动DTO
     * @throws IllegalArgumentException 如果项目代码或任务编号已存在
     */
    ProjectTaskDTO createProjectTask(ProjectTaskCreateDTO createDTO);
    
    /**
     * 更新项目-任务-活动组合
     * 
     * @param id 唯一标识（格式：projectId-taskId）
     * @param updateDTO 更新数据
     * @return 更新后的项目-任务-活动DTO
     * @throws jakarta.persistence.EntityNotFoundException 如果项目或任务不存在
     */
    ProjectTaskDTO updateProjectTask(String id, ProjectTaskUpdateDTO updateDTO);
    
    /**
     * 删除项目-任务-活动组合（软删除）
     * 
     * @param id 唯一标识（格式：projectId-taskId）
     * @throws jakarta.persistence.EntityNotFoundException 如果项目或任务不存在
     */
    void deleteProjectTask(String id);
}

