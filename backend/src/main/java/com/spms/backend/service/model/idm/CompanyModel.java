package com.spms.backend.service.model.idm;

import com.spms.backend.repository.entities.idm.Company;
import com.spms.backend.repository.entities.idm.CompanyType;
import com.spms.backend.service.BaseModel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Model class representing a Company entity.
 * Contains all business logic related fields and validations.
 */
@Getter
@Setter
public class CompanyModel extends BaseModel<Company> {

    private Long id;
    private Boolean active;
    private LocalDateTime lastModified;

    @NotBlank(message = "Company name is required")
    @Size(max = 100, message = "Company name must be less than 100 characters")
    private String name;

    @Size(max = 1000, message = "Description must be less than 1000 characters")
    private String description;

    @NotNull(message = "Language tags are required")
    private Map<String, String> languageTags;

    @NotNull(message = "Company type is required")
    private CompanyType companyType;
    private Map<String, String> companyProfiles;
    private String createdBy;
    private String updatedBy;
    private LocalDateTime createdTime;
    private CompanyModel parent;
    private Long divisionHeadId;
    private Long departmentHeadId;
    private List<CompanyModel> children;

    @Override
    public Company toEntityForCreate() {
        Company company = toEntityForUpdate();
        company.setCreatedBy(createdBy);
        company.setId(null);
        company.setActive(true); // Ensure active is set to non-null default
        return company;
    }

    @Override
    public Company toEntityForUpdate() {
        Company company = new Company();
        company.setCompanyType(companyType);
        company.setCompanyProfiles(companyProfiles);
        company.setId(id);
        company.setActive(active != null ? active : true); // Default to true if null
        company.setName(name);
        company.setDescription(description);
        company.setLanguageTags(languageTags);
        company.setCompanyProfiles(companyProfiles);
        company.setUpdatedBy(updatedBy);
        if (parent != null) {
            company.setParent(new Company());
            company.getParent().setId(parent.getId());
        }
        company.setDivisionHeadId(divisionHeadId);
        company.setDepartmentHeadId(departmentHeadId);
        return company;
    }

    public static CompanyModel fromEntity(Company companyE) {
        CompanyModel company = new CompanyModel();
        company.setCompanyType(companyE.getCompanyType());
        company.setCompanyProfiles(companyE.getCompanyProfiles());
        company.setId(companyE.getId());
        company.setActive(companyE.getActive());
        company.setName(companyE.getName());
        company.setDescription(companyE.getDescription());
        company.setLanguageTags(companyE.getLanguageTags());
        company.setLastModified(companyE.getLastModified());
        company.setUpdatedBy(companyE.getUpdatedBy());
        company.setCreatedBy(companyE.getCreatedBy());
        company.setCreatedTime(companyE.getCreatedTime());
        if (companyE.getParent() != null) {
            company.setParent(fromEntity(companyE.getParent()));
        }
        company.setDivisionHeadId(companyE.getDivisionHeadId());
        company.setDepartmentHeadId(companyE.getDepartmentHeadId());
        return company;
    }
}
