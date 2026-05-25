package Controllers.LAPR;

import Repositories.LAPR.*;
import Model.LAPR.*;
import Services.LAPR.CrossingDetectionService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Controller responsible for scheduling trains along routes.
 * Provides helpers to list trains/routes/lines, create routes, compute schedules,
 * and dispatch multiple trains with crossing detection.
 */
public class TrainSchedulerController {

    private CrossingDetectionService crossingService;

    /**
     * Creates a new controller with a crossing detection service.
     */
    public TrainSchedulerController() {
        this.crossingService = new CrossingDetectionService();
    }

    /**
     * Retrieves all available trains from the repository.
     *
     * @return list of trains
     */
    public List<Train> getAvailableTrains() {
        return TrainRepository.getInstance().getTrains();
    }

    /**
     * Retrieves all available routes from the repository.
     *
     * @return list of routes
     */
    public List<Route> getAvailableRoutes() {
        return RouteRepository.getInstance().getRoutes();
    }

    /**
     * Retrieves all available railway lines from the repository.
     *
     * @return list of lines
     */
    public List<Line> getLines() {
        return LineRepository.getInstance().getLines();
    }

    /**
     * Retrieves all segments for a given line, ordered by their sequence (order field).
     *
     * @param lineId the line identifier
     * @return ordered list of segments belonging to the line
     */
    public List<Segment> getSegmentsForLineOrdered(int lineId) {
        return SegmentRepository.getInstance().getSegments().stream()
                .filter(s -> s.getLineID() == lineId)
                .sorted(Comparator.comparingInt(Segment::getOrder))
                .collect(Collectors.toList());
    }

    /**
     * Create a manual route that contains all segments belonging to the selected line (ordered).
     */
    public Route createManualRoute(String routeName, int lineId) {
        List<Route> existing = RouteRepository.getInstance().getRoutes();
        Route r = new Route(existing.size() + 1, routeName);
        List<Segment> segments = getSegmentsForLineOrdered(lineId);
        segments.forEach(r::addSegment);
        RouteRepository.getInstance().addRoute(r);
        return r;
    }

    // Em controllers.LAPR.TrainSchedulerController.java
    public Route createCustomRoute(String routeName, List<Integer> segmentIds) {
        List<Route> existing = RouteRepository.getInstance().getRoutes();
        Route r = new Route(existing.size() + 1, routeName);

        // Ordenar segmentos para garantir sequência correta
        List<Segment> segments = SegmentRepository.getInstance().getSegments().stream()
                .filter(s -> segmentIds.contains(s.getOrder()))
                .sorted(Comparator.comparingInt(Segment::getOrder))
                .collect(Collectors.toList());

        segments.forEach(r::addSegment);
        RouteRepository.getInstance().addRoute(r);
        return r;
    }

    /**
     * Calculate a simple schedule for the train over the route starting at departure.
     * Uses Locomotive.calculateEstimatedTimeTravel for per-segment time.
     */
    public TrainSchedule calculateSchedule(Train train, Route route, LocalDateTime departure) {
        if (train == null || route == null || departure == null) {
            throw new IllegalArgumentException("Train, route and departure time cannot be null");
        }

        if (route.getSegments().isEmpty()) {
            throw new IllegalArgumentException("Route has no segments");
        }

        // Verificar compatibilidade de bitola
        for (Segment s : route.getSegments()) {
            Line line = LineRepository.getInstance().getLines().stream()
                    .filter(l -> l.getLineId() == s.getLineID()).findFirst().orElse(null);
            if (line != null && line.getGauge() != train.getLocomotive().getGaugeId()) {
                throw new IllegalArgumentException(String.format(
                        "Train gauge (%d) incompatible with line %s gauge (%d)",
                        train.getLocomotive().getGaugeId(), line.getName(), line.getGauge()
                ));
            }
        }
        TrainSchedule schedule = new TrainSchedule(train, route, departure);
        LocalDateTime current = departure;

        for (Segment s : route.getSegments()) {
            // compute seconds using locomotive characteristics
            double seconds = 0.0;
            try {
                seconds = Locomotive.calculateEstimatedTimeTravel(s, train.getLocomotive());
            } catch (Exception ex) {
                // fallback: estimate with 40 km/h
                double fallbackSpeed = 40.0 / 3.6;
                seconds = s.getLength() / fallbackSpeed;
            }

            current = current.plusSeconds((long) seconds);

            // attempt to find a facility for the end of the line where this segment belongs
            Optional<Line> lineOpt = LineRepository.getInstance().getLines().stream()
                    .filter(l -> l.getLineId() == s.getLineID()).findFirst();
            Facility facility = null;
            Line line = LineRepository.getInstance().getLineById(s.getLineID());
            String facilityId = line.getEndName();
            facility = FacilityRepository.getInstance().getFacilities().stream()
                    .filter(f -> f.getStationId().equals(facilityId))
                    .findFirst()
                    .orElse(null);

            schedule.addSchedulePoint(new SchedulePoint(facility, current,3));
        }

        return schedule;
    }

    /**
     * Calculate an ADVANCED schedule considering weight, power, and track limitations.
     * This method uses the new calculateEstimatedTimeTravelAdvanced method.
     *
     * @param train     the train to schedule
     * @param route     the route to follow
     * @param departure the departure time
     * @return complete schedule with realistic travel times
     */
    public TrainSchedule calculateAdvancedSchedule(Train train, Route route, LocalDateTime departure) {
        TrainSchedule schedule = new TrainSchedule(train, route, departure);
        LocalDateTime current = departure;

        List<Segment> segments = route.getSegments();

        for (int i = 0; i < segments.size(); i++) {
            Segment s = segments.get(i);

            // Calculate travel time for this segment
            double seconds = 0.0;
            try {
                // Use ADVANCED calculation (weight, power, speed limits)
                seconds = Locomotive.calculateEstimatedTimeTravelAdvanced(s, train.getLocomotive(), train);
            } catch (Exception ex) {
                // fallback to simple calculation
                try {
                    seconds = Locomotive.calculateEstimatedTimeTravel(s, train.getLocomotive());
                } catch (Exception ex2) {
                    // ultimate fallback: 40 km/h
                    double fallbackSpeed = 40.0 / 3.6;
                    seconds = s.getLength() / fallbackSpeed;
                }
            }

            // Add travel time to current time to get arrival
            current = current.plusSeconds((long) seconds);

            // Find facility at end of this segment
            Optional<Line> lineOpt = LineRepository.getInstance().getLines().stream()
                    .filter(l -> l.getLineId() == s.getLineID()).findFirst();
            Facility facility = null;
            if (lineOpt.isPresent()) {
                String facilityId = lineOpt.get().getEndName();
                List<Facility> facilities = FacilityRepository.getInstance().getFacilities();
                List<Facility> matched = facilities.stream()
                        .filter(f -> f.getStationId().equals(facilityId))
                        .toList();

                facility = FacilityRepository.getInstance().getFacilities().stream()
                        .filter(f -> f.getStationId().equals(facilityId))
                        .findFirst()
                        .orElse(null);


            }

            // Create schedule point with arrival time and segment reference
            SchedulePoint sp = new SchedulePoint(facility, current, s,3);

            // Determine stop duration based on position
            if (i == segments.size() - 1) {
                // Last station - no departure needed (final destination)
                sp.setStopDurationMinutes(0);
            } else if (i == 0) {
                // First station after departure - short stop (1 min)
                sp.setStopDurationMinutes(1);
            } else {
                // Intermediate stations - normal stop (3 minutes)
                sp.setStopDurationMinutes(3);
            }

            schedule.addSchedulePoint(sp);

            // Update current time to departure time for next segment
            current = sp.getEstimatedDeparture();
        }

        return schedule;
    }

    /**
     * Dispatch multiple trains with crossing detection and conflict resolution.
     * This method schedules all trains and detects/resolves crossing conflicts.
     *
     * @param trainRouteMap            map of trains to their assigned routes
     * @param startTime                base departure time for the first train
     * @param departureIntervalMinutes time gap between consecutive train departures
     * @return list of complete schedules with crossing operations marked
     */
    public List<TrainSchedule> dispatchMultipleTrains(Map<Train, Route> trainRouteMap,
                                                      LocalDateTime startTime,
                                                      int departureIntervalMinutes) {
        List<TrainSchedule> schedules = new ArrayList<>();
        LocalDateTime currentDeparture = startTime;

        // 1. Calculate initial schedules for all trains
        for (Map.Entry<Train, Route> entry : trainRouteMap.entrySet()) {
            Train train = entry.getKey();
            Route route = entry.getValue();

            TrainSchedule schedule = calculateAdvancedSchedule(train, route, currentDeparture);
            schedules.add(schedule);

            // Increment departure time for next train
            currentDeparture = currentDeparture.plusMinutes(departureIntervalMinutes);
        }

        // 2. Detect crossings on single-track segments
        List<Crossing> crossings = crossingService.detectCrossings(schedules);

        // 3. Apply delays to resolve crossings
        crossingService.applyCrossingDelays(schedules, crossings);

        // 4. Return complete schedules with crossing information
        return schedules;
    }

    /**
     * Detect crossings for a list of schedules without modifying them.
     *
     * @param schedules list of train schedules
     * @return detected crossing points
     */
    public List<Crossing> detectCrossings(List<TrainSchedule> schedules) {
        return crossingService.detectCrossings(schedules);
    }
}
