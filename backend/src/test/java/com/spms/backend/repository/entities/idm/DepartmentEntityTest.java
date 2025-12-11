package com.spms.backend.repository.entities.idm;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DepartmentEntityTest {

    @Test
    void testDepartmentType() {
        Department department = new Department();
        department.setType(DepartmentType.FUNCTIONAL);
        assertEquals(DepartmentType.FUNCTIONAL, department.getType());
    }

    @Test
    void testDepartmentHeadRelationship() {
        Department department = new Department();
        User head = new User();
        department.setDepartmentHead(head);
        assertEquals(head, department.getDepartmentHead());
    }

    @Test
    void testUserRelationships() {
        Department department = new Department();
        Set<User> users = new HashSet<>();
        User user1 = new User();
        users.add(user1);
        
        department.setUsers(users);
        assertEquals(1, department.getUsers().size());
        assertTrue(department.getUsers().contains(user1));
    }

    @Test
    void testTagsMap() {
        Department department = new Department();
        Map<String, String> tags = new HashMap<>();
        tags.put("key1", "value1");
        
        department.setTags(tags);
        assertEquals(tags, department.getTags());
    }

    @Test
    void testTimestampFields() {
        Department department = new Department();
        LocalDateTime now = LocalDateTime.now();
        department.setCreatedAt(now);
        assertEquals(now, department.getCreatedAt());
    }

    @Test 
    void testDefaultActiveStatus() {
        Department department = new Department();
        assertTrue(department.getActive());
    }

    @Test
    void testParentField() {
        Department department = new Department();
        department.setParent(123L);
        assertEquals(123L, department.getParent());
    }

    @Test
    void testLevelField() {
        Department department = new Department();
        department.setLevel(2);
        assertEquals(2, department.getLevel());
    }

    @Test
    void testAuditFields() {
        Department department = new Department();
        department.setCreatedBy("admin");
        department.setUpdatedBy("user");
        assertEquals("admin", department.getCreatedBy());
        assertEquals("user", department.getUpdatedBy());
    }

    @Test
    void testTimestampBehavior() {
        Department department = new Department();
        department.onCreate();
        assertNotNull(department.getCreatedAt());
        assertNotNull(department.getUpdatedAt());
    }
}
