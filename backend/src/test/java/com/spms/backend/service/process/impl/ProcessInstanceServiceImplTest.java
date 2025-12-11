package com.spms.backend.service.process.impl;

import com.spms.backend.repository.entities.process.ProcessDefinitionEntity;
import com.spms.backend.repository.entities.process.ProcessVersionEntity;
import com.spms.backend.repository.process.ProcessVersionRepository;
import com.spms.backend.service.exception.NotFoundException;
import com.spms.backend.service.exception.ValidationException;
import com.spms.backend.service.idm.UserService;
import com.spms.backend.service.model.process.BusinessKeyModel;
import com.spms.backend.service.model.process.ProcessInstanceModel;
import com.spms.backend.service.model.process.TaskModel;
import com.spms.backend.service.process.BusinessKeyGenerator;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.history.HistoricProcessInstanceQuery;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.engine.runtime.ProcessInstanceBuilder;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ProcessInstanceServiceImplTest {

    @Mock
    private UserService userService;

    @Mock
    private ProcessEngine flowableEngine;
    @Mock
    private BusinessKeyGenerator businessKeyGenerator;

    private ProcessInstanceServiceImpl processService;

    @Mock
    private ProcessVersionRepository processVersionRepository;

    private final String TEST_INSTANCE_ID = "instance123";
    private final String TEST_TASK_ID = "task456";
    private final Long TEST_USER_ID = 100L;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        BusinessKeyModel keyModel = new BusinessKeyModel();
        keyModel.setPrefix("pref");
        keyModel.setSequence(11L);
        keyModel.setSplit("-");
        when(businessKeyGenerator.generateBusinessKey(anyString(), anyString()))
                .thenReturn(keyModel);


        processService = new ProcessInstanceServiceImpl(userService,
                flowableEngine,
                businessKeyGenerator,
                processVersionRepository,
                null
        );
        when(userService.getCurrentUserId()).thenReturn(TEST_USER_ID);



    }

    @Test
    public void testStartInstance_Success() {
        RuntimeService runtimeService = mock(RuntimeService.class);
        when(flowableEngine.getRuntimeService()).thenReturn(runtimeService);
        
        ProcessInstanceBuilder builder = mock(ProcessInstanceBuilder.class);
        when(runtimeService.createProcessInstanceBuilder()).thenReturn(builder);
        when(builder.processDefinitionKey("testDef")).thenReturn(builder);
        when(builder.variables(any())).thenReturn(builder);
        when(builder.processDefinitionKey(anyString())).thenReturn(builder);
        when(builder.businessKey(anyString())).thenReturn(builder);
        
        ProcessInstance instance = mock(ProcessInstance.class);
        when(instance.getId()).thenReturn(TEST_INSTANCE_ID);
        when(builder.start()).thenReturn(instance);
        
        TaskService taskService = mock(TaskService.class);
        when(flowableEngine.getTaskService()).thenReturn(taskService);
        
        TaskQuery taskQuery = mock(TaskQuery.class);
        when(taskService.createTaskQuery()).thenReturn(taskQuery);
        when(taskQuery.processInstanceId(TEST_INSTANCE_ID)).thenReturn(taskQuery);
        
        Task task = mock(Task.class);
        when(task.getId()).thenReturn(TEST_TASK_ID);
        when(task.getName()).thenReturn("Test Task");
        when(task.getAssignee()).thenReturn("user123");
        when(taskQuery.list()).thenReturn(Collections.singletonList(task));

        ProcessVersionEntity processVersion = new ProcessVersionEntity();
        var processDef= new ProcessDefinitionEntity();
        processVersion.setProcessDefinition(processDef);
        processVersion.setKey("test_key");

        when(processVersionRepository.findByFlowableDefinitionId(anyString()))
                .thenReturn(Optional.of(processVersion));


        ProcessInstanceModel result = processService.startInstance(
            1L,
            null, 
            Map.of("key", "value"), 
            Map.of("initiator", TEST_USER_ID.toString())
        );

        assertNotNull(result);
        assertEquals(TEST_INSTANCE_ID, result.getInstanceId());
        assertEquals(1, result.getActiveTasks().size());
        assertEquals(TEST_TASK_ID, result.getActiveTasks().get(0).getTaskId());
    }

    @Test
    public void testStartInstance_ValidationFailure() {
        assertThrows(ValidationException.class, () -> {
            processService.startInstance(null, null, null, null);
        });
    }

    @Test
    public void testCompleteTask_Success() {
        TaskService taskService = mock(TaskService.class);
        when(flowableEngine.getTaskService()).thenReturn(taskService);
        Map<String, Object> completeData = Map.of("approve", true);

        processService.completeTask(TEST_INSTANCE_ID, TEST_TASK_ID, TEST_USER_ID, completeData);

        verify(taskService).complete(TEST_TASK_ID, completeData);
    }

    @Test
    public void testCompleteTask_ValidationFailure() {
        assertThrows(ValidationException.class, () -> {
            processService.completeTask(null, null, null, null);
        });
    }

    @Test
    public void testGetInstanceStatus_Active() {
        org.flowable.engine.HistoryService historyService = mock(org.flowable.engine.HistoryService.class);
        when(flowableEngine.getHistoryService()).thenReturn(historyService);
        
        HistoricProcessInstanceQuery query = mock(HistoricProcessInstanceQuery.class);
        when(historyService.createHistoricProcessInstanceQuery()).thenReturn(query);
        when(query.processInstanceId(TEST_INSTANCE_ID)).thenReturn(query);
        
        HistoricProcessInstance instance = mock(HistoricProcessInstance.class);
        when(query.singleResult()).thenReturn(instance);
        when(instance.getEndTime()).thenReturn(null);
        when(instance.getStartTime()).thenReturn(new java.util.Date());

        TaskService taskService = mock(TaskService.class);
        when(flowableEngine.getTaskService()).thenReturn(taskService);
        
        TaskQuery taskQuery = mock(TaskQuery.class);
        when(taskService.createTaskQuery()).thenReturn(taskQuery);
        when(taskQuery.processInstanceId(TEST_INSTANCE_ID)).thenReturn(taskQuery);
        
        Task task = mock(Task.class);
        when(task.getId()).thenReturn(TEST_TASK_ID);
        when(task.getName()).thenReturn("Test Task");
        when(task.getAssignee()).thenReturn("user123");
        when(taskQuery.list()).thenReturn(Collections.singletonList(task));

        ProcessInstanceModel result = processService.getInstanceStatus(TEST_INSTANCE_ID);

        assertNotNull(result);
        assertEquals("ACTIVE", result.getStatus());
        assertEquals(1, result.getActiveTasks().size());
        assertEquals(TEST_TASK_ID, result.getActiveTasks().get(0).getTaskId());
    }

    @Test
    public void testGetInstanceStatus_Completed() {
        org.flowable.engine.HistoryService historyService = mock(org.flowable.engine.HistoryService.class);
        when(flowableEngine.getHistoryService()).thenReturn(historyService);
        
        HistoricProcessInstanceQuery query = mock(HistoricProcessInstanceQuery.class);
        when(historyService.createHistoricProcessInstanceQuery()).thenReturn(query);
        when(query.processInstanceId(TEST_INSTANCE_ID)).thenReturn(query);
        
        HistoricProcessInstance instance = mock(HistoricProcessInstance.class);
        when(query.singleResult()).thenReturn(instance);
        when(instance.getEndTime()).thenReturn(new java.util.Date());
        when(instance.getStartTime()).thenReturn(new java.util.Date());

        TaskService taskService = mock(TaskService.class);
        when(flowableEngine.getTaskService()).thenReturn(taskService);
        
        TaskQuery taskQuery = mock(TaskQuery.class);
        when(taskService.createTaskQuery()).thenReturn(taskQuery);
        when(taskQuery.processInstanceId(TEST_INSTANCE_ID)).thenReturn(taskQuery);
        when(taskQuery.list()).thenReturn(Collections.emptyList());

        ProcessInstanceModel result = processService.getInstanceStatus(TEST_INSTANCE_ID);

        assertNotNull(result);
        assertEquals("COMPLETED", result.getStatus());
        assertTrue(result.getActiveTasks().isEmpty());
    }

    @Test
    public void testGetInstanceStatus_ValidationFailure() {
        assertThrows(ValidationException.class, () -> {
            processService.getInstanceStatus(null);
        });
    }

    @Test
    public void testGetInstanceTasks_Success() {
        TaskService taskService = mock(TaskService.class);
        when(flowableEngine.getTaskService()).thenReturn(taskService);
        
        TaskQuery taskQuery = mock(TaskQuery.class);
        when(taskService.createTaskQuery()).thenReturn(taskQuery);
        when(taskQuery.processInstanceId(TEST_INSTANCE_ID)).thenReturn(taskQuery);
        
        Task task = mock(Task.class);
        when(task.getId()).thenReturn(TEST_TASK_ID);
        when(task.getName()).thenReturn("Test Task");
        when(task.getAssignee()).thenReturn("user123");
        when(taskQuery.list()).thenReturn(Collections.singletonList(task));

        List<TaskModel> result = processService.getInstanceTasks(TEST_INSTANCE_ID);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(TEST_TASK_ID, result.get(0).getTaskId());
    }

    @Test
    public void testGetInstanceTasks_ValidationFailure() {
        assertThrows(ValidationException.class, () -> {
            processService.getInstanceTasks(null);
        });
    }
}
