package com.spms.backend.controller.process;

import com.spms.backend.repository.entities.idm.User;
import com.spms.backend.service.exception.VersionConflictException;
import com.spms.backend.service.idm.impl.UserModelFulfilledSupporterImpl;
import com.spms.backend.service.model.idm.UserModel;
import com.spms.backend.service.model.process.ProcessVersionModel;
import com.spms.backend.service.model.process.VersionStatus;
import com.spms.backend.service.process.ProcessDefinitionService;
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
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.data.domain.Pageable;

@SpringBootTest
@AutoConfigureMockMvc
class ProcessVersionTests {

    @MockitoBean
    private ClientRegistrationRepository clientRegistrationRepository; // Fixes the missing bean

    @Autowired
    private ProcessDefinitionService processService;

    @MockitoBean
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String definitionId;

    private ProcessVersionModel version1;
    private ProcessVersionModel version2;
    private ProcessVersionModel version3;
    private UserModel user;

    @BeforeEach
    public void setup() throws Exception {
        definitionId = "test-definition";
        
        // Create versions with different statuses
        version1 = createTestVersion("1.0.1", VersionStatus.DRAFT);
        version2 = createTestVersion("1.0.2", VersionStatus.ACTIVE);
        version3 = createTestVersion("1.0.3", VersionStatus.INACTIVE);

        user = new UserModel();
        user.setId(1L);
        user.setUsername("test-user");
        user.setType(User.UserType.STAFF);
        when(userService.getFulfilledSupporter()).thenReturn(new UserModelFulfilledSupporterImpl(userService));
        when(userService.getUserById(anyLong())).thenReturn(user);

    }

    private ProcessVersionModel createTestVersion(String version, VersionStatus status) throws Exception {
        ProcessVersionModel model = new ProcessVersionModel();
        model.setVersion(version);
        model.setStatus(status);
        model.setCreatedById(1L);
        model.setCreatedAt(new Date().getTime());
        model.setUpdatedById(1L);
        model.setName("hr-" + version);
        model.setKey("hr-" + version);

        model.setBpmnXml(new String(
                getClass().getClassLoader()
                        .getResourceAsStream("processes/it/"+version + ".xml")
                        .readAllBytes(), StandardCharsets.UTF_8));

        model.setDeployedToFlowable(false);
        return model;
    }

    private ProcessVersionModel createVersionModel(long id, VersionStatus status) {
        ProcessVersionModel model = new ProcessVersionModel();
        model.setId(id);
        model.setStatus(status);
        return model;
    }

    @Test
    @WithMockUser(roles = "PROCESS_OWNER")
    void testActivateVersion_Success() throws Exception {

    }

    @Test
    @WithMockUser(roles = "PROCESS_OWNER")
    void testActivateVersion_Conflict() throws Exception {
    }

    @Test
    void testGetVersions_StatusFilter() throws Exception {
    }

    @Test
    @WithMockUser(roles = "PROCESS_OWNER")
    void testConcurrentActivation() throws Exception {
    }
}
