package com.spms.backend.controller.dto.process;

import lombok.Data;

import java.util.Map;

@Data
public class TaskDTO {
    private String taskId;
    private String name;
    private String assignee;
    private String processInstanceId;
    private Map<String,Object> context;
    private String status;
}
