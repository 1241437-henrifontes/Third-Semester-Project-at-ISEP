package UI.Menu;

import UI.LAPR.TrafficManagement;
import UI.Utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Application main menu.
 * <p>
 * Provides access to the Station Storage Manager and Traffic Dispatcher menus.
 */
public class MainMenuUI implements Runnable {

    /**
     * Creates a new MainMenuUI instance.
     */
    public MainMenuUI() {
    }

    /**
     * Displays the main menu and runs the selected submenu.
     * The loop exits when the user cancels the menu (returns -1).
     */
    public void run() {
        List<MenuItem> options = new ArrayList<MenuItem>();
        options.add(new MenuItem("Station Storage Manager", new StationStorageManagerMenu()));
        options.add(new MenuItem("Traffic Dispatcher", new TrafficDispatcherMenu()));
        options.add(new MenuItem("Station Planner", new StationPlanner()));
        options.add(new MenuItem("Operations Planner",new OperationsPlanner()));
        options.add(new MenuItem( "Belgium Railway Planner", new BelgiumRailwayPlanner()));
        options.add(new MenuItem( "Traffic Management", new TrafficManagement()));
        int option = 0;
        do {
            option = Utils.showAndSelectIndex(options, "\n\n--- MAIN MENU --------------------------");

            if ((option >= 0) && (option < options.size())) {
                options.get(option).run();
            }
        } while (option != -1);
    }
}
