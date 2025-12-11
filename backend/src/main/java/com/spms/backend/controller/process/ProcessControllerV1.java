/**
 * REST controller for process definition and instance management.
 * Provides endpoints for:
 * - Process definition lifecycle (create, read, versions)
 * - Process instance operations
 * - Process task management
 * Version: v1
 * Base Path: /api/v1/process
 */
package com.spms.backend.controller.process;

import com.spms.backend.controller.BaseController;
import com.spms.backend.controller.ProcessConverter;
import com.spms.backend.controller.dto.process.*;
import com.spms.backend.service.exception.ValidationException;
import com.spms.backend.service.model.process.FormVersionModel;
import com.spms.backend.service.model.process.ProcessDefinitionModel;
import com.spms.backend.service.exception.NotFoundException;
import com.spms.backend.service.exception.SpmsRuntimeException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.spms.backend.service.idm.UserService;
import com.spms.backend.service.model.process.ProcessVersionModel;
import com.spms.backend.service.model.process.VersionStatus;
import com.spms.backend.service.process.FormService;
import com.spms.backend.service.process.ProcessDeploymentService;
import jakarta.validation.Valid;
import com.spms.backend.service.process.ProcessDefinitionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Slf4j
@RestController
@RequestMapping("/api/v1/process")
public class ProcessControllerV1 extends BaseController {

    @Autowired
    private ProcessDefinitionService processService;

    @Autowired
    private ProcessDeploymentService processDeploymentService;
    
    @Autowired
    private UserService userService;

    @Autowired
    private ProcessConverter processConverter;

    @Autowired
    private FormService formService;

    // Process Definition Endpoints
    
    /**
     * Get all process definitions with pagination
     * 
     * @param pageable Pagination configuration (page, size, sort)
     * @return Page of ProcessDefinitionDTO containing:
     *         - Definition ID
     *         - Process name and key
     *         - Status
     */
    @GetMapping("/definitions")
    public ResponseEntity<Page<ProcessDefinitionDTO>> getProcessDefinitions(Pageable pageable) {
        try{
            Page<ProcessDefinitionModel> models = processService.getProcessDefinitions(null, pageable);
            Page<ProcessDefinitionDTO> dtos = models.map(processConverter::toProcessDefinitionDto);
            return ResponseEntity.ok(dtos);
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }




    /**
     * Creates a new process definition version.
     * Initial status will be set to DRAFT.
     * 
     * @param request Process definition details including:
     *                - name: Human-readable name (e.g. "Order Fulfillment")
     *                - key: Business key (e.g. "order_fulfillment")
     *                - bpmnXml: Complete BPMN 2.0 XML definition
     *                - businessOwnerId: ID of responsible business owner
     * @return ProcessDefinitionDTO containing:
     *         - Generated version ID and definition ID
     *         - Process name, key and version
     *         - Current status (always DRAFT initially)
     *         - Deployment information (status, message, timestamp)
     *         - Owner and business owner references
     * @throws SpmsRuntimeException if validation fails (400 Bad Request)
     */
    @PostMapping("/definitions")
    @Transactional
    //@PreAuthorize("hasRole('PROCESS_OWNER')") - Currently commented out
    public ResponseEntity<ProcessDefinitionDTO> createProcessDefinition(@RequestBody CreateProcessDefinitionRequest request) {
        try {
            ProcessDefinitionModel processDefinitionModel = new ProcessDefinitionModel();
            processDefinitionModel.setDescription(request.getProcessDescription());
            processDefinitionModel.setName(request.getProcessName());
            processDefinitionModel.setKey(request.getProcessKey());
            processDefinitionModel.setOwner(userService.getUserById(request.getOwner()));
            processDefinitionModel.setBusinessOwner(userService.getUserById(request.getBusinessOwner()));
            processDefinitionModel = processService.createProcessDefinition(processDefinitionModel);
            return ResponseEntity.ok(processConverter.toProcessDefinitionDto(processDefinitionModel));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * 创建一个新的流程定义版本。
     *
     * @param definitionId 流程定义的唯一标识符
     * @param request 包含新版本信息的请求体（BPMN XML、版本号等）
     * @return 包含新创建的流程定义版本信息的响应实体
     *         如果流程定义不存在，返回404 Not Found
     *         如果请求参数无效，返回400 Bad Request
     *         如果服务器内部错误，返回500 Internal Server Error
    */
        @PostMapping("/definitions/{definitionId}/versions")
        @Transactional
    public ResponseEntity<ProcessDefinitionVersionDTO> createProcessDefinitionVersion(
            @PathVariable long definitionId,
            @RequestBody ProcessDefinitionVersionRequest request
    ) {
            try {
                // Check if definition exists
                ProcessDefinitionModel definition = processService.getProcessDefinition(definitionId);
                if (definition == null) {
                    return ResponseEntity.notFound().build();
                }

                // Convert request to model
                ProcessVersionModel versionModel = new ProcessVersionModel();
                versionModel.setBpmnXml(request.getBpmnXml());
                versionModel.setVersion(request.getVersion());
                versionModel.setStatus(VersionStatus.DRAFT);
                versionModel.setName(request.getName());
                versionModel.setKey(request.getKey());

                // Description is not currently supported in ProcessVersionModel
                // versionModel.setDescription(request.getDescription());

                // Create version
                ProcessVersionModel createdVersion = processService.createProcessDefinitionVersion(
                        definitionId,
                        versionModel
                );

                // Convert to DTO
                ProcessDefinitionVersionDTO dto = new ProcessDefinitionVersionDTO();
                dto.setId(createdVersion.getId());
                dto.setName(definition.getName()); // Use definition name
                dto.setKey(definition.getKey());   // Use definition key
                dto.setVersion(createdVersion.getVersion());
                dto.setStatus(createdVersion.getStatus().name());
                dto.setBpmnXml(createdVersion.getBpmnXml());

                return ResponseEntity.ok(dto);
            } catch (SpmsRuntimeException e) {
                return ResponseEntity.badRequest().build();
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.internalServerError().build();
            }
        }

    /**
     * Get process definition by ID
     * @param definitionId The process definition ID
     * @return ProcessDefinitionDTO with:
     *         - Definition ID and version details
     *         - BPMN XML content
     *         - Deployment status (if deployed)
     *         - Owner and business owner references
     *         - Creation timestamp
     */
    @GetMapping("/definitions/{definitionId}")
    public ResponseEntity<ProcessDefinitionDTO> getProcessDefinition(@PathVariable String definitionId) {
        try {
            ProcessDefinitionModel model = processService.getProcessDefinition(definitionId);
            if (model == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(processConverter.toProcessDefinitionDto(model));
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Retrieves all versions of a process definition with pagination
     * 
     * @param definitionId ID of the process definition
     * @param pageable Pagination configuration (page, size, sort)
     * @return Page of ProcessDefinitionDTOs containing:
     *         - Version history
     *         - Status of each version
     * @throws NotFoundException if definition not found (404)
     */
    @Transactional
    @GetMapping("/definitions/{definitionId}/versions")
    public ResponseEntity<Page<ProcessDefinitionVersionDTO>> getProcessDefinitionVersions(
            @PathVariable String definitionId,
            @RequestParam(value = "include-bpmn", defaultValue = "false") boolean includeBpmn,
            Pageable pageable) {
        Page<ProcessVersionModel> models = processService.getDefinitionVersions(definitionId, pageable);
        Page<ProcessDefinitionVersionDTO> dtos = models.map(processConverter::toProcessDefinitionVersionDTO).map(x->{
            if(!includeBpmn) {
                x.setBpmnXml(null);
            }
            return  x;
        });
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/definitions/deployments/{deployment-id}")
    @Transactional
    public ResponseEntity<ProcessDefinitionVersionDTO> createProcessDefinitionVersion(
            @PathVariable("deployment-id") String deploymentId) {
        Optional<ProcessVersionModel> versionModel = processService.getProcessVersionByDeploymentId(deploymentId);
        return versionModel.map(ProcessDefinitionVersionDTO::toProcessDefinitionVersionDTO)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new NotFoundException("Process Version Not found"));
    }

    /**
     * Search for process definitions matching criteria
     * @param search Optional search term to filter by name or key
     * @param pageable Pagination parameters
     * @return Page of ProcessDefinitionDTO containing:
     *         - Definition ID
     *         - Process name and key
     *         - Status
     */
    @GetMapping("/definitions/search")
    public ResponseEntity<Page<ProcessDefinitionDTO>> searchProcessDefinitions(
            @RequestParam(required = false) String search,
            Pageable pageable) {
        Page<ProcessDefinitionModel> models = processService.getProcessDefinitions(search, pageable);
        Page<ProcessDefinitionDTO> dtos = models.map(processConverter::toProcessDefinitionDto);
        return ResponseEntity.ok(dtos);
    }

    /**
     * Get specific version of a process definition
     * @param definitionId The process definition ID
     * @param version The version
     * @return ProcessDefinitionDTO with:
     *         - Version details (ID, status, version number)
     *         - BPMN XML content
     *         - Deployment status (if deployed)
     *         - Last deployment timestamp
     *         - Owner references
     */
    @GetMapping("/definitions/{definitionId}/versions/{version}")
    public ResponseEntity<ProcessDefinitionVersionDTO> getProcessDefinitionVersion(
            @PathVariable String definitionId,
            @PathVariable String version
    ) {
        try {
            Optional<ProcessVersionModel> result = processService.getProcessDefinitionVersion(definitionId, version);
            if (result.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            var model = result.get();
            return ResponseEntity.ok(ProcessDefinitionVersionDTO.toProcessDefinitionVersionDTO(model));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Activates a specific version of a process definition
     * Sets the version as the active runtime version
     * 
     * @param definitionId ID of the process definition
     * @param versionId Version of the version to activate
     * @return Empty response (200 OK) on success
     * @throws NotFoundException if definition/version not found (404)
     * @throws SpmsRuntimeException if activation fails (400)
     */
    @PostMapping("/definitions/{definitionId}/versions/{versionId}/active")
    public ResponseEntity<?> activeProcessDefinitionVersion(
            @PathVariable Long definitionId,
            @PathVariable Long versionId
    ) {
        try {
            Long currentUserId = userService.getCurrentUserId();
            processDeploymentService.deployProcessDefinition(definitionId, versionId, currentUserId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Deactivates/un-deploys a specific version of a process definition
     * @param definitionId ID of the process definition
     * @param versionId ID of the version to deactivate
     * @return ResponseEntity with 200 OK if successful
     */
    @DeleteMapping("/definitions/{definitionId}/versions/{versionId}/active")
    public ResponseEntity<?> disableProcessDefinitionVersion(
            @PathVariable Long definitionId,
            @PathVariable("versionId") Long versionId
    ) {
        try {
            Long currentUserId = userService.getCurrentUserId();
            processDeploymentService.undeployProcessDefinition(definitionId, versionId, currentUserId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Updates an existing process definition version
     * 
     * @param definitionId Process definition ID
     * @param versionId Version ID of the version to update
     * @param updateRequest Update request containing new data
     * @return Updated ProcessDefinitionVersionDTO
     */
    @PutMapping("/definitions/{definitionId}/versions/{versionId}")
    @Transactional
    public ResponseEntity<ProcessDefinitionVersionDTO> updateProcessDefinitionVersion(
            @PathVariable String definitionId,
            @PathVariable String versionId,
            @RequestBody UpdateProcessVersionRequest updateRequest
    ) {
        try {
            // Get existing version
            Optional<ProcessVersionModel> existingVersionOpt = processService.getProcessDefinitionVersion(definitionId, versionId);
            if (existingVersionOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            ProcessVersionModel existingVersion = existingVersionOpt.get();
            
            // Prepare update model
            ProcessVersionModel updateModel = new ProcessVersionModel();
            updateModel.setId(existingVersion.getId());
            updateModel.setDescription(updateRequest.getDescription());
            updateModel.setBpmnXml(updateRequest.getBpmnXml());
            
            // Handle form if provided
            if (updateRequest.getFormKey() != null && updateRequest.getFormVersion() != null) {
                FormVersionModel versionModel = formService.getVersion(updateRequest.getFormKey(), updateRequest.getFormVersion());
                updateModel.setRelatedForm(versionModel);
            }
            
            // Perform update
            ProcessVersionModel updatedVersion = processService.updateProcessDefinitionVersion(
                existingVersion.getId(), 
                updateModel
            );
            
            // Convert to DTO
            ProcessDefinitionVersionDTO dto = new ProcessDefinitionVersionDTO();
            dto.setId(updatedVersion.getId());
            dto.setName(updatedVersion.getName());
            dto.setKey(updatedVersion.getKey());
            dto.setVersion(updatedVersion.getVersion());
            dto.setStatus(updatedVersion.getStatus().name());
            dto.setBpmnXml(updatedVersion.getBpmnXml());
            
            return ResponseEntity.ok(dto);
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (ValidationException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    



}
