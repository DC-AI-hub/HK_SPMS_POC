package com.spms.backend.controller.sys;

import com.spms.backend.controller.dto.sys.BatchUpdateRequestDTO;
import com.spms.backend.controller.dto.sys.SystemConfigResponseDTO;
import com.spms.backend.controller.dto.sys.SystemConfigUpdateRequestDTO;
import com.spms.backend.controller.dto.sys.ValidationResultDTO;
import com.spms.backend.service.model.SystemConfigHistoryModel;
import com.spms.backend.service.model.SystemConfigModel;
import com.spms.backend.service.sys.SystemConfigService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controller for managing system configuration settings
 */
@RestController
@RequestMapping("/api/v1/system/settings")
public class SystemConfigController extends com.spms.backend.controller.BaseController {

    private final SystemConfigService systemConfigService;

    public SystemConfigController(SystemConfigService systemConfigService) {
        this.systemConfigService = systemConfigService;
    }

    /**
     * Get all system configuration settings
     */
    @GetMapping
    //@PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<SystemConfigResponseDTO>> getAllSystemConfig() {
        List<SystemConfigModel> configModels = systemConfigService.getAllSystemConfig();
        List<SystemConfigResponseDTO> response = configModels.stream()
                .map(SystemConfigResponseDTO::fromSystemConfigModel)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    /**
     * Get system configuration setting by key
     */
    @GetMapping("/{key}")
    //@PreAuthorize("isAuthenticated()")
    public ResponseEntity<SystemConfigResponseDTO> getSystemConfigByKey(@PathVariable String key) {
        SystemConfigModel configModel = systemConfigService.getSystemConfigByKey(key);
        return ResponseEntity.ok(SystemConfigResponseDTO.fromSystemConfigModel(configModel));
    }

    /**
     * Get system configuration settings by category
     */
    @GetMapping(params = "category")
    //@PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<SystemConfigResponseDTO>> getSystemConfigByCategory(
            @RequestParam String category) {
        List<SystemConfigModel> configModels = systemConfigService.getSystemConfigByCategory(category);
        List<SystemConfigResponseDTO> response = configModels.stream()
                .map(SystemConfigResponseDTO::fromSystemConfigModel)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    /**
     * Update a system configuration setting
     */
    @PutMapping("/{key}")
    //@PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<SystemConfigResponseDTO> updateSystemConfig(
            @PathVariable String key,
            @Valid @RequestBody SystemConfigUpdateRequestDTO updateRequest) {
        SystemConfigModel configModel = new SystemConfigModel();
        configModel.setKey(key);
        configModel.setValue(updateRequest.getValue());
        // modifiedBy will be set from current user in service layer
        
        SystemConfigModel updatedConfig = systemConfigService.updateSystemConfig(key, configModel);
        return ResponseEntity.ok(SystemConfigResponseDTO.fromSystemConfigModel(updatedConfig));
    }

    /**
     * Batch update multiple system configuration settings
     */
    @PatchMapping("/batch")
    //@PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<List<SystemConfigResponseDTO>> batchUpdateSystemConfig(
            @Valid @RequestBody BatchUpdateRequestDTO batchRequest) {
        List<SystemConfigModel> updates = batchRequest.getUpdates().stream()
                .map(item -> {
                    SystemConfigModel model = new SystemConfigModel();
                    model.setKey(item.getKey());
                    model.setValue(item.getValue());
                    return model;
                })
                .collect(Collectors.toList());
        
        List<SystemConfigModel> updatedConfigs = systemConfigService.batchUpdateSystemConfig(updates);
        List<SystemConfigResponseDTO> response = updatedConfigs.stream()
                .map(SystemConfigResponseDTO::fromSystemConfigModel)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    /**
     * Reset system configuration setting to default value
     */
    @PostMapping("/{key}/reset")
    //@PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<SystemConfigResponseDTO> resetSystemConfig(@PathVariable String key) {
        SystemConfigModel resetConfig = systemConfigService.resetSystemConfig(key);
        return ResponseEntity.ok(SystemConfigResponseDTO.fromSystemConfigModel(resetConfig));
    }

    /**
     * Get system configuration settings history
     */
    @GetMapping("/{key}/history")
    //@PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<Page<SystemConfigHistoryModel>> getSystemConfigHistory(
            @PathVariable String key, Pageable pageable) {
        Page<SystemConfigHistoryModel> history = systemConfigService.getSystemConfigHistory(key, pageable);
        return ResponseEntity.ok(history);
    }

    /**
     * Validate system configuration value
     */
    @PostMapping("/{key}/validate")
    //@PreAuthorize("isAuthenticated()")
    public ResponseEntity<ValidationResultDTO> validateSystemConfig(
            @PathVariable String key, @RequestBody Map<String, Object> validationRequest) {
        Object value = validationRequest.get("value");
        Map<String, Object> validationResult = systemConfigService.validateSystemConfig(key, value);
        
        ValidationResultDTO response = ValidationResultDTO.builder()
                .isValid((Boolean) validationResult.get("isValid"))
                .message((String) validationResult.get("message"))
                .build();
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get system configuration categories
     */
    @GetMapping("/categories")
    //@PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<String>> getSystemConfigCategories() {
        List<String> categories = systemConfigService.getSystemConfigCategories();
        return ResponseEntity.ok(categories);
    }

    /**
     * Export system configuration to file
     */
    @GetMapping("/export")
    //@PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<byte[]> exportSystemConfig(@RequestParam(defaultValue = "json") String format) {
        byte[] exportData = systemConfigService.exportSystemConfig(format);
        return ResponseEntity.ok()
                .header("Content-Type", getContentTypeForFormat(format))
                .header("Content-Disposition", "attachment; filename=\"system-config." + format + "\"")
                .body(exportData);
    }

    /**
     * Import system configuration from file
     */
    @PostMapping("/import")
    //@PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<Map<String, Object>> importSystemConfig(
            @RequestParam("file") MultipartFile file,
            @RequestParam(defaultValue = "false") boolean overwrite) {
        Map<String, Object> importResult = systemConfigService.importSystemConfig(file, overwrite);
        return ResponseEntity.status(HttpStatus.CREATED).body(importResult);
    }

    private String getContentTypeForFormat(String format) {
        switch (format.toLowerCase()) {
            case "json":
                return "application/json";
            case "yaml":
                return "application/x-yaml";
            case "properties":
                return "text/plain";
            default:
                return "application/octet-stream";
        }
    }
}
