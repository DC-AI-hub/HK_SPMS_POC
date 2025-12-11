package com.spms.backend.dto;

import com.spms.backend.controller.dto.idm.DepartmentDTO;
import com.spms.backend.repository.entities.idm.DepartmentType;
import com.spms.backend.service.model.idm.DepartmentModel;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class DepartmentDTOTest {

    @Test
    void testFieldGettersAndSetters() {
        // Create via empty model
        DepartmentModel model = new DepartmentModel();
        DepartmentDTO dto = new DepartmentDTO(model);

        // Test basic fields
        Long testId = 123L;
        dto.setId(testId);
        assertEquals(testId, dto.getId());

        String testName = "Test Department";
        dto.setName(testName);
        assertEquals(testName, dto.getName());

        // Test map field
        Map<String, String> testTags = new HashMap<>();
        testTags.put("tag1", "value1");
        dto.setTags(testTags);
        assertEquals(testTags, dto.getTags());

        // Test enum field
        DepartmentType testType = DepartmentType.FUNCTIONAL;
        dto.setType(testType);
        assertEquals(testType, dto.getType());

        // Test parent relationship
        Long testParentId = 456L;
        dto.setParent(testParentId);
        assertEquals(testParentId, dto.getParent());

        // Test audit fields
        String testCreatedBy = "admin";
        dto.setCreatedBy(testCreatedBy);
        assertEquals(testCreatedBy, dto.getCreatedBy());

        LocalDateTime testCreatedTime = LocalDateTime.now();
        dto.setCreatedTime(testCreatedTime);
        assertEquals(testCreatedTime, dto.getCreatedTime());
    }

    @Test
    void testConstructorWithDepartmentModel() {
        DepartmentModel model = new DepartmentModel();
        model.setId(123L);
        model.setName("Test Model");
        model.setType(DepartmentType.TEAM);
        model.setParent(456L);
        model.setActive(true);
        model.setCreatedBy("creator");
        model.setCreatedAt(LocalDateTime.now());

        DepartmentDTO dto = new DepartmentDTO(model);

        assertEquals(model.getId(), dto.getId());
        assertEquals(model.getName(), dto.getName());
        assertEquals(model.getType(), dto.getType());
        assertEquals(model.getParent(), dto.getParent());
        assertEquals(model.isActive(), dto.getActive());
        assertEquals(model.getCreatedBy(), dto.getCreatedBy());
        assertEquals(model.getCreatedAt(), dto.getCreatedTime());
    }

    @Test
    void testAllDepartmentTypeValues() {
        // Create via empty model
        DepartmentModel model = new DepartmentModel();
        DepartmentDTO dto = new DepartmentDTO(model);
        
        // Verify all enum values can be set
        dto.setType(DepartmentType.FUNCTIONAL);
        assertEquals(DepartmentType.FUNCTIONAL, dto.getType());
        
        dto.setType(DepartmentType.FUNCTIONAL_TEAM);
        assertEquals(DepartmentType.FUNCTIONAL_TEAM, dto.getType());
        
        dto.setType(DepartmentType.LOCAL);
        assertEquals(DepartmentType.LOCAL, dto.getType());
        
        dto.setType(DepartmentType.TEAM);
        assertEquals(DepartmentType.TEAM, dto.getType());
    }
}
