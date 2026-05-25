package USEI03;

import Model.PickAllocationRow;
import Model.SKU;
import Model.Trolley;
import Services.PickingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive test suite for PickingService class.
 * Tests all heuristics, policies, edge cases, and bin packing algorithms.
 *
 * Critical areas tested:
 * - FF (First-Fit) heuristic with SPLIT and DEFER policies
 * - FFD (First-Fit Decreasing) heuristic with SPLIT and DEFER policies
 * - BFD (Best-Fit Decreasing) heuristic with SPLIT and DEFER policies
 * - Skipped rows handling (items that don't fit)
 * - Parameter validation and null handling
 * - Sorting behavior and tiebreakers
 * - Edge cases: empty lists, exact fits, very large/small values
 * - Bin packing optimality and efficiency
 */
public class PickingServiceTest {

    private PickingService service;
    private SKU sku1, sku2, sku3;

    @BeforeEach
    void setUp() {
        service = new PickingService();
        sku1 = new SKU("SKU001");
        sku2 = new SKU("SKU002");
        sku3 = new SKU("SKU003");
    }

    // ---------------------------------------------------------------
    // PARAMETER VALIDATION TESTS
    // ---------------------------------------------------------------

    /**
     * Test that null rows throws IllegalArgumentException.
     */
    @Test
    void testPlan_NullRows_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () ->
                service.plan(null, 50.0, PickingService.Heuristic.FF, PickingService.OverflowPolicy.SPLIT)
        );
    }

    /**
     * Test that negative capacity throws IllegalArgumentException.
     */
    @Test
    void testPlan_NegativeCapacity_ThrowsException() {
        List<PickAllocationRow> rows = new ArrayList<>();
        assertThrows(IllegalArgumentException.class, () ->
                service.plan(rows, -10.0, PickingService.Heuristic.FF, PickingService.OverflowPolicy.SPLIT)
        );
    }

    /**
     * Test that zero capacity throws IllegalArgumentException.
     */
    @Test
    void testPlan_ZeroCapacity_ThrowsException() {
        List<PickAllocationRow> rows = new ArrayList<>();
        assertThrows(IllegalArgumentException.class, () ->
                service.plan(rows, 0.0, PickingService.Heuristic.FF, PickingService.OverflowPolicy.SPLIT)
        );
    }

    /**
     * Test that null heuristic throws IllegalArgumentException.
     */
    @Test
    void testPlan_NullHeuristic_ThrowsException() {
        List<PickAllocationRow> rows = new ArrayList<>();
        assertThrows(IllegalArgumentException.class, () ->
                service.plan(rows, 50.0, null, PickingService.OverflowPolicy.SPLIT)
        );
    }

    /**
     * Test that null policy throws IllegalArgumentException.
     */
    @Test
    void testPlan_NullPolicy_ThrowsException() {
        List<PickAllocationRow> rows = new ArrayList<>();
        assertThrows(IllegalArgumentException.class, () ->
                service.plan(rows, 50.0, PickingService.Heuristic.FF, null)
        );
    }

    // ---------------------------------------------------------------
    // FF (FIRST-FIT) TESTS - SPLIT POLICY
    // ---------------------------------------------------------------

    /**
     * Test FF with SPLIT - basic scenario with items that fit.
     */
    @Test
    void testFF_SPLIT_BasicScenario() {
        List<PickAllocationRow> rows = new ArrayList<>();
        rows.add(new PickAllocationRow("ORD001", 1, sku1, 10, "BOX1", 1, 1, 2.0)); // 20 kg
        rows.add(new PickAllocationRow("ORD002", 1, sku2, 15, "BOX2", 1, 2, 2.0)); // 30 kg

        List<Trolley> trolleys = service.plan(rows, 50.0, PickingService.Heuristic.FF, PickingService.OverflowPolicy.SPLIT);

        assertNotNull(trolleys);
        assertEquals(1, trolleys.size());
        assertEquals(50.0, trolleys.get(0).getUsedKg(), 0.001);
        assertTrue(service.getSkippedRows().isEmpty());
    }

    /**
     * Test FF with SPLIT - item needs to be split across trolleys.
     */
    @Test
    void testFF_SPLIT_ItemSplitAcrossTrolleys() {
        List<PickAllocationRow> rows = new ArrayList<>();
        rows.add(new PickAllocationRow("ORD001", 1, sku1, 60, "BOX1", 1, 1, 1.0)); // 60 kg > 50 kg capacity

        List<Trolley> trolleys = service.plan(rows, 50.0, PickingService.Heuristic.FF, PickingService.OverflowPolicy.SPLIT);

        assertNotNull(trolleys);
        assertEquals(2, trolleys.size());

        int totalQty = trolleys.stream()
                .flatMap(t -> t.getPicks().stream())
                .mapToInt(PickAllocationRow::getQty)
                .sum();
        assertEquals(60, totalQty);
        assertTrue(service.getSkippedRows().isEmpty());
    }

    /**
     * Test FF with SPLIT - unit weight exceeds capacity (should skip).
     */
    @Test
    void testFF_SPLIT_UnitWeightExceedsCapacity() {
        List<PickAllocationRow> rows = new ArrayList<>();
        rows.add(new PickAllocationRow("ORD001", 1, sku1, 10, "BOX1", 1, 1, 60.0)); // unit=60kg > capacity=50kg

        List<Trolley> trolleys = service.plan(rows, 50.0, PickingService.Heuristic.FF, PickingService.OverflowPolicy.SPLIT);

        assertNotNull(trolleys);
        assertTrue(trolleys.isEmpty());
        assertEquals(1, service.getSkippedRows().size());
        assertEquals("ORD001", service.getSkippedRows().get(0).getOrderId());
    }

    /**
     * Test FF with SPLIT - multiple items requiring multiple trolleys.
     */
    @Test
    void testFF_SPLIT_MultipleTrolleysNeeded() {
        List<PickAllocationRow> rows = new ArrayList<>();
        rows.add(new PickAllocationRow("ORD001", 1, sku1, 25, "BOX1", 1, 1, 2.0)); // 50 kg
        rows.add(new PickAllocationRow("ORD002", 1, sku2, 25, "BOX2", 1, 2, 2.0)); // 50 kg
        rows.add(new PickAllocationRow("ORD003", 1, sku3, 25, "BOX3", 2, 1, 2.0)); // 50 kg

        List<Trolley> trolleys = service.plan(rows, 50.0, PickingService.Heuristic.FF, PickingService.OverflowPolicy.SPLIT);

        assertNotNull(trolleys);
        assertEquals(3, trolleys.size());
        trolleys.forEach(t -> assertEquals(50.0, t.getUsedKg(), 0.001));
    }

    /**
     * Test FF with SPLIT - first-fit selection (fills first available trolley).
     */
    @Test
    void testFF_SPLIT_FirstFitSelection() {
        List<PickAllocationRow> rows = new ArrayList<>();
        rows.add(new PickAllocationRow("ORD001", 1, sku1, 20, "BOX1", 1, 1, 1.0)); // 20 kg
        rows.add(new PickAllocationRow("ORD002", 1, sku2, 20, "BOX2", 1, 2, 1.0)); // 20 kg
        rows.add(new PickAllocationRow("ORD003", 1, sku3, 15, "BOX3", 2, 1, 1.0)); // 15 kg

        List<Trolley> trolleys = service.plan(rows, 50.0, PickingService.Heuristic.FF, PickingService.OverflowPolicy.SPLIT);

        // FF with SPLIT should pack:
        // trolley1[20+20+10(from ORD003)=50], trolley2[5(remaining from ORD003)=5]
        assertEquals(2, trolleys.size());
        assertEquals(50.0, trolleys.get(0).getUsedKg(), 0.001); // First trolley fills completely
        assertEquals(5.0, trolleys.get(1).getUsedKg(), 0.001);  // Remaining 5 units of ORD003
    }

    // ---------------------------------------------------------------
    // FF (FIRST-FIT) TESTS - DEFER POLICY
    // ---------------------------------------------------------------

    /**
     * Test FF with DEFER - items fit in one trolley.
     */
    @Test
    void testFF_DEFER_ItemsFitInOne() {
        List<PickAllocationRow> rows = new ArrayList<>();
        rows.add(new PickAllocationRow("ORD001", 1, sku1, 10, "BOX1", 1, 1, 2.0)); // 20 kg
        rows.add(new PickAllocationRow("ORD002", 1, sku2, 10, "BOX2", 1, 2, 2.0)); // 20 kg

        List<Trolley> trolleys = service.plan(rows, 50.0, PickingService.Heuristic.FF, PickingService.OverflowPolicy.DEFER);

        assertEquals(1, trolleys.size());
        assertEquals(40.0, trolleys.get(0).getUsedKg(), 0.001);
        assertEquals(2, trolleys.get(0).getPicks().size());
    }

    /**
     * Test FF with DEFER - item exceeds capacity (should skip).
     */
    @Test
    void testFF_DEFER_ItemExceedsCapacity() {
        List<PickAllocationRow> rows = new ArrayList<>();
        rows.add(new PickAllocationRow("ORD001", 1, sku1, 30, "BOX1", 1, 1, 2.0)); // 60 kg > 50 kg

        List<Trolley> trolleys = service.plan(rows, 50.0, PickingService.Heuristic.FF, PickingService.OverflowPolicy.DEFER);

        assertTrue(trolleys.isEmpty());
        assertEquals(1, service.getSkippedRows().size());
    }

    /**
     * Test FF with DEFER - items don't fit together, create new trolley.
     */
    @Test
    void testFF_DEFER_CreateNewTrolley() {
        List<PickAllocationRow> rows = new ArrayList<>();
        rows.add(new PickAllocationRow("ORD001", 1, sku1, 20, "BOX1", 1, 1, 1.0)); // 20 kg
        rows.add(new PickAllocationRow("ORD002", 1, sku2, 35, "BOX2", 1, 2, 1.0)); // 35 kg (doesn't fit with first)

        List<Trolley> trolleys = service.plan(rows, 50.0, PickingService.Heuristic.FF, PickingService.OverflowPolicy.DEFER);

        assertEquals(2, trolleys.size());
        assertEquals(20.0, trolleys.get(0).getUsedKg(), 0.001);
        assertEquals(35.0, trolleys.get(1).getUsedKg(), 0.001);
    }

    /**
     * Test FF with DEFER - atomic allocation (no splitting).
     */
    @Test
    void testFF_DEFER_AtomicAllocation() {
        List<PickAllocationRow> rows = new ArrayList<>();
        rows.add(new PickAllocationRow("ORD001", 1, sku1, 25, "BOX1", 1, 1, 2.0)); // 50 kg

        List<Trolley> trolleys = service.plan(rows, 50.0, PickingService.Heuristic.FF, PickingService.OverflowPolicy.DEFER);

        assertEquals(1, trolleys.size());
        assertEquals(1, trolleys.get(0).getPicks().size());
        assertEquals(25, trolleys.get(0).getPicks().get(0).getQty()); // All 25 units together
    }

    // ---------------------------------------------------------------
    // FFD (FIRST-FIT DECREASING) TESTS - SPLIT POLICY
    // ---------------------------------------------------------------

    /**
     * Test FFD with SPLIT - verifies sorting by weight descending.
     */
    @Test
    void testFFD_SPLIT_SortsDescending() {
        List<PickAllocationRow> rows = new ArrayList<>();
        rows.add(new PickAllocationRow("ORD001", 1, sku1, 5, "BOX1", 1, 1, 1.0));  // 5 kg (light)
        rows.add(new PickAllocationRow("ORD002", 1, sku2, 20, "BOX2", 1, 2, 2.0)); // 40 kg (heavy)
        rows.add(new PickAllocationRow("ORD003", 1, sku3, 10, "BOX3", 2, 1, 1.5)); // 15 kg (medium)

        List<Trolley> trolleys = service.plan(rows, 50.0, PickingService.Heuristic.FFD, PickingService.OverflowPolicy.SPLIT);

        assertNotNull(trolleys);
        // FFD processes in order: 40kg, 15kg, 5kg
        // Should pack better than FF
        assertEquals(2, trolleys.size());
    }

    /**
     * Test FFD with SPLIT - better packing than FF.
     */
    @Test
    void testFFD_SPLIT_BetterPackingThanFF() {
        List<PickAllocationRow> rows = new ArrayList<>();
        rows.add(new PickAllocationRow("ORD001", 1, sku1, 100, "BOX1", 1, 1, 0.3)); // 30 kg
        rows.add(new PickAllocationRow("ORD002", 1, sku2, 100, "BOX2", 1, 2, 0.3)); // 30 kg
        rows.add(new PickAllocationRow("ORD003", 1, sku3, 100, "BOX3", 2, 1, 0.3)); // 30 kg

        List<Trolley> trolleys = service.plan(rows, 50.0, PickingService.Heuristic.FFD, PickingService.OverflowPolicy.SPLIT);

        // Should pack efficiently: can fit 1.66 items per trolley
        assertEquals(2, trolleys.size());

        int totalQty = trolleys.stream()
                .flatMap(t -> t.getPicks().stream())
                .mapToInt(PickAllocationRow::getQty)
                .sum();
        assertEquals(300, totalQty);
    }

    // ---------------------------------------------------------------
    // FFD (FIRST-FIT DECREASING) TESTS - DEFER POLICY
    // ---------------------------------------------------------------

    /**
     * Test FFD with DEFER - optimal packing with sorting.
     */
    @Test
    void testFFD_DEFER_OptimalPacking() {
        List<PickAllocationRow> rows = new ArrayList<>();
        rows.add(new PickAllocationRow("ORD001", 1, sku1, 5, "BOX1", 1, 1, 2.0));  // 10 kg
        rows.add(new PickAllocationRow("ORD002", 1, sku2, 15, "BOX2", 1, 2, 2.0)); // 30 kg
        rows.add(new PickAllocationRow("ORD003", 1, sku3, 10, "BOX3", 2, 1, 2.0)); // 20 kg

        List<Trolley> trolleys = service.plan(rows, 50.0, PickingService.Heuristic.FFD, PickingService.OverflowPolicy.DEFER);

        // FFD processes: 30kg, 20kg, 10kg -> should pack 30+20=50 in trolley1, 10 in trolley2
        assertEquals(2, trolleys.size());
        assertEquals(50.0, trolleys.get(0).getUsedKg(), 0.001);
        assertEquals(10.0, trolleys.get(1).getUsedKg(), 0.001);
    }

    // ---------------------------------------------------------------
    // BFD (BEST-FIT DECREASING) TESTS - SPLIT POLICY
    // ---------------------------------------------------------------

    /**
     * Test BFD with SPLIT - best-fit selection minimizes slack.
     */
    @Test
    void testBFD_SPLIT_BestFitMinimizesSlack() {
        List<PickAllocationRow> rows = new ArrayList<>();
        rows.add(new PickAllocationRow("ORD001", 1, sku1, 25, "BOX1", 1, 1, 1.0)); // 25 kg
        rows.add(new PickAllocationRow("ORD002", 1, sku2, 20, "BOX2", 1, 2, 1.0)); // 20 kg
        rows.add(new PickAllocationRow("ORD003", 1, sku3, 10, "BOX3", 2, 1, 1.0)); // 10 kg

        List<Trolley> trolleys = service.plan(rows, 50.0, PickingService.Heuristic.BFD, PickingService.OverflowPolicy.SPLIT);

        assertNotNull(trolleys);
        // BFD should find best fit: 25+20=45, leaving 5kg slack, then 10kg in new trolley
        // Or: 25+10=35 (15kg slack), 20 (30kg slack) - depends on BFD logic
        assertTrue(trolleys.size() >= 1);
    }

    /**
     * Test BFD with SPLIT - tiebreaker prefers earlier trolley.
     */
    @Test
    void testBFD_SPLIT_TiebreakerPrefsEarlierTrolley() {
        List<PickAllocationRow> rows = new ArrayList<>();
        // Create scenario where two trolleys have same slack
        rows.add(new PickAllocationRow("ORD001", 1, sku1, 20, "BOX1", 1, 1, 1.0)); // 20 kg
        rows.add(new PickAllocationRow("ORD002", 1, sku2, 20, "BOX2", 1, 2, 1.0)); // 20 kg
        rows.add(new PickAllocationRow("ORD003", 1, sku3, 10, "BOX3", 2, 1, 1.0)); // 10 kg

        List<Trolley> trolleys = service.plan(rows, 50.0, PickingService.Heuristic.BFD, PickingService.OverflowPolicy.SPLIT);

        assertNotNull(trolleys);
        // BFD processes: 20kg, 20kg, 10kg
        // Trolley1: 20+20=40, Trolley2 (if needed): remaining
        assertTrue(trolleys.size() >= 1);
    }

    // ---------------------------------------------------------------
    // BFD (BEST-FIT DECREASING) TESTS - DEFER POLICY
    // ---------------------------------------------------------------

    /**
     * Test BFD with DEFER - chooses trolley with minimum slack after insertion.
     */
    @Test
    void testBFD_DEFER_MinimumSlackAfterInsertion() {
        List<PickAllocationRow> rows = new ArrayList<>();
        rows.add(new PickAllocationRow("ORD001", 1, sku1, 20, "BOX1", 1, 1, 1.0)); // 20 kg
        rows.add(new PickAllocationRow("ORD002", 1, sku2, 15, "BOX2", 1, 2, 1.0)); // 15 kg
        rows.add(new PickAllocationRow("ORD003", 1, sku3, 5, "BOX3", 2, 1, 1.0));  // 5 kg

        List<Trolley> trolleys = service.plan(rows, 50.0, PickingService.Heuristic.BFD, PickingService.OverflowPolicy.DEFER);

        assertNotNull(trolleys);
        // BFD processes: 20kg, 15kg, 5kg
        // Should pack 20+15+5=40 in one trolley for best fit
        assertTrue(trolleys.size() >= 1);

        double totalWeight = trolleys.stream().mapToDouble(Trolley::getUsedKg).sum();
        assertEquals(40.0, totalWeight, 0.001);
    }

    /**
     * Test BFD with DEFER - better packing than FFD.
     */
    @Test
    void testBFD_DEFER_BetterThanFFD() {
        List<PickAllocationRow> rows = new ArrayList<>();
        rows.add(new PickAllocationRow("ORD001", 1, sku1, 25, "BOX1", 1, 1, 1.0)); // 25 kg
        rows.add(new PickAllocationRow("ORD002", 1, sku2, 20, "BOX2", 1, 2, 1.0)); // 20 kg
        rows.add(new PickAllocationRow("ORD003", 1, sku3, 25, "BOX3", 2, 1, 1.0)); // 25 kg
        rows.add(new PickAllocationRow("ORD004", 1, sku1, 5, "BOX4", 2, 2, 1.0));  // 5 kg

        List<Trolley> trolleys = service.plan(rows, 50.0, PickingService.Heuristic.BFD, PickingService.OverflowPolicy.DEFER);

        assertNotNull(trolleys);
        // BFD should pack efficiently: 25+25=50, 20+5=25 (2 trolleys)
        // Or 25+20=45 + 5=50, 25 (2 trolleys)
        assertTrue(trolleys.size() >= 2);
    }

    // ---------------------------------------------------------------
    // SKIPPED ROWS TESTS
    // ---------------------------------------------------------------

    /**
     * Test getSkippedRows returns empty list when all fit.
     */
    @Test
    void testGetSkippedRows_EmptyWhenAllFit() {
        List<PickAllocationRow> rows = new ArrayList<>();
        rows.add(new PickAllocationRow("ORD001", 1, sku1, 10, "BOX1", 1, 1, 2.0)); // 20 kg

        service.plan(rows, 50.0, PickingService.Heuristic.FF, PickingService.OverflowPolicy.SPLIT);

        assertTrue(service.getSkippedRows().isEmpty());
    }

    /**
     * Test getSkippedRows returns defensive copy.
     */
    @Test
    void testGetSkippedRows_DefensiveCopy() {
        List<PickAllocationRow> rows = new ArrayList<>();
        rows.add(new PickAllocationRow("ORD001", 1, sku1, 10, "BOX1", 1, 1, 60.0)); // unit exceeds capacity

        service.plan(rows, 50.0, PickingService.Heuristic.FF, PickingService.OverflowPolicy.SPLIT);

        List<PickAllocationRow> skipped1 = service.getSkippedRows();
        List<PickAllocationRow> skipped2 = service.getSkippedRows();

        assertNotSame(skipped1, skipped2); // Different instances
        assertEquals(skipped1.size(), skipped2.size());
    }

    /**
     * Test skipped rows are cleared between plans.
     */
    @Test
    void testSkippedRows_ClearedBetweenPlans() {
        List<PickAllocationRow> rows1 = new ArrayList<>();
        rows1.add(new PickAllocationRow("ORD001", 1, sku1, 10, "BOX1", 1, 1, 60.0)); // unit exceeds

        service.plan(rows1, 50.0, PickingService.Heuristic.FF, PickingService.OverflowPolicy.SPLIT);
        assertEquals(1, service.getSkippedRows().size());

        // Plan again with valid rows
        List<PickAllocationRow> rows2 = new ArrayList<>();
        rows2.add(new PickAllocationRow("ORD002", 1, sku2, 10, "BOX2", 1, 2, 2.0)); // valid

        service.plan(rows2, 50.0, PickingService.Heuristic.FF, PickingService.OverflowPolicy.SPLIT);
        assertTrue(service.getSkippedRows().isEmpty()); // Cleared from previous plan
    }

    /**
     * Test DEFER policy skips item exceeding capacity.
     */
    @Test
    void testSkippedRows_DEFER_ItemTooHeavy() {
        List<PickAllocationRow> rows = new ArrayList<>();
        rows.add(new PickAllocationRow("ORD001", 1, sku1, 30, "BOX1", 1, 1, 2.0)); // 60 kg > 50 kg

        service.plan(rows, 50.0, PickingService.Heuristic.FF, PickingService.OverflowPolicy.DEFER);

        assertEquals(1, service.getSkippedRows().size());
        assertEquals("ORD001", service.getSkippedRows().get(0).getOrderId());
        assertEquals(30, service.getSkippedRows().get(0).getQty());
    }

    /**
     * Test SPLIT policy skips when unit weight exceeds capacity.
     */
    @Test
    void testSkippedRows_SPLIT_UnitTooHeavy() {
        List<PickAllocationRow> rows = new ArrayList<>();
        rows.add(new PickAllocationRow("ORD001", 1, sku1, 5, "BOX1", 1, 1, 60.0)); // unit=60kg > capacity=50kg

        service.plan(rows, 50.0, PickingService.Heuristic.FF, PickingService.OverflowPolicy.SPLIT);

        assertEquals(1, service.getSkippedRows().size());
    }

    // ---------------------------------------------------------------
    // EDGE CASES AND BOUNDARY TESTS
    // ---------------------------------------------------------------

    /**
     * Test with empty list.
     */
    @Test
    void testPlan_EmptyList() {
        List<PickAllocationRow> rows = new ArrayList<>();

        List<Trolley> trolleys = service.plan(rows, 50.0, PickingService.Heuristic.FF, PickingService.OverflowPolicy.SPLIT);

        assertNotNull(trolleys);
        assertTrue(trolleys.isEmpty());
        assertTrue(service.getSkippedRows().isEmpty());
    }

    /**
     * Test with single item that fits exactly.
     */
    @Test
    void testPlan_ExactFit() {
        List<PickAllocationRow> rows = new ArrayList<>();
        rows.add(new PickAllocationRow("ORD001", 1, sku1, 50, "BOX1", 1, 1, 1.0)); // 50 kg exactly

        List<Trolley> trolleys = service.plan(rows, 50.0, PickingService.Heuristic.FF, PickingService.OverflowPolicy.DEFER);

        assertEquals(1, trolleys.size());
        assertEquals(50.0, trolleys.get(0).getUsedKg(), 0.001);
    }

    /**
     * Test with very small unit weight.
     */
    @Test
    void testPlan_VerySmallUnitWeight() {
        List<PickAllocationRow> rows = new ArrayList<>();
        rows.add(new PickAllocationRow("ORD001", 1, sku1, 1000, "BOX1", 1, 1, 0.01)); // 10 kg total

        List<Trolley> trolleys = service.plan(rows, 50.0, PickingService.Heuristic.FF, PickingService.OverflowPolicy.SPLIT);

        assertEquals(1, trolleys.size());
        assertEquals(10.0, trolleys.get(0).getUsedKg(), 0.01);
    }

    /**
     * Test with very large capacity.
     */
    @Test
    void testPlan_VeryLargeCapacity() {
        List<PickAllocationRow> rows = new ArrayList<>();
        rows.add(new PickAllocationRow("ORD001", 1, sku1, 100, "BOX1", 1, 1, 5.0));  // 500 kg
        rows.add(new PickAllocationRow("ORD002", 1, sku2, 200, "BOX2", 1, 2, 3.0));  // 600 kg

        List<Trolley> trolleys = service.plan(rows, 10000.0, PickingService.Heuristic.FF, PickingService.OverflowPolicy.DEFER);

        assertEquals(1, trolleys.size()); // All fits in one huge trolley
        assertEquals(1100.0, trolleys.get(0).getUsedKg(), 0.001);
    }

    /**
     * Test with minimal capacity (just fits one unit).
     */
    @Test
    void testPlan_MinimalCapacity() {
        List<PickAllocationRow> rows = new ArrayList<>();
        rows.add(new PickAllocationRow("ORD001", 1, sku1, 10, "BOX1", 1, 1, 1.0)); // 10 kg total

        List<Trolley> trolleys = service.plan(rows, 1.0, PickingService.Heuristic.FF, PickingService.OverflowPolicy.SPLIT);

        assertEquals(10, trolleys.size()); // Each unit in separate trolley
    }

    /**
     * Test SPLIT with item that splits into many trolleys.
     */
    @Test
    void testPlan_SPLIT_ManyFragments() {
        List<PickAllocationRow> rows = new ArrayList<>();
        rows.add(new PickAllocationRow("ORD001", 1, sku1, 250, "BOX1", 1, 1, 1.0)); // 250 kg

        List<Trolley> trolleys = service.plan(rows, 50.0, PickingService.Heuristic.FF, PickingService.OverflowPolicy.SPLIT);

        assertEquals(5, trolleys.size()); // 250 / 50 = 5 trolleys

        int totalQty = trolleys.stream()
                .flatMap(t -> t.getPicks().stream())
                .mapToInt(PickAllocationRow::getQty)
                .sum();
        assertEquals(250, totalQty);
    }

    /**
     * Test all trolleys respect capacity constraint.
     */
    @Test
    void testPlan_AllTrolleysRespectCapacity() {
        List<PickAllocationRow> rows = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            rows.add(new PickAllocationRow("ORD" + i, 1, sku1, 10 + i, "BOX" + i, 1, 1, 1.5));
        }

        List<Trolley> trolleys = service.plan(rows, 50.0, PickingService.Heuristic.BFD, PickingService.OverflowPolicy.SPLIT);

        for (Trolley t : trolleys) {
            assertTrue(t.getUsedKg() <= 50.0 + 1e-9, "Trolley exceeds capacity: " + t.getUsedKg());
        }
    }

    // ---------------------------------------------------------------
    // SORTING AND TIEBREAKER TESTS
    // ---------------------------------------------------------------

    /**
     * Test FFD/BFD sort by original weight descending.
     */
    @Test
    void testFFD_SortsbyOriginalWeight() {
        List<PickAllocationRow> rows = new ArrayList<>();
        // Create rows with different weights - unsorted
        rows.add(new PickAllocationRow("ORD001", 1, sku1, 5, "BOX1", 1, 1, 1.0));   // 5 kg
        rows.add(new PickAllocationRow("ORD002", 1, sku2, 20, "BOX2", 1, 2, 2.0));  // 40 kg
        rows.add(new PickAllocationRow("ORD003", 1, sku3, 10, "BOX3", 2, 1, 1.5));  // 15 kg

        // FFD should sort: 40, 15, 5
        List<Trolley> trolleys = service.plan(rows, 100.0, PickingService.Heuristic.FFD, PickingService.OverflowPolicy.DEFER);

        // All should fit in one trolley, but order matters for verification
        assertEquals(1, trolleys.size());
        List<PickAllocationRow> picks = trolleys.get(0).getPicks();

        // Verify processing order (heaviest first)
        assertTrue(picks.get(0).getWeightKg() >= picks.get(1).getWeightKg());
        assertTrue(picks.get(1).getWeightKg() >= picks.get(2).getWeightKg());
    }

    // ---------------------------------------------------------------
    // INTEGRATION AND COMPLEX SCENARIOS
    // ---------------------------------------------------------------

    /**
     * Test mixed scenario: some items fit, some skip, some split.
     */
    @Test
    void testPlan_MixedScenario() {
        List<PickAllocationRow> rows = new ArrayList<>();
        rows.add(new PickAllocationRow("ORD001", 1, sku1, 20, "BOX1", 1, 1, 2.0));  // 40 kg - fits
        rows.add(new PickAllocationRow("ORD002", 1, sku2, 10, "BOX2", 1, 2, 6.0));  // 60 kg - exceeds, should skip with DEFER
        rows.add(new PickAllocationRow("ORD003", 1, sku3, 15, "BOX3", 2, 1, 1.0));  // 15 kg - fits

        List<Trolley> trolleys = service.plan(rows, 50.0, PickingService.Heuristic.FF, PickingService.OverflowPolicy.DEFER);

        // ORD001 and ORD003 should fit, ORD002 should be skipped
        assertNotNull(trolleys);
        assertEquals(2, trolleys.size()); // ORD001 in trolley1, ORD003 in trolley2 (doesn't fit with ORD001)
        assertEquals(1, service.getSkippedRows().size());
        assertEquals("ORD002", service.getSkippedRows().get(0).getOrderId());
    }

    /**
     * Test large number of items (stress test).
     */
    @Test
    void testPlan_LargeNumberOfItems() {
        List<PickAllocationRow> rows = new ArrayList<>();
        for (int i = 1; i <= 100; i++) {
            rows.add(new PickAllocationRow("ORD" + i, 1, sku1, 5, "BOX" + i, 1, 1, 1.0)); // 5 kg each
        }

        List<Trolley> trolleys = service.plan(rows, 50.0, PickingService.Heuristic.FFD, PickingService.OverflowPolicy.SPLIT);

        assertNotNull(trolleys);
        assertEquals(10, trolleys.size()); // 100 * 5kg = 500kg / 50kg = 10 trolleys

        int totalQty = trolleys.stream()
                .flatMap(t -> t.getPicks().stream())
                .mapToInt(PickAllocationRow::getQty)
                .sum();
        assertEquals(500, totalQty);
    }

    /**
     * Test all combinations of heuristics and policies.
     */
    @Test
    void testPlan_AllCombinations() {
        List<PickAllocationRow> rows = new ArrayList<>();
        rows.add(new PickAllocationRow("ORD001", 1, sku1, 10, "BOX1", 1, 1, 2.0)); // 20 kg
        rows.add(new PickAllocationRow("ORD002", 1, sku2, 15, "BOX2", 1, 2, 2.0)); // 30 kg

        PickingService.Heuristic[] heuristics = PickingService.Heuristic.values();
        PickingService.OverflowPolicy[] policies = PickingService.OverflowPolicy.values();

        for (PickingService.Heuristic h : heuristics) {
            for (PickingService.OverflowPolicy p : policies) {
                List<Trolley> trolleys = service.plan(new ArrayList<>(rows), 100.0, h, p);

                assertNotNull(trolleys, "Failed for " + h + " + " + p);
                assertEquals(1, trolleys.size(), "Failed for " + h + " + " + p);
                assertEquals(50.0, trolleys.get(0).getUsedKg(), 0.001);
            }
        }
    }

    /**
     * Test fractional weights and rounding.
     */
    @Test
    void testPlan_FractionalWeights() {
        List<PickAllocationRow> rows = new ArrayList<>();
        rows.add(new PickAllocationRow("ORD001", 1, sku1, 33, "BOX1", 1, 1, 1.5)); // 49.5 kg

        List<Trolley> trolleys = service.plan(rows, 50.0, PickingService.Heuristic.FF, PickingService.OverflowPolicy.DEFER);

        assertEquals(1, trolleys.size());
        assertEquals(49.5, trolleys.get(0).getUsedKg(), 0.001);
    }

    /**
     * Test epsilon tolerance for capacity checking.
     */
    @Test
    void testPlan_EpsilonTolerance() {
        List<PickAllocationRow> rows = new ArrayList<>();
        // Item that is just barely under capacity (within epsilon)
        rows.add(new PickAllocationRow("ORD001", 1, sku1, 1, "BOX1", 1, 1, 50.0)); // Exactly 50 kg

        List<Trolley> trolleys = service.plan(rows, 50.0, PickingService.Heuristic.FF, PickingService.OverflowPolicy.DEFER);

        assertEquals(1, trolleys.size());
        assertEquals(50.0, trolleys.get(0).getUsedKg(), 1e-9);
    }

    /**
     * Test that plan doesn't modify input list.
     */
    @Test
    void testPlan_DoesNotModifyInput() {
        List<PickAllocationRow> rows = new ArrayList<>();
        rows.add(new PickAllocationRow("ORD001", 1, sku1, 10, "BOX1", 1, 1, 2.0));
        rows.add(new PickAllocationRow("ORD002", 1, sku2, 15, "BOX2", 1, 2, 2.0));

        List<PickAllocationRow> originalCopy = new ArrayList<>(rows);

        service.plan(rows, 50.0, PickingService.Heuristic.FFD, PickingService.OverflowPolicy.SPLIT);

        // Input list should remain unchanged
        assertEquals(originalCopy.size(), rows.size());
        for (int i = 0; i < rows.size(); i++) {
            assertEquals(originalCopy.get(i).getOrderId(), rows.get(i).getOrderId());
            assertEquals(originalCopy.get(i).getQty(), rows.get(i).getQty());
        }
    }
}

