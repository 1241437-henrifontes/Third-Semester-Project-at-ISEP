package Repositories;

import Model.Graph.Node;
import Model.Graph.Edge;
import Model.ReadFromCSV;
import Model.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * Repository responsible for loading and providing access to station nodes and edges
 * from CSV datasets. Offers helpers to process records and retrieve built lists.
 */
public class StationRepository {
    private static StationRepository instance = new StationRepository();
    private List<Node> stations;
    private List<Edge<Node, Double>> edges;

    private StationRepository() {
        this.stations = new ArrayList<>();
        this.edges = new ArrayList<>();
    }

    public static StationRepository getInstance() {
        return instance;
    }

    public List<Node> getStations() {
        return stations;
    }

    public List<Edge<Node, Double>> getEdges() {
        return edges;
    }

    void clear() {
        this.stations.clear();
        this.edges.clear();
    }

    public void loadStations() {
        stations = new ArrayList<>();
        ReadFromCSV.readFile("stations", this::processStationRecord);
    }

    Node processStationRecord(String[] fields) {
        if (fields.length != 6) {
            throw new IllegalArgumentException("Invalid station record: expected 6 columns but got " + fields.length);
        }

        String id = fields[0];
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("Missing id in stations.csv record.");
        }

        String name = fields[1];
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Missing name for station " + id + ".");
        }

        double geoLatitude = Double.parseDouble(fields[2]);
        if (geoLatitude < -90 || geoLatitude > 90) {
            throw new IllegalArgumentException("Invalid latitude for station " + name + ": must be between -90 and 90.");
        }

        double geoLongitude = Double.parseDouble(fields[3]);
        if (geoLongitude < -180 || geoLongitude > 180) {
            throw new IllegalArgumentException("Invalid longitude for station " + name + ": must be between -180 and 180.");
        }

        double x = Double.parseDouble(fields[4]);
        if (x < 0) {
            throw new IllegalArgumentException("Invalid x coordinate for station " + name + ": must be >= 0.");
        }

        double y = Double.parseDouble(fields[5]);
        if (y < 0) {
            throw new IllegalArgumentException("Invalid y coordinate for station " + name + ": must be >= 0.");
        }

        Pair<Double, Double> geoCoordinates = new Pair<>(geoLatitude, geoLongitude);
        Pair<Double, Double> cartesianCoordinates = new Pair<>(x, y);

        Node node = new Node(id, name, geoCoordinates, cartesianCoordinates);
        stations.add(node);
        return node;
    }

    public void loadEdges() {
        edges = new ArrayList<>();
        ReadFromCSV.readFile("lines", this::processLineRecord);
    }

    Edge<Node, Double> processLineRecord(String[] fields) {
        if (fields.length != 5) {
            throw new IllegalArgumentException("Invalid line record: expected 5 columns but got " + fields.length);
        }

        String vOrigId = fields[0];
        if (vOrigId == null || vOrigId.isEmpty()) {
            throw new IllegalArgumentException("Missing origin station id in lines.csv record.");
        }

        Node vOrig = foundNodeById(vOrigId);
        if (vOrig == null) {
            throw new IllegalArgumentException("Inexistent station in lines.csv record.");
        }

        String vDestId = fields[1];
        if (vDestId == null || vDestId.isEmpty()) {
            throw new IllegalArgumentException("Missing destination station id in lines.csv record.");
        }

        Node vDest = foundNodeById(vDestId);
        if (vDest == null) {
            throw new IllegalArgumentException("Inexistent station in lines.csv record.");
        }

        double dist = Double.parseDouble(fields[2]);
        if (dist <= 0) {
            throw new IllegalArgumentException("Invalid distance for line between stations " + vOrigId + " and " + vDestId + ": must be > 0.");
        }

        int capacity = Integer.parseInt(fields[3]);
        if (capacity <= 0) {
            throw new IllegalArgumentException("Invalid capacity for line between stations " + vOrigId + " and " + vDestId + ": must be > 0.");
        }

        double cost = Double.parseDouble(fields[4]);

        Edge<Node, Double> edge = new Edge<>(vOrig, vDest, cost, dist, capacity);
        edges.add(edge);
        return edge;
    }

    private Node foundNodeById(String id) {
        return stations.stream().filter(n -> n.getNode_id().equals(id)).findFirst().orElse(null);
    }
}
