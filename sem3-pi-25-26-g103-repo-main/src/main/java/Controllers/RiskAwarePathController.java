package Controllers;

import Model.Graph.Graph;
import Model.Graph.Node;
import Model.RiskAwarePathFinder;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Controller for computing risk-aware routes between railway stations.
 * <p>
 * Wraps the RiskAwarePathFinder service and provides helper methods to list
 * available stations and compute robust routes based on user input identifiers
 * or names.
 */
public class RiskAwarePathController {

    private final Graph<Node, Double> graph;
    private final RiskAwarePathFinder pathFinder;

    /**
     * Creates a controller bound to a specific station graph.
     *
     * @param mainGraph directed weighted graph of stations
     */
    public RiskAwarePathController(Graph<Node, Double> mainGraph) {
        this.graph = mainGraph;
        this.pathFinder = new RiskAwarePathFinder();
    }

    /**
     * Returns all stations from the current graph, sorted by station id.
     *
     * @return list of station nodes; empty list when the graph is null
     */
    public List<Node> getAllStations() {
        if (graph == null) return new ArrayList<>();

        ArrayList<Node> stations = new ArrayList<>(graph.vertices());

        stations.sort(Comparator.comparing(Node::getNode_id));
        return stations;
    }

    /**
     * Computes a risk-aware route between two stations given their id or name.
     * If either station cannot be found, a localized error message is returned.
     *
     * @param startInput station id or name for origin
     * @param endInput station id or name for destination
     * @return a textual description of the robust route, or an error message
     */
    public String calculateRiskAwareRoute(String startInput, String endInput) {
        Node startNode = findNode(startInput);
        Node endNode = findNode(endInput);

        if (startNode == null) {
            return "Erro: A estação de origem '" + startInput + "' não foi encontrada.";
        }
        if (endNode == null) {
            return "Erro: A estação de destino '" + endInput + "' não foi encontrada.";
        }

        return pathFinder.findRobustRoute(graph, startNode, endNode);
    }

    private Node findNode(String input) {
        if (input == null || graph == null) return null;

        String cleanInput = input.trim();

        for (Node n : graph.vertices()) {

            if (n.getNode_id().equalsIgnoreCase(cleanInput)) {
                return n;
            }

            if (n.getName() != null && n.getName().trim().equalsIgnoreCase(cleanInput)) {
                return n;
            }
        }
        return null;
    }

}