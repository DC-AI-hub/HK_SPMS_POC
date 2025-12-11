package com.spms.backend.controller.dto.project;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 任务数据传输对象
 * 用于API请求和响应中的任务数据传输
 */
public record TaskDTO(
    Long id,
    
    @NotBlank(message = "任务编号不能为空")
    @Size(max = 50, message = "任务编号长度不能超过50个字符")
    String taskNumber,
    
    @NotBlank(message = "任务名称不能为空")
    @Size(max = 200, message = "任务名称长度不能超过200个字符")
    String taskName,
    
    @Size(max = 1000, message = "任务描述长度不能超过1000个字符")
    String description,
    
    @NotNull(message = "项目ID不能为空")
    Long projectId,
    
    String projectCode,
    String projectName,
    
    Long createdAt,
    Long updatedAt,
    Long createdById,
    Long updatedById
) {
    /**
     * 紧凑构造器，用于数据验证
     */
    public TaskDTO {
        if (taskNumber != null && taskNumber.isBlank()) {
            throw new IllegalArgumentException("任务编号不能为空");
        }
        if (taskName != null && taskName.isBlank()) {
            throw new IllegalArgumentException("任务名称不能为空");
        }
        if (projectId == null) {
            throw new IllegalArgumentException("项目ID不能为空");
        }
    }
    
    /**
     * 创建用于新建任务的DTO
     */
    public static TaskDTO forCreate(String taskNumber, String taskName, String description, Long projectId) {
        return new TaskDTO(null, taskNumber, taskName, description, projectId, 
                         null, null, null, null, null, null);
    }
}
