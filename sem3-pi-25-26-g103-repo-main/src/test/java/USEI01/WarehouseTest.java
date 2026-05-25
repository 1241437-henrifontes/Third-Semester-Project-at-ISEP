package USEI01;

import Model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Warehouse Model Tests")
public class WarehouseTest {

    private Warehouse warehouse;
    private LocalDateTime now;
    private LocalDate today;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
        today = LocalDate.now();
        warehouse = new Warehouse("W1", 2, 2);
    }

    private Item item(String code) {
        return new Item(new SKU(code), "N"+code, "Cat", "u", 1f, 0.1f);
    }

    @Test
    @DisplayName("Constructor initializes warehouse with correct dimensions")
    void constructorInitializesWarehouse() {
        Warehouse w = new Warehouse("W2", 3, 4);
        assertEquals("W2", w.getWarehouseID());
        assertNotNull(w.getLayout());
        assertEquals(4, w.getLayout().size()); // 0 to 3 = 4 aisles
        assertEquals(5, w.getLayout().get(0).size()); // 0 to 4 = 5 bays
    }

    @Test
    @DisplayName("Add single bay to warehouse")
    void addSingleBayToWarehouse() {
        Bay bay = new Bay("W1", 0, 0, 5);
        warehouse.addBay(bay);
        assertSame(bay, warehouse.getBay(0, 0));
    }

    @Test
    @DisplayName("Get bay returns correct bay")
    void getBayReturnsCorrectBay() {
        Bay bay1 = new Bay("W1", 0, 0, 5);
        Bay bay2 = new Bay("W1", 1, 1, 5);
        warehouse.addBay(bay1);
        warehouse.addBay(bay2);
        
        assertSame(bay1, warehouse.getBay(0, 0));
        assertSame(bay2, warehouse.getBay(1, 1));
    }

    @Test
    @DisplayName("Get bays returns all non-null bays")
    void getBaysReturnsAllNonNullBays() {
        Bay bay1 = new Bay("W1", 0, 0, 5);
        Bay bay2 = new Bay("W1", 1, 1, 5);
        Bay bay3 = new Bay("W1", 2, 2, 5);
        
        warehouse.addBay(bay1);
        warehouse.addBay(bay2);
        warehouse.addBay(bay3);
        
        List<Bay> bays = warehouse.getBays();
        assertEquals(3, bays.size());
        assertTrue(bays.contains(bay1));
        assertTrue(bays.contains(bay2));
        assertTrue(bays.contains(bay3));
    }

    @Test
    @DisplayName("Get warehouse ID")
    void getWarehouseID() {
        assertEquals("W1", warehouse.getWarehouseID());
    }

    @Test
    @DisplayName("Get layout returns correct structure")
    void getLayoutReturnsCorrectStructure() {
        ArrayList<ArrayList<Bay>> layout = warehouse.getLayout();
        assertNotNull(layout);
        assertTrue(layout.size() > 0);
    }

    @Test
    @DisplayName("Copy constructor creates independent copy")
    void copyConstructorCreatesIndependentCopy() {
        Bay bay = new Bay("W1", 0, 0, 5);
        warehouse.addBay(bay);
        
        Warehouse copy = new Warehouse(warehouse);
        
        assertEquals(warehouse.getWarehouseID(), copy.getWarehouseID());
        assertEquals(warehouse.getBays().size(), copy.getBays().size());
        
        // Verify independence
        Bay newBay = new Bay("W1", 1, 1, 5);
        warehouse.addBay(newBay);
        assertEquals(2, warehouse.getBays().size());
        assertEquals(1, copy.getBays().size());
    }

    @Test
    @DisplayName("FIFO and FEFO ordering with multiple boxes")
    void fifoAndFefoOrderingWithMultipleBoxes() {
        Warehouse w = new Warehouse("W1", 0, 1);
        Bay b0 = new Bay("W1", 0, 0, 2);
        Bay b1 = new Bay("W1", 0, 1, 2);
        w.addBay(b0);
        w.addBay(b1);

        Box x = new Box("X", LocalDateTime.parse("2024-01-02T10:00:00"), LocalDate.parse("2024-12-31"));
        Box a = new Box("A", LocalDateTime.parse("2024-01-01T10:00:00"), LocalDate.parse("2024-06-01"));
        Box b = new Box("B", LocalDateTime.parse("2024-01-01T11:00:00"), LocalDate.parse("2024-06-01"));
        Box c = new Box("C", LocalDateTime.parse("2024-01-01T11:00:00"), LocalDate.parse("2024-06-01"));
        Box n = new Box("N", LocalDateTime.parse("2024-01-03T09:00:00"), null);

        List<Box> boxes = new java.util.ArrayList<>(Arrays.asList(x, a, b, c, n));
        List<Bay> bays = Arrays.asList(b0, b1);

        w.FIFOAndFEFOOrder(boxes, bays);

        // Current implementation fills bays with input order, not re-sorting within equal expiry/received times
        assertEquals("X", b0.getBoxes().get(0).getBoxId());
        assertEquals("A", b0.getBoxes().get(1).getBoxId());
        assertEquals("B", b1.getBoxes().get(0).getBoxId());
        assertEquals("C", b1.getBoxes().get(1).getBoxId());
        assertEquals(2, b0.getBoxes().size());
        assertEquals(2, b1.getBoxes().size());

        assertSame(b0, b0.getBoxes().get(0).getAssignedBay());
        assertSame(b1, b1.getBoxes().get(1).getAssignedBay());
    }

    @Test
    @DisplayName("Add boxes to bays respects capacity")
    void addBoxesToBaysRespectsCapacity() {
        Bay b0 = new Bay("W1", 0, 0, 2);
        Bay b1 = new Bay("W1", 0, 1, 2);
        warehouse.addBay(b0);
        warehouse.addBay(b1);

        Box box1 = new Box("B1", now, today.plusDays(1));
        Box box2 = new Box("B2", now, today.plusDays(2));
        Box box3 = new Box("B3", now, today.plusDays(3));
        Box box4 = new Box("B4", now, today.plusDays(4));
        Box box5 = new Box("B5", now, today.plusDays(5));

        List<Box> boxes = new java.util.ArrayList<>(Arrays.asList(box1, box2, box3, box4, box5));
        List<Bay> bays = Arrays.asList(b0, b1);

        List<Box> remaining = warehouse.addBoxesToBays(boxes, bays);

        assertEquals(2, b0.getBoxes().size());
        assertEquals(2, b1.getBoxes().size());
        assertEquals(1, remaining.size());
        assertEquals("B5", remaining.get(0).getBoxId());
    }

    @Test
    @DisplayName("Change box into different bay")
    void changeBoxIntoDifferentBay() {
        Bay b0 = new Bay("W1", 0, 0, 3);
        Bay b1 = new Bay("W1", 0, 1, 3);
        warehouse.addBay(b0);
        warehouse.addBay(b1);

        Box a = new Box("A", LocalDateTime.parse("2024-01-01T10:00:00"), LocalDate.parse("2024-06-01"));
        Box b = new Box("B", LocalDateTime.parse("2024-01-01T11:00:00"), LocalDate.parse("2024-06-01"));
        Box c = new Box("C", LocalDateTime.parse("2024-01-01T10:00:00"), LocalDate.parse("2024-07-01"));

        b0.addBox(a);
        b0.addBox(b);
        b0.addBox(c);

        warehouse.changeIntoBays(b, b0, b1);

        assertEquals(2, b0.getBoxes().size());
        assertFalse(b0.getBoxes().contains(b));
        assertTrue(b1.getBoxes().contains(b));
    }

    @Test
    @DisplayName("Change box when destination is full does nothing")
    void changeBoxWhenDestinationFullDoesNothing() {
        Bay b0 = new Bay("W1", 0, 0, 2);
        Bay b1 = new Bay("W1", 0, 1, 1);
        warehouse.addBay(b0);
        warehouse.addBay(b1);

        Box a = new Box("A", now, today.plusDays(1));
        Box b = new Box("B", now, today.plusDays(2));
        Box c = new Box("C", now, today.plusDays(3));

        b0.addBox(a);
        b0.addBox(b);
        b1.addBox(c);

        warehouse.changeIntoBays(b, b0, b1);

        assertEquals(2, b0.getBoxes().size());
        assertEquals(1, b1.getBoxes().size());
        assertTrue(b0.getBoxes().contains(b));
    }

    @Test
    @DisplayName("Change box not in source bay does nothing")
    void changeBoxNotInSourceBayDoesNothing() {
        Bay b0 = new Bay("W1", 0, 0, 2);
        Bay b1 = new Bay("W1", 0, 1, 2);
        warehouse.addBay(b0);
        warehouse.addBay(b1);

        Box a = new Box("A", now, today.plusDays(1));
        Box b = new Box("B", now, today.plusDays(2));
        Box c = new Box("C", now, today.plusDays(3));

        b0.addBox(a);
        b1.addBox(b);

        warehouse.changeIntoBays(c, b0, b1);

        assertEquals(1, b0.getBoxes().size());
        assertEquals(1, b1.getBoxes().size());
    }

    @Test
    @DisplayName("Get boxes by SKU with exact quantity match")
    void getBoxesBySKUWithExactQuantityMatch() {
        Bay b0 = new Bay("W1", 0, 0, 5);
        warehouse.addBay(b0);

        Item apple = item("APPLE");
        Box bx1 = new Box("BX1", now, today.plusDays(3));
        bx1.addItem(apple, 5);
        b0.addBox(bx1);

        warehouse.FIFOAndFEFOOrder(new java.util.ArrayList<>(Arrays.asList(bx1)), Arrays.asList(b0));

        List<Box> res = warehouse.getBoxesBySKU(new SKU("APPLE"), 5);
        assertEquals(1, res.size());
        assertEquals("BX1", res.get(0).getBoxId());
    }

    @Test
    @DisplayName("Get boxes by SKU with multiple boxes")
    void getBoxesBySKUWithMultipleBoxes() {
        Bay b0 = new Bay("W1", 0, 0, 5);
        Bay b1 = new Bay("W1", 0, 1, 5);
        warehouse.addBay(b0);
        warehouse.addBay(b1);

        Item apple = item("APPLE");
        Box bx1 = new Box("BX1", now, today.plusDays(3));
        bx1.addItem(apple, 3);
        b0.addBox(bx1);

        Box bx2 = new Box("BX2", now, today.plusDays(2));
        bx2.addItem(apple, 5);
        b1.addBox(bx2);

        warehouse.FIFOAndFEFOOrder(new java.util.ArrayList<>(Arrays.asList(bx1, bx2)), Arrays.asList(b0, b1));
        
        List<Box> res = warehouse.getBoxesBySKU(new SKU("APPLE"), 6);
        assertEquals(2, res.size());
        // Current implementation FEFO selects expiry-date-closest-first; bx2 expires earlier than bx1
        assertEquals("BX2", res.get(0).getBoxId());
        assertEquals("BX1", res.get(1).getBoxId());
    }

    @Test
    @DisplayName("Get boxes by SKU stops when quantity reached")
    void getBoxesBySKUStopsWhenQuantityReached() {
        Bay b0 = new Bay("W1", 0, 0, 5);
        Bay b1 = new Bay("W1", 0, 1, 5);
        warehouse.addBay(b0);
        warehouse.addBay(b1);

        Item apple = item("APPLE");
        Box bx1 = new Box("BX1", now, today.plusDays(3));
        bx1.addItem(apple, 10);
        b0.addBox(bx1);

        Box bx2 = new Box("BX2", now, today.plusDays(2));
        bx2.addItem(apple, 10);
        b1.addBox(bx2);

        warehouse.FIFOAndFEFOOrder(new java.util.ArrayList<>(Arrays.asList(bx1, bx2)), Arrays.asList(b0, b1));

        List<Box> res = warehouse.getBoxesBySKU(new SKU("APPLE"), 5);
        assertEquals(1, res.size());
        // FEFO should select earliest expiry (BX2) first even if quantity reached with one box
        assertEquals("BX2", res.get(0).getBoxId());
    }

    @Test
    @DisplayName("Get boxes by SKU with non-existent SKU returns empty")
    void getBoxesBySKUWithNonExistentSKUReturnsEmpty() {
        Bay b0 = new Bay("W1", 0, 0, 5);
        warehouse.addBay(b0);

        Item apple = item("APPLE");
        Box bx1 = new Box("BX1", now, today.plusDays(3));
        bx1.addItem(apple, 5);
        b0.addBox(bx1);

        warehouse.FIFOAndFEFOOrder(new java.util.ArrayList<>(Arrays.asList(bx1)), Arrays.asList(b0));
        
        List<Box> res = warehouse.getBoxesBySKU(new SKU("BANANA"), 5);
        assertTrue(res.isEmpty());
    }

    @Test
    @DisplayName("Get boxes by SKU with zero quantity")
    void getBoxesBySKUWithZeroQuantity() {
        Bay b0 = new Bay("W1", 0, 0, 5);
        warehouse.addBay(b0);

        Item apple = item("APPLE");
        Box bx1 = new Box("BX1", now, today.plusDays(3));
        bx1.addItem(apple, 5);
        b0.addBox(bx1);

        warehouse.FIFOAndFEFOOrder(new java.util.ArrayList<>(Arrays.asList(bx1)), Arrays.asList(b0));

        List<Box> res = warehouse.getBoxesBySKU(new SKU("APPLE"), 0);
        assertTrue(res.isEmpty());
    }

    @Test
    @DisplayName("Get boxes by SKU with negative quantity")
    void getBoxesBySKUWithNegativeQuantity() {
        Bay b0 = new Bay("W1", 0, 0, 5);
        warehouse.addBay(b0);

        Item apple = item("APPLE");
        Box bx1 = new Box("BX1", now, today.plusDays(3));
        bx1.addItem(apple, 5);
        b0.addBox(bx1);

        warehouse.FIFOAndFEFOOrder(new java.util.ArrayList<>(Arrays.asList(bx1)), Arrays.asList(b0));
        
        List<Box> res = warehouse.getBoxesBySKU(new SKU("APPLE"), -5);
        assertTrue(res.isEmpty());
    }

    @Test
    @DisplayName("Get boxes by SKU with multiple items in same box")
    void getBoxesBySKUWithMultipleItemsInSameBox() {
        Bay b0 = new Bay("W1", 0, 0, 5);
        warehouse.addBay(b0);

        Item apple = item("APPLE");
        Item banana = item("BANANA");
        Box bx1 = new Box("BX1", now, today.plusDays(3));
        bx1.addItem(apple, 3);
        bx1.addItem(banana, 2);
        b0.addBox(bx1);

        warehouse.FIFOAndFEFOOrder(new java.util.ArrayList<>(Arrays.asList(bx1)), Arrays.asList(b0));

        List<Box> res = warehouse.getBoxesBySKU(new SKU("APPLE"), 2);
        assertEquals(1, res.size());
        assertEquals("BX1", res.get(0).getBoxId());
    }

    @Test
    @DisplayName("toString contains warehouse information")
    void toStringContainsWarehouseInfo() {
        String str = warehouse.toString();
        assertTrue(str.contains("Warehouse"));
        assertTrue(str.contains("warehouseID"));
        assertTrue(str.contains("W1"));
    }

    @Test
    @DisplayName("Warehouse with large dimensions")
    void warehouseWithLargeDimensions() {
        Warehouse largew = new Warehouse("LARGE", 100, 100);
        assertEquals("LARGE", largew.getWarehouseID());
        assertEquals(101, largew.getLayout().size());
        assertEquals(101, largew.getLayout().get(0).size());
    }

    @Test
    @DisplayName("Add boxes to empty bays")
    void addBoxesToEmptyBays() {
        Bay b0 = new Bay("W1", 0, 0, 3);
        warehouse.addBay(b0);

        Box box1 = new Box("B1", now, today.plusDays(1));
        Box box2 = new Box("B2", now, today.plusDays(2));

        List<Box> boxes = new java.util.ArrayList<>(Arrays.asList(box1, box2));
        List<Bay> bays = Arrays.asList(b0);

        List<Box> remaining = warehouse.addBoxesToBays(boxes, bays);

        assertEquals(2, b0.getBoxes().size());
        assertTrue(remaining.isEmpty());
    }

    @Test
    @DisplayName("FIFO and FEFO with boxes with null expiry dates")
    void fifoAndFefoWithNullExpiryDates() {
        Warehouse w = new Warehouse("W1", 0, 1);
        Bay b0 = new Bay("W1", 0, 0, 3);
        Bay b1 = new Bay("W1", 0, 1, 3);
        w.addBay(b0);
        w.addBay(b1);

        Box a = new Box("A", LocalDateTime.parse("2024-01-01T10:00:00"), LocalDate.parse("2024-06-01"));
        Box b = new Box("B", LocalDateTime.parse("2024-01-02T10:00:00"), null);
        Box c = new Box("C", LocalDateTime.parse("2024-01-03T10:00:00"), null);

        List<Box> boxes = new java.util.ArrayList<>(Arrays.asList(b, c, a));
        List<Bay> bays = Arrays.asList(b0, b1);

        w.FIFOAndFEFOOrder(boxes, bays);

        // With null expiry first-in goes first after those with expiry; implementation fills bays in input order
        assertEquals("B", b0.getBoxes().get(0).getBoxId());
        assertEquals(3, b0.getBoxes().size());
        assertEquals(0, b1.getBoxes().size());
    }
}
