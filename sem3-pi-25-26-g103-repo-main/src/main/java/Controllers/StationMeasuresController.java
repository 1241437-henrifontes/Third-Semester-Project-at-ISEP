package Controllers;

import Model.Graph.Node;
import Model.StationMeasures;
import Services.DTO.StationMeasuresResultDTO;
import Repositories.GraphRepository;

/**
 * Controller for computing network centrality and connectivity measures for stations.
 * <p>
 * Exposes methods to retrieve stations from the loaded graph and to calculate
 * betweenness, harmonic closeness, degree, strength, and a composite hub score.
 */
public class StationMeasuresController {

    private final GraphRepository graphRepository;

    /**
     * Creates a controller using the singleton GraphRepository instance.
     */
    public StationMeasuresController() {
        this.graphRepository = GraphRepository.getInstance();
    }

    /**
     * Calculates network measures for a given station and returns them as a DTO.
     *
     * @param station the station node to analyze (must not be null)
     * @return StationMeasuresResultDTO with all computed metrics
     * @throws IllegalArgumentException if station is null
     */
    public StationMeasuresResultDTO calculateStationMeasures(Node station) {
        if (station == null) {
            throw new IllegalArgumentException("Station cannot be null");
        }

        double betweenness = StationMeasures.betweenness(station);
        double harmonicCloseness = StationMeasures.harmonicCloseness(station);
        double degree = StationMeasures.degree(station);
        double strength = StationMeasures.strength(station);
        double hubScore = StationMeasures.hubScore(betweenness, harmonicCloseness, strength);

        return new StationMeasuresResultDTO(station, betweenness, harmonicCloseness, degree, strength, hubScore);
    }

    /**
     * Finds a station by its name (case-insensitive) in the current graph.
     *
     * @param stationName the station name
     * @return the Node when found; otherwise null
     * @throws IllegalStateException if the station graph is not initialized
     */
    public Node getStationByName(String stationName) {
        if (graphRepository.getStationGraph() == null) {
            throw new IllegalStateException("Station graph is not initialized");
        }

        for (Node node : graphRepository.getStationGraph().vertices()) {
            if (node.getName().equalsIgnoreCase(stationName)) {
                return node;
            }
        }
        return null;
    }

    /**
     * Finds a station by its identifier (case-insensitive) in the current graph.
     *
     * @param stationId the station identifier
     * @return the Node when found; otherwise null
     * @throws IllegalStateException if the station graph is not initialized
     */
    public Node getStationById(String stationId) {
        if (graphRepository.getStationGraph() == null) {
            throw new IllegalStateException("Station graph is not initialized");
        }

        for (Node node : graphRepository.getStationGraph().vertices()) {
            if (node.getNode_id().equalsIgnoreCase(stationId)) {
                return node;
            }
        }
        return null;
    }

    /**
     * Returns all stations available in the graph.
     *
     * @return list of station nodes
     * @throws IllegalStateException if the station graph is not initialized
     */
    public java.util.List<Node> getAllStations() {
        if (graphRepository.getStationGraph() == null) {
            throw new IllegalStateException("Station graph is not initialized");
        }

        java.util.List<Node> stations = new java.util.ArrayList<>();
        for (Node node : graphRepository.getStationGraph().vertices()) {
            stations.add(node);
        }
        return stations;
    }
}
