package USEI14;

import Model.Graph.*;
import Model.Pair;
import Model.MaxFlowResult;
import Services.MaxFlowAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes para USEI14 - Fluxo Máximo entre Dois Hubs (Edmonds-Karp).
 * Testa o algoritmo de max flow com diferentes cenários de rede ferroviária.
 */
@DisplayName("USEI14 - Maximum Flow Tests")
class MaxFlowTest {

    private Graph<Node, Double> graph;
    private Node nodeA, nodeB, nodeC, nodeD;

    @BeforeEach
    void setUp() {
        // Criar grafo dirigido simples para testes
        graph = new MapGraph<>(true);

        // Criar nós (estações) com coordenadas
        nodeA = new Node("A", "Station A", new Pair<>(0.0, 0.0), new Pair<>(0.0, 0.0));
        nodeB = new Node("B", "Station B", new Pair<>(1.0, 1.0), new Pair<>(1.0, 1.0));
        nodeC = new Node("C", "Station C", new Pair<>(2.0, 2.0), new Pair<>(2.0, 2.0));
        nodeD = new Node("D", "Station D", new Pair<>(3.0, 3.0), new Pair<>(3.0, 3.0));

        graph.addVertex(nodeA);
        graph.addVertex(nodeB);
        graph.addVertex(nodeC);
        graph.addVertex(nodeD);
    }

    @Test
    @DisplayName("Deve calcular fluxo máximo num caminho simples")
    void testSimpleMaxFlow() {
        // Cenário: A -> B -> D com capacidade 10
        graph.addEdge(nodeA, nodeB, 1.0, 100.0, 10);
        graph.addEdge(nodeB, nodeD, 1.0, 100.0, 10);

        MaxFlowResult result = MaxFlowAlgorithm.calculateMaxFlow(graph, nodeA, nodeD);

        // Verificações
        assertNotNull(result, "O resultado não deve ser null");
        assertEquals(10.0, result.getMaxFlowValue(), "Fluxo máximo deve ser 10");
        assertEquals(1, result.getAugmentingPaths().size(), "Deve encontrar 1 caminho aumentante");
    }

    @Test
    @DisplayName("Deve calcular fluxo máximo com múltiplos caminhos")
    void testMaxFlowWithMultiplePaths() {
        // Cenário: Dois caminhos paralelos de A para D
        // A -> B (10) -> D (15)
        // A -> C (5) -> D (10)
        graph.addEdge(nodeA, nodeB, 1.0, 100.0, 10);
        graph.addEdge(nodeA, nodeC, 1.0, 50.0, 5);
        graph.addEdge(nodeB, nodeD, 1.0, 100.0, 15);
        graph.addEdge(nodeC, nodeD, 1.0, 80.0, 10);

        MaxFlowResult result = MaxFlowAlgorithm.calculateMaxFlow(graph, nodeA, nodeD);

        // Verificações: Fluxo total = 10 + 5 = 15
        assertEquals(15.0, result.getMaxFlowValue(), "Fluxo máximo deve ser 15 (soma dos dois caminhos)");
        assertTrue(result.getAugmentingPaths().size() >= 2, "Deve encontrar pelo menos 2 caminhos");
    }

    @Test
    @DisplayName("Deve respeitar bottleneck (gargalo) na rede")
    void testBottleneck() {
        // Cenário: A -> B (10) -> C (5 <- GARGALO) -> D (10)
        graph.addEdge(nodeA, nodeB, 1.0, 100.0, 10);
        graph.addEdge(nodeB, nodeC, 1.0, 100.0, 5);  // Capacidade mínima
        graph.addEdge(nodeC, nodeD, 1.0, 100.0, 10);

        MaxFlowResult result = MaxFlowAlgorithm.calculateMaxFlow(graph, nodeA, nodeD);

        // Verificações: Fluxo limitado pelo gargalo
        assertEquals(5.0, result.getMaxFlowValue(), "Fluxo deve ser limitado pelo gargalo (5)");
    }

    @Test
    @DisplayName("Deve retornar 0 quando não há caminho entre origem e destino")
    void testNoPath() {
        // Cenário: A e D não estão conectados
        graph.addEdge(nodeA, nodeB, 1.0, 100.0, 10);
        graph.addEdge(nodeC, nodeD, 1.0, 100.0, 10);

        MaxFlowResult result = MaxFlowAlgorithm.calculateMaxFlow(graph, nodeA, nodeD);

        // Verificações
        assertEquals(0.0, result.getMaxFlowValue(), "Fluxo deve ser 0 quando não há caminho");
        assertEquals(0, result.getAugmentingPaths().size(), "Não deve encontrar caminhos aumentantes");
    }

    @Test
    @DisplayName("Deve lançar exceção quando origem = destino")
    void testSameSourceAndSink() {
        graph.addEdge(nodeA, nodeB, 1.0, 100.0, 10);

        // Verificação: Deve lançar IllegalArgumentException
        assertThrows(IllegalArgumentException.class, () -> {
            MaxFlowAlgorithm.calculateMaxFlow(graph, nodeA, nodeA);
        }, "Deve lançar exceção quando origem = destino");
    }

    @Test
    @DisplayName("Deve lançar exceção com parâmetros null")
    void testNullParameters() {
        graph.addEdge(nodeA, nodeB, 1.0, 100.0, 10);

        // Verificações: Todos devem lançar IllegalArgumentException
        assertThrows(IllegalArgumentException.class, () ->
            MaxFlowAlgorithm.calculateMaxFlow(null, nodeA, nodeD),
            "Deve lançar exceção com grafo null");

        assertThrows(IllegalArgumentException.class, () ->
            MaxFlowAlgorithm.calculateMaxFlow(graph, null, nodeD),
            "Deve lançar exceção com origem null");

        assertThrows(IllegalArgumentException.class, () ->
            MaxFlowAlgorithm.calculateMaxFlow(graph, nodeA, null),
            "Deve lançar exceção com destino null");
    }

    @Test
    @DisplayName("Deve calcular Maximum Flow Graph = Initial - Residual")
    void testMaxFlowGraphCalculation() {
        // Cenário: A -> B (10) -> D (10)
        graph.addEdge(nodeA, nodeB, 1.0, 100.0, 10);
        graph.addEdge(nodeB, nodeD, 1.0, 100.0, 10);

        MaxFlowResult result = MaxFlowAlgorithm.calculateMaxFlow(graph, nodeA, nodeD);

        // Verificações do Maximum Flow Graph (segundo o PowerPoint)
        assertNotNull(result.getMaxFlowGraph(), "Deve calcular Maximum Flow Graph");

        Edge<Node, Double> flowEdge = result.getMaxFlowGraph().edge(nodeA, nodeB);
        assertNotNull(flowEdge, "Maximum Flow Graph deve conter arestas");
        assertEquals(10, flowEdge.getCapacity(),
            "Fluxo que passou deve ser 10 (capacidade original - capacidade residual)");
    }

    @Test
    @DisplayName("Deve retornar Residual Graph com capacidades restantes")
    void testResidualGraph() {
        // Cenário: A -> B (10) -> D (10)
        graph.addEdge(nodeA, nodeB, 1.0, 100.0, 10);
        graph.addEdge(nodeB, nodeD, 1.0, 100.0, 10);

        MaxFlowResult result = MaxFlowAlgorithm.calculateMaxFlow(graph, nodeA, nodeD);

        // Verificações do Residual Graph
        assertNotNull(result.getResidualGraph(), "Deve retornar Residual Graph");

        Edge<Node, Double> residualEdge = result.getResidualGraph().edge(nodeA, nodeB);
        assertEquals(0, residualEdge.getCapacity(),
            "Capacidade residual deve ser 0 (tudo foi usado)");
    }

    @Test
    @DisplayName("Não deve modificar o grafo original")
    void testOriginalGraphNotModified() {
        // Cenário: A -> B (10) -> D (10)
        graph.addEdge(nodeA, nodeB, 1.0, 100.0, 10);
        graph.addEdge(nodeB, nodeD, 1.0, 100.0, 10);

        int originalEdges = graph.numEdges();
        Edge<Node, Double> originalEdge = graph.edge(nodeA, nodeB);
        int originalCapacity = originalEdge.getCapacity();

        // Executar algoritmo
        MaxFlowAlgorithm.calculateMaxFlow(graph, nodeA, nodeD);

        // Verificações: Grafo original não foi modificado
        assertEquals(originalEdges, graph.numEdges(), "Número de arestas deve permanecer igual");
        assertEquals(originalCapacity, originalEdge.getCapacity(),
            "Capacidade original não deve ser alterada");
    }

    @Test
    @DisplayName("Deve ter complexidade temporal O(V * E^2)")
    void testTemporalComplexity() {
        String complexity = MaxFlowAlgorithm.getTemporalComplexity();

        assertNotNull(complexity, "Descrição da complexidade não deve ser null");
        assertTrue(complexity.contains("O(V * E^2)"),
            "Deve documentar complexidade O(V * E^2) do Edmonds-Karp");
    }

    @Test
    @DisplayName("Teste de integração: Rede ferroviária realista")
    void testRealisticRailwayNetwork() {
        // Cenário: Rede inspirada em ferrovias belgas
        Node ougree = new Node("2011", "OUGREE", new Pair<>(50.6, 5.6), new Pair<>(50.6, 5.6));
        Node anderlecht = new Node("2089", "ANDERLECHT", new Pair<>(50.8, 4.3), new Pair<>(50.8, 4.3));
        Node liege = new Node("2001", "LIEGE", new Pair<>(50.6, 5.5), new Pair<>(50.6, 5.5));
        Node brussels = new Node("2080", "BRUSSELS", new Pair<>(50.8, 4.3), new Pair<>(50.8, 4.3));

        Graph<Node, Double> railway = new MapGraph<>(true);
        railway.addVertex(ougree);
        railway.addVertex(anderlecht);
        railway.addVertex(liege);
        railway.addVertex(brussels);

        // Ligações ferroviárias com capacidades (comboios por dia)
        railway.addEdge(ougree, liege, 10.0, 15.0, 15);      // OUGREE -> LIEGE (15)
        railway.addEdge(liege, brussels, 12.0, 95.0, 12);    // LIEGE -> BRUSSELS (12)
        railway.addEdge(brussels, anderlecht, 5.0, 10.0, 20); // BRUSSELS -> ANDERLECHT (20)

        MaxFlowResult result = MaxFlowAlgorithm.calculateMaxFlow(railway, ougree, anderlecht);

        // Verificações
        assertNotNull(result, "Resultado não deve ser null");
        assertTrue(result.getMaxFlowValue() > 0, "Deve encontrar fluxo na rede");
        assertTrue(result.getMaxFlowValue() <= 15,
            "Fluxo não pode exceder capacidade de saída da origem");

        // Verificar conformidade com especificação: source stid, target stid, maxFlowValue
        assertEquals("2011", ougree.getNode_id(), "Source stid deve estar disponível");
        assertEquals("2089", anderlecht.getNode_id(), "Target stid deve estar disponível");
        assertNotNull(result.getMaxFlowValue(), "maxFlowValue deve estar disponível");
    }
}

