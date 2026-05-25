package Controllers;

import Repositories.AllocationRepository;
import Repositories.ItemRepository;
import Repositories.TrolleyRepository;
import Model.*;
import Services.PickingService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Exposes operations to plan picking into trolleys and to transform allocations into pick rows.
 * <p>
 * This controller validates inputs, delegates planning to the service layer, and persists the
 * last executed plan for later inspection.
 */
public class PickingController {

    private final PickingService planner = new PickingService();

    private AllocationRepository getAllocationRepository() {
        return AllocationRepository.getInstance();
    }

    private TrolleyRepository getTrolleyRepository() {
        return TrolleyRepository.getInstance();
    }

    /**
     * Plans the distribution of pick rows into trolleys and stores the plan metadata.
     *
     * @param rows               rows to plan
     * @param trolleyCapacityKg  capacity of each trolley in kilograms
     * @param heuristic          bin packing heuristic
     * @param policy             overflow handling policy
     * @return list of planned trolleys
     */
    public List<Trolley> plan(List<PickAllocationRow> rows,
                              double trolleyCapacityKg,
                              PickingService.Heuristic heuristic,
                              PickingService.OverflowPolicy policy) {
        List<Trolley> trolleys = planner.plan(rows, trolleyCapacityKg, heuristic, policy);

        getTrolleyRepository().storePlan(trolleys, heuristic, policy, trolleyCapacityKg);

        return trolleys;
    }

    /**
     * Returns the rows that could not be planned in the last execution.
     *
     * @return list of skipped rows
     */
    public List<PickAllocationRow> getSkippedRows() {
        return planner.getSkippedRows();
    }

    /**
     * Converts allocation outcomes into pick rows suitable for planning.
     *
     * @return list of pick rows generated from current allocations
     */
    public List<PickAllocationRow> convertToPickRows() {
        List<PickAllocationRow> rows = new ArrayList<>();

        for (Allocation allocation : getAllocationRepository().getAllocationList()) {
            OrderLine line = allocation.getLine();

            if (line.getStatus() == Status.UNDISPATCHABLE) continue;

            Item item = ItemRepository.getInstance().getItemBySKU(line.getSku());
            if (item == null) {
                System.err.println("WARNING: SKU " + line.getSku().getSku() + " not found. Skipping.");
                continue;
            }

            double unitWeight = item.getUnitWeight();
            if (unitWeight <= 0) {
                System.err.println("WARNING: SKU " + line.getSku().getSku() +
                        " has invalid weight (" + unitWeight + " kg). Skipping line " +
                        line.getOrderId() + "#" + line.getLineNumber());
                continue;
            }

            for (Map<Box, Integer> boxEntry : allocation.getBoxes()) {
                Box box = boxEntry.keySet().iterator().next();
                Integer qty = boxEntry.values().iterator().next();

                PickAllocationRow row = new PickAllocationRow(
                        line.getOrderId(),
                        line.getLineNumber(),
                        line.getSku(),
                        qty,
                        box.getBoxId(),
                        box.getAssignedBay().getAisle(),
                        box.getAssignedBay().getBay(),
                        unitWeight
                );

                rows.add(row);
            }
        }

        return rows;
    }
}
