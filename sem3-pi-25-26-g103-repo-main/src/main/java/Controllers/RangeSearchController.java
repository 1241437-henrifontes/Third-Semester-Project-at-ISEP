package Controllers;

import Repositories.RangeSearchRepository;
import Repositories.TreeRepository;
import Model.Filters.RailwayStationSearchFilters;
import Model.Range;
import Model.RangeSearch;

public class RangeSearchController {

    public void searchRange(RailwayStationSearchFilters filters, Range range) {
        RangeSearch rangeSearch = new RangeSearch();
        rangeSearch.search(TreeRepository.getInstance().getKdtree().getRoot(),true, filters, range);
        getRangeSearchRepository().addRangeSearch(filters, rangeSearch, range);
    }

    public Boolean alreadyExists(RailwayStationSearchFilters filters, Range range) {
        return getRangeSearchRepository().rangeSearchExist(filters, range);
    }

    public RangeSearchRepository getRangeSearchRepository() {
        return RangeSearchRepository.getInstance();
    }
}
