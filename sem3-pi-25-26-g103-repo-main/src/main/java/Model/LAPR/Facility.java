package Model.LAPR;

/**
 * Represents a railway facility/station with an identifier and a human-readable name.
 */
public class Facility {

    private String stationId;
    private String name;
    public boolean hasSidings;
    private int numberOfSidings;

    /**
     * Creates a facility/station record.
     *
     * @param stationId        unique station identifier
     * @param name             human-readable station name
     * @param hasSidings       whether the facility has sidings
     * @param numberOfSidings  number of sidings available
     */
    public Facility(String stationId, String name, boolean hasSidings, int numberOfSidings) {
        this.stationId = stationId;
        this.name = name;
        this.hasSidings = hasSidings;
        this.numberOfSidings = numberOfSidings;
    }

    public boolean hasSidings() {
        return hasSidings;
    }

    public int getNumberOfSidings() {
        return numberOfSidings;
    }

    public String getStationId() {
        return stationId;
    }

    public void setStationId(String stationId) {
        this.stationId = stationId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Facility{" +
                "stationId='" + stationId + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
