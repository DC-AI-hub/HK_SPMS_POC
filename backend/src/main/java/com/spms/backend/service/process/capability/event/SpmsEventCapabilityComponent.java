package com.spms.backend.service.process.capability.event;

import org.flowable.engine.delegate.TaskListener;


public interface SpmsEventCapabilityComponent extends TaskListener {

    String getName();

    String getDescription();

    boolean isEnabled();
}
