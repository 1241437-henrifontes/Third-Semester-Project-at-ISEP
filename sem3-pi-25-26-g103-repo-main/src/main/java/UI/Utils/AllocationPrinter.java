package UI.Utils;

import Model.Allocation;
import Model.Box;
import Model.OrderLine;
import Model.Status;

import java.util.List;

/**
 * Utility class for printing allocation results in a human-readable format and
 * marking order line eligibility based on strict or non-strict rules.
 */
public class AllocationPrinter {

    /**
     * Evaluates allocation results and assigns eligibility status to each order line, then prints a human-readable summary.
     * In strict mode, a line is eligible only if fully allocated; otherwise, partial allocations are marked as PARTIAL.
     *
     * @param results list of allocation results to evaluate and print
     * @param isStrictFlag true for strict eligibility (all-or-nothing), false to allow PARTIAL eligibility
     */
    public static void elect(List<Allocation> results, boolean isStrictFlag) {
        String currentOrderId = "";

        for (Allocation allocation : results) {

            if (!allocation.getLine().getOrderId().equals(currentOrderId)) {
                currentOrderId = allocation.getLine().getOrderId();
                System.out.println();
                System.out.println("-------Order " + currentOrderId + "-------");
            }

            OrderLine line = allocation.getLine();
            int requested = line.getQty();
            int allocated = allocation.getAllocatedQty();

            if (isStrictFlag) {
                if (requested > allocated) {
                    line.setStatus(Status.UNDISPATCHABLE);
                } else {
                    line.setStatus(Status.ELIGIBLE);
                }
            } else {
                if (allocated == 0) line.setStatus(Status.UNDISPATCHABLE);
                else if (allocated < requested) line.setStatus(Status.PARTIAL);
                else line.setStatus(Status.ELIGIBLE);
            }

            System.out.printf(
                    " | Line:  %-4d" +
                            " | SKU: %-10s" +
                            " | Requested Quantity: %-5d " +
                            " | Allocated Quantity: %-5d " +
                            " | Eligibility: %-15s  | ",
                    line.getLineNumber(),
                    line.getSku().getSku(),
                    requested,
                    allocated,
                    line.getStatus()
            );

            if (allocation.getAllocatedQty() != 0) {
                System.out.print("Box(es): ");

                for (int i = 0; i < allocation.getBoxes().size(); i++) {
                    Box box = allocation.getBoxes().get(i).keySet().iterator().next();
                    System.out.printf(
                            "%-5s (Aisle: %-1d; Bay: %-1d); ",
                            box.getBoxId(),
                            box.getAssignedBay().getAisle(),
                            box.getAssignedBay().getBay()
                    );
                }
            }

            System.out.println();
        }
    }
}
