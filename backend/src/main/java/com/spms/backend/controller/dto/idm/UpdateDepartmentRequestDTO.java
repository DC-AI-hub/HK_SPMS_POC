package com.spms.backend.controller.dto.idm;

import com.spms.backend.repository.entities.idm.DepartmentType;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * DTO for updating department information
 */
@Getter
@Setter
public class UpdateDepartmentRequestDTO {
    private String name;
    private Map<String, String> tags;
    private Long parent;
    private DepartmentType type;
    private Integer level;
    private boolean active;
    private Long departmentHeadId;
}
