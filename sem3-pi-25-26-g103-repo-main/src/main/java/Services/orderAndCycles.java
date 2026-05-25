package Services;

import Model.Graph.Graph;
import Model.Graph.Node;

import java.util.*;

/**
 * Utilities for topological ordering and cycle detection over directed graphs.
 * <p>
 * Provides a Kahn-style topological sort and a DFS-based routine to find the
 * first cycle encountered in the graph.
 */
public class orderAndCycles {

    public static List<Node> topologicalOrder(Graph<Node, Double> graph) {

        Map<Node, Integer> inDegree = new HashMap<>();

        for (Node v : graph.vertices()) {
            inDegree.put(v, graph.inDegree(v));
        }

        Queue<Node> queue = new LinkedList<>();
        for (Node v : inDegree.keySet()) {
            if (inDegree.get(v) == 0) {
                queue.add(v);
            }
        }

        List<Node> order = new ArrayList<>();

        while (!queue.isEmpty()) {
            Node v = queue.poll();
            order.add(v);

            for (Node adj : graph.adjVertices(v)) {
                inDegree.put(adj, inDegree.get(adj) - 1);
                if (inDegree.get(adj) == 0) {
                    queue.add(adj);
                }
            }
        }

        if (order.size() != graph.numVertices()) {
            return null;
        }

        return order;
    }

    public static List<Node> findFirstCycle(Graph<Node, Double> graph) {

        Map<Node, Integer> state = new HashMap<>();
        Deque<Node> stack = new ArrayDeque<>();

        for (Node v : graph.vertices()) {
            state.put(v, 0);
        }

        for (Node v : graph.vertices()) {
            if (state.get(v) == 0) {
                List<Node> cycle = dfs(graph, v, state, stack);
                if (cycle != null) {
                    return cycle;
                }
            }
        }

        return Collections.emptyList();
    }

    private static List<Node> dfs(Graph<Node, Double> graph,
                                  Node current,
                                  Map<Node, Integer> state,
                                  Deque<Node> stack) {

        state.put(current, 1);
        stack.push(current);

        for (Node adj : graph.adjVertices(current)) {

            if (state.get(adj) == 0) {
                List<Node> cycle = dfs(graph, adj, state, stack);
                if (cycle != null) return cycle;

            } else if (state.get(adj) == 1) {
                List<Node> cycle = new ArrayList<>();
                for (Node n : stack) {
                    cycle.add(n);
                    if (n.equals(adj)) break;
                }
                Collections.reverse(cycle);
                return cycle;
            }
        }

        stack.pop();
        state.put(current, 2);
        return null;
    }
}
