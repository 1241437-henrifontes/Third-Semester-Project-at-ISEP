package USEI12;

import Model.Graph.MapGraph;
import Model.Graph.Node;
import Model.Pair;
import UI.Utils.GraphToDot;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the GraphToDot utility class.
 *
 * <p>This test verifies that a DOT file is correctly generated
 * from a graph and that duplicate or invalid edges are not included.</p>
 */
class GraphToDotTest {

    private final Path dotPath = Path.of("outputFiles/MBN.dot");

    /**
     * Deletes the generated DOT file after each test execution.
     *
     * @throws IOException if file deletion fails
     */
    @AfterEach
    void cleanup() throws IOException {
        if (Files.exists(dotPath)) {
            Files.delete(dotPath);
        }
    }

    /**
     * Tests the generation of a DOT file from a graph.
     *
     * @throws IOException if file reading fails
     */
    @Test
    void generateDotFile() throws IOException {
        MapGraph<Node, Double> g = new MapGraph<>(false);
        Node a = new Node("1", "A", new Pair<>(0.0, 0.0), new Pair<>(0.0, 0.0));
        Node b = new Node("2", "B", new Pair<>(0.0, 0.0), new Pair<>(72.0, 72.0));
        Node c = new Node("3", "C", new Pair<>(0.0, 0.0), new Pair<>(36.0, 36.0));

        g.addEdge(a, a, 1.0, 1.0, 1);
        g.addEdge(a, b, 1.0, 1.0, 1);
        g.addEdge(b, a, 1.0, 1.0, 1);
        g.addEdge(b, c, 1.0, 1.0, 1);

        GraphToDot.generateDotFile(g);
        assertTrue(Files.exists(dotPath));

        String content = Files.readString(dotPath);
        assertTrue(content.contains("graph \"Minimal Backbone Network\""));
        assertTrue(content.contains("\"A\""));
        assertTrue(content.contains("\"B\""));
        assertTrue(content.contains("\"C\""));
        assertFalse(content.contains("\"A\" -- \"A\""));
        int countAB = content.split("\"A\" -- \"B\"").length - 1 + content.split("\"B\" -- \"A\"").length - 1;
        assertEquals(1, countAB);
    }
}
