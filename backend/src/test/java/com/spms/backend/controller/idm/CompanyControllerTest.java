package com.spms.backend.controller.idm;

import com.spms.backend.controller.dto.idm.CompanyDTO;
import com.spms.backend.controller.dto.idm.CreateCompanyRequestDTO;
import com.spms.backend.controller.dto.idm.JoinToChildrenRequestDTO;
import com.spms.backend.controller.dto.idm.OrganizationChartDTO;
import com.spms.backend.controller.dto.idm.ChartMode;
import com.spms.backend.repository.entities.idm.CompanyType;
import com.spms.backend.service.idm.CompanyService;
import com.spms.backend.service.idm.OrganizationService;
import com.spms.backend.service.model.idm.CompanyModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompanyControllerTest {

    @Mock
    private CompanyService companyService;

    @Mock
    private OrganizationService organizationService;

    @InjectMocks
    private CompanyController companyController;

    // Test Data Builders
    private static class TestDataBuilder {
        static CompanyModel validCompanyModel() {
            CompanyModel model = new CompanyModel();
            model.setId(1L);
            model.setName("Test Company");
            model.setActive(true);
            model.setCompanyType(CompanyType.BUSINESS_ENTITY);
            return model;
        }

        static CreateCompanyRequestDTO validCreateRequest() {
            CreateCompanyRequestDTO request = new CreateCompanyRequestDTO();
            request.setName("New Company");
            request.setActive(true);
            request.setCompanyType(CompanyType.BUSINESS_ENTITY);
            request.setParentId(1L);
            return request;
        }

        static CompanyDTO validCompanyDTO() {
            CompanyDTO dto = new CompanyDTO();
            dto.setId(1L);
            dto.setName("Test Company");
            dto.setActive(true);
            dto.setCompanyType(CompanyType.BUSINESS_ENTITY);
            return dto;
        }

        static Page<CompanyModel> companyModelPage() {
            return new PageImpl<>(Collections.singletonList(validCompanyModel()));
        }

        static OrganizationChartDTO validOrganizationChart() {
            OrganizationChartDTO chart = new OrganizationChartDTO();
            chart.setId("1");
            chart.setName("Test Org Chart");
            return chart;
        }
    }

    @Test
    void getValidParents_ReturnsListOfCompanies() {
        // Given
        when(companyService.getValidParents(any()))
            .thenReturn(Collections.singletonList(TestDataBuilder.validCompanyModel()));

        // When
        ResponseEntity<List<CompanyDTO>> response = 
            companyController.getValidParents(CompanyType.BUSINESS_ENTITY);

        // Then
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(companyService).getValidParents(any());
    }

    @Test
    void addChildren_WithValidRequest_ReturnsPageOfCompanies() {
        // Given
        JoinToChildrenRequestDTO request = new JoinToChildrenRequestDTO();
        request.setIds(Collections.singletonList(1L));
        when(companyService.isCompanyExists(any(Long.class))).thenReturn(true);
        when(companyService.canAddAsChildren(any(), any())).thenReturn(Collections.emptyList());
        doNothing().when(companyService).addCompanyToChildren(any(), any());
        when(companyService.getChildren(any(), any(), any()))
            .thenReturn(TestDataBuilder.companyModelPage());

        // When
        ResponseEntity<Page<CompanyDTO>> response = 
            companyController.addChildren(1L, request);

        // Then
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        verify(companyService).addCompanyToChildren(any(), any());
    }

    @Test
    void getChildren_ReturnsPageOfCompanies() {
        // Given
        when(companyService.isCompanyExists(any(Long.class))).thenReturn(true);
        when(companyService.getChildren(any(), any(), any()))
            .thenReturn(TestDataBuilder.companyModelPage());

        // When
        ResponseEntity<Page<CompanyDTO>> response = 
            companyController.getChildren(1L, null, Pageable.unpaged());

        // Then
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        verify(companyService).getChildren(any(), any(), any());
    }

    @Test
    void getChildren_WhenCompanyNotFound_ReturnsNotFound() {
        // Given
        when(companyService.isCompanyExists(any(Long.class))).thenReturn(false);

        // When
        ResponseEntity<Page<CompanyDTO>> response = 
            companyController.getChildren(1L, null, Pageable.unpaged());

        // Then
        assertEquals(404, response.getStatusCodeValue());
        verify(companyService, never()).getChildren(any(), any(), any());
    }

    @Test
    void getAllCompanies_ReturnsPageOfCompanies() {
        // Given
        when(companyService.getAllCompanies(any(), any(), any()))
            .thenReturn(TestDataBuilder.companyModelPage());

        // When
        ResponseEntity<Page<CompanyDTO>> response = 
            companyController.getAllCompanies(null, null, Pageable.unpaged());

        // Then
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getTotalElements());
        verify(companyService).getAllCompanies(any(), any(), any());
    }

    @Test
    void createCompany_WithValidParent_ReturnsCreatedCompany() {
        // Given
        CreateCompanyRequestDTO request = TestDataBuilder.validCreateRequest();
        CompanyModel savedModel = TestDataBuilder.validCompanyModel();
        
        when(companyService.isValidParent(any(), any())).thenReturn(true);
        when(companyService.getCompanyByCompanyId(any()))
            .thenReturn(Optional.of(new CompanyModel()));
        when(companyService.createCompany(any())).thenReturn(savedModel);

        // When
        ResponseEntity<CompanyDTO> response = companyController.createCompany(request);

        // Then
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(savedModel.getId(), response.getBody().getId());
        verify(companyService).createCompany(any());
    }

    @Test
    void createCompany_WithInvalidParent_ReturnsBadRequest() {
        // Given
        CreateCompanyRequestDTO request = TestDataBuilder.validCreateRequest();
        when(companyService.isValidParent(any(), any())).thenReturn(false);

        // When
        ResponseEntity<CompanyDTO> response = companyController.createCompany(request);

        // Then
        assertEquals(400, response.getStatusCodeValue());
        verify(companyService, never()).createCompany(any());
    }

    @Test
    void createCompany_WithMissingParent_ReturnsNotFound() {
        // Given
        CreateCompanyRequestDTO request = TestDataBuilder.validCreateRequest();
        when(companyService.isValidParent(any(), any())).thenReturn(true);
        when(companyService.getCompanyByCompanyId(any()))
            .thenReturn(Optional.empty());

        // When
        ResponseEntity<CompanyDTO> response = companyController.createCompany(request);

        // Then
        assertEquals(404, response.getStatusCodeValue());
        verify(companyService, never()).createCompany(any());
    }

    @Test
    void updateCompany_WithValidRequest_ReturnsUpdatedCompany() {
        // Given
        CompanyDTO updateRequest = TestDataBuilder.validCompanyDTO();
        CompanyModel updatedModel = TestDataBuilder.validCompanyModel();
        updatedModel.setName("Updated Name");
        
        when(companyService.updateCompany(any(Long.class), any(CompanyModel.class)))
            .thenReturn(updatedModel);

        // When
        ResponseEntity<CompanyDTO> response = companyController.updateCompany(1L, updateRequest);

        // Then
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("Updated Name", response.getBody().getName());
        verify(companyService).updateCompany(any(Long.class), any(CompanyModel.class));
    }

    @Test
    void deleteCompany_WithExistingId_ReturnsSuccess() {
        // Given
        doNothing().when(companyService).deleteCompany(any(Long.class));

        // When
        ResponseEntity<Void> response = companyController.deleteCompany(1L);

        // Then
        assertEquals(204, response.getStatusCodeValue());
        verify(companyService).deleteCompany(any(Long.class));
    }

    @Test
    void deleteCompany_WithNonExistingId_ReturnsNotFound() {
        // Given
        doNothing().when(companyService).deleteCompany(any(Long.class));

        // When
        ResponseEntity<Void> response = companyController.deleteCompany(1L);

        // Then
        assertEquals(204, response.getStatusCodeValue());
        verify(companyService).deleteCompany(any(Long.class));
    }

    @Test
    void addChildren_WithInvalidChildren_ReturnsBadRequest() {
        // Given
        JoinToChildrenRequestDTO request = new JoinToChildrenRequestDTO();
        request.setIds(Collections.singletonList(1L));
        
        when(companyService.canAddAsChildren(any(), any()))
            .thenReturn(Collections.singletonList("Invalid relationship"));

        // When
        ResponseEntity<Page<CompanyDTO>> response = 
            companyController.addChildren(1L, request);

        // Then
        assertEquals(400, response.getStatusCodeValue());
        verify(companyService, never()).addCompanyToChildren(any(), any());
    }


    @Test
    void getOrganizationChart_WithValidCompany_ReturnsChart() {
        // Given
        OrganizationChartDTO chart = TestDataBuilder.validOrganizationChart();
        
        when(organizationService.getOrganizationChart(any(Long.class), any(ChartMode.class)))
            .thenReturn(chart);

        // When
        ResponseEntity<OrganizationChartDTO> response = 
            companyController.getOrganizationChart(1L, ChartMode.FUNCTIONAL);

        // Then
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("Test Org Chart", response.getBody().getName());
        verify(organizationService).getOrganizationChart(any(Long.class), any());
    }

    @Test
    void getOrganizationChart_WithInvalidCompany_ReturnsNotFound() {
        // Given
        when(organizationService.getOrganizationChart(any(Long.class), any(ChartMode.class)))
            .thenReturn(null);

        // When
        ResponseEntity<OrganizationChartDTO> response = 
            companyController.getOrganizationChart(1L, ChartMode.FUNCTIONAL);

        // Then
        assertEquals(200, response.getStatusCodeValue());
        verify(organizationService).getOrganizationChart(any(Long.class), any());
    }
}
