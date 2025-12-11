package com.spms.backend.controller.sys;

import com.spms.backend.controller.dto.sys.MenuRequestDTO;
import com.spms.backend.controller.dto.sys.MenuResponseDTO;
import com.spms.backend.service.sys.MenuService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for managing dynamic menu items with role-based access control
 */
@RestController
@RequestMapping("/api/v1/menus")
public class MenuController extends com.spms.backend.controller.BaseController {

    private final MenuService menuService;

    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    /**
     * Create a new menu item
     */
    @PostMapping
    //@PreAuthorize("hasAuthority('menu:write')")
    public ResponseEntity<MenuResponseDTO> createMenu(@Valid @RequestBody MenuRequestDTO menuRequest) {
        var menuModel = menuRequest.toMenuModel();
        var createdMenu = menuService.createMenu(menuModel);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(MenuResponseDTO.fromMenuModel(createdMenu));
    }

    /**
     * Get menu by ID
     */
    @GetMapping("/{id}")
    //@PreAuthorize("hasAuthority('menu:read')")
    public ResponseEntity<MenuResponseDTO> getMenuById(@PathVariable Long id) {
        var menuModel = menuService.getMenuById(id);
        return ResponseEntity.ok(MenuResponseDTO.fromMenuModel(menuModel));
    }

    /**
     * Get all menus with pagination
     */
    @GetMapping
    //@PreAuthorize("hasAuthority('menu:read')")
    public ResponseEntity<Page<MenuResponseDTO>> getAllMenus(Pageable pageable) {
        var menuModels = menuService.getAllMenus(pageable);
        var response = menuModels.map(MenuResponseDTO::fromMenuModel);
        return ResponseEntity.ok(response);
    }

    /**
     * Update menu item
     */
    @PutMapping("/{id}")
    //@PreAuthorize("hasAuthority('menu:write')")
    public ResponseEntity<MenuResponseDTO> updateMenu(
            @PathVariable Long id,
            @Valid @RequestBody MenuRequestDTO menuRequest) {
        var menuModel = menuRequest.toMenuModel();
        var updatedMenu = menuService.updateMenu(id, menuModel);
        return ResponseEntity.ok(MenuResponseDTO.fromMenuModel(updatedMenu));
    }

    /**
     * Delete menu item
     */
    @DeleteMapping("/{id}")
    //@PreAuthorize("hasAuthority('menu:write')")
    public ResponseEntity<Void> deleteMenu(@PathVariable Long id) {
        menuService.deleteMenu(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get menus for current user based on their roles
     */
    @GetMapping("/user/current")
    public ResponseEntity<List<MenuResponseDTO>> getMenusForCurrentUser() {
        var menuModels = menuService.getMenusForCurrentUser();
        var response = menuModels.stream()
                .map(MenuResponseDTO::fromMenuModel)
                .toList();
        return ResponseEntity.ok(response);
    }

    /**
     * Get menus for specific user (admin only)
     */
    @GetMapping("/user/{userId}")
    //@PreAuthorize("hasAuthority('menu:read')")
    public ResponseEntity<List<MenuResponseDTO>> getMenusForUser(@PathVariable Long userId) {
        var menuModels = menuService.getMenusForUser(userId);
        var response = menuModels.stream()
                .map(MenuResponseDTO::fromMenuModel)
                .toList();
        return ResponseEntity.ok(response);
    }

    /**
     * Add role to menu
     */
    @PostMapping("/{menuId}/roles/{roleId}")
    //@PreAuthorize("hasAuthority('menu:write')")
    public ResponseEntity<MenuResponseDTO> addRoleToMenu(
            @PathVariable Long menuId,
            @PathVariable Long roleId) {
        var updatedMenu = menuService.addRoleToMenu(menuId, roleId);
        return ResponseEntity.ok(MenuResponseDTO.fromMenuModel(updatedMenu));
    }

    /**
     * Remove role from menu
     */
    @DeleteMapping("/{menuId}/roles/{roleId}")
    //@PreAuthorize("hasAuthority('menu:write')")
    public ResponseEntity<MenuResponseDTO> removeRoleFromMenu(
            @PathVariable Long menuId,
            @PathVariable Long roleId) {
        var updatedMenu = menuService.removeRoleFromMenu(menuId, roleId);
        return ResponseEntity.ok(MenuResponseDTO.fromMenuModel(updatedMenu));
    }
}
