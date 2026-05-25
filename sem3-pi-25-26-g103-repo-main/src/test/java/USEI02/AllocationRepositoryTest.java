package USEI02;

import Repositories.AllocationRepository;
import Repositories.ItemRepository;
import Repositories.WarehouseRepository;
import Model.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class AllocationRepositoryTest {
    private AllocationRepository allocationRepository;
    private WarehouseRepository warehouseRepository;
    private ItemRepository itemRepository;
    private PriorityQueue<Order> orders;
    private Warehouse warehouse;
    private Bay bay;
    private Box box;
    private Item item;
    private SKU sku;
    private Order order;
    private OrderLine orderLine;

    @BeforeEach
    void setUp() {
        allocationRepository = AllocationRepository.getInstance();
        warehouseRepository = WarehouseRepository.getInstance();
        itemRepository = ItemRepository.getInstance();

        allocationRepository.getAllocationList().clear();

        sku = new SKU("SKU123");
        item = new Item(sku, "Test Item", "Test Category", "EA", 1.0f, 1.0f);

        if (itemRepository.getItems() == null) {
            itemRepository.loadItems();
        }
        List<Item> items = new ArrayList<>();
        items.add(item);
        itemRepository.getItems().clear();
        itemRepository.getItems().addAll(items);

        box = new Box("BOX123", LocalDateTime.now(), LocalDate.now().plusDays(30));
        box.addItem(item, 10);

        bay = new Bay("WH1", 1, 1, 5);

        warehouse = new Warehouse("WH1", 5, 5);
        warehouse.addBay(bay);
        List<Box> initialBoxes = new ArrayList<>();
        initialBoxes.add(box);
        warehouse.FIFOAndFEFOOrder(initialBoxes, warehouse.getBays());

        warehouseRepository.getWarehouses().clear();
        warehouseRepository.updateWarehouse(warehouse);

        orderLine = new OrderLine("ORD123", 1, sku, 5);

        order = new Order("ORD123", LocalDateTime.now().plusDays(1), 1);
        List<OrderLine> orderLines = new ArrayList<>();
        orderLines.add(orderLine);
        order.setLines(orderLines);

        orders = new PriorityQueue<>();
        orders.add(order);
    }

    @AfterEach
    void tearDown() {
        allocationRepository.getAllocationList().clear();

        warehouseRepository.getWarehouses().clear();

        if (itemRepository.getItems() != null) {
            itemRepository.getItems().clear();
        }
    }

    @Test
    void getInstance() {
        assertNotNull(AllocationRepository.getInstance());

        AllocationRepository instance1 = AllocationRepository.getInstance();
        AllocationRepository instance2 = AllocationRepository.getInstance();
        assertSame(instance1, instance2);
    }

    @Test
    void getAllocationList() {
        assertNotNull(allocationRepository.getAllocationList());

        assertTrue(allocationRepository.getAllocationList().isEmpty());

        Allocation allocation = new Allocation(orderLine, 5, new ArrayList<>());
        allocationRepository.getAllocationList().add(allocation);

        assertEquals(1, allocationRepository.getAllocationList().size());
        assertEquals(allocation, allocationRepository.getAllocationList().get(0));
    }

    @Test
    void allocate() {
        allocationRepository.allocate(orders);

        List<Allocation> allocations = allocationRepository.getAllocationList();
        assertFalse(allocations.isEmpty());
        assertEquals(1, allocations.size());

        Allocation allocation = allocations.get(0);
        assertEquals(orderLine, allocation.getLine());
        assertEquals(5, allocation.getAllocatedQty());
        assertFalse(allocation.getBoxes().isEmpty());

        assertEquals(1, allocation.getBoxes().size());
        assertEquals("BOX123", allocation.getBoxes().get(0).keySet().iterator().next().getBoxId());
    }

    @Test
    void allocateWithInsufficientStock() {
        orderLine.setQty(20);

        allocationRepository.allocate(orders);

        List<Allocation> allocations = allocationRepository.getAllocationList();
        assertFalse(allocations.isEmpty());

        Allocation allocation = allocations.get(0);
        assertEquals(orderLine, allocation.getLine());
        assertEquals(10, allocation.getAllocatedQty());
        assertFalse(allocation.getBoxes().isEmpty());
    }

    @Test
    void allocateWithMultipleWarehouses() {
        Warehouse warehouse2 = new Warehouse("WH2", 5, 5);
        Bay bay2 = new Bay("WH2", 1, 1, 5);
        Box box2 = new Box("BOX456", LocalDateTime.now(), LocalDate.now().plusDays(30));
        box2.addItem(item, 5);
        warehouse2.addBay(bay2);
        List<Box> boxes2 = new ArrayList<>();
        boxes2.add(box2);
        warehouse2.FIFOAndFEFOOrder(boxes2, warehouse2.getBays());

        warehouseRepository.updateWarehouse(warehouse2);

        orderLine.setQty(15);

        allocationRepository.allocate(orders);

        List<Allocation> allocations = allocationRepository.getAllocationList();
        assertFalse(allocations.isEmpty());

        Allocation allocation = allocations.get(0);
        assertEquals(orderLine, allocation.getLine());
        assertEquals(15, allocation.getAllocatedQty());
        assertEquals(2, allocation.getBoxes().size());
    }

    @Test
    void allocateWithNoStock() {
        SKU newSku = new SKU("SKU456");
        Item newItem = new Item(newSku, "New Item", "New Category", "EA", 1.0f, 1.0f);
        itemRepository.getItems().add(newItem);

        OrderLine newOrderLine = new OrderLine("ORD123", 2, newSku, 5);
        order.getLines().add(newOrderLine);

        allocationRepository.allocate(orders);

        List<Allocation> allocations = allocationRepository.getAllocationList();
        assertEquals(2, allocations.size());

        Allocation allocation = null;
        for (Allocation a : allocations) {
            if (a.getLine().equals(newOrderLine)) {
                allocation = a;
                break;
            }
        }

        assertNotNull(allocation);
        assertEquals(newOrderLine, allocation.getLine());
        assertEquals(0, allocation.getAllocatedQty());
        assertTrue(allocation.getBoxes().isEmpty());
    }

    @Test
    void allocateWithEmptyOrderLine() {
        Order emptyOrder = new Order("ORD456", LocalDateTime.now().plusDays(1), 1);
        emptyOrder.setLines(new ArrayList<>());

        PriorityQueue<Order> emptyOrders = new PriorityQueue<>();
        emptyOrders.add(emptyOrder);

        allocationRepository.allocate(emptyOrders);

        List<Allocation> allocations = allocationRepository.getAllocationList();
        assertTrue(allocations.isEmpty());
    }

    @Test
    void allocateAcrossMultipleBoxesSingleWarehouse() {
        // Add another box with the same item in the same warehouse
        Box secondBox = new Box("BOX789", LocalDateTime.now().minusHours(1), LocalDate.now().plusDays(60));
        secondBox.addItem(item, 8);
        List<Box> extra = new ArrayList<>();
        extra.add(secondBox);
        warehouse.FIFOAndFEFOOrder(extra, warehouse.getBays());

        // Request more than the first box has, but less than total
        orderLine.setQty(12);

        allocationRepository.allocate(orders);

        List<Allocation> allocations = allocationRepository.getAllocationList();
        assertEquals(1, allocations.size());
        Allocation allocation = allocations.get(0);
        assertEquals(12, allocation.getAllocatedQty());
        assertEquals(orderLine, allocation.getLine());
        assertEquals(2, allocation.getBoxes().size());

        // Verify that both box IDs were used in allocation (order may depend on FEFO/FIFO rules)
        List<String> usedBoxIds = new ArrayList<>();
        for (Map<Box, Integer> map : allocation.getBoxes()) {
            usedBoxIds.add(map.keySet().iterator().next().getBoxId());
        }
        assertTrue(usedBoxIds.contains("BOX123"));
        assertTrue(usedBoxIds.contains("BOX789"));
    }

    @Test
    void allocateExactMatchAndZeroQty() {
        // Exact match: 10 units available in BOX123
        orderLine.setQty(10);
        allocationRepository.allocate(orders);
        List<Allocation> allocations = allocationRepository.getAllocationList();
        assertEquals(1, allocations.size());
        Allocation allocation = allocations.get(0);
        assertEquals(10, allocation.getAllocatedQty());
        assertEquals(1, allocation.getBoxes().size());

        // Zero-quantity line should produce an allocation of 0 with no boxes
        Order zeroOrder = new Order("ORD-ZERO", LocalDateTime.now().plusDays(1), 1);
        OrderLine zeroLine = new OrderLine("ORD-ZERO", 1, sku, 0);
        List<OrderLine> zeroLines = new ArrayList<>();
        zeroLines.add(zeroLine);
        zeroOrder.setLines(zeroLines);
        PriorityQueue<Order> q = new PriorityQueue<>();
        q.add(zeroOrder);

        allocationRepository.getAllocationList().clear();
        allocationRepository.allocate(q);
        allocations = allocationRepository.getAllocationList();
        assertEquals(1, allocations.size());
        allocation = allocations.get(0);
        assertEquals(0, allocation.getAllocatedQty());
        assertTrue(allocation.getBoxes().isEmpty());
        assertEquals(zeroLine, allocation.getLine());
    }
}
