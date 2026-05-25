package Model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;

/**
 * Represents a physical box/pallet unit that can store multiple item types with quantities.
 * A box may have an expiry date and is placed in a specific bay when stored in a warehouse.
 */
public class Box {
    private String boxId;
    private LocalDateTime receivedAt;
    private LocalDate expiryDate;
    private HashMap<Item, Integer> items;
    private Bay assignedBay;

    public Box(String boxId, LocalDateTime receivedAt, LocalDate expiryDate) {
        this.boxId = boxId;
        this.receivedAt = receivedAt;
        this.expiryDate = expiryDate;
        this.items = new HashMap<>();
    }

    public Box(Box other) {
        this.boxId = other.boxId;
        this.receivedAt = other.receivedAt;
        this.expiryDate = other.expiryDate;
        this.items = new HashMap<>(other.items);
        this.assignedBay = other.assignedBay;
    }

    public String getBoxId() {
        return boxId;
    }

    public LocalDateTime getReceivedAt() {
        return receivedAt;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    /**
     * Adds a quantity for the given item to this box, accumulating with any existing quantity.
     *
     * @param item item to add
     * @param quantity number of units to add (can be positive to increase stock)
     */
    public void addItem(Item item, int quantity) {
        items.put(item, items.getOrDefault(item, 0) + quantity);
    }

    public HashMap<Item, Integer> getItems() {
        return items;
    }

    public Bay getAssignedBay() {
        return assignedBay;
    }

    public void setAssignedBay(Bay assignedBay) {
        this.assignedBay = assignedBay;
    }

    @Override
    public String toString() {
        return "Box{" +
                "boxId='" + boxId + '\'' +
                ", receivedAt=" + receivedAt +
                ", expiryDate=" + expiryDate +
                ", items=" + items +
                '}';
    }
}