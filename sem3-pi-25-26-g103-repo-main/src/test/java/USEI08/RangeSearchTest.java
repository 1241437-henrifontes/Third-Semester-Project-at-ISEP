package USEI08;

import Model.*;
import Model.Filters.RailwayStationSearchFilters;
import Model.Trees.KDnode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("RangeSearch Unit Tests")
class RangeSearchTest {
    private RangeSearch rangeSearch;
    private Range testRange;

    @Mock
    private KDnode mockNode;

    @Mock
    private RailwayStationSearchFilters mockFilters;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testRange = new Range(40.0, 30.0, 10.0, 0.0);
        rangeSearch = new RangeSearch();
    }

    @Test
    @DisplayName("Should create RangeSearch with default constructor")
    void testRangeSearchCreation() {
        assertNotNull(rangeSearch);
        assertNotNull(rangeSearch.getResultStations());
    }

    @Test
    @DisplayName("Should initialize with empty result stations")
    void testInitialResultStationsEmpty() {
        assertTrue(rangeSearch.getResultStations().isEmpty());
    }

    @Test
    @DisplayName("Should return empty HashMap when no stations found")
    void testGetResultStationsEmpty() {
        HashMap<String, RailwayStation> results = rangeSearch.getResultStations();
        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    @Test
    @DisplayName("Should handle null node in search")
    void testSearchWithNullNode() {
        rangeSearch.search(null, true, mockFilters, testRange);
        assertTrue(rangeSearch.getResultStations().isEmpty());
    }

    @Test
    @DisplayName("Should search with valid node and filters")
    void testSearchWithValidNodeAndFilters() {
        RailwayStation station = createMockStation("Estação Central", 35.0, 5.0, true, true, false, Country.PT);

        when(mockNode.getLatitude()).thenReturn(35.0);
        when(mockNode.getLongitude()).thenReturn(5.0);
        when(mockNode.getStations()).thenReturn(List.of(station));
        when(mockNode.getLeft()).thenReturn(null);
        when(mockNode.getRight()).thenReturn(null);
        when(mockFilters.respectsFilters(station)).thenReturn(true);

        rangeSearch.search(mockNode, true, mockFilters, testRange);

        assertFalse(rangeSearch.getResultStations().isEmpty());
        assertTrue(rangeSearch.getResultStations().containsKey("Estação Central"));
    }

    @Test
    @DisplayName("Should filter stations that do not respect filters")
    void testSearchFiltersOutNonMatchingStations() {
        RailwayStation station = createMockStation("Estação", 35.0, 5.0, true, true, false, Country.PT);

        when(mockNode.getLatitude()).thenReturn(35.0);
        when(mockNode.getLongitude()).thenReturn(5.0);
        when(mockNode.getStations()).thenReturn(List.of(station));
        when(mockNode.getLeft()).thenReturn(null);
        when(mockNode.getRight()).thenReturn(null);
        when(mockFilters.respectsFilters(station)).thenReturn(false);

        rangeSearch.search(mockNode, true, mockFilters, testRange);

        assertTrue(rangeSearch.getResultStations().isEmpty());
    }

    @Test
    @DisplayName("Should handle multiple stations in node")
    void testSearchWithMultipleStationsInNode() {
        RailwayStation station1 = createMockStation("Estação 1", 35.0, 5.0, true, true, false, Country.PT);
        RailwayStation station2 = createMockStation("Estação 2", 35.5, 5.5, false, false, true, Country.ES);

        when(mockNode.getLatitude()).thenReturn(35.0);
        when(mockNode.getLongitude()).thenReturn(5.0);
        when(mockNode.getStations()).thenReturn(List.of(station1, station2));
        when(mockNode.getLeft()).thenReturn(null);
        when(mockNode.getRight()).thenReturn(null);
        when(mockFilters.respectsFilters(station1)).thenReturn(true);
        when(mockFilters.respectsFilters(station2)).thenReturn(true);

        rangeSearch.search(mockNode, true, mockFilters, testRange);

        assertEquals(2, rangeSearch.getResultStations().size());
    }

    @Test
    @DisplayName("Should not include stations outside range")
    void testSearchExcludesStationsOutsideRange() {
        RailwayStation station = createMockStation("Estação Fora", 50.0, 15.0, true, true, false, Country.PT);

        when(mockNode.getLatitude()).thenReturn(50.0);
        when(mockNode.getLongitude()).thenReturn(15.0);
        when(mockNode.getStations()).thenReturn(List.of(station));
        when(mockNode.getLeft()).thenReturn(null);
        when(mockNode.getRight()).thenReturn(null);

        rangeSearch.search(mockNode, true, mockFilters, testRange);

        assertTrue(rangeSearch.getResultStations().isEmpty());
    }

    @Test
    @DisplayName("Should handle search with level true")
    void testSearchWithLevelTrue() {
        RailwayStation station = createMockStation("Estação", 35.0, 5.0, true, true, false, Country.PT);

        when(mockNode.getLatitude()).thenReturn(35.0);
        when(mockNode.getLongitude()).thenReturn(5.0);
        when(mockNode.getStations()).thenReturn(List.of(station));
        when(mockNode.getLeft()).thenReturn(null);
        when(mockNode.getRight()).thenReturn(null);
        when(mockFilters.respectsFilters(station)).thenReturn(true);

        rangeSearch.search(mockNode, true, mockFilters, testRange);

        assertFalse(rangeSearch.getResultStations().isEmpty());
    }

    @Test
    @DisplayName("Should handle search with level false")
    void testSearchWithLevelFalse() {
        RailwayStation station = createMockStation("Estação", 35.0, 5.0, true, true, false, Country.PT);

        when(mockNode.getLatitude()).thenReturn(35.0);
        when(mockNode.getLongitude()).thenReturn(5.0);
        when(mockNode.getStations()).thenReturn(List.of(station));
        when(mockNode.getLeft()).thenReturn(null);
        when(mockNode.getRight()).thenReturn(null);
        when(mockFilters.respectsFilters(station)).thenReturn(true);

        rangeSearch.search(mockNode, false, mockFilters, testRange);

        assertFalse(rangeSearch.getResultStations().isEmpty());
    }

    private RailwayStation createMockStation(String name, double lat, double lon, boolean isCity,
                                             boolean isMainStation, boolean isAirport, Country country) {
        RailwayStation station = mock(RailwayStation.class);
        when(station.getName()).thenReturn(name);
        when(station.getLatitude()).thenReturn(lat);
        when(station.getLongitude()).thenReturn(lon);
        when(station.isCity()).thenReturn(isCity);
        when(station.isMainStation()).thenReturn(isMainStation);
        when(station.isAirport()).thenReturn(isAirport);
        when(station.getCountry()).thenReturn(country);
        return station;
    }
}
