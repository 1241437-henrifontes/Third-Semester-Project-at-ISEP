package UI.Menu;

import UI.MBNUI;
import UI.Utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class InfPlanner implements Runnable {

    public void run() {
        List<MenuItem> options = new ArrayList<MenuItem>();
        options.add(new MenuItem("Minimal Backbone Network", new MBNUI()));
        int option = 0;
        do {
            option = Utils.showAndSelectIndex(options, "\n\n--- INFRASTRUCTURE PLANNER MENU --------------------------");
            if ((option >= 0) && (option < options.size())) {
                options.get(option).run();
            }
        } while (option != -1);
    }
}
