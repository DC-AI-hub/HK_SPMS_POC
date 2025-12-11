package com.spms.backend.service.process.impl;

import com.spms.backend.service.model.process.CapabilityModel;
import com.spms.backend.service.process.EngineCapabilityService;
import com.spms.backend.service.process.capability.event.SpmsEventCapabilityComponent;
import com.spms.backend.service.process.capability.task.SpmsTaskCapabilityComponents;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Service
public class EnginCapabilityServiceImpl implements EngineCapabilityService {

    private List<SpmsTaskCapabilityComponents> spmsTaskCapabilityComponents;

    private List<SpmsEventCapabilityComponent> spmsEventCapabilityComponents;

    public EnginCapabilityServiceImpl(
            List<SpmsTaskCapabilityComponents> spmsTaskCapabilityComponents,
            List<SpmsEventCapabilityComponent> eventCapabilityComponents

    ) {
        this.spmsTaskCapabilityComponents = spmsTaskCapabilityComponents;
        this.spmsEventCapabilityComponents = eventCapabilityComponents;
    }

    @Override
    public Stream<CapabilityModel> getCapabilities() {
        return Stream.concat(
                spmsEventCapabilityComponents.stream().map(x -> {
                    CapabilityModel model = new CapabilityModel();
                    model.setName(x.getName());
                    model.setDescription(x.getDescription());
                    model.setEnabled(x.isEnabled());
                    return model;
                }),
                spmsTaskCapabilityComponents.stream().map(x -> {
                    CapabilityModel model = new CapabilityModel();
                    model.setName(x.getName());
                    model.setDescription(x.getDescription());
                    model.setEnabled(x.isEnabled());
                    return model;
                })
        );
    }
}
