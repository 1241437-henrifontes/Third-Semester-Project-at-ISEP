package Model.Graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * @author DEI-ISEP
 */
public class MatrixGraph<Node, Double> extends CommonGraph<Node, Double> {

    public static final int INITIAL_CAPACITY = 10;
    public static final float RESIZE_FACTOR = 1.5F;

    Edge<Node, Double>[][] edgeMatrix;
    
    @SuppressWarnings("unchecked")
    public MatrixGraph(boolean directed, int initialCapacity) {
        super(directed);
        edgeMatrix = (Edge<Node, Double>[][])( new Edge<?, ?>[initialCapacity][initialCapacity]);
    }

    public MatrixGraph(boolean directed) {
        this(directed, INITIAL_CAPACITY);
    }

    public MatrixGraph(Graph<Node, Double> g) {
        this(g.isDirected(), g.numVertices());
        copy(g, this);
    }

    public MatrixGraph(boolean directed, ArrayList<Node> vs, Integer[][] m) {
        this(directed, vs.size());
        numVertex = vs.size();
        vertices = new ArrayList<>(vs);
        for (int i = 0 ; i < numVertex ; i++)
            for (int j = 0 ; j < numVertex ; j++)
                if (j != i && m[i][j] != null)
                    addEdge(vertices.get(i), vertices.get(j), m[i][j], 0.0, 0);
    }

    @Override
    public Collection<Node> adjVertices(Node vert) {
        int index = key(vert);
        if (index == -1)
            return null;

        ArrayList<Node> outVertices = new ArrayList<>();
        for (int i = 0; i < numVertex; i++)
            if (edgeMatrix[index][i] != null)
                outVertices.add(vertices.get(i));
        return outVertices;
    }

    @Override
    public Collection<Edge<Node, Double>> edges() {
        ArrayList<Edge<Node, Double>> edges = new ArrayList<>();

        for (int i = 0; i < numVertex; i++)
            for (int j = 0; j < numVertex; j++)
                if (edgeMatrix[i][j] != null)
                    edges.add(edgeMatrix[i][j]);

        return edges;
    }

    @Override
    public Edge<Node, Double> edge(Node vOrig, Node vDest) {
        int vOrigKey = key(vOrig);
        int vDestKey = key(vDest);

        if ((vOrigKey < 0) || (vDestKey < 0))
            return null;

        return edgeMatrix[vOrigKey][vDestKey];
    }

    @Override
    public Edge<Node, Double> edge(int vOrigKey, int vDestKey) {
        if (vOrigKey >= numVertex && vDestKey >= numVertex)
            return null;
        return edgeMatrix[vOrigKey][vDestKey];
    }

    @Override
    public int outDegree(Node vert) {
        int vertKey = key(vert);
        if (vertKey == -1)
            return -1;

        int edgeCount = 0;
        for (int i = 0; i < numVertex; i++)
            if (edgeMatrix[vertKey][i] != null)
                edgeCount++;
        return edgeCount;
    }

    @Override
    public int inDegree(Node vert) {
        int vertKey = key(vert);
        if (vertKey == -1)
            return -1;

        int edgeCount = 0;
        for (int i = 0; i < numVertex; i++)
            if (edgeMatrix[i][vertKey] != null)
                edgeCount++;
        return edgeCount;
    }

    @Override
    public Collection<Edge<Node, Double>> outgoingEdges(Node vert) {
        Collection<Edge<Node, Double>> ce = new ArrayList<>();
        int vertKey = key(vert);
        if (vertKey == -1)
            return ce;

        for (int i = 0; i < numVertex; i++)
            if (edgeMatrix[vertKey][i] != null)
                ce.add(edgeMatrix[vertKey][i]);
        return ce;
    }


    @Override
    public Collection<Edge<Node, Double>> incomingEdges(Node vert) {
        Collection <Edge<Node, Double>> ce = new ArrayList<>();
        int vertKey = key(vert);
        if (vertKey == -1)
            return ce;

        for (int i = 0; i < numVertex; i++)
            if (edgeMatrix[i][vertKey] != null)
                ce.add(edgeMatrix[i][vertKey]);
        return ce;
    }

    @Override
    public boolean addVertex(Node vert) {
        int vertKey = key(vert);
        if (vertKey != -1)
            return false;

        vertices.add(vert);
        numVertex++;
        resizeMatrix();
        return true;
    }

    /**
     * Resizes the matrix when a new vertex increases the length of ArrayList
     */
    private void resizeMatrix() {
        if(edgeMatrix.length < numVertex){
            int newSize = (int) (edgeMatrix.length * RESIZE_FACTOR);

            @SuppressWarnings("unchecked")
            Edge<Node, Double>[][] temp = (Edge<Node, Double>[][]) new Edge<?, ?> [newSize][newSize];
            for (int i = 0; i < edgeMatrix.length; i++)
                temp[i] = Arrays.copyOf(edgeMatrix[i], newSize);
            edgeMatrix = temp;
        }
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

        int vOrigKey = key(vOrig);
        int vDestKey = key(vDest);

        edgeMatrix[vOrigKey][vDestKey] = new Edge<>(vOrig, vDest, cost, distance, capacity);
        numEdges++;
        if (!isDirected) {
            edgeMatrix[vDestKey][vOrigKey] = new Edge<>(vDest, vOrig, cost, distance, capacity);
            numEdges++;
        }
        return true;
    }

    @Override
    public boolean removeVertex(Node vert) {
        int vertKey = key(vert);
        if (vertKey == -1)
            return false;

        // first let's remove edges from the vertex
        for (int i = 0; i < numVertex; i++)
            removeEdge(vertKey,i);
        if (isDirected) {
            // first let's remove edges to the vertex
            for (int i = 0; i < numVertex; i++)
                removeEdge(i, vertKey);
        }

        // to remove shifts left all vertices after the one removed
        // It is necessary to collapse the edge matrix        
        for (int i = vertKey; i < numVertex - 1; i++) {
            if (numVertex >= 0) System.arraycopy(edgeMatrix[i + 1], 0, edgeMatrix[i], 0, numVertex);
        }
        for (int i = vertKey; i < numVertex - 1; i++) {
            for (int j = 0; j < numVertex; j++) {
                edgeMatrix[j][i] = edgeMatrix[j][i + 1];
            }
        }
        for (int j = 0; j < numVertex; j++) {
            edgeMatrix[j][numVertex - 1] = null;
            edgeMatrix[numVertex - 1][j] = null;
        }

        vertices.remove(vert);
        numVertex--;
        return true;
    }

    private void removeEdge(int vOrigKey, int vDestKey) {
        if (edgeMatrix[vOrigKey][vDestKey] != null) {
            edgeMatrix[vOrigKey][vDestKey] = null;
            numEdges--;
        }
        if (!isDirected && (edgeMatrix[vDestKey][vOrigKey] != null)) {
            edgeMatrix[vDestKey][vOrigKey] = null;
            numEdges--;
        }
    }

    @Override
    public boolean removeEdge(Node vOrig, Node vDest) {
        int vOrigKey = key(vOrig);
        int vDestKey = key(vDest);

        if ((vOrigKey < 0) || (vDestKey < 0) || (edgeMatrix[vOrigKey][vDestKey] == null))
            return false;

        removeEdge(vOrigKey,vDestKey);
        return true;
    }

    @Override
    public MatrixGraph<Node, Double> clone() {
        MatrixGraph<Node, Double> g = new MatrixGraph<>(this.isDirected, this.edgeMatrix.length);

        copy(this,g);

        return g;
    }

    /**
     * Returns a string representation of the graph.
     * Matrix only represents the existence of Edge
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("Vertices:\n");
        for (int i = 0 ; i < numVertex ; i++)
            sb.append(vertices.get(i)).append("\n");

        sb.append("\nMatrix:\n");

        sb.append("  ");
        for (int i = 0 ; i < numVertex ; i++)
        {
            sb.append(" |  ").append(i).append(" ");
        }
        sb.append("\n");

        // aligned only when vertices < 10
        for (int i = 0 ; i < numVertex ; i++)
        {
            sb.append(" ").append(i).append(" ");
            for (int j = 0 ; j < numVertex ; j++)
                if(edgeMatrix[i][j] != null)
                    sb.append("|  X  ");
                else
                    sb.append("|     ");
            sb.append("\n");
        }

        sb.append("\nEdges:\n");

        for (int i = 0; i < numVertex ; i++)
            for (int j = 0 ; j < numVertex; j++)
                if (edgeMatrix[i][j] != null)
                    sb.append("From ").append(i).append(" to ").append(j).append("-> ").append(edgeMatrix[i][j]).append("\n");

        sb.append("\n");

        return sb.toString();
    }
}
