package Model.LAPR;

/**
 * Represents a locomotive with technical specifications used for travel time estimations
 * and compatibility checks with railway segments.
 * USLP09 - Enhanced with location tracking for train assembly operations.
 */
/**
 * Represents a locomotive and its technical characteristics, including physical dimensions,
 * performance metrics, electrical specs, operator, and current operational location/state.
 */
public class Locomotive {
    private int numberOfLocomotives;
    private String locomotiveName;
    private String make;
    private String model;
    private int service;
    private int numberOfBogies;
    private String bogies;
    private int power;
    private double length;
    private double width;
    private double height;
    private double weight;
    private double maxSpeed;
    private double operationalSpeed;
    private double traction;
    private String type;
    private double voltage;
    private double frequency;
    private String operator;
    private int gaugeId;
    private double gas;
    private RollingStockLocation location; // USLP09 - Track locomotive location and status

    public Locomotive(int numberOfLocomotives, String locomotiveName, String make, String model,
                      int service, int numberOfBogies, String bogies, int power, double length,
                      double width, double height, double weight, double maxSpeed,
                      double operationalSpeed, double traction, String type, double voltage,
                      double frequency, String operator, int gaugeId, double gas) {
        this.numberOfLocomotives = numberOfLocomotives;
        this.locomotiveName = locomotiveName;
        this.make = make;
        this.model = model;
        this.service = service;
        this.numberOfBogies = numberOfBogies;
        this.bogies = bogies;
        this.power = power;
        this.length = length;
        this.width = width;
        this.height = height;
        this.weight = weight;
        this.maxSpeed = maxSpeed;
        this.operationalSpeed = operationalSpeed;
        this.traction = traction;
        this.type = type;
        this.voltage = voltage;
        this.frequency = frequency;
        this.operator = operator;
        this.gaugeId = gaugeId;
        this.gas = gas;
        this.location = new RollingStockLocation(); // Initialize location
    }

    public int getNumberOfLocomotives() {
        return numberOfLocomotives;
    }

    public void setNumberOfLocomotives(int numberOfLocomotives) {
        this.numberOfLocomotives = numberOfLocomotives;
    }

    public double getGas() {
        return gas;
    }

    public void setGas(double gas) {
        this.gas = gas;
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

    public double getFrequency() {
        return frequency;
    }

    public void setFrequency(double frequency) {
        this.frequency = frequency;
    }

    public double getVoltage() {
        return voltage;
    }

    public void setVoltage(double voltage) {
        this.voltage = voltage;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getTraction() {
        return traction;
    }

    public void setTraction(double traction) {
        this.traction = traction;
    }

    public double getOperationalSpeed() {
        return operationalSpeed;
    }

    public void setOperationalSpeed(double operationalSpeed) {
        this.operationalSpeed = operationalSpeed;
    }

    public double getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(double maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getLength() {
        return length;
    }

    public void setLength(double length) {
        this.length = length;
    }

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }

    public String getBogies() {
        return bogies;
    }

    public void setBogies(String bogies) {
        this.bogies = bogies;
    }

    public int getNumberOfBogies() {
        return numberOfBogies;
    }

    public void setNumberOfBogies(int numberOfBogies) {
        this.numberOfBogies = numberOfBogies;
    }

    public int getService() {
        return service;
    }

    public void setService(int service) {
        this.service = service;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getLocomotiveName() {
        return locomotiveName;
    }

    public void setLocomotiveName(String locomotiveName) {
        this.locomotiveName = locomotiveName;
    }

    public RollingStockLocation getLocation() {
        return location;
    }

    public void setLocation(RollingStockLocation location) {
        this.location = location;
    }

    /**
     * Checks if the locomotive is compatible with the given gauge.
     */
    public boolean isCompatibleWithGauge(int gaugeId) {
        return this.gaugeId == gaugeId;
    }

    /**
     * Checks if the locomotive is available for assignment.
     */
    public boolean isAvailable() {
        return location != null && location.isAvailable();
    }

    @Override
    public String toString() {
        return "Locomotive{" +
                "numberOfLocomotives='" + numberOfLocomotives + '\'' +
                ", locomotiveName='" + locomotiveName + '\'' +
                ", make='" + make + '\'' +
                ", model='" + model + '\'' +
                ", service=" + service +
                ", numberOfBogies=" + numberOfBogies +
                ", bogies='" + bogies + '\'' +
                ", power=" + power +
                ", length=" + length +
                ", width=" + width +
                ", height=" + height +
                ", weight=" + weight +
                ", maxSpeed=" + maxSpeed +
                ", operationalSpeed=" + operationalSpeed +
                ", traction=" + traction +
                ", type='" + type + '\'' +
                ", voltage=" + voltage +
                ", frequency=" + frequency +
                ", operator='" + operator + '\'' +
                ", gaugeId='" + gaugeId + '\'' +
                ", gas=" + gas +
                ", location=" + location +
                '}';
    }

    /**
     * Estimates the travel time for a locomotive to traverse a given railway segment.
     * <p>
     * The calculation uses the locomotive's maximum speed converted from km/h to m/s
     * and the segment length (in meters): time = length / speed.
     * </p>
     *
     * @param segment railway segment providing the distance (meters)
     * @param locomotive locomotive providing the speed capability (km/h)
     * @return estimated time in seconds to traverse the segment
     * @throws IllegalArgumentException if the computed speed is not greater than zero
     */
    public static double calculateEstimatedTimeTravel(Segment segment, Locomotive locomotive){
        double distance = segment.getLength();
        double speed = locomotive.getMaxSpeed()/3.6;
        if (speed <= 0) {
            throw new IllegalArgumentException("Operational speed must be greater than zero.");
        }
        return distance / speed;
    }

    /**
     * Advanced travel time calculation considering:
     * - Segment speed limits
     * - Train total weight (locomotive + wagons + loads)
     * - Locomotive power
     * - Track conditions
     *
     * @param segment the railway segment to traverse
     * @param locomotive the locomotive pulling the train
     * @param train the complete train (for weight calculation)
     * @return estimated time in seconds to traverse the segment
     */
    public static double calculateEstimatedTimeTravelAdvanced(Segment segment, Locomotive locomotive, Train train) {
        double distance = segment.getLength();

        // 1. Determine the effective speed limit (minimum of loco max and segment max)
        double effectiveMaxSpeed = locomotive.getMaxSpeed();

        // 2. Calculate power-to-weight ratio and apply speed reduction
        double trainWeight = train.getTotalWeightKg(); // in kg
        double locoPower = locomotive.getPower(); // in kW

        // Power-to-weight ratio (kW per tonne)
        double trainWeightTonnes = trainWeight / 1000.0;
        double powerPerTonne = trainWeightTonnes > 0 ? locoPower / trainWeightTonnes : locoPower;

        // Speed reduction factor based on power-to-weight ratio
        // Good ratio: >20 kW/tonne => 100% speed
        // Medium: 10-20 kW/tonne => 80-100% speed
        // Poor: <10 kW/tonne => 60-80% speed
        double speedFactor;
        if (powerPerTonne >= 20.0) {
            speedFactor = 1.0;
        } else if (powerPerTonne >= 10.0) {
            speedFactor = 0.8 + (powerPerTonne - 10.0) / 10.0 * 0.2;
        } else if (powerPerTonne > 0) {
            speedFactor = 0.6 + powerPerTonne / 10.0 * 0.2;
        } else {
            speedFactor = 0.6; // minimum 60% if no power data
        }

        // 3. Check weight limit of the segment
        double segmentMaxWeight = segment.getMaxWeight();
        if (segmentMaxWeight > 0 && trainWeight > segmentMaxWeight) {
            // Train exceeds segment weight limit - further reduce speed
            speedFactor *= 0.7; // 30% additional penalty for overweight
        }

        // 4. Calculate final effective speed
        double effectiveSpeed = effectiveMaxSpeed * speedFactor;

        if (effectiveSpeed <= 0) {
            throw new IllegalArgumentException("Effective speed must be greater than zero.");
        }

        // Convert km/h to m/s and calculate time
        double speedMs = effectiveSpeed / 3.6;
        return distance / speedMs;
    }
}
