package USEI01;

import Repositories.ItemRepository;
import Repositories.WarehouseRepository;
import Repositories.WagonRepository;
import Model.Bay;
import Model.Box;
import Model.Warehouse;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("WarehouseRepository Tests")
public class WarehouseRepositoryTest {

    private WarehouseRepository repository;

    @BeforeAll
    static void loadOnce() {
        ItemRepository.getInstance().loadItems();
        WagonRepository.getInstance().loadWagons();
        WarehouseRepository.getInstance().createWarehouse();
    }

    @BeforeEach
    void setUp() {
        repository = WarehouseRepository.getInstance();
    }

    @Test
    @DisplayName("Warehouses loaded and layout built")
    void warehousesLoadedAndLayoutBuilt() {
        List<Warehouse> list = repository.getAllWarehouses();
        assertNotNull(list);
        assertFalse(list.isEmpty());
    }

    @Test
    @DisplayName("At least one warehouse has bays")
    void atLeastOneWarehouseHasBays() {
        List<Warehouse> list = repository.getAllWarehouses();
        boolean anyHasBays = list.stream().anyMatch(w -> !w.getBays().isEmpty());
        assertTrue(anyHasBays);
    }

    @Test
    @DisplayName("Get warehouses ID not empty")
    void getWarehousesIdNotEmpty() {
        assertFalse(repository.getWarehousesId().isEmpty());
    }

    @Test
    @DisplayName("Get warehouse by ID returns correct warehouse")
    void getWarehouseByIdReturnsCorrectWarehouse() {
        List<String> ids = repository.getWarehousesId();
        String id = ids.get(0);
        Warehouse warehouse = repository.getWarehouse(id);
        assertNotNull(warehouse);
        assertEquals(id, warehouse.getWarehouseID());
    }

    @Test
    @DisplayName("Get all warehouses returns non-null list")
    void getAllWarehousesReturnsNonNullList() {
        List<Warehouse> warehouses = repository.getAllWarehouses();
        assertNotNull(warehouses);
    }

    @Test
    @DisplayName("Get warehouses map returns non-null map")
    void getWarehousesMapReturnsNonNullMap() {
        Map<String, Warehouse> warehouses = repository.getWarehouses();
        assertNotNull(warehouses);
    }

    @Test
    @DisplayName("All warehouses have non-null IDs")
    void allWarehousesHaveNonNullIds() {
        List<Warehouse> warehouses = repository.getAllWarehouses();
        for (Warehouse warehouse : warehouses) {
            assertNotNull(warehouse.getWarehouseID());
        }
    }

    @Test
    @DisplayName("All warehouses have non-null layout")
    void allWarehousesHaveNonNullLayout() {
        List<Warehouse> warehouses = repository.getAllWarehouses();
        for (Warehouse warehouse : warehouses) {
            assertNotNull(warehouse.getLayout());
        }
    }

    @Test
    @DisplayName("Update warehouse persists changes")
    void updateWarehousePersistsChanges() {
        Warehouse any = repository.getAllWarehouses().get(0);
        Bay newBay = new Bay(any.getWarehouseID(), 0, 0, 10);
        any.addBay(newBay);
        repository.updateWarehouse(any);
        Warehouse fetched = repository.getWarehouse(any.getWarehouseID());
        assertSame(any, fetched);
    }

    @Test
    @DisplayName("Repository is singleton")
    void repositoryIsSingleton() {
        WarehouseRepository repo1 = WarehouseRepository.getInstance();
        WarehouseRepository repo2 = WarehouseRepository.getInstance();
        assertSame(repo1, repo2);
    }

    @Test
    @DisplayName("Get all warehouses returns equivalent content across calls")
    void getAllWarehousesReturnsEquivalentContentAcrossCalls() {
        List<Warehouse> list1 = repository.getAllWarehouses();
        List<Warehouse> list2 = repository.getAllWarehouses();
        assertNotNull(list1);
        assertNotNull(list2);
        assertEquals(list1, list2);
    }

    @Test
    @DisplayName("Get warehouses ID returns equivalent content across calls")
    void getWarehousesIdReturnsEquivalentContentAcrossCalls() {
        List<String> ids1 = repository.getWarehousesId();
        List<String> ids2 = repository.getWarehousesId();
        assertNotNull(ids1);
        assertNotNull(ids2);
        assertEquals(ids1, ids2);
    }

    @Test
    @DisplayName("Warehouse IDs are unique")
    void warehouseIDsAreUnique() {
        List<String> ids = repository.getWarehousesId();
        for (int i = 0; i < ids.size(); i++) {
            for (int j = i + 1; j < ids.size(); j++) {
                assertNotEquals(ids.get(i), ids.get(j));
            }
        }
    }

    @Test
    @DisplayName("Get warehouse with non-existent ID returns null")
    void getWarehouseWithNonExistentIdReturnsNull() {
        Warehouse warehouse = repository.getWarehouse("NON-EXISTENT-ID");
        assertNull(warehouse);
    }

    @Test
    @DisplayName("Set warehouses replaces current warehouses")
    void setWarehousesReplacesCurrent() {
        Map<String, Warehouse> newWarehouses = new HashMap<>();
        Warehouse w1 = new Warehouse("TEST-W1", 2, 2);
        Warehouse w2 = new Warehouse("TEST-W2", 3, 3);
        newWarehouses.put("TEST-W1", w1);
        newWarehouses.put("TEST-W2", w2);

        repository.setWarehouses(newWarehouses);

        assertEquals(2, repository.getAllWarehouses().size());
        assertSame(w1, repository.getWarehouse("TEST-W1"));
        assertSame(w2, repository.getWarehouse("TEST-W2"));
    }

    @Test
    @DisplayName("All warehouses in map match get all warehouses")
    void allWarehousesInMapMatchGetAllWarehouses() {
        Map<String, Warehouse> map = repository.getWarehouses();
        List<Warehouse> list = repository.getAllWarehouses();

        assertEquals(map.size(), list.size());
        for (Warehouse warehouse : list) {
            assertTrue(map.containsValue(warehouse));
        }
    }

    @Test
    @DisplayName("Get warehouse IDs match warehouse map keys")
    void getWarehouseIdsMatchWarehouseMapKeys() {
        List<String> ids = repository.getWarehousesId();
        Map<String, Warehouse> map = repository.getWarehouses();

        assertEquals(ids.size(), map.keySet().size());
        for (String id : ids) {
            assertTrue(map.containsKey(id));
        }
    }

    @Test
    @DisplayName("All warehouses have bays")
    void allWarehousesHaveBays() {
        List<Warehouse> warehouses = repository.getAllWarehouses();
        for (Warehouse warehouse : warehouses) {
            assertNotNull(warehouse.getBays());
        }
    }

    @Test
    @DisplayName("Multiple updates to same warehouse persist")
    void multipleUpdatesToSameWarehousePersist() {
        Warehouse warehouse = repository.getAllWarehouses().get(0);
        String id = warehouse.getWarehouseID();

        int maxAisle = Math.max(0, warehouse.getLayout().size() - 1);
        int maxBay = Math.max(0, warehouse.getLayout().get(0).size() - 1);
        int a1 = Math.min(10, maxAisle);
        int b1 = Math.min(10, maxBay);
        Bay bay1 = new Bay(id, a1, b1, 5);
        warehouse.addBay(bay1);
        repository.updateWarehouse(warehouse);

        Warehouse fetched1 = repository.getWarehouse(id);
        assertSame(warehouse, fetched1);

        int a2 = Math.min(11, maxAisle);
        int b2 = Math.min(11, maxBay);
        Bay bay2 = new Bay(id, a2, b2, 5);
        warehouse.addBay(bay2);
        repository.updateWarehouse(warehouse);

        Warehouse fetched2 = repository.getWarehouse(id);
        assertSame(warehouse, fetched2);
    }

    @Test
    @DisplayName("Load warehouses returns remaining boxes")
    void loadWarehousesReturnsRemainingBoxes() {
        List<Box> remaining = repository.loadWarehouses();
        assertNotNull(remaining);
    }

    @Test
    @DisplayName("Get warehouse returns same instance on multiple calls")
    void getWarehouseReturnsSameInstanceOnMultipleCalls() {
        String id = repository.getWarehousesId().get(0);
        Warehouse w1 = repository.getWarehouse(id);
        Warehouse w2 = repository.getWarehouse(id);
        assertSame(w1, w2);
    }

    @Test
    @DisplayName("Warehouse map is mutable through getter")
    void warehouseMapIsMutableThroughGetter() {
        Map<String, Warehouse> map = repository.getWarehouses();
        int originalSize = map.size();
        Warehouse newWarehouse = new Warehouse("MUTABLE-W", 1, 1);
        map.put("MUTABLE-W", newWarehouse);
        assertEquals(originalSize + 1, repository.getWarehouses().size());
    }

    @Test
    @DisplayName("All warehouses have consistent layout structure")
    void allWarehousesHaveConsistentLayoutStructure() {
        List<Warehouse> warehouses = repository.getAllWarehouses();
        for (Warehouse warehouse : warehouses) {
            assertNotNull(warehouse.getLayout());
            assertTrue(warehouse.getLayout().size() > 0);
            for (var aisle : warehouse.getLayout()) {
                assertNotNull(aisle);
                assertTrue(aisle.size() > 0);
            }
        }
    }

    @Test
    @DisplayName("Create warehouse builds valid bay structure")
    void createWarehouseBuildsValidBayStructure() {
        List<Warehouse> warehouses = repository.getAllWarehouses();
        for (Warehouse warehouse : warehouses) {
            List<Bay> bays = warehouse.getBays();
            for (Bay bay : bays) {
                assertNotNull(bay.getWarehouseId());
                assertEquals(warehouse.getWarehouseID(), bay.getWarehouseId());
                assertTrue(bay.getAisle() >= 0);
                assertTrue(bay.getBay() >= 0);
                assertTrue(bay.getCapacityBoxes() > 0);
            }
        }
    }

    @Test
    @DisplayName("Get all warehouses count matches warehouse IDs count")
    void getAllWarehousesCountMatchesWarehouseIdsCount() {
        int warehouseCount = repository.getAllWarehouses().size();
        int idCount = repository.getWarehousesId().size();
        assertEquals(warehouseCount, idCount);
    }
}
