package com.spms.backend.controller.timecard;

import com.spms.backend.controller.dto.ApiResponse;
import com.spms.backend.controller.dto.timecard.ProjectTaskDTO;
import com.spms.backend.controller.dto.timecard.ProjectTaskCreateDTO;
import com.spms.backend.controller.dto.timecard.ProjectTaskUpdateDTO;
import com.spms.backend.service.timecard.ProjectTaskActivityService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 项目-任务-活动控制器
 * 提供项目-任务-活动组合的CRUD REST API端点
 */
@RestController
@RequestMapping("/api/v1/timecard/projects/tasks")
public class ProjectTaskActivityController {
    
    private final ProjectTaskActivityService projectTaskActivityService;
    
    @Autowired
    public ProjectTaskActivityController(ProjectTaskActivityService projectTaskActivityService) {
        this.projectTaskActivityService = projectTaskActivityService;
    }
    
    /**
     * 获取项目-任务-活动列表（支持搜索和状态筛选）
     * 
     * @param search 搜索关键字（可选，搜索项目代码、项目名称、任务编号）
     * @param status 状态筛选（可选：ACTIVE/COMPLETED/ALL）
     * @return 项目-任务-活动列表
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<ProjectTaskDTO>>> getProjectTasks(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status) {
        try {
            List<ProjectTaskDTO> projectTasks = projectTaskActivityService.getProjectTasks(search, status);
            return ResponseEntity.ok(new ApiResponse<>("SUCCESS", "获取成功", projectTasks));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("ERROR", "获取项目-任务列表失败: " + e.getMessage(), null));
        }
    }
    
    /**
     * 创建项目-任务-活动组合
     * 
     * @param createDTO 创建数据
     * @return 创建的项目-任务-活动DTO
     */
    @PostMapping
    public ResponseEntity<ApiResponse<ProjectTaskDTO>> createProjectTask(
            @Valid @RequestBody ProjectTaskCreateDTO createDTO) {
        try {
            ProjectTaskDTO created = projectTaskActivityService.createProjectTask(createDTO);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>("SUCCESS", "项目-任务-活动组合创建成功", created));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>("ERROR", e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("ERROR", "创建项目-任务-活动组合失败: " + e.getMessage(), null));
        }
    }
    
    /**
     * 更新项目-任务-活动组合
     * 
     * @param id 唯一标识（格式：projectId-taskId）
     * @param updateDTO 更新数据
     * @return 更新后的项目-任务-活动DTO
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProjectTaskDTO>> updateProjectTask(
            @PathVariable String id,
            @Valid @RequestBody ProjectTaskUpdateDTO updateDTO) {
        try {
            ProjectTaskDTO updated = projectTaskActivityService.updateProjectTask(id, updateDTO);
            return ResponseEntity.ok(new ApiResponse<>("SUCCESS", "项目-任务-活动组合更新成功", updated));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>("ERROR", e.getMessage(), null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>("ERROR", e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("ERROR", "更新项目-任务-活动组合失败: " + e.getMessage(), null));
        }
    }
    
    /**
     * 删除项目-任务-活动组合（软删除）
     * 
     * @param id 唯一标识（格式：projectId-taskId）
     * @return 204 No Content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProjectTask(@PathVariable String id) {
        try {
            projectTaskActivityService.deleteProjectTask(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

