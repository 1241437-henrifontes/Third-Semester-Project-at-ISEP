package USEI12;

import Model.Graph.Edge;
import Model.Graph.MapVertex;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the MapVertex class.
 *
 * <p>These tests validate adjacency management, edge storage,
 * vertex queries, string representation, and argument validation.</p>
 */
class MapVertexTest {

    /**
     * Tests constructor behavior when a null element is provided.
     */
    @Test
    void constructorNullGuard() {
        RuntimeException ex = assertThrows(RuntimeException.class, () -> new MapVertex<String, Double>(null));
        assertTrue(ex.getMessage().toLowerCase().contains("cannot"));
    }

    /**
     * Tests adding and removing adjacent vertices and querying
     * adjacency information.
     */
    @Test
    void addRemoveAdjacencyAndQueries() {
        MapVertex<String, Double> mv = new MapVertex<>("A");
        assertEquals("A", mv.getElement());
        assertEquals(0, mv.numAdjVertex());
        assertTrue(mv.getAllAdjVertex().isEmpty());
        assertTrue(mv.getAllOutEdges().isEmpty());

        Edge<String, Double> eAB = new Edge<>("A", "B", 1.0, 10.0, 5);
        mv.addAdjVert("B", eAB);
        assertEquals(1, mv.numAdjVertex());
        assertEquals(eAB, mv.getEdge("B"));

        Collection<String> adjs = mv.getAllAdjVertex();
        assertEquals(1, adjs.size());
        assertTrue(adjs.contains("B"));

        Collection<Edge<String, Double>> outs = mv.getAllOutEdges();
        assertEquals(1, outs.size());
        assertTrue(outs.contains(eAB));

        String s = mv.toString();
        assertTrue(s.contains("A"));
        assertTrue(s.contains("B"));

        mv.remAdjVert("B");
        assertEquals(0, mv.numAdjVertex());
        assertNull(mv.getEdge("B"));
    }
}
