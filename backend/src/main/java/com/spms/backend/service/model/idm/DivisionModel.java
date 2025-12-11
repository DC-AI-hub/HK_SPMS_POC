package com.spms.backend.service.model.idm;

import com.spms.backend.repository.entities.idm.Division;
import com.spms.backend.repository.entities.idm.User;
import com.spms.backend.repository.entities.idm.DivisionType;
import com.spms.backend.service.BaseModel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class DivisionModel extends BaseModel<Division> {

    private Long id;
    private Boolean active;
    private LocalDateTime lastModified;

    @NotBlank(message = "Division name is required")
    @Size(max = 100, message = "Division name must be less than 100 characters")
    private String name;

    @NotNull(message = "Division type is required")
    private DivisionType type;

    @NotNull(message = "Company ID is required")
    private Long companyId;

    private String createdBy;
    private String updatedBy;
    private LocalDateTime createdTime;

    @Size(max = 500, message = "Description must be less than 500 characters")
    private String description;

    private Long divisionHeadId;

    @Override
    public Division toEntityForCreate() {
        Division division = toEntityForUpdate();
        division.setCreatedBy(createdBy);
        division.setId(null);
        return division;
    }

    @Override
    public Division toEntityForUpdate() {
        Division division = new Division();
        division.setId(id);
        division.setActive(active);
        division.setName(name);
        division.setType(this.type != null ? this.type : null);
        division.setDescription(this.description != null ? this.description.trim() : null);
        division.setUpdatedBy(updatedBy);
        if (divisionHeadId != null) {
            division.setDivisionHead(new User());
            division.getDivisionHead().setId(divisionHeadId);
        }
        return division;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description != null ? description.trim() : null;
    }

    public static DivisionModel fromEntity(Division division) {
        DivisionModel model = new DivisionModel();
        model.setId(division.getId());
        model.setActive(division.getActive());
        model.setName(division.getName());
        model.setType(division.getType() != null ? division.getType() : null);
        model.setCompanyId(division.getCompany().getId());
        model.setLastModified(division.getLastModified());
        model.setUpdatedBy(division.getUpdatedBy());
        model.setCreatedBy(division.getCreatedBy());
        model.setCreatedTime(division.getCreatedTime());
        if (division.getDivisionHead() != null) {
            model.setDivisionHeadId(division.getDivisionHead().getId());
        }
        return model;
    }
}
