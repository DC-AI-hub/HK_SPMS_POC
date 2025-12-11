package com.spms.backend.controller.dto.process;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@EqualsAndHashCode
public class ProcessDefinitionVersionRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String key;

    @NotBlank(message = "BPMN XML content is required")
    private String bpmnXml;
    
    @NotBlank(message = "Version number is required")
    private String version;
    
    private String description;
    
    @NotNull(message = "Owner ID is required")
    private Long ownerId;
    
    @NotNull(message = "Business owner ID is required")
    private Long businessOwnerId;
}
