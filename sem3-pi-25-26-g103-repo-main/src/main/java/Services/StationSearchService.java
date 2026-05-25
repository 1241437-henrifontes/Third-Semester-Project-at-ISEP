package Services;

import Repositories.TreeRepository;
import Model.RailwayStation;
import Model.StationDistanceResult;
import Model.Trees.AVLTree;
import Model.Trees.KDnode;
import Model.Trees.KDtree;
import Utils.DistanceUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Service providing proximity queries over railway stations using the KD-tree.
 * - Allows searching stations within a given radius (haversine distance).
 * - Produces simple density summaries by country and city status.
 */
public class StationSearchService {

    private final KDtree stationTree;


    /**
     * Creates a new StationSearchService bound to the KD-tree from TreeRepository.
     *
     * @throws IllegalStateException if the KD-tree is not available
     */
    public StationSearchService() {
        this.stationTree = TreeRepository.getInstance().getKdtree();

        if (this.stationTree == null) {
            throw new IllegalStateException("Critical Error: KDtree not found in the Repository.");
        }
    }

    public AVLTree<StationDistanceResult> findStationsInRadius(double targetLat, double targetLon, double radiusKm) {
        AVLTree<StationDistanceResult> results = new AVLTree<>();
        if (stationTree.getRoot() == null) {
            System.out.println("WARNING: The KD-Tree is empty. Make sure you have run the 'Build KD-Tree' option (USEI07) first.");
            return results;
        }

        recursiveRadiusSearch(stationTree.getRoot(), targetLat, targetLon, radiusKm, results);
        return results;
    }

    private void recursiveRadiusSearch(KDnode node, double targetLat, double targetLon,
                                       double radiusKm, AVLTree<StationDistanceResult> results) {
        if (node == null) return;

        for (RailwayStation station : node.getStations()) {


            double distance = DistanceUtils.haversineDistance(targetLat, targetLon,
                    station.getLatitude(),
                    station.getLongitude());

            if (distance <= radiusKm) {
                results.insert(new StationDistanceResult(station, distance));
            }
        }

        int axis = node.getAxis();

        double nodeCoord = (axis == 0) ? node.getLatitude() : node.getLongitude();
        double targetCoord = (axis == 0) ? targetLat : targetLon;

        double axisDistance;
        if (axis == 0) {
            axisDistance = DistanceUtils.haversineDistance(targetLat, targetLon, node.getLatitude(), targetLon);
        } else {
            axisDistance = DistanceUtils.haversineDistance(targetLat, targetLon, targetLat, node.getLongitude());
        }

        KDnode nearChild = (targetCoord < nodeCoord) ? node.getLeft() : node.getRight();
        KDnode farChild = (targetCoord < nodeCoord) ? node.getRight() : node.getLeft();

        recursiveRadiusSearch(nearChild, targetLat, targetLon, radiusKm, results);

        if (axisDistance <= radiusKm) {
            recursiveRadiusSearch(farChild, targetLat, targetLon, radiusKm, results);
        }
    }

    public Map<String, Map<String, Integer>> generateDensitySummary(AVLTree<StationDistanceResult> results) {
        Map<String, Integer> countryCounts = new HashMap<>();
        Map<String, Integer> isCityCounts = new HashMap<>();

        if (results != null && !results.isEmpty()) {
            for (StationDistanceResult result : results.inOrder()) {
                RailwayStation station = result.getStation();

                String countryKey = (station.getCountry() != null) ? station.getCountry().toString() : "UNKNOWN";
                countryCounts.put(countryKey, countryCounts.getOrDefault(countryKey, 0) + 1);

                String cityStatus = station.isCity() ? "City" : "Not City";
                isCityCounts.put(cityStatus, isCityCounts.getOrDefault(cityStatus, 0) + 1);
            }
        }

        Map<String, Map<String, Integer>> summaries = new HashMap<>();
        summaries.put("byCountry", countryCounts);
        summaries.put("byCityStatus", isCityCounts);
        return summaries;
    }
}