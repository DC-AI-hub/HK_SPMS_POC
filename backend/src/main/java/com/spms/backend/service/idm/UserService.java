package com.spms.backend.service.idm;

import com.spms.backend.repository.entities.idm.DepartmentType;
import com.spms.backend.service.BaseService;
import com.spms.backend.service.exception.NotFoundException;
import com.spms.backend.service.exception.ValidationException;
import com.spms.backend.service.model.idm.DepartmentModel;
import com.spms.backend.service.model.idm.RoleModel;
import com.spms.backend.service.model.idm.UserModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for user management operations.
 * Defines contract for user-related business logic.
 * 
 * @Transactional Some methods require transaction boundaries
 */
public interface UserService extends BaseService {
    /**
     * Get current authenticated user ID
     *
     * @return user ID string
     */
    Long getCurrentUserId();

    /**
     * Retrieves user by ID
     *
     * @param id User ID to retrieve
     * @return UserModel containing user details
     * @throws NotFoundException if user not found
     * @Transactional(readOnly = true) Optimized for read operations
     */
    UserModel getUserById(Long id);

    Optional<UserModel> getUserByProviderInformation(String provider, String providerId);

    Optional<UserModel> linkUserToProvider(String provider, String providerId, String userName);

    UserModel getUserByUserName(String userName);

    /**
     * Creates a new user with validation
     *
     * @param userModel User data for creation
     * @return Created UserModel
     * @throws ValidationException if username/email exists
     * @Transactional Required for write operation
     */
    UserModel createUser(UserModel userModel);

    /**
     * Update existing user
     *
     * @param id        User ID to update
     * @param userModel Updated UserModel data
     * @return Updated UserModel
     */
    UserModel updateUser(Long id, UserModel userModel);

    /**
     * Delete user by ID
     *
     * @param id User ID to delete
     */
    void deleteUser(Long id);

    /**
     * Searches users with pagination support
     *
     * @param query    Search query (username or email)
     * @param pageable Pagination parameters
     * @return Page of matching UserModels
     * @Transactional(readOnly = true) Optimized for read operations
     */
    Page<UserModel> searchUsers(String query, Pageable pageable);

    /**
     * Check if username exists
     *
     * @param username Username to check
     * @return true if exists
     */
    boolean usernameExists(String username);

    /**
     * Check if email exists
     *
     * @param email Email to check
     * @return true if exists
     */
    boolean emailExists(String email);

    List<UserModel> getUserFilterByDepartment(DepartmentModel department);

    UserModel getCurrentUser();

    UserModelFulfilledSupporter getFulfilledSupporter();
    
    long countUsers();

    boolean assignRoles(UserModel userModel, List<RoleModel> roles);

    boolean unassignRoles(UserModel userModel, List<RoleModel> roles);
}
