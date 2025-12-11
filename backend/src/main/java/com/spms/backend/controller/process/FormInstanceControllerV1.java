package com.spms.backend.controller.process;

import com.spms.backend.controller.dto.process.CreateFormInstanceRequestDTO;
import com.spms.backend.controller.dto.process.FormInstanceDTO;
import com.spms.backend.controller.dto.process.UpdateFormInstanceRequestDTO;
import com.spms.backend.service.exception.NotFoundException;
import com.spms.backend.service.exception.ValidationException;
import com.spms.backend.service.model.process.FormInstanceModel;
import com.spms.backend.service.process.FormInstanceService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/forms-instance")
public class FormInstanceControllerV1 {
    private static final Logger logger = LoggerFactory.getLogger(FormInstanceControllerV1.class);

    private final FormInstanceService formInstanceService;

    public FormInstanceControllerV1(FormInstanceService formInstanceService) {
        this.formInstanceService = formInstanceService;
    }

    /**
     * Creates a new form instance
     *
     * @param request Form instance creation request
     * @return Created form instance
     * @throws ValidationException if input validation fails
     */
    @PostMapping
    public ResponseEntity<FormInstanceDTO> createFormInstance(
            @Valid @RequestBody CreateFormInstanceRequestDTO request) {
        logger.debug("Creating new form instance for form: {}", request.getFormKey());
        try {
            FormInstanceModel model = formInstanceService.create(request.getFormKey(),request.getVersion(),request.getData());
            logger.info("Created form instance {} for form {}", model.getId(), request.getFormKey());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(convertToDTO(model));
        } catch (IllegalArgumentException e) {
            logger.error("Error creating form instance: {}", e.getMessage());
            ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
            problem.setDetail(e.getMessage());
            return ResponseEntity.badRequest().body(null);
        }
    }

    /**
     * Retrieves a form instance by ID
     *
     * @param id Form instance ID
     * @return Requested form instance
     * @throws NotFoundException if form instance not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<FormInstanceDTO> getFormInstance(
            @PathVariable String id) {
        logger.debug("Getting form instance: {}", id);
        FormInstanceModel model = formInstanceService.getById(id);
        logger.debug("Found form instance {}", id);
        return ResponseEntity.ok(convertToDTO(model));
    }

    /**
     * Updates an existing form instance
     *
     * @param id      Form instance ID
     * @param request Update request
     * @return Updated form instance
     * @throws ValidationException if input validation fails
     * @throws NotFoundException   if form instance not found
     */
    @PutMapping("/{id}")
    public ResponseEntity<FormInstanceDTO> updateFormInstance(
            @PathVariable String id,
            @Valid @RequestBody UpdateFormInstanceRequestDTO request) {
        logger.debug("Updating form instance: {}", id);
        try {
            FormInstanceModel model = formInstanceService.update(id, request.getData());
            logger.info("Updated form instance {}", id);
            return ResponseEntity.ok(convertToDTO(model));
        } catch (IllegalArgumentException e) {
            logger.error("Error updating form instance: {}", e.getMessage());
            ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
            problem.setDetail(e.getMessage());
            return ResponseEntity.badRequest().body(null);
        }
    }

    /**
     * Deletes a form instance
     *
     * @param id Form instance ID
     * @return No content
     * @throws NotFoundException if form instance not found
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFormInstance(
            @PathVariable String id) {
        logger.debug("Deleting form instance: {}", id);
        formInstanceService.delete(id);
        logger.info("Deleted form instance {}", id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Lists form instances by form key
     *
     * @param formKey Form definition key
     * @return List of form instances
     */
    @GetMapping("/form/{formKey}")
    public ResponseEntity<List<FormInstanceDTO>> listByFormKey(
            @PathVariable String formKey) {
        logger.debug("Listing form instances for form: {}", formKey);
        List<FormInstanceModel> models = formInstanceService.findByFormKey(formKey);
        logger.debug("Found {} form instances for form {}", models.size(), formKey);
        return ResponseEntity.ok(
                models.stream()
                        .map(this::convertToDTO)
                        .collect(Collectors.toList()));
    }

    /**
     * Lists form instances by form key and version
     *
     * @param formKey  Form definition key
     * @param version  Form version
     * @return List of form instances
     */
    @GetMapping("/form/{formKey}/version/{version}")
    public ResponseEntity<List<FormInstanceDTO>> listByFormKeyAndVersion(
            @PathVariable String formKey,
            @PathVariable String version) {
        logger.debug("Listing form instances for form {} version {}", formKey, version);
        List<FormInstanceModel> models = formInstanceService.findByFormKeyAndVersion(formKey, version);
        logger.debug("Found {} form instances for form {} version {}", models.size(), formKey, version);
        return ResponseEntity.ok(
                models.stream()
                        .map(this::convertToDTO)
                        .collect(Collectors.toList()));
    }

    private FormInstanceDTO convertToDTO(FormInstanceModel model) {
        FormInstanceDTO dto = new FormInstanceDTO();
        dto.setId(model.getId());
        dto.setFormKey(model.getFormKey());
        dto.setVersion(model.getVersion());
        dto.setData(model.getData());
        dto.setCreatedDate(model.getCreatedDate());
        dto.setModifiedDate(model.getModifiedDate());
        return dto;
    }
}
