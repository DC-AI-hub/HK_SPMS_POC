package com.spms.backend.service.idm;

import com.spms.backend.service.BaseService;
import java.util.List;
import com.spms.backend.service.model.idm.DivisionModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service interface for division management operations.
 * Defines business logic for division-related actions including CRUD operations,
 * searching, and existence checks.
 */
public interface DivisionService extends BaseService {
    
    /**
     * Retrieves a paginated list of all divisions.
     * 
     * @param pageable pagination configuration (page number, size, sorting)
     * @return page of division models
     */
    Page<DivisionModel> getAllDivisions(Pageable pageable);
    
    /**
     * Searches divisions by name containing given query (case-insensitive).
     * 
     * @param query search string
     * @param pageable pagination configuration
     * @return page of matching division models
     */
    Page<DivisionModel> searchDivisions(String query, Pageable pageable);
    
    /**
     * Creates a new division.
     * 
     * @param divisionModel division data transfer object
     * @return created division model
     * @throws NotFoundException if associated company is not found
     */
    DivisionModel createDivision(DivisionModel divisionModel);
    
    /**
     * Updates an existing division.
     * 
     * @param id division identifier to update
     * @param divisionModel updated division data
     * @return updated division model
     * @throws NotFoundException if division or associated company is not found
     */
    DivisionModel updateDivision(Long id, DivisionModel divisionModel);
    
    /**
     * Deletes a division by ID.
     * 
     * @param id division identifier to delete
     * @throws NotFoundException if division is not found
     */
    void deleteDivision(Long id);
    
    /**
     * Deletes multiple divisions in bulk.
     * 
     * @param ids list of division identifiers to delete
     * @throws NotFoundException if any division is not found
     */
    void bulkDeleteDivisions(List<Long> ids);
    
    /**
     * Checks if a division exists by ID.
     * 
     * @param divisionId division identifier to check
     * @return true if division exists, false otherwise
     */
    boolean isDivisionExists(Long divisionId);
    
    /**
     * Checks if a division exists by name.
     * 
     * @param divisionName division name to check
     * @return true if division exists, false otherwise
     */
    boolean isDivisionExists(String divisionName);
    
    /**
     * Retrieves a division by its ID.
     * 
     * @param id division identifier
     * @return division model
     * @throws NotFoundException if division is not found
     */
    DivisionModel getDivisionById(Long id);
    
    /**
     * Finds divisions by company ID.
     * 
     * @param companyId company identifier
     * @return list of division models belonging to the company
     */
    List<DivisionModel> findByCompanyId(Long companyId);
    
    /**
     * Counts the total number of divisions.
     * 
     * @return total division count
     */
    Long countDivision();
}
