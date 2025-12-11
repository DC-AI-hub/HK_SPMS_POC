package com.spms.backend.controller.dto.sys;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for system configuration update request
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemConfigUpdateRequestDTO {

    @NotBlank(message = "Configuration value is required")
    private String value;

    private String modifiedBy; // Optional, will be set from current user if not provided
}
