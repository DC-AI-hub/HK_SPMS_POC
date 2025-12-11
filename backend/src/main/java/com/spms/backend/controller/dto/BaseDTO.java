package com.spms.backend.controller.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class BaseDTO {
    // Getters and setters
    private Long id;
    private Long createdDate;
    private Long modifiedDate;

}
