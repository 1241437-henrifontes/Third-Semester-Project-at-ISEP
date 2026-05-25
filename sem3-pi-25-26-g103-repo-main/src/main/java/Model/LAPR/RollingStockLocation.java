package Model.LAPR;

/**
 * Represents the current location information of rolling stock (locomotive or wagon).
 * USLP09 - Tracks whether rolling stock is parked or in transit, and its location details.
 */
public class RollingStockLocation {
    private RollingStockStatus status;
    private String currentStationId;      // Station where parked (if PARKED)
    private String currentStationName;    // Station name where parked (if PARKED)
    private Integer routeId;              // Route ID (if IN_TRANSIT)
    private String destinationStationId;  // Final destination station ID (if IN_TRANSIT)
    private String destinationStationName; // Final destination station name (if IN_TRANSIT)
    private Double distanceFromPoint;     // Distance from a reference point (for sorting)

    public RollingStockLocation() {
        this.status = RollingStockStatus.AVAILABLE;
    }

    // Constructor for PARKED rolling stock
    public RollingStockLocation(String currentStationId, String currentStationName) {
        this.status = RollingStockStatus.PARKED;
        this.currentStationId = currentStationId;
        this.currentStationName = currentStationName;
    }

    // Constructor for IN_TRANSIT rolling stock
    public RollingStockLocation(Integer routeId, String destinationStationId, String destinationStationName) {
        this.status = RollingStockStatus.IN_TRANSIT;
        this.routeId = routeId;
        this.destinationStationId = destinationStationId;
        this.destinationStationName = destinationStationName;
    }

    // Getters and Setters
    public RollingStockStatus getStatus() {
        return status;
    }

    public void setStatus(RollingStockStatus status) {
        this.status = status;
    }

    public String getCurrentStationId() {
        return currentStationId;
    }

    public void setCurrentStationId(String currentStationId) {
        this.currentStationId = currentStationId;
    }

    public String getCurrentStationName() {
        return currentStationName;
    }

    public void setCurrentStationName(String currentStationName) {
        this.currentStationName = currentStationName;
    }

    public Integer getRouteId() {
        return routeId;
    }

    public void setRouteId(Integer routeId) {
        this.routeId = routeId;
    }

    public String getDestinationStationId() {
        return destinationStationId;
    }

    public void setDestinationStationId(String destinationStationId) {
        this.destinationStationId = destinationStationId;
    }

    public String getDestinationStationName() {
        return destinationStationName;
    }

    public void setDestinationStationName(String destinationStationName) {
        this.destinationStationName = destinationStationName;
    }

    public Double getDistanceFromPoint() {
        return distanceFromPoint;
    }

    public void setDistanceFromPoint(Double distanceFromPoint) {
        this.distanceFromPoint = distanceFromPoint;
    }

    public boolean isParked() {
        return status == RollingStockStatus.PARKED;
    }

    public boolean isInTransit() {
        return status == RollingStockStatus.IN_TRANSIT;
    }

    public boolean isAvailable() {
        return status == RollingStockStatus.AVAILABLE || status == RollingStockStatus.PARKED;
    }

    @Override
    public String toString() {
        if (isParked()) {
            return "Parked at: " + currentStationName + " (ID: " + currentStationId + ")";
        } else if (isInTransit()) {
            return "In transit to: " + destinationStationName + " (Route ID: " + routeId + ")";
        } else {
            return "Status: " + status;
        }
    }
}

