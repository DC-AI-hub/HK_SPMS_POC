package com.spms.backend.controller.dto.timecard;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 项目-任务-活动创建 DTO
 * 用于创建项目-任务-活动组合
 */
@Data
public class ProjectTaskCreateDTO {
    
    /**
     * 项目代码（必填）
     */
    @NotBlank(message = "项目代码不能为空")
    @Size(max = 50, message = "项目代码长度不能超过50个字符")
    private String projectCode;
    
    /**
     * 项目名称（必填）
     */
    @NotBlank(message = "项目名称不能为空")
    @Size(max = 200, message = "项目名称长度不能超过200个字符")
    private String projectName;
    
    /**
     * 任务编号（必填）
     */
    @NotBlank(message = "任务编号不能为空")
    @Size(max = 50, message = "任务编号长度不能超过50个字符")
    private String taskNumber;
    
    /**
     * 活动类型（必填）
     */
    @NotBlank(message = "活动类型不能为空")
    @Size(max = 255, message = "活动类型长度不能超过255个字符")
    private String activity;
    
    /**
     * 项目状态（可选，默认 ACTIVE）
     */
    @Size(max = 20, message = "项目状态长度不能超过20个字符")
    private String status = "ACTIVE";
}

