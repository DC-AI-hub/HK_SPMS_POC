package com.spms.backend.controller.dto.project;

import com.spms.backend.model.ProjectStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 项目创建数据传输对象
 * 专用于创建新项目的请求
 */
public record ProjectCreateDTO(
    @NotBlank(message = "项目代码不能为空")
    @Size(max = 50, message = "项目代码长度不能超过50个字符")
    String projectCode,
    
    @NotBlank(message = "项目名称不能为空")
    @Size(max = 200, message = "项目名称长度不能超过200个字符")
    String projectName,
    
    @Size(max = 1000, message = "项目描述长度不能超过1000个字符")
    String description,
    
    Long projectManagerId,
    
    ProjectStatus status
) {
    /**
     * 紧凑构造器，用于数据验证
     */
    public ProjectCreateDTO {
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
}
