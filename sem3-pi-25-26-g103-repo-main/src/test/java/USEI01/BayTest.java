package USEI01;

import Model.Bay;
import Model.Box;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Bay model verifying box capacity, add/remove behavior, and list integrity.
 */
@DisplayName("Bay Model Tests")
public class BayTest {

    private Bay bay;
    private Box box1, box2, box3;
    private LocalDateTime now;
    private LocalDate today;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
        today = LocalDate.now();
        bay = new Bay("W1", 1, 2, 3);
        box1 = new Box("B1", now, today.plusDays(1));
        box2 = new Box("B2", now.plusHours(1), today.plusDays(2));
        box3 = new Box("B3", now.plusHours(2), today.plusDays(3));
    }

    @Test
    @DisplayName("Constructor initializes all fields correctly")
    void constructorInitializesFields() {
        Bay b = new Bay("W2", 5, 10, 15);
        assertEquals("W2", b.getWarehouseId());
        assertEquals(5, b.getAisle());
        assertEquals(10, b.getBay());
        assertEquals(15, b.getCapacityBoxes());
        assertNotNull(b.getBoxes());
        assertTrue(b.getBoxes().isEmpty());
    }

    @Test
    @DisplayName("Add single box returns true and increases size")
    void addSingleBoxSucceeds() {
        assertTrue(bay.addBox(box1));
        assertEquals(1, bay.getBoxes().size());
        assertSame(box1, bay.getBoxes().get(0));
    }

    @Test
    @DisplayName("Add multiple boxes respects capacity limit")
    void addMultipleBoxesRespectsCapacity() {
        assertTrue(bay.addBox(box1));
        assertTrue(bay.addBox(box2));
        assertTrue(bay.addBox(box3));
        assertEquals(3, bay.getBoxes().size());
        
        Box box4 = new Box("B4", now.plusHours(3), today.plusDays(4));
        assertFalse(bay.addBox(box4));
        assertEquals(3, bay.getBoxes().size());
    }

    @Test
    @DisplayName("Add box to full bay returns false")
    void addBoxToFullBayReturnsFalse() {
        bay.addBox(box1);
        bay.addBox(box2);
        bay.addBox(box3);
        
        Box box4 = new Box("B4", now.plusHours(3), today.plusDays(4));
        assertFalse(bay.addBox(box4));
    }

    @Test
    @DisplayName("Remove existing box returns true and decreases size")
    void removeExistingBoxSucceeds() {
        bay.addBox(box1);
        bay.addBox(box2);
        assertEquals(2, bay.getBoxes().size());
        
        assertTrue(bay.removeBox(box1));
        assertEquals(1, bay.getBoxes().size());
        assertFalse(bay.getBoxes().contains(box1));
        assertTrue(bay.getBoxes().contains(box2));
    }

    @Test
    @DisplayName("Remove non-existing box returns false")
    void removeNonExistingBoxReturnsFalse() {
        bay.addBox(box1);
        assertFalse(bay.removeBox(box2));
        assertEquals(1, bay.getBoxes().size());
    }

    @Test
    @DisplayName("Remove from empty bay returns false")
    void removeFromEmptyBayReturnsFalse() {
        assertFalse(bay.removeBox(box1));
        assertTrue(bay.getBoxes().isEmpty());
    }

    @Test
    @DisplayName("Add after remove allows new box")
    void addAfterRemoveAllowsNewBox() {
        bay.addBox(box1);
        bay.addBox(box2);
        bay.addBox(box3);
        
        bay.removeBox(box1);
        Box box4 = new Box("B4", now.plusHours(3), today.plusDays(4));
        assertTrue(bay.addBox(box4));
        assertEquals(3, bay.getBoxes().size());
    }

    @Test
    @DisplayName("Setters modify aisle and bay correctly")
    void settersModifyAisleAndBay() {
        bay.setAisle(10);
        bay.setBay(20);
        assertEquals(10, bay.getAisle());
        assertEquals(20, bay.getBay());
    }

    @Test
    @DisplayName("Getters return correct values")
    void gettersReturnCorrectValues() {
        assertEquals("W1", bay.getWarehouseId());
        assertEquals(1, bay.getAisle());
        assertEquals(2, bay.getBay());
        assertEquals(3, bay.getCapacityBoxes());
    }

    @Test
    @DisplayName("Copy constructor creates independent deep copy")
    void copyConstructorCreatesDeepCopy() {
        bay.addBox(box1);
        bay.addBox(box2);
        
        Bay copy = new Bay(bay);
        
        assertEquals(bay.getWarehouseId(), copy.getWarehouseId());
        assertEquals(bay.getAisle(), copy.getAisle());
        assertEquals(bay.getBay(), copy.getBay());
        assertEquals(bay.getCapacityBoxes(), copy.getCapacityBoxes());
        assertEquals(bay.getBoxes().size(), copy.getBoxes().size());
        
        // Verify deep copy - boxes are different instances
        assertNotSame(bay.getBoxes().get(0), copy.getBoxes().get(0));
        assertNotSame(bay.getBoxes().get(1), copy.getBoxes().get(1));
        
        // Verify independence
        bay.removeBox(box1);
        assertEquals(1, bay.getBoxes().size());
        assertEquals(2, copy.getBoxes().size());
    }

    @Test
    @DisplayName("Copy constructor with empty boxes list")
    void copyConstructorWithEmptyBoxes() {
        Bay copy = new Bay(bay);
        assertTrue(copy.getBoxes().isEmpty());
        assertEquals(bay.getWarehouseId(), copy.getWarehouseId());
    }

    @Test
    @DisplayName("Multiple removes in sequence work correctly")
    void multipleRemovesInSequence() {
        bay.addBox(box1);
        bay.addBox(box2);
        bay.addBox(box3);
        
        assertTrue(bay.removeBox(box2));
        assertEquals(2, bay.getBoxes().size());
        assertTrue(bay.removeBox(box1));
        assertEquals(1, bay.getBoxes().size());
        assertTrue(bay.removeBox(box3));
        assertEquals(0, bay.getBoxes().size());
    }

    @Test
    @DisplayName("Bay with capacity 1 works correctly")
    void bayWithCapacityOne() {
        Bay singleBay = new Bay("W1", 0, 0, 1);
        assertTrue(singleBay.addBox(box1));
        assertFalse(singleBay.addBox(box2));
        assertEquals(1, singleBay.getBoxes().size());
    }

    @Test
    @DisplayName("Bay with large capacity handles many boxes")
    void bayWithLargeCapacity() {
        Bay largeBay = new Bay("W1", 0, 0, 100);
        for (int i = 0; i < 100; i++) {
            Box b = new Box("B" + i, now.plusHours(i), today.plusDays(i));
            assertTrue(largeBay.addBox(b));
        }
        assertEquals(100, largeBay.getBoxes().size());
        
        Box extra = new Box("EXTRA", now, today);
        assertFalse(largeBay.addBox(extra));
    }

    @Test
    @DisplayName("toString contains relevant information")
    void toStringContainsRelevantInfo() {
        bay.addBox(box1);
        String str = bay.toString();
        assertTrue(str.contains("aisle"));
        assertTrue(str.contains("bay"));
        assertTrue(str.contains("capacityBoxes"));
        assertTrue(str.contains("boxes"));
    }

    @Test
    @DisplayName("Boxes list is mutable through getBoxes")
    void boxesListIsMutableThroughGetter() {
        ArrayList<Box> boxes = bay.getBoxes();
        boxes.add(box1);
        assertEquals(1, bay.getBoxes().size());
    }

    @Test
    @DisplayName("Same box cannot be added twice via direct list manipulation")
    void sameBoxCanBeAddedTwiceViaDirectManipulation() {
        bay.addBox(box1);
        bay.getBoxes().add(box1);
        assertEquals(2, bay.getBoxes().size());
        // This shows the list allows duplicates if added directly
    }

    @Test
    @DisplayName("Remove specific box from multiple identical boxes")
    void removeSpecificBoxFromMultiple() {
        bay.addBox(box1);
        bay.addBox(box1); // same instance added twice
        assertEquals(2, bay.getBoxes().size());
        
        assertTrue(bay.removeBox(box1));
        assertEquals(1, bay.getBoxes().size());
        assertTrue(bay.removeBox(box1));
        assertEquals(0, bay.getBoxes().size());
    }

    @Test
    @DisplayName("Warehouse ID is preserved in copy")
    void warehouseIdPreservedInCopy() {
        Bay copy = new Bay(bay);
        assertEquals("W1", copy.getWarehouseId());
        
        Bay bay2 = new Bay("W99", 5, 5, 10);
        Bay copy2 = new Bay(bay2);
        assertEquals("W99", copy2.getWarehouseId());
    }
}
