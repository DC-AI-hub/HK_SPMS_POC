package com.spms.backend.controller.timecard;

import com.spms.backend.controller.dto.timecard.*;
import com.spms.backend.service.timecard.TimecardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Timecard 控制器
 * 提供 Timecard 相关的 REST API 端点
 */
@RestController
@RequestMapping("/api/v1/timecard")
public class TimecardController {
    
    private final TimecardService timecardService;
    
    @Autowired
    public TimecardController(TimecardService timecardService) {
        this.timecardService = timecardService;
    }
    
    /**
     * 获取活跃项目列表
     * 用于 Timecard 表单中的项目选择
     * 
     * @param search 搜索关键词（可选）
     * @return 项目任务列表
     */
    @GetMapping("/projects/active")
    public ResponseEntity<List<ProjectTaskDTO>> getActiveProjects(
            @RequestParam(required = false) String search) {
        List<ProjectTaskDTO> projects = timecardService.getActiveProjects(search);
        return ResponseEntity.ok(projects);
    }
    
    /**
     * 验证项目代码
     * 用于验证用户输入的项目代码是否有效
     * 
     * @param projectCode 项目代码
     * @return 项目验证结果 DTO
     */
    @GetMapping("/projects/validate/{projectCode}")
    public ResponseEntity<ProjectValidationDTO> validateProjectCode(
            @PathVariable String projectCode) {
        ProjectValidationDTO validation = timecardService.validateProjectCode(projectCode);
        return ResponseEntity.ok(validation);
    }
}

