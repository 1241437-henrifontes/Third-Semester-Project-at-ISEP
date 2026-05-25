package Model.LAPR;

/**
 * Represents a railway line with its endpoints, ownership, and track gauge.
 */
public class Line {

    private int lineId;
    private String name;
    private int startId;
    private int endId;
    private int gauge;
    private String startName;
    private String endName;
    private String ownerManagingVatNumber;

    public Line(int lineId, String name, int startId, int endId, int gauge, String startName, String endName, String ownerManagingVatNumber) {
        this.lineId = lineId;
        this.name = name;
        this.startId = startId;
        this.endId = endId;
        this.gauge = gauge;
        this.startName = startName;
        this.endName = endName;
        this.ownerManagingVatNumber = ownerManagingVatNumber;
    }

    public int getLineId() {
        return lineId;
    }

    public void setLineId(int lineId) {
        this.lineId = lineId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStartId() {
        return startId;
    }

    public void setStartId(int startId) {
        this.startId = startId;
    }

    public int getEndId() {
        return endId;
    }

    public void setEndId(int endId) {
        this.endId = endId;
    }

    public int getGauge() {
        return gauge;
    }

    public void setGauge(int gauge) {
        this.gauge = gauge;
    }

    public String getStartName() {
        return startName;
    }

    public void setStartName(String startName) {
        this.startName = startName;
    }

    public String getEndName() {
        return endName;
    }

    public void setEndName(String endName) {
        this.endName = endName;
    }

    public String getOwnerManagingVatNumber() {
        return ownerManagingVatNumber;
    }

    public void setOwnerManagingVatNumber(String ownerManagingVatNumber) {
        this.ownerManagingVatNumber = ownerManagingVatNumber;
    }

    @Override
    public String toString() {
        return "Line{" +
                "lineId=" + lineId +
                ", name='" + name + '\'' +
                ", startId=" + startId +
                ", endId=" + endId +
                ", gauge=" + gauge +
                ", startName='" + startName + '\'' +
                ", endName='" + endName + '\'' +
                ", ownerManagingVatNumber='" + ownerManagingVatNumber + '\'' +
                '}';
    }
}
