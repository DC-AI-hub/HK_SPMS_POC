package com.spms.backend.service.process;

import com.spms.backend.service.model.idm.UserModel;
import com.spms.backend.service.model.process.ProcessActivityModel;
import com.spms.backend.service.model.process.ProcessInstanceModel;
import com.spms.backend.service.model.process.TaskModel;
import com.spms.backend.service.exception.NotFoundException;
import com.spms.backend.service.exception.ValidationException;
import com.spms.backend.service.exception.SpmsRuntimeException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Map;

/**
 * Service interface for managing process instances and related operations.
 * Provides methods for starting, managing, and querying process instances.
 */
public interface ProcessInstanceService {

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
    ProcessInstanceModel startInstance(Long definitionId, Long formId, Map<String, String> formContext, Map<String, String> context)
        throws NotFoundException, SpmsRuntimeException;

    /**
     * Retrieves status information for a specific process instance.
     * 
     * @param instanceId unique identifier of the process instance
     * @return ProcessInstanceModel containing status details and active tasks
     * @throws ValidationException if instanceId is null or empty
     * @throws NotFoundException if no process instance matches the ID
     * @throws SpmsRuntimeException if status retrieval fails
     */
    ProcessInstanceModel getInstanceStatus(String instanceId) throws NotFoundException, SpmsRuntimeException;

    /**
     * Retrieves all tasks for a specific process instance.
     * 
     * @param instanceId ID of the process instance (cannot be null or empty)
     * @return list of TaskModel objects containing task information
     * @throws ValidationException if instanceId is null or empty
     * @throws NotFoundException if instance not found
     */
    List<TaskModel> getInstanceTasks(String instanceId) throws NotFoundException;

    /**
     * Retrieves paginated activity history for a process instance.
     * 
     * @param processInstanceId ID of the process instance
     * @param pageable pagination configuration
     * @return page of ProcessActivityModel objects
     * @throws ValidationException if processInstanceId is null or empty
     * @throws NotFoundException if process instance not found
     * @throws SpmsRuntimeException if an error occurs during retrieval
     */
    Page<ProcessActivityModel> getProcessActivities(String processInstanceId, Pageable pageable) 
        throws NotFoundException, SpmsRuntimeException;

    /**
     * Completes a task in a process instance with the provided completion values.
     * 
     * @param instanceId ID of the process instance containing the task
     * @param taskId ID of the task to complete
     * @param userId ID of the user completing the task
     * @param completedValues map containing completion data
     * @throws ValidationException if parameters are invalid
     * @throws NotFoundException if instance/task not found
     * @throws SpmsRuntimeException if task completion operation fails
     */
    void completeTask(String instanceId, String taskId, Long userId, Map<String, Object> completedValues)
        throws NotFoundException, SpmsRuntimeException;

    /**
     * Rejects a task in a process instance with specified rejection values.
     * 
     * @param instanceId ID of the process instance containing the task
     * @param taskId ID of the task to reject
     * @param userId ID of the user rejecting the task
     * @param rejectValues map containing rejection data including mandatory 'rejectionReason'
     * @throws ValidationException if parameters are invalid or rejectionReason is missing
     * @throws NotFoundException if task is not found or not assigned to user
     * @throws SpmsRuntimeException if task rejection operation fails
     */
    void rejectTask(String instanceId, String taskId, Long userId, Map<String, Object> rejectValues);

    /**
     * Saves draft data to a process instance without completing the task.
     * Only updates process instance variables, does not advance the workflow.
     * 
     * @param instanceId ID of the process instance
     * @param taskId ID of the task (for validation)
     * @param userId ID of the user saving the draft
     * @param draftValues map containing draft data
     * @throws ValidationException if parameters are invalid
     * @throws NotFoundException if instance/task not found
     * @throws SpmsRuntimeException if draft save operation fails
     */
    void saveDraft(String instanceId, String taskId, Long userId, Map<String, Object> draftValues)
        throws NotFoundException, SpmsRuntimeException;

    /**
     * Retrieves a paginated list of all process instances.
     * 
     * @param pageable pagination configuration (page number, size, sorting)
     * @return list of ProcessInstanceModel objects representing process instances
     * @throws SpmsRuntimeException if an error occurs during retrieval
     */
    List<ProcessInstanceModel> getInstances(Pageable pageable);

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
    List<ProcessInstanceModel> getUserRelatedInstances(Pageable pageable, UserModel user);
    
    /**
     * Counts active (incomplete) tasks across all process instances.
     * 
     * @return count of incomplete tasks
     * @throws SpmsRuntimeException if an error occurs during counting
     */
    long countIncompleteTasks();
    
    /**
     * Counts completed tasks across all process instances.
     * 
     * @return count of completed tasks
     * @throws SpmsRuntimeException if an error occurs during counting
     */
    long countCompletedTasks();
    
    /**
     * Counts currently active (running) process instances.
     * 
     * @return count of active process instances
     * @throws SpmsRuntimeException if an error occurs during counting
     */
    long countRunningProcesses();
}
