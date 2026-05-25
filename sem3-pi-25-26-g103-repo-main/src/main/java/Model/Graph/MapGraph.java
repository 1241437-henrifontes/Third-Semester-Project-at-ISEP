package Model.Graph;

import java.util.*;

/**
 * @param <Node> Vertex value type
 * @param <Double> Edge value type
 * @author DEI-ESINF
 */
public class MapGraph<Node, Double> extends CommonGraph<Node, Double> {
    final private Map<Node, MapVertex<Node, Double>> mapVertices;  // all the Vertices of the graph

    // Constructs an empty graph (either undirected or directed)
    public MapGraph(boolean directed) {
        super(directed);
        mapVertices = new LinkedHashMap<>();
    }

    public MapGraph(Graph<Node, Double> g) {
        this(g.isDirected());
        copy(g, this);
    }

    @Override
    public boolean validVertex(Node vert) {
        return (mapVertices.get(vert) != null);
    }

    @Override
    public Collection<Node> adjVertices(Node vert) {
        return new ArrayList<>(mapVertices.get(vert).getAllAdjVertex());
    }

    @Override
    public Collection<Edge<Node, Double>> edges() {

        ArrayList<Edge<Node, Double>> le = new ArrayList<>(numEdges);

        for (MapVertex<Node, Double> mv : mapVertices.values())
            le.addAll(mv.getAllOutEdges());

        return le;
    }

    @Override
    public Edge<Node, Double> edge(Node vOrig, Node vDest) {

        if (!validVertex(vOrig) || !validVertex(vDest))
            return null;

        MapVertex<Node, Double> mv = mapVertices.get(vOrig);

        return mv.getEdge(vDest);
    }

    @Override
    public Edge<Node, Double> edge(int vOrigKey, int vDestKey) {
        Node vOrig = vertex(vOrigKey);
        Node vDest = vertex(vDestKey);

        return edge(vOrig, vDest);
    }

    @Override
    public int outDegree(Node vert) {

        if (!validVertex(vert))
            return -1;

        MapVertex<Node, Double> mv = mapVertices.get(vert);

        return mv.numAdjVertex();
    }

    @Override
    public int inDegree(Node vert) {

        if (!validVertex(vert))
            return -1;

        int degree = 0;
        for (Node otherVert : mapVertices.keySet())
            if (edge(otherVert, vert) != null)
                degree++;

        return degree;
    }

    @Override
    public Collection<Edge<Node, Double>> outgoingEdges(Node vert) {

        if (!validVertex(vert))
            return null;

        MapVertex<Node, Double> mv = mapVertices.get(vert);

        return mv.getAllOutEdges();
    }

    @Override
    public Collection<Edge<Node, Double>> incomingEdges(Node vert) {
        Collection<Edge<Node, Double>> incoming = new ArrayList<>();

        if (!validVertex(vert))
            return incoming;

        for (MapVertex<Node, Double> mv : mapVertices.values()) {
            for (Edge<Node, Double> edge : mv.getAllOutEdges()) {
                if (edge.getVDest().equals(vert)) {
                    incoming.add(edge);
                }
            }
        }

        return incoming;
    }

    @Override
    public boolean addVertex(Node vert) {

        if (vert == null) throw new RuntimeException("Vertices cannot be null!");
        if (validVertex(vert))
            return false;

        MapVertex<Node, Double> mv = new MapVertex<>(vert);
        vertices.add(vert);
        mapVertices.put(vert, mv);
        numVertex++;

        return true;
    }

    @Override
    public boolean addEdge(Node vOrig, Node vDest, double cost, double distance, int capacity) {

        if (vOrig == null || vDest == null) throw new RuntimeException("Vertices cannot be null!");
        if (edge(vOrig, vDest) != null)
            return false;

        if (!validVertex(vOrig))
            addVertex(vOrig);

        if (!validVertex(vDest))
            addVertex(vDest);

        MapVertex<Node, Double> mvo = mapVertices.get(vOrig);
        MapVertex<Node, Double> mvd = mapVertices.get(vDest);

        Edge<Node, Double> newEdge = new Edge<>(mvo.getElement(), mvd.getElement(), cost, distance, capacity);
        mvo.addAdjVert(mvd.getElement(), newEdge);
        numEdges++;

        //if graph is not direct insert another edge in the opposite direction
        if (!isDirected)
            // if vDest different vOrig
            if (edge(vDest, vOrig) == null) {
                Edge<Node, Double> otherEdge = new Edge<>(mvd.getElement(), mvo.getElement(), cost, distance, capacity);
                mvd.addAdjVert(mvo.getElement(), otherEdge);
                numEdges++;
            }

        return true;
    }

    @Override
    public boolean removeVertex(Node vert) {

        if (vert == null) throw new RuntimeException("Vertices cannot be null!");
        if (!validVertex(vert))
            return false;

        //remove all edges that point to vert
        for (Edge<Node, Double> edge : incomingEdges(vert)) {
            removeEdge(edge.getVOrig(), vert);
        }

        MapVertex<Node, Double> mv = mapVertices.get(vert);

        //The edges that live from vert are removed with the vertex
        numEdges -= mv.numAdjVertex();
        mapVertices.remove(vert);
        vertices.remove(vert);

        numVertex--;

        return true;
    }

    @Override
    public boolean removeEdge(Node vOrig, Node vDest) {

        if (vOrig == null || vDest == null) throw new RuntimeException("Vertices cannot be null!");
        if (!validVertex(vOrig) || !validVertex(vDest))
            return false;

        Edge<Node, Double> edge = edge(vOrig, vDest);

        if (edge == null)
            return false;

        MapVertex<Node, Double> mvo = mapVertices.get(vOrig);

        mvo.remAdjVert(vDest);
        numEdges--;

        //if the graph is not directed
        if (!isDirected) {
            edge = edge(vDest, vOrig);
            if (edge != null) {
                MapVertex<Node, Double> mvd = mapVertices.get(vDest);
                mvd.remAdjVert(vOrig);
                numEdges--;
            }
        }
        return true;
    }

    //Returns a clone of the graph
    @Override
    public MapGraph<Node, Double> clone() {

        MapGraph<Node, Double> g = new MapGraph<>(this.isDirected);

        copy(this, g);

        return g;
    }

    //string representation
    @Override
    public String toString() {
        StringBuilder s;
        if (numVertex == 0) {
            s = new StringBuilder("\nGraph not defined!!");
        } else {
            s = new StringBuilder("Graph: " + numVertex + " vertices, " + numEdges + " edges\n");
            for (MapVertex<Node, Double> mv : mapVertices.values())
                s.append(mv).append("\n");
        }
        return s.toString();
    }
}
