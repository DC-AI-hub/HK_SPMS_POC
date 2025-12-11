package com.spms.backend.service.process.impl;

import com.spms.backend.repository.entities.process.ProcessDefinitionEntity;
import com.spms.backend.repository.entities.process.ProcessVersionEntity;
import com.spms.backend.repository.entities.process.ProcessVersionStatus;
import com.spms.backend.repository.process.ProcessDefinitionRepository;
import com.spms.backend.repository.process.ProcessVersionRepository;
import com.spms.backend.service.exception.NotFoundException;
import com.spms.backend.service.exception.SpmsRuntimeException;
import com.spms.backend.service.exception.ValidationException;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.DeploymentBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProcessDeploymentServiceImplTest {

    @Mock
    private ProcessDefinitionRepository processDefinitionRepository;

    @Mock
    private ProcessVersionRepository processVersionRepository;

    @Mock
    private ProcessEngine flowableEngine;

    @InjectMocks
    private ProcessDeploymentServiceImpl processDeploymentService;

    private final Long TEST_DEFINITION_ID = 1L;
    private final String TEST_VERSION = "1.0.0";
    private final Long TEST_OWNER_ID = 100L;
    private final String TEST_DEPLOYMENT_ID = "deployment123";

    private ProcessDefinitionEntity definitionEntity;
    private ProcessVersionEntity versionEntity;

    @BeforeEach
    void setUp() {
        definitionEntity = new ProcessDefinitionEntity();
        definitionEntity.setId(TEST_DEFINITION_ID);
        definitionEntity.setName("Test Process");
        definitionEntity.setKey("testKey");

        versionEntity = new ProcessVersionEntity();
        versionEntity.setId(1L);
        versionEntity.setName("Test Version");
        versionEntity.setVersion(TEST_VERSION);
        versionEntity.setBpmnXml("<bpmn>test</bpmn>");
        versionEntity.setStatus(ProcessVersionStatus.DRAFT);
        versionEntity.setProcessDefinition(definitionEntity);
        
        // Set flowableDefinitionId on the version entity
        versionEntity.setFlowableDefinitionId("test-flowable-id");

        lenient().when(processVersionRepository.findByProcessDefinitionId(anyLong(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.emptyList()));
    }

    @Test
    void deployProcessDefinition_Success() {
        // Mock dependencies
        when(processDefinitionRepository.findById(TEST_DEFINITION_ID)).thenReturn(Optional.of(definitionEntity));
        when(processVersionRepository.findByProcessDefinitionIdAndVersion(TEST_DEFINITION_ID, TEST_VERSION))
            .thenReturn(Optional.of(versionEntity));

        // Mock Flowable services
        RepositoryService repositoryService = mock(RepositoryService.class);
        when(flowableEngine.getRepositoryService()).thenReturn(repositoryService);
        
        DeploymentBuilder deploymentBuilder = mock(DeploymentBuilder.class);
        when(repositoryService.createDeployment()).thenReturn(deploymentBuilder);
        when(deploymentBuilder.addString(anyString(), anyString())).thenReturn(deploymentBuilder);
        when(deploymentBuilder.key(anyString())).thenReturn(deploymentBuilder);
        when(deploymentBuilder.name(anyString())).thenReturn(deploymentBuilder);
        
        // Mock the flowableDefinitionId getter
        versionEntity.setFlowableDefinitionId("test-flowable-id");
        //lenient().when(versionEntity.getFlowableDefinitionId()).thenReturn("test-flowable-id");
        
        Deployment deployment = mock(Deployment.class);
        when(deployment.getId()).thenReturn(TEST_DEPLOYMENT_ID);
        when(deploymentBuilder.deploy()).thenReturn(deployment);

        // Execute
        processDeploymentService.deployProcessDefinition(TEST_DEFINITION_ID, 1L, TEST_OWNER_ID);

        // Verify
        verify(processVersionRepository).save(versionEntity);
        assertEquals(ProcessVersionStatus.DEPLOYED, versionEntity.getStatus());
        assertEquals(TEST_DEPLOYMENT_ID, versionEntity.getFlowableDeploymentId());
        assertTrue(versionEntity.getDeployedToFlowable());
    }

    @Test
    void deployProcessDefinition_VersionAlreadyDeployed() {
        // Setup
        versionEntity.setStatus(ProcessVersionStatus.DEPLOYED);
        
        // Mock dependencies
        when(processDefinitionRepository.findById(TEST_DEFINITION_ID)).thenReturn(Optional.of(definitionEntity));
        when(processVersionRepository.findByProcessDefinitionIdAndVersion(TEST_DEFINITION_ID, TEST_VERSION))
            .thenReturn(Optional.of(versionEntity));

        // Execute and verify
        SpmsRuntimeException exception = assertThrows(SpmsRuntimeException.class, () -> {
            processDeploymentService.deployProcessDefinition(TEST_DEFINITION_ID, 1L, TEST_OWNER_ID);
        });
        assertEquals("Version " + TEST_VERSION + " is already deployed", exception.getMessage());
    }

    @Test
    void deployProcessDefinition_VersionDeprecated() {
        // Setup
        versionEntity.setStatus(ProcessVersionStatus.DEPRECATED);
        
        // Mock dependencies
        when(processDefinitionRepository.findById(TEST_DEFINITION_ID)).thenReturn(Optional.of(definitionEntity));
        when(processVersionRepository.findByProcessDefinitionIdAndVersion(anyLong(), anyString()))
            .thenReturn(Optional.of(versionEntity));

        // Execute and verify
        SpmsRuntimeException exception = assertThrows(SpmsRuntimeException.class, () -> {
            processDeploymentService.deployProcessDefinition(TEST_DEFINITION_ID, 1L, TEST_OWNER_ID);
        });
        assertEquals("Version " + TEST_VERSION + " is deprecated and cannot be deployed", exception.getMessage());
    }

    @Test
    void undeployProcessDefinition_Success() {
        // Setup
        versionEntity.setFlowableDeploymentId(TEST_DEPLOYMENT_ID);
        versionEntity.setDeployedToFlowable(true);
        
        // Mock dependencies
        when(processVersionRepository.findByProcessDefinitionIdAndVersion(TEST_DEFINITION_ID, TEST_VERSION))
            .thenReturn(Optional.of(versionEntity));
        
        // Mock Flowable services
        RepositoryService repositoryService = mock(RepositoryService.class);
        when(flowableEngine.getRepositoryService()).thenReturn(repositoryService);

        // Execute
        processDeploymentService.undeployProcessDefinition(TEST_DEFINITION_ID, 1L, TEST_OWNER_ID);

        // Verify
        verify(repositoryService).deleteDeployment(TEST_DEPLOYMENT_ID, true);
        verify(processVersionRepository).save(versionEntity);
        assertNull(versionEntity.getFlowableDeploymentId());
        assertFalse(versionEntity.getDeployedToFlowable());
    }

    @Test
    void undeployProcessDefinition_VersionNotFound() {
        // Mock dependencies
        when(processVersionRepository.findByProcessDefinitionIdAndVersion(TEST_DEFINITION_ID, TEST_VERSION))
            .thenReturn(Optional.empty());

        // Execute and verify
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            processDeploymentService.undeployProcessDefinition(TEST_DEFINITION_ID, 1L, TEST_OWNER_ID);
        });
        assertEquals("Process version not found", exception.getMessage());
    }
}
