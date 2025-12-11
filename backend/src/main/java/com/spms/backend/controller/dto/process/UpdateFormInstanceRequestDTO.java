package com.spms.backend.controller.dto.process;

import jakarta.validation.constraints.NotNull;
import java.util.Map;

/**
 * DTO representing the request to update an existing form instance.
 */
public class UpdateFormInstanceRequestDTO {
    @NotNull(message = "Form data is required")
    private Map<String, Object> data;

    // Getters and setters
    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }
}
