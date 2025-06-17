package com.rotainteligente.datastructures;

import com.rotainteligente.model.CongestionLevel;
import com.rotainteligente.model.Street;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class StreetStatusTableTest {
    private StreetStatusTable table;
    private Street streetA;
    private Street streetB;

    @BeforeEach
    void setUp() {
        table = new StreetStatusTable();
        streetA = new Street("Rua A", CongestionLevel.LOW, false, false);
        streetB = new Street("Rua B", CongestionLevel.MEDIUM, true, false);
        table.addStreet(streetA);
        table.addStreet(streetB);
    }

    @Test
    void testAddStreetAndGetStreetStatus() {
        assertNotNull(table.getStreetStatus("Rua A"), "Rua A should exist.");
        assertEquals("Rua A", table.getStreetStatus("Rua A").getStreetName());
        assertEquals(CongestionLevel.LOW, table.getStreetStatus("Rua A").getCongestionLevel());

        Street streetC = new Street("Rua C", CongestionLevel.HIGH, false, true);
        table.addStreet(streetC);
        assertNotNull(table.getStreetStatus("Rua C"), "Rua C should exist after adding.");
        assertEquals(CongestionLevel.HIGH, table.getStreetStatus("Rua C").getCongestionLevel());
        assertTrue(table.getStreetStatus("Rua C").isBlocked());
    }

    @Test
    void testGetStreetStatusNonExistent() {
        assertNull(table.getStreetStatus("Rua X"), "Rua X should not exist.");
    }

    @Test
    void testUpdateStreetStatus() {
        boolean updated = table.updateStreetStatus("Rua A", CongestionLevel.HIGH, true, true);
        assertTrue(updated, "Update should be successful for existing street.");
        assertEquals(CongestionLevel.HIGH, table.getStreetStatus("Rua A").getCongestionLevel());
        assertTrue(table.getStreetStatus("Rua A").hasAccident());
        assertTrue(table.getStreetStatus("Rua A").isBlocked());
    }

    @Test
    void testUpdateStreetStatusNonExistent() {
        boolean updated = table.updateStreetStatus("Rua X", CongestionLevel.LOW, false, false);
        assertFalse(updated, "Update should fail for non-existing street.");
    }

    @Test
    void testGetAllStreets() {
        assertEquals(2, table.getAllStreets().size(), "Should have 2 streets initially.");
        Street streetC = new Street("Rua C", CongestionLevel.LOW, false, false);
        table.addStreet(streetC);
        assertEquals(3, table.getAllStreets().size(), "Should have 3 streets after adding C.");
    }
     @Test
    void addNullStreet() {
        table.addStreet(null); // Should not throw an error and not increase size.
        assertEquals(2, table.getAllStreets().size(), "Adding null street should not change size.");
    }
}
