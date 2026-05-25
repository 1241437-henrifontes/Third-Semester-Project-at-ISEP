package Model;

import Model.Graph.Algorithms;
import Model.Graph.Edge;
import Model.Graph.Graph;
import Model.Graph.Node;

import java.util.LinkedList;

public class RiskAwarePathFinder {

    public String findRobustRoute(Graph<Node, Double> graph, Node start, Node end) {

        if (graph == null || start == null || end == null) {
            return "Erro: Dados de entrada inválidos.";
        }

        LinkedList<Node> path = new LinkedList<>();
        Double totalCost = Algorithms.shortestPathBellmanFord(graph, start, end, path);

        // Erro ou Ciclo detetado (totalCost é null)
        if (totalCost == null) {
            if (!path.isEmpty() && path.size() >= 2) {
                Node u = path.get(0);
                Node v = path.get(1);

                return String.format("Negative cycle detected: stations and edges involved: %s -> %s",
                        u.getName(), v.getName());
            }
            return "Não existe caminho possível entre " + start.getName() + " e " + end.getName();
        }

        return formatPathOutput(graph, path, totalCost);
    }

    private String formatPathOutput(Graph<Node, Double> graph, LinkedList<Node> path, double totalCost) {
        StringBuilder sb = new StringBuilder();

        sb.append("(");

        for (int i = 0; i < path.size(); i++) {
            Node current = path.get(i);

            sb.append(current.getName());

            if (i < path.size() - 1) {
                Node next = path.get(i + 1);
                Edge<Node, Double> edge = graph.edge(current, next);

                double segmentCost = (edge != null) ? edge.getCost() : 0.0;

                sb.append(", ").append(String.format("%.2f", segmentCost)).append(", ");
            }
        }

        sb.append(") Total cost: ").append(String.format("%.2f", totalCost));

        return sb.toString();
    }
}