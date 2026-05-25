package Model.LAPR;

import java.time.LocalDateTime;

/**
 * A scheduled passing point (usually a facility/station) with estimated arrival and departure times.
 */
public class SchedulePoint {
    private Facility facility;
    private LocalDateTime estimatedArrival;
    private LocalDateTime estimatedDeparture;
    private boolean requiresCrossing;
    private Segment segmentBefore; // Segment that leads TO this point

    public SchedulePoint(Facility facility, LocalDateTime estimatedArrival, int waitTimeMinutes) {
        this.facility = facility;
        this.estimatedArrival = estimatedArrival;
        // Default: 3 minutes stop at each station
        this.estimatedDeparture = estimatedArrival.plusMinutes(waitTimeMinutes);
        this.requiresCrossing = false;
        this.segmentBefore = null;
    }

    public SchedulePoint(Facility facility, LocalDateTime estimatedArrival, Segment segmentBefore, int waitTimeMinutes) {
        this.facility = facility;
        this.estimatedArrival = estimatedArrival;
        this.estimatedDeparture = estimatedArrival.plusMinutes(waitTimeMinutes);
        this.requiresCrossing = false;
        this.segmentBefore = segmentBefore;
    }

    public Facility getFacility() {
        return facility;
    }

    public LocalDateTime getEstimatedArrival() {
        return estimatedArrival;
    }

    public LocalDateTime getEstimatedDeparture() {
        return estimatedDeparture;
    }

    public boolean isRequiresCrossing() {
        return requiresCrossing;
    }

    public void setDepartureDelaySeconds(long seconds) {
        this.estimatedDeparture = this.estimatedDeparture.plusSeconds(seconds);
    }

    public void setStopDurationMinutes(int minutes) {
        this.estimatedDeparture = this.estimatedArrival.plusMinutes(minutes);
    }

    public Segment getSegmentBefore() {
        return segmentBefore;
    }

    public void setSegmentBefore(Segment segment) {
        this.segmentBefore = segment;
    }

    public void markRequiresCrossing() {
        this.requiresCrossing = true;
    }

    /**
     * Add a delay (in seconds) to this schedule point. This shifts both arrival and departure
     * so downstream schedule points remain consistent when applied cumulatively.
     */
    public void addDelaySeconds(long seconds) {
        if (seconds == 0) return;
        this.estimatedArrival = this.estimatedArrival.plusSeconds(seconds);
        this.estimatedDeparture = this.estimatedDeparture.plusSeconds(seconds);
    }

    @Override
    public String toString() {
        return "SchedulePoint{" +
                "facility=" + (facility != null ? facility.getName() : "<none>") +
                ", arrival=" + estimatedArrival +
                ", departure=" + estimatedDeparture +
                ", requiresCrossing=" + requiresCrossing +
                '}';
    }
}
