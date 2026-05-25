package UI.Menu;

import java.util.ArrayList;
import java.util.List;

import UI.MaxFlowUI;
import UI.StationMeasuresUI;
import UI.RoutePlannerUI;
import UI.UpgradePlannerUI;
import UI.Utils.Utils;


/**
 * Top-level menu for the Belgium Railway Planner demo.
 * Provides entry points to several features (upgrade planner, infrastructure planner,
 * station hub score, route planner, and maximum flow).
 */
public class BelgiumRailwayPlanner implements Runnable {

    /**
     * Displays the Belgium Railway Planner menu and dispatches the selected option.
     */
    @Override
    public void run() {

        List<MenuItem> options = new ArrayList<>();

       options.add(new MenuItem("Upgrade Planner", new UpgradePlannerUI()));
       options.add(new MenuItem("Infrastructure Planner", new InfPlanner()));
       options.add(new MenuItem("Calculate Hubscore",new StationMeasuresUI()));
       options.add(new MenuItem("Route Planner", new RoutePlannerUI()));
       options.add(new MenuItem("Maximum Flow", new MaxFlowUI()));

        int option;
        do {
            option = Utils.showAndSelectIndex(
                    options,
                    "\n\n--- BELGIUM RAILWAY PLANNER --------------------------"
            );

            if (option >= 0 && option < options.size()) {
                options.get(option).run();
            }

        } while (option != -1);
    }
}
