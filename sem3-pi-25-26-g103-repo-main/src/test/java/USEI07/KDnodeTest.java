package USEI07;

import Model.Country;
import Model.RailwayStation;
import Model.TimeZoneGroup;
import Model.Trees.KDnode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for KDnode class covering coordinates storage, getters,
 * station list sorting, and child node assignment.
 */
@DisplayName("KDnode Tests")
public class KDnodeTest {

    private KDnode kdNode;
    private RailwayStation station1, station2, station3;
    private List<RailwayStation> stations;
    private final double LATITUDE = 40.5;
    private final double LONGITUDE = -8.2;
    private final int AXIS = 0;

    /**
     * Initializes test objects before each test.
     */
    @BeforeEach
    void setUp() {
        // Create test railway stations
        station1 = new RailwayStation("Station C", LATITUDE, LONGITUDE, Country.PT, "WET", TimeZoneGroup.WET_GMT, false, true, false);
        station2 = new RailwayStation("Station A", LATITUDE, LONGITUDE, Country.PT, "WET", TimeZoneGroup.WET_GMT, true, false, false);
        station3 = new RailwayStation("Station B", LATITUDE, LONGITUDE, Country.PT, "WET", TimeZoneGroup.WET_GMT, false, false, true);

        // Create list of stations (intentionally not sorted)
        stations = new ArrayList<>(Arrays.asList(station1, station2, station3));

        // Create KDnode with test data
        kdNode = new KDnode(LATITUDE, LONGITUDE, stations, AXIS);
    }

    /**
     * Verifies that coordinates are stored correctly in the KDnode.
     */
    @Test
    @DisplayName("Coordinates are stored correctly")
    void testCoordinatesStorage() {
        Point2D.Double coords = kdNode.getCoords();
        assertNotNull(coords);
        assertEquals(LONGITUDE, coords.x);
        assertEquals(LATITUDE, coords.y);
    }

    /**
     * Verifies that the latitude getter returns the expected value.
     */
    @Test
    @DisplayName("Latitude getter returns expected value")
    void testLatitudeGetter() {
        assertEquals(LATITUDE, kdNode.getLatitude());
    }

    /**
     * Verifies that the longitude getter returns the expected value.
     */
    @Test
    @DisplayName("Longitude getter returns expected value")
    void testLongitudeGetter() {
        assertEquals(LONGITUDE, kdNode.getLongitude());
    }

    /**
     * Verifies that the axis value is stored correctly.
     */
    @Test
    @DisplayName("Axis value is stored correctly")
    void testAxisStorage() {
        assertEquals(AXIS, kdNode.getAxis());
    }

    /**
     * Verifies that the stations list is sorted by name during construction.
     */
    @Test
    @DisplayName("Stations list is sorted by name during construction")
    void testStationsListSorting() {
        List<RailwayStation> sortedStations = kdNode.getStations();
        assertEquals(3, sortedStations.size());
        assertEquals("Station A", sortedStations.get(0).getName());
        assertEquals("Station B", sortedStations.get(1).getName());
        assertEquals("Station C", sortedStations.get(2).getName());
    }

    /**
     * Verifies that the stations list remains sorted after construction.
     */
    @Test
    @DisplayName("Stations list remains sorted after construction")
    void testStationsListRemainsSorted() {
        List<RailwayStation> differentOrderStations = new ArrayList<>(Arrays.asList(station3, station1, station2));
        KDnode newNode = new KDnode(LATITUDE, LONGITUDE, differentOrderStations, AXIS);

        List<RailwayStation> sortedStations = newNode.getStations();
        assertEquals(3, sortedStations.size());
        assertEquals("Station A", sortedStations.get(0).getName());
        assertEquals("Station B", sortedStations.get(1).getName());
        assertEquals("Station C", sortedStations.get(2).getName());
    }

    /**
     * Verifies that left child assignment works correctly.
     */
    @Test
    @DisplayName("Left child assignment works correctly")
    void testLeftChildAssignment() {
        assertNull(kdNode.getLeft());


        KDnode leftChild = new KDnode(LATITUDE - 1, LONGITUDE - 1, new ArrayList<>(), 1);


        kdNode.setLeft(leftChild);


        assertNotNull(kdNode.getLeft());
        assertEquals(leftChild, kdNode.getLeft());
        assertEquals(LATITUDE - 1, kdNode.getLeft().getLatitude());
        assertEquals(LONGITUDE - 1, kdNode.getLeft().getLongitude());
    }

    /**
     * Verifies that right child assignment works correctly.
     */
    @Test
    @DisplayName("Right child assignment works correctly")
    void testRightChildAssignment() {

        assertNull(kdNode.getRight());


        KDnode rightChild = new KDnode(LATITUDE + 1, LONGITUDE + 1, new ArrayList<>(), 1);


        kdNode.setRight(rightChild);


        assertNotNull(kdNode.getRight());
        assertEquals(rightChild, kdNode.getRight());
        assertEquals(LATITUDE + 1, kdNode.getRight().getLatitude());
        assertEquals(LONGITUDE + 1, kdNode.getRight().getLongitude());
    }

    /**
     * Verifies that toString method returns a string representation with correct values.
     */
    @Test
    @DisplayName("toString returns correct string representation")
    void testToString() {
        String nodeString = kdNode.toString();
        assertTrue(nodeString.contains(String.valueOf(LONGITUDE)));
        assertTrue(nodeString.contains(String.valueOf(LATITUDE)));
        assertTrue(nodeString.contains(String.valueOf(AXIS)));
    }
}
