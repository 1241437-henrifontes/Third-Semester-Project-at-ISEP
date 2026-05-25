package UI.LAPR;

import Controllers.LAPR.TrainAssemblyController;
import Model.LAPR.*;
import Repositories.LAPR.TrainRepository;

import java.util.*;

public class TrainAssemblyUI implements Runnable {

    private final TrainAssemblyController controller;
    private final Scanner scanner;

    public TrainAssemblyUI() {
        this.controller = new TrainAssemblyController();
        this.scanner = new Scanner(System.in);
    }

    @Override
    public void run() {
        while (true) {
            System.out.println("\n=========================================");
            System.out.println("           TRAIN ASSEMBLY MENU            ");
            System.out.println("=========================================");
            System.out.println("1. Assemble Train");
            System.out.println("2. Display Status");
            System.out.println("0. Exit");
            System.out.print("Select option: ");

            int option = readInt();
            switch (option) {
                case 1 -> assembleTrain();
                case 2 -> statusDisplay();
                case 0 -> { return; }
                default -> System.out.println("Invalid option.");
            }
        }
    }


    private void statusDisplay() {

        System.out.println("\n" + "=".repeat(80));
        System.out.println("ROLLING STOCK STATUS (ALL)");
        System.out.println("=".repeat(80));

        List<RollingStockDTO> locomotives =
                controller.getAllLocomotivesForStatus(0);
        List<RollingStockDTO> wagons =
                controller.getAllWagonsForStatus(0);

        List<Train> trains = TrainRepository.getInstance().getAllTrains();

        Set<Integer> locosInRoute = new HashSet<>();
        Set<String> wagonsInRoute = new HashSet<>();

        for (Train t : trains) {
            if (t.getAssignedRoute() != null) {
                if (t.getLocomotive() != null) {
                    locosInRoute.add(t.getLocomotive().getNumberOfLocomotives());
                }
                if (t.getWagons() != null) {
                    for (RailwayWagon w : t.getWagons()) {
                        wagonsInRoute.add(w.getWagonId());
                    }
                }
            }
        }

        System.out.println("\nLocomotives:");
        printLocoHeader();
        int i = 1;
        for (RollingStockDTO l : locomotives) {
            RollingStockStatus status =
                    locosInRoute.contains(l.getId())
                            ? RollingStockStatus.IN_TRANSIT
                            : l.getStatus();
            System.out.printf("%3d - %-6d %-25s %-12s%n",
                    i++, l.getId(),
                    truncate(l.getName(), 25),
                    statusName(status));
        }

        System.out.println("\nWagons:");
        printWagonHeader();
        i = 1;
        for (RollingStockDTO w : wagons) {
            RollingStockStatus status =
                    wagonsInRoute.contains(w.getName())
                            ? RollingStockStatus.IN_TRANSIT
                            : w.getStatus();
            System.out.printf("%3d - %-18s %-25s %-12s%n",
                    i++,
                    truncate(w.getName(), 18),
                    truncate(w.getRollingStockType(), 25),
                    statusName(status));
        }
    }

    private void assembleTrain() {

        System.out.println("\n" + "=".repeat(80));
        System.out.println("TRAIN ASSEMBLY");
        System.out.println("=".repeat(80));

        Route route = selectRoute();
        if (route == null) return;

        List<Integer> locoIds = selectLocomotive();
        if (locoIds.isEmpty()) return;

        List<String> wagonIds = selectWagons();

        System.out.print("\nEnter Train ID: ");
        int trainId = readInt();

        Train train = controller.assembleAndAssignTrain(
                trainId, locoIds, wagonIds, route);

        if (train != null) {
            System.out.println("\n✓ Train assembled successfully!");
            displayTrainDetails(train);
        } else {
            System.out.println("\n✗ Failed to assemble train.");
        }
    }

    private Route selectRoute() {
        List<Route> routes = controller.getAllRoutes();
        if (routes.isEmpty()) {
            System.out.println("No routes available.");
            return null;
        }

        System.out.println("\nAvailable Routes:");
        for (int i = 0; i < routes.size(); i++) {
            System.out.printf("%2d. %s%n", i + 1, routes.get(i).getRouteName());
        }

        System.out.print("Select route (0 cancel): ");
        int opt = readInt();
        if (opt <= 0 || opt > routes.size()) return null;

        return routes.get(opt - 1);
    }

    private List<Integer> selectLocomotive() {

        List<RollingStockDTO> locomotives =
                controller.getAvailableLocomotivesForRoute(0);

        if (locomotives.isEmpty()) {
            System.out.println("No free locomotives available.");
            return List.of();
        }

        System.out.println("\nAvailable Locomotives (select one by number):");
        String locoFormat = "%4s - %-12s %-15s %-12s %6s";
        String locoHeader = String.format(
                locoFormat, "#", "ID", "Name", "Type", "Gauge");
        System.out.println(locoHeader);
        System.out.println("-".repeat(locoHeader.length()));

        for (int i = 0; i < locomotives.size(); i++) {
            RollingStockDTO loco = locomotives.get(i);
            System.out.println(String.format(
                    locoFormat,
                    i + 1,
                    loco.getId(),
                    truncate(loco.getName(), 15),
                    truncate(loco.getRollingStockType(), 12),
                    loco.getGaugeId()));
        }

        System.out.print("Select locomotive (0 cancel): ");
        int opt = readInt();
        if (opt <= 0 || opt > locomotives.size()) return List.of();

        return List.of(locomotives.get(opt - 1).getId());
    }

    private List<String> selectWagons() {

        List<RollingStockDTO> wagons =
                controller.getAvailableWagonsForRoute(0);

        List<String> selected = new ArrayList<>();

        if (wagons.isEmpty()) {
            System.out.println("No free wagons available.");
            return selected;
        }

        System.out.println("\nAvailable Wagons (pick numbers; enter 0 to finish):");
        String wagonFormat = "%4s - %-18s %-18s %-15s %6s";
        String wagonHeader = String.format(
                wagonFormat, "#", "Wagon ID", "Type", "Make", "Gauge");
        System.out.println(wagonHeader);
        System.out.println("-".repeat(wagonHeader.length()));

        for (int i = 0; i < wagons.size(); i++) {
            RollingStockDTO wagon = wagons.get(i);
            System.out.println(String.format(
                    wagonFormat,
                    i + 1,
                    truncate(wagon.getName(), 18),
                    truncate(wagon.getRollingStockType(), 18),
                    truncate(wagon.getMake(), 15),
                    wagon.getGaugeId()));
        }

        while (true) {
            System.out.print("Add wagon (0 finish): ");
            int opt = readInt();
            if (opt == 0) break;
            if (opt < 1 || opt > wagons.size()) continue;
            selected.add(wagons.get(opt - 1).getName());
        }

        return selected;
    }

    private int readInt() {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (Exception e) {
            return -1;
        }
    }

    private String truncate(String s, int len) {
        if (s == null) return "";
        return s.length() <= len ? s : s.substring(0, len - 3) + "...";
    }

    private String statusName(RollingStockStatus s) {
        if (s == null) return "-";
        return s == RollingStockStatus.IN_TRANSIT ? "in transit" : "parked";
    }

    private void printLocoHeader() {
        System.out.printf("%3s - %-6s %-25s %-12s%n",
                "#", "ID", "Name", "Status");
        System.out.println("-".repeat(55));
    }

    private void printWagonHeader() {
        System.out.printf("%3s - %-18s %-25s %-12s%n",
                "#", "Wagon ID", "Type", "Status");
        System.out.println("-".repeat(65));
    }

    private void displayTrainDetails(Train t) {
        System.out.println("\nTrain ID: " + t.getTrainId());
        System.out.println("Route: " + t.getAssignedRoute().getRouteName());
        System.out.println("Locomotive: " + t.getLocomotive().getLocomotiveName());
        System.out.println("Wagons:");
        for (RailwayWagon w : t.getWagons()) {
            System.out.println(" - " + w.getWagonId());
        }
    }
}
