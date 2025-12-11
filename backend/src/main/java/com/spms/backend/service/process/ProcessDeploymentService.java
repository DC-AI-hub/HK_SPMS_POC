package com.spms.backend.service.process;

public interface ProcessDeploymentService {

    void deployProcessDefinition(Long definitionId, Long version, Long ownerId);

    void undeployProcessDefinition(Long definitionId, Long version, Long userId);

}
