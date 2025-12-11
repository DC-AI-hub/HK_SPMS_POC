package com.spms.backend.controller.dto.process;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;

@Setter
@Getter
@ToString
@EqualsAndHashCode
public class ProcessInstanceRequest {
    // Setters
    // Getters
    @NotBlank(message = "Definition Id cannot be blank")
    private Long definitionId;

    private Long formId;

    private Map<String, String> variable;

    private Map<String,String> formVariable;

}
