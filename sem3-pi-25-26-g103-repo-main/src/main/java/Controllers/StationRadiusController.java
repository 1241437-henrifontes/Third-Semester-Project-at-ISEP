package Controllers;

import Model.StationDistanceResult;
import Model.Trees.AVLTree;
import Services.StationSearchService;
import Services.DTO.RadiusSearchResultDTO;
import java.util.Map;

public class StationRadiusController {

    private final StationSearchService searchService;

    public StationRadiusController() {
        this.searchService = new StationSearchService();
    }

    public RadiusSearchResultDTO findStationsInRadius(double lat, double lon, double radius) {
        AVLTree<StationDistanceResult> results = searchService.findStationsInRadius(lat, lon, radius);
        Map<String, Map<String, Integer>> summary = searchService.generateDensitySummary(results);

        return new RadiusSearchResultDTO(results, summary);
    }
}