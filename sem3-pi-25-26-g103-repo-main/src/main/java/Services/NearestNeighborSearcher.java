package Services;

import Model.RailwayStation;
import Model.StationDistance;
import Model.Filters.StationFilter;
import Model.Trees.KDnode;
import Utils.DistanceUtils;

import java.util.*;

/**
 * Service for finding the N nearest railway stations to a target coordinate.
 * Uses a KD-tree with pruning optimization for efficient search.
 *
 * Temporal Complexity Analysis:
 * - Best case: O(log k + n) where k is total stations and n is requested neighbors
 * - Average case: O(log k + n)
 * - Worst case: O(k) when little pruning is possible (e.g., all stations in similar distance)
 *
 * The pruning technique significantly reduces the number of nodes visited compared to linear search O(k).
 */
public class NearestNeighborSearcher {

    private int nodesVisited;

    /**
     * Finds the N nearest stations to a target coordinate.
     *
     * @param root The root of the KD-tree
     * @param targetLat Target latitude
     * @param targetLon Target longitude
     * @param n Number of nearest neighbors to find
     * @param filter Optional filter for stations (can be null)
     * @return List of nearest stations sorted by distance (ascending) and name (descending for ties)
     */
    public List<StationDistance> findNearestN(KDnode root, double targetLat, double targetLon,
                                              int n, StationFilter filter) {
        if (root == null) {
            return new ArrayList<>();
        }
        if (n <= 0) {
            throw new IllegalArgumentException("N must be positive");
        }

        // Reset metrics
        nodesVisited = 0;

        // Max-heap to keep track of the N closest stations
        PriorityQueue<StationDistance> nearestSoFar = new PriorityQueue<>(
                new Comparator<StationDistance>() {
                    @Override
                    public int compare(StationDistance a, StationDistance b) {
                        int cmp = Double.compare(b.getDistance(), a.getDistance());
                        if (cmp != 0) return cmp;
                        return a.getStation().getName().compareToIgnoreCase(b.getStation().getName());
                    }
                }
        );


        // Perform recursive search with pruning
        searchWithPruning(root, targetLat, targetLon, n, filter, nearestSoFar);

        // Convert to list and sort properly
        List<StationDistance> result = new ArrayList<>(nearestSoFar);
        result.sort(Comparator.naturalOrder());

        // Check for insufficient stations
        if (result.size() < n) {
            System.out.printf("Warning: Only %d station(s) found matching the criteria (requested %d).%n",
                    result.size(), n);
        }

        return result;
    }

    /**
     * Recursive search with pruning optimization.
     *
     * @param node Current node in the KD-tree
     * @param targetLat Target latitude
     * @param targetLon Target longitude
     * @param n Number of neighbors to find
     * @param filter Optional filter
     * @param nearestSoFar Max-heap containing the N nearest stations found so far
     */
    private void searchWithPruning(KDnode node, double targetLat, double targetLon,
                                   int n, StationFilter filter,
                                   PriorityQueue<StationDistance> nearestSoFar) {
        if (node == null) {
            return;
        }

        nodesVisited++;

        // Check all stations at this node
        for (RailwayStation station : node.getStations()) {
            // Apply filter if provided
            if (filter != null && !filter.matches(station)) {
                continue;
            }

            // Calculate distance using Haversine
            double distance = DistanceUtils.haversineDistance(
                    targetLat, targetLon,
                    station.getLatitude(), station.getLongitude()
            );

            StationDistance sd = new StationDistance(station, distance);

            // If we have fewer than N stations, just add it
            if (nearestSoFar.size() < n) {
                nearestSoFar.offer(sd);
            }
            // If this station is closer than the farthest in our collection, replace it
            else {
                StationDistance farthest = nearestSoFar.peek();
                if (farthest != null && distance < farthest.getDistance()) {
                    nearestSoFar.poll();
                    nearestSoFar.offer(sd);
                }
            }
        }

        // Determine which subtree to explore first
        boolean targetIsLeft;
        if (node.getAxis() == 0) {
            targetIsLeft = targetLat < node.getLatitude();
        } else {
            targetIsLeft = targetLon < node.getLongitude();
        }

        KDnode nearSubtree = targetIsLeft ? node.getLeft() : node.getRight();
        KDnode farSubtree = targetIsLeft ? node.getRight() : node.getLeft();

        // Always explore the near subtree
        searchWithPruning(nearSubtree, targetLat, targetLon, n, filter, nearestSoFar);

        // Pruning: only explore far subtree if necessary
        if (shouldExploreFarSubtree(node, targetLat, targetLon, n, nearestSoFar)) {
            searchWithPruning(farSubtree, targetLat, targetLon, n, filter, nearestSoFar);
        }
    }

    /**
     * Determines if we should explore the far subtree using precise Haversine distance.
     * This method calculates the actual distance to the splitting plane to improve pruning accuracy.
     *
     * @param node Current node
     * @param targetLat Target latitude
     * @param targetLon Target longitude
     * @param n Number of neighbors needed
     * @param nearestSoFar Current nearest stations
     * @return true if we should explore the far subtree, false if we can prune it
     */
    private boolean shouldExploreFarSubtree(KDnode node, double targetLat, double targetLon,
                                            int n, PriorityQueue<StationDistance> nearestSoFar) {
        // If we don't have N stations yet, we must explore
        if (nearestSoFar.size() < n) {
            return true;
        }

        // Check if peek() returns null (should not happen, but safety check)
        StationDistance farthest = nearestSoFar.peek();
        if (farthest == null) {
            return true;
        }

        // Calculate the closest point on the splitting plane
        double planeLat, planeLon;
        if (node.getAxis() == 0) {
            planeLat = node.getLatitude();
            planeLon = targetLon;
        } else {
            planeLat = targetLat;
            planeLon = node.getLongitude();
        }

        // Calculate precise Haversine distance to the splitting plane
        double distanceToPlaneKm = DistanceUtils.haversineDistance(
                targetLat, targetLon,
                planeLat, planeLon
        );

        // Only explore the far subtree if the distance to the plane is less than or equal to
        // the distance to our farthest neighbor
        return distanceToPlaneKm <= farthest.getDistance();
    }

    /**
     * Checks if there are enough stations matching the filter criteria.
     *
     * @param root The root of the KD-tree
     * @param filter Optional filter
     * @param minRequired Minimum number of stations required
     * @return true if there are at least minRequired stations matching the criteria
     */
    public boolean hasEnoughStations(KDnode root, StationFilter filter, int minRequired) {
        return countMatchingStations(root, filter) >= minRequired;
    }

    /**
     * Counts the total number of stations matching the filter criteria.
     *
     * @param node Current node
     * @param filter Optional filter
     * @return Number of matching stations in this subtree
     */
    private int countMatchingStations(KDnode node, StationFilter filter) {
        if (node == null) {
            return 0;
        }

        int count = 0;
        for (RailwayStation station : node.getStations()) {
            if (filter == null || filter.matches(station)) {
                count++;
            }
        }

        return count +
                countMatchingStations(node.getLeft(), filter) +
                countMatchingStations(node.getRight(), filter);
    }

    /**
     * Returns the number of KD-tree nodes visited during the last search.
     * Useful for performance analysis and pruning effectiveness.
     *
     * @return number of visited nodes in the most recent query
     */
    public int getNodesVisited() {
        return nodesVisited;
    }

    /**
     * Finds stations and provides detailed search metrics.
     *
     * @param root The root of the KD-tree
     * @param targetLat Target latitude
     * @param targetLon Target longitude
     * @param n Number of nearest neighbors
     * @param filter Optional filter
     * @return SearchResult containing stations and metrics
     */
    public SearchResult findNearestNWithMetrics(KDnode root, double targetLat, double targetLon,
                                                int n, StationFilter filter) {
        long startTime = System.nanoTime();
        List<StationDistance> results = findNearestN(root, targetLat, targetLon, n, filter);
        long endTime = System.nanoTime();

        double durationMs = (endTime - startTime) / 1_000_000.0;

        return new SearchResult(results, nodesVisited, durationMs);
    }

    /**
     * Inner class to hold search results with metrics.
     */
    public static class SearchResult {
        private final List<StationDistance> stations;
        private final int nodesVisited;
        private final double durationMs;

        public SearchResult(List<StationDistance> stations, int nodesVisited, double durationMs) {
            this.stations = stations;
            this.nodesVisited = nodesVisited;
            this.durationMs = durationMs;
        }

        public List<StationDistance> getStations() {
            return stations;
        }

        public int getNodesVisited() {
            return nodesVisited;
        }

        public double getDurationMs() {
            return durationMs;
        }

        @Override
        public String toString() {
            return String.format("SearchResult{found=%d stations, visited=%d nodes, duration=%.3f ms}",
                    stations.size(), nodesVisited, durationMs);
        }
    }
}