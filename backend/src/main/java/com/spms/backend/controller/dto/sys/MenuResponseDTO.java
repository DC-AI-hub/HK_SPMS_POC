package com.spms.backend.controller.dto.sys;

import com.spms.backend.service.model.MenuModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * DTO for menu response data
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuResponseDTO {

    private Long id;
    private String title;
    private String path;
    private String icon;
    private Integer displayOrder;
    private Boolean active;
    private Long parentMenuId;
    private String createdBy;
    private String modifiedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Set<Long> allowedRoleIds;

    /**
     * Convert from MenuModel to MenuResponseDTO
     */
    public static MenuResponseDTO fromMenuModel(MenuModel menuModel) {
        return MenuResponseDTO.builder()
                .id(menuModel.getId())
                .title(menuModel.getTitle())
                .path(menuModel.getPath())
                .icon(menuModel.getIcon())
                .displayOrder(menuModel.getDisplayOrder())
                .active(menuModel.getActive())
                .parentMenuId(menuModel.getParentMenuId())
                .createdBy(menuModel.getCreatedBy())
                .modifiedBy(menuModel.getModifiedBy())
                .createdAt(menuModel.getCreatedAt())
                .updatedAt(menuModel.getUpdatedAt())
                .allowedRoleIds(menuModel.getAllowedRoleIds())
                .build();
    }
}
