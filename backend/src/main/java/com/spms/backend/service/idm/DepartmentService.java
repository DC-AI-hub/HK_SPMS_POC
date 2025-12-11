package com.spms.backend.service.idm;

import com.spms.backend.repository.entities.idm.DepartmentType;
import com.spms.backend.service.model.idm.DepartmentModel;
import com.spms.backend.service.model.idm.UserModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface DepartmentService {
    /**
     * Retrieves paginated list of departments filtered by search string
     * 
     * @param pageable Pagination and sorting parameters
     * @param search Search string for department name
     * @return Page of department models matching search criteria
     */
    Page<DepartmentModel> listDepartments(Pageable pageable, String search);

    /**
     * Retrieves paginated list of departments with filters
     * 
     * @param pageable Pagination and sorting parameters
     * @param name Department name filter
     * @param departmentType Department type filter
     * @return Page of filtered department models
     */
    Page<DepartmentModel> listDepartments(Pageable pageable, String name,DepartmentType departmentType);

    /**
     * Creates a new department entity
     * 
     * @param departmentModel Department data transfer object
     * @return Created department model
     * @throws IllegalArgumentException If required fields are missing
     */
    DepartmentModel createDepartment(DepartmentModel departmentModel);

    /**
     * Updates an existing department
     *
     * @param id The ID of the department to update
     * @param departmentModel DTO containing updated department data
     * @return Updated DepartmentModel
     */
    DepartmentModel updateDepartment(Long id, DepartmentModel departmentModel);

    /**
     * Finds departments by parent ID and department type
     * 
     * @param parentId ID of parent department
     * @param type Department type to filter by
     * @return List of matching department models
     * @throws IllegalArgumentException If parentId is invalid
     */
    List<DepartmentModel> findByParentAndType(String parentId, DepartmentType type);

    /**
     * Validates parent department based on hierarchy rules
     * 
     * @param level Department level in hierarchy
     * @param parentId Proposed parent department ID
     * @param departmentType Type of department being validated
     * @return true if valid parent, false otherwise
     */
    boolean isValidParent(Integer level, Long parentId, DepartmentType departmentType);

    /**
     * Retrieves department by its unique identifier
     * 
     * @param departmentId Department ID to lookup
     * @return Optional containing department model if found
     * @throws IllegalArgumentException If departmentId is null
     */
    Optional<DepartmentModel> getDepartmentById(Long departmentId);

    /**
     * Adds users to a department
     * 
     * @param department Department model
     * @param users List of user models to add
     * @return true if operation succeeded, false otherwise
     */
    boolean addUserToDepartment(DepartmentModel department, List<UserModel> users);

    /**
     * Removes users from a department
     * 
     * @param department Department model
     * @param users List of user models to remove
     * @return true if operation succeeded, false otherwise
     * @throws IllegalArgumentException If parameters are invalid
     */
    boolean deleteUserFromDepartment(DepartmentModel department, List<UserModel> users);

    /**
     * Retrieves departments associated with a user
     * 
     * @param user User model
     * @return List of department models the user belongs to
     * @throws EntityNotFoundException If user not found
     */
    List<DepartmentModel> getUserDepartment(UserModel user);

    /**
     * Counts departments of specific type
     * 
     * @param departmentType Department type to count
     * @return Number of departments matching type
     */
    long countDepartments(DepartmentType departmentType );
    
    /**
     * Checks if department exists by ID
     * 
     * @param id Department ID to check
     * @return true if department exists, false otherwise
     */
    boolean isExist(Long id);
}
