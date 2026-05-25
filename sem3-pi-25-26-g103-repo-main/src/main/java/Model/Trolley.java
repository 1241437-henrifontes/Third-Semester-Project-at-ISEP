package Model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Represents a trolley used to load pick rows up to a maximum weight capacity.
 */
public class Trolley implements Comparable<Trolley> {

    private static final double EPS = 1e-9;
    private final double capacityKg;
    private double usedKg = 0.0;
    private final List<PickAllocationRow> picks = new ArrayList<>();

    public Trolley(double capacityKg) {
        if (capacityKg <= 0) throw new IllegalArgumentException("capacityKg must be > 0");
        this.capacityKg = capacityKg;
    }

    /**
     * Current remaining capacity in kilograms.
     *
     * @return remaining weight that can be loaded
     */
    public double remainingKg() { return capacityKg - usedKg; }

    /**
     * Checks whether a load of the given weight can fit into the trolley without exceeding capacity.
     *
     * @param weightKg weight to test (kg)
     * @return true if it fits, false otherwise
     */
    public boolean canFit(double weightKg) { return usedKg + weightKg <= capacityKg + EPS; }

    /**
     * Attempts to add the row to the trolley if it fits; does nothing otherwise.
     *
     * @param row allocation row to add
     * @return true if added; false if it would exceed capacity
     */
    public boolean tryAdd(PickAllocationRow row) {
        Objects.requireNonNull(row, "row");
        double w = row.getWeightKg();
        if (!canFit(w)) return false;
        picks.add(row);
        usedKg += w;
        return true;
    }

    /**
     * Adds the row to the trolley or throws if it does not fit.
     *
     * @param row allocation row to add
     * @throws IllegalStateException if the row exceeds capacity
     */
    public void add(PickAllocationRow row) {
        if (!tryAdd(row)) {
            throw new IllegalStateException("Row exceeds trolley capacity (" + usedKg + " + " + row.getWeightKg() +
                    " > " + capacityKg + ")");
        }
    }

    public double getCapacityKg() { return capacityKg; }

    public double getUsedKg() { return usedKg; }

    public double getUtilisation() { return usedKg / capacityKg; }

    public List<PickAllocationRow> getPicks() { return Collections.unmodifiableList(picks); }

    /**
     * Natural ordering by remaining capacity (ASC) so that trolleys with less free space come first.
     *
     * @param other other trolley
     * @return comparison of remaining kilograms
     */
    @Override
    public int compareTo(Trolley other) {
        return Double.compare(this.remainingKg(), other.remainingKg());
    }

    /**
     * Structural equality based on capacity, used weight and contained pick rows.
     *
     * @param o object to compare
     * @return true if both trolleys have the same state
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Trolley trolley = (Trolley) o;
        return Double.compare(trolley.capacityKg, capacityKg) == 0 &&
                Double.compare(trolley.usedKg, usedKg) == 0 &&
                Objects.equals(picks, trolley.picks);
    }

    /**
     * Hash code consistent with equals.
     *
     * @return hash of trolley fields
     */
    @Override
    public int hashCode() {
        return Objects.hash(capacityKg, usedKg, picks);
    }
}
