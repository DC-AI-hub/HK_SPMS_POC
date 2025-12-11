package com.spms.backend.service.model.idm;

import com.spms.backend.repository.entities.idm.Department;
import com.spms.backend.repository.entities.idm.DepartmentType;
import com.spms.backend.service.BaseModel;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Getter
@Setter
public class DepartmentModel extends BaseModel<Department> {
    private Long id;
    private String name;
    private Map<String, String> tags;
    private Long parent;
    private DepartmentType type;
    private Integer level;
    private String createdBy;
    private String updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UserModel departmentHead;
    private boolean active;

    public DepartmentModel() {
        super();
    }

    public DepartmentModel(Department department) {
        super();
        this.id= department.getId();
        this.name = department.getName();
        this.tags = department.getTags();
        this.parent = department.getParent();
        this.type = department.getType();
        this.level = department.getLevel();
        this.createdAt = department.getCreatedAt();
        this.updatedAt = department.getUpdatedAt();
        this.createdBy = department.getCreatedBy();
        this.updatedBy = department.getUpdatedBy();
        this.active = department.getActive();
        if(department.getDepartmentHead()!=null) {
            var head = department.getDepartmentHead();
            //prevent loop.
            head.setDepartments(Set.of());

            this.departmentHead = UserModel.fromEntity(head);
        }
    }

    @Override
    public Department toEntityForCreate() {
        Department department = new Department();
        department.setName(this.name);
        department.setTags(this.tags);
        department.setParent(this.parent);
        department.setType(this.type);
        department.setLevel(this.level);
        department.setCreatedAt(createdAt);
        department.setUpdatedBy(updatedBy);
        department.setActive(active);
        if(departmentHead!=null) {
            department.setDepartmentHead(departmentHead.toEntityForUpdate());
        }
        return department;
    }

    @Override
    public Department toEntityForUpdate() {
        Department department = new Department();
        department.setName(this.name);
        department.setTags(this.tags);
        department.setParent(this.parent);
        department.setType(this.type);
        department.setLevel(this.level);
        department.setUpdatedBy(updatedBy);
        department.setActive(active);
        return department;
    }
}
