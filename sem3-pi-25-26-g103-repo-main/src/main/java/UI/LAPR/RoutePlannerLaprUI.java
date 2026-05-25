package UI.LAPR;

import Controllers.LAPR.RouteController;
import Model.LAPR.Facility;
import Model.LAPR.Freight;
import Model.LAPR.Line;
import Model.LAPR.Route;
import Model.LAPR.Segment;

import java.util.List;
import java.util.Scanner;

public class RoutePlannerLaprUI implements Runnable {

    private final RouteController controller;
    private final Scanner scanner;

    public RoutePlannerLaprUI() {
        this.controller = new RouteController();
        this.scanner = new Scanner(System.in);
    }

    @Override
    public void run() {
        while (true) {
            System.out.println("\n=========================================");
            System.out.println("             Route Planner               ");
            System.out.println("=========================================");
            System.out.println("1. Create New Route (Select Stations Manually)");
            System.out.println("2. Create New Route (Automatic)");
            System.out.println("3. List Pending Freights");
            System.out.println("4. List Created Routes (In-Memory)");
            System.out.println("5. List All Lines");
            System.out.println("0. Exit");
            System.out.print("Select option: ");

            int option = readIntegerInput();

            switch (option) {
                case 1:
                    createManualRouteFlow();
                    break;
                case 2:
                    createAutomaticRouteFlow();
                    break;
                case 3:
                    listPendingFreights();
                    break;
                case 4:
                    listCreatedRoutes();
                    break;
                case 5:
                    listAllLines();
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    private void listAllLines() {
        List<Line> lines = controller.getAllLines();
        if (lines.isEmpty()) {
            System.out.println("\n[!] No lines found.");
        } else {
            System.out.println("\n--- Existing Railway Lines ---");
            for (Line l : lines) {
                System.out.printf("Line ID: %d | Name: %s | %s (%d) <-> %s (%d)\n",
                        l.getLineId(), l.getName(), l.getStartName(), l.getStartId(), l.getEndName(), l.getEndId());
            }
        }
    }

    private void listPendingFreights() {
        List<Freight> freights = controller.getPendingFreights();
        if (freights.isEmpty()) {
            System.out.println("\nNo pending freights found in Database.");
        } else {
            System.out.println("\n--- Pending Freights (Oracle DB) ---");
            for (Freight f : freights) {
                System.out.println(f);
            }
        }
    }

    private void createAutomaticRouteFlow() {
        // Reutilizamos a lógica de seleção de fretes atualizada
        List<Freight> selected = selectFreightsFlow();
        if (selected == null || selected.isEmpty()) return;

        Facility startFac = null;
        while (true) {
            System.out.println("\n--- Select Overall Route Start Station ---");
            List<Facility> available = controller.getAvailableFacilities();
            for (int i = 0; i < available.size(); i++) {
                System.out.printf("%d. %s\n", (i + 1), available.get(i).getName());
            }
            System.out.print("> Choose number (0 for default start): ");
            int startIdx = readIntegerInput();
            if (startIdx == 0) {
                startFac = null;
                break;
            } else if (startIdx > 0 && startIdx <= available.size()) {
                Facility selectedFac = available.get(startIdx - 1);
                if (controller.getFacilitiesConnectedTo(selectedFac).isEmpty()) {
                    System.out.println(" [!] This Station doesn't have line to connect. Select another one.");
                } else {
                    startFac = selectedFac;
                    break;
                }
            } else {
                System.out.println(" [!] Invalid option.");
            }
        }

        Facility endFac = null;
        while (true) {
            System.out.println("\n--- Select Overall Route End Station ---");
            List<Facility> available = controller.getAvailableFacilities();
            for (int i = 0; i < available.size(); i++) {
                System.out.printf("%d. %s\n", (i + 1), available.get(i).getName());
            }
            System.out.print("> Choose number (0 for default end): ");
            int endIdx = readIntegerInput();
            if (endIdx == 0) {
                endFac = null;
                break;
            } else if (endIdx > 0 && endIdx <= available.size()) {
                Facility selectedFac = available.get(endIdx - 1);
                if (controller.getFacilitiesConnectedTo(selectedFac).isEmpty()) {
                    System.out.println(" [!] Esta estação não tem linhas a conectar. Escolha outra.");
                } else {
                    endFac = selectedFac;
                    break;
                }
            } else {
                System.out.println(" [!] Invalid option.");
            }
        }

        System.out.print("\n> Enter Route Name: ");
        String name = scanner.nextLine();
        if (name.trim().isEmpty()) name = scanner.nextLine();

        System.out.println("Generating path and saving...");
        boolean success = controller.createAutomaticRoute(name, startFac, endFac);

        if (success) {
            System.out.println("SUCCESS! Automatic route created.");
        } else {
            System.out.println("ERROR: Could not find a path that satisfies all freights or failed to save.");
        }
    }

    private List<Freight> selectFreightsFlow() {
        System.out.println("\n>>> Select Freights to Transport");
        List<Freight> freights = controller.getPendingFreights();

        if (freights.isEmpty()) {
            System.out.println("No freights available.");
            return null;
        }

        boolean selecting = true;
        boolean hasSelection = false;

        while (selecting) {
            List<Freight> currentAvailable = controller.getPendingFreights();
            List<Freight> selectedInThisSession = controller.getSelectedFreights();
            currentAvailable.removeIf(f -> selectedInThisSession.stream().anyMatch(sf -> sf.getId() == f.getId()));

            if (currentAvailable.isEmpty() && !hasSelection) {
                System.out.println("No more freights available.");
                break;
            }

            System.out.println("\nAvailable Freights:");
            for (int i = 0; i < currentAvailable.size(); i++) {
                System.out.printf("%d. %s\n", (i + 1), currentAvailable.get(i).toString());
            }
            System.out.print("> Choose number to add (0 to next step): ");
            int idx = readIntegerInput();

            if (idx == 0) {
                selecting = false;
            } else if (idx > 0 && idx <= currentAvailable.size()) {
                Freight selected = currentAvailable.get(idx - 1);
                controller.addFreightToCurrentPlan(selected);
                System.out.println(" [OK] Freight #" + selected.getId() + " selected.");
                hasSelection = true;
            } else {
                System.out.println(" [!] Invalid option.");
            }
        }

        if (!hasSelection) {
            System.out.println("No freights selected. Aborting.");
            return null;
        }
        return controller.getSelectedFreights();
    }

    private void createManualRouteFlow() {
        // --- PASSO 1: Escolher Mercadorias ---
        if (selectFreightsFlow() == null) return;

        // --- PASSO 2: Escolher Estações (Caminho Manual) ---
        System.out.println("\n>>> STEP 2: Define Route Path (Station by Station)");

        boolean adding = true;
        int count = 1;

        while (adding) {
            Facility last = controller.getLastSelectedFacility();
            List<Facility> available;
            
            if (last == null) {
                System.out.println("\n--- Select Starting Facility ---");
                available = controller.getAvailableFacilities();
            } else {
                System.out.println("\n--- Select Facility #" + count + " (Connected to " + last.getName() + ") ---");
                available = controller.getFacilitiesConnectedTo(last);
                if (available.isEmpty()) {
                    System.out.println(" [!] This station doesn't have more connections.");
                    controller.removeLastFacilityFromSequence();
                    count--;
                    continue;
                }
            }

            for (int i = 0; i < available.size(); i++) {
                System.out.printf("%d. %s\n", (i + 1), available.get(i).getName());
            }

            System.out.print("> Choose number (0 to finish path): ");
            int idx = readIntegerInput();

            if (idx == 0) {
                if (count <= 2) {
                    System.out.println(" [!] Route requires at least 2 stations (Origin -> Destination).");
                } else if (!controller.validateAllFreightsInSequence()) {
                    System.out.println(" [!] Current path does not satisfy all selected freights in order.");
                    System.out.println("     Each freight must be satisfied sequentially (Freight N must end before Freight N+1 completes).");
                    System.out.println("     Selected Freights (Must follow this order):");
                    for (Freight f : controller.getSelectedFreights()) {
                        System.out.printf("      - Freight #%d: %s -> %s\n", f.getId(), f.getOrigin().getName(), f.getDestination().getName());
                    }
                } else {
                    adding = false;
                }
            } else if (idx > 0 && idx <= available.size()) {
                Facility fac = available.get(idx - 1);
                controller.addFacilityToSequence(fac);
                System.out.println(" [OK] Added to path: " + fac.getName());
                count++;
            } else {
                System.out.println(" [!] Invalid option.");
            }
        }

        // --- PASSO 3: Gravar ---
        System.out.print("\n> Enter Route Name: ");
        String name = scanner.nextLine();
        if (name.trim().isEmpty()) name = scanner.nextLine();

        System.out.println("Saving...");
        boolean success = controller.createAndSaveRoute(name);

        if (success) {
            System.out.println("SUCCESS!");
        } else {
            System.out.println("ERROR: Failed to save route.");
        }
    }

    private void listCreatedRoutes() {
        List<Route> routes = controller.getAllCreatedRoutes();

        if (routes.isEmpty()) {
            System.out.println("\n[!] No routes created yet.");
        } else {
            System.out.println("\n--- Created Routes Repository ---");
            for (Route r : routes) {
                System.out.println("------------------------------------------------");
                System.out.printf("Route ID: %d | Name: %s\n", r.getRouteId(), r.getRouteName());
                System.out.println("Path:");

                int step = 1;
                double totalDist = 0;
                for (Segment s : r.getSegments()) {
                    totalDist += s.getLength();
                    String startName = s.getStartStationName();
                    String endName = s.getEndStationName();

                    if (startName == null) startName = controller.getFacilityNameById(s.getStartStationId());
                    if (endName == null) endName = controller.getFacilityNameById(s.getEndStationId());

                    System.out.printf("  %d. %s -> %s (Line %d, Len: %.1f km, Electrified: %s)\n",
                            step++,
                            startName,
                            endName,
                            s.getLineID(),
                            s.getLength(),
                            s.getElectrified() ? "Yes" : "No");
                }
                System.out.printf("Total Distance: %.1f km\n", totalDist);
                System.out.print("Freights: ");
                List<Freight> fList = r.getFreights();
                for (int i = 0; i < fList.size(); i++) {
                    System.out.print("#" + fList.get(i).getId() + (i == fList.size() - 1 ? "" : ", "));
                }
                System.out.println();
            }
            System.out.println("------------------------------------------------");
        }
    }

    private int readIntegerInput() {
        try {
            String line = scanner.nextLine();
            return Integer.parseInt(line.trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}