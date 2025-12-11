package com.spms.backend.service.idm.impl.department;

import com.spms.backend.repository.entities.idm.*;
import com.spms.backend.repository.idm.CompanyRepository;
import com.spms.backend.repository.idm.DepartmentRepository;
import com.spms.backend.repository.idm.DivisionRepository;
import com.spms.backend.repository.idm.UserRepository;
import com.spms.backend.service.model.idm.DepartmentModel;
import com.spms.backend.service.idm.impl.DepartmentServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class DepartmentQueryTests {
    @MockitoBean
    private ClientRegistrationRepository clientRegistrationRepository;

    @Autowired
    private DepartmentServiceImpl departmentService;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private DivisionRepository divisionRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    private Department functionalDepartment;
    private Department localDepartment;
    private Department functionalTeam;
    private Department localTeam;
    private Company companyBusinessEnt ;
    private Division division;
    @BeforeEach
    void setUp() {

        division = new Division();
        division.setName("Division Test");
        division.setActive(true);
        division.setType(DivisionType.BUSINESS);
        division.setDescription("Division Test");

        division = divisionRepository.save(division);

        companyBusinessEnt = new Company();
        companyBusinessEnt.setCompanyType(CompanyType.BUSINESS_ENTITY);
        companyBusinessEnt.setName("Business");
        companyBusinessEnt.setDescription("Description Company");
        companyBusinessEnt =companyRepository.save(companyBusinessEnt);




        functionalDepartment = new Department();
        functionalDepartment.setName("Test Functional");
        functionalDepartment.setType(DepartmentType.FUNCTIONAL);
        functionalDepartment.setLevel(1);
        functionalDepartment.setParent(division.getId());
        functionalDepartment = departmentRepository.save(functionalDepartment);


        localDepartment = new Department();
        localDepartment.setName("Local Department");
        localDepartment.setType(DepartmentType.LOCAL);
        localDepartment.setLevel(1);
        localDepartment.setParent(companyBusinessEnt.getId());
        localDepartment = departmentRepository.save(localDepartment);

        localTeam = new Department();
        localTeam.setName("Local Team");
        localTeam.setType(DepartmentType.TEAM);
        localTeam.setParent(localDepartment.getId());
        localTeam.setLevel(1);
        localTeam = departmentRepository.save(localTeam);

        functionalTeam = new Department();
        functionalTeam.setName("Test Team");
        functionalTeam.setType(DepartmentType.FUNCTIONAL_TEAM);
        functionalTeam.setParent(functionalDepartment.getId());
        functionalTeam.setLevel(1);
        functionalTeam = departmentRepository.save(functionalTeam);
    }

    @AfterEach
    void tearDown() {
        departmentRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void testFindByParentAndType_validInput() {
        List<DepartmentModel> results = departmentService.findByParentAndType(
            localDepartment.getId().toString(), DepartmentType.TEAM);
        
        assertEquals(1, results.size());
        assertEquals(localTeam.getId(), results.get(0).getId());
    }

    @Test
    void testFindByParentAndType_emptyParentId() {

        // All the department shall have the parent.
        assertThrows(IllegalArgumentException.class,()->{
            List<DepartmentModel> results = departmentService.findByParentAndType(
                    null, DepartmentType.FUNCTIONAL);
        });
    }

    @Test
    void testFindByParentAndType_nullType() {
        assertThrows(IllegalArgumentException.class, () -> 
            departmentService.findByParentAndType(functionalDepartment.getId().toString(), null));
    }

    @Test
    void testFindByParentAndType_invalidParentId() {
        List<DepartmentModel> results = departmentService.findByParentAndType(
            "-1", DepartmentType.TEAM);
        
        assertTrue(results.isEmpty());
    }
}
