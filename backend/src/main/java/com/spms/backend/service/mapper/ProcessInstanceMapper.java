package com.spms.backend.service.mapper;

import com.spms.backend.service.model.process.ProcessInstanceModel;
import org.flowable.engine.runtime.ProcessInstance;

public class ProcessInstanceMapper {
    public static ProcessInstanceModel toModel(ProcessInstance instance) {
        return ProcessInstanceModel.builder()
            .instanceId(instance.getId())
            .definitionId(instance.getProcessDefinitionId())
            .startTime(instance.getStartTime().getTime())
            .setBusinessKey(instance.getBusinessKey())
            .build();
    }
}
