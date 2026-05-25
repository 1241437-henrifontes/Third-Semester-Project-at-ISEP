package UI.LAPR;

import Controllers.LAPR.SchedulerController;
import Model.LAPR.*;
import Repositories.LAPR.TrainRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

public class SchedulerUI implements Runnable {

    private final SchedulerController controller;
    private final Scanner scanner;
    private final DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-M-d HH:mm:ss");
    private final DateTimeFormatter[] parseFormatters = new DateTimeFormatter[] {
            DateTimeFormatter.ofPattern("yyyy-M-d HH:mm:ss"),
            DateTimeFormatter.ofPattern("yyyy-M-d HH:mm")
    };

    private static class DispatchItem {
        Train train;
        Route route;
        LocalDateTime departure;
        LocalDateTime arrival;

        DispatchItem(Train t, Route r, LocalDateTime d, LocalDateTime a) {
            this.train = t;
            this.route = r;
            this.departure = d;
            this.arrival = a;
        }
    }

    public SchedulerUI() {
        this.controller = new SchedulerController();
        this.scanner = new Scanner(System.in);
    }

    @Override
    public void run() {
        List<DispatchItem> dispatchList = new ArrayList<>();

        System.out.println("\n--- Train Scheduler Dispatch ---");


        while (true) {
            System.out.println("\nSelected Items in Dispatch List: " + dispatchList.size());
            System.out.println("1. Add Train/Route to Dispatch List");
            System.out.println("2. Generate and View Schedule");
            System.out.println("0. Exit");
            System.out.print("Select option: ");

            String opt = scanner.nextLine();
            if (opt.equals("1")) {
                addTrainFlow(dispatchList);
            } else if (opt.equals("2")) {
                if (dispatchList.isEmpty()) {
                    System.out.println("Please select at least one train.");
                } else {
                    generateAndShowSchedule(dispatchList);
                    for (DispatchItem item : dispatchList) {
                        TrainRepository.getInstance().removeTrain(item.train);
                    }
                    dispatchList.clear();
                }
            } else if (opt.equals("0")) {
                break;
            }
        }
    }

    private void addTrainFlow( List<DispatchItem> dispatchList) {

        System.out.println("\nAvailable Routes:");
        List<Route> availableRoutes = controller.getAvailableRoutes();
        if (availableRoutes.isEmpty()) {
            System.out.println("No routes available. Please create a route first in Route Planner.");
            return;
        }
        for (int i = 0; i < availableRoutes.size(); i++) {
            System.out.println((i + 1) + ". " + availableRoutes.get(i).getRouteName());
        }

        Route route = null;
        while (route == null) {
            System.out.print("Select route (0 to cancel): ");
            String sel = scanner.nextLine().trim();
            if (sel.isEmpty()) {
                System.out.println("Selection cannot be empty. Please enter a number.");
                continue;
            }
            int rIdx;
            try {
                rIdx = Integer.parseInt(sel) - 1;
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Please enter the route number.");
                continue;
            }
            if (rIdx < 0) {
                System.out.println("Operation cancelled by user.");
                return;
            }
            if (rIdx >= availableRoutes.size()) {
                System.out.println("Number out of range. Pick a number between 1 and " + availableRoutes.size());
                continue;
            }
            route = availableRoutes.get(rIdx);
        }

        List<Train> trains = TrainRepository.getInstance().getTrainsByRouteId(route.getRouteId());

        System.out.println("\nAvailable Trains:");
        if (trains.isEmpty()) {
            System.out.println("No trains available. Please assemble and assign a Train to this route.");
            return;
        }
        for (int i = 0; i < trains.size(); i++) {
            System.out.println((i + 1) + ". " + trains.get(i).getTrainId());
        }

        Train train = null;
        while (train == null) {
            System.out.print("Select train (0 to cancel): ");
            String sel = scanner.nextLine().trim();
            if (sel.isEmpty()) {
                System.out.println("Selection cannot be empty. Please enter a number.");
                continue;
            }
            int rIdx;
            try {
                rIdx = Integer.parseInt(sel) - 1;
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Please enter the train number.");
                continue;
            }
            if (rIdx < 0) {
                System.out.println("Operation cancelled by user.");
                return;
            }
            if (rIdx >= trains.size()) {
                System.out.println("Number out of range. Pick a number between 1 and " + trains.size());
                continue;
            }
            train = trains.get(rIdx);
        }

        LocalDateTime departure = null;

        while (departure == null) {
            System.out.print("Enter departure time (Press Enter to Default '2026-01-01 12:00'): ");
            String dateStr = scanner.nextLine().trim();

            if (dateStr.isEmpty()) {
                departure = LocalDateTime.of(2026, 1, 1, 12, 0);
                continue;
            }
            for (DateTimeFormatter df : parseFormatters) {
                try {
                    departure = LocalDateTime.parse(dateStr, df);
                } catch (DateTimeParseException ignored) {
                }
            }

            if (departure == null) {
                System.out.println("Invalid date/time format. Use yyyy-M-d HH:mm or yyyy-M-d HH:mm:ss (e.g. 2025-1-1 12:00 or 2025-1-1 12:00:00)");
            }
        }


        LocalDateTime arrival = controller.getEstimatedArrivalTime(train, route, departure);

        List<Train> currentTrains = new ArrayList<>();
        List<LocalDateTime> currentDeps = new ArrayList<>();
        List<LocalDateTime> currentArrs = new ArrayList<>();
        for(DispatchItem item : dispatchList) {
            currentTrains.add(item.train);
            currentDeps.add(item.departure);
            currentArrs.add(item.arrival);
        }

        if (controller.isTrainOccupied(train.getTrainId(), departure, arrival, currentTrains, currentDeps, currentArrs)) {
            System.out.println("\n[!] ERROR: Train " + train.getTrainId() + " is already scheduled during this period.");
            System.out.println("New requested period: " + departure.format(outputFormatter) + " to " + arrival.format(outputFormatter));
            return;
        }

        dispatchList.add(new DispatchItem(train, route, departure, arrival));
        System.out.println("[OK] Added to dispatch list.");
    }

    private void generateAndShowSchedule(List<DispatchItem> dispatchList) {
        List<Train> trains = new ArrayList<>();
        List<Route> routes = new ArrayList<>();
        List<LocalDateTime> departures = new ArrayList<>();

        for (DispatchItem item : dispatchList) {
            trains.add(item.train);
            routes.add(item.route);
            departures.add(item.departure);
        }
        
        List<TrainSchedule> schedules = controller.generateSchedules(trains, routes, departures);
        
        List<String> warnings = controller.getConflictWarnings();
        if (!warnings.isEmpty()) {
            System.out.println("\n--- CONFLICT WARNINGS ---");
            for (String w : warnings) {
                System.out.println("[!] " + w);
            }
        }

        for (TrainSchedule s : schedules) {
            System.out.println("\nSchedule for Train: " + s.getTrain().getTrainId() + " (Route: " + s.getRoute().getRouteName() + ")");
            List<SchedulePoint> points = s.getSchedulePoints();
            for (int i = 0; i < points.size(); i++) {
                SchedulePoint p = points.get(i);
                if (i == 0) {
                    System.out.printf("- %s | Dep: %s %s\n",
                            p.getFacility().getName(),
                            p.getEstimatedDeparture().format(outputFormatter),
                            p.isRequiresCrossing() ? "[CROSSING OPERATION]" : ""
                    );
                } else if (i == points.size()-1) {
                    System.out.printf("- %s | Arr: %s %s\n",
                            p.getFacility().getName(),
                            p.getEstimatedDeparture().format(outputFormatter),
                            p.isRequiresCrossing() ? "[CROSSING OPERATION]" : ""
                    );
                }else {
                    System.out.printf("- %s | Arr: %s | Dep: %s %s\n",
                            p.getFacility().getName(),
                            p.getEstimatedArrival().format(outputFormatter),
                            p.getEstimatedDeparture().format(outputFormatter),
                            p.isRequiresCrossing() ? "[CROSSING OPERATION]" : ""
                    );
                }
            }
        }
    }
}
