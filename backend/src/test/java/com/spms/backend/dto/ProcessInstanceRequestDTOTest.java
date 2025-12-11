package com.spms.backend.dto;

import com.spms.backend.controller.dto.process.ProcessInstanceRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

class ProcessInstanceRequestDTOTest {
    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();

        validator = factory.getValidator();
    }

    @Test
    void testFieldGettersAndSetters() {
        ProcessInstanceRequest request = new ProcessInstanceRequest();
        
        String testDefinitionId = "test-def-id";
        request.setDefinitionId(1L);
        assertEquals(testDefinitionId, request.getDefinitionId());

        //String testBusinessKey = "test-key";
        //request.setBusinessKey(testBusinessKey);
        //assertEquals(testBusinessKey, request.getBusinessKey());

    }

    @Test
    void testValidationConstraints() {
        ProcessInstanceRequest request = new ProcessInstanceRequest();
        
        // Test empty fields
        Set<ConstraintViolation<ProcessInstanceRequest>> violations = validator.validate(request);
        assertEquals(2, violations.size());

        // Test valid fields
        request.setDefinitionId(1L);
        request.setFormId(1L);
        violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testEqualsAndHashCode() {
        ProcessInstanceRequest req1 = new ProcessInstanceRequest();
        req1.setDefinitionId(1L);


        ProcessInstanceRequest req2 = new ProcessInstanceRequest();
        req2.setDefinitionId(1L);


        ProcessInstanceRequest req3 = new ProcessInstanceRequest();
        req3.setDefinitionId(1L);


        assertEquals(req1, req2);
        assertNotEquals(req1, req3);
        assertEquals(req1.hashCode(), req2.hashCode());
        assertNotEquals(req1.hashCode(), req3.hashCode());
    }

    @Test
    void testToString() {
        ProcessInstanceRequest request = new ProcessInstanceRequest();
        request.setDefinitionId(1L);
        
        String toString = request.toString();
        assertTrue(toString.contains("test-def"));
    }
}
