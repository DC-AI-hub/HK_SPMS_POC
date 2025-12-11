package com.spms.backend.controller.process;

import com.spms.backend.controller.dto.process.FormDefinitionDTO;
import com.spms.backend.controller.dto.process.FormVersionDTO;
import com.spms.backend.service.exception.NotFoundException;
import com.spms.backend.service.exception.ValidationException;
import com.spms.backend.service.model.process.FormDefinitionModel;
import com.spms.backend.service.model.process.FormVersionModel;
import com.spms.backend.service.process.FormService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/forms")
public class FormControllerV1 {
    private static final Logger logger = LoggerFactory.getLogger(FormControllerV1.class);

    private final FormService formService;

    public FormControllerV1(FormService formService) {
        this.formService = formService;
    }

    // Conversion methods removed and replaced with DTO static methods

    /**
     * Creates a new form version
     *
     * @param key Form definition key
     * @param dto Form definition data
     * @return Created form version
     * @throws ValidationException if input validation fails
     * @throws NotFoundException   if form definition not found
     */
    @PostMapping("/{key}/versions")
    public ResponseEntity<FormVersionDTO> createNewVersion(
            @PathVariable String key,
            @RequestBody FormDefinitionDTO dto) {
        logger.debug("Creating new version for form: {}", key);
        try {
            FormDefinitionModel model = FormDefinitionDTO.convertToModel(dto);
            FormVersionModel createdVersion = formService.createFormVersion(key, model);
            logger.info("Created new version {} for form {}", createdVersion.getVersion(), key);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(FormVersionDTO.convertToDTO(createdVersion));
        } catch (IllegalArgumentException e) {
            logger.error("Error creating form version: {}", e.getMessage());
            ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
            problem.setDetail(e.getMessage());
            return ResponseEntity.badRequest().body(null);
        }
    }

    /**
     * Gets the latest version of a form
     *
     * @param key Form definition key
     * @return Latest form version
     * @throws NotFoundException if form definition not found
     */
    /**
     * Retrieves all distinct form keys
     *
     * @return List of form keys
     */
    @GetMapping
    public ResponseEntity<List<String>> getAllFormKeys() {
        logger.debug("Getting all form keys");
        List<String> keys = formService.getAllFormKeys();
        logger.debug("Found {} form keys", keys.size());
        return ResponseEntity.ok(keys);
    }

    @GetMapping("/{key}/versions/latest")
    public ResponseEntity<FormVersionDTO> getLatestVersion(
            @PathVariable String key) {
        logger.debug("Getting latest version for form: {}", key);
        FormVersionModel latestVersion = formService.getLatestVersion(key);
        logger.debug("Found latest version {} for form {}", latestVersion.getVersion(), key);
        return ResponseEntity.ok(FormVersionDTO.convertToDTO(latestVersion));
    }

    /**
     * Gets a specific version of a form
     *
     * @param key     Form definition key
     * @param version Version identifier
     * @return Requested form version
     * @throws NotFoundException if form or version not found
     */
    @GetMapping("/{key}/versions/{version}")
    public ResponseEntity<FormVersionDTO> getVersion(
            @PathVariable String key,
            @PathVariable String version) {
        logger.debug("Getting version {} for form: {}", version, key);
        FormVersionModel versionModel = formService.getVersion(key, version);
        logger.debug("Found version {} for form {}", version, key);
        return ResponseEntity.ok(FormVersionDTO.convertToDTO(versionModel));
    }

    /**
     * Lists all versions of a form
     *
     * @param key Form definition key
     * @return List of form versions
     * @throws NotFoundException if form definition not found
     */
    @GetMapping("/{key}/versions")
    public ResponseEntity<List<FormVersionDTO>> listVersions(
            @PathVariable String key) {
        logger.debug("Listing versions for form: {}", key);
        List<FormVersionModel> versions = formService.listVersions(key);
        logger.debug("Found {} versions for form {}", versions.size(), key);
        return ResponseEntity.ok(FormVersionDTO.convertToDTOList(versions));
    }

    /**
     * Deprecates a form version
     *
     * @param key     Form definition key
     * @param version Version identifier
     * @return No content
     * @throws NotFoundException   if form or version not found
     * @throws ValidationException if version is already deprecated
     */
    @PostMapping("/{key}/versions/{version}/deprecate")
    public ResponseEntity<Void> deprecateVersion(
            @PathVariable @NotBlank String key,
            @PathVariable @NotBlank @Pattern(regexp = "[a-zA-Z0-9\\-.]+") String version) {
        logger.debug("Deprecating version {} for form: {}", version, key);
        formService.deprecateVersion(key, version);
        logger.info("Deprecated version {} for form {}", version, key);
        return ResponseEntity.noContent().build();
    }

}
