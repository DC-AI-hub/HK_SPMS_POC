package com.spms.backend.repository.entities.idm;

import com.spms.backend.repository.entities.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Entity representing a company in the system.
 * Contains company details and related information.
 */
@Entity
@Table(name = "spms_company")
@Getter
@Setter
public class Company extends BaseEntity {

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

    @Column(unique = true, nullable = false)
    private String name;

    @Column()
    private String description;

    @ElementCollection
    @CollectionTable(name = "spms_company_language_tags",
            joinColumns = @JoinColumn(name = "company_id"))
    @MapKeyColumn(name = "language")
    @Column(name = "value")
    private Map<String, String> languageTags;

    @ElementCollection
    @CollectionTable(name = "spms_company_profiles",
            joinColumns = @JoinColumn(name = "company_id"))
    @MapKeyColumn(name = "key")
    @Column(name = "value")
    private Map<String, String> companyProfiles;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CompanyType companyType;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Company parent;
    
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private Set<Company> children = new HashSet<>();

    @Column(name = "division_head_id")
    private Long divisionHeadId;

    @Column(name = "department_head_id")
    private Long departmentHeadId;

    /**
     * This field will store the flow Engine group ID.
     */
    @Column
    private String engineId;

}
