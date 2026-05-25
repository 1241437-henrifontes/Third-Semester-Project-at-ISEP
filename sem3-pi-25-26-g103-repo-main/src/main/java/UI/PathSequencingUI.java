package UI;

import Repositories.TrolleyRepository;
import Controllers.PathSequencingController;
import Model.Trolley;
import java.util.List;

/**
 * UI to perform pick path sequencing for previously generated picking plans.
 */
public class PathSequencingUI implements Runnable {

    private final PathSequencingController controller = new PathSequencingController();

    @Override
    public void run() {
        System.out.println();
        System.out.println("=== USEI04 — Pick Path Sequencing ===");

        var repo = TrolleyRepository.getInstance();
        if (!repo.hasTrolleys()) {
            System.out.println("No trolleys available — please run the Picking Plan (USEI03) first.");
            return;
        }

        List<Trolley> trolleys = repo.getAllTrolleys();

        System.out.println("Loaded " + repo.getTrolleyCount() + " trolleys from repository.");
        System.out.println("Last Plan: Heuristic = " + repo.getLastHeuristic()
                + " | Policy = " + repo.getLastPolicy()
                + " | Capacity = " + repo.getLastTrolleyCapacity() + " kg");
        System.out.println("──────────────────────────────────────────────────────────────");

        controller.process(trolleys);

        System.out.println("──────────────────────────────────────────────────────────────");
        System.out.println("Path sequencing completed.\n");
    }
}
