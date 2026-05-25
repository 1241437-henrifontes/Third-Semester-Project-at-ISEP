package Model.LAPR;

/**
 * Represents a railway segment belonging to a specific railway line.
 *
 * A segment defines a portion of a railway line and includes
 * infrastructure characteristics such as electrification,
 * maximum-supported weight, length, number of tracks and siding availability.
 *
 * Segments may optionally store information about their start and end stations.
 */
public class Segment {
    private int lineID;
    private int order;
    private Boolean isElectrified;
    private double maxWeight;
    private double length;
    private int numberOfTracks;
    private boolean siding;

    private String startStationId;
    private String endStationId;
    private String startStationName;
    private String endStationName;

    /**
     * Creates a fully defined railway segment.
     *
     * @param lineID             identifier of the railway line
     * @param order              order of the segment within the line
     * @param isElectrified      whether the segment is electrified
     * @param maxWeight          maximum supported weight
     * @param length             segment length
     * @param numberOfTracks     number of tracks in the segment
     * @param siding             whether the segment has a siding
     * @param startStationId     identifier of the start station
     * @param endStationId       identifier of the end station
     * @param startStationName   name of the start station
     * @param endStationName     name of the end station
     */
    public Segment(int lineID, int order, Boolean isElectrified, double maxWeight, double length, int numberOfTracks, boolean siding, String startStationId, String endStationId, String startStationName, String endStationName) {
        this.lineID = lineID;
        this.order = order;
        this.isElectrified = isElectrified;
        this.maxWeight = maxWeight;
        this.length = length;
        this.numberOfTracks = numberOfTracks;
        this.siding = siding;
        this.startStationId = startStationId;
        this.endStationId = endStationId;
        this.startStationName = startStationName;
        this.endStationName = endStationName;
    }

    public Segment(int lineID, int order, Boolean isElectrified, double maxWeight, double length, int numberOfTracks, boolean siding) {
        this(lineID, order, isElectrified, maxWeight, length, numberOfTracks, siding, null, null, null, null);
    }

    public int getLineID() { return lineID; }
    public int getOrder() { return order; }
    public Boolean getElectrified() { return isElectrified; }
    public double getMaxWeight() { return maxWeight; }
    public double getLength() { return length; }
    public int getNumberOfTracks() { return numberOfTracks; }
    public boolean hasSiding() { return siding; }

    public String getStartStationId() { return startStationId; }
    public String getEndStationId() { return endStationId; }
    public String getStartStationName() { return startStationName; }
    public String getEndStationName() { return endStationName; }

    @Override
    public String toString() {
        return "Segment{" + "lineID=" + lineID + ", order=" + order + ", length=" + length + '}';
    }
    public boolean isSingleTrack() {
        return numberOfTracks == 1;
    }

    public void setSiding(boolean siding) {
        this.siding = siding;
    }
}