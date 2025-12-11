package com.spms.backend.service.idm.impl;

import com.spms.backend.repository.entities.idm.CompanyType;
import com.spms.backend.service.exception.NotFoundException;
import com.spms.backend.repository.entities.idm.Company;
import com.spms.backend.repository.idm.CompanyRepository;
import com.spms.backend.service.BaseServiceImpl;
import com.spms.backend.service.idm.CompanyService;
import com.spms.backend.service.model.idm.CompanyModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

@Service
public class CompanyServiceImpl extends BaseServiceImpl<Company, CompanyRepository>
    implements CompanyService {
    @Autowired
    public CompanyServiceImpl(
        CompanyRepository repository
    ) {
        super(repository);
    }

    @Override
    public Page<CompanyModel> getAllCompanies(String search, CompanyType type, Pageable pageable) {
        if (type != null) {
            if (search != null && !search.isEmpty()) {
                return repository.findByCompanyTypeAndNameContainingIgnoreCase(type, search, pageable)
                        .map(CompanyModel::fromEntity);
            }
            return repository.findByCompanyType(type, pageable)
                    .map(CompanyModel::fromEntity);
        }
        if (search != null && !search.isEmpty()) {
            return repository.findByNameContainingIgnoreCase(search, pageable)
                    .map(CompanyModel::fromEntity);
        }
        return repository.findAll(pageable)
                .map(CompanyModel::fromEntity);
    }

    @Override
    public CompanyModel createCompany(CompanyModel companyModel) {
        // Validate head IDs exist if provided
        if (companyModel.getDivisionHeadId() != null && 
            !repository.existsById(companyModel.getDivisionHeadId())) {
            throw new NotFoundException("Division head not found");
        }
        if (companyModel.getDepartmentHeadId() != null && 
            !repository.existsById(companyModel.getDepartmentHeadId())) {
            throw new NotFoundException("Department head not found");
        }

        return CompanyModel.fromEntity(repository.save(companyModel.toEntityForCreate()));
    }

    @Override
    public CompanyModel updateCompany(Long id, CompanyModel companyModel) {
        Company existing = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Company not found"));

        // Validate head IDs exist if provided
        if (companyModel.getDivisionHeadId() != null && 
            !repository.existsById(companyModel.getDivisionHeadId())) {
            throw new NotFoundException("Division head not found");
        }
        if (companyModel.getDepartmentHeadId() != null && 
            !repository.existsById(companyModel.getDepartmentHeadId())) {
            throw new NotFoundException("Department head not found");
        }

        Company updatedEntity = companyModel.toEntityForUpdate();
        updatedEntity.setId(existing.getId());
        
        return CompanyModel.fromEntity(repository.save(updatedEntity));
    }

    @Override
    public List<CompanyModel> findByParentIdAndCompanyType(Long id, CompanyType companyType) {
        if (id == null || companyType == null) {
            return List.of();
        }
        return repository.findByParentIdAndCompanyType(id, companyType)
            .stream()
            .map(CompanyModel::fromEntity)
            .toList();
    }

    @Override
    public void deleteCompany(Long id) {
        if (!repository.existsById(id)) {
            throw new NotFoundException("Company not found");
        }
        repository.deleteById(id);
    }

    /**
     * evaluate the companies can add into provided or not .
     *
     * @param companyId    the companyId
     * @param childToAdded children to add to
     * @return error message
     */
    /**
     * Validates if companies can be added as children to a parent company
     * 
     * @param companyId The ID of the parent company
     * @param childToAdded List of child company IDs to validate
     * @return List of validation error messages, empty if all valid
     */
    @Override
    public List<String> canAddAsChildren(Long companyId, List<Long> childToAdded) {
        List<String> errors = new ArrayList<>();

        if (companyId == null) {
            errors.add("Parent company ID cannot be null");
            return errors;
        }

        Optional<Company> parentOpt = repository.findById(companyId);
        if (parentOpt.isEmpty()) {
            errors.add("Parent company not found");
            return errors;
        }
        Company parent = parentOpt.get();

        // Check for duplicate child IDs
        Set<Long> uniqueChildIds = new HashSet<>(childToAdded);
        if (uniqueChildIds.size() != childToAdded.size()) {
            errors.add("Duplicate child company IDs found");
        }

        for (Long childId : uniqueChildIds) {
            if (childId == null) {
                errors.add("Child company ID cannot be null");
                continue;
            }

            Optional<Company> childOpt = repository.findById(childId);
            if (childOpt.isEmpty()) {
                errors.add("Child company not found: " + childId);
                continue;
            }
            Company child = childOpt.get();

            // Check circular reference
            if (Objects.equals(childId, companyId)) {
                errors.add("Cannot add company as child of itself: " + childId);
            }

            // Check if already a child
            if (child.getParent() != null && 
                Objects.equals(child.getParent().getId(), companyId)) {
                errors.add("Company is already a child: " + childId);
            }

            // Validate parent-child type relationship
            if (!isValidParent(child.getCompanyType(), companyId)) {
                errors.add(String.format(
                    "Invalid parent-child relationship: %s cannot be child of %s",
                    child.getCompanyType(), parent.getCompanyType()));
            }
        }

        return errors;
    }


    /**
     * Add the companies as children under the provided company
     *
     * @param companyId    the companyId
     * @param childToAdded children to add to
     */
    @Override
    public void addCompanyToChildren(Long companyId, List<Long> childToAdded) {
        Company parentCompany = repository.findById(companyId)
                .orElseThrow(() -> new NotFoundException("Parent company not found"));

        childToAdded.forEach(childId -> {
            Company childCompany = repository.findById(childId)
                    .orElseThrow(() -> new NotFoundException("Child company not found"));
            
            // Update child's parent reference
            childCompany.setParent(parentCompany);
            
            // Update parent's children collection
            parentCompany.getChildren().add(childCompany);
            
            repository.save(childCompany);
        });
        
        // Save parent to ensure children collection is persisted
        repository.save(parentCompany);
    }

    @Override
    public boolean isCompanyExists(Long companyId) {
        return repository.existsById(companyId);
    }

    @Override
    public boolean isCompanyExists(String companyName) {
        return repository.existsByName(companyName);
    }

    @Override
    public Page<CompanyModel> getChildren(Long companyId, String search, Pageable pageable) {
        if (companyId == null || companyId < 1) {
            return getAllCompanies(search,null, pageable);
        }

        if (search != null && !search.isEmpty()) {
            return repository.findByParentIdAndNameContainingIgnoreCase(companyId, search, pageable)
                    .map(CompanyModel::fromEntity);
        }
        return repository.findByParentId(companyId, pageable)
                .map(CompanyModel::fromEntity);
    }

    /**
     * Retrieves valid parent companies for a given company type
     * 
     * @param type The company type to find valid parents for
     * @return List of valid parent CompanyModels
     */
    @Override
    public List<CompanyModel> getValidParents(CompanyType type) {
        if (type == null) {
            return List.of();
        }

        switch (type) {
            case GROUP:
                return List.of(); // Groups can't have parents
            case BUSINESS_ENTITY:
                return repository.findByCompanyType(CompanyType.GROUP)
                        .stream()
                        .map(CompanyModel::fromEntity)
                        .toList();
            case VENDOR:
            case CUSTOMER:
                return Stream.concat(
                    repository.findByCompanyType(CompanyType.GROUP).stream(),
                    repository.findByCompanyType(CompanyType.BUSINESS_ENTITY).stream())
                    .map(CompanyModel::fromEntity)
                    .toList();
            default:
                return List.of();
        }
    }

    /**
     * Retrieves a company by its ID
     * 
     * @param companyId The ID of the company to retrieve
     * @return Optional containing the CompanyModel if found, empty otherwise
     */
    @Override
    public Optional<CompanyModel> getCompanyByCompanyId(Long companyId) {
        if (companyId == null) {
            throw new IllegalArgumentException("Company ID cannot be null");
        }
        if (!repository.existsById(companyId)) {
            throw new NotFoundException("Company not found");
        }
        return repository.findById(companyId)
                .map(CompanyModel::fromEntity);
    }


    /**
     * Validates if a company can be a parent of another company based on their types
     * 
     * @param type The CompanyType of the child company
     * @param parentId The ID of the potential parent company
     * @return true if the parent-child relationship is valid, false otherwise
     */
    @Override
    public boolean isValidParent(CompanyType type, Long parentId) {
        if (type == null) {
            return false;
        }
        
        if (parentId == null) {
            // Only GROUP type companies can be root nodes (no parent)
            return type == CompanyType.GROUP;
        }

        Company parent = repository.findById(parentId)
            .orElseThrow(() -> new NotFoundException("Parent company not found"));

        switch (type) {
            case GROUP:
                return false; // Groups cannot have parents
            case BUSINESS_ENTITY:
                // BUSINESS_ENTITY can have GROUP as parent
                return parent.getCompanyType() == CompanyType.GROUP;
            case VENDOR:
            case CUSTOMER:
                // VENDOR/CUSTOMER can have GROUP or BUSINESS_ENTITY as parent
                return parent.getCompanyType() == CompanyType.GROUP || 
                       parent.getCompanyType() == CompanyType.BUSINESS_ENTITY;
            default:
                return false;
        }
    }

}
