package UI;

import Controllers.KDtreeBuildController;
import UI.Menu.MenuItem;
import UI.Utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class KDtreeBuildUI implements Runnable {

    private final KDtreeBuildController controller = new KDtreeBuildController();

    private void buildKDTree() {
        controller.buildKDTree();
    }

    private void showKDTreeStats() {
        controller.showKDTreeStats();
    }

    @Override
    public void run() {
        List<MenuItem> options = new ArrayList<>();

        options.add(new MenuItem("Build KD-tree", this::buildKDTree));
        options.add(new MenuItem("Show KD-tree statistics", this::showKDTreeStats));

        int option;

        do {
            option = Utils.showAndSelectIndex(
                    options,
                    "\n\n--- KD-TREE CONSTRUCTION --------------------------"
            );

            if (option >= 0 && option < options.size()) {
                options.get(option).run();
            }

        } while (option != -1);
    }
}
