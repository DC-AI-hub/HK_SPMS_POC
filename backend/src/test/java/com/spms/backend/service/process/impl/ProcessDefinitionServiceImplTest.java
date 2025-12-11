package com.spms.backend.service.process.impl;

import com.spms.backend.repository.entities.idm.User;
import com.spms.backend.repository.entities.process.ProcessDefinitionEntity;
import com.spms.backend.repository.entities.process.ProcessVersionStatus;
import com.spms.backend.repository.process.FormVersionRepository;
import com.spms.backend.repository.process.ProcessDefinitionRepository;
import com.spms.backend.service.SpmsService;
import com.spms.backend.service.exception.NotFoundException;
import com.spms.backend.service.exception.ValidationException;
import com.spms.backend.repository.entities.process.ProcessVersionEntity;
import com.spms.backend.repository.process.ProcessVersionRepository;
import com.spms.backend.service.idm.IdmService;
import com.spms.backend.service.process.ProcessDeploymentService;
import com.spms.backend.service.idm.UserModelFulfilledSupporter;
import com.spms.backend.service.idm.UserService;
import com.spms.backend.service.idm.impl.UserModelFulfilledSupporterImpl;
import com.spms.backend.service.model.idm.UserModel;
import com.spms.backend.service.model.process.ProcessDefinitionModel;
import com.spms.backend.service.model.process.ProcessVersionModel;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.DeploymentBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ProcessDefinitionServiceImplTest {

    @Mock
    private ProcessVersionRepository processVersionRepository;
    
    @Mock
    private ProcessDeploymentService processDeploymentService;
    
    @Mock
    private UserModelFulfilledSupporter userModelFulfilledSupporter;
    
    @Mock
    private SpmsService spmsService;
    
    @Mock
    private IdmService idmService;
    
    @Mock
    private UserService userService;
    
    @Mock
    private ProcessDefinitionRepository processDefinitionRepository;

    private ProcessDefinitionServiceImpl processService;

    @Mock
    FormVersionRepository formVersionRepository;

    private final String TEST_DEFINITION_ID = "testDef123";
    private final Long TEST_VERSION_ID = 1L;
    private final String TEST_VERSION = "1.0.0";
    private final Long TEST_USER_ID = 100L;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        when(spmsService.getIdmService()).thenReturn(idmService);
        when(idmService.getUserService()).thenReturn(userService);

        processService = new ProcessDefinitionServiceImpl(
            userService,
            processVersionRepository,
            processDefinitionRepository,
                formVersionRepository
        );
        
        userModelFulfilledSupporter = new UserModelFulfilledSupporterImpl(userService);
        UserModel userModel = new UserModel();
        userModel.setId(1L);
        userModel.setType(User.UserType.STAFF);
        when(userService.getUserById(any())).thenReturn(userModel);
        when(spmsService.getIdmService().getUserService().getCurrentUserId()).thenReturn(TEST_USER_ID);
        when(userService.getFulfilledSupporter()).thenReturn(userModelFulfilledSupporter);
    }

    private ProcessVersionEntity createTestProcessVersionEntity() {
        ProcessVersionEntity entity = new ProcessVersionEntity();
        entity.setId(TEST_VERSION_ID);
        entity.setName("Test Process");
        entity.setKey("testKey");
        entity.setVersion("1.0");
        entity.setBpmnXml("<bpmn>test</bpmn>");
        entity.setStatus(ProcessVersionStatus.DRAFT);
        entity.setDeployedToFlowable(true);
        entity.setFlowableDefinitionId(TEST_DEFINITION_ID);
        entity.setCreatedAt(new Date().getTime());
        entity.setUpdatedAt(new Date().getTime());
        entity.setCreatedById(TEST_USER_ID);
        entity.setUpdatedById(TEST_USER_ID);
        return entity;
    }

    private ProcessDefinitionEntity createTestProcessDefinitionEntity() {
        ProcessDefinitionEntity entity = new ProcessDefinitionEntity();
        entity.setId(TEST_VERSION_ID);
        entity.setName("Test Process");
        entity.setKey("testKey");
        entity.setCreatedAt(new Date().getTime());
        entity.setUpdatedAt(new Date().getTime());
        entity.setCreatedById(TEST_USER_ID);
        entity.setUpdatedById(TEST_USER_ID);
        return entity;
    }

    @Test
    public void testGetDefinitionVersions_NullDefinitionId() {
        assertThrows(ValidationException.class, () -> {
            processService.getDefinitionVersions(null, PageRequest.of(0, 10));
        });
    }

    @Test
    public void testGetProcessDefinitions_Success() {
        ProcessDefinitionEntity entity = createTestProcessDefinitionEntity();
        Pageable pageable = PageRequest.of(0, 10);
        Page<ProcessDefinitionEntity> page = new PageImpl<>(Collections.singletonList(entity), pageable, 1);
        when(processDefinitionRepository.findDefinitions("test", pageable)).thenReturn(page);

        Page<ProcessDefinitionModel> result = processService.getProcessDefinitions("test", pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(processDefinitionRepository).findDefinitions("test", pageable);
    }

    @Test
    public void testGetProcessDefinitions_EmptyResults() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<ProcessDefinitionEntity> page = new PageImpl<>(Collections.emptyList(), pageable, 0);
        when(processDefinitionRepository.findDefinitions("nonexistent", pageable)).thenReturn(page);

        Page<ProcessDefinitionModel> result = processService.getProcessDefinitions("nonexistent", pageable);

        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
    }

    @Test
    public void testGetProcessDefinitions_NullPageable() {
        assertThrows(ValidationException.class, () -> {
            processService.getProcessDefinitions("test", null);
        });
    }

    @Test
    public void testGetProcessDefinitions_NullSearch() {
        ProcessDefinitionEntity entity = createTestProcessDefinitionEntity();
        Pageable pageable = PageRequest.of(0, 10);
        Page<ProcessDefinitionEntity> page = new PageImpl<>(Collections.singletonList(entity), pageable, 1);
        when(processDefinitionRepository.findDefinitions(null, pageable)).thenReturn(page);

        Page<ProcessDefinitionModel> result = processService.getProcessDefinitions(null, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    public void testGetProcessDefinition_Success() {
        ProcessDefinitionEntity entity = createTestProcessDefinitionEntity();
        when(processDefinitionRepository.findById(TEST_VERSION_ID)).thenReturn(Optional.of(entity));

        ProcessDefinitionModel result = processService.getProcessDefinition(TEST_VERSION_ID.toString());

        assertNotNull(result);
        assertEquals(TEST_VERSION_ID, result.getId());
        verify(processDefinitionRepository).findById(TEST_VERSION_ID);
    }

    @Test
    public void testGetProcessDefinition_InvalidIdFormat() {
        assertThrows(ValidationException.class, () -> {
            processService.getProcessDefinition("invalid");
        });
    }

    @Test
    public void testGetProcessDefinitionVersion_Success() {
        ProcessVersionEntity entity = createTestProcessVersionEntity();
        when(processVersionRepository.findByProcessDefinitionIdAndVersion(1L, "1.0.0"))
            .thenReturn(Optional.of(entity));

        Optional<ProcessVersionModel> result = processService.getProcessDefinitionVersion("1", "1.0.0");

        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals(TEST_VERSION_ID, result.get().getId());
    }

    @Test
    public void testGetProcessDefinitionVersion_NotFound() {
        when(processVersionRepository.findByProcessDefinitionIdAndVersion(1L, "1.0.0"))
                .thenReturn(Optional.empty());
        Assertions.assertFalse(processService.getProcessDefinitionVersion("1", "1.0.0").isPresent());
    }

    // Deployment tests removed - now handled by ProcessDeploymentServiceImpl

    @Test
    public void testCreateProcessDefinition_Success() {
        ProcessDefinitionModel model = new ProcessDefinitionModel();
        model.setName("Test Process");
        model.setKey("testKey");
        model.setDescription("description");

        ProcessDefinitionEntity savedEntity = createTestProcessDefinitionEntity();
        when(processDefinitionRepository.save(any())).thenReturn(savedEntity);

        ProcessDefinitionModel result = processService.createProcessDefinition(model);

        assertNotNull(result);
        assertEquals(TEST_VERSION_ID, result.getId());
        verify(processDefinitionRepository).save(any());
    }
}
