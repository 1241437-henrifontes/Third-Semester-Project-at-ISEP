package Model.Wrappers;

import Model.RailwayStation;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Represents a longitude wrapper that groups railway stations sharing the same longitude.
 * Implements {@link Comparable} to allow ordering by longitude in data structures.
 */
public class RailwayStationLongitude implements Comparable<RailwayStationLongitude> {
    private final double longitude;
    private final List<RailwayStation> stations;
    private boolean sorted;

    /**
     * Creates a longitude wrapper without stations.
     *
     * @param longitude the longitude value
     */
    public RailwayStationLongitude(double longitude) {
        this.longitude = longitude;
        this.stations = new ArrayList<>();
        this.sorted = true;
    }

    /**
     * Creates a longitude wrapper with an initial station.
     *
     * @param longitude the longitude value
     * @param station the station to add
     */
    public RailwayStationLongitude(double longitude, RailwayStation station) {
        this.longitude = longitude;
        this.stations = new ArrayList<>();
        this.stations.add(station);
        this.sorted = true;
    }

    public double getLongitude() {
        return longitude;
    }

    /**
     * Returns the list of stations sorted by name.
     * Sorting is applied lazily if new stations were added.
     *
     * @return sorted list of stations
     */
    public List<RailwayStation> getStations() {
        ensureSorted();
        return stations;
    }

    /**
     * Adds a station to this longitude group.
     * Marks the internal list as unsorted until next retrieval.
     *
     * @param station the station to add
     */
    public void addStation(RailwayStation station) {
        stations.add(station);
        sorted = false;
    }

    private void ensureSorted() {
        if (!sorted) {
            stations.sort(Comparator.comparing(RailwayStation::getName));
            sorted = true;
        }
    }

    /**
     * Compares this longitude wrapper to another by longitude value.
     *
     * @param other the other longitude wrapper
     * @return negative, zero, or positive based on comparison
     */
    @Override
    public int compareTo(RailwayStationLongitude other) {
        return Double.compare(this.longitude, other.longitude);
    }

    /**
     * Returns a string representation in the format:
     * "longitude -> [stations]".
     *
     * @return formatted string
     */
    @Override
    public String toString() {
        ensureSorted();
        return longitude + " -> " + stations;
    }
}
