package com.spms.backend.controller.project;

import com.spms.backend.controller.dto.project.TaskDTO;
import com.spms.backend.controller.dto.project.TaskCreateDTO;
import com.spms.backend.controller.dto.project.TaskOptionDTO;
import com.spms.backend.service.project.TaskService;
import com.spms.backend.controller.dto.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 任务管理控制器
 * 提供任务相关的REST API接口
 */
@RestController
@RequestMapping("/api/v1/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    /**
     * 创建新任务
     * @param createDTO 任务创建数据
     * @return 创建的任务信息
     */
    @PostMapping
    // @PreAuthorize("hasAuthority('task:write')") // 临时禁用
    public ResponseEntity<ApiResponse<TaskDTO>> createTask(@Valid @RequestBody TaskCreateDTO createDTO) {
        try {
            TaskDTO result = taskService.createTask(createDTO);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>("SUCCESS", "任务创建成功", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>("ERROR", e.getMessage(), null));
        }
    }

    /**
     * 根据ID获取任务
     * @param id 任务ID
     * @return 任务信息
     */
    @GetMapping("/{id}")
    // @PreAuthorize("hasAuthority('task:read')") // 临时禁用
    public ResponseEntity<ApiResponse<TaskDTO>> getTask(@PathVariable Long id) {
        try {
            TaskDTO result = taskService.getTaskById(id);
            return ResponseEntity.ok(new ApiResponse<>("SUCCESS", "获取成功", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>("ERROR", e.getMessage(), null));
        }
    }

    /**
     * 根据任务编号获取任务
     * @param number 任务编号
     * @return 任务信息
     */
    @GetMapping("/number/{number}")
    // @PreAuthorize("hasAuthority('task:read')") // 临时禁用
    public ResponseEntity<ApiResponse<TaskDTO>> getTaskByNumber(@PathVariable String number) {
        try {
            TaskDTO result = taskService.getTaskByNumber(number);
            return ResponseEntity.ok(new ApiResponse<>("SUCCESS", "获取成功", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>("ERROR", e.getMessage(), null));
        }
    }

    /**
     * 更新任务
     * @param id 任务ID
     * @param taskDTO 更新的任务数据
     * @return 更新后的任务信息
     */
    @PutMapping("/{id}")
    // @PreAuthorize("hasAuthority('task:write')") // 临时禁用
    public ResponseEntity<ApiResponse<TaskDTO>> updateTask(@PathVariable Long id, 
                                                         @Valid @RequestBody TaskDTO taskDTO) {
        try {
            TaskDTO result = taskService.updateTask(id, taskDTO);
            return ResponseEntity.ok(new ApiResponse<>("SUCCESS", "更新成功", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>("ERROR", e.getMessage(), null));
        }
    }

    /**
     * 删除任务
     * @param id 任务ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    // @PreAuthorize("hasAuthority('task:write')") // 临时禁用
    public ResponseEntity<ApiResponse<Void>> deleteTask(@PathVariable Long id) {
        try {
            taskService.deleteTask(id);
            return ResponseEntity.ok(new ApiResponse<>("SUCCESS", "删除成功", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>("ERROR", e.getMessage(), null));
        }
    }

    /**
     * 分页查询任务
     * @param pageable 分页参数
     * @return 任务分页数据
     */
    @GetMapping
    // @PreAuthorize("hasAuthority('task:read')") // 临时禁用
    public ResponseEntity<ApiResponse<Page<TaskDTO>>> getTasks(Pageable pageable) {
        try {
            Page<TaskDTO> result = taskService.getTasks(pageable);
            return ResponseEntity.ok(new ApiResponse<>("SUCCESS", "查询成功", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>("ERROR", e.getMessage(), null));
        }
    }

    /**
     * 根据项目ID查询任务
     * @param projectId 项目ID
     * @return 任务列表
     */
    @GetMapping("/project/{projectId}")
    // @PreAuthorize("hasAuthority('task:read')") // 临时禁用
    public ResponseEntity<ApiResponse<List<TaskDTO>>> getTasksByProject(@PathVariable Long projectId) {
        try {
            List<TaskDTO> result = taskService.getTasksByProjectId(projectId);
            return ResponseEntity.ok(new ApiResponse<>("SUCCESS", "查询成功", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>("ERROR", e.getMessage(), null));
        }
    }

    /**
     * 根据项目代码查询任务
     * @param projectCode 项目代码
     * @return 任务列表
     */
    @GetMapping("/project/code/{projectCode}")
    // @PreAuthorize("hasAuthority('task:read')") // 临时禁用
    public ResponseEntity<ApiResponse<List<TaskDTO>>> getTasksByProjectCode(@PathVariable String projectCode) {
        try {
            List<TaskDTO> result = taskService.getTasksByProjectCode(projectCode);
            return ResponseEntity.ok(new ApiResponse<>("SUCCESS", "查询成功", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>("ERROR", e.getMessage(), null));
        }
    }

    /**
     * 根据任务名称模糊查询
     * @param name 任务名称关键字
     * @return 任务列表
     */
    @GetMapping("/search")
    // @PreAuthorize("hasAuthority('task:read')") // 临时禁用
    public ResponseEntity<ApiResponse<List<TaskDTO>>> searchTasks(@RequestParam String name) {
        try {
            List<TaskDTO> result = taskService.searchTasksByName(name);
            return ResponseEntity.ok(new ApiResponse<>("SUCCESS", "搜索成功", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>("ERROR", e.getMessage(), null));
        }
    }

    /**
     * 获取项目的任务选项（用于下拉选择）
     * @param projectId 项目ID
     * @return 任务选项列表
     */
    @GetMapping("/project/{projectId}/options")
    // @PreAuthorize("hasAuthority('task:read')") // 临时禁用
    public ResponseEntity<ApiResponse<List<TaskOptionDTO>>> getTaskOptionsByProject(@PathVariable Long projectId) {
        try {
            List<TaskOptionDTO> result = taskService.getTaskOptionsByProjectId(projectId);
            return ResponseEntity.ok(new ApiResponse<>("SUCCESS", "获取成功", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>("ERROR", e.getMessage(), null));
        }
    }

    /**
     * 检查任务编号是否存在
     * @param number 任务编号
     * @return 检查结果
     */
    @GetMapping("/exists/{number}")
    // @PreAuthorize("hasAuthority('task:read')") // 临时禁用
    public ResponseEntity<ApiResponse<Boolean>> checkTaskNumberExists(@PathVariable String number) {
        try {
            Boolean result = taskService.existsByTaskNumber(number);
            return ResponseEntity.ok(new ApiResponse<>("SUCCESS", "检查完成", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>("ERROR", e.getMessage(), null));
        }
    }

    /**
     * 获取项目的任务数量
     * @param projectId 项目ID
     * @return 任务数量
     */
    @GetMapping("/project/{projectId}/count")
    // @PreAuthorize("hasAuthority('task:read')") // 临时禁用
    public ResponseEntity<ApiResponse<Long>> getTaskCountByProject(@PathVariable Long projectId) {
        try {
            Long result = taskService.getTaskCountByProjectId(projectId);
            return ResponseEntity.ok(new ApiResponse<>("SUCCESS", "获取成功", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>("ERROR", e.getMessage(), null));
        }
    }
}
