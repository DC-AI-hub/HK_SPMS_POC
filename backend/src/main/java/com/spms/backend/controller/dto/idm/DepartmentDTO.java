package com.spms.backend.controller.dto.idm;

import com.spms.backend.repository.entities.idm.DepartmentType;
import com.spms.backend.service.model.idm.DepartmentModel;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class DepartmentDTO {
    private Long id;
    private String name;
    private Map<String, String> tags;
    private Long parent;
    private DepartmentType type;
    private Integer level;
    private Boolean active;
    private String createdBy;
    private String updatedBy;
    private LocalDateTime createdTime;
    private LocalDateTime lastModified;
    private UserDTO departmentHead;
    private Long departmentHeadId;

    public DepartmentDTO() {

    }

    public DepartmentDTO(DepartmentModel departmentModel) {

        this.id = departmentModel.getId();
        this.name = departmentModel.getName();
        this.tags = departmentModel.getTags();
        this.parent = departmentModel.getParent();
        this.type = departmentModel.getType();
        this.level = departmentModel.getLevel();
        this.active = departmentModel.isActive();
        this.createdBy = departmentModel.getCreatedBy();
        this.updatedBy = departmentModel.getUpdatedBy();
        this.createdTime = departmentModel.getCreatedAt();
        this.lastModified = departmentModel.getUpdatedAt();

        if (departmentModel.getDepartmentHead() != null) {
            var head = departmentModel.getDepartmentHead();
            //Prevent Loop
            head.setDepartments(List.of());
            this.departmentHead = UserDTO.fromUserModel(head);
            this.departmentHeadId = head.getId();
        }
    }
}
