package Model.Wrappers;

import Model.RailwayStation;

/**
 * Comparable wrapper (Java record) around a {@link RailwayStation} that
 * orders stations by their {@code name}. Useful for indexing or storing
 * stations in ordered trees/collections by name.
 *
 * <p>This type is implemented as a {@code record} and is immutable.</p>
 */
public record RailwayStationByName(RailwayStation station) implements Comparable<RailwayStationByName> {

    /**
     * Compares this wrapper with another by the wrapped station's name.
     *
     * @param other the other {@code RailwayStationByName}
     * @return a negative, zero, or positive integer as this name is less than,
     *         equal to, or greater than the specified name
     */
    @Override
    public int compareTo(RailwayStationByName other) {
        return this.station.getName().compareTo(other.station.getName());
    }

    /**
     * Returns the wrapped station's name.
     *
     * @return the station name
     */
    @Override
    public String toString() {
        return station.getName();
    }
}
