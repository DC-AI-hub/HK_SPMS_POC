package com.spms.backend.converter;

import com.spms.backend.controller.dto.process.ProcessInstanceDTO;
import com.spms.backend.controller.dto.process.TaskDTO;
import com.spms.backend.service.model.process.ProcessInstanceModel;
import com.spms.backend.service.model.process.TaskModel;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProcessInstanceConvertor {

    public ProcessInstanceDTO convertToProcessInstanceDTO(ProcessInstanceModel model, boolean includeActiveTasks) {
        if (model == null) return null;

        ProcessInstanceDTO dto = new ProcessInstanceDTO();
        dto.setInstanceId(model.getInstanceId());
        dto.setDefinitionId(model.getDefinitionId());
        dto.setStatus(model.getStatus());
        dto.setStartTime(model.getStartTime());
        dto.setEndTime(model.getEndTime());
        dto.setBusinessKey(model.getBusinessKey());
        dto.setDeploymentId(model.getDeploymentId());
        dto.setContextValue(model.getContextValue());

        if (includeActiveTasks && model.getActiveTasks() != null) {
            dto.setActiveTasks(convertTaskModels(model.getActiveTasks()));
        } else {
            dto.setActiveTasks(Collections.emptyList());
        }

        return dto;
    }

    public List<TaskDTO> convertTaskModels(List<TaskModel> tasks) {
        if (tasks == null || tasks.isEmpty()) {
            return Collections.emptyList();
        }
        return tasks.stream()
                .map(this::convertToTaskDTO)
                .collect(Collectors.toList());
    }

    public TaskDTO convertToTaskDTO(TaskModel model) {
        if (model == null) return null;

        TaskDTO dto = new TaskDTO();
        dto.setTaskId(model.getTaskId());
        dto.setName(model.getName());
        dto.setAssignee(model.getAssignee());
        dto.setProcessInstanceId(model.getProcessDefinitionId());
        dto.setStatus(model.getStatus());
        if (model.getProcessContext() != null) {
            dto.setContext(model.getProcessContext());
        }
        return dto;
    }
}
