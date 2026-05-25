package UI.Menu;

import UI.LAPR.TravelTimeUI;
import UI.Utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Menu for Traffic Dispatcher operations.
 * <p>
 * Provides access to travel time estimation tools.
 */
public class TrafficDispatcherMenu implements Runnable {

    /**
     * Displays the Traffic Dispatcher menu and executes the selected option.
     * The loop exits when the user cancels the menu (returns -1).
     */
    public void run() {
        List<MenuItem> options = new ArrayList<MenuItem>();
        options.add(new MenuItem("Estimate Travel Time", new TravelTimeUI()));
        int option = 0;
        do {
            option = Utils.showAndSelectIndex(options, "\n\n--- TRAFFIC DISPATCHER MENU --------------------------");

            if ((option >= 0) && (option < options.size())) {
                options.get(option).run();
            }
        } while (option != -1);
    }
}
