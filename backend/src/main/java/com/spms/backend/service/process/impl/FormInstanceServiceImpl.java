package com.spms.backend.service.process.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import com.spms.backend.repository.entities.process.FormInstanceEntity;
import com.spms.backend.repository.process.FormInstanceRepository;
import com.spms.backend.service.exception.NotFoundException;
import com.spms.backend.service.exception.ValidationException;
import com.spms.backend.service.model.process.FormInstanceModel;
import com.spms.backend.service.process.FormInstanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Transactional
public class FormInstanceServiceImpl implements FormInstanceService {

    private static final Pattern FORM_KEY_PATTERN = Pattern.compile("^[a-zA-Z0-9_-]{3,50}$");
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    private final FormInstanceRepository formInstanceRepository;

    @Autowired
    public FormInstanceServiceImpl(FormInstanceRepository formInstanceRepository) {
        this.formInstanceRepository = formInstanceRepository;
    }

    /**
     * Creates a new form instance from the provided request DTO
     * 
     * @param request CreateFormInstanceRequestDTO containing form data
     * @return FormInstanceModel representing the created form
     * @throws ValidationException if request validation fails
     */
    @Override
    public FormInstanceModel create(String formKey, String version, Map<String, Object> data) throws ValidationException {
        validateFormKey(formKey);
        validateVersion(version);
        validateFormData(data);
        FormInstanceEntity entity = new FormInstanceEntity();
        entity.setFormKey(formKey);
        entity.setVersion(version);
        entity.setFormData(convertDataToString(data));
        entity.setCreatedAt(new Date().getTime());
        entity.setUpdatedAt(new Date().getTime());
        FormInstanceEntity savedEntity = formInstanceRepository.save(entity);
        return convertToModel(savedEntity);
    }

    /**
     * Retrieves a form instance by its unique identifier
     * 
     * @param id UUID of the form instance
     * @return FormInstanceModel representing the form
     * @throws NotFoundException if no form found with given ID
     */
    @Override
    public FormInstanceModel getById(String id) throws NotFoundException {
        Optional<FormInstanceEntity> entity = formInstanceRepository.findById(id);
        if (entity.isEmpty()) {
            throw new NotFoundException("Form instance not found with ID: " + id);
        }
        return convertToModel(entity.get());
    }

    /**
     * Updates an existing form instance
     * 
     * @param id UUID of the form to update
     * @param request UpdateFormInstanceRequestDTO with updated data
     * @return Updated FormInstanceModel
     * @throws ValidationException if request validation fails
     * @throws NotFoundException if no form found with given ID
     */
    @Override
    public FormInstanceModel update(String id, Map<String, Object> data) 
        throws ValidationException, NotFoundException {
        
        Optional<FormInstanceEntity> optionalEntity = formInstanceRepository.findById(id);
        if (optionalEntity.isEmpty()) {
            throw new NotFoundException("Form instance not found with ID: " + id);
        }
        
        FormInstanceEntity entity = optionalEntity.get();
        validateFormData(data);
        entity.setFormData(convertDataToString(data));
        entity.setUpdatedAt(new Date().getTime());
        
        FormInstanceEntity updatedEntity = formInstanceRepository.save(entity);
        return convertToModel(updatedEntity);
    }

    /**
     * Deletes a form instance by its ID
     * 
     * @param id UUID of the form to delete
     * @throws NotFoundException if no form found with given ID
     */
    @Override
    public void delete(String id) throws NotFoundException {
        if (!formInstanceRepository.existsById(id)) {
            throw new NotFoundException("Form instance not found with ID: " + id);
        }
        formInstanceRepository.deleteById(id);
    }

    /**
     * Finds form instances by form key
     * 
     * @param formKey Key identifier of the form
     * @return List of matching FormInstanceModels
     */
    @Override
    public List<FormInstanceModel> findByFormKey(String formKey) {
        List<FormInstanceEntity> entities = formInstanceRepository.findByFormKey(formKey);
        return entities.stream()
                .map(this::convertToModel)
                .collect(Collectors.toList());
    }

    /**
     * Finds form instances by form key and version
     * 
     * @param formKey Key identifier of the form
     * @param version Version of the form
     * @return List of matching FormInstanceModels
     */
    @Override
    public List<FormInstanceModel> findByFormKeyAndVersion(String formKey, String version) {
        List<FormInstanceEntity> entities = formInstanceRepository.findByFormKeyAndVersion(formKey, version);
        return entities.stream()
                .map(this::convertToModel)
                .collect(Collectors.toList());
    }

    private void validateFormKey(String formKey) throws ValidationException {
        if (formKey == null || formKey.isBlank()) {
            throw new ValidationException("Form key is required");
        }
        if (!FORM_KEY_PATTERN.matcher(formKey).matches()) {
            throw new ValidationException("Form key must be 3-50 characters and contain only letters, numbers, underscores, and hyphens");
        }
    }

    private void validateVersion(String version) throws ValidationException {
        if (version == null || version.isBlank()) {
            throw new ValidationException("Version is required");
        }
        // Additional semantic version validation can be added here
    }

    private void validateFormData(Map<String, Object> data) throws ValidationException {
        if (data == null || data.isEmpty()) {
            throw new ValidationException("Form data is required");
        }
    }

    private String convertDataToString(Map<String, Object> data) throws ValidationException {
        try {
            return objectMapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            throw new ValidationException("Invalid form data format: " + e.getMessage());
        }
    }

    private Map<String, Object> convertStringToData(String json) throws ValidationException {
        try {
            return objectMapper.readValue(json, Map.class);
        } catch (JsonProcessingException e) {
            throw new ValidationException("Invalid form data in storage: " + e.getMessage());
        }
    }

    private FormInstanceModel convertToModel(FormInstanceEntity entity) {
        FormInstanceModel model = new FormInstanceModel();
        model.setId(entity.getId().toString());
        model.setFormKey(entity.getFormKey());
        model.setVersion(entity.getVersion());
        model.setData(convertStringToData(entity.getFormData()));
        entity.setCreatedAt(new Date().getTime());
        
        if (entity.getUpdatedAt() != null) {
            entity.setUpdatedAt(new Date().getTime());
        }
        return model;
    }
}
