package com.spms.backend.controller.dto.process;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Map;

/**
 * DTO representing the request to create a new form instance.
 */
public class CreateFormInstanceRequestDTO {
    @NotBlank(message = "Form key is required")
    private String formKey;
    
    @NotBlank(message = "Version is required")
    private String version;
    
    @NotNull(message = "Form data is required")
    private Map<String, Object> data;

    // Getters and setters
    public String getFormKey() {
        return formKey;
    }

    public void setFormKey(String formKey) {
        this.formKey = formKey;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }
}
