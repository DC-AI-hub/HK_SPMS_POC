package com.spms.backend.service.process;

import com.spms.backend.service.exception.ValidationException;
import com.spms.backend.service.model.process.ProcessVersionModel;

public interface ProcessValidationService {
    /**
     * Validates process definition ID format
     * @param definitionId The ID to validate
     * @throws ValidationException if invalid format
     */
    void validateDefinitionId(String definitionId) throws ValidationException;

    /**
     * Validates process version ID format
     * @param versionId The version ID to validate
     * @throws ValidationException if invalid format
     */
    void validateVersionId(String versionId) throws ValidationException;

    /**
     * Validates user ID is not null
     * @param userId The user ID to validate
     * @throws ValidationException if null
     */
    void validateUserId(Long userId) throws ValidationException;

    /**
     * Validates process instance ID is not null/empty
     * @param instanceId The instance ID to validate
     * @throws ValidationException if invalid
     */
    void validateInstanceId(String instanceId) throws ValidationException;

    /**
     * Validates task ID is not null/empty
     * @param taskId The task ID to validate
     * @throws ValidationException if invalid
     */
    void validateTaskId(String taskId) throws ValidationException;

    /**
     * Validates process version model for creation
     * @param model The model to validate
     * @throws ValidationException if invalid
     */
    void validateProcessVersionModel(ProcessVersionModel model) throws ValidationException;

    void validateVersion(String versionText) throws ValidationException;
}
