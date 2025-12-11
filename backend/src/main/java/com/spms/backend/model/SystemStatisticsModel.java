package com.spms.backend.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class SystemStatisticsModel {
    // Getters and setters
    private String name;
    private String description;
    private Date asOfDate;
    private Long value;
}


