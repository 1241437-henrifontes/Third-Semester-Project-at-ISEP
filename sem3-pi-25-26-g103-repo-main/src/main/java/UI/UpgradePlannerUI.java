package UI;

import Controllers.UpgradePlannerController;
import Model.Graph.Node;
import UI.Menu.MenuItem;
import UI.Utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class UpgradePlannerUI implements Runnable {

    private final UpgradePlannerController controller = new UpgradePlannerController();

    private void executeUSEI11() {

        List<Node> result = controller.executeUSEI11();

        if (result == null) {
            System.out.println("\nStation graph is empty. Please load data first.");
            return;
        }

        int totalStations = controller.getNumberOfStations();

        System.out.println("\n--- USEI11 – DIRECTED LINE UPGRADE PLAN --------------------------");

        if (result.size() == totalStations) {
            System.out.println("Graph without cycles.");
            System.out.println("Upgrade order of stations:\n");

            int i = 1;
            System.out.println();
            for (Node n : result) {
                System.out.print(i++ + " -> " + n.getName());
            }

        } else {
            System.out.println("Graph with cycles detected.");
            System.out.println("Stations involved in the first detected cycle:\n");

            System.out.println();
            for (Node n : result) {
                System.out.print("-> " + n.getName());
            }
        }
    }

    @Override
    public void run() {

        List<MenuItem> options = new ArrayList<>();

        options.add(new MenuItem(
                "Compute upgrade order ",
                this::executeUSEI11
        ));

        int option;
        do {
            option = Utils.showAndSelectIndex(
                    options,
                    "\n\n--- USEI11 MENU --------------------------"
            );

            if (option >= 0 && option < options.size()) {
                options.get(option).run();
            }

        } while (option != -1);
    }
}

