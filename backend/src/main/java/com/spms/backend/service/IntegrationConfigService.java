package com.spms.backend.service;

import com.spms.backend.service.model.IntegrationConfigModel;

import java.util.Optional;

public interface IntegrationConfigService {
    IntegrationConfigModel saveConfig(IntegrationConfigModel model);

    Optional<IntegrationConfigModel> getConfig(Long id);

    Optional<IntegrationConfigModel> getConfig(String name);

    void deleteConfig(Long id);

    void deleteConfig(String name);


}
