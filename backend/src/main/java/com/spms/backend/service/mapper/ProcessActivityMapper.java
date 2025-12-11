package com.spms.backend.service.mapper;

import com.spms.backend.service.model.process.ProcessActivityModel;
import org.flowable.engine.runtime.ActivityInstance;

public class ProcessActivityMapper {
    public static ProcessActivityModel toModel(ActivityInstance activity) {
        ProcessActivityModel model = new ProcessActivityModel();
        model.setId(activity.getId());
        model.setProcessInstanceId(activity.getProcessInstanceId());
        model.setProcessDefinitionId(activity.getProcessDefinitionId());
        model.setStartTime(activity.getStartTime());
        model.setEndTime(activity.getEndTime());
        model.setDurationInMillis(activity.getDurationInMillis());
        model.setTransactionOrder(activity.getTransactionOrder());
        model.setDeleteReason(activity.getDeleteReason());
        model.setActivityId(activity.getActivityId());
        model.setActivityName(activity.getActivityName());
        model.setActivityType(activity.getActivityType());
        model.setExecutionId(activity.getExecutionId());
        model.setAssignee(activity.getAssignee());
        model.setTaskId(activity.getTaskId());
        model.setCalledProcessInstanceId(activity.getCalledProcessInstanceId());
        model.setTenantId(activity.getTenantId());
        return model;
    }
}
