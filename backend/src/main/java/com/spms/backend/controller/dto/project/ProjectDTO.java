package com.spms.backend.controller.dto.project;

import com.spms.backend.model.ProjectStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 项目数据传输对象
 * 用于API请求和响应中的项目数据传输
 */
public record ProjectDTO(
    Long id,
    
    @NotBlank(message = "项目代码不能为空")
    @Size(max = 50, message = "项目代码长度不能超过50个字符")
    String projectCode,
    
    @NotBlank(message = "项目名称不能为空")
    @Size(max = 200, message = "项目名称长度不能超过200个字符")
    String projectName,
    
    @Size(max = 1000, message = "项目描述长度不能超过1000个字符")
    String description,
    
    Long projectManagerId,
    String projectManagerName,
    
    @NotNull(message = "项目状态不能为空")
    ProjectStatus status,
    
    Long createdAt,
    Long updatedAt,
    Long createdById,
    Long updatedById
) {
    /**
     * 紧凑构造器，用于数据验证
     */
    public ProjectDTO {
        if (projectCode != null && projectCode.isBlank()) {
            throw new IllegalArgumentException("项目代码不能为空");
        }
        if (projectName != null && projectName.isBlank()) {
            throw new IllegalArgumentException("项目名称不能为空");
        }
        if (status == null) {
            status = ProjectStatus.ACTIVE;
        }
    }
    
    /**
     * 创建用于新建项目的DTO
     */
    public static ProjectDTO forCreate(String projectCode, String projectName, String description, 
                                     Long projectManagerId, ProjectStatus status) {
        return new ProjectDTO(null, projectCode, projectName, description, projectManagerId, 
                            null, status != null ? status : ProjectStatus.ACTIVE, 
                            null, null, null, null);
    }
}
