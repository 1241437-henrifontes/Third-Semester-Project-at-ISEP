package USEI06;

import Model.Wrappers.RailwayStationLatitude;
import Model.Country;
import Model.RailwayStation;
import Model.TimeZoneGroup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RailwayStationLatitudeTest {

    private RailwayStationLatitude lat;

    @BeforeEach
    void setUp() {
        lat = new RailwayStationLatitude(41.0);
    }

    private RailwayStation rs(String name) {
        return new RailwayStation(name, 41.0, -8.0, Country.PT, "WET", TimeZoneGroup.WET_GMT, false, false, false);
    }

    @Test
    void addStation() {
        lat.addStation(rs("Porto"));
        lat.addStation(rs("Aveiro"));
        String s = lat.toString();
        assertTrue(s.contains("41.0"));
        assertEquals(java.util.List.of("Aveiro","Porto"), lat.getStations().stream().map(RailwayStation::getName).toList());
    }

    @Test
    void compareTo() {
        RailwayStationLatitude other = new RailwayStationLatitude(42.5);
        assertTrue(lat.compareTo(other) < 0);
        assertEquals(41.0, lat.getLatitude());
    }
    
    @Test
    void tieBreakingByNameAndResortAfterAdd() {
        var s1 = new RailwayStation("b", 41.0, -8.0, Country.PT, "WET", TimeZoneGroup.WET_GMT, false, false, false);
        var s2 = new RailwayStation("A", 41.0, -8.0, Country.PT, "WET", TimeZoneGroup.WET_GMT, false, false, false);
        lat.addStation(s1);
        lat.addStation(s2);
        assertEquals(java.util.List.of("A","b"), lat.getStations().stream().map(RailwayStation::getName).toList());
        lat.addStation(new RailwayStation("aa", 41.0, -8.0, Country.PT, "WET", TimeZoneGroup.WET_GMT, false, false, false));
        assertEquals(java.util.List.of("A","aa","b"), lat.getStations().stream().map(RailwayStation::getName).toList());
    }

    @Test
    void compareToEqualitySameLatitude() {
        RailwayStationLatitude same = new RailwayStationLatitude(41.0);
        assertEquals(0, lat.compareTo(same));
    }
}