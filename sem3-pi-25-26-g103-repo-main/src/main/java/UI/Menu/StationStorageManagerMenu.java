package UI.Menu;

import UI.*;
import UI.Utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Menu for Station Storage Manager operations.
 * <p>
 * Provides entry points to warehouse loading, order allocation, picking plans,
 * pick path sequencing, and returns management.
 */
public class StationStorageManagerMenu implements Runnable {

    /**
     * Displays the Station Storage Manager menu and dispatches the selected option.
     * The loop exits when the user cancels the menu (returns -1).
     */
    public void run() {
        List<MenuItem> options = new ArrayList<MenuItem>();
        options.add(new MenuItem("Load and Organize Warehouse", new WarehouseUI()));
        options.add(new MenuItem("Check Orders", new AllocationUI()));
        options.add(new MenuItem("Picking Plans", new PickingUI()));
        options.add(new MenuItem("Pick Path Sequencing", new PathSequencingUI()));
        options.add(new MenuItem("Returns Management", new ReturnsUI()));
        int option = 0;
        do {
            option = Utils.showAndSelectIndex(options, "\n\n--- STATION STORAGE MANAGER MENU --------------------------");
            if ((option >= 0) && (option < options.size())) {
                options.get(option).run();
            }
        } while (option != -1);
    }
}
