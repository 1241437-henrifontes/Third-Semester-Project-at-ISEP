package Model;

import java.util.Objects;


public class StationDistanceResult implements Comparable<StationDistanceResult> {

    private final RailwayStation station;
    private final double distance;

    public StationDistanceResult(RailwayStation station, double distance) {
        if (station == null) {
            throw new IllegalArgumentException("Station cannot be null.");
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

    @Override
    public int compareTo(StationDistanceResult other) {
        int distCompare = Double.compare(this.distance, other.distance);
        if (distCompare != 0) {
            return distCompare;
        }
        return other.getStation().getName().compareTo(this.getStation().getName());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        StationDistanceResult that = (StationDistanceResult) obj;
        return Double.compare(that.distance, distance) == 0 &&
                Objects.equals(station, that.station);
    }

    @Override
    public int hashCode() {
        return Objects.hash(station, distance);
    }

    @Override
    public String toString() {
        return String.format("%s (%.2f km)", station.getName(), distance);
    }
}