package Services.LAPR;

import Model.LAPR.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Service responsible for detecting potential train crossings on single-track segments
 * and determining where/when crossing operations are needed.
 */
public class CrossingDetectionService {

    /**
     * Analyzes multiple train schedules and detects where crossings are needed.
     * Only detects conflicts on SINGLE-TRACK segments where trains overlap in time.
     *
     * @param schedules list of all train schedules to analyze
     * @return list of detected crossings
     */
    public List<Crossing> detectCrossings(List<TrainSchedule> schedules) {
        List<Crossing> crossings = new ArrayList<>();

        // Compare each pair of schedules
        for (int i = 0; i < schedules.size(); i++) {
            for (int j = i + 1; j < schedules.size(); j++) {
                TrainSchedule schedule1 = schedules.get(i);
                TrainSchedule schedule2 = schedules.get(j);

                // Check if they conflict on single-track segments
                List<Crossing> pairCrossings = detectCrossingsBetweenTwo(schedule1, schedule2);
                crossings.addAll(pairCrossings);
            }
        }

        return crossings;
    }

    /**
     * Detects if two trains will conflict on single-track segments.
     * A conflict occurs when both trains are on the SAME single-track segment at overlapping times.
     */
    // Em services.LAPR.CrossingDetectionService.java
    private List<Crossing> detectCrossingsBetweenTwo(TrainSchedule schedule1, TrainSchedule schedule2) {
        List<Crossing> crossings = new ArrayList<>();
        List<SchedulePoint> points1 = schedule1.getSchedulePoints();
        List<SchedulePoint> points2 = schedule2.getSchedulePoints();

        // Verificar apenas segmentos de via única
        for (int i = 1; i < points1.size(); i++) {
            SchedulePoint currentPoint1 = points1.get(i);
            SchedulePoint previousPoint1 = points1.get(i-1);
            Segment segment1 = currentPoint1.getSegmentBefore();

            if (segment1 == null || !segment1.isSingleTrack()) continue;

            // Obter janela de tempo para o trem 1 neste segmento
            LocalDateTime train1Enter = previousPoint1.getEstimatedDeparture();
            LocalDateTime train1Exit = currentPoint1.getEstimatedArrival();

            for (int j = 1; j < points2.size(); j++) {
                SchedulePoint currentPoint2 = points2.get(j);
                SchedulePoint previousPoint2 = points2.get(j-1);
                Segment segment2 = currentPoint2.getSegmentBefore();

                if (segment2 == null || segment2.getOrder() != segment1.getOrder()) continue;

                // Obter janela de tempo para o trem 2 neste segmento
                LocalDateTime train2Enter = previousPoint2.getEstimatedDeparture();
                LocalDateTime train2Exit = currentPoint2.getEstimatedArrival();

                // Verificar sobreposição de tempos
                if (timeWindowsOverlap(train1Enter, train1Exit, train2Enter, train2Exit)) {
                    // Verificar se há uma facility com sidings próxima para o cruzamento
                    Facility crossingLocation = findBestCrossingLocation(
                            previousPoint1.getFacility(),
                            previousPoint2.getFacility(),
                            currentPoint1.getFacility()
                    );

                    if (crossingLocation != null) {
                        LocalDateTime crossingTime = train1Enter.isAfter(train2Enter) ?
                                train1Enter : train2Enter;

                        crossings.add(new Crossing(crossingLocation, schedule1, schedule2, crossingTime));
                    }
                }
            }
        }
        return crossings;
    }

    private Facility findBestCrossingLocation(Facility start1, Facility start2, Facility end) {
        // Verificar se as facilities de início têm sidings
        if (start1 != null && start1.hasSidings()) return start1;
        if (start2 != null && start2.hasSidings()) return start2;
        if (end != null && end.hasSidings()) return end;

        // Se nenhuma tem sidings, usar a primeira facility
        return start1 != null ? start1 : start2;
    }

    /**
     * Checks if two time windows overlap.
     */
    private boolean timeWindowsOverlap(LocalDateTime start1, LocalDateTime end1,
                                       LocalDateTime start2, LocalDateTime end2) {
        return !end1.isBefore(start2) && !end2.isBefore(start1);
    }

    /**
     * Applies crossing operations to schedules by adding delays where needed.
     * The later train waits at the station before the conflict segment.
     *
     * @param schedules all train schedules
     * @param crossings detected crossings
     */
    // Em services.LAPR.CrossingDetectionService.java
    public void applyCrossingDelays(List<TrainSchedule> schedules, List<Crossing> crossings) {
        for (Crossing crossing : crossings) {
            TrainSchedule train1 = crossing.getTrain1();
            TrainSchedule train2 = crossing.getTrain2();
            Facility location = crossing.getLocation();

            if (location == null) continue;

            // Verificar se a facility tem sidings
            if (location.hasSidings() && location.getNumberOfSidings() > 0) {
                // Se há sidings, nenhum trem precisa esperar - podem cruzar simultaneamente
                System.out.printf("Crossing at %s can be handled using sidings - no delays required.%n",
                        location.getName());
                continue;
            }

            // Se não há sidings, o trem mais lento deve esperar
            double train1Speed = calculateAverageSpeed(train1);
            double train2Speed = calculateAverageSpeed(train2);

            TrainSchedule slowerTrain = train1Speed < train2Speed ? train1 : train2;

            // Encontrar o ponto de programação na facility para o trem mais lento
            for (SchedulePoint sp : slowerTrain.getSchedulePoints()) {
                if (sp.getFacility() != null && sp.getFacility().getStationId().equals(location.getStationId())) {
                    sp.markRequiresCrossing();
                    // Atraso realista baseado no comprimento do trem e velocidade
                    long delaySeconds = calculateCrossingDelay(slowerTrain.getTrain());
                    sp.setDepartureDelaySeconds(delaySeconds);
                    System.out.printf("Added %d second delay for crossing at %s%n",
                            delaySeconds, location.getName());
                    break;
                }
            }
        }
    }

    /**
     * Calculates the average speed of a train schedule based on total distance and total travel time.
     *
     * @param schedule The train schedule to calculate speed for
     * @return Average speed in km/h
     */
    private double calculateAverageSpeed(TrainSchedule schedule) {
        double totalDistance = 0.0;
        long totalSeconds = 0;

        // Calculate total distance traveled (sum of all segment lengths)
        for (Segment segment : schedule.getRoute().getSegments()) {
            totalDistance += segment.getLength() / 1000.0; // Convert meters to kilometers
        }

        // Calculate total travel time
        if (!schedule.getSchedulePoints().isEmpty()) {
            SchedulePoint firstPoint = schedule.getSchedulePoints().get(0);
            SchedulePoint lastPoint = schedule.getSchedulePoints().get(schedule.getSchedulePoints().size() - 1);

            totalSeconds = java.time.Duration.between(
                    firstPoint.getEstimatedArrival(),
                    lastPoint.getEstimatedDeparture()
            ).getSeconds();
        }

        // Avoid division by zero
        if (totalSeconds == 0) {
            return 0.0;
        }

        // Convert to km/h: (km / seconds) * 3600 seconds/hour
        return (totalDistance / totalSeconds) * 3600;
    }

    private long calculateCrossingDelay(Train train) {
        // Cálculo realista baseado no comprimento do trem
        double trainLength = calculateTrainLength(train);
        double safeSpeed = 10.0 / 3.6; // 10 km/h em m/s para manobra segura

        // Tempo para mover o trem para o lado + tempo de segurança
        return (long) ((trainLength / safeSpeed) + 60); // +1 minuto de segurança
    }

    private double calculateTrainLength(Train train) {
        // Cálculo aproximado: locomotiva (20m) + vagões (15m cada)
        return 20 + (train.getNumberOfWagons() * 15);
    }
}
