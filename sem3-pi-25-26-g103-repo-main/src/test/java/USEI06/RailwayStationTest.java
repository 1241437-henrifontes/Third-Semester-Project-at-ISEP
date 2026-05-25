package USEI06;

import Model.Country;
import Model.RailwayStation;
import Model.TimeZoneGroup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RailwayStationTest {

    private RailwayStation a;
    private RailwayStation b;

    @BeforeEach
    void setUp() {
        a = new RailwayStation("Porto", 41.15, -8.61, Country.PT, "WET", TimeZoneGroup.WET_GMT, true, true, false);
        b = new RailwayStation("porto", 41.15, -8.61, Country.PT, "WET", TimeZoneGroup.WET_GMT, true, true, false);
    }

    @Test
    void compareTo() {
        assertEquals(0, a.compareTo(b));
        RailwayStation c = new RailwayStation("Aveiro", 40.64, -8.65, Country.PT, "WET", TimeZoneGroup.WET_GMT, false, false, false);
        assertTrue(c.compareTo(a) < 0);
    }

    @Test
    void testEquals() {
        assertNotEquals(a, b);
        RailwayStation aCopy = new RailwayStation("Porto", 41.15, -8.61, Country.PT, "WET", TimeZoneGroup.WET_GMT, true, true, false);
        assertEquals(a, aCopy);
        assertEquals(a.hashCode(), aCopy.hashCode());
        assertNotEquals(a, null);
        assertNotEquals(a, new Object());
        String s = a.toString();
        assertTrue(s.contains("Porto"));
        assertTrue(s.contains("latitude"));
        assertTrue(s.contains("timezoneGroup"));
    }
}