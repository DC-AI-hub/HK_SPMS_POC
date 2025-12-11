package com.spms.backend.repository.project;

import com.spms.backend.repository.entities.project.ProjectEntity;
import com.spms.backend.model.ProjectStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 项目数据访问接口
 * 提供项目相关的数据库操作方法
 */
@Repository
public interface ProjectRepository extends JpaRepository<ProjectEntity, Long>, JpaSpecificationExecutor<ProjectEntity> {

    /**
     * 根据项目代码查找项目
     * @param projectCode 项目代码
     * @return 项目实体
     */
    Optional<ProjectEntity> findByProjectCode(String projectCode);

    /**
     * 检查项目代码是否存在
     * @param projectCode 项目代码
     * @return 是否存在
     */
    boolean existsByProjectCode(String projectCode);

    /**
     * 根据状态查找项目
     * @param status 项目状态
     * @return 项目列表
     */
    List<ProjectEntity> findByStatus(ProjectStatus status);

    /**
     * 根据项目经理ID查找项目
     * @param managerId 项目经理ID
     * @return 项目列表
     */
    @Query("SELECT p FROM ProjectEntity p WHERE p.projectManager.id = :managerId")
    List<ProjectEntity> findByProjectManagerId(@Param("managerId") Long managerId);

    /**
     * 根据项目名称模糊查询
     * @param projectName 项目名称关键字
     * @return 项目列表
     */
    @Query("SELECT p FROM ProjectEntity p WHERE p.projectName LIKE %:projectName%")
    List<ProjectEntity> findByProjectNameContaining(@Param("projectName") String projectName);

    /**
     * 查找所有活跃项目
     * @return 活跃项目列表
     */
    @Query("SELECT p FROM ProjectEntity p WHERE p.status = 'ACTIVE' ORDER BY p.projectName")
    List<ProjectEntity> findActiveProjects();
    
    /**
     * 查找所有活跃且未删除的项目
     * 用于 Timecard 表单中的项目选择
     * @return 活跃项目列表
     */
    @Query("SELECT p FROM ProjectEntity p WHERE p.status = 'ACTIVE' AND p.isActive = true ORDER BY p.projectName")
    List<ProjectEntity> findAllActiveProjects();
    
    /**
     * 根据项目代码查找活跃项目
     * 用于验证 Timecard 中输入的项目代码
     * @param projectCode 项目代码
     * @return 项目实体（如果存在且活跃）
     */
    @Query("SELECT p FROM ProjectEntity p WHERE p.projectCode = :projectCode AND p.status = 'ACTIVE' AND p.isActive = true")
    Optional<ProjectEntity> findActiveProjectByCode(@Param("projectCode") String projectCode);
    
    /**
     * 根据项目代码或名称模糊查询活跃项目
     * 用于 Timecard 表单中的项目搜索
     * @param search 搜索关键词
     * @return 活跃项目列表
     */
    @Query("SELECT p FROM ProjectEntity p WHERE (p.projectCode LIKE %:search% OR p.projectName LIKE %:search%) " +
           "AND p.status = 'ACTIVE' AND p.isActive = true ORDER BY p.projectName")
    List<ProjectEntity> searchActiveProjects(@Param("search") String search);
}
