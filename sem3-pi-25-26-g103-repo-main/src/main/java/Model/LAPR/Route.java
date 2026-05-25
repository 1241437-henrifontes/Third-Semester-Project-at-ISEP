package Model.LAPR;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a manual or predefined route composed by an ordered list of segments.
 */
public class Route {
    private int routeId;
    private String routeName;
    private List<Segment> segments;
    private List<Freight> freights;

    public Route(int routeId, String routeName) {
        this.routeId = routeId;
        this.routeName = routeName;
        this.segments = new ArrayList<>();
        this.freights = new ArrayList<>();
    }

    public void addFreight(Freight f) {
        this.freights.add(f);
    }

    public List<Freight> getFreights() {
        return new ArrayList<>(freights);
    }

    public void addSegment(Segment s) {
        this.segments.add(s);
    }

    public List<Segment> getSegments() {
        return segments;
    }

    public int getRouteId() {
        return routeId;
    }

    public String getRouteName() {
        return routeName;
    }

    public List<String> getAllOrigins() {
        List<String> origins = new ArrayList<>();
        for (Freight f : freights) {
            origins.add(f.getOrigin().getStationId());
        }
        return origins;
    }

    public List<String> getAllDestinations() {
        List<String> destinations = new ArrayList<>();
        for (Freight f : freights) {
            destinations.add(f.getDestination().getStationId());
        }
        return destinations;
    }

    @Override
    public String toString() {
        return "Route{" +
                "routeId=" + routeId +
                ", routeName='" + routeName + '\'' +
                ", segments=" + segments.size() +
                '}';
    }
}
