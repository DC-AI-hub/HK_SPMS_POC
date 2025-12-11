package com.spms.backend.repository.entities.idm;

import static org.junit.jupiter.api.Assertions.*;
import java.util.Map;
import org.junit.jupiter.api.Test;
import com.fasterxml.jackson.core.JsonProcessingException;

class JpaConverterTest {
    private final JpaConverter converter = new JpaConverter();

    @Test
    void testConvertValidMapToJson() {
        Map<String, Object> testMap = Map.of(
            "name", "Test",
            "value", 123,
            "active", true
        );
        
        String json = converter.convertToDatabaseColumn(testMap);
        assertNotNull(json);
        assertTrue(json.contains("\"name\":\"Test\""));
        assertTrue(json.contains("\"value\":123"));
    }

    @Test
    void testConvertNullMapToJson() {
        assertNull(converter.convertToDatabaseColumn(null));
    }

    @Test
    void testConvertEmptyMapToJson() {
        Map<String, Object> emptyMap = Map.of();
        String json = converter.convertToDatabaseColumn(emptyMap);
        assertEquals("{}", json);
    }

    @Test
    void testConvertValidJsonToMap() {
        String json = "{\"name\":\"Test\",\"value\":123,\"active\":true}";
        Map<String, Object> result = converter.convertToEntityAttribute(json);
        
        assertEquals("Test", result.get("name"));
        assertEquals(123, result.get("value"));
        assertEquals(true, result.get("active"));
    }

    @Test
    void testConvertNullJsonToMap() {
        assertNull(converter.convertToEntityAttribute(null));
    }

    @Test
    void testConvertEmptyJsonToMap() {
        assertNull(converter.convertToEntityAttribute(""));
        assertNull(converter.convertToEntityAttribute("  "));
    }

    @Test
    void testConvertInvalidJsonThrowsException() {
        assertThrows(IllegalArgumentException.class, 
            () -> converter.convertToEntityAttribute("{invalid}"));
    }

    @Test
    void testConvertMalformedJsonThrowsException() {
        assertThrows(IllegalArgumentException.class,
            () -> converter.convertToEntityAttribute("{\"name\":\"Test\""));
    }
}
