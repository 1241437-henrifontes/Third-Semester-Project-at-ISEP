package Model.Filters;

import Model.RailwayStation;
import Model.TimeZoneGroup;

/**
 * Filter class for filtering railway stations based on various criteria.
 * Used in proximity search operations.
 */
public class StationFilter {

    private TimeZoneGroup minTimeZone;
    private TimeZoneGroup maxTimeZone;
    private Boolean isCity;
    private Boolean isMainStation;
    private Boolean isAirport;

    public StationFilter() {
        // Default constructor - no filters applied
    }

    public StationFilter withTimeZoneRange(TimeZoneGroup minTimeZone, TimeZoneGroup maxTimeZone) {
        this.minTimeZone = minTimeZone;
        this.maxTimeZone = maxTimeZone;
        return this;
    }

    public StationFilter withIsCity(Boolean isCity) {
        this.isCity = isCity;
        return this;
    }

    public StationFilter withIsMainStation(Boolean isMainStation) {
        this.isMainStation = isMainStation;
        return this;
    }

    public StationFilter withIsAirport(Boolean isAirport) {
        this.isAirport = isAirport;
        return this;
    }

    /**
     * Checks if a station matches all the active filters.
     *
     * @param station The station to check
     * @return true if the station matches all filters, false otherwise
     */
    public boolean matches(RailwayStation station) {
        if (station == null) {
            return false;
        }

        // Check timezone filter
        if (minTimeZone != null && maxTimeZone != null) {
            TimeZoneGroup stationTimeZone = station.getTimeZoneGroup();
            if (stationTimeZone == null || !stationTimeZone.isBetween(minTimeZone, maxTimeZone)) {
                return false;
            }
        }

        // Check city filter
        if (isCity != null && station.isCity() != isCity) {
            return false;
        }

        // Check main station filter
        if (isMainStation != null && station.isMainStation() != isMainStation) {
            return false;
        }

        // Check airport filter
        if (isAirport != null && station.isAirport() != isAirport) {
            return false;
        }

        return true;
    }

    public TimeZoneGroup getMinTimeZone() {
        return minTimeZone;
    }

    public TimeZoneGroup getMaxTimeZone() {
        return maxTimeZone;
    }

    public Boolean getIsCity() {
        return isCity;
    }

    public Boolean getIsMainStation() {
        return isMainStation;
    }

    public Boolean getIsAirport() {
        return isAirport;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("StationFilter{");
        if (minTimeZone != null && maxTimeZone != null) {
            sb.append("timeZone=").append(minTimeZone).append("-").append(maxTimeZone).append(", ");
        }
        if (isCity != null) {
            sb.append("isCity=").append(isCity).append(", ");
        }
        if (isMainStation != null) {
            sb.append("isMainStation=").append(isMainStation).append(", ");
        }
        if (isAirport != null) {
            sb.append("isAirport=").append(isAirport);
        }
        sb.append("}");
        return sb.toString();
    }
}
