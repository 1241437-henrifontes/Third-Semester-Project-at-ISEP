package USEI01;

import Model.Bay;
import Model.Box;
import Model.Item;
import Model.SKU;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Box model validating item aggregation, quantity handling,
 * bay assignment, and date fields.
 */
@DisplayName("Box Model Tests")
public class BoxTest {

    private Box box;
    private Item item1, item2, item3;
    private LocalDateTime now;
    private LocalDate today;
    private Bay bay;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
        today = LocalDate.now();
        box = new Box("B1", now, today.plusDays(10));
        item1 = makeItem("SKU001", "Item1");
        item2 = makeItem("SKU002", "Item2");
        item3 = makeItem("SKU003", "Item3");
        bay = new Bay("W1", 0, 0, 5);
    }

    private Item makeItem(String skuCode, String name) {
        return new Item(new SKU(skuCode), name, "Category", "unit", 1.0f, 0.1f);
    }

    @Test
    @DisplayName("Constructor initializes all fields correctly")
    void constructorInitializesFields() {
        LocalDateTime recv = LocalDateTime.parse("2024-01-15T10:30:00");
        LocalDate exp = LocalDate.parse("2024-12-31");
        Box b = new Box("BOX123", recv, exp);
        
        assertEquals("BOX123", b.getBoxId());
        assertEquals(recv, b.getReceivedAt());
        assertEquals(exp, b.getExpiryDate());
        assertNotNull(b.getItems());
        assertTrue(b.getItems().isEmpty());
        assertNull(b.getAssignedBay());
    }

    @Test
    @DisplayName("Add single item with quantity")
    void addSingleItemWithQuantity() {
        box.addItem(item1, 5);
        assertEquals(1, box.getItems().size());
        assertEquals(5, box.getItems().get(item1));
    }

    @Test
    @DisplayName("Add same item multiple times aggregates quantities")
    void addSameItemMultipleTimesAggregates() {
        box.addItem(item1, 3);
        box.addItem(item1, 2);
        box.addItem(item1, 5);
        
        assertEquals(1, box.getItems().size());
        assertEquals(10, box.getItems().get(item1));
    }

    @Test
    @DisplayName("Add different items creates separate entries")
    void addDifferentItemsCreatesSeparateEntries() {
        box.addItem(item1, 3);
        box.addItem(item2, 5);
        box.addItem(item3, 2);
        
        assertEquals(3, box.getItems().size());
        assertEquals(3, box.getItems().get(item1));
        assertEquals(5, box.getItems().get(item2));
        assertEquals(2, box.getItems().get(item3));
    }

    @Test
    @DisplayName("Add item with zero quantity")
    void addItemWithZeroQuantity() {
        box.addItem(item1, 0);
        assertEquals(1, box.getItems().size());
        assertEquals(0, box.getItems().get(item1));
    }

    @Test
    @DisplayName("Add item with negative quantity")
    void addItemWithNegativeQuantity() {
        box.addItem(item1, 5);
        box.addItem(item1, -2);
        assertEquals(1, box.getItems().size());
        assertEquals(3, box.getItems().get(item1));
    }

    @Test
    @DisplayName("Add large quantities")
    void addLargeQuantities() {
        box.addItem(item1, 1000000);
        box.addItem(item1, 2000000);
        assertEquals(3000000, box.getItems().get(item1));
    }

    @Test
    @DisplayName("Getters return correct values")
    void gettersReturnCorrectValues() {
        assertEquals("B1", box.getBoxId());
        assertEquals(now, box.getReceivedAt());
        assertEquals(today.plusDays(10), box.getExpiryDate());
    }

    @Test
    @DisplayName("Set and get assigned bay")
    void setAndGetAssignedBay() {
        assertNull(box.getAssignedBay());
        box.setAssignedBay(bay);
        assertSame(bay, box.getAssignedBay());
    }

    @Test
    @DisplayName("Change assigned bay")
    void changeAssignedBay() {
        box.setAssignedBay(bay);
        Bay bay2 = new Bay("W2", 1, 1, 5);
        box.setAssignedBay(bay2);
        assertSame(bay2, box.getAssignedBay());
        assertNotSame(bay, box.getAssignedBay());
    }

    @Test
    @DisplayName("Copy constructor creates independent copy")
    void copyConstructorCreatesIndependentCopy() {
        box.addItem(item1, 5);
        box.addItem(item2, 3);
        box.setAssignedBay(bay);
        
        Box copy = new Box(box);
        
        assertEquals(box.getBoxId(), copy.getBoxId());
        assertEquals(box.getReceivedAt(), copy.getReceivedAt());
        assertEquals(box.getExpiryDate(), copy.getExpiryDate());
        assertEquals(box.getItems().size(), copy.getItems().size());
        assertEquals(5, copy.getItems().get(item1));
        assertEquals(3, copy.getItems().get(item2));
        assertSame(bay, copy.getAssignedBay());
    }

    @Test
    @DisplayName("Copy constructor creates new HashMap instance")
    void copyConstructorCreatesNewHashMapInstance() {
        box.addItem(item1, 5);
        Box copy = new Box(box);
        
        // Modify original's items
        box.addItem(item1, 10);
        
        // Copy should not be affected
        assertEquals(5, copy.getItems().get(item1));
        assertEquals(15, box.getItems().get(item1));
    }

    @Test
    @DisplayName("Copy constructor with empty items")
    void copyConstructorWithEmptyItems() {
        Box copy = new Box(box);
        assertTrue(copy.getItems().isEmpty());
        assertEquals(box.getBoxId(), copy.getBoxId());
    }

    @Test
    @DisplayName("Copy constructor with null expiry date")
    void copyConstructorWithNullExpiryDate() {
        Box boxNoExpiry = new Box("B_NO_EXP", now, null);
        Box copy = new Box(boxNoExpiry);
        
        assertNull(copy.getExpiryDate());
        assertEquals("B_NO_EXP", copy.getBoxId());
    }

    @Test
    @DisplayName("toString contains relevant information")
    void toStringContainsRelevantInfo() {
        box.addItem(item1, 5);
        String str = box.toString();
        assertTrue(str.contains("boxId"));
        assertTrue(str.contains("B1"));
        assertTrue(str.contains("receivedAt"));
        assertTrue(str.contains("expiryDate"));
        assertTrue(str.contains("items"));
    }

    @Test
    @DisplayName("Multiple items with different quantities")
    void multipleItemsWithDifferentQuantities() {
        box.addItem(item1, 10);
        box.addItem(item2, 20);
        box.addItem(item3, 30);
        box.addItem(item1, 5);
        
        assertEquals(3, box.getItems().size());
        assertEquals(15, box.getItems().get(item1));
        assertEquals(20, box.getItems().get(item2));
        assertEquals(30, box.getItems().get(item3));
    }

    @Test
    @DisplayName("Items map is mutable through getter")
    void itemsMapIsMutableThroughGetter() {
        box.addItem(item1, 5);
        HashMap<Item, Integer> items = box.getItems();
        items.put(item2, 10);
        
        assertEquals(2, box.getItems().size());
        assertEquals(10, box.getItems().get(item2));
    }

    @Test
    @DisplayName("Box with past received date")
    void boxWithPastReceivedDate() {
        LocalDateTime pastDate = LocalDateTime.parse("2020-01-01T00:00:00");
        Box oldBox = new Box("OLD", pastDate, today);
        assertEquals(pastDate, oldBox.getReceivedAt());
    }

    @Test
    @DisplayName("Box with future expiry date")
    void boxWithFutureExpiryDate() {
        LocalDate futureDate = today.plusYears(5);
        Box futureBox = new Box("FUTURE", now, futureDate);
        assertEquals(futureDate, futureBox.getExpiryDate());
    }

    @Test
    @DisplayName("Box with same received and expiry dates")
    void boxWithSameReceivedAndExpiryDates() {
        LocalDateTime sameTime = LocalDateTime.parse("2024-06-15T12:00:00");
        LocalDate sameDate = LocalDate.parse("2024-06-15");
        Box sameBox = new Box("SAME", sameTime, sameDate);
        assertEquals(sameTime, sameBox.getReceivedAt());
        assertEquals(sameDate, sameBox.getExpiryDate());
    }

    @Test
    @DisplayName("Add item with maximum integer quantity")
    void addItemWithMaxIntegerQuantity() {
        box.addItem(item1, Integer.MAX_VALUE);
        assertEquals(Integer.MAX_VALUE, box.getItems().get(item1));
    }

    @Test
    @DisplayName("Copy constructor preserves all state")
    void copyConstructorPreservesAllState() {
        box.addItem(item1, 100);
        box.addItem(item2, 200);
        box.setAssignedBay(bay);
        
        Box copy = new Box(box);
        
        // Verify all state is preserved
        assertEquals("B1", copy.getBoxId());
        assertEquals(now, copy.getReceivedAt());
        assertEquals(today.plusDays(10), copy.getExpiryDate());
        assertEquals(2, copy.getItems().size());
        assertEquals(100, copy.getItems().get(item1));
        assertEquals(200, copy.getItems().get(item2));
        assertSame(bay, copy.getAssignedBay());
    }

    @Test
    @DisplayName("Different box instances with same ID are independent")
    void differentBoxInstancesWithSameIdAreIndependent() {
        Box box2 = new Box("B1", now, today.plusDays(10));
        
        box.addItem(item1, 5);
        box2.addItem(item2, 10);
        
        assertEquals(1, box.getItems().size());
        assertEquals(1, box2.getItems().size());
        assertNotSame(box, box2);
    }
}
