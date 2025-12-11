package com.spms.backend.controller.idm;

import com.spms.backend.controller.BaseController;

import com.spms.backend.controller.dto.idm.RoleDTO;
import com.spms.backend.service.idm.RoleService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/v1/roles")
public class RoleController extends BaseController {
    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RoleDTO> createRole(@RequestBody RoleDTO roleDTO) {
        return ResponseEntity.ok(RoleDTO.fromRoleModel(
            roleService.createRole(roleDTO.toRoleModel())
        ));
    }

    @GetMapping("/{id}")
    //@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<RoleDTO> getRole(@PathVariable Long id) {
        return ResponseEntity.ok(RoleDTO.fromRoleModel(
            roleService.getRoleById(id)
        ));
    }

    @GetMapping
    //@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Page<RoleDTO>> getAllRoles(Pageable pageable) {
        return ResponseEntity.ok(roleService.getAllRoles(pageable)
            .map(RoleDTO::fromRoleModel));
    }

    @PutMapping("/{id}")
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RoleDTO> updateRole(
            @PathVariable Long id, @RequestBody RoleDTO roleDTO) {
        return ResponseEntity.ok(RoleDTO.fromRoleModel(
            roleService.updateRole(id, roleDTO.toRoleModel())
        ));
    }

    @DeleteMapping("/{id}")
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/permissions")
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RoleDTO> addPermission(
            @PathVariable Long id, @RequestParam String permission) {
        return ResponseEntity.ok(RoleDTO.fromRoleModel(
            roleService.addPermission(id, permission)
        ));
    }

    @DeleteMapping("/{id}/permissions")
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RoleDTO> removePermission(
            @PathVariable Long id, @RequestParam String permission) {
        return ResponseEntity.ok(RoleDTO.fromRoleModel(
            roleService.removePermission(id, permission)
        ));
    }

    @GetMapping("/{id}/permissions")
   // @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Set<String>> getRolePermissions(@PathVariable Long id) {
        return ResponseEntity.ok(roleService.getRolePermissions(id));
    }

    @PostMapping("/{id}/parents")
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RoleDTO> addParentRole(
            @PathVariable Long id, @RequestParam(name = "parentId") Long parentId) {
        return ResponseEntity.ok(RoleDTO.fromRoleModel(
            roleService.addParentRole(id, parentId)
        ));
    }

    @DeleteMapping("/{id}/parents")
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RoleDTO> removeParentRole(
            @PathVariable Long id, @RequestParam Long parentId) {
        return ResponseEntity.ok(RoleDTO.fromRoleModel(
            roleService.removeParentRole(id, parentId)
        ));
    }

    @GetMapping("/search")
    //@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Page<RoleDTO>> searchRoles(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String description,
            Pageable pageable) {
        return ResponseEntity.ok(roleService.searchRoles(name, description, pageable)
            .map(RoleDTO::fromRoleModel));
    }
}
