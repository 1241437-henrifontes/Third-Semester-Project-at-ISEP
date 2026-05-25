package Model.LAPR;

/**
 * Enum representing the possible statuses of rolling stock (locomotives and wagons).
 * USLP09 - Required to distinguish between parked and in-transit rolling stock.
 */
public enum RollingStockStatus {
    /**
     * Rolling stock is currently parked at a station
     */
    PARKED,

    /**
     * Rolling stock is currently in transit on a route
     */
    IN_TRANSIT,

    /**
     * Rolling stock is under maintenance
     */
    MAINTENANCE,

    /**
     * Rolling stock is available for assignment
     */
    AVAILABLE
}

