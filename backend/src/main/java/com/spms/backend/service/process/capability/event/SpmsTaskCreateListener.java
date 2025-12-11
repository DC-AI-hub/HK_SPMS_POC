package com.spms.backend.service.process.capability.event;

import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.TaskListener;
import org.flowable.task.service.delegate.DelegateTask;

import java.util.Date;

@Slf4j
public class SpmsTaskCreateListener implements TaskListener {

    @Override
    public void notify(DelegateTask delegateTask) {
        delegateTask.setAssignee("tester");
        delegateTask.setInProgressStartDueDate(new Date());
        System.out.println("hello");
    }
}
