package com.spms.backend.controller.dto.idm;

import com.spms.backend.repository.entities.idm.CompanyType;
import com.spms.backend.service.model.idm.CompanyModel;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
public class CompanyDTO {

    private Long id;
    private Boolean active;
    private String name;
    private String description;
    private Map<String, String> languageTags;
    private CompanyType companyType;
    private Map<String, String> companyProfiles;

    private String createdBy;
    private String updatedBy;
    private LocalDateTime createdTime;
    private LocalDateTime lastModified;
    
    private CompanyDTO parent;

    public Long getParentId() {
        if (parent != null) {
            return parent.getId();
        }
        return null;
    }

    public static CompanyDTO fromCompanyModel(CompanyModel model) {
        CompanyDTO companyDTO = new CompanyDTO();
        companyDTO.setId(model.getId());
        companyDTO.setActive(model.getActive());
        companyDTO.setName(model.getName());
        companyDTO.setDescription(model.getDescription());
        companyDTO.setLanguageTags(model.getLanguageTags());
        companyDTO.setCompanyType(model.getCompanyType());
        companyDTO.setCompanyProfiles(model.getCompanyProfiles());
        companyDTO.setCreatedBy(model.getCreatedBy());
        companyDTO.setUpdatedBy(model.getUpdatedBy());
        companyDTO.setCreatedTime(model.getCreatedTime());
        companyDTO.setLastModified(model.getLastModified());
        if (model.getParent() != null) {
            companyDTO.setParent(fromCompanyModel(model.getParent()));
        }
        return companyDTO;
    }

    public CompanyModel toCompanyModel() {
        CompanyModel companyModel = new CompanyModel();
        companyModel.setId(this.id);
        companyModel.setActive(this.active);
        companyModel.setName(this.name);
        companyModel.setDescription(this.description);
        companyModel.setLanguageTags(this.languageTags);
        companyModel.setCompanyType(this.companyType);
        companyModel.setCompanyProfiles(this.companyProfiles);
        companyModel.setCreatedBy(this.createdBy);
        companyModel.setUpdatedBy(this.updatedBy);
        companyModel.setCreatedTime(this.createdTime);
        companyModel.setLastModified(this.lastModified);
        if (this.parent != null) {
            companyModel.setParent(new CompanyModel());
            companyModel.getParent().setId(this.parent.getId());
        }
        return companyModel;
    }
}
