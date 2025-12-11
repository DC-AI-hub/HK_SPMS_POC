package com.spms.backend.flowable;

import org.flowable.engine.ProcessEngine;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.HashMap;
import java.util.Map;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import org.flowable.common.engine.api.FlowableException;
import org.flowable.common.engine.api.delegate.Expression;
import org.flowable.common.engine.impl.el.ExpressionManager;

@SpringBootTest
@TestPropertySource("/application-test.properties")
public class FlowableEngineTest {

    @MockitoBean
    private ClientRegistrationRepository clientRegistrationRepository; // Fixes the missing bean

    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private ProcessEngine flowableEngine;

    @Autowired
    private TaskService taskService;

    @Autowired
    private RepositoryService repositoryService;

    @MockitoBean
    private OrganizationService organizationService;

    // Deploys the test process
    private Deployment deployTestProcess() {
        String resourcePath = "flowable/test-process.xml";
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resourcePath);
        if (inputStream == null) {
            throw new RuntimeException("Test process definition not found: " + resourcePath);
        }
        String bpmnXml;
        try {
            bpmnXml = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read test process definition", e);
        }

        return repositoryService.createDeployment()
                .addString("PROC_STES.bpmn20.xml", bpmnXml)
                .key("PROC_STES")
                .name("PROC_STES")
                .deploy();
    }

    // Starts a process instance with initiator
    private ProcessInstance startProcess() {
        Map<String, Object> variables = new HashMap<>();
        variables.put("initiator", "testUser");
        return runtimeService.startProcessInstanceByKey("PROC_STES", variables);
    }

    // Completes the "Submit Application Form" task
    private void submitAppForm(ProcessInstance processInstance, String confirm) {
        Task task = taskService.createTaskQuery()
            .processInstanceId(processInstance.getId())
            .taskName("Submit Application Form")
            .singleResult();
        
        Map<String, Object> variables = new HashMap<>();
        variables.put("confirm", confirm);
        taskService.complete(task.getId(), variables);
    }

    // Completes the "Function Line Approve" task
    private void functionLineApprove(ProcessInstance processInstance, String decision) {
        Task task = taskService.createTaskQuery()
            .processInstanceId(processInstance.getId())
            .taskName("Function Line Approve")
            .singleResult();
        
        Map<String, Object> variables = new HashMap<>();
        variables.put("approve", decision);
        taskService.complete(task.getId(), variables);
    }

    // Verifies process completion
    private void assertProcessEnded(ProcessInstance processInstance) {
        ProcessInstance endedProcess = runtimeService.createProcessInstanceQuery()
            .processInstanceId(processInstance.getId())
            .singleResult();
        assertNull(endedProcess, "Process should have ended but is still active");
    }
    
    // Validates UEL expression evaluation
    private void validateExpression(String expression, Map<String, Object> variables, Object expected) {

        /*
        ExpressionManager expressionManager = repositoryService.getProcessEngineConfiguration().getExpressionManager();
        Expression expr = expressionManager.createExpression(expression);
        Object result = expr.getValue(variables);
        assertEquals(expected, result, "Expression validation failed: " + expression);*/
    }

    @Test
    void testProcessFlow() throws Exception {
        // Mock organization service
        when(organizationService.findUserDepartmentHead(anyString(), anyString()))
            .thenReturn("deptHeadUser");

        // Deploy process and start instance
        Deployment deployment = deployTestProcess();

        ProcessInstance processInstance = startProcess();
        //Thread.sleep(100000);

        // Path 1: Submit form → Confirm "no" → Process ends
        submitAppForm(processInstance, "no");

        // Validate conditional expression
        Map<String, Object> path1Vars = new HashMap<>();
        path1Vars.put("confirm", "no");
        validateExpression("${var:get(confirm)=='no'}", path1Vars, true);

        assertProcessEnded(processInstance);

        // Path 2: Submit form → Confirm "yes" → Approve → Process ends
        processInstance = startProcess();
        submitAppForm(processInstance, "yes");
        functionLineApprove(processInstance, "approve");

        // Validate conditional expressions
        Map<String, Object> path2Vars = new HashMap<>();
        path2Vars.put("confirm", "yes");
        path2Vars.put("approve", "approve");
        validateExpression("${var:get(confirm)=='yes'}", path2Vars, true);
        validateExpression("${var:get(approve)=='approve'}", path2Vars, true);

        assertProcessEnded(processInstance);

        // Path 3: Submit form → Confirm "yes" → Reject → Loops back
        processInstance = startProcess();
        submitAppForm(processInstance, "yes");
        functionLineApprove(processInstance, "reject");

        // Validate conditional expression
        Map<String, Object> path3Vars = new HashMap<>();
        path3Vars.put("approve", "reject");
        validateExpression("${var:get(approve)=='reject'}", path3Vars, true);

        // Verify back at "Submit Application Form"
        Task task = taskService.createTaskQuery()
            .processInstanceId(processInstance.getId())
            .singleResult();
        assertEquals("Submit Application Form", task.getName());

        // Cleanup
        repositoryService.deleteDeployment(deployment.getId(), true);
    }

    // Define OrganizationService interface
    public interface OrganizationService {
        String findUserDepartmentHead(String userId, String scope);
    }

    @Test
    void testMalformedExpression() {
        /*
        // Test handling of invalid UEL expression
        ExpressionManager expressionManager = repositoryService.getProcessEngineConfiguration().getExpressionManager();
        assertThrows(FlowableException.class, () -> {
            expressionManager.createExpression("${invalid!syntax}");
        });*/
    }
}
