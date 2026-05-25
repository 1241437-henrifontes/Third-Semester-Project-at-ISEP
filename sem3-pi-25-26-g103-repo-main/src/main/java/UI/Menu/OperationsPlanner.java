package UI.Menu;

import UI.StationRadiusUI;
import UI.Utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class OperationsPlanner implements Runnable {
    public void run() {
        List<MenuItem> options = new ArrayList<>();

        options.add(new MenuItem("Radius Search", new StationRadiusUI()));

        int option;

        do {
            option = Utils.showAndSelectIndex(
                    options,
                    "\n\n--- OPERATIONS PLANNER MENU --------------------------"
            );

            if (option >= 0 && option < options.size()) {
                options.get(option).run();
            }

        } while (option != -1);
    }

}
