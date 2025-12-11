package com.spms.backend.service.idm.impl;

import com.spms.backend.repository.entities.idm.Company;
import com.spms.backend.repository.idm.CompanyRepository;
import com.spms.backend.repository.idm.DivisionRepository;
import com.spms.backend.service.idm.DivisionService;
import com.spms.backend.service.model.idm.DivisionModel;
import com.spms.backend.repository.entities.idm.Division;
import com.spms.backend.service.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DivisionServiceImpl implements DivisionService {

    private final DivisionRepository divisionRepository;
    private final CompanyRepository companyRepository;

    /**
     * Retrieves a paginated list of all divisions.
     *
     * @param pageable pagination configuration (page number, size, sorting)
     * @return page of division models
     */
    @Override
    @Transactional(readOnly = true)
    public Page<DivisionModel> getAllDivisions(Pageable pageable) {
        return divisionRepository.findAll(pageable)
                .map(DivisionModel::fromEntity);
    }

    /**
     * Retrieves a division by its ID.
     *
     * @param id division identifier
     * @return division model
     * @throws NotFoundException if division is not found
     */
    @Override
    @Transactional(readOnly = true)
    public DivisionModel getDivisionById(Long id) {
        return divisionRepository.findById(id)
                .map(DivisionModel::fromEntity)
                .orElseThrow(() -> new NotFoundException("Division not found with id: " + id));
    }

    /**
     * Finds divisions by company ID.
     *
     * @param companyId company identifier
     * @return list of division models belonging to the company
     */
    @Override
    public List<DivisionModel> findByCompanyId(Long companyId) {
        return divisionRepository.findByCompanyId(companyId).stream().map(DivisionModel::fromEntity).toList();
    }

    /**
     * Counts the total number of divisions.
     *
     * @return total division count
     */
    @Override
    public Long countDivision() {
        return divisionRepository.count();
    }

    /**
     * Creates a new division.
     *
     * @param divisionModel division data transfer object
     * @return created division model
     * @throws NotFoundException if associated company is not found
     */
    @Override
    @Transactional
    public DivisionModel createDivision(DivisionModel divisionModel) {
        Division division = divisionModel.toEntityForCreate();
        Company company = companyRepository.findById(divisionModel.getCompanyId())
                .orElseThrow(() -> new NotFoundException("Business Unit not found with id: " + divisionModel.getCompanyId()));
        division.setCompany(company);
        Division savedDivision = divisionRepository.save(division);
        return DivisionModel.fromEntity(savedDivision);
    }

    /**
     * Updates an existing division.
     *
     * @param id division identifier to update
     * @param divisionModel updated division data
     * @return updated division model
     * @throws NotFoundException if division or associated company is not found
     */
    @Override
    @Transactional
    public DivisionModel updateDivision(Long id, DivisionModel divisionModel) {
        Division existingDivision = divisionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Division not found with id: " + id));
        Company company = companyRepository.findById(divisionModel.getCompanyId())
                .orElseThrow(() -> new NotFoundException("Business Unit not found with id: " + divisionModel.getCompanyId()));
        
        // Update only the fields that should change
        existingDivision.setName(divisionModel.getName());
        existingDivision.setCompany(company);
        existingDivision.setType(divisionModel.getType());
        existingDivision.setDescription(divisionModel.getDescription());
        existingDivision.setActive(divisionModel.getActive());
        
        Division updatedDivision = divisionRepository.save(existingDivision);
        return DivisionModel.fromEntity(updatedDivision);
    }

    /**
     * Deletes a division by ID.
     *
     * @param id division identifier to delete
     * @throws NotFoundException if division is not found
     */
    @Override
    @Transactional
    public void deleteDivision(Long id) {
        Division division = divisionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Division not found with id: " + id));
        divisionRepository.delete(division);
    }

    /**
     * Deletes multiple divisions in bulk.
     *
     * @param ids list of division identifiers to delete
     * @throws NotFoundException if any division is not found
     */
    @Override
    @Transactional
    public void bulkDeleteDivisions(List<Long> ids) {
        List<Division> divisions = divisionRepository.findAllById(ids);
        if (divisions.size() != ids.size()) {
            throw new NotFoundException("Some divisions not found");
        }
        divisionRepository.deleteAll(divisions);
    }

    /**
     * Checks if a division exists by ID.
     *
     * @param divisionId division identifier to check
     * @return true if division exists, false otherwise
     */
    @Override
    @Transactional(readOnly = true)
    public boolean isDivisionExists(Long divisionId) {
        return divisionId !=null && divisionRepository.existsById(divisionId);
    }

    /**
     * Checks if a division exists by name.
     *
     * @param divisionName division name to check
     * @return true if division exists, false otherwise
     */
    @Override
    @Transactional(readOnly = true)
    public boolean isDivisionExists(String divisionName) {
        return divisionRepository.existsByName(divisionName);
    }

    /**
     * Searches divisions by name containing given query (case-insensitive).
     *
     * @param query search string
     * @param pageable pagination configuration
     * @return page of matching division models
     */
    @Override
    @Transactional(readOnly = true)
    public Page<DivisionModel> searchDivisions(String query, Pageable pageable) {
        return divisionRepository.findByNameContainingIgnoreCase(query, pageable)
                .map(DivisionModel::fromEntity);
    }


}
