package Services;

import Model.Graph.Graph;
import Model.Graph.Node;
import java.util.List;

public class UpgradePlanner {
    public List<Node> Upgrades(Graph<Node, Double> graph) {

        List<Node> order = orderAndCycles.topologicalOrder(graph);
        if (order!= null){
            return order;
        }

        return orderAndCycles.findFirstCycle(graph);



    }
}
