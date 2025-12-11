package com.spms.backend.repository.entities.process;

import com.spms.backend.repository.entities.AuditableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Represents a process definition in the system.
 * A process definition serves as a template for process instances.
 */
@Getter
@Setter
@Entity
@Table(name = ProcessDefinitionEntity.TABLE_NAME)
public class ProcessDefinitionEntity extends AuditableEntity {

    public static final String TABLE_NAME = "spms_process_def";

    /**
     * The name of the process definition.
     */
    @Column(nullable = false)
    private String name;

    /**
     * Unique key identifying the process definition.
     */
    @Column(nullable = false)
    private String key;

    /**
     * Description of the process definition.
     */
    @Column()
    private String description;

    /**
     * ID of the user who owns this process definition.
     */
    @Column(name = "owner_id")
    private Long ownerId;

    /**
     * ID of the business owner responsible for this process.
     */
    @Column(name = "business_owner_id")
    private Long businessOwnerId;

    /**
     * List of versions associated with this process definition.
     * Uses bidirectional mapping with orphan removal.
     */
    @OneToMany(
        fetch = FetchType.LAZY,
        cascade = CascadeType.ALL,
        orphanRemoval = true,
        mappedBy = "processDefinition"
    )
    private List<ProcessVersionEntity> versions;

}
