package UI.LAPR;

import Controllers.LAPR.TravelTimeController;
import Model.LAPR.Facility;
import Model.LAPR.Line;
import Model.LAPR.Locomotive;
import Model.LAPR.Segment;

import java.util.List;
import java.util.Scanner;

/**
 * UI to estimate travel time along a selected line using a compatible locomotive.
 */
public class TravelTimeUI implements Runnable {

    private final Scanner sc = new Scanner(System.in);
    private TravelTimeController controller;

    /**
     * Runs the travel time estimator workflow.
     */
    @Override
    public void run() {
        controller = new TravelTimeController();
        System.out.println("--- Travel Time Estimator ---");

        Facility departure = selectFacility();
        if (departure == null) return;

        Line line = selectLineFromFacility(departure);
        if (line == null) return;

        Locomotive loco = selectLocomotiveForLine(line);
        if (loco == null) return;

        List<Segment> segments = controller.getSegmentsForLineOrdered(line.getLineId());
        if (segments.isEmpty()) {
            System.out.println("No segments found for the selected line.");
            return;
        }

        double totalSeconds = 0.0;
        int idx = 1;
        for (Segment s : segments) {
            double seconds = Locomotive.calculateEstimatedTimeTravel(s, loco);
            totalSeconds += seconds;
            System.out.printf("Segment %d (ID %d, length %dm): %.2f seconds%n", idx++, s.getOrder(), s.getLength(), seconds );
        }
        System.out.printf("\nTotal estimated time: %.2f seconds \n", totalSeconds);
    }

    /**
     * Prompts the user to select a departure facility.
     * @return selected Facility or null if none available
     */
    private Facility selectFacility() {
        List<Facility> facilities = controller.getFacilities();
        if (facilities.isEmpty()) {
            System.out.println("No facilities available.");
            return null;
        }
        System.out.println("Select departure facility:");
        for (int i = 0; i < facilities.size(); i++) {
            Facility f = facilities.get(i);
            System.out.printf("%d. %s %n", i + 1, f.getName());
        }
        int idx = getUserSelection(facilities.size());
        return facilities.get(idx);
    }

    /**
     * Prompts the user to select a line that starts at the given facility.
     * @param facility starting facility
     * @return selected Line or null if none available
     */
    private Line selectLineFromFacility(Facility facility) {
        List<Line> lines = controller.getLinesStartingAt(facility.getStationId());
        if (lines.isEmpty()) {
            System.out.println("No lines start at the selected facility.");
            return null;
        }
        System.out.println("Select line starting at " + facility.getName() + ":");
        for (int i = 0; i < lines.size(); i++) {
            Line l = lines.get(i);
            String endName = controller.findFacilityById(l.getEndId()).map(Facility::getName).orElse(l.getEndName());
            System.out.printf("%d. %s [gaugeId   %d] -> %s%n", i + 1, l.getName(), l.getGauge(), endName);
        }
        int idx = getUserSelection(lines.size());
        return lines.get(idx);
    }

    /**
     * Prompts the user to select a locomotive compatible with the line's gauge.
     * @param line selected line
     * @return selected Locomotive or null if none available
     */
    private Locomotive selectLocomotiveForLine(Line line) {
        List<Locomotive> locos = controller.getLocomotivesCompatibleWithLine(line);
        if (locos.isEmpty()) {
            System.out.println("No compatible locomotives for the line gauge.");
            return null;
        }
        System.out.println("Select locomotive:");
        for (int i = 0; i < locos.size(); i++) {
            Locomotive l = locos.get(i);
            System.out.printf("%d. %s (%s %s) - op speed: %.1f km/h%n", i + 1, l.getLocomotiveName(), l.getMake(), l.getModel(), l.getMaxSpeed());
        }
        int idx = getUserSelection(locos.size());
        return locos.get(idx);
    }

    /**
     * Reads an option number between 1 and max from the user.
     * @param max number of options
     * @return zero-based selection index
     */
    private int getUserSelection(int max) {
        int selection = -1;
        while (selection < 0 || selection >= max) {
            System.out.print("Select an option (1-" + max + "): ");
            while (!sc.hasNextInt()) {
                System.out.print("Invalid input. Enter a number (1-" + max + "): ");
                sc.next();
            }
            selection = sc.nextInt() - 1;
            if (selection < 0 || selection >= max) {
                System.out.println("Invalid selection. Try again.");
            }
        }
        return selection;
    }
}
