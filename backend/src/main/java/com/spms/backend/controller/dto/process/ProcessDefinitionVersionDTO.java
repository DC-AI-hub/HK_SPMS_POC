package com.spms.backend.controller.dto.process;

import java.time.LocalDateTime;

import com.spms.backend.controller.dto.idm.UserDTO;
import com.spms.backend.service.model.process.ProcessVersionModel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@EqualsAndHashCode
public class ProcessDefinitionVersionDTO {
    // Getters and setters
    private Long id;
    private String name;
    private String key;
    private String bpmnXml;
    private String version;
    private Long createdAt;
    private Boolean deployedToFlowable;
    private String status;
    private String currentVersionStatus;
    private String deploymentStatus;
    private Long lastDeployedAt;
    private String deploymentMessage;

    private FormVersionDTO formVersion;


    public static ProcessDefinitionVersionDTO toProcessDefinitionVersionDTO(ProcessVersionModel model) {
        ProcessDefinitionVersionDTO dto = new ProcessDefinitionVersionDTO();
        dto.setId(model.getId());
        dto.setName(model.getName());
        dto.setKey(model.getKey());
        dto.setBpmnXml(model.getBpmnXml());
        dto.setVersion(model.getVersion());
        dto.setStatus(model.getStatus().name());
        dto.setCreatedAt(model.getCreatedAt());
        dto.setDeployedToFlowable(model.getDeployedToFlowable());
        if (model.getRelatedForm() != null) {
            dto.setFormVersion(FormVersionDTO.convertToDTO(model.getRelatedForm()));
        }
        return dto;
    }
}
