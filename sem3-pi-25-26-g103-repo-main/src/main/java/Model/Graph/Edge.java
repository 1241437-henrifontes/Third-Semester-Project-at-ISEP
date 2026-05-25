package Model.Graph;

import java.util.Objects;

/**
 * @param <Node> Vertex value type
 * @param <Double> Edge value type
 * @author DEI-ESINF & Henri Fontes
 */
/**
 * Graph edge connecting two vertices with associated attributes.
 * Stores cost (generic weight), distance (e.g., km), and capacity (e.g., trains/hour).
 *
 * @param <Node>   vertex type
 * @param <Double> edge weight type placeholder (kept for API compatibility)
 */
public class Edge<Node, Double> {
    final private Node vOrig;
    final private Node vDest;
    private double cost;
    private double distance;
    private int capacity;

    public Edge(Node vOrig, Node vDest, double cost, double distance, int capacity) {
        if ((vOrig == null) || (vDest == null)) throw new RuntimeException("Edge vertices cannot be null!");
        this.vOrig = vOrig;
        this.vDest = vDest;
        this.cost = cost;
        this.distance = distance;
        this.capacity = capacity;
    }

    public Node getVOrig() {
        return vOrig;
    }

    public Node getVDest() {
        return vDest;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    @Override
    public String toString() {
        return String.format("%s -> %s\ncost: %s", vOrig, vDest, cost);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        @SuppressWarnings("unchecked") Edge<Node, Double> edge = (Edge<Node, Double>) o;
        return  vOrig.equals(edge.vOrig) &&
                vDest.equals(edge.vDest);
    }

    @Override
    public int hashCode() {
        return Objects.hash(vOrig, vDest);
    }
}
