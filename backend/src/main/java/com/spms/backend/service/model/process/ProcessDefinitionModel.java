package com.spms.backend.service.model.process;

import com.spms.backend.repository.entities.process.ProcessDefinitionEntity;
import com.spms.backend.service.BaseModel;
import com.spms.backend.service.idm.UserModelFulfilledSupporter;
import com.spms.backend.service.model.idm.UserModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.NotImplementedException;

import java.util.Date;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper=false)
public class ProcessDefinitionModel extends BaseModel<ProcessDefinitionEntity> {

    private Long id;
    private String name;
    private String key;
    private UserModel owner;
    private UserModel businessOwner;
    private String description;
    private List<ProcessVersionModel> versions;
    private Long createdAt;
    private Long updatedAt;


    public static ProcessDefinitionModel fromEntity(ProcessDefinitionEntity entity, UserModelFulfilledSupporter supporter) {
        ProcessDefinitionModel model = new ProcessDefinitionModel();
        supporter.includeFulfilled(entity.getBusinessOwnerId(), model::setBusinessOwner);
        supporter.includeFulfilled(entity.getOwnerId(), model::setOwner);
        model.setId(entity.getId());
        model.setName(entity.getName());
        model.setKey(entity.getKey());
        model.setUpdatedAt(entity.getUpdatedAt());
        model.setCreatedAt(entity.getCreatedAt());
        model.setDescription(entity.getDescription());
        if(entity.getVersions()!=null) {
            model.setVersions(entity.getVersions().stream().map(ProcessVersionModel::fromEntity).toList());
        }else {
            model.setVersions(List.of());
        }
        return model;
    }


    public static ProcessDefinitionModel fromEntityWithVersions(ProcessDefinitionEntity entity, UserModelFulfilledSupporter supporter) {
        ProcessDefinitionModel model = fromEntity(entity, supporter);
        //TODO: not implement
        throw new NotImplementedException();

        //return model;
    }



    @Override
    public ProcessDefinitionEntity toEntityForUpdate() {
        ProcessDefinitionEntity procDef = new ProcessDefinitionEntity();
        procDef.setId(id);
        procDef.setKey(key);
        procDef.setName(name);
        procDef.setDescription(this.description);
        procDef.setUpdatedAt(new Date().getTime());
        if (owner != null) {
            procDef.setOwnerId(owner.getId());
        }
        if (businessOwner != null) {
            procDef.setBusinessOwnerId(businessOwner.getId());
        }
        return procDef;
    }

    @Override
    public ProcessDefinitionEntity toEntityForCreate() {
        ProcessDefinitionEntity procDef = toEntityForUpdate();
        procDef.setId(null);
        procDef.setCreatedAt(new Date().getTime());
        return procDef;
    }
}
