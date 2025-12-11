package com.spms.backend.controller.dto.idm;

import com.spms.backend.service.model.idm.RoleModel;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
public class RoleDTO {
    private Long id;
    private String name;
    private String description;
    private Set<String> permissions;
    private Set<RoleDTO> parentRoles;
    private Set<RoleDTO> childRoles;
    
    // Audit trail fields
    private Boolean active;
    private Long lastModified;
    private String createdBy;
    private String updatedBy;
    private Long createdTime;

    public static RoleDTO fromRoleModel(RoleModel model) {
        RoleDTO dto = new RoleDTO();
        dto.setId(model.getId());
        dto.setName(model.getName());
        dto.setDescription(model.getDescription());
        dto.setPermissions(model.getPermissions());
        
        // Convert parent roles
        if (model.getParentRoles() != null) {
            dto.setParentRoles(model.getParentRoles().stream()
                .map(RoleDTO::fromRoleModel)
                .collect(java.util.stream.Collectors.toSet()));
        }
        
        // Convert child roles
        if (model.getChildRoles() != null) {
            dto.setChildRoles(model.getChildRoles().stream()
                .map(RoleDTO::fromRoleModel)
                .collect(java.util.stream.Collectors.toSet()));
        }
        
        // Audit trail fields
        dto.setActive(model.getActive());
        dto.setLastModified(model.getLastModified());
        dto.setCreatedBy(model.getCreatedBy());
        dto.setUpdatedBy(model.getUpdatedBy());
        dto.setCreatedTime(model.getCreatedTime());
        
        return dto;
    }

    public RoleModel toRoleModel() {
        RoleModel model = new RoleModel();
        model.setId(this.id);
        model.setName(this.name);
        model.setDescription(this.description);
        model.setPermissions(this.permissions);
        
        // Convert parent roles
        if (this.parentRoles != null) {
            model.setParentRoles(this.parentRoles.stream()
                .map(RoleDTO::toRoleModel)
                .collect(java.util.stream.Collectors.toSet()));
        }
        
        // Convert child roles
        if (this.childRoles != null) {
            model.setChildRoles(this.childRoles.stream()
                .map(RoleDTO::toRoleModel)
                .collect(java.util.stream.Collectors.toSet()));
        }
        
        // Audit trail fields
        model.setActive(this.active);
        model.setLastModified(this.lastModified);
        model.setCreatedBy(this.createdBy);
        model.setUpdatedBy(this.updatedBy);
        model.setCreatedTime(this.createdTime);
        
        return model;
    }
}
