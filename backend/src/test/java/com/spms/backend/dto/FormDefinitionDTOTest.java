package com.spms.backend.dto;

import org.junit.jupiter.api.Test;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

import com.spms.backend.controller.dto.process.FormDefinitionDTO;

class FormDefinitionDTOTest {
    
    private final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = validatorFactory.getValidator();
    
    @Test
    void testGettersAndSetters() {
        FormDefinitionDTO dto = new FormDefinitionDTO();
        
        // Test key
        String testKey = "test-key";
        dto.setKey(testKey);
        assertEquals(testKey, dto.getKey());
        
        // Test name
        String testName = "Test Name";
        dto.setName(testName);
        assertEquals(testName, dto.getName());
        
        // Test version
        String testVersion = "1.0.0";
        dto.setVersion(testVersion);
        assertEquals(testVersion, dto.getVersion());
        
        // Test schema

        dto.setSchema("testSchema");
        assertEquals("testSchema", dto.getSchema());
    }
    
    @Test
    void testEqualsAndHashCode() {
        // Create test instances with same data
        FormDefinitionDTO dto1 = new FormDefinitionDTO();
        dto1.setKey("test-key");
        dto1.setName("Test Name");
        dto1.setVersion("1.0.0");
        
        FormDefinitionDTO dto2 = new FormDefinitionDTO();
        dto2.setKey("test-key");
        dto2.setName("Test Name");
        dto2.setVersion("1.0.0");
        
        // Verify equals and hashCode work correctly
        assertEquals(dto1, dto2, "DTOs with same data should be equal");
        assertEquals(dto1.hashCode(), dto2.hashCode(), "Hash codes should match for equal DTOs");
        
        // Test different key
        FormDefinitionDTO dto3 = new FormDefinitionDTO();
        dto3.setKey("different-key");
        dto3.setName("Test Name");
        dto3.setVersion("1.0.0");
        
        assertNotEquals(dto1, dto3, "DTOs with different keys should not be equal");
        
        // Test different name
        FormDefinitionDTO dto4 = new FormDefinitionDTO();
        dto4.setKey("test-key");
        dto4.setName("Different Name");
        dto4.setVersion("1.0.0");
        
        assertNotEquals(dto1, dto4, "DTOs with different names should not be equal");
        
        // Test different version
        FormDefinitionDTO dto5 = new FormDefinitionDTO();
        dto5.setKey("test-key");
        dto5.setName("Test Name");
        dto5.setVersion("2.0.0");
        
        assertNotEquals(dto1, dto5, "DTOs with different versions should not be equal");
    }
    
    @Test
    void testToString() {
        FormDefinitionDTO dto = new FormDefinitionDTO();
        dto.setKey("test-key");
        dto.setName("Test Name");
        dto.setVersion("1.0.0");
        
        String toStringResult = dto.toString();
        
        // Basic verification that toString contains important fields
        assertTrue(toStringResult.contains("FormDefinitionDTO"), "ToString should contain class name");
        assertTrue(toStringResult.contains("key=test-key"), "ToString should contain key field");
        assertTrue(toStringResult.contains("name=Test Name"), "ToString should contain name field");
        assertTrue(toStringResult.contains("version=1.0.0"), "ToString should contain version field");
    }
    
    @Test
    void testNoArgsConstructor() {
        FormDefinitionDTO dto = new FormDefinitionDTO();
        assertNotNull(dto);
    }
    
    @Test
    void testKeyValidation() {
        FormDefinitionDTO dto = new FormDefinitionDTO();
        
        // Test blank key validation
        Set<jakarta.validation.ConstraintViolation<FormDefinitionDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Should have validation errors when key is blank");
        
        dto.setKey("");
        violations = validator.validate(dto);
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Form key cannot be blank")));
        
        // Test invalid key format (uppercase)
        dto.setKey("InvalidKey");
        violations = validator.validate(dto);
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Form key must be lowercase alphanumeric with hyphens, dots, or underscores")));
        
        // Test invalid key format (special characters)
        dto.setKey("invalid/key");
        violations = validator.validate(dto);
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Form key must be lowercase alphanumeric with hyphens, dots, or underscores")));
        
        // Test valid key format (with hyphen)
        String validKey1 = "valid-key-123";
        dto.setKey(validKey1);
        violations = validator.validate(dto);
        assertTrue(violations.stream().noneMatch(
                x->"key".equals(x.getPropertyPath().toString())),
                "Valid key should not produce validation errors");
        assertEquals(validKey1, dto.getKey());
        
        // Test valid key format (with dot)
        String validKey2 = "valid.key.123";
        dto.setKey(validKey2);
        violations = validator.validate(dto);
        assertTrue(violations.stream().noneMatch(
                        x->"key".equals(x.getPropertyPath().toString())),
                "Valid key should not produce validation errors");
        assertEquals(validKey2, dto.getKey());
        
        // Test valid key format (with underscore)
        String validKey3 = "valid_key_123";
        dto.setKey(validKey3);
        violations = validator.validate(dto);
        assertTrue(violations.stream().noneMatch(
                x->"key".equals(x.getPropertyPath().toString())),
                "Valid key should not produce validation errors");
        assertEquals(validKey3, dto.getKey());
    }

    @Test
    void testNameValidation() {
        FormDefinitionDTO dto = new FormDefinitionDTO();
        dto.setVersion("1.2.3");
        dto.setKey("valid-key"); // Set valid key first
        
        // Test blank name validation
        dto.setName("");
        Set<jakarta.validation.ConstraintViolation<FormDefinitionDTO>> violations = validator.validate(dto);
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("name must not be blank")));
        
        // Test valid name with spaces and special characters
        String validName = "Test Form - Special Characters !@#$%^&*()";
        dto.setName(validName);
        violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "Valid name should not produce validation errors");
        assertEquals(validName, dto.getName());
    }

    @Test
    void testVersionValidation() {
        FormDefinitionDTO dto = new FormDefinitionDTO();
        dto.setKey("valid-key"); // Set valid key first
        dto.setName("Valid Name"); // Set valid name first
        
        // Test invalid version format
        dto.setVersion("invalid-version");
        Set<jakarta.validation.ConstraintViolation<FormDefinitionDTO>> violations = validator.validate(dto);
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Version must follow semantic versioning format")));
        
        // Test valid version format
        String validVersion = "1.2.3";
        dto.setVersion(validVersion);
        violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "Valid version should not produce validation errors");
        assertEquals(validVersion, dto.getVersion());
    }
}
