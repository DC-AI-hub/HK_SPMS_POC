package com.spms.backend.controller.dto.process;

import com.spms.backend.controller.dto.BaseDTO;
import com.spms.backend.service.model.process.FormDefinitionModel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;



@Setter
@Getter
@ToString
@EqualsAndHashCode(callSuper = false)
public class FormDefinitionDTO extends BaseDTO {
    @NotBlank(message = "Form key cannot be blank")
    @Pattern(regexp = "^[a-z0-9-._]+$", message = "Form key must be lowercase alphanumeric with hyphens, dots, or underscores")
    private String key;
    
    @NotBlank(message = "name must not be blank")
    private String name;
    
    private String schema;  // Changed from Map to String
    
    @Pattern(regexp = "^(\\d+\\.){2}\\d+$", message = "Version must follow semantic versioning format")
    private String version;
    
    private String description;
    
    // Converts DTO to Model with null safety
    public static FormDefinitionModel convertToModel(FormDefinitionDTO dto) {
        if (dto == null) return null;
        
        FormDefinitionModel model = new FormDefinitionModel();
        model.setDefinition(dto.getSchema());
        model.setName(dto.getName());
        model.setDescription(dto.getDescription());
        model.setVersion(dto.getVersion());  // 修复：使用getter方法而不是直接访问字段
        return model;
    }
    
    // Converts Model to DTO with null safety
    public static FormDefinitionDTO convertToDTO(FormDefinitionModel model) {
        if (model == null) return null;
        
        FormDefinitionDTO dto = new FormDefinitionDTO();
        dto.setSchema(model.getDefinition());
        dto.setName(model.getName());
        dto.setDescription(model.getDescription());
        dto.setVersion(model.getVersion());  // 添加：设置version字段
        return dto;
    }
}
