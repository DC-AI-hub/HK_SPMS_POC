package com.spms.backend.jobs.data;

import com.spms.backend.service.process.ProcessInstanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProcessInstanceDataPointCollector implements DataPointCollector {

    @Autowired
    private ProcessInstanceService processInstanceService;

    @Override
    public String name() {
        return "statistics:process.instance.count.name";
    }

    @Override
    public String description() {
        return "statistics:process.instance.count.description";
    }

    @Override
    public Long getValueCurrentValue() {
        // TODO: Replace with total process instances count when available
        return processInstanceService.countRunningProcesses();
    }
}
