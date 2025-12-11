package com.spms.backend.repository.entities.process;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class FormVersionEntityTest {

    @Test
    void testPublishedDateValidation() {
        FormVersionEntity entity = new FormVersionEntity();
        entity.setKey("test-key");
        entity.setVersion(10000L); // 1.0.0 as Long
        
        long pastDate = System.currentTimeMillis() - 86400000; // 1 day ago
        entity.setPublishedDate(pastDate);
        assertEquals(pastDate, entity.getPublishedDate());

        long futureDate = System.currentTimeMillis() + 86400000; // 1 day from now
        entity.setPublishedDate(futureDate);
        assertEquals(futureDate, entity.getPublishedDate());
    }

    @Test
    void testEntityEquality() {
        long now = System.currentTimeMillis();
        
        FormVersionEntity entity1 = new FormVersionEntity();
        entity1.setKey("test-key");
        entity1.setVersion(10000L); // 1.0.0 as Long
        entity1.setPublishedDate(now);
        
        FormVersionEntity entity2 = new FormVersionEntity();
        entity2.setKey("test-key");
        entity2.setVersion(10000L); // 1.0.0 as Long
        entity2.setPublishedDate(now);
        
        assertEquals(entity1.getKey(), entity2.getKey());
        assertEquals(entity1.getVersion(), entity2.getVersion());
        assertEquals(entity1.getPublishedDate(), entity2.getPublishedDate());
    }
}
