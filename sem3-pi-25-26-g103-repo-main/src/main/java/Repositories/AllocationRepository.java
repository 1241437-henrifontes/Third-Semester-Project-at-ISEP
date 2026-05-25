package Repositories;

import Model.*;

import java.util.*;

/**
 * Repository that computes and stores allocation results for orders.
 * <p>
 * Provides a singleton instance that, given a queue of orders, attempts to allocate
 * quantities from copies of the current warehouses state and records the outcome per line.
 */
public class AllocationRepository {
    private List<Allocation> allocationList = new ArrayList<>();
    private static AllocationRepository instance = new AllocationRepository();

    private AllocationRepository() {}

    /** Returns the AllocationRepository singleton. */
    public static AllocationRepository getInstance() {
        return instance;
    }

    /** Returns the current list of computed allocations. */
    public List<Allocation> getAllocationList() {
        return allocationList;
    }

    /**
     * Allocates inventory for all lines of all orders in the given priority queue.
     * The operation uses a working copy of warehouses to avoid mutating the original state.
     *
     * @param orders priority queue of orders to allocate
     */
    public void allocate(PriorityQueue<Order> orders) {
        if (getAllocationList() != null) {
            getAllocationList().clear();
        }

        List<Warehouse> warehousesOriginal = WarehouseRepository.getInstance().getAllWarehouses();
        List<Warehouse> warehousesCopy = new ArrayList<>();

        for (Warehouse warehouse : warehousesOriginal) {
            warehousesCopy.add(new Warehouse(warehouse));
        }

        for (Order order : orders) {
            for (OrderLine line : order.getLines()) {
                examine(line, warehousesCopy);
            }
        }
    }

    /** Examines a single order line and records an allocation result. */
    private static void examine(OrderLine line, List<Warehouse> wrs) {
        ItemRepository itemRepository = ItemRepository.getInstance();
        SKU targetSKU = line.getSku();
        Item targetItem = itemRepository.getItemBySKU(targetSKU);

        int remaining = line.getQty();
        int totalAllocated = 0;
        List<Map<Box, Integer>> allUsedBoxes = new ArrayList<>();

        for (Warehouse wr : wrs) {
            int allocatedHere = allocateFromWarehouse(line, wr, targetItem, remaining, allUsedBoxes);
            totalAllocated += allocatedHere;
            remaining -= allocatedHere;

            if (remaining == 0) break;
        }

        Allocation allocation = new Allocation(line, totalAllocated, allUsedBoxes);
        getInstance().getAllocationList().add(allocation);
    }

    /**
     * Attempts to allocate the remaining quantity of a line from a specific warehouse.
     *
     * @return the quantity allocated from the warehouse
     */
    private static int allocateFromWarehouse(OrderLine line, Warehouse wr, Item targetItem, int remaining, List<Map<Box, Integer>> allUsedBoxes) {
        List<Box> boxes = wr.getBoxesBySKU(line.getSku(), remaining);
        int allocated = 0;

        for (Box box : boxes) {
            Integer available = box.getItems().get(targetItem);
            if (available == null || available <= 0) continue;

            int toAllocate = Math.min(remaining, available);
            box.getItems().put(targetItem, available - toAllocate);

            allocated += toAllocate;
            remaining -= toAllocate;

            Map<Box, Integer> usedBox = new HashMap<>();
            usedBox.put(box, allocated);
            allUsedBoxes.add(usedBox);

            if (remaining == 0) break;
        }

        return allocated;
    }
}
