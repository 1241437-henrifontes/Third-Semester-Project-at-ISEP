package Services;

import Model.Graph.Algorithms;
import Model.Graph.Edge;
import Model.Graph.Graph;
import Model.Graph.Node;
import Model.MaxFlowResult;

import java.util.*;

/**
 * Service class that implements the Edmonds-Karp algorithm (Ford-Fulkerson with BFS)
 * to calculate the maximum flow between two stations in a railway network.
 * This implementation uses methods from the Algorithms class and follows the PowerPoint approach:
 * Maximum Flow Graph = Initial Graph - Residual Graph
 */
public class MaxFlowAlgorithm {

    /**
     * Calculates the maximum flow between a source and sink station.
     * Implements the Edmonds-Karp algorithm (Ford-Fulkerson with BFS).
     * Temporal Complexity: O(V * E^2) where V is the number of vertices and E is the number of edges.
     * - Each BFS takes O(E) time
     * - There can be at most O(V*E) augmenting paths
     *
     * @param graph the railway network graph with capacity constraints
     * @param source the source station
     * @param sink the sink station
     * @return MaxFlowResult containing the maximum flow value, residual graph, max flow graph, and augmenting paths
     */
    public static MaxFlowResult calculateMaxFlow(Graph<Node, Double> graph, Node source, Node sink) {
        if (graph == null || source == null || sink == null) {
            throw new IllegalArgumentException("Graph, source, and sink cannot be null");
        }

        if (!graph.validVertex(source) || !graph.validVertex(sink)) {
            throw new IllegalArgumentException("Source and sink must be valid vertices in the graph");
        }

        if (source.equals(sink)) {
            throw new IllegalArgumentException("Source and sink must be different vertices");
        }

        return edmondsKarp(graph, source, sink);
    }

    /**
     * Implements the Edmonds-Karp algorithm using methods from Algorithms class.
     * Uses Algorithms.bfsMaxFlow() to find augmenting paths.
     * Uses Algorithms.cloneGraph() to create the residual graph.
     *
     * Algorithm:
     * 1. Clone original graph (for calculating Maximum Flow Graph at the end)
     * 2. Create residual graph using Algorithms.cloneGraph()
     * 3. While Algorithms.bfsMaxFlow() finds an augmenting path:
     *    - Calculate minimum capacity (bottleneck) on the path
     *    - Update residual capacities (forward and reverse edges)
     *    - Add to total flow
     * 4. Calculate Maximum Flow Graph = Initial - Residual (PowerPoint approach)
     *
     * @param graph the original graph
     * @param source the source node
     * @param sink the sink node
     * @return MaxFlowResult with max flow value, residual graph, max flow graph, and paths
     */
    private static MaxFlowResult edmondsKarp(Graph<Node, Double> graph, Node source, Node sink) {
        // 1. Clone original graph to preserve initial capacities (for Maximum Flow Graph calculation)
        Graph<Node, Double> originalGraph = Algorithms.cloneGraph(graph);

        // 2. Create residual graph (will be modified during execution)
        Graph<Node, Double> residualGraph = Algorithms.cloneGraph(graph);

        double maxFlow = 0;
        List<List<Node>> augmentingPaths = new ArrayList<>();
        Map<Node, Node> parent = new HashMap<>();

        // 3. Find augmenting paths using BFS from Algorithms class
        while (Algorithms.bfsMaxFlow(residualGraph, source, sink, parent)) {
            // Calculate minimum capacity along the path (bottleneck)
            double pathFlow = Double.POSITIVE_INFINITY;
            List<Node> path = new ArrayList<>();

            // Reconstruct path and find minimum capacity
            Node current = sink;
            while (!current.equals(source)) {
                path.add(0, current);
                Node prev = parent.get(current);
                Edge<Node, Double> edge = residualGraph.edge(prev, current);

                if (edge != null) {
                    pathFlow = Math.min(pathFlow, edge.getCapacity());
                }
                current = prev;
            }
            path.add(0, source);
            augmentingPaths.add(new ArrayList<>(path));

            // Update residual graph capacities
            current = sink;
            while (!current.equals(source)) {
                Node prev = parent.get(current);

                // Decrease forward edge capacity
                Edge<Node, Double> forwardEdge = residualGraph.edge(prev, current);
                if (forwardEdge != null) {
                    int newCapacity = (int) (forwardEdge.getCapacity() - pathFlow);
                    forwardEdge.setCapacity(newCapacity);
                }

                // Increase reverse edge capacity (create if doesn't exist)
                Edge<Node, Double> reverseEdge = residualGraph.edge(current, prev);
                if (reverseEdge != null) {
                    int newCapacity = (int) (reverseEdge.getCapacity() + pathFlow);
                    reverseEdge.setCapacity(newCapacity);
                } else {
                    residualGraph.addEdge(current, prev, 0, 0, (int) pathFlow);
                }

                current = prev;
            }

            maxFlow += pathFlow;
            parent.clear();
        }

        // 4. Calculate Maximum Flow Graph = Initial Graph - Residual Graph (PowerPoint approach)
        Graph<Node, Double> maxFlowGraph = calculateMaxFlowGraph(originalGraph, residualGraph);

        return new MaxFlowResult(maxFlow, residualGraph, maxFlowGraph, augmentingPaths);
    }

    /**
     * Calculates the Maximum Flow Graph: Initial Graph - Residual Graph.
     * Shows how much flow actually passed through each edge.
     * According to the course PowerPoint: Maximum Flow Graph = Initial - Residual
     *
     * @param originalGraph the original graph with initial capacities
     * @param residualGraph the residual graph after all iterations
     * @return the maximum flow graph showing flow that passed through each edge
     */
    private static Graph<Node, Double> calculateMaxFlowGraph(Graph<Node, Double> originalGraph,
                                                              Graph<Node, Double> residualGraph) {
        // Clone original graph structure
        Graph<Node, Double> maxFlowGraph = Algorithms.cloneGraph(originalGraph);

        // For each edge in the original graph
        for (Edge<Node, Double> originalEdge : originalGraph.edges()) {
            Node orig = originalEdge.getVOrig();
            Node dest = originalEdge.getVDest();

            int originalCapacity = originalEdge.getCapacity();

            // Get remaining capacity from residual graph
            Edge<Node, Double> residualEdge = residualGraph.edge(orig, dest);
            int residualCapacity = (residualEdge != null) ? residualEdge.getCapacity() : 0;

            // Flow that passed = Original Capacity - Residual Capacity
            int flowPassed = originalCapacity - residualCapacity;

            // Update the edge in maxFlowGraph with the flow that passed
            Edge<Node, Double> maxFlowEdge = maxFlowGraph.edge(orig, dest);
            if (maxFlowEdge != null) {
                maxFlowEdge.setCapacity(flowPassed);
            }
        }

        return maxFlowGraph;
    }

    /**
     * Returns a description of the temporal complexity of the Edmonds-Karp algorithm.
     *
     * @return String describing the O(V * E^2) complexity
     */
    public static String getTemporalComplexity() {
        return "O(V * E^2) where V = number of vertices, E = number of edges";
    }
}