package USEI14;

import Model.Graph.*;
import Model.MaxFlowResult;
import Model.Pair;
import Services.MaxFlowAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for USEI14 - Maximum Throughput Between Two Hubs.
 * Tests the complete flow from algorithm to result according to specification:
 * "Expected Returns – maxflow summary: source stid, target stid, maxFlowValue"
 */
public class IntegrationTest {

    private Graph<Node, Double> railwayNetwork;
    private Node ougree, anderlecht, liege, brussels, namur;

    @BeforeEach
    void setUp() {
        // Create a realistic railway network similar to Belgian railways
        railwayNetwork = new MapGraph<>(true);

        // Create stations
        ougree = new Node("2011", "OUGREE", new Pair<>(50.6, 5.6), new Pair<>(50.6, 5.6));
        anderlecht = new Node("2089", "ANDERLECHT", new Pair<>(50.8, 4.3), new Pair<>(50.8, 4.3));
        liege = new Node("2001", "LIEGE", new Pair<>(50.6, 5.5), new Pair<>(50.6, 5.5));
        brussels = new Node("2080", "BRUSSELS", new Pair<>(50.8, 4.3), new Pair<>(50.8, 4.3));
        namur = new Node("2050", "NAMUR", new Pair<>(50.4, 4.8), new Pair<>(50.4, 4.8));

        // Add stations to network
        railwayNetwork.addVertex(ougree);
        railwayNetwork.addVertex(anderlecht);
        railwayNetwork.addVertex(liege);
        railwayNetwork.addVertex(brussels);
        railwayNetwork.addVertex(namur);

        // Add railway connections with capacities (trains per day)
        // OUGREE -> LIEGE (capacity 15)
        railwayNetwork.addEdge(ougree, liege, 10.0, 15.0, 15);

        // LIEGE -> NAMUR (capacity 10)
        railwayNetwork.addEdge(liege, namur, 15.0, 60.0, 10);

        // LIEGE -> BRUSSELS (capacity 12)
        railwayNetwork.addEdge(liege, brussels, 12.0, 95.0, 12);

        // NAMUR -> BRUSSELS (capacity 8)
        railwayNetwork.addEdge(namur, brussels, 8.0, 50.0, 8);

        // BRUSSELS -> ANDERLECHT (capacity 20)
        railwayNetwork.addEdge(brussels, anderlecht, 5.0, 10.0, 20);
    }

    @Test
    void testUSEI14_CompleteFlow_OugreeToAnderlecht() {
        // Execute the complete max flow calculation
        MaxFlowResult result = MaxFlowAlgorithm.calculateMaxFlow(
            railwayNetwork, ougree, anderlecht
        );

        // Verify result according to specification
        assertNotNull(result, "Result should not be null");

        // Expected Returns: maxFlowValue
        assertTrue(result.getMaxFlowValue() > 0,
            "Max flow value should be greater than 0");

        // The maximum flow should be limited by the bottleneck in the network
        assertTrue(result.getMaxFlowValue() <= 15,
            "Max flow cannot exceed source capacity");

        System.out.println("------------------------------------------------");
        System.out.println("|     USEI14 Integration Test Result           |");
        System.out.println("------------------------------------------------");
        System.out.printf("| Source Station ID  | %-23s|%n", ougree.getNode_id());
        System.out.printf("| Target Station ID  | %-23s|%n", anderlecht.getNode_id());
        System.out.printf("| Max Flow Value     | %-23.0f|%n", result.getMaxFlowValue());
        System.out.println("------------------------------------------------");
    }

    @Test
    void testUSEI14_VerifyEdmondsKarpCorrectness() {
        MaxFlowResult result = MaxFlowAlgorithm.calculateMaxFlow(
            railwayNetwork, ougree, anderlecht
        );

        // Verify Edmonds-Karp algorithm characteristics
        assertNotNull(result.getAugmentingPaths(),
            "Should return augmenting paths found");
        assertTrue(result.getAugmentingPaths().size() > 0,
            "Should find at least one augmenting path");

        // Verify O(V*E²) complexity description
        String complexity = MaxFlowAlgorithm.getTemporalComplexity();
        assertTrue(complexity.contains("O(V * E^2)"),
            "Should document correct temporal complexity");
    }

    @Test
    void testUSEI14_MaxFlowGraphCalculation() {
        MaxFlowResult result = MaxFlowAlgorithm.calculateMaxFlow(
            railwayNetwork, ougree, anderlecht
        );

        // Verify Maximum Flow Graph = Initial - Residual (PowerPoint approach)
        assertNotNull(result.getMaxFlowGraph(),
            "Should calculate Maximum Flow Graph");
        assertNotNull(result.getResidualGraph(),
            "Should return Residual Graph");

        // Verify flow conservation: flow that passed <= original capacity
        for (Edge<Node, Double> flowEdge : result.getMaxFlowGraph().edges()) {
            Edge<Node, Double> originalEdge = railwayNetwork.edge(
                flowEdge.getVOrig(), flowEdge.getVDest()
            );

            if (originalEdge != null) {
                assertTrue(flowEdge.getCapacity() <= originalEdge.getCapacity(),
                    "Flow that passed cannot exceed original capacity");
            }
        }
    }

    @Test
    void testUSEI14_NoPathScenario() {
        // Create isolated node
        Node isolatedStation = new Node("9999", "ISOLATED", new Pair<>(0.0, 0.0), new Pair<>(0.0, 0.0));
        railwayNetwork.addVertex(isolatedStation);

        MaxFlowResult result = MaxFlowAlgorithm.calculateMaxFlow(
            railwayNetwork, ougree, isolatedStation
        );

        assertEquals(0.0, result.getMaxFlowValue(),
            "Max flow should be 0 when no path exists");
        assertEquals(0, result.getAugmentingPaths().size(),
            "Should find no augmenting paths");
    }

    @Test
    void testUSEI14_DirectConnection() {
        // Test with direct connection
        railwayNetwork.addEdge(ougree, anderlecht, 5.0, 200.0, 25);

        MaxFlowResult result = MaxFlowAlgorithm.calculateMaxFlow(
            railwayNetwork, ougree, anderlecht
        );

        assertTrue(result.getMaxFlowValue() >= 25,
            "Max flow should use direct high-capacity connection");
    }

    @Test
    void testUSEI14_BottleneckIdentification() {
        // Create a network with clear bottleneck
        Graph<Node, Double> bottleneckNetwork = new MapGraph<>(true);
        Node a = new Node("A", "Station A", new Pair<>(0.0, 0.0), new Pair<>(0.0, 0.0));
        Node b = new Node("B", "Station B", new Pair<>(1.0, 1.0), new Pair<>(1.0, 1.0));
        Node c = new Node("C", "Station C", new Pair<>(2.0, 2.0), new Pair<>(2.0, 2.0));

        bottleneckNetwork.addVertex(a);
        bottleneckNetwork.addVertex(b);
        bottleneckNetwork.addVertex(c);

        // A -> B (capacity 100)
        bottleneckNetwork.addEdge(a, b, 1.0, 100.0, 100);
        // B -> C (capacity 5) <- BOTTLENECK
        bottleneckNetwork.addEdge(b, c, 1.0, 100.0, 5);

        MaxFlowResult result = MaxFlowAlgorithm.calculateMaxFlow(bottleneckNetwork, a, c);

        assertEquals(5.0, result.getMaxFlowValue(),
            "Max flow should be limited by bottleneck");
    }

    @Test
    void testUSEI14_ComplexNetworkWithMultiplePaths() {
        // Add additional paths to create more complex network
        Node mechelen = new Node("2070", "MECHELEN", new Pair<>(51.0, 4.5), new Pair<>(51.0, 4.5));
        railwayNetwork.addVertex(mechelen);

        railwayNetwork.addEdge(liege, mechelen, 10.0, 70.0, 8);
        railwayNetwork.addEdge(mechelen, anderlecht, 6.0, 20.0, 10);

        MaxFlowResult result = MaxFlowAlgorithm.calculateMaxFlow(
            railwayNetwork, ougree, anderlecht
        );

        assertTrue(result.getMaxFlowValue() > 0,
            "Should find flow in complex network");
        assertTrue(result.getAugmentingPaths().size() >= 2,
            "Should find multiple augmenting paths");
    }

    @Test
    void testUSEI14_SymmetricCapacities() {
        // Test with symmetric capacities
        Graph<Node, Double> symmetricNetwork = new MapGraph<>(true);
        Node x = new Node("X", "Station X", new Pair<>(0.0, 0.0), new Pair<>(0.0, 0.0));
        Node y = new Node("Y", "Station Y", new Pair<>(1.0, 1.0), new Pair<>(1.0, 1.0));

        symmetricNetwork.addVertex(x);
        symmetricNetwork.addVertex(y);
        symmetricNetwork.addEdge(x, y, 1.0, 100.0, 10);
        symmetricNetwork.addEdge(y, x, 1.0, 100.0, 10);

        MaxFlowResult result = MaxFlowAlgorithm.calculateMaxFlow(symmetricNetwork, x, y);

        assertEquals(10.0, result.getMaxFlowValue(),
            "Should handle symmetric capacities correctly");
    }

    @Test
    void testUSEI14_SpecificationCompliance() {
        // Verify compliance with specification:
        // "Expected Returns – maxflow summary: source stid, target stid, maxFlowValue"

        MaxFlowResult result = MaxFlowAlgorithm.calculateMaxFlow(
            railwayNetwork, ougree, anderlecht
        );

        // Can extract source stid
        assertNotNull(ougree.getNode_id(), "Source station ID should be available");

        // Can extract target stid
        assertNotNull(anderlecht.getNode_id(), "Target station ID should be available");

        // Can extract maxFlowValue
        assertNotNull(result.getMaxFlowValue(), "Max flow value should be available");
        assertTrue(result.getMaxFlowValue() >= 0, "Max flow value should be non-negative");

        System.out.println("\n USEI14 Specification Compliance Verified:");
        System.out.println("   - source stid: " + ougree.getNode_id());
        System.out.println("   - target stid: " + anderlecht.getNode_id());
        System.out.println("   - maxFlowValue: " + result.getMaxFlowValue());
    }
}
