package Services;

import Model.PickAllocationRow;
import Model.Trolley;

import java.util.*;

/**
 * Plans how picking allocations are distributed across trolleys under capacity constraints.
 * <p>
 * Supports three bin‑packing heuristics (FF, FFD, BFD) and two overflow policies (SPLIT, DEFER).
 * Implementations guarantee no trolley exceeds its capacity and all rows are either planned
 * or reported as skipped.
 */
public class PickingService {

    /** Tolerance for floating-point comparisons (epsilon). */
    private static final double EPS = 1e-9;

    /**
     * Supported bin-packing heuristics used to select trolleys.
     * <p>
     * FF places items in the first trolley that fits; FFD sorts by weight desc then applies FF;
     * BFD sorts by weight desc and places items to minimize remaining slack.
     */
    public enum Heuristic {
        /** First-Fit: places item in first trolley where it fits */
        FF,
        /** First-Fit Decreasing: sorts by weight desc, then first-fit */
        FFD,
        /** Best-Fit Decreasing: sorts by weight desc, then best-fit */
        BFD
    }

    /**
     * Enumeration of overflow policies when an item doesn't fit in a trolley.
     *
     * <p><b>SPLIT:</b> Allows splitting allocation by units into multiple trolleys.</p>
     * <p><b>DEFER:</b> Allocation is indivisible; either fits entirely or is rejected/new trolley.</p>
     */
    public enum OverflowPolicy {
        /** Allows dividing allocation into multiple trolleys */
        SPLIT,
        /** Allocation is atomic (cannot be divided) */
        DEFER
    }

    /** List of allocations that didn't fit in the plan (insufficient capacity). */
    private final List<PickAllocationRow> skippedRows = new ArrayList<>();

    /**
     * Returns a copy of the rows that could not be placed in the last plan.
     *
     * @return a defensive copy of skipped rows (never null)
     */
    public List<PickAllocationRow> getSkippedRows() {
        return new ArrayList<>(skippedRows);
    }

    /**
     * Plans how rows are placed into trolleys according to the chosen heuristic and policy.
     * Ensures returned trolleys never exceed capacity; rows that cannot fit are recorded as skipped.
     *
     * @param rows rows to distribute (non-null)
     * @param trolleyCapacityKg trolley capacity in kilograms (> 0)
     * @param heuristic heuristic to apply
     * @param policy overflow policy to apply
     * @return list of trolleys produced by the plan
     * @throws IllegalArgumentException if arguments are invalid
     */
    public List<Trolley> plan(List<PickAllocationRow> rows,
                              double trolleyCapacityKg,
                              Heuristic heuristic,
                              OverflowPolicy policy) {
        if (rows == null) throw new IllegalArgumentException("rows cannot be null");
        if (trolleyCapacityKg <= 0) throw new IllegalArgumentException("trolleyCapacityKg must be > 0");
        if (heuristic == null) throw new IllegalArgumentException("heuristic cannot be null");
        if (policy == null) throw new IllegalArgumentException("policy cannot be null");

        skippedRows.clear();
        List<Trolley> trolleys = new ArrayList<>();

        List<PickAllocationRow> work = new ArrayList<>(rows);
        if (heuristic == Heuristic.FFD || heuristic == Heuristic.BFD) {
            Collections.sort(work);
        }

        for (PickAllocationRow row : work) {
            if (policy == OverflowPolicy.SPLIT) {
                processSplit(trolleys, row, trolleyCapacityKg, heuristic);
            } else {
                processDefer(trolleys, row, trolleyCapacityKg, heuristic);
            }
        }

        return trolleys;
    }

    /**
     * Processes an allocation row with SPLIT policy.
     * <p>The row can be split into multiple units distributed across several trolleys.</p>
     *
     * <p><b>Algorithm:</b></p>
     * <pre>
     * remaining = row.qty
     * while remaining > 0:
     *     1. Select target trolley (FF/FFD: first-fit, BFD: best-fit)
     *     2. Calculate how many units fit: floor(slack / unitWeight)
     *     3. Place min(remaining, canQty) units
     *     4. remaining -= placed
     * </pre>
     *
     * @param trolleys list of existing trolleys (mutable)
     * @param row allocation row to process
     * @param capacity capacity of each trolley in kg
     * @param heuristic heuristic to use for trolley selection
     */
    private void processSplit(List<Trolley> trolleys, PickAllocationRow row,
                             double capacity, Heuristic heuristic) {
        int remaining = row.getQty();
        double unitWeight = row.getUnitWeightKg();

        if (unitWeight > capacity + EPS) {
            skippedRows.add(row);
            return;
        }

        while (remaining > 0) {
            Trolley target = selectTrolleyForSplit(trolleys, unitWeight, heuristic);

            if (target == null) {
                target = new Trolley(capacity);
                trolleys.add(target);
            }

            int canQty = (int) Math.floor(target.remainingKg() / unitWeight);

            if (canQty <= 0) {
                target = new Trolley(capacity);
                trolleys.add(target);
                canQty = (int) Math.floor(target.remainingKg() / unitWeight);

                if (canQty <= 0) {
                    skippedRows.add(row.copyWithQty(remaining));
                    break;
                }
            }

            int placeQty = Math.min(remaining, canQty);
            target.add(row.copyWithQty(placeQty));
            remaining -= placeQty;
        }
    }

    /**
     * Processes an allocation row with DEFER policy.
     * <p>The row is treated as atomic (indivisible); either fits entirely in a trolley or creates new.</p>
     *
     * <p><b>Algorithm:</b></p>
     * <pre>
     * 1. If row.weight > capacity: skip (impossible to accommodate)
     * 2. Select trolley where entire row fits
     * 3. If no trolley works: create new trolley
     * 4. Add entire row to selected trolley
     * </pre>
     *
     * @param trolleys list of existing trolleys (mutable)
     * @param row allocation row to process
     * @param capacity capacity of each trolley in kg
     * @param heuristic heuristic to use for trolley selection
     */
    private void processDefer(List<Trolley> trolleys, PickAllocationRow row,
                             double capacity, Heuristic heuristic) {
        double allocationWeight = row.getWeightKg();

        // Validation: allocation doesn't fit even in empty trolley
        if (allocationWeight > capacity + EPS) {
            skippedRows.add(row);
            return;
        }

        Trolley target = selectTrolleyForDefer(trolleys, row, heuristic);

        if (target == null) {
            target = new Trolley(capacity);
            trolleys.add(target);
        }

        target.add(row);
    }

    /**
     * Selects a trolley for SPLIT policy.
     *
     * <p><b>FF/FFD:</b> Returns the first trolley with slack &gt;= unitWeight (linear scan).</p>
     * <p><b>BFD:</b> Returns the trolley with minimum slack AFTER placing maximum possible units.</p>
     *
     * <p><b>BFD tiebreaker:</b> In case of slack tie, chooses trolley with lower index (created first).</p>
     *
     * @param trolleys list of available trolleys
     * @param unitWeight weight of one unit of the allocation (in kg)
     * @param heuristic heuristic to apply
     * @return selected trolley, or null if no trolley has space for 1 unit
     */
    private Trolley selectTrolleyForSplit(List<Trolley> trolleys, double unitWeight,
                                         Heuristic heuristic) {
        if (trolleys.isEmpty()) return null;

        if (heuristic == Heuristic.BFD) {
            Trolley bestFit = null;
            double minSlackAfter = Double.MAX_VALUE;
            int bestIndex = -1;

            for (int i = 0; i < trolleys.size(); i++) {
                Trolley t = trolleys.get(i);
                double slack = t.remainingKg();

                if (slack >= unitWeight - EPS) {
                    int maxFit = (int) Math.floor(slack / unitWeight);
                    double slackAfter = slack - (maxFit * unitWeight);

                    if (slackAfter < minSlackAfter - EPS ||
                        (Math.abs(slackAfter - minSlackAfter) < EPS && (bestFit == null || i < bestIndex))) {
                        minSlackAfter = slackAfter;
                        bestFit = t;
                        bestIndex = i;
                    }
                }
            }

            return bestFit;
        } else {
            for (Trolley t : trolleys) {
                if (t.remainingKg() >= unitWeight - EPS) {
                    return t;
                }
            }
            return null;
        }
    }

    /**
     * Selects a trolley for DEFER policy.
     *
     * <p><b>FF/FFD:</b> Returns the first trolley where entire allocation fits.</p>
     * <p><b>BFD:</b> Returns the trolley with minimum slack AFTER placing entire allocation.</p>
     *
     * <p><b>BFD tiebreaker:</b> In case of slack tie, chooses trolley with lower index.</p>
     *
     * @param trolleys list of available trolleys
     * @param row allocation row to place
     * @param heuristic heuristic to apply
     * @return selected trolley, or null if no trolley has space for complete allocation
     */
    private Trolley selectTrolleyForDefer(List<Trolley> trolleys, PickAllocationRow row,
                                         Heuristic heuristic) {
        if (trolleys.isEmpty()) return null;

        double allocationWeight = row.getWeightKg();

        if (heuristic == Heuristic.BFD) {
            Trolley bestFit = null;
            double minSlackAfter = Double.MAX_VALUE;
            int bestIndex = -1;

            for (int i = 0; i < trolleys.size(); i++) {
                Trolley t = trolleys.get(i);

                if (t.canFit(allocationWeight)) {
                    double slackAfter = t.remainingKg() - allocationWeight;

                    if (slackAfter < minSlackAfter - EPS ||
                        (Math.abs(slackAfter - minSlackAfter) < EPS && (bestFit == null || i < bestIndex))) {
                        minSlackAfter = slackAfter;
                        bestFit = t;
                        bestIndex = i;
                    }
                }
            }

            return bestFit;
        } else {
            for (Trolley t : trolleys) {
                if (t.canFit(allocationWeight)) {
                    return t;
                }
            }
            return null;
        }
    }
}
