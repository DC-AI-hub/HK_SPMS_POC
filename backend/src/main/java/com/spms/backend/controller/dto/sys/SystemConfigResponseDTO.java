package com.spms.backend.controller.dto.sys;

import com.spms.backend.service.model.SystemConfigModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for system configuration response data
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemConfigResponseDTO {

    private Long id;
    private String key;
    private String value;
    private String dataType;
    private String description;
    private String category;
    private Boolean isReadOnly;
    private String defaultValue;
    private String validationRules;
    private String createdBy;
    private String modifiedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Convert from SystemConfigModel to SystemConfigResponseDTO
     */
    public static SystemConfigResponseDTO fromSystemConfigModel(SystemConfigModel model) {
        return SystemConfigResponseDTO.builder()
                .id(model.getId())
                .key(model.getKey())
                .value(model.getValue())
                .dataType(model.getDataType())
                .description(model.getDescription())
                .category(model.getCategory())
                .isReadOnly(model.getIsReadOnly())
                .defaultValue(model.getDefaultValue())
                .validationRules(model.getValidationRules())
                .createdBy(model.getCreatedBy())
                .modifiedBy(model.getModifiedBy())
                .createdAt(model.getCreatedAt())
                .updatedAt(model.getUpdatedAt())
                .build();
    }
}
