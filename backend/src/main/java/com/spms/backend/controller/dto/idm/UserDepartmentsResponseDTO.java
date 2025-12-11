package com.spms.backend.controller.dto.idm;

import com.spms.backend.controller.dto.idm.DepartmentDTO;
import java.util.List;

public class UserDepartmentsResponseDTO {
    private List<DepartmentDTO> departments;

    public List<DepartmentDTO> getDepartments() {
        return departments;
    }

    public void setDepartments(List<DepartmentDTO> departments) {
        this.departments = departments;
    }
}
