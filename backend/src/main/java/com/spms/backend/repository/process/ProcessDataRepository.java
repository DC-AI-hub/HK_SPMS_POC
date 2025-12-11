package com.spms.backend.repository.process;

import com.spms.backend.repository.entities.process.ProcessDataEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 统一的流程数据仓库
 */
@Repository
public interface ProcessDataRepository extends JpaRepository<ProcessDataEntity, Long> {

    /**
     * 根据用户ID和流程定义键查找数据
     */
    @Query("SELECT p FROM ProcessDataEntity p WHERE p.userId = :userId AND p.processDefinitionKey = :processDefinitionKey AND p.status != 'CANCELLED' ORDER BY p.createdAt DESC")
    List<ProcessDataEntity> findByUserIdAndProcessDefinitionKey(
        @Param("userId") Long userId,
        @Param("processDefinitionKey") String processDefinitionKey);

    /**
     * 根据流程实例ID查找数据
     */
    Optional<ProcessDataEntity> findByProcessInstanceId(@Param("processInstanceId") String processInstanceId);

    /**
     * 根据用户ID、流程定义键和状态分页查找
     */
    @Query("SELECT p FROM ProcessDataEntity p WHERE (:userId IS NULL OR p.userId = :userId) AND (:processDefinitionKey IS NULL OR p.processDefinitionKey = :processDefinitionKey) AND (:status IS NULL OR p.status = :status) AND p.status != 'CANCELLED' ORDER BY p.createdAt DESC")
    Page<ProcessDataEntity> search(
        @Param("userId") Long userId,
        @Param("processDefinitionKey") String processDefinitionKey,
        @Param("status") ProcessDataEntity.ProcessDataStatus status,
        Pageable pageable);

    /**
     * 统计用户的流程数据数量
     */
    @Query("SELECT COUNT(p) FROM ProcessDataEntity p WHERE p.userId = :userId AND p.processDefinitionKey = :processDefinitionKey AND p.status != 'CANCELLED'")
    long countByUserIdAndProcessDefinitionKey(
        @Param("userId") Long userId,
        @Param("processDefinitionKey") String processDefinitionKey);

    /**
     * 查找用户的所有活跃流程数据
     */
    @Query("SELECT p FROM ProcessDataEntity p WHERE p.userId = :userId AND p.status IN ('SUBMITTED', 'APPROVED') ORDER BY p.createdAt DESC")
    List<ProcessDataEntity> findActiveProcessesByUserId(@Param("userId") Long userId);
}
