package com.spms.backend.controller.dto.process;


import com.spms.backend.controller.dto.BaseDTO;
import com.spms.backend.controller.dto.idm.UserDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class ProcessDefinitionDTO extends BaseDTO {

    private String processKey;
    private String processName;
    private String processDescription;
    private UserDTO owner;
    private UserDTO business;
    private List<ProcessDefinitionVersionDTO> versions;

}
