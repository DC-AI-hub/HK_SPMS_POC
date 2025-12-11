package com.spms.backend.controller.dto.idm;

import com.spms.backend.repository.entities.idm.CompanyType;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Data Transfer Object for Company entity responses.
 * Used to transfer company data in API responses.
 */
@Data
public class CompanyResponseDTO {
    private Long id;
    private Boolean active;
    private LocalDateTime lastModified;
    private String name;
    private String description;
    private Map<String, String> languageTags;
    private CompanyType companyType;
    private Map<String, String> companyProfiles;
}
