package com.spms.backend.service;

import com.spms.backend.service.exception.NotFoundException;
import com.spms.backend.service.exception.ValidationException;
import com.spms.backend.repository.entities.idm.Role;
import com.spms.backend.repository.idm.RoleRepository;
import com.spms.backend.service.idm.RoleService;
import com.spms.backend.service.model.idm.RoleModel;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = "/application-test.properties")
public class RoleServiceIntegrationTest {

    @MockitoBean
    private ClientRegistrationRepository clientRegistrationRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private RoleService roleService;

    private Role adminRole;
    private Role userRole;
    private Role guestRole;

    @BeforeEach
    void setUp() {

        this.adminRole = new Role();
        adminRole.setName("ADMIN");
        adminRole.setDescription("Administrator role");
        roleRepository.save(adminRole);

        this.userRole = new Role();
        userRole.setName("USER");
        userRole.setDescription("Standard user role");
        roleRepository.save(userRole);

        this.guestRole = new Role();
        guestRole.setName("GUEST");
        guestRole.setDescription("Guest role");
        roleRepository.save(guestRole);

        userRole.setChildRoles(Set.of(guestRole));
        userRole.setParentRoles(Set.of(adminRole));
        userRole.setPermissions(Set.of("BASIC_ACCESS"));
        roleRepository.save(userRole);

        adminRole.setPermissions(Set.of("ALL_PERMISSIONS"));
        roleRepository.save(adminRole);



    }

    @AfterEach
    void tearDown() {
        roleRepository.deleteAll();
    }

    private RoleModel createTestRole(String name, String description) {
        RoleModel role = new RoleModel();
        role.setName(name);
        role.setDescription(description);
        return roleService.createRole(role);
    }

    @Test
    void createRole_validInput_shouldCreateRole() {
        RoleModel newRole = new RoleModel();
        newRole.setName("TEST_ROLE");
        newRole.setDescription("Test role description");
        
        RoleModel created = roleService.createRole(newRole);
        
        assertNotNull(created.getId());
        assertEquals("TEST_ROLE", created.getName());
        assertEquals("Test role description", created.getDescription());
    }

    @Test
    void createRole_duplicateName_shouldThrowValidationException() {
        RoleModel duplicateRole = new RoleModel();
        duplicateRole.setName("ADMIN"); // Duplicate from setup
        duplicateRole.setDescription("Duplicate role");
        
        assertThrows(ValidationException.class, () -> {
            roleService.createRole(duplicateRole);
        });
    }

    @Test
    void createRole_nullValues_shouldThrowNullPointerException() {
        RoleModel invalidRole = new RoleModel();
        invalidRole.setName(null);
        invalidRole.setDescription(null);
        
        assertThrows(NullPointerException.class, () -> {
            roleService.createRole(invalidRole);
        });
    }

    @Test
    void getRoleById_validId_shouldReturnRole() {
        RoleModel result = roleService.getRoleById(adminRole.getId());
        
        assertEquals("ADMIN", result.getName());
        assertEquals("Administrator role", result.getDescription());
        assertTrue(result.getPermissions().contains("ALL_PERMISSIONS"));
    }

    @Test
    void getRoleById_invalidId_shouldThrowNotFoundException() {
        assertThrows(NotFoundException.class, () -> {
            roleService.getRoleById(-1L);
        });
    }

    @Test
    void getAllRoles_shouldReturnPaginatedResults() {
        Page<RoleModel> result = roleService.getAllRoles(PageRequest.of(0, 2));
        
        assertEquals(3, result.getTotalElements());
        assertEquals(2, result.getContent().size());
    }

    @Test
    void getAllRoles_withSorting_shouldReturnSortedResults() {
        Page<RoleModel> result = roleService.getAllRoles(
            PageRequest.of(0, 10, Sort.by("name").descending()));
        
        assertEquals("USER", result.getContent().get(0).getName());
        assertEquals("GUEST", result.getContent().get(1).getName());
    }

    @Test
    @WithMockUser(username = "test")
    void updateRole_validUpdate_shouldUpdateRole() {
        RoleModel updateData = new RoleModel();
        updateData.setName("ADMIN_UPDATED");
        updateData.setDescription("Updated admin role");
        
        RoleModel updated = roleService.updateRole(adminRole.getId(), updateData);
        
        assertEquals("ADMIN_UPDATED", updated.getName());
        assertEquals("Updated admin role", updated.getDescription());
        
        // Verify database state
        RoleModel dbRole = roleService.getRoleById(adminRole.getId());
        assertEquals("ADMIN_UPDATED", dbRole.getName());
    }

    @Test
    void updateRole_nonExistentRole_shouldThrowNotFoundException() {
        RoleModel updateData = new RoleModel();
        updateData.setName("NON_EXISTENT");
        
        assertThrows(NotFoundException.class, () -> {
            roleService.updateRole(-1L, updateData);
        });
    }

    @Test
    void updateRole_duplicateName_shouldThrowValidationException() {
        RoleModel updateData = new RoleModel();
        updateData.setName("USER"); // Duplicate of userRole
        
        assertThrows(ValidationException.class, () -> {
            roleService.updateRole(adminRole.getId(), updateData);
        });
    }

    @Test
    @WithMockUser(username = "test")
    void deleteRole_validId_shouldDeleteRole() {
        roleService.deleteRole(guestRole.getId());
        
        assertFalse(roleRepository.existsById(guestRole.getId()));
    }

    @Test
    void deleteRole_nonExistentId_shouldThrowNotFoundException() {
        assertThrows(NotFoundException.class, () -> {
            roleService.deleteRole(-1L);
        });
    }

    @Test
    void addPermission_validInput_shouldAddPermission() {
        roleService.addPermission(userRole.getId(), "EXTRA_PERMISSION");
        
        Set<String> permissions = roleService.getRolePermissions(userRole.getId());
        assertTrue(permissions.contains("EXTRA_PERMISSION"));
        assertTrue(permissions.contains("BASIC_ACCESS"));
    }

    @Test
    void removePermission_validInput_shouldRemovePermission() {
        roleService.removePermission(userRole.getId(), "BASIC_ACCESS");
        
        Set<String> permissions = roleService.getRolePermissions(userRole.getId());
        assertFalse(permissions.contains("BASIC_ACCESS"));
    }

    @Test
    void removePermission_nonExistentPermission_shouldThrowValidationException() {
        assertThrows(ValidationException.class, () -> {
            roleService.removePermission(userRole.getId(), "NON_EXISTENT");
        });
    }

    @Test
    void getRolePermissions_shouldReturnAllPermissions() {
        Set<String> permissions = roleService.getRolePermissions(adminRole.getId());
        
        assertEquals(1, permissions.size());
        assertTrue(permissions.contains("ALL_PERMISSIONS"));
    }

    @Test
    void addParentRole_validInput_shouldAddRelationship() {
        roleService.addParentRole(guestRole.getId(), adminRole.getId());
        
        RoleModel updatedGuest = roleService.getRoleById(guestRole.getId());
        assertTrue(updatedGuest.getParentRoles().stream()
            .anyMatch(r -> r.getId().equals(adminRole.getId())));
    }

    @Test
    void addParentRole_selfReference_shouldThrowValidationException() {
        assertThrows(ValidationException.class, () -> {
            roleService.addParentRole(adminRole.getId(), adminRole.getId());
        });
    }

    @Test
    void addParentRole_duplicateRelationship_shouldThrowValidationException() {
        assertThrows(ValidationException.class, () -> {
            roleService.addParentRole(userRole.getId(), adminRole.getId());
        });
    }

    @Test
    void removeParentRole_validInput_shouldRemoveRelationship() {
        roleService.removeParentRole(userRole.getId(), adminRole.getId());
        
        RoleModel updatedUser = roleService.getRoleById(userRole.getId());
        assertFalse(updatedUser.getParentRoles().stream()
            .anyMatch(r -> r.getId().equals(adminRole.getId())));
    }

    @Test
    void removeParentRole_nonExistentRelationship_shouldThrowValidationException() {
        assertThrows(ValidationException.class, () -> {
            roleService.removeParentRole(guestRole.getId(), adminRole.getId());
        });
    }

    @Test
    void searchRoles_byName_shouldReturnMatchingRoles() {
        Page<RoleModel> result = roleService.searchRoles(
            "ADM", "", PageRequest.of(0, 10));
        
        assertEquals(1, result.getTotalElements());
        assertEquals("ADMIN", result.getContent().get(0).getName());
    }

    @Test
    void searchRoles_byDescription_shouldReturnMatchingRoles() {
        Page<RoleModel> result = roleService.searchRoles(
            "", "Standard", PageRequest.of(0, 10));
        
        assertEquals(1, result.getTotalElements());
        assertEquals("USER", result.getContent().get(0).getName());
    }

    @Test
    void searchRoles_emptyQuery_shouldReturnAllRoles() {
        Page<RoleModel> result = roleService.searchRoles(
            "", "", PageRequest.of(0, 10));
        
        assertEquals(3, result.getTotalElements());
    }
}
