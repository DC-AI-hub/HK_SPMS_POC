package com.spms.backend.controller.dto.process;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Setter
@Getter
public class JobScheduleRequest {
    // Getters and Setters
    private String jobName;
    private String jobType;
    private String cronExpression;
    private Map<String, Object> jobParameters;

}
