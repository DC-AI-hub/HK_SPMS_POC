package com.spms.backend.dto;

import java.time.LocalDateTime;
import java.util.Date;

import com.spms.backend.controller.dto.process.ProcessDefinitionVersionDTO;
import com.spms.backend.controller.dto.idm.UserDTO;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ProcessDefinitionDTOTest {

    @Test
    void testFieldGettersAndSetters() {
        ProcessDefinitionVersionDTO dto = new ProcessDefinitionVersionDTO();

        String testName = "Test Process";
        dto.setName(testName);
        assertEquals(testName, dto.getName());

    }

}
