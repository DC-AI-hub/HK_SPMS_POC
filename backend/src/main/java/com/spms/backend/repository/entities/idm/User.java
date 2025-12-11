package com.spms.backend.repository.entities.idm;

import com.spms.backend.repository.entities.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Getter
@Setter
@Table(name = "spms_user")
public class User extends BaseEntity {

    public enum UserType {
        STAFF,
        VENDOR,
        MACHINE;

        public static UserType of(String userType) {
            return Arrays.stream(values()).filter(x -> x.name().equals(userType)).findFirst().orElse(STAFF);
        }
    }

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String provider;
    /**
     * Because of the provider Will assigned to when in the case of user login and combine to
     */
    @Column
    private String providerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserType type;

    @Column()
    private String description;

    @ElementCollection
    @CollectionTable(name = "spms_user_profiles",
            joinColumns = @JoinColumn(name = "user_id"))
    @MapKeyColumn(name = "key")
    @Column(name = "value")
    private Map<String, String> userProfiles;

    @ManyToMany
    @JoinTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    @ManyToMany(mappedBy = "users", fetch = FetchType.LAZY)
    private Set<Department> departments = new HashSet<>();

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String modifiedBy;

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
