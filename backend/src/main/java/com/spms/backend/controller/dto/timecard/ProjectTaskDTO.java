package com.spms.backend.controller.dto.timecard;

import lombok.Data;

/**
 * 项目任务 DTO
 * 用于 Timecard 表单中的项目任务选择
 * 每条记录代表一个项目-任务组合
 */
@Data
public class ProjectTaskDTO {
    
    /**
     * 唯一标识（格式：projectId-taskId）
     */
    private String id;
    
    /**
     * 项目代码
     */
    private String projectCode;
    
    /**
     * 项目名称
     */
    private String projectName;
    
    /**
     * 任务编号
     */
    private String taskNumber;
    
    /**
     * 活动类型（如 Development, Testing）
     */
    private String activity;
    
    /**
     * 项目状态（如 ACTIVE, COMPLETED）
     */
    private String status;
}

