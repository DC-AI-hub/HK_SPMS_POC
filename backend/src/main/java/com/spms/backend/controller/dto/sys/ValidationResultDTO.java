package com.spms.backend.controller.dto.sys;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for validation result response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ValidationResultDTO {

    private boolean isValid;
    private String message;
}
