package UI;

import Controllers.MaxFlowController;
import Model.Graph.Node;
import Services.DTO.MaxFlowResultDTO;
import java.util.List;
import java.util.Scanner;

/**
 * User Interface for USEI14 - Maximum Throughput Between Two Hubs.
 * Allows an operations analyst to compute the maximum flow between two selected stations.
 */
public class MaxFlowUI implements Runnable {

    private final MaxFlowController controller;

    public MaxFlowUI() {
        this.controller = new MaxFlowController();
    }

    @Override
    public void run() {
        System.out.println("\n--------------------------------");
        System.out.println("Maximum Throughput Between Two Hubs");
        System.out.println("--------------------------------");

        Scanner scanner = new Scanner(System.in);

        try {
            // Get all available stations
            List<Node> stations = controller.getAllStations();
            if (stations.isEmpty()) {
                System.out.println("\nError: No stations available in the graph.");
                return;
            }

            System.out.println("\nAvailable Stations:");
            System.out.println("---------------------------------------------");
            displayStationsList(stations);

            // Get source station
            Node sourceStation = null;
            while (sourceStation == null) {
                System.out.print("\nEnter Source Station Name or ID: ");
                String sourceInput = scanner.nextLine().trim();

                if (sourceInput.isEmpty()) {
                    System.out.println("Error: Station name/ID cannot be empty.");
                    continue;
                }

                sourceStation = controller.getStationByName(sourceInput);
                if (sourceStation == null) {
                    sourceStation = controller.getStationById(sourceInput);
                }

                if (sourceStation == null) {
                    System.out.println("Error: Source station '" + sourceInput + "' not found. Please try again.");
                }
            }

            // Get sink station
            Node sinkStation = null;
            while (sinkStation == null) {
                System.out.print("\nEnter Sink Station Name or ID: ");
                String sinkInput = scanner.nextLine().trim();

                if (sinkInput.isEmpty()) {
                    System.out.println("Error: Station name/ID cannot be empty.");
                    continue;
                }

                if (sinkInput.equalsIgnoreCase(sourceStation.getName()) ||
                        sinkInput.equalsIgnoreCase(sourceStation.getNode_id())) {
                    System.out.println("Error: Sink station must be different from source station.");
                    continue;
                }

                sinkStation = controller.getStationByName(sinkInput);
                if (sinkStation == null) {
                    sinkStation = controller.getStationById(sinkInput);
                }

                if (sinkStation == null) {
                    System.out.println("Error: Sink station '" + sinkInput + "' not found. Please try again.");
                }
            }

            // Calculate max flow
            System.out.println("\nCalculating maximum flow using Edmonds-Karp algorithm...");
            MaxFlowResultDTO result = controller.calculateMaxFlow(sourceStation, sinkStation);

            // Display results
            displayMaxFlowResult(result);

        } catch (IllegalArgumentException e) {
            System.out.println("\nError: " + e.getMessage());
        } catch (IllegalStateException e) {
            System.out.println("\nError: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("\nUnexpected Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Displays a list of all available stations.
     *
     * @param stations the list of stations to display
     */
    private void displayStationsList(List<Node> stations) {
        int count = 0;
        for (Node station : stations) {
            System.out.printf("%-10s | %-45s", station.getNode_id(), station.getName());
            count++;
            if (count % 1 == 0) {
                System.out.println();
            }
        }
        if (count % 1 != 0) {
            System.out.println();
        }
    }

    /**
     * Displays the maximum flow result in a formatted table.
     *
     * @param result the MaxFlowResultDTO to display
     */
    private void displayMaxFlowResult(MaxFlowResultDTO result) {
        String sourceName = result.getSourceStation().getName();
        String sourceId = result.getSourceStation().getNode_id();
        String sinkName = result.getSinkStation().getName();
        String sinkId = result.getSinkStation().getNode_id();
        double maxFlow = result.getMaxFlowValue();
        String complexity = result.getTemporalComplexity();

        int totalWidth = 70;
        String line = "-".repeat(totalWidth);

        System.out.println("\n-----" + line + "-----");
        System.out.printf("%-68s\n", "MAXIMUM FLOW ANALYSIS RESULT");
        System.out.println("---" + line + "----");
        System.out.printf("\n", "Parameter", "Value");
        System.out.println("----------------------------------------------");

        // Source station
        System.out.printf("Source Station (ID)            %-35s\n",
                truncate(sourceId, 35));
        System.out.printf("Source Station (Name)          %-35s\n",
                truncate(sourceName, 35));

        System.out.println("----------------------------------------------");
        // Sink station
        System.out.printf("Sink Station (ID)              %-35s\n",
                truncate(sinkId, 35));
        System.out.printf("Sink Station (Name)            %-35s\n",
                truncate(sinkName, 35));

        System.out.println("---" + line + "---");

        // Maximum flow value
        System.out.printf("Maximum Throughput (trains/day) %-35.0f \n", maxFlow);

        System.out.println("----" + line + "----");

        // Additional information
        if (maxFlow == 0) {
            System.out.println("\n  Warning: No path exists between the source and sink stations,");
            System.out.println("    or all paths have zero capacity.");
        } else {
            System.out.println("\n Analysis complete! The maximum throughput represents the");
            System.out.println("   theoretical upper limit of trains that can travel from the");
            System.out.println("   source to the sink per day, considering track capacities.");
        }
    }

    /**
     * Truncates a string to a maximum length, adding "..." if needed.
     *
     * @param str the string to truncate
     * @param maxLength the maximum length
     * @return the truncated string
     */
    private String truncate(String str, int maxLength) {
        if (str.length() > maxLength) {
            return str.substring(0, maxLength - 3) + "...";
        }
        return str;
    }
}