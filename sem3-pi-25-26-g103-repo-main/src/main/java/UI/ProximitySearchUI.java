package UI;

import Controllers.ProximitySearchController;
import Model.StationDistance;
import Model.TimeZoneGroup;
import Model.Filters.StationFilter;
import Services.NearestNeighborSearcher;

import java.util.List;
import java.util.Scanner;

/**
 * User Interface for USEI09 - Proximity Search (Nearest-N with Filters).
 * Allows users to search for the nearest N railway stations to a given coordinate.
 */
public class ProximitySearchUI implements Runnable {

    private final ProximitySearchController controller;
    private final Scanner scanner;

    public ProximitySearchUI() {
        this.controller = new ProximitySearchController();
        this.scanner = new Scanner(System.in);
    }

    @Override
    public void run() {
        System.out.println("\n-----------------------------------------------------------");
        System.out.println("|  USEI09 - Proximity Search (Nearest-N with Filters)       |");
        System.out.println("------------------------------------------------------------\n");

        try {
            // Get target coordinates
            double targetLat = requestCoordinate("Enter target LATITUDE (-90 to 90): ", -90, 90);
            double targetLon = requestCoordinate("Enter target LONGITUDE (-180 to 180): ", -180, 180);

            // Get number of neighbors
            int n = requestPositiveInteger("How many nearest stations do you want to find? ");

            // Ask if user wants to apply filters
            StationFilter filter = null;
            if (requestBoolean("Do you want to apply filters? (y/n): ")) {
                filter = requestFilters();
            }

            // Ask if user wants detailed metrics
            boolean showMetrics = requestBoolean("Show performance metrics? (y/n): ");

            // Perform search
            System.out.println("\nSearching for nearest stations...\n");

            if (showMetrics) {
                searchWithMetrics(targetLat, targetLon, n, filter);
            } else {
                searchBasic(targetLat, targetLon, n, filter);
            }

        } catch (IllegalStateException e) {
            System.err.println("\nError: " + e.getMessage());
            System.err.println("Please ensure that the KD-tree has been built and stations are loaded.");
        } catch (Exception e) {
            System.err.println("\nAn error occurred: " + e.getMessage());
        }
    }

    private void searchBasic(double targetLat, double targetLon, int n, StationFilter filter) {
        List<StationDistance> results = controller.findNearestStations(targetLat, targetLon, n, filter);
        displayResults(results, targetLat, targetLon);
    }

    private void searchWithMetrics(double targetLat, double targetLon, int n, StationFilter filter) {
        NearestNeighborSearcher.SearchResult result =
            controller.findNearestStationsWithMetrics(targetLat, targetLon, n, filter);

        displayResults(result.getStations(), targetLat, targetLon);

        // Display metrics
        System.out.println("\n" + "═".repeat(60));
        System.out.println("PERFORMANCE METRICS");
        System.out.println("=".repeat(60));
        System.out.printf("Nodes visited: %d%n", result.getNodesVisited());
        System.out.printf("Search duration: %.3f ms%n", result.getDurationMs());
        System.out.println("=".repeat(60));
    }

    private void displayResults(List<StationDistance> results, double targetLat, double targetLon) {
        if (results.isEmpty()) {
            System.out.println("\nNo stations found matching the criteria.");
            return;
        }

        System.out.println("\n" + "=".repeat(80));
        System.out.printf("Target Coordinates: (%.4f, %.4f)%n", targetLat, targetLon);
        System.out.printf("Found %d nearest station(s):%n", results.size());
        System.out.println("=".repeat(80));

        int rank = 1;
        for (StationDistance sd : results) {
            System.out.printf("%n%d. %s%n", rank++, sd.getStation().getName());
            System.out.printf("-Distance: %.2f km%n", sd.getDistance());
            System.out.printf("-Coordinates: (%.4f, %.4f)%n",
                sd.getStation().getLatitude(), sd.getStation().getLongitude());
            System.out.printf("-Country: %s%n", sd.getStation().getCountry());
            System.out.printf("-Timezone: %s%n", sd.getStation().getTimeZoneGroup());

            if (sd.getStation().isCity()) System.out.println("City Station");
            if (sd.getStation().isMainStation()) System.out.println("Main Station");
            if (sd.getStation().isAirport()) System.out.println("Airport");
        }

        System.out.println("\n" + "=".repeat(80));
    }

    private StationFilter requestFilters() {
        StationFilter filter = new StationFilter();
        boolean hasFilter = false;

        // Timezone filter
        if (requestBoolean("\nFilter by timezone? (y/n): ")) {
            System.out.println("\nAvailable timezones:");
            for (TimeZoneGroup tz : TimeZoneGroup.values()) {
                System.out.printf("  %d. %s%n", tz.ordinal() + 1, tz.getName());
            }

            int minIndex = requestInteger("Enter minimum timezone number (1-4): ", 1, 4) - 1;
            int maxIndex = requestInteger("Enter maximum timezone number (1-4): ", 1, 4) - 1;

            TimeZoneGroup minTz = TimeZoneGroup.values()[minIndex];
            TimeZoneGroup maxTz = TimeZoneGroup.values()[maxIndex];

            filter.withTimeZoneRange(minTz, maxTz);
            hasFilter = true;
        }

        // City filter
        if (requestBoolean("\nFilter by city stations? (y/n): ")) {
            boolean isCity = requestBoolean("Show only city stations? (y/n): ");
            filter.withIsCity(isCity);
            hasFilter = true;
        }

        // Main station filter
        if (requestBoolean("\nFilter by main stations? (y/n): ")) {
            boolean isMainStation = requestBoolean("Show only main stations? (y/n): ");
            filter.withIsMainStation(isMainStation);
            hasFilter = true;
        }

        // Airport filter
        if (requestBoolean("\nFilter by airports? (y/n): ")) {
            boolean isAirport = requestBoolean("Show only airports? (y/n): ");
            filter.withIsAirport(isAirport);
            hasFilter = true;
        }

        return hasFilter ? filter : null;
    }

    private double requestCoordinate(String message, double min, double max) {
        while (true) {
            System.out.print(message);
            try {
                double value = Double.parseDouble(scanner.nextLine().trim());
                if (value >= min && value <= max) {
                    return value;
                }
                System.out.printf("Value must be between %.2f and %.2f%n", min, max);
            } catch (NumberFormatException e) {
                System.out.println("Invalid number format. Please try again.");
            }
        }
    }

    private int requestPositiveInteger(String message) {
        while (true) {
            System.out.print(message);
            try {
                int value = Integer.parseInt(scanner.nextLine().trim());
                if (value > 0) {
                    return value;
                }
                System.out.println("Value must be greater than 0.");
            } catch (NumberFormatException e) {
                System.out.println("Invalid number format. Please try again.");
            }
        }
    }

    private int requestInteger(String message, int min, int max) {
        while (true) {
            System.out.print(message);
            try {
                int value = Integer.parseInt(scanner.nextLine().trim());
                if (value >= min && value <= max) {
                    return value;
                }
                System.out.printf("Value must be between %d and %d%n", min, max);
            } catch (NumberFormatException e) {
                System.out.println("Invalid number format. Please try again.");
            }
        }
    }

    private boolean requestBoolean(String message) {
        while (true) {
            System.out.print(message);
            String input = scanner.nextLine().trim().toLowerCase();
            if (input.equals("y") || input.equals("yes")) {
                return true;
            } else if (input.equals("n") || input.equals("no")) {
                return false;
            }
            System.out.println("Please enter 'y' or 'n'.");
        }
    }
}
