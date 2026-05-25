package Model.Graph;

import Model.Pair;

import java.util.Objects;

/**
 * Represents a station (vertex) in the railway network graph.
 * Holds identifiers, display name, and both geographic and cartesian coordinates.
 */
public class Node {
    private String node_id;
    private String name;
    private Pair<Double, Double> geoCoordinates;
    private Pair<Double, Double> xyCoordinates;

    public Node(String node_id, String name, Pair<Double, Double> geoCoordinates, Pair<Double, Double> xyCoordinates) {
        this.node_id = node_id;
        this.name = name;
        this.geoCoordinates = geoCoordinates;
        this.xyCoordinates = xyCoordinates;
    }

    public String getNode_id() {
        return node_id;
    }

    public void setNode_id(String node_id) {
        this.node_id = node_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Pair<Double, Double> getGeoCoordinates() {
        return geoCoordinates;
    }

    public void setGeoCoordinates(Pair<Double, Double> geoCoordinates) {
        this.geoCoordinates = geoCoordinates;
    }

    public Pair<Double, Double> getXyCoordinates() {
        return xyCoordinates;
    }

    public void setXyCoordinates(Pair<Double, Double> xyCoordinates) {
        this.xyCoordinates = xyCoordinates;
    }

    @Override
    public String toString() {
        return "Node{" +
                "node_id='" + node_id + '\'' +
                ", name='" + name + '\'' +
                ", geoCoordinates=" + geoCoordinates +
                ", xyCoordinates=" + xyCoordinates +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return Objects.equals(node_id, node.node_id) && Objects.equals(name, node.name) && Objects.equals(geoCoordinates, node.geoCoordinates) && Objects.equals(xyCoordinates, node.xyCoordinates);
    }

    @Override
    public int hashCode() {
        return Objects.hash(node_id, name, geoCoordinates, xyCoordinates);
    }
}
