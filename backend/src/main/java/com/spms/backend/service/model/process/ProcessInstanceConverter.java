package com.spms.backend.service.model.process;

import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.engine.history.HistoricProcessInstance;

import java.util.Collections;
import java.util.Date;

/**
 * Utility class for converting Flowable objects to domain models.
 */
public class ProcessInstanceConverter {

    /**
     * Converts a Flowable ProcessInstance to ProcessInstanceModel.
     * 
     * @param instance Flowable ProcessInstance object
     * @return Converted ProcessInstanceModel
     */
    public static ProcessInstanceModel convert(ProcessInstance instance) {
        if (instance == null) return null;
        
        return ProcessInstanceModel.builder()
            .instanceId(instance.getId())
            .definitionId(instance.getProcessDefinitionId())
            .status(instance.isEnded() ? "COMPLETED" : "RUNNING")
            .startTime(convertDateToTimestamp(instance.getStartTime()))
            .setBusinessKey(instance.getBusinessKey())
            .setContextValue(instance.getProcessVariables())

            .activeTasks(Collections.emptyList()) // Will be populated separately
            .build();
    }

    /**
     * Converts a Flowable Task to TaskModel.
     * 
     * @param task Flowable Task object
     * @return Converted TaskModel
     */
    public static TaskModel convert(Task task) {
        if (task == null) return null;
        
        return TaskModel.builder()
            .taskId(task.getId())
            .name(task.getName())
            .assignee(task.getAssignee())
            .processInstanceId(task.getProcessInstanceId())
            .processContext(task.getProcessVariables())
            .status(task.getState())
            .build();
    }

    /**
     * Converts Date to timestamp (milliseconds since epoch)
     * 
     * @param date Date object to convert
     * @return Timestamp or null if input is null
     */
    public static ProcessInstanceModel convert(HistoricProcessInstance instance) {
        if (instance == null) return null;
        
        return ProcessInstanceModel.builder()
            .instanceId(instance.getId())
            .definitionId(instance.getProcessDefinitionId())
            .status("COMPLETED")
            .startTime(convertDateToTimestamp(instance.getStartTime()))
            .endTime(convertDateToTimestamp(instance.getEndTime()))
            .setBusinessKey(instance.getBusinessKey())
            .setDeploymentId(instance.getDeploymentId())
            .activeTasks(Collections.emptyList())
            .build();
    }

    private static Long convertDateToTimestamp(Date date) {
        return date != null ? date.getTime() : null;
    }
}
