package com.spms.backend.controller.dto.sys;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;

/**
 * DTO for creating and updating menu items
 */
@Getter
@AllArgsConstructor
public class MenuRequestDTO {

    @NotBlank(message = "Menu title is required")
    @Size(min = 1, max = 100, message = "Title must be between 1 and 100 characters")
    private final String title;

    @NotBlank(message = "Menu path is required")
    private final String path;

    private final String icon;

    @Min(value = 0, message = "Display order must be non-negative")
    private final Integer displayOrder;

    private final Boolean active;

    private final Long parentMenuId;

    private final Set<Long> allowedRoleIds;

    /**
     * Convert to MenuModel for service layer processing
     */
    public com.spms.backend.service.model.MenuModel toMenuModel() {
        return com.spms.backend.service.model.MenuModel.builder()
                .title(this.title)
                .path(this.path)
                .icon(this.icon)
                .displayOrder(this.displayOrder)
                .active(this.active)
                .parentMenuId(this.parentMenuId)
                .allowedRoleIds(this.allowedRoleIds)
                .build();
    }
}
