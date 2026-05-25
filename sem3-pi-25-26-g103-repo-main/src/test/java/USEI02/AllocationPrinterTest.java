package USEI02;

import Model.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import UI.Utils.AllocationPrinter;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for AllocationPrinter covering election logic and output formatting.
 */
class AllocationPrinterTest {
    private List<Allocation> allocations;
    private OrderLine eligibleLine;
    private OrderLine partialLine;
    private OrderLine undispatchableLine;
    private SKU sku;
    private Box box;
    private Bay bay;

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setUp() {
        outContent.reset();
        System.setOut(new PrintStream(outContent));

        sku = new SKU("SKU123");

        eligibleLine = new OrderLine("ORD001", 1, sku, 10);
        partialLine = new OrderLine("ORD001", 2, sku, 20);
        undispatchableLine = new OrderLine("ORD002", 1, sku, 15);

        bay = new Bay("WH1", 1, 2, 5);
        box = new Box("BOX123", LocalDateTime.now(), LocalDate.now().plusDays(30));
        box.setAssignedBay(bay);
        Map<Box, Integer> boxUsed = new HashMap<>();
        boxUsed.put(box, 5);

        List<Map<Box, Integer>> boxes = new ArrayList<>();
        boxes.add(boxUsed);

        Allocation eligibleAllocation = new Allocation(eligibleLine, 10, boxes);
        Allocation partialAllocation = new Allocation(partialLine, 10, boxes);
        Allocation undispatchableAllocation = new Allocation(undispatchableLine, 0, new ArrayList<>());

        allocations = new ArrayList<>();
        allocations.add(eligibleAllocation);
        allocations.add(partialAllocation);
        allocations.add(undispatchableAllocation);
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);

        allocations.clear();
        outContent.reset();
    }

    @Test
    void elect_StrictMode() {
        AllocationPrinter.elect(allocations, true);

        assertEquals(Status.ELIGIBLE, eligibleLine.getStatus());
        assertEquals(Status.UNDISPATCHABLE, partialLine.getStatus());
        assertEquals(Status.UNDISPATCHABLE, undispatchableLine.getStatus());

        String output = outContent.toString();
        assertTrue(output.contains("Order ORD001"));
        assertTrue(output.contains("Order ORD002"));
        assertTrue(output.contains("SKU: SKU123"));
        assertTrue(output.contains("Eligibility: ELIGIBLE"));
        assertTrue(output.contains("Eligibility: UNDISPATCHABLE"));
    }

    @Test
    void elect_NonStrictMode() {
        AllocationPrinter.elect(allocations, false);

        assertEquals(Status.ELIGIBLE, eligibleLine.getStatus());
        assertEquals(Status.PARTIAL, partialLine.getStatus());
        assertEquals(Status.UNDISPATCHABLE, undispatchableLine.getStatus());

        String output = outContent.toString();
        assertTrue(output.contains("Order ORD001"));
        assertTrue(output.contains("Order ORD002"));
        assertTrue(output.contains("SKU: SKU123"));
        assertTrue(output.contains("Eligibility: ELIGIBLE"));
        assertTrue(output.contains("Eligibility: PARTIAL"));
        assertTrue(output.contains("Eligibility: UNDISPATCHABLE"));
    }

    @Test
    void elect_EmptyList() {
        List<Allocation> emptyList = new ArrayList<>();
        AllocationPrinter.elect(emptyList, false);

        String output = outContent.toString().trim();
        assertEquals("", output);
    }

    @Test
    void elect_GroupsHeadersByOrder_PrintsOncePerOrder() {
        // Arrange: two allocations for the same order, then one for a different order
        SKU localSku = new SKU("SKU-A");
        OrderLine l1 = new OrderLine("ORD100", 1, localSku, 5);
        OrderLine l2 = new OrderLine("ORD100", 2, localSku, 7);
        OrderLine l3 = new OrderLine("ORD200", 1, localSku, 3);

        Bay localBay = new Bay("WH1", 3, 4, 10);
        Box b = new Box("B-001", LocalDateTime.now(), LocalDate.now().plusDays(10));
        b.setAssignedBay(localBay);
        Map<Box, Integer> m = new HashMap<>();
        m.put(b, 3);
        List<Map<Box, Integer>> bl = new ArrayList<>();
        bl.add(m);

        List<Allocation> list = new ArrayList<>();
        list.add(new Allocation(l1, 5, bl));
        list.add(new Allocation(l2, 7, bl));
        list.add(new Allocation(l3, 3, bl));

        // Act
        AllocationPrinter.elect(list, false);
        String output = outContent.toString();

        // Assert: header appears exactly once for each order
        int ord100Headers = output.split("-------Order ORD100-------", -1).length - 1;
        int ord200Headers = output.split("-------Order ORD200-------", -1).length - 1;
        assertEquals(1, ord100Headers);
        assertEquals(1, ord200Headers);
    }

    @Test
    void elect_PrintsMultipleBoxes_WithAisleAndBay() {
        // Arrange
        SKU localSku = new SKU("SKU-B");
        OrderLine l1 = new OrderLine("ORD300", 1, localSku, 10);

        Bay bay1 = new Bay("WH1", 3, 4, 10);
        Bay bay2 = new Bay("WH1", 5, 6, 10);
        Box b1 = new Box("B1", LocalDateTime.now(), LocalDate.now().plusDays(20));
        b1.setAssignedBay(bay1);
        Box b2 = new Box("B2", LocalDateTime.now(), LocalDate.now().plusDays(20));
        b2.setAssignedBay(bay2);

        Map<Box, Integer> used1 = new HashMap<>();
        used1.put(b1, 5);
        Map<Box, Integer> used2 = new HashMap<>();
        used2.put(b2, 2);

        List<Map<Box, Integer>> boxes = new ArrayList<>();
        boxes.add(used1);
        boxes.add(used2);

        List<Allocation> list = new ArrayList<>();
        list.add(new Allocation(l1, 7, boxes));

        // Act
        AllocationPrinter.elect(list, false);
        String output = outContent.toString();

        // Assert: Box details printed for both boxes
        assertTrue(output.contains("Box(es): "));
        assertTrue(output.contains("B1"));
        assertTrue(output.contains("(Aisle: 3; Bay: 4)"));
        assertTrue(output.contains("B2"));
        assertTrue(output.contains("(Aisle: 5; Bay: 6)"));
    }

    @Test
    void elect_DoesNotPrintBoxes_WhenAllocatedIsZero() {
        // Arrange: allocated is zero but boxes list is not empty
        SKU localSku = new SKU("SKU-C");
        OrderLine l1 = new OrderLine("ORD400", 1, localSku, 8);

        Bay bay1 = new Bay("WH1", 1, 1, 10);
        Box b1 = new Box("B-0", LocalDateTime.now(), LocalDate.now().plusDays(5));
        b1.setAssignedBay(bay1);
        Map<Box, Integer> used = new HashMap<>();
        used.put(b1, 1);
        List<Map<Box, Integer>> boxes = new ArrayList<>();
        boxes.add(used);

        List<Allocation> list = new ArrayList<>();
        list.add(new Allocation(l1, 0, boxes));

        // Act
        AllocationPrinter.elect(list, true); // strict flag doesn't matter here
        String output = outContent.toString();

        // Assert: no "Box(es)" section is printed when allocated is zero
        assertFalse(output.contains("Box(es): "));
    }
}
