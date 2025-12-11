package com.spms.backend.service.model.process;

import lombok.Data;

@Data
public class FormDefinitionModel {
    private String version;
    private String definition;
    private String description;
    private String name;
}
