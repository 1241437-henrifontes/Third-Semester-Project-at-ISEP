package Services.LAPR;

import Model.LAPR.*;
import Repositories.LAPR.SchedulerRepository;
import java.time.LocalDateTime;
import java.util.*;

public class TrainSchedulerService {

    private final SchedulerRepository repository;
    private final List<String> lastConflicts = new ArrayList<>();

    public TrainSchedulerService() {
        this.repository = new SchedulerRepository();
    }

    public List<TrainSchedule> scheduleTrains(List<Train> trains, List<Route> routes, List<LocalDateTime> departureTimes) {
        lastConflicts.clear();
        List<TrainSchedule> schedules = new ArrayList<>();
        
        for (int i = 0; i < trains.size(); i++) {
            Train train = trains.get(i);
            Route route = routes.get(i);
            LocalDateTime departure = departureTimes.get(i);
            TrainSchedule schedule = calculateInitialSchedule(train, route, departure);
            schedules.add(schedule);
        }

        resolveConflicts(schedules);
        return schedules;
    }

    public List<String> getLastConflicts() {
        return new ArrayList<>(lastConflicts);
    }

    public LocalDateTime calculateEstimatedArrivalTime(Train train, Route route, LocalDateTime departure) {
        TrainSchedule schedule = calculateInitialSchedule(train, route, departure);
        if (schedule.getSchedulePoints().isEmpty()) return departure;
        return schedule.getSchedulePoints().get(schedule.getSchedulePoints().size() - 1).getEstimatedArrival();
    }

    private TrainSchedule calculateInitialSchedule(Train train, Route route, LocalDateTime departure) {
        TrainSchedule schedule = new TrainSchedule(train, route, departure);
        LocalDateTime currentTime = departure;
        List<String> origins = route.getAllOrigins();
        List<String> destinations = route.getAllDestinations();
        boolean flagPassOrigin = false;
        int counter = 0;
        int waitTimeMinutes;

        String firstStationId = route.getSegments().get(0).getStartStationId();
        String firstStationName = route.getSegments().get(0).getStartStationName();
        schedule.addSchedulePoint(new SchedulePoint(new Facility(firstStationId, firstStationName, false, 0), currentTime, 0));

        for (Segment segment : route.getSegments()) {
            double travelTimeSeconds = Locomotive.calculateEstimatedTimeTravelAdvanced(segment, train.getLocomotive(), train);
            currentTime = currentTime.plusSeconds((long) travelTimeSeconds);
            Facility endFacility = new Facility(segment.getEndStationId(), segment.getEndStationName(), segment.hasSiding(), segment.hasSiding() ? 1 : 0);
            if (endFacility.getStationId().equals(origins.get(counter))) {
                waitTimeMinutes = 30;
                flagPassOrigin = true;
            } else if (endFacility.getStationId().equals(destinations.get(counter)) && flagPassOrigin) {
                waitTimeMinutes = 30;
                flagPassOrigin = false;
                if (destinations.size() != counter+1) {
                    counter++;
                }
            }else {
                waitTimeMinutes = 3;
            }
            schedule.addSchedulePoint(new SchedulePoint(endFacility, currentTime, segment, waitTimeMinutes));
            currentTime = currentTime.plusMinutes(waitTimeMinutes);
        }
        return schedule;
    }

    private void resolveConflicts(List<TrainSchedule> schedules) {
        class Occupation {
            TrainSchedule schedule;
            SchedulePoint point;
            SchedulePoint prevPoint;
            java.time.LocalDateTime enter;
            java.time.LocalDateTime leave;
            int priority;
            Segment segment;
            Occupation(TrainSchedule sch, SchedulePoint p, SchedulePoint prev, java.time.LocalDateTime e, java.time.LocalDateTime l, int pr, Segment seg) {
                this.schedule = sch; this.point = p; this.prevPoint = prev; this.enter = e; this.leave = l; this.priority = pr; this.segment = seg;
            }
        }

        Map<String, List<Occupation>> bySegment = new HashMap<>();
        for (int i = 0; i < schedules.size(); i++) {
            TrainSchedule sch = schedules.get(i);
            for (SchedulePoint p : sch.getSchedulePoints()) {
                if (p.getSegmentBefore() == null) continue;
                SchedulePoint prev = getPreviousPoint(sch, p);
                if (prev == null) continue;
                java.time.LocalDateTime enter = prev.getEstimatedDeparture();
                java.time.LocalDateTime leave = p.getEstimatedArrival();
                Segment seg = p.getSegmentBefore();
                String key = seg.getLineID() + ":" + seg.getOrder();
                Occupation occ = new Occupation(sch, p, prev, enter, leave, i, seg);
                bySegment.computeIfAbsent(key, k -> new ArrayList<>()).add(occ);
            }
        }

        for (Map.Entry<String, List<Occupation>> entry : bySegment.entrySet()) {
            List<Occupation> occs = entry.getValue();
            occs.sort(Comparator.comparingInt((Occupation o) -> o.priority).thenComparing(o -> o.enter));
            if (occs.isEmpty()) continue;
            int tracks = occs.get(0).segment.getNumberOfTracks();
            List<Occupation> accepted = new ArrayList<>();

            for (Occupation occ : occs) {
                occ.enter = occ.prevPoint.getEstimatedDeparture();
                occ.leave = occ.point.getEstimatedArrival();

                int overlapping = 0;
                for (Occupation a : accepted) {
                    if (!(a.leave.isBefore(occ.enter) || occ.leave.isBefore(a.enter))) overlapping++;
                }

                if (overlapping < tracks) {
                    accepted.add(occ);
                    continue;
                }

                java.time.LocalDateTime earliestLeave = null;
                for (Occupation a : accepted) {
                    if (!(a.leave.isBefore(occ.enter) || occ.leave.isBefore(a.enter))) {
                        if (earliestLeave == null || a.leave.isBefore(earliestLeave)) earliestLeave = a.leave;
                    }
                }
                if (earliestLeave == null) {
                    accepted.add(occ);
                    continue;
                }

                long requiredDelaySeconds = java.time.Duration.between(occ.prevPoint.getEstimatedDeparture(), earliestLeave).getSeconds();
                if (requiredDelaySeconds > 0) {
                    applyDelay(occ.schedule, occ.prevPoint, requiredDelaySeconds);
                    lastConflicts.add(String.format("Conflict: Train %s delayed by %ds on Line %d at %s to allow crossing (tracks=%d).",
                            occ.schedule.getTrain().getTrainId(), requiredDelaySeconds, occ.segment.getLineID(), occ.prevPoint.getFacility().getName(), tracks));

                    occ.enter = occ.prevPoint.getEstimatedDeparture();
                    occ.leave = occ.point.getEstimatedArrival();

                    int attempts = 0;
                    while (attempts < 5) {
                        overlapping = 0;
                        for (Occupation a : accepted) {
                            if (!(a.leave.isBefore(occ.enter) || occ.leave.isBefore(a.enter))) overlapping++;
                        }
                        if (overlapping < tracks) {
                            accepted.add(occ);
                            break;
                        }
                        java.time.LocalDateTime nextEarliest = null;
                        for (Occupation a : accepted) {
                            if (!(a.leave.isBefore(occ.enter) || occ.leave.isBefore(a.enter))) {
                                if (nextEarliest == null || a.leave.isBefore(nextEarliest)) nextEarliest = a.leave;
                            }
                        }
                        if (nextEarliest == null) {
                            accepted.add(occ);
                            break;
                        }
                        long extraDelay = java.time.Duration.between(occ.prevPoint.getEstimatedDeparture(), nextEarliest).getSeconds();
                        if (extraDelay <= 0) break;
                        applyDelay(occ.schedule, occ.prevPoint, extraDelay);
                        lastConflicts.add(String.format("Conflict: Train %s additional delay by %ds on Line %d at %s (tracks=%d).",
                                occ.schedule.getTrain().getTrainId(), extraDelay, occ.segment.getLineID(), occ.prevPoint.getFacility().getName(), tracks));
                        occ.enter = occ.prevPoint.getEstimatedDeparture();
                        occ.leave = occ.point.getEstimatedArrival();
                        attempts++;
                    }
                } else {
                    accepted.add(occ);
                }
            }
        }
    }

    private SchedulePoint getPreviousPoint(TrainSchedule schedule, SchedulePoint current) {
        int idx = schedule.getSchedulePoints().indexOf(current);
        if (idx > 0) return schedule.getSchedulePoints().get(idx - 1);
        return null;
    }

    private boolean isSamePhysicalSegment(Segment s1, Segment s2) {
        return s1.getLineID() == s2.getLineID() && s1.getOrder() == s2.getOrder();
    }

    private boolean overlaps(TrainSchedule s1, SchedulePoint p1, TrainSchedule s2, SchedulePoint p2) {
        SchedulePoint p1_prev = getPreviousPoint(s1, p1);
        SchedulePoint p2_prev = getPreviousPoint(s2, p2);
        if (p1_prev == null || p2_prev == null) return false;
        LocalDateTime s1_enter = p1_prev.getEstimatedDeparture();
        LocalDateTime s1_leave = p1.getEstimatedArrival();
        LocalDateTime s2_enter = p2_prev.getEstimatedDeparture();
        LocalDateTime s2_leave = p2.getEstimatedArrival();
        return !(s1_leave.isBefore(s2_enter) || s2_leave.isBefore(s1_enter));
    }

    private void applyDelay(TrainSchedule schedule, SchedulePoint startPoint, long delaySeconds) {
        boolean found = false;
        for (SchedulePoint p : schedule.getSchedulePoints()) {
            if (!found && p == startPoint) {
                found = true;
                p.markRequiresCrossing();
                p.addDelaySeconds(delaySeconds);
                continue;
            }
            if (found) {
                p.addDelaySeconds(delaySeconds);
            }
        }
    }
 }
