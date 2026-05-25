package USEI12;

import Model.Graph.Algorithms;
import Model.Graph.MapGraph;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for graph algorithms.
 *
 * <p>This test suite validates BFS, DFS, shortest path algorithms,
 * Prim's MST, and Bellman-Ford behavior.</p>
 */
class AlgorithmsTest {

    /**
     * Tests BFS and DFS traversals on a simple directed graph.
     */
    @Test
    void bfsAndDfsOnSimpleGraph() {
        MapGraph<String, Double> g = new MapGraph<>(true);
        g.addEdge("A", "B", 1.0, 1.0, 1);
        g.addEdge("A", "C", 1.0, 1.0, 1);
        g.addEdge("B", "D", 1.0, 1.0, 1);

        LinkedList<String> bfs = Algorithms.BreadthFirstSearch(g, "A");
        assertEquals("A", bfs.getFirst());
        assertTrue(bfs.contains("B"));
        assertTrue(bfs.contains("C"));
        assertTrue(bfs.contains("D"));

        LinkedList<String> dfs = Algorithms.DepthFirstSearch(g, "A");
        assertEquals("A", dfs.getFirst());
        assertTrue(dfs.contains("B"));
        assertTrue(dfs.contains("C"));
        assertTrue(dfs.contains("D"));
    }

    /**
     * Tests the computation of shortest paths from a source vertex.
     */
    @Test
    void shortestPathsFromSource() {
        MapGraph<String, Double> g = new MapGraph<>(true);
        g.addEdge("A", "B", 2.0, 2.0, 1);
        g.addEdge("A", "C", 5.0, 5.0, 1);
        g.addEdge("B", "C", 1.0, 1.0, 1);

        var paths = new ArrayList<List<String>>();
        var dists = new ArrayList<Double>();

        boolean ok = Algorithms.shortestPaths(g, "A", null, null, null, paths, dists);
        assertTrue(ok);

        int a = g.key("A");
        int b = g.key("B");
        int c = g.key("C");

        assertEquals(0.0, dists.get(a));
        assertEquals(2.0, dists.get(b));
        assertEquals(3.0, dists.get(c));

        assertEquals(g.numVertices(), paths.size());

        assertEquals(List.of("A"), paths.get(a));
        assertEquals(List.of("A", "B"), paths.get(b));
        assertEquals(List.of("A", "B", "C"), paths.get(c));
    }

    /**
     * Tests whether Prim's algorithm builds a minimal spanning tree.
     */
    @Test
    void primMSTBuildsTreeWithMinimalTotalDistance() {
        MapGraph<String, Double> g = new MapGraph<>(false);
        g.addEdge("A", "B", 0.0, 1.0, 1);
        g.addEdge("B", "C", 0.0, 2.0, 1);
        g.addEdge("C", "D", 0.0, 1.0, 1);
        g.addEdge("A", "D", 0.0, 4.0, 1);
        g.addEdge("B", "D", 0.0, 3.0, 1);

        var mst = Algorithms.primMST(g);
        assertEquals(2 * (g.numVertices() - 1), mst.numEdges());
        double total = 0.0;
        for (var e : mst.edges()) total += e.getDistance();
        assertEquals(2 * 4.0, total, 1e-6);
    }

    /**
     * Tests Bellman-Ford shortest path, negative cycle detection,
     * and unreachable destination scenarios.
     */
    @Test
    void bellmanFordShortestPathAndNegativeCycleAndNoPath() {
        MapGraph<String, Double> g = new MapGraph<>(true);
        g.addEdge("A", "B", 10.0, 1.0, 1);
        g.addEdge("B", "C", -5.0, 1.0, 1);
        g.addEdge("C", "D", 20.0, 1.0, 1);
        g.addEdge("A", "D", 50.0, 1.0, 1);

        LinkedList<String> path = new LinkedList<>();
        Double cost = Algorithms.shortestPathBellmanFord(g, "A", "D", path);
        assertNotNull(cost);
        assertEquals(25.0, cost, 1e-6);
        assertEquals(java.util.List.of("A","B","C","D"), path);

        g.addEdge("D", "B", -30.0, 1.0, 1);
        path.clear();
        assertNull(Algorithms.shortestPathBellmanFord(g, "A", "D", path));
        assertTrue(path.isEmpty());

        MapGraph<String, Double> g2 = new MapGraph<>(true);
        g2.addEdge("A", "B", 1.0, 1.0, 1);
        LinkedList<String> p2 = new LinkedList<>();
        assertNull(Algorithms.shortestPathBellmanFord(g2, "A", "D", p2));
    }
}
