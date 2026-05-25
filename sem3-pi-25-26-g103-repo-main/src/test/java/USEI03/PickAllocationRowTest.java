package USEI03;

import Model.PickAllocationRow;
import Model.SKU;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive test suite for PickAllocationRow class.
 * Tests all constructors, methods, edge cases, and business rules.
 *
 * Critical areas tested:
 * - Constructor validation (null checks, blank checks, invalid values)
 * - copyWithQty() preserves originalTotalWeight (essential for SPLIT policy)
 * - compareTo() natural ordering (essential for FFD/BFD sorting)
 * - equals() and hashCode() contracts
 * - Immutability guarantees
 * - Edge cases and integration scenarios
 */
public class PickAllocationRowTest {

    private SKU sku1;
    private SKU sku2;
    private SKU sku3;

    @BeforeEach
    void setUp() {
        sku1 = new SKU("SKU001");
        sku2 = new SKU("SKU002");
        sku3 = new SKU("SKU003");
    }

    // ---------------------------------------------------------------
    // CONSTRUCTOR TESTS
    // ---------------------------------------------------------------

    /**
     * Test valid construction with all correct parameters.
     */
    @Test
    void testValidConstruction() {
        PickAllocationRow row = new PickAllocationRow(
                "ORD001", 1, sku1, 10, "BOX123", 5, 3, 2.5
        );

        assertEquals("ORD001", row.getOrderId());
        assertEquals(1, row.getLineNo());
        assertEquals(sku1, row.getSku());
        assertEquals(10, row.getQty());
        assertEquals("BOX123", row.getBoxId());
        assertEquals(5, row.getAisle());
        assertEquals(3, row.getBay());
        assertEquals(2.5, row.getUnitWeightKg(), 0.001);
        assertEquals(25.0, row.getWeightKg(), 0.001); // 10 * 2.5
        assertEquals(25.0, row.getOriginalTotalWeight(), 0.001);
    }

    /**
     * Test that null orderId throws NullPointerException.
     */
    @Test
    void testNullOrderId() {
        assertThrows(NullPointerException.class, () ->
                new PickAllocationRow(null, 1, sku1, 10, "BOX123", 5, 3, 2.5)
        );
    }

    /**
     * Test that blank orderId throws IllegalArgumentException.
     */
    @Test
    void testBlankOrderId() {
        assertThrows(IllegalArgumentException.class, () ->
                new PickAllocationRow("", 1, sku1, 10, "BOX123", 5, 3, 2.5)
        );

        assertThrows(IllegalArgumentException.class, () ->
                new PickAllocationRow("   ", 1, sku1, 10, "BOX123", 5, 3, 2.5)
        );
    }

    /**
     * Test that null SKU throws NullPointerException.
     */
    @Test
    void testNullSKU() {
        assertThrows(NullPointerException.class, () ->
                new PickAllocationRow("ORD001", 1, null, 10, "BOX123", 5, 3, 2.5)
        );
    }

    /**
     * Test that null boxId throws NullPointerException.
     */
    @Test
    void testNullBoxId() {
        assertThrows(NullPointerException.class, () ->
                new PickAllocationRow("ORD001", 1, sku1, 10, null, 5, 3, 2.5)
        );
    }

    /**
     * Test that blank boxId throws IllegalArgumentException.
     */
    @Test
    void testBlankBoxId() {
        assertThrows(IllegalArgumentException.class, () ->
                new PickAllocationRow("ORD001", 1, sku1, 10, "", 5, 3, 2.5)
        );
    }

    /**
     * Test that lineNo <= 0 throws IllegalArgumentException.
     */
    @Test
    void testInvalidLineNo() {
        assertThrows(IllegalArgumentException.class, () ->
                new PickAllocationRow("ORD001", 0, sku1, 10, "BOX123", 5, 3, 2.5)
        );

        assertThrows(IllegalArgumentException.class, () ->
                new PickAllocationRow("ORD001", -1, sku1, 10, "BOX123", 5, 3, 2.5)
        );
    }

    /**
     * Test that qty <= 0 throws IllegalArgumentException.
     */
    @Test
    void testInvalidQty() {
        assertThrows(IllegalArgumentException.class, () ->
                new PickAllocationRow("ORD001", 1, sku1, 0, "BOX123", 5, 3, 2.5)
        );

        assertThrows(IllegalArgumentException.class, () ->
                new PickAllocationRow("ORD001", 1, sku1, -5, "BOX123", 5, 3, 2.5)
        );
    }

    /**
     * Test that unitWeight <= 0 throws IllegalArgumentException.
     */
    @Test
    void testInvalidUnitWeight() {
        assertThrows(IllegalArgumentException.class, () ->
                new PickAllocationRow("ORD001", 1, sku1, 10, "BOX123", 5, 3, 0.0)
        );

        assertThrows(IllegalArgumentException.class, () ->
                new PickAllocationRow("ORD001", 1, sku1, 10, "BOX123", 5, 3, -2.5)
        );
    }

    /**
     * Test that aisle and bay can be zero or negative (valid).
     */
    @Test
    void testAisleAndBayCanBeZeroOrNegative() {
        PickAllocationRow row = new PickAllocationRow(
                "ORD001", 1, sku1, 10, "BOX123", 0, 0, 2.5
        );
        assertEquals(0, row.getAisle());
        assertEquals(0, row.getBay());

        row = new PickAllocationRow(
                "ORD001", 1, sku1, 10, "BOX123", -1, -2, 2.5
        );
        assertEquals(-1, row.getAisle());
        assertEquals(-2, row.getBay());
    }

    /**
     * Test very small unit weights (0.01 kg).
     */
    @Test
    void testVerySmallUnitWeight() {
        PickAllocationRow row = new PickAllocationRow(
                "ORD001", 1, sku1, 100, "BOX123", 5, 3, 0.01
        );
        assertEquals(0.01, row.getUnitWeightKg(), 0.0001);
        assertEquals(1.0, row.getWeightKg(), 0.0001);
    }

    /**
     * Test very large quantities and weights.
     */
    @Test
    void testVeryLargeValues() {
        PickAllocationRow row = new PickAllocationRow(
                "ORD001", 1, sku1, 10000, "BOX123", 5, 3, 999.99
        );
        assertEquals(10000, row.getQty());
        assertEquals(999.99, row.getUnitWeightKg(), 0.01);
        assertEquals(9999900.0, row.getWeightKg(), 1.0);
    }

    // ---------------------------------------------------------------
    // COPY WITH QTY TESTS - CRITICAL FOR SPLIT POLICY
    // ---------------------------------------------------------------

    /**
     * Test that copyWithQty preserves originalTotalWeight.
     * CRITICAL: This is essential for correct sorting in FFD/BFD after splits.
     */
    @Test
    void testCopyWithQtyPreservesOriginalWeight() {
        PickAllocationRow original = new PickAllocationRow(
                "ORD001", 1, sku1, 10, "BOX123", 5, 3, 2.5
        );
        // Original: 10 units × 2.5 kg = 25 kg total

        PickAllocationRow copy1 = original.copyWithQty(6);
        PickAllocationRow copy2 = original.copyWithQty(4);

        // Check quantities
        assertEquals(10, original.getQty());
        assertEquals(6, copy1.getQty());
        assertEquals(4, copy2.getQty());

        // Check current weights
        assertEquals(25.0, original.getWeightKg(), 0.001);
        assertEquals(15.0, copy1.getWeightKg(), 0.001); // 6 × 2.5
        assertEquals(10.0, copy2.getWeightKg(), 0.001); // 4 × 2.5

        // CRITICAL: originalTotalWeight must be preserved!
        assertEquals(25.0, original.getOriginalTotalWeight(), 0.001);
        assertEquals(25.0, copy1.getOriginalTotalWeight(), 0.001);
        assertEquals(25.0, copy2.getOriginalTotalWeight(), 0.001);
    }

    /**
     * Test that copyWithQty preserves all fields except qty.
     */
    @Test
    void testCopyPreservesAllFields() {
        PickAllocationRow original = new PickAllocationRow(
                "ORD999", 7, sku1, 100, "BOX999", 12, 8, 3.75
        );

        PickAllocationRow copy = original.copyWithQty(50);

        assertEquals("ORD999", copy.getOrderId());
        assertEquals(7, copy.getLineNo());
        assertEquals(sku1, copy.getSku());
        assertEquals("BOX999", copy.getBoxId());
        assertEquals(12, copy.getAisle());
        assertEquals(8, copy.getBay());
        assertEquals(3.75, copy.getUnitWeightKg(), 0.001);
        assertEquals(50, copy.getQty()); // Only qty changed
    }

    /**
     * Test that copyWithQty rejects invalid quantities.
     */
    @Test
    void testCopyWithInvalidQty() {
        PickAllocationRow original = new PickAllocationRow(
                "ORD001", 1, sku1, 10, "BOX123", 5, 3, 2.5
        );

        assertThrows(IllegalArgumentException.class, () -> original.copyWithQty(0));
        assertThrows(IllegalArgumentException.class, () -> original.copyWithQty(-5));
    }

    /**
     * Test that copyWithQty allows quantity larger than original.
     */
    @Test
    void testCopyWithLargerQty() {
        PickAllocationRow original = new PickAllocationRow(
                "ORD001", 1, sku1, 10, "BOX123", 5, 3, 2.5
        );

        PickAllocationRow copy = original.copyWithQty(20);

        assertEquals(10, original.getQty());
        assertEquals(20, copy.getQty());
        assertEquals(25.0, original.getWeightKg(), 0.001);
        assertEquals(50.0, copy.getWeightKg(), 0.001);
        assertEquals(25.0, copy.getOriginalTotalWeight(), 0.001);
    }

    /**
     * Test that copies are independent instances.
     */
    @Test
    void testCopiesAreIndependent() {
        PickAllocationRow original = new PickAllocationRow(
                "ORD001", 1, sku1, 10, "BOX123", 5, 3, 2.5
        );

        PickAllocationRow copy1 = original.copyWithQty(5);
        PickAllocationRow copy2 = original.copyWithQty(3);

        assertNotSame(original, copy1);
        assertNotSame(original, copy2);
        assertNotSame(copy1, copy2);

        assertEquals(10, original.getQty());
        assertEquals(5, copy1.getQty());
        assertEquals(3, copy2.getQty());
    }

    // ---------------------------------------------------------------
    // COMPARABLE TESTS - CRITICAL FOR FFD/BFD SORTING
    // ---------------------------------------------------------------

    /**
     * Test primary ordering criterion: originalTotalWeight descending.
     */
    @Test
    void testOrderByOriginalWeightDesc() {
        PickAllocationRow heavy = new PickAllocationRow(
                "ORD001", 1, sku1, 10, "BOX1", 1, 1, 5.0
        ); // 50 kg

        PickAllocationRow light = new PickAllocationRow(
                "ORD002", 1, sku2, 10, "BOX2", 1, 1, 2.0
        ); // 20 kg

        assertTrue(heavy.compareTo(light) < 0); // heavy comes BEFORE light (desc)
        assertTrue(light.compareTo(heavy) > 0);
    }

    /**
     * Test first tiebreaker: unitWeight descending.
     */
    @Test
    void testTiebreaker1_UnitWeight() {
        PickAllocationRow heavier = new PickAllocationRow(
                "ORD001", 1, sku1, 10, "BOX1", 1, 1, 3.0
        ); // 30 kg, unit 3.0

        PickAllocationRow lighter = new PickAllocationRow(
                "ORD002", 1, sku2, 15, "BOX2", 1, 1, 2.0
        ); // 30 kg, unit 2.0

        assertTrue(heavier.compareTo(lighter) < 0);
        assertTrue(lighter.compareTo(heavier) > 0);
    }

    /**
     * Test second tiebreaker: qty descending.
     */
    @Test
    void testTiebreaker2_Qty() {
        PickAllocationRow base = new PickAllocationRow(
                "ORD001", 1, sku1, 20, "BOX1", 1, 1, 1.5
        ); // 30 kg

        // Both copies preserve original 30 kg, same unit weight 1.5
        PickAllocationRow copy20 = base.copyWithQty(20);
        PickAllocationRow copy10 = base.copyWithQty(10);

        // More qty comes first (desc)
        assertTrue(copy20.compareTo(copy10) < 0);
        assertTrue(copy10.compareTo(copy20) > 0);
    }

    /**
     * Test third tiebreaker: orderId ascending.
     */
    @Test
    void testTiebreaker3_OrderId() {
        PickAllocationRow row1 = new PickAllocationRow(
                "ORD001", 1, sku1, 10, "BOX1", 1, 1, 2.0
        ); // 20 kg

        PickAllocationRow row2 = new PickAllocationRow(
                "ORD002", 1, sku1, 10, "BOX2", 1, 1, 2.0
        ); // 20 kg, same everything except orderId

        assertTrue(row1.compareTo(row2) < 0);
        assertTrue(row2.compareTo(row1) > 0);
    }

    /**
     * Test fourth tiebreaker: lineNo ascending.
     */
    @Test
    void testTiebreaker4_LineNo() {
        PickAllocationRow line1 = new PickAllocationRow(
                "ORD001", 1, sku1, 10, "BOX1", 1, 1, 2.0
        );

        PickAllocationRow line2 = new PickAllocationRow(
                "ORD001", 2, sku1, 10, "BOX2", 1, 1, 2.0
        );

        assertTrue(line1.compareTo(line2) < 0);
        assertTrue(line2.compareTo(line1) > 0);
    }

    /**
     * Test that comparing same row returns 0.
     */
    @Test
    void testCompareSameRow() {
        PickAllocationRow row = new PickAllocationRow(
                "ORD001", 1, sku1, 10, "BOX1", 1, 1, 2.0
        );

        assertEquals(0, row.compareTo(row));
    }

    /**
     * Test transitivity: a > b, b > c => a > c.
     */
    @Test
    void testTransitivity() {
        PickAllocationRow a = new PickAllocationRow(
                "ORD001", 1, sku1, 10, "BOX1", 1, 1, 5.0
        ); // 50 kg

        PickAllocationRow b = new PickAllocationRow(
                "ORD002", 1, sku2, 10, "BOX2", 1, 1, 3.0
        ); // 30 kg

        PickAllocationRow c = new PickAllocationRow(
                "ORD003", 1, sku3, 10, "BOX3", 1, 1, 1.0
        ); // 10 kg

        assertTrue(a.compareTo(b) < 0);
        assertTrue(b.compareTo(c) < 0);
        assertTrue(a.compareTo(c) < 0); // transitivity
    }

    /**
     * Test that fragments after split preserve original weight in sorting.
     * CRITICAL: Ensures FFD/BFD work correctly with split allocations.
     */
    @Test
    void testSortingAfterSplits() {
        PickAllocationRow original = new PickAllocationRow(
                "ORD001", 1, sku1, 100, "BOX1", 1, 1, 1.0
        ); // 100 kg

        PickAllocationRow fragment1 = original.copyWithQty(60); // 60 kg current, 100 kg original
        PickAllocationRow fragment2 = original.copyWithQty(40); // 40 kg current, 100 kg original

        PickAllocationRow smallRow = new PickAllocationRow(
                "ORD002", 1, sku2, 50, "BOX2", 1, 1, 1.0
        ); // 50 kg

        // Both fragments should be ordered BEFORE smallRow (100 kg original > 50 kg)
        assertTrue(fragment1.compareTo(smallRow) < 0);
        assertTrue(fragment2.compareTo(smallRow) < 0);

        // Fragments with same original weight use qty as tiebreaker
        assertTrue(fragment1.compareTo(fragment2) < 0); // 60 > 40
    }

    // ---------------------------------------------------------------
    // EQUALS AND HASHCODE TESTS
    // ---------------------------------------------------------------

    /**
     * Test equals when all fields match.
     */
    @Test
    void testEquals_AllFieldsMatch() {
        PickAllocationRow row1 = new PickAllocationRow(
                "ORD001", 1, sku1, 10, "BOX123", 5, 3, 2.5
        );

        PickAllocationRow row2 = new PickAllocationRow(
                "ORD001", 1, sku1, 10, "BOX123", 5, 3, 2.5
        );

        assertEquals(row1, row2);
        assertEquals(row2, row1); // symmetry
        assertEquals(row1.hashCode(), row2.hashCode());
    }

    /**
     * Test reflexivity: row.equals(row).
     */
    @Test
    void testEquals_Reflexive() {
        PickAllocationRow row = new PickAllocationRow(
                "ORD001", 1, sku1, 10, "BOX123", 5, 3, 2.5
        );

        assertEquals(row, row);
    }

    /**
     * Test transitivity: a=b, b=c => a=c.
     */
    @Test
    void testEquals_Transitive() {
        PickAllocationRow row1 = new PickAllocationRow(
                "ORD001", 1, sku1, 10, "BOX123", 5, 3, 2.5
        );
        PickAllocationRow row2 = new PickAllocationRow(
                "ORD001", 1, sku1, 10, "BOX123", 5, 3, 2.5
        );
        PickAllocationRow row3 = new PickAllocationRow(
                "ORD001", 1, sku1, 10, "BOX123", 5, 3, 2.5
        );

        assertEquals(row1, row2);
        assertEquals(row2, row3);
        assertEquals(row1, row3);
    }

    /**
     * Test inequality when orderId differs.
     */
    @Test
    void testNotEquals_DifferentOrderId() {
        PickAllocationRow row1 = new PickAllocationRow(
                "ORD001", 1, sku1, 10, "BOX123", 5, 3, 2.5
        );
        PickAllocationRow row2 = new PickAllocationRow(
                "ORD002", 1, sku1, 10, "BOX123", 5, 3, 2.5
        );

        assertNotEquals(row1, row2);
    }

    /**
     * Test inequality when lineNo differs.
     */
    @Test
    void testNotEquals_DifferentLineNo() {
        PickAllocationRow row1 = new PickAllocationRow(
                "ORD001", 1, sku1, 10, "BOX123", 5, 3, 2.5
        );
        PickAllocationRow row2 = new PickAllocationRow(
                "ORD001", 2, sku1, 10, "BOX123", 5, 3, 2.5
        );

        assertNotEquals(row1, row2);
    }

    /**
     * Test inequality when SKU differs.
     */
    @Test
    void testNotEquals_DifferentSKU() {
        PickAllocationRow row1 = new PickAllocationRow(
                "ORD001", 1, sku1, 10, "BOX123", 5, 3, 2.5
        );
        PickAllocationRow row2 = new PickAllocationRow(
                "ORD001", 1, sku2, 10, "BOX123", 5, 3, 2.5
        );

        assertNotEquals(row1, row2);
    }

    /**
     * Test inequality when qty differs.
     */
    @Test
    void testNotEquals_DifferentQty() {
        PickAllocationRow row1 = new PickAllocationRow(
                "ORD001", 1, sku1, 10, "BOX123", 5, 3, 2.5
        );
        PickAllocationRow row2 = new PickAllocationRow(
                "ORD001", 1, sku1, 15, "BOX123", 5, 3, 2.5
        );

        assertNotEquals(row1, row2);
    }

    /**
     * Test inequality when boxId differs.
     */
    @Test
    void testNotEquals_DifferentBoxId() {
        PickAllocationRow row1 = new PickAllocationRow(
                "ORD001", 1, sku1, 10, "BOX123", 5, 3, 2.5
        );
        PickAllocationRow row2 = new PickAllocationRow(
                "ORD001", 1, sku1, 10, "BOX999", 5, 3, 2.5
        );

        assertNotEquals(row1, row2);
    }

    /**
     * Test inequality when aisle differs.
     */
    @Test
    void testNotEquals_DifferentAisle() {
        PickAllocationRow row1 = new PickAllocationRow(
                "ORD001", 1, sku1, 10, "BOX123", 5, 3, 2.5
        );
        PickAllocationRow row2 = new PickAllocationRow(
                "ORD001", 1, sku1, 10, "BOX123", 6, 3, 2.5
        );

        assertNotEquals(row1, row2);
    }

    /**
     * Test inequality when bay differs.
     */
    @Test
    void testNotEquals_DifferentBay() {
        PickAllocationRow row1 = new PickAllocationRow(
                "ORD001", 1, sku1, 10, "BOX123", 5, 3, 2.5
        );
        PickAllocationRow row2 = new PickAllocationRow(
                "ORD001", 1, sku1, 10, "BOX123", 5, 4, 2.5
        );

        assertNotEquals(row1, row2);
    }

    /**
     * Test inequality when unitWeight differs.
     */
    @Test
    void testNotEquals_DifferentUnitWeight() {
        PickAllocationRow row1 = new PickAllocationRow(
                "ORD001", 1, sku1, 10, "BOX123", 5, 3, 2.5
        );
        PickAllocationRow row2 = new PickAllocationRow(
                "ORD001", 1, sku1, 10, "BOX123", 5, 3, 3.0
        );

        assertNotEquals(row1, row2);
    }

    /**
     * Test that equals returns false for null.
     */
    @Test
    void testNotEquals_Null() {
        PickAllocationRow row = new PickAllocationRow(
                "ORD001", 1, sku1, 10, "BOX123", 5, 3, 2.5
        );

        assertNotEquals(row, null);
    }

    /**
     * Test that equals returns false for different class.
     */
    @Test
    void testNotEquals_DifferentClass() {
        PickAllocationRow row = new PickAllocationRow(
                "ORD001", 1, sku1, 10, "BOX123", 5, 3, 2.5
        );

        assertNotEquals(row, "ORD001");
        assertNotEquals(row, new Object());
    }

    /**
     * Test that equals ignores originalTotalWeight.
     * Note: originalTotalWeight is NOT part of equals by design.
     */
    @Test
    void testEquals_IgnoresOriginalTotalWeight() {
        PickAllocationRow original = new PickAllocationRow(
                "ORD001", 1, sku1, 10, "BOX123", 5, 3, 2.5
        );

        PickAllocationRow copy = original.copyWithQty(10);

        assertEquals(original, copy);
    }

    // ---------------------------------------------------------------
    // GETTER TESTS
    // ---------------------------------------------------------------

    /**
     * Test all getters return correct values.
     */
    @Test
    void testAllGetters() {
        PickAllocationRow row = new PickAllocationRow(
                "ORD999", 7, sku1, 25, "BOX777", 12, 8, 3.75
        );

        assertEquals("ORD999", row.getOrderId());
        assertEquals(7, row.getLineNo());
        assertEquals(sku1, row.getSku());
        assertEquals(25, row.getQty());
        assertEquals("BOX777", row.getBoxId());
        assertEquals(12, row.getAisle());
        assertEquals(8, row.getBay());
        assertEquals(3.75, row.getUnitWeightKg(), 0.001);
        assertEquals(93.75, row.getWeightKg(), 0.001); // 25 × 3.75
        assertEquals(93.75, row.getOriginalTotalWeight(), 0.001);
    }

    /**
     * Test that getWeightKg() reflects current qty.
     */
    @Test
    void testGetWeightKg_ReflectsCurrentQty() {
        PickAllocationRow row = new PickAllocationRow(
                "ORD001", 1, sku1, 100, "BOX1", 1, 1, 0.5
        );

        assertEquals(50.0, row.getWeightKg(), 0.001); // 100 × 0.5

        PickAllocationRow copy = row.copyWithQty(50);
        assertEquals(25.0, copy.getWeightKg(), 0.001); // 50 × 0.5
    }

    /**
     * Test that getOriginalTotalWeight() remains constant across copies.
     */
    @Test
    void testGetOriginalTotalWeight_IsConstant() {
        PickAllocationRow original = new PickAllocationRow(
                "ORD001", 1, sku1, 100, "BOX1", 1, 1, 1.0
        );

        assertEquals(100.0, original.getOriginalTotalWeight(), 0.001);

        PickAllocationRow copy1 = original.copyWithQty(60);
        PickAllocationRow copy2 = copy1.copyWithQty(30);
        PickAllocationRow copy3 = copy2.copyWithQty(15);

        assertEquals(100.0, copy1.getOriginalTotalWeight(), 0.001);
        assertEquals(100.0, copy2.getOriginalTotalWeight(), 0.001);
        assertEquals(100.0, copy3.getOriginalTotalWeight(), 0.001);
    }

    // ---------------------------------------------------------------
    // TOSTRING TESTS
    // ---------------------------------------------------------------

    /**
     * Test that toString() contains key information.
     */
    @Test
    void testToString_ContainsKeyInfo() {
        PickAllocationRow row = new PickAllocationRow(
                "ORD001", 5, sku1, 10, "BOX123", 7, 3, 2.5
        );

        String str = row.toString();

        assertTrue(str.contains("ORD001"));
        assertTrue(str.contains("5")); // lineNo
        assertTrue(str.contains("10")); // qty
        assertTrue(str.contains("BOX123"));
        assertTrue(str.contains("7")); // aisle
        assertTrue(str.contains("3")); // bay
    }

    /**
     * Test that toString() does not throw exception.
     */
    @Test
    void testToString_NoException() {
        PickAllocationRow row = new PickAllocationRow(
                "ORD001", 1, sku1, 10, "BOX123", 5, 3, 2.5
        );

        assertDoesNotThrow(() -> row.toString());
    }

    // ---------------------------------------------------------------
    // IMMUTABILITY TESTS
    // ---------------------------------------------------------------

    /**
     * Test that SKU reference is immutable (no setters).
     */
    @Test
    void testSKUCannotBeChanged() {
        PickAllocationRow row = new PickAllocationRow(
                "ORD001", 1, sku1, 10, "BOX123", 5, 3, 2.5
        );

        SKU originalSKU = row.getSku();
        assertEquals(sku1, originalSKU);
        assertEquals(sku1, row.getSku());
    }

    /**
     * Test that all primitive fields are immutable (no setters).
     */
    @Test
    void testPrimitiveFieldsImmutable() {
        PickAllocationRow row = new PickAllocationRow(
                "ORD001", 1, sku1, 10, "BOX123", 5, 3, 2.5
        );

        String orderId = row.getOrderId();
        int lineNo = row.getLineNo();
        int qty = row.getQty();
        String boxId = row.getBoxId();
        int aisle = row.getAisle();
        int bay = row.getBay();
        double unitWeight = row.getUnitWeightKg();

        // No setters - values remain unchanged
        assertEquals(orderId, row.getOrderId());
        assertEquals(lineNo, row.getLineNo());
        assertEquals(qty, row.getQty());
        assertEquals(boxId, row.getBoxId());
        assertEquals(aisle, row.getAisle());
        assertEquals(bay, row.getBay());
        assertEquals(unitWeight, row.getUnitWeightKg(), 0.001);
    }

    // ---------------------------------------------------------------
    // EDGE CASES AND INTEGRATION TESTS
    // ---------------------------------------------------------------

    /**
     * Test floating point precision with small weights.
     */
    @Test
    void testFloatingPointPrecision() {
        PickAllocationRow row = new PickAllocationRow(
                "ORD001", 1, sku1, 3, "BOX123", 1, 1, 0.1
        );

        assertEquals(0.3, row.getWeightKg(), 0.0001);
    }

    /**
     * Test very long orderId and boxId strings.
     */
    @Test
    void testLongStrings() {
        String longOrderId = "ORD" + "X".repeat(100);
        String longBoxId = "BOX" + "Y".repeat(100);

        PickAllocationRow row = new PickAllocationRow(
                longOrderId, 1, sku1, 10, longBoxId, 1, 1, 2.5
        );

        assertEquals(longOrderId, row.getOrderId());
        assertEquals(longBoxId, row.getBoxId());
    }

    /**
     * Test special characters in orderId and boxId.
     */
    @Test
    void testSpecialCharacters() {
        PickAllocationRow row = new PickAllocationRow(
                "ORD-001_#@", 1, sku1, 10, "BOX/123\\ABC", 1, 1, 2.5
        );

        assertEquals("ORD-001_#@", row.getOrderId());
        assertEquals("BOX/123\\ABC", row.getBoxId());
    }

    /**
     * Test sorting scenario simulating FFD/BFD.
     * Rows should be ordered by weight descending: 100kg, 60kg, 30kg.
     */
    @Test
    void testSortingScenario() {
        PickAllocationRow row1 = new PickAllocationRow(
                "ORD001", 1, sku1, 20, "BOX1", 1, 1, 5.0
        ); // 100 kg

        PickAllocationRow row2 = new PickAllocationRow(
                "ORD002", 1, sku2, 15, "BOX2", 1, 1, 4.0
        ); // 60 kg

        PickAllocationRow row3 = new PickAllocationRow(
                "ORD003", 1, sku3, 10, "BOX3", 1, 1, 3.0
        ); // 30 kg

        List<PickAllocationRow> rows = new ArrayList<>();
        rows.add(row3);
        rows.add(row1);
        rows.add(row2);

        Collections.sort(rows);

        assertEquals(row1, rows.get(0)); // 100 kg
        assertEquals(row2, rows.get(1)); // 60 kg
        assertEquals(row3, rows.get(2)); // 30 kg
    }

    /**
     * Test multiple copies preserve original weight and sort correctly.
     * Fragments should be ordered by qty descending: 40, 35, 25.
     */
    @Test
    void testMultipleCopiesSorting() {
        PickAllocationRow original = new PickAllocationRow(
                "ORD001", 1, sku1, 100, "BOX1", 1, 1, 1.0
        ); // 100 kg

        PickAllocationRow fragment1 = original.copyWithQty(40);
        PickAllocationRow fragment2 = original.copyWithQty(35);
        PickAllocationRow fragment3 = original.copyWithQty(25);

        List<PickAllocationRow> fragments = new ArrayList<>();
        fragments.add(fragment3);
        fragments.add(fragment1);
        fragments.add(fragment2);

        Collections.sort(fragments);

        // Should be ordered by qty (desc) since all have same original weight
        assertEquals(40, fragments.get(0).getQty());
        assertEquals(35, fragments.get(1).getQty());
        assertEquals(25, fragments.get(2).getQty());
    }
}
