package com.spms.backend.service.process.impl;

import com.spms.backend.service.exception.ValidationException;
import com.spms.backend.service.model.process.ProcessVersionModel;
import com.spms.backend.service.process.ProcessValidationService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class ProcessValidationServiceImpl implements ProcessValidationService {

    @Override
    public void validateDefinitionId(String definitionId) throws ValidationException {
        if (!StringUtils.hasLength(definitionId)) {
            throw new ValidationException("Definition ID cannot be null or empty");
        }
    }

    @Override
    public void validateVersionId(String versionId) throws ValidationException {
        if (!StringUtils.hasLength(versionId)) {
            throw new ValidationException("Version ID cannot be null or empty");
        }
    }

    @Override
    public void validateVersion(String versionText) throws ValidationException {
        if (!StringUtils.hasLength(versionText)) {
            throw new ValidationException("Version cannot be null or empty");
        }
        
        String[] parts = versionText.split("\\.");
        if (parts.length != 3) {
            throw new ValidationException("Version must follow major.minor.revision format (e.g., 1.0.0)");
        }
        
        try {
            for (String part : parts) {
                int num = Integer.parseInt(part);
                if (num < 0) {
                    throw new ValidationException("Version components must be non-negative integers");
                }
            }
        } catch (NumberFormatException e) {
            throw new ValidationException("Version components must be integers");
        }
    }

    @Override
    public void validateUserId(Long userId) throws ValidationException {
        if (userId == null) {
            throw new ValidationException("User ID cannot be null");
        }
    }

    @Override
    public void validateInstanceId(String instanceId) throws ValidationException {
        if (StringUtils.isEmpty(instanceId)) {
            throw new ValidationException("Instance ID cannot be null or empty");
        }
    }

    @Override
    public void validateTaskId(String taskId) throws ValidationException {
        if (StringUtils.isEmpty(taskId)) {
            throw new ValidationException("Task ID cannot be null or empty");
        }
    }

    @Override
    public void validateProcessVersionModel(ProcessVersionModel model) throws ValidationException {
        if (model == null) {
            throw new ValidationException("Process version model cannot be null");
        }
        if (!StringUtils.hasLength(model.getName())) {
            throw new ValidationException("Process name cannot be empty");
        }
        if (!StringUtils.hasLength(model.getFlowableDefinitionId())) {
            throw new ValidationException("Process definition ID cannot be empty");
        }
    }
}
