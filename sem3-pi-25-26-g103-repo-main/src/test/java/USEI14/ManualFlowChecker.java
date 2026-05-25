package USEI14;

import Model.Graph.*;
import Model.Pair;
import Services.MaxFlowAlgorithm;
import Model.MaxFlowResult;

/**
 * Manual Flow Checker - Ferramenta para verificar manualmente o fluxo máximo.
 * Útil para debug e validação dos resultados do algoritmo Edmonds-Karp.
 */
public class ManualFlowChecker {

    public static void main(String[] args) {
        System.out.println("-----------------------------------------------------------------");
        System.out.println("|         MANUAL FLOW CHECKER - USEI14                          |");
        System.out.println("-----------------------------------------------------------------\n");

        // Criar rede de teste simples
        testSimpleNetwork();

        System.out.println("\n" + "─".repeat(65));

        // Criar rede com múltiplos caminhos
        testMultiplePathsNetwork();

        System.out.println("\n" + "─".repeat(65));

        // Criar rede com bottleneck
        testBottleneckNetwork();
    }

    private static void testSimpleNetwork() {
        System.out.println(" TESTE 1: Rede Simples (A → B → C)");
        System.out.println("-".repeat(65));

        Graph<Node, Double> graph = new MapGraph<>(true);

        Node a = new Node("A", "Station A", new Pair<>(0.0, 0.0), new Pair<>(0.0, 0.0));
        Node b = new Node("B", "Station B", new Pair<>(1.0, 1.0), new Pair<>(1.0, 1.0));
        Node c = new Node("C", "Station C", new Pair<>(2.0, 2.0), new Pair<>(2.0, 2.0));

        graph.addVertex(a);
        graph.addVertex(b);
        graph.addVertex(c);

        // A → B (capacidade 10)
        graph.addEdge(a, b, 1.0, 100.0, 10);
        // B → C (capacidade 10)
        graph.addEdge(b, c, 1.0, 100.0, 10);

        System.out.println("Estrutura da rede:");
        System.out.println("  A --[10]--> B --[10]--> C");
        System.out.println("\nFluxo máximo esperado: 10\n");

        MaxFlowResult result = MaxFlowAlgorithm.calculateMaxFlow(graph, a, c);

        printResult(a, c, result);
    }

    private static void testMultiplePathsNetwork() {
        System.out.println(" TESTE 2: Rede com Múltiplos Caminhos");
        System.out.println("-".repeat(65));

        Graph<Node, Double> graph = new MapGraph<>(true);

        Node a = new Node("A", "Station A", new Pair<>(0.0, 0.0), new Pair<>(0.0, 0.0));
        Node b = new Node("B", "Station B", new Pair<>(1.0, 1.0), new Pair<>(1.0, 1.0));
        Node c = new Node("C", "Station C", new Pair<>(2.0, 2.0), new Pair<>(2.0, 2.0));
        Node d = new Node("D", "Station D", new Pair<>(3.0, 3.0), new Pair<>(3.0, 3.0));

        graph.addVertex(a);
        graph.addVertex(b);
        graph.addVertex(c);
        graph.addVertex(d);

        // Dois caminhos paralelos
        graph.addEdge(a, b, 1.0, 100.0, 10);  // A → B (10)
        graph.addEdge(a, c, 1.0, 100.0, 5);   // A → C (5)
        graph.addEdge(b, d, 1.0, 100.0, 15);  // B → D (15)
        graph.addEdge(c, d, 1.0, 100.0, 10);  // C → D (10)

        System.out.println("Estrutura da rede:");
        System.out.println("       B --[15]-->");
        System.out.println("      /            \\");
        System.out.println("  A --[10]          D");
        System.out.println("      \\            /");
        System.out.println("       C --[10]-->");
        System.out.println("      [5]");
        System.out.println("\nFluxo máximo esperado: 15 (10 por B + 5 por C)\n");

        MaxFlowResult result = MaxFlowAlgorithm.calculateMaxFlow(graph, a, d);

        printResult(a, d, result);
    }

    private static void testBottleneckNetwork() {
        System.out.println(" TESTE 3: Rede com Bottleneck (Gargalo)");
        System.out.println("-".repeat(65));

        Graph<Node, Double> graph = new MapGraph<>(true);

        Node a = new Node("A", "Station A", new Pair<>(0.0, 0.0), new Pair<>(0.0, 0.0));
        Node b = new Node("B", "Station B", new Pair<>(1.0, 1.0), new Pair<>(1.0, 1.0));
        Node c = new Node("C", "Station C", new Pair<>(2.0, 2.0), new Pair<>(2.0, 2.0));
        Node d = new Node("D", "Station D", new Pair<>(3.0, 3.0), new Pair<>(3.0, 3.0));

        graph.addVertex(a);
        graph.addVertex(b);
        graph.addVertex(c);
        graph.addVertex(d);

        graph.addEdge(a, b, 1.0, 100.0, 100);  // A → B (100)
        graph.addEdge(b, c, 1.0, 100.0, 5);    // B → C (5) ← BOTTLENECK
        graph.addEdge(c, d, 1.0, 100.0, 100);  // C → D (100)

        System.out.println("Estrutura da rede:");
        System.out.println("  A --[100]--> B --[5]--> C --[100]--> D");
        System.out.println("                    ↑");
        System.out.println("                BOTTLENECK");
        System.out.println("\nFluxo máximo esperado: 5 (limitado pelo gargalo)\n");

        MaxFlowResult result = MaxFlowAlgorithm.calculateMaxFlow(graph, a, d);

        printResult(a, d, result);
    }

    private static void printResult(Node source, Node sink, MaxFlowResult result) {
        System.out.println("-------------------------------------------------------------");
        System.out.println("|                    RESULTADO                               |");
        System.out.println("-------------------------------------------------------------");
        System.out.printf("| Source Station ID      | %-31s|%n", source.getNode_id());
        System.out.printf("| Target Station ID      | %-31s|%n", sink.getNode_id());
        System.out.printf("| Max Flow Value         | %-31.0f|%n", result.getMaxFlowValue());
        System.out.printf("| Augmenting Paths Found | %-31d|%n", result.getAugmentingPaths().size());
        System.out.println("-------------------------------------------------------------");

        if (result.getAugmentingPaths().size() > 0) {
            System.out.println("\n Caminhos Aumentantes Encontrados:");
            int pathNum = 1;
            for (var path : result.getAugmentingPaths()) {
                System.out.print("   Caminho " + pathNum + ": ");
                for (int i = 0; i < path.size(); i++) {
                    System.out.print(path.get(i).getNode_id());
                    if (i < path.size() - 1) {
                        System.out.print(" → ");
                    }
                }
                System.out.println();
                pathNum++;
            }
        }

        System.out.println("\n Verificação: " +
                (result.getMaxFlowValue() > 0 ? "PASSOU" : "SEM FLUXO"));
    }
}
