package com.spms.backend.service.process;

import com.spms.backend.repository.entities.process.ProcessDataEntity;
import com.spms.backend.service.process.FormDataProcessor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * 统一的流程数据服务接口
 */
public interface ProcessDataService {

    /**
     * 创建新的流程数据
     */
    ProcessDataEntity create(String processInstanceId, String processDefinitionKey,
                           FormDataProcessor.UnifiedFormData unifiedFormData,
                           Long userId);

    /**
     * 根据流程实例ID查找流程数据
     */
    Optional<ProcessDataEntity> findByProcessInstanceId(String processInstanceId);

    /**
     * 更新流程数据状态
     */
    ProcessDataEntity updateStatus(String processInstanceId,
                                 ProcessDataEntity.ProcessDataStatus status,
                                 String approvalComment);

    /**
     * 分页查找流程数据
     */
    Page<ProcessDataEntity> search(Long userId, String processDefinitionKey,
                                 ProcessDataEntity.ProcessDataStatus status,
                                 Pageable pageable);

    /**
     * 查找用户的所有活跃流程
     */
    List<ProcessDataEntity> findActiveProcessesByUserId(Long userId);

    /**
     * 取消流程数据
     */
    void cancel(String processInstanceId, String reason);
}
