package com.spms.backend.service.idm.impl;

import com.spms.backend.service.exception.NotFoundException;
import com.spms.backend.repository.entities.idm.Company;
import com.spms.backend.repository.entities.idm.CompanyType;
import com.spms.backend.repository.entities.idm.DivisionType;
import com.spms.backend.repository.entities.idm.Division;
import com.spms.backend.repository.idm.CompanyRepository;
import com.spms.backend.repository.idm.DivisionRepository;
import com.spms.backend.service.model.idm.DivisionModel;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class DivisionServiceImplTest {

    @MockitoBean
    private ClientRegistrationRepository clientRegistrationRepository;

    @Autowired
    private DivisionServiceImpl divisionService;

    @Autowired
    private DivisionRepository divisionRepository;

    @Autowired
    private CompanyRepository companyRepository;

    private Company testCompany;
    private Division testDivision1;
    private Division testDivision2;

    @BeforeEach
    void setUp() {


        // Create test company
        testCompany = new Company();
        testCompany.setName("Test Company");
        testCompany.setCompanyType(CompanyType.BUSINESS_ENTITY);
        testCompany.setActive(true);
        companyRepository.save(testCompany);

        // Create test divisions
        testDivision1 = new Division();
        testDivision1.setName("Division 1");
        testDivision1.setCompany(testCompany);
        testDivision1.setType(DivisionType.BUSINESS);
        testDivision1.setActive(true);
        divisionRepository.save(testDivision1);

        testDivision2 = new Division();
        testDivision2.setName("Division 2");
        testDivision2.setCompany(testCompany);
        testDivision2.setType(DivisionType.TECHNOLOGY);
        testDivision2.setActive(true);
        divisionRepository.save(testDivision2);
    }

    @AfterEach
    void tearDown() {
        divisionRepository.deleteAll();
        companyRepository.deleteAll();
    }

    @Test
    void contextLoads() {
        assertNotNull(divisionService);
    }

    @Test
    void getAllDivisions_shouldReturnPagedResults() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<DivisionModel> result = divisionService.getAllDivisions(pageable);
        
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertTrue(result.getContent().stream()
            .anyMatch(d -> d.getId().equals(testDivision1.getId())));
        assertTrue(result.getContent().stream()
            .anyMatch(d -> d.getId().equals(testDivision2.getId())));
    }

    @Test
    void getDivisionById_shouldReturnDivisionWhenExists() {
        DivisionModel result = divisionService.getDivisionById(testDivision1.getId());
        
        assertNotNull(result);
        assertEquals(testDivision1.getId(), result.getId());
        assertEquals(testDivision1.getName(), result.getName());
        assertEquals(testDivision1.getCompany().getId(), result.getCompanyId());
    }

    @Test
    void getDivisionById_shouldThrowNotFoundWhenMissing() {
        Long nonExistingId = -1L;
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            divisionService.getDivisionById(nonExistingId);
        });
        assertEquals("Division not found with id: " + nonExistingId, exception.getMessage());
    }

    @Test
    void findByCompanyId_shouldReturnDivisionsForCompany() {
        List<DivisionModel> result = divisionService.findByCompanyId(testCompany.getId());
        
        assertEquals(2, result.size());
        assertTrue(result.stream()
            .anyMatch(d -> d.getId().equals(testDivision1.getId())));
        assertTrue(result.stream()
            .anyMatch(d -> d.getId().equals(testDivision2.getId())));
        assertEquals(testCompany.getId(), result.get(0).getCompanyId());
    }

    @Test
    void createDivision_shouldCreateWhenCompanyExists() {
        DivisionModel newDivision = new DivisionModel();
        newDivision.setName("New Division");
        newDivision.setCompanyId(testCompany.getId());
        newDivision.setType(DivisionType.BUSINESS);
        newDivision.setActive(true);
        
        DivisionModel result = divisionService.createDivision(newDivision);
        
        assertNotNull(result.getId());
        assertEquals("New Division", result.getName());
        assertEquals(testCompany.getId(), result.getCompanyId());
        
        // Verify it was saved to database
        Division saved = divisionRepository.findById(result.getId()).orElseThrow();
        assertEquals("New Division", saved.getName());
        assertEquals(testCompany.getId(), saved.getCompany().getId());
    }

    @Test
    void createDivision_shouldThrowWhenCompanyMissing() {
        Long nonExistingCompanyId = -1L;
        DivisionModel newDivision = new DivisionModel();
        newDivision.setName("New Division");
        newDivision.setCompanyId(nonExistingCompanyId);
        newDivision.setActive(true);
        
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            divisionService.createDivision(newDivision);
        });
        
        assertEquals("Business Unit not found with id: " + nonExistingCompanyId, exception.getMessage());
        
        // Verify no division was created
        assertEquals(2, divisionRepository.count());
    }

    @Test
    void updateDivision_shouldUpdateWhenBothExist() {
        DivisionModel updateModel = new DivisionModel();
        updateModel.setName("Updated Division Name");
        updateModel.setCompanyId(testCompany.getId());
        updateModel.setType(DivisionType.TECHNOLOGY);
        updateModel.setActive(true);
        
        DivisionModel result = divisionService.updateDivision(testDivision1.getId(), updateModel);
        
        assertEquals("Updated Division Name", result.getName());
        assertEquals(testCompany.getId(), result.getCompanyId());
        
        // Verify changes were saved
        Division updated = divisionRepository.findById(testDivision1.getId()).orElseThrow();
        assertEquals("Updated Division Name", updated.getName());
        assertEquals(testCompany.getId(), updated.getCompany().getId());
    }

    @Test
    void deleteDivision_shouldDeleteWhenExists() {
        long initialCount = divisionRepository.count();
        divisionService.deleteDivision(testDivision1.getId());
        
        assertEquals(initialCount - 1, divisionRepository.count());
        assertFalse(divisionRepository.findById(testDivision1.getId()).isPresent());
    }

    @Test
    void bulkDeleteDivisions_shouldDeleteAllWhenExist() {
        long initialCount = divisionRepository.count();
        List<Long> idsToDelete = List.of(testDivision1.getId(), testDivision2.getId());
        
        divisionService.bulkDeleteDivisions(idsToDelete);
        
        assertEquals(initialCount - 2, divisionRepository.count());
        assertFalse(divisionRepository.findById(testDivision1.getId()).isPresent());
        assertFalse(divisionRepository.findById(testDivision2.getId()).isPresent());
    }

    @Test
    void isDivisionExists_shouldReturnCorrectBoolean() {
        assertTrue(divisionService.isDivisionExists(testDivision1.getId()));
        assertFalse(divisionService.isDivisionExists(-1L));
    }

    @Test
    void searchDivisions_shouldReturnMatchingResults() {
        Pageable pageable = PageRequest.of(0, 10);
        
        // Test exact match
        Page<DivisionModel> exactResult = divisionService.searchDivisions("Division 1", pageable);
        assertEquals(1, exactResult.getTotalElements());
        assertEquals(testDivision1.getId(), exactResult.getContent().get(0).getId());
        
        // Test partial match
        Page<DivisionModel> partialResult = divisionService.searchDivisions("Div", pageable);
        assertEquals(2, partialResult.getTotalElements());
        
        // Test case insensitive
        Page<DivisionModel> caseResult = divisionService.searchDivisions("division 2", pageable);
        assertEquals(1, caseResult.getTotalElements());
        assertEquals(testDivision2.getId(), caseResult.getContent().get(0).getId());
        
        // Test no match
        Page<DivisionModel> noMatch = divisionService.searchDivisions("Nonexistent", pageable);
        assertEquals(0, noMatch.getTotalElements());
    }
}
