package com.spms.backend.jobs.data;

import com.spms.backend.service.idm.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DepartmentDataPointCollector implements DataPointCollector {

    @Autowired
    private DepartmentService departmentService;

    @Override
    public String name() {
        return "statistics:department.count.name";
    }

    @Override
    public String description() {
        return "statistics:department.count.description";
    }

    @Override
    public Long getValueCurrentValue() {
        // Pass null to count all departments regardless of type
        return departmentService.countDepartments(null);
    }
}
