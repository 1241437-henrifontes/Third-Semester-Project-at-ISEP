package USEI11;

import Model.Graph.Graph;
import Model.Graph.MapGraph;
import Model.Graph.Node;
import Model.Pair;
import Services.UpgradePlanner;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the UpgradePlanner, covering both acyclic and cyclic graphs.
 */
class UpgradePlannerTest {

    @Test
    void upgradesReturnsOrderedListWhenGraphHasNoCycle() {
        UpgradePlanner planner = new UpgradePlanner();
        Graph<Node, Double> graph = new MapGraph<Node, Double>(true);

        Node a = new Node("A", "A", new Pair<>(0.0, 0.0), new Pair<>(0.0, 0.0));
        Node b = new Node("B", "B", new Pair<>(0.0, 0.0), new Pair<>(0.0, 0.0));

        graph.addVertex(a);
        graph.addVertex(b);
        graph.addEdge(a, b, 1.0,2,8);

        List<Node> result = planner.Upgrades(graph);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.indexOf(a) < result.indexOf(b));
    }

    @Test
    void upgradesReturnsCycleWhenGraphHasCycle() {
        UpgradePlanner planner = new UpgradePlanner();
        Graph<Node, Double> graph = new MapGraph<Node, Double>(true);

        Node a = new Node("A", "A", new Pair<>(0.0, 0.0), new Pair<>(0.0, 0.0));
        Node b = new Node("B", "B", new Pair<>(0.0, 0.0), new Pair<>(0.0, 0.0));

        graph.addVertex(a);
        graph.addVertex(b);
        graph.addEdge(a, b, 1.0,2,8);
        graph.addEdge(b, a, 1.0,2,8);

        List<Node> result = planner.Upgrades(graph);

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }
}
