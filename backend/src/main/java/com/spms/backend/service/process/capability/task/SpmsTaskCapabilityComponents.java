package com.spms.backend.service.process.capability.task;

import org.flowable.engine.delegate.JavaDelegate;

public interface SpmsTaskCapabilityComponents extends JavaDelegate {
    String getName();

    String getDescription();

    boolean isEnabled();
}
