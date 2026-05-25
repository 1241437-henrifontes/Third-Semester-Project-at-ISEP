package UI.Menu;

import java.util.ArrayList;
import java.util.List;
import UI.KDtreeBuildUI;
import UI.Utils.Utils;

/**
 * Menu for data engineering utilities.
 * Currently provides access to KD-tree construction features.
 */
public class DataEngineer implements Runnable {

    /**
     * Displays the Data Engineer menu and runs the selected option.
     */
    @Override
    public void run() {
        List<MenuItem> options = new ArrayList<>();

        options.add(new MenuItem("KD-tree Construction ", new KDtreeBuildUI()));

        int option;

        do {
            option = Utils.showAndSelectIndex(
                    options,
                    "\n\n--- DATA ENGINEER MENU --------------------------"
            );

            if (option >= 0 && option < options.size()) {
                options.get(option).run();
            }

        } while (option != -1);
    }
}
