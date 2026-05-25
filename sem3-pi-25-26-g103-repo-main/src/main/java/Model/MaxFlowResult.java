package Model;

import Model.Graph.Graph;
import Model.Graph.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the result of a maximum flow calculation using the Edmonds-Karp algorithm.
 * Following the approach from the course material:
 * - Initial Graph: Original graph with initial capacities
 * - Residual Graph: Graph with remaining capacities after all iterations
 * - Maximum Flow Graph: Shows how much flow passed through each edge (Initial - Residual)
 */
public class MaxFlowResult {
    private final double maxFlowValue;
    private final Graph<Node, Double> residualGraph;
    private final Graph<Node, Double> maxFlowGraph;
    private final List<List<Node>> augmentingPaths;

    /**
     * Constructs a MaxFlowResult with the given parameters.
     *
     * @param maxFlowValue the maximum flow value calculated
     * @param residualGraph the residual graph (remaining capacities after all iterations)
     * @param maxFlowGraph the maximum flow graph (shows flow that passed through each edge)
     * @param augmentingPaths the list of augmenting paths found
     */
    public MaxFlowResult(double maxFlowValue, Graph<Node, Double> residualGraph,
                         Graph<Node, Double> maxFlowGraph, List<List<Node>> augmentingPaths) {
        this.maxFlowValue = maxFlowValue;
        this.residualGraph = residualGraph;
        this.maxFlowGraph = maxFlowGraph;
        this.augmentingPaths = new ArrayList<>(augmentingPaths);
    }

    /**
     * Gets the maximum flow value.
     *
     * @return the maximum flow value
     */
    public double getMaxFlowValue() {
        return maxFlowValue;
    }

    /**
     * Gets the residual graph (remaining capacities after all iterations).
     *
     * @return the residual graph
     */
    public Graph<Node, Double> getResidualGraph() {
        return residualGraph;
    }

    /**
     * Gets the Maximum Flow Graph.
     * Shows how much flow actually passed through each edge.
     * Calculated as: Initial Graph - Residual Graph
     *
     * @return the maximum flow graph
     */
    public Graph<Node, Double> getMaxFlowGraph() {
        return maxFlowGraph;
    }

    /**
     * Gets the list of augmenting paths found during the algorithm execution.
     *
     * @return an unmodifiable list of augmenting paths
     */
    public List<List<Node>> getAugmentingPaths() {
        return new ArrayList<>(augmentingPaths);
    }

    /**
     * Gets the number of augmenting paths found.
     *
     * @return the number of augmenting paths
     */
    public int getNumberOfPaths() {
        return augmentingPaths.size();
    }

    @Override
    public String toString() {
        return "MaxFlowResult{" +
                "maxFlowValue=" + maxFlowValue +
                ", numberOfPaths=" + augmentingPaths.size() +
                '}';
    }
}
