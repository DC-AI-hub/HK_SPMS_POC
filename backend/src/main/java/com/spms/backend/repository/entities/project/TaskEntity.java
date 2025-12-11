package com.spms.backend.repository.entities.project;

import com.spms.backend.repository.entities.AuditableEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Map;

/**
 * 任务实体类
 * 用于存储项目任务信息，每个任务都归属于一个项目
 */
@Entity
@Table(name = "spms_task")
@Data
@EqualsAndHashCode(callSuper = true)
public class TaskEntity extends AuditableEntity {

    /**
     * 任务编号，唯一标识
     */
    @Column(name = "task_number", unique = true, nullable = false)
    @NotBlank(message = "任务编号不能为空")
    @Size(max = 50, message = "任务编号长度不能超过50个字符")
    private String taskNumber;

    /**
     * 任务名称
     */
    @Column(name = "task_name", nullable = false)
    @NotBlank(message = "任务名称不能为空")
    @Size(max = 200, message = "任务名称长度不能超过200个字符")
    private String taskName;

    /**
     * 任务描述
     */
    @Column(name = "description", columnDefinition = "TEXT")
    @Size(max = 1000, message = "任务描述长度不能超过1000个字符")
    private String description;

    /**
     * 所属项目
     * 每个任务必须归属于一个项目
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    @NotNull(message = "任务必须归属于一个项目")
    private ProjectEntity project;

    // ========== 扩展字段（与数据库迁移脚本匹配） ==========
    
    /**
     * 自定义字段存储（JSONB格式，PostgreSQL专用）
     * 用于存储生产环境中的特殊字段，避免频繁表结构变更
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "custom_fields", columnDefinition = "JSONB")
    private Map<String, Object> customFields;

    /**
     * 任务活动（单值或以分隔符分隔的多值），来自列 activity VARCHAR(255)
     */
    @Column(name = "activity", length = 255)
    private String activity;

    /**
     * 外部系统任务ID（用于与现有系统集成）
     */
    @Column(name = "external_task_id", length = 100)
    private String externalTaskId;

    /**
     * 是否激活（软删除标记）
     */
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
}
