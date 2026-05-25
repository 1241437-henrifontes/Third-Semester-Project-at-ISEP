package Model.LAPR;

import java.time.LocalDateTime;

/**
 * Represents a planned crossing (meeting) of two trains at a location and time.
 */
public class Crossing {
    private Facility location;
    private TrainSchedule train1;
    private TrainSchedule train2;
    private LocalDateTime crossingTime;

    public Crossing(Facility location, TrainSchedule train1, TrainSchedule train2, LocalDateTime crossingTime) {
        this.location = location;
        this.train1 = train1;
        this.train2 = train2;
        this.crossingTime = crossingTime;
    }

    public Facility getLocation() {
        return location;
    }

    public TrainSchedule getTrain1() {
        return train1;
    }

    public TrainSchedule getTrain2() {
        return train2;
    }

    public LocalDateTime getCrossingTime() {
        return crossingTime;
    }

    @Override
    public String toString() {
        return "Crossing{" +
                "location=" + (location != null ? location.getName() : "<none>") +
                ", time=" + crossingTime +
                '}';
    }
}
