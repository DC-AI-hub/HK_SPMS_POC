package com.spms.backend.controller.dto.idm;

import com.spms.backend.repository.entities.idm.CompanyType;
import com.spms.backend.service.model.idm.CompanyModel;
import lombok.Data;

import java.util.Map;

@Data
public class CreateCompanyRequestDTO {
    private Boolean active;
    private String name;
    private String description;
    private Map<String, String> languageTags;
    private CompanyType companyType;
    private Map<String, String> companyProfiles;
    private Long parentId;

    public CompanyModel toCompanyModel() {
        return toCompanyModel(null);
    }

    public CompanyModel toCompanyModel(CompanyModel parent) {
        CompanyModel companyModel = new CompanyModel();
        companyModel.setActive(active);
        companyModel.setName(name);
        companyModel.setDescription(description);
        companyModel.setCompanyType(companyType);
        companyModel.setCompanyProfiles(companyProfiles);
        companyModel.setLanguageTags(languageTags);
        companyModel.setParent(parent);
        return companyModel;
    }
}
