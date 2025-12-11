package com.spms.backend.service;

import com.spms.backend.service.idm.IdmService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class SpmsServiceImpl implements SpmsService {

    private IdmService idmService;

    @Override
    public IdmService getIdmService() {
        return idmService;
    }
}
