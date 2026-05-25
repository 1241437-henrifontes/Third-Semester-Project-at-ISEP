package Model;

import java.util.ArrayList;

/**
 * Represents a physical bay location inside a warehouse layout.
 * A bay belongs to a warehouse and an aisle, holds boxes up to a configured capacity.
 */
public class Bay {
    private String warehouseId;
    private int aisle;
    private int bay;
    private int capacityBoxes;
    private ArrayList<Box> boxes;

    public Bay(String warehouseId,int aisle,int bay ,int capacityBoxes) {
        this.warehouseId = warehouseId;
        this.aisle = aisle;
        this.bay = bay;
        this.capacityBoxes = capacityBoxes;
        this.boxes = new ArrayList<>();
    }

    public Bay(Bay other) {
        this.warehouseId = other.warehouseId;
        this.aisle = other.aisle;
        this.bay = other.bay;
        this.capacityBoxes = other.capacityBoxes;

        this.boxes = new ArrayList<>();
        for (Box box : other.getBoxes()) {
            this.boxes.add(new Box(box));
        }
    }

    public int getAisle() {
        return aisle;
    }

    public void setAisle(int aisle) {
        this.aisle = aisle;
    }

    public int getBay() {
        return bay;
    }

    public void setBay(int bay) {
        this.bay = bay;
    }

    public int getCapacityBoxes() {
        return capacityBoxes;
    }

    public String getWarehouseId() {
        return warehouseId;
    }

    /**
     * Attempts to place a box in this bay if there is remaining capacity.
     *
     * @param box the box to add
     * @return true if the box was added; false if the bay is already full
     */
    public boolean addBox(Box box) {
        if (boxes.size() < capacityBoxes) {
            boxes.add(box);
            return true;
        }
        return false;
    }

    /**
     * Removes the given box from this bay, if present.
     *
     * @param box the box to remove
     * @return true if the box existed and was removed; false otherwise
     */
    public boolean removeBox(Box box) {
        return boxes.remove(box);
    }

    public ArrayList<Box> getBoxes() {
        return boxes;
    }

    @Override
    public String toString() {
        return "Bay{" +
                "aisle=" + aisle +
                ", bay=" + bay +
                ", capacityBoxes=" + capacityBoxes +
                ", boxes=" + boxes +
                '}';
    }
}