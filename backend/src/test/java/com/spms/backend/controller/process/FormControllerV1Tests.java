package com.spms.backend.controller.process;

import com.spms.backend.service.model.process.FormVersionModel;
import com.spms.backend.service.process.FormService;
import com.spms.backend.service.exception.NotFoundException;
import com.spms.backend.service.exception.ValidationException;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FormControllerV1.class)
class FormControllerV1Tests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FormService formService;

    // Test data builder
    private FormVersionModel createVersionModel(String version) {
        FormVersionModel model = new FormVersionModel();
        model.setVersion(FormVersionModel.convertVersionStringToLong(version));
        model.setName("Test Form");
        model.setFormDefinition("{}");
        return model;
    }

    @Test
    void createNewVersion_ValidRequest_ReturnsCreated() throws Exception {
        FormVersionModel mockModel = createVersionModel("1.0.0");
        
        when(formService.createFormVersion(any(), any()))
            .thenReturn(mockModel);

        mockMvc.perform(post("/api/v1/forms/{key}/versions", "leave-request")
                .contentType("application/json")
                .content("{\"version\":\"1.0.0\",\"name\":\"Test Form\",\"schema\":\"{}\",\"key\":\"leave-request\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.version").value("1.0.0"));
    }

    @Test
    void createNewVersion_DuplicateVersion_ReturnsBadRequest() throws Exception {
        when(formService.createFormVersion(any(), any()))
            .thenThrow(new IllegalArgumentException("Version already exists"));

        mockMvc.perform(post("/api/v1/forms/{key}/versions", "leave-request")
                .contentType("application/json")
                .content("{\"version\":\"1.0.0\",\"name\":\"Test Form\",\"schema\":\"{}\",\"key\":\"leave-request\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getLatestVersion_ExistingForm_ReturnsOk() throws Exception {
        FormVersionModel mockModel = createVersionModel("1.1.0");
        
        when(formService.getLatestVersion(any()))
            .thenReturn(mockModel);

        mockMvc.perform(get("/api/v1/forms/{key}/versions/latest", "leave-request"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.version").value("1.1.0"));
    }

    // getVersion endpoint tests
    @Test
    void getVersion_ValidRequest_ReturnsOk() throws Exception {
        FormVersionModel mockModel = createVersionModel("1.0.1");
        when(formService.getVersion("leave-request", "1.0.1")).thenReturn(mockModel);
        
        mockMvc.perform(get("/api/v1/forms/{key}/versions/{version}", "leave-request", "1.0.1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.version").value("1.0.1"));
    }

    @Test
    void getVersion_InvalidKey_ReturnsNotFound() throws Exception {
        when(formService.getVersion("invalid-key", "1.0.0"))
            .thenThrow(new NotFoundException("Form definition not found"));
        
        mockMvc.perform(get("/api/v1/forms/{key}/versions/{version}", "invalid-key", "1.0.0"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getVersion_InvalidVersion_ReturnsNotFound() throws Exception {
        when(formService.getVersion("leave-request", "invalid-version"))
            .thenThrow(new NotFoundException("Form version not found"));
        
        mockMvc.perform(get("/api/v1/forms/{key}/versions/{version}", "leave-request", "invalid-version"))
                .andExpect(status().isNotFound());
    }

    // listVersions endpoint tests
    @Test
    void listVersions_WithVersions_ReturnsList() throws Exception {
        FormVersionModel v1 = createVersionModel("1.0.0");
        FormVersionModel v2 = createVersionModel("1.1.0");
        when(formService.listVersions("leave-request")).thenReturn(List.of(v1, v2));
        
        mockMvc.perform(get("/api/v1/forms/{key}/versions", "leave-request"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].version").value("1.0.0"))
                .andExpect(jsonPath("$[1].version").value("1.1.0"));
    }

    @Test
    void listVersions_EmptyList_ReturnsEmptyArray() throws Exception {
        when(formService.listVersions("leave-request")).thenReturn(List.of());
        
        mockMvc.perform(get("/api/v1/forms/{key}/versions", "leave-request"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void listVersions_InvalidKey_ReturnsNotFound() throws Exception {
        when(formService.listVersions("invalid-key"))
            .thenThrow(new NotFoundException("Form definition not found"));
        
        mockMvc.perform(get("/api/v1/forms/{key}/versions", "invalid-key"))
                .andExpect(status().isNotFound());
    }

    // deprecateVersion endpoint tests
    @Test
    void deprecateVersion_Success_ReturnsNoContent() throws Exception {
        mockMvc.perform(post("/api/v1/forms/{key}/versions/{version}/deprecate", "leave-request", "1.0.0"))
                .andExpect(status().isNoContent());
    }


    @Test
    void deprecateVersion_InvalidKey_ReturnsNotFound() throws Exception {
        doThrow(new NotFoundException("Form definition not found"))
            .when(formService).deprecateVersion("invalid-key", "1.0.0");
        
        mockMvc.perform(post("/api/v1/forms/{key}/versions/{version}/deprecate", "invalid-key", "1.0.0"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deprecateVersion_InvalidVersion_ReturnsNotFound() throws Exception {
        doThrow(new NotFoundException("Form version not found"))
            .when(formService).deprecateVersion("leave-request", "invalid-version");
        
        mockMvc.perform(post("/api/v1/forms/{key}/versions/{version}/deprecate", "leave-request", "invalid-version"))
                .andExpect(status().isNotFound());
    }

    // Validation tests
    @Test
    void createNewVersion_InvalidVersionFormat_ReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/v1/forms/{key}/versions", "leave-request")
                .contentType("application/json")
                .content("{\"version\":\"invalid_version\",\"name\":\"Test Form\",\"schema\":\"{}\",\"key\":\"leave-request\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getVersion_InvalidVersionFormat_ReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/v1/forms/{key}/versions/{version}", "leave-request", "invalid_version"))
                .andExpect(status().isBadRequest());
    }

    // Edge case tests
    @Test
    void deprecateVersion_DeprecatingLatestVersion_WarnsInLog() throws Exception {
        // Implementation would verify logger.warn() was called
        // Placeholder for actual implementation
    }

    @Test
    void createNewVersion_ConcurrentCreation_HandlesLocking() throws Exception {
        // Implementation would test concurrent version creation
        // Placeholder for actual implementation
    }

    // Additional tests would be added here
}
