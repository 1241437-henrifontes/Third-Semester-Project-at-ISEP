package USEI09;

import Model.Country;
import Model.RailwayStation;
import Model.StationDistance;
import Model.TimeZoneGroup;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class StationDistanceTest {

    private RailwayStation station1;
    private RailwayStation station2;
    private RailwayStation station3;
    private StationDistance stationDistance1;
    private StationDistance stationDistance2;

    @BeforeEach
    void setUp() {
        station1 = new RailwayStation("Lisboa", 38.7223, -9.1393, Country.PT, "Europe/Lisbon", TimeZoneGroup.WET_GMT, true, true, false);
        station2 = new RailwayStation("Porto", 41.1496, -8.6109, Country.PT, "Europe/Lisbon", TimeZoneGroup.WET_GMT, true, true, false);
        station3 = new RailwayStation("Madrid", 40.4168, -3.7038, Country.ES, "Europe/Madrid", TimeZoneGroup.CET, true, true, false);

        stationDistance1 = new StationDistance(station1, 100.5);
        stationDistance2 = new StationDistance(station2, 200.75);
    }

    @AfterEach
    void tearDown() {
        station1 = null;
        station2 = null;
        station3 = null;
        stationDistance1 = null;
        stationDistance2 = null;
    }

    @Test
    void testConstructorWithValidParameters() {
        StationDistance sd = new StationDistance(station1, 50.0);
        assertNotNull(sd);
        assertEquals(station1, sd.getStation());
        assertEquals(50.0, sd.getDistance(), 0.001);
    }

    @Test
    void testConstructorWithNullStation() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new StationDistance(null, 100.0);
        });
        assertEquals("Station cannot be null", exception.getMessage());
    }

    @Test
    void testConstructorWithNegativeDistance() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new StationDistance(station1, -50.0);
        });
        assertEquals("Distance cannot be negative", exception.getMessage());
    }

    @Test
    void testConstructorWithZeroDistance() {
        StationDistance sd = new StationDistance(station1, 0.0);
        assertNotNull(sd);
        assertEquals(0.0, sd.getDistance(), 0.001);
    }

    @Test
    void testGetStation() {
        assertEquals(station1, stationDistance1.getStation());
        assertEquals("Lisboa", stationDistance1.getStation().getName());
    }

    @Test
    void testGetDistance() {
        assertEquals(100.5, stationDistance1.getDistance(), 0.001);
        assertEquals(200.75, stationDistance2.getDistance(), 0.001);
    }

    @Test
    void testCompareToSameDistance() {
        StationDistance sd1 = new StationDistance(station1, 100.0);
        StationDistance sd2 = new StationDistance(station2, 100.0);

        // When distances are equal, should compare by name descending
        // "Porto" > "Lisboa" alphabetically, so sd2 should come before sd1
        assertTrue(sd1.compareTo(sd2) > 0);
        assertTrue(sd2.compareTo(sd1) < 0);
    }

    @Test
    void testCompareToDifferentDistances() {
        // stationDistance1 has distance 100.5
        // stationDistance2 has distance 200.75
        // Lower distance should come first
        assertTrue(stationDistance1.compareTo(stationDistance2) < 0);
        assertTrue(stationDistance2.compareTo(stationDistance1) > 0);
    }

    @Test
    void testCompareToSameObject() {
        assertEquals(0, stationDistance1.compareTo(stationDistance1));
    }

    @Test
    void testCompareToAscendingOrder() {
        StationDistance sd1 = new StationDistance(station1, 50.0);
        StationDistance sd2 = new StationDistance(station2, 100.0);
        StationDistance sd3 = new StationDistance(station3, 150.0);

        assertTrue(sd1.compareTo(sd2) < 0);
        assertTrue(sd2.compareTo(sd3) < 0);
        assertTrue(sd1.compareTo(sd3) < 0);
    }

    @Test
    void testEqualsWithSameStationAndDistance() {
        StationDistance sd1 = new StationDistance(station1, 100.0);
        StationDistance sd2 = new StationDistance(station1, 100.0);

        assertEquals(sd1, sd2);
    }

    @Test
    void testEqualsWithDifferentDistance() {
        StationDistance sd1 = new StationDistance(station1, 100.0);
        StationDistance sd2 = new StationDistance(station1, 200.0);

        assertNotEquals(sd1, sd2);
    }

    @Test
    void testEqualsWithDifferentStation() {
        StationDistance sd1 = new StationDistance(station1, 100.0);
        StationDistance sd2 = new StationDistance(station2, 100.0);

        assertNotEquals(sd1, sd2);
    }

    @Test
    void testEqualsWithSameObject() {
        assertEquals(stationDistance1, stationDistance1);
    }

    @Test
    void testEqualsWithNull() {
        assertNotEquals(null, stationDistance1);
    }

    @Test
    void testEqualsWithDifferentClass() {
        assertNotEquals(stationDistance1, "NotAStationDistance");
    }

    @Test
    void testHashCodeConsistency() {
        StationDistance sd1 = new StationDistance(station1, 100.0);
        StationDistance sd2 = new StationDistance(station1, 100.0);

        assertEquals(sd1.hashCode(), sd2.hashCode());
    }

    @Test
    void testHashCodeDifferentForDifferentObjects() {
        assertNotEquals(stationDistance1.hashCode(), stationDistance2.hashCode());
    }

    @Test
    void testToString() {
        String result = stationDistance1.toString();
        assertNotNull(result);
        assertTrue(result.contains("Lisboa"));
        assertTrue(result.contains("km"));
    }

    @Test
    void testToStringFormat() {
        StationDistance sd = new StationDistance(station1, 123.456);
        String result = sd.toString();
        assertNotNull(result);
        assertTrue(result.contains("Lisboa"));
        assertTrue(result.contains("km"));
    }

    @Test
    void testCompareToWithVerySmallDifference() {
        StationDistance sd1 = new StationDistance(station1, 100.0001);
        StationDistance sd2 = new StationDistance(station1, 100.0002);

        assertTrue(sd1.compareTo(sd2) < 0);
    }

    @Test
    void testMultipleStationsOrdering() {
        StationDistance sd1 = new StationDistance(station1, 50.0);
        StationDistance sd2 = new StationDistance(station2, 100.0);
        StationDistance sd3 = new StationDistance(station3, 75.0);

        assertTrue(sd1.compareTo(sd2) < 0);
        assertTrue(sd1.compareTo(sd3) < 0);
        assertTrue(sd3.compareTo(sd2) < 0);
    }
}
