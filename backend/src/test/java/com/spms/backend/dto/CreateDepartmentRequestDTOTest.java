package com.spms.backend.dto;

import com.spms.backend.controller.dto.idm.CreateDepartmentRequestDTO;
import com.spms.backend.repository.entities.idm.DepartmentType;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CreateDepartmentRequestDTOTest {

    @Test
    void testLombokAnnotations() {
        CreateDepartmentRequestDTO dto = new CreateDepartmentRequestDTO();
        dto.setName("Test Department");
        
        Map<String, String> tags = new HashMap<>();
        tags.put("en", "English");
        dto.setTags(tags);

        dto.setParent(1L);
        dto.setType(DepartmentType.FUNCTIONAL);
        dto.setLevel(1);
        dto.setActive(true);
        dto.setDepartmentHeadId(2L);

        assertNotNull(dto.toString());
        assertEquals("Test Department", dto.getName());
        assertEquals(1, dto.getTags().size());
        assertEquals(1L, dto.getParent());
        assertEquals(DepartmentType.FUNCTIONAL, dto.getType());
        assertEquals(1, dto.getLevel());
        assertTrue(dto.getActive());
        assertEquals(2L, dto.getDepartmentHeadId());
    }

    @Test
    void testEnumValues() {
        CreateDepartmentRequestDTO dto = new CreateDepartmentRequestDTO();
        dto.setType(DepartmentType.FUNCTIONAL_TEAM);
        assertEquals(DepartmentType.FUNCTIONAL_TEAM, dto.getType());
        
        dto.setType(DepartmentType.LOCAL);
        assertEquals(DepartmentType.LOCAL, dto.getType());
        
        dto.setType(DepartmentType.TEAM);
        assertEquals(DepartmentType.TEAM, dto.getType());
    }

    @Test
    void testNullFields() {
        CreateDepartmentRequestDTO dto = new CreateDepartmentRequestDTO();
        assertNull(dto.getName());
        assertNull(dto.getTags());
        assertNull(dto.getParent());
        assertNull(dto.getType());
        assertNull(dto.getLevel());
        assertNull(dto.getActive());
        assertNull(dto.getDepartmentHeadId());
    }
}
