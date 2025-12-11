package com.spms.backend.service;

import com.spms.backend.repository.process.IntegrationConfigRepository;
import com.spms.backend.repository.entities.process.IntegrationConfig;
import com.spms.backend.service.model.IntegrationConfigModel;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IntegrationConfigServiceImpl implements IntegrationConfigService {
    private final IntegrationConfigRepository repository;

    @Autowired
    public IntegrationConfigServiceImpl(IntegrationConfigRepository repository) {
        this.repository = repository;
    }

    /**
     * Saves the given integration configuration model to the database.
     * Converts the model to an entity, persists it, and returns the saved
     * configuration as a model.
     * 
     * @param model The integration configuration model to be saved
     * @return The saved integration configuration model
     */
    @Override
    public IntegrationConfigModel saveConfig(IntegrationConfigModel model) {

        IntegrationConfig config = IntegrationConfigModel.toEntity(model);
        return IntegrationConfigModel.fromEntity(repository.save(config));
    }

    /**
     * Retrieves an integration configuration by its ID.
     *
     * @param id The ID of the integration configuration to retrieve
     * @return An Optional containing the IntegrationConfigModel if found, empty
     *         otherwise
     */
    @Override
    public Optional<IntegrationConfigModel> getConfig(Long id) {
        return repository.findById(id).map(IntegrationConfigModel::fromEntity);
    }

    /**
     * Retrieves an integration configuration by name.
     * 
     * @param name The name of the integration configuration to retrieve
     * @return Optional containing the IntegrationConfigModel if found, empty
     *         otherwise
     */
    @Override
    public Optional<IntegrationConfigModel> getConfig(String name) {
        return repository.findByName(name).map(IntegrationConfigModel::fromEntity);
    }

    /**
     * Deletes an integration configuration by its ID.
     * 
     * @param id The ID of the integration configuration to delete
     */
    @Override
    public void deleteConfig(Long id) {
        repository.deleteById(id);
    }

    /**
     * Deletes an integration configuration by its name.
     * 
     * @param name The name of the integration configuration to delete
     */
    @Override
    public void deleteConfig(String name) {
        repository.deleteByName(name);
    }

    
}
