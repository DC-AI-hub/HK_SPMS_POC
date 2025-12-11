package com.spms.backend.controller.dto;

import java.util.Date;

/**
 * Data Transfer Object for process history information.
 * Mirrors the ProcessHistoryModel structure for API responses.
 */
public class ProcessHistoryDTO {
    private String processInstanceId;
    private Date startTime;
    private Date endTime;
    private String startUserId;
    private String businessKey;

    // Getters and setters
    public String getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getStartUserId() {
        return startUserId;
    }

    public void setStartUserId(String startUserId) {
        this.startUserId = startUserId;
    }

    public String getBusinessKey() {
        return businessKey;
    }

    public void setBusinessKey(String businessKey) {
        this.businessKey = businessKey;
    }
}
