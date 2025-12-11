package com.spms.backend.dto;

import com.spms.backend.controller.dto.idm.CreateCompanyRequestDTO;
import com.spms.backend.repository.entities.idm.CompanyType;
import com.spms.backend.service.model.idm.CompanyModel;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CreateCompanyRequestDTOTest {

    @Test
    void testLombokAnnotations() {
        CreateCompanyRequestDTO dto = new CreateCompanyRequestDTO();
        dto.setActive(true);
        dto.setName("Test Company");
        dto.setDescription("Test Description");
        
        Map<String, String> tags = new HashMap<>();
        tags.put("en", "English");
        dto.setLanguageTags(tags);

        dto.setCompanyType(CompanyType.BUSINESS_ENTITY);
        dto.setParentId(1L);

        Map<String, String> profiles = new HashMap<>();
        profiles.put("profile1", "value1");
        dto.setCompanyProfiles(profiles);

        assertNotNull(dto.toString());
        assertTrue(dto.getActive());
        assertEquals("Test Company", dto.getName());
        assertEquals("Test Description", dto.getDescription());
        assertEquals(1, dto.getLanguageTags().size());
        assertEquals(CompanyType.BUSINESS_ENTITY, dto.getCompanyType());
        assertEquals(1L, dto.getParentId());
        assertEquals(1, dto.getCompanyProfiles().size());
    }

    @Test
    void testToCompanyModel() {
        CreateCompanyRequestDTO dto = new CreateCompanyRequestDTO();
        dto.setActive(true);
        dto.setName("Test Company");
        dto.setDescription("Test Description");
        dto.setCompanyType(CompanyType.BUSINESS_ENTITY);
        dto.setParentId(1L);

        CompanyModel parent = new CompanyModel();
        parent.setId(1L);

        CompanyModel model = dto.toCompanyModel(parent);
        
        assertTrue(model.getActive());
        assertEquals("Test Company", model.getName());
        assertEquals("Test Description", model.getDescription());
        assertEquals(CompanyType.BUSINESS_ENTITY, model.getCompanyType());
        assertEquals(parent, model.getParent());
    }

    @Test
    void testNullFields() {
        CreateCompanyRequestDTO dto = new CreateCompanyRequestDTO();
        assertNull(dto.getActive());
        assertNull(dto.getName());
        assertNull(dto.getDescription());
        assertNull(dto.getLanguageTags());
        assertNull(dto.getCompanyType());
        assertNull(dto.getParentId());
        assertNull(dto.getCompanyProfiles());
    }
}
