package Controllers;

import Repositories.WarehouseRepository;
import Model.Bay;
import Model.Box;
import Model.Warehouse;

import java.util.List;
import java.util.Map;

/**
 * Provides simple operations for warehouse management used by the UI layer.
 */
public class WarehouseController {

    /**
     * Moves a box into two bays within the given warehouse.
     *
     * @param warehouse the target warehouse
     * @param bay1      first destination bay
     * @param bay2      second destination bay
     * @param box       the box to be split/moved
     */
    public void changeBoxIntoBays(Warehouse warehouse, Bay bay1, Bay bay2, Box box) {
        warehouse.changeIntoBays(box, bay1, bay2);
    }

    /** Returns the WarehouseRepository singleton. */
    public WarehouseRepository getWarehouseRepository() {
        return WarehouseRepository.getInstance();
    }

    /** Returns the warehouses indexed by their identifier. */
    public Map<String, Warehouse> getWarehouses() {
        return getWarehouseRepository().getWarehouses();
    }

    /** Loads boxes into warehouses and returns the remaining not-placed boxes. */
    public List<Box> loadWarehouses() {
        return getWarehouseRepository().loadWarehouses();
    }
}
