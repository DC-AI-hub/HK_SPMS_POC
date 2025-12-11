package com.spms.backend.controller.dto.idm;

import com.spms.backend.repository.entities.idm.DepartmentType;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * Data Transfer Object for creating new Department entities
 */
@Getter
@Setter
public class CreateDepartmentRequestDTO {
    private String name;
    private Map<String, String> tags;
    private Long parent;
    private DepartmentType type;
    private Integer level;
    private Boolean active;
    private Long departmentHeadId;
}
