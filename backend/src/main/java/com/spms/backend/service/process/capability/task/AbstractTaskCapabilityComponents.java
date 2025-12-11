package com.spms.backend.service.process.capability.task;

import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;

@Slf4j
public abstract class AbstractTaskCapabilityComponents implements SpmsTaskCapabilityComponents {

    protected abstract void executeInternal(DelegateExecution execution);

    @Override
    public void execute(DelegateExecution execution) {
        if(log.isDebugEnabled()){
            log.debug("");
        }

        if (isEnabled()) {
            executeInternal(execution);
        } else {
            log.warn("The Task capability disabled, execute ignore: {}, process Def Id: {}, process instance Id: {}",
                    this.getName(),execution.getProcessDefinitionId(), execution.getProcessInstanceId());
        }
    }

    @Override
    public String getDescription() {
        return getName() + ":desc";
    }
}
