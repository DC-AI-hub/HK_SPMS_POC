package com.spms.backend.repository.entities.process;

import com.spms.backend.repository.entities.AuditableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents a specific version of a process definition.
 */
@Getter
@Setter
@Entity
@Table(name = "spms_process_version", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"key", "version"}, name = "spms_uk_process_version_key_version")
})
public class ProcessVersionEntity extends AuditableEntity {

    /**
     * The name of the process version.
     */
    @Column(nullable = false)
    private String name;

    /**
     * Description of the process version.
     */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * Unique key identifying the process version.
     */
    @Column(nullable = false)
    private String key;

    /**
     * Semantic version of the process (e.g., 1.0.0).
     */
    @Column(nullable = false)
    private String version;

    /**
     * BPMN XML definition of the process version.
     * Stored as a CLOB for large XML content.
     */
    @Lob
    @Column(nullable = false)
    @Basic(fetch = FetchType.LAZY)
    private String bpmnXml;

    /**
     * Current status of the process version.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProcessVersionStatus status;

    /**
     * Indicates if this version has been deployed to Flowable.
     */
    @Column(nullable = false)
    private Boolean deployedToFlowable = false;

    /**
     * Flowable's internal definition ID after deployment.
     */
    @Column
    private String flowableDefinitionId;

    /**
     * Flowable's deployment ID for this version.
     */
    @Column
    private String flowableDeploymentId;

    /**
     * The process definition this version belongs to.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "process_definition_id")
    private ProcessDefinitionEntity processDefinition;

    /**
     * The form version associated with this process version.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "form_version_id")
    private FormVersionEntity formVersion;
}

