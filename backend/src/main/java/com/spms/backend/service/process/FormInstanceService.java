package com.spms.backend.service.process;

import com.spms.backend.service.exception.NotFoundException;
import com.spms.backend.service.exception.ValidationException;
import com.spms.backend.service.model.process.FormInstanceModel;
import java.util.List;
import java.util.Map;

/**
 * Service interface for managing form instances.
 */
public interface FormInstanceService {
    FormInstanceModel create(String formKey, String version, Map<String, Object> data) throws ValidationException;
    FormInstanceModel getById(String id) throws NotFoundException;
    FormInstanceModel update(String id, Map<String, Object> data) throws ValidationException, NotFoundException;
    void delete(String id) throws NotFoundException;
    List<FormInstanceModel> findByFormKey(String formKey);
    List<FormInstanceModel> findByFormKeyAndVersion(String formKey, String version);
}
