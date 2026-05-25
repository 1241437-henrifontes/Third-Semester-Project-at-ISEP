package USEI02;

import Model.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Allocation model covering constructors, getters, and setters.
 */
class AllocationTest {
    private OrderLine orderLine;
    private List<Map<Box, Integer>> boxes;
    private Box box1;
    private Box box2;
    Map<Box, Integer> box1Used;
    Map<Box, Integer> box2Used;
    private SKU sku;

    @BeforeEach
    void setUp() {
        sku = new SKU("SKU123");
        orderLine = new OrderLine("ORD001", 1, sku, 10);

        box1 = new Box("BOX123", LocalDateTime.now(), LocalDate.now().plusDays(30));
        box2 = new Box("BOX456", LocalDateTime.now(), LocalDate.now().plusDays(60));

        boxes = new ArrayList<>();

        box1Used = new HashMap<>();
        box2Used = new HashMap<>();
        box1Used.put(box1, 5);
        box2Used.put(box2, 5);

        boxes.add(box1Used);
        boxes.add(box2Used);
    }

    @AfterEach
    void tearDown() {
        orderLine = null;
        boxes = null;
        box1 = null;
        box2 = null;
        sku = null;
    }

    @Test
    void testDefaultConstructor() {
        Allocation allocation = new Allocation();

        assertNull(allocation.getLine());
        assertNull(allocation.getAllocatedQty());
        assertNotNull(allocation.getBoxes());
        assertTrue(allocation.getBoxes().isEmpty());
    }

    @Test
    void testParameterizedConstructor() {
        Allocation allocation = new Allocation(orderLine, 10, boxes);

        assertEquals(orderLine, allocation.getLine());
        assertEquals(10, allocation.getAllocatedQty());
        assertEquals(boxes, allocation.getBoxes());
        assertEquals(2, allocation.getBoxes().size());
    }

    @Test
    void getLine() {
        Allocation allocation = new Allocation(orderLine, 10, boxes);

        assertEquals(orderLine, allocation.getLine());
        assertEquals("ORD001", allocation.getLine().getOrderId());
        assertEquals(1, allocation.getLine().getLineNumber());
    }

    @Test
    void setLine() {
        Allocation allocation = new Allocation();

        assertNull(allocation.getLine());

        allocation.setLine(orderLine);

        assertEquals(orderLine, allocation.getLine());

        OrderLine newOrderLine = new OrderLine("ORD002", 2, sku, 20);

        allocation.setLine(newOrderLine);

        assertEquals(newOrderLine, allocation.getLine());
        assertEquals("ORD002", allocation.getLine().getOrderId());
        assertEquals(2, allocation.getLine().getLineNumber());
    }

    @Test
    void getAllocatedQty() {
        Allocation allocation = new Allocation(orderLine, 10, boxes);

        assertEquals(10, allocation.getAllocatedQty());
    }

    @Test
    void setAllocatedQty() {
        Allocation allocation = new Allocation();

        assertNull(allocation.getAllocatedQty());

        allocation.setAllocatedQty(10);

        assertEquals(10, allocation.getAllocatedQty());

        allocation.setAllocatedQty(20);

        assertEquals(20, allocation.getAllocatedQty());

        allocation.setAllocatedQty(0);

        assertEquals(0, allocation.getAllocatedQty());
    }

    @Test
    void getBoxes() {
        Allocation allocation = new Allocation(orderLine, 10, boxes);

        assertEquals(boxes, allocation.getBoxes());
        assertEquals(2, allocation.getBoxes().size());
        assertTrue(allocation.getBoxes().contains(box1Used));
        assertTrue(allocation.getBoxes().contains(box2Used));
    }

    @Test
    void setBoxes() {
        Allocation allocation = new Allocation();

        assertNotNull(allocation.getBoxes());
        assertTrue(allocation.getBoxes().isEmpty());

        allocation.setBoxes(boxes);

        assertEquals(boxes, allocation.getBoxes());
        assertEquals(2, allocation.getBoxes().size());

        List<Map<Box, Integer>> newBoxes = new ArrayList<>();
        newBoxes.add(box1Used);

        allocation.setBoxes(newBoxes);

        assertEquals(newBoxes, allocation.getBoxes());
        assertEquals(1, allocation.getBoxes().size());
        assertTrue(allocation.getBoxes().contains(box1Used));
        assertFalse(allocation.getBoxes().contains(box2Used));

        allocation.setBoxes(new ArrayList<>());

        assertNotNull(allocation.getBoxes());
        assertTrue(allocation.getBoxes().isEmpty());
    }
}
