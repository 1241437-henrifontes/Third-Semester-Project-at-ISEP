package Controllers;

import Repositories.GraphRepository;
import Model.Graph.Graph;
import Model.Graph.Node;
import Services.UpgradePlanner;


import java.util.List;

/**
 * Controller for USEI11 (Upgrade Planner).
 * <p>
 * Delegates to the UpgradePlanner service to either compute a valid upgrade order
 * for stations (when the graph is acyclic) or return a cycle (when one exists).
 */
public class UpgradePlannerController {

    private final UpgradePlanner planner = new UpgradePlanner();

    /**
     * Executes the USEI11 upgrade planning over the ordered station graph.
     *
     * @return a list of stations representing either a valid upgrade order when the
     *         graph has no cycles, or a non-empty list representing a detected cycle;
     *         returns null if no graph is available
     */
    public List<Node> executeUSEI11() {

        GraphRepository graphRepo = GraphRepository.getInstance();
        Graph<Node, Double> graph = graphRepo.getStationGraphOrdered();

        if (graph == null || graph.numVertices() == 0) {
            return null;
        }

        return planner.Upgrades(graph);
    }

    /**
     * Gets the number of stations currently loaded in the main station graph.
     *
     * @return total station count, or 0 when no graph is available
     */
    public int getNumberOfStations() {
        Graph<Node, Double> graph = GraphRepository.getInstance().getStationGraph();
        return graph == null ? 0 : graph.numVertices();
    }
}
