package com.spms.backend.controller.dto.idm;

import com.spms.backend.repository.entities.idm.DivisionType;
import com.spms.backend.service.model.idm.DivisionModel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for Division operations
 */
@Data
public class DivisionDTO {
    private Long id;

    @NotBlank(message = "Division name is required")
    @Size(max = 100, message = "Division name must be less than 100 characters")
    private String name;

    @NotNull(message = "Division type is required")
    private DivisionType type;

    @Size(max = 500, message = "Description must be less than 500 characters")
    private String description;

    @NotNull(message = "Active status is required")
    private Boolean active;

    private LocalDateTime lastModified;

    @NotNull(message = "Company ID is required")
    private Long companyId;

    private Long divisionHeadId;

    public static DivisionDTO fromDivisionModel(DivisionModel model) {
        if (model == null) {
            return null;
        }

        DivisionDTO dto = new DivisionDTO();
        dto.setId(model.getId());
        dto.setName(model.getName());
        dto.setType(model.getType() != null ? model.getType() : null);
        dto.setDescription(model.getDescription());
        dto.setActive(model.getActive());
        dto.setLastModified(model.getLastModified());
        dto.setCompanyId(model.getCompanyId());
        dto.setDivisionHeadId(model.getDivisionHeadId());
        return dto;
    }

    public DivisionModel toDivisionModel() {
        DivisionModel model = new DivisionModel();
        model.setId(this.id);
        model.setName(this.name != null ? this.name.trim() : null);
        model.setType(this.type != null ? this.type : null);
        model.setDescription(this.description != null ? this.description.trim() : null);
        model.setActive(this.active);
        model.setLastModified(this.lastModified);
        model.setCompanyId(this.companyId);
        model.setDivisionHeadId(this.divisionHeadId);
        return model;
    }
}
