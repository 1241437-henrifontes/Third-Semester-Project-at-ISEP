package Model;

import Repositories.ItemRepository;
import Repositories.WarehouseRepository;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Manages returned goods placed in quarantine, deciding whether to restock or discard them
 * based on the return reason, and applying FIFO/FEFO placement when restocking.
 */
public class Quarantine {

    private final Set<Return> quarantineSet = new TreeSet<>();

    private final String auditLogPath = "logs/audit.log";
    private final ItemRepository itemRepository;
    private final List<Warehouse> warehouses;
    private final WarehouseRepository warehouseRepository;
    public Quarantine(ItemRepository itemRepository, List<Warehouse> warehouses) {
        this.itemRepository = itemRepository;
        this.warehouses = warehouses;
        this.warehouseRepository = WarehouseRepository.getInstance();
    }

    /**
     * Adds a return record to the quarantine set for later inspection.
     *
     * @param r return to add
     */
    public void addReturn(Return r) {
        quarantineSet.add(r);
    }

    /**
     * Iterates all returns currently in quarantine and inspects them, updating warehouses as needed.
     *
     * @return the list of warehouses (possibly updated with restocked boxes)
     */
    public List<Warehouse> processAllReturns() {
        for (Return r : quarantineSet) {
            inspectReturn(r);
        }

        return warehouses;
    }

    /**
     * Inspects one return and decides whether to restock a box into a warehouse or discard it.
     * Side effects: may update a warehouse and writes an audit log entry.
     *
     * @param r return to inspect
     */
    private void inspectReturn(Return r) {
        if (r.isProcessed()) return;

        boolean restock = canBeRestocked(r.getReason());
        String action = restock ? "Restocked" : "Discarded";

        if (restock) {
            LocalDate expiry = (r.getExpiryDate() != null)
                    ? r.getExpiryDate().toLocalDate()
                    : null;
            Box newBox = new Box("RET-" + r.getReturnId(), LocalDateTime.now(), expiry);

            Item item = itemRepository.getItemBySKU(r.getSku());
            if (item == null) {
                item = new Item(r.getSku(), r.getSku().getSku(), "", "", 0f, 0f);
            }
            newBox.addItem(item, r.getQuantity());

            Warehouse warehouse = null;

            for (Warehouse whouse: warehouses){
                ArrayList<ArrayList<Bay>> layout = whouse.getLayout();

                boolean fits = warehouseHasAvailableBayFromLayout(layout);

                if (fits){
                    warehouse = whouse;
                    break;
                }
            }

            if (warehouse == null) throw new IllegalStateException("No warehouse available for restocking.");


            //Adiciona a Box ao armazém com FIFO/FEFO
            List<Box> singleBoxList = new ArrayList<>();
            singleBoxList.add(newBox);
            warehouse.FIFOAndFEFOOrder(singleBoxList, warehouse.getBays());
            System.out.println("Restocked box placed in warehouse: " + warehouse.getWarehouseID());
            r.setAction(ReturnAction.RESTOCKED);
            warehouseRepository.updateWarehouse(warehouse);
        } else {
            r.setAction(ReturnAction.DISCARDED);
        }
        r.setProcessed(true);
        logInspection(r, action);
    }

    /**
     * Business rule that decides whether a return can be restocked based on the reason.
     *
     * @param reason the reason for the return
     * @return true if the return is eligible for restocking, false to discard
     */
    private boolean canBeRestocked(ReturnReason reason) {
        return reason == ReturnReason.CUSTOMER_REMORSE || reason == ReturnReason.CYCLE_COUNT;
    }

    /**
     * Appends a single-line entry to the audit log indicating the outcome of an inspection.
     * Fails silently by printing an error to stderr if the log cannot be written.
     *
     * @param r the inspected return
     * @param action the action taken (e.g., Restocked or Discarded)
     */
    private void logInspection(Return r, String action) {
        String logLine = String.format(
                "%s | returnId=%s | sku=%s | action=%s | qty=%d",
                LocalDateTime.now(),
                r.getReturnId(),
                r.getSku().getSku(),
                action,
                r.getQuantity()
        );

        try (FileWriter fw = new FileWriter(auditLogPath, true)) {
            fw.write(logLine + System.lineSeparator());
        } catch (IOException e) {
            System.err.println("Failed to write to audit log: " + e.getMessage());
        }
    }

    /**
     * Prints the current contents of the quarantine set to stdout for debugging/monitoring.
     */
    public void printQuarantine() {
        System.out.println("Current Quarantine Contents:");
        for (Return r : quarantineSet) {
            System.out.println(r.getReturnId() + " | " + r.getTimestamp() + " | " + r.getReason());
        }
    }


    /**
     * Scans the given warehouse layout to check if any bay can accept at least one more box.
     *
     * @param layout 2D grid of bays (null entries represent unused positions)
     * @return true if a suitable bay exists; false otherwise
     */
    public boolean warehouseHasAvailableBayFromLayout(ArrayList<ArrayList<Bay>> layout) {
        for (ArrayList<Bay> aisle : layout) {
            for (Bay bay : aisle) {
                if (bay != null && doesBoxFitInBay(bay)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Checks whether the provided bay has remaining capacity for another box.
     *
     * @param bay the bay to evaluate
     * @return true if the number of boxes is below the bay capacity
     */
    public boolean doesBoxFitInBay(Bay bay) {
        return bay.getBoxes().size() < bay.getCapacityBoxes();
    }
}
