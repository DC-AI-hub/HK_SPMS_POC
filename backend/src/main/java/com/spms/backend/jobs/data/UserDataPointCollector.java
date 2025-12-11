package com.spms.backend.jobs.data;

import com.spms.backend.service.idm.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserDataPointCollector implements DataPointCollector{

    @Autowired
    UserService userService;

    @Override
    public String name() {
        return "statistics:user.count.name";
    }

    @Override
    public String description() {
        return "statistics:user.count.description";
    }

    @Override
    public Long getValueCurrentValue() {
        return userService.countUsers();
    }
}
