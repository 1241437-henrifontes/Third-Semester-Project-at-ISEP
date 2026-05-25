package Model.Graph;

import java.util.*;
import java.util.function.BinaryOperator;

/**
 * @author DEI-ISEP
 */
public class Algorithms {
    /**
     * Performs breadth-first search of a Graph starting in a vertex
     *
     * @param g    Graph instance
     * @param vert vertex that will be the source of the search
     * @return a LinkedList with the vertices of breadth-first search
     */
    public static <Node, Double> LinkedList<Node> BreadthFirstSearch(Graph<Node, Double> g, Node vert) {
        LinkedList<Node> qbfs = new LinkedList<>();
        LinkedList<Node> quax = new LinkedList<>();
        boolean[] visited = new boolean[g.numVertices()];

        qbfs.add(vert);
        quax.add(vert);
        visited[g.key(vert)] = true;

        while (!quax.isEmpty()) {
            vert = quax.poll();
            for (Node vAdj : g.adjVertices(vert)) {
                if (!visited[g.key(vAdj)]) {
                    qbfs.add(vAdj);
                    quax.add(vAdj);
                    visited[g.key(vAdj)] = true;
                }
            }
        }

        return qbfs;
    }

    /**
     * Performs depth-first search starting in a vertex
     *
     * @param g       Graph instance
     * @param vOrig   vertex of graph g that will be the source of the search
     * @param visited set of previously visited vertices
     * @param qdfs    return LinkedList with vertices of depth-first search
     */
    private static <Node, Double> void DepthFirstSearch(Graph<Node, Double> g, Node vOrig, boolean[] visited, LinkedList<Node> qdfs) {
        if (visited[g.key(vOrig)]) return;

        qdfs.add(vOrig);
        visited[g.key(vOrig)] = true;

        for (Node vAdj : g.adjVertices(vOrig)) {
            DepthFirstSearch(g, vAdj, visited, qdfs);
        }
    }

    /**
     * Performs depth-first search starting in a vertex
     *
     * @param g    Graph instance
     * @param vert vertex of graph g that will be the source of the search
     * @return a LinkedList with the vertices of depth-first search
     */
    public static <Node, Double> LinkedList<Node> DepthFirstSearch(Graph<Node, Double> g, Node vert) {
        LinkedList<Node> qdfs = new LinkedList<>();
        boolean[] visited = new boolean[g.numVertices()];

        DepthFirstSearch(g, vert, visited, qdfs);

        return qdfs;
    }

    /**
     * Returns all paths from vOrig to vDest
     *
     * @param g       Graph instance
     * @param vOrig   Vertex that will be the source of the path
     * @param vDest   Vertex that will be the end of the path
     * @param visited set of discovered vertices
     * @param path    stack with vertices of the current path (the path is in reverse order)
     * @param paths   ArrayList with all the paths (in correct order)
     */
    private static <Node, Double> void allPaths(Graph<Node, Double> g, Node vOrig, Node vDest, boolean[] visited, LinkedList<Node> path, ArrayList<LinkedList<Node>> paths) {
        path.add(vOrig);
        visited[g.key(vOrig)] = true;

        for (Node vAdj : g.adjVertices(vOrig)) {
            if (vAdj == vDest) {
                path.add(vDest);
                paths.add(path);
                path.removeLast();
            } else {
                if (!visited[g.key(vAdj)]) {
                    allPaths(g, vAdj, vDest, visited, path, paths);
                }
            }
        }

        path.removeLast();
    }

    /**
     * Returns all paths from vOrig to vDest
     *
     * @param g     Graph instance
     * @param vOrig information of the Vertex origin
     * @param vDest information of the Vertex destination
     * @return paths ArrayList with all paths from vOrig to vDest
     */
    public static <Node, Double> ArrayList<LinkedList<Node>> allPaths(Graph<Node, Double> g, Node vOrig, Node vDest) {
        LinkedList<Node> path = new LinkedList<>();
        boolean[] visited = new boolean[g.numVertices()];
        ArrayList<LinkedList<Node>> paths = new ArrayList<>();

        allPaths(g, vOrig, vDest, visited, path, paths);

        return paths;
    }

    /**
     * Computes shortest-path distance from a source vertex to all reachable
     * vertices of a graph g with non-negative edge weights
     * This implementation uses Dijkstra's algorithm
     *
     * @param g        Graph instance
     * @param vOrig    Vertex that will be the source of the path
     * @param visited  set of previously visited vertices
     * @param pathKeys minimum path vertices keys
     * @param dist     minimum distances
     */
    private static <Node, Double> void shortestPathDijkstra(Graph<Node, Double> g, Node vOrig, Comparator<Integer> ce, BinaryOperator<Integer> sum, Integer zero, boolean[] visited, Node[] pathKeys, double[] dist) {
        int n = g.numVertices();
        int vOrigKey = g.key(vOrig);

        for (int i = 0; i < n; i++) {
            dist[i] = java.lang.Double.POSITIVE_INFINITY;
            pathKeys[i] = null;
            visited[i] = false;
        }

        dist[vOrigKey] = 0.0;

        boolean found = true;

        while (found) {
            found = false;
            int vKey = -1;
            double minDist = java.lang.Double.POSITIVE_INFINITY;

            for (int i = 0; i < n; i++) {
                if (!visited[i] && dist[i] < minDist) {
                    minDist = dist[i];
                    vKey = i;
                    found = true;
                }
            }

            if (found) {
                visited[vKey] = true;
                Node v = g.vertex(vKey);

                for (Node adj : g.adjVertices(v)) {
                    int adjKey = g.key(adj);
                    double cost = g.edge(v, adj).getCost();

                    if (!visited[adjKey] && dist[vKey] + cost < dist[adjKey]) {
                        dist[adjKey] = dist[vKey] + cost;
                        pathKeys[adjKey] = v;
                    }
                }
            }
        }
    }



    /**
     * Shortest-path between two vertices
     *
     * @param g         graph
     * @param vOrig     origin vertex
     * @param vDest     destination vertex
     * @param ce        comparator between elements of type Integer
     * @param sum       sum two elements of type Integer
     * @param zero      neutral element of the sum in elements of type Integer
     * @param shortPath returns the vertices which make the shortest path
     * @return if vertices exist in the graph and are connected, true, false otherwise
     */
    public static <Node, Double> Integer shortestPath(Graph<Node, Double> g, Node vOrig, Node vDest, Comparator<Integer> ce, BinaryOperator<Integer> sum, Integer zero, LinkedList<Node> shortPath) {
        if (!g.validVertex(vOrig) || !g.validVertex(vDest))
            return null;

        boolean[] visited = new boolean[g.numVertices()];
        Node[] pathKeys = (Node[]) new Object[g.numVertices()];
        double[] dist = new double[g.numVertices()];

        shortestPathDijkstra(g, vOrig, ce, sum, zero, visited, pathKeys, dist);

        int destKey = g.key(vDest);

        if (dist[destKey] == java.lang.Double.POSITIVE_INFINITY)
            return null;

        shortPath.clear();
        getPath(g, vOrig, vDest, pathKeys, shortPath);

        return (Integer) (Object) dist[destKey];
    }


    /**
     * Shortest-path between a vertex and all other vertices
     *
     * @param g     graph
     * @param vOrig start vertex
     * @param ce    comparator between elements of type Integer
     * @param sum   sum two elements of type Integer
     * @param zero  neutral element of the sum in elements of type Integer
     * @param paths returns all the minimum paths
     * @param dists returns the corresponding minimum distances
     * @return if vOrig exists in the graph, true, false otherwise
     */

    public static <Node, Double> boolean shortestPaths(
            Graph<Node, Double> g,
            Node vOrig,
            Comparator<Integer> ce,
            BinaryOperator<Integer> sum,
            Integer zero,
            ArrayList<List<Node>> paths,
            ArrayList<java.lang.Double> dists) {

        if (!g.validVertex(vOrig)) return false;

        boolean[] visited = new boolean[g.numVertices()];
        Node[] pathKeys = (Node[]) new Object[g.numVertices()];
        double[] dist = new double[g.numVertices()];

        shortestPathDijkstra(g, vOrig, ce, sum, zero, visited, pathKeys, dist);

        paths.clear();
        dists.clear();

        for (int i = 0; i < g.numVertices(); i++) {
            LinkedList<Node> ordedPath = new LinkedList<>();
            Node vDest = g.vertex(i);

            if (dist[i] != java.lang.Double.POSITIVE_INFINITY) {
                getPath(g, vOrig, vDest, pathKeys, ordedPath);
            } else {
                ordedPath.clear();
            }

            paths.add(ordedPath);
            dists.add(dist[i]);
        }
        return true;
    }



    /**
     * Extracts from pathKeys the minimum path between voInf and vdInf
     * The path is constructed from the end to the beginning
     *
     * @param g        Graph instance
     * @param vOrig    information of the Vertex origin
     * @param vDest    information of the Vertex destination
     * @param pathKeys minimum path vertices keys
     * @param path     stack with the minimum path (correct order)
     */
    private static <Node, Double> void getPath(Graph<Node, Double> g, Node vOrig, Node vDest, Node[] pathKeys, LinkedList<Node> path) {
        if (vOrig.equals(vDest)) {
            path.add(vOrig);
        } else {
            Node prev = pathKeys[g.key(vDest)];
            if (prev != null) {
                getPath(g, vOrig, prev,pathKeys, path);
                path.add(vDest);
            }
        }
    }

    /**
     * Calculates the minimum distance graph using Floyd-Warshall
     *
     * @param g   initial graph
     * @param ce  comparator between elements of type Integer
     * @param sum sum two elements of type Integer
     * @return the minimum distance graph
     */
    public static <Node, Double> MatrixGraph<Node, Double> minDistGraph(Graph<Node, Double> g, Comparator<Double> ce, BinaryOperator<Double> sum) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Computes the minimum spanning tree graph using Prim's algorithm and two auxiliary methods
     *
     * @param g initial graph
     * @return the minimum spanning tree graph
     */
    public static <Node> Graph<Node, Double> primMST(Graph<Node, Double> g) {
        int n = g.numVertices();

        double[] dist = new double[n];
        int[] path = new int[n];
        boolean[] visited = new boolean[n];

        for (int i = 0; i < n; i++) {
            dist[i] = Double.POSITIVE_INFINITY;
            path[i] = -1;
            visited[i] = false;
        }

        int vOrig = 0;
        dist[vOrig] = 0.0;

        while (vOrig != -1) {
            visited[vOrig] = true;
            Node v = g.vertex(vOrig);
            List<Node> adj = new ArrayList<>(g.adjVertices(v));
            adj.sort(Comparator.comparingInt(g::key));
            for (Node vAdj : adj) {
                int adjKey = g.key(vAdj);
                double distance = g.edge(v, vAdj).getDistance();
                if (!visited[adjKey] && dist[adjKey] > distance) {
                    dist[adjKey] = distance;
                    path[adjKey] = vOrig;
                }
            }
            vOrig = getVertMinDist(dist, visited);
        }
        return buildMst(g, path, dist);
    }

    /**
     * Returns the index of the vertex with the minimum distance in dist
     *
     * @param dist array of distances
     * @param visited array of visited vertices
     * @return the index of the vertex with the minimum distance
     */
    private static int getVertMinDist(double[] dist, boolean[] visited) {
        double min = Double.POSITIVE_INFINITY;
        int v = -1;

        for (int i = 0; i < dist.length; i++) {
            if (!visited[i] && dist[i] < min) {
                min = dist[i];
                v = i;
            }
        }

        return v;
    }

    /**
     * Builds the minimum spanning tree graph from the path and distance arrays
     *
     * @param g intial graph
     * @param path array of vertices in the path from the vertex with minimum distance to the root
     * @param dist array of distances in the path
     * @return the minimum spanning tree graph
     */
    private static <Node> Graph<Node, Double> buildMst(Graph<Node, Double> g, int[] path, double[] dist) {
        Graph<Node, Double> mst = new MapGraph<>(false);

        for (int i = 0; i < g.numVertices(); i++) {
            mst.addVertex(g.vertex(i));
        }

        for (int i = 0; i < path.length; i++) {
            if (path[i] != -1) {
                Node v = g.vertex(i);
                Node u = g.vertex(path[i]);
                mst.addEdge(u, v, 0.0, dist[i], 0);
            }
        }

        return mst;
    }

    /**
     * Calculates the shortest path between two vertices using the Bellman-Ford algorithm.
     * Capable of handling negative edge weights and detecting negative cycles.
     *
     * @param g         The graph
     * @param vOrig     The source vertex
     * @param vDest     The destination vertex
     * @param shortPath Output parameter: LinkedList to store the vertices of the shortest path
     * @return The total cost of the path (as Double), or null if no path exists or a negative cycle is detected.
     */
    public static <Node> Double shortestPathBellmanFord(Graph<Node, Double> g, Node vOrig, Node vDest, LinkedList<Node> shortPath) {

        if (!g.validVertex(vOrig) || !g.validVertex(vDest)) {
            return null;
        }

        int numVerts = g.numVertices();
        double[] dist = new double[numVerts];
        Node[] predecessors = (Node[]) new Object[numVerts];

        for (int i = 0; i < numVerts; i++) {
            dist[i] = Double.POSITIVE_INFINITY;
            predecessors[i] = null;
        }

        int srcKey = g.key(vOrig);
        dist[srcKey] = 0.0;

        for (int i = 1; i < numVerts; i++) {
            boolean changed = false;
            for (Edge<Node, Double> edge : g.edges()) {
                Node u = edge.getVOrig();
                Node v = edge.getVDest();
                int uKey = g.key(u);
                int vKey = g.key(v);
                double cost = edge.getCost();

                if (dist[uKey] != Double.POSITIVE_INFINITY && dist[uKey] + cost < dist[vKey]) {
                    dist[vKey] = dist[uKey] + cost;
                    predecessors[vKey] = u;
                    changed = true;
                }
            }
            if (!changed) break;
        }

        // Deteção de Ciclos Negativos
        for (Edge<Node, Double> edge : g.edges()) {
            Node u = edge.getVOrig();
            Node v = edge.getVDest();
            int uKey = g.key(u);
            int vKey = g.key(v);
            double cost = edge.getCost();

            if (dist[uKey] != Double.POSITIVE_INFINITY && dist[uKey] + cost < dist[vKey]) {

                shortPath.clear();
                shortPath.add(u);
                shortPath.add(v);
                return null;
            }
        }

        int destKey = g.key(vDest);
        if (dist[destKey] == Double.POSITIVE_INFINITY) {
            return null;
        }
        shortPath.clear();
        Node curr = vDest;
        while (curr != null) {
            shortPath.addFirst(curr);
            if (curr.equals(vOrig)) break;
            if (shortPath.size() > numVerts) return null;
            int currKey = g.key(curr);
            curr = predecessors[currKey];
        }

        if (shortPath.isEmpty() || !shortPath.getFirst().equals(vOrig)) {
            shortPath.clear();
            return null;
        }
        return dist[destKey];
    }

    /**
     * Performs a specialized BFS for finding augmenting paths in max flow algorithms.
     * This BFS only follows edges with remaining capacity > 0.
     * Used by Edmonds-Karp algorithm.
     *
     * @param g      Graph instance (residual graph)
     * @param source Source vertex
     * @param sink   Sink vertex
     * @param parent Map to store parent relationships for path reconstruction
     * @return true if an augmenting path exists from source to sink, false otherwise
     */
    public static <Node, Double> boolean bfsMaxFlow(Graph<Node, Double> g, Node source, Node sink, Map<Node, Node> parent) {
        Set<Node> visited = new HashSet<>();
        Queue<Node> queue = new LinkedList<>();

        // Clear parent map to avoid conflicts from previous iterations
        parent.clear();

        queue.add(source);
        visited.add(source);
        parent.put(source, null);

        while (!queue.isEmpty()) {
            Node current = queue.poll();

            for (Node neighbor : g.adjVertices(current)) {
                Edge<Node, Double> edge = g.edge(current, neighbor);

                // Only follow edges with remaining capacity
                if (!visited.contains(neighbor) && edge != null && edge.getCapacity() > 0) {
                    visited.add(neighbor);
                    parent.put(neighbor, current);
                    queue.add(neighbor);

                    // Early termination if sink is reached
                    if (neighbor.equals(sink)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * Creates a deep copy (residual graph) of the original graph.
     * Used for max flow algorithms to maintain capacity state.
     *
     * @param original The original graph
     * @return A deep copy of the graph
     */
    public static <Node, Double> Graph<Node, Double> cloneGraph(Graph<Node, Double> original) {
        Graph<Node, Double> clone = new MapGraph<>(original.isDirected());

        // Copy all vertices
        for (Node vertex : original.vertices()) {
            clone.addVertex(vertex);
        }

        // Copy all edges with their properties
        for (Edge<Node, Double> edge : original.edges()) {
            clone.addEdge(
                    edge.getVOrig(),
                    edge.getVDest(),
                    edge.getCost(),
                    edge.getDistance(),
                    edge.getCapacity()
            );
        }

        return clone;
    }
}
