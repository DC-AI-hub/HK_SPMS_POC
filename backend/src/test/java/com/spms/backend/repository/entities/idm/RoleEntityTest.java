package com.spms.backend.repository.entities.idm;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import java.util.Set;

public class RoleEntityTest {

    @Test
    void testNameField() {
        Role role = new Role();
        role.setName("Admin");
        assertEquals("Admin", role.getName());
    }

    @Test
    void testDescriptionField() {
        Role role = new Role();
        role.setDescription("Administrator role");
        assertEquals("Administrator role", role.getDescription());
    }

    @Test
    void testPermissionsCollection() {
        Role role = new Role();
        Set<String> perms = new HashSet<>();
        perms.add("users.read");
        perms.add("users.write");
        
        role.setPermissions(perms);
        assertEquals(2, role.getPermissions().size());
        assertTrue(role.getPermissions().contains("users.read"));
    }

    @Test
    void testRoleHierarchy() {
        Role parent = new Role("Parent");
        Role child = new Role("Child");
        
        child.getParentRoles().add(parent);
        parent.getChildRoles().add(child);
        assertEquals(1, child.getParentRoles().size());
        assertEquals(1, parent.getChildRoles().size());
    }

    @Test
    void testAuditFields() {
        Role role = new Role();
        role.setActive(true);
        role.setCreatedBy("admin");
        role.setUpdatedBy("user");
        
        assertTrue(role.getActive());
        assertEquals("admin", role.getCreatedBy());
        assertEquals("user", role.getUpdatedBy());
    }

    @Test
    void testGetAllPermissions() {
        Role parent = new Role("Parent");
        parent.getPermissions().add("users.read");
        
        Role child = new Role("Child");
        child.getPermissions().add("users.write");
        child.getParentRoles().add(parent);
        
        Set<String> allPerms = child.getAllPermissions();
        assertEquals(2, allPerms.size());
        assertTrue(allPerms.contains("users.read"));
        assertTrue(allPerms.contains("users.write"));
    }
}
