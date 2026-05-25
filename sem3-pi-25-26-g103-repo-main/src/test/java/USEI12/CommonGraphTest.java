package USEI12;

import Model.Graph.MapGraph;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for common graph operations using the MapGraph implementation.
 *
 * <p>This class validates basic graph properties such as vertex and edge
 * management, cloning, equality, and vertex lookup operations.</p>
 */
class CommonGraphTest {

    /**
     * Tests basic graph behavior using a MapGraph instance.
     */
    @Test
    void basicsViaMapGraph() {
        MapGraph<String, Double> g = new MapGraph<>(true);
        assertTrue(g.isDirected());
        assertEquals(0, g.numVertices());
        assertEquals(0, g.numEdges());
        assertTrue(g.vertices().isEmpty());

        g.addVertex("A");
        g.addVertex("B");
        g.addEdge("A", "B", 1.0, 2.0, 1);

        assertTrue(g.validVertex("A"));
        assertFalse(g.validVertex("Z"));
        assertTrue(g.key("A") >= 0);
        assertNull(g.vertex(99));

        String found = g.vertex(v -> v.equals("B"));
        assertEquals("B", found);
        assertNull(g.vertex(v -> v.equals("X")));

        MapGraph<String, Double> clone = g.clone();
        assertEquals(g, clone);
        assertEquals(g.hashCode(), clone.hashCode());
    }
}
