package com.spms.backend.repository.entities.process;

import com.spms.backend.repository.entities.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import com.spms.backend.repository.entities.idm.JpaConverter;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Setter
@Getter
@Entity
@Table(name = "spms_form_definition")
public class FormDefinitionEntity extends BaseEntity {
    // Getters and setters
    @Column(unique = true, nullable = false)
    private String key;
    
    @Column(nullable = false)
    private String name;
    
    @Column()
    @Convert(converter = JpaConverter.class)
    private Map<String, Object> schema;
    
    @Column(nullable = false)
    private String version;

}
