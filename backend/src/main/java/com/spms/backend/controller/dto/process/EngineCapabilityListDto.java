package com.spms.backend.controller.dto.process;

import lombok.Data;

import java.util.List;

@Data
public class EngineCapabilityListDto {

    private List<EngineCapabilityDto> events;

    private List<EngineCapabilityDto> tasks;

}
