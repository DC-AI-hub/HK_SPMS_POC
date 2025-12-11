package com.spms.backend.jobs.data;

import com.spms.backend.service.idm.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ActiveUserDataPointCollector implements DataPointCollector {

    @Autowired
    private UserService userService;

    @Override
    public String name() {
        return "statistics:user.active.count.name";
    }

    @Override
    public String description() {
        return "statistics:user.active.count.description";
    }

    @Override
    public Long getValueCurrentValue() {
        // TODO: Replace with active user count when available
        return userService.countUsers();
    }
}
