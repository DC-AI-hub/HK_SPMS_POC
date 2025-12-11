package com.spms.backend.service.process.impl;

import com.spms.backend.repository.entities.process.ProcessDefinitionEntity;
import com.spms.backend.repository.entities.process.ProcessVersionEntity;
import com.spms.backend.repository.entities.process.ProcessVersionStatus;
import com.spms.backend.repository.process.ProcessDefinitionRepository;
import com.spms.backend.repository.process.ProcessVersionRepository;
import com.spms.backend.service.exception.NotFoundException;
import com.spms.backend.service.exception.SpmsRuntimeException;
import com.spms.backend.service.exception.ValidationException;
import com.spms.backend.service.idm.UserService;
import com.spms.backend.service.model.idm.UserModel;
import com.spms.backend.service.model.process.ProcessInstanceModel;
import com.spms.backend.service.model.process.TaskModel;
import com.spms.backend.service.process.BusinessKeyGenerator;
import com.spms.backend.service.process.ProcessInstanceService;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.runtime.ActivityInstance;
import org.flowable.engine.runtime.ActivityInstanceQuery;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.engine.runtime.ProcessInstanceQuery;
import org.flowable.task.api.Task;
import org.flowable.common.engine.api.FlowableObjectNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.spms.backend.service.mapper.ProcessActivityMapper;
import com.spms.backend.service.mapper.ProcessInstanceMapper;
import com.spms.backend.service.model.process.ProcessActivityModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 流程实例服务实现类，提供流程实例的启动、任务完成、状态查询等功能。
 *
 * <p>该类实现了 {@link ProcessInstanceService} 接口，负责与流程引擎交互，管理流程实例的生命周期。</p>
 *
 * <p>主要功能包括：</p>
 * <ul>
 *   <li>启动新的流程实例</li>
 *   <li>完成任务</li>
 *   <li>查询流程实例状态</li>
 *   <li>获取流程实例的任务列表</li>
 * </ul>
 *
 * @see ProcessInstanceService
 * @see ProcessEngine
 * @see RuntimeService
 */
@Service
public class ProcessInstanceServiceImpl implements ProcessInstanceService {

    private final UserService userService;
    private final ProcessEngine flowableEngine;
    private final BusinessKeyGenerator businessKeyGenerator;
    private final ProcessVersionRepository processVersionRepository;
    private final ProcessDefinitionRepository processDefinitionRepository;

    private static final Logger log = LoggerFactory.getLogger(ProcessInstanceServiceImpl.class);

    public ProcessInstanceServiceImpl(
            UserService userService,
            ProcessEngine flowableEngine,
            BusinessKeyGenerator businessKeyGenerator,
            ProcessVersionRepository processVersionRepository,
            ProcessDefinitionRepository processDefinitionRepository
    ) {
        this.userService = userService;
        this.flowableEngine = flowableEngine;
        this.businessKeyGenerator = businessKeyGenerator;
        this.processVersionRepository = processVersionRepository;
        this.processDefinitionRepository = processDefinitionRepository;
    }

    /**
     * Starts a new process instance based on the given definition ID.
     * 
     * <p>Validates input parameters, retrieves the latest deployed version of the process definition,
     * generates a business key, and initializes the process instance with context variables.</p>
     *
     * @param definitionId ID of the process definition to start
     * @param formId ID of the associated form (optional)
     * @param formContext form context data (optional)
     * @param context process context data (optional)
     * @return ProcessInstanceModel containing details of the started instance
     * @throws ValidationException if definitionId is null or current user ID is null
     * @throws NotFoundException if no deployed version exists for the definition
     * @throws SpmsRuntimeException if process instance creation fails
     */
    @Override
    @Transactional
    public ProcessInstanceModel startInstance(Long definitionId, Long formId, Map<String, String> formContext, Map<String, String> context) {
        log.info("Starting process instance with definitionId: {}", definitionId);
        if (definitionId == null) {
            log.warn("Validation failed: Definition ID is null or empty");
            throw new ValidationException("Definition ID cannot be null or empty");
        }
        UserModel user = userService.getCurrentUser();
        if (user == null) {
            log.warn("Validation failed: User ID is null for definitionId: {}", definitionId);
            throw new ValidationException("User ID cannot be null");
        }


        // Get the latest deployed version
        RuntimeService runtimeService = flowableEngine.getRuntimeService();
        try {

            ProcessDefinitionEntity procDef = processDefinitionRepository.getReferenceById(definitionId);
            ProcessVersionEntity processVersion = procDef.getVersions().stream().filter(x->x.getStatus() == ProcessVersionStatus.DEPLOYED)
                    .findFirst().orElseThrow(
                            ()->{
                                log.error("Process version not found for definitionId: {}", definitionId);
                                return new SpmsRuntimeException("Process version not found for definition ID: " + definitionId, null);
                            }
                    );
            // Find the process definition by process version
            ProcessDefinitionEntity processDefinition = processVersion.getProcessDefinition();
            if (processDefinition == null) {
                log.error("Process definition not found for versionId: {}", processVersion.getId());
                throw new SpmsRuntimeException("Process definition not found for version ID: " + processVersion.getId(), null);
            }

            // Generate the businessKey using the process version's key as prefix
            String businessKey = businessKeyGenerator.generateBusinessKey(processVersion.getKey(), "")
                    .getSeqStr('0', 10);  // Use '0' placeholder and 10-digit sequence

            Map<String,Object> variables = new HashMap<>();
            variables.put("initiator", user.getUsername());
            if (formContext != null && !formContext.isEmpty()) {
                variables.put("formData", new HashMap<>(formContext));
            }
            if (context != null && !context.isEmpty()) {
                variables.putAll(context);
            }

            ProcessInstance instance = runtimeService.createProcessInstanceBuilder()
                    .processDefinitionKey(processVersion.getKey())
                    .businessKey(businessKey)
                    .owner(processDefinition.getOwnerId().toString())
                    //.tenantId(processDefinition.getBusinessOwnerId().toString())
                    .variables(variables)
                    .transientVariables(Map.of("defId",processDefinition.getId()))
                    .start();
            log.info("Process instance started successfully: instanceId={}, definitionId={}",
                    instance.getId(), definitionId);

            return ProcessInstanceModel.builder()
                    .instanceId(instance.getId())
                    .definitionId(instance.getProcessDefinitionId())
                    .status("ACTIVE")
                    .startTime(System.currentTimeMillis())
                    .activeTasks(getInstanceTasks(instance.getId()))
                    .build();
        } catch (Exception ex) {
            log.error("Failed to start process instance", ex);
            throw new SpmsRuntimeException("Failed to start process instance", ex);
        }
    }

    /**
     * Rejects a task in a process instance with specified rejection values.
     * 
     * <p>Validates input parameters, verifies task assignment, sets rejection variables,
     * and triggers the rejection event in the BPMN engine.</p>
     *
     * @param instanceId ID of the process instance containing the task
     * @param taskId ID of the task to reject
     * @param userId ID of the user rejecting the task
     * @param rejectValues map containing rejection data including mandatory 'rejectionReason'
     * @throws ValidationException if parameters are invalid or rejectionReason is missing
     * @throws NotFoundException if task is not found or not assigned to user
     * @throws SpmsRuntimeException if task rejection operation fails
     */
    @Override
    public void rejectTask(String instanceId, String taskId, Long userId, Map<String, Object> rejectValues) {
        log.info("Rejecting task: instanceId={}, taskId={}, userId={}", instanceId, taskId, userId);
        
        // Validate input parameters
        if (instanceId == null || instanceId.isEmpty()) {
            throw new ValidationException("Instance ID cannot be null or empty");
        }
        if (taskId == null || taskId.isEmpty()) {
            throw new ValidationException("Task ID cannot be null or empty");
        }
        if (userId == null) {
            throw new ValidationException("User ID cannot be null");
        }
        if (rejectValues == null || !rejectValues.containsKey("rejectionReason")) {
            throw new ValidationException("rejectionReason is required in rejectValues");
        }

        try {
            TaskService taskService = flowableEngine.getTaskService();
            RuntimeService runtimeService = flowableEngine.getRuntimeService();

            // Verify task exists and is assigned to user
            Task task = taskService.createTaskQuery()
                .taskId(taskId)
                .singleResult();
            
            if (task == null) {
                throw new NotFoundException("Task not found or not assigned to user");
            }

            // Set rejection variables
            Map<String,Object> safe = ensureFormDataNotOverwritten(instanceId, rejectValues);
            taskService.setVariables(taskId, safe);
            runtimeService.setVariables(instanceId, safe);
            // Trigger BPMN event
            //runtimeService.signalEventReceived("taskRejected", task.getExecutionId(), rejectValues);
            flowableEngine.getTaskService().complete(taskId,rejectValues);

            log.info("Task rejected successfully: taskId={}", taskId);
        } catch (FlowableObjectNotFoundException e) {
            throw new SpmsRuntimeException("Task or process instance not found", e);
        } catch (Exception e) {
            throw new SpmsRuntimeException("Failed to reject task", e);
        }
    }

    /**
     * Completes a task in a process instance with the provided completion values.
     * 
     * <p>Validates input parameters and delegates task completion to the Flowable engine.</p>
     *
     * @param instanceId ID of the process instance containing the task
     * @param taskId ID of the task to complete
     * @param userId ID of the user completing the task
     * @param completedValues map containing completion data
     * @throws ValidationException if parameters are invalid
     * @throws SpmsRuntimeException if task completion operation fails
     */
    @Override
    @Transactional
    public void completeTask(String instanceId, String taskId, Long userId, Map<String, Object> completedValues) {
        log.info("Completing task: instanceId={}, taskId={}, userId={}", instanceId, taskId, userId);
        log.info("FormData keys in completed values: {}", 
            completedValues != null && completedValues.containsKey("formData") 
                ? (completedValues.get("formData") instanceof Map<?,?> m ? m.keySet() : "not a map") 
                : "no formData key");
        
        if (instanceId == null || instanceId.isEmpty()) {
            log.warn("Validation failed: Instance ID is null or empty");
            throw new ValidationException("Instance ID cannot be null or empty");
        }
        if (taskId == null || taskId.isEmpty()) {
            throw new ValidationException("Task ID cannot be null or empty");
        }
        if (userId == null) {
            throw new ValidationException("User ID cannot be null");
        }

        try {
            // Avoid accidentally clearing formData if client submits empty {}
            Map<String,Object> safe = ensureFormDataNotOverwritten(instanceId, completedValues);
            flowableEngine.getRuntimeService().setVariables(instanceId, safe);
            flowableEngine.getTaskService().complete(taskId, safe);
            log.info("Task completed successfully: taskId={}", taskId);
        } catch (Exception e) {
            throw new SpmsRuntimeException("Failed to complete task", e);
        }
    }

    /**
     * Saves draft data to a process instance without completing the task.
     * Only updates process instance variables, does not advance the workflow.
     */
    @Override
    @Transactional
    public void saveDraft(String instanceId, String taskId, Long userId, Map<String, Object> draftValues) {
        log.info("Saving draft: instanceId={}, taskId={}, userId={}", instanceId, taskId, userId);
        
        if (instanceId == null || instanceId.isEmpty()) {
            throw new ValidationException("Instance ID cannot be null or empty");
        }
        if (taskId == null || taskId.isEmpty()) {
            throw new ValidationException("Task ID cannot be null or empty");
        }
        if (userId == null) {
            throw new ValidationException("User ID cannot be null");
        }

        try {
            TaskService taskService = flowableEngine.getTaskService();
            RuntimeService runtimeService = flowableEngine.getRuntimeService();

            // Verify task exists and is assigned to user
            Task task = taskService.createTaskQuery()
                .taskId(taskId)
                .singleResult();
            
            if (task == null) {
                throw new NotFoundException("Task not found or not assigned to user");
            }

            // Avoid accidentally clearing formData if client submits empty {}
            Map<String,Object> safe = ensureFormDataNotOverwritten(instanceId, draftValues);
            // Only update variables, do NOT complete the task
            taskService.setVariables(taskId, safe);
            runtimeService.setVariables(instanceId, safe);
            
            log.info("Draft saved successfully: taskId={}", taskId);
        } catch (FlowableObjectNotFoundException e) {
            throw new SpmsRuntimeException("Task or process instance not found", e);
        } catch (Exception e) {
            throw new SpmsRuntimeException("Failed to save draft", e);
        }
    }

    /**
     * Ensure that incoming variables will not erase existing 'formData' when it is empty or null.
     */
    private Map<String, Object> ensureFormDataNotOverwritten(String instanceId, Map<String, Object> incoming) {
        if (incoming == null) return null;
        if (!incoming.containsKey("formData")) return incoming;
        
        Object newFormData = incoming.get("formData");
        boolean empty = false;
        if (newFormData == null) {
            empty = true;
            log.info("FormData is null");
        } else if (newFormData instanceof Map<?,?> m) {
            empty = m.isEmpty();
            log.info("FormData is Map with {} keys", m.size());
        } else if (newFormData instanceof String s) {
            empty = s.isBlank();
            log.info("FormData is String with length {}", s.length());
        } else {
            log.info("FormData is of type: {}", newFormData.getClass().getSimpleName());
        }
        
        if (!empty) {
            log.info("FormData is not empty, using incoming data");
            return incoming;
        }
        
        log.warn("No existing formData found, removing invalid formData");
        try {
            Map<String, Object> current = flowableEngine.getRuntimeService().getVariables(instanceId);
            Object existing = current != null ? current.get("formData") : null;
            if (existing != null) {
                log.info("Found existing formData, preserving it");
                incoming.put("formData", existing);
            } else {
                log.warn("No existing formData found in process instance");
                incoming.remove("formData");
            }
        } catch (Exception e) {
            log.error("Error retrieving existing formData: {}", e.getMessage());
            incoming.remove("formData");
        }
        return incoming;
    }

    /**
     * Retrieves a paginated list of all process instances.
     * 
     * @param pageable pagination configuration (page number, size, sorting)
     * @return list of ProcessInstanceModel objects representing process instances
     * @throws SpmsRuntimeException if an error occurs during retrieval
     */
    @Override
    public List<ProcessInstanceModel> getInstances(Pageable pageable) {
        log.debug("Fetching all process instances with pagination: {}", pageable);
        try {
            ProcessInstanceQuery query = flowableEngine.getRuntimeService()
                    .createProcessInstanceQuery()
                    .orderByStartTime()
                    .desc();

            List<ProcessInstance> instances = query.listPage(
                    (int) pageable.getOffset(),
                    pageable.getPageSize()
            );

            return instances.stream()
                    .map(x-> toProcessInstanceModel(x,new HashMap<>()))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error fetching process instances", e);
            throw new SpmsRuntimeException("Failed to retrieve process instances", e);
        }
    }

    /**
     * Retrieves paginated list of process instances related to a user.
     * 
     * <p>Includes instances started by the user and instances where the user has assigned tasks.</p>
     *
     * @param pageable pagination configuration
     * @param user user model object
     * @return list of ProcessInstanceModel objects related to the user
     * @throws SpmsRuntimeException if an error occurs during retrieval
     */
    @Override
    public List<ProcessInstanceModel> getUserRelatedInstances(Pageable pageable, UserModel user) {
        log.debug("Fetching user-related process instances for user: {}", user.getId());
        try {
            String userId = user.getId().toString();
            RuntimeService runtimeService = flowableEngine.getRuntimeService();
            TaskService taskService = flowableEngine.getTaskService();

            // 查询用户启动的流程实例
            List<ProcessInstance> startedProcesses = runtimeService
                    .createProcessInstanceQuery()
                    .variableValueEquals("initiator", userId)
                    .list();

            // 查询用户被分配任务的流程实例
            List<Task> userTasks = taskService.createTaskQuery()
                    .taskAssignee(userId)
                    .list();

            Set<String> processInstanceIds = userTasks.stream()
                    .map(Task::getProcessInstanceId)
                    .collect(Collectors.toSet());

            List<ProcessInstance> taskProcesses = runtimeService
                    .createProcessInstanceQuery()
                    .processInstanceIds(processInstanceIds)
                    .list();

            // 合并并去重
            Set<ProcessInstance> combined = new LinkedHashSet<>();
            combined.addAll(startedProcesses);
            combined.addAll(taskProcesses);

            // 应用分页
            List<ProcessInstance> paginated = combined.stream()
                    .skip(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .toList();

            return paginated.stream()
                    .map(x->toProcessInstanceModel(x,new HashMap<>()))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error fetching user-related process instances for user: {}", user.getId(), e);
            throw new SpmsRuntimeException("Failed to retrieve user-related process instances", e);
        }
    }

    /**
     * Counts active (incomplete) tasks across all process instances.
     * 
     * @return count of incomplete tasks
     * @throws SpmsRuntimeException if an error occurs during counting
     */
    @Override
    public long countIncompleteTasks() {
        try {
            return flowableEngine.getTaskService().createTaskQuery().active().count();
        } catch (Exception e) {
            log.error("Error counting incomplete tasks", e);
            throw new SpmsRuntimeException("Failed to count incomplete tasks", e);
        }
    }

    /**
     * Counts completed tasks across all process instances.
     * 
     * @return count of completed tasks
     * @throws SpmsRuntimeException if an error occurs during counting
     */
    @Override
    public long countCompletedTasks() {
        try {
            return flowableEngine.getHistoryService().createHistoricTaskInstanceQuery().finished().count();
        } catch (Exception e) {
            log.error("Error counting completed tasks", e);
            throw new SpmsRuntimeException("Failed to count completed tasks", e);
        }
    }

    /**
     * Counts currently active (running) process instances.
     * 
     * @return count of active process instances
     * @throws SpmsRuntimeException if an error occurs during counting
     */
    @Override
    public long countRunningProcesses() {
        try {
            return flowableEngine.getRuntimeService().createProcessInstanceQuery().active().count();
        } catch (Exception e) {
            log.error("Error counting running processes", e);
            throw new SpmsRuntimeException("Failed to count running processes", e);
        }
    }

    /**
     * Retrieves status information for a specific process instance.
     * 
     * @param instanceId unique identifier of the process instance
     * @return ProcessInstanceModel containing status details
     * @throws ValidationException if instanceId is null or empty
     * @throws NotFoundException if no process instance matches the ID
     */
    @Override
    @Transactional
    public ProcessInstanceModel getInstanceStatus(String instanceId) {
        log.debug("Getting instance status: instanceId={}", instanceId);
        if (instanceId == null || instanceId.isEmpty()) {
            log.warn("Validation failed: Instance ID is null or empty");
            throw new ValidationException("Instance ID cannot be null or empty");
        }

        ProcessInstance processInstance = flowableEngine.getRuntimeService()
                .createProcessInstanceQuery()
                .processInstanceId(instanceId)
                .singleResult();

        if (processInstance == null) {
            log.error("Process instance not found: instanceId={}", instanceId);
            throw new NotFoundException("Process instance not found");
        }
        var result = flowableEngine.getRuntimeService().getVariables(instanceId);

        var model=  toProcessInstanceModel(processInstance,result);
        return model;
    }

    /**
     * Converts a ProcessInstance object to a ProcessInstanceModel object.
     * 
     * @param instance ProcessInstance to convert
     * @return converted ProcessInstanceModel
     */
    //TODO: migrate to a standalone class
    private ProcessInstanceModel toProcessInstanceModel(ProcessInstance instance, Map<String,Object> context) {
        String instanceId = instance.getId();
        return ProcessInstanceModel.builder()
                .instanceId(instanceId)
                .definitionId(instance.getProcessDefinitionId())
                .startTime(instance.getStartTime().getTime())
                .activeTasks(getInstanceTasks(instanceId))
                .setBusinessKey(instance.getBusinessKey())
                .setDeploymentId(instance.getDeploymentId())
                .status(instance.getBusinessStatus())
                .setContextValue(context)
                .build();
    }

    /**
     * Retrieves all tasks for a specific process instance.
     * 
     * @param instanceId ID of the process instance (cannot be null or empty)
     * @return list of TaskModel objects containing task information
     * @throws ValidationException if instanceId is null or empty
     */
    @Override
    public List<TaskModel> getInstanceTasks(String instanceId) {
        log.debug("Getting tasks for instance: instanceId={}", instanceId);
        if (instanceId == null || instanceId.isEmpty()) {
            log.warn("Validation failed: Instance ID is null or empty");
            throw new ValidationException("Instance ID cannot be null or empty");
        }

        List<Task> tasks = flowableEngine.getTaskService()
                .createTaskQuery()
                .processInstanceId(instanceId)
                .list();

        List<TaskModel> taskModels = tasks.stream()
                .map(task -> TaskModel.builder()
                        .taskId(task.getId())
                        .name(task.getName())
                        .assignee(task.getAssignee())
                        .build())
                .collect(Collectors.toList());

        log.debug("Found {} tasks for instance: instanceId={}", taskModels.size(), instanceId);
        return taskModels;
    }

    /**
     * Retrieves paginated activity history for a process instance.
     * 
     * @param processInstanceId ID of the process instance
     * @param pageable pagination configuration
     * @return page of ProcessActivityModel objects
     * @throws NotFoundException if process instance not found
     * @throws SpmsRuntimeException if an error occurs during retrieval
     */
    @Override
    public Page<ProcessActivityModel> getProcessActivities(String processInstanceId, Pageable pageable)
            throws NotFoundException, SpmsRuntimeException {
        log.debug("Getting activities for process instance: instanceId={}", processInstanceId);
        if (processInstanceId == null || processInstanceId.isEmpty()) {
            log.warn("Validation failed: Process instance ID is null or empty");
            throw new ValidationException("Process instance ID cannot be null or empty");
        }

        try {
            ActivityInstanceQuery query = flowableEngine.getRuntimeService()
                    .createActivityInstanceQuery()
                .processInstanceId(processInstanceId)
                .orderByActivityInstanceStartTime()
                .desc();

            long total = query.count();
            List<ActivityInstance> activities = query.listPage(
                (int) pageable.getOffset(), 
                pageable.getPageSize()
            );

            List<ProcessActivityModel> models = activities.stream()
                .map(this::toProcessActivityModel)
                .collect(Collectors.toList());

            return new PageImpl<>(models, pageable, total);
        } catch (Exception e) {
            log.error("Error fetching activities for instance: {}", processInstanceId, e);
            throw new SpmsRuntimeException("Failed to retrieve process activities", e);
        }
    }

    /**
     * Converts an ActivityInstance to a ProcessActivityModel.
     * 
     * @param activity ActivityInstance to convert
     * @return converted ProcessActivityModel
     */
    //TODO: migrate to a standalone class
    private ProcessActivityModel toProcessActivityModel(ActivityInstance activity) {
        ProcessActivityModel model = new ProcessActivityModel();
        model.setId(activity.getId());
        model.setProcessInstanceId(activity.getProcessInstanceId());
        model.setProcessDefinitionId(activity.getProcessDefinitionId());
        model.setStartTime(activity.getStartTime());
        model.setEndTime(activity.getEndTime());
        model.setDurationInMillis(activity.getDurationInMillis());
        model.setTransactionOrder(activity.getTransactionOrder());
        model.setDeleteReason(activity.getDeleteReason());
        model.setActivityId(activity.getActivityId());
        model.setActivityName(activity.getActivityName());
        model.setActivityType(activity.getActivityType());
        model.setExecutionId(activity.getExecutionId());
        model.setAssignee(activity.getAssignee());
        model.setTaskId(activity.getTaskId());
        model.setCalledProcessInstanceId(activity.getCalledProcessInstanceId());
        model.setTenantId(activity.getTenantId());
        return model;
    }



}
