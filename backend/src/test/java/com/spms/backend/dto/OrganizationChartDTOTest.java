package com.spms.backend.dto;

import com.spms.backend.controller.dto.idm.OrganizationChartDTO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OrganizationChartDTOTest {

    @Test
    void testLombokAnnotations() {
        OrganizationChartDTO dto = new OrganizationChartDTO();
        dto.setId("1");
        dto.setName("Test Org");
        dto.setType("COMPANY");
        
        OrganizationChartDTO child = new OrganizationChartDTO("2", "Child", "DEPARTMENT");
        dto.getChildren().add(child);

        assertNotNull(dto.toString());
        assertEquals("1", dto.getId());
        assertEquals("Test Org", dto.getName());
        assertEquals("COMPANY", dto.getType());
        assertEquals(1, dto.getChildren().size());
        assertEquals("2", dto.getChildren().get(0).getId());
    }

    @Test
    void testConstructors() {
        OrganizationChartDTO empty = new OrganizationChartDTO();
        assertNull(empty.getId());
        assertNull(empty.getName());
        assertNull(empty.getType());
        assertNotNull(empty.getChildren());
        assertTrue(empty.getChildren().isEmpty());

        OrganizationChartDTO withParams = new OrganizationChartDTO("1", "Test", "COMPANY");
        assertEquals("1", withParams.getId());
        assertEquals("Test", withParams.getName());
        assertEquals("COMPANY", withParams.getType());
        assertNotNull(withParams.getChildren());
        assertTrue(withParams.getChildren().isEmpty());
    }

    @Test
    void testChildrenOperations() {
        OrganizationChartDTO parent = new OrganizationChartDTO();
        OrganizationChartDTO child1 = new OrganizationChartDTO("1", "Child1", "DEPARTMENT");
        OrganizationChartDTO child2 = new OrganizationChartDTO("2", "Child2", "DEPARTMENT");

        parent.getChildren().add(child1);
        parent.getChildren().add(child2);

        assertEquals(2, parent.getChildren().size());
        assertEquals("Child1", parent.getChildren().get(0).getName());
        assertEquals("Child2", parent.getChildren().get(1).getName());
    }

    @Test
    void testNullFields() {
        OrganizationChartDTO dto = new OrganizationChartDTO();
        assertNull(dto.getId());
        assertNull(dto.getName());
        assertNull(dto.getType());
        assertNotNull(dto.getChildren());
    }
}
