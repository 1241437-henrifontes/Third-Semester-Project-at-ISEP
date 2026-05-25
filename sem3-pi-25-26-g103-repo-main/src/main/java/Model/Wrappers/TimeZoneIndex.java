package Model.Wrappers;

import Model.*;
import Model.Trees.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents an index for a specific {@link TimeZoneGroup}, grouping countries and their stations.
 * Initially stores data in a temporary map for fast insertion, then builds an AVL tree for efficient queries.
 */
public class TimeZoneIndex implements Comparable<TimeZoneIndex> {
    private final TimeZoneGroup timeZoneGroup;
    private AVLTree<CountryIndex> countries;
    private Map<Country, CountryIndex> tempCountries;
    private boolean isBuilt;

    /**
     * Creates a TimeZoneIndex for the given time zone group.
     *
     * @param timeZoneGroup the time zone group
     */
    public TimeZoneIndex(TimeZoneGroup timeZoneGroup) {
        this.timeZoneGroup = timeZoneGroup;
        this.tempCountries = new HashMap<>();
        this.isBuilt = false;
    }

    public TimeZoneGroup getTimeZoneGroup() {
        return timeZoneGroup;
    }

    /**
     * Returns the AVL tree of countries, building it if necessary.
     *
     * @return AVL tree of CountryIndex objects
     */
    public AVLTree<CountryIndex> getCountries() {
        ensureBuilt();
        return countries;
    }

    /**
     * Adds a station quickly when the tree is not yet built.
     * If the tree is built, delegates to {@link #addStation(RailwayStation)}.
     *
     * @param station the station to add
     */
    public void addStationFast(RailwayStation station) {
        if (isBuilt) {
            addStation(station);
        } else {
            Country country = station.getCountry();
            CountryIndex countryIndex = tempCountries.computeIfAbsent(country, CountryIndex::new);

            countryIndex.addStationFast(station);
        }
    }

    /**
     * Adds a station to the AVL tree structure.
     * Builds the tree if it has not been built yet.
     *
     * @param station the station to add
     */
    public void addStation(RailwayStation station) {
        ensureBuilt();
        Country country = station.getCountry();
        CountryIndex countryIndex = new CountryIndex(country);
        AVLTree.Node<CountryIndex> foundNode = countries.find(countries.root(), countryIndex);

        CountryIndex actual;
        if (foundNode == null) {
            countries.insert(countryIndex);
            actual = countryIndex;
        } else {
            actual = foundNode.getElement();
        }

        actual.addStation(station);
    }

    /**
     * Ensures the AVL tree is built from temporary data.
     */
    private void ensureBuilt() {
        if (!isBuilt) {
            buildTree();
        }
    }

    /**
     * Builds the AVL tree from temporary country data.
     * Transfers all CountryIndex objects into the tree and clears the temporary map.
     */
    private void buildTree() {
        if (isBuilt) return;

        countries = new AVLTree<>();

        for (CountryIndex countryIndex : tempCountries.values()) {
            countryIndex.getStationsInOrder();

            countries.insert(countryIndex);
        }

        tempCountries = null;
        isBuilt = true;
    }

    /**
     * Compares two TimeZoneIndex objects by the order of their TimeZoneGroup.
     *
     * @param other the other TimeZoneIndex
     * @return negative, zero, or positive based on comparison
     */
    @Override
    public int compareTo(TimeZoneIndex other) {
        return Integer.compare(this.timeZoneGroup.getOrder(), other.timeZoneGroup.getOrder());
    }

    /**
     * Returns the name of the associated TimeZoneGroup.
     *
     * @return the time zone group name
     */
    @Override
    public String toString() {
        return timeZoneGroup.name();
    }
}
