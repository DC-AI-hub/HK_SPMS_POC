package com.spms.backend.repository.entities.project;

import com.spms.backend.repository.entities.AuditableEntity;
import com.spms.backend.repository.entities.idm.User;
import com.spms.backend.model.ProjectStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

/**
 * 项目实体类
 * 用于存储项目的基本信息，支持动态扩展字段
 */
@Entity
@Table(name = "spms_project")
@Data
@EqualsAndHashCode(callSuper = true)
public class ProjectEntity extends AuditableEntity {

    /**
     * 项目代码，唯一标识
     */
    @Column(name = "project_code", unique = true, nullable = false)
    @NotBlank(message = "项目代码不能为空")
    @Size(max = 50, message = "项目代码长度不能超过50个字符")
    private String projectCode;

    /**
     * 项目名称
     */
    @Column(name = "project_name", nullable = false)
    @NotBlank(message = "项目名称不能为空")
    @Size(max = 200, message = "项目名称长度不能超过200个字符")
    private String projectName;

    /**
     * 项目描述
     */
    @Column(name = "description", columnDefinition = "TEXT")
    @Size(max = 1000, message = "项目描述长度不能超过1000个字符")
    private String description;

    /**
     * 项目经理
     * 关联到用户表
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id")
    private User projectManager;

    /**
     * 项目状态
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ProjectStatus status = ProjectStatus.ACTIVE;

    // ========== 预留扩展字段（根据生产数据可能需要） ==========
    
    /**
     * 项目预算
     */
    @Column(name = "budget", precision = 15, scale = 2)
    private BigDecimal budget;

    /**
     * 项目开始日期
     */
    @Column(name = "start_date")
    private LocalDate startDate;

    /**
     * 项目结束日期
     */
    @Column(name = "end_date")
    private LocalDate endDate;

    /**
     * 项目优先级
     */
    @Column(name = "priority")
    private Integer priority;

    /**
     * 项目类型（可能的生产环境字段）
     */
    @Column(name = "project_type", length = 50)
    private String projectType;

    /**
     * 客户ID（如果项目与客户关联）
     */
    @Column(name = "client_id")
    private Long clientId;

    /**
     * 自定义字段存储（JSONB格式，PostgreSQL专用）
     * 用于存储生产环境中的特殊字段，避免频繁表结构变更
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "custom_fields", columnDefinition = "JSONB")
    private Map<String, Object> customFields;

    /**
     * 外部系统项目ID（用于与现有系统集成）
     */
    @Column(name = "external_project_id", length = 100)
    private String externalProjectId;

    /**
     * 项目标签（多个标签用逗号分隔）
     */
    @Column(name = "tags", length = 500)
    private String tags;

    /**
     * 是否激活（软删除标记）
     */
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
}