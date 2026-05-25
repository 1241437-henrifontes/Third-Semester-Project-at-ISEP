package USEI06;

import Repositories.TreeRepository;
import Model.Country;
import Model.RailwayStation;
import Model.TimeZoneGroup;
import Model.Wrappers.RailwayStationLatitude;
import Model.Wrappers.RailwayStationLongitude;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class TreeRepositoryTest {

    private TreeRepository repo;

    @BeforeEach
    void setUp() throws Exception {
        repo = TreeRepository.getInstance();
        resetField(repo, "latitudeTree", new Model.Trees.AVLTree<Model.Wrappers.RailwayStationLatitude>());
        resetField(repo, "longitudeTree", new Model.Trees.AVLTree<Model.Wrappers.RailwayStationLongitude>());
        resetField(repo, "timeZoneCountryTree", new Model.Trees.AVLTree<Model.Wrappers.TimeZoneIndex>());
        add("Porto", 41.15, -8.61, Country.PT, TimeZoneGroup.WET_GMT);
        add("Lisboa", 38.72, -9.14, Country.PT, TimeZoneGroup.WET_GMT);
        add("Madrid", 40.42, -3.70, Country.ES, TimeZoneGroup.CET);
        add("Berlin", 52.52, 13.40, Country.DE, TimeZoneGroup.CET);
    }

    private static void resetField(Object target, String fieldName, Object value) throws Exception {
        Field f = target.getClass().getDeclaredField(fieldName);
        f.setAccessible(true);
        f.set(target, value);
    }

    private void add(String name, double lat, double lon, Country c, TimeZoneGroup g) {
        repo.addStation(new RailwayStation(name, lat, lon, c, g.getName(), g, false, false, false));
    }

    @Test
    void loadStations() {
        assertNotNull(repo.getLatitudeTree());
        assertNotNull(repo.getLongitudeTree());
    }

    @Test
    void addStation() {
        assertEquals(4, repo.getLatitudeTree().size());
        assertEquals(4, repo.getLongitudeTree().size());
        var countriesWET = repo.filterCountries(TimeZoneGroup.WET_GMT);
        assertTrue(countriesWET.contains(Country.PT));
    }

    @Test
    void searchByLatitudeRange() {
        List<RailwayStationLatitude> list = repo.searchByLatitudeRange(39.0, 45.0);
        Set<Double> lats = list.stream().map(RailwayStationLatitude::getLatitude).collect(Collectors.toSet());
        assertTrue(lats.containsAll(java.util.Set.of(41.15, 40.42)));
        assertFalse(lats.contains(38.72));
        assertFalse(lats.contains(52.52));
    }

    @Test
    void searchByLongitudeRange() {
        List<RailwayStationLongitude> list = repo.searchByLongitudeRange(-10.0, 0.0);
        Set<Double> lons = list.stream().map(RailwayStationLongitude::getLongitude).collect(Collectors.toSet());
        assertTrue(lons.containsAll(java.util.Set.of(-8.61, -9.14, -3.70)));
        assertFalse(lons.contains(13.40));
    }

    @Test
    void searchByTimeZoneAndCountry() {
        List<RailwayStation> es = repo.searchByTimeZoneAndCountry(TimeZoneGroup.CET, Country.ES);
        assertEquals(List.of("Madrid"), es.stream().map(RailwayStation::getName).toList());
        List<RailwayStation> pt = repo.searchByTimeZoneAndCountry(TimeZoneGroup.WET_GMT, Country.PT);
        assertEquals(List.of("Lisboa","Porto"), pt.stream().map(RailwayStation::getName).toList());
    }

    @Test
    void getStationsByTimeZoneWindow() {
        List<RailwayStation> res = repo.getStationsByTimeZoneWindow(TimeZoneGroup.WET_GMT, TimeZoneGroup.CET);
        var names = res.stream().map(RailwayStation::getName).sorted().toList();
        assertEquals(List.of("Berlin","Lisboa","Madrid","Porto"), names);
    }
    
    @Test
    void invertedLatitudeRangeReturnsEmpty() {
        var res = repo.searchByLatitudeRange(50.0, 10.0);
        assertTrue(res.isEmpty());
    }

    @Test
    void invertedLongitudeRangeReturnsEmpty() {
        var res = repo.searchByLongitudeRange(10.0, -10.0);
        assertTrue(res.isEmpty());
    }

    @Test
    void filterCountriesAbsentTimeZoneReturnsEmpty() {
        assertTrue(repo.filterCountries(TimeZoneGroup.FET).isEmpty());
    }

    @Test
    void timeZoneWindowLowerGreaterThanUpperReturnsEmpty() {
        var res = repo.getStationsByTimeZoneWindow(TimeZoneGroup.FET, TimeZoneGroup.CET);
        assertTrue(res.isEmpty());
    }

    @Test
    void exactTimeZoneWindowSingleGroup() {
        var res = repo.getStationsByTimeZoneWindow(TimeZoneGroup.CET, TimeZoneGroup.CET);
        var names = res.stream().map(RailwayStation::getName).sorted().toList();
        assertEquals(java.util.List.of("Berlin","Madrid"), names);
    }
}