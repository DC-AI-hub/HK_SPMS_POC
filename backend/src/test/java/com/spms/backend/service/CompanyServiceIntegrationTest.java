package com.spms.backend.service;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import com.spms.backend.service.exception.NotFoundException;
import com.spms.backend.repository.entities.idm.Company;
import com.spms.backend.repository.entities.idm.CompanyType;
import com.spms.backend.repository.idm.CompanyRepository;
import com.spms.backend.service.idm.impl.CompanyServiceImpl;
import com.spms.backend.service.model.idm.CompanyModel;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class CompanyServiceIntegrationTest {

    @MockitoBean
    private ClientRegistrationRepository clientRegistrationRepository; // Fixes the missing bean

    @Autowired
    private CompanyServiceImpl companyService;

    @Autowired
    private CompanyRepository companyRepository;

    private Company groupCompany;
    private Company businessEntity;

    @BeforeEach
    void setUp() {
        // Create test companies
        groupCompany = new Company();
        groupCompany.setName("Test Group");
        groupCompany.setCompanyType(CompanyType.GROUP);
        groupCompany.setDescription("Test Group Company");
        companyRepository.save(groupCompany);

        businessEntity = new Company();
        businessEntity.setName("Test Business");
        businessEntity.setCompanyType(CompanyType.BUSINESS_ENTITY);
        businessEntity.setDescription("Test Business Entity");
        companyRepository.save(businessEntity);
    }

    @AfterEach
    void tearDown() {
        companyRepository.deleteAll();
    }

    @Test
    void contextLoads() {
        assertNotNull(companyService);
    }

    @Test
    void createCompany_validInput_shouldCreateCompany() {
        CompanyModel newCompany = new CompanyModel();
        newCompany.setName("New Company");
        newCompany.setCompanyType(CompanyType.BUSINESS_ENTITY);
        newCompany.setDescription("New Description");
        
        CompanyModel created = companyService.createCompany(newCompany);
        
        assertNotNull(created.getId());
        assertEquals("New Company", created.getName());
        assertEquals(CompanyType.BUSINESS_ENTITY, created.getCompanyType());
        assertEquals("New Description", created.getDescription());
        
        // Verify database state
        Company dbCompany = companyRepository.findById(created.getId()).orElseThrow();
        assertEquals("New Company", dbCompany.getName());
    }

    @Test
    void updateCompany_validInput_shouldUpdateCompany() {
        CompanyModel updateData = new CompanyModel();
        updateData.setName("Updated Business");
        updateData.setCompanyType(CompanyType.VENDOR);
        updateData.setDescription("Updated Description");
        
        CompanyModel updated = companyService.updateCompany(businessEntity.getId(), updateData);
        
        assertEquals(businessEntity.getId(), updated.getId());
        assertEquals("Updated Business", updated.getName());
        assertEquals(CompanyType.VENDOR, updated.getCompanyType());
        assertEquals("Updated Description", updated.getDescription());
        
        // Verify database state
        Company dbCompany = companyRepository.findById(businessEntity.getId()).orElseThrow();
        assertEquals("Updated Business", dbCompany.getName());
        assertEquals(CompanyType.VENDOR, dbCompany.getCompanyType());
    }

    @Test
    void updateCompany_nonExistentId_shouldThrow() {
        CompanyModel updateData = new CompanyModel();
        updateData.setName("Non-existent");
        
        assertThrows(NotFoundException.class, () -> {
            companyService.updateCompany(-1L, updateData);
        });
    }

    @Test
    void deleteCompany_validId_shouldDeleteCompany() {
        Long companyId = businessEntity.getId();
        companyService.deleteCompany(companyId);
        
        assertFalse(companyRepository.existsById(companyId));
    }

    @Test
    void deleteCompany_nonExistentId_shouldThrow() {
        assertThrows(NotFoundException.class, () -> {
            companyService.deleteCompany(-1L);
        });
    }

    @Test
    void getCompanyByCompanyId_validId_shouldReturnCompany() {
        Optional<CompanyModel> result = companyService.getCompanyByCompanyId(businessEntity.getId());
        
        assertTrue(result.isPresent());
        CompanyModel company = result.get();
        assertEquals(businessEntity.getId(), company.getId());
        assertEquals(businessEntity.getName(), company.getName());
        assertEquals(businessEntity.getCompanyType(), company.getCompanyType());
    }

    @Test
    void getCompanyByCompanyId_nonExistentId_shouldThrow() {
        assertThrows(NotFoundException.class, () -> {
            companyService.getCompanyByCompanyId(-1L);
        });
    }

    @Test
    void getCompanyByCompanyId_nullId_shouldThrow() {
        assertThrows(IllegalArgumentException.class, () -> {
            companyService.getCompanyByCompanyId(null);
        });
    }

    @Test
    void getValidParents_validCompanyType_shouldReturnParents() {
        List<CompanyModel> validParents = companyService.getValidParents(businessEntity.getCompanyType());
        
        assertNotNull(validParents);
        assertTrue(validParents.stream()
            .anyMatch(c -> c.getId().equals(groupCompany.getId())));
    }

    @Test
    void isValidParent_validRelationship_shouldReturnTrue() {
        assertTrue(companyService.isValidParent(businessEntity.getCompanyType(), groupCompany.getId()));
    }

    @Test
    void addCompanyToChildren_validInput_shouldUpdateRelationships() {
        Company newChild = new Company();
        newChild.setName("New Child");
        newChild.setCompanyType(CompanyType.BUSINESS_ENTITY);
        companyRepository.save(newChild);
        
        companyService.addCompanyToChildren(groupCompany.getId(), List.of(newChild.getId()));
        
        Company updatedParent = companyRepository.findById(groupCompany.getId()).orElseThrow();
        assertTrue(updatedParent.getChildren().stream()
            .anyMatch(c -> c.getId().equals(newChild.getId())));

        Company updatedChild = companyRepository.findById(newChild.getId()).orElseThrow();
        assertEquals(groupCompany.getId(), updatedChild.getParent().getId());
    }

    @Test
    void getAllCompanies_withPaging_shouldReturnPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<CompanyModel> result = companyService.getAllCompanies(null, null, pageable);
        
        assertNotNull(result);
        assertTrue(result.getTotalElements() >= 2);
        assertTrue(result.getContent().stream()
            .anyMatch(c -> c.getId().equals(businessEntity.getId())));
    }

    @Test
    void getChildren_validParent_shouldReturnChildren() {
        companyService.addCompanyToChildren(groupCompany.getId(), List.of(businessEntity.getId()));
        
        Pageable pageable = PageRequest.of(0, 10);
        Page<CompanyModel> children = companyService.getChildren(
            groupCompany.getId(), 
            null, 
            pageable);
        
        assertNotNull(children);
        assertTrue(children.stream()
            .anyMatch(c -> c.getId().equals(businessEntity.getId())));
    }

    @Test
    void findByParentIdAndCompanyType_validInput_shouldReturnMatches() {
        companyService.addCompanyToChildren(groupCompany.getId(), List.of(businessEntity.getId()));
        
        List<CompanyModel> matches = companyService.findByParentIdAndCompanyType(
            groupCompany.getId(), 
            businessEntity.getCompanyType());
        
        assertNotNull(matches);
        assertTrue(matches.stream()
            .anyMatch(c -> c.getId().equals(businessEntity.getId())));
    }
}
