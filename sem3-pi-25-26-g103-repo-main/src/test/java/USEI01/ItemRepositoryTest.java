package USEI01;

import Repositories.ItemRepository;
import Model.Item;
import Model.SKU;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ItemRepository Tests")
public class ItemRepositoryTest {

    private ItemRepository repository;

    @BeforeAll
    static void loadOnce() {
        ItemRepository.getInstance().loadItems();
    }

    @BeforeEach
    void setUp() {
        repository = ItemRepository.getInstance();
    }

    @Test
    @DisplayName("Items loaded from CSV are not null")
    void itemsLoadedFromCSVAreNotNull() {
        List<Item> items = repository.getItems();
        assertNotNull(items);
    }

    @Test
    @DisplayName("Items loaded from CSV list is initialized (may be empty if no dataset)")
    void itemsLoadedFromCSVListIsInitialized() {
        List<Item> items = repository.getItems();
        assertNotNull(items);
    }

    @Test
    @DisplayName("Get item by SKU returns correct item")
    void getItemBySKUReturnsCorrectItem() {
        Item any = repository.getItems().get(0);
        Item found = repository.getItemBySKU(any.getSku());
        assertNotNull(found);
        assertEquals(any.getSku(), found.getSku());
    }

    @Test
    @DisplayName("Get item by non-existent SKU returns null")
    void getItemByNonExistentSKUReturnsNull() {
        SKU nonExistent = new SKU("NON-EXISTENT-SKU-12345");
        Item found = repository.getItemBySKU(nonExistent);
        assertNull(found);
    }

    @Test
    @DisplayName("Get item by null SKU returns null")
    void getItemByNullSKUReturnsNull() {
        Item found = repository.getItemBySKU(null);
        assertNull(found);
    }

    @Test
    @DisplayName("All loaded items have non-null SKU")
    void allLoadedItemsHaveNonNullSKU() {
        List<Item> items = repository.getItems();
        for (Item item : items) {
            assertNotNull(item.getSku());
        }
    }

    @Test
    @DisplayName("All loaded items have non-null name")
    void allLoadedItemsHaveNonNullName() {
        List<Item> items = repository.getItems();
        for (Item item : items) {
            assertNotNull(item.getName());
        }
    }

    @Test
    @DisplayName("All loaded items have non-null category")
    void allLoadedItemsHaveNonNullCategory() {
        List<Item> items = repository.getItems();
        for (Item item : items) {
            assertNotNull(item.getCategory());
        }
    }

    @Test
    @DisplayName("All loaded items have non-null unit")
    void allLoadedItemsHaveNonNullUnit() {
        List<Item> items = repository.getItems();
        for (Item item : items) {
            assertNotNull(item.getUnit());
        }
    }

    @Test
    @DisplayName("All loaded items have positive volume")
    void allLoadedItemsHavePositiveVolume() {
        List<Item> items = repository.getItems();
        for (Item item : items) {
            assertTrue(item.getVolume() >= 0);
        }
    }

    @Test
    @DisplayName("All loaded items have positive unit weight")
    void allLoadedItemsHavePositiveUnitWeight() {
        List<Item> items = repository.getItems();
        for (Item item : items) {
            assertTrue(item.getUnitWeight() >= 0);
        }
    }

    @Test
    @DisplayName("Set items replaces current items")
    void setItemsReplacesCurrent() {
        List<Item> newItems = new ArrayList<>();
        Item item1 = new Item(new SKU("TEST1"), "Test1", "Cat", "unit", 1.0f, 0.1f);
        Item item2 = new Item(new SKU("TEST2"), "Test2", "Cat", "unit", 2.0f, 0.2f);
        newItems.add(item1);
        newItems.add(item2);

        repository.setItems(newItems);

        assertEquals(2, repository.getItems().size());
        assertEquals(item1, repository.getItems().get(0));
        assertEquals(item2, repository.getItems().get(1));
    }

    @Test
    @DisplayName("Get items returns the same list instance")
    void getItemsReturnsSameListInstance() {
        List<Item> items1 = repository.getItems();
        List<Item> items2 = repository.getItems();
        assertSame(items1, items2);
    }

    @Test
    @DisplayName("Get item by SKU finds first matching item")
    void getItemBySKUFindsFirstMatchingItem() {
        List<Item> items = repository.getItems();
        if (items.size() > 0) {
            SKU sku = items.get(0).getSku();
            Item found = repository.getItemBySKU(sku);
            assertEquals(items.get(0), found);
        }
    }

    @Test
    @DisplayName("Repository is singleton")
    void repositoryIsSingleton() {
        ItemRepository repo1 = ItemRepository.getInstance();
        ItemRepository repo2 = ItemRepository.getInstance();
        assertSame(repo1, repo2);
    }

    @Test
    @DisplayName("Multiple get item by SKU calls return same item")
    void multipleGetItemBySKUCallsReturnSameItem() {
        Item any = repository.getItems().get(0);
        Item found1 = repository.getItemBySKU(any.getSku());
        Item found2 = repository.getItemBySKU(any.getSku());
        assertEquals(found1, found2);
    }

    @Test
    @DisplayName("Items list is mutable through getter")
    void itemsListIsMutableThroughGetter() {
        List<Item> items = repository.getItems();
        int originalSize = items.size();
        Item newItem = new Item(new SKU("MUTABLE"), "Mutable", "Cat", "unit", 1.0f, 0.1f);
        items.add(newItem);
        assertEquals(originalSize + 1, repository.getItems().size());
    }

    @Test
    @DisplayName("Get item by SKU with different SKU instances but same value")
    void getItemBySKUWithDifferentSKUInstancesSameValue() {
        Item any = repository.getItems().get(0);
        SKU sku1 = any.getSku();
        SKU sku2 = new SKU(sku1.getSku());
        
        Item found1 = repository.getItemBySKU(sku1);
        Item found2 = repository.getItemBySKU(sku2);
        
        assertEquals(found1, found2);
    }

    @Test
    @DisplayName("All items have unique SKUs")
    void allItemsHaveUniqueSKUs() {
        List<Item> items = repository.getItems();
        List<SKU> skus = new ArrayList<>();
        for (Item item : items) {
            for (SKU sku : skus) {
                if (sku.equals(item.getSku())) {
                    fail("Duplicate SKU found: " + sku.getSku());
                }
            }
            skus.add(item.getSku());
        }
    }

    @Test
    @DisplayName("Loaded items have valid data types")
    void loadedItemsHaveValidDataTypes() {
        List<Item> items = repository.getItems();
        for (Item item : items) {
            assertNotNull(item.getSku());
            assertIsInstance(item.getName(), String.class);
            assertIsInstance(item.getCategory(), String.class);
            assertIsInstance(item.getUnit(), String.class);
            assertTrue(item.getVolume() instanceof Float);
            assertTrue(item.getUnitWeight() instanceof Float);
        }
    }

    private void assertIsInstance(Object obj, Class<?> clazz) {
        assertTrue(clazz.isInstance(obj), "Expected instance of " + clazz.getName());
    }

    @Test
    @DisplayName("Get items count is consistent")
    void getItemsCountIsConsistent() {
        int count1 = repository.getItems().size();
        int count2 = repository.getItems().size();
        assertEquals(count1, count2);
    }

    @Test
    @DisplayName("Set items with empty list")
    void setItemsWithEmptyList() {
        List<Item> emptyList = new ArrayList<>();
        repository.setItems(emptyList);
        assertTrue(repository.getItems().isEmpty());
    }

    @Test
    @DisplayName("Set items with null list")
    void setItemsWithNullList() {
        repository.setItems(null);
        assertNull(repository.getItems());
    }
}
