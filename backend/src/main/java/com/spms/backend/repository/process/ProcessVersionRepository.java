package com.spms.backend.repository.process;

import com.spms.backend.repository.entities.process.ProcessVersionEntity;
import org.springframework.data.repository.query.Param;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProcessVersionRepository extends JpaRepository<ProcessVersionEntity, Long> {
    List<ProcessVersionEntity> findByKey(String key);

    /**
     * Finds process version entities by key and version.
     * 
     * @param key     The process key to search for
     * @param version The version number to search for
     * @return List of matching process version entities
     */
    List<ProcessVersionEntity> findByKeyAndVersion(String key, String version);

    /**
     * Finds all process version entities with the specified status.
     * 
     * @param status the status to search for
     * @return a list of process version entities matching the given status
     */
    List<ProcessVersionEntity> findByStatus(String status);

    /**
     * Finds a ProcessVersionEntity by its ID and Flowable definition ID.
     * 
     * @param id                   the ID of the process version
     * @param flowableDefinitionId the Flowable definition ID
     * @return an Optional containing the found ProcessVersionEntity, or empty if
     *         not found
     */
    Optional<ProcessVersionEntity> findByIdAndFlowableDefinitionId(Long id, String flowableDefinitionId);

    /**
     * Finds a ProcessVersionEntity by its ID and Flowable definition ID.
     *
     * @param version                  the Version of the process version
     * @param flowableDefinitionId the Flowable definition ID
     * @return an Optional containing the found ProcessVersionEntity, or empty if
     *         not found
     */
    Optional<ProcessVersionEntity> findByVersionAndFlowableDefinitionId(String version, String flowableDefinitionId);

    /**
     * Finds a ProcessVersionEntity by its Flowable definition ID.
     *
     * @param flowableDefinitionId the Flowable definition ID
     * @return an Optional containing the found ProcessVersionEntity, or empty if not found
     */
    Optional<ProcessVersionEntity> findByFlowableDefinitionId(String flowableDefinitionId);

    /**
     * Finds a ProcessVersionEntity by its Flowable deployment ID.
     *
     * @param flowableDeploymentId the Flowable deployment ID
     * @return an Optional containing the found ProcessVersionEntity, or empty if not found
     */
    Optional<ProcessVersionEntity> findByFlowableDeploymentId(String flowableDeploymentId);

    /**
     * Finds process definitions with pagination and search support.
     *
     * @param search The search term to filter by name or key (optional)
     * @param pageable The pagination information
     * @return A page of ProcessVersionEntity matching the search criteria
     */
    @Query("SELECT p FROM ProcessVersionEntity p WHERE " +
           "(:search IS NULL OR p.name LIKE %:search% OR p.key LIKE %:search%) " )
    Page<ProcessVersionEntity> findDefinitions(
        @Param("search") String search,
        Pageable pageable);

    /**
     * Finds process versions by process definition ID with pagination support.
     * 
     * @param definitionId The process definition ID to search for
     * @param pageable The pagination information
     * @return A page of ProcessVersionEntity matching the given definition ID
     */
    @Query("SELECT p FROM ProcessVersionEntity p WHERE p.processDefinition.id = :definitionId")
    Page<ProcessVersionEntity> findByProcessDefinitionId(@Param("definitionId") Long definitionId, Pageable pageable);

    /**
     * Finds a ProcessVersionEntity by its ID and associated process definition ID.
     * 
     * @param id The ID of the process version
     * @param processDefinitionId The ID of the process definition
     * @return An Optional containing the found ProcessVersionEntity, or empty if not found
     */
    Optional<ProcessVersionEntity> findByIdAndProcessDefinitionId(Long id, Long processDefinitionId);

    /**
     * Finds a ProcessVersionEntity by process definition ID and version string.
     * 
     * @param processDefinitionId The ID of the process definition
     * @param version The version string
     * @return An Optional containing the found ProcessVersionEntity, or empty if not found
     */
    Optional<ProcessVersionEntity> findByProcessDefinitionIdAndVersion(Long processDefinitionId, String version);

    /**
     * Finds the latest deployed version for a process definition, ordered by creation date descending.
     * 
     * @param definitionId The process definition ID
     * @param pageable The pagination information (should request first page with size=1)
     * @return A page containing the latest deployed version (or empty if none)
     */
    @Query("SELECT p FROM ProcessVersionEntity p WHERE p.processDefinition.id = :definitionId AND p.status = 'DEPLOYED' ORDER BY p.createdAt DESC")
    Page<ProcessVersionEntity> findLatestDeployedVersion(@Param("definitionId") Long definitionId, Pageable pageable);
}
