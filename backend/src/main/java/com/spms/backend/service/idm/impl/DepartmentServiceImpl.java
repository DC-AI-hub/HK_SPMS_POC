package com.spms.backend.service.idm.impl;

import com.spms.backend.repository.entities.BaseEntity;
import com.spms.backend.repository.entities.idm.CompanyType;
import com.spms.backend.repository.entities.idm.DepartmentType;
import com.spms.backend.repository.entities.idm.User;
import com.spms.backend.repository.idm.DepartmentRepository;
import com.spms.backend.repository.entities.idm.Department;
import com.spms.backend.repository.idm.UserRepository;
import com.spms.backend.service.idm.CompanyService;
import com.spms.backend.service.idm.DepartmentService;
import com.spms.backend.service.idm.DivisionService;
import com.spms.backend.service.model.idm.DepartmentModel;
import com.spms.backend.service.model.idm.UserModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service implementation for department management operations.
 * <p>
 * Handles CRUD operations, department-user associations, and validation
 * for department hierarchy and types.
 */
@Slf4j
@Service
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;

    private final UserRepository userRepository;

    private final DivisionService divisionService;

    private final CompanyService companyService;

    /**
     * Constructs DepartmentService implementation with required dependencies
     * 
     * @param departmentRepository Repository for department data access
     * @param divisionService Service for division operations
     * @param companyService Service for company operations
     * @param userRepository Repository for user data access
     */
    public DepartmentServiceImpl(DepartmentRepository departmentRepository,
                                 DivisionService divisionService,
                                 CompanyService companyService,
                                 UserRepository userRepository
    ) {
        this.departmentRepository = departmentRepository;
        this.divisionService = divisionService;
        this.companyService = companyService;
        this.userRepository = userRepository;
    }

    /**
     * Retrieves paginated list of departments with optional filters
     * 
     * @param pageable Pagination and sorting parameters
     * @param name Department name filter
     * @param departmentType Department type filter
     * @return Page of filtered department models
     */
    @Override
    public Page<DepartmentModel> listDepartments(Pageable pageable, String name, DepartmentType departmentType) {
        // Create dynamic query using Specifications
        Specification<Department> spec = Specification.where(null);
        
        // Add name filter if provided
        if (name != null && !name.trim().isEmpty()) {
            spec = spec.and((root, query, cb) -> 
                cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
        }
        
        // Add department type filter if provided
        if (departmentType != null) {
            spec = spec.and((root, query, cb) -> 
                cb.equal(root.get("type"), departmentType));
        }
        
        // Execute query and map results
        Page<Department> departments = departmentRepository.findAll(spec, pageable);
        return departments.map(DepartmentModel::new);
    }

    /**
     * Retrieves paginated list of departments filtered by search string
     * 
     * @param pageable Pagination and sorting parameters
     * @param search Search string for department name
     * @return Page of department models matching search criteria
     */
    @Override
    public Page<DepartmentModel> listDepartments(Pageable pageable, String search) {
        return this.listDepartments(pageable,search,null);
    }

    /**
     * Creates a new department entity
     * 
     * @param departmentModel Department data transfer object
     * @return Created department model
     * @throws IllegalArgumentException If required fields are missing
     */
    @Override
    public DepartmentModel createDepartment(DepartmentModel departmentModel) {
        if (departmentModel == null) {
            throw new IllegalArgumentException("Department model cannot be null");
        }
        if (departmentModel.getName() == null || departmentModel.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Department name cannot be empty");
        }
        if (departmentModel.getType() == null) {
            throw new IllegalArgumentException("Department type cannot be null");
        }

        Department department = departmentModel.toEntityForCreate();
        department = departmentRepository.save(department);
        return new DepartmentModel(department);
    }

    
    /**
     * Finds departments by parent ID and department type.
     * 
     * @param parentId ID of parent department (as String)
     * @param type Department type to filter by
     * @return List of matching department models
     * @throws IllegalArgumentException If parentId is empty or invalid number
     */
    @Override
    public List<DepartmentModel> findByParentAndType(String parentId, DepartmentType type) {
        if (parentId == null || parentId.trim().isEmpty()) {
            throw new IllegalArgumentException("Parent ID cannot be empty");
        }
        if (type == null) {
            throw new IllegalArgumentException("Department type cannot be null");
        }

        try {
            Long parent = Long.parseLong(parentId);
            List<Department> departments = departmentRepository.findByParentAndType(parent, type);
            return departments.stream()
                .map(DepartmentModel::new)
                .collect(Collectors.toList());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Parent ID must be a valid number");
        }
    }

    /**
     * Retrieves department by its unique identifier
     * 
     * @param departmentId Department ID to lookup
     * @return Optional containing department model if found
     * @throws IllegalArgumentException If departmentId is null
     */
    @Override
    public Optional<DepartmentModel> getDepartmentById(Long departmentId) {
        if (departmentId == null) {
            throw new IllegalArgumentException("Department Id shall not be null.");
        }

        return this.departmentRepository.findById(departmentId).map(DepartmentModel::new);
    }

    /**
     * Adds users to a department
     * 
     * @param department Department model to add users to
     * @param users List of user models to add
     * @return true if operation succeeded, false otherwise
     */
    @Transactional
    @Override
    public boolean addUserToDepartment(DepartmentModel department, List<UserModel> users) {
        try {
            if (users.isEmpty()) {
                return false;
            }

            Department deptEntity = this.departmentRepository.getReferenceById(department.getId());
            if (deptEntity.getUsers() == null) {
                deptEntity.setUsers(new HashSet<>());
            }
            Set<Long> userExists = deptEntity.getUsers().stream().map(BaseEntity::getId).collect(Collectors.toSet());
            users.forEach(x -> {
                if (!userExists.contains(x.getId())) {
                    deptEntity.getUsers().add(x.toEntityForUpdate());
                    log.info("Add user {} from department: {}", x.getUsername(), department.getName());
                }
            });
            log.info("Count of User :{}, department: {}", deptEntity.getUsers().size(), department.getName());
            this.departmentRepository.save(deptEntity);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * Removes users from a department
     * 
     * @param department Department model to remove users from
     * @param users List of user models to remove
     * @return true if operation succeeded, false otherwise
     * @throws IllegalArgumentException If parameters are null or empty
     */
    @Override
    public boolean deleteUserFromDepartment(DepartmentModel department, List<UserModel> users) {
        if (department == null || users == null || users.isEmpty()) {
            throw new IllegalArgumentException("Parameters shall not be null or empty");
        }

        try {
            Department deptEntity = this.departmentRepository.getReferenceById(department.getId());
            var existUsers = deptEntity.getUsers().stream().collect(Collectors.toMap(BaseEntity::getId, x -> x));
            users.forEach(x -> {
                if (existUsers.containsKey(x.getId())) {
                    deptEntity.getUsers().remove(existUsers.get(x.getId()));
                    log.info("remove user {} from department: {}", x.getUsername(), department.getName());
                }
            });
            this.departmentRepository.save(deptEntity);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * Retrieves departments associated with a user
     * 
     * @param user User model to find departments for
     * @return List of department models the user belongs to
     * @throws EntityNotFoundException If user not found
     */
    @Transactional(readOnly = true)
    @Override
    public List<DepartmentModel> getUserDepartment(UserModel user) {
        User userEntity = userRepository.findById(user.getId())
            .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + user.getId()));

        return departmentRepository.findDepartmentWithUsers(userEntity.getId())
            .stream()
            .map(DepartmentModel::new)
            .toList();
    }

    /**
     * Counts departments of specific type
     * 
     * @param departmentType Department type to count (if null, counts all departments)
     * @return Number of departments matching type
     */
    @Override
    public long countDepartments(DepartmentType departmentType) {
        if (departmentType == null) {
            return departmentRepository.count();
        }
        
        // Create specification to filter by department type
        Specification<Department> spec = (root, query, cb) -> 
            cb.equal(root.get("type"), departmentType);
            
        return departmentRepository.count(spec);
    }


    /**
     * Checks if a department exists by ID
     * 
     * @param id Department ID to check
     * @return true if department exists, false otherwise
     */
    @Override
    public boolean isExist(Long id) {
        return departmentRepository.existsById(id);
    }

    @Override
    /**
     * Validates parent department based on hierarchy rules.
     * 
     * @param level Department level in hierarchy
     * @param parentId Proposed parent department ID
     * @param departmentType Type of department being validated
     * @return true if valid parent, false otherwise
     */
    public boolean isValidParent(Integer level, Long parentId, DepartmentType departmentType) {
        switch (departmentType) {
            case FUNCTIONAL ->
            {
                /// FUNCTIONAL WILL Need to connect to function department or division
                return  level==1 ? this.divisionService.isDivisionExists(parentId) :
                        getDepartmentById(parentId)
                            .map(x-> x.getLevel() < level && x.getType() == DepartmentType.FUNCTIONAL )
                            .orElse(false);
            }
            case LOCAL ->
            {
                /// IF IS LOCAL DEPARTMENT ,it shall be a BUSINESS ENTITY OR A HIGHER LEVEL [LOCAL] DEPARTMENT
                return  level==1 ? this.companyService.getCompanyByCompanyId(parentId).map(x->x.getCompanyType() == CompanyType.BUSINESS_ENTITY).orElse(false)
                        : getDepartmentById(parentId).map(x-> x.getLevel() < level && x.getType() == DepartmentType.LOCAL )
                        .orElse(false);
            }
            case TEAM ->
            {
                return this.getDepartmentById(parentId).map(x->
                                level==1 ? x.getType() == DepartmentType.LOCAL :
                                x.getType() == DepartmentType.TEAM && x.getLevel()< level)
                        .orElse(false);
            }
            case FUNCTIONAL_TEAM -> {
                return this.getDepartmentById(parentId).map(x ->
                                level == 1 ? x.getType() == DepartmentType.FUNCTIONAL :
                                        x.getType() == DepartmentType.FUNCTIONAL_TEAM && x.getLevel() < level)
                        .orElse(false);
            }
        }
        return false;
    }

    /**
     * Updates an existing department.
     * 
     * @param id ID of department to update
     * @param departmentModel Updated department data
     * @return Updated department model
     * @throws EntityNotFoundException If department not found
     * @throws IllegalArgumentException If invalid parent department
     */
    @Transactional
    @Override
    public DepartmentModel updateDepartment(Long id, DepartmentModel departmentModel) {
        // Find existing department
        Department existingDepartment = departmentRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Department not found with id: " + id));
        
        // Validate parent if changed
        if (!existingDepartment.getParent().equals(departmentModel.getParent()) ||
            !existingDepartment.getLevel().equals(departmentModel.getLevel()) ||
            !existingDepartment.getType().equals(departmentModel.getType())) {
            
            if (!isValidParent(departmentModel.getLevel(), departmentModel.getParent(), departmentModel.getType())) {
                throw new IllegalArgumentException("Invalid parent department");
            }
        }
        
        // Update fields
        existingDepartment.setName(departmentModel.getName());
        existingDepartment.setTags(departmentModel.getTags());
        existingDepartment.setParent(departmentModel.getParent());
        existingDepartment.setType(departmentModel.getType());
        existingDepartment.setLevel(departmentModel.getLevel());
        existingDepartment.setActive(departmentModel.isActive());
        
        // Handle department head
        if (departmentModel.getDepartmentHead() != null) {
            UserModel departmentHead = departmentModel.getDepartmentHead();
            User headUser = userRepository.findById(departmentHead.getId())
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + departmentHead.getId()));
            existingDepartment.setDepartmentHead(headUser);
        } else {
            existingDepartment.setDepartmentHead(null);
        }
        
        // Save updated department
        Department updatedDepartment = departmentRepository.save(existingDepartment);
        return new DepartmentModel(updatedDepartment);
    }
}
