package Model;

import Model.Graph.Edge;
import Repositories.GraphRepository;
import Model.Graph.Node;

import java.util.*;

public class StationMeasures {

    public static GraphRepository getGraphRepository() {
        return GraphRepository.getInstance();
    }


    public static double betweenness(Node station) {
        var repo = getGraphRepository();
        var graph = repo.getStationGraph();
        int n = graph.numVertices();
        if (n <= 2) return 0.0;

        double sum = 0.0;

        HashMap<Node, ArrayList<List<Node>>> allPaths = repo.getAllShortestPaths();

        for (Map.Entry<Node, ArrayList<List<Node>>> entry : allPaths.entrySet()) {
            Node s = entry.getKey();
            if (s.equals(station)) continue;

            ArrayList<List<Node>> pathsFromS = entry.getValue();

            for (int i = 0; i < n; i++) {
                Node t = graph.vertex(i);
                if (t.equals(station) || t.equals(s)) continue;

                List<Node> path = pathsFromS.get(graph.key(t));
                if (path == null || path.isEmpty()) continue;

                if (containsStrictlyBetween(path, station)) {
                    sum += 1.0;
                }
            }
        }

        return sum / ((n - 1) * (n - 2));
    }

    private static boolean containsStrictlyBetween(List<Node> path, Node station) {
        if (path.size() < 3) return false;
        if (station.equals(path.get(0)) || station.equals(path.get(path.size() - 1))) return false;

        for (int k = 1; k < path.size() - 1; k++) {
            if (station.equals(path.get(k))) return true;
        }
        return false;
    }


    public static double harmonicCloseness(Node station){
        double inverseNumVertices = 1.0 /(getGraphRepository().getStationGraph().numVertices()-1);

        HashMap<Node, ArrayList<Double>> allDistances =  getGraphRepository().getAllDistances();
        ArrayList<Double> dists = allDistances.get(station);
        double summation = 0.0;
        for(Double dist : dists) {
            if (dist > 0 && !Double.isInfinite(dist)) {
                summation += 1.0 / dist;
            }
        }
        return inverseNumVertices *  summation;
    }

    public static double degree (Node station){
        double degree = getGraphRepository().getStationGraph().outDegree(station);
        return degree / getGraphRepository().maxDegree;
    }

    public static double strength (Node station){
        double strength = 0;
        for(Edge e : getGraphRepository().getStationGraph().outgoingEdges(station)){
            strength += e.getCost();
        }
        return  strength / getGraphRepository().maxStrength;
    }

    public static double hubScore (double betweenness, double harmonicCloseness, double strength){
        return 0.35 *betweenness + 0.35 * harmonicCloseness + 0.30 * strength;
    }
}
