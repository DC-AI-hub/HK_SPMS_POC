package com.spms.backend.service.model.idm;

import com.spms.backend.repository.entities.idm.Role;
import com.spms.backend.service.model.BaseModel;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Model class representing a Role entity.
 */
@Getter
@Setter
public class RoleModel extends BaseModel<Role> {

    private Long id;
    private String name;
    private String description;
    private Set<String> permissions;
    private Set<RoleModel> parentRoles;
    private Set<RoleModel> childRoles;

    // Audit trail fields from Company
    private Boolean active;
    private Long lastModified;
    private String createdBy;
    private String updatedBy;
    private Long createdTime;

    @Override
    public Role toEntityForCreate() {
        Role role = toEntityForUpdate();
        role.setCreatedBy(createdBy);
        role.setId(null);
        role.setUpdatedBy(updatedBy);
        role.setLastModified(new Date().getTime());
        role.setCreatedTime(new Date().getTime());
        return role;
    }

    @Override
    public Role toEntityForUpdate() {
        Role role = new Role(name);
        role.setId(id);
        role.setDescription(description);
        role.setPermissions(permissions);
        return role;
    }

    public static RoleModel fromEntity(Role role, List<Role> parent, List<Role> children) {
        RoleModel model = new RoleModel();
        model.setId(role.getId());
        model.setName(role.getName());
        model.setDescription(role.getDescription());
        model.setPermissions(role.getPermissions());

        // Audit trail fields from Company
        model.setActive(role.getActive());
        model.setLastModified(role.getLastModified());
        model.setCreatedBy(role.getCreatedBy());
        model.setUpdatedBy(role.getUpdatedBy());
        model.setCreatedTime(role.getCreatedTime());

        model.setParentRoles(parent.stream().map(RoleModel::fromEntity).collect(Collectors.toSet()));
        model.setChildRoles(children.stream().map(RoleModel::fromEntity).collect(Collectors.toSet()));

        return model;

    }


    public static RoleModel fromEntity(Role role) {
        RoleModel model = new RoleModel();
        model.setId(role.getId());
        model.setName(role.getName());
        model.setDescription(role.getDescription());
        model.setPermissions(role.getPermissions());

        // Audit trail fields from Company
        model.setActive(role.getActive());
        model.setLastModified(role.getLastModified());
        model.setCreatedBy(role.getCreatedBy());
        model.setUpdatedBy(role.getUpdatedBy());
        model.setCreatedTime(role.getCreatedTime());

        return model;
    }

}
