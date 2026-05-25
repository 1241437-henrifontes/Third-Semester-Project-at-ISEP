package Model;

import java.util.Objects;

/**
 * Represents a railway station with its distance from a target point.
 * Used for nearest-neighbor search results.
 * Implements Comparable to allow sorting by distance and then by name (descending).
 */
public class StationDistance implements Comparable<StationDistance> {

    private final RailwayStation station;
    private final double distance;

    public StationDistance(RailwayStation station, double distance) {
        if (station == null) {
            throw new IllegalArgumentException("Station cannot be null");
        }
        if (distance < 0) {
            throw new IllegalArgumentException("Distance cannot be negative");
        }
        this.station = station;
        this.distance = distance;
    }

    public RailwayStation getStation() {
        return station;
    }

    public double getDistance() {
        return distance;
    }

    /**
     * Compares this StationDistance with another.
     * First compares by distance (ascending), then by station name (descending).
     *
     * @param other The other StationDistance to compare to
     * @return negative if this < other, 0 if equal, positive if this > other
     */
    @Override
    public int compareTo(StationDistance other) {
        // First, compare by distance (ascending)
        int distanceComparison = Double.compare(this.distance, other.distance);
        if (distanceComparison != 0) {
            return distanceComparison;
        }

        // If distances are equal, compare by name (descending for tie-breaking)
        return other.station.getName().compareToIgnoreCase(this.station.getName());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StationDistance that = (StationDistance) o;
        return Double.compare(that.distance, distance) == 0 &&
                Objects.equals(station.getName(), that.station.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(station.getName(), distance);
    }

    @Override
    public String toString() {
        return String.format("%s (%.2f km)", station.getName(), distance);
    }
}

