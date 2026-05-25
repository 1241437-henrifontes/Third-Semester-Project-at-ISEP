package UI.Menu;

import java.util.ArrayList;
import java.util.List;

import Repositories.TreeRepository;
import UI.ProximitySearchUI;
import UI.RangeSearchUI;
import UI.TimeZoneIndexUI;
import UI.Utils.Utils;

public class StationPlanner implements Runnable {

    public void run() {
        List<MenuItem> options = new ArrayList<MenuItem>();
        options.add(new MenuItem("Time-Zone Index and Windowed Queries", new TimeZoneIndexUI()));
        options.add(new MenuItem("Data Engineer Menu", new DataEngineer()));
        options.add(new MenuItem("Search KD-tree by a range", new RangeSearchUI()));
        options.add(new MenuItem("Proximity Search (Nearest-N with Filters)", new ProximitySearchUI()));
        int option = 0;
        do {
            option = Utils.showAndSelectIndex(options, "\n\n--- STATION PLANNER MENU --------------------------");

            if ((option >= 0) && (option < options.size())) {
                if (option == 2 && (TreeRepository.getInstance().getKdtree() == null || TreeRepository.getInstance().getKdtree().size() == 0)) {
                    System.out.println("\nKD-tree is empty. Please build it first.");
                } else {
                    options.get(option).run();
                }

            }
        } while (option != -1);
    }
}
