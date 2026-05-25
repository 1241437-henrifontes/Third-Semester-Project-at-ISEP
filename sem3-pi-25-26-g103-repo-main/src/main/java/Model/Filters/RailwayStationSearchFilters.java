package Model.Filters;

import Model.Country;
import Model.RailwayStation;

import java.util.List;
import java.util.Objects;

public class RailwayStationSearchFilters {
    private Boolean isCity;
    private Boolean isMainStation;
    private Boolean isAirport;
    private List<Country> countries;

    public RailwayStationSearchFilters(Boolean isCity, Boolean isMainStation, Boolean isAirport, List<Country> countries) {
        this.isCity = isCity;
        this.isMainStation = isMainStation;
        this.isAirport = isAirport;
        this.countries = countries;
    }

    public Boolean getCity() {
        return isCity;
    }

    public Boolean getMainStation() {
        return isMainStation;
    }

    public Boolean getAirport() {
        return isAirport;
    }

    public List<Country> getCountries() {
        return countries;
    }

    public boolean respectsFilters(RailwayStation station) {
        return (station.isCity() == isCity && station.isMainStation() == isMainStation && station.isAirport() == isAirport && countries.contains(station.getCountry()));
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        RailwayStationSearchFilters that = (RailwayStationSearchFilters) o;
        return Objects.equals(isCity, that.isCity) && Objects.equals(isMainStation, that.isMainStation) && Objects.equals(isAirport, that.isAirport) && Objects.equals(countries, that.countries);
    }

    @Override
    public int hashCode() {
        return Objects.hash(isCity, isMainStation, isAirport, countries);
    }
}
