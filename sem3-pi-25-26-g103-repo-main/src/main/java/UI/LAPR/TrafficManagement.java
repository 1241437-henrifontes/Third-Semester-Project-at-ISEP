package UI.LAPR;

import Repositories.TreeRepository;
import UI.Menu.MenuItem;
import UI.Utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class TrafficManagement implements Runnable{
    public void run() {
        List<MenuItem> options = new ArrayList<MenuItem>();
        options.add(new MenuItem("Route Planner", new RoutePlannerLaprUI()));
        options.add(new MenuItem("Train Assembly", new TrainAssemblyUI()));
        options.add(new MenuItem("Scheduler", new SchedulerUI()));
        int option = 0;
        do {
            option = Utils.showAndSelectIndex(options, "\n\n--- TRAFFIC MANAGEMENT MENU --------------------------");

            if ((option >= 0) && (option < options.size())) {
                options.get(option).run();
            }
        } while (option != -1);
    }
}
