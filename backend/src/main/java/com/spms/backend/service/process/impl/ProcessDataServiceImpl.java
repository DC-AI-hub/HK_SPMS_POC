package com.spms.backend.service.process.impl;

import com.spms.backend.repository.entities.process.ProcessDataEntity;
import com.spms.backend.repository.process.ProcessDataRepository;
import com.spms.backend.service.process.FormDataProcessor;
import com.spms.backend.service.process.ProcessDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 统一的流程数据服务实现
 */
@Slf4j
@Service
@Transactional
public class ProcessDataServiceImpl implements ProcessDataService {

    @Autowired
    private ProcessDataRepository processDataRepository;

    @Autowired
    private FormDataProcessor formDataProcessor;

    @Override
    public ProcessDataEntity create(String processInstanceId, String processDefinitionKey,
                                  FormDataProcessor.UnifiedFormData unifiedFormData,
                                  Long userId) {
        log.info("Creating process data for instance: {}", processInstanceId);

        ProcessDataEntity entity = new ProcessDataEntity();
        entity.setProcessInstanceId(processInstanceId);
        entity.setProcessDefinitionKey(processDefinitionKey);
        entity.setUserId(userId);
        entity.setFormType(unifiedFormData.getFormType().name());
        entity.setFormData(formDataProcessor.serializeUnifiedFormData(unifiedFormData));
        entity.setStatus(ProcessDataEntity.ProcessDataStatus.SUBMITTED);

        ProcessDataEntity savedEntity = processDataRepository.save(entity);
        log.info("Process data created successfully: {}", savedEntity.getId());

        return savedEntity;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProcessDataEntity> findByProcessInstanceId(String processInstanceId) {
        return processDataRepository.findByProcessInstanceId(processInstanceId);
    }

    @Override
    public ProcessDataEntity updateStatus(String processInstanceId,
                                        ProcessDataEntity.ProcessDataStatus status,
                                        String approvalComment) {
        log.info("Updating process data status: {} -> {}", processInstanceId, status);

        ProcessDataEntity entity = processDataRepository.findByProcessInstanceId(processInstanceId)
            .orElseThrow(() -> new RuntimeException("Process data not found: " + processInstanceId));

        entity.setStatus(status);
        if (approvalComment != null) {
            entity.setApprovalComment(approvalComment);
        }

        ProcessDataEntity updatedEntity = processDataRepository.save(entity);
        log.info("Process data status updated successfully");

        return updatedEntity;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProcessDataEntity> search(Long userId, String processDefinitionKey,
                                        ProcessDataEntity.ProcessDataStatus status,
                                        Pageable pageable) {
        return processDataRepository.search(userId, processDefinitionKey, status, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProcessDataEntity> findActiveProcessesByUserId(Long userId) {
        return processDataRepository.findActiveProcessesByUserId(userId);
    }

    @Override
    public void cancel(String processInstanceId, String reason) {
        log.info("Cancelling process data: {}", processInstanceId);

        ProcessDataEntity entity = processDataRepository.findByProcessInstanceId(processInstanceId)
            .orElseThrow(() -> new RuntimeException("Process data not found: " + processInstanceId));

        entity.setStatus(ProcessDataEntity.ProcessDataStatus.CANCELLED);
        entity.setApprovalComment(reason != null ? reason : "Process cancelled");

        processDataRepository.save(entity);
        log.info("Process data cancelled successfully");
    }
}
