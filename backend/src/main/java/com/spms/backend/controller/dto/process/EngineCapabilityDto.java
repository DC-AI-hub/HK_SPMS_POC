package com.spms.backend.controller.dto.process;

import lombok.Data;

@Data
public class EngineCapabilityDto {

    private String name;

    private String description;

    private boolean enabled;

    private String category;

}
