package UI;

import Controllers.RiskAwarePathController;
import Repositories.GraphRepository;
import Model.Graph.Graph;
import Model.Graph.Node;

import java.util.List;
import java.util.Scanner;

public class RoutePlannerUI implements Runnable {

    @Override
    public void run() {
        System.out.println("\n=== US15: Risk-Aware Route Planner ===");

        Graph<Node, Double> graph = GraphRepository.getInstance().getStationGraphOrdered();

        if (graph == null || graph.numVertices() == 0) {
            System.out.println("ERROR: The stations graph is empty. Please load the data first.");
            return;
        }

        RiskAwarePathController controller = new RiskAwarePathController(graph);
        Scanner scanner = new Scanner(System.in);

        List<Node> stations = controller.getAllStations();
        System.out.println("\n--- Available Stations ---");
        displayStationsList(stations);
        System.out.println("--------------------------");

        // 3. Loop de Interação
        while (true) {
            System.out.println("\nEnter the ID or NAME of the stations (or 'exit' to quit)");

            // Input Origem
            System.out.print("Origen: ");
            String startInput = scanner.nextLine().trim();
            if (startInput.equalsIgnoreCase("exit")) break;

            // Input Destino
            System.out.print("Destination: ");
            String endInput = scanner.nextLine().trim();
            if (endInput.equalsIgnoreCase("exit")) break;

            if (startInput.isEmpty() || endInput.isEmpty()) {
                System.out.println("Invalid input. Try again.");
                continue;
            }

            // 4. Executar cálculo
            System.out.println(">> Calculating robust route...");
            try {
                String result = controller.calculateRiskAwareRoute(startInput, endInput);
                System.out.println(result);
            } catch (Exception e) {
                System.out.println("Unexpected error: " + e.getMessage());
            }
        }
    }


    private void displayStationsList(List<Node> stations) {
        if (stations.isEmpty()) {
            System.out.println("(No station found)");
            return;
        }

        // Formatação simples: ID - NOME
        for (Node station : stations) {
            System.out.printf("%-5s - %s\n", station.getNode_id(), station.getName());
        }
    }
}