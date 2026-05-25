package Controllers;

import Model.Bay;
import Model.PickPathSequencer;
import Model.PickAllocationRow;
import Model.Trolley;

import java.util.ArrayList;
import java.util.List;

/**
 * Coordinates pick-path sequencing for each trolley and outputs the resulting routes.
 * <p>
 * This controller collects unique bays from the trolley picks, applies multiple
 * sequencing strategies, and prints the suggested paths for comparison.
 */
public class PathSequencingController {

    private final PickPathSequencer sequencer = new PickPathSequencer();

    /**
     * Runs sequencing strategies for each provided trolley and prints their paths.
     *
     * @param trolleys the trolleys to process; if null or empty, no action is performed
     */
    public void process(List<Trolley> trolleys) {
        if (trolleys == null || trolleys.isEmpty()) {
            System.out.println("No trolleys available to process");
            return;
        }

        int index = 1;
        for (Trolley trolley : trolleys) {
            System.out.println("\n=== Trolley #" + index + " ===");

            List<Bay> bays = new ArrayList<>();
            for (PickAllocationRow row : trolley.getPicks()) {
                bays.add(new Bay("", row.getAisle(), row.getBay(), 0));
            }

            List<Bay> clean = sequencer.removeDuplicates(bays);
            if (clean.isEmpty()) {
                System.out.println("(No valid bays for this trolley)");
                index++;
                continue;
            }

            System.out.println("\nStrategy A (Deterministic Sweep):");
            System.out.println(sequencer.strategyA(clean));

            System.out.println("\nStrategy B (Nearest Neighbour):");
            System.out.println(sequencer.strategyB(clean));

            index++;
        }
    }
}
