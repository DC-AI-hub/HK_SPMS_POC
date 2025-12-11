package com.spms.backend.service.idm.impl.department;

import com.spms.backend.repository.entities.idm.*;
import com.spms.backend.repository.idm.CompanyRepository;
import com.spms.backend.repository.idm.DepartmentRepository;
import com.spms.backend.repository.idm.DivisionRepository;
import com.spms.backend.repository.idm.UserRepository;
import com.spms.backend.service.idm.impl.DepartmentServiceImpl;
import com.spms.backend.service.model.idm.DepartmentModel;
import com.spms.backend.service.model.idm.UserModel;
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
public class DepartmentServiceIntegrationTest {

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

        testUser = new User();
        testUser.setUsername("testUser");
        testUser.setEmail("testuser@email.com");
        testUser.setType(User.UserType.STAFF);
        testUser.setDescription("Description");
        testUser.setProvider("keycloak");
        testUser =userRepository.save(testUser);
    }

    @AfterEach
    void tearDown() {
        divisionRepository.deleteAll();
        departmentRepository.deleteAll();
        userRepository.deleteAll();
        companyRepository.deleteAll();
    }


    @Test
    void contextLoads() {
        assertNotNull(departmentService);
    }

    @Test
    void testListDepartments_withPaging() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<DepartmentModel> result = departmentService.listDepartments(pageable, null);

        assertNotNull(result);
        assertEquals(4, result.getTotalElements());
        assertTrue(result.getContent().stream()
            .anyMatch(d -> d.getName().equals("Test Functional")));
        assertTrue(result.getContent().stream()
            .anyMatch(d -> d.getName().equals("Local Team")));
    }

    @Test
    void testListDepartments_withNameFilter() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<DepartmentModel> result = departmentService.listDepartments(pageable, "Functional");

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Test Functional", result.getContent().get(0).getName());
    }

    @Test
    void testAddUserToDepartment_success() {
        DepartmentModel deptModel = new DepartmentModel(functionalDepartment);
        UserModel userModel = UserModel.fromEntity(testUser);
        
        boolean result = departmentService.addUserToDepartment(deptModel, List.of(userModel));
        
        assertTrue(result);
        Department updatedDept = departmentRepository.findById(functionalDepartment.getId()).get();
        assertTrue(updatedDept.getUsers().stream()
            .anyMatch(u -> u.getId().equals(testUser.getId())));
    }

    @Test
    void testAddUserToDepartment_duplicateUsers() {
        DepartmentModel deptModel = new DepartmentModel(functionalDepartment);
        UserModel userModel = UserModel.fromEntity(testUser);
        
        // First add should succeed
        boolean firstResult = departmentService.addUserToDepartment(deptModel, List.of(userModel));
        assertTrue(firstResult);

        // Mute add should be succeed
        boolean secondResult = departmentService.addUserToDepartment(deptModel, List.of(userModel));
        assertTrue(secondResult);
        
        // Verify only one user association exists
        Department updatedDept = departmentRepository.findById(functionalDepartment.getId()).get();
        assertEquals(1, updatedDept.getUsers().size());
    }

    @Test
    void testAddUserToDepartment_failure() {
        // Test null department
        assertFalse(departmentService.addUserToDepartment(null, List.of(UserModel.fromEntity(testUser))));
        
        // Test null users list
        DepartmentModel deptModel = new DepartmentModel(functionalDepartment);

        assertFalse(departmentService.addUserToDepartment(deptModel, null));
        
        // Test empty users list
        assertFalse(departmentService.addUserToDepartment(deptModel, List.of()));
    }

    @Test
    void testDeleteUserFromDepartment_success() {
        // First add user
        DepartmentModel deptModel = new DepartmentModel(functionalDepartment);
        departmentService.addUserToDepartment(deptModel, List.of(UserModel.fromEntity(testUser)));
        
        // Then delete user
        boolean result = departmentService.deleteUserFromDepartment(deptModel, List.of(UserModel.fromEntity(testUser)));
        
        assertTrue(result);
        Department updatedDept = departmentRepository.findById(functionalDepartment.getId()).get();
        assertTrue(updatedDept.getUsers().isEmpty());
    }

    @Test
    void testDeleteUserFromDepartment_nonExistingUsers() {
        DepartmentModel deptModel = new DepartmentModel(functionalDepartment);
        UserModel userModel = UserModel.fromEntity(testUser);
        
        // Try to delete user that was never added
        boolean result = departmentService.deleteUserFromDepartment(deptModel, List.of(userModel));
        
        assertFalse(result);
        Department updatedDept = departmentRepository.findById(functionalDepartment.getId()).get();
        assertNull(updatedDept.getUsers());
        //assertTrue(updatedDept.getUsers().isEmpty());
    }

    @Test
    void testDeleteUserFromDepartment_failure() {
        // Test null department
        assertThrows(IllegalArgumentException.class, () -> 
            departmentService.deleteUserFromDepartment(null, List.of(UserModel.fromEntity(testUser))));
        
        // Test null users list
        DepartmentModel deptModel = new DepartmentModel(functionalDepartment);
        assertThrows(IllegalArgumentException.class, () -> 
            departmentService.deleteUserFromDepartment(deptModel, null));
        
        // Test empty users list
        assertThrows(IllegalArgumentException.class, () -> 
            departmentService.deleteUserFromDepartment(deptModel, List.of()));
    }

    @Test
    void testGetUserDepartments_withDepartments() {
        // Add user to department
        DepartmentModel deptModel = new DepartmentModel(functionalDepartment);
        departmentService.addUserToDepartment(deptModel, List.of(UserModel.fromEntity(testUser)));
        
        // Get user's departments
        List<DepartmentModel> results = departmentService.getUserDepartment(UserModel.fromEntity(testUser));
        
        assertFalse(results.isEmpty());
        assertTrue(results.stream()
            .anyMatch(d -> d.getId().equals(functionalDepartment.getId())));
    }

    @Test
    void testGetUserDepartments_noDepartments() {
        // Create new user that hasn't been added to any departments
        User newUser = new User();
        newUser.setUsername("newuser");
        newUser.setEmail("new@example.com");
        newUser.setType(User.UserType.STAFF);
        newUser.setProvider("local");
        userRepository.save(newUser);
        
        // Get user's departments
        List<DepartmentModel> results = departmentService.getUserDepartment(UserModel.fromEntity(newUser));
        
        assertTrue(results.isEmpty());
    }

    @Test
    void testIsValidParent_FUNCTIONAL() {
        // Level 1 department with valid division parent
        // Using functionalDepartment as parent since it's already created in setUp()
        boolean level1Result = departmentService.isValidParent(1, division.getId(), DepartmentType.FUNCTIONAL);
        assertTrue(level1Result);
        
        // Create level 2 functional department
        Department level2Dept = new Department();
        level2Dept.setName("Level 2 Functional");
        level2Dept.setType(DepartmentType.FUNCTIONAL);
        level2Dept.setLevel(2);
        level2Dept.setParent(functionalDepartment.getId());
        departmentRepository.save(level2Dept);
        
        // Level 2 department with valid functional parent
        boolean level2Result = departmentService.isValidParent(2, functionalDepartment.getId(), DepartmentType.FUNCTIONAL);
        assertTrue(level2Result);
    }
}
