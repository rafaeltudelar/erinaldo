package com.rotainteligente.core;

import com.rotainteligente.datastructures.StreetStatusTable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class RoutingEngineTest {
    private RoutingEngine routingEngine;
    private StreetStatusTable statusTable;

    @BeforeEach
    void setUp() {
        routingEngine = new RoutingEngine();
        statusTable = new StreetStatusTable();
        // Populate statusTable with data consistent with MapData for testing
        // Streets: Rua A, Rua B, Rua C, Rua D, Rua E, Rua F
        TrafficDataCollector.simulateDataAndUpdateTable(statusTable);
        // simulateDataAndUpdateTable already prints to console, which is fine for tests too.
        // It also modifies Rua B to HIGH congestion and accident.
    }

    @Test
    void findBestRoute_simplePathExists() {
        // Path: Rua A -> Rua B -> Rua E -> Rua F
        // Rua B is HIGH congestion with accident.
        // Expected: A -> B -> E -> F
        // Weights:
        // A: LOW (10)
        // B: HIGH (50) + ACCIDENT (100) = 150
        // C: HIGH (50) + ACCIDENT (100) = 150 (connects to B)
        // D: LOW (10) but BLOCKED (Infinity) (connects to A)
        // E: MEDIUM (25) (connects to B, F)
        // F: LOW (10) (connects to E)

        // Dijkstra from A (cost 0):
        //  -> B (cost of B is 150), path [A,B], total path weight to B = 150
        //  -> D (cost of D is INF), path [A,D], total path weight to D = INF
        // Min queue: [B (150), D (INF)] -> Pop B
        // Current: B (path [A,B], total 150)
        // Neighbors of B: A, C, E
        //  -> A: statusTable.getStreetStatus("Rua A").getCongestionLevel() is LOW (10). newDistToNeighbor = 150 + 10 = 160. Current dist(A)=0. No update.
        //  -> C: statusTable.getStreetStatus("Rua C") has ACCIDENT and HIGH. Cost = 150. newDistToNeighbor = 150 + 150 = 300. dist(C)=INF. Update dist(C)=300, pred(C)=B. Add C(300) to queue. Path [A,B,C]
        //  -> E: statusTable.getStreetStatus("Rua E") is MEDIUM. Cost = 25. newDistToNeighbor = 150 + 25 = 175. dist(E)=INF. Update dist(E)=175, pred(E)=B. Add E(175) to queue. Path [A,B,E]
        // Min queue: [E (175), C (300), D (INF)] -> Pop E
        // Current: E (path [A,B,E], total 175)
        // Neighbors of E: B, F
        //  -> B: Cost 150. newDist = 175 + 150 = 325. Current dist(B)=150. No update.
        //  -> F: statusTable.getStreetStatus("Rua F") is LOW. Cost = 10. newDist = 175 + 10 = 185. dist(F)=INF. Update dist(F)=185, pred(F)=E. Add F(185) to queue. Path [A,B,E,F]
        // Min queue: [F (185), C (300), D (INF)] -> Pop F
        // Current: F (path [A,B,E,F], total 185). Target found.
        // Reconstruct path from F: F <- E <- B <- A. So, [A, B, E, F].

        List<String> route = routingEngine.findBestRoute("Rua A", "Rua F", statusTable);
        assertNotNull(route);
        assertFalse(route.isEmpty(), "A route should be found.");
        assertEquals(List.of("Rua A", "Rua B", "Rua E", "Rua F"), route, "Route should be A -> B -> E -> F");
    }

    @Test
    void findBestRoute_alternativePathDueToBlockage() {
        // Rua D is blocked by TrafficDataCollector.simulateDataAndUpdateTable(statusTable);
        // Attempting to route A -> D. Path should be [A, D].
        // Cost to D is INF, but it's the destination.
        // If we try to route A -> F, path should not go via D.
        // This test is similar to findBestRoute_simplePathExists as D's blockage is already accounted for.
        List<String> route = routingEngine.findBestRoute("Rua A", "Rua F", statusTable);
        assertEquals(List.of("Rua A", "Rua B", "Rua E", "Rua F"), route, "Route should avoid blocked Rua D for path A->F.");
    }

    @Test
    void findBestRoute_noPathExists() {
        // Add a new street G that is not connected in MapData.
        statusTable.addStreet(new com.rotainteligente.model.Street("Rua G", com.rotainteligente.model.CongestionLevel.LOW, false, false));
        List<String> route = routingEngine.findBestRoute("Rua A", "Rua G", statusTable);
        assertNotNull(route, "Route object should not be null.");
        assertTrue(route.isEmpty(), "Route should be empty as Rua G is not connected.");
    }

    @Test
    void findBestRoute_startAndEndSame() {
        List<String> route = routingEngine.findBestRoute("Rua A", "Rua A", statusTable);
        assertNotNull(route);
        assertEquals(List.of("Rua A"), route, "Route to self should be just the street itself.");
    }

    @Test
    void findBestRoute_destinationIsBlockedButReachable() {
        // Rua D is blocked. Let's try to route to Rua D.
        // The engine should still return the path to it [A,D] because it's the destination.
        List<String> route = routingEngine.findBestRoute("Rua A", "Rua D", statusTable);
        assertNotNull(route);
        assertFalse(route.isEmpty(), "Path to a blocked destination should be found.");
        assertEquals(List.of("Rua A", "Rua D"), route, "Should find path to a blocked destination [A, D].");
    }

    @Test
    void findBestRoute_startStreetDoesNotExist() {
        List<String> route = routingEngine.findBestRoute("Rua Z", "Rua A", statusTable);
        assertTrue(route.isEmpty(), "Route should be empty if start street does not exist.");
    }

    @Test
    void findBestRoute_endStreetDoesNotExist() {
        // End street not existing in statusTable means it can't be a destination.
        List<String> route = routingEngine.findBestRoute("Rua A", "Rua Z", statusTable);
        assertTrue(route.isEmpty(), "Route should be empty if end street does not exist in status table.");
    }
}
