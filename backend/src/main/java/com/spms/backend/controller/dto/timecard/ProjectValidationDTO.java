package com.spms.backend.controller.dto.timecard;

import lombok.Data;
import java.util.List;

/**
 * 项目验证结果 DTO
 * 用于验证用户输入的项目代码是否有效
 */
@Data
public class ProjectValidationDTO {
    
    /**
     * 验证结果（true 表示项目代码有效）
     */
    private boolean valid;
    
    /**
     * 项目代码
     */
    private String projectCode;
    
    /**
     * 项目名称
     */
    private String projectName;
    
    /**
     * 该项目下的任务列表
     */
    private List<TaskInfoDTO> tasks;
    
    /**
     * 错误消息（仅当 valid = false 时）
     */
    private String message;
    
    /**
     * 任务信息内部类
     */
    @Data
    public static class TaskInfoDTO {
        
        /**
         * 任务编号
         */
        private String taskNumber;
        
        /**
         * 任务名称
         */
        private String taskName;
        
        /**
         * 活动类型
         */
        private String activity;
    }
}

