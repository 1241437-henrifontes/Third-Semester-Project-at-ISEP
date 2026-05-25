package USEI15;

import Model.Graph.MapGraph;
import Model.RailwayStation;
import Model.Graph.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for graph shortest path algorithms.
 *
 * This test class focuses on the Bellman-Ford shortest path algorithm,
 * validating correct behavior under various scenarios including:
 *
 * Negative edge weights without cycles
 * Negative cycle detection
 * Disconnected graphs
 * Zero-cost edges
 * Source equal to destination
 *
 * The tests ensure algorithm robustness and correctness when applied
 * to directed graphs representing railway networks.
 */
class AlgorithmsTest {

    private MapGraph<RailwayStation, Double> graph;
    private RailwayStation s1, s2, s3, s4, s5;

    @BeforeEach
    void setUp() {
        graph = new MapGraph<>(true); // Grafo Direcionado

        s1 = new RailwayStation("Porto", 0, 0, null, null, null, true, true, false);
        s2 = new RailwayStation("Aveiro", 0, 0, null, null, null, true, true, false);
        s3 = new RailwayStation("Coimbra", 0, 0, null, null, null, true, true, false);
        s4 = new RailwayStation("Lisboa", 0, 0, null, null, null, true, true, false);
        s5 = new RailwayStation("Faro", 0, 0, null, null, null, true, true, false);

        graph.addVertex(s1);
        graph.addVertex(s2);
        graph.addVertex(s3);
        graph.addVertex(s4);
        graph.addVertex(s5);
    }



    @Test
    void testShortestPathWithNegativeWeights() {
        //Caminho com pesos negativos (Bónus) mas SEM ciclos negativos
        graph.addEdge(s1, s2, 10.0, 2, 8);
        graph.addEdge(s2, s3, -5.0, 2, 8);
        graph.addEdge(s3, s4, 20.0, 2, 8);
        graph.addEdge(s1, s4, 50.0, 2, 8);

        LinkedList<RailwayStation> path = new LinkedList<>();
        Double cost = Algorithms.shortestPathBellmanFord(graph, s1, s4, path);

        assertNotNull(cost);
        assertEquals(25.0, cost, 0.01);
        assertEquals(4, path.size());
    }

    @Test
    void testNegativeCycleDetection() {
        //Ciclo Negativo Infinito
        graph.addEdge(s1, s2, 10.0, 2, 8);
        graph.addEdge(s2, s3, -5.0, 2, 8);
        graph.addEdge(s3, s4, 20.0, 2, 8);
        graph.addEdge(s4, s2, -30.0, 2, 8);

        LinkedList<RailwayStation> path = new LinkedList<>();
        Double cost = Algorithms.shortestPathBellmanFord(graph, s1, s4, path);

        assertNull(cost, "Deve retornar null ao detetar ciclo negativo");
        assertFalse(path.isEmpty(), "Deve conter os nós do ciclo para reportar o erro");
    }

    @Test
    void testNoPath() {
        //Sem conexão
        graph.addEdge(s1, s2, 10.0, 2, 8);


        LinkedList<RailwayStation> path = new LinkedList<>();
        Double cost = Algorithms.shortestPathBellmanFord(graph, s1, s4, path);

        assertNull(cost);
        assertTrue(path.isEmpty());
    }

    @Test
    void testTotalNegativePathCost() {
        // O custo TOTAL da viagem é negativo (Lucro total), mas sem ciclos.

        graph.addEdge(s1, s2, 5.0, 2, 8);
        graph.addEdge(s2, s3, -20.0, 2, 8);

        LinkedList<RailwayStation> path = new LinkedList<>();
        Double cost = Algorithms.shortestPathBellmanFord(graph, s1, s3, path);

        assertNotNull(cost);
        assertEquals(-15.0, cost, 0.01, "O custo total deve ser negativo");
        assertEquals(3, path.size()); // s1, s2, s3
    }

    @Test
    void testStandardPositivePath() {
        //Caminho simples apenas com pesos positivos
        graph.addEdge(s1, s2, 10.0, 2, 8);
        graph.addEdge(s2, s3, 10.0, 2, 8);
        graph.addEdge(s3, s4, 10.0, 2, 8);

        LinkedList<RailwayStation> path = new LinkedList<>();
        Double cost = Algorithms.shortestPathBellmanFord(graph, s1, s4, path);

        assertNotNull(cost);
        assertEquals(30.0, cost, 0.01);
        assertEquals(4, path.size());
    }

    @Test
    void testSourceEqualsDestination() {
        //Origem é igual ao destino
        LinkedList<RailwayStation> path = new LinkedList<>();
        Double cost = Algorithms.shortestPathBellmanFord(graph, s1, s1, path);

        assertNotNull(cost);
        assertEquals(0.0, cost, 0.01, "Custo para a própria estação deve ser 0");
        assertEquals(1, path.size());
        assertEquals(s1, path.getFirst());
    }

    @Test
    void testChooseCheaperPathOverShorterHops() {
        //Existem dois caminhos.
        graph.addEdge(s1, s4, 100.0, 2, 8);

        graph.addEdge(s1, s2, 10.0, 2, 8);
        graph.addEdge(s2, s3, 5.0, 2, 8);
        graph.addEdge(s3, s4, 5.0, 2, 8);

        LinkedList<RailwayStation> path = new LinkedList<>();
        Double cost = Algorithms.shortestPathBellmanFord(graph, s1, s4, path);

        assertEquals(20.0, cost, 0.01);
        assertEquals(4, path.size(), "Deve escolher o caminho com mais estações mas menor custo");
        assertEquals(s2, path.get(1)); // Garante que passou por Aveiro
    }

    @Test
    void testZeroCostEdges() {
        //Arestas com custo 0
        graph.addEdge(s1, s2, 0.0, 2, 8);
        graph.addEdge(s2, s3, 0.0, 2, 8);
        graph.addEdge(s3, s4, 5.0, 2, 8);

        LinkedList<RailwayStation> path = new LinkedList<>();
        Double cost = Algorithms.shortestPathBellmanFord(graph, s1, s4, path);

        assertEquals(5.0, cost, 0.01);
    }

    @Test
    void testPositiveCycleSafe() {
        //Existe um ciclo, mas é positivo (aumenta o custo).
        graph.addEdge(s1, s2, 10.0, 2, 8);
        graph.addEdge(s2, s1, 10.0, 2, 8);
        graph.addEdge(s2, s3, 5.0, 2, 8);

        LinkedList<RailwayStation> path = new LinkedList<>();
        Double cost = Algorithms.shortestPathBellmanFord(graph, s1, s3, path);

        assertNotNull(cost);
        assertEquals(15.0, cost, 0.01);
    }

    @Test
    void testInvalidVertices() {
        //Pedir rota para uma estação que não existe no grafo
        RailwayStation ghostStation = new RailwayStation("Ghost", 0,0, null, null, null, true, true, false);

        LinkedList<RailwayStation> path = new LinkedList<>();
        Double cost = Algorithms.shortestPathBellmanFord(graph, s1, ghostStation, path);

        assertNull(cost);
        assertTrue(path.isEmpty() || path.size() == 0);
    }

    @Test
    void testDisconnectedGraphComponents() {
        graph.addEdge(s1, s2, 10.0, 2, 8);
        graph.addEdge(s4, s5, 10.0, 2, 8);

        LinkedList<RailwayStation> path = new LinkedList<>();
        Double cost = Algorithms.shortestPathBellmanFord(graph, s1, s5, path);

        assertNull(cost);
    }
}