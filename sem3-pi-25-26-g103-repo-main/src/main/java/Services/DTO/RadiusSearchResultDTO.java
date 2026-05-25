package Services.DTO;

import Model.StationDistanceResult;
import Model.Trees.AVLTree;
import java.util.Map;

public class RadiusSearchResultDTO {

    private final AVLTree<StationDistanceResult> resultsTree;
    private final Map<String, Map<String, Integer>> summary;

    public RadiusSearchResultDTO(AVLTree<StationDistanceResult> resultsTree,
                                 Map<String, Map<String, Integer>> summary) {
        this.resultsTree = resultsTree;
        this.summary = summary;
    }

    public AVLTree<StationDistanceResult> getResultsTree() {
        return resultsTree;
    }

    public Map<String, Map<String, Integer>> getSummary() {
        return summary;
    }
}