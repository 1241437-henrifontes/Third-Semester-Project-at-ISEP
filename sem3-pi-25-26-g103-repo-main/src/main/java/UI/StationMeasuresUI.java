package UI;

import Controllers.StationMeasuresController;
import Model.Graph.Node;
import Repositories.GraphRepository;
import Services.DTO.StationMeasuresResultDTO;
import java.util.List;
import java.util.Scanner;

/**
 * Console UI to inspect centrality and connectivity measures for railway stations.
 * Relies on StationMeasuresController and GraphRepository state.
 */
public class StationMeasuresUI implements Runnable {

    private final StationMeasuresController controller;

    public StationMeasuresUI() {
        this.controller = new StationMeasuresController();
    }

    @Override
    public void run() {
        if (!GraphRepository.getInstance().isBuilding()){
            System.out.println("\n--- Station Measures Analysis ---");
            Scanner scanner = new Scanner(System.in);

            try {
                List<Node> stations = controller.getAllStations();
                if (stations.isEmpty()) {
                    System.out.println("Error: No stations available in the graph.");
                    return;
                }

                System.out.println("\nAvailable Stations:");
                displayStationsList(stations);

                Node station = null;
                while (station == null) {
                    System.out.print("\nEnter Station Name or ID: ");
                    String input = scanner.nextLine().trim();

                    if (input.isEmpty()) {
                        System.out.println("Error: Station name/ID cannot be empty.");
                        continue;
                    }

                    station = controller.getStationByName(input);
                    if (station == null) {
                        station = controller.getStationById(input);
                    }

                    if (station == null) {
                        System.out.println("Error: Station '" + input + "' not found. Please try again.");
                    }
                }

                StationMeasuresResultDTO result = controller.calculateStationMeasures(station);

                displayResultsTable(result);

            } catch (NumberFormatException e) {
                System.out.println("Error: Invalid input format.");
            } catch (Exception e) {
                System.out.println("Unexpected Error: " + e.getMessage());
                e.printStackTrace();
            }
        }else{
            System.out.println("Wait! The calculation of all shortest paths is still in progress. Please try again later.");
        }
    }

    private void displayStationsList(List<Node> stations) {
        for (Node station : stations) {
            System.out.println(station.getNode_id() + " - " + station.getName());
        }
    }

    private void displayResultsTable(StationMeasuresResultDTO result) {
        String name = result.getStation().getName();
        String id = result.getStation().getNode_id();
        
        int totalWidth = 64;
        String line = "═".repeat(totalWidth);

        System.out.println("\n╔" + line + "╗");
        System.out.printf("║ %-62s ║\n", "STATION MEASURES ANALYSIS");
        System.out.println("╠" + line + "╣");
        System.out.printf("║ Station Name: %-48s ║\n", name.length() > 48 ? name.substring(0, 45) + "..." : name);
        System.out.printf("║ Station ID:   %-48s ║\n", id);
        System.out.println("╠" + line + "╣");
        System.out.printf("║ %-31s │ %-28s ║\n", "Metric", "Value");
        System.out.println("╟─────────────────────────────────┼──────────────────────────────╢");
        System.out.printf("║ Betweenness                     │ %-28.6f ║\n", result.getBetweenness());
        System.out.printf("║ Harmonic Closeness              │ %-28.6f ║\n", result.getHarmonicCloseness());
        System.out.printf("║ Degree (Normalized)             │ %-28.6f ║\n", result.getDegree());
        System.out.printf("║ Strength (Normalized)           │ %-28.6f ║\n", result.getStrength());
        System.out.println("╠" + line + "╣");
        System.out.printf("║ Hub Score (Composite)           │ %-28.6f ║\n", result.getHubScore());
        System.out.println("╚" + line + "╝");
    }
}
