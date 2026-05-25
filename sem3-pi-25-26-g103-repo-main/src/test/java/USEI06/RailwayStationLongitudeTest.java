package USEI06;

import Model.Wrappers.RailwayStationLongitude;
import Model.Country;
import Model.RailwayStation;
import Model.TimeZoneGroup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RailwayStationLongitudeTest {

    private RailwayStationLongitude lon;

    @BeforeEach
    void setUp() {
        lon = new RailwayStationLongitude(-8.0);
    }

    private RailwayStation rs(String name) {
        return new RailwayStation(name, 41.0, -8.0, Country.PT, "WET", TimeZoneGroup.WET_GMT, false, false, false);
    }

    @Test
    void addStation() {
        lon.addStation(rs("Porto"));
        lon.addStation(rs("Aveiro"));
        String s = lon.toString();
        assertTrue(s.contains("-8.0"));
        assertEquals(java.util.List.of("Aveiro","Porto"), lon.getStations().stream().map(RailwayStation::getName).toList());
    }

    @Test
    void compareTo() {
        RailwayStationLongitude other = new RailwayStationLongitude(0.0);
        assertTrue(lon.compareTo(other) < 0);
        assertEquals(-8.0, lon.getLongitude());
    }
    
    @Test
    void tieBreakingByNameAndResortAfterAdd() {
        var s1 = new RailwayStation("b", 41.0, -8.0, Country.PT, "WET", TimeZoneGroup.WET_GMT, false, false, false);
        var s2 = new RailwayStation("A", 41.0, -8.0, Country.PT, "WET", TimeZoneGroup.WET_GMT, false, false, false);
        lon.addStation(s1);
        lon.addStation(s2);
        assertEquals(java.util.List.of("A","b"), lon.getStations().stream().map(RailwayStation::getName).toList());
        lon.addStation(new RailwayStation("aa", 41.0, -8.0, Country.PT, "WET", TimeZoneGroup.WET_GMT, false, false, false));
        assertEquals(java.util.List.of("A","aa","b"), lon.getStations().stream().map(RailwayStation::getName).toList());
    }

    @Test
    void compareToEqualitySameLongitude() {
        RailwayStationLongitude same = new RailwayStationLongitude(-8.0);
        assertEquals(0, lon.compareTo(same));
    }
}