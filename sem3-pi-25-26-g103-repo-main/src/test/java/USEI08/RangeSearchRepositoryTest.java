package USEI08;

import Repositories.RangeSearchRepository;
import Model.Country;
import Model.Filters.RailwayStationSearchFilters;
import Model.Range;
import Model.RangeSearch;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("RangeSearchRepository Unit Tests")
class RangeSearchRepositoryTest {
    private RangeSearchRepository repository;
    private RailwayStationSearchFilters testFilters;
    private RangeSearch testRangeSearch;
    private Range testRange;

    @BeforeEach
    void setUp() {
        repository = RangeSearchRepository.getInstance();
        repository.getRangeSearchMap().clear();

        List<Country> countries = new ArrayList<>();
        countries.add(Country.PT);
        testRange = new Range(40.0, 30.0, 10.0, 0.0);
        testFilters = new RailwayStationSearchFilters(true, true, false, countries);
        testRangeSearch = new RangeSearch();
    }

    @Test
    @DisplayName("Should return singleton instance")
    void testGetInstanceReturnsSingleton() {
        RangeSearchRepository instance1 = RangeSearchRepository.getInstance();
        RangeSearchRepository instance2 = RangeSearchRepository.getInstance();
        assertSame(instance1, instance2);
    }

    @Test
    @DisplayName("Should return false when range search does not exist")
    void testRangeSearchExistReturnsFalse() {
        assertFalse(repository.rangeSearchExist(testFilters, testRange));
    }

    @Test
    @DisplayName("Should return true when range search exists")
    void testRangeSearchExistReturnsTrue() {
        repository.addRangeSearch(testFilters, testRangeSearch, testRange);
        assertTrue(repository.rangeSearchExist(testFilters, testRange));
    }

    @Test
    @DisplayName("Should add range search to repository")
    void testAddRangeSearch() {
        repository.addRangeSearch(testFilters, testRangeSearch, testRange);
        assertTrue(repository.rangeSearchExist(testFilters, testRange));
    }

    @Test
    @DisplayName("Should retrieve added range search")
    void testRetrieveAddedRangeSearch() {
        repository.addRangeSearch(testFilters, testRangeSearch, testRange);
        HashMap<Range, HashMap<RailwayStationSearchFilters, RangeSearch>> map = repository.getRangeSearchMap();

        assertTrue(map.containsKey(testRange));
        assertTrue(map.get(testRange).containsKey(testFilters));
        assertEquals(testRangeSearch, map.get(testRange).get(testFilters));
    }

    @Test
    @DisplayName("Should return empty map initially")
    void testGetRangeSearchMapInitiallyEmpty() {
        HashMap<Range, HashMap<RailwayStationSearchFilters, RangeSearch>> map = repository.getRangeSearchMap();
        assertTrue(map.isEmpty());
    }

    @Test
    @DisplayName("Should add multiple range searches with same range")
    void testAddMultipleRangeSearchesSameRange() {
        List<Country> countries2 = new ArrayList<>();
        countries2.add(Country.ES);
        RailwayStationSearchFilters filters2 = new RailwayStationSearchFilters(false, false, true, countries2);
        RangeSearch rangeSearch2 = new RangeSearch();

        repository.addRangeSearch(testFilters, testRangeSearch, testRange);
        repository.addRangeSearch(filters2, rangeSearch2, testRange);

        HashMap<Range, HashMap<RailwayStationSearchFilters, RangeSearch>> map = repository.getRangeSearchMap();
        assertEquals(1, map.size());
        assertEquals(2, map.get(testRange).size());
        assertTrue(map.get(testRange).containsKey(testFilters));
        assertTrue(map.get(testRange).containsKey(filters2));
    }

    @Test
    @DisplayName("Should add multiple range searches with different ranges")
    void testAddMultipleRangeSearchesDifferentRanges() {
        Range range2 = new Range(50.0, 40.0, 20.0, 10.0);
        RangeSearch rangeSearch2 = new RangeSearch();

        repository.addRangeSearch(testFilters, testRangeSearch, testRange);
        repository.addRangeSearch(testFilters, rangeSearch2, range2);

        HashMap<Range, HashMap<RailwayStationSearchFilters, RangeSearch>> map = repository.getRangeSearchMap();
        assertEquals(2, map.size());
        assertEquals(1, map.get(testRange).size());
        assertEquals(1, map.get(range2).size());
    }

    @Test
    @DisplayName("Should overwrite existing range search with same filters and range")
    void testOverwriteExistingRangeSearch() {
        repository.addRangeSearch(testFilters, testRangeSearch, testRange);

        RangeSearch newRangeSearch = new RangeSearch();
        repository.addRangeSearch(testFilters, newRangeSearch, testRange);

        HashMap<Range, HashMap<RailwayStationSearchFilters, RangeSearch>> map = repository.getRangeSearchMap();
        assertEquals(1, map.size());
        assertEquals(1, map.get(testRange).size());
        assertEquals(newRangeSearch, map.get(testRange).get(testFilters));
    }

    @Test
    @DisplayName("Should return correct range search map reference")
    void testGetRangeSearchMapReference() {
        repository.addRangeSearch(testFilters, testRangeSearch, testRange);
        HashMap<Range, HashMap<RailwayStationSearchFilters, RangeSearch>> map1 = repository.getRangeSearchMap();
        HashMap<Range, HashMap<RailwayStationSearchFilters, RangeSearch>> map2 = repository.getRangeSearchMap();

        assertSame(map1, map2);
    }

    @Test
    @DisplayName("Should handle null filters gracefully")
    void testHandleNullFilters() {
        assertDoesNotThrow(() -> repository.addRangeSearch(null, testRangeSearch, testRange));
        HashMap<Range, HashMap<RailwayStationSearchFilters, RangeSearch>> map = repository.getRangeSearchMap();
        assertTrue(map.get(testRange).containsKey(null));
        assertEquals(testRangeSearch, map.get(testRange).get(null));
    }

    @Test
    @DisplayName("Should handle null range search gracefully")
    void testHandleNullRangeSearch() {
        assertDoesNotThrow(() -> repository.addRangeSearch(testFilters, null, testRange));
        assertTrue(repository.rangeSearchExist(testFilters, testRange));
        assertNull(repository.getRangeSearchMap().get(testRange).get(testFilters));
    }

    @Test
    @DisplayName("Should verify filters equality in repository")
    void testFiltersEqualityInRepository() {
        List<Country> countries = new ArrayList<>();
        countries.add(Country.PT);
        RailwayStationSearchFilters filters1 = new RailwayStationSearchFilters(true, true, false, countries);
        RailwayStationSearchFilters filters2 = new RailwayStationSearchFilters(true, true, false, countries);

        repository.addRangeSearch(filters1, testRangeSearch, testRange);

        assertTrue(repository.rangeSearchExist(filters2, testRange));
        assertEquals(testRangeSearch, repository.getRangeSearchMap().get(testRange).get(filters2));
    }

    @Test
    @DisplayName("Should maintain repository state across multiple operations")
    void testRepositoryStateConsistency() {
        repository.addRangeSearch(testFilters, testRangeSearch, testRange);
        assertTrue(repository.rangeSearchExist(testFilters, testRange));

        HashMap<Range, HashMap<RailwayStationSearchFilters, RangeSearch>> map = repository.getRangeSearchMap();
        assertEquals(1, map.size());
        assertEquals(1, map.get(testRange).size());

        repository.addRangeSearch(testFilters, testRangeSearch, testRange);
        assertEquals(1, map.size());
        assertEquals(1, map.get(testRange).size());
    }
}
