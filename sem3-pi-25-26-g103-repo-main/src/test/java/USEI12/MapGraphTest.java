package USEI12;

import Model.Graph.Edge;
import Model.Graph.MapGraph;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the MapGraph class.
 *
 * <p>This test suite validates vertex and edge management,
 * directed and undirected behavior, argument validation,
 * cloning, equality, and string representation.</p>
 */
class MapGraphTest {

    /**
     * Tests vertex and edge insertion in a directed graph.
     */
    @Test
    void addVerticesAndEdgesDirectedGraph() {
        MapGraph<String, Double> g = new MapGraph<>(true);
        assertTrue(g.addVertex("A"));
        assertTrue(g.addVertex("B"));
        assertFalse(g.addVertex("A")); // duplicate

        assertTrue(g.addEdge("A", "B", 1.0, 10.0, 5));
        assertFalse(g.addEdge("A", "B", 2.0, 20.0, 10)); // duplicate edge not added

        assertEquals(2, g.numVertices());
        assertEquals(1, g.numEdges());

        Edge<String, Double> e = g.edge("A", "B");
        assertNotNull(e);
        assertEquals("A", e.getVOrig());
        assertEquals("B", e.getVDest());
        assertNull(g.edge("B", "A"));

        assertEquals(1, g.outDegree("A"));
        assertEquals(0, g.outDegree("B"));
        assertEquals(0, g.inDegree("A"));
        assertEquals(1, g.inDegree("B"));

        Collection<Edge<String, Double>> outA = g.outgoingEdges("A");
        assertEquals(1, outA.size());
        Collection<Edge<String, Double>> inB = g.incomingEdges("B");
        assertEquals(1, inB.size());

        Collection<String> adjsA = g.adjVertices("A");
        assertEquals(1, adjsA.size());
        assertTrue(adjsA.contains("B"));

        int kA = g.key("A");
        assertEquals("A", g.vertex(kA));
        assertNull(g.vertex(-1));
        assertNull(g.vertex(99));

        assertEquals(1, g.edges().size());

        assertTrue(g.removeEdge("A", "B"));
        assertEquals(0, g.numEdges());
        assertFalse(g.removeEdge("A", "B"));
        assertTrue(g.removeVertex("A"));
        assertFalse(g.removeVertex("A"));

        MapGraph<String, Double> g2 = new MapGraph<>(false);
        g2.addEdge("X", "Y", 2.0, 1.0, 1);
        MapGraph<String, Double> gClone = g2.clone();
        assertEquals(g2, gClone);
        assertNotSame(g2, gClone);
    }

    /**
     * Tests edge behavior in undirected graphs, including
     * reverse edge insertion and removal.
     */
    @Test
    void undirectedAddsReverseEdgeAndRemovesBothOnDelete() {
        MapGraph<String, Double> g = new MapGraph<>(false);
        g.addVertex("A");
        g.addVertex("B");
        assertTrue(g.addEdge("A", "B", 1.0, 3.0, 1));
        assertEquals(2, g.numEdges()); // both directions
        assertNotNull(g.edge("B", "A"));

        assertTrue(g.removeEdge("A", "B"));
        assertEquals(0, g.numEdges());
    }

    /**
     * Tests behavior when invalid arguments are provided.
     */
    @Test
    void invalidArgsGuards() {
        MapGraph<String, Double> g = new MapGraph<>(true);
        assertThrows(RuntimeException.class, () -> g.addVertex(null));
        assertThrows(RuntimeException.class, () -> g.addEdge(null, "B", 1.0, 1.0, 1));
        assertThrows(RuntimeException.class, () -> g.addEdge("A", null, 1.0, 1.0, 1));
        assertThrows(RuntimeException.class, () -> g.removeVertex(null));
        assertThrows(RuntimeException.class, () -> g.removeEdge(null, "B"));
        assertThrows(RuntimeException.class, () -> g.removeEdge("A", null));

        assertEquals(-1, g.outDegree("X"));
        assertEquals(-1, g.inDegree("X"));
        assertNull(g.outgoingEdges("X"));
        assertTrue(g.incomingEdges("X").isEmpty());
    }

    /**
     * Tests string representation and equality comparison.
     */
    @Test
    void toStringAndEquals() {
        MapGraph<String, Double> g1 = new MapGraph<>(false);
        g1.addEdge("A", "B", 1.0, 2.0, 1);
        String s = g1.toString();
        assertTrue(s.contains("Graph:"));

        MapGraph<String, Double> g2 = g1.clone();
        assertEquals(g1, g2);

        MapGraph<String, Double> g3 = new MapGraph<>(false);
        g3.addEdge("A", "C", 1.0, 2.0, 1);
        assertNotEquals(g1, g3);

        MapGraph<String, Double> empty = new MapGraph<>(false);
        assertTrue(empty.toString().contains("Graph not defined"));
    }

    /**
     * Tests vertex lookup using predicates and vertex collection copying.
     */
    @Test
    void vertexPredicateAndVerticesCopy() {
        MapGraph<String, Double> g = new MapGraph<>(true);
        g.addEdge("A", "B", 1.0, 2.0, 1);
        assertTrue(g.vertices().contains("A"));
        assertTrue(g.vertices().contains("B"));

        String found = g.vertex(v -> v.equals("B"));
        assertEquals("B", found);
        assertNull(g.vertex(v -> v.equals("Z")));
    }
}
