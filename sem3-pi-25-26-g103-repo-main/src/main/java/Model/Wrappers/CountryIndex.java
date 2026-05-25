package Model.Wrappers;

import Model.*;
import Model.Trees.AVLTree;

import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;

/**
 * Index of stations for a specific {@link Country}. Supports a two-phase
 * build approach:
 * <ul>
 *   <li><b>Temporary phase</b>: accumulate stations in a list for fast insertion ({@link #addStationFast(RailwayStation)}).</li>
 *   <li><b>Final phase</b>: build an {@link AVLTree} of {@link RailwayStationByName} for efficient ordered traversal ({@link #addStation(RailwayStation)}).</li>
 * </ul>
 */
public class CountryIndex implements Comparable<CountryIndex> {
    private final Country country;
    private AVLTree<RailwayStationByName> stations;
    private List<RailwayStation> tempStations;
    private boolean isBuilt;

    /**
     * Creates a {@code CountryIndex} for the given country.
     * Starts in the temporary phase (tree not built).
     *
     * @param country the country this index represents
     */
    public CountryIndex(Country country) {
        this.country = country;
        this.tempStations = new ArrayList<>();
        this.isBuilt = false;
    }

    /**
     * Adds a station in the most performant way when the tree is not yet built.
     * If the tree is already built, delegates to {@link #addStation(RailwayStation)}.
     *
     * @param station the station to add
     */
    public void addStationFast(RailwayStation station) {
        if (isBuilt) {
            addStation(station);
        } else {
            tempStations.add(station);
        }
    }

    /**
     * Adds a station to the AVL tree structure. Ensures the tree is built first.
     *
     * @param station the station to add
     */
    public void addStation(RailwayStation station) {
        ensureBuilt();
        stations.insert(new RailwayStationByName(station));
    }

    /**
     * Ensures the final AVL tree is built from the temporary list.
     * If not built, invokes {@link #buildTree()}.
     */
    private void ensureBuilt() {
        if (!isBuilt) {
            buildTree();
        }
    }

    /**
     * Builds the AVL tree from the temporarily stored stations, sorting by name
     * before insertion to keep ordering consistent.
     *
     * <p>After building, the temporary list is cleared and the index enters the
     * final phase.</p>
     */
    private void buildTree() {
        if (isBuilt) return;

        stations = new AVLTree<>();

        tempStations.sort(Comparator.comparing(RailwayStation::getName));

        for (RailwayStation station : tempStations) {
            stations.insert(new RailwayStationByName(station));
        }

        tempStations = null;
        isBuilt = true;
    }

    /**
     * Returns all stations in-order (by name) from the underlying AVL tree.
     * Ensures the tree is built before traversal.
     *
     * @return ordered list of stations
     */
    public List<RailwayStation> getStationsInOrder() {
        ensureBuilt();
        List<RailwayStation> ordered = new ArrayList<>();
        inOrderTraversal(stations.root(), ordered);
        return ordered;
    }

    /**
     * Performs an in-order traversal over the AVL tree, accumulating stations.
     *
     * @param node current tree node
     * @param acc accumulator list to collect stations in order
     */
    private void inOrderTraversal(AVLTree.Node<RailwayStationByName> node, List<RailwayStation> acc) {
        if (node == null) return;
        inOrderTraversal(node.getLeft(), acc);
        acc.add(node.getElement().station());
        inOrderTraversal(node.getRight(), acc);
    }

    public Country getCountry() {
        return country;
    }

    /**
     * Compares indexes lexicographically by country name.
     *
     * @param other another {@code CountryIndex}
     * @return a negative, zero, or positive integer as this country name is less than,
     *         equal to, or greater than the specified country name
     */
    @Override
    public int compareTo(CountryIndex other) {
        return this.country.name().compareTo(other.country.name());
    }

    /**
     * Returns the country's name.
     *
     * @return country name
     */
    @Override
    public String toString() {
        return country.name();
    }
}
