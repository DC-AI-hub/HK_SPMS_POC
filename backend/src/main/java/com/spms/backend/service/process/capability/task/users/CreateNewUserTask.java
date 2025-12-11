package com.spms.backend.service.process.capability.task.users;

import com.spms.backend.service.idm.UserService;
import com.spms.backend.service.model.idm.UserModel;
import com.spms.backend.service.process.capability.task.AbstractTaskCapabilityComponents;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Component;

@Slf4j
@Component(CreateNewUserTask.CREATE_NEW_USER_TO_SPMS)
public class CreateNewUserTask extends AbstractTaskCapabilityComponents {

    public static final String CREATE_NEW_USER_TO_SPMS = "spms:tasks:user:create_new_user_spms";
    private final UserService userService;

    public CreateNewUserTask(UserService userService) {
        this.userService = userService;
    }

    @Override
    protected void executeInternal(DelegateExecution execution) {
        UserModel user = execution.getVariable("user", UserModel.class);
        UserModel created = userService.createUser(user);
        log.info("create a new user to database");
    }

    @Override
    public String getName() {
        return CREATE_NEW_USER_TO_SPMS;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
