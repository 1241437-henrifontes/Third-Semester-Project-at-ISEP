package Model.Trees;

import Model.RailwayStation;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class KDnode {


    protected Point2D.Double coords;
    protected List<RailwayStation> stations;

    protected KDnode left;
    protected KDnode right;
    protected int axis;

    public KDnode(double latitude, double longitude, List<RailwayStation> sameCoordStations, int axis) {

        this.coords = new Point2D.Double(longitude, latitude);
        this.axis = axis;


        this.stations = new ArrayList<>(sameCoordStations);
        this.stations.sort(Comparator.comparing(
                RailwayStation::getName,
                String.CASE_INSENSITIVE_ORDER
        ));
    }

    public double getLatitude() {
        return coords.y;
    }

    public double getLongitude() {
        return coords.x;
    }

    public Point2D.Double getCoords() {
        return coords;
    }

    public int getAxis() {
        return axis;
    }

    public List<RailwayStation> getStations() {
        return stations;
    }

    public KDnode getLeft() {
        return left;
    }

    public KDnode getRight() {
        return right;
    }

    public void setLeft(KDnode left) {
        this.left = left;
    }

    public void setRight(KDnode right) {
        this.right = right;
    }

    @Override
    public String toString() {
        return "KDnode{" +
                "coords=(" + getLongitude() + ", " + getLatitude() + ")" +
                ", axis=" + axis +
                ", stations=" + stations +
                '}';
    }
}
