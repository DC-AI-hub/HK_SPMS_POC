package com.spms.backend.service.process.capability.event.notify;

import com.spms.backend.service.process.capability.event.AbstractEventCapabilityComponent;
import lombok.extern.slf4j.Slf4j;
import org.flowable.task.service.delegate.DelegateTask;
import org.springframework.stereotype.Component;

@Slf4j
@Component(SpmsNotifyAllParticipator.SPMS_NOTIFY_APP_PARTICIPATOR)
public class SpmsNotifyAllParticipator extends AbstractEventCapabilityComponent {

    public static final String SPMS_NOTIFY_APP_PARTICIPATOR ="spms:events:notify:notify_all_participator";

    public SpmsNotifyAllParticipator() {

    }

    @Override
    protected void notifyInternal(DelegateTask delegateTask) {

    }

    @Override
    public String getName() {
        return SPMS_NOTIFY_APP_PARTICIPATOR;
    }


    @Override
    public boolean isEnabled() {
        return false;
    }
}
