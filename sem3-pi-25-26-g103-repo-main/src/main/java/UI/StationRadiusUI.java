package UI;

import Controllers.StationRadiusController;
import Services.DTO.RadiusSearchResultDTO;
import Model.StationDistanceResult;
import java.util.Scanner;

public class StationRadiusUI implements Runnable {

    private final StationRadiusController controller;

    public StationRadiusUI() {
        this.controller = new StationRadiusController();
    }

    @Override
    public void run() {
        System.out.println("\n--- USEI10: Radius Search ---");
        Scanner scanner = new Scanner(System.in);

        try {
            System.out.print("Enter Latitude (e.g., 41.1496): ");
            double lat = Double.parseDouble(scanner.nextLine().replace(",", "."));

            System.out.print("Enter Longitude (e.g., -8.6109): ");
            double lon = Double.parseDouble(scanner.nextLine().replace(",", "."));

            System.out.print("Enter Radius in KM (e.g., 10): ");
            double radius = Double.parseDouble(scanner.nextLine().replace(",", "."));

            // Executa a pesquisa
            RadiusSearchResultDTO data = controller.findStationsInRadius(lat, lon, radius);

            displayResults(data);

        } catch (NumberFormatException e) {
            System.out.println("Error: Invalid number format. Please ensure you enter numbers.");
        } catch (Exception e) {
            System.out.println("Unexpected Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void displayResults(RadiusSearchResultDTO data) {
        var tree = data.getResultsTree();
        var summary = data.getSummary();

        System.out.println("\n=== SUMMARY ===");
        System.out.println("By Country: " + summary.get("byCountry"));
        System.out.println("By Type: " + summary.get("byCityStatus"));

        System.out.println("\n=== STATIONS FOUND (" + tree.size() + ") ===");
        if (tree.isEmpty()) {
            System.out.println("(No stations found)");
        } else {
            for (StationDistanceResult res : tree.inOrder()) {
                System.out.printf("- %s -> %.2f km (%s)\n",
                        res.getStation().getName(),
                        res.getDistance(),
                        res.getStation().getCountry());
            }
        }
        System.out.println("-----------------------------");
    }
}