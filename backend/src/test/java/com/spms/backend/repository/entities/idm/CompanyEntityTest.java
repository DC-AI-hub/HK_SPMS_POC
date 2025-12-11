package com.spms.backend.repository.entities.idm;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class CompanyEntityTest {

    @Test
    void testDefaultActiveValue() {
        Company company = new Company();
        assertTrue(company.getActive());
    }

    @Test
    void testRequiredNameField() {
        Company company = new Company();
        company.setName("Test Company");
        assertEquals("Test Company", company.getName());
    }

    @Test
    void testDescriptionField() {
        Company company = new Company();
        company.setDescription("Test Description");
        assertEquals("Test Description", company.getDescription());
    }

    @Test
    void testCompanyTypeEnum() {
        Company company = new Company();
        company.setCompanyType(CompanyType.BUSINESS_ENTITY);
        assertEquals(CompanyType.BUSINESS_ENTITY, company.getCompanyType());
    }

    @Test
    void testEngineIdField() {
        Company company = new Company();
        company.setEngineId("engine123");
        assertEquals("engine123", company.getEngineId());
    }

    @Test
    void testTimestampFields() {
        Company company = new Company();
        LocalDateTime now = LocalDateTime.now();
        
        company.setCreatedTime(now);
        assertEquals(now, company.getCreatedTime());
        
        company.setLastModified(now);
        assertEquals(now, company.getLastModified());
    }

    @Test
    void testAuditFields() {
        Company company = new Company();
        company.setCreatedBy("admin");
        assertEquals("admin", company.getCreatedBy());
        
        company.setUpdatedBy("user");
        assertEquals("user", company.getUpdatedBy());
    }

    @Test
    void testLanguageTagsMap() {
        Company company = new Company();
        Map<String, String> tags = new HashMap<>();
        tags.put("en", "English");
        
        company.setLanguageTags(tags);
        assertEquals(tags, company.getLanguageTags());
    }

    @Test
    void testCompanyProfilesMap() {
        Company company = new Company();
        Map<String, String> profiles = new HashMap<>();
        profiles.put("profile1", "value1");
        
        company.setCompanyProfiles(profiles);
        assertEquals(profiles, company.getCompanyProfiles());
    }

    @Test
    void testParentRelationship() {
        Company parent = new Company();
        parent.setId(1L);
        
        Company child = new Company();
        child.setParent(parent);
        
        assertEquals(parent, child.getParent());
    }

    @Test
    void testChildrenCollection() {
        Company parent = new Company();
        Company child = new Company();
        
        parent.getChildren().add(child);
        child.setParent(parent);
        
        assertFalse(parent.getChildren().isEmpty());
        assertEquals(1, parent.getChildren().size());
        assertTrue(parent.getChildren().contains(child));
    }
}
