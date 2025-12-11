package com.spms.backend.dto;

import com.spms.backend.controller.dto.idm.CompanyDTO;
import com.spms.backend.repository.entities.idm.CompanyType;
import com.spms.backend.service.model.idm.CompanyModel;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CompanyDTOTest {

    @Test
    void testGettersAndSetters() {
        CompanyDTO dto = new CompanyDTO();

        // Test simple fields
        Long testId = 123L;
        dto.setId(testId);
        assertEquals(testId, dto.getId());

        Boolean testActive = true;
        dto.setActive(testActive);
        assertEquals(testActive, dto.getActive());

        String testName = "Test Company";
        dto.setName(testName);
        assertEquals(testName, dto.getName());

        String testDescription = "Test Description";
        dto.setDescription(testDescription);
        assertEquals(testDescription, dto.getDescription());

        // Test enum field
        CompanyType testType = CompanyType.BUSINESS_ENTITY;
        dto.setCompanyType(testType);
        assertEquals(testType, dto.getCompanyType());

        // Test map fields
        Map<String, String> testLanguageTags = new HashMap<>();
        testLanguageTags.put("en", "English");
        dto.setLanguageTags(testLanguageTags);
        assertEquals(testLanguageTags, dto.getLanguageTags());

        Map<String, String> testProfiles = new HashMap<>();
        testProfiles.put("profile1", "value1");
        dto.setCompanyProfiles(testProfiles);
        assertEquals(testProfiles, dto.getCompanyProfiles());

        // Test audit fields
        String testCreatedBy = "admin";
        dto.setCreatedBy(testCreatedBy);
        assertEquals(testCreatedBy, dto.getCreatedBy());

        String testUpdatedBy = "user";
        dto.setUpdatedBy(testUpdatedBy);
        assertEquals(testUpdatedBy, dto.getUpdatedBy());

        LocalDateTime testCreatedTime = LocalDateTime.now();
        dto.setCreatedTime(testCreatedTime);
        assertEquals(testCreatedTime, dto.getCreatedTime());

        LocalDateTime testModifiedTime = LocalDateTime.now();
        dto.setLastModified(testModifiedTime);
        assertEquals(testModifiedTime, dto.getLastModified());

        // Test parent relationship
        CompanyDTO parent = new CompanyDTO();
        parent.setId(456L);
        dto.setParent(parent);
        assertEquals(parent, dto.getParent());
        assertEquals(456L, dto.getParentId());
    }

    @Test
    void testNoArgsConstructor() {
        CompanyDTO dto = new CompanyDTO();
        assertNotNull(dto);
    }

    @Test
    void testFromCompanyModel() {
        CompanyModel model = new CompanyModel();
        model.setId(123L);
        model.setActive(true);
        model.setName("Test Model");
        model.setDescription("Test Description");
        model.setCompanyType(CompanyType.GROUP);
        
        Map<String, String> testLanguageTags = new HashMap<>();
        testLanguageTags.put("zh", "Chinese");
        model.setLanguageTags(testLanguageTags);

        CompanyDTO dto = CompanyDTO.fromCompanyModel(model);
        
        assertEquals(model.getId(), dto.getId());
        assertEquals(model.getActive(), dto.getActive());
        assertEquals(model.getName(), dto.getName());
        assertEquals(model.getDescription(), dto.getDescription());
        assertEquals(model.getCompanyType(), dto.getCompanyType());
        assertEquals(model.getLanguageTags(), dto.getLanguageTags());
    }

    @Test
    void testToCompanyModel() {
        CompanyDTO dto = new CompanyDTO();
        dto.setId(123L);
        dto.setActive(false);
        dto.setName("Test DTO");
        dto.setDescription("Test Description");
        dto.setCompanyType(CompanyType.BUSINESS_ENTITY);
        
        Map<String, String> testProfiles = new HashMap<>();
        testProfiles.put("profile1", "value1");
        dto.setCompanyProfiles(testProfiles);

        CompanyModel model = dto.toCompanyModel();
        
        assertEquals(dto.getId(), model.getId());
        assertEquals(dto.getActive(), model.getActive());
        assertEquals(dto.getName(), model.getName());
        assertEquals(dto.getDescription(), model.getDescription());
        assertEquals(dto.getCompanyType(), model.getCompanyType());
        assertEquals(dto.getCompanyProfiles(), model.getCompanyProfiles());
    }

    @Test
    void testParentRelationshipConversion() {
        // Test parent relationship in model conversion
        CompanyModel parentModel = new CompanyModel();
        parentModel.setId(456L);
        
        CompanyModel model = new CompanyModel();
        model.setId(123L);
        model.setParent(parentModel);
        
        CompanyDTO dto = CompanyDTO.fromCompanyModel(model);
        assertEquals(456L, dto.getParentId());
        
        CompanyModel convertedModel = dto.toCompanyModel();
        assertEquals(456L, convertedModel.getParent().getId());
    }
}
