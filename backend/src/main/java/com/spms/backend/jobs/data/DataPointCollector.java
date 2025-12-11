package com.spms.backend.jobs.data;

public interface DataPointCollector {

    String name();

    String description();

    Long getValueCurrentValue();
}
