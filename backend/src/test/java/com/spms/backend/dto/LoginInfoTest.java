package com.spms.backend.dto;

import com.spms.backend.controller.dto.sys.LoginInfo;
import com.spms.backend.repository.entities.idm.Role;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LoginInfoTest {

    @Test
    void testFieldGettersAndSetters() {
        LoginInfo loginInfo = new LoginInfo();
        
        String testUsername = "testuser";
        loginInfo.setUsername(testUsername);
        assertEquals(testUsername, loginInfo.getUsername());

        String testEmail = "test@example.com";
        loginInfo.setEmail(testEmail);
        assertEquals(testEmail, loginInfo.getEmail());

        String testFirstName = "Test";
        loginInfo.setFirstName(testFirstName);
        assertEquals(testFirstName, loginInfo.getFirstName());

        String testLastName = "User";
        loginInfo.setLastName(testLastName);
        assertEquals(testLastName, loginInfo.getLastName());

        Role testRole = new Role();
        testRole.setName("ADMIN");
        List<Role> testRoles = Collections.singletonList(testRole);
        loginInfo.setRoles(testRoles);
        assertEquals(testRoles, loginInfo.getRoles());
    }

    @Test
    void testNullHandling() {
        LoginInfo loginInfo = new LoginInfo();
        loginInfo.setFirstName(null);
        loginInfo.setLastName(null);
        loginInfo.setRoles(null);

        assertNull(loginInfo.getFirstName());
        assertNull(loginInfo.getLastName());
        assertNull(loginInfo.getRoles());
    }

    @Test
    void testEmptyRolesHandling() {
        LoginInfo loginInfo = new LoginInfo();
        loginInfo.setRoles(Collections.emptyList());

        assertNotNull(loginInfo.getRoles());
        assertTrue(loginInfo.getRoles().isEmpty());
    }

    @Test
    void testMultipleRolesHandling() {
        Role role1 = new Role();
        role1.setName("ADMIN");
        
        Role role2 = new Role();
        role2.setName("USER");
        
        List<Role> roles = Arrays.asList(role1, role2);
        LoginInfo loginInfo = new LoginInfo();
        loginInfo.setRoles(roles);

        assertEquals(2, loginInfo.getRoles().size());
        assertEquals("ADMIN", loginInfo.getRoles().get(0).getName());
        assertEquals("USER", loginInfo.getRoles().get(1).getName());
    }
}
