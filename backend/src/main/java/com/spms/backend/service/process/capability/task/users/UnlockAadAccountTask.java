package com.spms.backend.service.process.capability.task.users;

import com.spms.backend.service.process.capability.task.AbstractTaskCapabilityComponents;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Component;

@Slf4j
@Component(UnlockAadAccountTask.UNLOCK_AAD_ACCOUNT)
public class UnlockAadAccountTask  extends AbstractTaskCapabilityComponents {

    public static final String UNLOCK_AAD_ACCOUNT = "spms:tasks:user:unlock_aad_account";

    @Override
    protected void executeInternal(DelegateExecution execution) {

    }

    @Override
    public String getName() {
        return UNLOCK_AAD_ACCOUNT;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
