package Model.Trees;

import Repositories.TreeRepository;
import Model.Wrappers.RailwayStationLatitude;
import Model.Wrappers.RailwayStationLongitude;
import Model.RailwayStation;

import java.util.*;

public class KDtree {

    private KDnode root;

    public KDnode getRoot() {
        return root;
    }

    public void buildTree() {
        TreeRepository stationRepo = TreeRepository.getInstance();

        List<RailwayStation> sortedByLat = new ArrayList<>();
        for (RailwayStationLatitude n : stationRepo.getLatitudeTree().inOrder()) {
            sortedByLat.addAll(n.getStations());
        }

        if (sortedByLat.isEmpty()) {
            root = null;
            return;
        }

        List<RailwayStation> sortedByLon = new ArrayList<>();
        for (RailwayStationLongitude n : stationRepo.getLongitudeTree().inOrder()) {
            sortedByLon.addAll(n.getStations());
        }

        root = buildRec(sortedByLat, sortedByLon, 0);
    }

    public void buildTreeFromStationList(List<RailwayStation> stations) {

        if (stations == null || stations.isEmpty()) {
            this.root = null;
            return;
        }


        List<RailwayStation> byLat = new ArrayList<RailwayStation>(stations);
        byLat.sort(Comparator.comparingDouble(RailwayStation::getLatitude));


        List<RailwayStation> byLon = new ArrayList<RailwayStation>(stations);
        byLon.sort(Comparator.comparingDouble(RailwayStation::getLongitude));


        this.root = buildRec(byLat, byLon, 0);
    }


    private KDnode buildRec(List<RailwayStation> byLat,
                            List<RailwayStation> byLon,
                            int depth) {

        if (byLat.isEmpty()) return null;

        int axis = depth % 2;
        List<RailwayStation> primary = (axis == 0 ? byLat : byLon);

        int medianIndex = primary.size() / 2;
        RailwayStation median = primary.get(medianIndex);

        double mLat = median.getLatitude();
        double mLon = median.getLongitude();

        List<RailwayStation> sameCoords = new ArrayList<>();
        for (RailwayStation s : primary) {
            if (sameCoordinates(s, mLat, mLon)) sameCoords.add(s);
        }

        List<RailwayStation> leftByLat = new ArrayList<>();
        List<RailwayStation> rightByLat = new ArrayList<>();
        for (RailwayStation s : byLat) {
            if (sameCoordinates(s, mLat, mLon)) continue;
            if (isLeftOf(s, mLat, mLon, axis)) leftByLat.add(s);
            else rightByLat.add(s);
        }

        List<RailwayStation> leftByLon = new ArrayList<>();
        List<RailwayStation> rightByLon = new ArrayList<>();
        for (RailwayStation s : byLon) {
            if (sameCoordinates(s, mLat, mLon)) continue;
            if (isLeftOf(s, mLat, mLon, axis)) leftByLon.add(s);
            else rightByLon.add(s);
        }

        KDnode node = new KDnode(mLat, mLon, sameCoords, axis);

        node.setLeft(buildRec(leftByLat, leftByLon, depth + 1));
        node.setRight(buildRec(rightByLat, rightByLon, depth + 1));

        return node;
    }

    private boolean sameCoordinates(RailwayStation s, double lat, double lon) {
        return Double.compare(s.getLatitude(), lat) == 0 &&
                Double.compare(s.getLongitude(), lon) == 0;
    }

    private boolean isLeftOf(RailwayStation s, double mLat, double mLon, int axis) {
        if (axis == 0) {
            int cmpLat = Double.compare(s.getLatitude(), mLat);
            if (cmpLat < 0) return true;
            if (cmpLat > 0) return false;
            return Double.compare(s.getLongitude(), mLon) < 0;
        } else {
            int cmpLon = Double.compare(s.getLongitude(), mLon);
            if (cmpLon < 0) return true;
            if (cmpLon > 0) return false;
            return Double.compare(s.getLatitude(), mLat) < 0;
        }
    }

    public int size(){
        return sizeRec(root);
    }

    private int sizeRec(KDnode node){
        if (node == null) return 0;
        return 1 + sizeRec(node.getLeft()) + sizeRec(node.getRight());
    }

    public int height(){
        return heightRec(root);
    }

    private int heightRec(KDnode node){
        if (node == null) return 0;
        return 1 + Math.max(heightRec(node.getLeft()), heightRec(node.getRight()));
    }

    public List<Integer> bucketSizes() {
        List<Integer> sizes = new ArrayList<>();
        collectBucketSizes(root, sizes);
        return sizes;
    }

    private void collectBucketSizes(KDnode node, List<Integer> sizes) {
        if (node == null) return;
        sizes.add(node.getStations().size());
        collectBucketSizes(node.getLeft(), sizes);
        collectBucketSizes(node.getRight(), sizes);
    }

    public Set<Integer> distinctBucketSizes() {
        return new HashSet<>(bucketSizes());
    }

    public void inspectTree() {
        System.out.println("KD-tree size  " + size());
        System.out.println("KD-tree height  " + height());
        System.out.println("Distinct bucket sizes " + distinctBucketSizes());
    }
}
