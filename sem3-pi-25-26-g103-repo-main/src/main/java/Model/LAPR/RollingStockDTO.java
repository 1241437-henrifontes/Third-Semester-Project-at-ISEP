package Model.LAPR;

/**
 * Data Transfer Object for displaying rolling stock (locomotives and wagons) information.
 * USLP09 - Facilitates the presentation of available rolling stock with location details.
 */
public class RollingStockDTO implements Comparable<RollingStockDTO> {
    private int id;
    private String name;
    private String type;              // "LOCOMOTIVE" or "WAGON"
    private String rollingStockType;  // Specific type (e.g., "Freight", "Passenger", "Electric")
    private String make;
    private String model;
    private double weight;            // Total weight in kg
    private int gaugeId;
    private String operator;
    private RollingStockStatus status;
    private String locationDescription; // Human-readable location
    private String stationId;          // Current station (if parked)
    private String stationName;        // Current station name (if parked)
    private String destinationId;      // Destination station (if in transit)
    private String destinationName;    // Destination station name (if in transit)
    private Double distanceFromStart;  // Distance from route starting point (for sorting)

    // Constructor for Locomotive
    public static RollingStockDTO fromLocomotive(Locomotive locomotive) {
        RollingStockDTO dto = new RollingStockDTO();
        dto.id = locomotive.getNumberOfLocomotives();
        dto.name = locomotive.getLocomotiveName();
        dto.type = "LOCOMOTIVE";
        dto.rollingStockType = locomotive.getType();
        dto.make = locomotive.getMake();
        dto.model = locomotive.getModel();
        dto.weight = locomotive.getWeight();
        dto.gaugeId = locomotive.getGaugeId();
        dto.operator = locomotive.getOperator();

        RollingStockLocation location = locomotive.getLocation();
        if (location != null) {
            dto.status = location.getStatus();
            dto.locationDescription = location.toString();
            dto.stationId = location.getCurrentStationId();
            dto.stationName = location.getCurrentStationName();
            dto.destinationId = location.getDestinationStationId();
            dto.destinationName = location.getDestinationStationName();
            dto.distanceFromStart = location.getDistanceFromPoint();
        }

        return dto;
    }

    // Constructor for RailwayWagon
    public static RollingStockDTO fromWagon(RailwayWagon wagon) {
        RollingStockDTO dto = new RollingStockDTO();
        dto.id = parseWagonId(wagon.getWagonId());
        dto.name = wagon.getWagonId();
        dto.type = "WAGON";
        dto.rollingStockType = wagon.getWagonType();
        dto.make = wagon.getMake();
        dto.model = wagon.getModel();
        dto.weight = wagon.getTotalWeight();
        dto.gaugeId = wagon.getGaugeId();
        dto.operator = wagon.getOperator();

        RollingStockLocation location = wagon.getLocation();
        if (location != null) {
            dto.status = location.getStatus();
            dto.locationDescription = location.toString();
            dto.stationId = location.getCurrentStationId();
            dto.stationName = location.getCurrentStationName();
            dto.destinationId = location.getDestinationStationId();
            dto.destinationName = location.getDestinationStationName();
            dto.distanceFromStart = location.getDistanceFromPoint();
        }

        return dto;
    }

    private static int parseWagonId(String raw) {
        if (raw == null) {
            return -1;
        }
        String digits = raw.replaceAll("\\D+", "");
        if (digits.isEmpty()) {
            int fallback = raw.hashCode();
            return (fallback == Integer.MIN_VALUE) ? 0 : Math.abs(fallback);
        }
        try {
            return Integer.parseInt(digits);
        } catch (NumberFormatException e) {
            long val = 0;
            for (int i = 0; i < digits.length(); i++) {
                val = val * 10 + (digits.charAt(i) - '0');
                if (val > Integer.MAX_VALUE) {
                    int fallback = raw.hashCode();
                    return (fallback == Integer.MIN_VALUE) ? 0 : Math.abs(fallback);
                }
            }
            return (int) val;
        }
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getRollingStockType() {
        return rollingStockType;
    }

    public String getMake() {
        return make;
    }

    public String getModel() {
        return model;
    }

    public double getWeight() {
        return weight;
    }

    public int getGaugeId() {
        return gaugeId;
    }

    public String getOperator() {
        return operator;
    }

    public RollingStockStatus getStatus() {
        return status;
    }

    // Allow setting status programmatically when DB location fields need to be interpreted
    public void setStatus(RollingStockStatus status) {
        this.status = status;
    }

    public String getLocationDescription() {
        return locationDescription;
    }

    public String getStationId() {
        return stationId;
    }

    public String getStationName() {
        return stationName;
    }

    public String getDestinationId() {
        return destinationId;
    }

    public String getDestinationName() {
        return destinationName;
    }

    public Double getDistanceFromStart() {
        return distanceFromStart;
    }

    public void setDistanceFromStart(Double distanceFromStart) {
        this.distanceFromStart = distanceFromStart;
    }

    public boolean isParked() {
        return status == RollingStockStatus.PARKED;
    }

    public boolean isInTransit() {
        return status == RollingStockStatus.IN_TRANSIT;
    }

    public boolean isLocomotive() {
        return "LOCOMOTIVE".equals(type);
    }

    public boolean isWagon() {
        return "WAGON".equals(type);
    }

    /**
     * USLP09 - Compares rolling stock by distance from starting point (descending order).
     * Used to sort parked rolling stock by distance from route starting point.
     */
    @Override
    public int compareTo(RollingStockDTO other) {
        if (this.distanceFromStart == null && other.distanceFromStart == null) {
            return 0;
        }
        if (this.distanceFromStart == null) {
            return 1;
        }
        if (other.distanceFromStart == null) {
            return -1;
        }
        // Descending order (closer items first)
        return Double.compare(other.distanceFromStart, this.distanceFromStart);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(type).append(" - ").append(name);
        if (rollingStockType != null) {
            sb.append(" (").append(rollingStockType).append(")");
        }
        sb.append(" | ").append(locationDescription);
        if (distanceFromStart != null) {
            sb.append(String.format(" | Distance: %.2f km", distanceFromStart));
        }
        return sb.toString();
    }
}

