package com.spms.backend.service.process;

import com.spms.backend.service.model.process.CapabilityModel;

import java.util.stream.Stream;

/*
* This service provide the Engine's Capability
* */
public interface EngineCapabilityService {

    Stream<CapabilityModel> getCapabilities();

}
