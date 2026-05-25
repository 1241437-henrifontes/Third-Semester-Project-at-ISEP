package Repositories;

import Model.*;

import java.util.*;

/**
 * Repository for managing warehouses and their contents.
 */
public class WarehouseRepository {
    private Map<String, Warehouse> warehouses = new HashMap<>();
    private static WarehouseRepository instance = new WarehouseRepository();

    private WarehouseRepository() {}

    /** Returns the WarehouseRepository singleton. */
    public static WarehouseRepository getInstance() {
        return instance;
    }

    /** Replaces the current warehouses map. */
    public void setWarehouses(Map<String, Warehouse> warehouses) {
        this.warehouses = warehouses;
    }

    /** Inserts or updates a warehouse entry. */
    public void updateWarehouse(Warehouse warehouse){
        this.warehouses.put(warehouse.getWarehouseID(), warehouse);
    }

    /** Returns all warehouses as a list. */
    public List<Warehouse> getAllWarehouses() {
        return new ArrayList<>(warehouses.values());
    }

    /** Returns a warehouse by its identifier. */
    public Warehouse getWarehouse(String id) {
        return warehouses.get(id);
    }

    /** Returns the internal map of warehouses. */
    public Map<String, Warehouse> getWarehouses() {
        return warehouses;
    }

    /** Returns the list of all warehouse identifiers. */
    public List<String> getWarehousesId() {
        return new ArrayList<>(warehouses.keySet());
    }

    /**
     * Creates warehouses and their bays from the CSV source named "bays".
     */
    public void createWarehouse() {
        List<Bay> bays = ReadFromCSV.readFile("bays", campos -> {
            if (campos.length < 4) {
                throw new IllegalArgumentException("Invalid bay record: expected 4 columns but got " + campos.length);
            }
            String warehouseId = campos[0];
            String aisleStr = campos[1];
            String bayStr = campos[2];
            String capacityStr = campos[3];

            if (warehouseId == null || warehouseId.isEmpty()) {
                throw new IllegalArgumentException("Missing warehouseId in bays.csv record.");
            }

            int aisle;
            try { aisle = Integer.parseInt(aisleStr); }
            catch (NumberFormatException nfe) { throw new IllegalArgumentException("Invalid aisle '" + aisleStr + "' for warehouse '" + warehouseId + "': must be an integer."); }
            if (aisle < 1) { throw new IllegalArgumentException("Invalid aisle '" + aisleStr + "' for warehouse '" + warehouseId + "': must be >= 1."); }

            int bayIdx;
            try { bayIdx = Integer.parseInt(bayStr); }
            catch (NumberFormatException nfe) { throw new IllegalArgumentException("Invalid bay '" + bayStr + "' for warehouse '" + warehouseId + "': must be an integer."); }
            if (bayIdx < 1) { throw new IllegalArgumentException("Invalid bay '" + bayStr + "' for warehouse '" + warehouseId + "': must be >= 1."); }

            int capacity;
            try { capacity = Integer.parseInt(capacityStr); }
            catch (NumberFormatException nfe) { throw new IllegalArgumentException("Invalid capacityBoxes '" + capacityStr + "' for warehouse '" + warehouseId + "' (aisle " + aisle + ", bay " + bayIdx + "): must be an integer."); }
            if (capacity < 0) { throw new IllegalArgumentException("Invalid capacityBoxes '" + capacityStr + "' for warehouse '" + warehouseId + "' (aisle " + aisle + ", bay " + bayIdx + "): must be >= 0."); }

            return new Bay(warehouseId, aisle, bayIdx, capacity);
        });

        Map<String, Integer> maxAisles = new HashMap<>();
        Map<String, Integer> maxBays = new HashMap<>();

        for (Bay bay : bays) {
            maxAisles.put(bay.getWarehouseId(), Math.max(maxAisles.getOrDefault(bay.getWarehouseId(), 0), bay.getAisle()));
            maxBays.put(bay.getWarehouseId(), Math.max(maxBays.getOrDefault(bay.getWarehouseId(), 0), bay.getBay()));
        }

        for (String warehouseId : maxAisles.keySet()) {
            warehouses.put(warehouseId, new Warehouse(warehouseId, maxAisles.get(warehouseId), maxBays.get(warehouseId)));
        }

        for (Bay bay : bays) {
            warehouses.get(bay.getWarehouseId()).addBay(bay);
        }

    }

    /**
     * Loads boxes into warehouses following FIFO/FEFO order; returns boxes that remain unplaced.
     *
     * @return list of boxes that were not placed back into warehouses
     */
    public List<Box> loadWarehouses(){
        List<Box> boxes = WagonRepository.getInstance().getAllBoxesAndRemove();
        for (Warehouse warehouse : warehouses.values()) {
            boxes = warehouse.FIFOAndFEFOOrder(boxes, warehouse.getBays());
        }
        WagonRepository.getInstance().addBoxes(boxes);
        return boxes;
    }
}