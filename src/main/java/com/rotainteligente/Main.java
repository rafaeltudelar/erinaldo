package com.rotainteligente;

import com.rotainteligente.core.RoutingEngine;
import com.rotainteligente.core.TrafficDataCollector;
import com.rotainteligente.datastructures.StreetStatusTable;
import com.rotainteligente.model.Street; // Not strictly needed here but good for context
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        StreetStatusTable statusTable = new StreetStatusTable();
        RoutingEngine routingEngine = new RoutingEngine();

        // 1. Simulate initial traffic data
        System.out.println("--- Carregando Dados Iniciais de Trânsito ---");
        TrafficDataCollector.simulateDataAndUpdateTable(statusTable);
        // simulateDataAndUpdateTable already prints details of its operations

        System.out.println("\n--- Demonstração do Motor de Roteamento ---");

        // Example 1: Find a known route
        String startStreet = "Rua A";
        String endStreet = "Rua F";
        System.out.println("\nCalculando rota de '" + startStreet + "' para '" + endStreet + "'...");
        List<String> route = routingEngine.findBestRoute(startStreet, endStreet, statusTable);

        if (route.isEmpty()) {
            System.out.println("Nenhuma rota encontrada de '" + startStreet + "' para '" + endStreet + "'.");
        } else {
            System.out.println("Melhor Rota Encontrada:");
            System.out.println(String.join(" -> ", route));
        }

        // Example 2: Try a route to a blocked street (Rua D is blocked by simulator)
        startStreet = "Rua A";
        endStreet = "Rua D";
        System.out.println("\nCalculando rota de '" + startStreet + "' para '" + endStreet + "' (Rua D está bloqueada)...");
        route = routingEngine.findBestRoute(startStreet, endStreet, statusTable);

        if (route.isEmpty()) {
            System.out.println("Nenhuma rota encontrada de '" + startStreet + "' para '" + endStreet + "'.");
        } else {
            System.out.println("Melhor Rota Encontrada (destino pode estar bloqueado/custo alto):");
            System.out.println(String.join(" -> ", route));
        }

        // Example 3: Try a route where no path exists (e.g., to a non-connected/fictional street)
        startStreet = "Rua A";
        endStreet = "Rua Z (Inexistente)"; // Not in MapData nor StatusTable
        System.out.println("\nCalculando rota de '" + startStreet + "' para '" + endStreet + "'...");
        route = routingEngine.findBestRoute(startStreet, endStreet, statusTable);

        if (route.isEmpty()) {
            System.out.println("Nenhuma rota encontrada de '" + startStreet + "' para '" + endStreet + "'.");
        } else {
            System.out.println("Melhor Rota Encontrada:");
             System.out.println(String.join(" -> ", route));
        }

        // Interactive part (optional)
        Scanner scanner = new Scanner(System.in);
        System.out.println("\n--- Teste Interativo ---");
        System.out.println("Ruas disponíveis para teste (entre outras simuladas): Rua A, Rua B, Rua C, Rua D, Rua E, Rua F");
        System.out.print("Digite a rua de origem: ");
        String inputOrigin = scanner.nextLine();
        System.out.print("Digite a rua de destino: ");
        String inputDestination = scanner.nextLine();

        if (inputOrigin.trim().isEmpty() || inputDestination.trim().isEmpty()) {
            System.out.println("Origem e destino não podem ser vazios para o teste interativo.");
        } else {
            System.out.println("\nCalculando rota de '" + inputOrigin + "' para '" + inputDestination + "'...");
            List<String> interactiveRoute = routingEngine.findBestRoute(inputOrigin, inputDestination, statusTable);

            if (interactiveRoute.isEmpty()) {
                System.out.println("Nenhuma rota encontrada de '" + inputOrigin + "' para '" + inputDestination + "'.");
            } else {
                System.out.println("Melhor Rota Encontrada:");
                System.out.println(String.join(" -> ", interactiveRoute));
            }
        }
        scanner.close();
        System.out.println("\n--- Simulação Concluída ---");
    }
}
