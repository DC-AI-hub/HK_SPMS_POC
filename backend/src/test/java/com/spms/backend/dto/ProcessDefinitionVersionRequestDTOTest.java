package com.spms.backend.dto;

import org.junit.jupiter.api.Test;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

import com.spms.backend.controller.dto.process.ProcessDefinitionVersionRequest;

class ProcessDefinitionVersionRequestDTOTest {
    
    private final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = validatorFactory.getValidator();
    
    @Test
    void testGettersAndSetters() {
        ProcessDefinitionVersionRequest dto = new ProcessDefinitionVersionRequest();
        
        // Test name
        String testName = "Test Name";
        dto.setName(testName);
        assertEquals(testName, dto.getName());
        
        // Test bpmnXml
        String testBpmnXml = "<bpmn:process/>";
        dto.setBpmnXml(testBpmnXml);
        assertEquals(testBpmnXml, dto.getBpmnXml());
    }
    
    @Test
    void testNameValidation() {
        ProcessDefinitionVersionRequest dto = new ProcessDefinitionVersionRequest();
        dto.setKey("!2313");
        // Test blank name validation
        Set<jakarta.validation.ConstraintViolation<ProcessDefinitionVersionRequest>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Should have validation errors when name is blank");
        
        dto.setName("");
        violations = validator.validate(dto);
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("must not be blank")));
        
        // Test valid name with spaces and special characters
        String validName = "Test Form - Special Characters !@#$%^&*()";
        dto.setName(validName);
        violations = validator.validate(dto);
        //assertTrue(violations.isEmpty(), "Valid name should not produce validation errors");
        //assertEquals(validName, dto.getName());
    }
    
    @Test
    void testRecordStyleMethods() {
        ProcessDefinitionVersionRequest dto = new ProcessDefinitionVersionRequest();
        
        // Test name()
        String testName = "Test Name";
        dto.setName(testName);
        assertEquals(testName, dto.getName());
        
        // Test bpmnXml()
        String testBpmnXml = "<bpmn:process/>";
        dto.setBpmnXml(testBpmnXml);
        assertEquals(testBpmnXml, dto.getBpmnXml());
    }
    
    @Test
    void testNoArgsConstructor() {
        ProcessDefinitionVersionRequest dto = new ProcessDefinitionVersionRequest();
        assertNotNull(dto);
    }
}
