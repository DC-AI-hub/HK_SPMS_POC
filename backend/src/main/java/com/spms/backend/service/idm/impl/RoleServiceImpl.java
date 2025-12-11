package com.spms.backend.service.idm.impl;

import com.spms.backend.service.exception.NotFoundException;
import com.spms.backend.service.exception.ValidationException;

import com.spms.backend.repository.entities.idm.Role;
import com.spms.backend.repository.idm.RoleRepository;
import com.spms.backend.service.idm.RoleService;
import com.spms.backend.service.model.idm.RoleModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Set;

@Service
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;
    private static final Logger logger = LoggerFactory.getLogger(RoleServiceImpl.class);

    /**
     * Constructs RoleService with required repository
     *
     * @param roleRepository Role data access repository
     */
    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    /**
     * Creates new role after validating input and checking uniqueness
     *
     * @param roleModel Role data transfer object
     * @return Persisted role model
     * @throws NullPointerException if name/description missing
     * @throws ValidationException  if role name exists
     * @Transactional - Writes to database
     */
    @Override
    @Transactional
    public RoleModel createRole(RoleModel roleModel) {
        if (roleModel.getName() == null || roleModel.getDescription() == null) {
            throw new NullPointerException("Name and description cannot be null");
        }


        if (roleRepository.existsByName(roleModel.getName())) {
            throw new ValidationException("Role with this name already exists");
        }
        return RoleModel.fromEntity(roleRepository.save(roleModel.toEntityForCreate()));
    }

    /**
     * Retrieves role by ID with parent/child relationships
     *
     * @param id Role identifier
     * @return Role model with hierarchy relationships
     * @throws NotFoundException if role doesn't exist
     * @Transactional(readOnly=true) - Read-only operation
     */
    @Override
    @Transactional
    public RoleModel getRoleById(Long id) {
        Role role = roleRepository.findByIdWithRelations(id)
                .orElseThrow(() -> new NotFoundException("Role not found"));
        return RoleModel.fromEntity(role,
                role.getParentRoles().stream().toList(),
                role.getChildRoles().stream().toList());
    }

    /**
     * Retrieves paginated list of all roles
     *
     * @param pageable Pagination configuration
     * @return Page of role models
     */
    @Override
    @Transactional(readOnly = true)
    public Page<RoleModel> getAllRoles(Pageable pageable) {
        return roleRepository.findAll(pageable)
                .map(RoleModel::fromEntity);
    }

    /**
     * Updates existing role attributes after validation
     *
     * @param id        Role ID to update
     * @param roleModel Updated role data
     * @return Updated role model
     * @throws NotFoundException   if role doesn't exist
     * @throws ValidationException if new name conflicts
     */
    @Override
    @Transactional
    public RoleModel updateRole(Long id, RoleModel roleModel) {
        Role existingRole = roleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Role not found"));

        // Check for duplicate name, excluding current role
        if (!existingRole.getName().equals(roleModel.getName()) &&
                roleRepository.existsByName(roleModel.getName())) {
            throw new ValidationException("Role with this name already exists");
        }

        roleModel.setId(existingRole.getId());
        return RoleModel.fromEntity(roleRepository.save(roleModel.toEntityForUpdate()));
    }

    /**
     * Deletes role by identifier
     *
     * @param id Role ID to delete
     * @Transactional - Writes to database
     */
    @Override
    @Transactional
    public void deleteRole(Long id) {
        RoleModel roleModel = getRoleById(id);
        roleRepository.delete(roleModel.toEntityForUpdate());
    }

    /**
     * Adds permission to specified role
     *
     * @param roleId     Role ID to modify
     * @param permission Permission string to add
     * @return Updated role model
     * @Transactional - Writes to database
     */
    @Override
    @Transactional
    public RoleModel addPermission(Long roleId, String permission) {
        RoleModel roleModel = getRoleById(roleId);
        Role role = roleModel.toEntityForUpdate();
        role.getPermissions().add(permission);
        return RoleModel.fromEntity(roleRepository.save(role));
    }

    /**
     * Removes permission from specified role
     *
     * @param roleId     Role ID to modify
     * @param permission Permission string to remove
     * @return Updated role model
     * @throws ValidationException if permission not found
     * @Transactional - Writes to database
     */
    @Override
    @Transactional
    public RoleModel removePermission(Long roleId, String permission) {
        RoleModel roleModel = getRoleById(roleId);
        Role role = roleModel.toEntityForUpdate();
        if (role.getPermissions().contains(permission)) {
            role.getPermissions().remove(permission);
        } else {
            throw new ValidationException("permission is not found");
        }
        return RoleModel.fromEntity(roleRepository.save(role));
    }

    /**
     * Retrieves all permissions for specified role
     *
     * @param id Role ID
     * @return Set of permission strings
     * @Transactional(readOnly=true) - Read-only operation
     */
    @Override
    @Transactional(readOnly = true)
    public Set<String> getRolePermissions(Long id) {
        RoleModel roleModel = getRoleById(id);
        return roleModel.getPermissions();
    }

    /**
     * Establishes parent-child relationship between roles
     *
     * @param roleId   Child role ID
     * @param parentId Parent role ID
     * @return Updated role model with hierarchy
     * @throws ValidationException if self-reference or duplicate
     * @throws NotFoundException   if roles not found
     * @Transactional - Writes to database
     */
    @Override
    @Transactional
    public RoleModel addParentRole(Long roleId, Long parentId) {
        if (roleId.equals(parentId)) {
            throw new ValidationException("Cannot add self as parent role");
        }

        Role role = roleRepository.findByIdWithRelations(roleId)
                .orElseThrow(() -> new NotFoundException("Role not found"));
        Role parentRole = roleRepository.findById(parentId)
                .orElseThrow(() -> new NotFoundException("Parent role not found"));

        // Check both in-memory and database for existing relationship
        boolean relationshipExists = role.getParentRoles().stream()
                .anyMatch(r -> r.getId().equals(parentId)) ||
                roleRepository.existsByChildIdAndParentId(roleId, parentId);

        if (relationshipExists) {
            throw new ValidationException("Parent role already exists");
        }

        role.getParentRoles().add(parentRole);
        Role savedRole = roleRepository.save(role);
        Role response = roleRepository.findByIdWithRelations(savedRole.getId()).get();

        // Refresh the entity to ensure relationships are loaded
        return RoleModel.fromEntity(
                response,
                response.getParentRoles().stream().toList(),
                response.getChildRoles().stream().toList()
        );
    }

    /**
     * Removes parent-child relationship between roles
     *
     * @param roleId   Child role ID
     * @param parentId Parent role ID to remove
     * @return Updated role model
     * @throws ValidationException if relationship doesn't exist
     * @Transactional - Writes to database
     */
    @Override
    @Transactional
    public RoleModel removeParentRole(Long roleId, Long parentId) {
        RoleModel roleModel = getRoleById(roleId);
        RoleModel parentModel = getRoleById(parentId);

        boolean parentExists = roleModel.getParentRoles().stream()
                .anyMatch(parent -> parent.getId().equals(parentId));

        if (!parentExists) {
            throw new ValidationException("Parent role does not exist");
        }

        roleModel.getParentRoles().removeIf(parent -> parent.getId().equals(parentId));
        return RoleModel.fromEntity(
                roleRepository.save(roleModel.toEntityForUpdate())
        );
    }

    /**
     * Searches roles by name/description with pagination
     *
     * @param name        Partial name match
     * @param description Partial description match
     * @param pageable    Pagination configuration
     * @return Page of matching role models
     * @Transactional(readOnly=true) - Read-only operation
     */
    @Override
    @Transactional(readOnly = true)
    public Page<RoleModel> searchRoles(String name, String description, Pageable pageable) {

        if (StringUtils.hasLength(name) && StringUtils.hasLength(description)) {

            return roleRepository.findByNameContainingIgnoreCaseAndDescriptionContainingIgnoreCase(
                            name, description, pageable)
                    .map(RoleModel::fromEntity);
        }
        return this.getAllRoles(pageable);
    }

}
