package com.spms.backend.controller.dto.timecard;

import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 项目-任务-活动更新 DTO
 * 用于更新项目-任务-活动组合（projectCode 不可修改）
 */
@Data
public class ProjectTaskUpdateDTO {
    
    /**
     * 项目名称（可选）
     */
    @Size(max = 200, message = "项目名称长度不能超过200个字符")
    private String projectName;
    
    /**
     * 任务编号（可选）
     */
    @Size(max = 50, message = "任务编号长度不能超过50个字符")
    private String taskNumber;
    
    /**
     * 活动类型（可选）
     */
    @Size(max = 255, message = "活动类型长度不能超过255个字符")
    private String activity;
    
    /**
     * 项目状态（可选）
     */
    @Size(max = 20, message = "项目状态长度不能超过20个字符")
    private String status;
}

