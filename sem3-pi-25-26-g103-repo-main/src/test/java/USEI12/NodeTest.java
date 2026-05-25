package USEI12;

import Model.Graph.Node;
import Model.Pair;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Node class.
 *
 * <p>This class validates getters, setters, equality,
 * hash code consistency, and string representation.</p>
 */
class NodeTest {

    /**
     * Tests node getters, setters, equality, hash code,
     * and string representation.
     */
    @Test
    void gettersSettersEqualsHashCodeToString() {
        Pair<Double, Double> geo = new Pair<>(41.0, -8.0);
        Pair<Double, Double> xy = new Pair<>(100.0, 200.0);
        Node n = new Node("ID1", "Porto", geo, xy);

        assertEquals("ID1", n.getNode_id());
        assertEquals("Porto", n.getName());
        assertEquals(geo, n.getGeoCoordinates());
        assertEquals(xy, n.getXyCoordinates());

        n.setNode_id("IDX");
        n.setName("Lisboa");
        Pair<Double, Double> geo2 = new Pair<>(38.7, -9.1);
        Pair<Double, Double> xy2 = new Pair<>(300.0, 400.0);
        n.setGeoCoordinates(geo2);
        n.setXyCoordinates(xy2);

        assertEquals("IDX", n.getNode_id());
        assertEquals("Lisboa", n.getName());
        assertEquals(geo2, n.getGeoCoordinates());
        assertEquals(xy2, n.getXyCoordinates());

        Node n2 = new Node("IDX", "Lisboa", geo2, xy2);
        assertEquals(n, n2);
        assertEquals(n.hashCode(), n2.hashCode());
        Node n3 = new Node("OTHER", "Lisboa", geo2, xy2);
        assertNotEquals(n, n3);

        String s = n.toString();
        assertTrue(s.contains("IDX"));
        assertTrue(s.contains("Lisboa"));
    }
}
