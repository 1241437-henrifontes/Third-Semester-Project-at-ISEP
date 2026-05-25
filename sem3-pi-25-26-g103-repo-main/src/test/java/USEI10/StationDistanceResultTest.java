package USEI10;

import Model.Country;
import Model.RailwayStation;
import Model.StationDistanceResult;
import Model.TimeZoneGroup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class StationDistanceResultTest {

    private RailwayStation stationA;
    private RailwayStation stationB;
    private RailwayStation stationZ;

    @BeforeEach
    void setUp() {
        stationA = new RailwayStation("Aarhus", 56.0, 10.0, Country.DK, "CET", TimeZoneGroup.CET, true, false, false);
        stationB = new RailwayStation("Berlin", 52.0, 13.0, Country.DE, "CET", TimeZoneGroup.CET, true, true, false);
        stationZ = new RailwayStation("Zurich", 47.0, 8.0, Country.CH, "CET", TimeZoneGroup.CET, true, true, false);
    }

    @Test
    void testConstructor_ValidInput() {
        double dist = 15.5;
        StationDistanceResult result = new StationDistanceResult(stationA, dist);

        assertNotNull(result);
        assertEquals(stationA, result.getStation());
        assertEquals(dist, result.getDistance());
    }

    @Test
    void testConstructor_NullStation_ThrowsException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new StationDistanceResult(null, 10.0);
        });
        assertEquals("Station cannot be null.", exception.getMessage());
    }

    @Test
    void testConstructor_NegativeDistance_Allowed() {
        StationDistanceResult result = new StationDistanceResult(stationA, -5.0);
        assertEquals(-5.0, result.getDistance());
    }

    @Test
    void testCompareTo_DifferentDistances() {
        StationDistanceResult r1 = new StationDistanceResult(stationZ, 2.0);
        StationDistanceResult r2 = new StationDistanceResult(stationA, 10.0);

        assertTrue(r1.compareTo(r2) < 0);
        assertTrue(r2.compareTo(r1) > 0);
    }

    @Test
    void testCompareTo_SameDistance_DifferentNames() {
        StationDistanceResult rZ = new StationDistanceResult(stationZ, 10.0);
        StationDistanceResult rA = new StationDistanceResult(stationA, 10.0);

        assertTrue(rZ.compareTo(rA) < 0);
        assertTrue(rA.compareTo(rZ) > 0);
    }

    @Test
    void testCompareTo_SameDistance_SameName() {
        StationDistanceResult r1 = new StationDistanceResult(stationA, 10.0);
        StationDistanceResult r2 = new StationDistanceResult(stationA, 10.0);

        assertEquals(0, r1.compareTo(r2));
    }

    @Test
    void testCompareTo_LogicVerification() {
        StationDistanceResult rSmallDist = new StationDistanceResult(stationZ, 5.0);
        StationDistanceResult rBigDist = new StationDistanceResult(stationA, 20.0);

        StationDistanceResult rTieZ = new StationDistanceResult(stationZ, 10.0);
        StationDistanceResult rTieA = new StationDistanceResult(stationA, 10.0);

        assertTrue(rSmallDist.compareTo(rTieZ) < 0);
        assertTrue(rTieZ.compareTo(rTieA) < 0);
        assertTrue(rTieA.compareTo(rBigDist) < 0);
    }

    @Test
    void testEquals() {
        StationDistanceResult r1 = new StationDistanceResult(stationA, 10.0);
        StationDistanceResult r2 = new StationDistanceResult(stationA, 10.0);
        StationDistanceResult r3 = new StationDistanceResult(stationA, 20.0);
        StationDistanceResult r4 = new StationDistanceResult(stationB, 10.0);

        assertEquals(r1, r1);

        assertEquals(r1, r2);
        assertEquals(r2, r1);

        assertNotEquals(r1, r3);
        assertNotEquals(r1, r4);
        assertNotEquals(r1, null);
        assertNotEquals(r1, "Uma String");
    }

    @Test
    void testHashCode() {
        StationDistanceResult r1 = new StationDistanceResult(stationA, 10.0);
        StationDistanceResult r2 = new StationDistanceResult(stationA, 10.0);

        assertEquals(r1.hashCode(), r2.hashCode());
    }

    @Test
    void testToString() {
        StationDistanceResult result = new StationDistanceResult(stationA, 12.3456);
        String output = result.toString();

        assertTrue(output.contains("Aarhus"));

        boolean hasPoint = output.contains("12.35");
        boolean hasComma = output.contains("12,35");
        assertTrue(hasPoint || hasComma);
    }
}