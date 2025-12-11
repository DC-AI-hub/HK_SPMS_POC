package com.spms.backend.dto;

import com.spms.backend.controller.dto.idm.RoleDTO;
import com.spms.backend.service.model.idm.RoleModel;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class RoleDTOTest {

    @Test
    void testFieldGettersAndSetters() {
        RoleDTO dto = new RoleDTO();
        
        Long testId = 1L;
        dto.setId(testId);
        assertEquals(testId, dto.getId());

        String testName = "Admin";
        dto.setName(testName);
        assertEquals(testName, dto.getName());

        String testDescription = "Administrator role";
        dto.setDescription(testDescription);
        assertEquals(testDescription, dto.getDescription());

        Set<String> testPermissions = new HashSet<>();
        testPermissions.add("user.create");
        testPermissions.add("user.delete");
        dto.setPermissions(testPermissions);
        assertEquals(testPermissions, dto.getPermissions());

        Set<RoleDTO> testParentRoles = new HashSet<>();
        dto.setParentRoles(testParentRoles);
        assertEquals(testParentRoles, dto.getParentRoles());

        Set<RoleDTO> testChildRoles = new HashSet<>();
        dto.setChildRoles(testChildRoles);
        assertEquals(testChildRoles, dto.getChildRoles());

        Boolean testActive = true;
        dto.setActive(testActive);
        assertEquals(testActive, dto.getActive());

        Date testLastModified = new Date();
        dto.setLastModified(testLastModified.getTime());
        assertEquals(testLastModified, dto.getLastModified());

        String testCreatedBy = "system";
        dto.setCreatedBy(testCreatedBy);
        assertEquals(testCreatedBy, dto.getCreatedBy());

        String testUpdatedBy = "admin";
        dto.setUpdatedBy(testUpdatedBy);
        assertEquals(testUpdatedBy, dto.getUpdatedBy());

        Date testCreatedTime = new Date();
        dto.setCreatedTime(testCreatedTime.getTime());
        assertEquals(testCreatedTime.getTime(), dto.getCreatedTime());
    }

    @Test
    void testFromRoleModelConversion() {
        RoleModel model = new RoleModel();
        model.setId(1L);
        model.setName("Admin");
        model.setDescription("Administrator role");
        
        Set<String> permissions = new HashSet<>();
        permissions.add("user.create");
        permissions.add("user.delete");
        model.setPermissions(permissions);

        // Set audit fields
        model.setActive(true);
        model.setLastModified(new Date().getTime());
        model.setCreatedBy("system");
        model.setUpdatedBy("admin");
        model.setCreatedTime(new Date().getTime());

        // Test empty parent/child roles
        RoleDTO dto = RoleDTO.fromRoleModel(model);
        assertEquals(model.getId(), dto.getId());
        assertEquals(model.getName(), dto.getName());
        assertEquals(model.getDescription(), dto.getDescription());
        assertEquals(model.getPermissions(), dto.getPermissions());
        assertNull(dto.getParentRoles());
        assertNull(dto.getChildRoles());
        assertEquals(model.getActive(), dto.getActive());
        assertEquals(model.getLastModified(), dto.getLastModified());
        assertEquals(model.getCreatedBy(), dto.getCreatedBy());
        assertEquals(model.getUpdatedBy(), dto.getUpdatedBy());
        assertEquals(model.getCreatedTime(), dto.getCreatedTime());

        // Test with role hierarchy
        Set<RoleModel> parentRoles = new HashSet<>();
        RoleModel parentRole = new RoleModel();
        parentRole.setId(2L);
        parentRole.setName("SuperAdmin");
        parentRoles.add(parentRole);
        model.setParentRoles(parentRoles);

        Set<RoleModel> childRoles = new HashSet<>();
        RoleModel childRole = new RoleModel();
        childRole.setId(3L);
        childRole.setName("LimitedAdmin");
        childRoles.add(childRole);
        model.setChildRoles(childRoles);

        dto = RoleDTO.fromRoleModel(model);
        assertNotNull(dto.getParentRoles());
        assertEquals(1, dto.getParentRoles().size());
        assertEquals("SuperAdmin", dto.getParentRoles().iterator().next().getName());
        
        assertNotNull(dto.getChildRoles());
        assertEquals(1, dto.getChildRoles().size());
        assertEquals("LimitedAdmin", dto.getChildRoles().iterator().next().getName());
    }

    @Test
    void testToRoleModelConversion() {
        RoleDTO dto = new RoleDTO();
        dto.setId(1L);
        dto.setName("Admin");
        dto.setDescription("Administrator role");
        
        Set<String> permissions = new HashSet<>();
        permissions.add("user.create");
        permissions.add("user.delete");
        dto.setPermissions(permissions);

        // Set audit fields
        dto.setActive(true);
        dto.setLastModified(new Date().getTime());
        dto.setCreatedBy("system");
        dto.setUpdatedBy("admin");
        dto.setCreatedTime(new Date().getTime());

        // Test empty parent/child roles
        RoleModel model = dto.toRoleModel();
        assertEquals(dto.getId(), model.getId());
        assertEquals(dto.getName(), model.getName());
        assertEquals(dto.getDescription(), model.getDescription());
        assertEquals(dto.getPermissions(), model.getPermissions());
        assertNull(model.getParentRoles());
        assertNull(model.getChildRoles());
        assertEquals(dto.getActive(), model.getActive());
        assertEquals(dto.getLastModified(), model.getLastModified());
        assertEquals(dto.getCreatedBy(), model.getCreatedBy());
        assertEquals(dto.getUpdatedBy(), model.getUpdatedBy());
        assertEquals(dto.getCreatedTime(), model.getCreatedTime());

        // Test with role hierarchy
        Set<RoleDTO> parentRoles = new HashSet<>();
        RoleDTO parentRole = new RoleDTO();
        parentRole.setId(2L);
        parentRole.setName("SuperAdmin");
        parentRoles.add(parentRole);
        dto.setParentRoles(parentRoles);

        Set<RoleDTO> childRoles = new HashSet<>();
        RoleDTO childRole = new RoleDTO();
        childRole.setId(3L);
        childRole.setName("LimitedAdmin");
        childRoles.add(childRole);
        dto.setChildRoles(childRoles);

        model = dto.toRoleModel();
        assertNotNull(model.getParentRoles());
        assertEquals(1, model.getParentRoles().size());
        assertEquals("SuperAdmin", model.getParentRoles().iterator().next().getName());
        
        assertNotNull(model.getChildRoles());
        assertEquals(1, model.getChildRoles().size());
        assertEquals("LimitedAdmin", model.getChildRoles().iterator().next().getName());
    }

    @Test
    void testNullHandlingInConversions() {
        // Test null collections
        RoleDTO dto = new RoleDTO();
        dto.setPermissions(null);
        dto.setParentRoles(null);
        dto.setChildRoles(null);

        RoleModel model = dto.toRoleModel();
        assertNull(model.getPermissions());
        assertNull(model.getParentRoles());
        assertNull(model.getChildRoles());

        RoleDTO convertedBack = RoleDTO.fromRoleModel(model);
        assertNull(convertedBack.getPermissions());
        assertNull(convertedBack.getParentRoles());
        assertNull(convertedBack.getChildRoles());
    }

    @Test
    void testEmptyCollectionsHandling() {
        // Test empty collections
        RoleDTO dto = new RoleDTO();
        dto.setPermissions(new HashSet<>());
        dto.setParentRoles(new HashSet<>());
        dto.setChildRoles(new HashSet<>());

        RoleModel model = dto.toRoleModel();
        assertNotNull(model.getPermissions());
        assertTrue(model.getPermissions().isEmpty());
        assertNotNull(model.getParentRoles());
        assertTrue(model.getParentRoles().isEmpty());
        assertNotNull(model.getChildRoles());
        assertTrue(model.getChildRoles().isEmpty());
    }
}
