package USEI06;

import Model.Wrappers.CountryIndex;
import Model.Wrappers.TimeZoneIndex;
import Model.Country;
import Model.RailwayStation;
import Model.TimeZoneGroup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TimeZoneIndexTest {

    private TimeZoneIndex tzi;

    @BeforeEach
    void setUp() {
        tzi = new TimeZoneIndex(TimeZoneGroup.CET);
    }

    private RailwayStation rs(String name, Country c, TimeZoneGroup g) {
        return new RailwayStation(name, 0.0, 0.0, c, g.getName(), g, false, false, false);
    }

    @Test
    void addStationFast() {
        tzi.addStationFast(rs("Madrid", Country.ES, TimeZoneGroup.CET));
        tzi.addStationFast(rs("Lisboa", Country.PT, TimeZoneGroup.WET_GMT));
        var tree = tzi.getCountries();
        var inOrder = tree.inOrder();
        java.util.List<String> names = new java.util.ArrayList<>();
        for (CountryIndex ci : inOrder) names.add(ci.getCountry().name());
        assertArrayEquals(new String[]{"ES","PT"}, names.toArray(new String[0]));
    }

    @Test
    void addStation() {
        tzi.addStationFast(rs("Zaragoza", Country.ES, TimeZoneGroup.CET));
        tzi.getCountries();
        tzi.addStation(rs("Barcelona", Country.ES, TimeZoneGroup.CET));
        var esNode = tzi.getCountries().find(tzi.getCountries().root(), new CountryIndex(Country.ES));
        assertNotNull(esNode);
        assertEquals(java.util.List.of("Barcelona","Zaragoza"), esNode.getElement().getStationsInOrder().stream().map(RailwayStation::getName).toList());
    }

    @Test
    void compareTo() {
        TimeZoneIndex a = new TimeZoneIndex(TimeZoneGroup.WET_GMT);
        TimeZoneIndex b = new TimeZoneIndex(TimeZoneGroup.CET);
        assertTrue(a.compareTo(b) < 0);
        assertEquals("CET", tzi.toString());
    }
    
    @Test
    void emptyIndexHasNoCountries() {
        TimeZoneIndex empty = new TimeZoneIndex(TimeZoneGroup.FET);
        assertTrue(empty.getCountries().inOrder().iterator().hasNext() == false);
        assertEquals(0, empty.compareTo(new TimeZoneIndex(TimeZoneGroup.FET)));
        assertEquals("FET", empty.toString());
    }

    @Test
    void addNewCountryAfterBuild() {
        tzi.addStationFast(rs("Zaragoza", Country.ES, TimeZoneGroup.CET));
        tzi.getCountries();
        tzi.addStation(rs("Berlin", Country.DE, TimeZoneGroup.CET));
        var deNode = tzi.getCountries().find(tzi.getCountries().root(), new CountryIndex(Country.DE));
        assertNotNull(deNode);
        assertEquals(java.util.List.of("Berlin"), deNode.getElement().getStationsInOrder().stream().map(RailwayStation::getName).toList());
    }
}