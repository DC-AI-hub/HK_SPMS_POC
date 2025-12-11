package com.spms.backend.controller.dto.idm;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class OrganizationChartDTO {
    private String id;
    private String name;
    private String type;
    private List<OrganizationChartDTO> children;

    // Constructors
    public OrganizationChartDTO() {
        this.children = new ArrayList<>();

    }
    
    public OrganizationChartDTO(String id, String name, String type) {
        this();
        this.id = id;
        this.name = name;
        this.type = type;

    }

}
