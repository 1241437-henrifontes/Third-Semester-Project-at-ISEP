package USEI11;

import org.junit.jupiter.api.Test;

import java.util.List;
import Model.Graph.Graph;
import Model.Graph.Node;
import Model.Pair;
import Services.orderAndCycles;
import Model.Graph.MapGraph;
import Model.Graph.CommonGraph;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for topological order and cycle detection utilities.
 */
class OrderAndCyclesTest {

    @Test
    void topologicalSortReturnsValidOrderWhenGraphHasNoCycles() {
        MapGraph<Node, Double> graph = new MapGraph<>(true);

        Node a = new Node("A", "A", new Pair<>(0.0, 0.0), new Pair<>(0.0, 0.0));
        Node b = new Node("B", "B", new Pair<>(0.0, 0.0), new Pair<>(0.0, 0.0));
        Node c = new Node("C", "C", new Pair<>(0.0, 0.0), new Pair<>(0.0, 0.0));

        graph.addVertex(a);
        graph.addVertex(b);
        graph.addVertex(c);

        graph.addEdge(a, b, 1.0,2,8);
        graph.addEdge(b, c, 1.0,2,8);

        List<Node> order = orderAndCycles.topologicalOrder(graph);

        assertNotNull(order);
        assertEquals(3, order.size());
        assertTrue(order.indexOf(a) < order.indexOf(b));
        assertTrue(order.indexOf(b) < order.indexOf(c));
    }

    @Test
    void topologicalSortReturnsNullWhenGraphHasCycle() {
        Graph<Node, Double> graph = new MapGraph<Node, Double>(true);

        Node a = new Node("A", "A", new Pair<>(0.0, 0.0), new Pair<>(0.0, 0.0));
        Node b = new Node("B", "B", new Pair<>(0.0, 0.0), new Pair<>(0.0, 0.0));

        graph.addVertex(a);
        graph.addVertex(b);

        graph.addEdge(a, b, 1.0,2,8);
        graph.addEdge(b, a, 1.0,2,8);

        List<Node> order = orderAndCycles.topologicalOrder(graph);

        assertNull(order);
    }

    @Test
    void findFirstCycleReturnsEmptyListWhenNoCycleExists() {
        Graph<Node, Double> graph = new MapGraph<Node, Double>(true);

        Node a = new Node("A", "A", new Pair<>(0.0, 0.0), new Pair<>(0.0, 0.0));
        Node b = new Node("B", "B", new Pair<>(0.0, 0.0), new Pair<>(0.0, 0.0));

        graph.addVertex(a);
        graph.addVertex(b);
        graph.addEdge(a, b, 1.0,2,8);

        List<Node> cycle = orderAndCycles.findFirstCycle(graph);

        assertNotNull(cycle);
        assertTrue(cycle.isEmpty());
    }

    @Test
    void findFirstCycleReturnsNonEmptyListWhenCycleExists() {
        Graph<Node, Double> graph = new MapGraph<Node, Double>(true);

        Node a = new Node("A", "A", new Pair<>(0.0, 0.0), new Pair<>(0.0, 0.0));
        Node b = new Node("B", "B", new Pair<>(0.0, 0.0), new Pair<>(0.0, 0.0));

        graph.addVertex(a);
        graph.addVertex(b);

        graph.addEdge(a, b, 1.0,2,8);
        graph.addEdge(b, a, 1.0,2,8);

        List<Node> cycle = orderAndCycles.findFirstCycle(graph);

        assertNotNull(cycle);
        assertFalse(cycle.isEmpty());
    }
}
