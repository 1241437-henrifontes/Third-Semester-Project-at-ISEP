package Model;

import java.util.*;

/**
 * Represents a warehouse layout with bays and manages box placement using FIFO/FEFO policies.
 */
public class Warehouse {
    private String warehouseID;
    private ArrayList<ArrayList<Bay>> layout;
    private List<Box> allSortedBoxes;

    public Warehouse(String warehouseID, int maxAisle, int maxBay) {
        this.warehouseID = warehouseID;
        layout = new ArrayList<>();
        for (int i = 0; i <= maxAisle; i++) {
            ArrayList<Bay> aisle = new ArrayList<>();
            for (int j = 0; j <= maxBay; j++) {
                aisle.add(null);
            }
            layout.add(aisle);
        }
        this.allSortedBoxes = new ArrayList<>();
    }

    public Warehouse(Warehouse other) {
        this.warehouseID = other.warehouseID;

        this.layout = new ArrayList<>();
        for (ArrayList<Bay> row : other.getLayout()) {
            ArrayList<Bay> newRow = new ArrayList<>();
            for (Bay bay : row) {
                newRow.add(bay != null ? new Bay(bay) : null);
            }
            this.layout.add(newRow);
        }
        this.allSortedBoxes = other.allSortedBoxes;
    }

    public String getWarehouseID() {
        return warehouseID;
    }

    /**
     * Inserts a bay into the warehouse layout at its (aisle,bay) coordinates.
     *
     * @param bay bay to add to the layout
     */
    public void addBay(Bay bay) {
        layout.get(bay.getAisle()).set(bay.getBay(), bay);
    }

    public Bay getBay(int aisle, int bay) {
        return layout.get(aisle).get(bay);
    }

    public ArrayList<ArrayList<Bay>> getLayout() {
        return layout;
    }

    @Override
    public String toString() {
        return "Warehouse{" +
                "warehouseID='" + warehouseID + '\'' +
                ", layout=" + layout +
                '}';
    }

    /**
     * Flattens the 2D layout into a list of all existing bays (ignoring null slots).
     *
     * @return list of bays present in the layout
     */
    public List<Bay> getBays() {
        List<Bay> bays = new ArrayList<>();
        for (ArrayList<Bay> aisle : layout) {
            for (Bay bay : aisle) {
                if (bay != null) {
                    bays.add(bay);
                }
            }
        }
        return bays;
    }

    /**
     * Orders the global box queue by FEFO (earliest expiry first) then FIFO (earliest received),
     * persists this ordering internally, and tries to place the provided boxes into the given bays.
     *
     * @param boxes boxes to be (re)placed
     * @param bays candidate bays to receive boxes
     * @return boxes that could not be placed due to insufficient space
     */
    public List<Box> FIFOAndFEFOOrder(List<Box> boxes, List<Bay> bays) {
        PriorityQueue<Box> queue = new PriorityQueue<>(
                Comparator.comparing(Box::getExpiryDate, Comparator.nullsLast(Comparator.naturalOrder()))
                        .thenComparing(Box::getReceivedAt)
                        .thenComparing(Box::getBoxId)
        );
        queue.addAll(allSortedBoxes);
        queue.addAll(boxes);
        allSortedBoxes.clear();
        allSortedBoxes.addAll(queue);
        return addBoxesToBays(boxes, bays);
    }

    /**
     * Greedily fills bays with the given boxes until each bay reaches its capacity.
     * The box's assignedBay reference is kept in sync.
     *
     * @param boxes boxes to place
     * @param bays destination bays
     * @return leftover boxes that did not fit
     */
    public List<Box> addBoxesToBays(List<Box> boxes, List<Bay> bays) {
        for (Bay bay : bays) {
            while (bay.getBoxes().size() < bay.getCapacityBoxes() && (!boxes.isEmpty())) {
                boxes.get(0).setAssignedBay(bay);
                bay.addBox(boxes.remove(0));
            }
        }
        List<Box> remainingBoxes = new ArrayList<>();
        remainingBoxes.addAll(boxes);
        return remainingBoxes;
    }

    /**
     * Moves a box from one bay to another if the box is present in the source and the destination has capacity.
     *
     * @param box the box to move
     * @param bay1 source bay
     * @param bay2 destination bay
     */
    public void changeIntoBays(Box box, Bay bay1, Bay bay2) {
        if (bay1.getBoxes().contains(box) && bay2.getBoxes().size() < bay2.getCapacityBoxes()) {
            bay1.removeBox(box);
            bay2.addBox(box);
            box.setAssignedBay(bay2);
        }
    }

    /**
     * Retrieves boxes that contain the requested SKU until the cumulative quantity meets or exceeds the target.
     *
     * @param sku SKU to search for
     * @param quantity desired total quantity of units
     * @return list of boxes contributing to the requested quantity (may exceed it)
     */
    public List<Box> getBoxesBySKU(SKU sku, int quantity) {
        List<Box> boxes = new ArrayList<>();
        if (quantity <= 0) return boxes;
        for( Box box : allSortedBoxes){
            int matchedQtyInBox = 0;
            for (Map.Entry<Item, Integer> entry : box.getItems().entrySet()) {
                if (sku.equals(entry.getKey().getSku())) {
                    matchedQtyInBox += entry.getValue();
                }
            }
            if (matchedQtyInBox > 0) {
                boxes.add(box);
                quantity -= matchedQtyInBox;
                if (quantity <= 0) {
                    return boxes;
                }
            }
        }
        return boxes;
    }
}