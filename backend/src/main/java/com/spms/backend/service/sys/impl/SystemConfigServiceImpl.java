package com.spms.backend.service.sys.impl;

import com.spms.backend.repository.entities.sys.SystemConfigEntity;
import com.spms.backend.repository.entities.sys.SystemConfigHistoryEntity;
import com.spms.backend.repository.sys.SystemConfigHistoryRepository;
import com.spms.backend.repository.sys.SystemConfigRepository;
import com.spms.backend.service.exception.NotFoundException;
import com.spms.backend.service.exception.ValidationException;
import com.spms.backend.service.idm.UserService;
import com.spms.backend.service.model.SystemConfigHistoryModel;
import com.spms.backend.service.model.SystemConfigModel;
import com.spms.backend.service.model.idm.UserModel;
import com.spms.backend.service.sys.SystemConfigService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SystemConfigServiceImpl implements SystemConfigService {

    private final SystemConfigRepository systemConfigRepository;
    private final SystemConfigHistoryRepository systemConfigHistoryRepository;
    private final UserService userService;
    private final ObjectMapper objectMapper;

    public SystemConfigServiceImpl(SystemConfigRepository systemConfigRepository,
                                  SystemConfigHistoryRepository systemConfigHistoryRepository,
                                  UserService userService,
                                  ObjectMapper objectMapper) {
        this.systemConfigRepository = systemConfigRepository;
        this.systemConfigHistoryRepository = systemConfigHistoryRepository;
        this.userService = userService;
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<SystemConfigModel> getAllSystemConfig() {
        List<SystemConfigEntity> entities = systemConfigRepository.findAll();
        return entities.stream()
                .map(SystemConfigModel::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public SystemConfigModel getSystemConfigByKey(String key) {
        SystemConfigEntity entity = systemConfigRepository.findByKey(key)
                .orElseThrow(() -> new NotFoundException("System configuration not found with key: " + key));
        return SystemConfigModel.fromEntity(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SystemConfigModel> getSystemConfigByCategory(String category) {
        List<SystemConfigEntity> entities = systemConfigRepository.findByCategoryOrderByKey(category);
        return entities.stream()
                .map(SystemConfigModel::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public SystemConfigModel updateSystemConfig(String key, SystemConfigModel configModel) {
        SystemConfigEntity existingEntity = systemConfigRepository.findByKey(key)
                .orElseThrow(() -> new NotFoundException("System configuration not found with key: " + key));

        // Check if setting is read-only
        if (existingEntity.getIsReadOnly()) {
            throw new ValidationException("Cannot update read-only configuration: " + key);
        }

        // Validate the new value based on data type
        validateConfigValue(existingEntity.getDataType(), configModel.getValue(), existingEntity.getValidationRules());

        // Save old value for history
        String oldValue = existingEntity.getValue();

        // Update entity
        existingEntity.setValue(configModel.getValue());
        
        UserModel currentUser = userService.getCurrentUser();
        existingEntity.setModifiedBy(currentUser.getUsername());
        existingEntity.setUpdatedAt(LocalDateTime.now());

        SystemConfigEntity updatedEntity = systemConfigRepository.save(existingEntity);

        // Create history record
        createHistoryRecord(updatedEntity, oldValue, configModel.getValue(), "UPDATE", currentUser.getUsername());

        return SystemConfigModel.fromEntity(updatedEntity);
    }

    @Override
    @Transactional
    public List<SystemConfigModel> batchUpdateSystemConfig(List<SystemConfigModel> updates) {
        UserModel currentUser = userService.getCurrentUser();
        List<SystemConfigModel> results = new ArrayList<>();

        for (SystemConfigModel update : updates) {
            try {
                SystemConfigModel updatedConfig = updateSystemConfig(update.getKey(), update);
                results.add(updatedConfig);
            } catch (Exception e) {
                // Log error and continue with other updates
                // In a real implementation, you might want to handle this differently
                throw new ValidationException("Failed to update configuration with key: " + update.getKey() + " - " + e.getMessage());
            }
        }

        return results;
    }

    @Override
    @Transactional
    public SystemConfigModel resetSystemConfig(String key) {
        SystemConfigEntity existingEntity = systemConfigRepository.findByKey(key)
                .orElseThrow(() -> new NotFoundException("System configuration not found with key: " + key));

        if (existingEntity.getDefaultValue() == null) {
            throw new ValidationException("No default value defined for configuration: " + key);
        }

        // Save old value for history
        String oldValue = existingEntity.getValue();

        // Reset to default value
        existingEntity.setValue(existingEntity.getDefaultValue());
        
        UserModel currentUser = userService.getCurrentUser();
        existingEntity.setModifiedBy(currentUser.getUsername());
        existingEntity.setUpdatedAt(LocalDateTime.now());

        SystemConfigEntity updatedEntity = systemConfigRepository.save(existingEntity);

        // Create history record
        createHistoryRecord(updatedEntity, oldValue, existingEntity.getDefaultValue(), "RESET", currentUser.getUsername());

        return SystemConfigModel.fromEntity(updatedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SystemConfigHistoryModel> getSystemConfigHistory(String key, Pageable pageable) {
        SystemConfigEntity configEntity = systemConfigRepository.findByKey(key)
                .orElseThrow(() -> new NotFoundException("System configuration not found with key: " + key));

        Page<SystemConfigHistoryEntity> historyEntities = systemConfigHistoryRepository
                .findByConfigIdOrderByChangeTimestampDesc(configEntity.getId(), pageable);

        return historyEntities.map(SystemConfigHistoryModel::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> validateSystemConfig(String key, Object value) {
        SystemConfigEntity entity = systemConfigRepository.findByKey(key)
                .orElseThrow(() -> new NotFoundException("System configuration not found with key: " + key));

        Map<String, Object> result = new HashMap<>();
        try {
            validateConfigValue(entity.getDataType(), value.toString(), entity.getValidationRules());
            result.put("isValid", true);
            result.put("message", "Validation successful");
        } catch (ValidationException e) {
            result.put("isValid", false);
            result.put("message", e.getMessage());
        }

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getSystemConfigCategories() {
        return systemConfigRepository.findDistinctCategories();
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] exportSystemConfig(String format) {
        // Implementation for exporting configuration to various formats
        // This would typically convert all configs to JSON, YAML, or properties format
        // For now, return empty array as placeholder
        return new byte[0];
    }

    @Override
    @Transactional
    public Map<String, Object> importSystemConfig(MultipartFile file, boolean overwrite) {
        // Implementation for importing configuration from file
        // This would parse the file and update configurations accordingly
        Map<String, Object> result = new HashMap<>();
        result.put("imported", 0);
        result.put("skipped", 0);
        result.put("errors", new ArrayList<String>());
        return result;
    }

    private void validateConfigValue(String dataType, String value, String validationRules) {
        try {
            switch (dataType) {
                case "NUMBER":
                    Double.parseDouble(value);
                    break;
                case "BOOLEAN":
                    if (!"true".equalsIgnoreCase(value) && !"false".equalsIgnoreCase(value)) {
                        throw new ValidationException("Value must be 'true' or 'false' for BOOLEAN type");
                    }
                    break;
                case "JSON":
                    objectMapper.readTree(value); // Validate JSON format
                    break;
                case "ARRAY":
                    objectMapper.readValue(value, List.class); // Validate array format
                    break;
                // STRING type doesn't need additional validation beyond basic checks
            }
        } catch (Exception e) {
            throw new ValidationException("Invalid value for data type " + dataType + ": " + e.getMessage());
        }

        // TODO: Implement validation rules parsing and application
        // validationRules would contain JSON with validation constraints
    }

    private void createHistoryRecord(SystemConfigEntity config, String oldValue, String newValue, String changeType, String changedBy) {
        SystemConfigHistoryEntity history = new SystemConfigHistoryEntity();
        history.setConfig(config);
        history.setOldValue(oldValue);
        history.setNewValue(newValue);
        history.setChangeType(changeType);
        history.setChangedBy(changedBy);
        history.setChangeTimestamp(LocalDateTime.now());

        systemConfigHistoryRepository.save(history);
    }
}
