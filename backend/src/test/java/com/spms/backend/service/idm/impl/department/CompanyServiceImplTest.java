package com.spms.backend.service.idm.impl.department;

import com.spms.backend.repository.entities.idm.Department;
import com.spms.backend.repository.entities.idm.DepartmentType;
import com.spms.backend.repository.entities.idm.User;
import com.spms.backend.repository.idm.DepartmentRepository;
import com.spms.backend.repository.idm.UserRepository;
import com.spms.backend.service.idm.CompanyService;
import com.spms.backend.service.idm.DivisionService;
import com.spms.backend.service.idm.impl.DepartmentServiceImpl;
import com.spms.backend.service.model.idm.DepartmentModel;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class CompanyServiceImplTest {

    @MockitoBean
    private ClientRegistrationRepository clientRegistrationRepository;

    @MockitoBean
    private DepartmentRepository departmentRepository;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private DivisionService divisionService;

    @MockitoBean
    private CompanyService companyService;

    @Autowired
    private DepartmentServiceImpl departmentService;

    private Department testDepartment;
    private User testUser;

    @BeforeEach
    void setUp() {
        // Create test department
        testDepartment = new Department();
        testDepartment.setId(1L);
        testDepartment.setName("Test Department");
        testDepartment.setType(DepartmentType.FUNCTIONAL);
        testDepartment.setActive(true);

        // Create test user
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
    }

    @AfterEach
    void tearDown() {
        reset(departmentRepository);
        reset(userRepository);
        reset(divisionService);
        reset(companyService);
    }

    @Test
    void contextLoads() {
        assertNotNull(departmentService);
    }

    @Test
    void listDepartments_shouldReturnPagedResults() {
        // Setup
        Pageable pageable = PageRequest.of(0, 10);

        Page<Department> mockPage = new PageImpl<>(
                List.of(testDepartment),
                pageable,1
        );
        when(departmentRepository.findAll(pageable)).thenReturn(mockPage);

        // Execute
        Page<DepartmentModel> result = departmentService.listDepartments(pageable, null);

        // Verify
        assertNotNull(result);
        assertNotNull(result.getContent());
        assertEquals(1, result.getTotalElements());
        assertEquals(testDepartment.getId(), result.getContent().get(0).getId());
        assertEquals(testDepartment.getName(), result.getContent().get(0).getName());
        verify(departmentRepository).findAll(pageable);
    }

    @Test
    void createDepartment_withValidData_shouldCreateDepartment() {
        // Setup
        DepartmentModel inputModel = new DepartmentModel();
        inputModel.setName("New Department");
        inputModel.setType(DepartmentType.FUNCTIONAL);
        inputModel.setActive(true);

        when(departmentRepository.save(any(Department.class))).thenAnswer(invocation -> {
            Department saved = invocation.getArgument(0);
            saved.setId(2L); // Simulate generated ID
            return saved;
        });

        // Execute
        DepartmentModel result = departmentService.createDepartment(inputModel);

        // Verify
        assertNotNull(result);
        assertEquals(2L, result.getId());
        assertEquals("New Department", result.getName());
        assertEquals(DepartmentType.FUNCTIONAL, result.getType());
        verify(departmentRepository).save(any(Department.class));
    }

    @Test
    void createDepartment_withEmptyName_shouldThrowException() {
        // Setup
        DepartmentModel inputModel = new DepartmentModel();
        inputModel.setName("");
        inputModel.setType(DepartmentType.FUNCTIONAL);
        inputModel.setActive(true);

        // Execute & Verify
        assertThrows(IllegalArgumentException.class, () -> {
            departmentService.createDepartment(inputModel);
        });
        verify(departmentRepository, never()).save(any());
    }

    @Test
    void createDepartment_withMissingName_shouldThrowException() {
        // Setup
        DepartmentModel inputModel = new DepartmentModel();
        inputModel.setType(DepartmentType.FUNCTIONAL);
        inputModel.setActive(true);

        // Execute & Verify
        assertThrows(IllegalArgumentException.class, () -> {
            departmentService.createDepartment(inputModel);
        });
        verify(departmentRepository, never()).save(any());
    }

    @Test
    void createDepartment_withMissingType_shouldThrowException() {
        // Setup
        DepartmentModel inputModel = new DepartmentModel();
        inputModel.setName("Test");
        inputModel.setActive(true);

        // Execute & Verify
        assertThrows(IllegalArgumentException.class, () -> {
            departmentService.createDepartment(inputModel);
        });
        verify(departmentRepository, never()).save(any());
    }

    @Test
    void findByParentAndType_withValidInput_shouldReturnDepartments() {
        // Setup
        String parentId = "1";
        DepartmentType type = DepartmentType.FUNCTIONAL;
        Department department = new Department();
        department.setId(1L);
        department.setName("Test Department");
        department.setParent(1L);
        department.setType(type);

        when(departmentRepository.findByParentAndType(1L, type))
            .thenReturn(List.of(department));

        // Execute
        List<DepartmentModel> result = departmentService.findByParentAndType(parentId, type);

        // Verify
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(department.getId(), result.get(0).getId());
        verify(departmentRepository).findByParentAndType(1L, type);
    }

    @Test
    void findByParentAndType_withEmptyParentId_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> {
            departmentService.findByParentAndType("", DepartmentType.FUNCTIONAL);
        });
    }

    @Test
    void findByParentAndType_withNullParentId_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> {
            departmentService.findByParentAndType(null, DepartmentType.FUNCTIONAL);
        });
    }

    @Test
    void findByParentAndType_withNullType_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> {
            departmentService.findByParentAndType("1", null);
        });
    }

    @Test
    void findByParentAndType_withInvalidNumberFormat_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> {
            departmentService.findByParentAndType("invalid", DepartmentType.FUNCTIONAL);
        });
    }

    @Test
    void findByParentAndType_withNoResults_shouldReturnEmptyList() {
        // Setup
        when(departmentRepository.findByParentAndType(1L, DepartmentType.FUNCTIONAL))
            .thenReturn(List.of());

        // Execute
        List<DepartmentModel> result = departmentService.findByParentAndType("1", DepartmentType.FUNCTIONAL);

        // Verify
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
