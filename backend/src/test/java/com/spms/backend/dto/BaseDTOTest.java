package com.spms.backend.dto;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

import com.spms.backend.controller.dto.BaseDTO;

class BaseDTOTest {
    
    @Test
    void testGettersAndSetters() {
        BaseDTO dto = new BaseDTO();
        
        // Test id
        Long testId = 123L;
        dto.setId(testId);
        assertEquals(testId, dto.getId());
        
        // Test createdDate
        Long testCreatedDate = new Date().getTime();
        dto.setCreatedDate(testCreatedDate);
        assertEquals(testCreatedDate, dto.getCreatedDate());
        
        // Test modifiedDate
        Long testModifiedDate= new Date().getTime();
        dto.setModifiedDate(testModifiedDate);
        assertEquals(testModifiedDate, dto.getModifiedDate());
    }
    
    @Test
    void testNoArgsConstructor() {
        BaseDTO dto = new BaseDTO();
        assertNotNull(dto);
    }
}
