package UI;

import Controllers.PickingController;
import Model.PickAllocationRow;
import Model.Trolley;
import Services.PickingService;

import java.util.*;

/**
 * UI for generating picking plans and sequencing items into trolleys.
 */
public class PickingUI implements Runnable {

    private PickingController controller;

    /**
     * Constructs the PickingUI and initializes its controller.
     */
    public PickingUI() {
        this.controller = new PickingController();
    }

    /**
     * Gets the underlying PickingController.
     * @return controller instance
     */
    private PickingController getController() {
        return controller;
    }

    /**
     * Runs the picking plan workflow: reads configuration, runs planning, and prints results.
     */
    @Override
    public void run() {
        List<PickAllocationRow> rows = getController().convertToPickRows();

        if (rows == null || rows.isEmpty()) {
            System.out.println();
            System.out.println("=== NO DATA AVAILABLE ===");
            System.out.println("Before generating picking plans, you must:");
            System.out.println("  1. Unload and Organize Warehouse");
            System.out.println("  2. Check Orders");
            System.out.println();
            return;
        }

        Scanner sc = new Scanner(System.in);
        sc.useLocale(Locale.US);

        System.out.println();
        System.out.println();
        System.out.println("--- PICKING PLANS --------------------------");
        System.out.println("WARNING: This functionality requires that you have already run");
        System.out.println("'Check Orders' to have allocation data available.");
        System.out.println();

        System.out.print("Trolley capacity (kg): ");
        double capacity = askPositiveDouble(sc);

        System.out.println();
        System.out.println("Heuristic (1-FF, 2-FFD, 3-BFD):");
        System.out.println("  1) FF  - First Fit");
        System.out.println("  2) FFD - First Fit Decreasing (sort by weight desc)");
        System.out.println("  3) BFD - Best Fit Decreasing (sort by weight desc + best slack)");
        PickingService.Heuristic heuristic =
                PickingService.Heuristic.values()[askInt(sc, 1, 3) - 1];

        System.out.println();
        System.out.println("Overflow policy (1-SPLIT, 2-DEFER):");
        System.out.println("  1) SPLIT - Split a line across trolleys if needed");
        System.out.println("  2) DEFER - Move the whole line to a new trolley");
        PickingService.OverflowPolicy policy =
                PickingService.OverflowPolicy.values()[askInt(sc, 1, 2) - 1];

        System.out.println();
        System.out.println("Configuration:");
        System.out.println("  - Trolley capacity: " + capacity + " kg");
        System.out.println("  - Heuristic: " + heuristic);
        System.out.println("  - Policy: " + policy);
        System.out.print("Proceed? (Y/N): ");
        String confirm = sc.next().trim().toUpperCase();
        if (!confirm.equals("Y")) {
            System.out.println("Operation cancelled.");
            return;
        }

        List<Trolley> trolleys;
        try {
            trolleys = getController().plan(rows, capacity, heuristic, policy);
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
            return;
        }

        printResult(trolleys, heuristic, policy, capacity);

        List<PickAllocationRow> skipped = getController().getSkippedRows();
        if (!skipped.isEmpty()) {
            printSkippedRows(skipped, capacity);
        }
    }

    /**
     * Prints rows that could not fit in any trolley, sorted by weight descending.
     * @param skipped list of skipped rows
     * @param trolleyCapacity trolley capacity used
     */
    private static void printSkippedRows(List<PickAllocationRow> skipped, double trolleyCapacity) {
        System.out.println();
        System.out.println("----------------------------------------------------------------------------");
        System.out.println("  ROWS THAT COULD NOT FIT IN TROLLEYS");
        System.out.println("----------------------------------------------------------------------------");
        System.out.println("Total skipped rows: " + skipped.size());

        double totalSkippedWeight = skipped.stream()
                .mapToDouble(PickAllocationRow::getWeightKg)
                .sum();

        System.out.printf("Total weight NOT picked: %.2f kg%n", totalSkippedWeight);
        System.out.println();
        System.out.println("These items exceed trolley capacity (" + String.format("%.2f", trolleyCapacity) + " kg)");
        System.out.println("and could not be split (unit weight too large):");
        System.out.println();

        List<PickAllocationRow> sortedSkipped = new ArrayList<>(skipped);
        sortedSkipped.sort(Comparator.comparingDouble(PickAllocationRow::getWeightKg).reversed());

        System.out.println("  order | line | aisle | bay | boxId | sku | qty | weightKg | unitWeightKg");
        for (PickAllocationRow row : sortedSkipped) {
            System.out.printf("  %s | %d | %d | %d | %s | %s | %d | %.2f | %.2f%n",
                    row.getOrderId(),
                    row.getLineNo(),
                    row.getAisle(),
                    row.getBay(),
                    row.getBoxId(),
                    row.getSku().getSku(),
                    row.getQty(),
                    row.getWeightKg(),
                    row.getUnitWeightKg());
        }
        System.out.println();
        System.out.println("  ACTION REQUIRED: These items need manual handling or larger trolleys.");
        System.out.println("-----------------------------------------------------------------------------");
    }

    /**
     * Prints the planning result and trolley utilisation details.
     * @param trolleys list of generated trolleys
     * @param heuristic heuristic used
     * @param policy overflow policy used
     * @param trolleyCapacityKg trolley capacity used
     */
    private static void printResult(List<Trolley> trolleys,
                                    PickingService.Heuristic heuristic,
                                    PickingService.OverflowPolicy policy,
                                    double trolleyCapacityKg) {
        System.out.println();
        System.out.println("----------------------------------------------------------------------------");
        System.out.println("Result — Heuristic: " + heuristic + " | Policy: " + policy);
        System.out.println("----------------------------------------------------------------------------");
        System.out.println("Total trolleys: " + trolleys.size());

        double totalUsed = 0.0;
        for (Trolley t : trolleys) totalUsed += t.getUsedKg();
        double avgUtil;
        if (trolleys.isEmpty()) {
            avgUtil = 0.0;
        } else {
            avgUtil = trolleys.stream().mapToDouble(Trolley::getUtilisation).average().orElse(0.0);
        }

        System.out.printf("Total weight picked: %.2f kg | Avg utilisation: %.0f%% | Waste: %.2f kg%n",
                totalUsed,
                100 * avgUtil,
                (trolleys.size() * trolleyCapacityKg) - totalUsed);

        for (int i = 0; i < trolleys.size(); i++) {
            Trolley tr = trolleys.get(i);
            String util = String.format("%.0f%%", tr.getUtilisation() * 100.0);
            System.out.println();
            System.out.println("Trolley #" + (i + 1) + " — Utilisation: " + util +
                    " (" + String.format("%.2f", tr.getUsedKg()) + "/" + String.format("%.2f", tr.getCapacityKg()) + " kg)");

            if (tr.getPicks().isEmpty()) {
                System.out.println("  (empty)");
                continue;
            }

            System.out.println("  order | line | aisle | bay | boxId | sku | qty | unitWeight | trolleyWeight | allocationWeight");
            for (var r : tr.getPicks()) {
                System.out.printf("  %s | %d | %d | %d | %s | %s | %d | %.2f | %.2f | %.2f%n",
                        r.getOrderId(), r.getLineNo(), r.getAisle(), r.getBay(),
                        r.getBoxId(), r.getSku().getSku(), r.getQty(),
                        r.getUnitWeightKg(), r.getWeightKg(), r.getOriginalTotalWeight());
            }
        }
    }

    /**
     * Reads a positive double value from the scanner.
     * @param sc scanner for input
     * @return positive double
     */
    private static double askPositiveDouble(Scanner sc) {
        while (true) {
            try {
                double v = sc.nextDouble();
                if (v > 0) return v;
                System.out.println("Value must be > 0. Try again.");
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Enter a positive number.");
                sc.next();
            }
        }
    }

    /**
     * Reads an int between min and max (inclusive).
     * @param sc scanner for input
     * @param min minimum value
     * @param max maximum value
     * @return validated int
     */
    private static int askInt(Scanner sc, int min, int max) {
        while (true) {
            System.out.print("> ");
            try {
                int v = sc.nextInt();
                if (v >= min && v <= max) return v;
            } catch (InputMismatchException e) {
                sc.next();
            }
            System.out.println("Invalid option. Choose between " + min + " and " + max + ".");
        }
    }
}
