package com.rotainteligente.controller;

import com.rotainteligente.service.RoutingEngine;
import com.rotainteligente.service.TrafficDataCollector;
import com.rotainteligente.datastructures.StreetStatusTable;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api")
public class RouteController {

    // For simplicity in this version, we instantiate services directly.
    // In a more typical Spring app, these would be @Autowired services.
    private StreetStatusTable statusTable;
    private RoutingEngine routingEngine;
    // TrafficDataCollector is static, so no instance needed for its methods.

    public RouteController() {
        // Initialize services here or ensure they are thread-safe if shared.
        // For this simple case, let's re-initialize the table on each relevant call
        // to ensure fresh data for each /route request as per the plan.
        // RoutingEngine is stateless so can be instantiated once.
        this.routingEngine = new RoutingEngine();
    }

    @GetMapping("/route")
    public List<String> findRoute(
            @RequestParam(required = false) String origin,
            @RequestParam(required = false) String destination) {

        if (origin == null || origin.trim().isEmpty() || destination == null || destination.trim().isEmpty()) {
            // Consider returning a ResponseEntity with an error status code and message
            // For now, returning an empty list or a list with an error message.
            System.err.println("Origin or destination parameter is missing or empty.");
            return Collections.singletonList("Erro: Origem e destino são obrigatórios.");
        }

        System.out.println("Received request for route from: " + origin + " to: " + destination);

        // Re-initialize and populate the table for each request to simulate fresh data.
        // This is not efficient for a real application but matches the current TrafficDataCollector model.
        this.statusTable = new StreetStatusTable();
        TrafficDataCollector.simulateDataAndUpdateTable(this.statusTable);
        // simulateDataAndUpdateTable prints to console, which is fine for server logs here.

        List<String> route = routingEngine.findBestRoute(origin, destination, this.statusTable);

        if (route.isEmpty()) {
            System.out.println("No route found from " + origin + " to " + destination);
            // Optionally, return a specific message in the list for the frontend
            // return Collections.singletonList("Nenhuma rota encontrada.");
        } else {
            System.out.println("Route found: " + String.join(" -> ", route));
        }
        return route; // Spring Boot will automatically convert this List<String> to JSON
    }
}
