package com.spms.backend.controller.dto.process;

import com.spms.backend.controller.dto.BaseRequestDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CreateProcessDefinitionRequest extends BaseRequestDTO {

    private String processKey;
    private String processName;
    private String processDescription;
    private Long owner;
    private Long businessOwner;
}
