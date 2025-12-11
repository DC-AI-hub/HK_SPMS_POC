package com.spms.backend.controller;

import com.spms.backend.controller.dto.process.ProcessDefinitionDTO;
import com.spms.backend.service.model.process.ProcessDefinitionModel;
import com.spms.backend.service.model.process.ProcessVersionModel;
import com.spms.backend.service.idm.UserService;
import com.spms.backend.controller.dto.process.ProcessDefinitionVersionDTO;
import com.spms.backend.controller.dto.idm.UserDTO;
import org.springframework.stereotype.Component;

@Component
public class ProcessConverter {
    private final UserService userService;

    public ProcessConverter(UserService userService) {
        this.userService = userService;
    }

    public ProcessDefinitionVersionDTO toProcessDefinitionVersionDTO(ProcessVersionModel model) {
        ProcessDefinitionVersionDTO dto = new ProcessDefinitionVersionDTO();
        dto.setId(model.getId());
        dto.setName(model.getName());
        dto.setKey(model.getKey());
        dto.setBpmnXml(model.getBpmnXml());
        dto.setVersion(model.getVersion());
        dto.setStatus(model.getStatus().name());
        dto.setCreatedAt(model.getCreatedAt());
        dto.setDeployedToFlowable(model.getDeployedToFlowable());
        return dto;
    }

    public ProcessDefinitionDTO toProcessDefinitionDto(ProcessDefinitionModel model) {
        ProcessDefinitionDTO dto = new ProcessDefinitionDTO();
        dto.setId(model.getId());
        dto.setProcessName(model.getName());
        if (model.getOwner() != null) {
            dto.setOwner(UserDTO.fromUserModel(model.getOwner()));
        }
        if (model.getBusinessOwner() != null) {
            dto.setBusiness(UserDTO.fromUserModel(model.getBusinessOwner()));
        }
        dto.setProcessDescription(model.getDescription());
        dto.setProcessKey(model.getKey());
        dto.setCreatedDate(model.getCreatedAt());
        dto.setModifiedDate(model.getUpdatedAt());
        dto.setVersions ( model.getVersions().stream().map(ProcessDefinitionVersionDTO::toProcessDefinitionVersionDTO).toList());
        return dto;
    }

}
