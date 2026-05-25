package Controllers;

import Repositories.GraphRepository;
import Model.Graph.Graph;
import Model.Graph.Node;
import UI.Utils.GraphToDot;

/**
 * Controller responsible for generating visualization artifacts of the
 * Minimal Backbone Network (MBN).
 */
public class MBNController {

    /**
     * Retrieves the singleton GraphRepository instance.
     *
     * @return the application's GraphRepository
     */
    private GraphRepository getGraphRepository() {
        return GraphRepository.getInstance();
    }

    /**
     * Generates a DOT file representing the Minimal Backbone Network.
     * The file is written to the default output path managed by GraphToDot.
     */
    public void generateDot() {
        GraphRepository graphRepo = getGraphRepository();
        Graph<Node, Double> graph = graphRepo.getMinimalBackboneGraph();
        GraphToDot.generateDotFile(graph);
    }
}
