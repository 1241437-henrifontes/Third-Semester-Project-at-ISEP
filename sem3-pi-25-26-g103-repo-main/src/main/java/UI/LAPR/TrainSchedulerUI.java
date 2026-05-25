package UI.LAPR;

import Repositories.LAPR.FacilityRepository;
import Repositories.LAPR.LineRepository;
import Repositories.LAPR.SegmentRepository;
import Controllers.LAPR.TrainSchedulerController;
import Model.LAPR.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * Simple command-line UI to interact with the TrainSchedulerController.
 */
public class TrainSchedulerUI implements Runnable {

    private final Scanner sc = new Scanner(System.in);
    private final TrainSchedulerController controller = new TrainSchedulerController();
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Override
    public void run() {
        System.out.println("=== Train Scheduler (USLP07) ===");
        while (true) {
            System.out.println("\n1) List available trains");
            System.out.println("2) List railway lines");
            System.out.println("3) Create route from line");
            System.out.println("4) Create custom route (select segments)");
            System.out.println("5) Dispatch trains and calculate schedules");
            System.out.println("6) Exit");
            System.out.print("Select option: ");
            String opt = sc.nextLine().trim();
            switch (opt) {
                case "1":
                    listTrains();
                    break;
                case "2":
                    listLines();
                    break;
                case "3":
                    createRouteFromLine();
                    break;
                case "4":
                    createCustomRouteFlow();
                    break; // Faltava o break aqui
                case "5":
                    dispatchTrainsFlow();
                    break;
                case "6":
                    System.out.println("Exiting scheduler.");
                    return;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    private void listTrains() {
        List<Train> trains = controller.getAvailableTrains();
        if (trains.isEmpty()) {
            System.out.println("No trains available.");
            return;
        }
        System.out.println("Available trains:");
        for (int i = 0; i < trains.size(); i++) {
            System.out.printf("%d) %s\n", i + 1, trains.get(i));
        }
    }

    private void listLines() {
        List<Line> lines = controller.getLines();
        System.out.println("Lines:");
        for (int i = 0; i < lines.size(); i++) {
            Line l = lines.get(i);
            System.out.printf("%d) %s (id=%d) start=%s end=%s gauge=%d\n",
                    i + 1, l.getName(), l.getLineId(), l.getStartId(), l.getEndId(), l.getGauge());
        }
    }

    private void createRouteFromLine() {
        listLines();
        System.out.print("Enter line id to create route from: ");
        String in = sc.nextLine().trim();
        try {
            int lineId = Integer.parseInt(in);
            System.out.print("Enter route name: ");
            String name = sc.nextLine().trim();
            Route r = controller.createManualRoute(name, lineId);
            System.out.println("Created route: " + r);
        } catch (NumberFormatException ex) {
            System.out.println("Invalid line id");
        }
    }

    private void createCustomRouteFlow() {
        List<Segment> allSegments = SegmentRepository.getInstance().getSegments();
        System.out.println("Available segments:");
        for (int i = 0; i < allSegments.size(); i++) {
            Segment s = allSegments.get(i);
            Line line = LineRepository.getInstance().getLines().stream()
                    .filter(l -> l.getLineId() == s.getLineID()).findFirst().orElse(null);
            System.out.printf("%d) Segment %d (Line: %s) - %s to %s%n",
                    i + 1, s.getOrder(),
                    line != null ? line.getName() : "Unknown",
                    line.getStartId(),
                    line.getEndName());
        }

        System.out.print("Enter route name: ");
        String routeName = sc.nextLine().trim();

        System.out.print("Enter segment IDs to include (comma separated): ");
        String input = sc.nextLine().trim();
        List<Integer> segmentIds = Arrays.stream(input.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(Integer::parseInt)
                .collect(Collectors.toList());

        Route route = controller.createCustomRoute(routeName, segmentIds);
        System.out.println("Created custom route: " + route);
    }

    /**
     * Main flow: dispatch one or more trains with full schedule calculation.
     * Includes advanced speed calculation (weight, power, track limits) and crossing detection.
     */
    private void dispatchTrainsFlow() {
        List<Train> trains = controller.getAvailableTrains();
        if (trains.isEmpty()) {
            System.out.println("No trains available.");
            return;
        }

        List<Route> routes = controller.getAvailableRoutes();
        if (routes.isEmpty()) {
            System.out.println("No routes available. Create routes first (option 3 or 4).");
            return;
        }

        System.out.print("\nHow many trains to dispatch? ");
        String in = sc.nextLine().trim();
        int count;
        try {
            count = Integer.parseInt(in);
            if (count < 1 || count > trains.size()) {
                System.out.println("Invalid count.");
                return;
            }
        } catch (NumberFormatException ex) {
            System.out.println("Invalid number.");
            return;
        }

        Map<Train, Route> trainRouteMap = new java.util.HashMap<>();

        // Assign route to each train
        for (int i = 0; i < count; i++) {
            System.out.printf("\n--- Configure Train %d/%d ---\n", i + 1, count);

            System.out.println("Available trains:");
            for (int j = 0; j < trains.size(); j++) {
                Train t = trains.get(j);
                System.out.printf("%d) %s (weight=%.0f kg, power=%d kW)\n",
                        j + 1, t.getTrainId(), t.getTotalWeightKg(), t.getLocomotive().getPower());
            }
            System.out.print("Select train: ");
            int tIdx = readIndex(trains.size());
            Train train = trains.get(tIdx);

            System.out.println("\nAvailable routes:");
            for (int j = 0; j < routes.size(); j++) {
                Route r = routes.get(j);
                System.out.printf("%d) %s (%d segments)\n", j + 1, r.getRouteName(), r.getSegments().size());
            }
            System.out.print("Select route: ");
            int rIdx = readIndex(routes.size());
            Route route = routes.get(rIdx);

            trainRouteMap.put(train, route);
        }

        // Get departure time
        System.out.print("\nEnter departure time for first train (yyyy-MM-dd HH:mm): ");
        String dt = sc.nextLine().trim();
        LocalDateTime startTime;
        try {
            startTime = LocalDateTime.parse(dt, dtf);
        } catch (Exception ex) {
            System.out.println("Invalid format. Using current time.");
            startTime = LocalDateTime.now();
        }

        // Get interval between trains
        int interval = 15;
        if (count > 1) {
            System.out.print("Enter departure interval between trains (minutes): ");
            try {
                interval = Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException ex) {
                System.out.println("Invalid. Using 15 minutes.");
            }
        }

        // DISPATCH TRAINS
        System.out.println("\n" + "=".repeat(70));
        System.out.println("CALCULATING SCHEDULES");
        System.out.println("- Considering: train weight, locomotive power, track speed limits");
        System.out.println("- Detecting: potential train crossings on single-track segments");
        System.out.println("=".repeat(70));

        // Primeiro calcular os schedules (esta linha estava faltando)
        List<TrainSchedule> schedules = controller.dispatchMultipleTrains(trainRouteMap, startTime, interval);

        // Display schedules
        for (int i = 0; i < schedules.size(); i++) {
            TrainSchedule sched = schedules.get(i);
            System.out.printf("\n--- SCHEDULE FOR TRAIN: %s ---\n", sched.getTrain().getTrainId());
            System.out.printf("Route: %s\n", sched.getRoute().getRouteName());
            System.out.printf("Departure: %s\n", sched.getDepartureTime().format(dtf));
            System.out.printf("Train weight: %.0f kg | Locomotive power: %d kW\n",
                    sched.getTrain().getTotalWeightKg(),
                    sched.getTrain().getLocomotive().getPower());

            System.out.println("\nEstimated passage times:");
            System.out.println("-".repeat(70));

            for (SchedulePoint sp : sched.getSchedulePoints()) {
                String facilityName = sp.getFacility() != null ? sp.getFacility().getName() : "Unknown Station";
                String crossingMark = sp.isRequiresCrossing() ? " >>> CROSSING OPERATION REQUIRED <<<" : "";

                System.out.printf("%-30s | Arrival: %s | Depart: %s%s\n",
                        facilityName,
                        sp.getEstimatedArrival().format(dtf),
                        sp.getEstimatedDeparture().format(dtf),
                        crossingMark);
            }
        }

        // Detect crossings - AGORA SIM A VARIÁVEL SCHEDULES EXISTE!
        List<Crossing> crossings = controller.detectCrossings(schedules);

        System.out.println("\n" + "=".repeat(70));
        if (!crossings.isEmpty()) {
            System.out.println("CROSSING OPERATIONS DETECTED: " + crossings.size());
            System.out.println("=".repeat(70));

            for (int i = 0; i < crossings.size(); i++) {
                Crossing c = crossings.get(i);
                Facility location = c.getLocation();

                System.out.printf("\n[Crossing %d]%n", i + 1);
                System.out.printf("  Location: %s %s%n",
                        location != null ? location.getName() : "Unknown",
                        location != null && location.hasSidings() ?
                                String.format("(has %d sidings - no delay needed)", location.getNumberOfSidings()) :
                                "(no sidings available)");

                System.out.printf("  Time: %s%n", c.getCrossingTime().format(dtf));
                System.out.printf("  Trains: %s <-> %s%n",
                        c.getTrain1().getTrain().getTrainId(),
                        c.getTrain2().getTrain().getTrainId());

                // Mostrar detalhes do atraso aplicado
                for (SchedulePoint sp : c.getTrain2().getSchedulePoints()) {
                    if (sp.isRequiresCrossing() && sp.getFacility() != null &&
                            sp.getFacility().getStationId().equals(location.getStationId())) {
                        long delay = java.time.Duration.between(
                                sp.getEstimatedDeparture().minusSeconds(300), // Tempo original
                                sp.getEstimatedDeparture()
                        ).getSeconds();

                        if (delay > 0) {
                            System.out.printf("  Action: Train %s delayed by %d seconds%n",
                                    c.getTrain2().getTrain().getTrainId(), delay);
                        }
                    }
                }
            }
        } else {
            System.out.println("NO CROSSING CONFLICTS - All trains can proceed without delays");
            System.out.println("=".repeat(70));
        }

        System.out.println("\n>>> Schedules calculated successfully <<<\n");
    }

    private int readIndex(int size) {
        while (true) {
            String in = sc.nextLine().trim();
            try {
                int idx = Integer.parseInt(in) - 1;
                if (idx >= 0 && idx < size) return idx;
            } catch (NumberFormatException ignored) {}
            System.out.print("Invalid selection. Try again: ");
        }
    }
}