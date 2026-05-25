package USEI06;

import Model.Wrappers.CountryIndex;
import Model.Country;
import Model.RailwayStation;
import Model.TimeZoneGroup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CountryIndexTest {

    private CountryIndex ci;

    @BeforeEach
    void setUp() {
        ci = new CountryIndex(Country.PT);
    }

    private RailwayStation rs(String name, Country c) {
        return new RailwayStation(name, 0.0, 0.0, c, "WET", TimeZoneGroup.WET_GMT, false, false, false);
    }

    @Test
    void addStationFast() {
        ci.addStationFast(rs("Porto", Country.PT));
        ci.addStationFast(rs("Lisboa", Country.PT));
        List<RailwayStation> list = ci.getStationsInOrder();
        assertEquals(List.of("Lisboa", "Porto"), list.stream().map(RailwayStation::getName).toList());
        ci.addStationFast(rs("Aveiro", Country.PT));
        assertEquals(List.of("Aveiro","Lisboa","Porto"), ci.getStationsInOrder().stream().map(RailwayStation::getName).toList());
    }

    @Test
    void addStation() {
        ci.addStationFast(rs("Porto", Country.PT));
        ci.getStationsInOrder();
        ci.addStation(rs("Braga", Country.PT));
        assertEquals(List.of("Braga","Porto"), ci.getStationsInOrder().stream().map(RailwayStation::getName).toList());
    }

    @Test
    void getStationsInOrder() {
        ci.addStationFast(rs("c", Country.PT));
        ci.addStationFast(rs("a", Country.PT));
        ci.addStationFast(rs("b", Country.PT));
        assertEquals(List.of("a","b","c"), ci.getStationsInOrder().stream().map(RailwayStation::getName).toList());
    }

    @Test
    void getCountry() {
        assertEquals(Country.PT, ci.getCountry());
    }

    @Test
    void compareTo() {
        CountryIndex ci2 = new CountryIndex(Country.ES);
        assertTrue(ci.compareTo(ci2) > 0);
        assertEquals("Portugal", ci.getCountry().getName());
        assertEquals("PT", ci.getCountry().name());
        assertEquals("PT", ci.toString());
    }
    
    @Test
    void duplicateNamesAndListIndependence() {
        ci.addStationFast(rs("alpha", Country.PT));
        ci.addStationFast(rs("alpha", Country.PT));
        var list = ci.getStationsInOrder();
        assertEquals(2, list.size());
        assertEquals("alpha", list.get(0).getName());
        assertEquals("alpha", list.get(1).getName());
        list.clear();
        assertEquals(2, ci.getStationsInOrder().size());
    }

    @Test
    void compareToEqualitySameCountry() {
        CountryIndex ci2 = new CountryIndex(Country.PT);
        assertEquals(0, ci.compareTo(ci2));
        assertEquals("PT", ci.toString());
    }
}