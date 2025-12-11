package com.spms.backend.dto;

import com.spms.backend.controller.dto.idm.ChartMode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ChartModeTest {

    @Test
    void testEnumValues() {
        // Verify all enum values exist
        assertEquals(2, ChartMode.values().length);
        assertEquals(ChartMode.FUNCTIONAL, ChartMode.valueOf("FUNCTIONAL"));
        assertEquals(ChartMode.REALISTIC, ChartMode.valueOf("REALISTIC"));
    }

    @Test
    void testValueOf() {
        // Test valid valueOf cases
        assertEquals(ChartMode.FUNCTIONAL, ChartMode.valueOf("FUNCTIONAL"));
        assertEquals(ChartMode.REALISTIC, ChartMode.valueOf("REALISTIC"));
    }

    @Test
    void testValueOfInvalid() {
        // Test invalid valueOf cases
        assertThrows(IllegalArgumentException.class, () -> {
            ChartMode.valueOf("INVALID");
        });
    }

    @Test
    void testOrdinal() {
        // Verify ordinal positions
        assertEquals(0, ChartMode.FUNCTIONAL.ordinal());
        assertEquals(1, ChartMode.REALISTIC.ordinal());
    }

    @Test
    void testToString() {
        // Verify toString matches enum name
        assertEquals("FUNCTIONAL", ChartMode.FUNCTIONAL.toString());
        assertEquals("REALISTIC", ChartMode.REALISTIC.toString());
    }
}
