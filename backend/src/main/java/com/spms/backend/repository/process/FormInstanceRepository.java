package com.spms.backend.repository.process;

import com.spms.backend.repository.entities.process.FormInstanceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FormInstanceRepository extends JpaRepository<FormInstanceEntity, String> {
    List<FormInstanceEntity> findByFormKey(String formKey);
    List<FormInstanceEntity> findByFormKeyAndVersion(String formKey, String version);
}
