package Model.Wrappers;

import Model.RailwayStation;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Represents a latitude wrapper that groups railway stations sharing the same latitude.
 * Implements {@link Comparable} to allow ordering by latitude in data structures.
 */
public class RailwayStationLatitude implements Comparable<RailwayStationLatitude> {
    private final double latitude;
    private final List<RailwayStation> stations;
    private boolean sorted;

    /**
     * Creates a latitude wrapper without stations.
     *
     * @param latitude the latitude value
     */
    public RailwayStationLatitude(double latitude) {
        this.latitude = latitude;
        this.stations = new ArrayList<>();
        this.sorted = true;
    }

    /**
     * Creates a latitude wrapper with an initial station.
     *
     * @param latitude the latitude value
     * @param station the station to add
     */
    public RailwayStationLatitude(double latitude, RailwayStation station) {
        this.latitude = latitude;
        this.stations = new ArrayList<>();
        this.stations.add(station);
        this.sorted = true;
    }

    public double getLatitude() {
        return latitude;
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
     * Adds a station to this latitude group.
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
     * Compares this latitude wrapper to another by latitude value.
     *
     * @param other the other latitude wrapper
     * @return negative, zero, or positive based on comparison
     */
    @Override
    public int compareTo(RailwayStationLatitude other) {
        return Double.compare(this.latitude, other.latitude);
    }

    /**
     * Returns a string representation in the format:
     * "latitude -> [stations]".
     *
     * @return formatted string
     */
    @Override
    public String toString() {
        ensureSorted();
        return latitude + " -> " + stations;
    }
}
