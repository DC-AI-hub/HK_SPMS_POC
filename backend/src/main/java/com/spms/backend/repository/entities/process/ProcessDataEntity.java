package com.spms.backend.repository.entities.process;

import com.spms.backend.repository.entities.AuditableEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

/**
 * 统一的流程数据实体
 */
@Entity
@Table(name = "spms_process_data")
@Data
@EqualsAndHashCode(callSuper = true)
public class ProcessDataEntity extends AuditableEntity {

    /**
     * 流程实例ID
     */
    @Column(name = "process_instance_id", nullable = false, unique = true)
    private String processInstanceId;

    /**
     * 用户ID
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /**
     * 流程定义键
     */
    @Column(name = "process_definition_key", nullable = false)
    private String processDefinitionKey;

    /**
     * 表单类型
     */
    @Column(name = "form_type", nullable = false)
    private String formType;

    /**
     * 表单数据（JSON格式）
     */
    @Column(name = "form_data", nullable = false)
    private String formData;

    /**
     * 业务数据（JSON格式）
     */
    @Column(name = "business_data")

    private String businessData;

    /**
     * 状态
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProcessDataStatus status = ProcessDataStatus.DRAFT;

    /**
     * 审批意见
     */
    @Column(name = "approval_comment")
    private String approvalComment;

    /**
     * 版本号（用于乐观锁）
     */
    @Version
    private Integer version = 0;

    /**
     * 流程数据状态枚举
     */
    public enum ProcessDataStatus {
        DRAFT("草稿"),
        SUBMITTED("已提交"),
        APPROVED("已批准"),
        REJECTED("已拒绝"),
        CANCELLED("已取消");

        private final String description;

        ProcessDataStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}
