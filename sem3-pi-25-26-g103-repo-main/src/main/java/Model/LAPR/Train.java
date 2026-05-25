package Model.LAPR;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a train consisting of one or more locomotives and a set of wagons.
 * USLP09 - Enhanced to support detailed wagon management and route assignment.
 */
public class Train {
    private String trainId;
    private Locomotive locomotive;
    private List<RailwayWagon> wagons; // USLP09 - List of wagons in the train
    private Route assignedRoute; // USLP09 - Route assigned to this train
    private int numberOfWagons;
    private double totalWagonsLoad; // current load in kg (sum of cargo on wagons)
    private double emptyWagonsWeight; // combined empty weight of wagons in kg

    public Train(String trainId, Locomotive locomotive) {
        this.trainId = trainId;
        this.locomotive = locomotive;
        this.wagons = new ArrayList<>();
        this.numberOfWagons = 0;
        this.totalWagonsLoad = 0.0;
        this.emptyWagonsWeight = 0.0;
        this.assignedRoute = null;
    }

    // Constructor with route assignment
    public Train(String trainId, Locomotive locomotive, Route route) {
        this(trainId, locomotive);
        this.assignedRoute = route;
    }

    public String getTrainId() {
        return trainId;
    }

    public void setTrainId(String trainId) {
        this.trainId = trainId;
    }

    public Locomotive getLocomotive() {
        return locomotive;
    }

    public void setLocomotive(Locomotive locomotive) {
        this.locomotive = locomotive;
    }

    public List<RailwayWagon> getWagons() {
        return wagons;
    }

    public Route getAssignedRoute() {
        return assignedRoute;
    }

    public void setAssignedRoute(Route assignedRoute) {
        this.assignedRoute = assignedRoute;
    }

    /**
     * Adds a wagon to the train by supplying its empty weight and current load.
     * @deprecated Use addWagon(RailwayWagon) instead for better wagon management
     */
    @Deprecated
    public void addWagon(double emptyWeightKg, double currentLoadKg) {
        this.numberOfWagons++;
        this.emptyWagonsWeight += emptyWeightKg;
        this.totalWagonsLoad += currentLoadKg;
    }

    /**
     * USLP09 - Adds a railway wagon to the train composition.
     * Validates gauge compatibility before adding.
     *
     * @param wagon the railway wagon to add
     * @return true if wagon was added successfully, false if incompatible
     */
    public boolean addWagon(RailwayWagon wagon) {
        if (wagon == null) {
            return false;
        }

        // Check gauge compatibility with locomotive
        if (locomotive != null && !wagon.isCompatibleWithGauge(locomotive.getGaugeId())) {
            return false;
        }

        wagons.add(wagon);
        numberOfWagons = wagons.size();
        emptyWagonsWeight += wagon.getEmptyWeight();
        totalWagonsLoad += wagon.getCurrentLoad();
        return true;
    }

    /**
     * USLP09 - Removes a wagon from the train composition.
     *
     * @param wagonId the ID of the wagon to remove
     * @return true if wagon was removed, false if not found
     */
    public boolean removeWagon(String wagonId) {
        RailwayWagon toRemove = null;
        for (RailwayWagon wagon : wagons) {
            if (wagon.getWagonId().equals(wagonId)) {
                toRemove = wagon;
                break;
            }
        }

        if (toRemove != null) {
            wagons.remove(toRemove);
            numberOfWagons = wagons.size();
            emptyWagonsWeight -= toRemove.getEmptyWeight();
            totalWagonsLoad -= toRemove.getCurrentLoad();
            return true;
        }
        return false;
    }

    /**
     * USLP09 - Removes all wagons from the train.
     */
    public void clearWagons() {
        wagons.clear();
        numberOfWagons = 0;
        emptyWagonsWeight = 0.0;
        totalWagonsLoad = 0.0;
    }

    public int getNumberOfWagons() {
        return numberOfWagons;
    }

    /**
     * Returns the total weight of the train (locomotive + wagons + loads) in kilograms.
     */
    public double getTotalWeightKg() {
        double locoWeight = locomotive != null ? locomotive.getWeight() : 0.0;
        return locoWeight + emptyWagonsWeight + totalWagonsLoad;
    }

    /**
     * USLP09 - Returns the total length of the train in meters.
     */
    public double getTotalLength() {
        double locoLength = locomotive != null ? locomotive.getLength() : 0.0;
        double wagonsLength = wagons.stream()
                .mapToDouble(RailwayWagon::getLength)
                .sum();
        return locoLength + wagonsLength;
    }

    /**
     * USLP09 - Checks if the train composition is valid (has locomotive and at least one wagon).
     */
    public boolean isValidComposition() {
        return locomotive != null && numberOfWagons > 0;
    }

    /**
     * USLP09 - Alias for isValidComposition() to support controller usage.
     */
    public boolean isValid() {
        return isValidComposition();
    }

    /**
     * USLP09 - Returns the total weight of the train (alias for getTotalWeightKg).
     */
    public double getTotalWeight() {
        return getTotalWeightKg();
    }

    /**
     * USLP09 - Checks if a route is assigned to this train.
     */
    public boolean hasAssignedRoute() {
        return assignedRoute != null;
    }

    @Override
    public String toString() {
        return "Train{" +
                "trainId='" + trainId + '\'' +
                ", loco=" + (locomotive != null ? locomotive.getLocomotiveName() : "<none>") +
                ", wagons=" + numberOfWagons +
                ", totalWeight=" + getTotalWeightKg() + " kg" +
                ", route=" + (assignedRoute != null ? assignedRoute.getRouteName() : "<none>") +
                '}';
    }
}
