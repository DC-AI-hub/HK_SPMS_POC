package com.spms.backend.dto;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Set;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import static org.junit.jupiter.api.Assertions.*;

import com.spms.backend.controller.dto.process.FormVersionDTO;

class FormVersionDTOTest {
    
    private final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = validatorFactory.getValidator();
    
    @Test
    void testGettersAndSetters() {
        FormVersionDTO dto = new FormVersionDTO();
        
        // Test key
        String testKey = "test-key";
        dto.setKey(testKey);
        assertEquals(testKey, dto.getKey());
        
        // Test version
        String testVersion = "1.0.0";
        dto.setVersion(testVersion);
        assertEquals(testVersion, dto.getVersion());
        
        // Test publishedDate
        Long testPublishedDate = Instant.now().toEpochMilli();
        dto.setPublishedDate(testPublishedDate);
        assertEquals(testPublishedDate, dto.getPublishedDate());
        
        // Test deprecated
        boolean testDeprecated = true;
        dto.setDeprecated(testDeprecated);
        assertEquals(testDeprecated, dto.isDeprecated());
    }
    
    @Test
    void testKeyValidation() {
        FormVersionDTO dto = new FormVersionDTO();
        dto.setVersion("1.2.3");
        
        // Test blank key validation
        Set<jakarta.validation.ConstraintViolation<FormVersionDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Should have validation errors when key is blank");
        
        dto.setKey("");
        violations = validator.validate(dto);
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Form Version key cannot be blank")));
        
        // Test valid key format (with hyphen)
        String validKey1 = "valid-key-123";
        dto.setKey(validKey1);
        violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "Valid key should not produce validation errors");
        assertEquals(validKey1, dto.getKey());
        
        // Test valid key format (with dot)
        String validKey2 = "valid.key.123";
        dto.setKey(validKey2);
        violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "Valid key should not produce validation errors");
        assertEquals(validKey2, dto.getKey());
        
        // Test valid key format (with underscore)
        String validKey3 = "valid_key_123";
        dto.setKey(validKey3);
        violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "Valid key should not produce validation errors");
        assertEquals(validKey3, dto.getKey());
    }

    @Test
    void testVersionValidation() {
        FormVersionDTO dto = new FormVersionDTO();
        dto.setVersion("1.2.3");
        dto.setKey("valid-key"); // Set valid key first
        
        // Test blank version validation
        Set<jakarta.validation.ConstraintViolation<FormVersionDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "Should have validation errors when version is blank");
        
        dto.setVersion("");
        violations = validator.validate(dto);
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Version cannot be blank")));
        
        // Test valid version format
        String validVersion = "1.2.3";
        dto.setVersion(validVersion);
        violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "Valid version should not produce validation errors");
        assertEquals(validVersion, dto.getVersion());
        
        // Test invalid version format
        String invalidVersion = "invalid-version";
        dto.setVersion(invalidVersion);
        violations = validator.validate(dto);
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Version must follow semantic versioning format")));
        
        // Test version with patch number (invalid)
        String validVersionWithPatch = "1.2.3.4";
        dto.setVersion(validVersionWithPatch);
        violations = validator.validate(dto);
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Version must follow semantic versioning format")));
    }
    
    @Test
    void testEqualsAndHashCode() {
        // Create two identical instances
        FormVersionDTO dto1 = new FormVersionDTO();
        dto1.setKey("test-key");
        dto1.setVersion("1.0.0");
        
        FormVersionDTO dto2 = new FormVersionDTO();
        dto2.setKey("test-key");
        dto2.setVersion("1.0.0");
        
        // Test equality and hash codes
        assertEquals(dto1, dto2, "DTOs with same data should be equal");
        assertEquals(dto1.hashCode(), dto2.hashCode(), "Hash codes should match for equal DTOs");
        
        // Test different key
        FormVersionDTO dto3 = new FormVersionDTO();
        dto3.setKey("different-key");
        dto3.setVersion("1.0.0");
        
        assertNotEquals(dto1, dto3, "DTOs with different keys should not be equal");
        
        // Test different version
        FormVersionDTO dto4 = new FormVersionDTO();
        dto4.setKey("test-key");
        dto4.setVersion("2.0.0");
        
        assertNotEquals(dto1, dto4, "DTOs with different versions should not be equal");
    }
    
    @Test
    void testToString() {
        FormVersionDTO dto = new FormVersionDTO();
        dto.setKey("test-key");
        dto.setVersion("1.0.0");
        dto.setPublishedDate(LocalDateTime.of(2025, 5, 25, 8, 0).toInstant(ZoneOffset.UTC).toEpochMilli());
        dto.setDeprecated(true);
        
        String toStringResult = dto.toString();
        
        // Verify that toString contains important fields
        assertTrue(toStringResult.contains("FormVersionDTO"), "ToString should contain class name");
        assertTrue(toStringResult.contains("key=test-key"), "ToString should contain key field");
        assertTrue(toStringResult.contains("version=1.0.0"), "ToString should contain version field");
        //assertTrue(toStringResult.contains("publishedDate=2025-05-25T08:00"), "ToString should contain publishedDate field");
        assertTrue(toStringResult.contains("deprecated=true"), "ToString should contain deprecated field");
    }
    
    @Test
    void testNoArgsConstructor() {
        FormVersionDTO dto = new FormVersionDTO();
        assertNotNull(dto);
    }
}
