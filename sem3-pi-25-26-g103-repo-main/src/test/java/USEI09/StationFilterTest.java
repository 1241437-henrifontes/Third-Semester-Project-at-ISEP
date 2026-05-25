package USEI09;

import Model.Country;
import Model.Filters.StationFilter;
import Model.RailwayStation;
import Model.TimeZoneGroup;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class StationFilterTest {

    private RailwayStation stationCity;
    private RailwayStation stationMainStation;
    private RailwayStation stationAirport;
    private RailwayStation stationCET;
    private RailwayStation stationEET;
    private RailwayStation stationWET;
    private StationFilter filter;

    @BeforeEach
    void setUp() {
        stationCity = new RailwayStation("Lisboa", 38.7223, -9.1393, Country.PT, "Europe/Lisbon", TimeZoneGroup.WET_GMT, true, true, false);
        stationMainStation = new RailwayStation("Porto", 41.1496, -8.6109, Country.PT, "Europe/Lisbon", TimeZoneGroup.WET_GMT, true, true, false);
        stationAirport = new RailwayStation("Airport Station", 38.7613, -9.1283, Country.PT, "Europe/Lisbon", TimeZoneGroup.WET_GMT, false, false, true);
        stationCET = new RailwayStation("Madrid", 40.4168, -3.7038, Country.ES, "Europe/Madrid", TimeZoneGroup.CET, true, true, false);
        stationEET = new RailwayStation("Athens", 37.9838, 23.7275, Country.GR, "Europe/Athens", TimeZoneGroup.EET, true, true, false);
        stationWET = new RailwayStation("London", 51.5074, -0.1278, Country.GB, "Europe/London", TimeZoneGroup.WET_GMT, true, true, false);

        filter = new StationFilter();
    }

    @AfterEach
    void tearDown() {
        stationCity = null;
        stationMainStation = null;
        stationAirport = null;
        stationCET = null;
        stationEET = null;
        stationWET = null;
        filter = null;
    }

    @Test
    void testDefaultConstructor() {
        StationFilter sf = new StationFilter();
        assertNotNull(sf);
        assertNull(sf.getMinTimeZone());
        assertNull(sf.getMaxTimeZone());
        assertNull(sf.getIsCity());
        assertNull(sf.getIsMainStation());
        assertNull(sf.getIsAirport());
    }

    @Test
    void testWithTimeZoneRange() {
        StationFilter sf = filter.withTimeZoneRange(TimeZoneGroup.WET_GMT, TimeZoneGroup.CET);

        assertNotNull(sf);
        assertEquals(TimeZoneGroup.WET_GMT, sf.getMinTimeZone());
        assertEquals(TimeZoneGroup.CET, sf.getMaxTimeZone());
    }

    @Test
    void testWithIsCity() {
        StationFilter sf = filter.withIsCity(true);

        assertNotNull(sf);
        assertEquals(true, sf.getIsCity());
    }

    @Test
    void testWithIsMainStation() {
        StationFilter sf = filter.withIsMainStation(true);

        assertNotNull(sf);
        assertEquals(true, sf.getIsMainStation());
    }

    @Test
    void testWithIsAirport() {
        StationFilter sf = filter.withIsAirport(true);

        assertNotNull(sf);
        assertEquals(true, sf.getIsAirport());
    }

    @Test
    void testFluentInterfaceChaining() {
        StationFilter sf = new StationFilter()
                .withTimeZoneRange(TimeZoneGroup.WET_GMT, TimeZoneGroup.CET)
                .withIsCity(true)
                .withIsMainStation(true);

        assertEquals(TimeZoneGroup.WET_GMT, sf.getMinTimeZone());
        assertEquals(TimeZoneGroup.CET, sf.getMaxTimeZone());
        assertEquals(true, sf.getIsCity());
        assertEquals(true, sf.getIsMainStation());
    }

    @Test
    void testMatchesWithNoFilters() {
        assertTrue(filter.matches(stationCity));
        assertTrue(filter.matches(stationMainStation));
        assertTrue(filter.matches(stationAirport));
        assertTrue(filter.matches(stationCET));
    }

    @Test
    void testMatchesWithNullStation() {
        assertFalse(filter.matches(null));
    }

    @Test
    void testMatchesWithCityFilter() {
        filter.withIsCity(true);

        assertTrue(filter.matches(stationCity));
        assertTrue(filter.matches(stationMainStation));
        assertFalse(filter.matches(stationAirport));
    }

    @Test
    void testMatchesWithMainStationFilter() {
        filter.withIsMainStation(true);

        assertTrue(filter.matches(stationCity));
        assertTrue(filter.matches(stationMainStation));
        assertFalse(filter.matches(stationAirport));
    }

    @Test
    void testMatchesWithAirportFilter() {
        filter.withIsAirport(true);

        assertTrue(filter.matches(stationAirport));
        assertFalse(filter.matches(stationCity));
        assertFalse(filter.matches(stationMainStation));
    }

    @Test
    void testMatchesWithTimeZoneFilter() {
        filter.withTimeZoneRange(TimeZoneGroup.WET_GMT, TimeZoneGroup.WET_GMT);

        assertTrue(filter.matches(stationWET));
        assertTrue(filter.matches(stationCity));
        assertFalse(filter.matches(stationCET));
        assertFalse(filter.matches(stationEET));
    }

    @Test
    void testMatchesWithTimeZoneRangeWETToCET() {
        filter.withTimeZoneRange(TimeZoneGroup.WET_GMT, TimeZoneGroup.CET);

        assertTrue(filter.matches(stationWET));
        assertTrue(filter.matches(stationCET));
        assertFalse(filter.matches(stationEET));
    }

    @Test
    void testMatchesWithTimeZoneRangeCETToEET() {
        filter.withTimeZoneRange(TimeZoneGroup.CET, TimeZoneGroup.EET);

        assertTrue(filter.matches(stationCET));
        assertTrue(filter.matches(stationEET));
        assertFalse(filter.matches(stationWET));
    }

    @Test
    void testMatchesWithMultipleFilters() {
        filter.withIsCity(true).withIsMainStation(true).withTimeZoneRange(TimeZoneGroup.WET_GMT, TimeZoneGroup.WET_GMT);

        assertTrue(filter.matches(stationCity));
        assertTrue(filter.matches(stationMainStation));
        assertFalse(filter.matches(stationAirport));
        assertFalse(filter.matches(stationCET));
    }

    @Test
    void testMatchesWithConflictingFilters() {
        filter.withIsCity(true).withIsAirport(true);

        assertFalse(filter.matches(stationCity));
        assertFalse(filter.matches(stationAirport));
    }

    @Test
    void testMatchesIsCityFalse() {
        filter.withIsCity(false);

        assertFalse(filter.matches(stationCity));
        assertTrue(filter.matches(stationAirport));
    }

    @Test
    void testMatchesIsMainStationFalse() {
        filter.withIsMainStation(false);

        assertFalse(filter.matches(stationCity));
        assertTrue(filter.matches(stationAirport));
    }

    @Test
    void testMatchesIsAirportFalse() {
        filter.withIsAirport(false);

        assertTrue(filter.matches(stationCity));
        assertFalse(filter.matches(stationAirport));
    }

    @Test
    void testMatchesWithNullTimeZoneInStation() {
        RailwayStation stationNoTimeZone = new RailwayStation("NoTimeZone", 40.0, -8.0, Country.PT, "Europe/Lisbon", null, true, false, false);

        filter.withTimeZoneRange(TimeZoneGroup.WET_GMT, TimeZoneGroup.CET);

        assertFalse(filter.matches(stationNoTimeZone));
    }

    @Test
    void testGetMinTimeZone() {
        filter.withTimeZoneRange(TimeZoneGroup.WET_GMT, TimeZoneGroup.CET);
        assertEquals(TimeZoneGroup.WET_GMT, filter.getMinTimeZone());
    }

    @Test
    void testGetMaxTimeZone() {
        filter.withTimeZoneRange(TimeZoneGroup.WET_GMT, TimeZoneGroup.CET);
        assertEquals(TimeZoneGroup.CET, filter.getMaxTimeZone());
    }

    @Test
    void testGetIsCity() {
        filter.withIsCity(true);
        assertEquals(true, filter.getIsCity());
    }

    @Test
    void testGetIsMainStation() {
        filter.withIsMainStation(false);
        assertEquals(false, filter.getIsMainStation());
    }

    @Test
    void testGetIsAirport() {
        filter.withIsAirport(true);
        assertEquals(true, filter.getIsAirport());
    }

    @Test
    void testAllFiltersNull() {
        assertNull(filter.getMinTimeZone());
        assertNull(filter.getMaxTimeZone());
        assertNull(filter.getIsCity());
        assertNull(filter.getIsMainStation());
        assertNull(filter.getIsAirport());
    }

    @Test
    void testFilterReturnsSameInstance() {
        StationFilter sf = filter.withIsCity(true);
        assertSame(filter, sf);
    }

    @Test
    void testComplexFilterScenario() {
        filter.withTimeZoneRange(TimeZoneGroup.WET_GMT, TimeZoneGroup.EET)
              .withIsCity(true)
              .withIsMainStation(true)
              .withIsAirport(false);

        assertTrue(filter.matches(stationCity));
        assertTrue(filter.matches(stationCET));
        assertFalse(filter.matches(stationAirport));
    }

    @Test
    void testTimeZoneFilterWithSameMinMax() {
        filter.withTimeZoneRange(TimeZoneGroup.CET, TimeZoneGroup.CET);

        assertTrue(filter.matches(stationCET));
        assertFalse(filter.matches(stationWET));
        assertFalse(filter.matches(stationEET));
    }

    @Test
    void testFilterWithAllTimeZones() {
        filter.withTimeZoneRange(TimeZoneGroup.WET_GMT, TimeZoneGroup.FET);

        assertTrue(filter.matches(stationWET));
        assertTrue(filter.matches(stationCET));
        assertTrue(filter.matches(stationEET));
    }

    @Test
    void testMultipleFilterApplications() {
        filter.withIsCity(true);
        assertTrue(filter.matches(stationCity));

        filter.withIsMainStation(true);
        assertTrue(filter.matches(stationCity));

        filter.withIsAirport(false);
        assertTrue(filter.matches(stationCity));
        assertFalse(filter.matches(stationAirport));
    }
}

