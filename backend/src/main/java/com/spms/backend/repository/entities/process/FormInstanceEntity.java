package com.spms.backend.repository.entities.process;

import com.spms.backend.repository.entities.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "spms_form_instance")
public class FormInstanceEntity extends BaseEntity {

    @Column(name = "form_key", nullable = false)
    private String formKey;

    @Column(nullable = false)
    private String version;

    @Column(name = "form_data", columnDefinition = "TEXT")
    private String formData;

    @Column(name = "created_at", nullable = false)
    private Long createdAt;

    @Column(name = "updated_at")
    private Long updatedAt;

}
