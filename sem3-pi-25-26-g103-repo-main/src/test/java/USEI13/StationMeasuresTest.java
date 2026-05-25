package USEI13;

import Model.Graph.Edge;
import Model.Graph.Node;
import Model.Pair;
import Model.StationMeasures;
import Repositories.GraphRepository;
import Repositories.StationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the StationMeasures utility class.
 *
 * This test class validates the correct computation of graph-based
 * station metrics such as betweenness centrality, harmonic closeness,
 * normalized degree, normalized strength, and hub score.
 *
 * The tests are executed on a simple path graph to allow predictable
 * and verifiable metric values.
 */
@DisplayName("StationMeasures Unit Tests")
class StationMeasuresTest {

    private Node n1, n2, n3, n4;

    @BeforeEach
    void setUp() throws Exception {
        Constructor<GraphRepository> gConstructor = GraphRepository.class.getDeclaredConstructor();
        gConstructor.setAccessible(true);
        GraphRepository gInstance = gConstructor.newInstance();
        Field gField = GraphRepository.class.getDeclaredField("instance");
        gField.setAccessible(true);
        gField.set(null, gInstance);

        Constructor<StationRepository> sConstructor = StationRepository.class.getDeclaredConstructor();
        sConstructor.setAccessible(true);
        StationRepository sInstance = sConstructor.newInstance();
        Field sField = StationRepository.class.getDeclaredField("instance");
        sField.setAccessible(true);
        sField.set(null, sInstance);

        n1 = new Node("1", "S1", new Pair<>(1.0, 1.0), new Pair<>(10.0, 10.0));
        n2 = new Node("2", "S2", new Pair<>(2.0, 2.0), new Pair<>(20.0, 20.0));
        n3 = new Node("3", "S3", new Pair<>(3.0, 3.0), new Pair<>(30.0, 30.0));
        n4 = new Node("4", "S4", new Pair<>(4.0, 4.0), new Pair<>(40.0, 40.0));

        StationRepository sRepo = StationRepository.getInstance();
        sRepo.getStations().add(n1);
        sRepo.getStations().add(n2);
        sRepo.getStations().add(n3);
        sRepo.getStations().add(n4);

        sRepo.getEdges().add(new Edge<>(n1, n2, 1.0, 10.0, 10));
        sRepo.getEdges().add(new Edge<>(n2, n3, 1.0, 10.0, 10));
        sRepo.getEdges().add(new Edge<>(n3, n4, 1.0, 10.0, 10));

        GraphRepository gRepo = GraphRepository.getInstance();
        gRepo.buildGraph(false);
        gRepo.loadAllShortestPaths();
    }


    @Test
    @DisplayName("Should calculate betweenness correctly (path graph)")
    void testBetweenness() {
        double b1 = StationMeasures.betweenness(n1);
        double b4 = StationMeasures.betweenness(n4);
        double b2 = StationMeasures.betweenness(n2);
        double b3 = StationMeasures.betweenness(n3);

        assertEquals(0.0, b1, 1e-9);
        assertEquals(0.0, b4, 1e-9);

        assertTrue(b2 > 0.0);
        assertTrue(b3 > 0.0);
        assertEquals(b2, b3, 1e-9);
    }

    @Test
    @DisplayName("Should calculate harmonic closeness correctly")
    void testHarmonicCloseness() {
        
        double h1 = StationMeasures.harmonicCloseness(n1);
        assertEquals(1.8333333333333333 / 3.0, h1, 0.001);
    }

    @Test
    @DisplayName("Should calculate normalized degree correctly")
    void testDegree() {
        double d2 = StationMeasures.degree(n2);
        assertEquals(1.0, d2, 0.001);

        double d1 = StationMeasures.degree(n1);
        assertEquals(0.5, d1, 0.001);
    }

    @Test
    @DisplayName("Should calculate normalized strength correctly")
    void testStrength() {

        double s2 = StationMeasures.strength(n2);
        assertEquals(1.0, s2, 0.001);
        double s1 = StationMeasures.strength(n1);
        assertEquals(0.5, s1, 0.001);
    }

    @Test
    @DisplayName("Should calculate hub score correctly")
    void testHubScore() {
        double b = 2.0;
        double h = 0.5;
        double s = 1.0;
        double expected = 0.35 * b + 0.35 * h + 0.30 * s;
        assertEquals(expected, StationMeasures.hubScore(b, h, s), 0.001);
    }
}
