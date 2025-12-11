package com.spms.backend.controller.dto.process;

import com.spms.backend.controller.dto.BaseDTO;
import com.spms.backend.service.model.process.FormVersionModel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 表单版本数据传输对象（DTO），用于在应用层和表示层之间传递表单版本信息。
 * 继承自 {@link BaseDTO}，提供基本的 DTO 功能。
 *
 * <p>
 * 包含以下字段：
 * <ul>
 * <li>{@code key} - 表单版本的唯一标识符，必须为小写字母、数字、连字符、点或下划线组成。</li>
 * <li>{@code version} - 表单版本号，必须符合语义化版本格式（如 "1.0.0"）。</li>
 * <li>{@code publishedDate} - 表单版本的发布时间戳。</li>
 * <li>{@code deprecated} - 标记表单版本是否已弃用。</li>
 * </ul>
 *
 * <p>
 * 提供以下静态方法：
 * <ul>
 * <li>{@link #convertToDTO(FormVersionModel)} - 将 {@link FormVersionModel} 转换为
 * {@link FormVersionDTO}。</li>
 * <li>{@link #convertToDTOList(List)} - 将 {@link FormVersionModel} 列表转换为
 * {@link FormVersionDTO} 列表。</li>
 * </ul>
 *
 * <p>
 * 内部辅助方法：
 * <ul>
 * <li>{@link #convertLongToVersionString(Long)} - 将长整型版本号转换为语义化版本字符串。</li>
 * </ul>
 */
@Setter
@Getter
@ToString
@EqualsAndHashCode(callSuper = false)
public class FormVersionDTO extends BaseDTO {
    @NotBlank(message = "Form Version key cannot be blank")
    @Pattern(regexp = "^[a-z0-9-._]+$", message = "Form key must be lowercase alphanumeric with hyphens, dots, or underscores")
    private String key;

    @NotBlank(message = "Version cannot be blank")
    @Pattern(regexp = "^(\\d+\\.){2}\\d+$", message = "Version must follow semantic versioning format")
    private String version;
    private Long publishedDate;  // Changed to Long to match model
    private boolean deprecated;
    private String name;
    private String description;
    private String schema;
    
    // Converts Model to DTO with null safety
    public static FormVersionDTO convertToDTO(FormVersionModel model) {
        if (model == null) return null;
        FormVersionDTO dto = new FormVersionDTO();
        dto.setKey(model.getKey());
        dto.setId(model.getId());
        dto.setVersion(convertLongToVersionString(model.getVersion())); // Convert Long to String
        dto.setPublishedDate(model.getPublishedDate());
        dto.setDeprecated(model.isDeprecated());
        dto.setName(model.getName());
        dto.setDescription(model.getDescription());
        dto.setSchema(model.getFormDefinition());
        return dto;
    }
    
    // Converts Model list to DTO list
    public static List<FormVersionDTO> convertToDTOList(List<FormVersionModel> models) {
        if (models == null) return Collections.emptyList();
        
        return models.stream()
            .map(FormVersionDTO::convertToDTO)
            .collect(Collectors.toList());
    }
    
    // Helper method to convert Long version to semantic version string
    private static String convertLongToVersionString(Long version) {
        if (version == null) return null;
        long major = version / (10000 * 10000);
        long minor = version / (10000) % 10000;
        long patch = version % 10000;
        return major + "." + minor + "." + patch;
    }
}
