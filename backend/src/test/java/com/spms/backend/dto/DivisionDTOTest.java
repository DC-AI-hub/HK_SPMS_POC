package com.spms.backend.dto;

import com.spms.backend.controller.dto.idm.DivisionDTO;
import com.spms.backend.repository.entities.idm.DivisionType;
import com.spms.backend.service.model.idm.DivisionModel;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class DivisionDTOTest {
    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidDivisionDTO() {
        DivisionDTO dto = new DivisionDTO();
        dto.setName("Valid Division");
        dto.setType(DivisionType.CORE);
        dto.setActive(true);
        dto.setCompanyId(1L);

        Set<ConstraintViolation<DivisionDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testInvalidNameValidation() {
        DivisionDTO dto = new DivisionDTO();
        dto.setName(""); // Empty name violates @NotBlank
        dto.setType(DivisionType.BUSINESS);
        dto.setActive(true);
        dto.setCompanyId(1L);

        Set<ConstraintViolation<DivisionDTO>> violations = validator.validate(dto);
        assertEquals(1, violations.size());
        assertEquals("Division name is required", violations.iterator().next().getMessage());
    }

    @Test
    void testFromDivisionModel() {
        DivisionModel model = new DivisionModel();
        model.setId(123L);
        model.setName("Test Division");
        model.setType(DivisionType.TECHNOLOGY);
        model.setDescription("Test Description");
        model.setActive(true);
        model.setLastModified(LocalDateTime.now());
        model.setCompanyId(456L);
        model.setDivisionHeadId(789L);

        DivisionDTO dto = DivisionDTO.fromDivisionModel(model);

        assertEquals(model.getId(), dto.getId());
        assertEquals(model.getName(), dto.getName());
        assertEquals(model.getType(), dto.getType());
        assertEquals(model.getDescription(), dto.getDescription());
        assertEquals(model.getActive(), dto.getActive());
        assertEquals(model.getLastModified(), dto.getLastModified());
        assertEquals(model.getCompanyId(), dto.getCompanyId());
        assertEquals(model.getDivisionHeadId(), dto.getDivisionHeadId());
    }

    @Test
    void testToDivisionModel() {
        DivisionDTO dto = new DivisionDTO();
        dto.setId(123L);
        dto.setName("Test Division");
        dto.setType(DivisionType.STRATEGY);
        dto.setDescription("Test Description");
        dto.setActive(true);
        dto.setLastModified(LocalDateTime.now());
        dto.setCompanyId(456L);
        dto.setDivisionHeadId(789L);

        DivisionModel model = dto.toDivisionModel();

        assertEquals(dto.getId(), model.getId());
        assertEquals(dto.getName().trim(), model.getName());
        assertEquals(dto.getType(), model.getType());
        assertEquals(dto.getDescription().trim(), model.getDescription());
        assertEquals(dto.getActive(), model.getActive());
        assertEquals(dto.getLastModified(), model.getLastModified());
        assertEquals(dto.getCompanyId(), model.getCompanyId());
        assertEquals(dto.getDivisionHeadId(), model.getDivisionHeadId());
    }

    @Test
    void testAllDivisionTypeValues() {
        DivisionDTO dto = new DivisionDTO();
        
        dto.setType(DivisionType.CORE);
        assertEquals(DivisionType.CORE, dto.getType());
        
        dto.setType(DivisionType.BUSINESS);
        assertEquals(DivisionType.BUSINESS, dto.getType());
        
        dto.setType(DivisionType.TECHNOLOGY);
        assertEquals(DivisionType.TECHNOLOGY, dto.getType());
        
        dto.setType(DivisionType.STRATEGY);
        assertEquals(DivisionType.STRATEGY, dto.getType());
        
        dto.setType(DivisionType.SUPPORT);
        assertEquals(DivisionType.SUPPORT, dto.getType());
    }
}
