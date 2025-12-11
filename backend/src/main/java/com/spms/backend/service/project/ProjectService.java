package com.spms.backend.service.project;

import com.spms.backend.controller.dto.project.ProjectDTO;
import com.spms.backend.controller.dto.project.ProjectCreateDTO;
import com.spms.backend.controller.dto.project.ProjectOptionDTO;
import com.spms.backend.model.ProjectStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * 项目服务接口
 * 定义项目相关的业务逻辑方法
 */
public interface ProjectService {

    /**
     * 创建新项目
     * @param createDTO 项目创建数据
     * @return 创建的项目DTO
     */
    ProjectDTO createProject(ProjectCreateDTO createDTO);

    /**
     * 根据ID获取项目
     * @param id 项目ID
     * @return 项目DTO
     */
    ProjectDTO getProjectById(Long id);

    /**
     * 根据项目代码获取项目
     * @param projectCode 项目代码
     * @return 项目DTO
     */
    ProjectDTO getProjectByCode(String projectCode);

    /**
     * 更新项目
     * @param id 项目ID
     * @param projectDTO 更新的项目数据
     * @return 更新后的项目DTO
     */
    ProjectDTO updateProject(Long id, ProjectDTO projectDTO);

    /**
     * 删除项目
     * @param id 项目ID
     */
    void deleteProject(Long id);

    /**
     * 分页查询项目
     * @param pageable 分页参数
     * @return 项目分页数据
     */
    Page<ProjectDTO> getProjects(Pageable pageable);

    /**
     * 根据状态查询项目
     * @param status 项目状态
     * @return 项目列表
     */
    List<ProjectDTO> getProjectsByStatus(ProjectStatus status);

    /**
     * 根据项目经理ID查询项目
     * @param managerId 项目经理ID
     * @return 项目列表
     */
    List<ProjectDTO> getProjectsByManagerId(Long managerId);

    /**
     * 根据项目名称模糊查询
     * @param projectName 项目名称关键字
     * @return 项目列表
     */
    List<ProjectDTO> searchProjectsByName(String projectName);

    /**
     * 获取所有活跃项目选项（用于下拉选择）
     * @return 项目选项列表
     */
    List<ProjectOptionDTO> getActiveProjectOptions();

    /**
     * 检查项目代码是否存在
     * @param projectCode 项目代码
     * @return 是否存在
     */
    boolean existsByProjectCode(String projectCode);
}
