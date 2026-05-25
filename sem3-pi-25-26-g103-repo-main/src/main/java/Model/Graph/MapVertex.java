package Model.Graph;

import java.util.*;

/**
 * Internal vertex wrapper used by MapGraph to keep adjacency and outgoing edges.
 *
 * @param <Node>   vertex element type
 * @param <Double> edge value type parameter (for compatibility with Edge)
 */
public class MapVertex<Node, Double> {
    final private Node element;
    final private Map<Node, Edge<Node, Double>> outVertex;

    public MapVertex(Node vert) {
        if (vert == null) throw new RuntimeException("Vertice information cannot be null!");
        element = vert;
        outVertex = new LinkedHashMap<>();
    }

    public Node getElement() {
        return element;
    }

    public void addAdjVert(Node vAdj, Edge<Node, Double> edge) {
        outVertex.put(vAdj, edge);
    }

    public void remAdjVert(Node vAdj) {
        outVertex.remove(vAdj);
    }

    public Edge<Node, Double> getEdge(Node vAdj) {
        return outVertex.get(vAdj);
    }

    public int numAdjVertex() {
        return outVertex.size();
    }

    public Collection<Node> getAllAdjVertex() {
        return new ArrayList<>(outVertex.keySet());
    }

    public Collection<Edge<Node, Double>> getAllOutEdges() {
        return new ArrayList<>(outVertex.values());
    }

    @Override
    public String toString() {
        StringBuilder st = new StringBuilder(element + ": \n");
        if (!outVertex.isEmpty())
            for (Node vert : outVertex.keySet())
                st.append(outVertex.get(vert));

        return st.toString();
    }
}
