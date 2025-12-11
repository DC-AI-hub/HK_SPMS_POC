package com.spms.backend.service.process.impl;

import com.spms.backend.repository.entities.process.ProcessVersionEntity;
import com.spms.backend.repository.entities.process.ProcessVersionStatus;
import com.spms.backend.repository.process.ProcessDefinitionRepository;
import com.spms.backend.repository.process.ProcessVersionRepository;
import com.spms.backend.service.exception.NotFoundException;
import com.spms.backend.service.exception.SpmsRuntimeException;
import com.spms.backend.service.exception.ValidationException;
import com.spms.backend.service.process.ProcessDeploymentService;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.Deployment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ProcessDeploymentServiceImpl implements ProcessDeploymentService {

    private final ProcessDefinitionRepository processDefinitionRepository;
    private final ProcessVersionRepository processVersionRepository;
    private final ProcessEngine flowableEngine;
    
    private static final Logger log = LoggerFactory.getLogger(ProcessDeploymentServiceImpl.class);

    public ProcessDeploymentServiceImpl(
            ProcessDefinitionRepository processDefinitionRepository,
            ProcessVersionRepository processVersionRepository,
            ProcessEngine flowableEngine
    ) {
        this.processDefinitionRepository = processDefinitionRepository;
        this.processVersionRepository = processVersionRepository;
        this.flowableEngine = flowableEngine;
    }

    /**
     * Deploys a process definition version with enhanced logging
     */
    @Override
    @Transactional
    public void deployProcessDefinition(Long definitionId, Long version, Long ownerId) {
        log.debug("Starting deployment for definitionId: {}, version: {}, ownerId: {}", 
                  definitionId, version, ownerId);
        
        if (version == null) {
            log.warn("Deployment failed: Version cannot be null");
            throw new ValidationException("Version cannot be null or empty");
        }
        if (ownerId == null) {
            log.warn("Deployment failed: Owner ID cannot be null");
            throw new ValidationException("Owner ID cannot be null");
        }
        
        var definition = processDefinitionRepository.findById(definitionId)
                .orElseThrow(() -> new NotFoundException("Process definition not found"));

        var result = processVersionRepository.findById(version)
                .filter(x -> Objects.equals(x.getProcessDefinition().getId(), definitionId))
                .stream().findFirst();

        var deployList = processVersionRepository.findByProcessDefinitionId(definitionId, Pageable.unpaged());
        if (!deployList.getContent().isEmpty()) {
            List<ProcessVersionEntity> deployedVersions = deployList.getContent().stream()
                .filter(v -> v.getStatus() == ProcessVersionStatus.DEPLOYED)
                .toList();
            
            for (ProcessVersionEntity deployedVersion : deployedVersions) {
                log.info("Un-deploying version {} of process definition {} (ID: {})",
                    deployedVersion.getVersion(), definition.getName(), definitionId);
                deployedVersion.setStatus(ProcessVersionStatus.DEPRECATED);
                deployedVersion.setUpdatedById(ownerId);
                processVersionRepository.save(deployedVersion);
                log.info("Marked version {} as DEPRECATED for definition {} (ID: {})", 
                         deployedVersion.getVersion(), definition.getName(), definitionId);
            }
        }

        if (result.isPresent()) {
            try {
                ProcessVersionEntity entity = result.get();
                log.info("Processing deployment for definition: {} (ID: {}), version: {}", 
                         definition.getName(), definitionId, version);
                
                RepositoryService repositoryService = flowableEngine.getRepositoryService();


                if (entity.getStatus() == ProcessVersionStatus.DEPLOYED) {
                    log.error("Attempted to deploy version {} of process definition {} (ID: {}) which is already deployed", 
                        version, definition.getName(), definitionId);
                    throw new SpmsRuntimeException(
                        "Version " + version + " is already deployed",
                        new Exception("PROCESS_VERSION_ALREADY_DEPLOYED")
                    );
                }

                if (entity.getStatus() == ProcessVersionStatus.DEPRECATED) {
                    log.error("Attempted to deploy version {} of process definition {} (ID: {}) which is deprecated", 
                        version, definition.getName(), definitionId);
                    throw new SpmsRuntimeException(
                        "Version " + version + " is deprecated and cannot be deployed",
                        new Exception("PROCESS_VERSION_DEPRECATED")
                    );
                }

                Deployment deployment = repositoryService.createDeployment()
                        .addString(definition.getKey() + ".bpmn20.xml", entity.getBpmnXml())
                        .key(entity.getFlowableDefinitionId())
                        .name(entity.getName())
                        .deploy();

                //entity.setFlowableDefinitionId(deployment.get);
                entity.setFlowableDeploymentId(deployment.getId());
                entity.setStatus(ProcessVersionStatus.DEPLOYED);
                entity.setDeployedToFlowable(true);
                entity.setUpdatedById(ownerId);
                processVersionRepository.save(entity);

                log.info("Successfully deployed process. Definition: {} (ID: {}), Version: {}, Deployment ID: {}", 
                         definition.getName(), definitionId, version, deployment.getId());

            } catch (SpmsRuntimeException spmsRuntimeException) {
                log.error("Deployment workflow exception: {}", spmsRuntimeException.getMessage(), spmsRuntimeException);
                throw spmsRuntimeException;
            } catch (Exception ex) {
                log.error("Deployment failed for definitionId: {}, version: {}. Error: {}", 
                          definitionId, version, ex.getMessage(), ex);
                throw new SpmsRuntimeException("Deployment failed", ex);
            }
        } else {
            log.error("Process version not found for definition ID: {} and version: {}", definitionId, version);
            throw new NotFoundException("Process version not found for definition ID: " + definitionId + " and version: " + version);
        }
    }

    /**
     * Undeploys a process definition version with enhanced logging
     */
    @Override
    @Transactional
    public void undeployProcessDefinition(Long definitionId, Long version, Long userId) {
        log.debug("Starting undeployment for definitionId: {}, version: {}, userId: {}", 
                  definitionId, version, userId);
        
        if (version == null) {
            log.warn("Un-deployment failed: Version cannot be null");
            throw new ValidationException("Version cannot be null or empty");
        }
        if (userId == null) {
            log.warn("Un-deployment failed: User ID cannot be null");
            throw new ValidationException("User ID cannot be null");
        }

        var entity = processVersionRepository.findById(version)
                .filter(x -> Objects.equals(x.getProcessDefinition().getId(), definitionId))
                .orElseThrow(() -> {
                    log.error("Process version not found for definition ID: {} and version: {}", definitionId, version);
                    return new NotFoundException("Process version not found");
                });

        try {
            log.info("Undeplying version {} for definition {} (ID: {})", 
                     version, entity.getProcessDefinition().getName(), definitionId);
            
            if (entity.getFlowableDeploymentId() != null) {
                flowableEngine.getRepositoryService()
                        .deleteDeployment(entity.getFlowableDeploymentId(), true);
            }
            
            entity.setDeployedToFlowable(false);
            entity.setUpdatedById(userId);
            entity.setFlowableDeploymentId(null);
            entity.setStatus(ProcessVersionStatus.DEPRECATED);
            processVersionRepository.save(entity);
            
            log.info("Marked version {} as DEPRECATED for definition {} (ID: {})", 
                     version, entity.getProcessDefinition().getName(), definitionId);
        } catch (Exception e) {
            log.error("Undeployment failed for definitionId: {}, version: {}. Error: {}", 
                      definitionId, version, e.getMessage(), e);
            throw new SpmsRuntimeException("Failed to undeploy process from Flowable", e);
        }
    }
}
