package USEI01;

import Model.Item;
import Model.SKU;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Item model verifying constructors, getters, and setters.
 */
@DisplayName("Item Model Tests")
public class ItemTest {

    private Item item;
    private SKU sku;

    @BeforeEach
    void setUp() {
        sku = new SKU("SKU-001");
        item = new Item(sku, "TestItem", "Electronics", "unit", 1.5f, 0.25f);
    }

    @Test
    @DisplayName("Constructor initializes all fields correctly")
    void constructorInitializesAllFields() {
        SKU testSku = new SKU("TEST-SKU");
        Item testItem = new Item(testSku, "TestName", "TestCategory", "kg", 2.5f, 0.5f);
        
        assertEquals(testSku, testItem.getSku());
        assertEquals("TestName", testItem.getName());
        assertEquals("TestCategory", testItem.getCategory());
        assertEquals("kg", testItem.getUnit());
        assertEquals(2.5f, testItem.getVolume());
        assertEquals(0.5f, testItem.getUnitWeight());
    }

    @Test
    @DisplayName("Default constructor initializes with null/zero values")
    void defaultConstructorInitializesWithNullValues() {
        Item defaultItem = new Item();
        assertNull(defaultItem.getSku());
        assertNull(defaultItem.getName());
        assertNull(defaultItem.getCategory());
        assertNull(defaultItem.getUnit());
        assertEquals(0, defaultItem.getVolume());
        assertEquals(0, defaultItem.getUnitWeight());
    }

    @Test
    @DisplayName("Getter returns SKU correctly")
    void getterReturnsSKU() {
        assertEquals(sku, item.getSku());
    }

    @Test
    @DisplayName("Setter modifies SKU correctly")
    void setterModifiesSKU() {
        SKU newSku = new SKU("NEW-SKU");
        item.setSku(newSku);
        assertEquals(newSku, item.getSku());
    }

    @Test
    @DisplayName("Getter returns name correctly")
    void getterReturnsName() {
        assertEquals("TestItem", item.getName());
    }

    @Test
    @DisplayName("Setter modifies name correctly")
    void setterModifiesName() {
        item.setName("NewName");
        assertEquals("NewName", item.getName());
    }

    @Test
    @DisplayName("Getter returns unit correctly")
    void getterReturnsUnit() {
        assertEquals("unit", item.getUnit());
    }

    @Test
    @DisplayName("Setter modifies unit correctly")
    void setterModifiesUnit() {
        item.setUnit("kg");
        assertEquals("kg", item.getUnit());
    }

    @Test
    @DisplayName("Getter returns volume correctly")
    void getterReturnsVolume() {
        assertEquals(1.5f, item.getVolume());
    }

    @Test
    @DisplayName("Setter modifies volume correctly")
    void setterModifiesVolume() {
        item.setVolume(3.5f);
        assertEquals(3.5f, item.getVolume());
    }

    @Test
    @DisplayName("Getter returns unit weight correctly")
    void getterReturnsUnitWeight() {
        assertEquals(0.25f, item.getUnitWeight());
    }

    @Test
    @DisplayName("Setter modifies unit weight correctly")
    void setterModifiesUnitWeight() {
        item.setUnitWeight(0.75f);
        assertEquals(0.75f, item.getUnitWeight());
    }

    @Test
    @DisplayName("Multiple setters work independently")
    void multipleSettersWorkIndependently() {
        item.setName("Name1");
        item.setUnit("unit1");
        item.setVolume(1.0f);
        item.setUnitWeight(0.1f);
        
        assertEquals("Name1", item.getName());
        assertEquals("unit1", item.getUnit());
        assertEquals(1.0f, item.getVolume());
        assertEquals(0.1f, item.getUnitWeight());
        
        item.setName("Name2");
        assertEquals("Name2", item.getName());
        assertEquals("unit1", item.getUnit()); // unchanged
    }

    @Test
    @DisplayName("toString contains relevant information")
    void toStringContainsRelevantInfo() {
        String str = item.toString();
        assertTrue(str.contains("Item"));
        assertTrue(str.contains("sku"));
        assertTrue(str.contains("name"));
        assertTrue(str.contains("category"));
        assertTrue(str.contains("unit"));
        assertTrue(str.contains("volume"));
        assertTrue(str.contains("unitWeight"));
    }

    @Test
    @DisplayName("Item with zero volume")
    void itemWithZeroVolume() {
        Item zeroVolItem = new Item(sku, "ZeroVol", "Cat", "unit", 0.0f, 0.1f);
        assertEquals(0.0f, zeroVolItem.getVolume());
    }

    @Test
    @DisplayName("Item with zero unit weight")
    void itemWithZeroUnitWeight() {
        Item zeroWeightItem = new Item(sku, "ZeroWeight", "Cat", "unit", 1.0f, 0.0f);
        assertEquals(0.0f, zeroWeightItem.getUnitWeight());
    }

    @Test
    @DisplayName("Item with large volume and weight")
    void itemWithLargeVolumeAndWeight() {
        Item largeItem = new Item(sku, "Large", "Cat", "unit", 1000.5f, 500.25f);
        assertEquals(1000.5f, largeItem.getVolume());
        assertEquals(500.25f, largeItem.getUnitWeight());
    }

    @Test
    @DisplayName("Item with very small volume and weight")
    void itemWithVerySmallVolumeAndWeight() {
        Item smallItem = new Item(sku, "Small", "Cat", "unit", 0.001f, 0.0001f);
        assertEquals(0.001f, smallItem.getVolume());
        assertEquals(0.0001f, smallItem.getUnitWeight());
    }

    @Test
    @DisplayName("Item with empty string fields")
    void itemWithEmptyStringFields() {
        Item emptyItem = new Item(sku, "", "", "", 1.0f, 0.1f);
        assertEquals("", emptyItem.getName());
        assertEquals("", emptyItem.getCategory());
        assertEquals("", emptyItem.getUnit());
    }

    @Test
    @DisplayName("Item with null SKU")
    void itemWithNullSKU() {
        Item nullSkuItem = new Item(null, "Name", "Cat", "unit", 1.0f, 0.1f);
        assertNull(nullSkuItem.getSku());
    }

    @Test
    @DisplayName("Item with null name")
    void itemWithNullName() {
        Item nullNameItem = new Item(sku, null, "Cat", "unit", 1.0f, 0.1f);
        assertNull(nullNameItem.getName());
    }

    @Test
    @DisplayName("Item with null category")
    void itemWithNullCategory() {
        Item nullCatItem = new Item(sku, "Name", null, "unit", 1.0f, 0.1f);
        assertNull(nullCatItem.getCategory());
    }

    @Test
    @DisplayName("Item with null unit")
    void itemWithNullUnit() {
        Item nullUnitItem = new Item(sku, "Name", "Cat", null, 1.0f, 0.1f);
        assertNull(nullUnitItem.getUnit());
    }

    @Test
    @DisplayName("Setting SKU to null")
    void settingSKUToNull() {
        item.setSku(null);
        assertNull(item.getSku());
    }

    @Test
    @DisplayName("Setting name to null")
    void settingNameToNull() {
        item.setName(null);
        assertNull(item.getName());
    }

    @Test
    @DisplayName("Setting unit to null")
    void settingUnitToNull() {
        item.setUnit(null);
        assertNull(item.getUnit());
    }

    @Test
    @DisplayName("Setting volume to negative value")
    void settingVolumeToNegativeValue() {
        item.setVolume(-5.0f);
        assertEquals(-5.0f, item.getVolume());
    }

    @Test
    @DisplayName("Setting unit weight to negative value")
    void settingUnitWeightToNegativeValue() {
        item.setUnitWeight(-2.5f);
        assertEquals(-2.5f, item.getUnitWeight());
    }

    @Test
    @DisplayName("Item with special characters in name")
    void itemWithSpecialCharactersInName() {
        Item specialItem = new Item(sku, "Item@#$%^&*()", "Cat", "unit", 1.0f, 0.1f);
        assertEquals("Item@#$%^&*()", specialItem.getName());
    }

    @Test
    @DisplayName("Item with very long name")
    void itemWithVeryLongName() {
        String longName = "Item" + "A".repeat(1000);
        Item longNameItem = new Item(sku, longName, "Cat", "unit", 1.0f, 0.1f);
        assertEquals(longName, longNameItem.getName());
    }

    @Test
    @DisplayName("Item with spaces in fields")
    void itemWithSpacesInFields() {
        Item spaceItem = new Item(sku, "Item Name", "Category Name", "unit type", 1.0f, 0.1f);
        assertEquals("Item Name", spaceItem.getName());
        assertEquals("Category Name", spaceItem.getCategory());
        assertEquals("unit type", spaceItem.getUnit());
    }

    @Test
    @DisplayName("Multiple items with same SKU are independent")
    void multipleItemsWithSameSKUAreIndependent() {
        Item item1 = new Item(sku, "Item1", "Cat1", "unit1", 1.0f, 0.1f);
        Item item2 = new Item(sku, "Item2", "Cat2", "unit2", 2.0f, 0.2f);
        
        assertEquals(sku, item1.getSku());
        assertEquals(sku, item2.getSku());
        assertNotEquals(item1.getName(), item2.getName());
        assertNotEquals(item1.getVolume(), item2.getVolume());
    }

    @Test
    @DisplayName("Floating point precision in volume and weight")
    void floatingPointPrecisionInVolumeAndWeight() {
        Item precisionItem = new Item(sku, "Precision", "Cat", "unit", 1.23456789f, 0.98765432f);
        assertEquals(1.23456789f, precisionItem.getVolume(), 0.00001f);
        assertEquals(0.98765432f, precisionItem.getUnitWeight(), 0.00001f);
    }

    @Test
    @DisplayName("Default item can be populated with setters")
    void defaultItemCanBePopulatedWithSetters() {
        Item defaultItem = new Item();
        defaultItem.setSku(sku);
        defaultItem.setName("PopulatedName");
        defaultItem.setUnit("kg");
        defaultItem.setVolume(5.0f);
        defaultItem.setUnitWeight(1.0f);
        
        assertEquals(sku, defaultItem.getSku());
        assertEquals("PopulatedName", defaultItem.getName());
        assertEquals("kg", defaultItem.getUnit());
        assertEquals(5.0f, defaultItem.getVolume());
        assertEquals(1.0f, defaultItem.getUnitWeight());
    }
}
