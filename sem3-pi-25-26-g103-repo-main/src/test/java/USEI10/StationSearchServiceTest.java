package USEI10;

import Model.Country;
import Model.RailwayStation;
import Model.StationDistanceResult;
import Model.TimeZoneGroup;
import Model.Trees.AVLTree;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import Services.StationSearchService;

import java.util.Map;
import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.*;

class StationSearchServiceTest {

    private StationSearchService service;

    private final RailwayStation stationA = new RailwayStation("Aarhus", 0, 0, Country.DK, "TZ", TimeZoneGroup.CET, true, false, false);
    private final RailwayStation stationZ = new RailwayStation("Zaragoza", 0, 0, Country.ES, "TZ", TimeZoneGroup.CET, true, false, false);
    private final RailwayStation stationLis = new RailwayStation("Lisboa C", 0, 0, Country.PT, "TZ", TimeZoneGroup.WET_GMT, true, true, false);
    private final RailwayStation stationBrg = new RailwayStation("Braga", 0, 0, Country.PT, "TZ", TimeZoneGroup.WET_GMT, false, false, false);
    private final RailwayStation stationNullCountry = new RailwayStation("NoCountry", 0, 0, null, "TZ", TimeZoneGroup.CET, false, false, false);

    @BeforeEach
    void setUp() {
        service = new StationSearchService() {
        };
    }

    @Test
    void testGenerateDensitySummary_ValidResults() {
        AVLTree<StationDistanceResult> testResults = new AVLTree<>();
        testResults.insert(new StationDistanceResult(stationA, 10.0));
        testResults.insert(new StationDistanceResult(stationLis, 5.0));

        Map<String, Map<String, Integer>> summary = service.generateDensitySummary(testResults);

        assertEquals(1, summary.get("byCountry").get(Country.PT.toString()));
        assertEquals(1, summary.get("byCountry").get(Country.DK.toString()));
    }

    @Test
    void testGenerateDensitySummary_EmptyResults() {
        AVLTree<StationDistanceResult> emptyResults = new AVLTree<>();
        Map<String, Map<String, Integer>> summary = service.generateDensitySummary(emptyResults);
        assertTrue(summary.get("byCountry").isEmpty());
        assertTrue(summary.get("byCityStatus").isEmpty());
    }

    @Test
    void testGenerateDensitySummary_NullResultsInput() {
        Map<String, Map<String, Integer>> summary = service.generateDensitySummary(null);
        assertNotNull(summary);
        assertTrue(summary.get("byCountry").isEmpty());
    }

    @Test
    void testGenerateDensitySummary_MultipleCountriesCount() {
        AVLTree<StationDistanceResult> testResults = new AVLTree<>();
        testResults.insert(new StationDistanceResult(stationLis, 10.0));
        testResults.insert(new StationDistanceResult(stationBrg, 15.0));
        testResults.insert(new StationDistanceResult(stationA, 5.0));

        Map<String, Integer> countryCounts = service.generateDensitySummary(testResults).get("byCountry");
        assertEquals(2, countryCounts.get("PT"));
        assertEquals(1, countryCounts.get("DK"));
    }

    @Test
    void testGenerateDensitySummary_AllSameCountry() {
        AVLTree<StationDistanceResult> testResults = new AVLTree<>();
        testResults.insert(new StationDistanceResult(stationLis, 10.0));
        testResults.insert(new StationDistanceResult(stationBrg, 15.0));

        Map<String, Integer> countryCounts = service.generateDensitySummary(testResults).get("byCountry");
        assertEquals(1, countryCounts.size());
        assertEquals(2, countryCounts.get("PT"));
    }

    @Test
    void testGenerateDensitySummary_NullCountryHandling() {
        AVLTree<StationDistanceResult> testResults = new AVLTree<>();
        testResults.insert(new StationDistanceResult(stationNullCountry, 10.0));

        Map<String, Integer> countryCounts = service.generateDensitySummary(testResults).get("byCountry");
        assertTrue(countryCounts.containsKey("UNKNOWN"));
        assertEquals(1, countryCounts.get("UNKNOWN"));
    }

    @Test
    void testGenerateDensitySummary_CityStatusCounts() {
        AVLTree<StationDistanceResult> testResults = new AVLTree<>();
        testResults.insert(new StationDistanceResult(stationLis, 10.0));
        testResults.insert(new StationDistanceResult(stationBrg, 15.0));

        Map<String, Integer> cityCounts = service.generateDensitySummary(testResults).get("byCityStatus");
        assertEquals(1, cityCounts.get("City"));
        assertEquals(1, cityCounts.get("Not City"));
    }

    @Test
    void testGenerateDensitySummary_TotalCountConsistency() {
        AVLTree<StationDistanceResult> testResults = new AVLTree<>();
        testResults.insert(new StationDistanceResult(stationA, 10.0));
        testResults.insert(new StationDistanceResult(stationZ, 20.0));
        testResults.insert(new StationDistanceResult(stationLis, 30.0));

        Map<String, Map<String, Integer>> summary = service.generateDensitySummary(testResults);

        int totalCountries = summary.get("byCountry").values().stream().mapToInt(Integer::intValue).sum();
        int totalCities = summary.get("byCityStatus").values().stream().mapToInt(Integer::intValue).sum();

        assertEquals(3, totalCountries);
        assertEquals(3, totalCities);
    }


    @Test
    void testAVLSorting_DistanceAscendingPrimary() {
        AVLTree<StationDistanceResult> testResults = new AVLTree<>();
        testResults.insert(new StationDistanceResult(stationLis, 5.0));
        testResults.insert(new StationDistanceResult(stationA, 10.0));
        testResults.insert(new StationDistanceResult(stationZ, 2.0));

        Iterator<StationDistanceResult> it = testResults.inOrder().iterator();
        assertEquals(2.0, it.next().getDistance());
        assertEquals(5.0, it.next().getDistance());
        assertEquals(10.0, it.next().getDistance());
    }

    @Test
    void testAVLSorting_NameDescendingTieBreaker() {

        AVLTree<StationDistanceResult> testResults = new AVLTree<>();
        testResults.insert(new StationDistanceResult(stationLis, 15.0));
        testResults.insert(new StationDistanceResult(stationBrg, 15.0));
        testResults.insert(new StationDistanceResult(stationZ, 15.0));

        Iterator<StationDistanceResult> it = testResults.inOrder().iterator();
        assertEquals("Zaragoza", it.next().getStation().getName());
        assertEquals("Lisboa C", it.next().getStation().getName());
        assertEquals("Braga", it.next().getStation().getName());
    }

    @Test
    void testAVLSorting_CombinedCriteria() {
        StationDistanceResult r1 = new StationDistanceResult(stationZ, 50.0); // Z, longe
        StationDistanceResult r2 = new StationDistanceResult(stationA, 10.0); // A, perto

        AVLTree<StationDistanceResult> testResults = new AVLTree<>();
        testResults.insert(r1);
        testResults.insert(r2);

        Iterator<StationDistanceResult> it = testResults.inOrder().iterator();
        assertEquals(10.0, it.next().getDistance()); // Distância ganha
        assertEquals(50.0, it.next().getDistance());
    }

    @Test
    void testCompareTo_EqualObjects() {

        StationDistanceResult r1 = new StationDistanceResult(stationA, 10.0);
        StationDistanceResult r2 = new StationDistanceResult(stationA, 10.0);
        assertEquals(0, r1.compareTo(r2));
    }

    @Test
    void testCompareTo_SameDistanceDifferentName() {
        StationDistanceResult r1 = new StationDistanceResult(stationA, 10.0); // Aarhus
        StationDistanceResult r2 = new StationDistanceResult(stationZ, 10.0); // Zaragoza

        assertTrue(r2.compareTo(r1) < 0);
    }

    @Test
    void testCompareTo_DifferentDistanceSameName() {

        StationDistanceResult r1 = new StationDistanceResult(stationA, 10.0);
        StationDistanceResult r2 = new StationDistanceResult(stationA, 20.0);

        assertTrue(r1.compareTo(r2) < 0);
    }

    @Test
    void testAVLSorting_InsertInReverseOrder() {
        AVLTree<StationDistanceResult> testResults = new AVLTree<>();
        testResults.insert(new StationDistanceResult(stationA, 30.0));
        testResults.insert(new StationDistanceResult(stationA, 20.0));
        testResults.insert(new StationDistanceResult(stationA, 10.0));

        Iterator<StationDistanceResult> it = testResults.inOrder().iterator();
        assertEquals(10.0, it.next().getDistance());
        assertEquals(20.0, it.next().getDistance());
        assertEquals(30.0, it.next().getDistance());
    }

    @Test
    void testAVLSorting_InsertInOrder() {
        AVLTree<StationDistanceResult> testResults = new AVLTree<>();
        testResults.insert(new StationDistanceResult(stationA, 10.0));
        testResults.insert(new StationDistanceResult(stationA, 20.0));
        testResults.insert(new StationDistanceResult(stationA, 30.0));

        Iterator<StationDistanceResult> it = testResults.inOrder().iterator();
        assertEquals(10.0, it.next().getDistance());
        assertEquals(20.0, it.next().getDistance());
        assertEquals(30.0, it.next().getDistance());
    }

    @Test
    void testAVLSorting_CaseInsensitiveCheck() {

        RailwayStation s1 = new RailwayStation("alfa", 0,0,Country.PT,"",TimeZoneGroup.CET,false,false,false);
        RailwayStation s2 = new RailwayStation("BETA", 0,0,Country.PT,"",TimeZoneGroup.CET,false,false,false);

        StationDistanceResult r1 = new StationDistanceResult(s1, 10.0);
        StationDistanceResult r2 = new StationDistanceResult(s2, 10.0);


        assertTrue(r1.compareTo(r2) < 0);
    }

    @Test
    void testDuplicateInsertion() {

        AVLTree<StationDistanceResult> testResults = new AVLTree<>();
        testResults.insert(new StationDistanceResult(stationA, 10.0));
        testResults.insert(new StationDistanceResult(stationA, 10.0)); // Duplicado

    }


    @Test
    void testWrapper_NullStationThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            new StationDistanceResult(null, 10.0);
        });
    }

    @Test
    void testWrapper_NegativeDistanceAllowed() {

        StationDistanceResult res = new StationDistanceResult(stationA, -5.0);
        assertEquals(-5.0, res.getDistance());
    }

    @Test
    void testWrapper_Getters() {
        StationDistanceResult res = new StationDistanceResult(stationA, 123.45);
        assertEquals(stationA, res.getStation());
        assertEquals(123.45, res.getDistance());
    }

    @Test
    void testWrapper_ToString() {
        StationDistanceResult res = new StationDistanceResult(stationA, 10.5);
        String str = res.toString();
        assertTrue(str.contains("Aarhus"));
        assertTrue(str.contains("10,50") || str.contains("10.50")); // Locale check
    }
}