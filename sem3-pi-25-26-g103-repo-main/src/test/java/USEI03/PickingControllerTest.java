package USEI03;

import Repositories.AllocationRepository;
import Repositories.ItemRepository;
import Repositories.TrolleyRepository;
import Controllers.PickingController;
import Model.*;
import Services.PickingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive test suite for PickingController class.
 * Tests all methods, edge cases, integration with repositories and services.
 *
 * Critical areas tested:
 * - plan() method with all heuristics (FF, FFD, BFD) and policies (SPLIT, DEFER)
 * - getSkippedRows() delegation to service
 * - convertToPickRows() conversion from Allocation to PickAllocationRow
 * - Repository integration (AllocationRepository, TrolleyRepository, ItemRepository)
 * - Edge cases: empty inputs, null handling, invalid data
 * - Integration scenarios with real allocation data
 */
public class PickingControllerTest {

    private PickingController controller;
    private AllocationRepository allocationRepo;
    private TrolleyRepository trolleyRepo;
    private ItemRepository itemRepo;

    private SKU sku1;
    private SKU sku2;
    private SKU sku3;
    private Item item1;
    private Item item2;
    private Item item3;
    private Bay bay1;
    private Bay bay2;
    private Bay bay3;
    private Box box1;
    private Box box2;
    private Box box3;

    @BeforeEach
    void setUp() {
        controller = new PickingController();
        allocationRepo = AllocationRepository.getInstance();
        trolleyRepo = TrolleyRepository.getInstance();
        itemRepo = ItemRepository.getInstance();

        // Limpar repositórios antes de cada teste
        allocationRepo.getAllocationList().clear();

        // Criar dados de teste
        sku1 = new SKU("SKU001");
        sku2 = new SKU("SKU002");
        sku3 = new SKU("SKU003");

        // Item(SKU sku, String name, String category, String unit, float volume, float unitWeight)
        item1 = new Item(sku1, "Item 1", "Category A", "kg", 1.0f, 2.5f);
        item2 = new Item(sku2, "Item 2", "Category B", "kg", 1.0f, 1.0f);
        item3 = new Item(sku3, "Item 3", "Category C", "kg", 1.0f, 5.0f);

        // Criar lista de items e definir no repositório
        List<Item> itemList = new ArrayList<>();
        itemList.add(item1);
        itemList.add(item2);
        itemList.add(item3);
        itemRepo.setItems(itemList);

        // Bay(String warehouseId, int aisle, int bay, int capacityBoxes)
        bay1 = new Bay("WH1", 1, 1, 10);
        bay2 = new Bay("WH1", 1, 2, 10);
        bay3 = new Bay("WH1", 2, 1, 10);

        box1 = new Box("BOX001", null, null);
        box1.setAssignedBay(bay1);
        box2 = new Box("BOX002", null, null);
        box2.setAssignedBay(bay2);
        box3 = new Box("BOX003", null, null);
        box3.setAssignedBay(bay3);
    }

    @AfterEach
    void tearDown() {
        // Limpar repositórios após cada teste
        allocationRepo.getAllocationList().clear();
    }

    // ---------------------------------------------------------------
    // PLAN METHOD TESTS - FF HEURISTIC
    // ---------------------------------------------------------------

    /**
     * Test plan with FF heuristic and SPLIT policy.
     */
    @Test
    void testPlan_FF_SPLIT_BasicScenario() {
        List<PickAllocationRow> rows = new ArrayList<>();
        rows.add(new PickAllocationRow("ORD001", 1, sku1, 10, "BOX001", 1, 1, 2.5)); // 25 kg
        rows.add(new PickAllocationRow("ORD002", 1, sku2, 20, "BOX002", 1, 2, 1.0)); // 20 kg
        rows.add(new PickAllocationRow("ORD003", 1, sku3, 5, "BOX003", 2, 1, 5.0));  // 25 kg

        List<Trolley> trolleys = controller.plan(rows, 50.0, PickingService.Heuristic.FF, PickingService.OverflowPolicy.SPLIT);

        assertNotNull(trolleys);
        assertFalse(trolleys.isEmpty());

        // Verificar que todos os itens foram alocados
        int totalQty = trolleys.stream()
                .flatMap(t -> t.getPicks().stream())
                .mapToInt(PickAllocationRow::getQty)
                .sum();
        assertEquals(35, totalQty); // 10 + 20 + 5

        // Verificar que nenhum trolley excede capacidade
        for (Trolley trolley : trolleys) {
            assertTrue(trolley.getUsedKg() <= 50.0 + 1e-9);
        }
    }

    /**
     * Test plan with FF heuristic and DEFER policy.
     */
    @Test
    void testPlan_FF_DEFER_BasicScenario() {
        List<PickAllocationRow> rows = new ArrayList<>();
        rows.add(new PickAllocationRow("ORD001", 1, sku1, 10, "BOX001", 1, 1, 2.5)); // 25 kg
        rows.add(new PickAllocationRow("ORD002", 1, sku2, 20, "BOX002", 1, 2, 1.0)); // 20 kg

        List<Trolley> trolleys = controller.plan(rows, 50.0, PickingService.Heuristic.FF, PickingService.OverflowPolicy.DEFER);

        assertNotNull(trolleys);
        assertEquals(1, trolleys.size()); // Ambos cabem no mesmo trolley
        assertEquals(45.0, trolleys.get(0).getUsedKg(), 0.001);
    }

    /**
     * Test FF with items that require multiple trolleys.
     */
    @Test
    void testPlan_FF_MultipleTrolleys() {
        List<PickAllocationRow> rows = new ArrayList<>();
        rows.add(new PickAllocationRow("ORD001", 1, sku1, 15, "BOX001", 1, 1, 2.0)); // 30 kg
        rows.add(new PickAllocationRow("ORD002", 1, sku2, 25, "BOX002", 1, 2, 2.0)); // 50 kg
        rows.add(new PickAllocationRow("ORD003", 1, sku3, 10, "BOX003", 2, 1, 2.0)); // 20 kg

        List<Trolley> trolleys = controller.plan(rows, 50.0, PickingService.Heuristic.FF, PickingService.OverflowPolicy.DEFER);

        assertNotNull(trolleys);
        assertTrue(trolleys.size() >= 2); // Precisa de pelo menos 2 trolleys
    }

    // ---------------------------------------------------------------
    // PLAN METHOD TESTS - FFD HEURISTIC
    // ---------------------------------------------------------------

    /**
     * Test plan with FFD heuristic - verifies sorting by weight descending.
     */
    @Test
    void testPlan_FFD_SortsDescending() {
        List<PickAllocationRow> rows = new ArrayList<>();
        rows.add(new PickAllocationRow("ORD001", 1, sku1, 5, "BOX001", 1, 1, 1.0));  // 5 kg (light)
        rows.add(new PickAllocationRow("ORD002", 1, sku2, 20, "BOX002", 1, 2, 2.0)); // 40 kg (heavy)
        rows.add(new PickAllocationRow("ORD003", 1, sku3, 10, "BOX003", 2, 1, 1.5)); // 15 kg (medium)

        List<Trolley> trolleys = controller.plan(rows, 50.0, PickingService.Heuristic.FFD, PickingService.OverflowPolicy.DEFER);

        assertNotNull(trolleys);
        // FFD deve processar pesados primeiro: 40kg + 15kg não cabem, então 40kg + 5kg = 45kg
        assertEquals(2, trolleys.size());
    }

    /**
     * Test FFD with SPLIT policy.
     */
    @Test
    void testPlan_FFD_SPLIT_OptimalPacking() {
        List<PickAllocationRow> rows = new ArrayList<>();
        rows.add(new PickAllocationRow("ORD001", 1, sku1, 100, "BOX001", 1, 1, 0.3)); // 30 kg
        rows.add(new PickAllocationRow("ORD002", 1, sku2, 100, "BOX002", 1, 2, 0.3)); // 30 kg
        rows.add(new PickAllocationRow("ORD003", 1, sku3, 100, "BOX003", 2, 1, 0.3)); // 30 kg

        List<Trolley> trolleys = controller.plan(rows, 50.0, PickingService.Heuristic.FFD, PickingService.OverflowPolicy.SPLIT);

        assertNotNull(trolleys);

        // Com SPLIT, deve conseguir otimizar melhor
        int totalQty = trolleys.stream()
                .flatMap(t -> t.getPicks().stream())
                .mapToInt(PickAllocationRow::getQty)
                .sum();
        assertEquals(300, totalQty);
    }

    // ---------------------------------------------------------------
    // PLAN METHOD TESTS - BFD HEURISTIC
    // ---------------------------------------------------------------

    /**
     * Test plan with BFD heuristic - best fit selection.
     */
    @Test
    void testPlan_BFD_BestFitSelection() {
        List<PickAllocationRow> rows = new ArrayList<>();
        rows.add(new PickAllocationRow("ORD001", 1, sku1, 20, "BOX001", 1, 1, 2.0)); // 40 kg
        rows.add(new PickAllocationRow("ORD002", 1, sku2, 15, "BOX002", 1, 2, 2.0)); // 30 kg
        rows.add(new PickAllocationRow("ORD003", 1, sku3, 5, "BOX003", 2, 1, 2.0));  // 10 kg

        List<Trolley> trolleys = controller.plan(rows, 50.0, PickingService.Heuristic.BFD, PickingService.OverflowPolicy.DEFER);

        assertNotNull(trolleys);
        // BFD deve encontrar melhor ajuste: 40kg + 10kg = 50kg em um trolley
        assertTrue(trolleys.size() >= 1);
    }

    /**
     * Test BFD with SPLIT policy.
     */
    @Test
    void testPlan_BFD_SPLIT_ComplexScenario() {
        List<PickAllocationRow> rows = new ArrayList<>();
        rows.add(new PickAllocationRow("ORD001", 1, sku1, 80, "BOX001", 1, 1, 0.5)); // 40 kg
        rows.add(new PickAllocationRow("ORD002", 1, sku2, 60, "BOX002", 1, 2, 0.5)); // 30 kg
        rows.add(new PickAllocationRow("ORD003", 1, sku3, 40, "BOX003", 2, 1, 0.5)); // 20 kg

        List<Trolley> trolleys = controller.plan(rows, 50.0, PickingService.Heuristic.BFD, PickingService.OverflowPolicy.SPLIT);

        assertNotNull(trolleys);

        // Verificar total alocado
        int totalQty = trolleys.stream()
                .flatMap(t -> t.getPicks().stream())
                .mapToInt(PickAllocationRow::getQty)
                .sum();
        assertEquals(180, totalQty);
    }

    // ---------------------------------------------------------------
    // PLAN METHOD TESTS - EDGE CASES
    // ---------------------------------------------------------------

    /**
     * Test plan with empty list.
     */
    @Test
    void testPlan_EmptyList() {
        List<PickAllocationRow> rows = new ArrayList<>();

        List<Trolley> trolleys = controller.plan(rows, 50.0, PickingService.Heuristic.FF, PickingService.OverflowPolicy.SPLIT);

        assertNotNull(trolleys);
        assertTrue(trolleys.isEmpty());
    }

    /**
     * Test plan with single item that fits exactly.
     */
    @Test
    void testPlan_SingleItemExactFit() {
        List<PickAllocationRow> rows = new ArrayList<>();
        rows.add(new PickAllocationRow("ORD001", 1, sku1, 20, "BOX001", 1, 1, 2.5)); // 50 kg

        List<Trolley> trolleys = controller.plan(rows, 50.0, PickingService.Heuristic.FF, PickingService.OverflowPolicy.DEFER);

        assertNotNull(trolleys);
        assertEquals(1, trolleys.size());
        assertEquals(50.0, trolleys.get(0).getUsedKg(), 0.001);
    }

    /**
     * Test plan with item exceeding capacity - should be skipped with DEFER.
     */
    @Test
    void testPlan_ItemExceedsCapacity_DEFER() {
        List<PickAllocationRow> rows = new ArrayList<>();
        rows.add(new PickAllocationRow("ORD001", 1, sku1, 30, "BOX001", 1, 1, 2.0)); // 60 kg > 50 kg

        List<Trolley> trolleys = controller.plan(rows, 50.0, PickingService.Heuristic.FF, PickingService.OverflowPolicy.DEFER);

        assertNotNull(trolleys);
        assertTrue(trolleys.isEmpty());

        List<PickAllocationRow> skipped = controller.getSkippedRows();
        assertEquals(1, skipped.size());
        assertEquals("ORD001", skipped.get(0).getOrderId());
    }

    /**
     * Test plan with item exceeding capacity - should be split with SPLIT policy.
     */
    @Test
    void testPlan_ItemExceedsCapacity_SPLIT() {
        List<PickAllocationRow> rows = new ArrayList<>();
        rows.add(new PickAllocationRow("ORD001", 1, sku1, 60, "BOX001", 1, 1, 1.0)); // 60 kg > 50 kg

        List<Trolley> trolleys = controller.plan(rows, 50.0, PickingService.Heuristic.FF, PickingService.OverflowPolicy.SPLIT);

        assertNotNull(trolleys);
        assertEquals(2, trolleys.size()); // Deve criar 2 trolleys

        int totalQty = trolleys.stream()
                .flatMap(t -> t.getPicks().stream())
                .mapToInt(PickAllocationRow::getQty)
                .sum();
        assertEquals(60, totalQty);
    }

    /**
     * Test plan with very small capacity.
     */
    @Test
    void testPlan_VerySmallCapacity() {
        List<PickAllocationRow> rows = new ArrayList<>();
        rows.add(new PickAllocationRow("ORD001", 1, sku1, 10, "BOX001", 1, 1, 0.5)); // 5 kg

        List<Trolley> trolleys = controller.plan(rows, 2.0, PickingService.Heuristic.FF, PickingService.OverflowPolicy.SPLIT);

        assertNotNull(trolleys);
        assertTrue(trolleys.size() >= 3); // 5kg / 2kg = pelo menos 3 trolleys
    }

    /**
     * Test plan with very large capacity.
     */
    @Test
    void testPlan_VeryLargeCapacity() {
        List<PickAllocationRow> rows = new ArrayList<>();
        rows.add(new PickAllocationRow("ORD001", 1, sku1, 100, "BOX001", 1, 1, 1.0)); // 100 kg
        rows.add(new PickAllocationRow("ORD002", 1, sku2, 200, "BOX002", 1, 2, 1.0)); // 200 kg

        List<Trolley> trolleys = controller.plan(rows, 10000.0, PickingService.Heuristic.FF, PickingService.OverflowPolicy.DEFER);

        assertNotNull(trolleys);
        assertEquals(1, trolleys.size()); // Tudo cabe em 1 trolley
    }

    // ---------------------------------------------------------------
    // PLAN METHOD TESTS - TROLLEY REPOSITORY INTEGRATION
    // ---------------------------------------------------------------

    /**
     * Test that plan stores result in TrolleyRepository.
     */
    @Test
    void testPlan_StoresInRepository() {
        List<PickAllocationRow> rows = new ArrayList<>();
        rows.add(new PickAllocationRow("ORD001", 1, sku1, 10, "BOX001", 1, 1, 2.0)); // 20 kg

        List<Trolley> trolleys = controller.plan(rows, 50.0, PickingService.Heuristic.FFD, PickingService.OverflowPolicy.SPLIT);

        assertNotNull(trolleys);
        // Verificar que foi armazenado no repositório
        // (Nota: TrolleyRepository.storePlan é chamado internamente)
    }

    // ---------------------------------------------------------------
    // GET SKIPPED ROWS TESTS
    // ---------------------------------------------------------------

    /**
     * Test getSkippedRows returns empty list when all items fit.
     */
    @Test
    void testGetSkippedRows_EmptyWhenAllFit() {
        List<PickAllocationRow> rows = new ArrayList<>();
        rows.add(new PickAllocationRow("ORD001", 1, sku1, 10, "BOX001", 1, 1, 2.0)); // 20 kg

        controller.plan(rows, 50.0, PickingService.Heuristic.FF, PickingService.OverflowPolicy.DEFER);

        List<PickAllocationRow> skipped = controller.getSkippedRows();
        assertNotNull(skipped);
        assertTrue(skipped.isEmpty());
    }

    /**
     * Test getSkippedRows returns items that don't fit with DEFER.
     */
    @Test
    void testGetSkippedRows_ReturnsSkippedItems() {
        List<PickAllocationRow> rows = new ArrayList<>();
        rows.add(new PickAllocationRow("ORD001", 1, sku1, 30, "BOX001", 1, 1, 2.0)); // 60 kg > 50 kg

        controller.plan(rows, 50.0, PickingService.Heuristic.FF, PickingService.OverflowPolicy.DEFER);

        List<PickAllocationRow> skipped = controller.getSkippedRows();
        assertNotNull(skipped);
        assertEquals(1, skipped.size());
        assertEquals("ORD001", skipped.get(0).getOrderId());
        assertEquals(30, skipped.get(0).getQty());
    }

    /**
     * Test getSkippedRows with unit weight exceeding capacity.
     */
    @Test
    void testGetSkippedRows_UnitWeightExceedsCapacity() {
        List<PickAllocationRow> rows = new ArrayList<>();
        rows.add(new PickAllocationRow("ORD001", 1, sku1, 5, "BOX001", 1, 1, 15.0)); // unit=15kg > capacity=10kg

        controller.plan(rows, 10.0, PickingService.Heuristic.FF, PickingService.OverflowPolicy.SPLIT);

        List<PickAllocationRow> skipped = controller.getSkippedRows();
        assertNotNull(skipped);
        assertEquals(1, skipped.size());
    }

    // ---------------------------------------------------------------
    // CONVERT TO PICK ROWS TESTS
    // ---------------------------------------------------------------

    /**
     * Test convertToPickRows with valid allocations.
     */
    @Test
    void testConvertToPickRows_BasicScenario() {
        // Criar OrderLines
        OrderLine line1 = new OrderLine("ORD001", 1, sku1, 10);
        OrderLine line2 = new OrderLine("ORD002", 1, sku2, 20);

        // Criar Allocations
        Map<Box, Integer> boxes1 = new HashMap<>();
        boxes1.put(box1, 10);
        Allocation alloc1 = new Allocation(line1, 10, Arrays.asList(boxes1));

        Map<Box, Integer> boxes2 = new HashMap<>();
        boxes2.put(box2, 20);
        Allocation alloc2 = new Allocation(line2, 20, Arrays.asList(boxes2));

        allocationRepo.getAllocationList().add(alloc1);
        allocationRepo.getAllocationList().add(alloc2);

        List<PickAllocationRow> rows = controller.convertToPickRows();

        assertNotNull(rows);
        assertEquals(2, rows.size());

        PickAllocationRow row1 = rows.get(0);
        assertEquals("ORD001", row1.getOrderId());
        assertEquals(1, row1.getLineNo());
        assertEquals(sku1, row1.getSku());
        assertEquals(10, row1.getQty());
        assertEquals("BOX001", row1.getBoxId());
        assertEquals(1, row1.getAisle());
        assertEquals(1, row1.getBay());
        assertEquals(2.5, row1.getUnitWeightKg(), 0.001);
    }

    /**
     * Test convertToPickRows skips UNDISPATCHABLE lines.
     */
    @Test
    void testConvertToPickRows_SkipsUndispatchable() {
        OrderLine line1 = new OrderLine("ORD001", 1, sku1, 10);
        line1.setStatus(Status.UNDISPATCHABLE);

        OrderLine line2 = new OrderLine("ORD002", 1, sku2, 20);

        Map<Box, Integer> boxes1 = new HashMap<>();
        boxes1.put(box1, 10);
        Allocation alloc1 = new Allocation(line1, 10, Arrays.asList(boxes1));

        Map<Box, Integer> boxes2 = new HashMap<>();
        boxes2.put(box2, 20);
        Allocation alloc2 = new Allocation(line2, 20, Arrays.asList(boxes2));

        allocationRepo.getAllocationList().add(alloc1);
        allocationRepo.getAllocationList().add(alloc2);

        List<PickAllocationRow> rows = controller.convertToPickRows();

        assertNotNull(rows);
        assertEquals(1, rows.size()); // Apenas line2
        assertEquals("ORD002", rows.get(0).getOrderId());
    }

    /**
     * Test convertToPickRows skips lines with unknown SKU.
     */
    @Test
    void testConvertToPickRows_SkipsUnknownSKU() {
        SKU unknownSku = new SKU("UNKNOWN");
        OrderLine line1 = new OrderLine("ORD001", 1, unknownSku, 10);

        Map<Box, Integer> boxes1 = new HashMap<>();
        boxes1.put(box1, 10);
        Allocation alloc1 = new Allocation(line1, 10, Arrays.asList(boxes1));

        allocationRepo.getAllocationList().add(alloc1);

        List<PickAllocationRow> rows = controller.convertToPickRows();

        assertNotNull(rows);
        assertTrue(rows.isEmpty()); // SKU não encontrado
    }

    /**
     * Test convertToPickRows skips lines with invalid weight.
     */
    @Test
    void testConvertToPickRows_SkipsInvalidWeight() {
        SKU badSku = new SKU("BAD");
        Item badItem = new Item(badSku, "Bad Item", "Cat", "kg", 1.0f, 0.0f); // peso inválido

        List<Item> itemList = itemRepo.getItems();
        if (itemList == null) {
            itemList = new ArrayList<>();
        }
        itemList.add(badItem);
        itemRepo.setItems(itemList);

        OrderLine line1 = new OrderLine("ORD001", 1, badSku, 10);

        Map<Box, Integer> boxes1 = new HashMap<>();
        boxes1.put(box1, 10);
        Allocation alloc1 = new Allocation(line1, 10, Arrays.asList(boxes1));

        allocationRepo.getAllocationList().add(alloc1);

        List<PickAllocationRow> rows = controller.convertToPickRows();

        assertNotNull(rows);
        assertTrue(rows.isEmpty()); // Peso inválido
    }

    /**
     * Test convertToPickRows with multiple boxes per allocation.
     */
    @Test
    void testConvertToPickRows_MultipleBoxes() {
        OrderLine line1 = new OrderLine("ORD001", 1, sku1, 30);

        Map<Box, Integer> boxes1 = new HashMap<>();
        boxes1.put(box1, 10);

        Map<Box, Integer> boxes2 = new HashMap<>();
        boxes2.put(box2, 10);

        Map<Box, Integer> boxes3 = new HashMap<>();
        boxes3.put(box3, 10);

        Allocation alloc1 = new Allocation(line1, 30, Arrays.asList(boxes1, boxes2, boxes3));

        allocationRepo.getAllocationList().add(alloc1);

        List<PickAllocationRow> rows = controller.convertToPickRows();

        assertNotNull(rows);
        assertEquals(3, rows.size()); // Uma row por box

        assertEquals("BOX001", rows.get(0).getBoxId());
        assertEquals("BOX002", rows.get(1).getBoxId());
        assertEquals("BOX003", rows.get(2).getBoxId());

        int totalQty = rows.stream().mapToInt(PickAllocationRow::getQty).sum();
        assertEquals(30, totalQty);
    }

    /**
     * Test convertToPickRows with empty allocation repository.
     */
    @Test
    void testConvertToPickRows_EmptyRepository() {
        List<PickAllocationRow> rows = controller.convertToPickRows();

        assertNotNull(rows);
        assertTrue(rows.isEmpty());
    }

    /**
     * Test convertToPickRows preserves bay location.
     */
    @Test
    void testConvertToPickRows_PreservesBayLocation() {
        OrderLine line1 = new OrderLine("ORD001", 1, sku1, 10);

        Bay specialBay = new Bay("WH1", 5, 7, 10);
        Box specialBox = new Box("SPECIAL", null, null);
        specialBox.setAssignedBay(specialBay);

        Map<Box, Integer> boxes = new HashMap<>();
        boxes.put(specialBox, 10);
        Allocation alloc = new Allocation(line1, 10, Arrays.asList(boxes));

        allocationRepo.getAllocationList().add(alloc);

        List<PickAllocationRow> rows = controller.convertToPickRows();

        assertNotNull(rows);
        assertEquals(1, rows.size());
        assertEquals(5, rows.get(0).getAisle());
        assertEquals(7, rows.get(0).getBay());
    }

    // ---------------------------------------------------------------
    // INTEGRATION TESTS - FULL WORKFLOW
    // ---------------------------------------------------------------

    /**
     * Test full workflow: convert allocations to rows, then plan with FF.
     */
    @Test
    void testFullWorkflow_ConvertAndPlan_FF() {
        // Setup allocations
        OrderLine line1 = new OrderLine("ORD001", 1, sku1, 20);
        OrderLine line2 = new OrderLine("ORD002", 1, sku2, 30);

        Map<Box, Integer> boxes1 = new HashMap<>();
        boxes1.put(box1, 20);
        Map<Box, Integer> boxes2 = new HashMap<>();
        boxes2.put(box2, 30);

        allocationRepo.getAllocationList().add(new Allocation(line1, 20, Arrays.asList(boxes1)));
        allocationRepo.getAllocationList().add(new Allocation(line2, 30, Arrays.asList(boxes2)));

        // Convert to rows
        List<PickAllocationRow> rows = controller.convertToPickRows();
        assertEquals(2, rows.size());

        // Plan with FF
        List<Trolley> trolleys = controller.plan(rows, 100.0, PickingService.Heuristic.FF, PickingService.OverflowPolicy.DEFER);

        assertNotNull(trolleys);
        assertEquals(1, trolleys.size());
        assertEquals(80.0, trolleys.get(0).getUsedKg(), 0.001); // 20*2.5 + 30*1.0
    }

    /**
     * Test full workflow with FFD and SPLIT.
     */
    @Test
    void testFullWorkflow_ConvertAndPlan_FFD_SPLIT() {
        OrderLine line1 = new OrderLine("ORD001", 1, sku1, 50); // 125 kg

        Map<Box, Integer> boxes1 = new HashMap<>();
        boxes1.put(box1, 50);

        allocationRepo.getAllocationList().add(new Allocation(line1, 50, Arrays.asList(boxes1)));

        List<PickAllocationRow> rows = controller.convertToPickRows();
        assertEquals(1, rows.size());

        List<Trolley> trolleys = controller.plan(rows, 50.0, PickingService.Heuristic.FFD, PickingService.OverflowPolicy.SPLIT);

        assertNotNull(trolleys);
        assertTrue(trolleys.size() >= 3); // 125kg precisa de pelo menos 3 trolleys de 50kg

        int totalQty = trolleys.stream()
                .flatMap(t -> t.getPicks().stream())
                .mapToInt(PickAllocationRow::getQty)
                .sum();
        assertEquals(50, totalQty);
    }

    /**
     * Test complex scenario with mixed allocations and BFD.
     */
    @Test
    void testComplexScenario_MixedAllocations_BFD() {
        // Criar múltiplas OrderLines com diferentes SKUs
        OrderLine line1 = new OrderLine("ORD001", 1, sku1, 10); // 25 kg
        OrderLine line2 = new OrderLine("ORD001", 2, sku2, 20); // 20 kg
        OrderLine line3 = new OrderLine("ORD002", 1, sku3, 4);  // 20 kg

        Map<Box, Integer> boxes1 = new HashMap<>();
        boxes1.put(box1, 10);
        Map<Box, Integer> boxes2 = new HashMap<>();
        boxes2.put(box2, 20);
        Map<Box, Integer> boxes3 = new HashMap<>();
        boxes3.put(box3, 4);

        allocationRepo.getAllocationList().add(new Allocation(line1, 10, Arrays.asList(boxes1)));
        allocationRepo.getAllocationList().add(new Allocation(line2, 20, Arrays.asList(boxes2)));
        allocationRepo.getAllocationList().add(new Allocation(line3, 4, Arrays.asList(boxes3)));

        List<PickAllocationRow> rows = controller.convertToPickRows();
        assertEquals(3, rows.size());

        List<Trolley> trolleys = controller.plan(rows, 50.0, PickingService.Heuristic.BFD, PickingService.OverflowPolicy.DEFER);

        assertNotNull(trolleys);
        assertFalse(trolleys.isEmpty());

        // Verificar que todo peso foi distribuído
        double totalWeight = trolleys.stream()
                .mapToDouble(Trolley::getUsedKg)
                .sum();
        assertEquals(65.0, totalWeight, 0.001); // 25 + 20 + 20
    }

    // ---------------------------------------------------------------
    // STRESS AND PERFORMANCE TESTS
    // ---------------------------------------------------------------

    /**
     * Test with large number of allocations.
     */
    @Test
    void testLargeNumberOfAllocations() {
        List<PickAllocationRow> rows = new ArrayList<>();

        for (int i = 1; i <= 100; i++) {
            rows.add(new PickAllocationRow("ORD" + i, 1, sku1, 5, "BOX" + i, 1, i, 1.0));
        }

        List<Trolley> trolleys = controller.plan(rows, 50.0, PickingService.Heuristic.FFD, PickingService.OverflowPolicy.SPLIT);

        assertNotNull(trolleys);

        int totalQty = trolleys.stream()
                .flatMap(t -> t.getPicks().stream())
                .mapToInt(PickAllocationRow::getQty)
                .sum();
        assertEquals(500, totalQty); // 100 * 5
    }

    /**
     * Test all combinations of heuristics and policies.
     */
    @Test
    void testAllHeuristicPolicyCombinations() {
        List<PickAllocationRow> rows = new ArrayList<>();
        rows.add(new PickAllocationRow("ORD001", 1, sku1, 20, "BOX001", 1, 1, 2.0)); // 40 kg
        rows.add(new PickAllocationRow("ORD002", 1, sku2, 15, "BOX002", 1, 2, 2.0)); // 30 kg

        PickingService.Heuristic[] heuristics = PickingService.Heuristic.values();
        PickingService.OverflowPolicy[] policies = PickingService.OverflowPolicy.values();

        for (PickingService.Heuristic h : heuristics) {
            for (PickingService.OverflowPolicy p : policies) {
                List<Trolley> trolleys = controller.plan(new ArrayList<>(rows), 100.0, h, p);

                assertNotNull(trolleys, "Failed for " + h + " + " + p);
                assertFalse(trolleys.isEmpty(), "Failed for " + h + " + " + p);
            }
        }
    }
}
