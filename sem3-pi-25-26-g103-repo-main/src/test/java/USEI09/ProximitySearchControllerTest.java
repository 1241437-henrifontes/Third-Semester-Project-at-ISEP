package USEI09;

import Repositories.TreeRepository;
import Controllers.ProximitySearchController;
import Model.Country;
import Model.Filters.StationFilter;
import Model.RailwayStation;
import Model.StationDistance;
import Model.TimeZoneGroup;
import Model.Trees.KDtree;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import Services.NearestNeighborSearcher;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ProximitySearchControllerTest {

    private ProximitySearchController controller;
    private TreeRepository stationRepo;
    private KDtree tree;

    @BeforeEach
    void setUp() {
        controller = new ProximitySearchController();
        stationRepo = TreeRepository.getInstance();
        tree = stationRepo.getKdtree();

        stationRepo.addStation(new RailwayStation("Lisboa", 38.7223, -9.1393, Country.PT, "Europe/Lisbon", TimeZoneGroup.WET_GMT, true, true, false));
        stationRepo.addStation(new RailwayStation("Porto", 41.1496, -8.6109, Country.PT, "Europe/Lisbon", TimeZoneGroup.WET_GMT, true, true, false));
        stationRepo.addStation(new RailwayStation("Madrid", 40.4168, -3.7038, Country.ES, "Europe/Madrid", TimeZoneGroup.CET, true, true, false));
        stationRepo.addStation(new RailwayStation("Barcelona", 41.3851, 2.1734, Country.ES, "Europe/Madrid", TimeZoneGroup.CET, true, true, false));
        stationRepo.addStation(new RailwayStation("Paris", 48.8566, 2.3522, Country.FR, "Europe/Paris", TimeZoneGroup.CET, true, true, false));

        tree.buildTree();
    }

    @AfterEach
    void tearDown() {
        controller = null;
        tree = null;
    }

    @Test
    void testConstructor() {
        ProximitySearchController ctrl = new ProximitySearchController();
        assertNotNull(ctrl);
    }

    @Test
    void testFindNearestStations() {
        List<StationDistance> result = controller.findNearestStations(38.7, -9.1, 3, null);

        assertNotNull(result);
        assertEquals(3, result.size());
    }

    @Test
    void testFindNearestStationsNearLisbon() {
        List<StationDistance> result = controller.findNearestStations(38.7223, -9.1393, 2, null);

        assertNotNull(result);
        assertTrue(result.size() >= 1);
        assertEquals("Lisboa", result.get(0).getStation().getName());
    }

    @Test
    void testFindNearestStationsWithFilter() {
        StationFilter filter = new StationFilter().withTimeZoneRange(TimeZoneGroup.WET_GMT, TimeZoneGroup.WET_GMT);
        List<StationDistance> result = controller.findNearestStations(40.0, -8.0, 3, filter);

        assertNotNull(result);
        for (StationDistance sd : result) {
            assertEquals(TimeZoneGroup.WET_GMT, sd.getStation().getTimeZoneGroup());
        }
    }

    @Test
    void testFindNearestStationsWithNullFilter() {
        List<StationDistance> result = controller.findNearestStations(38.7, -9.1, 3, null);

        assertNotNull(result);
        assertEquals(3, result.size());
    }

    @Test
    void testFindNearestStationsResultsSorted() {
        List<StationDistance> result = controller.findNearestStations(38.7, -9.1, 3, null);

        for (int i = 0; i < result.size() - 1; i++) {
            assertTrue(result.get(i).getDistance() <= result.get(i + 1).getDistance());
        }
    }

    @Test
    void testFindNearestStationsWithMetrics() {
        NearestNeighborSearcher.SearchResult result = controller.findNearestStationsWithMetrics(38.7, -9.1, 3, null);

        assertNotNull(result);
        assertNotNull(result.getStations());
        assertEquals(3, result.getStations().size());
        assertTrue(result.getNodesVisited() > 0);
        assertTrue(result.getDurationMs() >= 0);
    }

    @Test
    void testFindNearestStationsWithMetricsAndFilter() {
        StationFilter filter = new StationFilter().withIsCity(true);
        NearestNeighborSearcher.SearchResult result = controller.findNearestStationsWithMetrics(38.7, -9.1, 3, filter);

        assertNotNull(result);
        assertNotNull(result.getStations());
        for (StationDistance sd : result.getStations()) {
            assertTrue(sd.getStation().isCity());
        }
    }

    @Test
    void testCreateTimeZoneFilter() {
        StationFilter filter = controller.createTimeZoneFilter(TimeZoneGroup.WET_GMT, TimeZoneGroup.CET);

        assertNotNull(filter);
        assertEquals(TimeZoneGroup.WET_GMT, filter.getMinTimeZone());
        assertEquals(TimeZoneGroup.CET, filter.getMaxTimeZone());
    }

    @Test
    void testCreateTimeZoneFilterSameZone() {
        StationFilter filter = controller.createTimeZoneFilter(TimeZoneGroup.CET, TimeZoneGroup.CET);

        assertNotNull(filter);
        assertEquals(TimeZoneGroup.CET, filter.getMinTimeZone());
        assertEquals(TimeZoneGroup.CET, filter.getMaxTimeZone());
    }

    @Test
    void testCreateTypeFilterAllTrue() {
        StationFilter filter = controller.createTypeFilter(true, true, false);

        assertNotNull(filter);
        assertEquals(true, filter.getIsCity());
        assertEquals(true, filter.getIsMainStation());
        assertEquals(false, filter.getIsAirport());
    }

    @Test
    void testCreateTypeFilterAllNull() {
        StationFilter filter = controller.createTypeFilter(null, null, null);

        assertNotNull(filter);
        assertNull(filter.getIsCity());
        assertNull(filter.getIsMainStation());
        assertNull(filter.getIsAirport());
    }

    @Test
    void testCreateTypeFilterMixed() {
        StationFilter filter = controller.createTypeFilter(true, null, false);

        assertNotNull(filter);
        assertEquals(true, filter.getIsCity());
        assertNull(filter.getIsMainStation());
        assertEquals(false, filter.getIsAirport());
    }

    @Test
    void testCreateTypeFilterOnlyCity() {
        StationFilter filter = controller.createTypeFilter(true, null, null);

        assertNotNull(filter);
        assertEquals(true, filter.getIsCity());
        assertNull(filter.getIsMainStation());
        assertNull(filter.getIsAirport());
    }

    @Test
    void testCreateTypeFilterOnlyMainStation() {
        StationFilter filter = controller.createTypeFilter(null, true, null);

        assertNotNull(filter);
        assertNull(filter.getIsCity());
        assertEquals(true, filter.getIsMainStation());
        assertNull(filter.getIsAirport());
    }

    @Test
    void testCreateTypeFilterOnlyAirport() {
        StationFilter filter = controller.createTypeFilter(null, null, true);

        assertNotNull(filter);
        assertNull(filter.getIsCity());
        assertNull(filter.getIsMainStation());
        assertEquals(true, filter.getIsAirport());
    }

    @Test
    void testGetLastSearchNodesVisited() {
        controller.findNearestStations(38.7, -9.1, 3, null);
        int nodesVisited = controller.getLastSearchNodesVisited();

        assertTrue(nodesVisited > 0);
    }

    @Test
    void testGetLastSearchNodesVisitedAfterMultipleSearches() {
        controller.findNearestStations(38.7, -9.1, 2, null);
        int firstVisited = controller.getLastSearchNodesVisited();

        controller.findNearestStations(40.4, -3.7, 2, null);
        int secondVisited = controller.getLastSearchNodesVisited();

        assertTrue(firstVisited > 0);
        assertTrue(secondVisited > 0);
    }

    @Test
    void testFindNearestStationsMultipleCalls() {
        List<StationDistance> result1 = controller.findNearestStations(38.7, -9.1, 2, null);
        List<StationDistance> result2 = controller.findNearestStations(40.4, -3.7, 2, null);

        assertNotNull(result1);
        assertNotNull(result2);
        assertNotEquals(result1.get(0).getStation().getName(), result2.get(0).getStation().getName());
    }

    @Test
    void testFindNearestStationsWithCityFilter() {
        StationFilter filter = controller.createTypeFilter(true, null, null);
        List<StationDistance> result = controller.findNearestStations(38.7, -9.1, 3, filter);

        assertNotNull(result);
        for (StationDistance sd : result) {
            assertTrue(sd.getStation().isCity());
        }
    }

    @Test
    void testFindNearestStationsWithMainStationFilter() {
        StationFilter filter = controller.createTypeFilter(null, true, null);
        List<StationDistance> result = controller.findNearestStations(38.7, -9.1, 3, filter);

        assertNotNull(result);
        for (StationDistance sd : result) {
            assertTrue(sd.getStation().isMainStation());
        }
    }

    @Test
    void testFindNearestStationsWithCombinedFilters() {
        StationFilter filter = controller.createTypeFilter(true, true, null);
        filter.withTimeZoneRange(TimeZoneGroup.WET_GMT, TimeZoneGroup.CET);

        List<StationDistance> result = controller.findNearestStations(40.0, -8.0, 3, filter);

        assertNotNull(result);
        for (StationDistance sd : result) {
            assertTrue(sd.getStation().isCity());
            assertTrue(sd.getStation().isMainStation());
        }
    }

    @Test
    void testFindNearestStationsLargeN() {
        List<StationDistance> result = controller.findNearestStations(38.7, -9.1, 100, null);

        assertNotNull(result);
        assertTrue(result.size() >= 5);
    }

    @Test
    void testFindNearestStationsSingleResult() {
        List<StationDistance> result = controller.findNearestStations(38.7223, -9.1393, 1, null);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Lisboa", result.get(0).getStation().getName());
    }

    @Test
    void testCreateTimeZoneFilterWETToFET() {
        StationFilter filter = controller.createTimeZoneFilter(TimeZoneGroup.WET_GMT, TimeZoneGroup.FET);

        assertNotNull(filter);
        assertEquals(TimeZoneGroup.WET_GMT, filter.getMinTimeZone());
        assertEquals(TimeZoneGroup.FET, filter.getMaxTimeZone());
    }

    @Test
    void testFindNearestStationsFarFromAll() {
        List<StationDistance> result = controller.findNearestStations(60.0, 10.0, 2, null);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.get(0).getDistance() > 1000);
    }

    @Test
    void testFindNearestStationsAtExactLocation() {
        List<StationDistance> result = controller.findNearestStations(38.7223, -9.1393, 1, null);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0).getDistance() < 0.1);
    }

    @Test
    void testMetricsConsistency() {
        NearestNeighborSearcher.SearchResult result = controller.findNearestStationsWithMetrics(38.7, -9.1, 3, null);
        int nodesFromResult = result.getNodesVisited();
        int nodesFromController = controller.getLastSearchNodesVisited();

        assertEquals(nodesFromResult, nodesFromController);
    }
}