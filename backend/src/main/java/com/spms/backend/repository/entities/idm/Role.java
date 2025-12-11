package com.spms.backend.repository.entities.idm;

import com.spms.backend.repository.entities.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "spms_role")
public class Role extends BaseEntity {
    @Column(unique = true, nullable = false)
    private String name;

    private String description;

    @ElementCollection
    @CollectionTable(name = "spms_role_permissions", joinColumns = @JoinColumn(name = "role_id"))
    @Column(name = "permission")
    private Set<String> permissions = new HashSet<>();

    @ManyToMany
    @JoinTable(name = "spms_role_hierarchy",
        joinColumns = @JoinColumn(name = "child_id"),
        inverseJoinColumns = @JoinColumn(name = "parent_id"))
    private Set<Role> parentRoles = new HashSet<>();

    @ManyToMany(mappedBy = "parentRoles")
    private Set<Role> childRoles = new HashSet<>();

    // Audit trail fields
    private Boolean active;
    private Long lastModified;
    private String createdBy;
    private String updatedBy;
    private Long createdTime;

    public Role() {}
    
    public Role(String name) {
        this.name = name;
    }

    public Set<String> getAllPermissions() {
        Set<String> allPermissions = new HashSet<>(this.permissions);
        parentRoles.forEach(parent -> 
            allPermissions.addAll(parent.getAllPermissions()));
        return allPermissions;
    }
}
