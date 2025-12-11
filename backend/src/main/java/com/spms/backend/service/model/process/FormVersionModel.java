package com.spms.backend.service.model.process;

import com.spms.backend.repository.entities.process.FormVersionEntity;
import com.spms.backend.service.BaseModel;

/**
 * 表示表单版本的模型类，继承自 BaseModel<FormVersionEntity>。
 * 用于封装表单版本的相关信息，包括键、版本号、发布时间、是否弃用等。
 */
public class FormVersionModel extends BaseModel<FormVersionEntity> {
    /**
     * 表单版本的唯一键。
     */
    private String key;

    /**
     * 表单版本的版本号（长整型表示）。
     */
    private Long version;

    /**
     * 表单版本的发布时间（Unix 时间戳，毫秒）。
     */
    private Long publishedDate;

    /**
     * 标记表单版本是否已弃用。
     */
    private boolean deprecated;

    /**
     * 表单的定义内容（JSON 或其他格式的字符串）。
     */
    private String formDefinition;

    /**
     * 表单版本的名称。
     */
    private String name;

    /**
     * 表单版本的描述信息。
     */
    private String description;

    /**
     * 表单版本的部署 ID。
     */
    private String deploymentId;

    /**
     * 获取表单版本的唯一键。
     * 
     * @return 表单版本的键。
     */
    public String getKey() {
        return key;
    }

    /**
     * 设置表单版本的唯一键。
     * 
     * @param key 表单版本的键。
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * 获取表单版本的版本号。
     * 
     * @return 表单版本的版本号（长整型）。
     */
    public Long getVersion() {
        return version;
    }

    /**
     * 设置表单版本的版本号。
     * 
     * @param version 表单版本的版本号（长整型）。
     */
    public void setVersion(Long version) {
        this.version = version;
    }

    /**
     * 获取表单版本的发布时间。
     * 
     * @return 表单版本的发布时间（Unix 时间戳，毫秒）。
     */
    public Long getPublishedDate() {
        return publishedDate;
    }

    /**
     * 设置表单版本的发布时间。
     * 
     * @param publishedDate 表单版本的发布时间（Unix 时间戳，毫秒）。
     */
    public void setPublishedDate(Long publishedDate) {
        this.publishedDate = publishedDate;
    }

    /**
     * 检查表单版本是否已弃用。
     * 
     * @return 如果表单版本已弃用，返回 true；否则返回 false。
     */
    public boolean isDeprecated() {
        return deprecated;
    }

    /**
     * 设置表单版本是否弃用。
     * 
     * @param deprecated 标记表单版本是否弃用。
     */
    public void setDeprecated(boolean deprecated) {
        this.deprecated = deprecated;
    }

    /**
     * 获取表单的定义内容。
     * 
     * @return 表单的定义内容（字符串形式）。
     */
    public String getFormDefinition() {
        return formDefinition;
    }

    /**
     * 设置表单的定义内容。
     * 
     * @param formDefinition 表单的定义内容（字符串形式）。
     */
    public void setFormDefinition(String formDefinition) {
        this.formDefinition = formDefinition;
    }

    /**
     * 获取表单版本的名称。
     * 
     * @return 表单版本的名称。
     */
    public String getName() {
        return name;
    }

    /**
     * 设置表单版本的名称。
     * 
     * @param name 表单版本的名称。
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取表单版本的描述信息。
     * 
     * @return 表单版本的描述信息。
     */
    public String getDescription() {
        return description;
    }

    /**
     * 设置表单版本的描述信息。
     * 
     * @param description 表单版本的描述信息。
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * 获取表单版本的部署 ID。
     * 
     * @return 表单版本的部署 ID。
     */
    public String getDeploymentId() {
        return deploymentId;
    }

    /**
     * 设置表单版本的部署 ID。
     * 
     * @param deploymentId 表单版本的部署 ID。
     */
    public void setDeploymentId(String deploymentId) {
        this.deploymentId = deploymentId;
    }

    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 将 FormVersionEntity 转换为 FormVersionModel。
     * 
     * @param entity 表单版本的实体对象。
     * @return 转换后的 FormVersionModel 对象。
     */
    public static FormVersionModel fromEntity(FormVersionEntity entity) {
        FormVersionModel model = new FormVersionModel();
        model.setId(entity.getId());
        model.setKey(entity.getKey());
        model.setVersion(entity.getVersion());
        model.setPublishedDate(entity.getPublishedDate());
        model.setDeprecated(entity.isDeprecated());
        model.setFormDefinition(entity.getFormDefinition());
        model.setName(entity.getName());
        model.setDescription(entity.getDescription());
        model.setDeploymentId(entity.getDeploymentId());
        return model;
    }

    /**
     * 将 FormVersionModel 转换为 FormVersionEntity。
     * 
     * @param model 表单版本的模型对象。
     * @return 转换后的 FormVersionEntity 对象。
     */
    public static FormVersionEntity toEntity(FormVersionModel model) {
        FormVersionEntity entity = new FormVersionEntity();
        entity.setKey(model.getKey());
        entity.setVersion(model.getVersion());
        entity.setPublishedDate(model.getPublishedDate());
        entity.setDeprecated(model.isDeprecated());
        entity.setFormDefinition(model.getFormDefinition());
        entity.setName(model.getName());
        entity.setDescription(model.getDescription());
        entity.setDeploymentId(model.getDeploymentId());
        return entity;
    }

    /**
     * 将版本字符串转换为长整型表示。
     * 
     * @param version 版本字符串（格式为 "major.minor.patch"）。
     * @return 版本的长整型表示。
     * @throws IllegalArgumentException 如果版本字符串格式无效。
     */
    public static Long convertVersionStringToLong(String version) {
        try {
            String[] parts = version.split("\\.");
            long major = Long.parseLong(parts[0]);
            long minor = parts.length > 1 ? Long.parseLong(parts[1]) : 0;
            long patch = parts.length > 2 ? Long.parseLong(parts[2]) : 0;
            return (major * 10000 * 10000) + (minor * 10000) + patch;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid version format: " + version);
        }
    }

    /**
     * 将长整型版本号转换为字符串表示。
     * 
     * @param version 长整型版本号。
     * @return 版本字符串（格式为 "major.minor.patch"）。
     */
    public static String convertVersionLongToString(Long version) {
        if (version == null)
            return null;
        long major = version / (10000 * 10000);
        long remainder = version % (10000 * 10000);
        long minor = remainder / 10000;
        long patch = remainder % 10000;
        return major + "." + minor + "." + patch;
    }

    /**
     * 将当前模型转换为用于更新的实体对象。
     * 
     * @return 转换后的 FormVersionEntity 对象。
     */
    @Override
    public FormVersionEntity toEntityForUpdate() {
        return toEntity(this);
    }

    /**
     * 将当前模型转换为用于创建的实体对象。
     * 
     * @return 转换后的 FormVersionEntity 对象。
     */
    @Override
    public FormVersionEntity toEntityForCreate() {
        return toEntityForUpdate();
    }
}
