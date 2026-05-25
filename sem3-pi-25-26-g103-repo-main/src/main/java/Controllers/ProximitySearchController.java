package Controllers;

import Repositories.TreeRepository;
import Model.StationDistance;
import Model.TimeZoneGroup;
import Model.Filters.StationFilter;
import Model.Trees.KDtree;
import Services.NearestNeighborSearcher;

import java.util.List;

/**
 * Controller for USEI09 - Proximity Search (Nearest-N with Filters).
 * Handles the business logic for finding nearest stations.
 */
public class ProximitySearchController {

    private final NearestNeighborSearcher searcher;
    private final TreeRepository treeRepository;

    public ProximitySearchController() {
        this.searcher = new NearestNeighborSearcher();
        this.treeRepository = TreeRepository.getInstance();
    }

    /**
     * Finds the N nearest stations to a given coordinate.
     *
     * @param targetLat Target latitude
     * @param targetLon Target longitude
     * @param n Number of nearest neighbors to find
     * @param filter Optional filter (can be null)
     * @return List of nearest stations with distances
     */
    public List<StationDistance> findNearestStations(double targetLat, double targetLon,
                                                      int n, StationFilter filter) {
        KDtree kdtree = treeRepository.getKdtree();

        if (kdtree == null || kdtree.getRoot() == null) {
            throw new IllegalStateException("KD-tree is not initialized. Please load stations first.");
        }

        return searcher.findNearestN(kdtree.getRoot(), targetLat, targetLon, n, filter);
    }

    /**
     * Finds the N nearest stations with detailed metrics.
     *
     * @param targetLat Target latitude
     * @param targetLon Target longitude
     * @param n Number of nearest neighbors
     * @param filter Optional filter
     * @return SearchResult with stations and performance metrics
     */
    public NearestNeighborSearcher.SearchResult findNearestStationsWithMetrics(
            double targetLat, double targetLon, int n, StationFilter filter) {

        KDtree kdtree = treeRepository.getKdtree();

        if (kdtree == null || kdtree.getRoot() == null) {
            throw new IllegalStateException("KD-tree is not initialized. Please load stations first.");
        }

        return searcher.findNearestNWithMetrics(kdtree.getRoot(), targetLat, targetLon, n, filter);
    }

    /**
     * Creates a filter based on timezone range.
     *
     * @param minTimeZone Minimum timezone
     * @param maxTimeZone Maximum timezone
     * @return StationFilter configured with timezone range
     */
    public StationFilter createTimeZoneFilter(TimeZoneGroup minTimeZone, TimeZoneGroup maxTimeZone) {
        return new StationFilter().withTimeZoneRange(minTimeZone, maxTimeZone);
    }

    /**
     * Creates a filter for specific station types.
     *
     * @param isCity Filter by city status (null = no filter)
     * @param isMainStation Filter by main station status (null = no filter)
     * @param isAirport Filter by airport status (null = no filter)
     * @return StationFilter configured with type filters
     */
    public StationFilter createTypeFilter(Boolean isCity, Boolean isMainStation, Boolean isAirport) {
        StationFilter filter = new StationFilter();
        if (isCity != null) {
            filter.withIsCity(isCity);
        }
        if (isMainStation != null) {
            filter.withIsMainStation(isMainStation);
        }
        if (isAirport != null) {
            filter.withIsAirport(isAirport);
        }
        return filter;
    }

    /**
     * Gets the number of nodes visited in the last search.
     * Useful for analyzing search efficiency.
     *
     * @return Number of nodes visited
     */
    public int getLastSearchNodesVisited() {
        return searcher.getNodesVisited();
    }
}

