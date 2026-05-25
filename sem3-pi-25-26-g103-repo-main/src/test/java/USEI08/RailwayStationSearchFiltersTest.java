package USEI08;

import Model.Country;
import Model.Filters.RailwayStationSearchFilters;
import Model.RailwayStation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@DisplayName("RailwayStationSearchFilters Unit Tests")
class RailwayStationSearchFiltersTest {
    private RailwayStationSearchFilters filters;
    private List<Country> countries;

    @Mock
    private RailwayStation mockStation;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        countries = new ArrayList<>();
        countries.add(Country.PT);
        countries.add(Country.ES);
        filters = new RailwayStationSearchFilters(true, true, false, countries);
    }

    @Test
    @DisplayName("Should create filters with valid parameters")
    void testFiltersCreation() {
        assertNotNull(filters);
        assertTrue(filters.getCity());
        assertTrue(filters.getMainStation());
        assertFalse(filters.getAirport());
        assertEquals(2, filters.getCountries().size());
    }

    @Test
    @DisplayName("Should return correct city filter")
    void testGetCity() {
        assertTrue(filters.getCity());
    }

    @Test
    @DisplayName("Should return correct main station filter")
    void testGetMainStation() {
        assertTrue(filters.getMainStation());
    }

    @Test
    @DisplayName("Should return correct airport filter")
    void testGetAirport() {
        assertFalse(filters.getAirport());
    }

    @Test
    @DisplayName("Should return correct countries list")
    void testGetCountries() {
        List<Country> retrievedCountries = filters.getCountries();
        assertEquals(2, retrievedCountries.size());
        assertTrue(retrievedCountries.contains(Country.PT));
        assertTrue(retrievedCountries.contains(Country.ES));
    }

    @Test
    @DisplayName("Should return true when station respects all filters")
    void testRespectsFiltersAllMatch() {
        when(mockStation.isCity()).thenReturn(true);
        when(mockStation.isMainStation()).thenReturn(true);
        when(mockStation.isAirport()).thenReturn(false);
        when(mockStation.getCountry()).thenReturn(Country.PT);

        assertTrue(filters.respectsFilters(mockStation));
    }

    @Test
    @DisplayName("Should return false when station city filter does not match")
    void testRespectsFiltersCityMismatch() {
        when(mockStation.isCity()).thenReturn(false);
        when(mockStation.isMainStation()).thenReturn(true);
        when(mockStation.isAirport()).thenReturn(false);
        when(mockStation.getCountry()).thenReturn(Country.PT);

        assertFalse(filters.respectsFilters(mockStation));
    }

    @Test
    @DisplayName("Should return false when station main station filter does not match")
    void testRespectsFiltersMainStationMismatch() {
        when(mockStation.isCity()).thenReturn(true);
        when(mockStation.isMainStation()).thenReturn(false);
        when(mockStation.isAirport()).thenReturn(false);
        when(mockStation.getCountry()).thenReturn(Country.PT);

        assertFalse(filters.respectsFilters(mockStation));
    }

    @Test
    @DisplayName("Should return false when station airport filter does not match")
    void testRespectsFiltersAirportMismatch() {
        when(mockStation.isCity()).thenReturn(true);
        when(mockStation.isMainStation()).thenReturn(true);
        when(mockStation.isAirport()).thenReturn(true);
        when(mockStation.getCountry()).thenReturn(Country.PT);

        assertFalse(filters.respectsFilters(mockStation));
    }

    @Test
    @DisplayName("Should return false when station country is not in filter list")
    void testRespectsFiltersCountryNotInList() {
        when(mockStation.isCity()).thenReturn(true);
        when(mockStation.isMainStation()).thenReturn(true);
        when(mockStation.isAirport()).thenReturn(false);
        when(mockStation.getCountry()).thenReturn(Country.FR);

        assertFalse(filters.respectsFilters(mockStation));
    }

    @Test
    @DisplayName("Should return true for equal filters")
    void testEqualsWithEqualFilters() {
        RailwayStationSearchFilters otherFilters = new RailwayStationSearchFilters(true, true, false, countries);
        assertEquals(filters, otherFilters);
    }

    @Test
    @DisplayName("Should return false for different city filter")
    void testEqualsWithDifferentCity() {
        RailwayStationSearchFilters otherFilters = new RailwayStationSearchFilters(false, true, false, countries);
        assertNotEquals(filters, otherFilters);
    }

    @Test
    @DisplayName("Should return false for different main station filter")
    void testEqualsWithDifferentMainStation() {
        RailwayStationSearchFilters otherFilters = new RailwayStationSearchFilters(true, false, false, countries);
        assertNotEquals(filters, otherFilters);
    }

    @Test
    @DisplayName("Should return false for different airport filter")
    void testEqualsWithDifferentAirport() {
        RailwayStationSearchFilters otherFilters = new RailwayStationSearchFilters(true, true, true, countries);
        assertNotEquals(filters, otherFilters);
    }

    @Test
    @DisplayName("Should return false for different countries")
    void testEqualsWithDifferentCountries() {
        List<Country> otherCountries = new ArrayList<>();
        otherCountries.add(Country.FR);
        RailwayStationSearchFilters otherFilters = new RailwayStationSearchFilters(true, true, false, otherCountries);
        assertNotEquals(filters, otherFilters);
    }

    @Test
    @DisplayName("Should return false when comparing with null")
    void testEqualsWithNull() {
        assertNotEquals(filters, null);
    }

    @Test
    @DisplayName("Should return false when comparing with different class")
    void testEqualsWithDifferentClass() {
        assertNotEquals(filters, "not a filter");
    }

    @Test
    @DisplayName("Should return same hash code for equal filters")
    void testHashCodeForEqualFilters() {
        RailwayStationSearchFilters otherFilters = new RailwayStationSearchFilters(true, true, false, countries);
        assertEquals(filters.hashCode(), otherFilters.hashCode());
    }

    @Test
    @DisplayName("Should return different hash code for different filters")
    void testHashCodeForDifferentFilters() {
        RailwayStationSearchFilters otherFilters = new RailwayStationSearchFilters(false, true, false, countries);
        assertNotEquals(filters.hashCode(), otherFilters.hashCode());
    }

    @Test
    @DisplayName("Should handle filters with null countries list")
    void testFiltersWithNullCountries() {
        RailwayStationSearchFilters nullCountriesFilters = new RailwayStationSearchFilters(true, true, false, null);
        assertNotNull(nullCountriesFilters);
        assertNull(nullCountriesFilters.getCountries());
    }

    @Test
    @DisplayName("Should handle filters with empty countries list")
    void testFiltersWithEmptyCountries() {
        List<Country> emptyCountries = new ArrayList<>();
        RailwayStationSearchFilters emptyFilters = new RailwayStationSearchFilters(true, true, false, emptyCountries);
        assertTrue(emptyFilters.getCountries().isEmpty());
    }
}