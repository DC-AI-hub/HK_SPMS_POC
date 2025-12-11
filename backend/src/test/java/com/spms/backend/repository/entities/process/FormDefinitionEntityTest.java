package com.spms.backend.repository.entities.process;

import static org.junit.jupiter.api.Assertions.*;
import java.util.Map;
import org.junit.jupiter.api.Test;

class FormDefinitionEntityTest {


    @Test
    void testSchemaStorage() {
        FormDefinitionEntity entity = new FormDefinitionEntity();
        entity.setKey("test-key");
        entity.setName("Test Form");
        entity.setVersion("1.0");
        
        Map<String, Object> schema = Map.of(
            "fields", new String[]{"field1", "field2"},
            "rules", Map.of("required", true)
        );
        entity.setSchema(schema);
        
        assertEquals(schema, entity.getSchema());
    }

    @Test
    void testNullSchema() {
        FormDefinitionEntity entity = new FormDefinitionEntity();
        entity.setKey("test-key");
        entity.setName("Test Form");
        entity.setVersion("1.0");
        
        entity.setSchema(null);
        assertNull(entity.getSchema());
    }

    @Test
    void testEntityEquality() {
        FormDefinitionEntity entity1 = new FormDefinitionEntity();
        entity1.setKey("test-key");
        entity1.setName("Test Form");
        entity1.setVersion("1.0");
        
        FormDefinitionEntity entity2 = new FormDefinitionEntity();
        entity2.setKey("test-key");
        entity2.setName("Test Form");
        entity2.setVersion("1.0");
        
        assertEquals(entity1.getKey(), entity2.getKey());
        assertEquals(entity1.getName(), entity2.getName());
        assertEquals(entity1.getVersion(), entity2.getVersion());
    }
}
