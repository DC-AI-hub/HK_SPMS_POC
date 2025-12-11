package com.spms.backend.service.model.process;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BusinessKeyModelTest {

    @Test
    public void testGetSeqStrWithCharacterPadding() {
        BusinessKeyModel model = new BusinessKeyModel();
        model.setPrefix("REQ");
        model.setSequence(999L);
        model.setSplit("-");
        
        String result = model.getSeqStr('a', 10);
        assertEquals("REQ-aaaaaaa999", result);
    }

    @Test
    public void testGetSeqStrWithZeroPadding() {
        BusinessKeyModel model = new BusinessKeyModel();
        model.setPrefix("REQ");
        model.setSequence(999L);
        model.setSplit("-");
        
        String result = model.getSeqStr('0', 10);
        assertEquals("REQ-0000000999", result);
    }

    @Test
    public void testGetSeqStrWithExactLength() {
        BusinessKeyModel model = new BusinessKeyModel();
        model.setPrefix("REQ");
        model.setSequence(1234567890L);
        model.setSplit("-");
        
        String result = model.getSeqStr('0', 10);
        assertEquals("REQ-1234567890", result);
    }

    @Test
    public void testGetSeqStrWithSequenceTooLong() {
        BusinessKeyModel model = new BusinessKeyModel();
        model.setPrefix("REQ");
        model.setSequence(12345678901L); // 11 digits
        model.setSplit("-");
        
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            model.getSeqStr('0', 10);
        });
        
        assertTrue(exception.getMessage().contains("exceeds maximum length"));
    }
}
