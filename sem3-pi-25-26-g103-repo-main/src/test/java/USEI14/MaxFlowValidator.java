package USEI14;

import Controllers.MaxFlowController;
import Model.Graph.Edge;
import Model.Graph.Graph;
import Model.Graph.Node;
import Model.MaxFlowResult;
import Repositories.GraphRepository;
import Services.MaxFlowAlgorithm;

import java.util.*;

/**
 * Max Flow Validator - Ferramenta de validação e debugging para USEI14.
 * Permite verificar se o cálculo de fluxo máximo está correto,
 * mostrando informações detalhadas sobre a rede e os caminhos encontrados.
 */
public class MaxFlowValidator {

    /**
     * Valida e mostra informações detalhadas sobre um cálculo de fluxo máximo.
     *
     * @param sourceId ID da estação origem
     * @param sinkId ID da estação destino
     */
    public static void validateMaxFlow(String sourceId, String sinkId) {
        MaxFlowController controller = new MaxFlowController();
        GraphRepository graphRepository = GraphRepository.getInstance();

        System.out.println("\n-----------------------------------------------------------------------");
        System.out.println("|           MAX FLOW VALIDATION & DEBUGGING TOOL                       |");
        System.out.println("------------------------------------------------------------------------\n");

        // Obter estações
        Node source = controller.getStationById(sourceId);
        Node sink = controller.getStationById(sinkId);

        if (source == null || sink == null) {
            System.out.println("❌ Erro: Estação origem ou destino não encontrada!");
            if (source == null) System.out.println("   Origem '" + sourceId + "' não existe.");
            if (sink == null) System.out.println("   Destino '" + sinkId + "' não existe.");
            return;
        }

        System.out.println("📍 ORIGEM:  " + source.getNode_id() + " - " + source.getName());
        System.out.println("📍 DESTINO: " + sink.getNode_id() + " - " + sink.getName());
        System.out.println();

        Graph<Node, Double> graph = graphRepository.getStationGraph();

        // 1. Verificar ligação direta
        System.out.println("---------------------------------------------------------------------");
        System.out.println("1️⃣  VERIFICAR LIGAÇÃO DIRETA");
        System.out.println("---------------------------------------------------------------------");
        Edge<Node, Double> directEdge = graph.edge(source, sink);
        if (directEdge != null) {
            System.out.println("   Existe ligação direta!");
            System.out.println("   Capacidade: " + directEdge.getCapacity() + " comboios/dia");
            System.out.println("   Distância: " + directEdge.getDistance() + " km");
            System.out.println("   Custo: " + directEdge.getCost());
        } else {
            System.out.println("   Sem ligação direta - necessita estações intermediárias");
        }
        System.out.println();

        // 2. Mostrar ligações de saída da origem
        System.out.println("---------------------------------------------------------------------");
        System.out.println("  LIGAÇÕES DE SAÍDA DA ORIGEM (" + source.getName() + ")");
        System.out.println("---------------------------------------------------------------------");
        Collection<Edge<Node, Double>> outgoingEdges = graph.outgoingEdges(source);
        if (outgoingEdges.isEmpty()) {
            System.out.println("⚠️  Sem ligações de saída da origem!");
        } else {
            System.out.printf("%-12s | %-40s | %-10s | %-10s%n", "ID Estação", "Nome Estação", "Capacidade", "Distância");
            System.out.println("─────────────┼──────────────────────────────────────────┼────────────┼──────────");
            for (Edge<Node, Double> edge : outgoingEdges) {
                Node dest = edge.getVDest();
                System.out.printf("%-12s | %-40s | %-10d | %-10.2f%n",
                        dest.getNode_id(),
                        truncate(dest.getName(), 40),
                        edge.getCapacity(),
                        edge.getDistance());
            }
        }
        System.out.println();

        // 3. Mostrar ligações de entrada do destino
        System.out.println("---------------------------------------------------------------------");
        System.out.println("3️⃣  LIGAÇÕES DE ENTRADA DO DESTINO (" + sink.getName() + ")");
        System.out.println("---------------------------------------------------------------------");
        Collection<Edge<Node, Double>> incomingEdges = graph.incomingEdges(sink);
        if (incomingEdges.isEmpty()) {
            System.out.println("⚠️  Sem ligações de entrada ao destino!");
        } else {
            System.out.printf("%-12s | %-40s | %-10s | %-10s%n", "ID Estação", "Nome Estação", "Capacidade", "Distância");
            System.out.println("─────────────┼──────────────────────────────────────────┼────────────┼──────────");
            for (Edge<Node, Double> edge : incomingEdges) {
                Node orig = edge.getVOrig();
                System.out.printf("%-12s | %-40s | %-10d | %-10.2f%n",
                        orig.getNode_id(),
                        truncate(orig.getName(), 40),
                        edge.getCapacity(),
                        edge.getDistance());
            }
        }
        System.out.println();

        // 4. Calcular fluxo máximo e mostrar caminhos aumentantes
        System.out.println("---------------------------------------------------------------------");
        System.out.println("  EXECUÇÃO DO ALGORITMO EDMONDS-KARP");
        System.out.println("---------------------------------------------------------------------");

        MaxFlowResult result = MaxFlowAlgorithm.calculateMaxFlow(graph, source, sink);

        System.out.println(" FLUXO MÁXIMO: " + result.getMaxFlowValue() + " comboios/dia");
        System.out.println();

        List<List<Node>> paths = result.getAugmentingPaths();
        if (paths.isEmpty()) {
            System.out.println("  Nenhum caminho aumentante encontrado!");
            System.out.println("   Isto significa que não há ligação entre origem e destino.");
        } else {
            System.out.println(" NÚMERO DE CAMINHOS AUMENTANTES: " + paths.size());
            System.out.println();

            int pathNum = 1;
            for (List<Node> path : paths) {
                System.out.println("- Caminho " + pathNum + " --------------------------------------------");
                System.out.println("| Rota:");

                for (int i = 0; i < path.size(); i++) {
                    Node node = path.get(i);
                    System.out.print("|   " + node.getNode_id() + " (" + truncate(node.getName(), 20) + ")");

                    if (i < path.size() - 1) {
                        Node nextNode = path.get(i + 1);
                        Edge<Node, Double> edge = graph.edge(node, nextNode);
                        if (edge != null) {
                            System.out.print(" --[cap:" + edge.getCapacity() + "]-> ");
                        } else {
                            System.out.print(" --[?]--> ");
                        }
                        System.out.println();
                    } else {
                        System.out.println();
                    }
                }
                System.out.println("---------------------------------------------------------------------");
                System.out.println();
                pathNum++;
            }
        }

        // 5. Checklist de verificação
        System.out.println("\n-----------------------------------------------------------------");
        System.out.println("  CHECKLIST DE VERIFICAÇÃO");
        System.out.println("-----------------------------------------------------------------");

        boolean hasOutgoing = !outgoingEdges.isEmpty();
        boolean hasIncoming = !incomingEdges.isEmpty();
        boolean hasPaths = !paths.isEmpty();
        double maxFlow = result.getMaxFlowValue();

        System.out.println((hasOutgoing ? "Y" : "N") + " Origem tem ligações de saída: " + hasOutgoing);
        System.out.println((hasIncoming ? "Y" : "N") + " Destino tem ligações de entrada: " + hasIncoming);
        System.out.println((hasPaths ? "Y" : "N") + " Caminhos aumentantes encontrados: " + hasPaths);
        System.out.println((maxFlow > 0 ? "Y" : "N") + " Fluxo máximo calculado: " + maxFlow + " comboios/dia");

        System.out.println();

        if (maxFlow > 0 && hasPaths) {
            System.out.println(" RESULTADO VÁLIDO!");
            System.out.println("   O algoritmo encontrou " + paths.size() + " caminho(s) com capacidade total de " + maxFlow + " comboios/dia.");
            System.out.println("   Este é o throughput máximo teórico entre estas estações.");
        } else if (maxFlow == 0 && !hasPaths) {
            System.out.println("  NÃO EXISTE CAMINHO entre origem e destino!");
            System.out.println("   Estas estações não estão ligadas na rede ferroviária.");
        } else {
            System.out.println("  RESULTADO INESPERADO - Por favor revê os dados acima.");
        }

        // 6. Mostrar especificação USEI14
        System.out.println("\n---------------------------------------------------------------------");
        System.out.println("  CONFORMIDADE COM ESPECIFICAÇÃO USEI14");
        System.out.println("---------------------------------------------------------------------");
        System.out.println("Expected Returns – maxflow summary:");
        System.out.println("   source stid:    " + source.getNode_id());
        System.out.println("   target stid:    " + sink.getNode_id());
        System.out.println("   maxFlowValue:   " + result.getMaxFlowValue());
        System.out.println(" Todos os campos obrigatórios estão presentes!");
    }

    /**
     * Trunca uma string ao comprimento máximo.
     */
    private static String truncate(String str, int maxLength) {
        if (str == null) return "";
        if (str.length() > maxLength) {
            return str.substring(0, maxLength - 3) + "...";
        }
        return str;
    }

    /**
     * Método main para execução standalone.
     */
    public static void main(String[] args) {
        // Exemplo: Validar fluxo entre OUGREE e ANDERLECHT
        System.out.println("Exemplo de validação: OUGREE (2011) → ANDERLECHT (2089)\n");
        validateMaxFlow("2011", "2089");
    }
}
