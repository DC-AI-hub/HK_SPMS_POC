package com.spms.backend.controller.dto.sys;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class SystemStatisticsDTO {
    // Getters and setters
    private String name;
    private String description;
    private Date asOfDate;
    private Long value;

}
