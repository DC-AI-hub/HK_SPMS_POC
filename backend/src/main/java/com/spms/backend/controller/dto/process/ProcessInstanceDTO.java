package com.spms.backend.controller.dto.process;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ProcessInstanceDTO {
    private String instanceId;
    private String definitionId;
    private String status;
    private Long startTime;
    private Long endTime;
    private String businessKey;
    private String deploymentId;
    private Map<String,Object> contextValue;
    private List<TaskDTO> activeTasks;
}
