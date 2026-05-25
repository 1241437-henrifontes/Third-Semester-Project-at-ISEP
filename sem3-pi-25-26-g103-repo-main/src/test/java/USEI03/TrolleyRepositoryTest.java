package USEI03;

import Repositories.TrolleyRepository;
import Model.PickAllocationRow;
import Model.SKU;
import Model.Trolley;
import Services.PickingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive test suite for TrolleyRepository class.
 * Tests all methods, singleton pattern, defensive copies, and metadata storage.
 *
 * Critical areas tested:
 * - Singleton pattern (getInstance)
 * - storePlan() method with validation
 * - getAllTrolleys() defensive copy
 * - getTrolley() by index with bounds checking
 * - getTrolleyCount() and hasTrolleys()
 * - clear() method
 * - Metadata getters (lastHeuristic, lastPolicy, lastTrolleyCapacity)
 * - Edge cases: null inputs, empty lists, out of bounds
 * - State management across multiple operations
 */
public class TrolleyRepositoryTest {

    private TrolleyRepository repository;
    private List<Trolley> sampleTrolleys;
    private SKU sku1;

    @BeforeEach
    void setUp() {
        repository = TrolleyRepository.getInstance();
        repository.clear(); // Limpar antes de cada teste

        sku1 = new SKU("SKU001");

        // Criar trolleys de exemplo
        sampleTrolleys = new ArrayList<>();
        Trolley t1 = new Trolley(50.0);
        t1.add(new PickAllocationRow("ORD001", 1, sku1, 10, "BOX1", 1, 1, 2.0));

        Trolley t2 = new Trolley(50.0);
        t2.add(new PickAllocationRow("ORD002", 1, sku1, 15, "BOX2", 1, 2, 2.0));

        sampleTrolleys.add(t1);
        sampleTrolleys.add(t2);
    }

    // ---------------------------------------------------------------
    // SINGLETON PATTERN TESTS
    // ---------------------------------------------------------------

    /**
     * Test that getInstance returns the same instance.
     */
    @Test
    void testGetInstance_ReturnsSameInstance() {
        TrolleyRepository instance1 = TrolleyRepository.getInstance();
        TrolleyRepository instance2 = TrolleyRepository.getInstance();

        assertSame(instance1, instance2);
    }

    /**
     * Test that singleton instance is not null.
     */
    @Test
    void testGetInstance_NotNull() {
        TrolleyRepository instance = TrolleyRepository.getInstance();
        assertNotNull(instance);
    }

    /**
     * Test that singleton maintains state across calls.
     */
    @Test
    void testSingleton_MaintainsState() {
        TrolleyRepository repo1 = TrolleyRepository.getInstance();
        repo1.storePlan(sampleTrolleys, PickingService.Heuristic.FF, PickingService.OverflowPolicy.SPLIT, 50.0);

        TrolleyRepository repo2 = TrolleyRepository.getInstance();

        assertEquals(2, repo2.getTrolleyCount());
    }

    // ---------------------------------------------------------------
    // STORE PLAN TESTS
    // ---------------------------------------------------------------

    /**
     * Test storePlan with valid trolleys.
     */
    @Test
    void testStorePlan_ValidTrolleys() {
        repository.storePlan(sampleTrolleys, PickingService.Heuristic.FF, PickingService.OverflowPolicy.SPLIT, 50.0);

        assertEquals(2, repository.getTrolleyCount());
        assertEquals(PickingService.Heuristic.FF, repository.getLastHeuristic());
        assertEquals(PickingService.OverflowPolicy.SPLIT, repository.getLastPolicy());
        assertEquals(50.0, repository.getLastTrolleyCapacity(), 0.001);
    }

    /**
     * Test storePlan with null trolleys throws exception.
     */
    @Test
    void testStorePlan_NullTrolleys_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () ->
                repository.storePlan(null, PickingService.Heuristic.FF, PickingService.OverflowPolicy.SPLIT, 50.0)
        );
    }

    /**
     * Test storePlan with empty list.
     */
    @Test
    void testStorePlan_EmptyList() {
        List<Trolley> emptyList = new ArrayList<>();

        repository.storePlan(emptyList, PickingService.Heuristic.FFD, PickingService.OverflowPolicy.DEFER, 100.0);

        assertEquals(0, repository.getTrolleyCount());
        assertFalse(repository.hasTrolleys());
        assertEquals(PickingService.Heuristic.FFD, repository.getLastHeuristic());
    }

    /**
     * Test storePlan creates defensive copy.
     */
    @Test
    void testStorePlan_CreatesDefensiveCopy() {
        List<Trolley> original = new ArrayList<>(sampleTrolleys);

        repository.storePlan(original, PickingService.Heuristic.FF, PickingService.OverflowPolicy.SPLIT, 50.0);

        // Modificar lista original não deve afetar o repositório
        original.clear();

        assertEquals(2, repository.getTrolleyCount());
    }

    /**
     * Test storePlan replaces previous data.
     */
    @Test
    void testStorePlan_ReplacesPreviousData() {
        // Armazenar primeiro plano
        repository.storePlan(sampleTrolleys, PickingService.Heuristic.FF, PickingService.OverflowPolicy.SPLIT, 50.0);
        assertEquals(2, repository.getTrolleyCount());

        // Armazenar novo plano
        List<Trolley> newTrolleys = new ArrayList<>();
        newTrolleys.add(new Trolley(100.0));

        repository.storePlan(newTrolleys, PickingService.Heuristic.BFD, PickingService.OverflowPolicy.DEFER, 100.0);

        assertEquals(1, repository.getTrolleyCount());
        assertEquals(PickingService.Heuristic.BFD, repository.getLastHeuristic());
        assertEquals(PickingService.OverflowPolicy.DEFER, repository.getLastPolicy());
        assertEquals(100.0, repository.getLastTrolleyCapacity(), 0.001);
    }

    /**
     * Test storePlan with all heuristic values.
     */
    @Test
    void testStorePlan_AllHeuristics() {
        for (PickingService.Heuristic h : PickingService.Heuristic.values()) {
            repository.storePlan(sampleTrolleys, h, PickingService.OverflowPolicy.SPLIT, 50.0);
            assertEquals(h, repository.getLastHeuristic());
        }
    }

    /**
     * Test storePlan with all policy values.
     */
    @Test
    void testStorePlan_AllPolicies() {
        for (PickingService.OverflowPolicy p : PickingService.OverflowPolicy.values()) {
            repository.storePlan(sampleTrolleys, PickingService.Heuristic.FF, p, 50.0);
            assertEquals(p, repository.getLastPolicy());
        }
    }

    /**
     * Test storePlan with different capacities.
     */
    @Test
    void testStorePlan_DifferentCapacities() {
        double[] capacities = {1.0, 10.0, 50.0, 100.0, 1000.0};

        for (double capacity : capacities) {
            repository.storePlan(sampleTrolleys, PickingService.Heuristic.FF, PickingService.OverflowPolicy.SPLIT, capacity);
            assertEquals(capacity, repository.getLastTrolleyCapacity(), 0.001);
        }
    }

    // ---------------------------------------------------------------
    // GET ALL TROLLEYS TESTS
    // ---------------------------------------------------------------

    /**
     * Test getAllTrolleys returns all stored trolleys.
     */
    @Test
    void testGetAllTrolleys_ReturnsAll() {
        repository.storePlan(sampleTrolleys, PickingService.Heuristic.FF, PickingService.OverflowPolicy.SPLIT, 50.0);

        List<Trolley> retrieved = repository.getAllTrolleys();

        assertEquals(2, retrieved.size());
    }

    /**
     * Test getAllTrolleys returns defensive copy.
     */
    @Test
    void testGetAllTrolleys_ReturnsDefensiveCopy() {
        repository.storePlan(sampleTrolleys, PickingService.Heuristic.FF, PickingService.OverflowPolicy.SPLIT, 50.0);

        List<Trolley> retrieved1 = repository.getAllTrolleys();
        List<Trolley> retrieved2 = repository.getAllTrolleys();

        assertNotSame(retrieved1, retrieved2);
        assertEquals(retrieved1.size(), retrieved2.size());
    }

    /**
     * Test getAllTrolleys - modifications don't affect repository.
     */
    @Test
    void testGetAllTrolleys_ModificationsDontAffectRepository() {
        repository.storePlan(sampleTrolleys, PickingService.Heuristic.FF, PickingService.OverflowPolicy.SPLIT, 50.0);

        List<Trolley> retrieved = repository.getAllTrolleys();
        retrieved.clear();

        assertEquals(2, repository.getTrolleyCount());
    }

    /**
     * Test getAllTrolleys when empty.
     */
    @Test
    void testGetAllTrolleys_WhenEmpty() {
        List<Trolley> retrieved = repository.getAllTrolleys();

        assertNotNull(retrieved);
        assertTrue(retrieved.isEmpty());
    }

    // ---------------------------------------------------------------
    // GET TROLLEY BY INDEX TESTS
    // ---------------------------------------------------------------

    /**
     * Test getTrolley with valid index.
     */
    @Test
    void testGetTrolley_ValidIndex() {
        repository.storePlan(sampleTrolleys, PickingService.Heuristic.FF, PickingService.OverflowPolicy.SPLIT, 50.0);

        Trolley t0 = repository.getTrolley(0);
        Trolley t1 = repository.getTrolley(1);

        assertNotNull(t0);
        assertNotNull(t1);
        assertNotSame(t0, t1);
    }

    /**
     * Test getTrolley with negative index throws exception.
     */
    @Test
    void testGetTrolley_NegativeIndex_ThrowsException() {
        repository.storePlan(sampleTrolleys, PickingService.Heuristic.FF, PickingService.OverflowPolicy.SPLIT, 50.0);

        assertThrows(IndexOutOfBoundsException.class, () -> repository.getTrolley(-1));
    }

    /**
     * Test getTrolley with index too large throws exception.
     */
    @Test
    void testGetTrolley_IndexTooLarge_ThrowsException() {
        repository.storePlan(sampleTrolleys, PickingService.Heuristic.FF, PickingService.OverflowPolicy.SPLIT, 50.0);

        assertThrows(IndexOutOfBoundsException.class, () -> repository.getTrolley(2));
    }

    /**
     * Test getTrolley when repository is empty.
     */
    @Test
    void testGetTrolley_EmptyRepository_ThrowsException() {
        assertThrows(IndexOutOfBoundsException.class, () -> repository.getTrolley(0));
    }

    /**
     * Test getTrolley returns correct trolley.
     */
    @Test
    void testGetTrolley_ReturnsCorrectTrolley() {
        repository.storePlan(sampleTrolleys, PickingService.Heuristic.FF, PickingService.OverflowPolicy.SPLIT, 50.0);

        Trolley first = repository.getTrolley(0);
        Trolley second = repository.getTrolley(1);

        // Verificar que retorna trolleys diferentes
        assertTrue(first.getUsedKg() != second.getUsedKg() ||
                   first.getPicks().size() != second.getPicks().size());
    }

    // ---------------------------------------------------------------
    // GET TROLLEY COUNT TESTS
    // ---------------------------------------------------------------

    /**
     * Test getTrolleyCount with empty repository.
     */
    @Test
    void testGetTrolleyCount_Empty() {
        assertEquals(0, repository.getTrolleyCount());
    }

    /**
     * Test getTrolleyCount with trolleys.
     */
    @Test
    void testGetTrolleyCount_WithTrolleys() {
        repository.storePlan(sampleTrolleys, PickingService.Heuristic.FF, PickingService.OverflowPolicy.SPLIT, 50.0);

        assertEquals(2, repository.getTrolleyCount());
    }

    /**
     * Test getTrolleyCount after multiple operations.
     */
    @Test
    void testGetTrolleyCount_AfterMultipleOperations() {
        // Armazenar 2 trolleys
        repository.storePlan(sampleTrolleys, PickingService.Heuristic.FF, PickingService.OverflowPolicy.SPLIT, 50.0);
        assertEquals(2, repository.getTrolleyCount());

        // Limpar
        repository.clear();
        assertEquals(0, repository.getTrolleyCount());

        // Armazenar 1 trolley
        List<Trolley> single = new ArrayList<>();
        single.add(new Trolley(50.0));
        repository.storePlan(single, PickingService.Heuristic.BFD, PickingService.OverflowPolicy.DEFER, 50.0);
        assertEquals(1, repository.getTrolleyCount());
    }

    // ---------------------------------------------------------------
    // HAS TROLLEYS TESTS
    // ---------------------------------------------------------------

    /**
     * Test hasTrolleys when empty.
     */
    @Test
    void testHasTrolleys_Empty() {
        assertFalse(repository.hasTrolleys());
    }

    /**
     * Test hasTrolleys when not empty.
     */
    @Test
    void testHasTrolleys_NotEmpty() {
        repository.storePlan(sampleTrolleys, PickingService.Heuristic.FF, PickingService.OverflowPolicy.SPLIT, 50.0);

        assertTrue(repository.hasTrolleys());
    }

    /**
     * Test hasTrolleys after clear.
     */
    @Test
    void testHasTrolleys_AfterClear() {
        repository.storePlan(sampleTrolleys, PickingService.Heuristic.FF, PickingService.OverflowPolicy.SPLIT, 50.0);
        assertTrue(repository.hasTrolleys());

        repository.clear();
        assertFalse(repository.hasTrolleys());
    }

    // ---------------------------------------------------------------
    // CLEAR TESTS
    // ---------------------------------------------------------------

    /**
     * Test clear removes all trolleys.
     */
    @Test
    void testClear_RemovesAllTrolleys() {
        repository.storePlan(sampleTrolleys, PickingService.Heuristic.FF, PickingService.OverflowPolicy.SPLIT, 50.0);
        assertEquals(2, repository.getTrolleyCount());

        repository.clear();

        assertEquals(0, repository.getTrolleyCount());
        assertFalse(repository.hasTrolleys());
    }

    /**
     * Test clear resets metadata.
     */
    @Test
    void testClear_ResetsMetadata() {
        repository.storePlan(sampleTrolleys, PickingService.Heuristic.FFD, PickingService.OverflowPolicy.DEFER, 100.0);

        repository.clear();

        assertNull(repository.getLastHeuristic());
        assertNull(repository.getLastPolicy());
        assertEquals(0.0, repository.getLastTrolleyCapacity(), 0.001);
    }

    /**
     * Test clear on empty repository.
     */
    @Test
    void testClear_OnEmptyRepository() {
        repository.clear();

        assertEquals(0, repository.getTrolleyCount());
        assertFalse(repository.hasTrolleys());
    }

    /**
     * Test clear can be called multiple times.
     */
    @Test
    void testClear_MultipleTimes() {
        repository.storePlan(sampleTrolleys, PickingService.Heuristic.FF, PickingService.OverflowPolicy.SPLIT, 50.0);

        repository.clear();
        repository.clear();
        repository.clear();

        assertEquals(0, repository.getTrolleyCount());
    }

    // ---------------------------------------------------------------
    // METADATA GETTER TESTS
    // ---------------------------------------------------------------

    /**
     * Test getLastHeuristic returns stored value.
     */
    @Test
    void testGetLastHeuristic() {
        repository.storePlan(sampleTrolleys, PickingService.Heuristic.BFD, PickingService.OverflowPolicy.SPLIT, 50.0);

        assertEquals(PickingService.Heuristic.BFD, repository.getLastHeuristic());
    }

    /**
     * Test getLastPolicy returns stored value.
     */
    @Test
    void testGetLastPolicy() {
        repository.storePlan(sampleTrolleys, PickingService.Heuristic.FF, PickingService.OverflowPolicy.DEFER, 50.0);

        assertEquals(PickingService.OverflowPolicy.DEFER, repository.getLastPolicy());
    }

    /**
     * Test getLastTrolleyCapacity returns stored value.
     */
    @Test
    void testGetLastTrolleyCapacity() {
        repository.storePlan(sampleTrolleys, PickingService.Heuristic.FF, PickingService.OverflowPolicy.SPLIT, 123.45);

        assertEquals(123.45, repository.getLastTrolleyCapacity(), 0.001);
    }

    /**
     * Test metadata getters when not initialized.
     */
    @Test
    void testMetadataGetters_NotInitialized() {
        assertNull(repository.getLastHeuristic());
        assertNull(repository.getLastPolicy());
        assertEquals(0.0, repository.getLastTrolleyCapacity(), 0.001);
    }

    /**
     * Test metadata persists until clear.
     */
    @Test
    void testMetadata_PersistsUntilClear() {
        repository.storePlan(sampleTrolleys, PickingService.Heuristic.FFD, PickingService.OverflowPolicy.SPLIT, 75.0);

        assertEquals(PickingService.Heuristic.FFD, repository.getLastHeuristic());
        assertEquals(PickingService.OverflowPolicy.SPLIT, repository.getLastPolicy());
        assertEquals(75.0, repository.getLastTrolleyCapacity(), 0.001);

        repository.clear();

        assertNull(repository.getLastHeuristic());
        assertNull(repository.getLastPolicy());
        assertEquals(0.0, repository.getLastTrolleyCapacity(), 0.001);
    }

    // ---------------------------------------------------------------
    // INTEGRATION AND COMPLEX SCENARIOS
    // ---------------------------------------------------------------

    /**
     * Test complete workflow: store, retrieve, modify, clear.
     */
    @Test
    void testCompleteWorkflow() {
        // Store
        repository.storePlan(sampleTrolleys, PickingService.Heuristic.FF, PickingService.OverflowPolicy.SPLIT, 50.0);
        assertEquals(2, repository.getTrolleyCount());

        // Retrieve
        List<Trolley> all = repository.getAllTrolleys();
        assertEquals(2, all.size());

        Trolley first = repository.getTrolley(0);
        assertNotNull(first);

        // Verify metadata
        assertEquals(PickingService.Heuristic.FF, repository.getLastHeuristic());
        assertEquals(PickingService.OverflowPolicy.SPLIT, repository.getLastPolicy());

        // Clear
        repository.clear();
        assertEquals(0, repository.getTrolleyCount());
        assertNull(repository.getLastHeuristic());
    }

    /**
     * Test storing large number of trolleys.
     */
    @Test
    void testStoreLargeNumberOfTrolleys() {
        List<Trolley> many = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            many.add(new Trolley(50.0));
        }

        repository.storePlan(many, PickingService.Heuristic.BFD, PickingService.OverflowPolicy.DEFER, 50.0);

        assertEquals(100, repository.getTrolleyCount());

        // Verificar que podemos aceder a todos
        for (int i = 0; i < 100; i++) {
            assertNotNull(repository.getTrolley(i));
        }
    }

    /**
     * Test repository state isolation between tests.
     */
    @Test
    void testStateIsolation() {
        // Este teste verifica que setUp() limpa o estado corretamente
        assertEquals(0, repository.getTrolleyCount());
        assertFalse(repository.hasTrolleys());
    }

    /**
     * Test successive updates preserve latest values.
     */
    @Test
    void testSuccessiveUpdates() {
        // Primeira atualização
        repository.storePlan(sampleTrolleys, PickingService.Heuristic.FF, PickingService.OverflowPolicy.SPLIT, 50.0);
        assertEquals(2, repository.getTrolleyCount());

        // Segunda atualização
        List<Trolley> newList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            newList.add(new Trolley(100.0));
        }
        repository.storePlan(newList, PickingService.Heuristic.BFD, PickingService.OverflowPolicy.DEFER, 100.0);

        // Verificar que apenas os valores mais recentes estão presentes
        assertEquals(5, repository.getTrolleyCount());
        assertEquals(PickingService.Heuristic.BFD, repository.getLastHeuristic());
        assertEquals(PickingService.OverflowPolicy.DEFER, repository.getLastPolicy());
        assertEquals(100.0, repository.getLastTrolleyCapacity(), 0.001);
    }

    /**
     * Test getAllTrolleys preserves trolley state.
     */
    @Test
    void testGetAllTrolleys_PreservesTrolleyState() {
        repository.storePlan(sampleTrolleys, PickingService.Heuristic.FF, PickingService.OverflowPolicy.SPLIT, 50.0);

        List<Trolley> retrieved = repository.getAllTrolleys();

        // Verificar que os trolleys têm o estado correto
        assertTrue(retrieved.get(0).getUsedKg() > 0);
        assertTrue(retrieved.get(1).getUsedKg() > 0);
        assertFalse(retrieved.get(0).getPicks().isEmpty());
        assertFalse(retrieved.get(1).getPicks().isEmpty());
    }

    /**
     * Test boundary: store exactly one trolley.
     */
    @Test
    void testBoundary_SingleTrolley() {
        List<Trolley> single = new ArrayList<>();
        single.add(new Trolley(50.0));

        repository.storePlan(single, PickingService.Heuristic.FFD, PickingService.OverflowPolicy.SPLIT, 50.0);

        assertEquals(1, repository.getTrolleyCount());
        assertTrue(repository.hasTrolleys());
        assertNotNull(repository.getTrolley(0));
    }

    /**
     * Test metadata with null heuristic/policy (allowed).
     */
    @Test
    void testMetadata_NullValues() {
        repository.storePlan(sampleTrolleys, null, null, 50.0);

        assertNull(repository.getLastHeuristic());
        assertNull(repository.getLastPolicy());
        assertEquals(50.0, repository.getLastTrolleyCapacity(), 0.001);
    }

    /**
     * Test metadata with zero capacity.
     */
    @Test
    void testMetadata_ZeroCapacity() {
        repository.storePlan(sampleTrolleys, PickingService.Heuristic.FF, PickingService.OverflowPolicy.SPLIT, 0.0);

        assertEquals(0.0, repository.getLastTrolleyCapacity(), 0.001);
    }

    /**
     * Test metadata with negative capacity.
     */
    @Test
    void testMetadata_NegativeCapacity() {
        repository.storePlan(sampleTrolleys, PickingService.Heuristic.FF, PickingService.OverflowPolicy.SPLIT, -10.0);

        assertEquals(-10.0, repository.getLastTrolleyCapacity(), 0.001);
    }
}

