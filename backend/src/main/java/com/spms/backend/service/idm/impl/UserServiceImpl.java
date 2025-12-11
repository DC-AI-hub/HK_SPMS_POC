package com.spms.backend.service.idm.impl;

import com.spms.backend.config.SpmsOidcUser;
import com.spms.backend.service.exception.NotFoundException;
import com.spms.backend.service.exception.ValidationException;
import com.spms.backend.repository.entities.idm.Department;
import com.spms.backend.repository.entities.idm.Role;
import com.spms.backend.repository.idm.*;
import com.spms.backend.service.model.idm.RoleModel;
import com.spms.backend.service.BaseServiceImpl;
import com.spms.backend.repository.entities.idm.User;
import com.spms.backend.service.idm.UserModelFulfilledSupporter;
import com.spms.backend.service.idm.UserService;
import com.spms.backend.service.model.idm.DepartmentModel;
import com.spms.backend.service.model.idm.UserModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.domain.Specification;

import java.util.*;

/**
 * Implementation of {@link UserService} providing user management operations.
 * Handles user CRUD operations, validation, and business logic.
 *
 * @Service Marks this as a Spring service component
 * @Transactional Most methods have transactional boundaries
 */
@Service
public class UserServiceImpl extends BaseServiceImpl<User, UserRepository> implements UserService {

    private DepartmentRepository departmentRepository;
    private final RoleRepository roleRepository;

    public UserServiceImpl(UserRepository repository,
                           DepartmentRepository departmentRepository,
                           RoleRepository roleRepository
    ) {
        super(repository);
        this.departmentRepository = departmentRepository;
        this.roleRepository = roleRepository;
    }



    /**
     * Retrieves the ID of the currently authenticated user
     * 
     * @return the ID of the current user
     */
    @Override
    @Transactional(readOnly = true)
    public Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        SpmsOidcUser user = (SpmsOidcUser) authentication.getPrincipal();
        return user.getAuthenticatedUser().getId();
    }

    /**
     * Retrieves the details of the currently authenticated user
     * 
     * @return UserModel containing current user details
     */
    public UserModel getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        SpmsOidcUser user = (SpmsOidcUser) authentication.getPrincipal();


        //TODO: add the execution context principal

        return user.getAuthenticatedUser();
    }

    /**
     * Checks if username already exists in system
     *
     * @param username Username to check
     * @return true if username exists, false otherwise
     * @throws IllegalArgumentException if username is null or empty
     */
    /**
     * Checks if a username already exists in the system
     * 
     * @param username the username to check
     * @return true if username exists, false otherwise
     * @throws IllegalArgumentException if username is null or empty
     */
    @Override
    @Transactional(readOnly = true)
    public boolean usernameExists(String username) {

        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        return repository.existsByUsername(username);
    }

    /**
     * Checks if an email already exists in the system
     * 
     * @param email the email to check
     * @return true if email exists, false otherwise
     */
    @Override
    public boolean emailExists(String email) {
        return repository.existsByEmail(email);
    }

    /**
     * Creates a new user with the provided details
     * 
     * @param userModel the user details to create
     * @return the created UserModel
     * @throws NullPointerException if username is null
     * @throws ValidationException if username or email already exists
     */
    @Override
    @Transactional
    public UserModel createUser(UserModel userModel) {

        if (userModel.getUsername() == null) {
            throw new NullPointerException("username shall not be nullable");
        }

        if (repository.existsByUsername(userModel.getUsername())) {
            throw new ValidationException("Username already exists");
        }
        if (repository.existsByEmail(userModel.getEmail())) {
            throw new ValidationException("Email already exists");
        }

        // Set createdBy to current authenticated user
        UserModel currentUser = getCurrentUser();
        userModel.setCreatedBy(currentUser.getUsername());

        return UserModel.fromEntity(this.repository.save(userModel.toEntityForCreate()));
    }

    /**
     * Retrieves a user by their ID
     * 
     * @param id the user ID to retrieve
     * @return the UserModel for the requested ID
     * @throws NotFoundException if no user is found with the given ID
     */
    @Override
    public UserModel getUserById(Long id) {
        return UserModel.fromEntity(repository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found")));
    }

    /**
     * Retrieves a user by their OAuth provider information
     * 
     * @param provider the OAuth provider name (e.g., "google")
     * @param providerId the user's ID from the OAuth provider
     * @return Optional containing UserModel if found, empty otherwise
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<UserModel> getUserByProviderInformation(String provider, String providerId) {
        Optional<User> user = repository.findByProviderAndProviderId(provider, providerId);
        if (user.isPresent()) {
            Map<String, String> userProfile = new HashMap<>(user.get().getUserProfiles());
            return Optional.of(UserModel.fromEntity(user.get(), userProfile));
        }
        return Optional.empty();
    }

    /**
     * Links an existing user to an OAuth provider
     * 
     * @param provider the OAuth provider name
     * @param providerId the user's ID from the OAuth provider
     * @param userName the username to link
     * @return Optional containing updated UserModel if successful, empty otherwise
     */
    @Override
    @Transactional()
    public Optional<UserModel> linkUserToProvider(String provider, String providerId, String userName) {
        User userByUserName = repository.findByUsername(userName);
        //Only Human can link to  && this user linked provider shall be pre-defined.
        if (userByUserName.getType() != User.UserType.MACHINE && provider.equals(userByUserName.getProvider())) {
            userByUserName.setProviderId(providerId);
        } else {
            return Optional.empty();
        }
        userByUserName = repository.save(userByUserName);
        Map<String, String> userProfile = new HashMap<>(userByUserName.getUserProfiles());
        return Optional.of(UserModel.fromEntity(repository.getReferenceById(userByUserName.getId()), userProfile));
    }

    /**
     * Retrieves a user by their username
     * 
     * @param userName the username to retrieve
     * @return UserModel for the requested username
     */
    @Override
    public UserModel getUserByUserName(String userName) {
        User user = repository.findByUsername(userName);
        return UserModel.fromEntity(user);
    }

    @Override
    public UserModelFulfilledSupporter getFulfilledSupporter() {
        return new UserModelFulfilledSupporterImpl(this);
    }
    
    @Override
    public long countUsers() {
        return repository.count();
    }

    @Override
    @Transactional
    public boolean assignRoles(UserModel userModel, List<RoleModel> roles) {
        User user = repository.findById(userModel.getId())
                .orElseThrow(() -> new NotFoundException("User not found"));
        
        // Convert RoleModel to Role entities
        List<Role> roleEntities = roles.stream()
                .map(roleModel -> roleRepository.findById(roleModel.getId())
                        .orElseThrow(() -> new NotFoundException("Role not found")))
                .toList();
        
        // Add roles to user
        user.getRoles().addAll(roleEntities);
        repository.save(user);
        return true;
    }

    @Override
    @Transactional
    public boolean unassignRoles(UserModel userModel, List<RoleModel> roles) {
        User user = repository.findById(userModel.getId())
                .orElseThrow(() -> new NotFoundException("User not found"));
        
        // Convert RoleModel to Role entities
        List<Role> roleEntities = roles.stream()
                .map(roleModel -> roleRepository.findById(roleModel.getId())
                        .orElseThrow(() -> new NotFoundException("Role not found")))
                .toList();
        
        // Remove roles from user
        user.getRoles().removeAll(roleEntities);
        repository.save(user);
        return true;
    }

    /**
     * Updates an existing user with new details
     * 
     * @param id the ID of the user to update
     * @param userModel the updated user details
     * @return the updated UserModel
     * @throws NotFoundException if no user is found with the given ID
     */
    @Override
    @Transactional
    public UserModel updateUser(Long id, UserModel userModel) {
        var existingUser = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));

        // Set modifiedBy to current authenticated user
        UserModel currentUser = getCurrentUser();
        userModel.setModifiedBy(currentUser.getUsername());
        userModel.setId(existingUser.getId());
        var entUpdate = userModel.toEntityForUpdate();
        return UserModel.fromEntity(repository.save(entUpdate));
    }

    /**
     * Deletes a user by ID
     * 
     * @param id the ID of the user to delete
     * @throws NotFoundException if no user is found with the given ID
     */
    @Override
    @Transactional
    public void deleteUser(Long id) {
        var user = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));
        repository.delete(user);
    }


    /**
     * Retrieves users filtered by department (Unused implementation)
     *
     * @param department Department to filter by
     * @return List of users in specified department
     * @deprecated Not currently used in any controller
     */
    @Deprecated
    public List<UserModel> getUserFilterByDepartment(DepartmentModel department) {
        Department dep = departmentRepository.getReferenceById(department.getId());
        return dep.getUsers().stream().map(UserModel::fromEntity).toList();
    }

    /**
     * Searches for users based on a query string with pagination
     * 
     * @param query the search query string
     * @param pageable pagination information
     * @return Page of UserModel results matching the query
     */
    @Override
    @Transactional(readOnly = true)
    public Page<UserModel> searchUsers(String query, Pageable pageable) {
        Specification<User> spec = Specification.where(null);

        if (query != null && !query.isEmpty()) {
            spec = spec.and(UserSpecifications.searchByQuery(query));
        }

        Page<User> users = repository.findAll(spec, pageable);
        return users.map(UserModel::fromEntity);
    }




}
