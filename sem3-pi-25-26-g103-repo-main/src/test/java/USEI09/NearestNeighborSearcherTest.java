package USEI09;

import Repositories.TreeRepository;
import Model.Country;
import Model.Filters.StationFilter;
import Model.RailwayStation;
import Model.StationDistance;
import Model.TimeZoneGroup;
import Model.Trees.KDnode;
import Model.Trees.KDtree;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import Services.NearestNeighborSearcher;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class NearestNeighborSearcherTest {

    private NearestNeighborSearcher searcher;
    private KDtree tree;
    private TreeRepository stationRepo;
    private KDnode root;

    @BeforeEach
    void setUp() {
        searcher = new NearestNeighborSearcher();
        tree = new KDtree();
        stationRepo = TreeRepository.getInstance();

        stationRepo.addStation(new RailwayStation("Lisboa", 38.7223, -9.1393, Country.PT, "Europe/Lisbon", TimeZoneGroup.WET_GMT, true, true, false));
        stationRepo.addStation(new RailwayStation("Porto", 41.1496, -8.6109, Country.PT, "Europe/Lisbon", TimeZoneGroup.WET_GMT, true, true, false));
        stationRepo.addStation(new RailwayStation("Madrid", 40.4168, -3.7038, Country.ES, "Europe/Madrid", TimeZoneGroup.CET, true, true, false));
        stationRepo.addStation(new RailwayStation("Barcelona", 41.3851, 2.1734, Country.ES, "Europe/Madrid", TimeZoneGroup.CET, true, true, false));
        stationRepo.addStation(new RailwayStation("Paris", 48.8566, 2.3522, Country.FR, "Europe/Paris", TimeZoneGroup.CET, true, true, false));

        tree.buildTree();
        root = tree.getRoot();
    }

    @AfterEach
    void tearDown() {
        searcher = null;
        tree = null;
        root = null;
    }

    @Test
    void testConstructor() {
        NearestNeighborSearcher nns = new NearestNeighborSearcher();
        assertNotNull(nns);
    }

    @Test
    void testFindNearestNWithNullRoot() {
        List<StationDistance> result = searcher.findNearestN(null, 38.7, -9.1, 3, null);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testFindNearestNWithZeroN() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            searcher.findNearestN(root, 38.7, -9.1, 0, null);
        });
        assertEquals("N must be positive", exception.getMessage());
    }

    @Test
    void testFindNearestNWithNegativeN() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            searcher.findNearestN(root, 38.7, -9.1, -5, null);
        });
        assertEquals("N must be positive", exception.getMessage());
    }

    @Test
    void testFindNearestNSingleStation() {
        List<StationDistance> result = searcher.findNearestN(root, 38.7, -9.1, 1, null);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Lisboa", result.get(0).getStation().getName());
    }

    @Test
    void testFindNearestNMultipleStations() {
        List<StationDistance> result = searcher.findNearestN(root, 38.7, -9.1, 3, null);

        assertNotNull(result);
        assertEquals(3, result.size());
    }

    @Test
    void testFindNearestNResultsSortedByDistance() {
        List<StationDistance> result = searcher.findNearestN(root, 38.7, -9.1, 3, null);

        for (int i = 0; i < result.size() - 1; i++) {
            assertTrue(result.get(i).getDistance() <= result.get(i + 1).getDistance());
        }
    }

    @Test
    void testFindNearestNNearLisbon() {
        List<StationDistance> result = searcher.findNearestN(root, 38.7223, -9.1393, 2, null);

        assertNotNull(result);
        assertTrue(result.size() >= 1);
        assertEquals("Lisboa", result.get(0).getStation().getName());
        assertTrue(result.get(0).getDistance() < 1.0);
    }

    @Test
    void testFindNearestNNearMadrid() {
        List<StationDistance> result = searcher.findNearestN(root, 40.4, -3.7, 2, null);

        assertNotNull(result);
        assertTrue(result.size() >= 1);
        assertEquals("Madrid", result.get(0).getStation().getName());
    }

    @Test
    void testFindNearestNWithFilter() {
        StationFilter filter = new StationFilter().withTimeZoneRange(TimeZoneGroup.WET_GMT, TimeZoneGroup.WET_GMT);
        List<StationDistance> result = searcher.findNearestN(root, 40.0, -8.0, 3, filter);

        assertNotNull(result);
        for (StationDistance sd : result) {
            assertEquals(TimeZoneGroup.WET_GMT, sd.getStation().getTimeZoneGroup());
        }
    }

    @Test
    void testFindNearestNWithCityFilter() {
        StationFilter filter = new StationFilter().withIsCity(true);
        List<StationDistance> result = searcher.findNearestN(root, 40.0, -8.0, 3, filter);

        assertNotNull(result);
        for (StationDistance sd : result) {
            assertTrue(sd.getStation().isCity());
        }
    }

    @Test
    void testFindNearestNWithMainStationFilter() {
        StationFilter filter = new StationFilter().withIsMainStation(true);
        List<StationDistance> result = searcher.findNearestN(root, 40.0, -8.0, 3, filter);

        assertNotNull(result);
        for (StationDistance sd : result) {
            assertTrue(sd.getStation().isMainStation());
        }
    }

    @Test
    void testFindNearestNMoreThanAvailable() {
        List<StationDistance> result = searcher.findNearestN(root, 38.7, -9.1, 100, null);

        assertNotNull(result);
        assertTrue(result.size() >= 5);
    }

    @Test
    void testFindNearestNAllStations() {
        List<StationDistance> result = searcher.findNearestN(root, 38.7, -9.1, 5, null);

        assertNotNull(result);
        assertEquals(5, result.size());
    }

    @Test
    void testGetNodesVisited() {
        searcher.findNearestN(root, 38.7, -9.1, 3, null);
        int nodesVisited = searcher.getNodesVisited();

        assertTrue(nodesVisited > 0);
        assertTrue(nodesVisited <= tree.size());
    }

    @Test
    void testGetNodesVisitedWithPruning() {
        searcher.findNearestN(root, 38.7, -9.1, 1, null);
        int nodesVisited = searcher.getNodesVisited();

        assertTrue(nodesVisited > 0);
    }

    @Test
    void testHasEnoughStationsTrue() {
        boolean result = searcher.hasEnoughStations(root, null, 3);
        assertTrue(result);
    }

    @Test
    void testHasEnoughStationsFalse() {
        boolean result = searcher.hasEnoughStations(root, null, 100);
        assertFalse(result);
    }

    @Test
    void testHasEnoughStationsWithFilter() {
        StationFilter filter = new StationFilter().withTimeZoneRange(TimeZoneGroup.WET_GMT, TimeZoneGroup.WET_GMT);
        boolean result = searcher.hasEnoughStations(root, filter, 2);
        assertTrue(result);
    }

    @Test
    void testHasEnoughStationsWithRestrictiveFilter() {
        StationFilter filter = new StationFilter().withIsAirport(true);
        boolean result = searcher.hasEnoughStations(root, filter, 1);
        assertFalse(result);
    }

    @Test
    void testFindNearestNWithMetrics() {
        NearestNeighborSearcher.SearchResult result = searcher.findNearestNWithMetrics(root, 38.7, -9.1, 3, null);

        assertNotNull(result);
        assertNotNull(result.getStations());
        assertTrue(result.getNodesVisited() > 0);
        assertTrue(result.getDurationMs() >= 0);
    }

    @Test
    void testSearchResultGetters() {
        NearestNeighborSearcher.SearchResult result = searcher.findNearestNWithMetrics(root, 38.7, -9.1, 3, null);

        List<StationDistance> stations = result.getStations();
        int nodesVisited = result.getNodesVisited();
        double duration = result.getDurationMs();

        assertNotNull(stations);
        assertTrue(nodesVisited > 0);
        assertTrue(duration >= 0);
    }

    @Test
    void testSearchResultToString() {
        NearestNeighborSearcher.SearchResult result = searcher.findNearestNWithMetrics(root, 38.7, -9.1, 3, null);

        String str = result.toString();
        assertNotNull(str);
        assertTrue(str.contains("SearchResult"));
        assertTrue(str.contains("stations"));
        assertTrue(str.contains("nodes"));
    }

    @Test
    void testFindNearestNAtExactLocation() {
        List<StationDistance> result = searcher.findNearestN(root, 38.7223, -9.1393, 1, null);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0).getDistance() < 0.1);
    }

    @Test
    void testFindNearestNFarFromAllStations() {
        List<StationDistance> result = searcher.findNearestN(root, 60.0, 10.0, 2, null);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.get(0).getDistance() > 1000);
    }

    @Test
    void testFindNearestNWithNullFilter() {
        List<StationDistance> result = searcher.findNearestN(root, 38.7, -9.1, 3, null);

        assertNotNull(result);
        assertEquals(3, result.size());
    }

    @Test
    void testPruningEfficiency() {
        searcher.findNearestN(root, 38.7, -9.1, 1, null);
        int nodesVisited1 = searcher.getNodesVisited();

        searcher.findNearestN(root, 38.7, -9.1, 5, null);
        int nodesVisited2 = searcher.getNodesVisited();

        assertTrue(nodesVisited1 > 0);
        assertTrue(nodesVisited2 > 0);
    }

    @Test
    void testConsecutiveSearches() {
        List<StationDistance> result1 = searcher.findNearestN(root, 38.7, -9.1, 2, null);
        List<StationDistance> result2 = searcher.findNearestN(root, 40.4, -3.7, 2, null);

        assertNotNull(result1);
        assertNotNull(result2);
        assertNotEquals(result1.get(0).getStation().getName(), result2.get(0).getStation().getName());
    }

    @Test
    void testFindNearestNWithTimeZoneFilterCET() {
        StationFilter filter = new StationFilter().withTimeZoneRange(TimeZoneGroup.CET, TimeZoneGroup.CET);
        List<StationDistance> result = searcher.findNearestN(root, 40.4, -3.7, 3, filter);

        assertNotNull(result);
        assertTrue(result.size() <= 3);
        for (StationDistance sd : result) {
            assertEquals(TimeZoneGroup.CET, sd.getStation().getTimeZoneGroup());
        }
    }

    @Test
    void testFindNearestNResultsNotNull() {
        List<StationDistance> result = searcher.findNearestN(root, 38.7, -9.1, 3, null);

        for (StationDistance sd : result) {
            assertNotNull(sd);
            assertNotNull(sd.getStation());
            assertTrue(sd.getDistance() >= 0);
        }
    }

    @Test
    void testNodesVisitedResetBetweenSearches() {
        searcher.findNearestN(root, 38.7, -9.1, 2, null);
        int firstVisited = searcher.getNodesVisited();

        searcher.findNearestN(root, 40.4, -3.7, 2, null);
        int secondVisited = searcher.getNodesVisited();

        assertTrue(firstVisited > 0);
        assertTrue(secondVisited > 0);
    }

    @Test
    void testSearchWithEmptyFilter() {
        StationFilter filter = new StationFilter();
        List<StationDistance> result = searcher.findNearestN(root, 38.7, -9.1, 3, filter);

        assertNotNull(result);
        assertEquals(3, result.size());
    }

    @Test
    void testMetricsAccuracy() {
        NearestNeighborSearcher.SearchResult result = searcher.findNearestNWithMetrics(root, 38.7, -9.1, 3, null);

        assertEquals(result.getNodesVisited(), searcher.getNodesVisited());
        assertEquals(3, result.getStations().size());
    }

    @Test
    void testLargeNValue() {
        List<StationDistance> result = searcher.findNearestN(root, 38.7, -9.1, 1000, null);

        assertNotNull(result);
        assertTrue(result.size() >= 5);
    }

    @Test
    void testSearchAtEquator() {
        List<StationDistance> result = searcher.findNearestN(root, 0.0, 0.0, 2, null);

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void testSearchAtNorthPole() {
        List<StationDistance> result = searcher.findNearestN(root, 89.0, 0.0, 2, null);

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void testDistancesIncrease() {
        List<StationDistance> result = searcher.findNearestN(root, 38.7, -9.1, 5, null);

        for (int i = 0; i < result.size() - 1; i++) {
            assertTrue(result.get(i).getDistance() <= result.get(i + 1).getDistance());
        }
    }

    @Test
    void testHasEnoughStationsWithNullRoot() {
        boolean result = searcher.hasEnoughStations(null, null, 3);
        assertFalse(result);
    }

    @Test
    void testSearchResultConstructor() {
        List<StationDistance> stations = new ArrayList<>();
        NearestNeighborSearcher.SearchResult result = new NearestNeighborSearcher.SearchResult(stations, 5, 1.5);

        assertNotNull(result);
        assertEquals(stations, result.getStations());
        assertEquals(5, result.getNodesVisited());
        assertEquals(1.5, result.getDurationMs(), 0.001);
    }
}

