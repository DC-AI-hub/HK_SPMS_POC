package com.spms.backend.repository.process;

import com.spms.backend.repository.entities.process.ProcessDefinitionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProcessDefinitionRepository extends JpaRepository<ProcessDefinitionEntity,Long> {

    /**
     * Finds process definitions with pagination and search support.
     *
     * @param search   The search term to filter by name or key (optional)
     * @param pageable The pagination information
     * @return A page of ProcessDefinitionEntity matching the search criteria
     */
    @Query("SELECT p FROM ProcessDefinitionEntity p WHERE " +
            "(:search IS NULL OR p.name LIKE %:search% OR p.key LIKE %:search%) ")
    Page<ProcessDefinitionEntity> findDefinitions(
            @Param("search") String search,
            Pageable pageable);

    /**
     * Finds a single process definition by unique business key.
     */
    @Query("SELECT p FROM ProcessDefinitionEntity p WHERE p.key = :key")
    ProcessDefinitionEntity findByKey(@Param("key") String key);
}
