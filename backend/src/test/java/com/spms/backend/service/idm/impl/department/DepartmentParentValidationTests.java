package com.spms.backend.service.idm.impl.department;

import com.spms.backend.repository.entities.idm.*;
import com.spms.backend.repository.idm.CompanyRepository;
import com.spms.backend.repository.idm.DepartmentRepository;
import com.spms.backend.repository.idm.DivisionRepository;
import com.spms.backend.service.idm.impl.DepartmentServiceImpl;
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
public class DepartmentParentValidationTests {

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

    @Test
    void testIsValidParent_FUNCTIONAL() {
        // Division
        assertTrue(departmentService.isValidParent(1, division.getId(), DepartmentType.FUNCTIONAL));
        // Function Department
        assertTrue(departmentService.isValidParent(2, functionalDepartment.getId(), DepartmentType.FUNCTIONAL));
    }

    @Test
    void testIsValidParent_LOCAL() {
        //First tier local department
        assertTrue(departmentService.isValidParent(1, companyBusinessEnt.getId(), DepartmentType.LOCAL));
        // Second tier local department shall attach it's local department
        assertTrue(departmentService.isValidParent(2, localDepartment.getId(), DepartmentType.LOCAL));
    }

    @Test
    void testIsValidParent_TEAM() {
        assertTrue(departmentService.isValidParent(1, localDepartment.getId(), DepartmentType.TEAM));
        assertTrue(departmentService.isValidParent(2, localTeam.getId(), DepartmentType.TEAM));
    }

    @Test
    void testIsValidParent_FUNCTIONAL_TEAM() {
        // Level 1 function Team shall add to a function department.
        assertTrue(departmentService.isValidParent(1 , functionalDepartment.getId(), DepartmentType.FUNCTIONAL_TEAM));
        // Level 2 + functional team shall add to a functional team
        assertTrue(departmentService.isValidParent(3, functionalTeam.getId(), DepartmentType.FUNCTIONAL_TEAM));
    }

    @Test
    void testIsValidParent_invalidParent() {
        assertFalse(departmentService.isValidParent(1, -1L, DepartmentType.FUNCTIONAL));
        assertFalse(departmentService.isValidParent(2, localDepartment.getId(), DepartmentType.FUNCTIONAL));
    }
}
