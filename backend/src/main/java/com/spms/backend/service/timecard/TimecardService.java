package com.spms.backend.service.timecard;

import com.spms.backend.controller.dto.timecard.*;
import java.util.List;

/**
 * Timecard 服务接口
 * 提供 Timecard 模块的核心业务功能
 */
public interface TimecardService {
    
    /**
     * 获取活跃项目列表（用于 Timecard 填报）
     * 返回所有状态为 ACTIVE 且未删除的项目及其任务
     * 
     * @param search 搜索关键词（可选，匹配项目代码或名称）
     * @return 项目任务列表
     */
    List<ProjectTaskDTO> getActiveProjects(String search);
    
    /**
     * 验证项目代码并获取项目信息
     * 用于验证用户输入的项目代码是否有效
     * 
     * @param projectCode 项目代码
     * @return 项目验证结果 DTO
     */
    ProjectValidationDTO validateProjectCode(String projectCode);
}

