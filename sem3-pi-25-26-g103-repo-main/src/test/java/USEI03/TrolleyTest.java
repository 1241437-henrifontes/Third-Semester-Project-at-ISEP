package USEI03;

import Model.PickAllocationRow;
import Model.SKU;
import Model.Trolley;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive test suite for Trolley class.
 * Tests all methods, constructor validation, capacity management, and Comparable interface.
 *
 * Critical areas tested:
 * - Constructor validation (capacity > 0)
 * - remainingKg() calculation
 * - canFit() with epsilon tolerance
 * - tryAdd() safe addition with capacity check
 * - add() forced addition with exception on overflow
 * - Getters (capacityKg, usedKg, utilisation, picks)
 * - compareTo() natural ordering by slack
 * - equals() and hashCode() contracts
 * - Immutability of picks list
 * - Edge cases: empty trolley, full trolley, exact fits, epsilon boundaries
 */
public class TrolleyTest {

    private SKU sku1, sku2, sku3;
    private PickAllocationRow row1, row2, row3;

    @BeforeEach
    void setUp() {
        sku1 = new SKU("SKU001");
        sku2 = new SKU("SKU002");
        sku3 = new SKU("SKU003");

        row1 = new PickAllocationRow("ORD001", 1, sku1, 10, "BOX1", 1, 1, 2.0); // 20 kg
        row2 = new PickAllocationRow("ORD002", 1, sku2, 15, "BOX2", 1, 2, 2.0); // 30 kg
        row3 = new PickAllocationRow("ORD003", 1, sku3, 5, "BOX3", 2, 1, 2.0);  // 10 kg
    }

    // ---------------------------------------------------------------
    // CONSTRUCTOR TESTS
    // ---------------------------------------------------------------

    /**
     * Test valid construction with positive capacity.
     */
    @Test
    void testConstructor_ValidCapacity() {
        Trolley trolley = new Trolley(50.0);

        assertEquals(50.0, trolley.getCapacityKg(), 0.001);
        assertEquals(0.0, trolley.getUsedKg(), 0.001);
        assertEquals(50.0, trolley.remainingKg(), 0.001);
        assertTrue(trolley.getPicks().isEmpty());
    }

    /**
     * Test constructor with zero capacity throws exception.
     */
    @Test
    void testConstructor_ZeroCapacity_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new Trolley(0.0));
    }

    /**
     * Test constructor with negative capacity throws exception.
     */
    @Test
    void testConstructor_NegativeCapacity_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new Trolley(-10.0));
    }

    /**
     * Test constructor with very small positive capacity.
     */
    @Test
    void testConstructor_VerySmallCapacity() {
        Trolley trolley = new Trolley(0.001);

        assertEquals(0.001, trolley.getCapacityKg(), 0.0001);
    }

    /**
     * Test constructor with very large capacity.
     */
    @Test
    void testConstructor_VeryLargeCapacity() {
        Trolley trolley = new Trolley(10000.0);

        assertEquals(10000.0, trolley.getCapacityKg(), 0.001);
    }

    // ---------------------------------------------------------------
    // REMAINING KG TESTS
    // ---------------------------------------------------------------

    /**
     * Test remainingKg on empty trolley.
     */
    @Test
    void testRemainingKg_EmptyTrolley() {
        Trolley trolley = new Trolley(50.0);

        assertEquals(50.0, trolley.remainingKg(), 0.001);
    }

    /**
     * Test remainingKg after adding items.
     */
    @Test
    void testRemainingKg_AfterAddingItems() {
        Trolley trolley = new Trolley(50.0);
        trolley.add(row1); // 20 kg

        assertEquals(30.0, trolley.remainingKg(), 0.001);

        trolley.add(row3); // 10 kg

        assertEquals(20.0, trolley.remainingKg(), 0.001);
    }

    /**
     * Test remainingKg when trolley is full.
     */
    @Test
    void testRemainingKg_FullTrolley() {
        Trolley trolley = new Trolley(50.0);
        trolley.add(row1); // 20 kg
        trolley.add(row2); // 30 kg

        assertEquals(0.0, trolley.remainingKg(), 0.001);
    }

    /**
     * Test remainingKg calculation is accurate.
     */
    @Test
    void testRemainingKg_AccurateCalculation() {
        Trolley trolley = new Trolley(100.0);
        trolley.add(row1); // 20 kg
        trolley.add(row2); // 30 kg
        trolley.add(row3); // 10 kg

        assertEquals(40.0, trolley.remainingKg(), 0.001); // 100 - 60 = 40
    }

    // ---------------------------------------------------------------
    // CAN FIT TESTS
    // ---------------------------------------------------------------

    /**
     * Test canFit returns true when item fits.
     */
    @Test
    void testCanFit_ItemFits() {
        Trolley trolley = new Trolley(50.0);

        assertTrue(trolley.canFit(20.0));
        assertTrue(trolley.canFit(50.0));
    }

    /**
     * Test canFit returns false when item doesn't fit.
     */
    @Test
    void testCanFit_ItemDoesNotFit() {
        Trolley trolley = new Trolley(50.0);

        assertFalse(trolley.canFit(51.0));
        assertFalse(trolley.canFit(100.0));
    }

    /**
     * Test canFit with exact capacity.
     */
    @Test
    void testCanFit_ExactCapacity() {
        Trolley trolley = new Trolley(50.0);

        assertTrue(trolley.canFit(50.0));
    }

    /**
     * Test canFit after partially filling trolley.
     */
    @Test
    void testCanFit_PartiallyFilled() {
        Trolley trolley = new Trolley(50.0);
        trolley.add(row1); // 20 kg used

        assertTrue(trolley.canFit(30.0));
        assertTrue(trolley.canFit(29.0));
        assertFalse(trolley.canFit(31.0));
    }

    /**
     * Test canFit with epsilon tolerance.
     */
    @Test
    void testCanFit_EpsilonTolerance() {
        Trolley trolley = new Trolley(50.0);
        trolley.add(row1); // 20 kg

        // 30.0 + epsilon should still fit due to tolerance
        assertTrue(trolley.canFit(30.0));
        // Very slightly over should still fit within epsilon
        assertTrue(trolley.canFit(30.0 + 1e-10));
    }

    /**
     * Test canFit with zero weight.
     */
    @Test
    void testCanFit_ZeroWeight() {
        Trolley trolley = new Trolley(50.0);

        assertTrue(trolley.canFit(0.0));
    }

    /**
     * Test canFit with negative weight.
     */
    @Test
    void testCanFit_NegativeWeight() {
        Trolley trolley = new Trolley(50.0);

        assertTrue(trolley.canFit(-10.0)); // Mathematically fits, but not realistic
    }

    // ---------------------------------------------------------------
    // TRY ADD TESTS
    // ---------------------------------------------------------------

    /**
     * Test tryAdd successfully adds item that fits.
     */
    @Test
    void testTryAdd_ItemFits() {
        Trolley trolley = new Trolley(50.0);

        boolean added = trolley.tryAdd(row1);

        assertTrue(added);
        assertEquals(1, trolley.getPicks().size());
        assertEquals(20.0, trolley.getUsedKg(), 0.001);
    }

    /**
     * Test tryAdd returns false when item doesn't fit.
     */
    @Test
    void testTryAdd_ItemDoesNotFit() {
        Trolley trolley = new Trolley(50.0);
        trolley.add(row2); // 30 kg

        PickAllocationRow heavyRow = new PickAllocationRow("ORD004", 1, sku1, 15, "BOX4", 1, 1, 2.0); // 30 kg
        boolean added = trolley.tryAdd(heavyRow);

        assertFalse(added);
        assertEquals(1, trolley.getPicks().size()); // Still only row2
        assertEquals(30.0, trolley.getUsedKg(), 0.001);
    }

    /**
     * Test tryAdd with null row throws NullPointerException.
     */
    @Test
    void testTryAdd_NullRow_ThrowsException() {
        Trolley trolley = new Trolley(50.0);

        assertThrows(NullPointerException.class, () -> trolley.tryAdd(null));
    }

    /**
     * Test tryAdd multiple items.
     */
    @Test
    void testTryAdd_MultipleItems() {
        Trolley trolley = new Trolley(100.0);

        assertTrue(trolley.tryAdd(row1)); // 20 kg
        assertTrue(trolley.tryAdd(row2)); // 30 kg
        assertTrue(trolley.tryAdd(row3)); // 10 kg

        assertEquals(3, trolley.getPicks().size());
        assertEquals(60.0, trolley.getUsedKg(), 0.001);
    }

    /**
     * Test tryAdd updates weight correctly.
     */
    @Test
    void testTryAdd_UpdatesWeight() {
        Trolley trolley = new Trolley(50.0);

        assertEquals(0.0, trolley.getUsedKg(), 0.001);

        trolley.tryAdd(row1);
        assertEquals(20.0, trolley.getUsedKg(), 0.001);

        trolley.tryAdd(row3);
        assertEquals(30.0, trolley.getUsedKg(), 0.001);
    }

    /**
     * Test tryAdd preserves picks list on failure.
     */
    @Test
    void testTryAdd_PreservesListOnFailure() {
        Trolley trolley = new Trolley(25.0);
        trolley.tryAdd(row1); // 20 kg - fits

        int sizeBeforeFail = trolley.getPicks().size();
        double weightBeforeFail = trolley.getUsedKg();

        trolley.tryAdd(row3); // 10 kg - doesn't fit

        assertEquals(sizeBeforeFail, trolley.getPicks().size());
        assertEquals(weightBeforeFail, trolley.getUsedKg(), 0.001);
    }

    // ---------------------------------------------------------------
    // ADD TESTS
    // ---------------------------------------------------------------

    /**
     * Test add successfully adds item that fits.
     */
    @Test
    void testAdd_ItemFits() {
        Trolley trolley = new Trolley(50.0);

        trolley.add(row1);

        assertEquals(1, trolley.getPicks().size());
        assertEquals(20.0, trolley.getUsedKg(), 0.001);
    }

    /**
     * Test add throws exception when item doesn't fit.
     */
    @Test
    void testAdd_ItemDoesNotFit_ThrowsException() {
        Trolley trolley = new Trolley(50.0);
        trolley.add(row2); // 30 kg

        PickAllocationRow heavyRow = new PickAllocationRow("ORD004", 1, sku1, 15, "BOX4", 1, 1, 2.0); // 30 kg

        assertThrows(IllegalStateException.class, () -> trolley.add(heavyRow));
    }

    /**
     * Test add with null row throws NullPointerException.
     */
    @Test
    void testAdd_NullRow_ThrowsException() {
        Trolley trolley = new Trolley(50.0);

        assertThrows(NullPointerException.class, () -> trolley.add(null));
    }

    /**
     * Test add exception message contains useful info.
     */
    @Test
    void testAdd_ExceptionMessageContainsInfo() {
        Trolley trolley = new Trolley(50.0);
        trolley.add(row2); // 30 kg

        PickAllocationRow heavyRow = new PickAllocationRow("ORD004", 1, sku1, 15, "BOX4", 1, 1, 2.0); // 30 kg

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> trolley.add(heavyRow));

        String message = exception.getMessage();
        assertTrue(message.contains("30")); // usedKg
        assertTrue(message.contains("50")); // capacity
    }

    /**
     * Test add multiple items in sequence.
     */
    @Test
    void testAdd_MultipleItems() {
        Trolley trolley = new Trolley(100.0);

        trolley.add(row1); // 20 kg
        trolley.add(row2); // 30 kg
        trolley.add(row3); // 10 kg

        assertEquals(3, trolley.getPicks().size());
        assertEquals(60.0, trolley.getUsedKg(), 0.001);
    }

    /**
     * Test add doesn't modify trolley when exception is thrown.
     */
    @Test
    void testAdd_NoModificationOnException() {
        Trolley trolley = new Trolley(25.0);
        trolley.add(row1); // 20 kg

        int sizeBeforeFail = trolley.getPicks().size();
        double weightBeforeFail = trolley.getUsedKg();

        try {
            trolley.add(row3); // 10 kg - doesn't fit
        } catch (IllegalStateException e) {
            // Expected
        }

        assertEquals(sizeBeforeFail, trolley.getPicks().size());
        assertEquals(weightBeforeFail, trolley.getUsedKg(), 0.001);
    }

    // ---------------------------------------------------------------
    // GETTER TESTS
    // ---------------------------------------------------------------

    /**
     * Test getCapacityKg returns correct value.
     */
    @Test
    void testGetCapacityKg() {
        Trolley trolley = new Trolley(75.5);

        assertEquals(75.5, trolley.getCapacityKg(), 0.001);
    }

    /**
     * Test getUsedKg on empty trolley.
     */
    @Test
    void testGetUsedKg_EmptyTrolley() {
        Trolley trolley = new Trolley(50.0);

        assertEquals(0.0, trolley.getUsedKg(), 0.001);
    }

    /**
     * Test getUsedKg after adding items.
     */
    @Test
    void testGetUsedKg_AfterAdding() {
        Trolley trolley = new Trolley(50.0);
        trolley.add(row1); // 20 kg

        assertEquals(20.0, trolley.getUsedKg(), 0.001);
    }

    /**
     * Test getUtilisation on empty trolley.
     */
    @Test
    void testGetUtilisation_Empty() {
        Trolley trolley = new Trolley(50.0);

        assertEquals(0.0, trolley.getUtilisation(), 0.001);
    }

    /**
     * Test getUtilisation half full.
     */
    @Test
    void testGetUtilisation_HalfFull() {
        Trolley trolley = new Trolley(50.0);
        trolley.add(row1); // 20 kg
        trolley.add(row3); // 10 kg (total 30 kg)

        // Actually 30/50 = 0.6, not 0.5
        assertEquals(0.6, trolley.getUtilisation(), 0.001);
    }

    /**
     * Test getUtilisation completely full.
     */
    @Test
    void testGetUtilisation_Full() {
        Trolley trolley = new Trolley(50.0);
        trolley.add(row1); // 20 kg
        trolley.add(row2); // 30 kg

        assertEquals(1.0, trolley.getUtilisation(), 0.001);
    }

    /**
     * Test getUtilisation with partial fill.
     */
    @Test
    void testGetUtilisation_PartialFill() {
        Trolley trolley = new Trolley(100.0);
        trolley.add(row1); // 20 kg

        assertEquals(0.2, trolley.getUtilisation(), 0.001); // 20/100 = 0.2
    }

    /**
     * Test getPicks returns immutable list.
     */
    @Test
    void testGetPicks_ReturnsImmutableList() {
        Trolley trolley = new Trolley(50.0);
        trolley.add(row1);

        List<PickAllocationRow> picks = trolley.getPicks();

        assertThrows(UnsupportedOperationException.class, () -> picks.add(row2));
        assertThrows(UnsupportedOperationException.class, () -> picks.clear());
    }

    /**
     * Test getPicks returns empty list for empty trolley.
     */
    @Test
    void testGetPicks_EmptyTrolley() {
        Trolley trolley = new Trolley(50.0);

        List<PickAllocationRow> picks = trolley.getPicks();

        assertNotNull(picks);
        assertTrue(picks.isEmpty());
    }

    /**
     * Test getPicks returns all added items.
     */
    @Test
    void testGetPicks_ReturnsAllItems() {
        Trolley trolley = new Trolley(100.0);
        trolley.add(row1);
        trolley.add(row2);
        trolley.add(row3);

        List<PickAllocationRow> picks = trolley.getPicks();

        assertEquals(3, picks.size());
        assertTrue(picks.contains(row1));
        assertTrue(picks.contains(row2));
        assertTrue(picks.contains(row3));
    }

    /**
     * Test getPicks preserves order of addition.
     */
    @Test
    void testGetPicks_PreservesOrder() {
        Trolley trolley = new Trolley(100.0);
        trolley.add(row1);
        trolley.add(row2);
        trolley.add(row3);

        List<PickAllocationRow> picks = trolley.getPicks();

        assertEquals(row1, picks.get(0));
        assertEquals(row2, picks.get(1));
        assertEquals(row3, picks.get(2));
    }

    // ---------------------------------------------------------------
    // COMPARABLE TESTS
    // ---------------------------------------------------------------

    /**
     * Test compareTo orders by remaining capacity ascending.
     */
    @Test
    void testCompareTo_OrdersByRemainingCapacity() {
        Trolley trolley1 = new Trolley(50.0); // 50 kg remaining
        Trolley trolley2 = new Trolley(50.0);
        trolley2.add(row1); // 30 kg remaining

        Trolley trolley3 = new Trolley(50.0);
        trolley3.add(row1); // 20 kg
        trolley3.add(row3); // 10 kg (total: 20 kg remaining)

        // trolley3 (20kg) < trolley2 (30kg) < trolley1 (50kg)
        assertTrue(trolley3.compareTo(trolley2) < 0);
        assertTrue(trolley2.compareTo(trolley1) < 0);
        assertTrue(trolley3.compareTo(trolley1) < 0);
    }

    /**
     * Test compareTo returns 0 for equal remaining capacity.
     */
    @Test
    void testCompareTo_EqualRemaining() {
        Trolley trolley1 = new Trolley(50.0);
        trolley1.add(row1); // 30 kg remaining

        Trolley trolley2 = new Trolley(50.0);
        trolley2.add(row1); // 30 kg remaining

        assertEquals(0, trolley1.compareTo(trolley2));
    }

    /**
     * Test compareTo with same trolley returns 0.
     */
    @Test
    void testCompareTo_SameTrolley() {
        Trolley trolley = new Trolley(50.0);

        assertEquals(0, trolley.compareTo(trolley));
    }

    /**
     * Test compareTo is transitive.
     */
    @Test
    void testCompareTo_Transitive() {
        Trolley a = new Trolley(50.0); // 50 remaining
        Trolley b = new Trolley(50.0);
        b.add(row3); // 40 remaining
        Trolley c = new Trolley(50.0);
        c.add(row1); // 30 remaining

        assertTrue(c.compareTo(b) < 0);
        assertTrue(b.compareTo(a) < 0);
        assertTrue(c.compareTo(a) < 0); // transitivity
    }

    /**
     * Test compareTo allows sorting.
     */
    @Test
    void testCompareTo_AllowsSorting() {
        Trolley t1 = new Trolley(50.0);
        t1.add(row1); // 30 kg remaining

        Trolley t2 = new Trolley(50.0); // 50 kg remaining

        Trolley t3 = new Trolley(50.0);
        t3.add(row1);
        t3.add(row3); // 20 kg remaining

        List<Trolley> trolleys = new ArrayList<>();
        trolleys.add(t2);
        trolleys.add(t1);
        trolleys.add(t3);

        Collections.sort(trolleys);

        // Should be sorted: t3 (20), t1 (30), t2 (50)
        assertEquals(t3, trolleys.get(0));
        assertEquals(t1, trolleys.get(1));
        assertEquals(t2, trolleys.get(2));
    }

    // ---------------------------------------------------------------
    // EQUALS AND HASHCODE TESTS
    // ---------------------------------------------------------------

    /**
     * Test equals with identical trolleys.
     */
    @Test
    void testEquals_IdenticalTrolleys() {
        Trolley t1 = new Trolley(50.0);
        t1.add(row1);

        Trolley t2 = new Trolley(50.0);
        t2.add(row1);

        assertEquals(t1, t2);
    }

    /**
     * Test equals reflexive.
     */
    @Test
    void testEquals_Reflexive() {
        Trolley trolley = new Trolley(50.0);

        assertEquals(trolley, trolley);
    }

    /**
     * Test equals symmetric.
     */
    @Test
    void testEquals_Symmetric() {
        Trolley t1 = new Trolley(50.0);
        Trolley t2 = new Trolley(50.0);

        assertEquals(t1, t2);
        assertEquals(t2, t1);
    }

    /**
     * Test equals transitive.
     */
    @Test
    void testEquals_Transitive() {
        Trolley t1 = new Trolley(50.0);
        Trolley t2 = new Trolley(50.0);
        Trolley t3 = new Trolley(50.0);

        assertEquals(t1, t2);
        assertEquals(t2, t3);
        assertEquals(t1, t3);
    }

    /**
     * Test equals with different capacity.
     */
    @Test
    void testEquals_DifferentCapacity() {
        Trolley t1 = new Trolley(50.0);
        Trolley t2 = new Trolley(100.0);

        assertNotEquals(t1, t2);
    }

    /**
     * Test equals with different used weight.
     */
    @Test
    void testEquals_DifferentUsedWeight() {
        Trolley t1 = new Trolley(50.0);
        t1.add(row1);

        Trolley t2 = new Trolley(50.0);
        t2.add(row2);

        assertNotEquals(t1, t2);
    }

    /**
     * Test equals with different picks.
     */
    @Test
    void testEquals_DifferentPicks() {
        Trolley t1 = new Trolley(50.0);
        t1.add(row1);

        Trolley t2 = new Trolley(50.0);
        t2.add(row3);

        assertNotEquals(t1, t2);
    }

    /**
     * Test equals with null.
     */
    @Test
    void testEquals_Null() {
        Trolley trolley = new Trolley(50.0);

        assertNotEquals(trolley, null);
    }

    /**
     * Test equals with different class.
     */
    @Test
    void testEquals_DifferentClass() {
        Trolley trolley = new Trolley(50.0);

        assertNotEquals(trolley, "Not a trolley");
    }

    /**
     * Test hashCode consistency with equals.
     */
    @Test
    void testHashCode_ConsistentWithEquals() {
        Trolley t1 = new Trolley(50.0);
        t1.add(row1);

        Trolley t2 = new Trolley(50.0);
        t2.add(row1);

        assertEquals(t1, t2);
        assertEquals(t1.hashCode(), t2.hashCode());
    }

    /**
     * Test hashCode different for different trolleys.
     */
    @Test
    void testHashCode_DifferentForDifferentTrolleys() {
        Trolley t1 = new Trolley(50.0);
        Trolley t2 = new Trolley(100.0);

        assertNotEquals(t1.hashCode(), t2.hashCode());
    }

    // ---------------------------------------------------------------
    // EDGE CASES AND INTEGRATION TESTS
    // ---------------------------------------------------------------

    /**
     * Test filling trolley to exact capacity.
     */
    @Test
    void testEdgeCase_ExactCapacity() {
        Trolley trolley = new Trolley(50.0);
        trolley.add(row1); // 20 kg
        trolley.add(row2); // 30 kg

        assertEquals(50.0, trolley.getUsedKg(), 0.001);
        assertEquals(0.0, trolley.remainingKg(), 0.001);
        assertEquals(1.0, trolley.getUtilisation(), 0.001);
    }

    /**
     * Test trolley with very small capacity.
     */
    @Test
    void testEdgeCase_VerySmallCapacity() {
        Trolley trolley = new Trolley(0.1);

        PickAllocationRow tiny = new PickAllocationRow("ORD001", 1, sku1, 1, "BOX1", 1, 1, 0.05);
        trolley.add(tiny);

        assertEquals(0.05, trolley.getUsedKg(), 0.001);
        assertEquals(0.05, trolley.remainingKg(), 0.001);
    }

    /**
     * Test trolley with fractional weights.
     */
    @Test
    void testEdgeCase_FractionalWeights() {
        Trolley trolley = new Trolley(50.0);

        PickAllocationRow fractional = new PickAllocationRow("ORD001", 1, sku1, 7, "BOX1", 1, 1, 1.5); // 10.5 kg
        trolley.add(fractional);

        assertEquals(10.5, trolley.getUsedKg(), 0.001);
        assertEquals(39.5, trolley.remainingKg(), 0.001);
    }

    /**
     * Test many small items.
     */
    @Test
    void testIntegration_ManySmallItems() {
        Trolley trolley = new Trolley(100.0);

        for (int i = 0; i < 20; i++) {
            PickAllocationRow small = new PickAllocationRow("ORD" + i, 1, sku1, 1, "BOX" + i, 1, 1, 1.0);
            trolley.add(small);
        }

        assertEquals(20, trolley.getPicks().size());
        assertEquals(20.0, trolley.getUsedKg(), 0.001);
        assertEquals(0.2, trolley.getUtilisation(), 0.001);
    }

    /**
     * Test sequential add and tryAdd operations.
     */
    @Test
    void testIntegration_MixedAddOperations() {
        Trolley trolley = new Trolley(50.0);

        trolley.add(row1); // 20 kg
        assertTrue(trolley.tryAdd(row3)); // 10 kg

        assertFalse(trolley.tryAdd(row2)); // 30 kg - doesn't fit

        assertEquals(2, trolley.getPicks().size());
        assertEquals(30.0, trolley.getUsedKg(), 0.001);
    }

    /**
     * Test trolley state remains consistent.
     */
    @Test
    void testStateConsistency() {
        Trolley trolley = new Trolley(100.0);

        trolley.add(row1);
        trolley.add(row2);

        // Verify all getters are consistent
        assertEquals(100.0, trolley.getCapacityKg(), 0.001);
        assertEquals(50.0, trolley.getUsedKg(), 0.001);
        assertEquals(50.0, trolley.remainingKg(), 0.001);
        assertEquals(0.5, trolley.getUtilisation(), 0.001);
        assertEquals(2, trolley.getPicks().size());
    }

    /**
     * Test epsilon boundary for canFit and tryAdd.
     */
    @Test
    void testEpsilonBoundary() {
        Trolley trolley = new Trolley(50.0);
        trolley.add(row1); // 20 kg used, 30 kg remaining

        // Item that weighs exactly the remaining capacity
        PickAllocationRow exactFit = new PickAllocationRow("ORD004", 1, sku1, 15, "BOX4", 1, 1, 2.0); // 30 kg

        assertTrue(trolley.canFit(30.0));
        assertTrue(trolley.tryAdd(exactFit));

        assertEquals(50.0, trolley.getUsedKg(), 0.001);
    }

    /**
     * Test immutability of capacity.
     */
    @Test
    void testImmutability_Capacity() {
        Trolley trolley = new Trolley(50.0);
        double initialCapacity = trolley.getCapacityKg();

        trolley.add(row1);
        trolley.add(row2);

        assertEquals(initialCapacity, trolley.getCapacityKg(), 0.001);
    }
}

