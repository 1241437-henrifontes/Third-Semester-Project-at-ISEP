package Repositories;

import Model.Filters.RailwayStationSearchFilters;
import Model.Range;
import Model.RangeSearch;

import java.util.HashMap;

public class RangeSearchRepository {
    private static RangeSearchRepository instance = new RangeSearchRepository();
    private HashMap<Range, HashMap<RailwayStationSearchFilters, RangeSearch>> rangeSearchMap;

    private RangeSearchRepository() {
        rangeSearchMap = new HashMap<>();
    }

    public static RangeSearchRepository getInstance() {
        return instance;
    }

    public Boolean rangeSearchExist(RailwayStationSearchFilters filters, Range range) {
        return rangeSearchMap.containsKey(range) && rangeSearchMap.get(range).containsKey(filters);
    }

    public void addRangeSearch(RailwayStationSearchFilters filters, RangeSearch rangeSearch, Range range) {
        if (!rangeSearchMap.containsKey(range)) {
            rangeSearchMap.put(range, new HashMap<>());
        }
        rangeSearchMap.get(range).put(filters, rangeSearch);
    }

    public HashMap<Range, HashMap<RailwayStationSearchFilters, RangeSearch>>getRangeSearchMap() {
        return rangeSearchMap;
    }
}
