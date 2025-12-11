package com.spms.backend.service.idm.impl;

import com.spms.backend.service.idm.CompanyService;
import com.spms.backend.service.idm.IdmService;
import com.spms.backend.service.idm.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;


@AllArgsConstructor
@Component
public class IdmServiceImpl implements IdmService {

    private CompanyService companyService;

    private UserService userService;


    @Override
    public CompanyService getCompanyService() {
        return companyService;
    }

    @Override
    public UserService getUserService() {
        return userService;
    }
}
