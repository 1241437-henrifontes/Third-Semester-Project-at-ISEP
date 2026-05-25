package Model;

import java.util.Objects;

/**
 * Represents a railway station with geographic and classification details.
 */
public class RailwayStation implements Comparable<RailwayStation> {
    private String name;
    private double latitude;
    private double longitude;
    private Country country;
    private String timeZone;
    private TimeZoneGroup timeZoneGroup;
    private boolean isCity;
    private boolean isMainStation;
    private boolean isAirport;

    /**
     * Constructs a RailwayStation with all required attributes.
     *
     * @param name station name
     * @param latitude station latitude
     * @param longitude station longitude
     * @param country station country
     * @param timeZone station time zone string
     * @param timeZoneGroup station time zone group enum
     * @param isCity true if station is in a city
     * @param isMainStation true if station is a main station
     * @param isAirport true if station is an airport
     */
    public RailwayStation(String name, double latitude, double longitude, Country country, String timeZone, TimeZoneGroup timeZoneGroup, boolean isCity, boolean isMainStation, boolean isAirport) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.country = country;
        this.timeZone = timeZone;
        this.timeZoneGroup = timeZoneGroup;
        this.isCity = isCity;
        this.isMainStation = isMainStation;
        this.isAirport = isAirport;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public String getTimezone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public TimeZoneGroup getTimeZoneGroup() {
        return timeZoneGroup;
    }

    public void setTimeZoneGroup(TimeZoneGroup timeZoneGroup) {
        this.timeZoneGroup = timeZoneGroup;
    }

    public boolean isCity() {
        return isCity;
    }

    public void setCity(boolean city) {
        isCity = city;
    }

    public boolean isMainStation() {
        return isMainStation;
    }

    public void setMainStation(boolean mainStation) {
        isMainStation = mainStation;
    }

    public boolean isAirport() {
        return isAirport;
    }

    public void setAirport(boolean airport) {
        isAirport = airport;
    }

    /**
     * Returns a string representation of the station.
     *
     * @return formatted string with station details
     */
    @Override
    public String toString() {
        return "RailwayStation{" +
                ", name='" + name + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", country=" + country +
                ", timezone='" + timeZone + '\'' +
                ", timezoneGroup=" + timeZoneGroup +
                ", isCity=" + isCity +
                ", isMainStation=" + isMainStation +
                ", isAirport=" + isAirport +
                '}';
    }

    /**
     * Compares this station to another by name (case-insensitive).
     *
     * @param o the other station
     * @return comparison result
     */
    @Override
    public int compareTo(RailwayStation o) {
        return this.name.compareToIgnoreCase(o.name);
    }

    /**
     * Checks equality based on all station attributes.
     *
     * @param o object to compare
     * @return true if equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        RailwayStation that = (RailwayStation) o;
        return isCity == that.isCity && isMainStation == that.isMainStation && isAirport == that.isAirport && Objects.equals(name, that.name) && Objects.equals(latitude, that.latitude) && Objects.equals(longitude, that.longitude) && country == that.country && Objects.equals(timeZone, that.timeZone) && timeZoneGroup == that.timeZoneGroup;
    }

    /**
     * Computes hash code based on station attributes.
     *
     * @return hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(name, latitude, longitude, country, timeZone, timeZoneGroup, isCity, isMainStation, isAirport);
    }
}
