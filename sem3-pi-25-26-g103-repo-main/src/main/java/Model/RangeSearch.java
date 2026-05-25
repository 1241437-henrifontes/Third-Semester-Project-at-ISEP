package Model;

import Model.Filters.RailwayStationSearchFilters;
import Model.Trees.KDnode;

import java.util.HashMap;

public class RangeSearch {
    public HashMap<String, RailwayStation> resultStations;

    public RangeSearch() {
        resultStations = new HashMap<>();
    }

    public HashMap<String, RailwayStation> getResultStations() {
        return resultStations;
    }

    public void search(KDnode node, Boolean level, RailwayStationSearchFilters filters, Range range) {
        resultStations = search(node, range, level, filters, resultStations);
    }

    private HashMap<String, RailwayStation> search(KDnode node, Range range, Boolean level, RailwayStationSearchFilters filters, HashMap<String, RailwayStation> resultStations) {
        if (node == null) return resultStations;

        if (range.contains(node.getLatitude(), node.getLongitude())) {
            for(RailwayStation rS : node.getStations()){
               if(filters.respectsFilters(rS)){
                   resultStations.put(rS.getName(), rS);
               }
            }
        }

        double cord = level ? node.getLongitude() : node.getLatitude();
        double max = level ? range.getMaxLon() : range.getMaxLat();
        double min = level ? range.getMinLon() : range.getMinLat();

        if (cord >= min && node.getLeft() != null) search(node.getLeft(), range, !level, filters, resultStations);
        if (cord <= max && node.getRight() != null) search(node.getRight(), range, !level, filters, resultStations);

        return resultStations;
    }
}
