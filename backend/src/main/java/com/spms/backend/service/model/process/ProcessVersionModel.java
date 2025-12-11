package com.spms.backend.service.model.process;

import com.spms.backend.repository.entities.process.ProcessVersionEntity;
import com.spms.backend.service.model.BaseModel;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ProcessVersionModel extends BaseModel<ProcessVersionEntity> {
    private long id;
    private String name;
    private String description;
    private String key; 
    private String version;
    private String bpmnXml;
    private VersionStatus status;  // Changed from String to VersionStatus
    private Boolean deployedToFlowable;
    private String flowableDefinitionId;
    private Long createdAt;
    private Long updatedAt;
    private Long createdById;
    private Long updatedById;
    private FormVersionModel relatedForm;

    public static ProcessVersionModel fromEntitySkipBpmnXml(ProcessVersionEntity entity) {
        ProcessVersionModel model = new ProcessVersionModel();
        model.setId(entity.getId());  // Convert Long to String
        model.setName(entity.getName());
        model.setDescription(entity.getDescription());
        model.setKey(entity.getKey());
        model.setVersion(entity.getVersion());
        model.setStatus(VersionStatus.valueOf(entity.getStatus().name()));  // Convert to VersionStatus
        model.setDeployedToFlowable(entity.getDeployedToFlowable());
        model.setFlowableDefinitionId(entity.getFlowableDefinitionId());
        model.setCreatedAt(entity.getCreatedAt());
        model.setUpdatedAt(entity.getUpdatedAt());
        model.setCreatedById(entity.getCreatedById());
        model.setUpdatedById(entity.getUpdatedById());
        if(entity.getFormVersion()!=null) {
            model.setRelatedForm(FormVersionModel.fromEntity(entity.getFormVersion()));
        }
        return model;
    }

    public static ProcessVersionModel fromEntity(ProcessVersionEntity entity) {
        ProcessVersionModel model = fromEntitySkipBpmnXml(entity);
        model.setBpmnXml(entity.getBpmnXml());
        return model;
    }
    
    @Override
    public ProcessVersionEntity toEntityForCreate() {
        ProcessVersionEntity entity = toEntityForUpdate();
        entity.setId(null);
        entity.setDeployedToFlowable(false);
        return entity;
    }

    @Override
    public ProcessVersionEntity toEntityForUpdate() {
        ProcessVersionEntity entity = new ProcessVersionEntity();
        entity.setId(id);  // Convert String to Long
        entity.setName(this.name);
        entity.setDescription(this.description);
        entity.setKey(this.key);
        entity.setVersion(this.version);
        entity.setBpmnXml(this.bpmnXml);
        entity.setDeployedToFlowable(this.deployedToFlowable);
        entity.setFlowableDefinitionId(this.flowableDefinitionId);
        entity.setUpdatedById(this.updatedById);
        return entity;
    }
}
