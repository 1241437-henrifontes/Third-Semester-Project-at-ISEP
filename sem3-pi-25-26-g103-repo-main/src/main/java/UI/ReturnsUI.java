package UI;

import Controllers.ReturnsController;
import UI.Menu.MenuItem;
import UI.Utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * UI for managing product returns and quarantine processing.
 */
public class ReturnsUI implements Runnable {

    private ReturnsController controller;

    /**
     * Runs the returns management workflow, including loading returns and showing a menu.
     */
    @Override
    public void run() {
        controller = new ReturnsController();

        controller.loadReturnsToQuarantine();

        List<MenuItem> options = new ArrayList<>();
        options.add(new MenuItem("Show Quarantine List", this::showQuarantine));
        options.add(new MenuItem("Process All Returns", this::processAllReturns));

        int option;
        do {
            option = Utils.showAndSelectIndex(options, "\n\n--- RETURNS & QUARANTINE MANAGEMENT --------------------------");

            if (option >= 0 && option < options.size()) {
                options.get(option).run();
            }

        } while (option != -1);

        System.out.println("Exiting Returns Management...");
    }

    /**
     * Displays the current quarantine list.
     */
    private void showQuarantine() {
        System.out.println("\n--- Returns in Quarantine ---");
        controller.printQuarantine();
    }

    /**
     * Processes all returns currently in quarantine and logs the actions.
     */
    private void processAllReturns() {
        System.out.println("\n--- Processing Returns ---");
        controller.processReturns();
        System.out.println("All returns have been processed. Check 'audit.log'.");
    }
}
