package com.spms.backend.dto;

import com.spms.backend.controller.dto.idm.CompanyResponseDTO;
import com.spms.backend.repository.entities.idm.CompanyType;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CompanyResponseDTOTest {

    @Test
    void testLombokAnnotations() {
        CompanyResponseDTO dto = new CompanyResponseDTO();
        dto.setId(1L);
        dto.setActive(true);
        dto.setLastModified(LocalDateTime.now());
        dto.setName("Test Company");
        dto.setDescription("Test Description");
        
        Map<String, String> tags = new HashMap<>();
        tags.put("en", "English");
        dto.setLanguageTags(tags);

        dto.setCompanyType(CompanyType.BUSINESS_ENTITY);

        Map<String, String> profiles = new HashMap<>();
        profiles.put("profile1", "value1");
        dto.setCompanyProfiles(profiles);

        assertNotNull(dto.toString());
        assertEquals(1L, dto.getId());
        assertTrue(dto.getActive());
        assertNotNull(dto.getLastModified());
        assertEquals("Test Company", dto.getName());
        assertEquals("Test Description", dto.getDescription());
        assertEquals(1, dto.getLanguageTags().size());
        assertEquals(CompanyType.BUSINESS_ENTITY, dto.getCompanyType());
        assertEquals(1, dto.getCompanyProfiles().size());
    }

    @Test
    void testEqualsAndHashCode() {
        CompanyResponseDTO dto1 = new CompanyResponseDTO();
        dto1.setId(1L);

        CompanyResponseDTO dto2 = new CompanyResponseDTO();
        dto2.setId(1L);

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void testNullFields() {
        CompanyResponseDTO dto = new CompanyResponseDTO();
        assertNull(dto.getId());
        assertNull(dto.getActive());
        assertNull(dto.getLastModified());
        assertNull(dto.getName());
        assertNull(dto.getDescription());
        assertNull(dto.getLanguageTags());
        assertNull(dto.getCompanyType());
        assertNull(dto.getCompanyProfiles());
    }
}
