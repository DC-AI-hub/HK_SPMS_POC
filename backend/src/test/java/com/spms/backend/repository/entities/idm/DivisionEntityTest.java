package com.spms.backend.repository.entities.idm;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

public class DivisionEntityTest {

    @Test
    void testDivisionType() {
        Division division = new Division();
        division.setType(DivisionType.BUSINESS);
        assertEquals(DivisionType.BUSINESS, division.getType());
    }

    @Test
    void testCompanyRelationship() {
        Division division = new Division();
        Company company = new Company();
        division.setCompany(company);
        assertEquals(company, division.getCompany());
    }

    @Test
    void testDivisionHeadRelationship() {
        Division division = new Division();
        User head = new User();
        division.setDivisionHead(head);
        assertEquals(head, division.getDivisionHead());
    }

    @Test
    void testTimestampFields() {
        Division division = new Division();
        LocalDateTime now = LocalDateTime.now();
        division.setCreatedTime(now);
        assertEquals(now, division.getCreatedTime());
    }

    @Test
    void testDefaultActiveStatus() {
        Division division = new Division();
        assertTrue(division.getActive());
    }

    @Test
    void testNameField() {
        Division division = new Division();
        division.setName("Test Division");
        assertEquals("Test Division", division.getName());
    }
}
