package com.rotainteligente.service;

import com.rotainteligente.datastructures.StreetStatusTable; // Corrected import
import com.rotainteligente.model.CongestionLevel;         // Corrected import
import com.rotainteligente.model.Street;                  // Corrected import

public class TrafficDataCollector {

    public static void simulateDataAndUpdateTable(StreetStatusTable table) {
        if (table == null) {
            System.err.println("StreetStatusTable cannot be null.");
            return;
        }

        // Create and add some initial streets
        Street ruaA = new Street("Rua A", CongestionLevel.LOW, false, false);
        Street ruaB = new Street("Rua B", CongestionLevel.MEDIUM, false, false);
        Street ruaC = new Street("Rua C", CongestionLevel.HIGH, true, false); // Accident on Rua C
        Street ruaD = new Street("Rua D", CongestionLevel.LOW, false, true);  // Rua D is blocked
        Street ruaE = new Street("Rua E", CongestionLevel.MEDIUM, false, false);
        Street ruaF = new Street("Rua F", CongestionLevel.LOW, false, false);

        table.addStreet(ruaA);
        table.addStreet(ruaB);
        table.addStreet(ruaC);
        table.addStreet(ruaD);
        table.addStreet(ruaE);
        table.addStreet(ruaF);

        System.out.println("Initial simulated data added to StreetStatusTable:");
        table.getAllStreets().forEach((name, street) -> System.out.println(" - " + street));

        // Simulate an update
        System.out.println("\nSimulating an update for Rua B...");
        table.updateStreetStatus("Rua B", CongestionLevel.HIGH, true, false);
        System.out.println("Rua B updated: " + table.getStreetStatus("Rua B"));

        System.out.println("\nSimulating an update for a non-existing street (Rua X)...");
        boolean updated = table.updateStreetStatus("Rua X", CongestionLevel.LOW, false, false);
        System.out.println("Update status for Rua X: " + (updated ? "Success" : "Failed (Not Found)"));
    }
    // Main method removed
}
