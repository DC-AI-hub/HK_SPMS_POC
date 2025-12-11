package com.spms.backend.service.process.impl;

import com.spms.backend.repository.entities.process.ProcessDefinitionEntity;
import com.spms.backend.repository.entities.process.ProcessVersionStatus;
import com.spms.backend.repository.process.FormVersionRepository;
import com.spms.backend.repository.process.ProcessDefinitionRepository;
import com.spms.backend.service.exception.NotFoundException;
import com.spms.backend.service.exception.ValidationException;
import com.spms.backend.service.idm.UserService;
import com.spms.backend.service.model.process.FormVersionModel;
import com.spms.backend.service.model.process.ProcessDefinitionModel;
import com.spms.backend.service.model.process.ProcessVersionModel;
import com.spms.backend.service.process.ProcessDefinitionService;
import com.spms.backend.repository.entities.process.ProcessVersionEntity;
import com.spms.backend.repository.process.ProcessVersionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

@Service
public class ProcessDefinitionServiceImpl implements ProcessDefinitionService {

    private final UserService userService;
    private final ProcessVersionRepository processVersionRepository;
    private final ProcessDefinitionRepository processDefinitionRepository;
    private final FormVersionRepository formVersionRepository;
    
    private static final Logger log = LoggerFactory.getLogger(ProcessDefinitionServiceImpl.class);

    public ProcessDefinitionServiceImpl(
            UserService userService,
            ProcessVersionRepository processVersionRepository,
            ProcessDefinitionRepository processDefinitionRepository,
            FormVersionRepository formVersionRepository
    ) {
        this.userService = userService;
        this.processVersionRepository = processVersionRepository;
        this.processDefinitionRepository = processDefinitionRepository;
        this.formVersionRepository = formVersionRepository;
    }

    @Override
    @Transactional
    public Page<ProcessDefinitionModel> getProcessDefinitions(String search, Pageable pageable) {
        if (pageable == null) {
            throw new ValidationException("Pageable cannot be null");
        }
        Page<ProcessDefinitionEntity> entities = processDefinitionRepository.findDefinitions(search, pageable);
        final var supporter = userService.getFulfilledSupporter();
        var result = entities.map(x -> ProcessDefinitionModel.fromEntity(x, supporter));
        supporter.fulfill();
        return result;
    }

    @Override
    public Page<ProcessVersionModel> getDefinitionVersions(String definitionId, Pageable pageable) {
        if (definitionId == null || definitionId.isEmpty()) {
            throw new ValidationException("Definition ID cannot be null or empty");
        }
        try {
            Long defId = Long.parseLong(definitionId);
            Page<ProcessVersionEntity> entities = processVersionRepository.findByProcessDefinitionId(defId, pageable);
            return entities.map(ProcessVersionModel::fromEntity);
        } catch (NumberFormatException e) {
            throw new ValidationException("Invalid definition ID format");
        }
    }

    @Override
    public long countProcessDefinitions() {
        return processDefinitionRepository.count();
    }

    @Override
    public ProcessDefinitionModel getProcessDefinition(Long definitionId) {
        ProcessDefinitionEntity entity = processDefinitionRepository.findById(definitionId)
                .orElseThrow(() -> new NotFoundException("Process definition not found"));
        return userService
                .getFulfilledSupporter()
                .fulfill(x -> ProcessDefinitionModel.fromEntity(entity, x));
    }



    @Override
    public ProcessDefinitionModel getProcessDefinition(String definitionId) {
        try {
            if (definitionId == null || definitionId.isEmpty()) {
                throw new ValidationException("Definition ID cannot be null or empty");
            }
            return getProcessDefinition(Long.parseLong(definitionId));
        } catch (NumberFormatException e) {
            throw new ValidationException("Invalid definition ID format");
        }
    }

    @Override
    public Optional<ProcessVersionModel> getProcessDefinitionVersion(Long definitionId, String version) {
        Optional<ProcessVersionEntity> entity = processVersionRepository.findByProcessDefinitionIdAndVersion(definitionId, version);
        return entity.map(ProcessVersionModel::fromEntity);
    }

    public Optional<ProcessVersionModel> getProcessVersionByDeploymentId(String deploymentId) {

        if (deploymentId == null || deploymentId.isEmpty()) {
            throw new ValidationException("Definition ID cannot be null or empty");
        }
        return processVersionRepository.findByFlowableDeploymentId(deploymentId).map(ProcessVersionModel::fromEntity);

    }

    @Override
    public Optional<ProcessVersionModel> getProcessDefinitionVersion(String definitionId, String versionIdStr) {
        if (definitionId == null || definitionId.isEmpty()) {
            throw new ValidationException("Definition ID cannot be null or empty");
        }
        if (versionIdStr == null || versionIdStr.isEmpty()) {
            throw new ValidationException("Version cannot be null or empty");
        }
        try {
            Long defId = Long.parseLong(definitionId);
            Long versionId = Long.parseLong(versionIdStr);

            return processVersionRepository.findById(versionId)
                    .filter(version -> version.getProcessDefinition().getId().equals(defId))
                    .map(ProcessVersionModel::fromEntity);
        } catch (NumberFormatException e) {
            throw new ValidationException("Invalid definition ID format");
        }
    }

    @Override
    @Transactional
    public ProcessVersionModel createProcessDefinitionVersion(Long definitionId, ProcessVersionModel versionModel) {
        // Validate inputs
        if (definitionId == null) {
            throw new ValidationException("Definition ID cannot be null");
        }
        if (versionModel == null) {
            throw new ValidationException("Version model cannot be null");
        }
        if (versionModel.getVersion() == null || versionModel.getVersion().isEmpty()) {
            throw new ValidationException("Version cannot be null or empty");
        }

        // Check if process definition exists
        ProcessDefinitionEntity definition = processDefinitionRepository.findById(definitionId)
                .orElseThrow(() -> new NotFoundException("Process definition not found with ID: " + definitionId));

        // Check for duplicate version
        Optional<ProcessVersionEntity> existingVersion = processVersionRepository.findByProcessDefinitionIdAndVersion(
                definitionId, versionModel.getVersion()
        );
        if (existingVersion.isPresent()) {
            throw new ValidationException("Version " + versionModel.getVersion() + " already exists for this definition");
        }

        // Convert model to entity
        ProcessVersionEntity newVersion = versionModel.toEntityForCreate();
        newVersion.setProcessDefinition(definition);
        newVersion.setKey(versionModel.getKey());
        newVersion.setName(versionModel.getName());

        // Set audit fields
        Long currentUserId = userService.getCurrentUserId();
        newVersion.setCreatedById(currentUserId);
        newVersion.setUpdatedById(currentUserId);
        newVersion.setUpdatedAt(new Date().getTime());
        newVersion.setCreatedAt(new Date().getTime());

        // Set initial status
        newVersion.setStatus(ProcessVersionStatus.DRAFT);

        if (versionModel.getRelatedForm() != null) {
            newVersion.setFormVersion(FormVersionModel.toEntity(versionModel.getRelatedForm()));
        }
        // Save new version
        ProcessVersionEntity savedVersion = processVersionRepository.save(newVersion);

        // Return created version as model
        return ProcessVersionModel.fromEntity(savedVersion);
    }


    @Override
    @Transactional
    public ProcessDefinitionModel createProcessDefinition(ProcessDefinitionModel model) {
        if (model == null) {
            throw new ValidationException("Model cannot be null");
        }
        
        ProcessDefinitionEntity entity = model.toEntityForCreate();
        entity.setUpdatedById(userService.getCurrentUserId());
        entity.setCreatedById(userService.getCurrentUserId());
        ProcessDefinitionEntity savedEntity = this.processDefinitionRepository.save(entity);
        var support = userService.getFulfilledSupporter();
        var result = ProcessDefinitionModel.fromEntity(savedEntity, support);
        support.fulfill();
        return result;
    }

    @Override
    @Transactional
    public ProcessVersionModel updateProcessDefinitionVersion(Long versionId, ProcessVersionModel versionModel) {
        // Validate inputs
        if (versionId == null) {
            throw new ValidationException("Version ID cannot be null");
        }
        if (versionModel == null) {
            throw new ValidationException("Version model cannot be null");
        }

        // Fetch existing version
        ProcessVersionEntity existingVersion = processVersionRepository.findById(versionId)
                .orElseThrow(() -> new NotFoundException("Process version not found with ID: " + versionId));

        // Check if version is editable (only DRAFT status can be modified)
        if (existingVersion.getStatus() != ProcessVersionStatus.DRAFT) {
            throw new ValidationException("Only DRAFT versions can be modified");
        }

        // Update allowed fields: description, formVersion, and bpmnXml
        existingVersion.setDescription(versionModel.getDescription());
        existingVersion.setBpmnXml(versionModel.getBpmnXml());

        // Update formVersion if provided
        if (versionModel.getRelatedForm() != null) {
            existingVersion.setFormVersion(formVersionRepository.getReferenceById(versionModel.getRelatedForm().getId()));
        }

        // Prevent modification of immutable fields
        if (versionModel.getKey() != null && !versionModel.getKey().equals(existingVersion.getKey())) {
            throw new ValidationException("Key cannot be modified");
        }
        if (versionModel.getVersion() != null && !versionModel.getVersion().equals(existingVersion.getVersion())) {
            throw new ValidationException("Version cannot be modified");
        }

        // Update audit fields
        existingVersion.setUpdatedById(userService.getCurrentUserId());
        existingVersion.setUpdatedAt(new Date().getTime());

        // Save updated version
        ProcessVersionEntity updatedVersion = processVersionRepository.save(existingVersion);

        // Return updated version as model
        return ProcessVersionModel.fromEntity(updatedVersion);
    }
}
