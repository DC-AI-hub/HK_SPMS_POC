package com.spms.backend.repository.entities;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

/**
 * Base class for entities requiring audit fields (createdAt, updatedAt, etc.).
 * Extends BaseEntity to include the ID field.
 */
@MappedSuperclass
@Getter
@Setter
public abstract class AuditableEntity extends BaseEntity {
    
    /**
     * Timestamp when the entity was created (milliseconds since epoch).
     * Cannot be updated after initial creation.
     */
    @Column(nullable = false, updatable = false)
    private Long createdAt;
    
    /**
     * Timestamp when the entity was last updated (milliseconds since epoch).
     */
    @Column(nullable = false)
    private Long updatedAt;
    
    /**
     * ID of the user who created the entity.
     * Cannot be updated after initial creation.
     */
    @Column(name = "created_by", nullable = false, updatable = false)
    private Long createdById;
    
    /**
     * ID of the user who last updated the entity.
     */
    @Column(name = "updated_by", nullable = false)
    private Long updatedById;

    
}
