package com.spms.backend.service.model;

import com.spms.backend.repository.entities.sys.SystemConfigEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemConfigModel {

    private Long id;

    @NotBlank(message = "Configuration key is required")
    @Size(min = 1, max = 255, message = "Key must be between 1 and 255 characters")
    private String key;

    @NotBlank(message = "Configuration value is required")
    private String value;

    @NotBlank(message = "Data type is required")
    private String dataType;

    private String description;

    private String category;

    private Boolean isReadOnly = false;

    private String defaultValue;

    private String validationRules;

    private String createdBy;
    private String modifiedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Conversion methods
    public static SystemConfigModel fromEntity(SystemConfigEntity entity) {
        return SystemConfigModel.builder()
                .id(entity.getId())
                .key(entity.getKey())
                .value(entity.getValue())
                .dataType(entity.getDataType())
                .description(entity.getDescription())
                .category(entity.getCategory())
                .isReadOnly(entity.getIsReadOnly())
                .defaultValue(entity.getDefaultValue())
                .validationRules(entity.getValidationRules())
                .createdBy(entity.getCreatedBy())
                .modifiedBy(entity.getModifiedBy())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public SystemConfigEntity toEntity() {
        SystemConfigEntity entity = new SystemConfigEntity();
        entity.setId(this.id);
        entity.setKey(this.key);
        entity.setValue(this.value);
        entity.setDataType(this.dataType);
        entity.setDescription(this.description);
        entity.setCategory(this.category);
        entity.setIsReadOnly(this.isReadOnly);
        entity.setDefaultValue(this.defaultValue);
        entity.setValidationRules(this.validationRules);
        entity.setCreatedBy(this.createdBy);
        entity.setModifiedBy(this.modifiedBy);
        entity.setCreatedAt(this.createdAt);
        entity.setUpdatedAt(this.updatedAt);
        return entity;
    }
}
