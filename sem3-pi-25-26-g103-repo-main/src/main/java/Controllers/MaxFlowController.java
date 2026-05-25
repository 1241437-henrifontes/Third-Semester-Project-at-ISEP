package Controllers;

import Model.Graph.Node;
import Model.MaxFlowResult;
import Services.DTO.MaxFlowResultDTO;
import Services.MaxFlowAlgorithm;
import Repositories.GraphRepository;

/**
 * Controller for the Maximum Flow (USEI14) use case.
 * Handles the calculation of maximum throughput between two railway stations
 * using the Edmonds-Karp algorithm.
 */
public class MaxFlowController {

    private final GraphRepository graphRepository;

    public MaxFlowController() {
        this.graphRepository = GraphRepository.getInstance();
    }

    /**
     * Calculates the maximum flow between a source and sink station.
     *
     * @param sourceStation the origin station
     * @param sinkStation the destination station
     * @return MaxFlowResultDTO containing the result and temporal complexity
     * @throws IllegalArgumentException if stations are null or invalid
     */
    public MaxFlowResultDTO calculateMaxFlow(Node sourceStation, Node sinkStation) {
        if (sourceStation == null || sinkStation == null) {
            throw new IllegalArgumentException("Source and sink stations cannot be null");
        }

        if (graphRepository.getStationGraph() == null) {
            throw new IllegalStateException("Station graph is not initialized");
        }

        // Calculate max flow using the Edmonds-Karp algorithm
        MaxFlowResult result = MaxFlowAlgorithm.calculateMaxFlow(
                graphRepository.getStationGraph(),
                sourceStation,
                sinkStation
        );

        // Get temporal complexity
        String complexity = MaxFlowAlgorithm.getTemporalComplexity();

        // Create and return DTO
        return new MaxFlowResultDTO(
                sourceStation,
                sinkStation,
                result.getMaxFlowValue(),
                complexity
        );
    }

    /**
     * Retrieves a station by its name.
     *
     * @param stationName the name of the station
     * @return the Node representing the station, or null if not found
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
     * Retrieves a station by its ID.
     *
     * @param stationId the ID of the station
     * @return the Node representing the station, or null if not found
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
     * Retrieves all stations in the graph.
     *
     * @return a list of all station nodes
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