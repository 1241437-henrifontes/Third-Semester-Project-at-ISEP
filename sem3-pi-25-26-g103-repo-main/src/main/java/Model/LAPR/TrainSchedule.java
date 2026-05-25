package Model.LAPR;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents the planned schedule for a Train along a Route from a given departure time.
 */
public class TrainSchedule {
    private Train train;
    private Route route;
    private LocalDateTime departureTime;
    private List<SchedulePoint> schedulePoints;

    public TrainSchedule(Train train, Route route, LocalDateTime departureTime) {
        this.train = train;
        this.route = route;
        this.departureTime = departureTime;
        this.schedulePoints = new ArrayList<>();
    }

    public Train getTrain() {
        return train;
    }

    public Route getRoute() {
        return route;
    }

    public LocalDateTime getDepartureTime() {
        return departureTime;
    }

    public List<SchedulePoint> getSchedulePoints() {
        return schedulePoints;
    }

    public void addSchedulePoint(SchedulePoint sp) {
        this.schedulePoints.add(sp);
    }

    @Override
    public String toString() {
        return "TrainSchedule{" +
                "train=" + (train != null ? train.getTrainId() : "<none>") +
                ", route=" + (route != null ? route.getRouteName() : "<none>") +
                ", departure=" + departureTime +
                ", points=" + schedulePoints.size() +
                '}';
    }
}
