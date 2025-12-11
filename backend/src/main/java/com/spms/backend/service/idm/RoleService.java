package com.spms.backend.service.idm;

import com.spms.backend.service.model.idm.RoleModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Set;

/**
 * Service interface for role management operations
 */
public interface RoleService {
    /**
     * Creates a new role from provided model data
     * @param roleModel DTO containing role attributes
     * @return Created role model
     */
    RoleModel createRole(RoleModel roleModel);
    /**
     * Retrieves role details by identifier
     * @param id Role ID
     * @return Role model with full details
     */
    RoleModel getRoleById(Long id);
    /**
     * Retrieves paginated list of all roles
     * @param pageable Pagination configuration
     * @return Page of role models
     */
    Page<RoleModel> getAllRoles(Pageable pageable);
    /**
     * Updates existing role with new attributes
     * @param id Role ID to update
     * @param roleModel Updated role data
     * @return Updated role model
     */
    RoleModel updateRole(Long id, RoleModel roleModel);
    /**
     * Deletes role by identifier
     * @param id Role ID to delete
     */
    void deleteRole(Long id);
    /**
     * Adds permission to specified role
     * @param roleId Role ID to modify
     * @param permission Permission string to add
     * @return Updated role model
     */
    RoleModel addPermission(Long roleId, String permission);
    /**
     * Removes permission from specified role
     * @param roleId Role ID to modify
     * @param permission Permission string to remove
     * @return Updated role model
     */
    RoleModel removePermission(Long roleId, String permission);
    /**
     * Retrieves all permissions for specified role
     * @param id Role ID
     * @return Set of permission strings
     */
    Set<String> getRolePermissions(Long id);
    /**
     * Adds parent role relationship
     * @param roleId Child role ID
     * @param parentId Parent role ID
     * @return Updated role model with hierarchy
     */
    RoleModel addParentRole(Long roleId, Long parentId);
    /**
     * Removes parent role relationship
     * @param roleId Child role ID
     * @param parentId Parent role ID to remove
     * @return Updated role model
     */
    RoleModel removeParentRole(Long roleId, Long parentId);
    /**
     * Searches roles by name and description
     * @param name Partial name match
     * @param description Partial description match
     * @param pageable Pagination configuration
     * @return Page of matching role models
     */
    Page<RoleModel> searchRoles(String name, String description, Pageable pageable);
}
