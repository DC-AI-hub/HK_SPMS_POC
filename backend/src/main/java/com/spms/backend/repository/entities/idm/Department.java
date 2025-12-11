package com.spms.backend.repository.entities.idm;

import com.spms.backend.repository.entities.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

@Entity
@Table(name = "spms_department")
@Getter
@Setter
public class Department extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_head_id")
    private User departmentHead;

    @Column(nullable = false, unique = true)
    private String name;

    @ElementCollection
    @CollectionTable(name = "spms_department_tags",
            joinColumns = @JoinColumn(name = "department_id"))
    @MapKeyColumn(name = "key")
    @Column(name = "value")
    private Map<String, String> tags;

    @Column(nullable = false)
    private Long parent; // Can be companyId or divisionId

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private DepartmentType type;

    @Column(nullable = false)
    private Integer level;

    @Column(nullable = false)
    private Boolean active = true;

    @ManyToMany
    @JoinTable(
            name = "spms_department_user",
            joinColumns = @JoinColumn(name = "department_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<User> users;

    @Column
    private String createdBy;

    @Column
    private String updatedBy;

    @Column
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }


}
