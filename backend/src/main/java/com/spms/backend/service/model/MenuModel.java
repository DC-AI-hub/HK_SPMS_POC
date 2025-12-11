package com.spms.backend.service.model;

import com.spms.backend.repository.entities.sys.MenuEntity;
import com.spms.backend.repository.entities.idm.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuModel {

    private Long id;

    @NotBlank(message = "Menu title is required")
    @Size(min = 1, max = 100, message = "Title must be between 1 and 100 characters")
    private String title;

    @NotBlank(message = "Menu path is required")
    private String path;

    private String icon;

    @Min(value = 0, message = "Display order must be non-negative")
    private Integer displayOrder;

    private Boolean active;

    private Long parentMenuId;

    private String createdBy;
    private String modifiedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Set<Long> allowedRoleIds = new HashSet<>();

    // Conversion methods
    public static MenuModel fromEntity(MenuEntity entity) {
        return MenuModel.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .path(entity.getPath())
                .icon(entity.getIcon())
                .displayOrder(entity.getDisplayOrder())
                .active(entity.getActive())
                .parentMenuId(entity.getParentMenu() != null ? entity.getParentMenu().getId() : null)
                .createdBy(entity.getCreatedBy())
                .modifiedBy(entity.getModifiedBy())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .allowedRoleIds(entity.getAllowedRoles().stream()
                        .map(Role::getId)
                        .collect(Collectors.toSet()))
                .build();
    }

    public MenuEntity toEntity() {
        MenuEntity entity = new MenuEntity();
        entity.setId(this.id);
        entity.setTitle(this.title);
        entity.setPath(this.path);
        entity.setIcon(this.icon);
        entity.setDisplayOrder(this.displayOrder);
        entity.setActive(this.active);
        entity.setCreatedBy(this.createdBy);
        entity.setModifiedBy(this.modifiedBy);
        entity.setCreatedAt(this.createdAt);
        entity.setUpdatedAt(this.updatedAt);
        // parentMenu and allowedRoles will be set separately in service layer
        return entity;
    }
}
