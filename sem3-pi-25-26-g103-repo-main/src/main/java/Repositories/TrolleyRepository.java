package Repositories;

import Model.Trolley;
import Services.PickingService;

import java.util.ArrayList;
import java.util.List;

/**
 * Repository that stores the last computed trolley plan and its metadata.
 */
public class TrolleyRepository {

    private List<Trolley> trolleys = new ArrayList<>();
    private PickingService.Heuristic lastHeuristic;
    private PickingService.OverflowPolicy lastPolicy;
    private double lastTrolleyCapacity;

    private static TrolleyRepository instance = new TrolleyRepository();

    private TrolleyRepository() {}

    /** Returns the TrolleyRepository singleton. */
    public static TrolleyRepository getInstance() {
        return instance;
    }

    /**
     * Stores a copy of the planned trolleys and records planning metadata.
     *
     * @param trolleys planned trolleys (cannot be null)
     * @param heuristic heuristic used during planning
     * @param policy overflow policy used during planning
     * @param trolleyCapacity trolley capacity used during planning
     */
    public void storePlan(List<Trolley> trolleys,
                         PickingService.Heuristic heuristic,
                         PickingService.OverflowPolicy policy,
                         double trolleyCapacity) {
        if (trolleys == null) {
            throw new IllegalArgumentException("trolleys cannot be null");
        }
        this.trolleys = new ArrayList<>(trolleys);
        this.lastHeuristic = heuristic;
        this.lastPolicy = policy;
        this.lastTrolleyCapacity = trolleyCapacity;
    }

    /** Returns a copy of all stored trolleys. */
    public List<Trolley> getAllTrolleys() {
        return new ArrayList<>(trolleys);
    }

    /**
     * Returns a trolley by index.
     *
     * @param index zero-based index
     * @return trolley at the given index
     */
    public Trolley getTrolley(int index) {
        if (index < 0 || index >= trolleys.size()) {
            throw new IndexOutOfBoundsException("Invalid trolley index: " + index);
        }
        return trolleys.get(index);
    }

    /** Returns the number of stored trolleys. */
    public int getTrolleyCount() {
        return trolleys.size();
    }

    /** Returns true if one or more trolleys are stored. */
    public boolean hasTrolleys() {
        return !trolleys.isEmpty();
    }

    /** Clears stored trolleys and associated metadata. */
    public void clear() {
        trolleys.clear();
        lastHeuristic = null;
        lastPolicy = null;
        lastTrolleyCapacity = 0.0;
    }

    /** Returns the heuristic used in the last plan. */
    public PickingService.Heuristic getLastHeuristic() {
        return lastHeuristic;
    }

    /** Returns the overflow policy used in the last plan. */
    public PickingService.OverflowPolicy getLastPolicy() {
        return lastPolicy;
    }

    /** Returns the trolley capacity used in the last plan. */
    public double getLastTrolleyCapacity() {
        return lastTrolleyCapacity;
    }
}

