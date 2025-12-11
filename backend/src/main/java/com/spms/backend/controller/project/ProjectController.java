package com.spms.backend.controller.project;

import com.spms.backend.controller.dto.project.ProjectDTO;
import com.spms.backend.controller.dto.project.ProjectCreateDTO;
import com.spms.backend.controller.dto.project.ProjectOptionDTO;
import com.spms.backend.model.ProjectStatus;
import com.spms.backend.service.project.ProjectService;
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
 * 项目管理控制器
 * 提供项目相关的REST API接口
 */
@RestController
@RequestMapping("/api/v1/projects")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    /**
     * 创建新项目
     * @param createDTO 项目创建数据
     * @return 创建的项目信息
     */
    @PostMapping
    // // @PreAuthorize("hasAuthority('project:write')") // 临时禁用 // 临时禁用权限检查
    public ResponseEntity<ApiResponse<ProjectDTO>> createProject(@Valid @RequestBody ProjectCreateDTO createDTO) {
        try {
            ProjectDTO result = projectService.createProject(createDTO);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>("SUCCESS", "项目创建成功", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>("ERROR", e.getMessage(), null));
        }
    }

    /**
     * 根据ID获取项目
     * @param id 项目ID
     * @return 项目信息
     */
    @GetMapping("/{id}")
    // @PreAuthorize("hasAuthority('project:read')") // 临时禁用
    public ResponseEntity<ApiResponse<ProjectDTO>> getProject(@PathVariable Long id) {
        try {
            ProjectDTO result = projectService.getProjectById(id);
            return ResponseEntity.ok(new ApiResponse<>("SUCCESS", "获取成功", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>("ERROR", e.getMessage(), null));
        }
    }

    /**
     * 根据项目代码获取项目
     * @param code 项目代码
     * @return 项目信息
     */
    @GetMapping("/code/{code}")
    // @PreAuthorize("hasAuthority('project:read')") // 临时禁用
    public ResponseEntity<ApiResponse<ProjectDTO>> getProjectByCode(@PathVariable String code) {
        try {
            ProjectDTO result = projectService.getProjectByCode(code);
            return ResponseEntity.ok(new ApiResponse<>("SUCCESS", "获取成功", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>("ERROR", e.getMessage(), null));
        }
    }

    /**
     * 更新项目
     * @param id 项目ID
     * @param projectDTO 更新的项目数据
     * @return 更新后的项目信息
     */
    @PutMapping("/{id}")
    // @PreAuthorize("hasAuthority('project:write')") // 临时禁用
    public ResponseEntity<ApiResponse<ProjectDTO>> updateProject(@PathVariable Long id, 
                                                               @Valid @RequestBody ProjectDTO projectDTO) {
        try {
            ProjectDTO result = projectService.updateProject(id, projectDTO);
            return ResponseEntity.ok(new ApiResponse<>("SUCCESS", "更新成功", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>("ERROR", e.getMessage(), null));
        }
    }

    /**
     * 删除项目
     * @param id 项目ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    // @PreAuthorize("hasAuthority('project:write')") // 临时禁用
    public ResponseEntity<ApiResponse<Void>> deleteProject(@PathVariable Long id) {
        try {
            projectService.deleteProject(id);
            return ResponseEntity.ok(new ApiResponse<>("SUCCESS", "删除成功", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>("ERROR", e.getMessage(), null));
        }
    }

    /**
     * 分页查询项目
     * @param pageable 分页参数
     * @return 项目分页数据
     */
    @GetMapping
    // @PreAuthorize("hasAuthority('project:read')") // 临时禁用
    public ResponseEntity<ApiResponse<Page<ProjectDTO>>> getProjects(Pageable pageable) {
        try {
            Page<ProjectDTO> result = projectService.getProjects(pageable);
            return ResponseEntity.ok(new ApiResponse<>("SUCCESS", "查询成功", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>("ERROR", e.getMessage(), null));
        }
    }

    /**
     * 根据状态查询项目
     * @param status 项目状态
     * @return 项目列表
     */
    @GetMapping("/status/{status}")
    // @PreAuthorize("hasAuthority('project:read')") // 临时禁用
    public ResponseEntity<ApiResponse<List<ProjectDTO>>> getProjectsByStatus(@PathVariable ProjectStatus status) {
        try {
            List<ProjectDTO> result = projectService.getProjectsByStatus(status);
            return ResponseEntity.ok(new ApiResponse<>("SUCCESS", "查询成功", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>("ERROR", e.getMessage(), null));
        }
    }

    /**
     * 根据项目经理ID查询项目
     * @param managerId 项目经理ID
     * @return 项目列表
     */
    @GetMapping("/manager/{managerId}")
    // @PreAuthorize("hasAuthority('project:read')") // 临时禁用
    public ResponseEntity<ApiResponse<List<ProjectDTO>>> getProjectsByManager(@PathVariable Long managerId) {
        try {
            List<ProjectDTO> result = projectService.getProjectsByManagerId(managerId);
            return ResponseEntity.ok(new ApiResponse<>("SUCCESS", "查询成功", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>("ERROR", e.getMessage(), null));
        }
    }

    /**
     * 根据项目名称模糊查询
     * @param name 项目名称关键字
     * @return 项目列表
     */
    @GetMapping("/search")
    // @PreAuthorize("hasAuthority('project:read')") // 临时禁用
    public ResponseEntity<ApiResponse<List<ProjectDTO>>> searchProjects(@RequestParam String name) {
        try {
            List<ProjectDTO> result = projectService.searchProjectsByName(name);
            return ResponseEntity.ok(new ApiResponse<>("SUCCESS", "搜索成功", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>("ERROR", e.getMessage(), null));
        }
    }

    /**
     * 获取活跃项目选项（用于下拉选择）
     * @return 项目选项列表
     */
    @GetMapping("/options")
    // @PreAuthorize("hasAuthority('project:read')") // 临时禁用
    public ResponseEntity<ApiResponse<List<ProjectOptionDTO>>> getActiveProjectOptions() {
        try {
            List<ProjectOptionDTO> result = projectService.getActiveProjectOptions();
            return ResponseEntity.ok(new ApiResponse<>("SUCCESS", "获取成功", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>("ERROR", e.getMessage(), null));
        }
    }

    /**
     * 检查项目代码是否存在
     * @param code 项目代码
     * @return 检查结果
     */
    @GetMapping("/exists/{code}")
    // @PreAuthorize("hasAuthority('project:read')") // 临时禁用
    public ResponseEntity<ApiResponse<Boolean>> checkProjectCodeExists(@PathVariable String code) {
        try {
            Boolean result = projectService.existsByProjectCode(code);
            return ResponseEntity.ok(new ApiResponse<>("SUCCESS", "检查完成", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>("ERROR", e.getMessage(), null));
        }
    }
}
