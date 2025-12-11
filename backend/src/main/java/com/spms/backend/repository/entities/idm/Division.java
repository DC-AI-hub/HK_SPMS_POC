package com.spms.backend.repository.entities.idm;

import com.spms.backend.repository.entities.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Entity representing a division within a company.
 */
@Entity
@Table(name = "spms_division")
@Getter
@Setter
public class Division extends BaseEntity {

    @Column(nullable = false)
    private Boolean active = true;

    @UpdateTimestamp
    @Column
    private LocalDateTime lastModified;

    @CreationTimestamp
    @Column
    private LocalDateTime createdTime;

    @Column
    private String createdBy;

    @Column
    private String updatedBy;

    @Column(unique = true, nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DivisionType type;

    @ManyToOne
    @JoinColumn(columnDefinition = "companyId",referencedColumnName = "id")
    private Company company;

    @Column(length = 500)
    private String description;


    @ManyToOne
    @JoinColumn(name = "division_head_id")
    private User divisionHead;

}
