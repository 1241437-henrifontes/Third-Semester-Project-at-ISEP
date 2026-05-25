package Controllers.LAPR;

import Repositories.LAPR.FacilityRepository;
import Repositories.LAPR.LineRepository;
import Repositories.LAPR.LocomotiveRepository;
import Repositories.LAPR.SegmentRepository;
import Model.LAPR.Facility;
import Model.LAPR.Line;
import Model.LAPR.Locomotive;
import Model.LAPR.Segment;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Provides read-only accessors and filters to support travel time calculations.
 */
public class TravelTimeController {

    /** Returns the singleton FacilityRepository. */
    public FacilityRepository getFacilityRepository() {
        return FacilityRepository.getInstance();
    }

    /** Returns the singleton LineRepository. */
    public LineRepository getLineRepository() {
        return LineRepository.getInstance();
    }

    /** Returns the singleton SegmentRepository. */
    public SegmentRepository getSegmentRepository() {
        return SegmentRepository.getInstance();
    }

    /** Returns the singleton LocomotiveRepository. */
    public LocomotiveRepository getLocomotiveRepository() {
        return LocomotiveRepository.getInstance();
    }

    /** Retrieves all facilities. */
    public List<Facility> getFacilities() {
        return getFacilityRepository().getFacilities();
    }

    /**
     * Lists all lines that start at the given facility ID.
     *
     * @param facilityId starting facility identifier
     * @return list of lines originating at the facility
     */
    public List<Line> getLinesStartingAt(String facilityId) {
        return getLineRepository().getLines().stream()
                .filter(l -> String.valueOf(l.getStartId()).equals(facilityId))
                .collect(Collectors.toList());
    }

    /**
     * Retrieves the segments of a line, ordered by their declared order.
     *
     * @param lineId the line identifier
     * @return ordered list of segments for the line
     */
    public List<Segment> getSegmentsForLineOrdered(int lineId) {
        return getSegmentRepository().getSegments().stream()
                .filter(s -> s.getLineID() == lineId)
                .sorted(Comparator.comparingInt(Segment::getOrder))
                .collect(Collectors.toList());
    }

    /**
     * Returns locomotives compatible with the gauge of the provided line.
     *
     * @param line the target line
     * @return list of compatible locomotives
     */
    public List<Locomotive> getLocomotivesCompatibleWithLine(Line line) {
        return getLocomotiveRepository().getLocomotives().stream()
                .filter(l -> l.getGaugeId() == line.getGauge())
                .collect(Collectors.toList());
    }

    /**
     * Finds a facility by its identifier.
     *
     * @param id facility ID
     * @return optional facility
     */
    public Optional<Facility> findFacilityById(int id) {
        return getFacilities().stream().filter(f -> f.getStationId().equals(id)).findFirst();
    }
}
