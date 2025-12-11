package com.spms.backend.controller.dto.process;

import org.quartz.Trigger.TriggerState;
import java.util.Date;

public class JobStatusResponse {
    private String jobName;
    private String jobClass;
    private Date nextFireTime;
    private TriggerState triggerState;

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getJobClass() {
        return jobClass;
    }

    public void setJobClass(String jobClass) {
        this.jobClass = jobClass;
    }

    public Date getNextFireTime() {
        return nextFireTime;
    }

    public void setNextFireTime(Date nextFireTime) {
        this.nextFireTime = nextFireTime;
    }

    public TriggerState getTriggerState() {
        return triggerState;
    }

    public void setTriggerState(TriggerState triggerState) {
        this.triggerState = triggerState;
    }
}
