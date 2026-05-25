package Controllers.LAPR;

import Model.LAPR.*;
import Repositories.LAPR.RouteRepository;
import Repositories.LAPR.SchedulerRepository;
import Services.LAPR.TrainSchedulerService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class SchedulerController {

    private final TrainSchedulerService service;
    private final SchedulerRepository repository;
    private final RouteRepository routeRepository;

    public SchedulerController() {
        this.service = new TrainSchedulerService();
        this.repository = new SchedulerRepository();
        this.routeRepository = RouteRepository.getInstance();
    }

    public List<Train> getAvailableTrains() {
        return repository.getAllTrains();
    }

    public List<Facility> getAllFacilities() {
        return repository.getAllFacilities();
    }

    public List<Segment> getAllSegments() {
        return repository.getAllSegments();
    }

    public List<Route> getAvailableRoutes() {
        return routeRepository.getRoutes();
    }

    public List<TrainSchedule> generateSchedules(List<Train> trains, List<Route> routes, List<LocalDateTime> departureTimes) {
        return service.scheduleTrains(trains, routes, departureTimes);
    }

    public LocalDateTime getEstimatedArrivalTime(Train train, Route route, LocalDateTime departure) {
        return service.calculateEstimatedArrivalTime(train, route, departure);
    }

    public List<String> getConflictWarnings() {
        return service.getLastConflicts();
    }

    public boolean isTrainOccupied(String trainId, LocalDateTime start, LocalDateTime end, List<Train> currentTrains, List<LocalDateTime> departures, List<LocalDateTime> arrivals) {
        for (int i = 0; i < currentTrains.size(); i++) {
            if (currentTrains.get(i).getTrainId().equals(trainId)) {
                LocalDateTime itemDep = departures.get(i);
                LocalDateTime itemArr = arrivals.get(i);
                if (start.isBefore(itemArr) && itemDep.isBefore(end)) {
                    return true;
                }
            }
        }
        return false;
    }
}
