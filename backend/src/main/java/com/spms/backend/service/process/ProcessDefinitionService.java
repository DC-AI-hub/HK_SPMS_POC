package com.spms.backend.service.process;

import com.spms.backend.service.model.process.ProcessDefinitionModel;
import com.spms.backend.service.model.process.ProcessVersionModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ProcessDefinitionService {
    /**
     * Gets paginated process definitions with optional search filtering
     *
     * @param search   Optional search term to filter by name or key
     * @param pageable Pagination information
     * @return Page of ProcessVersionModel matching criteria
     */
    Page<ProcessDefinitionModel> getProcessDefinitions(String search, Pageable pageable);

    /**
     * Get a single process definition by ID
     *
     * @param definitionId The process definition ID
     * @return ProcessVersionModel with definition details
     */
    ProcessDefinitionModel getProcessDefinition(String definitionId);

    /**
     * Get a single process definition by ID
     *
     * @param definitionId The process definition ID
     * @return ProcessVersionModel with definition details
     */
    ProcessDefinitionModel getProcessDefinition(Long definitionId);

    /**
     * Creates a new process definition version
     *
     * @param model Process definition details
     * @return Created ProcessDefinitionModel
     */
    ProcessDefinitionModel createProcessDefinition(ProcessDefinitionModel model);

    Optional<ProcessVersionModel> getProcessDefinitionVersion(Long definitionId, String version);

    Optional<ProcessVersionModel> getProcessDefinitionVersion(String definitionId, String versionId);

    ProcessVersionModel createProcessDefinitionVersion(
            Long definitionId,
            ProcessVersionModel versionModel
    );

    /**
     * Updates an existing process definition version.
     * 
     * @param versionId ID of the process version to update
     * @param versionModel Model containing updated fields
     * @return Updated ProcessVersionModel
     */
    ProcessVersionModel updateProcessDefinitionVersion(
            Long versionId,
            ProcessVersionModel versionModel
    );

    /**
     * Gets all versions of a process definition
     *
     * @param definitionId Process definition ID
     * @param pageable     Pagination information
     * @return Page of ProcessVersionModel
     */
    Page<ProcessVersionModel> getDefinitionVersions(String definitionId, Pageable pageable);
    
    /**
     * Count all process definitions
     * @return the total number of process definitions
     */
    long countProcessDefinitions();

    Optional<ProcessVersionModel> getProcessVersionByDeploymentId(String deploymentId);
}
