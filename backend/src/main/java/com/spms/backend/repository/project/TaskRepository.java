package com.spms.backend.repository.project;

import com.spms.backend.repository.entities.project.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 任务数据访问接口
 * 提供任务相关的数据库操作方法
 */
@Repository
public interface TaskRepository extends JpaRepository<TaskEntity, Long>, JpaSpecificationExecutor<TaskEntity> {

    /**
     * 根据任务编号查找任务
     * @param taskNumber 任务编号
     * @return 任务实体
     */
    Optional<TaskEntity> findByTaskNumber(String taskNumber);

    /**
     * 检查任务编号是否存在
     * @param taskNumber 任务编号
     * @return 是否存在
     */
    boolean existsByTaskNumber(String taskNumber);

    /**
     * 根据项目ID查找所有激活的任务
     * @param projectId 项目ID
     * @return 激活的任务列表
     */
    List<TaskEntity> findByProjectIdAndIsActiveTrue(Long projectId);
    
    /**
     * 根据项目ID查找所有任务
     * 使用EntityGraph避免N+1问题
     * @param projectId 项目ID
     * @return 任务列表
     */
    @EntityGraph(attributePaths = {"project"})
    @Query("SELECT t FROM TaskEntity t WHERE t.project.id = :projectId ORDER BY t.taskName")
    List<TaskEntity> findByProjectId(@Param("projectId") Long projectId);

    /**
     * 根据项目代码查找所有任务
     * @param projectCode 项目代码
     * @return 任务列表
     */
    @EntityGraph(attributePaths = {"project"})
    @Query("SELECT t FROM TaskEntity t WHERE t.project.projectCode = :projectCode ORDER BY t.taskName")
    List<TaskEntity> findByProjectCode(@Param("projectCode") String projectCode);

    /**
     * 根据任务名称模糊查询
     * @param taskName 任务名称关键字
     * @return 任务列表
     */
    @EntityGraph(attributePaths = {"project"})
    @Query("SELECT t FROM TaskEntity t WHERE t.taskName LIKE %:taskName%")
    List<TaskEntity> findByTaskNameContaining(@Param("taskName") String taskName);

    /**
     * 查询指定项目的任务数量
     * @param projectId 项目ID
     * @return 任务数量
     */
    @Query("SELECT COUNT(t) FROM TaskEntity t WHERE t.project.id = :projectId")
    Long countByProjectId(@Param("projectId") Long projectId);

    /**
     * 查找项目中的所有任务（用于下拉选择）
     * 返回实体以便在Service层组装activities
     * @param projectId 项目ID
     * @return 任务实体列表
     */
    @Query("SELECT t FROM TaskEntity t WHERE t.project.id = :projectId ORDER BY t.taskName")
    List<TaskEntity> findTaskOptionsByProjectId(@Param("projectId") Long projectId);
    
    /**
     * 根据项目 ID 查找所有活跃任务
     * 用于 Timecard 表单中的任务选择
     * @param projectId 项目ID
     * @return 活跃任务列表
     */
    @EntityGraph(attributePaths = {"project"})
    @Query("SELECT t FROM TaskEntity t WHERE t.project.id = :projectId AND t.isActive = true ORDER BY t.taskName")
    List<TaskEntity> findActiveTasksByProjectId(@Param("projectId") Long projectId);
}
