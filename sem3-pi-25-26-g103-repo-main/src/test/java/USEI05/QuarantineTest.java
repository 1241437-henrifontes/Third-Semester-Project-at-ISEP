package USEI05;

import Repositories.ItemRepository;
import Repositories.WarehouseRepository;
import Model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class QuarantineTest {

    private Quarantine quarantine;
    private ItemRepository itemRepository;
    private WarehouseRepository warehouseRepository;
    private List<Warehouse> warehouses;
    private Warehouse warehouse;

    @BeforeEach
    void setUp() {
        itemRepository = ItemRepository.getInstance();
        List<Item> testItems = createTestItems();
        itemRepository.setItems(testItems);

        warehouseRepository = WarehouseRepository.getInstance();
        Map<String, Warehouse> testWarehouses = createTestWarehouses();
        warehouseRepository.setWarehouses(testWarehouses);


        warehouse = warehouseRepository.getWarehouse("W1");
        assertNotNull(warehouse, "Test warehouse W1 should exist");

        warehouses = warehouseRepository.getAllWarehouses();
        quarantine = new Quarantine(itemRepository, warehouses);
    }



    private List<Item> createTestItems() {
        List<Item> items = new ArrayList<>();

        // Create 10 test items
        for (int i = 1; i <= 10; i++) {
            String skuCode = String.format("SKU%04d", i);
            SKU sku = new SKU(skuCode);
            Item item = new Item(sku, "Item_" + i, "TestCategory", "pack", 5.0f, 1.5f);
            items.add(item);
        }

        return items;
    }

    private Map<String, Warehouse> createTestWarehouses() {
        Map<String, Warehouse> warehouses = new HashMap<>();


        Warehouse w1 = new Warehouse("W1", 3, 5);


        for (int aisle = 1; aisle <= 3; aisle++) {
            for (int bay = 1; bay <= 5; bay++) {
                int capacity = 10 + (aisle + bay) % 6;
                Bay newBay = new Bay("W1", aisle, bay, capacity);
                w1.addBay(newBay);
            }
        }

        warehouses.put("W1", w1);


        Warehouse w2 = new Warehouse("W2", 2, 3);
        for (int aisle = 1; aisle <= 2; aisle++) {
            for (int bay = 1; bay <= 3; bay++) {
                Bay newBay = new Bay("W2", aisle, bay, 12);
                w2.addBay(newBay);
            }
        }

        warehouses.put("W2", w2);

        return warehouses;
    }

    private Return createTestReturn(String returnId, String skuCode, ReturnReason reason) {
        SKU sku = new SKU(skuCode);
        return new Return(
                returnId,
                sku,
                10,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(30),
                reason
        );
    }



    @Test
    void testAddReturn() {
        // Given
        Return testReturn = createTestReturn("RET-001", "SKU0001", ReturnReason.CUSTOMER_REMORSE);

        // When
        quarantine.addReturn(testReturn);

        // Then - Should not throw exception
        assertDoesNotThrow(() -> quarantine.addReturn(testReturn));
    }

    @Test
    void testProcessAllReturns_WithRestockableReturn() {

        Return testReturn = createTestReturn("RET-001", "SKU0001", ReturnReason.CUSTOMER_REMORSE);
        quarantine.addReturn(testReturn);


        quarantine.processAllReturns();


        assertTrue(testReturn.isProcessed());
        assertEquals(ReturnAction.RESTOCKED, testReturn.getAction());
    }

    @Test
    void testProcessAllReturns_WithDiscardableReturn() {

        Return testReturn = createTestReturn("RET-002", "SKU0002", ReturnReason.DAMAGED);
        quarantine.addReturn(testReturn);


        quarantine.processAllReturns();


        assertTrue(testReturn.isProcessed());
        assertEquals(ReturnAction.DISCARDED, testReturn.getAction());
    }

    @Test
    void testProcessAllReturns_CycleCountReason() {

        Return testReturn = createTestReturn("RET-003", "SKU0003", ReturnReason.CYCLE_COUNT);
        quarantine.addReturn(testReturn);


        quarantine.processAllReturns();


        assertTrue(testReturn.isProcessed());
        assertEquals(ReturnAction.RESTOCKED, testReturn.getAction());
    }

    @Test
    void testProcessAllReturns_NoWarehouseAvailable() {

        Quarantine emptyQuarantine = new Quarantine(itemRepository, new ArrayList<>());
        Return testReturn = createTestReturn("RET-004", "SKU0004", ReturnReason.CUSTOMER_REMORSE);
        emptyQuarantine.addReturn(testReturn);


        assertThrows(IllegalStateException.class, emptyQuarantine::processAllReturns);
    }

    @Test
    void testWarehouseHasAvailableBayFromLayout_WithSpace() {

        ArrayList<ArrayList<Bay>> layout = warehouse.getLayout();


        boolean hasSpace = quarantine.warehouseHasAvailableBayFromLayout(layout);


        assertTrue(hasSpace, "Empty warehouse should have available bays");
    }

    @Test
    void testWarehouseHasAvailableBayFromLayout_NoSpace() {

        ArrayList<ArrayList<Bay>> layout = warehouse.getLayout();
        for (ArrayList<Bay> aisle : layout) {
            for (Bay bay : aisle) {
                if (bay != null) {
                    while (bay.getBoxes().size() < bay.getCapacityBoxes()) {
                        Box fillerBox = new Box("FILLER-" + System.nanoTime(), LocalDateTime.now(), null);
                        bay.addBox(fillerBox);
                    }
                }
            }
        }


        boolean hasSpace = quarantine.warehouseHasAvailableBayFromLayout(layout);


        assertFalse(hasSpace, "All bays should be full");
    }

    @Test
    void testDoesBoxFitInBay_WithSpace() {

        Bay emptyBay = new Bay("W1-TEST", 1, 1, 10);


        boolean fits = quarantine.doesBoxFitInBay(emptyBay);


        assertTrue(fits, "Empty bay should have space for a box");
    }

    @Test
    void testDoesBoxFitInBay_NoSpace() {

        Bay fullBay = new Bay("W1-TEST", 1, 1, 10);
        while (fullBay.getBoxes().size() < fullBay.getCapacityBoxes()) {
            Box fillerBox = new Box("FILLER-" + System.nanoTime(), LocalDateTime.now(), null);
            fullBay.addBox(fillerBox);
        }


        boolean fits = quarantine.doesBoxFitInBay(fullBay);


        assertFalse(fits, "Full bay should not have space");
    }

    @Test
    void testProcessAllReturns_MultipleReturns() {

        Return return1 = createTestReturn("RET-005", "SKU0005", ReturnReason.CUSTOMER_REMORSE);
        Return return2 = createTestReturn("RET-006", "SKU0006", ReturnReason.DAMAGED);
        Return return3 = createTestReturn("RET-007", "SKU0007", ReturnReason.CYCLE_COUNT);

        quarantine.addReturn(return1);
        quarantine.addReturn(return2);
        quarantine.addReturn(return3);


        quarantine.processAllReturns();


        assertTrue(return1.isProcessed());
        assertTrue(return2.isProcessed());
        assertTrue(return3.isProcessed());
        assertEquals(ReturnAction.RESTOCKED, return1.getAction());
        assertEquals(ReturnAction.DISCARDED, return2.getAction());
        assertEquals(ReturnAction.RESTOCKED, return3.getAction());
    }

    @Test
    void testProcessAllReturns_AlreadyProcessedReturn() {

        Return testReturn = createTestReturn("RET-008", "SKU0008", ReturnReason.CUSTOMER_REMORSE);
        testReturn.setProcessed(true);
        testReturn.setAction(ReturnAction.RESTOCKED);
        quarantine.addReturn(testReturn);


        quarantine.processAllReturns();


        assertTrue(testReturn.isProcessed());
        assertEquals(ReturnAction.RESTOCKED, testReturn.getAction());
    }

    @Test
    void testProcessAllReturns_NonExistentSKU() {

        Return testReturn = createTestReturn("RET-009", "SKU9999", ReturnReason.CUSTOMER_REMORSE);
        quarantine.addReturn(testReturn);


        quarantine.processAllReturns();


        assertTrue(testReturn.isProcessed());
        assertEquals(ReturnAction.RESTOCKED, testReturn.getAction());
    }

    @Test
    void testProcessAllReturns_WarehouseCapacityExceeded() {

        ArrayList<ArrayList<Bay>> layout = warehouse.getLayout();
        int totalCapacity = 0;
        for (ArrayList<Bay> aisle : layout) {
            for (Bay bay : aisle) {
                if (bay != null) {
                    totalCapacity += bay.getCapacityBoxes();
                    while (bay.getBoxes().size() < bay.getCapacityBoxes() - 1) {
                        Box fillerBox = new Box("FILLER-" + System.nanoTime(), LocalDateTime.now(), null);
                        bay.addBox(fillerBox);
                    }
                }
            }
        }

        Return testReturn = createTestReturn("RET-010", "SKU0001", ReturnReason.CUSTOMER_REMORSE);
        quarantine.addReturn(testReturn);

        quarantine.processAllReturns();

        assertTrue(testReturn.isProcessed());
        assertEquals(ReturnAction.RESTOCKED, testReturn.getAction());
    }

    @Test
    void testProcessAllReturns_MultipleWarehousesAvailable() {
        Warehouse w1 = warehouseRepository.getWarehouse("W1");
        for (ArrayList<Bay> aisle : w1.getLayout()) {
            for (Bay bay : aisle) {
                if (bay != null) {
                    while (bay.getBoxes().size() < bay.getCapacityBoxes()) {
                        Box fillerBox = new Box("FILLER-" + System.nanoTime(), LocalDateTime.now(), null);
                        bay.addBox(fillerBox);
                    }
                }
            }
        }

        Return testReturn = createTestReturn("RET-011", "SKU0001", ReturnReason.CUSTOMER_REMORSE);
        quarantine.addReturn(testReturn);

        quarantine.processAllReturns();

        assertTrue(testReturn.isProcessed());
        assertEquals(ReturnAction.RESTOCKED, testReturn.getAction());
    }

    @Test
    void testRestockedBoxHasCorrectItems() {
        Return testReturn = createTestReturn("RET-012", "SKU0001", ReturnReason.CUSTOMER_REMORSE);
        quarantine.addReturn(testReturn);

        quarantine.processAllReturns();

        boolean boxFound = false;
        for (Warehouse wh : warehouses) {
            for (Bay bay : wh.getBays()) {
                for (Box box : bay.getBoxes()) {
                    if (box.getBoxId().startsWith("RET-")) {
                        boxFound = true;
                        assertFalse(box.getItems().isEmpty(), "Box should contain items");
                    }
                }
            }
        }
        assertTrue(boxFound, "Restocked box should be found in warehouse");
    }
}
