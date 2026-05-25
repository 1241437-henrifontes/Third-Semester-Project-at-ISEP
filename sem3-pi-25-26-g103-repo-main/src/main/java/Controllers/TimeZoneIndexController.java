package Controllers;

import Repositories.TreeRepository;
import Model.Wrappers.RailwayStationLatitude;
import Model.Wrappers.RailwayStationLongitude;
import Model.Country;
import Model.RailwayStation;
import Model.TimeZoneGroup;
import UI.Utils.FilePrinterStations;

import java.util.List;

public class TimeZoneIndexController {

    public TreeRepository getStationRepository() {
        return TreeRepository.getInstance();
    }

    private List<RailwayStationLatitude> getStationsByLat(double min, double max) {
        return getStationRepository().searchByLatitudeRange(min, max);
    }

    private List<RailwayStationLongitude> getStationsByLon(double min, double max) {
        return getStationRepository().searchByLongitudeRange(min, max);
    }

    private List<RailwayStation> getStationsByTZC(TimeZoneGroup tzGroup, Country country) {
        return getStationRepository().searchByTimeZoneAndCountry(tzGroup, country);
    }

    private List<RailwayStation> getStationsByWindow(TimeZoneGroup lower, TimeZoneGroup upper) {
        return getStationRepository().getStationsByTimeZoneWindow(lower, upper);
    }

    public void printStationsByLat(double min, double max) {
        FilePrinterStations.printLat(min, max, getStationsByLat(min, max));
    }

    public void printStationsByLon(double min, double max) {
        FilePrinterStations.printLon(min, max, getStationsByLon(min, max));
    }

    public List<Country> filterCountries(TimeZoneGroup tzGroup) {
        return getStationRepository().filterCountries(tzGroup);
    }

    public void printStationsByTZC(TimeZoneGroup tzGroup, Country country) {
        FilePrinterStations.printTZC(tzGroup, country, getStationsByTZC(tzGroup, country));
    }

    public void printStationsByWindowQuery(TimeZoneGroup lower, TimeZoneGroup upper) {
        FilePrinterStations.printWindow(lower, upper, getStationsByWindow(lower, upper));
    }
}
