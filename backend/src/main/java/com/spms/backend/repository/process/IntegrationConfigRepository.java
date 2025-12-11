package com.spms.backend.repository.process;

import com.spms.backend.repository.entities.process.IntegrationConfig;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface IntegrationConfigRepository 
       extends JpaRepository<IntegrationConfig, Long> {

    Optional<IntegrationConfig> findByName(String name);

    void deleteByName(String name);
}
