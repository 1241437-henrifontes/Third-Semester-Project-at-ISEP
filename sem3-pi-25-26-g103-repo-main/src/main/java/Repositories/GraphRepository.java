package Repositories;

import Model.Graph.Edge;
import Model.Graph.Graph;
import Model.Graph.Node;
import Model.Graph.MapGraph;

import java.util.*;
import java.util.function.BinaryOperator;

import static Model.Graph.Algorithms.primMST;
import static Model.Graph.Algorithms.shortestPaths;

/**
 * Central repository for graph structures used across the application.
 * <p>
 * Maintains the main station graph (undirected), an ordered variant (directed),
 * and the Minimal Backbone Network (MBN). It can also precompute and cache
 * all-pairs shortest paths and distances for efficiency.
 */
public class GraphRepository {
    /** Singleton instance. */
    public static GraphRepository instance = new GraphRepository();
    /** Undirected station graph used for most analyses. */
    private Graph<Node, Double> stationGraph;
    /** Directed variant of the station graph (ordered). */
    private Graph<Node, Double> stationGraphOrdered;
    /** Minimal Backbone Network computed from the station graph. */
    private Graph<Node, Double> minimalBackboneGraph;
    /** Holder for path reconstructions per source (optional legacy). */
    private ArrayList<ArrayList<LinkedList<Node>>> allPaths;
    /** Cached shortest paths per source vertex. */
    private HashMap<Node, ArrayList<List<Node>>> allShortestPaths;
    /** Cached distances per source vertex. */
    private HashMap<Node, ArrayList<Double>> allDistances;
    /** Maximum out-degree observed in the station graph. */
    public double maxDegree;
    /** Maximum strength (sum of edge costs) observed in the station graph. */
    public double maxStrength;
    /** Flag indicating whether background computation is running. */
    public volatile boolean isBuilding = false;

    private GraphRepository() {
        this.stationGraph = null;
        this.stationGraphOrdered = null;
        this.minimalBackboneGraph = new MapGraph<>(false);
        this.allPaths = new ArrayList<>();
    }

    public static GraphRepository getInstance() {
        return instance;
    }

    public Graph<Node, Double> getStationGraph() {
        return stationGraph;
    }

    public Graph<Node, Double> getStationGraphOrdered() {
        return stationGraphOrdered;
    }

    public Graph<Node, Double> getMinimalBackboneGraph() {
        return minimalBackboneGraph;
    }

    public boolean isBuilding() {
        return isBuilding;
    }

    public void setBuilding(boolean value) {
        isBuilding = value;
    }

    public void buildGraph(boolean ordered) {
        StationRepository repo = StationRepository.getInstance();
        List<Node> stations = repo.getStations();
        List<Edge<Node, Double>> edges = repo.getEdges();

        if (stations == null || edges == null) {
            return;
        }

        if (!ordered) {
            if (this.stationGraph == null) {
                this.stationGraph = new MapGraph<>(false);
            }

            for (Node n : stations) {
                stationGraph.addVertex(n);
            }

            for (Edge<Node, Double> e : edges) {
                stationGraph.addEdge(e.getVOrig(), e.getVDest(), e.getCost(), e.getDistance(), e.getCapacity());
            }
        } else {
            if (this.stationGraphOrdered == null) {
                this.stationGraphOrdered = new MapGraph<>(true);
            }

            for (Node n : stations) {
                stationGraphOrdered.addVertex(n);
            }

            for (Edge<Node, Double> e : edges) {
                stationGraphOrdered.addEdge(e.getVOrig(), e.getVDest(), e.getCost(), e.getDistance(), e.getCapacity());
            }
        }

        maxDegree = calculateMaxDegree(stationGraph);
        maxStrength = calculateMaxStrength(stationGraph);
    }


    public void loadAllShortestPaths() {
        allShortestPaths = new HashMap<>();
        allDistances = new HashMap<>();
        Comparator<Integer> ce = Integer::compare;
        BinaryOperator<Integer> sum = Integer::sum;
        Integer zero = 0;

        if (stationGraph != null && stationGraph.numVertices() > 0) {
            for (int i = 0; i < stationGraph.numVertices(); i++) {
                Node vOrig = stationGraph.vertex(i);

                ArrayList<List<Node>> pathsFromOrig = new ArrayList<>();
                ArrayList<Double> dists = new ArrayList<>();

                shortestPaths(stationGraph, vOrig, ce, sum, zero, pathsFromOrig, dists);

                allShortestPaths.put(vOrig, pathsFromOrig);
                allDistances.put(vOrig, dists);
            }
        }
    }


    public void buildAsync() {
        new Thread(() -> {
            setBuilding(true);
            try {
                loadAllShortestPaths();
            } finally {
                setBuilding(false);
            }
        }).start();
    }

    private double calculateMaxDegree(Graph<Node, Double> graph) {
        double maxDegree = 0;
        for (Node n : graph.vertices()) {
            if (graph.outDegree(n) > maxDegree) maxDegree = graph.outDegree(n);
        }
        return maxDegree;
    }

    private double calculateMaxStrength(Graph<Node, Double> graph) {
        double maxStrength = 0;
        for (Node n : graph.vertices()) {
            double strength = 0;
            for (Edge<Node, Double> e : graph.outgoingEdges(n)) {
                strength += e.getCost();
            }
            if (strength > maxStrength) maxStrength = strength;
        }
        return maxStrength;
    }

    public void computeMinimalBackboneNetwork() {
        minimalBackboneGraph = primMST(stationGraph);
    }

    public HashMap<Node, ArrayList<List<Node>>> getAllShortestPaths() {
        return allShortestPaths;
    }

    public HashMap<Node, ArrayList<java.lang.Double>> getAllDistances() {
        return allDistances;
    }
}
