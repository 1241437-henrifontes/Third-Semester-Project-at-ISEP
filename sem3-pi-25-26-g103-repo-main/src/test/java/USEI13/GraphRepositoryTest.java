package USEI13;

import Model.Graph.Edge;
import Model.Graph.Graph;
import Model.Graph.Node;
import Model.Pair;
import Repositories.GraphRepository;
import Repositories.StationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for GraphRepository class.
 *
 * This test class verifies the correct behavior of the graph repository,
 * including singleton instantiation, graph construction from the StationRepository class,
 * shortest path loading, and minimal backbone network computation.
 *
 * Reflection is used to reset singleton instances before each test
 * to ensure test isolation.
 */
@DisplayName("GraphRepository Unit Tests")
class GraphRepositoryTest {

    private GraphRepository repository;

    @BeforeEach
    void setUp() throws Exception {
        Constructor<GraphRepository> constructor = GraphRepository.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        GraphRepository newInstance = constructor.newInstance();
        
        Field instanceField = GraphRepository.class.getDeclaredField("instance");
        instanceField.setAccessible(true);
        instanceField.set(null, newInstance);
        
        repository = GraphRepository.getInstance();

        Constructor<StationRepository> sConstructor = StationRepository.class.getDeclaredConstructor();
        sConstructor.setAccessible(true);
        StationRepository sInstance = sConstructor.newInstance();
        Field sField = StationRepository.class.getDeclaredField("instance");
        sField.setAccessible(true);
        sField.set(null, sInstance);
    }

    @Test
    @DisplayName("Should return the same instance (Singleton)")
    void testGetInstance() {
        GraphRepository instance1 = GraphRepository.getInstance();
        GraphRepository instance2 = GraphRepository.getInstance();
        assertSame(instance1, instance2);
    }

    @Test
    @DisplayName("Should initialize with null station graph and empty backbone")
    void testInitialState() {
        assertNull(repository.getStationGraph());
        assertNotNull(repository.getMinimalBackboneGraph());
        assertFalse(repository.getMinimalBackboneGraph().isDirected());
    }

    @Test
    @DisplayName("Should build graph from StationRepository")
    void testBuildGraph() {
        Node n1 = new Node("1", "S1", new Pair<>(1.0, 1.0), new Pair<>(10.0, 10.0));
        Node n2 = new Node("2", "S2", new Pair<>(2.0, 2.0), new Pair<>(20.0, 20.0));
        Edge<Node, Double> edge = new Edge<>(n1, n2, 10.0, 100.0, 50);

        StationRepository sRepo = StationRepository.getInstance();
        sRepo.getStations().add(n1);
        sRepo.getStations().add(n2);
        sRepo.getEdges().add(edge);

        repository.buildGraph(false);

        Graph<Node, Double> graph = repository.getStationGraph();
        assertNotNull(graph);
        assertEquals(2, graph.numVertices());
        assertTrue(graph.numEdges() >= 1);
        assertTrue(graph.validVertex(n1));
        assertTrue(graph.validVertex(n2));
        
        assertEquals(1.0, repository.maxDegree);
        assertEquals(10.0, repository.maxStrength);
    }

    @Test
    @DisplayName("Should handle empty StationRepository when building graph")
    void testBuildGraphWithEmptyRepo() {
        repository.buildGraph(false);
        assertNotNull(repository.getStationGraph());
        assertEquals(0, repository.getStationGraph().numVertices());
    }


    @Test
    @DisplayName("Should load all shortest paths")
    void testLoadAllShortestPaths() {
        Node n1 = new Node("1", "S1", new Pair<>(1.0, 1.0), new Pair<>(10.0, 10.0));
        Node n2 = new Node("2", "S2", new Pair<>(2.0, 2.0), new Pair<>(20.0, 20.0));
        Node n3 = new Node("3", "S3", new Pair<>(3.0, 3.0), new Pair<>(30.0, 30.0));

        StationRepository sRepo = StationRepository.getInstance();
        sRepo.getStations().add(n1);
        sRepo.getStations().add(n2);
        sRepo.getStations().add(n3);

        sRepo.getEdges().add(new Edge<>(n1, n2, 5.0, 50.0, 10));
        sRepo.getEdges().add(new Edge<>(n2, n3, 5.0, 50.0, 10));

        repository.buildGraph(false);
        repository.loadAllShortestPaths();

        assertNotNull(repository.getAllShortestPaths());
        assertNotNull(repository.getAllDistances());
        assertEquals(3, repository.getAllShortestPaths().size());

        var graph = repository.getStationGraph();
        var vertices = graph.vertices();

        ArrayList<List<Node>> pathsFromN1 = repository.getAllShortestPaths().get(n1);
        assertNotNull(pathsFromN1);

        assertEquals(vertices.size(), pathsFromN1.size());

        int idxN1 = graph.key(n1);
        int idxN2 = graph.key(n2);
        int idxN3 = graph.key(n3);

        List<Node> pathToN3 = pathsFromN1.get(idxN3);
        assertNotNull(pathToN3);
        assertEquals(List.of(n1, n2, n3), pathToN3);

        ArrayList<Double> distsFromN1 = repository.getAllDistances().get(n1);
        assertNotNull(distsFromN1);
        assertEquals(0.0, distsFromN1.get(idxN1));
        assertEquals(5.0, distsFromN1.get(idxN2));
        assertEquals(10.0, distsFromN1.get(idxN3));
    }


    @Test
    @DisplayName("Should compute minimal backbone network")
    void testComputeMinimalBackboneNetwork() {
        Node n1 = new Node("1", "S1", new Pair<>(1.0, 1.0), new Pair<>(10.0, 10.0));
        Node n2 = new Node("2", "S2", new Pair<>(2.0, 2.0), new Pair<>(20.0, 20.0));
        Node n3 = new Node("3", "S3", new Pair<>(3.0, 3.0), new Pair<>(30.0, 30.0));

        StationRepository sRepo = StationRepository.getInstance();
        sRepo.getStations().add(n1);
        sRepo.getStations().add(n2);
        sRepo.getStations().add(n3);
        sRepo.getEdges().add(new Edge<>(n1, n2, 5.0, 50.0, 10));
        sRepo.getEdges().add(new Edge<>(n2, n3, 10.0, 100.0, 10));
        sRepo.getEdges().add(new Edge<>(n1, n3, 2.0, 20.0, 10));

        repository.buildGraph(false);
        repository.computeMinimalBackboneNetwork();

        Graph<Node, Double> backbone = repository.getMinimalBackboneGraph();
        assertNotNull(backbone);
        assertEquals(3, backbone.numVertices());
        assertTrue(backbone.numEdges() >= 2); 
        
        assertNotNull(backbone.edge(n1, n3));
        assertNotNull(backbone.edge(n1, n2));
        assertNull(backbone.edge(n2, n3));
    }
}
