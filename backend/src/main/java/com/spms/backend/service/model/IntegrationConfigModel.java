package com.spms.backend.service.model;

import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spms.backend.repository.entities.process.IntegrationConfig;

import lombok.Data;

@Data
public class IntegrationConfigModel {

    private int id;
    private int retryCount;
    private long backoffPeriod;
    private String name;
    private Map<String, String> configure;
    private Map<String, String> credentials;

    private static ObjectMapper objectMapper = new ObjectMapper();

    public static IntegrationConfigModel fromEntity(IntegrationConfig integrationConfig) {
        IntegrationConfigModel integrationConfigModel = new IntegrationConfigModel();
        integrationConfigModel.setRetryCount(integrationConfig.getRetryCount());
        integrationConfigModel.setBackoffPeriod(integrationConfig.getBackoffPeriod());
        integrationConfigModel.setName(integrationConfig.getName());
        integrationConfigModel.setConfigure(integrationConfig.getConfiguration());
        integrationConfigModel.setCredentials(integrationConfig.getCredentials());
        integrationConfigModel.setId(integrationConfigModel.getId());
        return integrationConfigModel;
    }

    public static IntegrationConfig toEntity(IntegrationConfigModel integrationConfigModel) {
        IntegrationConfig integrationConfig = new IntegrationConfig();
        integrationConfig.setRetryCount(integrationConfigModel.getRetryCount());
        integrationConfig.setBackoffPeriod(integrationConfigModel.getBackoffPeriod());
        integrationConfig.setName(integrationConfigModel.getName());
        integrationConfig.setCredentials(integrationConfig.getCredentials());
        integrationConfig.setConfiguration(integrationConfig.getConfiguration());
        return integrationConfig;
    }
}
