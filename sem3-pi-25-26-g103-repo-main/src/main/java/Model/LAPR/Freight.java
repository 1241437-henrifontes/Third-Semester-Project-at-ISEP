package Model.LAPR;

/**
 * Represents a freight item to be transported between two facilities.
 *
 * A freight is characterized by its identifier, description, weight
 * and its origin and destination facilities.
 *
 * This class is typically used in logistics and transportation
 * planning scenarios.
 */
public class Freight {
    private int id;
    private String description;
    private double weight;
    private Facility origin;
    private Facility destination;

    /**
     * Creates a new freight instance.
     *
     * @param id          unique identifier of the freight
     * @param description textual description of the freight
     * @param weight      weight of the freight (in tons)
     * @param origin      origin facility
     * @param destination destination facility
     */
    public Freight(int id, String description, double weight, Facility origin, Facility destination) {
        this.id = id;
        this.description = description;
        this.weight = weight;
        this.origin = origin;
        this.destination = destination;
    }

    public int getId() { return id; }
    public Facility getOrigin() { return origin; }
    public Facility getDestination() { return destination; }

    @Override
    public String toString() {
        return String.format("Freight #%d | %s (%.1f ton) | From: %s -> To: %s",
                id, description, weight, origin.getName(), destination.getName());
    }
}