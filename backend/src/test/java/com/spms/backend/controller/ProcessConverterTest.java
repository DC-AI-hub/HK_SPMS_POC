package com.spms.backend.controller;

import com.spms.backend.controller.dto.process.ProcessDefinitionVersionDTO;
import com.spms.backend.repository.entities.idm.User;
import com.spms.backend.service.model.process.ProcessVersionModel;
import com.spms.backend.service.idm.UserService;
import com.spms.backend.service.model.idm.UserModel;
import com.spms.backend.service.model.process.VersionStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class ProcessConverterTest {

    @Mock
    private UserService userService;


    private ProcessConverter processConverter;

    private UserModel userModel;

    public  ProcessConverterTest(){
        userModel = new UserModel();
        userModel.setUsername("testuser");
        userModel.setEmail("test@example.com");
        userModel.setCreatedAt(LocalDateTime.now());
        userModel.setUpdatedAt(LocalDateTime.now());
        userModel.setCreatedBy("admin");
        userModel.setModifiedBy("admin");

        Map<String, String> profiles = new HashMap<>();
        profiles.put("firstName", "Test");
        profiles.put("lastName", "User");
        profiles.put("avatarUrl", "http://example.com/avatar.jpg");
        userModel.setUserProfiles(profiles);
        userModel.setType(User.UserType.STAFF);
    }
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        processConverter = new ProcessConverter(userService);
        when(userService.getUserById(any())).thenReturn(userModel);

    }

    @Test
    void toDefinitionDTO_ShouldConvertModelToProcessDTO() {
        // Arrange
        ProcessVersionModel model = new ProcessVersionModel();
        model.setId(1L);
        model.setName("Test Process");
        model.setKey("test_process");
        model.setVersion("1.0");
        model.setStatus(VersionStatus.DRAFT);
        model.setBpmnXml("<bpmn>test</bpmn>");
        model.setCreatedAt(new Date().getTime());
        model.setDeployedToFlowable(true);

        // Act
        ProcessDefinitionVersionDTO dto = processConverter.toProcessDefinitionVersionDTO(model);

        // Assert
        assertEquals(model.getId(), dto.getId());
        assertEquals(model.getName(), dto.getName());
        assertEquals(model.getKey(), dto.getKey());
        assertEquals(model.getVersion(), dto.getVersion());
        assertEquals(model.getStatus().name(), dto.getStatus());
        assertEquals(model.getBpmnXml(), dto.getBpmnXml());
        assertEquals(model.getCreatedAt(), dto.getCreatedAt());
        assertEquals(model.getDeployedToFlowable(), dto.getDeployedToFlowable());
    }

}
