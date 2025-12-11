package com.spms.backend.service.idm;

import com.spms.backend.repository.entities.idm.CompanyType;
import com.spms.backend.service.BaseService;
import com.spms.backend.service.model.idm.CompanyModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface CompanyService extends BaseService {

        /**
     * Retrieves paginated list of companies with optional search filters
     * 
     * @param search   Optional search term to filter companies
     * @param type     Optional company type filter
     * @param pageable Pagination parameters
     * @return Page of company models matching criteria
     */
    Page<CompanyModel> getAllCompanies(String search, CompanyType type, Pageable pageable);

        /**
     * Creates a new company entity
     * 
     * @param companyModel Company data transfer object
     * @return Created company model with generated ID
     */
    CompanyModel createCompany(CompanyModel companyModel);

        /**
     * Updates an existing company
     * 
     * @param id         ID of company to update
     * @param companyDTO Updated company data
     * @return Updated company model
     */
    CompanyModel updateCompany(Long id, CompanyModel companyDTO);

        /**
     * Finds companies by parent ID and company type
     * 
     * @param id          Parent company ID
     * @param companyType Type of company to retrieve
     * @return List of matching company models
     */
    List<CompanyModel> findByParentIdAndCompanyType(Long id, CompanyType companyType);

        /**
     * Deletes a company by ID
     * 
     * @param id ID of company to delete
     */
    void deleteCompany(Long id);

    /**
     * evaluate the companies can add into provided or not .
     *
     * @param companyId    the companyId
     * @param childToAdded children to add to
     * @return error message
     */
    List<String> canAddAsChildren(Long companyId, List<Long> childToAdded);

    /**
     * Add the companies as children under the provided company
     *
     * @param companyId    the companyId
     * @param childToAdded children to add to
     */
    void addCompanyToChildren(Long companyId, List<Long> childToAdded);


    /**
     * Checks if a company exists by ID
     * 
     * @param companyId Company ID to check
     * @return true if exists, false otherwise
     */
    boolean isCompanyExists(Long companyId);


    /**
     * Checks if a company exists by name
     * 
     * @param companyName Company name to check
     * @return true if exists, false otherwise
     */
    boolean isCompanyExists(String companyName);

    /**
     * Retrieves paginated list of children companies for a parent
     * 
     * @param companyId Parent company ID
     * @param search    Optional search term
     * @param pageable  Pagination parameters
     * @return Page of child company models
     */
    Page<CompanyModel> getChildren(Long companyId, String search, Pageable pageable);

    /**
     * Get valid parent companies for a given company type
     *
     * @param type the company type
     * @return list of valid parent companies
     */
    List<CompanyModel> getValidParents(CompanyType type);

    /**
     * Validate if a parent is valid for a given company type
     *
     * @param type     the company type
     * @param parentId the parent company id
     * @return true if valid, false otherwise
     */
    boolean isValidParent(CompanyType type, Long parentId);


    /**
     * Retrieves a company by its ID
     * 
     * @param companyId ID of company to retrieve
     * @return Optional containing company model if found
     */
    Optional<CompanyModel> getCompanyByCompanyId(Long companyId);

}
