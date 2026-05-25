package USEI12;

import Model.Graph.Edge;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Edge class.
 *
 * <p>These tests validate construction, getters, setters, equality,
 * hash code generation, string representation, and invalid argument handling.</p>
 */
class EdgeTest {

    /**
     * Tests edge construction and getter methods.
     */
    @Test
    void constructorAndGetters() {
        Edge<String, Double> e = new Edge<>("A", "B", 5.5, 10.0, 3);
        assertEquals("A", e.getVOrig());
        assertEquals("B", e.getVDest());
        assertEquals(5.5, e.getCost(), 1e-9);
        assertEquals(10.0, e.getDistance(), 1e-9);
        assertEquals(3, e.getCapacity());
    }

    /**
     * Tests setter methods for edge attributes.
     */
    @Test
    void setters() {
        Edge<Integer, Double> e = new Edge<>(1, 2, 1.0, 2.0, 1);
        e.setCost(7.25);
        assertEquals(7.25, e.getCost(), 1e-9);
        e.setDistance(42);
        assertEquals(42.0, e.getDistance(), 1e-9);
        e.setCapacity(9);
        assertEquals(9, e.getCapacity());
    }

    /**
     * Tests equality, hash code consistency, and string representation.
     */
    @Test
    void toStringAndEquality() {
        Edge<String, Double> e1 = new Edge<>("A", "B", 1.0, 1.0, 1);
        Edge<String, Double> e2 = new Edge<>("A", "B", 2.0, 2.0, 2);
        Edge<String, Double> e3 = new Edge<>("A", "C", 1.0, 1.0, 1);

        assertEquals(e1, e2);
        assertEquals(e1.hashCode(), e2.hashCode());
        assertNotEquals(e1, e3);

        String s = e1.toString();
        assertTrue(s.contains("A"));
        assertTrue(s.contains("B"));
        assertTrue(s.contains("cost"));
    }

    /**
     * Tests constructor behavior when null arguments are provided.
     */
    @Test
    void constructorNullGuards() {
        RuntimeException ex1 = assertThrows(RuntimeException.class, () -> new Edge<>(null, "B", 1.0, 1.0, 1));
        assertTrue(ex1.getMessage().toLowerCase().contains("cannot"));
        RuntimeException ex2 = assertThrows(RuntimeException.class, () -> new Edge<>("A", null, 1.0, 1.0, 1));
        assertTrue(ex2.getMessage().toLowerCase().contains("cannot"));
    }
}
