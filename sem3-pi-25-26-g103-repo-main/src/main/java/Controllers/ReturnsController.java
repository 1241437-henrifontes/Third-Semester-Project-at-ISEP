package Controllers;

import Repositories.ItemRepository;
import Repositories.ReturnRepository;
import Repositories.WarehouseRepository;
import Model.*;

/**
 * Manages loading, queuing, and processing of product returns into quarantine.
 * <p>
 * This controller coordinates repositories and exposes simple operations used by the UI layer.
 */
public class ReturnsController {

    private final ItemRepository itemRepository;
    private final ReturnRepository returnRepository;
    private final WarehouseRepository warehouseRepository;
    private final Quarantine quarantine;

    /**
     * Creates a ReturnsController with singleton repositories and a new Quarantine instance.
     */
    public ReturnsController() {
        this.itemRepository = ItemRepository.getInstance();
        this.returnRepository = ReturnRepository.getInstance();
        this.warehouseRepository = WarehouseRepository.getInstance();
        this.quarantine = new Quarantine(itemRepository, warehouseRepository.getAllWarehouses());
    }

    /**
     * Loads returns from the repository and enqueues them into quarantine.
     */
    public void loadReturnsToQuarantine() {
        returnRepository.loadReturns(itemRepository);
        for (Return r : returnRepository.getAllReturns()) {
            quarantine.addReturn(r);
        }
    }

    /**
     * Processes all queued returns within quarantine.
     */
    public void processReturns() {
         quarantine.processAllReturns();

    }

    /**
     * Prints the current state of the quarantine queue and processed items.
     */
    public void printQuarantine() {
        quarantine.printQuarantine();
    }

    /**
     * Exposes the underlying Quarantine instance for inspection.
     *
     * @return the quarantine object managed by this controller
     */
    public Quarantine getQuarantine() {
        return quarantine;
    }
}
