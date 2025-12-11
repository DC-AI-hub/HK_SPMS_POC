package com.spms.backend.service.sys.impl;

import com.spms.backend.repository.entities.idm.Role;
import com.spms.backend.repository.entities.sys.MenuEntity;
import com.spms.backend.repository.idm.RoleRepository;
import com.spms.backend.repository.sys.MenuRepository;
import com.spms.backend.service.exception.NotFoundException;
import com.spms.backend.service.exception.ValidationException;
import com.spms.backend.service.idm.UserService;
import com.spms.backend.service.model.MenuModel;
import com.spms.backend.service.model.idm.UserModel;
import com.spms.backend.service.sys.MenuService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class MenuServiceImpl implements MenuService {

    private final MenuRepository menuRepository;
    private final RoleRepository roleRepository;
    private final UserService userService;

    public MenuServiceImpl(MenuRepository menuRepository, RoleRepository roleRepository, UserService userService) {
        this.menuRepository = menuRepository;
        this.roleRepository = roleRepository;
        this.userService = userService;
    }

    @Override
    @Transactional
    public MenuModel createMenu(MenuModel menuModel) {
        // Validation logic
        if (menuRepository.existsByTitleAndPath(menuModel.getTitle(), menuModel.getPath())) {
            throw new ValidationException("Menu with this title and path already exists");
        }

        // Set audit fields
        UserModel currentUser = userService.getCurrentUser();
        menuModel.setCreatedBy(currentUser.getUsername());
        menuModel.setModifiedBy(currentUser.getUsername());
        menuModel.setCreatedAt(LocalDateTime.now());
        menuModel.setUpdatedAt(LocalDateTime.now());

        MenuEntity entity = menuModel.toEntity();

        // Handle parent menu
        if (menuModel.getParentMenuId() != null) {
            MenuEntity parentMenu = menuRepository.findById(menuModel.getParentMenuId())
                    .orElseThrow(() -> new NotFoundException("Parent menu not found with id: " + menuModel.getParentMenuId()));
            entity.setParentMenu(parentMenu);
        }

        // Handle role associations
        if (menuModel.getAllowedRoleIds() != null && !menuModel.getAllowedRoleIds().isEmpty()) {
            Set<Role> roles = new HashSet<>(roleRepository.findAllById(menuModel.getAllowedRoleIds()));
            if (roles.size() != menuModel.getAllowedRoleIds().size()) {
                throw new ValidationException("One or more role IDs are invalid");
            }
            entity.setAllowedRoles(roles);
        }

        MenuEntity savedEntity = menuRepository.save(entity);
        return MenuModel.fromEntity(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public MenuModel getMenuById(Long id) {
        MenuEntity menuEntity = menuRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Menu not found with id: " + id));
        return MenuModel.fromEntity(menuEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MenuModel> getAllMenus(Pageable pageable) {
        Page<MenuEntity> menuEntities = menuRepository.findAll(pageable);
        return menuEntities.map(MenuModel::fromEntity);
    }

    @Override
    @Transactional
    public MenuModel updateMenu(Long id, MenuModel menuModel) {
        MenuEntity existingMenu = menuRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Menu not found with id: " + id));

        // Check for duplicate title and path excluding current menu
        if (menuRepository.existsByTitleAndIdNot(menuModel.getTitle(), id)) {
            throw new ValidationException("Menu title already exists");
        }
        if (menuRepository.existsByPathAndIdNot(menuModel.getPath(), id)) {
            throw new ValidationException("Menu path already exists");
        }

        // Update fields
        existingMenu.setTitle(menuModel.getTitle());
        existingMenu.setPath(menuModel.getPath());
        existingMenu.setIcon(menuModel.getIcon());
        existingMenu.setDisplayOrder(menuModel.getDisplayOrder());
        existingMenu.setActive(menuModel.getActive());

        // Handle parent menu update
        if (menuModel.getParentMenuId() != null) {
            MenuEntity parentMenu = menuRepository.findById(menuModel.getParentMenuId())
                    .orElseThrow(() -> new NotFoundException("Parent menu not found with id: " + menuModel.getParentMenuId()));
            existingMenu.setParentMenu(parentMenu);
        } else {
            existingMenu.setParentMenu(null);
        }

        // Handle role associations update
        if (menuModel.getAllowedRoleIds() != null) {
            Set<Role> roles = new HashSet<>(roleRepository.findAllById(menuModel.getAllowedRoleIds()));
            if (roles.size() != menuModel.getAllowedRoleIds().size()) {
                throw new ValidationException("One or more role IDs are invalid");
            }
            existingMenu.setAllowedRoles(roles);
        }

        // Update audit fields
        UserModel currentUser = userService.getCurrentUser();
        existingMenu.setModifiedBy(currentUser.getUsername());
        existingMenu.setUpdatedAt(LocalDateTime.now());

        MenuEntity updatedEntity = menuRepository.save(existingMenu);
        return MenuModel.fromEntity(updatedEntity);
    }

    @Override
    @Transactional
    public void deleteMenu(Long id) {
        MenuEntity menuEntity = menuRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Menu not found with id: " + id));
        menuRepository.delete(menuEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MenuModel> getMenusForUser(Long userId) {
        // Get user's roles
        UserModel user = userService.getUserById(userId);
        Set<Long> userRoleIds = user.getRoles().stream()
                .map(role -> role.getId())
                .collect(Collectors.toSet());

        return menuRepository.findByAllowedRolesIdInAndActiveTrue(userRoleIds).stream()
                .map(MenuModel::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MenuModel> getMenusForCurrentUser() {
        UserModel currentUser = userService.getCurrentUser();
        Set<Long> userRoleIds = currentUser.getRoles().stream()
                .map(role -> role.getId())
                .collect(Collectors.toSet());

        return menuRepository.findByAllowedRolesIdInAndActiveTrue(userRoleIds).stream()
                .map(MenuModel::fromEntity)
                .sorted((m1, m2) -> Integer.compare(m1.getDisplayOrder(), m2.getDisplayOrder()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public MenuModel addRoleToMenu(Long menuId, Long roleId) {
        MenuEntity menuEntity = menuRepository.findById(menuId)
                .orElseThrow(() -> new NotFoundException("Menu not found with id: " + menuId));
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new NotFoundException("Role not found with id: " + roleId));

        menuEntity.getAllowedRoles().add(role);
        MenuEntity updatedEntity = menuRepository.save(menuEntity);
        return MenuModel.fromEntity(updatedEntity);
    }

    @Override
    @Transactional
    public MenuModel removeRoleFromMenu(Long menuId, Long roleId) {
        MenuEntity menuEntity = menuRepository.findById(menuId)
                .orElseThrow(() -> new NotFoundException("Menu not found with id: " + menuId));
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new NotFoundException("Role not found with id: " + roleId));

        menuEntity.getAllowedRoles().remove(role);
        MenuEntity updatedEntity = menuRepository.save(menuEntity);
        return MenuModel.fromEntity(updatedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean menuExistsByTitle(String title) {
        return menuRepository.existsByTitle(title);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean menuExistsByPath(String path) {
        return menuRepository.existsByPath(path);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean menuExistsByTitleAndPath(String title, String path) {
        return menuRepository.existsByTitleAndPath(title, path);
    }
}
