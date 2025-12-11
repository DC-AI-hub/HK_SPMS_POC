package com.spms.backend.dto;

import com.spms.backend.controller.dto.idm.UserDTO;
import com.spms.backend.repository.entities.idm.User;
import com.spms.backend.service.model.idm.UserModel;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserDTOTest {
    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testFieldGettersAndSetters() {
        UserDTO dto = new UserDTO();
        dto.setUserProfiles(new HashMap<>());
        
        String testUsername = "testuser";
        dto.setUsername(testUsername);
        assertEquals(testUsername, dto.getUsername());

        String testEmail = "test@example.com";
        dto.setEmail(testEmail);
        assertEquals(testEmail, dto.getEmail());

        String testFirstName = "Test";
        dto.setFirstName(testFirstName);
        assertEquals(testFirstName, dto.getFirstName());

        String testLastName = "User";
        dto.setLastName(testLastName);
        assertEquals(testLastName, dto.getLastName());

        String testAvatarUrl = "http://example.com/avatar.jpg";
        dto.setAvatarUrl(testAvatarUrl);
        assertEquals(testAvatarUrl, dto.getAvatarUrl());

        LocalDateTime testCreatedAt = LocalDateTime.now();
        dto.setCreatedAt(testCreatedAt);
        assertEquals(testCreatedAt, dto.getCreatedAt());

        LocalDateTime testUpdatedAt = LocalDateTime.now();
        dto.setUpdatedAt(testUpdatedAt);
        assertEquals(testUpdatedAt, dto.getUpdatedAt());

        String testCreatedBy = "admin";
        dto.setCreatedBy(testCreatedBy);
        assertEquals(testCreatedBy, dto.getCreatedBy());

        String testModifiedBy = "admin";
        dto.setModifiedBy(testModifiedBy);
        assertEquals(testModifiedBy, dto.getModifiedBy());
    }

    @Test
    void testValidationConstraints() {
        UserDTO dto = new UserDTO();
        
        // Test empty required fields
        Set<jakarta.validation.ConstraintViolation<UserDTO>> violations = validator.validate(dto);
        assertEquals(2, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Username is required")));
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Email is required")));

        // Test valid fields
        dto.setUsername("validuser");
        dto.setEmail("valid@example.com");
        violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testToUserModelConversion() {
        UserDTO dto = new UserDTO();
        dto.setUserProfiles(new HashMap<>());
        dto.setUsername("testuser");
        dto.setEmail("test@example.com");
        dto.setFirstName("Test");
        dto.setLastName("User");
        dto.setAvatarUrl("http://example.com/avatar.jpg");
        LocalDateTime now = LocalDateTime.now();
        dto.setCreatedAt(now);
        dto.setUpdatedAt(now);
        dto.setCreatedBy("admin");
        dto.setModifiedBy("admin");

        UserModel model = dto.toUserModel();
        assertEquals(dto.getUsername(), model.getUsername());
        assertEquals(dto.getEmail(), model.getEmail());
        assertEquals(dto.getCreatedAt(), model.getCreatedAt());
        assertEquals(dto.getUpdatedAt(), model.getUpdatedAt());
        assertEquals(dto.getCreatedBy(), model.getCreatedBy());
        assertEquals(dto.getModifiedBy(), model.getModifiedBy());
        assertNotNull(model.getUserProfiles());
        assertEquals(dto.getFirstName(), model.getUserProfiles().get("firstName"));
        assertEquals(dto.getLastName(), model.getUserProfiles().get("lastName"));
        assertEquals(dto.getAvatarUrl(), model.getUserProfiles().get("avatarUrl"));
    }

    @Test
    void testFromUserModelConversion() {
        UserModel model = new UserModel();
        model.setUsername("testuser");
        model.setEmail("test@example.com");
        model.setCreatedAt(LocalDateTime.now());
        model.setUpdatedAt(LocalDateTime.now());
        model.setCreatedBy("admin");
        model.setModifiedBy("admin");
        
        Map<String, String> profiles = new HashMap<>();
        profiles.put("firstName", "Test");
        profiles.put("lastName", "User");
        profiles.put("avatarUrl", "http://example.com/avatar.jpg");
        model.setUserProfiles(profiles);
        model.setType(User.UserType.STAFF);

        UserDTO dto = UserDTO.fromUserModel(model);
        assertEquals(model.getUsername(), dto.getUsername());
        assertEquals(model.getEmail(), dto.getEmail());
        assertEquals(model.getCreatedAt(), dto.getCreatedAt());
        assertEquals(model.getUpdatedAt(), dto.getUpdatedAt());
        assertEquals(model.getCreatedBy(), dto.getCreatedBy());
        assertEquals(model.getModifiedBy(), dto.getModifiedBy());
        assertEquals(model.getUserProfiles().get("firstName"), dto.getFirstName());
        assertEquals(model.getUserProfiles().get("lastName"), dto.getLastName());
        assertEquals(model.getUserProfiles().get("avatarUrl"), dto.getAvatarUrl());
    }

    @Test
    void testNullProfileHandling() {
        UserModel model = new UserModel();
        model.setUsername("testuser");
        model.setEmail("test@example.com");
        model.setUserProfiles(null);
        model.setType(User.UserType.STAFF);

        UserDTO dto = UserDTO.fromUserModel(model);
        assertNull(dto.getFirstName());
        assertNull(dto.getLastName());
        assertNull(dto.getAvatarUrl());
    }

    @Test
    void testEmptyProfileHandling() {
        UserDTO dto = new UserDTO();
        dto.setUserProfiles(new HashMap<>());
        dto.setUsername("testuser");
        dto.setEmail("test@example.com");
        dto.setFirstName(null);
        dto.setLastName(null);
        dto.setAvatarUrl(null);

        UserModel model = dto.toUserModel();
        assertNotNull(model.getUserProfiles());
        assertNull(model.getUserProfiles().get("firstName"));
        assertNull(model.getUserProfiles().get("lastName"));
        assertNull(model.getUserProfiles().get("avatarUrl"));
    }
}
