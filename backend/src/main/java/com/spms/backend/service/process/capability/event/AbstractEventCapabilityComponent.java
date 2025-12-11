package com.spms.backend.service.process.capability.event;

import lombok.extern.slf4j.Slf4j;
import org.flowable.task.service.delegate.DelegateTask;

@Slf4j
public abstract class AbstractEventCapabilityComponent implements SpmsEventCapabilityComponent {

    protected abstract void notifyInternal(DelegateTask delegateTask);

    @Override
    public void notify(DelegateTask delegateTask) {
        if (log.isDebugEnabled()) {
            log.debug("");
        }
        if (isEnabled()) {
            notifyInternal(delegateTask);
        } else {
            log.warn("The Event capability disabled, execute ignore: {}, process Def Id: {}, process instance Id: {}",
                    this.getName(), delegateTask.getProcessDefinitionId(), delegateTask.getProcessInstanceId());
        }

    }

    @Override
    public String getDescription() {
        return getName() + ":desc";
    }
}
