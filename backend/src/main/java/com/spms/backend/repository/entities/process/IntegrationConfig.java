package com.spms.backend.repository.entities.process;


import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.Table;
import lombok.Data;

import java.util.Map;

@Entity
@Data
@Table(name = "spms_integration_config")
public class IntegrationConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ElementCollection
    @CollectionTable(name = "spms_integration_configure",
            joinColumns = @JoinColumn(name = "config_id"))
    @MapKeyColumn(name = "key")
    @Column(name = "value")
    private Map<String, String> configuration;


    @ElementCollection
    @CollectionTable(name = "spms_integration_credentials",
            joinColumns = @JoinColumn(name = "config_id"))
    @MapKeyColumn(name = "key")
    @Column(name = "value")
    private Map<String, String> credentials;

    @Column(nullable = false)
    private Integer retryCount = 3;

    @Column(nullable = false)
    private Long backoffPeriod = 1000L; // ms
}