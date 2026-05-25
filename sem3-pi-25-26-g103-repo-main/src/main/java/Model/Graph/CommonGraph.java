package Model.Graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * @param <Node> Vertex value type
 * @param <Double> Edge value type
 * @author DEI-ISEP
 */
public abstract class CommonGraph<Node, Double> implements Graph<Node, Double> {
    protected int numVertex;
    protected int numEdges;
    protected final boolean isDirected;
    protected ArrayList<Node> vertices;

    public CommonGraph(boolean directed) {
        numVertex = 0;
        numEdges = 0;
        isDirected = directed;
        vertices = new ArrayList<>();
    }

    @Override
    public boolean isDirected() {
        return isDirected;
    }

    @Override
    public int numVertices() {
        return numVertex;
    }

    @Override
    public ArrayList<Node> vertices() {
        return new ArrayList<>(vertices);
    }

    @Override
    public boolean validVertex(Node vert) {
        return vertices.contains(vert);
    }

    @Override
    public int key(Node vert) {
        return vertices.indexOf(vert);
    }

    @Override
    public Node vertex(int key) {
        if ((key < 0) || (key >= numVertex)) return null;
        return vertices.get(key);
    }

    @Override
    public Node vertex(Predicate<Node> p) {
        for (Node v : vertices) {
            if (p.test(v)) return v;
        }
        return null;
    }

    @Override
    public int numEdges() {
        return numEdges;
    }

    /**
     * Copy graph from to graph to
     *
     * @param from graph from which to copy
     * @param to   graph for which to copy
     */
    protected void copy(Graph<Node, Double> from, Graph<Node, Double> to) {
        //insert all vertices
        for (Node v : from.vertices()) {
            to.addVertex(v);
        }

        //insert all edges
        for (Edge<Node, Double> e : from.edges()) {
            to.addEdge(e.getVOrig(), e.getVDest(), e.getCost(), e.getDistance(), e.getCapacity());
        }
    }

    /**
     * equals implementation compares graphs, independently of their representation
     *
     * @param otherObj the other graph to test for equality
     * @return true if both objects represent the same graph
     */
    @Override
    public boolean equals(Object otherObj) {

        if (this == otherObj)
            return true;

        if (!(otherObj instanceof Graph<?, ?>))
            return false;

        @SuppressWarnings("unchecked") Graph<Node, Double> otherGraph = (Graph<Node, Double>) otherObj;

        if (numVertex != otherGraph.numVertices() || numEdges != otherGraph.numEdges() || isDirected() != otherGraph.isDirected())
            return false;

        // graph must have same vertices
        Collection<Node> tvc = this.vertices();
        tvc.removeAll(otherGraph.vertices());
        if (!tvc.isEmpty()) return false;

        // graph must have same edges
        Collection<Edge<Node, Double>> tec = this.edges();
        tec.removeAll(otherGraph.edges());
        return (tec.isEmpty());
    }

    public abstract Graph<Node, Double> clone();

    @Override
    public int hashCode() {
        return Objects.hash(numVertex, numEdges, isDirected, vertices);
    }
}
