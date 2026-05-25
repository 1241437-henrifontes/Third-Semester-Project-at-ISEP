package Model.LAPR;

/**
 * Represents a railway wagon (carriage) used in train compositions.
 * USLP09 - Domain class for managing wagons that can be assigned to trains.
 * This is different from Model.Wagon which is used for warehouse operations.
 */
public class RailwayWagon {
    private String wagonId;
    private String wagonType;           // e.g., "Passenger", "Freight", "Tank", "Hopper"
    private String make;
    private String model;
    private double length;              // in meters
    private double width;               // in meters
    private double height;              // in meters
    private double emptyWeight;         // in kg
    private double maxLoadCapacity;     // in kg
    private double currentLoad;         // in kg
    private int gaugeId;                // Railway gauge identifier
    private String operator;            // Operating company
    private RollingStockLocation location; // Current location and status

    // Constructor with essential parameters
    public RailwayWagon(String wagonId, String wagonType, double emptyWeight, double maxLoadCapacity) {
        this.wagonId = wagonId;
        this.wagonType = wagonType;
        this.emptyWeight = emptyWeight;
        this.maxLoadCapacity = maxLoadCapacity;
        this.currentLoad = 0.0;
        this.location = new RollingStockLocation();
    }

    // Full constructor
    public RailwayWagon(String wagonId, String wagonType, String make, String model,
                        double length, double width, double height, double emptyWeight,
                        double maxLoadCapacity, double currentLoad, int gaugeId, String operator) {
        this.wagonId = wagonId;
        this.wagonType = wagonType;
        this.make = make;
        this.model = model;
        this.length = length;
        this.width = width;
        this.height = height;
        this.emptyWeight = emptyWeight;
        this.maxLoadCapacity = maxLoadCapacity;
        this.currentLoad = currentLoad;
        this.gaugeId = gaugeId;
        this.operator = operator;
        this.location = new RollingStockLocation();
    }

    // Getters and Setters
    public String getWagonId() {
        return wagonId;
    }

    public void setWagonId(String wagonId) {
        this.wagonId = wagonId;
    }

    public String getWagonType() {
        return wagonType;
    }

    public void setWagonType(String wagonType) {
        this.wagonType = wagonType;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public double getLength() {
        return length;
    }

    public void setLength(double length) {
        this.length = length;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getEmptyWeight() {
        return emptyWeight;
    }

    public void setEmptyWeight(double emptyWeight) {
        this.emptyWeight = emptyWeight;
    }

    public double getMaxLoadCapacity() {
        return maxLoadCapacity;
    }

    public void setMaxLoadCapacity(double maxLoadCapacity) {
        this.maxLoadCapacity = maxLoadCapacity;
    }

    public double getCurrentLoad() {
        return currentLoad;
    }

    public void setCurrentLoad(double currentLoad) {
        this.currentLoad = currentLoad;
    }

    public int getGaugeId() {
        return gaugeId;
    }

    public void setGaugeId(int gaugeId) {
        this.gaugeId = gaugeId;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public RollingStockLocation getLocation() {
        return location;
    }

    public void setLocation(RollingStockLocation location) {
        this.location = location;
    }

    /**
     * Returns the total weight of the wagon including its current load.
     */
    public double getTotalWeight() {
        return emptyWeight + currentLoad;
    }

    /**
     * Returns the available capacity remaining in the wagon.
     */
    public double getAvailableCapacity() {
        return maxLoadCapacity - currentLoad;
    }

    /**
     * Checks if the wagon is compatible with the given gauge.
     */
    public boolean isCompatibleWithGauge(int gaugeId) {
        return this.gaugeId == gaugeId;
    }

    @Override
    public String toString() {
        return "RailwayWagon{" +
                "wagonId='" + wagonId + '\'' +
                ", type='" + wagonType + '\'' +
                ", weight=" + getTotalWeight() + " kg" +
                ", location=" + location +
                '}';
    }
}