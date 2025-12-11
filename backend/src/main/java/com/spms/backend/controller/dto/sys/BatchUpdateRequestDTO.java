package com.spms.backend.controller.dto.sys;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for batch update system configuration request
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BatchUpdateRequestDTO {

    @NotEmpty(message = "At least one update is required")
    @Valid
    private List<BatchUpdateItem> updates;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BatchUpdateItem {
        @NotEmpty(message = "Configuration key is required")
        private String key;

        @NotEmpty(message = "Configuration value is required")
        private String value;
    }
}
