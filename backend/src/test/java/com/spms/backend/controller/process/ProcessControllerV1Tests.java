package com.spms.backend.controller.process;

import com.spms.backend.service.exception.NotFoundException;
import com.spms.backend.service.exception.SpmsRuntimeException;
import com.spms.backend.service.model.process.ProcessDefinitionModel;
import com.spms.backend.service.model.process.ProcessVersionModel;
import com.spms.backend.service.model.process.VersionStatus;
import com.spms.backend.service.process.ProcessDefinitionService;
import com.spms.backend.service.process.ProcessDeploymentService;
import com.spms.backend.service.idm.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ProcessControllerV1Tests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProcessDefinitionService processService;

    @MockitoBean
    private ProcessDeploymentService processDeploymentService;

    @MockitoBean
    private UserService userService;

    private ProcessDefinitionModel mockDefinition;
    private ProcessVersionModel mockVersion;

    @BeforeEach
    void setUp() {
        mockDefinition = new ProcessDefinitionModel();
        mockDefinition.setId(21L);
        mockDefinition.setName("Test Process");
        mockDefinition.setKey("test-process");

        mockVersion = new ProcessVersionModel();
        mockVersion.setId(1L);
        mockVersion.setVersion("1.0.0");
        mockVersion.setStatus(VersionStatus.DRAFT);
    }

    @Test
    @WithMockUser(roles = "PROCESS_OWNER")
    void createProcessDefinition_Success() throws Exception {
        when(processService.createProcessDefinition(any())).thenReturn(mockDefinition);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/process/definitions")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"processName\":\"Test Process\",\"processKey\":\"test-process\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Process"));
    }

    @Test
    @WithMockUser(roles = "PROCESS_OWNER")
    void createProcessDefinitionVersion_Success() throws Exception {
        when(processService.getProcessDefinition(anyString())).thenReturn(mockDefinition);
        when(processService.createProcessDefinitionVersion(anyLong(), any())).thenReturn(mockVersion);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/process/definitions/test-definition/versions")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"version\":\"1.0.0\",\"bpmnXml\":\"<bpmn></bpmn>\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.version").value("1.0.0"));
    }

    @Test
    @WithMockUser(roles = "PROCESS_OWNER")
    void getProcessDefinition_Success() throws Exception {
        when(processService.getProcessDefinition(anyString())).thenReturn(mockDefinition);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/process/definitions/test-definition"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Process"));
    }

    @Test
    @WithMockUser(roles = "PROCESS_OWNER")
    void getProcessDefinitionVersions_Success() throws Exception {
        Page<ProcessVersionModel> page = new PageImpl<>(Collections.singletonList(mockVersion));
        when(processService.getDefinitionVersions(anyString(), any())).thenReturn(page);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/process/definitions/test-definition/versions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].version").value("1.0.0"));
    }

    @Test
    @WithMockUser(roles = "PROCESS_OWNER")
    void activeProcessDefinitionVersion_Success() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/process/definitions/1/versions/1.0.0/active"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "PROCESS_OWNER")
    void disableProcessDefinitionVersion_Success() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/process/definitions/1/versions/1.0.0/active"))
                .andExpect(status().isOk());
    }
}